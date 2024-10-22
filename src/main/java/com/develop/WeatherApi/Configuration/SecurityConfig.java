package com.develop.WeatherApi.Configuration;

import com.develop.WeatherApi.Model.CustomOAuth2User;
import com.develop.WeatherApi.Model.User;
import com.develop.WeatherApi.Repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring security filter chain.");

        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/checking", "/testAuthorities").permitAll()
                        .requestMatchers("/fetchWeather").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService())
                        )
                        .successHandler((request, response, authentication) -> {
                            logger.info("OAuth2 login successful.");

                            // Manually update the authorities in the SecurityContextHolder
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                            String email = oAuth2User.getAttribute("email");

                            // Check if user exists in the database
                            User user = userRepository.findByEmail(email).orElse(null);

                            if (user == null) {
                                // Register new user if they don't exist
                                logger.info("Registering new user with email: {}", email);
                                User newUser = new User();
                                newUser.setEmail(email);
                                newUser.setName(oAuth2User.getAttribute("name"));
                                newUser.setRole("ROLE_USER"); // Assign default role
                                user = userRepository.save(newUser);
                                logger.info("Successfully registered new user with email: {}", email);
                            } else {
                                // Update user attributes if needed
                                logger.info("User found in database, updating details if necessary.");
                                user.setName(oAuth2User.getAttribute("name"));
                                user = userRepository.save(user);
                                logger.info("Updated user details for email: {}", email);
                            }

                            // Update authorities with roles
                            Set<GrantedAuthority> updatedAuthorities = new HashSet<>(oAuth2User.getAuthorities());
                            updatedAuthorities.add(new SimpleGrantedAuthority(user.getRole()));
                            logger.info("Updated authorities for user {}: {}", email, updatedAuthorities);

                            // Create a new UsernamePasswordAuthenticationToken with updated authorities
                            UsernamePasswordAuthenticationToken updatedAuth = new UsernamePasswordAuthenticationToken(
                                    new CustomOAuth2User(oAuth2User, updatedAuthorities),
                                    null,
                                    updatedAuthorities
                            );

                            // Set the updated Authentication object in the SecurityContext
                            SecurityContextHolder.getContext().setAuthentication(updatedAuth);

                            // Redirect to home page after successful login
                            response.sendRedirect("/");
                        })
                );

        return http.build();
    }


    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
        return userRequest -> {
            // Load user details from the OAuth provider (e.g., Google)
            OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
            String email = oAuth2User.getAttribute("email");
            logger.info("Attempting login for user with email: {}", email);

            // Try to find the user in the local database
            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                // Register new user if it doesn't exist in the database
                logger.info("Registering new user with email: {}", email);
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setName(oAuth2User.getAttribute("name"));
                newUser.setRole("ROLE_USER"); // Default role for new users
                user = userRepository.save(newUser);
                logger.info("Successfully registered new user with email: {}", email);
            } else {
                // Update user attributes if needed
                logger.info("User found in database, updating details if necessary.");
                user.setName(oAuth2User.getAttribute("name"));
                user = userRepository.save(user);
                logger.info("Updated user details for email: {}", email);
            }

            // Assign authorities (roles) to the authenticated user
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oAuth2User.getAuthorities());
            mappedAuthorities.add(new SimpleGrantedAuthority(user.getRole()));
            logger.info("Mapped authorities for user {}: {}", email, mappedAuthorities);

            // Return enriched OAuth2User
            return new CustomOAuth2User(oAuth2User, mappedAuthorities);
        };
    }

}

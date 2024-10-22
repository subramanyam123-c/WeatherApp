//package com.develop.WeatherApi.Service;
//
//import com.develop.WeatherApi.Model.CustomOAuth2UserDetails;
//import com.develop.WeatherApi.Model.User;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//@Service
//public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
//
//    private final UserRepo userRepository;
//    private final DefaultOAuth2UserService defaultOAuth2UserService;
//
//    public CustomOAuth2UserService(UserRepo userRepository) {
//        this.userRepository = userRepository;
//        this.defaultOAuth2UserService = new DefaultOAuth2UserService();
//    }
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        // Load user info from OAuth2 provider
//        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
//
//        // Extract the email or unique identifier
//        String email = oAuth2User.getAttribute("email");
//
//        // Load the user entity from your database
//        User userEntity = userRepository.findByEmail(email)
//                .orElseThrow(() -> new OAuth2AuthenticationException("User not found in database"));
//
//        // Return the custom OAuth2 user details with roles
//        return new CustomOAuth2UserDetails(userEntity, oAuth2User.getAttributes());
//    }
//}


# Weather Application 🌦️

## Overview
This project is a Spring Boot-based weather application that fetches real-time weather data for any city, provides user authentication with **Google OAuth2**, and even suggests outfits based on the current temperature. It integrates external APIs and uses reactive programming for efficient data fetching and processing.

## Features
- **Geolocation Conversion**: Convert city names into latitude and longitude using OpenWeatherMap’s geolocation API.
- **Weather Data Fetching**: Retrieve real-time weather data based on geographical coordinates using OpenWeatherMap’s weather API.
- **AI-Powered Outfit Suggestions**: Suggests outfits based on current temperature using OpenAI integration.
- **Google OAuth2 Authentication**: Secured endpoints with Google OAuth2-based user login and role-based access control.
- **Asynchronous Processing**: Handles API calls using reactive programming with **Project Reactor (Mono)** for non-blocking operations.
- **Custom User Management**: Automatically registers new users and manages roles based on successful OAuth2 logins.

## Tech Stack
- **Java** & **Spring Boot**
- **WebClient** for making asynchronous, non-blocking API requests
- **Spring Security** with Google OAuth2 for secure user authentication
- **OpenWeatherMap API** for weather and geolocation data
- **OpenAI ChatClient** for outfit suggestion
- **Project Reactor (Mono)** for reactive programming
- **H2/SQL Database** for user storage and management

## Endpoints

1. **/convert**: Converts a city name to latitude and longitude.
   - **Parameters**: `city`, `state` (optional), `country`, `limit` (optional)
   - **Response**: Returns the geolocation coordinates of the requested city.

2. **/fetchWeather**: Fetches the current weather data for a city.
   - **Parameters**: `city`, `state` (optional), `country`
   - **Response**: Returns the current weather details in JSON format.

3. **/suggestOutfit**: Suggests an outfit based on the current temperature.
   - **Response**: Returns AI-generated outfit suggestions based on weather.

4. **/checking**: Displays the current authenticated user's details.

5. **/testAuthorities**: Tests and logs the authorities of the current user.

## Getting Started

### Prerequisites
- Java 17+
- Maven
- OpenWeatherMap API key (register [here](https://home.openweathermap.org/users/sign_up))
- OpenAI API key (optional for outfit suggestion)
- Google OAuth2 credentials (configure in Google Developer Console)

### Setup
1. Clone the repository:
   \`\`\`bash
   git clone https://github.com/your-username/weather-app.git
   \`\`\`
   
2. Navigate to the project directory:
   \`\`\`bash
   cd weather-app
   \`\`\`

3. Add your OpenWeatherMap API key in the `application.properties` file:
   \`\`\`properties
   openweathermap.api.key=YOUR_API_KEY
   \`\`\`

4. Configure Google OAuth2 credentials for login.

5. Build the project using Maven:
   \`\`\`bash
   mvn clean install
   \`\`\`

6. Run the application:
   \`\`\`bash
   mvn spring-boot:run
   \`\`\`

7. Open your browser and access the app at `http://localhost:8080`.

### Authentication
- The app supports **Google OAuth2 login**.
- To access the `/fetchWeather` endpoint, users need to be authenticated and have the role of `ADMIN`.

## Future Enhancements
- Adding support for more weather details such as forecasts, wind speed, etc.
- Enhancing the AI outfit suggestions with more context (season, occasion, etc.).
- Adding user-specific dashboards for weather tracking.

## License
This project is licensed under the MIT License.

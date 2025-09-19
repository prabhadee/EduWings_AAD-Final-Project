package lk.ijse.gdse72.backend.config;

import lk.ijse.gdse72.backend.util.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.web.servlet.function.RequestPredicates.headers;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final PasswordEncoder passwordEncoder;

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(AbstractHttpConfigurer::disable)
//                .cors(cors -> cors.configurationSource(corsConfigurationCors()))
//                .authorizeHttpRequests(
//                        auth->
//                                auth.requestMatchers("/auth/**").permitAll()
//                                        .anyRequest().authenticated())
//                .sessionManagement(
//                        session->
//                                session.sessionCreationPolicy
//                                        (SessionCreationPolicy.STATELESS))
//                .authenticationProvider(authenticationProvider())
//                .addFilterBefore(jwtAuthFilter,
//                        UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationCors()))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // Allow all requests
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers
                    .frameOptions(frame -> frame.sameOrigin()) // ✅ allow iframe from same origin
            );

    return http.build();
}

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider
                = new DaoAuthenticationProvider();
        daoAuthenticationProvider
                .setUserDetailsService(userDetailsService);
        daoAuthenticationProvider
                .setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;

    }
    @Bean
    public CorsConfigurationSource corsConfigurationCors() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // allows all origins
        config.addAllowedMethod("*");         // allows GET, POST, PUT, DELETE, OPTIONS
        config.addAllowedHeader("*");         // allows Authorization, Content-Type, etc.
        config.setAllowCredentials(true);     // ✅ must be true to send Authorization header

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // ✅ apply to all paths
        return source;
    }




}
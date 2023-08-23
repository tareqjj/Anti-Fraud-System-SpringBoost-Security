package antifraud.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    public SecurityConfiguration(RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                .csrf(CsrfConfigurer::disable)                           // For modifying requests via Postman
                .exceptionHandling(handing -> handing
                        .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
                )
                .headers(headers -> headers.frameOptions().disable())           // for Postman, the H2 console
                .authorizeHttpRequests(requests -> requests                     // manage access
                                // You can use hasRole(MERCHANT) and ROLE_ prefix will be added by Spring
                                .requestMatchers("/actuator/shutdown", "/error/**", "/h2-console/**").permitAll()      // needs to run test
                                .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/api/auth/user/**").hasAuthority("ROLE_ADMINISTRATOR")
                                .requestMatchers(HttpMethod.GET, "/api/auth/list").hasAnyAuthority("ROLE_ADMINISTRATOR", "ROLE_SUPPORT")
                                .requestMatchers(HttpMethod.PUT, "/api/auth/access/**").hasAuthority("ROLE_ADMINISTRATOR")
                                .requestMatchers(HttpMethod.PUT, "/api/auth/role/**").hasAuthority("ROLE_ADMINISTRATOR")
                                .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction/**").hasAuthority("ROLE_MERCHANT")
                                .requestMatchers("/api/antifraud/suspicious-ip/**").hasAuthority("ROLE_SUPPORT")
                                .requestMatchers("/api/antifraud/stolencard/**").hasAuthority("ROLE_SUPPORT")
                                .requestMatchers(HttpMethod.GET, "/api/antifraud/history/**").hasAuthority("ROLE_SUPPORT")
                                .requestMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasAuthority("ROLE_SUPPORT")
                                .anyRequest().authenticated()
                        // other matchers
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                )
                // other configurations
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(13);
    }
}

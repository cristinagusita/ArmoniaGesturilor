package upt.licenta.cristinagusita.armoniagesturilor.securingweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUserRole;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/uploadSong", "/profile/**", "/achievements/**", "/settings/**", "/songs/checkLike/**", "/songs/toggle/**", "/songs/like/**", "/songs/dislike/**", "/songs/delete/**").authenticated()
                        .requestMatchers("/api/v1/**","/**.css", "/player.css", "/scripts/player.js", "/style.css", "/home", "/", "/descopera", "/canta", "/model/**", "/model.js", "/sounds/**", "/achievements.css", "/scripts/admin-functions.js", "/registration/**", "/songs/data/**", "/images/**", "/forgot-password/**", "update-email/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/api/v1/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll())
                .exceptionHandling((exceptions) -> exceptions
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
        );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
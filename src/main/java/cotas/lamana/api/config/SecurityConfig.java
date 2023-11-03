package cotas.lamana.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(new AntPathRequestMatcher("/usuarios/cadastrar")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/usuarios/confirmar-email")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/usuarios/esqueci-senha")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/usuarios/reenviar-email")).permitAll()
                        .anyRequest().authenticated());

        return http.build();

    }
}
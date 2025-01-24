package gov.va.api.lighthouse.hygieia.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  /** Set-up security filter chain. */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    /*
     * Add default security response headers.
     */
    http.headers(
        h ->
            h.frameOptions(FrameOptionsConfig::deny)
                .httpStrictTransportSecurity(s -> s.requestMatcher(AnyRequestMatcher.INSTANCE)));

    /*
     * We do not use cookies for authentication
     */
    http.csrf(AbstractHttpConfigurer::disable);

    /*
     *  Disable authorization on any path.
     */
    http.authorizeHttpRequests(auth -> auth.requestMatchers("/**").permitAll());
    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring().requestMatchers("$^");
  }
}

package uni.projects.talkmeow.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import uni.projects.talkmeow.repositories.UserRepository;
import uni.projects.talkmeow.services.CustomUserDetailsService;
import uni.projects.talkmeow.services.GlobalAttributeService;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;


    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/login", "/login/form", "/register", "/register/form", "/home", "/", "/logout", "/user/search",
                                        "/password-reset-email", "/password-reset-question", "/password-reset-change", "/password-reset/form", "/ws/**", "/image/**").permitAll()
                                .requestMatchers("/css/**", "/javascript/**", "/svg/**", "/images/**").permitAll() //static recourses
                                .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
                                .anyRequest().authenticated()

                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login/form")
                                .defaultSuccessUrl("/home", true)
                                .failureUrl("/login?error=true")
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login/form")
                                .permitAll()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);

        return providerManager;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }
}

package com.dummy.trivia.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomUserService customUserService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserService)
        .passwordEncoder(new BCryptPasswordEncoder(4));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
                .accessDeniedHandler(new TriviaAccessDeniedHandler())
                .authenticationEntryPoint(new TriviaAuthenticationEntryPoint())
                .and().authorizeRequests()
                .antMatchers(HttpMethod.POST, "/user").permitAll()
                .anyRequest().authenticated()
                .and().formLogin()
                .loginProcessingUrl("/login").permitAll()
                .successHandler(new TriviaAuthenticationSuccessHandler())
                .failureHandler(new TriviaAuthenticationFailureHandler())
                .and().logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new TriviaLogoutSuccessHandler())
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and().requiresChannel()
                .antMatchers("/pomer").requiresSecure()
                .anyRequest().requiresInsecure()
                .and().rememberMe()
                .tokenValiditySeconds(1800)
                .key("token_key");
    }
}

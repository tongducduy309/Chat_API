package com.gener.chat.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.CrossOriginEmbedderPolicyHeaderWriter;
import org.springframework.security.web.header.writers.CrossOriginOpenerPolicyHeaderWriter;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class SecurityConfig {
    @Value("${jwt.secret}")
    private String SIGNER_KEY;


    private final String[] PUBLIC_ENDPOINT_POST = {"/api/v1/auth/login","/api/v1/auth/introspect"};
    private final String[] PUBLIC_ENDPOINT_GET = {"/api/v1/auth/me"};
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .headers(h -> h
                        .crossOriginOpenerPolicy(coop -> coop.policy(CrossOriginOpenerPolicyHeaderWriter.CrossOriginOpenerPolicy.SAME_ORIGIN_ALLOW_POPUPS))
                        .crossOriginEmbedderPolicy(coep -> coep.policy(CrossOriginEmbedderPolicyHeaderWriter.CrossOriginEmbedderPolicy.UNSAFE_NONE))
                )
                .authorizeHttpRequests(request->
                        request.requestMatchers(HttpMethod.POST,PUBLIC_ENDPOINT_POST).permitAll()
                                .requestMatchers(HttpMethod.GET,PUBLIC_ENDPOINT_GET).permitAll()
//                        .requestMatchers(HttpMethod.GET,"/api/v1/users").hasRole(Role.ADMIN.name())
                                .requestMatchers("/ws/**").permitAll()
                                .anyRequest().authenticated());

        httpSecurity.oauth2ResourceServer(oauth2->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
                        ).authenticationEntryPoint(new JWTAuthenticationEntryPoint())
                        .accessDeniedHandler(new JsonAccessDeniedHandler())

        );

        httpSecurity.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);




        return httpSecurity.build();
    }

    @Bean
    JwtDecoder jwtDecoder(){
        SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}

package com.web.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.web.security.CustomAuthenticationEntryPoint;
import com.web.security.JwtAuthenticationEntryPoint;
import com.web.security.JwtAuthenticationFilter;
import com.web.service.Impl.UserDetailsServiceImpl;

import io.jsonwebtoken.lang.Arrays;



@Configuration
public class SecurityConfig {
	
	private final CustomAuthenticationEntryPoint authenticationEntryPoint;
	public SecurityConfig(CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }
	
	 @Bean
	 public JwtAuthenticationFilter jwtAuthenticationFilter() {
		 return new JwtAuthenticationFilter();
	 }

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint handler;

//    @Autowired
//    private JwtAuthenticationFilter jwtAuthenticationFilter;  // Autowired olarak alıyoruz

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();

                    //      config.addAllowedOrigin("https://127.0.0.1:5500"); // Sadece frontend adresini ekledik
              
                    config.setAllowedOrigins(List.of("https://localhost:8443", "https://127.0.0.1:5500")); // Doğru kullanım
                    config.addAllowedMethod("GET");
                    config.addAllowedMethod("POST");
                    config.addAllowedMethod("PUT");
                    config.addAllowedMethod("PATCH");
                    config.addAllowedMethod("DELETE");
                    config.addAllowedMethod("OPTIONS");
                    config.addAllowedHeader("*");
                    config.setAllowCredentials(true); // Kimlik bilgilerini (çerezler gibi) kabul et
                    config.setExposedHeaders(List.of("Set-Cookie"));
                    config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
                    // CORS yapılandırmasını belirli URL'lere uygulamak için kaynak nesnesi oluşturulur
                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", config);  // Bu ayar, tüm yol (endpoint) için geçerli olacaktır
                
                    return config;
                }))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(handling -> handling.authenticationEntryPoint(handler))
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
		                		.requestMatchers("/auth/**").permitAll()
		                        .requestMatchers("/auth/logout").authenticated()
		                        .requestMatchers("/auth/disable/").authenticated()
		                        
		                        .requestMatchers("/api/sessions/**").permitAll()
		                       
		                        
		                        .requestMatchers("/api/statistics").permitAll()
                        
                                
                                
                                

                                .requestMatchers("/comments/list/{contentId}").authenticated()
                                .requestMatchers("/comments/{contentId}/comment-count").authenticated()
                                .requestMatchers("/comments/public/**").permitAll()
                                .requestMatchers("/comments/{contentId}").authenticated()
                                .requestMatchers("/comments/comment-list").authenticated()

                                .requestMatchers("/contents/top10").permitAll()
                                .requestMatchers("/contents/top10/likes").permitAll()
                                .requestMatchers("/contents/top10/comments").permitAll()
                                .requestMatchers("/contents/top10/views").permitAll()
                                
//                                .requestMatchers("/contents/top10/private").authenticated()
//                                .requestMatchers("/contents/top10/likes/private").authenticated()
//                                .requestMatchers("/contents/top10/comments/private").authenticated()
//                                .requestMatchers("/contents/top10/views/private").authenticated()
                                
                                
                                .requestMatchers("/contents/top10/private").permitAll()
                                .requestMatchers("/contents/top10/likes/private").permitAll()
                                .requestMatchers("/contents/top10/comments/private").permitAll()
                                .requestMatchers("/contents/top10/views/private").permitAll()

                                .requestMatchers("/contents/last-week").authenticated()  
                                .requestMatchers("/contents/last-week/public").permitAll()

                                .requestMatchers("/contents/recent-four").permitAll()
//                                .requestMatchers("/contents/recent-four/private").authenticated()  
                                .requestMatchers("/contents/recent-four/private").permitAll()            
                                .requestMatchers("/contents/mostViewedContent").permitAll()
                                
                                .requestMatchers("/contents/webdesign").authenticated()   
                                .requestMatchers("/contents/public/**").permitAll()  
                                .requestMatchers("/contents/html").authenticated()
                                .requestMatchers("/contents/css").authenticated()
                                .requestMatchers("/contents/backendteknolojileri").authenticated()
                                .requestMatchers("/contents/javascript").authenticated()
                                .requestMatchers("/contents/veritabani").authenticated()
                                .requestMatchers("/contents/api").authenticated()
                                .requestMatchers("/contents/hosting").authenticated()
                                .requestMatchers("/contents/uiux").authenticated()
                                .requestMatchers("/contents/tasarimaraclari").authenticated()
                                .requestMatchers("/contents/webperformans").authenticated()
                                .requestMatchers("/contents/webguvenlik").authenticated()
                                .requestMatchers("/contents/userContents").authenticated()
                                .requestMatchers("/contents/userContents").authenticated()
                                .requestMatchers("/contents/statistics").authenticated()
                                
                                .requestMatchers("/contents/{contentId}").authenticated()
                                .requestMatchers("/contents/guest/**").permitAll()
                            
                                .requestMatchers("/contents/{userId}").authenticated()
                                .requestMatchers("/contents/create").authenticated()
                                
                               
                                
                                
                                
                                



                                .requestMatchers(HttpMethod.PUT, "/like/{contentId}").authenticated()

                                .requestMatchers("/like/{contentId}").authenticated()
                                .requestMatchers("/like/{contentId}/status").authenticated()
                                .requestMatchers("/like/{contentId}/like-count").authenticated()
                                .requestMatchers("/like/public/**").permitAll()
                                .requestMatchers("/like/like-list").authenticated()




                                .requestMatchers("/me").authenticated() // /me endpoint'ini koruma altına al
                
                                .requestMatchers("/users/{userId}").authenticated()
                                .requestMatchers("/users/author/{userId}").authenticated() //  
                                .requestMatchers("/users/public/**").permitAll()
                                .requestMatchers("/users/change-password").authenticated()
                                .requestMatchers("/users/delete-account").authenticated()


                                .requestMatchers("/uploads/public/**").permitAll() // Upload dizinine public erişim izni ver
                                .requestMatchers("/uploads/private/**").authenticated()


                                
                );

        
        // JWT filtreyi ekliyoruz
        httpSecurity.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        httpSecurity.exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint)); // Özel hata yöneticisi
        return httpSecurity.build();

    }

}

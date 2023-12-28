package omare.com.mx.com.demo.reactive.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import static org.springframework.security.config.Customizer.withDefaults;

//Clase S7
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@AllArgsConstructor
public class WebSecurityConfig {

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	private final ReactiveAuthenticationManager authenticationManager;
	
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http
				.requestCache(withDefaults())
						.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
				.exceptionHandling(exception -> exception.accessDeniedHandler(
								new CustomAccessDeniedHandler())
						.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
				.csrf(withDefaults())
				.formLogin(withDefaults())
				.httpBasic(withDefaults())
						.logout(withDefaults())
				.authenticationManager(authenticationManager)
				.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
				.authorizeExchange((authorize) -> authorize
				.pathMatchers(HttpMethod.OPTIONS).permitAll()
				//SWAGGER PARA SPRING SECURITY
				.pathMatchers("/swagger-resources/**").permitAll()
				.pathMatchers("/swagger-ui.html").permitAll()
				.pathMatchers("/webjars/**").permitAll()
				//SWAGGER PARA SPRING SECURITY
				.pathMatchers("/login").permitAll()
				.pathMatchers("/v2/login").permitAll()
				.pathMatchers("/v2/**").authenticated()
				//.pathMatchers("/v2/**").hasAnyAuthority("ADMIN")
				/*.pathMatchers("/v2/**")
					.access((mono, context) -> mono
	                        .map(auth -> auth.getAuthorities()
	                        		.stream()
	                                .filter(e -> e.getAuthority().equals("ADMIN"))
	                                .count() > 0)
	                        .map(AuthorizationDecision::new)
	                )*/
				.pathMatchers("/plates/**").authenticated()
				.pathMatchers("/clients/**").authenticated()
				.pathMatchers("/invoices/**").authenticated()
				.pathMatchers("/backpressure/**").permitAll()
				.pathMatchers("/users/**").authenticated()
				.pathMatchers("/menus/**").authenticated()
				.anyExchange().authenticated());
		return http.build();
	}
}

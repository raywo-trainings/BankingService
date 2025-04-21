package de.raywotrainings.banking.bankingservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.stream.Stream;

import static java.util.Arrays.stream;


@Configuration
public class CorsConfiguration {

  @Bean
  public WebMvcConfigurer corsConfigurer(final CorsConfigurationData allowed) {
    return new WebMvcConfigurer() {

      @Override
      public void addCorsMappings(@NonNull final CorsRegistry registry) {
        registry.addMapping("/**")
            .exposedHeaders(
                HttpHeaders.LOCATION,
                HttpHeaders.LINK
            )
            // allow all HTTP request methods
            .allowedMethods(
                stream(RequestMethod.values())
                    .map(Enum::name)
                    .toArray(String[]::new)
            )
            // allow the commonly used headers
            .allowedHeaders(
                Stream.concat(
                    stream(new String[]{
                        HttpHeaders.ORIGIN,
                        HttpHeaders.CONTENT_TYPE,
                        HttpHeaders.CONTENT_LANGUAGE,
                        HttpHeaders.ACCEPT,
                        HttpHeaders.ACCEPT_LANGUAGE,
                        HttpHeaders.IF_MATCH,
                        HttpHeaders.IF_NONE_MATCH
                    }),
                    stream(allowed.getHeaders())
                ).toArray(String[]::new)
            )
            // this is stage specific
            .allowedOrigins(allowed.getOrigins())
            .allowCredentials(allowed.isCredentials());
      }
    };
  }

}


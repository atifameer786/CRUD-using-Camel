
package sample.camel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication(scanBasePackages = {"sample.camel"})
public class RestApplication {


    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }
        @Bean
        public RestTemplate getRestTemplate() {
            return new RestTemplate();
        }
    }


 

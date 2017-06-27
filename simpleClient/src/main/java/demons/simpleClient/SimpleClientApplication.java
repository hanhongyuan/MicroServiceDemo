package demons.simpleClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class SimpleClientApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "simple-client-server");
        SpringApplication.run(SimpleClientApplication.class, args);
    }
}

package demons.complicatedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


/**
 * Created by demons on 2017/7/6.
 */
@Component
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    @Autowired
    @Qualifier("special")
    RestTemplate restTemplate;
    private Logger log = LoggerFactory.getLogger(LoggingRequestInterceptor.class);
    @Value("${spring.application.name}")
    private String name;

    @LoadBalanced
    @Bean(name = "special")
    RestTemplate specialRestTemplate() {
        return new RestTemplate();
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        ClientHttpResponse response = execution.execute(request, body);

        log(request, body, response);

        return response;
    }

    private void log(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
        //do logging
        String from = name;
        String to = request.getURI().
                toString().
                replace("http://", "").
                replace("http:// www.", "").
                replace("www.", "").
                replace("/", "%20").
                toLowerCase();

        System.out.println(from);
        System.out.println(to);

        restTemplate.postForObject("http://trace-callback-service/" + from + "/" + to, null, Object.class);
    }
}

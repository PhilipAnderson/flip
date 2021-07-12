package flip.raspberry;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.util.ClassUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.beans.factory.config.BeanDefinition;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonTypeName;

// import org.springframework.http.codec.json.Jackson2JsonDecoder;


@Configuration
class WebClientConfig {

    @Autowired Environment env;

    @Bean
    WebClient webClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(JsonTypeName.class));
        for(BeanDefinition candidate : provider.findCandidateComponents("flip.common.entities")) {
            objectMapper.registerSubtypes(ClassUtils.resolveClassName(candidate.getBeanClassName(), ClassUtils.getDefaultClassLoader()));
        }

        return webClientBuilder
            // .clientConnector(new ReactorClientHttpConnector(
            //     HttpClient.create().wiretap(true)
            // ))
            .baseUrl(env.getRequiredProperty("flip.api.url"))
            .defaultHeaders(header -> header
                            .setBasicAuth(env.getRequiredProperty("flip.api.username"),
                                          env.getRequiredProperty("flip.api.password")
                                          )
                            )
            .build();
    }
}

package flip.server;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.ui.Model;


import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.annotation.JsonTypeName;

import flip.server.controllers.TrialResultController;
import flip.server.controllers.LogMessageController;


@Controller
public class MvcConfig implements WebMvcConfigurer {

    @Autowired TrialResultController trialResultController;
    @Autowired LogMessageController logMessageController;

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/config").setViewName("config");
        registry.addViewController("/birds").setViewName("birds");
        registry.addViewController("/admin").setViewName("admin");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        converters.stream()
            .filter(converter -> AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converter.getClass()))
            .map(converter -> (AbstractJackson2HttpMessageConverter)converter)
            .map(converter -> converter.getObjectMapper())
            .forEach(objectMapper -> {

                    ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
                    provider.addIncludeFilter(new AnnotationTypeFilter(JsonTypeName.class));
                    for(BeanDefinition candidate : provider.findCandidateComponents("flip.common.entities")) {
                        objectMapper.registerSubtypes(ClassUtils.resolveClassName(candidate.getBeanClassName(), ClassUtils.getDefaultClassLoader()));
                    }

                    objectMapper.configure(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL, true);
                });
    }


    @GetMapping("/")
    public String showHome(Model model) {
        model.addAttribute("recentTrialResults", trialResultController.recent());
        model.addAttribute("recentLogMessages", logMessageController.recent());
        return "index";
    }
}

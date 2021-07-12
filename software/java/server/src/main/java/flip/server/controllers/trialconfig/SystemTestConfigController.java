package flip.server.controllers.trialconfig;

import flip.common.entities.trial.config.TrialConfig;
import flip.common.entities.trial.config.SystemTestConfig;
import flip.common.entities.trial.Delay;

import javax.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.stereotype.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


interface SystemTestConfigRepository extends CrudRepository<SystemTestConfig, Long> {}


@Controller
@RequestMapping("/config/system-test")
public class SystemTestConfigController {


    @Autowired TrialConfigRepository repository;
    @Autowired SystemTestConfigRepository systemTestConfigRepository;


    @GetMapping("/create")
    public String showCreateForm(SystemTestConfig systemTestConfig, Model model) {
        model.addAttribute("systemTestConfig", systemTestConfig);
        model.addAttribute("updating", false);
        return "config/forms/system-test";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        SystemTestConfig config = systemTestConfigRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Config Id: " + id));

        model.addAttribute("systemTestConfig", config);
        model.addAttribute("updating", true);
        return "config/forms/system-test";
    }

    @PostMapping("/create")
    public String createConfig(
                               @Valid SystemTestConfig systemTestConfig,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("updating", false);
            return "config/forms/system-test";
        }

        repository.save(systemTestConfig);
        return "redirect:/config";
    }

    @PostMapping("/update/{id}")
    public String updateConfig(
                               @PathVariable("id") long id,
                               @Valid SystemTestConfig systemTestConfig,
                               BindingResult result,
                               Model model) {

        if (result.hasErrors()) {
            model.addAttribute("updating", true);
            return "config/forms/system-test";
        }

        repository.save(systemTestConfig);
        return "redirect:/config";
    }
}

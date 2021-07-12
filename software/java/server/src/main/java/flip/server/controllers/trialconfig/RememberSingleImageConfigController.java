package flip.server.controllers.trialconfig;

import flip.common.entities.trial.config.TrialConfig;
import flip.common.entities.trial.config.RememberSingleImageConfig;
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


interface RememberSingleImageConfigRepository extends CrudRepository<RememberSingleImageConfig, Long> {}


@Controller
@RequestMapping("/config/remember-single-image")
public class RememberSingleImageConfigController {


    @Autowired TrialConfigRepository repository;
    @Autowired RememberSingleImageConfigRepository rememberSingleImageConfigRepository;


    @GetMapping("/create")
    public String showCreateForm(RememberSingleImageConfig rememberSingleImageConfig, Model model) {
        model.addAttribute("rememberSingleImageConfig", rememberSingleImageConfig);
        model.addAttribute("updating", false);
        return "config/forms/remember-single-image";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        RememberSingleImageConfig config = rememberSingleImageConfigRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Config Id: " + id));

        model.addAttribute("rememberSingleImageConfig", config);
        model.addAttribute("updating", true);
        return "config/forms/remember-single-image";
    }

    @PostMapping("/create")
    public String createConfig(
                               @Valid RememberSingleImageConfig rememberSingleImageConfig,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("updating", false);
            return "config/forms/remember-single-image";
        }

        repository.save(rememberSingleImageConfig);
        return "redirect:/config";
    }

    @PostMapping("/update/{id}")
    public String updateConfig(
                               @PathVariable("id") long id,
                               @Valid RememberSingleImageConfig rememberSingleImageConfig,
                               BindingResult result,
                               Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("updating", true);
            return "config/forms/remember-single-image";
        }

        repository.save(rememberSingleImageConfig);
        return "redirect:/config";
    }
}

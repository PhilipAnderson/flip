package flip.server.controllers.trialconfig;

import flip.common.entities.trial.config.TrialConfig;
import flip.common.entities.trial.config.SingleResponseConfig;
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


@Controller
@RequestMapping("/config")
public class TrialConfigController {

    @Autowired TrialConfigRepository repository;
    @Autowired SingleResponseConfigRepository singleResponseConfigRepository;
    @Autowired SystemTestConfigRepository systemTestConfigRepository;
    @Autowired RememberSingleImageConfigRepository rememberSingleImageConfigRepository;


    @GetMapping("/delete/{id}")
    public String deleteConfig(@PathVariable("id") long id) {
        TrialConfig config = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Trial Config Id: " + id));

        repository.delete(config);
        return "redirect:/config";
    }


    @GetMapping
    public String index(Model model) {
        model.addAttribute("singleResponseConfigs", singleResponseConfigRepository.findAll());
        model.addAttribute("systemTestConfigs", systemTestConfigRepository.findAll());
        model.addAttribute("rememberSingleImageConfigs", rememberSingleImageConfigRepository.findAll());
        return "config/index";
    }
}

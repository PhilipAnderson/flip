package flip.server.controllers.trialconfig;

import flip.common.entities.trial.config.TrialConfig;
import flip.common.entities.trial.config.SingleResponseConfig;
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


interface SingleResponseConfigRepository extends CrudRepository<SingleResponseConfig, Long> {}


@Controller
@RequestMapping("/config/single-response")
public class SingleResponseConfigController {


    @Autowired TrialConfigRepository repository;
    @Autowired SingleResponseConfigRepository singleResponseConfigRepository;


    @GetMapping("/create")
    public String showCreateForm(SingleResponseConfig singleResponseConfig, Model model) {
        model.addAttribute("singleResponseConfig", singleResponseConfig);
        model.addAttribute("updating", false);
        return "config/forms/single-response";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        SingleResponseConfig config = singleResponseConfigRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Config Id: " + id));

        model.addAttribute("singleResponseConfig", config);
        model.addAttribute("updating", true);
        return "config/forms/single-response";
    }

    @PostMapping("/create")
    public String createConfig(
                               @Valid SingleResponseConfig singleResponseConfig,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("updating", false);
            return "config/forms/single-response";
        }

        repository.save(singleResponseConfig);
        return "redirect:/config";
    }

    @PostMapping("/update/{id}")
    public String updateConfig(
                               @PathVariable("id") long id,
                               @Valid SingleResponseConfig singleResponseConfig,
                               BindingResult result,
                               Model model) {

        if (result.hasErrors()) {
            model.addAttribute("updating", true);
            return "config/forms/single-response";
        }

        repository.save(singleResponseConfig);
        return "redirect:/config";
    }
}

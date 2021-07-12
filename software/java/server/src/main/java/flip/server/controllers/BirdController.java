package flip.server.controllers;

import flip.common.entities.Bird;

import flip.server.controllers.trialconfig.TrialConfigRepository;

import java.util.Optional;

import javax.validation.Valid;


import org.springframework.validation.BindingResult;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.stereotype.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RepositoryRestResource(exported = false)
interface BirdRepository extends CrudRepository<Bird, Long> {
    Bird findByTagId(String tagId);
}

@Controller
@RequestMapping("/birds")
public class BirdController {

    Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired TrialConfigRepository trialConfigRepository;
    @Autowired BirdRepository repository;

    @ResponseBody
    @GetMapping("/by-tag-id/{tagId}")
    public Bird findByTagId(@PathVariable("tagId") String tagId) {
        return repository.findByTagId(tagId);
    }

    
    @GetMapping("/create")
    public String showCreateBirdForm(Bird bird, Model model) {
        model.addAttribute("trialConfigs", trialConfigRepository.findAll());
        return "birds/create";
    }

    @PostMapping("/create")
    public String createBird(@Valid Bird bird, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "birds/create";
        }

        repository.save(bird);
        return "redirect:/birds";
    }

    @GetMapping("/update/{id}")
    public String showUpdateBirdForm(@PathVariable("id") long id, Model model) {
        Bird bird = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Bird Id: " + id));

        model.addAttribute("trialConfigs", trialConfigRepository.findAll());
        model.addAttribute("bird", bird);
        return "birds/update";
    }

    @PostMapping("/update/{id}")
    public String updateBird(@PathVariable("id") long id, @Valid Bird bird, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "birds/update";
        }

        repository.save(bird);
        return "redirect:/birds";
    }

    @GetMapping("/delete/{id}")
    public String deleteBird(@PathVariable("id") long id) {
        Bird bird = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Bird Id:" + id));

        repository.delete(bird);
        return "redirect:/birds";
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("birds", repository.findAll());
        return "birds/index";
    }
}

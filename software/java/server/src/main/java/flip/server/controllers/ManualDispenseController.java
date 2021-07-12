package flip.server.controllers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;



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


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import com.jcabi.ssh.Shell;
import com.jcabi.ssh.Ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
@RequestMapping("/manual-dispense")
public class ManualDispenseController {

    Logger LOG = LoggerFactory.getLogger(getClass());

    Map<String, String> addresses = Map
        .of(
            "flip-0001", "172.20.76.44",
            "flip-0002", "172.20.76.47",
            "flip-0003", "172.20.76.48"
            );


    @PostMapping("/{box-id}")
    public String manualDispense(@PathVariable("box-id") String boxId) {

        try {
            String address = addresses.get(boxId);

            LOG.info("Sending manual reward request to {}/{}.", boxId, address);

            Shell shell = new Ssh(
                                  address,
                                  22,
                                  "flip",
                                  Files.readString(Path.of("/home/philip/.ssh/id_rsa")));

            int exitCode = new Shell.Empty(shell).exec("pidof java | xargs kill -s SIGUSR2");

            LOG.info("Got exit code {}/{}/{}.", boxId, address, exitCode);

            return "redirect:/admin";
            // return Optional.ofNullable(exitCode);

        } catch (Exception e) {

            LOG.error("Failed to send manual reward request to {}.", boxId);
            return "redirect:/admin";

            // return Optional.empty();
        }
    }
}

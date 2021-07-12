package flip.server.controllers;

import java.time.LocalDateTime;
import java.time.Duration;

import flip.common.entities.LogMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RepositoryRestResource(exported = false)
interface LogMessageRepository extends CrudRepository<LogMessage, Long> {
    Iterable<LogMessage> findByTimeAfterOrderByTimeAsc(LocalDateTime time);
}

@RestController
@RequestMapping("/log-messages")
public class LogMessageController {

    Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired LogMessageRepository repository;


    public Iterable<LogMessage> recent() {
        return repository.findByTimeAfterOrderByTimeAsc(LocalDateTime.now().minus(Duration.ofDays(1)));
    }

    @PostMapping
    void save(@RequestBody LogMessage logMessage) {
        logMessage = repository.save(logMessage);
    }
}

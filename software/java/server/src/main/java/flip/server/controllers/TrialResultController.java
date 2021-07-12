package flip.server.controllers;

import java.time.LocalDateTime;
import java.time.Duration;

import java.io.Writer;
import java.io.OutputStreamWriter;

import flip.common.entities.trial.result.TrialResult;
import flip.common.entities.trial.result.SystemTestResult;
import flip.common.entities.trial.result.SingleResponseResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.format.annotation.DateTimeFormat;

import javax.servlet.http.HttpServletResponse;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RepositoryRestResource(exported = false)
interface TrialResultRepository extends CrudRepository<TrialResult, Long> {
    Iterable<TrialResult> findByBeginTimeAfterOrderByBeginTimeAsc(LocalDateTime time);
}

@RepositoryRestResource(exported = false)
interface SystemTestResultRepository extends CrudRepository<SystemTestResult, Long> {
    Iterable<SystemTestResult> findByBeginTimeBetweenOrderByBeginTimeAsc(LocalDateTime from, LocalDateTime until);
}

@RepositoryRestResource(exported = false)
interface SingleResponseResultRepository extends CrudRepository<SingleResponseResult, Long> {
    Iterable<SingleResponseResult> findByBeginTimeBetweenOrderByBeginTimeAsc(LocalDateTime from, LocalDateTime until);
}

@RestController
@RequestMapping("/trial-results")
public class TrialResultController {

    Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired TrialResultRepository repository;
    @Autowired SystemTestResultRepository systemTestResultRepository;
    @Autowired SingleResponseResultRepository singleResponseResultRepository;
    
    public Iterable<TrialResult> recent() {
        return repository.findByBeginTimeAfterOrderByBeginTimeAsc(LocalDateTime.now().minus(Duration.ofDays(1)));
    }

    @PostMapping
    void saveAll(@RequestBody Iterable<TrialResult> trialResults) {
        trialResults = repository.saveAll(trialResults);
    }

    @PostMapping("download")
    void download(
                  @RequestParam(name="type", required=true)
                  String type,

                  @RequestParam(name="from", required=true)
                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                  LocalDateTime from,

                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                  @RequestParam(name="until", required=true)
                  LocalDateTime until,

                  HttpServletResponse response
                  ) {

        LOG.info("download from: " + from + " until: " + until);

        if ("system-test".equals(type)) {

            Iterable<SystemTestResult> results = systemTestResultRepository
                .findByBeginTimeBetweenOrderByBeginTimeAsc(from, until);

            response.setHeader("Content-Disposition", "attachment; filename=system-test.csv");
            response.setHeader("type", "application/force-download");

            try {
                Writer writer = new OutputStreamWriter(response.getOutputStream());
                StatefulBeanToCsv<SystemTestResult> csv = new StatefulBeanToCsvBuilder<SystemTestResult>(writer).build();

                csv.write(results.iterator());
                writer.flush();
                writer.close();

            } catch (Exception e) {
                LOG.error("Failed to make CSV.", e);
            }

        } else if ("single-response".equals(type)) {

            Iterable<SingleResponseResult> results = singleResponseResultRepository
                .findByBeginTimeBetweenOrderByBeginTimeAsc(from, until);

            response.setHeader("Content-Disposition", "attachment; filename=single-response.csv");
            response.setHeader("type", "application/force-download");

            try {
                Writer writer = new OutputStreamWriter(response.getOutputStream());
                StatefulBeanToCsv<SingleResponseResult> csv = new StatefulBeanToCsvBuilder<SingleResponseResult>(writer).build();

                csv.write(results.iterator());
                writer.flush();
                writer.close();

            } catch (Exception e) {
                LOG.error("Failed to make CSV.", e);
            }

        } else {
            LOG.error("Request to download unknown result type: {}", type);
        }
    }
}

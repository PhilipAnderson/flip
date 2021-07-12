package flip.raspberry;

import flip.common.entities.LogMessage;
// import flip.common.entities.ScaleReading;
import flip.common.entities.Bird;
import flip.common.entities.trial.config.TrialConfig;
import flip.common.entities.trial.result.TrialResult;

import flip.raspberry.trial.TrialEvent;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import org.springframework.context.annotation.Bean;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class Remote {
    
    final Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${flip.api.username}")
    String flipBoxId;

    @Autowired
    WebClient webClient;

    final BlockingQueue<TrialResult>  trialResults  = new LinkedBlockingQueue<>();

    final LoadingCache<String, TrialConfig> trialConfigCache = Caffeine
        .newBuilder()
        .refreshAfterWrite(5, TimeUnit.MINUTES)
        .build(tagId -> getTrialConfigImpl(tagId).orElse(null));


    public Optional<TrialConfig> getTrialConfig(String tagId) {
        return Optional.ofNullable(trialConfigCache.get(tagId));
    }

    public Optional<TrialConfig> getTrialConfigImpl(String tagId) {

        LOG.info("Fetching trial config for {}.", tagId);

        try {
            Optional<Bird> bird = Optional
                .ofNullable(webClient
                            .get()
                            .uri("/birds/by-tag-id/" + tagId) // room for escaping?
                            .retrieve()
                            .bodyToMono(Bird.class)
                            .block()
                            );

            return bird.map(Bird::getTrialConfig);
            
        } catch (Exception e) {
            LOG.error("Failed to fetch trial config for {}.", tagId, e);
            return Optional.empty();
        }
    }

    public void save(TrialResult trialResult) {
        trialResult.setFlipBoxId(flipBoxId);
        trialResults.add(trialResult);
    }

    @Scheduled(fixedRate = 60 * 1000)
    void postDataToServer() {

        List<TrialResult> trialResults = new ArrayList<>();
        this.trialResults.drainTo(trialResults);

        if (!trialResults.isEmpty()) {
            LOG.info("Posting {} trial results to server.", trialResults.size());

            try {
                webClient
                    .post()
                    .uri("/trial-results")
                    .bodyValue(trialResults)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            } catch (Exception e) {
                LOG.warn("Failed to post trial results to server.", e);
                this.trialResults.addAll(trialResults);
            }
        }
    }


    void log(String level, String formatter, Object... args) {
        String message = MessageFormat.format(formatter, args);

        LogMessage logMessage = new LogMessage();

        logMessage.setFlipBoxId(flipBoxId);
        logMessage.setTime(LocalDateTime.now());
        logMessage.setLevel(level);
        logMessage.setMessage(message);

        // If the last passed argument is a throwable, set the message stacktrace accordingly.
        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            logMessage.setStackTrace(getStackTrace((Throwable) args[args.length - 1]));
        }

        LOG.debug("Sending remote log message: {}.", logMessage);
        ForkJoinPool.commonPool().execute(() -> post(logMessage));
    }

    public void debug(String formatter, Object... args) {
        log("DEBUG", formatter, args);
    }

    public void info(String formatter, Object... args) {
        log("INFO", formatter, args);
    }

    public void warn(String formatter, Object... args) {
        log("WARN", formatter, args);
    }

    public void error(String formatter, Object... args) {
        log("ERROR", formatter, args);
    }

    public void trace(String formatter, Object... args) {
        log("TRACE", formatter, args);
    }

    String getStackTrace(Throwable throwable) {
        StringWriter errors = new StringWriter();
        throwable.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    void post(LogMessage logMessage) {

        try {
            webClient
                .post()
                .uri("/log-messages")
                .bodyValue(logMessage)
                .retrieve()
                .toBodilessEntity()
                .block();

        } catch (Exception e) {
            LOG.warn("Failed to post log message to server.", e);
        }
    }
}

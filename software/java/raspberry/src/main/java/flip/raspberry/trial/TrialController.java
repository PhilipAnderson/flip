package flip.raspberry.trial;

import flip.common.entities.Bird;
import flip.common.entities.trial.config.TrialConfig;
import flip.common.entities.trial.result.TrialResult;

import flip.raspberry.Remote;
import flip.raspberry.ImagePane;
import flip.raspberry.USBController;
import flip.raspberry.trial.runner.TrialRunner;

import java.util.*;
import java.util.stream.*;
import java.lang.reflect.*;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public class TrialController implements Runnable {

    final Logger                  LOG = LoggerFactory.getLogger(getClass());
    Thread                  thread;
    final Map<Class, TrialRunner> trialRunners;

    @Autowired TrialEventQueue trialEventQueue;
    @Autowired ImagePane       imagePane;
    @Autowired USBController   usbController;
    @Autowired Remote          remote;


    @Autowired
    public TrialController(List<TrialRunner> trialRunners) {
        LOG.info("Spring passed {} trial runners.", trialRunners.size());
        this.trialRunners = trialRunners.stream().collect(Collectors.toMap(runner -> runner.getTrialConfigClass(), runner -> runner));
    }

    @EventListener
    public void handleContextStarted(ContextStartedEvent event) {
        thread = new Thread(this);
        thread.start();
    }

    @EventListener
    public void handleContextClosed(ContextClosedEvent event) throws InterruptedException {
        thread.interrupt();
        thread.join();
    }

    @Override
    public void run() {

        LOG.info("TrialController started.");
        remote.info("Trial controller started.");

        while (true) {
            try {
                TrialEvent event = trialEventQueue.take();

                if (event instanceof TrialEvent.TagArrived) {
                    TrialEvent.TagArrived tagArrived = (TrialEvent.TagArrived) event;
                    runTrials(tagArrived.getTag());
                }

            } catch (TrialCanceledException e) {

                // This is either caused by the bird leaving or another bird getting
                // on the platform, so restore the event so that a new trial for the
                // new bird can be started.
                trialEventQueue.untake(e.getEvent());

                TrialResult result = e.getTrial();
                result.setEndTime(LocalDateTime.now());
                result.setState(TrialResult.State.INCOMPLETE);

                LOG.info("Incomplete trial ran, result {}.", result);

                remote.save(result);

            } catch (TrialConfigException e) {
                LOG.error("Trial config exception.", e);
                remote.error("Trial config exception: {0}", e.getMessage(), e);
            } catch (InterruptedException e) {
                LOG.error("TrialController interrupted, stopping.", e);
                remote.warn("Trial controller shutting down");
                break;
            }
        }

        LOG.info("TrialController stopped.");
    }

    void runTrials(String id) throws TrialCanceledException,
                                     TrialConfigException,
                                     InterruptedException {

        TrialConfig config = remote.getTrialConfig(id).orElseThrow(() -> new TrialConfigException("Config not found: " + id));
        TrialRunner runner = loadTrialRunner(config);

        while (true) {
            trialEventQueue.removeKeyPresses();
            LOG.info("Starting trial for bird {}.", id);
            TrialResult result = runner.run(id, config);
            LOG.info("Ran trial, got result {}.", result);
            remote.save(result);
        }
    }

    TrialRunner loadTrialRunner(TrialConfig config) throws TrialConfigException {
        TrialRunner runner = trialRunners.get(config.getClass());
        if (runner == null) {
            throw new TrialConfigException("No runner for config class " + config.getClass());
        }
        return runner;
    }
}

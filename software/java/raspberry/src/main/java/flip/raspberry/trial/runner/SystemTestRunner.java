package flip.raspberry.trial.runner;

import flip.common.entities.trial.config.TrialConfig;
import flip.common.entities.trial.result.TrialResult;
import flip.common.entities.trial.config.SystemTestConfig;
import flip.common.entities.trial.result.SystemTestResult;

import flip.raspberry.ImagePane;
import flip.raspberry.TonePlayer;
import flip.raspberry.USBController;

import flip.raspberry.trial.TrialEvent;
import flip.raspberry.trial.TrialEvent.*;
import flip.raspberry.trial.TrialEventQueue;
import flip.raspberry.trial.TrialCanceledException;
import flip.raspberry.trial.TrialConfigException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public final class SystemTestRunner implements TrialRunner {

    Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired ImagePane       imagePane;
    @Autowired TrialEventQueue trialEventQueue;
    @Autowired TonePlayer      tonePlayer;
    @Autowired USBController   usbController;

    @Override
    public Class getTrialConfigClass() {
        return SystemTestConfig.class;
    }

    @Override
    public TrialResult run(String birdId, TrialConfig trialConfig) throws TrialCanceledException,
                                                                          TrialConfigException,
                                                                          InterruptedException {

        try {
            SystemTestConfig config = (SystemTestConfig) trialConfig;
            SystemTestResult result = new SystemTestResult();

            Map<Integer, Integer> pressCounts = new HashMap<>();

            result.setBirdId(birdId);
            result.setBeginTime(LocalDateTime.now());

            for (int position : config.getTestPositions()) {
                pressCounts.put(position, 0);
                imagePane.setImage(position, "0.png");
            }

            long start = System.currentTimeMillis();
            imagePane.show();

            trialEventQueue.removeKeyPresses();

            while (true) {
                TrialEvent event = trialEventQueue.take();

                if (event instanceof TagArrived) throw new TrialCanceledException(event, result);
                if (event instanceof TagRemoved) throw new TrialCanceledException(event, result);
                if (event instanceof KeyPressed) {

                    int key = ((KeyPressed)event).getKey();

                    if (pressCounts.containsKey(key)) {
                        int count = pressCounts.compute(key, (k, v) -> v + 1);
                        result.getResponses().add(new SystemTestResult.Response(key, System.currentTimeMillis() - start));

                        if (0 <= count && count <= 9) {
                            imagePane.setImage(key, "" + count + ".png");
                            tonePlayer.play(config.getTone());
                        }

                        if (pressCounts.values().stream().allMatch(c -> c >= 9)) {
                            usbController.reward();
                            result.setState(TrialResult.State.PASSED);
                            result.setEndTime(LocalDateTime.now());

                            for (int position : config.getTestPositions()) {
                                imagePane.setImage(position, "damian.jpg");
                            }

                            Thread.sleep(2000);
                            return result;
                        }
                    }
                }
            }
        } finally {
            imagePane.clear();
        }
    }
}

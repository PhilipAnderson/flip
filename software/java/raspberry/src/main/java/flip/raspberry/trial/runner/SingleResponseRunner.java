package flip.raspberry.trial.runner;

import java.util.Random;
import java.time.LocalDateTime;

import flip.common.entities.trial.config.TrialConfig;
import flip.common.entities.trial.result.TrialResult;
import flip.common.entities.trial.config.SingleResponseConfig;
import flip.common.entities.trial.result.SingleResponseResult;

import flip.raspberry.trial.TrialEvent;
import flip.raspberry.trial.TrialEventQueue;
import flip.raspberry.trial.TrialCanceledException;
import flip.raspberry.trial.TrialConfigException;
import flip.raspberry.trial.TrialEvent.*;

import flip.raspberry.ImagePane;
import flip.raspberry.TonePlayer;
import flip.raspberry.USBController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public final class SingleResponseRunner implements TrialRunner {

    Logger LOG = LoggerFactory.getLogger(getClass());
    Random RNG = new Random();

    @Autowired ImagePane       imagePane;
    @Autowired TrialEventQueue trialEventQueue;
    @Autowired TonePlayer      tonePlayer;
    @Autowired USBController   usbController;

    @Override
    public Class getTrialConfigClass() {
        return SingleResponseConfig.class;
    }

    @Override
    public TrialResult run(String birdId, TrialConfig trialConfig) throws TrialCanceledException,
                                                                          TrialConfigException,
                                                                          InterruptedException {

        try {
            SingleResponseConfig config = (SingleResponseConfig) trialConfig;
            SingleResponseResult result = new SingleResponseResult();

            result.setBirdId(birdId);
            result.setBeginTime(LocalDateTime.now());

            long start = System.currentTimeMillis();

            imagePane.setImage(config.getImagePosition(), config.getImage());
            imagePane.show();

            trialEventQueue.removeKeyPresses();

            while (true) {
                TrialEvent event = trialEventQueue.take();

                if (event instanceof TagArrived) throw new TrialCanceledException(event, result);
                if (event instanceof TagRemoved) throw new TrialCanceledException(event, result);
                if (event instanceof KeyPressed) {

                    int key = ((KeyPressed)event).getKey();

                    if (key == config.getImagePosition()) {

                        result.setResponseTime(System.currentTimeMillis() - start);
                        usbController.reward();
                        tonePlayer.play(config.getTone());

                        result.setState(TrialResult.State.PASSED);
                        result.setEndTime(LocalDateTime.now());

                        imagePane.clear();
                        config.getDelay().perform(RNG);

                        return result;
                    }
                }
            }
        } finally {
            imagePane.clear();
        }
    }
}

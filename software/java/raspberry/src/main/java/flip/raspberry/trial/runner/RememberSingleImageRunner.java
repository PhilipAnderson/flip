package flip.raspberry.trial.runner;

import java.util.Random;
import java.time.LocalDateTime;

import flip.common.entities.trial.config.TrialConfig;
import flip.common.entities.trial.result.TrialResult;
import flip.common.entities.trial.config.RememberSingleImageConfig;
import flip.common.entities.trial.result.RememberSingleImageResult;

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
public final class RememberSingleImageRunner implements TrialRunner {

    Logger LOG = LoggerFactory.getLogger(getClass());
    Random RNG = new Random();
    
    @Autowired ImagePane       imagePane;
    @Autowired TrialEventQueue trialEventQueue;
    @Autowired TonePlayer      tonePlayer;
    @Autowired USBController   usbController;

    @Override
    public Class getTrialConfigClass() {
        return RememberSingleImageConfig.class;
    }

    @Override
    public TrialResult run(String birdId, TrialConfig trialConfig) throws TrialCanceledException,
                                                                          TrialConfigException,
                                                                          InterruptedException {

        try {
            RememberSingleImageConfig config = (RememberSingleImageConfig) trialConfig;
            RememberSingleImageResult result = new RememberSingleImageResult();

            result.setBirdId(birdId);
            result.setBeginTime(LocalDateTime.now());

            long start = System.currentTimeMillis();

            int imageCount = config.getImages().size();
            int imageAIndex = RNG.nextInt(imageCount);
            int imageBIndex = imageAIndex;

            while (imageAIndex == imageBIndex) {
                imageBIndex = RNG.nextInt(imageCount);
            }

            int positionCount = config.getTestPositions().size();
            int positionAIndex = RNG.nextInt(positionCount);
            int positionBIndex = positionAIndex;

            while (positionAIndex == positionBIndex) {
                positionBIndex = RNG.nextInt(positionCount);
            }

            String imageA = config.getImages().get(imageAIndex);
            String imageB = config.getImages().get(imageBIndex);

            int imageAPosition = config.getTestPositions().get(positionAIndex);
            int imageBPosition = config.getTestPositions().get(positionBIndex);

            result.setImageA(imageA);
            result.setImageB(imageB);
            result.setImageAPosition(imageAPosition);
            result.setImageBPosition(imageBPosition);

            imagePane.setImage(config.getInitialImagePosition(), imageA);
            imagePane.show();

            config.getShowImageDelay().perform(RNG);

            imagePane.clear();

            config.getHideImageDelay().perform(RNG);

            imagePane.setImage(imageAPosition, imageA);
            imagePane.setImage(imageBPosition, imageB);
            imagePane.show();

            trialEventQueue.removeKeyPresses();

            while (true) {
                TrialEvent event = trialEventQueue.take();

                if (event instanceof TagArrived) throw new TrialCanceledException(event, result);
                if (event instanceof TagRemoved) throw new TrialCanceledException(event, result);
                if (event instanceof KeyPressed) {

                    int key = ((KeyPressed)event).getKey();

                    if (key == imageAPosition) {

                        result.setResponseTime(System.currentTimeMillis() - start);

                        usbController.reward();
                        tonePlayer.play(config.getPassedTone());

                        result.setState(TrialResult.State.PASSED);
                        result.setEndTime(LocalDateTime.now());

                        imagePane.clear();
                        config.getDelayBetweenTrials().perform(RNG);

                        return result;
                    }

                    if (key == imageBPosition) {

                        result.setResponseTime(System.currentTimeMillis() - start);
                        usbController.reward();
                        tonePlayer.play(config.getFailedTone());

                        result.setState(TrialResult.State.FAILED);
                        result.setEndTime(LocalDateTime.now());

                        imagePane.clear();
                        config.getDelayBetweenTrials().perform(RNG);

                        return result;
                    }
                }
            }
        } finally {
            imagePane.clear();
        }
    }
}

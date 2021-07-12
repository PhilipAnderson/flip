package flip.raspberry.trial.runner;

import flip.common.entities.trial.config.TrialConfig;
import flip.common.entities.trial.result.TrialResult;

import flip.raspberry.trial.TrialCanceledException;
import flip.raspberry.trial.TrialConfigException;

// import java.util.concurrent.Executors;
// import java.util.concurrent.ScheduledExecutorService;

public interface TrialRunner {
    TrialResult run(String birdId, TrialConfig config) throws TrialCanceledException,
                                                              TrialConfigException,
                                                              InterruptedException;

    // static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    Class getTrialConfigClass();
}

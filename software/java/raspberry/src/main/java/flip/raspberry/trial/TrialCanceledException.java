package flip.raspberry.trial;

import flip.common.entities.trial.result.TrialResult;

import lombok.Value;
import lombok.EqualsAndHashCode;


@Value
@EqualsAndHashCode(callSuper=false)
public class TrialCanceledException extends Exception {
    TrialEvent  event;
    TrialResult trial;
}

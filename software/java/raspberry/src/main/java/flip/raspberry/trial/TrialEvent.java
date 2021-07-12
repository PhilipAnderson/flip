package flip.raspberry.trial;

import flip.raspberry.USBControllerMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;


public abstract class TrialEvent implements USBControllerMessage {

    @Override
    public void accept(USBControllerMessage.Visitor visitor) {
        visitor.accept(this);
    }

    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class TagArrived extends TrialEvent {
        String tag;
    }

    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class TagRemoved extends TrialEvent {
    }

    @Data
    @EqualsAndHashCode(callSuper=false)
    public static class KeyPressed extends TrialEvent {
        int key;
    }
}

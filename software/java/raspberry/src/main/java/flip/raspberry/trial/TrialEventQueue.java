package flip.raspberry.trial;

import java.util.concurrent.LinkedBlockingDeque;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Bean;



// TODO make bean not component
@Component
public class TrialEventQueue extends LinkedBlockingDeque<TrialEvent> {

    void untake(TrialEvent event) {
        addFirst(event);
    }

    public void removeKeyPresses() {
        removeIf(event -> event.getClass().equals(TrialEvent.KeyPressed.class));
    }
}

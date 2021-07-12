package flip.raspberry;

import flip.raspberry.trial.TrialEvent;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


// This file must contain all the message types that the controller can send.
// The json type property in the messages is used to deserialize the correct type.

@JsonTypeInfo(
    use      = JsonTypeInfo.Id.NAME,
    include  = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @Type(value = TrialEvent.TagArrived.class,    name = "tag_arrived"),
    @Type(value = TrialEvent.TagRemoved.class,    name = "tag_removed"),
    @Type(value = TrialEvent.KeyPressed.class,    name = "key_pressed"),
    @Type(value = DebugMessage.class,  name = "debug_message"),
    @Type(value = ErrorMessage.class,  name = "error_message")
})
public interface USBControllerMessage {

    public interface Visitor {
        public void accept(TrialEvent trialEvent);
        public void accept(DebugMessage debugMessage);
        public void accept(ErrorMessage errorMessage);
    }

    public abstract void accept(Visitor visitor);
}


@Data
class DebugMessage implements USBControllerMessage {
    String message;

    @Override
    public void accept(Visitor visitor) {
        visitor.accept(this);
    }
}

@Data
class ErrorMessage implements USBControllerMessage {
    String message;

    @Override
    public void accept(Visitor visitor) {
        visitor.accept(this);
    }
}

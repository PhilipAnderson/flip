package flip.common.entities.trial.result;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(
    use      = JsonTypeInfo.Id.NAME,
    include  = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type"
)
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class TrialResult {

    @EqualsAndHashCode.Include
    @GeneratedValue
    @Id
    Long id;

    @Basic(optional = false)
    String birdId;

    @Basic(optional = false)
    String flipBoxId;

    @NotNull
    LocalDateTime beginTime;

    @NotNull
    LocalDateTime endTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    State state;

    public static enum State {
        PASSED,
        FAILED,
        INCOMPLETE
    }

    @JsonGetter(value = "type")
    public String getType() {
        return this.getClass().getSimpleName();
    }
}

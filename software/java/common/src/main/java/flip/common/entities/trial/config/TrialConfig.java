package flip.common.entities.trial.config;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(
    use      = JsonTypeInfo.Id.NAME,
    include  = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible  = true
)
@Getter
@Setter
@NoArgsConstructor
public abstract class TrialConfig {

    @GeneratedValue
    @Id
    Long id;

    @Basic(optional = false)
    @Size(min = 1)
    @JsonProperty(required = true)
    String name;

    @JsonGetter(value = "type")
    public String getType() {
        return getClass().getSimpleName();
    }
}

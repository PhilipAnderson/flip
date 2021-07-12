package flip.common.entities.trial;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Data;


@Embeddable
@Data
public final class ToneDefinition {

    @Basic(optional = false)
    @Min(330)
    @Max(10000)
    long frequency;

    @Basic(optional = false)
    @Min(0)
    @Max(10000)
    long duration;

    @Override
    public String toString() {
        return String.format("%dhz %dms", frequency, duration);
    }
}

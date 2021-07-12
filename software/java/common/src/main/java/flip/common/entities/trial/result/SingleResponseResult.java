package flip.common.entities.trial.result;

import javax.persistence.Basic;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@JsonTypeName("SingleResponseResult")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public final class SingleResponseResult extends TrialResult {

    // Optional for no response
    @Basic(optional = true)
    Long responseTime;
}

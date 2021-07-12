package flip.common.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import flip.common.entities.trial.config.TrialConfig;

import lombok.Data;


@Entity
@Data
public final class Bird {

    @Id
    @GeneratedValue
    Long id;

    @NotNull
    @Size(min = 4, max = 40)
    String tagId;

    @NotNull
    String friendlyName;

    @NotNull
    @ManyToOne
    TrialConfig trialConfig;
}

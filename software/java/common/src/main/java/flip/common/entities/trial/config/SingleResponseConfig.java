package flip.common.entities.trial.config;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import flip.common.entities.trial.Delay;
import flip.common.entities.trial.ImagePositionConstraint;
import flip.common.entities.trial.ToneDefinition;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@JsonTypeName("SingleResponseConfig")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public final class SingleResponseConfig extends TrialConfig {

    @NotNull
    String image;

    @NotNull
    @ImagePositionConstraint
    Integer imagePosition;

    @Embedded
    @NotNull
    @Valid
    ToneDefinition tone;

    @Embedded
    @NotNull
    @Valid
    Delay delay;
}

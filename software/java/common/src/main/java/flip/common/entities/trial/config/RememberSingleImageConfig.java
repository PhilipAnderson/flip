package flip.common.entities.trial.config;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OrderColumn;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import flip.common.entities.trial.Delay;
import flip.common.entities.trial.ImagePositionConstraint;
import flip.common.entities.trial.ImagePositionListConstraint;
import flip.common.entities.trial.ToneDefinition;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@JsonTypeName("RememberSingleImageConfig")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public final class RememberSingleImageConfig extends TrialConfig {

    @NotNull
    @Size(min = 2)
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "ind")
    List<String> images = new ArrayList<>(List.of(""));

    @NotNull
    @ImagePositionConstraint
    Integer initialImagePosition;

    @NotNull
    @Size(min = 2)
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "ind")
    @ImagePositionListConstraint
    List<Integer> testPositions = new ArrayList<>(List.of(1, 2));

    @Embedded
    @NotNull
    @Valid
    Delay showImageDelay;

    @Embedded
    @NotNull
    @Valid
    Delay hideImageDelay;

    @Embedded
    @NotNull
    @Valid
    ToneDefinition passedTone;

    @Embedded
    @NotNull
    @Valid
    ToneDefinition failedTone;

    @Embedded
    @NotNull
    @Valid
    Delay delayBetweenTrials;
}

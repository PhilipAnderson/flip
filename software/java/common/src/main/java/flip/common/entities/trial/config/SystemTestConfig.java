package flip.common.entities.trial.config;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OrderColumn;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import flip.common.entities.trial.ImagePositionListConstraint;
import flip.common.entities.trial.ToneDefinition;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@JsonTypeName("SystemTestConfig")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public final class SystemTestConfig extends TrialConfig {

    @NotNull
    @Size(min = 1)
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "ind")
    @ImagePositionListConstraint
    List<Integer> testPositions = new ArrayList<>(List.of(1, 2));

    @NotNull
    @Embedded
    @Valid
    ToneDefinition tone;
}

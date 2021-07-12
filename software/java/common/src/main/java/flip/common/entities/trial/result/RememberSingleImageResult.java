package flip.common.entities.trial.result;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

import flip.common.entities.trial.ImagePositionConstraint;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@JsonTypeName("RememberSingleImageResult")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public final class RememberSingleImageResult extends TrialResult {

    @Basic
    Long showImageDelay;

    @Basic
    Long hideImageDelay;

    @Basic
    String imageA;

    @Basic
    String imageB;

    @Basic
    @ImagePositionConstraint
    Integer imageAPosition;

    @Basic
    @ImagePositionConstraint
    Integer imageBPosition;

    @Basic
    Long responseTime;
}

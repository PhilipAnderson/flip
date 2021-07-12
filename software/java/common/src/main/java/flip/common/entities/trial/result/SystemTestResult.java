package flip.common.entities.trial.result;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

import flip.common.entities.trial.ImagePositionConstraint;

import com.fasterxml.jackson.annotation.JsonTypeName;

import com.opencsv.bean.CsvIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@JsonTypeName("SystemTestResult")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public final class SystemTestResult extends TrialResult {

    // NOTE: Need to think about how to handle writing collection results to a csv when exporting,
    //       as this is just for testing I'm just ignoring it, and can just check the objects manually.
    @CsvIgnore
    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "ind")
    List<Response> responses = new ArrayList<>();

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class Response {

        @NotNull
        @ImagePositionConstraint
        Integer position;

        @Basic(optional = false)
        long time;
    }
}

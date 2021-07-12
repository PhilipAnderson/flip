package flip.common.entities;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class LogMessage {

    @Id
    @GeneratedValue
    Long id;

    @NotNull
    LocalDateTime time;

    @Basic(optional = false)
    String flipBoxId;

    @Basic(optional = false)
    String level;

    @Basic(optional = false)
    @Column(length = 10000)
    String message;

    @Basic(optional = true)
    @Column(length = 10000)
    String stackTrace;
}

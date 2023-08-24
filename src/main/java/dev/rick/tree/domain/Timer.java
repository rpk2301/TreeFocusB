package dev.rick.tree.domain;

import dev.rick.tree.domain.enumeration.TimerStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Timer.
 */
@Entity
@Table(name = "timer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Timer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "expiration_time")
    private ZonedDateTime expirationTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TimerStatus status;

    @OneToOne
    @JoinColumn(unique = true)
    private User assignedTo;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Timer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public Timer duration(Integer duration) {
        this.setDuration(duration);
        return this;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public ZonedDateTime getExpirationTime() {
        return this.expirationTime;
    }

    public Timer expirationTime(ZonedDateTime expirationTime) {
        this.setExpirationTime(expirationTime);
        return this;
    }

    public void setExpirationTime(ZonedDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public TimerStatus getStatus() {
        return this.status;
    }

    public Timer status(TimerStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TimerStatus status) {
        this.status = status;
    }

    public User getAssignedTo() {
        return this.assignedTo;
    }

    public void setAssignedTo(User user) {
        this.assignedTo = user;
    }

    public Timer assignedTo(User user) {
        this.setAssignedTo(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Timer)) {
            return false;
        }
        return id != null && id.equals(((Timer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Timer{" +
            "id=" + getId() +
            ", duration=" + getDuration() +
            ", expirationTime='" + getExpirationTime() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}

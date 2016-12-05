package de.projectride.ride.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Reservation.
 */
@Entity
@Table(name = "reservation")
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "passenger_id")
    private Long passengerId;

    @Column(name = "confirmed")
    private Boolean confirmed;

    @Column(name = "cancled")
    private Boolean cancled;

    @ManyToOne
    private Ride ride;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public Reservation passengerId(Long passengerId) {
        this.passengerId = passengerId;
        return this;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Boolean isConfirmed() {
        return confirmed;
    }

    public Reservation confirmed(Boolean confirmed) {
        this.confirmed = confirmed;
        return this;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Boolean isCancled() {
        return cancled;
    }

    public Reservation cancled(Boolean cancled) {
        this.cancled = cancled;
        return this;
    }

    public void setCancled(Boolean cancled) {
        this.cancled = cancled;
    }

    public Ride getRide() {
        return ride;
    }

    public Reservation ride(Ride ride) {
        this.ride = ride;
        return this;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Reservation reservation = (Reservation) o;
        if(reservation.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, reservation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Reservation{" +
            "id=" + id +
            ", passengerId='" + passengerId + "'" +
            ", confirmed='" + confirmed + "'" +
            ", cancled='" + cancled + "'" +
            '}';
    }
}

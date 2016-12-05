package de.projectride.ride.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Ride.
 */
@Entity
@Table(name = "ride")
public class Ride implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "start_date_time")
    private ZonedDateTime startDateTime;

    @Column(name = "flexible_start_place")
    private Integer flexibleStartPlace;

    @Column(name = "flexible_end_place")
    private Integer flexibleEndPlace;

    @Column(name = "price")
    private Float price;

    @Min(value = 1)
    @Max(value = 7)
    @Column(name = "number_of_seats")
    private Integer numberOfSeats;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "deleted")
    private Boolean deleted;

    @OneToOne
    @JoinColumn(unique = true)
    private Place startPlace;

    @OneToOne
    @JoinColumn(unique = true)
    private Place endPlace;

    @OneToMany(mappedBy = "ride")
    @JsonIgnore
    private Set<Reservation> reservations = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDriverId() {
        return driverId;
    }

    public Ride driverId(Long driverId) {
        this.driverId = driverId;
        return this;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public ZonedDateTime getStartDateTime() {
        return startDateTime;
    }

    public Ride startDateTime(ZonedDateTime startDateTime) {
        this.startDateTime = startDateTime;
        return this;
    }

    public void setStartDateTime(ZonedDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Integer getFlexibleStartPlace() {
        return flexibleStartPlace;
    }

    public Ride flexibleStartPlace(Integer flexibleStartPlace) {
        this.flexibleStartPlace = flexibleStartPlace;
        return this;
    }

    public void setFlexibleStartPlace(Integer flexibleStartPlace) {
        this.flexibleStartPlace = flexibleStartPlace;
    }

    public Integer getFlexibleEndPlace() {
        return flexibleEndPlace;
    }

    public Ride flexibleEndPlace(Integer flexibleEndPlace) {
        this.flexibleEndPlace = flexibleEndPlace;
        return this;
    }

    public void setFlexibleEndPlace(Integer flexibleEndPlace) {
        this.flexibleEndPlace = flexibleEndPlace;
    }

    public Float getPrice() {
        return price;
    }

    public Ride price(Float price) {
        this.price = price;
        return this;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public Ride numberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
        return this;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public String getDescription() {
        return description;
    }

    public Ride description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public Ride createdAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public Ride deleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Place getStartPlace() {
        return startPlace;
    }

    public Ride startPlace(Place place) {
        this.startPlace = place;
        return this;
    }

    public void setStartPlace(Place place) {
        this.startPlace = place;
    }

    public Place getEndPlace() {
        return endPlace;
    }

    public Ride endPlace(Place place) {
        this.endPlace = place;
        return this;
    }

    public void setEndPlace(Place place) {
        this.endPlace = place;
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public Ride reservations(Set<Reservation> reservations) {
        this.reservations = reservations;
        return this;
    }

    public Ride addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setRide(this);
        return this;
    }

    public Ride removeReservation(Reservation reservation) {
        reservations.remove(reservation);
        reservation.setRide(null);
        return this;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ride ride = (Ride) o;
        if(ride.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, ride.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Ride{" +
            "id=" + id +
            ", driverId='" + driverId + "'" +
            ", startDateTime='" + startDateTime + "'" +
            ", flexibleStartPlace='" + flexibleStartPlace + "'" +
            ", flexibleEndPlace='" + flexibleEndPlace + "'" +
            ", price='" + price + "'" +
            ", numberOfSeats='" + numberOfSeats + "'" +
            ", description='" + description + "'" +
            ", createdAt='" + createdAt + "'" +
            ", deleted='" + deleted + "'" +
            '}';
    }
}

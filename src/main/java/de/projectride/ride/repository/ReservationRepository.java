package de.projectride.ride.repository;

import de.projectride.ride.domain.Reservation;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Reservation entity.
 */
@SuppressWarnings("unused")
public interface ReservationRepository extends JpaRepository<Reservation,Long> {

}

package de.projectride.ride.repository;

import de.projectride.ride.domain.Ride;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Ride entity.
 */
@SuppressWarnings("unused")
public interface RideRepository extends JpaRepository<Ride,Long> {

}

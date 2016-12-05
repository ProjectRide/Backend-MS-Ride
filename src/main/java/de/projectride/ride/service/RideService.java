package de.projectride.ride.service;

import de.projectride.ride.domain.Ride;
import de.projectride.ride.repository.RideRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Ride.
 */
@Service
@Transactional
public class RideService {

    private final Logger log = LoggerFactory.getLogger(RideService.class);
    
    @Inject
    private RideRepository rideRepository;

    /**
     * Save a ride.
     *
     * @param ride the entity to save
     * @return the persisted entity
     */
    public Ride save(Ride ride) {
        log.debug("Request to save Ride : {}", ride);
        Ride result = rideRepository.save(ride);
        return result;
    }

    /**
     *  Get all the rides.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<Ride> findAll() {
        log.debug("Request to get all Rides");
        List<Ride> result = rideRepository.findAll();

        return result;
    }

    /**
     *  Get one ride by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Ride findOne(Long id) {
        log.debug("Request to get Ride : {}", id);
        Ride ride = rideRepository.findOne(id);
        return ride;
    }

    /**
     *  Delete the  ride by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Ride : {}", id);
        rideRepository.delete(id);
    }
}

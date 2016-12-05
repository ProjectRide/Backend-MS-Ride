package de.projectride.ride.service;

import de.projectride.ride.domain.Reservation;
import de.projectride.ride.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Reservation.
 */
@Service
@Transactional
public class ReservationService {

    private final Logger log = LoggerFactory.getLogger(ReservationService.class);
    
    @Inject
    private ReservationRepository reservationRepository;

    /**
     * Save a reservation.
     *
     * @param reservation the entity to save
     * @return the persisted entity
     */
    public Reservation save(Reservation reservation) {
        log.debug("Request to save Reservation : {}", reservation);
        Reservation result = reservationRepository.save(reservation);
        return result;
    }

    /**
     *  Get all the reservations.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<Reservation> findAll() {
        log.debug("Request to get all Reservations");
        List<Reservation> result = reservationRepository.findAll();

        return result;
    }

    /**
     *  Get one reservation by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Reservation findOne(Long id) {
        log.debug("Request to get Reservation : {}", id);
        Reservation reservation = reservationRepository.findOne(id);
        return reservation;
    }

    /**
     *  Delete the  reservation by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Reservation : {}", id);
        reservationRepository.delete(id);
    }
}

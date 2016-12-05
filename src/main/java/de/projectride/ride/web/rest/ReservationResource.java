package de.projectride.ride.web.rest;

import com.codahale.metrics.annotation.Timed;
import de.projectride.ride.domain.Reservation;
import de.projectride.ride.service.ReservationService;
import de.projectride.ride.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Reservation.
 */
@RestController
@RequestMapping("/api")
public class ReservationResource {

    private final Logger log = LoggerFactory.getLogger(ReservationResource.class);
        
    @Inject
    private ReservationService reservationService;

    /**
     * POST  /reservations : Create a new reservation.
     *
     * @param reservation the reservation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new reservation, or with status 400 (Bad Request) if the reservation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/reservations",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) throws URISyntaxException {
        log.debug("REST request to save Reservation : {}", reservation);
        if (reservation.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("reservation", "idexists", "A new reservation cannot already have an ID")).body(null);
        }
        Reservation result = reservationService.save(reservation);
        return ResponseEntity.created(new URI("/api/reservations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("reservation", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /reservations : Updates an existing reservation.
     *
     * @param reservation the reservation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated reservation,
     * or with status 400 (Bad Request) if the reservation is not valid,
     * or with status 500 (Internal Server Error) if the reservation couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/reservations",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Reservation> updateReservation(@RequestBody Reservation reservation) throws URISyntaxException {
        log.debug("REST request to update Reservation : {}", reservation);
        if (reservation.getId() == null) {
            return createReservation(reservation);
        }
        Reservation result = reservationService.save(reservation);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("reservation", reservation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /reservations : get all the reservations.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of reservations in body
     */
    @RequestMapping(value = "/reservations",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Reservation> getAllReservations() {
        log.debug("REST request to get all Reservations");
        return reservationService.findAll();
    }

    /**
     * GET  /reservations/:id : get the "id" reservation.
     *
     * @param id the id of the reservation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the reservation, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/reservations/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Reservation> getReservation(@PathVariable Long id) {
        log.debug("REST request to get Reservation : {}", id);
        Reservation reservation = reservationService.findOne(id);
        return Optional.ofNullable(reservation)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /reservations/:id : delete the "id" reservation.
     *
     * @param id the id of the reservation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/reservations/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        log.debug("REST request to delete Reservation : {}", id);
        reservationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("reservation", id.toString())).build();
    }

}

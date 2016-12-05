package de.projectride.ride.web.rest;

import com.codahale.metrics.annotation.Timed;
import de.projectride.ride.domain.Ride;
import de.projectride.ride.service.RideService;
import de.projectride.ride.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Ride.
 */
@RestController
@RequestMapping("/api")
public class RideResource {

    private final Logger log = LoggerFactory.getLogger(RideResource.class);
        
    @Inject
    private RideService rideService;

    /**
     * POST  /rides : Create a new ride.
     *
     * @param ride the ride to create
     * @return the ResponseEntity with status 201 (Created) and with body the new ride, or with status 400 (Bad Request) if the ride has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/rides",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ride> createRide(@Valid @RequestBody Ride ride) throws URISyntaxException {
        log.debug("REST request to save Ride : {}", ride);
        if (ride.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("ride", "idexists", "A new ride cannot already have an ID")).body(null);
        }
        Ride result = rideService.save(ride);
        return ResponseEntity.created(new URI("/api/rides/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("ride", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /rides : Updates an existing ride.
     *
     * @param ride the ride to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated ride,
     * or with status 400 (Bad Request) if the ride is not valid,
     * or with status 500 (Internal Server Error) if the ride couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/rides",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ride> updateRide(@Valid @RequestBody Ride ride) throws URISyntaxException {
        log.debug("REST request to update Ride : {}", ride);
        if (ride.getId() == null) {
            return createRide(ride);
        }
        Ride result = rideService.save(ride);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("ride", ride.getId().toString()))
            .body(result);
    }

    /**
     * GET  /rides : get all the rides.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of rides in body
     */
    @RequestMapping(value = "/rides",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Ride> getAllRides() {
        log.debug("REST request to get all Rides");
        return rideService.findAll();
    }

    /**
     * GET  /rides/:id : get the "id" ride.
     *
     * @param id the id of the ride to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ride, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/rides/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ride> getRide(@PathVariable Long id) {
        log.debug("REST request to get Ride : {}", id);
        Ride ride = rideService.findOne(id);
        return Optional.ofNullable(ride)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rides/:id : delete the "id" ride.
     *
     * @param id the id of the ride to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/rides/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {
        log.debug("REST request to delete Ride : {}", id);
        rideService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("ride", id.toString())).build();
    }

}

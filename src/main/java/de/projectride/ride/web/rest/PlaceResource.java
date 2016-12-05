package de.projectride.ride.web.rest;

import com.codahale.metrics.annotation.Timed;
import de.projectride.ride.domain.Place;
import de.projectride.ride.service.PlaceService;
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
 * REST controller for managing Place.
 */
@RestController
@RequestMapping("/api")
public class PlaceResource {

    private final Logger log = LoggerFactory.getLogger(PlaceResource.class);
        
    @Inject
    private PlaceService placeService;

    /**
     * POST  /places : Create a new place.
     *
     * @param place the place to create
     * @return the ResponseEntity with status 201 (Created) and with body the new place, or with status 400 (Bad Request) if the place has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/places",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Place> createPlace(@RequestBody Place place) throws URISyntaxException {
        log.debug("REST request to save Place : {}", place);
        if (place.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("place", "idexists", "A new place cannot already have an ID")).body(null);
        }
        Place result = placeService.save(place);
        return ResponseEntity.created(new URI("/api/places/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("place", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /places : Updates an existing place.
     *
     * @param place the place to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated place,
     * or with status 400 (Bad Request) if the place is not valid,
     * or with status 500 (Internal Server Error) if the place couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/places",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Place> updatePlace(@RequestBody Place place) throws URISyntaxException {
        log.debug("REST request to update Place : {}", place);
        if (place.getId() == null) {
            return createPlace(place);
        }
        Place result = placeService.save(place);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("place", place.getId().toString()))
            .body(result);
    }

    /**
     * GET  /places : get all the places.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of places in body
     */
    @RequestMapping(value = "/places",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Place> getAllPlaces() {
        log.debug("REST request to get all Places");
        return placeService.findAll();
    }

    /**
     * GET  /places/:id : get the "id" place.
     *
     * @param id the id of the place to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the place, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/places/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Place> getPlace(@PathVariable Long id) {
        log.debug("REST request to get Place : {}", id);
        Place place = placeService.findOne(id);
        return Optional.ofNullable(place)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /places/:id : delete the "id" place.
     *
     * @param id the id of the place to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/places/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        log.debug("REST request to delete Place : {}", id);
        placeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("place", id.toString())).build();
    }

}

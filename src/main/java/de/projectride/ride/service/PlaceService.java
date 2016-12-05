package de.projectride.ride.service;

import de.projectride.ride.domain.Place;
import de.projectride.ride.repository.PlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Place.
 */
@Service
@Transactional
public class PlaceService {

    private final Logger log = LoggerFactory.getLogger(PlaceService.class);
    
    @Inject
    private PlaceRepository placeRepository;

    /**
     * Save a place.
     *
     * @param place the entity to save
     * @return the persisted entity
     */
    public Place save(Place place) {
        log.debug("Request to save Place : {}", place);
        Place result = placeRepository.save(place);
        return result;
    }

    /**
     *  Get all the places.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<Place> findAll() {
        log.debug("Request to get all Places");
        List<Place> result = placeRepository.findAll();

        return result;
    }

    /**
     *  Get one place by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Place findOne(Long id) {
        log.debug("Request to get Place : {}", id);
        Place place = placeRepository.findOne(id);
        return place;
    }

    /**
     *  Delete the  place by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Place : {}", id);
        placeRepository.delete(id);
    }
}

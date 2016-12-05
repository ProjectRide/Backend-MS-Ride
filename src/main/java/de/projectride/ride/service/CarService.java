package de.projectride.ride.service;

import de.projectride.ride.domain.Car;
import de.projectride.ride.repository.CarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Car.
 */
@Service
@Transactional
public class CarService {

    private final Logger log = LoggerFactory.getLogger(CarService.class);
    
    @Inject
    private CarRepository carRepository;

    /**
     * Save a car.
     *
     * @param car the entity to save
     * @return the persisted entity
     */
    public Car save(Car car) {
        log.debug("Request to save Car : {}", car);
        Car result = carRepository.save(car);
        return result;
    }

    /**
     *  Get all the cars.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<Car> findAll() {
        log.debug("Request to get all Cars");
        List<Car> result = carRepository.findAll();

        return result;
    }

    /**
     *  Get one car by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Car findOne(Long id) {
        log.debug("Request to get Car : {}", id);
        Car car = carRepository.findOne(id);
        return car;
    }

    /**
     *  Delete the  car by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Car : {}", id);
        carRepository.delete(id);
    }
}

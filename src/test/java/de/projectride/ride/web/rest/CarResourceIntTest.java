package de.projectride.ride.web.rest;

import de.projectride.ride.RideApp;

import de.projectride.ride.domain.Car;
import de.projectride.ride.repository.CarRepository;
import de.projectride.ride.service.CarService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CarResource REST controller.
 *
 * @see CarResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RideApp.class)
public class CarResourceIntTest {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String DEFAULT_BRAND = "AAAAA";
    private static final String UPDATED_BRAND = "BBBBB";

    private static final String DEFAULT_MODEL = "AAAAA";
    private static final String UPDATED_MODEL = "BBBBB";

    private static final String DEFAULT_COLOR = "AAAAA";
    private static final String UPDATED_COLOR = "BBBBB";

    @Inject
    private CarRepository carRepository;

    @Inject
    private CarService carService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restCarMockMvc;

    private Car car;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CarResource carResource = new CarResource();
        ReflectionTestUtils.setField(carResource, "carService", carService);
        this.restCarMockMvc = MockMvcBuilders.standaloneSetup(carResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Car createEntity(EntityManager em) {
        Car car = new Car()
                .userId(DEFAULT_USER_ID)
                .brand(DEFAULT_BRAND)
                .model(DEFAULT_MODEL)
                .color(DEFAULT_COLOR);
        return car;
    }

    @Before
    public void initTest() {
        car = createEntity(em);
    }

    @Test
    @Transactional
    public void createCar() throws Exception {
        int databaseSizeBeforeCreate = carRepository.findAll().size();

        // Create the Car

        restCarMockMvc.perform(post("/api/cars")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(car)))
                .andExpect(status().isCreated());

        // Validate the Car in the database
        List<Car> cars = carRepository.findAll();
        assertThat(cars).hasSize(databaseSizeBeforeCreate + 1);
        Car testCar = cars.get(cars.size() - 1);
        assertThat(testCar.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testCar.getBrand()).isEqualTo(DEFAULT_BRAND);
        assertThat(testCar.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testCar.getColor()).isEqualTo(DEFAULT_COLOR);
    }

    @Test
    @Transactional
    public void getAllCars() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        // Get all the cars
        restCarMockMvc.perform(get("/api/cars?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(car.getId().intValue())))
                .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
                .andExpect(jsonPath("$.[*].brand").value(hasItem(DEFAULT_BRAND.toString())))
                .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL.toString())))
                .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR.toString())));
    }

    @Test
    @Transactional
    public void getCar() throws Exception {
        // Initialize the database
        carRepository.saveAndFlush(car);

        // Get the car
        restCarMockMvc.perform(get("/api/cars/{id}", car.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(car.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.brand").value(DEFAULT_BRAND.toString()))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL.toString()))
            .andExpect(jsonPath("$.color").value(DEFAULT_COLOR.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCar() throws Exception {
        // Get the car
        restCarMockMvc.perform(get("/api/cars/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCar() throws Exception {
        // Initialize the database
        carService.save(car);

        int databaseSizeBeforeUpdate = carRepository.findAll().size();

        // Update the car
        Car updatedCar = carRepository.findOne(car.getId());
        updatedCar
                .userId(UPDATED_USER_ID)
                .brand(UPDATED_BRAND)
                .model(UPDATED_MODEL)
                .color(UPDATED_COLOR);

        restCarMockMvc.perform(put("/api/cars")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCar)))
                .andExpect(status().isOk());

        // Validate the Car in the database
        List<Car> cars = carRepository.findAll();
        assertThat(cars).hasSize(databaseSizeBeforeUpdate);
        Car testCar = cars.get(cars.size() - 1);
        assertThat(testCar.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testCar.getBrand()).isEqualTo(UPDATED_BRAND);
        assertThat(testCar.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testCar.getColor()).isEqualTo(UPDATED_COLOR);
    }

    @Test
    @Transactional
    public void deleteCar() throws Exception {
        // Initialize the database
        carService.save(car);

        int databaseSizeBeforeDelete = carRepository.findAll().size();

        // Get the car
        restCarMockMvc.perform(delete("/api/cars/{id}", car.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Car> cars = carRepository.findAll();
        assertThat(cars).hasSize(databaseSizeBeforeDelete - 1);
    }
}

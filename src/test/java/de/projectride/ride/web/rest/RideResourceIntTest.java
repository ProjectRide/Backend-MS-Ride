package de.projectride.ride.web.rest;

import de.projectride.ride.RideApp;

import de.projectride.ride.domain.Ride;
import de.projectride.ride.repository.RideRepository;
import de.projectride.ride.service.RideService;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RideResource REST controller.
 *
 * @see RideResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RideApp.class)
public class RideResourceIntTest {

    private static final Long DEFAULT_DRIVER_ID = 1L;
    private static final Long UPDATED_DRIVER_ID = 2L;

    private static final ZonedDateTime DEFAULT_START_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_START_DATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_START_DATE_TIME_STR = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(DEFAULT_START_DATE_TIME);

    private static final Integer DEFAULT_FLEXIBLE_START_PLACE = 1;
    private static final Integer UPDATED_FLEXIBLE_START_PLACE = 2;

    private static final Integer DEFAULT_FLEXIBLE_END_PLACE = 1;
    private static final Integer UPDATED_FLEXIBLE_END_PLACE = 2;

    private static final Float DEFAULT_PRICE = 1F;
    private static final Float UPDATED_PRICE = 2F;

    private static final Integer DEFAULT_NUMBER_OF_SEATS = 1;
    private static final Integer UPDATED_NUMBER_OF_SEATS = 2;

    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATED_AT_STR = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(DEFAULT_CREATED_AT);

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    @Inject
    private RideRepository rideRepository;

    @Inject
    private RideService rideService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restRideMockMvc;

    private Ride ride;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RideResource rideResource = new RideResource();
        ReflectionTestUtils.setField(rideResource, "rideService", rideService);
        this.restRideMockMvc = MockMvcBuilders.standaloneSetup(rideResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ride createEntity(EntityManager em) {
        Ride ride = new Ride()
                .driverId(DEFAULT_DRIVER_ID)
                .startDateTime(DEFAULT_START_DATE_TIME)
                .flexibleStartPlace(DEFAULT_FLEXIBLE_START_PLACE)
                .flexibleEndPlace(DEFAULT_FLEXIBLE_END_PLACE)
                .price(DEFAULT_PRICE)
                .numberOfSeats(DEFAULT_NUMBER_OF_SEATS)
                .description(DEFAULT_DESCRIPTION)
                .createdAt(DEFAULT_CREATED_AT)
                .deleted(DEFAULT_DELETED);
        return ride;
    }

    @Before
    public void initTest() {
        ride = createEntity(em);
    }

    @Test
    @Transactional
    public void createRide() throws Exception {
        int databaseSizeBeforeCreate = rideRepository.findAll().size();

        // Create the Ride

        restRideMockMvc.perform(post("/api/rides")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ride)))
                .andExpect(status().isCreated());

        // Validate the Ride in the database
        List<Ride> rides = rideRepository.findAll();
        assertThat(rides).hasSize(databaseSizeBeforeCreate + 1);
        Ride testRide = rides.get(rides.size() - 1);
        assertThat(testRide.getDriverId()).isEqualTo(DEFAULT_DRIVER_ID);
        assertThat(testRide.getStartDateTime()).isEqualTo(DEFAULT_START_DATE_TIME);
        assertThat(testRide.getFlexibleStartPlace()).isEqualTo(DEFAULT_FLEXIBLE_START_PLACE);
        assertThat(testRide.getFlexibleEndPlace()).isEqualTo(DEFAULT_FLEXIBLE_END_PLACE);
        assertThat(testRide.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testRide.getNumberOfSeats()).isEqualTo(DEFAULT_NUMBER_OF_SEATS);
        assertThat(testRide.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRide.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testRide.isDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    public void getAllRides() throws Exception {
        // Initialize the database
        rideRepository.saveAndFlush(ride);

        // Get all the rides
        restRideMockMvc.perform(get("/api/rides?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(ride.getId().intValue())))
                .andExpect(jsonPath("$.[*].driverId").value(hasItem(DEFAULT_DRIVER_ID.intValue())))
                .andExpect(jsonPath("$.[*].startDateTime").value(hasItem(DEFAULT_START_DATE_TIME_STR)))
                .andExpect(jsonPath("$.[*].flexibleStartPlace").value(hasItem(DEFAULT_FLEXIBLE_START_PLACE)))
                .andExpect(jsonPath("$.[*].flexibleEndPlace").value(hasItem(DEFAULT_FLEXIBLE_END_PLACE)))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
                .andExpect(jsonPath("$.[*].numberOfSeats").value(hasItem(DEFAULT_NUMBER_OF_SEATS)))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT_STR)))
                .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

    @Test
    @Transactional
    public void getRide() throws Exception {
        // Initialize the database
        rideRepository.saveAndFlush(ride);

        // Get the ride
        restRideMockMvc.perform(get("/api/rides/{id}", ride.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(ride.getId().intValue()))
            .andExpect(jsonPath("$.driverId").value(DEFAULT_DRIVER_ID.intValue()))
            .andExpect(jsonPath("$.startDateTime").value(DEFAULT_START_DATE_TIME_STR))
            .andExpect(jsonPath("$.flexibleStartPlace").value(DEFAULT_FLEXIBLE_START_PLACE))
            .andExpect(jsonPath("$.flexibleEndPlace").value(DEFAULT_FLEXIBLE_END_PLACE))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.numberOfSeats").value(DEFAULT_NUMBER_OF_SEATS))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT_STR))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingRide() throws Exception {
        // Get the ride
        restRideMockMvc.perform(get("/api/rides/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRide() throws Exception {
        // Initialize the database
        rideService.save(ride);

        int databaseSizeBeforeUpdate = rideRepository.findAll().size();

        // Update the ride
        Ride updatedRide = rideRepository.findOne(ride.getId());
        updatedRide
                .driverId(UPDATED_DRIVER_ID)
                .startDateTime(UPDATED_START_DATE_TIME)
                .flexibleStartPlace(UPDATED_FLEXIBLE_START_PLACE)
                .flexibleEndPlace(UPDATED_FLEXIBLE_END_PLACE)
                .price(UPDATED_PRICE)
                .numberOfSeats(UPDATED_NUMBER_OF_SEATS)
                .description(UPDATED_DESCRIPTION)
                .createdAt(UPDATED_CREATED_AT)
                .deleted(UPDATED_DELETED);

        restRideMockMvc.perform(put("/api/rides")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRide)))
                .andExpect(status().isOk());

        // Validate the Ride in the database
        List<Ride> rides = rideRepository.findAll();
        assertThat(rides).hasSize(databaseSizeBeforeUpdate);
        Ride testRide = rides.get(rides.size() - 1);
        assertThat(testRide.getDriverId()).isEqualTo(UPDATED_DRIVER_ID);
        assertThat(testRide.getStartDateTime()).isEqualTo(UPDATED_START_DATE_TIME);
        assertThat(testRide.getFlexibleStartPlace()).isEqualTo(UPDATED_FLEXIBLE_START_PLACE);
        assertThat(testRide.getFlexibleEndPlace()).isEqualTo(UPDATED_FLEXIBLE_END_PLACE);
        assertThat(testRide.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testRide.getNumberOfSeats()).isEqualTo(UPDATED_NUMBER_OF_SEATS);
        assertThat(testRide.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRide.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testRide.isDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void deleteRide() throws Exception {
        // Initialize the database
        rideService.save(ride);

        int databaseSizeBeforeDelete = rideRepository.findAll().size();

        // Get the ride
        restRideMockMvc.perform(delete("/api/rides/{id}", ride.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Ride> rides = rideRepository.findAll();
        assertThat(rides).hasSize(databaseSizeBeforeDelete - 1);
    }
}

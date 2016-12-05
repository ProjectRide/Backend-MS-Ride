package de.projectride.ride.web.rest;

import de.projectride.ride.RideApp;

import de.projectride.ride.domain.Place;
import de.projectride.ride.repository.PlaceRepository;
import de.projectride.ride.service.PlaceService;

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
 * Test class for the PlaceResource REST controller.
 *
 * @see PlaceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RideApp.class)
public class PlaceResourceIntTest {

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    private static final Integer DEFAULT_POSTCODE = 1;
    private static final Integer UPDATED_POSTCODE = 2;

    private static final String DEFAULT_CITY_NAME = "AAAAA";
    private static final String UPDATED_CITY_NAME = "BBBBB";

    @Inject
    private PlaceRepository placeRepository;

    @Inject
    private PlaceService placeService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPlaceMockMvc;

    private Place place;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PlaceResource placeResource = new PlaceResource();
        ReflectionTestUtils.setField(placeResource, "placeService", placeService);
        this.restPlaceMockMvc = MockMvcBuilders.standaloneSetup(placeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Place createEntity(EntityManager em) {
        Place place = new Place()
                .latitude(DEFAULT_LATITUDE)
                .longitude(DEFAULT_LONGITUDE)
                .postcode(DEFAULT_POSTCODE)
                .cityName(DEFAULT_CITY_NAME);
        return place;
    }

    @Before
    public void initTest() {
        place = createEntity(em);
    }

    @Test
    @Transactional
    public void createPlace() throws Exception {
        int databaseSizeBeforeCreate = placeRepository.findAll().size();

        // Create the Place

        restPlaceMockMvc.perform(post("/api/places")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(place)))
                .andExpect(status().isCreated());

        // Validate the Place in the database
        List<Place> places = placeRepository.findAll();
        assertThat(places).hasSize(databaseSizeBeforeCreate + 1);
        Place testPlace = places.get(places.size() - 1);
        assertThat(testPlace.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testPlace.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testPlace.getPostcode()).isEqualTo(DEFAULT_POSTCODE);
        assertThat(testPlace.getCityName()).isEqualTo(DEFAULT_CITY_NAME);
    }

    @Test
    @Transactional
    public void getAllPlaces() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the places
        restPlaceMockMvc.perform(get("/api/places?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(place.getId().intValue())))
                .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
                .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
                .andExpect(jsonPath("$.[*].postcode").value(hasItem(DEFAULT_POSTCODE)))
                .andExpect(jsonPath("$.[*].cityName").value(hasItem(DEFAULT_CITY_NAME.toString())));
    }

    @Test
    @Transactional
    public void getPlace() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get the place
        restPlaceMockMvc.perform(get("/api/places/{id}", place.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(place.getId().intValue()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.postcode").value(DEFAULT_POSTCODE))
            .andExpect(jsonPath("$.cityName").value(DEFAULT_CITY_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPlace() throws Exception {
        // Get the place
        restPlaceMockMvc.perform(get("/api/places/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePlace() throws Exception {
        // Initialize the database
        placeService.save(place);

        int databaseSizeBeforeUpdate = placeRepository.findAll().size();

        // Update the place
        Place updatedPlace = placeRepository.findOne(place.getId());
        updatedPlace
                .latitude(UPDATED_LATITUDE)
                .longitude(UPDATED_LONGITUDE)
                .postcode(UPDATED_POSTCODE)
                .cityName(UPDATED_CITY_NAME);

        restPlaceMockMvc.perform(put("/api/places")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPlace)))
                .andExpect(status().isOk());

        // Validate the Place in the database
        List<Place> places = placeRepository.findAll();
        assertThat(places).hasSize(databaseSizeBeforeUpdate);
        Place testPlace = places.get(places.size() - 1);
        assertThat(testPlace.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testPlace.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testPlace.getPostcode()).isEqualTo(UPDATED_POSTCODE);
        assertThat(testPlace.getCityName()).isEqualTo(UPDATED_CITY_NAME);
    }

    @Test
    @Transactional
    public void deletePlace() throws Exception {
        // Initialize the database
        placeService.save(place);

        int databaseSizeBeforeDelete = placeRepository.findAll().size();

        // Get the place
        restPlaceMockMvc.perform(delete("/api/places/{id}", place.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Place> places = placeRepository.findAll();
        assertThat(places).hasSize(databaseSizeBeforeDelete - 1);
    }
}

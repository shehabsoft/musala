package com.musala.drone.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.musala.drone.IntegrationTest;
import com.musala.drone.domain.Drone;
import com.musala.drone.domain.Medication;
import com.musala.drone.domain.enumeration.Model;
import com.musala.drone.domain.enumeration.State;
import com.musala.drone.repository.DroneRepository;
import com.musala.drone.service.criteria.DroneCriteria;
import com.musala.drone.service.dto.DroneDTO;
import com.musala.drone.service.mapper.DroneMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DroneResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DroneResourceIT {

    private static final String DEFAULT_SERIAL_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL_NUMBER = "BBBBBBBBBB";

    private static final Model DEFAULT_MODEL = Model.Lightweight;
    private static final Model UPDATED_MODEL = Model.Middleweight;

    private static final Integer DEFAULT_WEIGHT_LIMIT = 12234;
    private static final Integer UPDATED_WEIGHT_LIMIT = 324234;

    private static final Long DEFAULT_BATTERY_CAPACITY = Long.valueOf(2);
    private static final Long UPDATED_BATTERY_CAPACITY = Long.valueOf(2);

    private static final State DEFAULT_STATE = State.IDLE;
    private static final State UPDATED_STATE = State.LOADING;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/drones";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DroneRepository droneRepository;

    @Autowired
    private DroneMapper droneMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDroneMockMvc;

    private Drone drone;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Drone createEntity(EntityManager em) {
        Drone drone = new Drone()
            .serialNumber(DEFAULT_SERIAL_NUMBER)
            .model(DEFAULT_MODEL)
            .weightLimit(DEFAULT_WEIGHT_LIMIT)
            .batteryCapacity(DEFAULT_BATTERY_CAPACITY)
            .state(DEFAULT_STATE)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return drone;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Drone createUpdatedEntity(EntityManager em) {
        Drone drone = new Drone()
            .serialNumber(UPDATED_SERIAL_NUMBER)
            .model(UPDATED_MODEL)
            .weightLimit(UPDATED_WEIGHT_LIMIT)
            .batteryCapacity(UPDATED_BATTERY_CAPACITY)
            .state(UPDATED_STATE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return drone;
    }

    @BeforeEach
    public void initTest() {
        drone = createEntity(em);
    }

    @Test
    @Transactional
    void createDrone() throws Exception {
        int databaseSizeBeforeCreate = droneRepository.findAll().size();
        // Create the Drone
        DroneDTO droneDTO = droneMapper.toDto(drone);
        restDroneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(droneDTO)))
            .andExpect(status().isCreated());

        // Validate the Drone in the database
        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeCreate + 1);
        Drone testDrone = droneList.get(droneList.size() - 1);
        assertThat(testDrone.getSerialNumber()).isEqualTo(DEFAULT_SERIAL_NUMBER);
        assertThat(testDrone.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testDrone.getWeightLimit()).isEqualTo(DEFAULT_WEIGHT_LIMIT);
        assertThat(testDrone.getBatteryCapacity()).isEqualTo(DEFAULT_BATTERY_CAPACITY);
        assertThat(testDrone.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testDrone.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testDrone.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testDrone.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testDrone.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void createDroneWithExistingId() throws Exception {
        // Create the Drone with an existing ID
        drone.setId(1L);
        DroneDTO droneDTO = droneMapper.toDto(drone);

        int databaseSizeBeforeCreate = droneRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDroneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(droneDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Drone in the database
        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSerialNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = droneRepository.findAll().size();
        // set the field null
        drone.setSerialNumber(null);

        // Create the Drone, which fails.
        DroneDTO droneDTO = droneMapper.toDto(drone);

        restDroneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(droneDTO)))
            .andExpect(status().isBadRequest());

        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkModelIsRequired() throws Exception {
        int databaseSizeBeforeTest = droneRepository.findAll().size();
        // set the field null
        drone.setModel(null);

        // Create the Drone, which fails.
        DroneDTO droneDTO = droneMapper.toDto(drone);

        restDroneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(droneDTO)))
            .andExpect(status().isBadRequest());

        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkWeightLimitIsRequired() throws Exception {
        int databaseSizeBeforeTest = droneRepository.findAll().size();
        // set the field null
        drone.setWeightLimit(null);

        // Create the Drone, which fails.
        DroneDTO droneDTO = droneMapper.toDto(drone);

        restDroneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(droneDTO)))
            .andExpect(status().isBadRequest());

        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBatteryCapacityIsRequired() throws Exception {
        int databaseSizeBeforeTest = droneRepository.findAll().size();
        // set the field null
        drone.setBatteryCapacity(null);

        // Create the Drone, which fails.
        DroneDTO droneDTO = droneMapper.toDto(drone);

        restDroneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(droneDTO)))
            .andExpect(status().isBadRequest());

        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStateIsRequired() throws Exception {
        int databaseSizeBeforeTest = droneRepository.findAll().size();
        // set the field null
        drone.setState(null);

        // Create the Drone, which fails.
        DroneDTO droneDTO = droneMapper.toDto(drone);

        restDroneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(droneDTO)))
            .andExpect(status().isBadRequest());

        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = droneRepository.findAll().size();
        // set the field null
        drone.setCreatedBy(null);

        // Create the Drone, which fails.
        DroneDTO droneDTO = droneMapper.toDto(drone);

        restDroneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(droneDTO)))
            .andExpect(status().isBadRequest());

        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = droneRepository.findAll().size();
        // set the field null
        drone.setCreatedDate(null);

        // Create the Drone, which fails.
        DroneDTO droneDTO = droneMapper.toDto(drone);

        restDroneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(droneDTO)))
            .andExpect(status().isBadRequest());

        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDrones() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList
        restDroneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(drone.getId().intValue())))
            .andExpect(jsonPath("$.[*].serialNumber").value(hasItem(DEFAULT_SERIAL_NUMBER)))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL.toString())))
            .andExpect(jsonPath("$.[*].weightLimit").value(hasItem(DEFAULT_WEIGHT_LIMIT)))
            .andExpect(jsonPath("$.[*].batteryCapacity").value(hasItem(DEFAULT_BATTERY_CAPACITY)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getDrone() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get the drone
        restDroneMockMvc
            .perform(get(ENTITY_API_URL_ID, drone.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(drone.getId().intValue()))
            .andExpect(jsonPath("$.serialNumber").value(DEFAULT_SERIAL_NUMBER))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL.toString()))
            .andExpect(jsonPath("$.weightLimit").value(DEFAULT_WEIGHT_LIMIT))
            .andExpect(jsonPath("$.batteryCapacity").value(DEFAULT_BATTERY_CAPACITY))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getDronesByIdFiltering() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        Long id = drone.getId();

        defaultDroneShouldBeFound("id.equals=" + id);
        defaultDroneShouldNotBeFound("id.notEquals=" + id);

        defaultDroneShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDroneShouldNotBeFound("id.greaterThan=" + id);

        defaultDroneShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDroneShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDronesBySerialNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where serialNumber equals to DEFAULT_SERIAL_NUMBER
        defaultDroneShouldBeFound("serialNumber.equals=" + DEFAULT_SERIAL_NUMBER);

        // Get all the droneList where serialNumber equals to UPDATED_SERIAL_NUMBER
        defaultDroneShouldNotBeFound("serialNumber.equals=" + UPDATED_SERIAL_NUMBER);
    }

    @Test
    @Transactional
    void getAllDronesBySerialNumberIsInShouldWork() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where serialNumber in DEFAULT_SERIAL_NUMBER or UPDATED_SERIAL_NUMBER
        defaultDroneShouldBeFound("serialNumber.in=" + DEFAULT_SERIAL_NUMBER + "," + UPDATED_SERIAL_NUMBER);

        // Get all the droneList where serialNumber equals to UPDATED_SERIAL_NUMBER
        defaultDroneShouldNotBeFound("serialNumber.in=" + UPDATED_SERIAL_NUMBER);
    }

    @Test
    @Transactional
    void getAllDronesBySerialNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where serialNumber is not null
        defaultDroneShouldBeFound("serialNumber.specified=true");

        // Get all the droneList where serialNumber is null
        defaultDroneShouldNotBeFound("serialNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllDronesBySerialNumberContainsSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where serialNumber contains DEFAULT_SERIAL_NUMBER
        defaultDroneShouldBeFound("serialNumber.contains=" + DEFAULT_SERIAL_NUMBER);

        // Get all the droneList where serialNumber contains UPDATED_SERIAL_NUMBER
        defaultDroneShouldNotBeFound("serialNumber.contains=" + UPDATED_SERIAL_NUMBER);
    }

    @Test
    @Transactional
    void getAllDronesBySerialNumberNotContainsSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where serialNumber does not contain DEFAULT_SERIAL_NUMBER
        defaultDroneShouldNotBeFound("serialNumber.doesNotContain=" + DEFAULT_SERIAL_NUMBER);

        // Get all the droneList where serialNumber does not contain UPDATED_SERIAL_NUMBER
        defaultDroneShouldBeFound("serialNumber.doesNotContain=" + UPDATED_SERIAL_NUMBER);
    }

    @Test
    @Transactional
    void getAllDronesByModelIsEqualToSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where model equals to DEFAULT_MODEL
        defaultDroneShouldBeFound("model.equals=" + DEFAULT_MODEL);

        // Get all the droneList where model equals to UPDATED_MODEL
        defaultDroneShouldNotBeFound("model.equals=" + UPDATED_MODEL);
    }

    @Test
    @Transactional
    void getAllDronesByModelIsInShouldWork() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where model in DEFAULT_MODEL or UPDATED_MODEL
        defaultDroneShouldBeFound("model.in=" + DEFAULT_MODEL + "," + UPDATED_MODEL);

        // Get all the droneList where model equals to UPDATED_MODEL
        defaultDroneShouldNotBeFound("model.in=" + UPDATED_MODEL);
    }

    @Test
    @Transactional
    void getAllDronesByModelIsNullOrNotNull() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where model is not null
        defaultDroneShouldBeFound("model.specified=true");

        // Get all the droneList where model is null
        defaultDroneShouldNotBeFound("model.specified=false");
    }

    @Test
    @Transactional
    void getAllDronesByWeightLimitIsEqualToSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where weightLimit equals to DEFAULT_WEIGHT_LIMIT
        defaultDroneShouldBeFound("weightLimit.equals=" + DEFAULT_WEIGHT_LIMIT);

        // Get all the droneList where weightLimit equals to UPDATED_WEIGHT_LIMIT
        defaultDroneShouldNotBeFound("weightLimit.equals=" + UPDATED_WEIGHT_LIMIT);
    }

    @Test
    @Transactional
    void getAllDronesByWeightLimitIsInShouldWork() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where weightLimit in DEFAULT_WEIGHT_LIMIT or UPDATED_WEIGHT_LIMIT
        defaultDroneShouldBeFound("weightLimit.in=" + DEFAULT_WEIGHT_LIMIT + "," + UPDATED_WEIGHT_LIMIT);

        // Get all the droneList where weightLimit equals to UPDATED_WEIGHT_LIMIT
        defaultDroneShouldNotBeFound("weightLimit.in=" + UPDATED_WEIGHT_LIMIT);
    }

    @Test
    @Transactional
    void getAllDronesByWeightLimitIsNullOrNotNull() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where weightLimit is not null
        defaultDroneShouldBeFound("weightLimit.specified=true");

        // Get all the droneList where weightLimit is null
        defaultDroneShouldNotBeFound("weightLimit.specified=false");
    }

    @Test
    @Transactional
    void getAllDronesByWeightLimitContainsSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where weightLimit contains DEFAULT_WEIGHT_LIMIT
        defaultDroneShouldBeFound("weightLimit.contains=" + DEFAULT_WEIGHT_LIMIT);

        // Get all the droneList where weightLimit contains UPDATED_WEIGHT_LIMIT
        defaultDroneShouldNotBeFound("weightLimit.contains=" + UPDATED_WEIGHT_LIMIT);
    }

    @Test
    @Transactional
    void getAllDronesByWeightLimitNotContainsSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where weightLimit does not contain DEFAULT_WEIGHT_LIMIT
        defaultDroneShouldNotBeFound("weightLimit.doesNotContain=" + DEFAULT_WEIGHT_LIMIT);

        // Get all the droneList where weightLimit does not contain UPDATED_WEIGHT_LIMIT
        defaultDroneShouldBeFound("weightLimit.doesNotContain=" + UPDATED_WEIGHT_LIMIT);
    }

    @Test
    @Transactional
    void getAllDronesByBatteryCapacityIsEqualToSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where batteryCapacity equals to DEFAULT_BATTERY_CAPACITY
        defaultDroneShouldBeFound("batteryCapacity.equals=" + DEFAULT_BATTERY_CAPACITY);

        // Get all the droneList where batteryCapacity equals to UPDATED_BATTERY_CAPACITY
        defaultDroneShouldNotBeFound("batteryCapacity.equals=" + UPDATED_BATTERY_CAPACITY);
    }

    @Test
    @Transactional
    void getAllDronesByBatteryCapacityIsInShouldWork() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where batteryCapacity in DEFAULT_BATTERY_CAPACITY or UPDATED_BATTERY_CAPACITY
        defaultDroneShouldBeFound("batteryCapacity.in=" + DEFAULT_BATTERY_CAPACITY + "," + UPDATED_BATTERY_CAPACITY);

        // Get all the droneList where batteryCapacity equals to UPDATED_BATTERY_CAPACITY
        defaultDroneShouldNotBeFound("batteryCapacity.in=" + UPDATED_BATTERY_CAPACITY);
    }

    @Test
    @Transactional
    void getAllDronesByBatteryCapacityIsNullOrNotNull() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where batteryCapacity is not null
        defaultDroneShouldBeFound("batteryCapacity.specified=true");

        // Get all the droneList where batteryCapacity is null
        defaultDroneShouldNotBeFound("batteryCapacity.specified=false");
    }

    @Test
    @Transactional
    void getAllDronesByBatteryCapacityContainsSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where batteryCapacity contains DEFAULT_BATTERY_CAPACITY
        defaultDroneShouldBeFound("batteryCapacity.contains=" + DEFAULT_BATTERY_CAPACITY);

        // Get all the droneList where batteryCapacity contains UPDATED_BATTERY_CAPACITY
        defaultDroneShouldNotBeFound("batteryCapacity.contains=" + UPDATED_BATTERY_CAPACITY);
    }

    @Test
    @Transactional
    void getAllDronesByBatteryCapacityNotContainsSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where batteryCapacity does not contain DEFAULT_BATTERY_CAPACITY
        defaultDroneShouldNotBeFound("batteryCapacity.doesNotContain=" + DEFAULT_BATTERY_CAPACITY);

        // Get all the droneList where batteryCapacity does not contain UPDATED_BATTERY_CAPACITY
        defaultDroneShouldBeFound("batteryCapacity.doesNotContain=" + UPDATED_BATTERY_CAPACITY);
    }

    @Test
    @Transactional
    void getAllDronesByStateIsEqualToSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where state equals to DEFAULT_STATE
        defaultDroneShouldBeFound("state.equals=" + DEFAULT_STATE);

        // Get all the droneList where state equals to UPDATED_STATE
        defaultDroneShouldNotBeFound("state.equals=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllDronesByStateIsInShouldWork() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where state in DEFAULT_STATE or UPDATED_STATE
        defaultDroneShouldBeFound("state.in=" + DEFAULT_STATE + "," + UPDATED_STATE);

        // Get all the droneList where state equals to UPDATED_STATE
        defaultDroneShouldNotBeFound("state.in=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    void getAllDronesByStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where state is not null
        defaultDroneShouldBeFound("state.specified=true");

        // Get all the droneList where state is null
        defaultDroneShouldNotBeFound("state.specified=false");
    }

    @Test
    @Transactional
    void getAllDronesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where createdBy equals to DEFAULT_CREATED_BY
        defaultDroneShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the droneList where createdBy equals to UPDATED_CREATED_BY
        defaultDroneShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDronesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultDroneShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the droneList where createdBy equals to UPDATED_CREATED_BY
        defaultDroneShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDronesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where createdBy is not null
        defaultDroneShouldBeFound("createdBy.specified=true");

        // Get all the droneList where createdBy is null
        defaultDroneShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDronesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where createdBy contains DEFAULT_CREATED_BY
        defaultDroneShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the droneList where createdBy contains UPDATED_CREATED_BY
        defaultDroneShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDronesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where createdBy does not contain DEFAULT_CREATED_BY
        defaultDroneShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the droneList where createdBy does not contain UPDATED_CREATED_BY
        defaultDroneShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDronesByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where createdDate equals to DEFAULT_CREATED_DATE
        defaultDroneShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the droneList where createdDate equals to UPDATED_CREATED_DATE
        defaultDroneShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllDronesByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultDroneShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the droneList where createdDate equals to UPDATED_CREATED_DATE
        defaultDroneShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllDronesByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where createdDate is not null
        defaultDroneShouldBeFound("createdDate.specified=true");

        // Get all the droneList where createdDate is null
        defaultDroneShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllDronesByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultDroneShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the droneList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultDroneShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDronesByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultDroneShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the droneList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultDroneShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDronesByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where lastModifiedBy is not null
        defaultDroneShouldBeFound("lastModifiedBy.specified=true");

        // Get all the droneList where lastModifiedBy is null
        defaultDroneShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDronesByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultDroneShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the droneList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultDroneShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDronesByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultDroneShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the droneList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultDroneShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDronesByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultDroneShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the droneList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultDroneShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllDronesByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultDroneShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the droneList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultDroneShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllDronesByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        // Get all the droneList where lastModifiedDate is not null
        defaultDroneShouldBeFound("lastModifiedDate.specified=true");

        // Get all the droneList where lastModifiedDate is null
        defaultDroneShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllDronesByDroneMedicationsIsEqualToSomething() throws Exception {
        Medication droneMedications;
        if (TestUtil.findAll(em, Medication.class).isEmpty()) {
            droneRepository.saveAndFlush(drone);
            droneMedications = MedicationResourceIT.createEntity(em);
        } else {
            droneMedications = TestUtil.findAll(em, Medication.class).get(0);
        }
        em.persist(droneMedications);
        em.flush();
        drone.addDroneMedications(droneMedications);
        droneRepository.saveAndFlush(drone);
        Long droneMedicationsId = droneMedications.getId();

        // Get all the droneList where droneMedications equals to droneMedicationsId
        defaultDroneShouldBeFound("droneMedicationsId.equals=" + droneMedicationsId);

        // Get all the droneList where droneMedications equals to (droneMedicationsId + 1)
        defaultDroneShouldNotBeFound("droneMedicationsId.equals=" + (droneMedicationsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDroneShouldBeFound(String filter) throws Exception {
        restDroneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(drone.getId().intValue())))
            .andExpect(jsonPath("$.[*].serialNumber").value(hasItem(DEFAULT_SERIAL_NUMBER)))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL.toString())))
            .andExpect(jsonPath("$.[*].weightLimit").value(hasItem(DEFAULT_WEIGHT_LIMIT)))
            .andExpect(jsonPath("$.[*].batteryCapacity").value(hasItem(DEFAULT_BATTERY_CAPACITY)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restDroneMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDroneShouldNotBeFound(String filter) throws Exception {
        restDroneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDroneMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDrone() throws Exception {
        // Get the drone
        restDroneMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDrone() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        int databaseSizeBeforeUpdate = droneRepository.findAll().size();

        // Update the drone
        Drone updatedDrone = droneRepository.findById(drone.getId()).get();
        // Disconnect from session so that the updates on updatedDrone are not directly saved in db
        em.detach(updatedDrone);
        updatedDrone
            .serialNumber(UPDATED_SERIAL_NUMBER)
            .model(UPDATED_MODEL)
            .weightLimit(UPDATED_WEIGHT_LIMIT)
            .batteryCapacity(UPDATED_BATTERY_CAPACITY)
            .state(UPDATED_STATE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        DroneDTO droneDTO = droneMapper.toDto(updatedDrone);

        restDroneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, droneDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(droneDTO))
            )
            .andExpect(status().isOk());

        // Validate the Drone in the database
        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeUpdate);
        Drone testDrone = droneList.get(droneList.size() - 1);
        assertThat(testDrone.getSerialNumber()).isEqualTo(UPDATED_SERIAL_NUMBER);
        assertThat(testDrone.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testDrone.getWeightLimit()).isEqualTo(UPDATED_WEIGHT_LIMIT);
        assertThat(testDrone.getBatteryCapacity()).isEqualTo(UPDATED_BATTERY_CAPACITY);
        assertThat(testDrone.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testDrone.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testDrone.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testDrone.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testDrone.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingDrone() throws Exception {
        int databaseSizeBeforeUpdate = droneRepository.findAll().size();
        drone.setId(count.incrementAndGet());

        // Create the Drone
        DroneDTO droneDTO = droneMapper.toDto(drone);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDroneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, droneDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(droneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Drone in the database
        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDrone() throws Exception {
        int databaseSizeBeforeUpdate = droneRepository.findAll().size();
        drone.setId(count.incrementAndGet());

        // Create the Drone
        DroneDTO droneDTO = droneMapper.toDto(drone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDroneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(droneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Drone in the database
        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDrone() throws Exception {
        int databaseSizeBeforeUpdate = droneRepository.findAll().size();
        drone.setId(count.incrementAndGet());

        // Create the Drone
        DroneDTO droneDTO = droneMapper.toDto(drone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDroneMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(droneDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Drone in the database
        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDroneWithPatch() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        int databaseSizeBeforeUpdate = droneRepository.findAll().size();

        // Update the drone using partial update
        Drone partialUpdatedDrone = new Drone();
        partialUpdatedDrone.setId(drone.getId());

        partialUpdatedDrone
            .model(UPDATED_MODEL)
            .weightLimit(UPDATED_WEIGHT_LIMIT)
            .batteryCapacity(UPDATED_BATTERY_CAPACITY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restDroneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDrone.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDrone))
            )
            .andExpect(status().isOk());

        // Validate the Drone in the database
        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeUpdate);
        Drone testDrone = droneList.get(droneList.size() - 1);
        assertThat(testDrone.getSerialNumber()).isEqualTo(DEFAULT_SERIAL_NUMBER);
        assertThat(testDrone.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testDrone.getWeightLimit()).isEqualTo(UPDATED_WEIGHT_LIMIT);
        assertThat(testDrone.getBatteryCapacity()).isEqualTo(UPDATED_BATTERY_CAPACITY);
        assertThat(testDrone.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testDrone.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testDrone.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testDrone.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testDrone.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateDroneWithPatch() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        int databaseSizeBeforeUpdate = droneRepository.findAll().size();

        // Update the drone using partial update
        Drone partialUpdatedDrone = new Drone();
        partialUpdatedDrone.setId(drone.getId());

        partialUpdatedDrone
            .serialNumber(UPDATED_SERIAL_NUMBER)
            .model(UPDATED_MODEL)
            .weightLimit(UPDATED_WEIGHT_LIMIT)
            .batteryCapacity(UPDATED_BATTERY_CAPACITY)
            .state(UPDATED_STATE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restDroneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDrone.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDrone))
            )
            .andExpect(status().isOk());

        // Validate the Drone in the database
        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeUpdate);
        Drone testDrone = droneList.get(droneList.size() - 1);
        assertThat(testDrone.getSerialNumber()).isEqualTo(UPDATED_SERIAL_NUMBER);
        assertThat(testDrone.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testDrone.getWeightLimit()).isEqualTo(UPDATED_WEIGHT_LIMIT);
        assertThat(testDrone.getBatteryCapacity()).isEqualTo(UPDATED_BATTERY_CAPACITY);
        assertThat(testDrone.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testDrone.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testDrone.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testDrone.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testDrone.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingDrone() throws Exception {
        int databaseSizeBeforeUpdate = droneRepository.findAll().size();
        drone.setId(count.incrementAndGet());

        // Create the Drone
        DroneDTO droneDTO = droneMapper.toDto(drone);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDroneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, droneDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(droneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Drone in the database
        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDrone() throws Exception {
        int databaseSizeBeforeUpdate = droneRepository.findAll().size();
        drone.setId(count.incrementAndGet());

        // Create the Drone
        DroneDTO droneDTO = droneMapper.toDto(drone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDroneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(droneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Drone in the database
        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDrone() throws Exception {
        int databaseSizeBeforeUpdate = droneRepository.findAll().size();
        drone.setId(count.incrementAndGet());

        // Create the Drone
        DroneDTO droneDTO = droneMapper.toDto(drone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDroneMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(droneDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Drone in the database
        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDrone() throws Exception {
        // Initialize the database
        droneRepository.saveAndFlush(drone);

        int databaseSizeBeforeDelete = droneRepository.findAll().size();

        // Delete the drone
        restDroneMockMvc
            .perform(delete(ENTITY_API_URL_ID, drone.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Drone> droneList = droneRepository.findAll();
        assertThat(droneList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

package com.musala.drone.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.musala.drone.IntegrationTest;
import com.musala.drone.domain.Audit;
import com.musala.drone.repository.AuditRepository;
import com.musala.drone.service.criteria.AuditCriteria;
import com.musala.drone.service.dto.AuditDTO;
import com.musala.drone.service.mapper.AuditMapper;
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
 * Integration tests for the {@link AuditResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AuditResourceIT {

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/audits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuditMockMvc;

    private Audit audit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Audit createEntity(EntityManager em) {
        Audit audit = new Audit()
            .message(DEFAULT_MESSAGE)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return audit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Audit createUpdatedEntity(EntityManager em) {
        Audit audit = new Audit()
            .message(UPDATED_MESSAGE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return audit;
    }

    @BeforeEach
    public void initTest() {
        audit = createEntity(em);
    }

    @Test
    @Transactional
    void createAudit() throws Exception {
        int databaseSizeBeforeCreate = auditRepository.findAll().size();
        // Create the Audit
        AuditDTO auditDTO = auditMapper.toDto(audit);
        restAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(auditDTO)))
            .andExpect(status().isCreated());

        // Validate the Audit in the database
        List<Audit> auditList = auditRepository.findAll();
        assertThat(auditList).hasSize(databaseSizeBeforeCreate + 1);
        Audit testAudit = auditList.get(auditList.size() - 1);
        assertThat(testAudit.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testAudit.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testAudit.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testAudit.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testAudit.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void createAuditWithExistingId() throws Exception {
        // Create the Audit with an existing ID
        audit.setId(1L);
        AuditDTO auditDTO = auditMapper.toDto(audit);

        int databaseSizeBeforeCreate = auditRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(auditDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Audit in the database
        List<Audit> auditList = auditRepository.findAll();
        assertThat(auditList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMessageIsRequired() throws Exception {
        int databaseSizeBeforeTest = auditRepository.findAll().size();
        // set the field null
        audit.setMessage(null);

        // Create the Audit, which fails.
        AuditDTO auditDTO = auditMapper.toDto(audit);

        restAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(auditDTO)))
            .andExpect(status().isBadRequest());

        List<Audit> auditList = auditRepository.findAll();
        assertThat(auditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = auditRepository.findAll().size();
        // set the field null
        audit.setCreatedBy(null);

        // Create the Audit, which fails.
        AuditDTO auditDTO = auditMapper.toDto(audit);

        restAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(auditDTO)))
            .andExpect(status().isBadRequest());

        List<Audit> auditList = auditRepository.findAll();
        assertThat(auditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = auditRepository.findAll().size();
        // set the field null
        audit.setCreatedDate(null);

        // Create the Audit, which fails.
        AuditDTO auditDTO = auditMapper.toDto(audit);

        restAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(auditDTO)))
            .andExpect(status().isBadRequest());

        List<Audit> auditList = auditRepository.findAll();
        assertThat(auditList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAudits() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList
        restAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(audit.getId().intValue())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getAudit() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get the audit
        restAuditMockMvc
            .perform(get(ENTITY_API_URL_ID, audit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(audit.getId().intValue()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getAuditsByIdFiltering() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        Long id = audit.getId();

        defaultAuditShouldBeFound("id.equals=" + id);
        defaultAuditShouldNotBeFound("id.notEquals=" + id);

        defaultAuditShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAuditShouldNotBeFound("id.greaterThan=" + id);

        defaultAuditShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAuditShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAuditsByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where message equals to DEFAULT_MESSAGE
        defaultAuditShouldBeFound("message.equals=" + DEFAULT_MESSAGE);

        // Get all the auditList where message equals to UPDATED_MESSAGE
        defaultAuditShouldNotBeFound("message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllAuditsByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where message in DEFAULT_MESSAGE or UPDATED_MESSAGE
        defaultAuditShouldBeFound("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE);

        // Get all the auditList where message equals to UPDATED_MESSAGE
        defaultAuditShouldNotBeFound("message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllAuditsByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where message is not null
        defaultAuditShouldBeFound("message.specified=true");

        // Get all the auditList where message is null
        defaultAuditShouldNotBeFound("message.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditsByMessageContainsSomething() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where message contains DEFAULT_MESSAGE
        defaultAuditShouldBeFound("message.contains=" + DEFAULT_MESSAGE);

        // Get all the auditList where message contains UPDATED_MESSAGE
        defaultAuditShouldNotBeFound("message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllAuditsByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where message does not contain DEFAULT_MESSAGE
        defaultAuditShouldNotBeFound("message.doesNotContain=" + DEFAULT_MESSAGE);

        // Get all the auditList where message does not contain UPDATED_MESSAGE
        defaultAuditShouldBeFound("message.doesNotContain=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllAuditsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where createdBy equals to DEFAULT_CREATED_BY
        defaultAuditShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the auditList where createdBy equals to UPDATED_CREATED_BY
        defaultAuditShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllAuditsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultAuditShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the auditList where createdBy equals to UPDATED_CREATED_BY
        defaultAuditShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllAuditsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where createdBy is not null
        defaultAuditShouldBeFound("createdBy.specified=true");

        // Get all the auditList where createdBy is null
        defaultAuditShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where createdBy contains DEFAULT_CREATED_BY
        defaultAuditShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the auditList where createdBy contains UPDATED_CREATED_BY
        defaultAuditShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllAuditsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where createdBy does not contain DEFAULT_CREATED_BY
        defaultAuditShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the auditList where createdBy does not contain UPDATED_CREATED_BY
        defaultAuditShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllAuditsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where createdDate equals to DEFAULT_CREATED_DATE
        defaultAuditShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the auditList where createdDate equals to UPDATED_CREATED_DATE
        defaultAuditShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllAuditsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultAuditShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the auditList where createdDate equals to UPDATED_CREATED_DATE
        defaultAuditShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllAuditsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where createdDate is not null
        defaultAuditShouldBeFound("createdDate.specified=true");

        // Get all the auditList where createdDate is null
        defaultAuditShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditsByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultAuditShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the auditList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultAuditShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllAuditsByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultAuditShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the auditList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultAuditShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllAuditsByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where lastModifiedBy is not null
        defaultAuditShouldBeFound("lastModifiedBy.specified=true");

        // Get all the auditList where lastModifiedBy is null
        defaultAuditShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllAuditsByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultAuditShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the auditList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultAuditShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllAuditsByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultAuditShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the auditList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultAuditShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllAuditsByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultAuditShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the auditList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultAuditShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllAuditsByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultAuditShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the auditList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultAuditShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllAuditsByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        // Get all the auditList where lastModifiedDate is not null
        defaultAuditShouldBeFound("lastModifiedDate.specified=true");

        // Get all the auditList where lastModifiedDate is null
        defaultAuditShouldNotBeFound("lastModifiedDate.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAuditShouldBeFound(String filter) throws Exception {
        restAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(audit.getId().intValue())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restAuditMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAuditShouldNotBeFound(String filter) throws Exception {
        restAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAuditMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAudit() throws Exception {
        // Get the audit
        restAuditMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAudit() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        int databaseSizeBeforeUpdate = auditRepository.findAll().size();

        // Update the audit
        Audit updatedAudit = auditRepository.findById(audit.getId()).get();
        // Disconnect from session so that the updates on updatedAudit are not directly saved in db
        em.detach(updatedAudit);
        updatedAudit
            .message(UPDATED_MESSAGE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        AuditDTO auditDTO = auditMapper.toDto(updatedAudit);

        restAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, auditDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditDTO))
            )
            .andExpect(status().isOk());

        // Validate the Audit in the database
        List<Audit> auditList = auditRepository.findAll();
        assertThat(auditList).hasSize(databaseSizeBeforeUpdate);
        Audit testAudit = auditList.get(auditList.size() - 1);
        assertThat(testAudit.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testAudit.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testAudit.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testAudit.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testAudit.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingAudit() throws Exception {
        int databaseSizeBeforeUpdate = auditRepository.findAll().size();
        audit.setId(count.incrementAndGet());

        // Create the Audit
        AuditDTO auditDTO = auditMapper.toDto(audit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, auditDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Audit in the database
        List<Audit> auditList = auditRepository.findAll();
        assertThat(auditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAudit() throws Exception {
        int databaseSizeBeforeUpdate = auditRepository.findAll().size();
        audit.setId(count.incrementAndGet());

        // Create the Audit
        AuditDTO auditDTO = auditMapper.toDto(audit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Audit in the database
        List<Audit> auditList = auditRepository.findAll();
        assertThat(auditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAudit() throws Exception {
        int databaseSizeBeforeUpdate = auditRepository.findAll().size();
        audit.setId(count.incrementAndGet());

        // Create the Audit
        AuditDTO auditDTO = auditMapper.toDto(audit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(auditDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Audit in the database
        List<Audit> auditList = auditRepository.findAll();
        assertThat(auditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAuditWithPatch() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        int databaseSizeBeforeUpdate = auditRepository.findAll().size();

        // Update the audit using partial update
        Audit partialUpdatedAudit = new Audit();
        partialUpdatedAudit.setId(audit.getId());

        partialUpdatedAudit.createdBy(UPDATED_CREATED_BY);

        restAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAudit.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAudit))
            )
            .andExpect(status().isOk());

        // Validate the Audit in the database
        List<Audit> auditList = auditRepository.findAll();
        assertThat(auditList).hasSize(databaseSizeBeforeUpdate);
        Audit testAudit = auditList.get(auditList.size() - 1);
        assertThat(testAudit.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testAudit.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testAudit.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testAudit.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testAudit.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateAuditWithPatch() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        int databaseSizeBeforeUpdate = auditRepository.findAll().size();

        // Update the audit using partial update
        Audit partialUpdatedAudit = new Audit();
        partialUpdatedAudit.setId(audit.getId());

        partialUpdatedAudit
            .message(UPDATED_MESSAGE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAudit.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAudit))
            )
            .andExpect(status().isOk());

        // Validate the Audit in the database
        List<Audit> auditList = auditRepository.findAll();
        assertThat(auditList).hasSize(databaseSizeBeforeUpdate);
        Audit testAudit = auditList.get(auditList.size() - 1);
        assertThat(testAudit.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testAudit.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testAudit.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testAudit.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testAudit.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingAudit() throws Exception {
        int databaseSizeBeforeUpdate = auditRepository.findAll().size();
        audit.setId(count.incrementAndGet());

        // Create the Audit
        AuditDTO auditDTO = auditMapper.toDto(audit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, auditDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(auditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Audit in the database
        List<Audit> auditList = auditRepository.findAll();
        assertThat(auditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAudit() throws Exception {
        int databaseSizeBeforeUpdate = auditRepository.findAll().size();
        audit.setId(count.incrementAndGet());

        // Create the Audit
        AuditDTO auditDTO = auditMapper.toDto(audit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(auditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Audit in the database
        List<Audit> auditList = auditRepository.findAll();
        assertThat(auditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAudit() throws Exception {
        int databaseSizeBeforeUpdate = auditRepository.findAll().size();
        audit.setId(count.incrementAndGet());

        // Create the Audit
        AuditDTO auditDTO = auditMapper.toDto(audit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(auditDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Audit in the database
        List<Audit> auditList = auditRepository.findAll();
        assertThat(auditList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAudit() throws Exception {
        // Initialize the database
        auditRepository.saveAndFlush(audit);

        int databaseSizeBeforeDelete = auditRepository.findAll().size();

        // Delete the audit
        restAuditMockMvc
            .perform(delete(ENTITY_API_URL_ID, audit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Audit> auditList = auditRepository.findAll();
        assertThat(auditList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

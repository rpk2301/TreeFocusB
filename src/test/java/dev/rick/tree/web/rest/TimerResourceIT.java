package dev.rick.tree.web.rest;

import static dev.rick.tree.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.rick.tree.IntegrationTest;
import dev.rick.tree.domain.Timer;
import dev.rick.tree.domain.enumeration.TimerStatus;
import dev.rick.tree.repository.TimerRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TimerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TimerResourceIT {

    private static final Integer DEFAULT_DURATION = 1;
    private static final Integer UPDATED_DURATION = 2;

    private static final ZonedDateTime DEFAULT_EXPIRATION_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_EXPIRATION_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final TimerStatus DEFAULT_STATUS = TimerStatus.Running;
    private static final TimerStatus UPDATED_STATUS = TimerStatus.Expired;

    private static final String ENTITY_API_URL = "/api/timers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TimerRepository timerRepository;

    @Mock
    private TimerRepository timerRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTimerMockMvc;

    private Timer timer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Timer createEntity(EntityManager em) {
        Timer timer = new Timer().duration(DEFAULT_DURATION).expirationTime(DEFAULT_EXPIRATION_TIME).status(DEFAULT_STATUS);
        return timer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Timer createUpdatedEntity(EntityManager em) {
        Timer timer = new Timer().duration(UPDATED_DURATION).expirationTime(UPDATED_EXPIRATION_TIME).status(UPDATED_STATUS);
        return timer;
    }

    @BeforeEach
    public void initTest() {
        timer = createEntity(em);
    }

    @Test
    @Transactional
    void createTimer() throws Exception {
        int databaseSizeBeforeCreate = timerRepository.findAll().size();
        // Create the Timer
        restTimerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timer)))
            .andExpect(status().isCreated());

        // Validate the Timer in the database
        List<Timer> timerList = timerRepository.findAll();
        assertThat(timerList).hasSize(databaseSizeBeforeCreate + 1);
        Timer testTimer = timerList.get(timerList.size() - 1);
        assertThat(testTimer.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testTimer.getExpirationTime()).isEqualTo(DEFAULT_EXPIRATION_TIME);
        assertThat(testTimer.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createTimerWithExistingId() throws Exception {
        // Create the Timer with an existing ID
        timer.setId(1L);

        int databaseSizeBeforeCreate = timerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timer)))
            .andExpect(status().isBadRequest());

        // Validate the Timer in the database
        List<Timer> timerList = timerRepository.findAll();
        assertThat(timerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTimers() throws Exception {
        // Initialize the database
        timerRepository.saveAndFlush(timer);

        // Get all the timerList
        restTimerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timer.getId().intValue())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].expirationTime").value(hasItem(sameInstant(DEFAULT_EXPIRATION_TIME))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTimersWithEagerRelationshipsIsEnabled() throws Exception {
        when(timerRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTimerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(timerRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTimersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(timerRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTimerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(timerRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTimer() throws Exception {
        // Initialize the database
        timerRepository.saveAndFlush(timer);

        // Get the timer
        restTimerMockMvc
            .perform(get(ENTITY_API_URL_ID, timer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(timer.getId().intValue()))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION))
            .andExpect(jsonPath("$.expirationTime").value(sameInstant(DEFAULT_EXPIRATION_TIME)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTimer() throws Exception {
        // Get the timer
        restTimerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTimer() throws Exception {
        // Initialize the database
        timerRepository.saveAndFlush(timer);

        int databaseSizeBeforeUpdate = timerRepository.findAll().size();

        // Update the timer
        Timer updatedTimer = timerRepository.findById(timer.getId()).get();
        // Disconnect from session so that the updates on updatedTimer are not directly saved in db
        em.detach(updatedTimer);
        updatedTimer.duration(UPDATED_DURATION).expirationTime(UPDATED_EXPIRATION_TIME).status(UPDATED_STATUS);

        restTimerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTimer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTimer))
            )
            .andExpect(status().isOk());

        // Validate the Timer in the database
        List<Timer> timerList = timerRepository.findAll();
        assertThat(timerList).hasSize(databaseSizeBeforeUpdate);
        Timer testTimer = timerList.get(timerList.size() - 1);
        assertThat(testTimer.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testTimer.getExpirationTime()).isEqualTo(UPDATED_EXPIRATION_TIME);
        assertThat(testTimer.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingTimer() throws Exception {
        int databaseSizeBeforeUpdate = timerRepository.findAll().size();
        timer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Timer in the database
        List<Timer> timerList = timerRepository.findAll();
        assertThat(timerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTimer() throws Exception {
        int databaseSizeBeforeUpdate = timerRepository.findAll().size();
        timer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Timer in the database
        List<Timer> timerList = timerRepository.findAll();
        assertThat(timerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTimer() throws Exception {
        int databaseSizeBeforeUpdate = timerRepository.findAll().size();
        timer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Timer in the database
        List<Timer> timerList = timerRepository.findAll();
        assertThat(timerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTimerWithPatch() throws Exception {
        // Initialize the database
        timerRepository.saveAndFlush(timer);

        int databaseSizeBeforeUpdate = timerRepository.findAll().size();

        // Update the timer using partial update
        Timer partialUpdatedTimer = new Timer();
        partialUpdatedTimer.setId(timer.getId());

        partialUpdatedTimer.expirationTime(UPDATED_EXPIRATION_TIME);

        restTimerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimer))
            )
            .andExpect(status().isOk());

        // Validate the Timer in the database
        List<Timer> timerList = timerRepository.findAll();
        assertThat(timerList).hasSize(databaseSizeBeforeUpdate);
        Timer testTimer = timerList.get(timerList.size() - 1);
        assertThat(testTimer.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testTimer.getExpirationTime()).isEqualTo(UPDATED_EXPIRATION_TIME);
        assertThat(testTimer.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateTimerWithPatch() throws Exception {
        // Initialize the database
        timerRepository.saveAndFlush(timer);

        int databaseSizeBeforeUpdate = timerRepository.findAll().size();

        // Update the timer using partial update
        Timer partialUpdatedTimer = new Timer();
        partialUpdatedTimer.setId(timer.getId());

        partialUpdatedTimer.duration(UPDATED_DURATION).expirationTime(UPDATED_EXPIRATION_TIME).status(UPDATED_STATUS);

        restTimerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimer))
            )
            .andExpect(status().isOk());

        // Validate the Timer in the database
        List<Timer> timerList = timerRepository.findAll();
        assertThat(timerList).hasSize(databaseSizeBeforeUpdate);
        Timer testTimer = timerList.get(timerList.size() - 1);
        assertThat(testTimer.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testTimer.getExpirationTime()).isEqualTo(UPDATED_EXPIRATION_TIME);
        assertThat(testTimer.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingTimer() throws Exception {
        int databaseSizeBeforeUpdate = timerRepository.findAll().size();
        timer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, timer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Timer in the database
        List<Timer> timerList = timerRepository.findAll();
        assertThat(timerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTimer() throws Exception {
        int databaseSizeBeforeUpdate = timerRepository.findAll().size();
        timer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Timer in the database
        List<Timer> timerList = timerRepository.findAll();
        assertThat(timerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTimer() throws Exception {
        int databaseSizeBeforeUpdate = timerRepository.findAll().size();
        timer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(timer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Timer in the database
        List<Timer> timerList = timerRepository.findAll();
        assertThat(timerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTimer() throws Exception {
        // Initialize the database
        timerRepository.saveAndFlush(timer);

        int databaseSizeBeforeDelete = timerRepository.findAll().size();

        // Delete the timer
        restTimerMockMvc
            .perform(delete(ENTITY_API_URL_ID, timer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Timer> timerList = timerRepository.findAll();
        assertThat(timerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

package dev.rick.tree.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.rick.tree.IntegrationTest;
import dev.rick.tree.domain.Tree;
import dev.rick.tree.domain.enumeration.TreeType;
import dev.rick.tree.repository.TreeRepository;
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
 * Integration tests for the {@link TreeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TreeResourceIT {

    private static final TreeType DEFAULT_TREES = TreeType.Dogwood;
    private static final TreeType UPDATED_TREES = TreeType.Willow;

    private static final String ENTITY_API_URL = "/api/trees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TreeRepository treeRepository;

    @Mock
    private TreeRepository treeRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTreeMockMvc;

    private Tree tree;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tree createEntity(EntityManager em) {
        Tree tree = new Tree().trees(DEFAULT_TREES);
        return tree;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tree createUpdatedEntity(EntityManager em) {
        Tree tree = new Tree().trees(UPDATED_TREES);
        return tree;
    }

    @BeforeEach
    public void initTest() {
        tree = createEntity(em);
    }

    @Test
    @Transactional
    void createTree() throws Exception {
        int databaseSizeBeforeCreate = treeRepository.findAll().size();
        // Create the Tree
        restTreeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tree)))
            .andExpect(status().isCreated());

        // Validate the Tree in the database
        List<Tree> treeList = treeRepository.findAll();
        assertThat(treeList).hasSize(databaseSizeBeforeCreate + 1);
        Tree testTree = treeList.get(treeList.size() - 1);
        assertThat(testTree.getTrees()).isEqualTo(DEFAULT_TREES);
    }

    @Test
    @Transactional
    void createTreeWithExistingId() throws Exception {
        // Create the Tree with an existing ID
        tree.setId(1L);

        int databaseSizeBeforeCreate = treeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTreeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tree)))
            .andExpect(status().isBadRequest());

        // Validate the Tree in the database
        List<Tree> treeList = treeRepository.findAll();
        assertThat(treeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTrees() throws Exception {
        // Initialize the database
        treeRepository.saveAndFlush(tree);

        // Get all the treeList
        restTreeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tree.getId().intValue())))
            .andExpect(jsonPath("$.[*].trees").value(hasItem(DEFAULT_TREES.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTreesWithEagerRelationshipsIsEnabled() throws Exception {
        when(treeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTreeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(treeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTreesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(treeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTreeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(treeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTree() throws Exception {
        // Initialize the database
        treeRepository.saveAndFlush(tree);

        // Get the tree
        restTreeMockMvc
            .perform(get(ENTITY_API_URL_ID, tree.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tree.getId().intValue()))
            .andExpect(jsonPath("$.trees").value(DEFAULT_TREES.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTree() throws Exception {
        // Get the tree
        restTreeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTree() throws Exception {
        // Initialize the database
        treeRepository.saveAndFlush(tree);

        int databaseSizeBeforeUpdate = treeRepository.findAll().size();

        // Update the tree
        Tree updatedTree = treeRepository.findById(tree.getId()).get();
        // Disconnect from session so that the updates on updatedTree are not directly saved in db
        em.detach(updatedTree);
        updatedTree.trees(UPDATED_TREES);

        restTreeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTree.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTree))
            )
            .andExpect(status().isOk());

        // Validate the Tree in the database
        List<Tree> treeList = treeRepository.findAll();
        assertThat(treeList).hasSize(databaseSizeBeforeUpdate);
        Tree testTree = treeList.get(treeList.size() - 1);
        assertThat(testTree.getTrees()).isEqualTo(UPDATED_TREES);
    }

    @Test
    @Transactional
    void putNonExistingTree() throws Exception {
        int databaseSizeBeforeUpdate = treeRepository.findAll().size();
        tree.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTreeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tree.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tree))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tree in the database
        List<Tree> treeList = treeRepository.findAll();
        assertThat(treeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTree() throws Exception {
        int databaseSizeBeforeUpdate = treeRepository.findAll().size();
        tree.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTreeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tree))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tree in the database
        List<Tree> treeList = treeRepository.findAll();
        assertThat(treeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTree() throws Exception {
        int databaseSizeBeforeUpdate = treeRepository.findAll().size();
        tree.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTreeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tree)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tree in the database
        List<Tree> treeList = treeRepository.findAll();
        assertThat(treeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTreeWithPatch() throws Exception {
        // Initialize the database
        treeRepository.saveAndFlush(tree);

        int databaseSizeBeforeUpdate = treeRepository.findAll().size();

        // Update the tree using partial update
        Tree partialUpdatedTree = new Tree();
        partialUpdatedTree.setId(tree.getId());

        partialUpdatedTree.trees(UPDATED_TREES);

        restTreeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTree.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTree))
            )
            .andExpect(status().isOk());

        // Validate the Tree in the database
        List<Tree> treeList = treeRepository.findAll();
        assertThat(treeList).hasSize(databaseSizeBeforeUpdate);
        Tree testTree = treeList.get(treeList.size() - 1);
        assertThat(testTree.getTrees()).isEqualTo(UPDATED_TREES);
    }

    @Test
    @Transactional
    void fullUpdateTreeWithPatch() throws Exception {
        // Initialize the database
        treeRepository.saveAndFlush(tree);

        int databaseSizeBeforeUpdate = treeRepository.findAll().size();

        // Update the tree using partial update
        Tree partialUpdatedTree = new Tree();
        partialUpdatedTree.setId(tree.getId());

        partialUpdatedTree.trees(UPDATED_TREES);

        restTreeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTree.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTree))
            )
            .andExpect(status().isOk());

        // Validate the Tree in the database
        List<Tree> treeList = treeRepository.findAll();
        assertThat(treeList).hasSize(databaseSizeBeforeUpdate);
        Tree testTree = treeList.get(treeList.size() - 1);
        assertThat(testTree.getTrees()).isEqualTo(UPDATED_TREES);
    }

    @Test
    @Transactional
    void patchNonExistingTree() throws Exception {
        int databaseSizeBeforeUpdate = treeRepository.findAll().size();
        tree.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTreeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tree.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tree))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tree in the database
        List<Tree> treeList = treeRepository.findAll();
        assertThat(treeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTree() throws Exception {
        int databaseSizeBeforeUpdate = treeRepository.findAll().size();
        tree.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTreeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tree))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tree in the database
        List<Tree> treeList = treeRepository.findAll();
        assertThat(treeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTree() throws Exception {
        int databaseSizeBeforeUpdate = treeRepository.findAll().size();
        tree.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTreeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(tree)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tree in the database
        List<Tree> treeList = treeRepository.findAll();
        assertThat(treeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTree() throws Exception {
        // Initialize the database
        treeRepository.saveAndFlush(tree);

        int databaseSizeBeforeDelete = treeRepository.findAll().size();

        // Delete the tree
        restTreeMockMvc
            .perform(delete(ENTITY_API_URL_ID, tree.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tree> treeList = treeRepository.findAll();
        assertThat(treeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

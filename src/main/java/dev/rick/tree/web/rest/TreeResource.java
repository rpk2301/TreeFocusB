package dev.rick.tree.web.rest;

import dev.rick.tree.domain.Tree;
import dev.rick.tree.repository.TreeRepository;
import dev.rick.tree.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link dev.rick.tree.domain.Tree}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TreeResource {

    private final Logger log = LoggerFactory.getLogger(TreeResource.class);

    private static final String ENTITY_NAME = "tree";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TreeRepository treeRepository;

    public TreeResource(TreeRepository treeRepository) {
        this.treeRepository = treeRepository;
    }

    /**
     * {@code POST  /trees} : Create a new tree.
     *
     * @param tree the tree to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tree, or with status {@code 400 (Bad Request)} if the tree has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/trees")
    public ResponseEntity<Tree> createTree(@RequestBody Tree tree) throws URISyntaxException {
        log.debug("REST request to save Tree : {}", tree);
        if (tree.getId() != null) {
            throw new BadRequestAlertException("A new tree cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Tree result = treeRepository.save(tree);
        return ResponseEntity
            .created(new URI("/api/trees/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /trees/:id} : Updates an existing tree.
     *
     * @param id the id of the tree to save.
     * @param tree the tree to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tree,
     * or with status {@code 400 (Bad Request)} if the tree is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tree couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/trees/{id}")
    public ResponseEntity<Tree> updateTree(@PathVariable(value = "id", required = false) final Long id, @RequestBody Tree tree)
        throws URISyntaxException {
        log.debug("REST request to update Tree : {}, {}", id, tree);
        if (tree.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tree.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!treeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Tree result = treeRepository.save(tree);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tree.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /trees/:id} : Partial updates given fields of an existing tree, field will ignore if it is null
     *
     * @param id the id of the tree to save.
     * @param tree the tree to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tree,
     * or with status {@code 400 (Bad Request)} if the tree is not valid,
     * or with status {@code 404 (Not Found)} if the tree is not found,
     * or with status {@code 500 (Internal Server Error)} if the tree couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/trees/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Tree> partialUpdateTree(@PathVariable(value = "id", required = false) final Long id, @RequestBody Tree tree)
        throws URISyntaxException {
        log.debug("REST request to partial update Tree partially : {}, {}", id, tree);
        if (tree.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tree.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!treeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Tree> result = treeRepository
            .findById(tree.getId())
            .map(existingTree -> {
                if (tree.getTrees() != null) {
                    existingTree.setTrees(tree.getTrees());
                }

                return existingTree;
            })
            .map(treeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tree.getId().toString())
        );
    }

    /**
     * {@code GET  /trees} : get all the trees.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trees in body.
     */
    @GetMapping("/trees")
    public List<Tree> getAllTrees(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Trees");
        if (eagerload) {
            return treeRepository.findAllWithEagerRelationships();
        } else {
            return treeRepository.findAll();
        }
    }

    /**
     * {@code GET  /trees/:id} : get the "id" tree.
     *
     * @param id the id of the tree to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tree, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/trees/{id}")
    public ResponseEntity<Tree> getTree(@PathVariable Long id) {
        log.debug("REST request to get Tree : {}", id);
        Optional<Tree> tree = treeRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(tree);
    }

    /**
     * {@code DELETE  /trees/:id} : delete the "id" tree.
     *
     * @param id the id of the tree to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/trees/{id}")
    public ResponseEntity<Void> deleteTree(@PathVariable Long id) {
        log.debug("REST request to delete Tree : {}", id);
        treeRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

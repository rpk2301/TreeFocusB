package dev.rick.tree.web.rest;

import dev.rick.tree.domain.Bank;
import dev.rick.tree.repository.BankRepository;
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
 * REST controller for managing {@link dev.rick.tree.domain.Bank}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BankResource {

    private final Logger log = LoggerFactory.getLogger(BankResource.class);

    private static final String ENTITY_NAME = "bank";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BankRepository bankRepository;

    public BankResource(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    /**
     * {@code POST  /banks} : Create a new bank.
     *
     * @param bank the bank to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bank, or with status {@code 400 (Bad Request)} if the bank has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/banks")
    public ResponseEntity<Bank> createBank(@RequestBody Bank bank) throws URISyntaxException {
        log.debug("REST request to save Bank : {}", bank);
        if (bank.getId() != null) {
            throw new BadRequestAlertException("A new bank cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Bank result = bankRepository.save(bank);
        return ResponseEntity
            .created(new URI("/api/banks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /banks/:id} : Updates an existing bank.
     *
     * @param id the id of the bank to save.
     * @param bank the bank to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bank,
     * or with status {@code 400 (Bad Request)} if the bank is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bank couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/banks/{id}")
    public ResponseEntity<Bank> updateBank(@PathVariable(value = "id", required = false) final Long id, @RequestBody Bank bank)
        throws URISyntaxException {
        log.debug("REST request to update Bank : {}, {}", id, bank);
        if (bank.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bank.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bankRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Bank result = bankRepository.save(bank);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bank.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /banks/:id} : Partial updates given fields of an existing bank, field will ignore if it is null
     *
     * @param id the id of the bank to save.
     * @param bank the bank to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bank,
     * or with status {@code 400 (Bad Request)} if the bank is not valid,
     * or with status {@code 404 (Not Found)} if the bank is not found,
     * or with status {@code 500 (Internal Server Error)} if the bank couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/banks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Bank> partialUpdateBank(@PathVariable(value = "id", required = false) final Long id, @RequestBody Bank bank)
        throws URISyntaxException {
        log.debug("REST request to partial update Bank partially : {}, {}", id, bank);
        if (bank.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bank.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bankRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Bank> result = bankRepository
            .findById(bank.getId())
            .map(existingBank -> {
                if (bank.getTreesowned() != null) {
                    existingBank.setTreesowned(bank.getTreesowned());
                }

                return existingBank;
            })
            .map(bankRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bank.getId().toString())
        );
    }

    /**
     * {@code GET  /banks} : get all the banks.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of banks in body.
     */
    @GetMapping("/banks")
    public List<Bank> getAllBanks(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Banks");
        if (eagerload) {
            return bankRepository.findAllWithEagerRelationships();
        } else {
            return bankRepository.findAll();
        }
    }

    /**
     * {@code GET  /banks/:id} : get the "id" bank.
     *
     * @param id the id of the bank to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bank, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/banks/{id}")
    public ResponseEntity<Bank> getBank(@PathVariable Long id) {
        log.debug("REST request to get Bank : {}", id);
        Optional<Bank> bank = bankRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(bank);
    }

    /**
     * {@code DELETE  /banks/:id} : delete the "id" bank.
     *
     * @param id the id of the bank to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/banks/{id}")
    public ResponseEntity<Void> deleteBank(@PathVariable Long id) {
        log.debug("REST request to delete Bank : {}", id);
        bankRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

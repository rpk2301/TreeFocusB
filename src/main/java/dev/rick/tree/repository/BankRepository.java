package dev.rick.tree.repository;

import dev.rick.tree.domain.Bank;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Bank entity.
 */
@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    default Optional<Bank> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Bank> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Bank> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct bank from Bank bank left join fetch bank.assignedTo",
        countQuery = "select count(distinct bank) from Bank bank"
    )
    Page<Bank> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct bank from Bank bank left join fetch bank.assignedTo")
    List<Bank> findAllWithToOneRelationships();

    @Query("select bank from Bank bank left join fetch bank.assignedTo where bank.id =:id")
    Optional<Bank> findOneWithToOneRelationships(@Param("id") Long id);
}

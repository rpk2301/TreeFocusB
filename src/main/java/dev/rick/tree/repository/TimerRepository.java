package dev.rick.tree.repository;

import dev.rick.tree.domain.Timer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Timer entity.
 */
@Repository
public interface TimerRepository extends JpaRepository<Timer, Long> {
    default Optional<Timer> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Timer> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Timer> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct timer from Timer timer left join fetch timer.assignedTo",
        countQuery = "select count(distinct timer) from Timer timer"
    )
    Page<Timer> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct timer from Timer timer left join fetch timer.assignedTo")
    List<Timer> findAllWithToOneRelationships();

    @Query("select timer from Timer timer left join fetch timer.assignedTo where timer.id =:id")
    Optional<Timer> findOneWithToOneRelationships(@Param("id") Long id);
}

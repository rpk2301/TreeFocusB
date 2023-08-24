package dev.rick.tree.repository;

import dev.rick.tree.domain.Tree;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Tree entity.
 */
@Repository
public interface TreeRepository extends JpaRepository<Tree, Long> {
    @Query("select tree from Tree tree where tree.assignedTo.login = ?#{principal.username}")
    List<Tree> findByAssignedToIsCurrentUser();

    default Optional<Tree> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Tree> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Tree> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct tree from Tree tree left join fetch tree.assignedTo",
        countQuery = "select count(distinct tree) from Tree tree"
    )
    Page<Tree> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct tree from Tree tree left join fetch tree.assignedTo")
    List<Tree> findAllWithToOneRelationships();

    @Query("select tree from Tree tree left join fetch tree.assignedTo where tree.id =:id")
    Optional<Tree> findOneWithToOneRelationships(@Param("id") Long id);
}

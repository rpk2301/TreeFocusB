package dev.rick.tree.domain;

import dev.rick.tree.domain.enumeration.TreeType;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Tree.
 */
@Entity
@Table(name = "tree")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tree implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "trees")
    private TreeType trees;

    @ManyToOne
    private User assignedTo;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tree id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TreeType getTrees() {
        return this.trees;
    }

    public Tree trees(TreeType trees) {
        this.setTrees(trees);
        return this;
    }

    public void setTrees(TreeType trees) {
        this.trees = trees;
    }

    public User getAssignedTo() {
        return this.assignedTo;
    }

    public void setAssignedTo(User user) {
        this.assignedTo = user;
    }

    public Tree assignedTo(User user) {
        this.setAssignedTo(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tree)) {
            return false;
        }
        return id != null && id.equals(((Tree) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tree{" +
            "id=" + getId() +
            ", trees='" + getTrees() + "'" +
            "}";
    }
}

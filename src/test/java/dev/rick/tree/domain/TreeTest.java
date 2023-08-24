package dev.rick.tree.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.rick.tree.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TreeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tree.class);
        Tree tree1 = new Tree();
        tree1.setId(1L);
        Tree tree2 = new Tree();
        tree2.setId(tree1.getId());
        assertThat(tree1).isEqualTo(tree2);
        tree2.setId(2L);
        assertThat(tree1).isNotEqualTo(tree2);
        tree1.setId(null);
        assertThat(tree1).isNotEqualTo(tree2);
    }
}

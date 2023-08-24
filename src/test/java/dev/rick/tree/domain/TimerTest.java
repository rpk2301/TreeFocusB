package dev.rick.tree.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.rick.tree.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Timer.class);
        Timer timer1 = new Timer();
        timer1.setId(1L);
        Timer timer2 = new Timer();
        timer2.setId(timer1.getId());
        assertThat(timer1).isEqualTo(timer2);
        timer2.setId(2L);
        assertThat(timer1).isNotEqualTo(timer2);
        timer1.setId(null);
        assertThat(timer1).isNotEqualTo(timer2);
    }
}

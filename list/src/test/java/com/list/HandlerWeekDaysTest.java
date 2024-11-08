package com.list;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

public class HandlerWeekDaysTest {

    @Test
    void testCreateList() {

        HandlerWeekDays handlerWeekDays = new HandlerWeekDays(); // 1
        assertThat(handlerWeekDays.days.size(), is(7));

    }

    @Test
    void testGetDays() {
        HandlerWeekDays handlerWeekDays = new HandlerWeekDays();
        assertThat(handlerWeekDays.getDays().get(0), is("Lunes"));
    }

}

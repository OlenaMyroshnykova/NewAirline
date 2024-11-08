package com.list;

import java.util.ArrayList;
import java.util.List;

public class HandlerWeekDays {
    public List<String> days;

    public HandlerWeekDays() {
        createList();
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
    this.days = days;
    }

    public void createList() {
        days = new ArrayList<String>();
        days.add("Lunes");
        days.add("Martes");
        days.add("Miercoles");
        days.add("Jueves");
        days.add("Viernes");
        days.add("Sabado");
        days.add("Domingo");
    }
}

package com.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HandlerWeekDays {
    public List<String> days;

    public HandlerWeekDays() {
        createList();
    }

    public List<String> getDays() {
        return days;
    }

    public int getDaysCount() {
        return days.size();
    }
    public boolean removeDay(String day) {
        return days.remove(day);
    }

    public String getDay(int index) {
        if (index >= 0 && index < days.size()) {
            return days.get(index);
        } else {
            throw new IndexOutOfBoundsException("Índice fuera de rango.");
        }
    }

    public boolean dayExists(String day) {
        return days.contains(day);
    }

    public void sortDaysAlphabetically() {
        Collections.sort(days);
    }

    public void clearDays() {
        days.clear();
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

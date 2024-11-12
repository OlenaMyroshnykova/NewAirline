# List
```mermaid
classDiagram
    class HandlerWeekDays {
        - List~String~ days
        + WeekDaysManager()
        + List~String~ getDays()
        + int getDaysCount()
        + boolean removeDay(String day)
        + String getDay(int index)
        + boolean dayExists(String day)
        + void sortDaysAlphabetically()
        + void clearDays()
    }
```
# Armstrong

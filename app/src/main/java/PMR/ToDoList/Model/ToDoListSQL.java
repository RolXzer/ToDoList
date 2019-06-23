package PMR.ToDoList.Model;

import androidx.room.Entity;

@Entity(tableName = "todolist_table")
public class ToDoListSQL {

    private String hashUser;
    private int idList;
    private String label;
}

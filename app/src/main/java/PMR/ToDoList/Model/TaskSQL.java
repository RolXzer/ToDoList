package PMR.ToDoList.Model;

import androidx.room.Entity;

@Entity(tableName = "task_table")
public class TaskSQL {

    private int idList;
    private int idTask;
    private String label;
    private int checked;
}

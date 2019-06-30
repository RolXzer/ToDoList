package PMR.ToDoList.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;

@Dao
public interface ToDoListDAO {

    @Insert
    void insertToDoList(ToDoList toDoList);

    @Update
    void updateToDoList(ToDoList toDoList);

    @Delete
    void deleteToDoList(ToDoList toDoList);

    @Query("DELETE FROM todolist_table")
    void deleteAllToDoLists();

    @Query("SELECT * FROM TODOLIST_TABLE")
    ArrayList<ToDoList> getAllToDoLists();

}

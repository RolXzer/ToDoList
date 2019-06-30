package PMR.ToDoList.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import PMR.ToDoList.data.Model.User;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Query("DELETE FROM user_table")
    void deleteAllUsers();

    @Query("SELECT * from user_table ORDER BY idUser ASC")
    List<User> getAllUsers();
}
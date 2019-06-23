package PMR.ToDoList.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;

@Dao
public interface UserDAO {

    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM user_table")
    void deleteAllUsers();

    @Query("SELECT pseudo,password,hash FROM user_table ORDER BY idUser DESC")
    ArrayList<User> getAllUsers();
}

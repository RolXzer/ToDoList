package PMR.ToDoList.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class UserSQL {

    //On crée un nouvel attribut qui sera la clé principale de notre base de données SQL
    @PrimaryKey(autoGenerate = true)
    private int idUser;

    private String pseudo;
    private String password;
    private String hash;

    public UserSQL(String pseudo, String password, String hash) {
        this.pseudo = pseudo;
        this.password = password;
        this.hash = hash;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}

package PMR.ToDoList.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {

    private String pseudo;
    private String password;
    private String hash;
    private ArrayList<ToDoList> toDoLists;

    // CONSTRUCTEURS

    public User(String pseudo, String password) {
        this.pseudo = pseudo;
        this.password = password;
    }

    protected User(Parcel in) {
        pseudo = in.readString();
        password = in.readString();
        hash = in.readString();
        in.readTypedList(this.toDoLists, ToDoList.CREATOR);
    }


    // GETTERS & SETTERS

    public String getPseudo() {
        return pseudo;
    }

    public String getHash() {
        return hash;
    }

    public ArrayList<ToDoList> getToDoLists() {
        return toDoLists;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setToDoLists(ArrayList<ToDoList> toDoLists) {
        toDoLists = toDoLists;
    }

    // PARCELABLE IMPLEMENTATION
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pseudo);
        dest.writeString(this.password);
        dest.writeString(this.hash);

        dest.writeTypedList(this.toDoLists);
    }


    // METHODES UTILES

    public void ajouteListe(ToDoList uneListe)
    {
        this.toDoLists.add(uneListe);
    }


    public void supprimeListe(int id)
    {
        this.toDoLists.remove(id);
    }



    @Override
    public String toString() {
        return "User{" +
                "pseudo='" + pseudo + '\'' +
                ", password='" + password + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }


}

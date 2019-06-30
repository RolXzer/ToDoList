package PMR.ToDoList.data.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(onDelete = CASCADE,entity = User.class,
        parentColumns = "idUser",
        childColumns = "keyUser"),
        tableName = "toDoList_table")
public class ToDoList implements Parcelable {

    @PrimaryKey(autoGenerate = false)
    private int idToDoList;//////////////////////////////////////////////
    private String label;
    private int keyUser;
    @Ignore
    private ArrayList<Task> tasksList;


    // CONSTRUCTEURS

    public ToDoList(int idToDoList, String label, int keyUser) {
        this.idToDoList = idToDoList;
        this.label = label;
        this.keyUser = keyUser;
        this.tasksList = new ArrayList<>();
    }

    @Ignore
    public ToDoList(int idToDoList, String label, int keyUser, ArrayList<Task> tasksList) {
        this.idToDoList = idToDoList;
        this.label = label;
        this.keyUser = keyUser;
        this.tasksList = tasksList;
    }

    @Ignore
    protected ToDoList(Parcel in) {
        idToDoList = in.readInt();
        label = in.readString();
        keyUser = in.readInt();
        tasksList= new ArrayList<>();
        in.readTypedList(this.tasksList, Task.CREATOR);
    }

    //GETTERS

    public int getIdToDoList() { return idToDoList; }

    public String getLabel() { return label; }

    public int getKeyUser() { return keyUser; }

    public void setKeyUser(int keyUser) { this.keyUser = keyUser; }

    public ArrayList<Task> getTasksList() { return tasksList; }

    // SETTERS
    public void setIdToDoList(int idToDoList) {
        this.idToDoList = idToDoList;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setTasksList(ArrayList<Task> tasksList) {
        this.tasksList = tasksList;
    }

    public void setLesItems(ArrayList<Task> lesItems) {
        this.tasksList = lesItems;
    }

    // PARCELABLE IMPLEMENTATION

    public static final Creator<ToDoList> CREATOR = new Creator<ToDoList>() {
        @Override
        public ToDoList createFromParcel(Parcel in) {
            return new ToDoList(in);
        }

        @Override
        public ToDoList[] newArray(int size) {
            return new ToDoList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idToDoList);
        dest.writeString(this.label);
        dest.writeInt(this.keyUser);
        dest.writeTypedList(this.tasksList);
    }


    // METHODES UTILES

    public void ajouterItem(Task unItem) {
        this.tasksList.add(unItem);
    }

    public Boolean validerItem(String s) {
        int indice = -1;

        if ((indice = rechercherItem(s)) >= 0) {
            this.tasksList.get(indice).setChecked(1);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    public int rechercherItem(String s) {
        int retour = -1;
        Boolean trouve = Boolean.FALSE;
        for (int i = 0; i < this.tasksList.size(); i++) {
            if (this.tasksList.get(i).getLabelTask() == s) {
                retour = i;
                i = this.tasksList.size();
                trouve = Boolean.TRUE;
            }
        }
        return retour;
    }

    @Override
    public String toString() {
        return "ToDoList{" +
                "idToDoList=" + idToDoList +
                ", label='" + label + '\'' +
                ", keyUser=" + keyUser +
                ", tasksList=" + tasksList +
                '}';
    }
}
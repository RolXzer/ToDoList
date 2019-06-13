package PMR.ToDoList.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;


public class Task implements Parcelable {
    private int id;
    private String label;
    private String url;
    private int checked;

    //CONSTRUCTEURS

    public Task(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public Task(int id, String label, String url, int checked) {
        this.id = id;
        this.label = label;
        this.url = url;
        this.checked = checked;
    }

    protected Task(Parcel in) {
        label = in.readString();
        checked = in.readInt();
        id = in.readInt();
    }

    //GETTERS & SETTERS

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }

    public int getChecked() {
        return checked;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    // PARCELABLE IMPLEMENTATION
    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.label);
        dest.writeInt(this.checked);
        dest.writeInt(this.id);
    }

    //Methodes utiles

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", url='" + url + '\'' +
                ", checked=" + checked +
                '}';
    }


}

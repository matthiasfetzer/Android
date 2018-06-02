package matthiasfetzer.com.todoliste.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by matthiasfetzer on 28.03.18.
 */

public class TodoItem implements Serializable{

    private long id;
    private String name;
    private String description;
    @SerializedName("done")
    private boolean todoDone;
    @SerializedName("favourite")
    private boolean important;
    @SerializedName("expiry")
    private long date;
    private List<String> contacts;


    public TodoItem() {
    }

    public TodoItem(String name) {
        this.name = name;
    }

    public TodoItem(String name, String description, boolean todoDone, boolean important, long date) {
        this.name = name;
        this.description = description;
        this.todoDone = todoDone;
        this.important = important;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isTodoDone() {
        return todoDone;
    }

    public void setTodoDone(boolean todoDone) {
        this.todoDone = todoDone;
    }

    public long getDate() {
        return date;
    }

    public void setDate(LocalDateTime dateTime) {
        this.date = date;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    @Override
    public boolean equals(Object obj) {

            if (obj == null) return false;
            if (obj == this) return true;
            if (!(obj instanceof TodoItem)) return false;
            TodoItem o = (TodoItem) obj;
            return o.id == this.id;
    }
}


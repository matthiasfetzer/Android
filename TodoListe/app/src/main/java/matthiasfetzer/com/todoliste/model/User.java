package matthiasfetzer.com.todoliste.model;

import java.io.Serializable;

public class User implements Serializable {

    private String email;
    private String pwd;


    public User(String email, String pwd) {
        this.email = email;
        this.pwd = pwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}

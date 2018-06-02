package matthiasfetzer.com.todoliste.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class Contact {

    private String name;
    private String email;
    private String mobileNumber;
    private Bitmap image;
    private Uri contactUri;


    public Contact(String name, String email, String mobileNumber, Uri contactUri) {
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.contactUri = contactUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Uri getContactUri() {
        return contactUri;
    }

    public void setContactUri(Uri contactUri) {
        this.contactUri = contactUri;
    }
}

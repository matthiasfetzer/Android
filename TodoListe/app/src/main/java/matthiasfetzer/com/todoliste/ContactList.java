package matthiasfetzer.com.todoliste;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import matthiasfetzer.com.todoliste.model.Contact;
import matthiasfetzer.com.todoliste.model.TodoItem;

public class ContactList extends AppCompatActivity {

    private ArrayAdapter contactArrayAdapter;
    private ViewGroup contactListView;
    private TodoItem todoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        todoItem = (TodoItem) getIntent().getSerializableExtra("todoItem");
        contactListView = (ViewGroup) findViewById(R.id.contactList);

        contactArrayAdapter = new ArrayAdapter<Contact>(this, R.layout.activity_contact_list_singleview) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View contactView, @NonNull ViewGroup parent) {

                if (contactView == null) {
                    contactView = getLayoutInflater().inflate(R.layout.activity_contact_list_singleview, null);
                }

                final Contact contact = getItem(position);




                final TextView contactViewName = (TextView) contactView.findViewById(R.id.contact_list_singleview_name);
                final ImageView contactViewImage = (ImageView) contactView.findViewById(R.id.contact_list_singleview_image);
                final ImageButton deleteImageButton = (ImageButton) contactView.findViewById(R.id.contact_list_singleview_delete_button);
                final ImageButton smsImageButton = (ImageButton) contactView.findViewById(R.id.contact_list_singleview_sms_button);
                final ImageButton emailImageButton = (ImageButton) contactView.findViewById(R.id.contact_list_singleview_email_button);


                deleteImageButton.setOnClickListener(null);
                deleteImageButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        contactArrayAdapter.remove(contact);
                        int contactIndex = todoItem.getContacts().indexOf(contact.getContactUri().toString());
                        List<String> tempContactList = new ArrayList<>();
                        tempContactList.addAll(todoItem.getContacts());
                        tempContactList.remove(contactIndex);
                        todoItem.setContacts(tempContactList);
                        Toast.makeText(ContactList.this, "Contact deleted! Please make sure to save you changes afterwards!",Toast.LENGTH_SHORT).show();
                    }

                });

                smsImageButton.setOnClickListener(null);
                smsImageButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        int checkSendSMS = ContextCompat.checkSelfPermission(ContactList.this, Manifest.permission.SEND_SMS);
                        int checkPhoneState = ContextCompat.checkSelfPermission(ContactList.this, Manifest.permission.READ_PHONE_STATE);

                            if (checkSendSMS != PackageManager.PERMISSION_GRANTED && checkPhoneState != PackageManager.PERMISSION_GRANTED ) {

                                requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE} , 101);
                            } else {
                                if (contact.getMobileNumber() !=null && !contact.getMobileNumber().trim().isEmpty()) {
                                    String smsText = "Hallo " + contact.getName() + ",\n" +
                                            "Ich habe dir das Todo: " + todoItem.getName() + " zugewiesen! \n" +
                                            "Beschreibung: " + todoItem.getDescription() + "\n" +
                                            "MfG Matthias";

                                    SmsManager.getDefault().sendTextMessage(contact.getMobileNumber(), null, smsText, null, null);
                                    Toast.makeText(ContactList.this, "SMS send to: " + contact.getName(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });

                emailImageButton.setOnClickListener(null);
                emailImageButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {


                        if (contact.getEmail() !=null && !contact.getEmail().trim().isEmpty() ) {
                            String emailText = "Hallo " + contact.getName() + ",\n" +
                                    "Ich habe dir das Todo: " + todoItem.getName() + " zugewiesen! \n" +
                                    "Beschreibung: " + todoItem.getDescription() + "\n" +
                                    "MfG Matthias";

                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL, new String[]{contact.getEmail()});
                            i.putExtra(Intent.EXTRA_SUBJECT, "Todo Item: " + todoItem.getName());
                            i.putExtra(Intent.EXTRA_TEXT, emailText);
                            try {
                                startActivity(Intent.createChooser(i, "Sende Mail..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(ContactList.this, "Es sind keine Mail-Clients installiert, weshalb die Mail nicht versendet werden kann.", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(ContactList.this, "Keine E-mail Adresse gefunden! Bitte SMS verschicken", Toast.LENGTH_LONG).show();
                        }
                    }

                });


                contactViewName.setText(contact.getName());
                contactViewImage.setImageBitmap(contact.getImage());

                return contactView;
            }
        };
        ((ListView)contactListView).setAdapter(contactArrayAdapter);
        contactArrayAdapter.setNotifyOnChange(true);

        readAndAddAllContacts();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_navigation_add_contact, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_contact) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            } else {
                addContact();
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                addContact();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot read contacts", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                Toast.makeText(this, "permission is granted now. Please try again.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot send SMS", Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            processSelectedContact(data.getData());
            List<String> tempModList = new ArrayList<>();
            tempModList.addAll(todoItem.getContacts());
            tempModList.add(data.getData().toString());
            todoItem.setContacts(tempModList);

        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent(ContactList.this, TodoListDetailView.class);
        returnIntent.putExtra("todoItem", todoItem);
        setResult(RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();
    }

    private void processSelectedContact (Uri uri) {
        Cursor cursor = getContentResolver().query(uri,null, null, null, null);
        if(cursor == null) {
            return;
        }
        cursor.moveToNext();
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));


        String email = "";
        String phoneNumber = "";
        Bitmap photoBitmap = null;

        Log.i("Contact Name: ", name);
        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);
        if (phoneCursor.getCount() > 0) {
            phoneCursor.moveToFirst();
            do {
                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int phoneNumberType = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA2));

                if (phoneNumberType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                    Log.i("mobile number", phoneNumber);
                    break;
                }

            } while (phoneCursor.moveToNext());
        }
        long emailId = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID));
        Cursor emailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[]{String.valueOf(emailId)}, null);
        if (emailCursor.getCount() > 0) {
            emailCursor.moveToFirst();

            email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            Log.i("email", email);
        }

        Uri photoUri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Photo.PHOTO, ContactsContract.Data._ID, ContactsContract.Data.CONTACT_ID };
        String selection = ContactsContract.Data.CONTACT_ID + " = " + contactId;
        Cursor imageCursor = getContentResolver().query(photoUri, projection, selection, null, null);

        try {
            while (imageCursor.moveToNext()) {
                byte[] photo = imageCursor.getBlob(0);
                if (photo != null) {
                    photoBitmap  = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                }
                else {
                    String imageDefaultUri = "@drawable/ic_contacts";
                    int imageResource = getResources().getIdentifier(imageDefaultUri, null, getPackageName());
                    photoBitmap = ((BitmapDrawable)getResources().getDrawable(imageResource)).getBitmap();

                }
            }
        } finally {
            imageCursor.close();
        }



        Contact contact = new Contact(name, email, phoneNumber, uri);
        contact.setImage(photoBitmap);
        contactArrayAdapter.add(contact);

    }

    private void addContact() {

        Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactIntent, 1 );
    }

    private void readAndAddAllContacts() {
        contactArrayAdapter.clear();
        for (String uri : todoItem.getContacts()) {
            processSelectedContact(Uri.parse(uri));
        }
    }

}

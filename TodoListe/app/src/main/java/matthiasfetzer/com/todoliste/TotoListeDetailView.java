package matthiasfetzer.com.todoliste;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import matthiasfetzer.com.todoliste.model.TodoItem;


public class TotoListeDetailView extends AppCompatActivity {

    public static final int RESULT_DELETE = 3;
    private TodoItem todoItem;
    private EditText todoName;
    private EditText todoDescription;
    private CheckBox todoDone;
    private Switch todoImportant;
    private Calendar calender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toto_liste_detail_view);

        todoName = (EditText)findViewById(R.id.totoDetailName);
        todoDescription = (EditText)findViewById(R.id.totoDetailDescription);
        todoDone = (CheckBox)findViewById(R.id.totoDetailDone);
        todoImportant = (Switch)findViewById(R.id.totoDetailImportant);
        final Button dateSelector = (Button)findViewById(R.id.totoDetailDate);
        final Button timeSelector = (Button)findViewById(R.id.totoDetailTime);
        calender = new GregorianCalendar();

        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        todoItem = (TodoItem) getIntent().getSerializableExtra ("todoItem");

        if (todoItem != null) {
            todoName.setText(todoItem.getName());
            todoDescription.setText(todoItem.getDescription());
            todoDone.setChecked(todoItem.isTodoDone());
            todoImportant.setChecked(todoItem.isImportant());
            calender.setTimeInMillis(todoItem.getDate());
            dateSelector.setText(dateFormat.format(todoItem.getDate()));
            timeSelector.setText(timeFormat.format(todoItem.getDate()));


        } else {
            todoItem = new TodoItem();
            // set default date today
            dateSelector.setText(dateFormat.format(calender.getTime()));
            timeSelector.setText(timeFormat.format(calender.getTime()));
        }





        dateSelector.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(TotoListeDetailView.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        calender.set(year, month, day);
                        dateSelector.setText(dateFormat.format(calender.getTime()));
                    }
                }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

            }
        });


        timeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog timePickerDialog = new  TimePickerDialog(TotoListeDetailView.this, new TimePickerDialog.OnTimeSetListener () {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        calender.set(calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH), hour, minute);
                        timeSelector.setText(timeFormat.format(calender.getTime()));
                    }
                }, calender.get(Calendar.HOUR), calender.get(Calendar.MINUTE), true);
                timePickerDialog.show();

            }
        });





        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_detailview);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_cancel:
                                Intent todoListIntent = new Intent(TotoListeDetailView.this, TodoListeOverview.class);
                                startActivity(todoListIntent);
                                return true;
                            case R.id.action_check:
                                saveItem();
                                return true;
                            case R.id.action_delete:
                                deleteItem();
                                return true;
                        }
                        return true;
                    }
         });
    }

    private void saveItem () {
        Intent returnIntent = new Intent(this, TodoListeOverview.class);
        todoItem.setName(todoName.getText().toString());
        todoItem.setDescription(todoDescription.getText().toString());
        todoItem.setTodoDone(todoDone.isChecked());
        todoItem.setImportant(todoImportant.isChecked());
        // TODO time
        todoItem.setDate(calender.getTimeInMillis());
        returnIntent.putExtra("saveTodo", todoItem);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void deleteItem () {

        new AlertDialog.Builder(this)
                .setMessage("Are you sure to delete this Todo Item?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent returnIntent = new Intent(TotoListeDetailView.this, TodoListeOverview.class);
                        returnIntent.putExtra("deleteTodo", todoItem);
                        setResult(RESULT_DELETE, returnIntent);
                        finish();

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void addContact() {

        Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactIntent, 1 );
    }

    private void processSelectedContact (Uri uri) {
        Cursor cursor = getContentResolver().query(uri,null, null, null, null);
        cursor.moveToNext();
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

        Log.i("Contact Name: ", name);
        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);
        if (phoneCursor.getCount() > 0) {
            phoneCursor.moveToFirst();
            do {
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
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
            do {
                String email = emailCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));

                Log.i("email", email);

            } while (phoneCursor.moveToNext());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_navigation_contacts_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_contacts) {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            processSelectedContact(data.getData());
        }
    }
}

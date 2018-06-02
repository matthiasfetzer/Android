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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import matthiasfetzer.com.todoliste.model.TodoItem;


public class TodoListDetailView extends AppCompatActivity {

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

        // if no contact is set yet initialize Array List
        if (todoItem.getContacts() == null) {
            todoItem.setContacts(new ArrayList<String>());
        }


        dateSelector.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(TodoListDetailView.this,new DatePickerDialog.OnDateSetListener() {
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

                TimePickerDialog timePickerDialog = new  TimePickerDialog(TodoListDetailView.this, new TimePickerDialog.OnTimeSetListener () {

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
                                Intent todoListIntent = new Intent(TodoListDetailView.this, TodoListOverview.class);
                                startActivity(todoListIntent);
                                return true;
                            case R.id.action_check:
                                saveItem();
                                return true;
                            case R.id.action_delete:
                                deleteItem();
                        }
                        return false;
                    }
         });
    }

    private void saveItem () {
        Intent returnIntent = new Intent(this, TodoListOverview.class);
        todoItem.setName(todoName.getText().toString());
        todoItem.setDescription(todoDescription.getText().toString());
        todoItem.setTodoDone(todoDone.isChecked());
        todoItem.setImportant(todoImportant.isChecked());
        todoItem.setDate(calender.getTimeInMillis());
        returnIntent.putExtra("saveTodo", todoItem);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void deleteItem () {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setCancelable(true);
        // Add the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Intent returnIntent = new Intent(TodoListDetailView.this, TodoListOverview.class);
                returnIntent.putExtra("deleteTodo", todoItem);
                setResult(RESULT_DELETE, returnIntent);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", null);
        // Set other dialog properties
        builder.setMessage(R.string.delete_todo);
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_navigation_contact_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_contact_list) {
            // show new Activity with list of contacts
            Intent contactIntent = new Intent(this, ContactList.class);
            contactIntent.putExtra("todoItem",todoItem);
            startActivityForResult(contactIntent, 1);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                TodoItem tempTodo = (TodoItem) data.getSerializableExtra("todoItem");
                todoItem.setContacts(tempTodo.getContacts());
        }
    }
}
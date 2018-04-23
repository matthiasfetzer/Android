package matthiasfetzer.com.todoliste;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import matthiasfetzer.com.todoliste.model.ITodoItemCRUDOperationsAsync;
import matthiasfetzer.com.todoliste.model.TodoItemApplication;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ((TodoItemApplication)getApplication()).isWebServiceAvailable(new ITodoItemCRUDOperationsAsync.CallbackFunction<Boolean>() {
            @Override
            public void process(Boolean results) {

                if (results) {
                    // webservice available call Login activity
                    Intent callLoginActivity = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(callLoginActivity);


                } else {
                    Toast.makeText(StartActivity.this, "Webservice not available, using local Data!!", Toast.LENGTH_LONG).show();
                    Intent callTodoList = new Intent(StartActivity.this, TodoListeOverview.class);
                    startActivity(callTodoList);

                }
            }
        });

    }
}

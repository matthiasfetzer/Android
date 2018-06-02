package matthiasfetzer.com.todoliste;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import matthiasfetzer.com.todoliste.model.ITodoItemCRUDOperationsAsync;
import matthiasfetzer.com.todoliste.model.TodoItem;
import matthiasfetzer.com.todoliste.model.TodoItemApplication;


public class TodoListOverview extends AppCompatActivity {

    private ViewGroup todoListView;
    private ArrayAdapter todoItemArrayAdapter;
    private ITodoItemCRUDOperationsAsync todoItemCRUDOperations;
    private final static String TODO_ITEM = "todoItem";
    private List<TodoItem> sortList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_liste);

        todoListView = (ViewGroup) findViewById(R.id.todoList);
        todoItemCRUDOperations = ((TodoItemApplication)getApplication()).getCRUDOperationsImpl();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_overview);
        todoItemArrayAdapter = new ArrayAdapter<TodoItem>(this, R.layout.activity_toto_liste_singleview, sortList){

            @NonNull
            @Override
            public View getView(int position, @Nullable View todoView, @NonNull ViewGroup parent) {

                if (todoView == null) {
                    todoView = getLayoutInflater().inflate(R.layout.activity_toto_liste_singleview, null);
                }

                final TextView todoItemView = (TextView)todoView.findViewById(R.id.toto_liste_singleview_name);
                final TextView todoItemTime = (TextView)todoView.findViewById(R.id.toto_liste_singleview_time);
                final CheckBox todoItemDone = (CheckBox)todoView.findViewById(R.id.toto_liste_singleview_done);
                final Switch todoItemImportant = (Switch)todoView.findViewById(R.id.toto_liste_singleview_important);

                final TodoItem todoItem = getItem(position);
                todoItemView.setText(todoItem.getName());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy HH:mm");
                todoItemTime.setText(dateFormat.format(todoItem.getDate()));
                if (System.currentTimeMillis() - todoItem.getDate() > 0 && !todoItem.isTodoDone()) {
                    todoItemTime.setTextColor(Color.RED);
                }
                todoItemImportant.setOnCheckedChangeListener(null);
                todoItemDone.setOnCheckedChangeListener(null);

                todoItemDone.setChecked(todoItem.isTodoDone());
                todoItemImportant.setChecked(todoItem.isImportant());



                todoItemDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        todoItem.setTodoDone(b);
                        updateAndShowTODOItem(todoItem);
                    }
                });

                todoItemImportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        todoItem.setImportant(b);
                        updateAndShowTODOItem(todoItem);
                    }
                });

                todoItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent detailViewIntent = new Intent(TodoListOverview.this, TodoListDetailView.class);
                        detailViewIntent.putExtra(TODO_ITEM,todoItem);
                        startActivityForResult(detailViewIntent, 2);
                    }
                });

                return todoView;
            }
        };

        ((ListView)todoListView).setAdapter(todoItemArrayAdapter);
        todoItemArrayAdapter.setNotifyOnChange(true);

        readTodosAndFillList();






        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_add_item:
                                addNewTodoItem();
                                return true;

                            case R.id.action_misc:
                                return true;

                        }
                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_navigation_sort_todos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort_date_import) {
            sortTodosByImportance();
            sortTodosByDate();
            sortTodoByDone();
        }
        else if (item.getItemId() == R.id.action_sort_import_date) {
            sortTodosByDate();
            sortTodosByImportance();
            sortTodoByDone();
        }
        return true;
    }

    private void sortTodosByDate() {
        Collections.sort(sortList, new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem todoItem1, TodoItem todoItem2) {
                if (todoItem1.getDate() == todoItem2.getDate()){
                    return 0;
                }
                else if (todoItem1.getDate() > todoItem2.getDate()){
                    return 1;
                } else{
                    return -1;
                }
            }
        });
        todoItemArrayAdapter.notifyDataSetChanged();
    }

    private void sortTodosByImportance() {
        Collections.sort(sortList, new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem todoItem1, TodoItem todoItem2) {
                if (todoItem1.isImportant() && todoItem2.isImportant()
                        || !todoItem1.isImportant() && !todoItem2.isImportant()){
                    return 0;
                }
                else if (todoItem2.isImportant()){
                    return 1;
                } else return -1;
            }
        });
        todoItemArrayAdapter.notifyDataSetChanged();
    }

    private void sortTodoByDone() {
        Collections.sort(sortList, new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem todoItem1, TodoItem todoItem2) {
                if (todoItem1.isTodoDone() && todoItem2.isTodoDone() || !todoItem1.isTodoDone() && !todoItem2.isTodoDone() ){
                    return 0;
                }
                else if (todoItem1.isTodoDone()){
                    return 1;
                } else return -1;
            }
        });
        todoItemArrayAdapter.notifyDataSetChanged();
    }

    private void readTodosAndFillList() {
        todoItemCRUDOperations.readAllTodoItems(new ITodoItemCRUDOperationsAsync.CallbackFunction<List<TodoItem>>() {
            @Override
            public void process(List<TodoItem> results) {
                for (TodoItem todoItem : results) {
                    addTodoItemToListView(todoItem);
                }
                sortTodoByDone();
            }
        });
    }

    private void createAndShowTODOItem (final TodoItem todoItem) {

        todoItemCRUDOperations.createTodoItem(todoItem, new ITodoItemCRUDOperationsAsync.CallbackFunction<TodoItem>() {
            @Override
            public void process(TodoItem results) {
                addTodoItemToListView(todoItem);
            }
        });
    }

    private void updateAndShowTODOItem (final TodoItem todoItem) {

        todoItemCRUDOperations.updateTodoItem(todoItem.getId(), todoItem, new ITodoItemCRUDOperationsAsync.CallbackFunction<TodoItem>() {
            @Override
            public void process(TodoItem results) {
                TodoItem toDelete = findListItemForId(results.getId());
                int itemPosition = todoItemArrayAdapter.getPosition(toDelete);
                todoItemArrayAdapter.remove(toDelete);
                todoItemArrayAdapter.insert(todoItem, itemPosition);
            }
        });
    }

    private void deleteTODOItem (final TodoItem todoItem) {

        todoItemCRUDOperations.deleteTodoItem(todoItem.getId(), new ITodoItemCRUDOperationsAsync.CallbackFunction<Boolean>() {
            @Override
            public void process(Boolean deleted) {
                if (deleted) {
                    todoItemArrayAdapter.remove(findListItemForId(todoItem.getId()));
                }
            }
        });
    }

    private void addTodoItemToListView(TodoItem todoItem) {
        todoItemArrayAdapter.add(todoItem);
    }

    private void addNewTodoItem() {
        Intent addNewTodoItemIntent = new Intent(this, TodoListDetailView.class);
        startActivityForResult(addNewTodoItemIntent, 1);
    }

    private TodoItem findListItemForId (long id) {

        for (int i = 0; i < todoItemArrayAdapter.getCount(); i++) {
            TodoItem todoItem = (TodoItem) todoItemArrayAdapter.getItem(i);
            if ( todoItem.getId() == id) {
                return todoItem;
            }
        }

        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // create Todo Item
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            TodoItem todoItemTemp = (TodoItem)data.getSerializableExtra("saveTodo");
            createAndShowTODOItem(todoItemTemp);
        }
        else if (requestCode == 2){
            // edit Todo Item
            if (resultCode == Activity.RESULT_OK) {
                TodoItem todoItemTemp = (TodoItem) data.getSerializableExtra("saveTodo");
                updateAndShowTODOItem(todoItemTemp);
            }
            // delete Todo Item
            else if (resultCode == TodoListDetailView.RESULT_DELETE) {
                TodoItem todoItemTemp = (TodoItem) data.getSerializableExtra("deleteTodo");
                deleteTODOItem(todoItemTemp);
            }
        }
    }
}

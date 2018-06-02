package matthiasfetzer.com.todoliste.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class TodoItemApplication extends Application implements ITodoItemCRUDOperationsAsync{

    private static String logger = TodoItemApplication.class.getSimpleName();
    private ITodoItemCRUDOperations syncCRUDOperations;

    public ITodoItemCRUDOperationsAsync getCRUDOperationsImpl () {
        return this;
    }
    @Override
    public void onCreate() {
        Log.i(logger, "onCreate() called!");
        syncCRUDOperations = new SynchTodoItemCRUDOperationsImpl(this);
        super.onCreate();
    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public void createTodoItem(TodoItem todoItem, final CallbackFunction<TodoItem> callback) {

        new AsyncTask<TodoItem, Void, TodoItem>() {

            @Override
            protected TodoItem doInBackground(TodoItem... todoItems) {
                return syncCRUDOperations.createTodoItem(todoItems[0]);
            }

            @Override
            protected void onPostExecute(TodoItem todoItem) {
                callback.process(todoItem);
            }
        }.execute(todoItem);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void readAllTodoItems(final CallbackFunction<List<TodoItem>> callback) {

        new AsyncTask<Void, Void, List<TodoItem>>() {

            @Override
            protected List<TodoItem> doInBackground(Void... voids) {
                return syncCRUDOperations.readAllTodoItems();
            }

            @Override
            protected void onPostExecute(List<TodoItem> todoItems) {
                callback.process(todoItems);
            }
        }.execute();

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void readTodoItem(long id, final CallbackFunction<TodoItem> callback) {
        new AsyncTask<Long, Void, TodoItem>() {

            @Override
            protected TodoItem doInBackground(Long... longs) {
                return syncCRUDOperations.readTodoItem(longs[0]);
            }

            @Override
            protected void onPostExecute(TodoItem result) {
                callback.process(result);
            }
        }.execute(id);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void updateTodoItem(long id, final TodoItem todoItem, final CallbackFunction<TodoItem> callback) {

        new AsyncTask<Object, Void, TodoItem>() {

            @Override
            protected TodoItem doInBackground(Object... objects) {
                Long tempId = (Long)objects[0];
                TodoItem tempTodo = (TodoItem)objects[1];
                return syncCRUDOperations.updateTodoItem(tempId, tempTodo);
            }

            @Override
            protected void onPostExecute(TodoItem todoItem) {
                callback.process(todoItem);
            }
        }.execute(id, todoItem);

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void deleteTodoItem(long id, final CallbackFunction<Boolean> callback) {

        new AsyncTask<Long, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Long... longs) {
                return syncCRUDOperations.deleteTodoItem(longs[0]);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                callback.process(result);
            }
        }.execute(id);
    }

    @SuppressLint("StaticFieldLeak")
    public void authenticateUser (User user, final CallbackFunction<Boolean> callback) {

        new AsyncTask<User, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(User... users) {
                return ((SynchTodoItemCRUDOperationsImpl)syncCRUDOperations).authenticateUser(users[0]);
            }

            @Override
            protected void onPostExecute(Boolean isAuthenticated) {
                callback.process(isAuthenticated);
            }
        }.execute(user);
    }

    @SuppressLint("StaticFieldLeak")
    public void isWebServiceAvailable (final CallbackFunction<Boolean> callback) {

        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... voids) {

                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL("http://10.0.2.2:8080/").openConnection();
                    connection.setConnectTimeout(500);

                    if (connection.getResponseCode() == 200) {
                        return true;
                    } else {
                        return false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    connection.disconnect();
                }
            }
            @Override
            protected void onPostExecute(Boolean isAvailable) {
                if (syncCRUDOperations instanceof SynchTodoItemCRUDOperationsImpl) {
                    ((SynchTodoItemCRUDOperationsImpl) syncCRUDOperations).setRemoteAvailable(isAvailable);
                }
                callback.process(isAvailable);
            }
        }.execute();
    }
}

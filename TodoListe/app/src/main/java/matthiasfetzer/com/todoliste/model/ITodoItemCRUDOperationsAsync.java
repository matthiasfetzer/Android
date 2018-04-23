package matthiasfetzer.com.todoliste.model;

import java.util.List;

public interface ITodoItemCRUDOperationsAsync {

    public static interface CallbackFunction<T> {
        public void process (T results);
    }

    // Create
    public void createTodoItem(TodoItem todoItem, CallbackFunction<TodoItem> callback);

    // Read All
    public void readAllTodoItems(CallbackFunction<List<TodoItem>> callback);

    // Read one
    public void readTodoItem(long id, CallbackFunction<TodoItem> callback);

    // update
    public void updateTodoItem(long id, TodoItem todoItem, CallbackFunction<TodoItem> callback);

    // delete
    public void deleteTodoItem(long id, CallbackFunction<Boolean> callback);
}

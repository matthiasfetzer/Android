package matthiasfetzer.com.todoliste.model;

import java.util.List;

import matthiasfetzer.com.todoliste.model.TodoItem;

public interface ITodoItemCRUDOperations {

    // Create
    public TodoItem createTodoItem(TodoItem todoItem);

    // Read All
    public List<TodoItem> readAllTodoItems();

    // Read one
    public TodoItem readTodoItem (long id);

    // update
    public TodoItem updateTodoItem (long id, TodoItem todoItem);

    // delete
    public boolean deleteTodoItem (long id);

    // delete all items
    public boolean deleteAllTodoItems();
}

package matthiasfetzer.com.todoliste.model;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import matthiasfetzer.com.todoliste.model.ITodoItemCRUDOperations;
import matthiasfetzer.com.todoliste.model.TodoItem;

public class TodoItemCRUDOperationsImpl implements ITodoItemCRUDOperations {


    @Override
    public TodoItem createTodoItem(TodoItem todoItem) {
        return null;
    }

    @Override
    public List<TodoItem> readAllTodoItems() {
        return Arrays.asList(new TodoItem []{new TodoItem("Cleaning", "This is a decription of the cleaning", true, true, Calendar.getInstance().getTimeInMillis()),new TodoItem("Programming Todo App", "This is a decription of the Programming", false, false, Calendar.getInstance().getTimeInMillis())});
    }

    @Override
    public TodoItem readTodoItem(long id) {
        return null;
    }

    @Override
    public TodoItem updateTodoItem(long id, TodoItem todoItem) {
        return null;
    }

    @Override
    public boolean deleteTodoItem(long id) {
        return false;
    }

    @Override
    public boolean deleteAllTodoItems() { return false; }

}

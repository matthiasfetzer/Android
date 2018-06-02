package matthiasfetzer.com.todoliste.model;

import android.content.Context;

import java.util.List;

public class SynchTodoItemCRUDOperationsImpl implements ITodoItemCRUDOperations {

    private ITodoItemCRUDOperations remoteCRUD;
    private ITodoItemCRUDOperations localCRUD;
    private boolean remoteAvailable;

    public SynchTodoItemCRUDOperationsImpl (Context context) {
        this.localCRUD = new LocalTodoItemCRUDOperationsImpl(context);
        this.remoteCRUD = new RemoteTodoItemCRUDOperatoinsImpl();
    }

    @Override
    public TodoItem createTodoItem(TodoItem todoItem) {
        todoItem = localCRUD.createTodoItem(todoItem);
        if (remoteAvailable) {
            remoteCRUD.createTodoItem(todoItem);
        }
        return todoItem;
    }

    @Override
    public List<TodoItem> readAllTodoItems() {
        List<TodoItem> localTodoItems = localCRUD.readAllTodoItems();
        if (remoteAvailable) {
            if (localTodoItems.size() > 0) {
                // delete all web todos and transfer local todos
                deleteAllTodoItems();
                for (TodoItem todoItem: localTodoItems) {
                    remoteCRUD.createTodoItem(todoItem);
                }
                return localTodoItems;
            } else {
                // transfer web todos to local db and return
                List<TodoItem> webTodos = remoteCRUD.readAllTodoItems();
                for (TodoItem webTodo: webTodos) {
                    localCRUD.createTodoItem(webTodo);
                }
                return webTodos;
            }
        }
        return localTodoItems;
    }

    @Override
    public TodoItem readTodoItem(long id) {
        // no synch necessary load local master data
        return localCRUD.readTodoItem(id);
    }

    @Override
    public TodoItem updateTodoItem(long id, TodoItem todoItem) {
        todoItem = localCRUD.updateTodoItem(id, todoItem);
        if (remoteAvailable) {
            remoteCRUD.updateTodoItem(id, todoItem);
        }
        return todoItem;
    }

    @Override
    public boolean deleteTodoItem(long id) {
        boolean deleted = localCRUD.deleteTodoItem(id);
        if (deleted && remoteAvailable) {
            remoteCRUD.deleteTodoItem(id);
        }
        return deleted;
    }

    public boolean authenticateUser (User user) {
        if (remoteAvailable && remoteCRUD instanceof RemoteTodoItemCRUDOperatoinsImpl) {
           return ((RemoteTodoItemCRUDOperatoinsImpl)remoteCRUD).authenticateUser(user);
        }
        return false;
    }

    @Override
    public boolean deleteAllTodoItems() {
        return remoteCRUD.deleteAllTodoItems();
    }

    public boolean isRemoteAvailable() {
        return remoteAvailable;
    }

    public void setRemoteAvailable(boolean remoteAvailable) {
        this.remoteAvailable = remoteAvailable;
    }
}

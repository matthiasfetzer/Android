package matthiasfetzer.com.todoliste.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class LocalTodoItemCRUDOperationsImpl implements ITodoItemCRUDOperations {

    protected static String logger = LocalTodoItemCRUDOperationsImpl.class.getSimpleName();

    private SQLiteDatabase db;

    public LocalTodoItemCRUDOperationsImpl(Context context) {

        db = context.openOrCreateDatabase("mydb.sqlite", context.MODE_PRIVATE, null);
        if (db.getVersion() == 0) {
            db.setVersion(1);
            db.execSQL("CREATE TABLE TODOITEMS (ID INTEGER PRIMARY KEY, NAME TEXT, DESCRIPTION TEXT, DONE BOOLEAN, IMPORTANT BOOLEAN, DUEDATE LONG)");
        }
    }

    @Override
    public TodoItem createTodoItem(TodoItem todoItem) {

        ContentValues values = new ContentValues();
        values.put("NAME", todoItem.getName());
        values.put("DESCRIPTION", todoItem.getDescription());
        values.put("DONE", todoItem.isTodoDone());
        values.put("IMPORTANT", todoItem.isImportant());
        values.put("DUEDATE", todoItem.getDate());

        long id = db.insert("TODOITEMS", null, values);
        todoItem.setId(id);

        return todoItem;
    }

    @Override
    public List<TodoItem> readAllTodoItems() {

        List<TodoItem> todoItems = new ArrayList<TodoItem>();
        Cursor cursor = db.query("TODOITEMS", new String[]{"ID", "NAME", "DESCRIPTION", "DONE", "IMPORTANT", "DUEDATE"}, null, null,null, null, "id");
        if (cursor.getCount()  > 0) {
            cursor.moveToFirst();
            boolean next = false;
            do {


                long id = cursor.getLong(cursor.getColumnIndex("ID"));
                String name = cursor.getString(cursor.getColumnIndex("NAME"));
                String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
                boolean done = cursor.getInt(cursor.getColumnIndex("DONE")) == 1?true:false;
                boolean important = cursor.getInt(cursor.getColumnIndex("IMPORTANT"))== 1?true:false;
                long date = cursor.getLong(cursor.getColumnIndex("DUEDATE"));

                TodoItem todoItem = new TodoItem(name, description, done, important, date);
                todoItem.setId(id);
                todoItems.add(todoItem);
                next = cursor.moveToNext();
            } while (next);
        }

        return todoItems;
    }

    @Override
    public TodoItem readTodoItem(long id) {

        Cursor cursor = db.rawQuery("SELECT * FROM TOTOITEMS WHERE id = ?", new String[] { String.valueOf(id) });

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            long dbId = cursor.getLong(cursor.getColumnIndex("ID"));
            String name = cursor.getString(cursor.getColumnIndex("NAME"));
            String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
            boolean done = cursor.getInt(cursor.getColumnIndex("DONE")) == 1 ? true : false;
            boolean important = cursor.getInt(cursor.getColumnIndex("IMPORTANT")) == 1 ? true : false;
            long date = cursor.getLong(cursor.getColumnIndex("DUEDATE"));

            TodoItem todoItem = new TodoItem(name, description, done, important, date);
            todoItem.setId(dbId);

            return todoItem;
        }
        return null;
    }

    @Override
    public TodoItem updateTodoItem(long id, TodoItem todoItem) {


        ContentValues newValues = new ContentValues();
        newValues.put("NAME", todoItem.getName());
        newValues.put("DESCRIPTION", todoItem.getDescription());
        newValues.put("DONE", todoItem.isTodoDone());
        newValues.put("IMPORTANT", todoItem.isImportant());
        newValues.put("DUEDATE", todoItem.getDate());

        int updatedRows = db.update("TODOITEMS", newValues, "ID=?", new String[]{String.valueOf(id)});
        if (updatedRows > 0) {
            return todoItem;
        }
        return null;
    }

    @Override
    public boolean deleteTodoItem(long id) {

        int numOfRows = db.delete("TODOITEMS", "ID=?", new String[] {String.valueOf(id)});

        if (numOfRows > 0) {
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteAllTodoItems() {
        throw new RuntimeException("only on Web App implemented yet !!");
    }

}

package matthiasfetzer.com.todoliste.model;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class RemoteTodoItemCRUDOperatoinsImpl implements ITodoItemCRUDOperations {

    public interface ITodoItemCRUDWebAPI {


        @POST("/api/todos")
        public Call<TodoItem> createTodoItem(@Body TodoItem todoItem);

        @GET("/api/todos")
        public Call<List<TodoItem>> readAllTodoItems();

        @GET("/api/todos/{id}")
        public Call<TodoItem> readTodoItem(@Path("id") long id);

        @PUT("/api/todos/{id}")
        public Call<TodoItem> updateTodoItem(@Path("id") long id, @Body TodoItem todoItem);

        @PUT("/api/users/auth")
        public Call<Boolean> loginUser(@Body User user);

        @DELETE("/api/todos/{id}")
        public Call<Boolean> deleteTodoItem(@Path("id") long id);

        @DELETE("/api/todos/")
        public Call<Boolean> deleteAllTodoItems();

    }

    private ITodoItemCRUDWebAPI webAPI;

    public RemoteTodoItemCRUDOperatoinsImpl() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/").
                addConverterFactory(GsonConverterFactory.create()).build();
        this.webAPI = retrofit.create(ITodoItemCRUDWebAPI.class);
    }

    @Override
    public TodoItem createTodoItem(TodoItem todoItem) {
        try {
            TodoItem createdTodoItem = this.webAPI.createTodoItem(todoItem).execute().body();
            return createdTodoItem;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public List<TodoItem> readAllTodoItems() {
        try {
            List<TodoItem> createdTodoItems  = this.webAPI.readAllTodoItems().execute().body();
            return createdTodoItems;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public TodoItem readTodoItem(long id) {
        try {
            TodoItem createdTodoItem = this.webAPI.readTodoItem(id).execute().body();
            return createdTodoItem;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public TodoItem updateTodoItem(long id, TodoItem todoItem) {
        try {
            TodoItem updatedTodoItem =this.webAPI.updateTodoItem(id, todoItem).execute().body();
            return updatedTodoItem;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public boolean deleteTodoItem(long id) {
        try {
            return this.webAPI.deleteTodoItem(id).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public boolean deleteAllTodoItems () {
        try {
            return this.webAPI.deleteAllTodoItems().execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public boolean authenticateUser (User user) {
        try {
            return this.webAPI.loginUser(user).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}

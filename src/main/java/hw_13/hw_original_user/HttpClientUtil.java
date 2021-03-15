package hw_13.hw_original_user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hw_13.hw_original_user.posts_data.Post;
import hw_13.hw_original_user.user_data.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HttpClientUtil {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    public static final Gson GSON = new Gson();
    public static final String USERS_END_PONT = "/users";
    public static final String POSTS_END_PONT = "/posts";


    public static String createNewUser(String uriString, User user) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriString + USERS_END_PONT))
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(user)))
                .header("Content-type", "application/json")
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String updateUser(String uriString, int userId, User updatedUser) throws IOException, InterruptedException {
        String requestBody = GSON.toJson(updatedUser);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s%s/%d", uriString, USERS_END_PONT, userId)))
                .header("Content-type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static int deleteUser(String uriString, User user) throws IOException, InterruptedException {
        String requestBody = GSON.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s%s/%d", uriString, USERS_END_PONT, user.getId())))
                .header("Content-type", "application/json")
                .method("DELETE", HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public static List<User> getAllUsers(String uriString) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriString + USERS_END_PONT))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return GSON.fromJson(response.body(), new TypeToken<List<User>>() {
        }.getType());
    }

    public static User getUserById(String uriString, int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s%s/%d", uriString, USERS_END_PONT, id)))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return GSON.fromJson(response.body(), User.class);
    }

    public static User getUserByName(String uriString, String name) throws IOException, InterruptedException {
        List<User> allUsers = getAllUsers(uriString);
        return allUsers.stream().filter(user -> user.getName().equals(name)).findAny().orElse(new User());
    }

    public static String getAllCommentsForLastPostOfUser(String uriString, User user) throws IOException, InterruptedException {
        Post lastPost = getLastPostOfUser(uriString + USERS_END_PONT, user);

        String fileName = "user-" + user.getId() + "-post-" + lastPost.getId() + "-comments.json";

        HttpRequest requestForComments = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d/%s", uriString + POSTS_END_PONT, lastPost.getId(), "comments")))
                .GET()
                .build();
        HttpResponse<Path> responseComments = CLIENT.send(requestForComments, HttpResponse.BodyHandlers.ofFile(Paths.get(fileName)));

        return "comments written to file " + responseComments.body();


    }

    private static Post getLastPostOfUser(String uriString, User user) throws IOException, InterruptedException {
        HttpRequest requestForPosts = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d/%s", uriString, user.getId(), "posts")))
                .GET()
                .build();
        HttpResponse<String> responsePosts = CLIENT.send(requestForPosts, HttpResponse.BodyHandlers.ofString());
        List<Post> allUserPosts = GSON.fromJson(responsePosts.body(), new TypeToken<List<Post>>() {
        }.getType());
        return Collections.max(allUserPosts, Comparator.comparingInt(Post::getId));
    }

    public static List<Task> getListOfOpenTasksForUser(String uriString, User user) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s%s/%d/%s", uriString, USERS_END_PONT, user.getId(), "todos")))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> allTasks = GSON.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        return allTasks.stream().filter(task -> !task.isCompleted())
                .collect(Collectors.toList());
    }
}

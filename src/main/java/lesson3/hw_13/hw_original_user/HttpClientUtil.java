package lesson3.hw_13.hw_original_user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lesson3.hw_13.hw_original_user.posts_data.Post;
import lesson3.hw_13.hw_original_user.user_data.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HttpClientUtil {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    public static final Gson GSON = new Gson();

    public static String createNewUser(String uriString, User user) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriString))
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(user)))
                .header("Content-type", "application/json")
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String updateUser(String uriString, int userId, User updatedUser) throws IOException, InterruptedException {
        String requestBody = GSON.toJson(updatedUser);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d", uriString, userId)))
                .header("Content-type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static int deleteUser(String uriString, User user) throws IOException, InterruptedException {
        String requestBody = GSON.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d", uriString, user.getId())))
                .header("Content-type", "application/json")
                .method("DELETE", HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public static List<User> getAllUsers(String uriString) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriString))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return GSON.fromJson(response.body(), new TypeToken<List<User>>() {
        }.getType());
    }

    public static User getUserById(String uriString, int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d", uriString, id)))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return GSON.fromJson(response.body(), User.class);
    }

    public static User getUserByName(String uriString, String name) throws IOException, InterruptedException {
        List<User> allUsers = getAllUsers(uriString);
        List<User> filteredUsersByName = allUsers.stream().filter(user -> user.getName().equals(name)).collect(Collectors.toList());
        if (filteredUsersByName.size() != 0) {
            return filteredUsersByName.get(0);
        } else {
            return null;
        }
    }

    public static String getAllCommentsForLastPostOfUser(String usersUriString, String postsUriString, User user) throws IOException, InterruptedException {
        Post lastPost = getLastPostOfUser(usersUriString, user);

        String fileName = "user-" + user.getId() + "-post-" + lastPost.getId() + "-comments.json";

        HttpRequest requestForComments = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d/%s", postsUriString, lastPost.getId(), "comments")))
                .GET()
                .build();
        HttpResponse<Path> responseComments = CLIENT.send(requestForComments, HttpResponse.BodyHandlers.ofFile(Paths.get(fileName)));

        return "comments written to file " + responseComments.body();


    }

    private static Post getLastPostOfUser(String usersUriString, User user) throws IOException, InterruptedException {
        HttpRequest requestForPosts = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d/%s", usersUriString, user.getId(), "posts")))
                .GET()
                .build();
        HttpResponse<String> responsePosts = CLIENT.send(requestForPosts, HttpResponse.BodyHandlers.ofString());
        List<Post> allUserPosts = GSON.fromJson(responsePosts.body(), new TypeToken<List<Post>>() {
        }.getType());
        return allUserPosts.stream().max(Comparator.comparingInt(Post::getId)).get();
    }

    public static List<Task> getListOfOpenTasksForUser(String uriString, User user) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d/%s", uriString, user.getId(), "todos")))
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> allTasks = GSON.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        return allTasks.stream().filter(task -> !task.isCompleted())
                .collect(Collectors.toList());
    }
}

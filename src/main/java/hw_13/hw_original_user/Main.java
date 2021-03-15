package hw_13.hw_original_user;

import com.google.gson.Gson;
import hw_13.hw_original_user.user_data.Address;
import hw_13.hw_original_user.user_data.Company;
import hw_13.hw_original_user.user_data.Geo;
import hw_13.hw_original_user.user_data.User;

import java.io.IOException;
import java.util.List;

public class Main {
    public static final Gson GSON = new Gson();
    public static final String DEFAULT_URI = "https://jsonplaceholder.typicode.com";
    public static final int DEFAULT_USER_ID_TO_OPERATE = 1;
    public static final String DEFAULT_USER_NAME = "Ervin Howell";

    public static void main(String[] args) throws IOException, InterruptedException {

        //Task 1.1

        System.out.println("Testing task 1.1: creating new User");
        User userToCreate = createDefaultUser();
        System.out.println("userToCreate = " + userToCreate);
        User createdUser = GSON.fromJson(HttpClientUtil.createNewUser(DEFAULT_URI, userToCreate), User.class);
        System.out.println("createdUser = " + createdUser);

        printingDelimeterBetweenTasks();

        //Task 1.2

        System.out.println("Testing task 1.2: updating the User");
        User updatedUser = new User();

        updatedUser.setName(createdUser.getName());
        updatedUser.setUsername("NewUserName");
        updatedUser.setEmail(createdUser.getEmail());
        updatedUser.setAddress(createdUser.getAddress());
        updatedUser.setWebsite(createdUser.getWebsite());
        updatedUser.setPhone(createdUser.getPhone());
        updatedUser.setCompany(createdUser.getCompany());

        String s = HttpClientUtil.updateUser(DEFAULT_URI, DEFAULT_USER_ID_TO_OPERATE, updatedUser);
        User checkUpdatedUser = GSON.fromJson(s, User.class);
        System.out.println(checkUpdatedUser);

        printingDelimeterBetweenTasks();

        //Task 1.3

        System.out.println("Testing task 1.3: deleting the User");
        createdUser.setId(DEFAULT_USER_ID_TO_OPERATE);
        System.out.println("Status for delete operation: " +
                HttpClientUtil.deleteUser(DEFAULT_URI, createdUser));

        printingDelimeterBetweenTasks();

        //Task 1.4

        System.out.println("Testing task 1.4: getting all users");
        List<User> allUsers = HttpClientUtil.getAllUsers(DEFAULT_URI);
        allUsers.forEach(System.out::println);

        printingDelimeterBetweenTasks();

        //Task 1.5

        System.out.println("Testing task 1.5: getting User by Id = 1");
        System.out.println("userGottenByIdOne = " + HttpClientUtil.getUserById(DEFAULT_URI, DEFAULT_USER_ID_TO_OPERATE));

        printingDelimeterBetweenTasks();

        //Task 1.6

        System.out.println("Testing task 1.6: getting User by Name = Ervin Howell");
        System.out.println("userGottenByName = " + HttpClientUtil.getUserByName(DEFAULT_URI, DEFAULT_USER_NAME));

        printingDelimeterBetweenTasks();

        //Task 2

        System.out.println("Testing task 2: getting all comments for last Post of User with id = 1: ");
        System.out.println();
        System.out.println(HttpClientUtil.getAllCommentsForLastPostOfUser(DEFAULT_URI, createdUser));

        printingDelimeterBetweenTasks();

        //Task 3

        System.out.println("Testing task 3: getting all open tasks for User with id = 1: ");
        System.out.println();
        System.out.println("List of open tasks: ");
        System.out.println();

        List<Task> allOpenTasks = HttpClientUtil.getListOfOpenTasksForUser(DEFAULT_URI, createdUser);
        allOpenTasks.forEach(System.out::println);

    }

    private static void printingDelimeterBetweenTasks() {
        System.out.println();
        System.out.println("-----------------");
        System.out.println();
    }

    private static User createDefaultUser() {
        User user = new User();
        user.setId(DEFAULT_USER_ID_TO_OPERATE);
        user.setName("Test");
        user.setUsername("TEST");
        user.setEmail("test@test.com");
        user.setAddress(createDefaultAddress());
        user.setPhone("1-770-736-8031 x56442");
        user.setWebsite("hildegard.org");
        user.setCompany(createDefaultCompany());
        return user;
    }

    private static Company createDefaultCompany() {
        Company company = new Company();
        company.setName("Romaguera-Crona");
        company.setCatchPhrase("Multi-layered client-server neural-net");
        company.setBs("harness real-time e-matkets");
        return company;
    }

    private static Address createDefaultAddress() {
        Address address = new Address();
        address.setStreet("Kulas Light");
        address.setSuit("Apt. 556");
        address.setCity("Gwenborough");
        address.setZipcode("92998-3874");
        Geo geo = new Geo();
        geo.setLat("-37.3159");
        geo.setLng("81.1496");
        address.setGeo(geo);
        return address;
    }
}

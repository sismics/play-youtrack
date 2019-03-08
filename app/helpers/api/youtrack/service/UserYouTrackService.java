package helpers.api.youtrack.service;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sismics.sapparot.exception.ValidationException;
import helpers.api.youtrack.YouTrackClient;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author jtremeaux
 */
public class UserYouTrackService {
    public YouTrackClient youTrackClient;

    public static final String APPLICATION_JSON = "application/json";

    public UserYouTrackService(YouTrackClient youTrackClient) {
        this.youTrackClient = youTrackClient;
    }

    /**
     * Get the user.
     *
     * @param login The user Login
     * @return The user ID, or null
     */
    public String getUserByLogin(String login) {
        Request request = new Request.Builder()
                .url(youTrackClient.getHubUrl("/users/?fields=id&query=login:+{" + login + "}"))
                .header("Accept", APPLICATION_JSON)
                .header("Authorization", youTrackClient.getAuthorizationToken())
                .get()
                .build();
        return youTrackClient.execute(request,
                (response) -> {
                    JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();
                    if (!json.has("users")) {
                        return null;
                    }
                    JsonArray users = json.getAsJsonArray("users");
                    if (users.size() == 0) {
                        return null;
                    }
                    if (users.size() > 1) {
                        throw new RuntimeException("More than one user returned");
                    }
                    return users.get(0).getAsJsonObject().get("id").getAsString();
                },
                null);
    }

    /**
     * Create a new user.
     *
     * @param login The login
     * @param email The user email
     * @param fullName The full name
     * @param password The password
     * @return The user ID
     */
    public String createUser(String login, String email, String fullName, String password) {
        JsonObject formBody = new JsonObject();
        JsonArray details = new JsonArray();
        JsonObject details0 = new JsonObject();
        details0.addProperty("type", "EmailuserdetailsJSON");
        JsonObject emailJson = new JsonObject();
        emailJson.addProperty("type", "EmailJSON");
        emailJson.addProperty("verified", "true");
        emailJson.addProperty("email", email);
        details0.add("email", emailJson);
        JsonObject passwordJson = new JsonObject();
        passwordJson.addProperty("type", "PlainpasswordJSON");
        passwordJson.addProperty("value", password);
        details0.add("password", passwordJson);
        details0.addProperty("passwordChangeRequired", "false");
        details.add(details0);
        formBody.add("details", details);
        formBody.addProperty("login", login);
        formBody.addProperty("name", fullName);
        Request request = new Request.Builder()
                .url(youTrackClient.getHubUrl("/users?fields=id"))
                .header("Content-Type", APPLICATION_JSON)
                .header("Accept", APPLICATION_JSON)
                .header("Authorization", youTrackClient.getAuthorizationToken())
                .post(RequestBody.create(MediaType.parse(APPLICATION_JSON), new GsonBuilder().create().toJson(formBody)))
                .build();
        return youTrackClient.execute(request,
                (response) -> {
                    JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();
                    return json.get("id").getAsString();
                },
                (response) -> {
                    String responseBody = response.body().string();
                    if (responseBody.contains("already exists.")) {
                        throw new ValidationException("user_youTrack_create_username_error");
                    }
                    throw new RuntimeException("Error creating user: " + login + ", response was: " + responseBody);
                });
    }

    /**
     * Delete a user.
     *
     * @param id The ID of the user to delete
     * @param successorId The ID of the user to merge to
     */
    public void deleteUser(String id, String successorId) {
        Request request = new Request.Builder()
                .url(youTrackClient.getHubUrl("/users/" + id + "?successor=" + successorId))
                .header("Accept", APPLICATION_JSON)
                .header("Authorization", youTrackClient.getAuthorizationToken())
                .delete()
                .build();
        youTrackClient.execute(request,
                (response) -> null,
                (response) -> {
                    throw new RuntimeException("Error deleting user: " + id + ", response was: " + response.body().string());
                });
    }
}

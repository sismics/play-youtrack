package helpers.api.youtrack;

import com.sismics.sapparot.function.CheckedConsumer;
import com.sismics.sapparot.function.CheckedFunction;
import com.sismics.sapparot.okhttp.OkHttpHelper;
import helpers.api.youtrack.service.UserYouTrackService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import play.Play;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;

/**
 * @author jtremeaux
 */
public class YouTrackClient {
    private OkHttpClient client;

    private static YouTrackClient youTrackClient;

    private UserYouTrackService userService;

    public static YouTrackClient get() {
        if (youTrackClient == null) {
            youTrackClient = new YouTrackClient();
        }
        return youTrackClient;
    }

    public YouTrackClient() {
        client = createClient();
        if (isMock()) {
            userService = mock(UserYouTrackService.class);
        } else {
            userService = new UserYouTrackService(this);
        }
    }

    private boolean isMock() {
        return Boolean.parseBoolean(Play.configuration.getProperty("youTrack.mock", "false"));
    }

    private static OkHttpClient createClient() {
        return new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public String getYouTrackApiUrl() {
        return Play.configuration.getProperty("youTrack.url") + "/rest";
    }

    public String getYouTrackHubUrl() {
        return Play.configuration.getProperty("youTrack.url") + "/hub/api/rest";
    }

    public String getYouTrackToken() {
        return Play.configuration.getProperty("youTrack.token");
    }

    public String getAuthorizationToken() {
        return "Bearer " + getYouTrackToken();
    }

    public String getUrl(String url) {
        return getYouTrackApiUrl() + url;
    }

    public String getHubUrl(String url) {
        return getYouTrackHubUrl() + url;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public UserYouTrackService getUserService() {
        return userService;
    }

    public <T> T execute(Request request, CheckedFunction<Response, T> onSuccess, CheckedConsumer<Response> onFailure) {
        return OkHttpHelper.execute(getClient(), request, onSuccess, onFailure);
    }
}

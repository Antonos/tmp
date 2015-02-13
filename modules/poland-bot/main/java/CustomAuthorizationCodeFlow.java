import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

/**
 * Created by anton on 28.01.2015.
 */
public class CustomAuthorizationCodeFlow extends GoogleAuthorizationCodeFlow {
    protected CustomAuthorizationCodeFlow(Builder builder) {
        super(builder);
    }
}

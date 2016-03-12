package libs;

/**
 * Created by Mahdi on 2/16/2016.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameValidator{

    private Pattern pattern;
    private Matcher matcher;

    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{5,15}$";

    public UsernameValidator(){
        pattern = Pattern.compile(USERNAME_PATTERN);
    }

    /**
     * Validate username with regular expression
     * @param username username for validation
     * @return true valid username, false invalid username
     */
    public boolean validate(final String username){

        matcher = pattern.matcher(username);
        return matcher.matches();

    }
}

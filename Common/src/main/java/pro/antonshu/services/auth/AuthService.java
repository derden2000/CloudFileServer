package pro.antonshu.services.auth;

public interface AuthService {
    /*
     *
     * This class needs for user authorization. There is 2 methods: authorization and registration.
     *
     */

    boolean authUser(String login, String password);

    boolean regNewUser(String login, String password);
}

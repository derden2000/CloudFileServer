package pro.antonshu.services.auth.impl;

import pro.antonshu.services.auth.AuthService;

import java.util.HashMap;
import java.util.Map;

public class AuthServiceImpl implements AuthService {

    private static Map<String, String> users = new HashMap<>();

    public AuthServiceImpl() {
        users.put("ivan", "123");
        users.put("petr", "345");
        users.put("123", "123");
    }

    @Override
    public boolean authUser(String login, String password) {
        String pwd = users.get(login);
        return pwd != null && pwd.equals(password);
    }

    @Override
    public boolean regNewUser(String login, String password) {
        users.put(login, password);
        return true;
    }
}

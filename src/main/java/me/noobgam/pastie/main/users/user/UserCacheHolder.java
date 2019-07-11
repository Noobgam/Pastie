package me.noobgam.pastie.main.users.user;

import me.noobgam.pastie.core.AbstractCache;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

public class UserCacheHolder extends AbstractCache<Map<String, User>> {
    private final UserDao userDao;

    public UserCacheHolder(UserDao userDao) {
        this.userDao = userDao;
    }

    protected User getUserByHandle(String handle) {
        return getCached().get(handle);
    }

    @Override
    protected Map<String, User> getCurrentValue() {
        return userDao.findAll()
                .join()
                .stream()
                .collect(Collectors.toMap(
                        User::getUsername,
                        user -> user
                ));
    }

    @Override
    protected Duration defaultDelay() {
        return Duration.ofSeconds(5);
    }
}

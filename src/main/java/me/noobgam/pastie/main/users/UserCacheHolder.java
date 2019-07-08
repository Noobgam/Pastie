package me.noobgam.pastie.main.users;

import me.noobgam.pastie.core.AbstractCache;

import java.time.Duration;
import java.util.Set;

public class UserCacheHolder extends AbstractCache<Set<String>> {
    private final UserDao userDao;

    public UserCacheHolder(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    protected Set<String> getCurrentValue() {
        return userDao.findAllHandles().join();
    }

    @Override
    protected Duration defaultDelay() {
        return Duration.ofSeconds(5);
    }
}

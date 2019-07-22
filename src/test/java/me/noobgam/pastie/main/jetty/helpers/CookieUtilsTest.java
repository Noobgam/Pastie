package me.noobgam.pastie.main.jetty.helpers;

import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.Cookie;

import static me.noobgam.pastie.main.jetty.helpers.CookieUtils.authCookieIsGood;
import static me.noobgam.pastie.main.jetty.helpers.CookieUtils.createAuthCookie;

public class CookieUtilsTest {
    @Test
    public void authCookieTest() {
        Cookie cookie = createAuthCookie();
        Assert.assertTrue(authCookieIsGood(cookie));

        cookie.setHttpOnly(false);
        Assert.assertTrue(authCookieIsGood(cookie));
        cookie.setHttpOnly(true);
        Assert.assertTrue(authCookieIsGood(cookie));

        cookie.setValue(cookie.getValue() + "X");
        Assert.assertFalse(authCookieIsGood(cookie));
        cookie.setValue(cookie.getValue().substring(0, cookie.getValue().length() - 1));
        Assert.assertTrue(authCookieIsGood(cookie));
    }
}

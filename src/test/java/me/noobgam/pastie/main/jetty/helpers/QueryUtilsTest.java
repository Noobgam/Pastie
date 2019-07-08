package me.noobgam.pastie.main.jetty.helpers;

import me.noobgam.pastie.utils.Cu;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class QueryUtilsTest {
    @Test
    public void splitToUrlParamsWorks() {
        Map<String, String> res;
        res = QueryUtils.splitToUrlParams("id=1&id2=2");
        Assert.assertEquals(Cu.map("id", "1", "id2", "2"), res);

        res = QueryUtils.splitToUrlParams("id=&&id2=44");
        Assert.assertEquals(Cu.map("id2", "44"), res);

        res = QueryUtils.splitToUrlParams("&&=&=&=&&=&=&");
        Assert.assertEquals(Cu.map(), res);

        res = QueryUtils.splitToUrlParams("id=1&id=2");
        Assert.assertEquals(1, res.size());
        Assert.assertTrue(Cu.set("1", "2").containsAll(res.values()));
    }
}

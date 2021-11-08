package com.bluecc.hubs;

import com.bluecc.hubs.common.Helper;
import org.junit.jupiter.api.Test;

public class TestCommon {
    @Test
    void testGson(){
        System.out.println(Helper.GSON.toJson("hello world"));
    }
}

package com.wjy;

import com.wjy.utils.AmapUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestAmap {
    @Value("${wjy.shop.address}")
    private String shopAddress;

    @Test
    void testProperties() {
        System.out.println(AmapUtil.getPathDistance(116.434307, 39.90909, 116.434446, 39.90816));
    }
}

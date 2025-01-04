package com.wjy.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AmapUtil {
    /**
     * Key
     */
    private static String KEY = "2ed49922396cc5009f261720292abe1d";

    public static String GD_URL = "https://restapi.amap.com/v3/geocode/geo";

    public static String DIS_URL = "https://restapi.amap.com/v4/direction/bicycling";

    /**
     * 成功标识
     */
    private static String SUCCESS_FLAG = "1";

    /**
     * 根据地址获取对应的经纬度信息
     *
     * @param address
     * @return
     */
    public static String getLonAndLatByAddress(String address) {
        String location = "";
        Map map = new HashMap();
        map.put("address", address);
        map.put("key", KEY);
        // 高德接口返回的是 JSON 格式的字符串
        String queryResult = HttpClientUtil.doGet(GD_URL, map);
        JSONObject obj = JSONObject.parseObject(queryResult);

        // 检查状态码
        if (SUCCESS_FLAG.equals(String.valueOf(obj.get("status")))) {
            // 获取 geocodes 数组
            JSONArray geocodesArray = obj.getJSONArray("geocodes");

            // 检查 geocodes 是否为空
            if (geocodesArray != null && !geocodesArray.isEmpty()) {
                // 获取第一个元素
                JSONObject jobJSON = geocodesArray.getJSONObject(0);
                location = jobJSON.getString("location"); // 获取位置字符串
            } else {
                throw new RuntimeException("未找到与地址匹配的经纬度信息。");
            }
        } else {
            throw new RuntimeException("地址转换经纬度失败，错误码：" + obj.get("infocode"));
        }

        return location;
    }

    public static String getPathDistance(Double origin_longitude, Double origin_latitude, Double destination_longitude, Double destination_latitude) {
        Map map = new HashMap();
        map.put("origin", String.valueOf(origin_longitude) + "," + String.valueOf(origin_latitude));
        map.put("destination", String.valueOf(destination_longitude) + "," + String.valueOf(destination_latitude));
        map.put("key", KEY);
        // 解析 JSON 响应
        String queryResult = HttpClientUtil.doGet(DIS_URL, map);
        JSONObject obj = JSONObject.parseObject(queryResult);
        log.info(queryResult);
        if (obj.get("errcode").equals(0)) {
            JSONObject data = (JSONObject) obj.get("data");
            JSONArray paths = data.getJSONArray("paths");
            JSONObject path = (JSONObject) paths.get(0);
            log.info(String.valueOf(path));
            return String.valueOf(path.get("distance")); // 米
        } else {
            throw new RuntimeException("计算距离失败，错误码：" + obj.get("errcode"));
        }
    }

}

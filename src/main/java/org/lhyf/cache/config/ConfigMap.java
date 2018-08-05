package org.lhyf.cache.config;

import java.util.HashMap;
import java.util.Map;

/****
 * @author YF
 * @date 2018-08-05 16:28
 * @desc ConfigMap
 *
 **/
public class ConfigMap {
    private Map<String,Object> map = new HashMap<>();

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}

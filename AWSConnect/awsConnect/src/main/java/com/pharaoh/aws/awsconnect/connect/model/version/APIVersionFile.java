package com.pharaoh.aws.awsconnect.connect.model.version;

import java.util.HashMap;
import java.util.Map;

public class APIVersionFile {
    private int code;
    private Map<String, AppTypeMap> flavor;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map<String, AppTypeMap> getFlavor() {
        return flavor;
    }

    public void setFlavor(Map<String, AppTypeMap> flavor) {
        this.flavor = flavor;
    }

    public DevicePlatformBean getTarget(String flavor, String appType, String betType, String platform) {
        try {
            return getFlavor().get(flavor).get(appType).get(betType).get(platform);
        } catch (Exception e) {
            return null;
        }
    }

    public static class AppTypeMap extends HashMap<String, BetTypeMap> {}

    public static class BetTypeMap extends HashMap<String, DevicePlatformMap> {}

    public static class DevicePlatformMap extends HashMap<String, DevicePlatformBean> {}

    public static class DevicePlatformBean {
        private float debug;
        private float release;
        private Map<String, String> update;

        public float getDebug() {
            return debug;
        }

        public void setDebug(float debug) {
            this.debug = debug;
        }

        public float getRelease() {
            return release;
        }

        public void setRelease(float release) {
            this.release = release;
        }

        public Map<String, String> getUpdate() {
            return update;
        }

        public void setUpdate(Map<String, String> update) {
            this.update = update;
        }
    }
}

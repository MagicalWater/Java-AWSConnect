package com.pharaoh.aws.awsconnect.connect.model.version;

import java.util.List;
import java.util.Map;

/**
 * Created by magicalwater on 2018/6/4.
 */

public class APIFlavorFile {

    private int code;
    private List<String> download;
    private Map<String, Map<String, DataBean>> flavor;
    private ConfigBean config;

    public ConfigBean getConfig() {
        return config;
    }

    public void setConfig(ConfigBean config) {
        this.config = config;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<String> getDownload() {
        return download;
    }

    public void setDownload(List<String> download) {
        this.download = download;
    }

    public Map<String, Map<String, DataBean>> getFlavor() {
        return flavor;
    }

    public void setFlavor(Map<String, Map<String, DataBean>> flavor) {
        this.flavor = flavor;
    }

    public static class ConfigBean {
        private String resetPWD;

        public String getResetPWD() {
            return resetPWD;
        }

        public void setResetPWD(String resetPWD) {
            this.resetPWD = resetPWD;
        }
    }

    public static class DataBean{
        private List<String> x_branch_source;
        private List<String> domains;
        private MaintainBean maintain;

        public MaintainBean getMaintain() {
            return maintain;
        }

        public void setMaintain(MaintainBean maintain) {
            this.maintain = maintain;
        }

        public List<String> getX_branch_source() {
            return x_branch_source;
        }

        public void setX_branch_source(List<String> x_branch_source) {
            this.x_branch_source = x_branch_source;
        }

        public List<String> getDomains() {
            return domains;
        }

        public void setDomains(List<String> domains) {
            this.domains = domains;
        }

        public static class MaintainBean{
            private boolean is_maintain;
            private String show_time_prompt;

            public boolean isIs_maintain() {
                return is_maintain;
            }

            public void setIs_maintain(boolean is_maintain) {
                this.is_maintain = is_maintain;
            }

            public String getShow_time_prompt() {
                return show_time_prompt;
            }

            public void setShow_time_prompt(String show_time_prompt) {
                this.show_time_prompt = show_time_prompt;
            }
        }
    }

}

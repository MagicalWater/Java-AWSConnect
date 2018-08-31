package com.pharaoh.aws.awsconnect.connect.model.s3Config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by magicalwater on 2018/6/5.
 */

public class APIS3Config {
    public Map<String,APIBetType> betTypeMap;

    public static class APIBetType extends HashMap<String, APIFlavorMap> {}

    public static class APIFlavorMap {
        public String flavor;
        public String ipa_from;
        public String ipa_to;
    }
}

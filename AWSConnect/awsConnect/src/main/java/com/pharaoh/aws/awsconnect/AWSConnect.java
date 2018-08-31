package com.pharaoh.aws.awsconnect;

import com.amazonaws.regions.Regions;
import com.pharaoh.aws.awsconnect.connect.AmazonS3Manager;
import com.pharaoh.aws.awsconnect.manager.APPVersionController;
import com.pharaoh.aws.awsconnect.manager.S3Configure;

import java.io.IOException;

/**
 * Created by magicalwater on 2018/5/31.
 */

public class AWSConnect {

    private static String mAccessKey = "放入access key";
    private static String mSecretKey = "放入secret key";
    private static String mBucketName = "設置訪問的 bucket name";
    private static Regions mRegion = Regions.AP_NORTHEAST_1; //設置區域

    private static APPVersionController mVersionController;

    public static void main(String[] args) throws IOException {
        initS3Setting();
        initVersionController();

        new S3Configure();
    }

    private static void initS3Setting() {
        AmazonS3Manager.Companion.setCrediential(mAccessKey, mSecretKey, mRegion);
        AmazonS3Manager.Companion.setBucket(mBucketName);
    }

    private static void initVersionController() {
        mVersionController = new APPVersionController();
    }

}

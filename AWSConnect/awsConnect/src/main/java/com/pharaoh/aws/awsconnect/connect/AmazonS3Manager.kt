package com.pharaoh.aws.awsconnect.connect

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import java.io.File

/**
 * Created by magicalwater on 2018/6/5.
 * doAsync 會造成 jar 無法停止, new Thread 則是不知道如何轉回主線成, 因此改成單線程
 */

class AmazonS3Manager {

    companion object {

        private var mAccessKey = ""
        private var mSecretKey = ""
        private var mRegion: Regions = Regions.DEFAULT_REGION
        private var mBucketName = ""

        private var mS3Client: AmazonS3? = null

        fun setCrediential(access: String, secret: String, region: Regions) {
            mAccessKey = access
            mSecretKey = secret
            mRegion = region

            initClient()
        }

        fun setBucket(name: String) {
            mBucketName = name
        }

        fun downloadData(fullPath: String): ByteArray? {
            val s3Object = mS3Client?.getObject(mBucketName, fullPath)
            val s3Stream = s3Object?.objectContent
            return if (s3Stream != null) {
                var buffer = ByteArray(s3Stream.available())
                s3Stream.read(buffer)
                s3Stream.close()
                buffer
            } else {
                null
            }
        }

        fun downloadText(fullPath: String): String? {
            val s3ObjectText = mS3Client?.getObjectAsString(mBucketName, fullPath)
            return s3ObjectText
        }

        fun upload(name: String, content: String): Boolean {
            val result = mS3Client?.putObject(mBucketName, name, content)
            return result != null && !result.contentMd5.isNullOrEmpty()
        }

        fun upload(name: String, file: File): Boolean {
            //先檢查file是否有檔案存在
            if (!file.exists()) {
                return false
            }
            val result = mS3Client?.putObject(mBucketName, name, file)
            return result != null && !result.contentMd5.isNullOrEmpty()
        }

        private fun initClient() {
            val s3Crediential = BasicAWSCredentials(mAccessKey, mSecretKey)
            val s3Provider = AWSStaticCredentialsProvider(s3Crediential)
            val s3builder = AmazonS3ClientBuilder.standard()
                    .withCredentials(s3Provider)
                    .withRegion(mRegion)

            mS3Client = s3builder.build()
        }

    }
}
package solutions.aon.aws.s3;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import solutions.aon.aws.AWS;
import solutions.aon.aws.exceptions.AonAwsErrorMessage;
import solutions.aon.aws.exceptions.AonAwsS3Exception;

public class S3 {
	
	private S3() {
	
	}
	
	public static final String DEFAULT_BUCKET = "aon-attach";
	public static final String AUTH_ATTACH_BUCKET = "aon-auth-attach";
	
	private static AmazonS3 connect() {
		return AmazonS3ClientBuilder.standard()
				.withCredentials(AWS.getProvider())
				.withRegion("eu-west-1")
				.build();
	}
	
	public static String upload(File file) {
		return upload(DEFAULT_BUCKET, file);
	}
	
	public static String upload(String bucket, File file) {
		String key = UUID.randomUUID().toString().replace("-", "");
		return upload(bucket, key, file);
	}
	
	public static String upload(String bucket, String key, File file) {
		AmazonS3 s3 = connect();
		if(!s3.doesBucketExistV2(bucket)) s3.createBucket(bucket);
		s3.putObject(new PutObjectRequest(bucket, key, file));
		return key;
	}

	public static String getPresignedURL(String key, Date expireDate) {
		return getPresignedURL(DEFAULT_BUCKET, key, expireDate);
	}
	
	public static String getPresignedURL(String bucket, String key, Date expireDate) {
		AmazonS3 s3 = connect();
		
		GeneratePresignedUrlRequest req = new  GeneratePresignedUrlRequest(bucket, key)
				.withExpiration(expireDate)
				.withMethod(s3.doesObjectExist(bucket, key) ?  HttpMethod.GET : HttpMethod.PUT);
		return s3.generatePresignedUrl(req).toString();
	}
	
	public static byte[] download(String key) throws IOException {
		return download(DEFAULT_BUCKET, key);
	}
	
	public static byte[] download(String bucket, String key) throws IOException {
		AmazonS3 s3 = connect();
		if(!s3.doesBucketExistV2(bucket)) 
			throw new AonAwsS3Exception(AonAwsErrorMessage.S3_BUCKET_NOT_EXIST.getMessage());
		S3Object object = s3.getObject(new GetObjectRequest(bucket, key));
		return object.getObjectContent().readAllBytes();
	}
	
	public static void delete(String key) {
		delete(DEFAULT_BUCKET, key);
	}
	
	public static void delete(String bucket, String key) {
		AmazonS3 s3 = connect();
		if(!s3.doesBucketExistV2(bucket)) 
			throw new AonAwsS3Exception(AonAwsErrorMessage.S3_BUCKET_NOT_EXIST.getMessage());
        s3.deleteObject(bucket, key);
	}
}

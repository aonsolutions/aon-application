package solutions.aon.aws.s3;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3rDoc{

	private static final String SCALEWAY_REGION = "fr-par";
	
	private S3rDoc() {
	}
	
	private static S3Client getClient(String bucket) {
		try {
			S3Client client = S3Client.builder()
			.endpointOverride(URI.create("https://s3.fr-par.scw.cloud"))
			.credentialsProvider(StaticCredentialsProvider.create(
	                AwsBasicCredentials.create("SCWR9W8EA2KZNBXF4SP9", "fef9cd73-0431-4eee-97c9-01748ea3ec6b")))
			.region(Region.of(SCALEWAY_REGION))
			.build();
			if(!existBucket(client, bucket)) {				
				createBucket(client, bucket);
			}
			return client;
		} catch (Exception e) {
			throw e;
		}
	}
	
	private static boolean existBucket(S3Client client, String bucket) {
		HeadBucketRequest request = HeadBucketRequest.builder().bucket(bucket).build();
		try {
			client.headBucket(request);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private static String createBucket(S3Client client, String bucket) {
		CreateBucketRequest request = CreateBucketRequest.builder().bucket(bucket).build();
		try {			
			client.createBucket(request);
		} catch (Exception e) {
			throw e;
		}
	    return bucket;
	}
	
	public static boolean existObject(String key, String bucket) {
		try {
			S3Client client = getClient(bucket);
			HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucket).key(key).build();
			client.headObject(request);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static String uploadObject(File file, String bucket) {
		try {
			S3Client client = getClient(bucket);
			String key = UUID.randomUUID().toString().replace("-", "");
			PutObjectRequest request = PutObjectRequest.builder().bucket(bucket).key(key).build();
			client.putObject(request, RequestBody.fromFile(file));
			return key;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static String uploadObject(byte[] bytes, String bucket, String domain) {
		try {
			S3Client client = getClient(bucket);
			Date date = new Date();
			String key = domain + "/" + UUID.randomUUID().toString().replace("-", "") + date.getTime();
			PutObjectRequest request = PutObjectRequest.builder().bucket(bucket).key(key).build();
			client.putObject(request, RequestBody.fromBytes(bytes));
			return key;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static void deleteObject(String key, String bucket) {
		try {
			S3Client client = getClient(bucket);
			DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucket).key(key).build();
			client.deleteObject(request);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static byte[] download(String key, String bucket) throws Exception {
		try {
			S3Client client = getClient(bucket);
			GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(key).build();
			return client.getObject(request).readAllBytes();
		} catch (Exception e) {
			throw e;
		}
	}
	
}

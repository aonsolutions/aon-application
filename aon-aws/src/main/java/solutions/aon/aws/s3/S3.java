package solutions.aon.aws.s3;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

public class S3 {
	
	private static Map<String, List<String>> AON_TABLE_BUCKETS_MAP;
	public static final String AON_TABLE = "AON_TABLE";
	
	private S3() {
		
	}
	
	private static S3Client getClient() {
		return S3Client.create();
	}
	
	// ----- BUCKET OPTIONS
	
	public static boolean existBucket(String bucket) {
		try(S3Client client = getClient()) {
			return existBucket(client, bucket);
		}
	}
	
	public static boolean existBucket(S3Client client, String bucket) {
		HeadBucketRequest request = HeadBucketRequest.builder().bucket(bucket).build();
		try {
			client.headBucket(request);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	// ----- GET BUCKET

	/**
	 * Get Aon Table Bucket, if not exist create.
	 * @param aonTable
	 * @return
	 */
	public static String getAonTableBucket(String aonTable) {
		try(S3Client client = getClient()) {
			List<String> list = getBucketsNames(client, aonTable);
			return list.isEmpty() 
				? createBucket(client, aonTable, aonTable)
				: list.getFirst();
		}
	}
	
	private static Optional<String> getBucketName(String aonTable, String key) {
		try (S3Client client = getClient()){
			return getBucketName(client,  aonTable, key);
		}
	}
	
	private static Optional<String> getBucketName(S3Client client, String aonTable, String key ) {
	    return getBucketsNames(client, aonTable).stream().filter( bucketName ->
	    	existObject(client, bucketName, key)).findFirst();
	}
	
	private static List<String> getBucketsNames(S3Client client, String aonTable ) {
		if ( AON_TABLE_BUCKETS_MAP == null || !AON_TABLE_BUCKETS_MAP.containsKey(aonTable))
			AON_TABLE_BUCKETS_MAP = getBucketsTagMap(client, AON_TABLE);
		
		return AON_TABLE_BUCKETS_MAP.getOrDefault(aonTable,Collections.emptyList());
	}
	
	private static Map<String, List<String>> getBucketsTagMap(S3Client client, String tag) {
		return client.listBuckets().buckets().stream()
		.map (bucket -> new String [] {getBucketTag(client, bucket.name(), tag), bucket.name()} )
		.filter(arr -> arr[0] != null )
		.map(arr -> Map.entry(arr[0],Collections.singletonList(arr[1])))
		.collect(Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue, (l1,l2) -> Stream.concat(l1.stream(), l2.stream()).toList() ));
	}

	private static String getBucketTag( S3Client client, String bucketName, String tag) {
		try {
			GetBucketTaggingResponse response = client.getBucketTagging(GetBucketTaggingRequest.builder().bucket(bucketName).build());
			return response.tagSet().stream().filter(t -> t.key().equals(tag)).map(Tag::value).findFirst().orElse(null);
		} catch (Exception e) {
			return null;
		}
	}
	
	// ----- CREATE BUCKET
	
	public static String createBucket(String bucket, String aonTable) {
		try(S3Client client = getClient()) {
			return createBucket(client, bucket, aonTable);
		}
	}
	
	public static String createBucket(S3Client client, String bucket, String aonTable) {
		createBucket(bucket);
		
		List<Tag> tags = new LinkedList<>();
		tags.add(Tag.builder().key(AON_TABLE).value(aonTable).build());

		PutBucketTaggingRequest request = PutBucketTaggingRequest.builder()
				.bucket(bucket).tagging(Tagging.builder().tagSet(tags).build()).build();
		
		client.putBucketTagging(request);
		return bucket;
	}
	
	public static String createBucket(String bucket) {
		try(S3Client client = getClient()) {
		    return createBucket(client, bucket);
		}
	}
	
	public static String createBucket(S3Client client, String bucket) {
		CreateBucketRequest request = CreateBucketRequest.builder().bucket(bucket).build();
		client.createBucket(request);
	    return bucket;
	}

	// ----- DELETE BUCKET
	
	public static String deleteBucket(String bucket) {
		try(S3Client client = getClient()) {
		    return deleteBucket(client, bucket);
		}
	}
	
	public static String deleteBucket(S3Client client, String bucket) {
		DeleteBucketRequest request = DeleteBucketRequest.builder().bucket(bucket).build();
		client.deleteBucket(request);
	    return bucket;
	}
	
	// ----- OBJECT OPTIONS
	
	public static boolean existObject(String bucket, String key) {
		try(S3Client client = getClient()) {
			return existObject(client, bucket, key);
		}
	}
	
	public static boolean existObject(S3Client client, String bucket, String key) {
		HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucket).key(key).build();
		try {
			client.headObject(request);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	// ----- UPLOAD OBJECT
	
	public static String upload(String bucket, File file) {
		String key = UUID.randomUUID().toString().replace("-", "");
		return upload(bucket, key, file);
	}
	
	public static String upload(String bucket, String key, File file) {
		try(S3Client client = getClient()) {
			PutObjectRequest request = PutObjectRequest.builder().bucket(bucket).key(key).build();
			client.putObject(request, RequestBody.fromFile(file));
			return key;
		}
	}

	public static String upload(String bucket, byte[] bytes) {
		String key = UUID.randomUUID().toString().replace("-", "");
		return upload(bucket, key, bytes);
	}
	
	public static String upload(String bucket, String key, byte [] bytes) {
		try(S3Client client = getClient()) {
			PutObjectRequest request = PutObjectRequest.builder().bucket(bucket).key(key).build();
		    client.putObject(request, RequestBody.fromBytes(bytes));
		    return key;
		}
	}
	
	public static String upload(String bucket, String content) {
		String key = UUID.randomUUID().toString().replace("-", "");
		return upload(bucket, key, content);
	}
	
	public static String upload(String bucket, String key, String content) {
		try(S3Client client = getClient()) {
			PutObjectRequest request = PutObjectRequest.builder().bucket(bucket).key(key).build();
		    client.putObject(request, RequestBody.fromString(content, StandardCharsets.UTF_8));
		    return key;
		}
	}
	
	public static String upload(String bucket, InputStream is, long contentLength) {
		String key = UUID.randomUUID().toString().replace("-", "");
		return upload(bucket, key, is, contentLength);
	}
	
	public static String upload(String bucket, String key, InputStream is, long contentLength) {
		try(S3Client client = getClient()) {
			PutObjectRequest request = PutObjectRequest.builder().bucket(bucket).key(key).build();
		    client.putObject(request, RequestBody.fromInputStream(is, contentLength));
		    return key;
		}
	}
	
	// ----- COPY OBJECT
	
	public static void copy(String fromBucket, String toBucket, String key) {
		copy(fromBucket, toBucket, key, key);
	}
	
	public static void copy(String fromBucket, String toBucket, String fromKey, String toKey) {
		try(S3Client client = getClient()) {
			CopyObjectRequest request = CopyObjectRequest.builder()
					.sourceBucket(fromBucket).sourceKey(fromKey)
					.destinationBucket(toBucket).destinationKey(toKey)
					.build();
			client.copyObject(request);
		}
    }

	// ----- GET OBJECT URL
	
	public static URL getURL(String bucket, String key) {
		try(S3Client client = getClient()) {
			GetUrlRequest request = GetUrlRequest.builder().bucket(bucket).key(key).build();
			return client.utilities().getUrl(request);
		}
	}
	
	public static URL getAonTableDownloadURL(String aonTable, String key) {
		String bucket = getBucketName(aonTable, key).orElseThrow();
		return getDownloadURL(bucket, key);
	}
	
	public static URL getAonTableDownloadURL(String aonTable, String key, String contentDisposition) {
		String bucket = getBucketName(aonTable, key).orElseThrow();
		return getDownloadURL(bucket, key, contentDisposition);
	}
	
	public static URL getDownloadURL(String bucket, String key) {
		return generatePresignedUrl(bucket, key, null);
	}
	
	public static URL getDownloadURL(String bucket, String key, String contentDisposition) {
		return generatePresignedUrl(bucket, key, contentDisposition);
	}
	
	public static URL generatePresignedUrl(String bucket, String key, String contentDisposition) {
        try (S3Presigner presigner = S3Presigner.create()) {

            GetObjectRequest objectRequest = contentDisposition != null
           		? GetObjectRequest.builder().bucket(bucket).key(key).responseContentDisposition(contentDisposition).build()
            	: GetObjectRequest.builder().bucket(bucket).key(key).build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5))  // The URL will expire in 5 minutes.
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            
            return presignedRequest.url();
        }
    }
	
	// GET OBJECT METADATA

	public static String getContentType(String bucket, String key) {
		try(S3Client client = getClient()) {
			HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucket).key(key).build();
			return client.headObject(request).contentType();
		}
	}

	public static Map<String, String> getObjectMetadata(String bucket, String key) {
		try(S3Client client = getClient()) {
			HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucket).key(key).build();
			return client.headObject(request).metadata();
		}
	}	
	
	// ----- DOWNLOAD OBJECT
	
	public static byte[] download(String bucket, String key) throws IOException {
		try(S3Client client = getClient()) {
			GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(key).build();
			return client.getObject(request).readAllBytes();
		}
	}
	
	// ----- DELETE OBJECT
	
	public static void delete(String bucket, String key) {
		try(S3Client client = getClient()) {
			DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucket).key(key).build();
			client.deleteObject(request);
		}
	}
	
}

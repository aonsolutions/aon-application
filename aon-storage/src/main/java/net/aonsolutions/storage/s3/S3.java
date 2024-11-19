package net.aonsolutions.storage.s3;

import java.net.URL;
import java.time.Duration;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetBucketTaggingResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

public class S3 {
	
	private static Map<String, List<String>> AON_TABLE_BUCKETS_MAP;

	private static S3Client getS3Client() {
		return S3Client.create();		
	}
	
	private static S3Client getS3Client(Region region) {
		return S3Client.builder().region(region).build();	
	}
	
	public static URL generatePresignedUrl(String bucket, String key) {
        try (S3Presigner presigner = S3Presigner.create()) {

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(120))  // The URL will expire in 5 minutes.
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            
            return presignedRequest.url();
        }
    }
	public static URL getDownloadURL(String s3Bucket, String s3Key) {
		return generatePresignedUrl(s3Bucket, s3Key);
	}
	
	public static URL getDownloadURL(String s3Bucket, String s3Key, String contentDisposition ) {
//		AmazonS3 s3 = getAmazonS3();
//		String bucketName = getBucketName(s3, s3Bucket, s3Key).orElseThrow();
//		
//		ResponseHeaderOverrides headerOverrides = new ResponseHeaderOverrides();
//		headerOverrides.setContentDisposition(contentDisposition);
//		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, s3Key)
//			.withExpiration(getExpiration()).withResponseHeaders(headerOverrides);
//		return generatePresignedUrl(s3, bucketName, request);
		S3Client client = getS3Client();
		String bucketName = getBucketName(client, s3Bucket, s3Key).orElseThrow();
		return generatePresignedUrl(bucketName, s3Key);
	}
	
	public static URL getContractDocDownloadURL(String s3Key) {
//		AmazonS3 s3 = getAmazonS3();
//		String bucketName = getBucketName(s3, "contract_doc", s3Key).orElseThrow();
//		return generatePresignedUrl(s3, bucketName, s3Key);		
		S3Client client = getS3Client();
		String bucketName = getBucketName(client, "contract_doc", s3Key).orElseThrow();
		return generatePresignedUrl(bucketName, s3Key);
	}

	public static URL getContractDocDownloadURL(String s3Key, String contentDisposition ) {
//		AmazonS3 s3 = getAmazonS3();
//		String bucketName = getBucketName(s3, "contract_doc", s3Key).orElseThrow();
//		
//		ResponseHeaderOverrides headerOverrides = new ResponseHeaderOverrides();
//		headerOverrides.setContentDisposition(contentDisposition);
//		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, s3Key)
//			.withExpiration(getExpiration()).withResponseHeaders(headerOverrides);
		
		S3Client client = getS3Client();
		String bucketName = getBucketName(client, "contract_doc", s3Key).orElseThrow();
		return generatePresignedUrl(bucketName, s3Key);
	}
	
	private static Optional<String> getBucketName(S3Client client, String aonTable, String s3Key ) {
	    return getBucketsNames(client, aonTable).stream().filter( bucketName -> doesObjectExist(client, bucketName, s3Key) ).findFirst();
	}

	private static boolean doesObjectExist(S3Client client, String bucketName, String s3Key) {
		HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucketName).key(s3Key).build();
		try {
			client.headObject(request);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private static List<String> getBucketsNames(S3Client client, String aonTable ) {
		if ( AON_TABLE_BUCKETS_MAP == null )
			AON_TABLE_BUCKETS_MAP = getBucketsTagMap(client, "AON_TABLE");
		
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
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		S3Client client = getS3Client();
		getBucketsTagMap(client, "AON_TABLE").forEach((k,v) -> System.out.println( k + " = " + v ));
	}

}

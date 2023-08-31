package net.aonsolutions.storage.s3;

import java.net.URL;
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

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketTaggingConfiguration;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.TagSet;

public class S3 {
	
	private static Map<String, List<String>> AON_TABLE_BUCKETS_MAP ;
	
	public static URL getContractDocDownloadURL(String s3Key) {
		AmazonS3 s3 = getAmazonS3();
		String bucketName = getBucketName(s3, "contract_doc", s3Key).orElseThrow();
		return generatePresignedUrl(s3, bucketName, s3Key);
	}

	public static URL getContractDocDownloadURL(String s3Key, String contentDisposition ) {
		AmazonS3 s3 = getAmazonS3();
		String bucketName = getBucketName(s3, "contract_doc", s3Key).orElseThrow();
		
		ResponseHeaderOverrides headerOverrides = new ResponseHeaderOverrides();
		headerOverrides.setContentDisposition(contentDisposition);
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, s3Key)
			.withExpiration(getExpiration()).withResponseHeaders(headerOverrides);
		return generatePresignedUrl(s3, bucketName, request);
	}

	private static Date getExpiration() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 120);
		return calendar.getTime();
	}
	
	
	private static Optional<String> getBucketName(AmazonS3 s3, String aonTable, String s3Key ) {
	    return getBucketsNames(s3, aonTable).stream().filter( bucketName -> doesObjectExist(s3, bucketName, s3Key) ).findFirst();
	}

	private static boolean doesObjectExist(AmazonS3 s3, String bucketName, String s3Key) {
	    return getAmazonS3(s3, bucketName).doesObjectExist(bucketName, s3Key);
	}
	
	private static URL generatePresignedUrl(AmazonS3 s3, String bucketName, String s3Key) {
	    AmazonS3 bucketS3 = getAmazonS3(s3, bucketName);
	    return bucketS3.generatePresignedUrl(bucketName, s3Key, getExpiration()/* , HttpMethod.GET */);
	}
	
	private static URL generatePresignedUrl(AmazonS3 s3, String bucketName, GeneratePresignedUrlRequest request) {
	    AmazonS3 bucketS3 = getAmazonS3(s3, bucketName);
	    return bucketS3.generatePresignedUrl(request/* , HttpMethod.GET */);
	}
	
	private static List<String> getBucketsNames(AmazonS3 s3, String aonTable ) {
		
		if ( AON_TABLE_BUCKETS_MAP == null )
			AON_TABLE_BUCKETS_MAP = getBucketsTagMap(s3, "AON_TABLE");
		
		return AON_TABLE_BUCKETS_MAP.getOrDefault(aonTable,Collections.emptyList());
	}

	private static Predicate<String> filterBucket( AmazonS3 s3, String aonTable) {
		return bucketName -> {
			BucketTaggingConfiguration bucketTaggingConfiguration = s3.getBucketTaggingConfiguration(bucketName);
			if ( bucketTaggingConfiguration == null ) 
				return false;
			TagSet tagSet = bucketTaggingConfiguration.getTagSet();
			if ( tagSet == null ) 
				return false;
			//tagSet.getAllTags().forEach((k,v) -> System.out.println( k + "=" + v ));
			String tagAonTable = tagSet.getTag("AON_TABLE");
			if ( tagAonTable == null ) 
				return false;
			return aonTable.equalsIgnoreCase(tagAonTable) ;
		};
	}
	
	private static Map<String, List<String>> getBucketsTagMap(AmazonS3 s3, String tag) {
		return 
		s3.listBuckets().stream()
		.map (bucket -> new String [] {getBucketTag(s3, bucket.getName(), tag), bucket.getName()} )
		.filter(arr -> arr[0] != null )
		.map(arr -> Map.entry(arr[0],Collections.singletonList(arr[1])))
		.collect(Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue, (l1,l2) -> Stream.concat(l1.stream(), l2.stream()).toList() ));
	}

	private static String getBucketTag( AmazonS3 s3, String bucketName, String tag) {
	    	AmazonS3 bucketS3 = getAmazonS3(s3, bucketName);
		BucketTaggingConfiguration bucketTaggingConfiguration = bucketS3.getBucketTaggingConfiguration(bucketName);
		if ( bucketTaggingConfiguration == null ) 
			return null;
		TagSet tagSet = bucketTaggingConfiguration.getTagSet();
		if ( tagSet == null ) 
			return null;
		return  tagSet.getTag(tag);
	}


	private static AmazonS3 getAmazonS3() {
		return AmazonS3ClientBuilder.defaultClient();
//		return 
//		AmazonS3ClientBuilder.standard()
//		.withEndpointConfiguration(new EndpointConfiguration("https://s3.fr-par.scw.cloud", "fr-par"))
//		.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("SCWD2FB35NZD0MW7HSS3", "2d19cd8d-4111-4bda-a290-f19dd80f0ee7")))
//		.build();		
	}
	
	private static AmazonS3 getAmazonS3(String region) {
		return AmazonS3ClientBuilder.standard().withRegion(region).build();	
	}

	private static AmazonS3 getAmazonS3(AmazonS3 s3, String bucketName) {
	    	return getAmazonS3(s3.getBucketLocation(bucketName));
	}

	public static void main(String[] args) {
		AmazonS3 s3 = getAmazonS3();
		getBucketsTagMap(s3, "AON_TABLE").forEach((k,v) -> System.out.println( k + " = " + v ));
	}
	
	
	
	

}

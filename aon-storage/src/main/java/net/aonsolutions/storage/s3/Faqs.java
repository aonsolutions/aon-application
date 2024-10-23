package net.aonsolutions.storage.s3;

import java.util.UUID;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;

public class Faqs {

	public static void main(String[] args) {
		S3Client client = S3Client.create();
//		AmazonS3 s3 = AmazonS3ClientBuilder.standard()
//				.withEndpointConfiguration(new EndpointConfiguration("https://s3.fr-par.scw.cloud", "fr-par"))
//				.withCredentials(new AWSStaticCredentialsProvider(
//						new BasicAWSCredentials("SCWD2FB35NZD0MW7HSS3", "2d19cd8d-4111-4bda-a290-f19dd80f0ee7")))
//				.build();

//		List<Bucket> buckets = s3.listBuckets();
//		System.out.println("Your Amazon S3 buckets are:");
//		for (Bucket b : buckets) {
//			System.out.println(b.getName());
//			ObjectListing result = s3.listObjects(b.getName());
//			List<S3ObjectSummary> objects = result.getObjectSummaries();
//			for (S3ObjectSummary os : objects) {
//				System.out.println("\t" + os.getKey());
//			}
//		}
		
		
//		s3.deleteObject("aon-contract-doc", "unknown");
		String key = UUID.randomUUID().toString();
		//s3.putObject("aon-contract-doc", key, "---");
		
		DeleteObjectsRequest request = DeleteObjectsRequest.builder()
				.bucket("aon-contract-doc").build();

		client.deleteObjects(request).deleted()
		.forEach(d -> {
			System.out.println(d.key() +" - " + d.versionId() +", " + d.deleteMarkerVersionId());
		});
		
	}

}

package net.aonsolutions.storage.s3;

import java.util.UUID;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;

public class Faqs {

	public static void main(String[] args) {

		AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();

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
		s3.deleteObjects(
		new DeleteObjectsRequest("aon-contract-doc")
		.withKeys(key)
//		.withGeneralProgressListener(new ProgressListener() {
//			@Override
//			public void progressChanged(ProgressEvent progressEvent) {
//				System.out.println(progressEvent.getEventType().describeConstable().toString());
//			}
//			
//		})
		).getDeletedObjects()
		.forEach(d -> {
			System.out.println(d.getKey() +" - " + d.getVersionId() +", " + d.getDeleteMarkerVersionId());
		});
		
	}

}

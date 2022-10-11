package net.aonsolutions.storage.s3;

import java.net.URL;
import java.util.Calendar;
import java.util.List;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class ListBuckets {

	public static void main(String[] args) {

		// final AmazonS3 s3 =
		// AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();

		AmazonS3 s3 = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new EndpointConfiguration("https://s3.fr-par.scw.cloud", "fr-par"))
				.withCredentials(new AWSStaticCredentialsProvider(
						new BasicAWSCredentials("SCWD2FB35NZD0MW7HSS3", "2d19cd8d-4111-4bda-a290-f19dd80f0ee7")))
				.build();

		List<Bucket> buckets = s3.listBuckets();
		System.out.println("Your Amazon S3 buckets are:");
		for (Bucket b : buckets) {
			System.out.println("* " + b.getName());
			Calendar expiration = Calendar.getInstance();
			expiration.add(Calendar.MINUTE, 5);
			URL url = s3.generatePresignedUrl(b.getName(), "presigned_url_put_test.pdf", expiration.getTime(), HttpMethod.PUT);
			System.out.println("URL : " + url.toString());

//			ObjectListing result = s3.listObjects(b.getName());
//			List<S3ObjectSummary> objects = result.getObjectSummaries();
//			for (S3ObjectSummary os : objects) {
//				System.out.println("* " + os.getKey());
//				URL url = s3.generatePresignedUrl(b.getName(), os.getKey(), expiration.getTime(), HttpMethod.GET);
//				System.out.println("URL : " + url.toString());
//			}
		}
	}

}

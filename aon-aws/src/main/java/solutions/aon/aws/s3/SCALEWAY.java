package solutions.aon.aws.s3;

import java.net.URI;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class SCALEWAY extends S3 {
	
	private static final String SCALEWAY_REGION = "fr-par";
	private static final String USER = "SCWPHPGAFS9GFT4K3JXR";
	private static final String PASS = "1e3474fc-1c51-445a-931d-30f5d1eb92b7";
	
	protected SCALEWAY() {
		super();
	}
	
	public static SCALEWAY getInstance() {
		return new SCALEWAY();
	}
	

	@Override
	public S3Client getClient() {
		try {
			S3Client client = S3Client.builder()
			.endpointOverride(URI.create("https://s3.fr-par.scw.cloud"))
			.credentialsProvider(StaticCredentialsProvider.create(
	                AwsBasicCredentials.create(USER, PASS)))
			.region(Region.of(SCALEWAY_REGION))
			.build();
			return client;
		} catch (Exception e) {
			throw e;
		}
	}	
	
}

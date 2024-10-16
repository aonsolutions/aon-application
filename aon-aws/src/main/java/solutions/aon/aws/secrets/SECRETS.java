package solutions.aon.aws.secrets;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.PutSecretValueRequest;

public class SECRETS {

	private static SecretsManagerClient connect() {
		return SecretsManagerClient.builder()
                .region(Region.EU_WEST_1)
                .build();
	}
	
	public static String getValue(String secretId) {
		SecretsManagerClient client = connect();
		try {
			GetSecretValueRequest valueRequest = GetSecretValueRequest.builder().secretId(secretId).build();

			GetSecretValueResponse valueResponse = client.getSecretValue(valueRequest);
			String secret = valueResponse.secretString();

			return secret;
		} finally {
			client.close();
		}
	}
	
	public static void putValue(String secretId, String value) {
		SecretsManagerClient client = connect();
		try {
			PutSecretValueRequest valueRequest = PutSecretValueRequest.builder()
				.secretId(secretId)
				.secretString(value)
				.build();
		
			client.putSecretValue(valueRequest);
		} finally {
			client.close();
		}
	}
}

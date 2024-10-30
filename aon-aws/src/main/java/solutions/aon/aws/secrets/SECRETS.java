package solutions.aon.aws.secrets;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.DeleteSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.PutSecretValueRequest;

public class SECRETS {

	private static final String AON_SECRET = "aonsolutions/aonsecret";
	private static final String AON_CERT = "aonsolutions/aoncert";
	
	private SECRETS() {
	
	}
	
	private static SecretsManagerClient getClient() {
		return SecretsManagerClient.create();
	}
	
	public static String getValue(String secretId) {
		try (SecretsManagerClient client = getClient()) {
			GetSecretValueRequest request = GetSecretValueRequest.builder().secretId(secretId).build();
			return client.getSecretValue(request).secretString();
		}
	}
	
	public static String create(String secretId, String secret) {
		try (SecretsManagerClient client = getClient()) {
			CreateSecretRequest request = CreateSecretRequest.builder()
				.name(secretId)
				.secretString(secret)
				.build();	
			client.createSecret(request);
			return secretId;
		}
	}
	
	public static void putValue(String secretId, String value) {
		try (SecretsManagerClient client = getClient()) {			
			PutSecretValueRequest request = PutSecretValueRequest.builder()
				.secretId(secretId)
				.secretString(value)
				.build();
		
			client.putSecretValue(request);
		}
	}
	
	public static void delete(String secretId) throws Exception {
		if(!canDelete(secretId)) throw new Exception("NO SE PUEDE BORRAR EL SECRETO " + secretId);
		try (SecretsManagerClient client = getClient()) {
			DeleteSecretRequest request = DeleteSecretRequest.builder().secretId(secretId).build();
			client.deleteSecret(request);
		}
	}
	
	private static boolean canDelete(String secretId) {
		return !AON_SECRET.equalsIgnoreCase(secretId) && !AON_CERT.equalsIgnoreCase(secretId);
	}
}

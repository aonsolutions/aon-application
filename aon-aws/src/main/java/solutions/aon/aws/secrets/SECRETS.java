package solutions.aon.aws.secrets;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class SECRETS {

	private static SecretsManagerClient connect() {
		return SecretsManagerClient.builder()
                .region(Region.EU_WEST_1)
                .build();
	}
	
	public static String getValue(String secretId) {
		SecretsManagerClient client = connect();
		 GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                 .secretId(secretId)
                 .build();

         GetSecretValueResponse valueResponse = client.getSecretValue(valueRequest);
         String secret = valueResponse.secretString();
         client.close();
         return secret;
	}

    public static void main(String[] args) {
    	String a = SECRETS.getValue("aonsolutions/aonsecret");
    	System.out.println(a);
    }
}

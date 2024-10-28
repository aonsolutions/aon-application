package solutions.aon.aws.secrets;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

public class SecretsTestCase {
	
	private static final String SECRET = "aon-aws-test/";
	
	@Test
	public void test() throws Exception {	
		String uuid = UUID.randomUUID().toString();
		String secretId = SECRET + uuid;
		
		String secret = "{\"test\":\"test\"}";

		SECRETS.create(secretId, secret);

		String value = SECRETS.getValue(secretId);

		assertEquals(secret, value);
		
		SECRETS.delete(secretId);
	}	
}

package solutions.aon.aws;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import solutions.aon.aws.s3.S3TestCase;
import solutions.aon.aws.secrets.SecretsTestCase;
import solutions.aon.aws.ses.SesTestCase;
import solutions.aon.aws.ses.dynamodb.DynamodbTestCase;

@RunWith(Suite.class)
@SuiteClasses({
	S3TestCase.class,
	SecretsTestCase.class,
	SesTestCase.class,
	DynamodbTestCase.class
})
public class AwsTestSuite {

	
}

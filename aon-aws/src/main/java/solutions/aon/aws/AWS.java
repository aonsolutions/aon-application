package solutions.aon.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

public class AWS {
	private static final String ACCESS_KEY = "AKIARG5OEKO7GGOK4NOG";
	private static final String PRIVATE_KEY = "WiosXlQ3q1s18H39itwXRGG9USUk7/zUmqIqhvby";
	
	public static AWSCredentials getCredentials(){
		BasicAWSCredentials basic =  new BasicAWSCredentials(ACCESS_KEY, PRIVATE_KEY);
		return new AWSStaticCredentialsProvider(basic).getCredentials();
	}
	
	public static AWSCredentialsProvider getProvider() {
		BasicAWSCredentials basic =  new BasicAWSCredentials(ACCESS_KEY, PRIVATE_KEY);
		return new AWSStaticCredentialsProvider(basic);
	}
}

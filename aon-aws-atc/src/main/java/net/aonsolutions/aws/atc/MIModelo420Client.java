package net.aonsolutions.aws.atc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;


public class MIModelo420Client {
	public static void call(String functionName, byte[] payload) {
		call(functionName, ByteBuffer.wrap(payload));
	}

	public static void call(String functionName, ByteBuffer payload) {

		InvokeRequest invokeRequest = 
				new InvokeRequest()
				.withFunctionName(functionName)
				.withPayload(payload);
		InvokeResult invokeResult = null;

		try {
			AWSLambda awsLambda = AWSLambdaClientBuilder.defaultClient();

			invokeResult = awsLambda.invoke(invokeRequest);

			String ans = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);

			// write out the return value
			System.out.println(ans);

		} catch (ServiceException e) {
			System.out.println(e);
		}

		System.out.println(invokeResult.getStatusCode());
	}

	public static void main(String[] args) throws IOException {
		try ( InputStream is = new FileInputStream(args[0])) {
			//arn:aws:lambda:eu-west-1:083580179390:function:aon-atc-mod420
			call("arn:aws:lambda:eu-west-1:083580179390:function:aon-atc-mod420" , is.readAllBytes());
		}
	}
}

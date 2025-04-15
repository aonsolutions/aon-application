package net.aonsolutions.aws.atc;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.GetFunctionConfigurationRequest;
import com.amazonaws.services.lambda.model.GetFunctionConfigurationResult;
import com.amazonaws.services.lambda.model.ServiceException;
import com.amazonaws.services.lambda.model.UpdateFunctionCodeRequest;
import com.amazonaws.services.lambda.model.UpdateFunctionCodeResult;
import com.amazonaws.waiters.WaiterParameters;


public class MIModelo420Setup {
	
	public static void setup(String functionName, ByteBuffer zipFile) {
		
		try {
			
			AWSLambda awsLambda = AWSLambdaClientBuilder.defaultClient();

			UpdateFunctionCodeRequest updateFunctionCodeRequest = 
			new UpdateFunctionCodeRequest()
			.withZipFile(zipFile)
			.withFunctionName(functionName)
			;
			
			UpdateFunctionCodeResult updateFunctionCodeResult = 
			awsLambda.updateFunctionCode(updateFunctionCodeRequest);
			

			GetFunctionConfigurationRequest getFunctionConfigRequest = 
					new GetFunctionConfigurationRequest()
					.withFunctionName(functionName);
			
			awsLambda.waiters().functionUpdated()
			.run(new WaiterParameters<GetFunctionConfigurationRequest>().withRequest(getFunctionConfigRequest));
			
			GetFunctionConfigurationResult functionConfigurationResult = 
			awsLambda.getFunctionConfiguration(getFunctionConfigRequest);
			
			System.out.printf("[ %s ] %s  %s \r\n", functionConfigurationResult.getFunctionName(), functionConfigurationResult.getLastModified(),  functionConfigurationResult.getLastUpdateStatus() );

		} catch (ServiceException e) {
			System.err.println(e);
		}

	}

	public static void setup(String functionName, String imageUri) {
		
		try {
			
			AWSLambda awsLambda = AWSLambdaClientBuilder.defaultClient();

			UpdateFunctionCodeRequest updateFunctionCodeRequest = 
			new UpdateFunctionCodeRequest()
			.withImageUri(imageUri)
			.withFunctionName(functionName)
			;
			
			UpdateFunctionCodeResult updateFunctionCodeResult = 
			awsLambda.updateFunctionCode(updateFunctionCodeRequest);
			

			GetFunctionConfigurationRequest getFunctionConfigRequest = 
					new GetFunctionConfigurationRequest()
					.withFunctionName(functionName);
			
			awsLambda.waiters().functionUpdated()
			.run(new WaiterParameters<GetFunctionConfigurationRequest>().withRequest(getFunctionConfigRequest));
			
			GetFunctionConfigurationResult functionConfigurationResult = 
			awsLambda.getFunctionConfiguration(getFunctionConfigRequest);
			
			System.out.printf("[ %s ] %s  %s \r\n", functionConfigurationResult.getFunctionName(), functionConfigurationResult.getLastModified(),  functionConfigurationResult.getLastUpdateStatus() );

		} catch (ServiceException e) {
			System.err.println(e);
		}

	}

	public static void main(String[] args) throws IOException {
//		String jarWithDependencies = "target/aon.aws.atc-9.23-SNAPSHOT-jar-with-dependencies.jar";
//		try ( InputStream is = new FileInputStream(jarWithDependencies)) {
//			setup("aon-atc-mod420" , ByteBuffer.wrap(is.readAllBytes()));
//		}
		setup("aon-aws-atc" , "083580179390.dkr.ecr.eu-west-1.amazonaws.com/aonsolutions/aon-aws-atc:test" );
	}
}

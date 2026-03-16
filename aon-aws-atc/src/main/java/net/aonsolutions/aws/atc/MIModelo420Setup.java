package net.aonsolutions.aws.atc;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.GetFunctionConfigurationRequest;
import com.amazonaws.services.lambda.model.GetFunctionConfigurationResult;
import com.amazonaws.services.lambda.model.ServiceException;
import com.amazonaws.services.lambda.model.UpdateFunctionCodeRequest;
import com.amazonaws.waiters.WaiterParameters;

public class MIModelo420Setup {
	
//	public static void setup(String functionName, ByteBuffer zipFile) {
//		
//		try {
//			
//			AWSLambda awsLambda = AWSLambdaClientBuilder.defaultClient();
//
//			UpdateFunctionCodeRequest updateFunctionCodeRequest = 
//			new UpdateFunctionCodeRequest()
//			.withZipFile(zipFile)
//			.withFunctionName(functionName)
//			;
//			
//			UpdateFunctionCodeResult updateFunctionCodeResult = 
//			awsLambda.updateFunctionCode(updateFunctionCodeRequest);
//
//			GetFunctionConfigurationRequest getFunctionConfigRequest = 
//					new GetFunctionConfigurationRequest()
//					.withFunctionName(functionName);
//			
//			awsLambda.waiters().functionUpdated()
//			.run(new WaiterParameters<GetFunctionConfigurationRequest>().withRequest(getFunctionConfigRequest));
//			
//			GetFunctionConfigurationResult functionConfigurationResult = 
//			awsLambda.getFunctionConfiguration(getFunctionConfigRequest);
//			
//			System.out.printf("[ %s ] %s  %s \r\n", functionConfigurationResult.getFunctionName(), functionConfigurationResult.getLastModified(),  functionConfigurationResult.getLastUpdateStatus() );
//
//		} catch (ServiceException e) {
//			System.err.println(e);
//		}
//
//	}

	public static void setup(String functionName, String imageUri) {
		
		try {
			
			AWSLambda awsLambda = AWSLambdaClientBuilder.defaultClient();

			UpdateFunctionCodeRequest updateFunctionCodeRequest = new UpdateFunctionCodeRequest()
																		.withImageUri(imageUri)
																		.withFunctionName(functionName);
			
//			UpdateFunctionCodeResult updateFunctionCodeResult = 
			awsLambda.updateFunctionCode(updateFunctionCodeRequest);

			GetFunctionConfigurationRequest getFunctionConfigRequest = new GetFunctionConfigurationRequest()
																			.withFunctionName(functionName);
			
			awsLambda.waiters().functionUpdated().run(new WaiterParameters<GetFunctionConfigurationRequest>().withRequest(getFunctionConfigRequest));
			
			GetFunctionConfigurationResult functionConfigurationResult = awsLambda.getFunctionConfiguration(getFunctionConfigRequest);
			
			System.out.printf("[ %s ] %s  %s \r\n", functionConfigurationResult.getFunctionName(), functionConfigurationResult.getLastModified(),  functionConfigurationResult.getLastUpdateStatus() );

		} catch (ServiceException e) {
			System.err.println(e);
		}

	}

	public static void main(String[] args) {
//		String jarWithDependencies = "target/aon.aws.atc-9.23-SNAPSHOT-jar-with-dependencies.jar";
//		try ( InputStream is = new FileInputStream(jarWithDependencies)) {
//			setup("aon-atc-mod420" , ByteBuffer.wrap(is.readAllBytes()));
//		}
		
		// Ejercicio 2025
//		setup("aon-aws-atc", "083580179390.dkr.ecr.eu-west-1.amazonaws.com/aonsolutions/aon-aws-atc:latest"); // Resto de modelos Ejercicio 2025
//		setup("aon-aws-atc-mod415", "083580179390.dkr.ecr.eu-west-1.amazonaws.com/aonsolutions/aon-aws-atc:m415test"); // Modelo 415 Ejercicio 2025
		
		// Ejercicio 2026
		setup("aon-aws-atc-2026", "083580179390.dkr.ecr.eu-west-1.amazonaws.com/aonsolutions/aon-aws-atc:m4XXe2026latest"); // Resto de modelos Ejercicio 2026
		
	}
}

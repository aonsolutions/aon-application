package net.aonsolutions.aws.atc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;

import net.aonsolutions.aws.atc.lambda.MIModelo420Result;


public class MIModelo420Client {
	
	public static void call(String functionName, byte[] payload) {
		call(functionName, ByteBuffer.wrap(payload));
	}

	public static void call(String functionName, ByteBuffer byteBuffer) {
		
		String payload =
		String.format("{"
		+ "\"declaracion\":\"%s\" "
		+ "}"
		, Base64.getEncoder().encodeToString(byteBuffer.array()));
		
		System.out.println(payload);
		
		InvokeRequest invokeRequest = 
				new InvokeRequest()
				.withFunctionName(functionName)
				.withPayload(payload);
		
		InvokeResult invokeResult = null;

		try {
			AWSLambda awsLambda = AWSLambdaClientBuilder.defaultClient();

			invokeResult = awsLambda.invoke(invokeRequest);
			
			// re-contruimos el objeto MIModelo420Result original  
			MIModelo420Result result = 
			new ObjectMapper().readValue(invokeResult.getPayload().array(), MIModelo420Result.class);

			// decodificamos el XML   
			System.out.println(org.grecasa.ext.codificador.Codificador.decodifica(result.getResultado()));

		} catch (Exception e) {
			System.out.println(e);
		}

		System.out.println(invokeResult.getStatusCode());
	}

	private static String obtenerXML() {
		String fileXML =
//			"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" +
//			"<DEC PER=\"1T\" ANY=\"2025\" MOD=\"420\">" +
//			"<IDE>" +
//			"<OTP PAI=\"ES\" CP=\"35002\" CMU=\"35016\" POP=\"35\" NVP=\"AGENCIA TRIBUTARIA CANARIA\" SVP=\"CL\" NRS=\"DECLARACION SIN ACTIVIDAD\" NIF=\"B50111111\"/>" +
//			"</IDE>" +
//			"<RES TIP=\"S\"/>" +
//			"</DEC>"
		"<DEC NJA=\"4200000000000\" COM=\"X\" PER=\"1T\" ANY=\"2025\" MOD=\"420\" >"
		+ "	<IDE TAC=\"PR\" FAC=\"01/02/2019\" ACR=\"S\">"
		+ "		<OTP PAI=\"ES\" CP=\"35003\" CMU=\"35016\" POP=\"35\" TEL=\"928999999\" PUE=\"B\" PIS=\"10\" ESC=\"A\" NPK=\"2\" NVP=\"AGENCIA TRIBUTARIA\" SVP=\"BO\" NRS=\"SUJETO PASIVO - COMPENSAR \" NIF=\"B50111111\" />"
		+ "	</IDE>"
		+ "	<IGI_DED TOT=\"-3900\">"
		+ "		<RBI CUO=\"-3900\"/>"
		+ "	</IGI_DED>"
		+ "	<LIQ RLI=\"-21100\" DAC=\"25000\" DIF=\"3900\"/>"
		+ "	<RES IMP=\"21100\" TIP=\"C\"/>"
		+ "</DEC>"
		;
		return fileXML;
	}

	public static void main(String[] args) throws IOException {
		call("aon-aws-atc" , obtenerXML().getBytes());
		//try ( InputStream is = new FileInputStream(args[0])) {
			//arn:aws:lambda:eu-west-1:083580179390:function:aon-atc-mod420
			//call("aon-atc-mod420" , is.readAllBytes());
		//}
	}
	
	
}

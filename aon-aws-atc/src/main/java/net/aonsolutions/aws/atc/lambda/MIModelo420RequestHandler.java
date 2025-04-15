package net.aonsolutions.aws.atc.lambda;

import java.util.Base64;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class MIModelo420RequestHandler implements RequestHandler<MIModelo420Request, MIModelo420Result> {
	
	@Override
	public MIModelo420Result handleRequest(MIModelo420Request input, Context context) {
		String  declaracion  = new String(Base64.getDecoder().decode(input.getDeclaracion()));
		System.out.println(declaracion);
		return new MIModelo420Handler().obtenerPresentacion(declaracion);
	}
}

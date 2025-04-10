package net.aonsolutions.aws.atc.lambda;

import java.util.Base64;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class MIModelo420RequestHandler implements RequestHandler<MIModelo420Request, String> {
	
	@Override
	public String handleRequest(MIModelo420Request input, Context context) {
		byte [] body = Base64.getDecoder().decode(input.getBody());
		String  declaracion  = new String(body);
		System.out.println(declaracion);
		byte [] impresoOficial = new MIModelo420Handler().obtenerImpresoOficial(declaracion);
		return new String(impresoOficial);
	}
}

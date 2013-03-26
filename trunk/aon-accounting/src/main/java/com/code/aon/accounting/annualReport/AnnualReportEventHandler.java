package com.code.aon.accounting.annualReport;

import org.apache.velocity.app.event.IncludeEventHandler;
import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.app.event.NullSetEventHandler;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.util.introspection.Info;

public class AnnualReportEventHandler implements IncludeEventHandler,InvalidReferenceEventHandler,MethodExceptionEventHandler,NullSetEventHandler,ReferenceInsertionEventHandler{

	private static String MSG01 = "No se pudo resolver la expresión [%s]";
	private static String MSG02 = "La ejecución de la expresión [%s], provocó un error: [%s]";
	
	@Override
	public Object referenceInsert(String reference, Object value) {
		return value;
	}

	@Override
	public Object methodException(@SuppressWarnings("rawtypes") Class claz, String method, Exception e) throws Exception {
		System.out.println(  String.format(MSG02, method, e.getMessage() ));
		return null;
	}

	@Override
	public Object invalidGetMethod(Context context, String reference, Object object, String property, Info info) {
		// TODO Auto-generated method stub
		System.out.println("invalidGetMethod");
		return null;
	}

	@Override
	public boolean invalidSetMethod(Context context, String leftreference, String rightreference, Info info) {
		// TODO Auto-generated method stub
		System.out.println("invalidSetMethod");
		return false;
	}

	@Override
	public Object invalidMethod(Context context, String reference, Object object, String method, Info info) {
		System.out.println(  String.format(MSG01, reference ));
		return null;
	}

	@Override
	public String includeEvent(String includeResourcePath, String currentResourcePath, String directiveName) {
		// TODO Auto-generated method stub
		System.out.println("includeEvent");
		return null;
	}

	@Override
	public boolean shouldLogOnNullSet(String lhs, String rhs) {
		// TODO Auto-generated method stub
		System.out.println("shouldLogOnNullSet");
		return false;
	}

}

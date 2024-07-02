package net.aonsolutions.invofox;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

public class OCRFilter  {
	
	private SimpleDateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
	private enum OCRFilterOperators {
		eq,is,gt,gte,lt,lte
		;
	}
	
	private JSONObject json = new JSONObject();
	
	// ------------------------------------------------------- EQ
	public OCRFilter eq(String name, Boolean bool 	) {return append(name, OCRFilterOperators.eq.toString(), bool );}
	public OCRFilter eq(String name, Number number 	) {return append(name, OCRFilterOperators.eq.toString(), number );}
	public OCRFilter eq(String name, Date date		) {return eq	(name, dataFormatter.format( date));}
	public OCRFilter eq(String name, String value	) {return append(name, OCRFilterOperators.eq.toString(), value );}
	
	// ------------------------------------------------------- IS
	public OCRFilter is(String name, Boolean bool 	) {return append(name, OCRFilterOperators.is.toString(), bool );}
	public OCRFilter is(String name, Number number 	) {return append(name, OCRFilterOperators.is.toString(), number );}
	public OCRFilter is(String name, Date date		) {return is	(name, dataFormatter.format( date));}
	public OCRFilter is(String name, String value	) {return append(name, OCRFilterOperators.is.toString(), value );}
	
	// ------------------------------------------------------- GT
	public OCRFilter gt(String name, Boolean bool 	) {return append(name, OCRFilterOperators.gt.toString(), bool );}
	public OCRFilter gt(String name, Number number 	) {return append(name, OCRFilterOperators.gt.toString(), number );}
	public OCRFilter gt(String name, Date date		) {return gt	(name, dataFormatter.format( date));}
	public OCRFilter gt(String name, String value	) {return append(name, OCRFilterOperators.gt.toString(), value );}
	
	// ------------------------------------------------------- GTE
	public OCRFilter gte(String name, Boolean bool 	) {return append(name, OCRFilterOperators.gte.toString(), bool );}
	public OCRFilter gte(String name, Number number ) {return append(name, OCRFilterOperators.gte.toString(), number );}
	public OCRFilter gte(String name, Date date		) {return gte	(name, dataFormatter.format( date));}
	public OCRFilter gte(String name, String value	) {return append(name, OCRFilterOperators.gte.toString(), value );}

	// ------------------------------------------------------- LT
	public OCRFilter lt(String name, Boolean bool 	) {return append(name, OCRFilterOperators.lt.toString(), bool );}
	public OCRFilter lt(String name, Number number 	) {return append(name, OCRFilterOperators.lt.toString(), number );}
	public OCRFilter lt(String name, Date date		) {return lt	(name, dataFormatter.format( date));}
	public OCRFilter lt(String name, String value	) {return append(name, OCRFilterOperators.lt.toString(), value );}

	// ------------------------------------------------------- LTE
	public OCRFilter lte(String name, Boolean bool 	) {return append(name, OCRFilterOperators.lte.toString(), bool );}
	public OCRFilter lte(String name, Number number ) {return append(name, OCRFilterOperators.lte.toString(), number );}
	public OCRFilter lte(String name, Date date		) {return lte	(name, dataFormatter.format( date));}
	public OCRFilter lte(String name, String value	) {return append(name, OCRFilterOperators.lte.toString(), value );}
	
	// ------------------------------------------------------- between
	public OCRFilter between(String name, Number from, Number to ) {
		return 
			append(name, OCRFilterOperators.gte.toString(), from)
			.append(name, OCRFilterOperators.lte.toString(), to);
	}
	public OCRFilter between(String name, Date from, Date to		) {
		return between( name, dataFormatter.format(from), dataFormatter.format(to));
	}
	public OCRFilter between(String name, String from, String to	) {
		return 
			append(name, OCRFilterOperators.gte.toString(), from)
			.append(name, OCRFilterOperators.lte.toString(), to);
	}

	private <T> OCRFilter append(String name, String operator, T value) {
		JSONObject attr = json.optJSONObject( name , new JSONObject() );
		attr.put( operator, value);
		json.put( name , attr );
		return this;
	}
	
	public String build() {
		System.out.println( json.toString(1) );
		return URLEncoder.encode(json.toString(), StandardCharsets.UTF_8);
	}
	
	
}

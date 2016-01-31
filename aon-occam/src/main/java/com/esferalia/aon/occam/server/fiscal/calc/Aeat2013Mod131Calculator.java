package com.esferalia.aon.occam.server.fiscal.calc;

import java.util.LinkedHashMap;
import java.util.Map;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;

public class Aeat2013Mod131Calculator {
	
	private static final IAccMiningKeyAccept ACCEPTER = new IAccMiningKeyAccept() {
		@Override
		public boolean acceptKey(Object key) {
			try {
				return (Mod131Key.valueOf((String) key) != null);	
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
	};

	public static final String  PLUS = "+";
	public static final String  MULT = "+";
	public static final String  MINUS = "+";
	public static final String  DIV = "+";
	
	public static Map<String,String> COMP = new LinkedHashMap<String,String>();
	static {	
		COMP.put(Mod131Key.AC01.toString(),"round(AC12 + AC22 + AC32 + AC42 + AC52)");
		COMP.put(Mod131Key.AC02.toString(),"round(AC14 + AC24 + AC34 + AC44 + AC54)");
		COMP.put(Mod131Key.C04.toString() ,"round(C03 * 2 / 100)");
		COMP.put(Mod131Key.C06.toString() ,"round(C05 * 2 / 100)");
		COMP.put(Mod131Key.C07.toString() ,"round(AC02 + C04 + C06)");
		COMP.put(Mod131Key.C10.toString() ,"round(C07 - C08 - C09)");
		COMP.put(Mod131Key.C13.toString() ,"round(C10 - C11 - C12)");
		COMP.put(Mod131Key.C15.toString() ,"round(C13 - C14)");
		
//		COMP.put(Mod131Key.AC01.toString()
//				,Mod131Key.AC14.toString()
//				+PLUS+Mod131Key.AC24.toString()
//				+PLUS+Mod131Key.AC34.toString()
//				+PLUS+Mod131Key.AC44.toString()
//				+PLUS+Mod131Key.AC54.toString());
//		COMP.put(Mod131Key.C04.toString()
//				,Mod131Key.C03.toString()+"* 2 / 100");
//		COMP.put(Mod131Key.C06.toString()
//				,Mod131Key.C05.toString()+"* 2 / 100");
//		COMP.put(Mod131Key.C07.toString()
//				,Mod131Key.AC02.toString()
//				+PLUS+Mod131Key.C04.toString()
//				+PLUS+Mod131Key.C06.toString());
//		COMP.put(Mod131Key.C10.toString()
//				,Mod131Key.C07.toString()
//				+MINUS+Mod131Key.C08.toString()
//				+MINUS+Mod131Key.C09.toString());
//		COMP.put(Mod131Key.C13.toString()
//				,Mod131Key.C10.toString()
//				+MINUS+Mod131Key.C11.toString()
//				+MINUS+Mod131Key.C12.toString());
//		COMP.put(Mod131Key.C15.toString()
//				,Mod131Key.C14.toString()
//				+MINUS+Mod131Key.C14.toString());
		
	}
	
	public static Mod131 calculate(AONContext aonctx, Mod131 mod131) {
		
		AccMiningMVELContext ctx = new AccMiningMVELContext( ACCEPTER );
		ctx.setExpressionMap(COMP);
		for (FiscalModelDetail detail : mod131.getMap().values()) {
			Mod131Key key = Mod131Key.getKey( detail.getType() );
			if (key != null) 
				ctx.put( key.toString(), detail.getAmount() );
		}

		for (String stringKey : COMP.keySet()) {
			Mod131Key key = Mod131Key.valueOf(stringKey);
			String expression = COMP.get(stringKey);
			Object ret = ctx.evaluateExpression(stringKey,expression);
			if (ret instanceof Double) {
				Double calculated = (Double) ret;
				ctx.put(stringKey, calculated);
				mod131.putAmount(key, calculated);
			}
		}
		return mod131;
	}

}

package com.esferalia.aon.occam.test.fiscal.mod303;

import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.json.VatContextJSON;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.watson.util.Pair;

public class VatContextJSONTest extends Mod303AbstractTest {
	
	@Test
	public void vatContextJSONTest( ) {
		for (Mod303 model : MODEL303.getMod303s(getOccam()) ) {
			Mod303 mod303 = MODEL303.get(getOccam(), model.getId());
			
			VATDAO.getNotInModelNoAccrualVatBreakdown(ctx, mod303)
				.map( br -> new Pair<VatContext,JSONObject>(br,null))
				.map( pair -> pair.setRight( VatContextJSON.toJSON(pair.getLeft())) )
//				.map( pair -> {
//					System.out.println( pair.getRight().toString(1) );		
//					return pair;
//				})
				.forEach( pair -> Asserts.assertEqualsVatContext(pair.getLeft() , VatContextJSON.fromJSON(pair.getRight())))
			;
		}
	}
	
}

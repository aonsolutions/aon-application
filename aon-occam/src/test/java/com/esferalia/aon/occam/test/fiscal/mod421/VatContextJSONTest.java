package com.esferalia.aon.occam.test.fiscal.mod421;

import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL421;
import com.esferalia.aon.occam.api.json.VatContextJSON;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.watson.util.Pair;

public class VatContextJSONTest extends Mod421AbstractTest {
	
	@Test
	public void vatContextJSONTest( ) {
		for (Mod421 model : MODEL421.getMod421s(getOccam()) ) {
			Mod421 mod421 = MODEL421.get(getOccam(), model.getId());
			
			VATDAO.getVatBreakdown(ctx, mod421)
				.map( br -> new Pair<VatContext,JSONObject>(br,null))
				.map( pair -> pair.setRight( VatContextJSON.toJSON(pair.getLeft())) )
				.forEach( pair -> Asserts.assertEqualsVatContext(pair.getLeft() , VatContextJSON.fromJSON(pair.getRight())))
			;
		}
	}
	
}

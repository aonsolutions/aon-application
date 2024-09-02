package com.esferalia.aon.occam.test.fiscal.mod111;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.json.IrpfBreakdownJSON;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.impl.jooq.dao.irpf.IRPFDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.watson.util.Pair;

public class IrpfBreakdownJSONTest extends AbstractOccamTest {
	
	@Test
	public void irpfBreakdownJSONTest( ) {
		for (Mod111 model : MODEL111.getMod111s(getOccam()) ) {
			Mod111 mod111 = MODEL111.get(getOccam(), model.getId());
			IRPFDAO.getModelInputInvoicesIrpfBreakdown(ctx, mod111)
				.map( br -> new Pair<IrpfBreakdown,JSONObject>(br,null))
				.map( pair -> pair.setRight( IrpfBreakdownJSON.toJSON(pair.getLeft())) )
				.forEach( pair -> Asserts.assertEqualsIrpfBreakdown(pair.getLeft() , IrpfBreakdownJSON.fromJSON(pair.getRight())))
			;
		}
	}
	
}

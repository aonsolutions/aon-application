package com.esferalia.aon.occam.test.fiscal.mod303;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEAT390nfoScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATGeneralRegimeScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATGeneralRegimeScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATSimplifiedRegime4TScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017AEATSimplifiedRegimeScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032017PrintAEATScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032021AEATResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032021PrintAEATScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032021_2AEATAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032021_2PrintAEATScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATAdditionalDataScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATGeneralRegimeScript1;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATGeneralRegimeScript2;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATResultScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATSimplifiedRegime4TScript;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model3032022AEATSimplifiedRegimeScript;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;

public class Mod303ScriptTest extends AbstractOccamTest {
	
	@Test
	public void testCommonTerritoryExpressions() {
		FiscalFakerParams params = new FiscalFakerParams(ctx,getOccam())
				.setIssueDate(new Date())
				.setMonthly(true)
				.setAdministration(Administration.COMMON_TERRITORY);
		Mod303 mod303 = FiscalFaker.getMod303(params);
		MODEL303.calculate(getOccam(), mod303);
		
		test( mod303, Model3032017AEAT390nfoScript.values() );
		test( mod303, Model3032017AEATAdditionalDataScript.values() );
		test( mod303, Model3032017AEATGeneralRegimeScript1.values() );
		test( mod303, Model3032017AEATGeneralRegimeScript2.values() );
		test( mod303, Model3032017AEATResultScript.values() );
		test( mod303, Model3032017AEATSimplifiedRegime4TScript.values() );
		test( mod303, Model3032017AEATSimplifiedRegimeScript.values() );
		test( mod303, Model3032017PrintAEATScript.values() );
		test( mod303, Model3032021_2AEATAdditionalDataScript.values() );
		test( mod303, Model3032021_2PrintAEATScript.values() );
		test( mod303, Model3032021AEATResultScript.values() );
		test( mod303, Model3032021PrintAEATScript.values() );
		test( mod303, Model3032022AEATAdditionalDataScript.values() );
		test( mod303, Model3032022AEATGeneralRegimeScript1.values() );
		test( mod303, Model3032022AEATGeneralRegimeScript2.values() );
		test( mod303, Model3032022AEATResultScript.values() );
		test( mod303, Model3032022AEATSimplifiedRegime4TScript.values() );
		test( mod303, Model3032022AEATSimplifiedRegimeScript.values() );
	}

	private void test( Mod303 mod303, IModelScript<Mod303Key>[] scripts) {
		for (IModelScript<Mod303Key> script : scripts) {
			System.out.println( " \t" + scripts.getClass().getSimpleName() + " > " + script );
			for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
				MODEL303.getInfo(getOccam(), mod303, script, infoKey);
			}
		}
	}
}

package com.esferalia.aon.occam.test.fiscal.mod390hf;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Mod390HFInsert extends AbstractOccamTest {
	
	@Test
	public void test() {
		test( Administration.ALAVA);
		test( Administration.BIZKAIA);
		test( Administration.GIPUZKOA);
	}

	public void test( Administration admon) {
		Date now = new Date();
		Mod390HF mod390HF = new Mod390HF();
		mod390HF.setDomain(DOMAIN_ID);
		mod390HF.setYear(AonDateUtils.getYear(now));
		mod390HF.setPeriod( Period.YEAR );
		mod390HF = MODEL390HF.initialize(getOccam(), mod390HF);
		mod390HF.setAdministration(admon);
		mod390HF = MODEL390HF.create(getOccam(), mod390HF);
		MODEL390HF.save(getOccam(), mod390HF);
	}
}

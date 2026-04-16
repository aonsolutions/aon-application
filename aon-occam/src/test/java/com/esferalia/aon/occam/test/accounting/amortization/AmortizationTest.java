package com.esferalia.aon.occam.test.accounting.amortization;


import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.type.AmortizationPeriod;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;

public class AmortizationTest extends AbstractOccamTest {

	@Test
	public void amortization_insert_test() {
		String date = "01/04/2019"; 
		Date initialDate = AonDateUtils.parse(date, "dd/MM/yyyy");
		String description = "OFICINA " + AonDateUtils.format( new Date(), AonDateUtils.DATE_TIME_FORMAT);
		AmortizationType at =  new AmortizationType()
			.setFixedAssetAccount("2110")
			.setAccumulatedAccount("2811")
			.setAllocationAccount("6811")
			.setPercentage(3.0000)
			.setDescription(description)
		;
		double amount = 151590.0000;
		String comments = "Segun Tasacion de KRATA - Valor del suelo: 148.410,-  + Valor del suelo: 151.590,-  (49.47% / 300.000,- )";
		Amortization am = new Amortization()
			.setDomain(DOMAIN_ID)
			.setDescription(description)
			.setInitialDate( initialDate )
			.setAmount( amount )
			.setFeePeriod( AmortizationPeriod.MONTHLY )
			.setComments(comments)
			.setAmortizationType(at)
		;
		am = ACCOUNTING.saveAmortization( getOccam(), am);
		
		am.setAmount( amount + 1000);
		am = ACCOUNTING.saveAmortization( getOccam(), am);
	}
	
}

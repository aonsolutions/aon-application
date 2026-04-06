package com.esferalia.aon.occam.test.accounting.amortization;


import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.type.AmortizationPeriod;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.amortization.AmortizationDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.OccamAsserts;
import com.esferalia.aon.occam.test.OccamMocker;
import com.esferalia.aon.watson.server.AonDateUtils;

public class AmortizationTest extends AbstractOccamTest {

	@Test
	public void amortization_pojo_test() {
		Amortization am1 = OccamMocker.mock( Amortization.class ); 
		 
		Amortization am2 = new Amortization();
		am2.setId(am1.getId());
		am2.setDomain(am1.getDomain());
		am2.setInvestAsset(am1.getInvestAsset());
		am2.setAllocationAccount(am1.getAllocationAccount());
		am2.setAccumulatedAccount(am1.getAccumulatedAccount());
		am2.setFixedAssetAccount(am1.getFixedAssetAccount());
		am2.setDescription(am1.getDescription());
		am2.setInitialDate(am1.getInitialDate());
		am2.setDeadline(am1.getDeadline());
		am2.setAmount(am1.getAmount());
		am2.setFeePeriod(am1.getFeePeriod());
		am2.setSaleAmount(am1.getSaleAmount());
		am2.setComments(am1.getComments());
		am2.setPercentage(am1.getPercentage());
		am2.setSecurityLevel(am1.getSecurityLevel());
		am2.setAmortizationType(am1.getAmortizationType());
		am1.detailStream().forEach( d  -> am2.addDetail(d));
		OccamAsserts.assertClassEquals( am1, am2);
	}

	@Test
	public void amortization_detail_pojo_test() {
		AmortizationDetail am1 = OccamMocker.mock( AmortizationDetail.class ); 
		AmortizationDetail am2 = new AmortizationDetail();
		am2.setId(am1.getId());
		am2.setDomain(am1.getDomain());
		am2.setAmortization(am1.getAmortization());
		am2.setAccountEntry(am1.getAccountEntry());
		am2.setFromDate(am1.getFromDate());
		am2.setToDate(am1.getToDate());
		am2.setCoefficient(am1.getCoefficient());
		am2.setAllocation(am1.getAllocation());
		am2.setStatus(am1.getStatus());
		am2.setFiscalAllocation(am1.getFiscalAllocation());
		am2.setAccumulated(am1.getAccumulated());
		am2.setPending(am1.getPending());
		am2.setFiscalAccumulated(am1.getFiscalAccumulated());
		am2.setFiscalPending(am1.getFiscalPending());

		OccamAsserts.assertClassEquals( am1, am2);
	}
	@Test
	public void aization_insert_test() {
		Date a = AonDateUtils.parse("01/04/2019", "dd/MM/yyyy");
		System.out.println( AonDateUtils.addDays(a, 12174) );
	}
	
	@Test
	public void amortization_insert_test() {
		String description = "OFICINA " + AonDateUtils.format( new Date(), AonDateUtils.DATE_TIME_FORMAT);
		AmortizationType at =  new AmortizationType()
			.setFixedAssetAccount("2110")
			.setAccumulatedAccount("2811")
			.setAllocationAccount("6811")
			.setPercentage(3.0000)
			.setDescription(description)
		;
		
		Amortization am = new Amortization()
			.setDomain(DOMAIN_ID)
			.setDescription("Local OFICINA")
			.setInitialDate( AonDateUtils.parse("01/04/2019", "dd/MM/yyyy") )
			.setAmount(151590.0000)
			.setFeePeriod( AmortizationPeriod.MONTHLY )
			.setComments("Segun Tasacion de KRATA - Valor del suelo: 148.410,-  + Valor del vuelo: 151.590,-  (49.47% / 300.000,- )")
			.setPercentage(3.0000)
			.setAmortizationType(at)
		;
		am = ACCOUNTING.saveAmortization( getOccam(), am);
	}
	
}

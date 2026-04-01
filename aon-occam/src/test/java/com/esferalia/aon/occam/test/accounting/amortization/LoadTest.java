package com.esferalia.aon.occam.test.accounting.amortization;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.occam.impl.jooq.dao.AmortizationTypeDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class LoadTest extends AbstractOccamTest {
	private static final AmortizationType[] TYPES= new AmortizationType[] { 
			new AmortizationType().setFixedAssetAccount("2060").setAccumulatedAccount("2806").setAllocationAccount("6806").setPercentage(25.0000).setDescription("APLICACIONES INFORMATICAS"),
			new AmortizationType().setFixedAssetAccount("2060").setAccumulatedAccount("2806").setAllocationAccount("6806").setPercentage(20.0000).setDescription("DESARROLLOS/TECNOLOGIA DE TERCEROS"),
			new AmortizationType().setFixedAssetAccount("2170").setAccumulatedAccount("2817").setAllocationAccount("6817").setPercentage(33.3300).setDescription("EQUIPOS INFORMATICOS"),
			new AmortizationType().setFixedAssetAccount("2180").setAccumulatedAccount("2818").setAllocationAccount("6818").setPercentage(20.0000).setDescription("ELEMENTOS DE TRANSPORTE"),
			new AmortizationType().setFixedAssetAccount("2110").setAccumulatedAccount("2811").setAllocationAccount("6811").setPercentage(3.0000).setDescription("Edificios para casa. oficina, uso comercial o servicios"),
			new AmortizationType().setFixedAssetAccount("2150").setAccumulatedAccount("2815").setAllocationAccount("6815").setPercentage(14.2900).setDescription("OBRAS , MANTENIMIENTO Y REFORMAS LOCAL"),
			new AmortizationType().setFixedAssetAccount("2040").setAccumulatedAccount("2804").setAllocationAccount("6804").setPercentage(12.5000).setDescription("FONDO DE COMERCIO"),
			new AmortizationType().setFixedAssetAccount("2160").setAccumulatedAccount("2816").setAllocationAccount("6816").setPercentage(25.0000).setDescription("INSTALACIONES, MOBILIARIO/RESTO INMOVILIZADO MATERIAL"),
			new AmortizationType().setFixedAssetAccount("2180").setAccumulatedAccount("2818").setAllocationAccount("6818").setPercentage(20.0000).setDescription("RENAULT CAPTUR"),
			new AmortizationType().setFixedAssetAccount("2160").setAccumulatedAccount("2816").setAllocationAccount("6816").setPercentage(25.0000).setDescription("INSTALACIONES, MOBILIARIO/RESTO INMOVILIZ ADO MATERIAL")
	 };
			
	@Test
	public void testInsert() throws IOException {
		AmortizationTypeParams params = new AmortizationTypeParams().setDomain(DOMAIN_ID);
		List<AmortizationType> types = AmortizationTypeDAO.getList(ctx, params);
		if (AonCollectionUtils.isEmpty(types)) {
			for (AmortizationType at : TYPES) {
				at.setDomain( new Domain().setId(DOMAIN_ID));
				AmortizationTypeDAO.save(ctx, at);
			}
		}
	}
	
}

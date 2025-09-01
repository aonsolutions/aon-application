package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.finance.VerifactuConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;

public class InvoiceCommunicationConfigurationDAO {
	
	private InvoiceCommunicationConfigurationDAO() {

	}
	
	public static InvoiceCommunicationConfiguration get(AONContext ctx, int domainId) {
		ctx.checkRead();
		InvoiceCommunicationConfiguration configuration = new InvoiceCommunicationConfiguration();
		
		ApplicationParameter administration = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(domainId)	
			.and(f.getNameProperty().eq(AppParam.FS_DEFAULT_ADMINISTRATION.toString())))
			.findFirst().orElse(new ApplicationParameter());
		
		configuration.setAdministration(administration.getValue() != null
				? Administration.safeValueOf(Integer.parseInt(administration.getValue()))
				: Administration.UNKNOWN);
				
		TbaiConfiguration tbaiConfiguration = TbaiConfigurationDAO.get(ctx);
		configuration.setTbai(tbaiConfiguration.isActive());
		if(tbaiConfiguration.isActive()) {
			configuration.setTest(tbaiConfiguration.isTest());
			configuration.setIncludeDate(tbaiConfiguration.getIncludeDate());
			configuration.setRegistryDate(tbaiConfiguration.getRegistryDate());
		}
		
		VerifactuConfiguration verifactuConfiguration = VerifactuConfigurationDAO.get(ctx);
		configuration.setVerifactu(verifactuConfiguration.isActive());
		if(verifactuConfiguration.isActive()) {
			configuration.setTest(verifactuConfiguration.isTest());
			configuration.setIncludeDate(verifactuConfiguration.getIncludeDate());
			configuration.setRegistryDate(verifactuConfiguration.getRegistryDate());
		}
		
		SiiConfiguration siiConfiguration = SiiConfigurationDAO.get(ctx);
		configuration.setSii(siiConfiguration.isActive());
		if(siiConfiguration.isActive()) {
			configuration.setTest(siiConfiguration.isTest());
			configuration.setIncludeDate(siiConfiguration.getIncludeDate());
			configuration.setRegistryDate(siiConfiguration.getRegistryDate());
		}

		return configuration;
	}
	
}





package com.esferalia.aon.occam.impl.jooq.dao.accounting.analytical;

import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalAccount;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalCostCenter;

public class ANALYTICAL {

	public static JAXBAnalytical getJAXBAnalytical(Analytical analytical) {
		if (analytical == null) return null;
		JAXBAnalytical jaxbAnalytical = new JAXBAnalytical();
		JAXBAnalyticalConfiguration jaxbConfig = new JAXBAnalyticalConfiguration();
		jaxbConfig.setName(analytical.getName());
		jaxbAnalytical.setConfiguration(jaxbConfig);
		if ( analytical.getCostCenters() != null) {
			for (AnalyticalCostCenter costCenter : analytical.getCostCenters().values()) {
				JAXBAnalyticalCostCenter jaxbCostCenter = new JAXBAnalyticalCostCenter();
				jaxbCostCenter.setDefault(costCenter.isMain());
				jaxbCostCenter.setName(costCenter.getName());
				jaxbCostCenter.setPercent(costCenter.getPercent());
				JAXBAnalyticalAccounts jaxbAccounts = new JAXBAnalyticalAccounts();
				if (costCenter.getAccounts() != null) {
					for ( AnalyticalAccount account : costCenter.getAccounts().values()) {
						JAXBAnalyticalAccount jaxbAccount = new JAXBAnalyticalAccount();
						jaxbAccount.setCode(account.getCode());
						jaxbAccount.setPercent(account.getPercent());
						jaxbAccounts.getAccount().add(jaxbAccount);
					}
				}
				jaxbCostCenter.setAccounts(jaxbAccounts);
				jaxbConfig.getCostCenter().add(jaxbCostCenter);
			}
		}
		return jaxbAnalytical;
	}

	public static Analytical getAnalytical(JAXBAnalytical jaxbAnalytical ) {
		if (jaxbAnalytical == null) return null;
		JAXBAnalyticalConfiguration jaxbConfig = jaxbAnalytical.getConfiguration();
		if (jaxbConfig == null) return null;
		Analytical analytical = new Analytical();
		analytical.setName(jaxbConfig.getName());
		if (jaxbConfig.getCostCenter() != null) {
			for (JAXBAnalyticalCostCenter jaxbCostCenter : jaxbConfig.getCostCenter()) {
				AnalyticalCostCenter costCenter = new AnalyticalCostCenter()
						.setMain(jaxbCostCenter.isDefault())
						.setPercent(jaxbCostCenter.getPercent())
						.setName(jaxbCostCenter.getName());
				if (jaxbCostCenter.isDefault()) {
					analytical.setDefaultCostCenter(jaxbCostCenter.getName());
				}
				JAXBAnalyticalAccounts jaxbAccounts = jaxbCostCenter.getAccounts();
				if (jaxbAccounts != null) {
					for (JAXBAnalyticalAccount jaxbAccount : jaxbAccounts.getAccount()) {
						costCenter.addAccount( new AnalyticalAccount()
								.setCode(jaxbAccount.getCode())
								.setPercent(jaxbAccount.getPercent()));
					}
				}
				analytical.add( costCenter );
			}
		}
		return analytical;
	}
}

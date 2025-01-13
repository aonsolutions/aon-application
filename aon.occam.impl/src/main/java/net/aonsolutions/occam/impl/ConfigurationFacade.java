package net.aonsolutions.occam.impl;

import net.aonsolutions.occam.api.model.Configuration;
import net.aonsolutions.occam.api.model.Occam;
import net.aonsolutions.occam.impl.AONContext.CloseableAONContext;
import net.aonsolutions.occam.impl.handler.ConfigurationBridge;

public class ConfigurationFacade {
	
	private ConfigurationFacade() {
		
	}

	public static Configuration getConfiguration(Occam occam, int domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			Configuration configuration = ConfigurationBridge.getConfiguration( ctx, domain);
			ConfigurationBridge.getCurrentUser(ctx, configuration);
			ConfigurationBridge.getCompany(ctx, configuration);
			ConfigurationBridge.getDefaultScope(ctx, configuration);
			ConfigurationBridge.getInheritanceDomainIds(ctx, configuration);
			ConfigurationBridge.getUserScopes(ctx, configuration);
			ConfigurationBridge.getActivities(ctx, configuration);
			ConfigurationBridge.getInvestAssets(ctx, configuration);
			ConfigurationBridge.getApplicationParameters(ctx, configuration);
			ConfigurationBridge.getTbaiConfiguration(ctx, configuration);
			ConfigurationBridge.getDefaultWorkplace(ctx, configuration);
			ConfigurationBridge.getPeriods(ctx, configuration);
			return configuration;
		}
	}
	
}

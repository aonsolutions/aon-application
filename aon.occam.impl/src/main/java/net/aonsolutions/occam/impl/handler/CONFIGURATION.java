package net.aonsolutions.occam.impl.handler;

import java.util.Optional;
import java.util.stream.Stream;

import net.aonsolutions.occam.api.model.Activity;
import net.aonsolutions.occam.api.model.ApplicationParameters;
import net.aonsolutions.occam.api.model.CompanyFull;
import net.aonsolutions.occam.api.model.Configuration;
import net.aonsolutions.occam.api.model.Domain;
import net.aonsolutions.occam.api.model.InvestAsset;
import net.aonsolutions.occam.api.model.Scope;
import net.aonsolutions.occam.api.model.TbaiConfiguration;
import net.aonsolutions.occam.api.model.User;
import net.aonsolutions.occam.api.model.Workplace;
import net.aonsolutions.occam.impl.AONContext;

public class CONFIGURATION {

	public static Configuration getConfiguration(AONContext ctx, int domain) {
		return ConfigurationHandler.getConfiguration( ctx, domain);
	}
	public static Domain getDomain(AONContext ctx, Configuration configuration ) {
		return ConfigurationHandler.getDomain( ctx, configuration);
	}
	public static User getCurrentUser(AONContext ctx, Configuration configuration) {
		return ConfigurationHandler.getUser( ctx, configuration);
	}
	public static CompanyFull getCompany(AONContext ctx, Configuration configuration) {
		return ConfigurationHandler.getCompany( ctx, configuration);
	}
	public static Optional<Scope> getDefaultScope(AONContext ctx, Configuration configuration ) {
		return ConfigurationHandler.getDefaultScope( ctx, configuration);
	}

	public static Integer[] getInheritanceDomainIds(AONContext ctx, Configuration configuration) {
		return ConfigurationHandler.getInheritanceDomainIds( ctx, configuration);
	}
	public static Integer[] getUserScopes(AONContext ctx, Configuration configuration) {
		return ConfigurationHandler.getUserScopes( ctx, configuration);
	}
	public static Stream<Activity> getActivities(AONContext ctx, Configuration configuration) {
		return ConfigurationHandler.getActivities( ctx, configuration);
	}
	public static Stream<InvestAsset> getInvestAssets(AONContext ctx, Configuration configuration) {
		return ConfigurationHandler.getInvestAssets( ctx, configuration);
	}
	public static ApplicationParameters getApplicationParameters(AONContext ctx, Configuration configuration) {
		return ConfigurationHandler.getApplicationParameters( ctx, configuration);
	}
	public static TbaiConfiguration getTbaiConfiguration(AONContext ctx, Configuration configuration) {
		return ConfigurationHandler.getTbaiConfiguration( ctx, configuration);
	}
	public static Optional<Workplace> getDefaultWorkplace(AONContext ctx, Configuration configuration) {
		return ConfigurationHandler.getDefaultWorkplace( ctx, configuration);
	}
	
}

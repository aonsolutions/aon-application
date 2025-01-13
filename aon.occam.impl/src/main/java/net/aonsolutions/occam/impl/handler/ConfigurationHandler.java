package net.aonsolutions.occam.impl.handler;

import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

import net.aonsolutions.occam.api.model.AccountPeriod;
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

class ConfigurationHandler {
	
	private ConfigurationHandler() {
		
	}

	static Configuration getConfiguration(AONContext ctx, int domain) {
		System.out.println( "ConfigurationHandler getConfiguration");
		return new Configuration()
			.setDomainId(domain)
			.setDomain( DomainHandler.get(ctx, domain).orElseThrow(() -> new AonCoreException(AonError.INVALID_DOMAIN.getMessage())))
		;
	}
	
	static Domain getDomain( AONContext ctx, Configuration config ) {
		return config.getDomain().orElseGet( () -> {
			System.out.println( "ConfigurationHandler getDomain");
			Domain domain = DomainHandler.get(ctx, config.getDomainId())
				.orElseThrow(() -> new AonCoreException(AonError.INVALID_DOMAIN.getMessage()));
			config.setDomain( domain );
			return domain;
		});		
	}

	static User getUser( AONContext ctx, Configuration config ) {
		return config.getUser().orElseGet( () -> {
			System.out.println( "ConfigurationHandler getUser");
			User user = SecurityHandler.getUser(ctx, config)
				.orElseThrow(() -> new AonCoreException(AonError.INVALID_USER.getMessage()));
			config.setUser( user );
			return user;
		});
	}

	static CompanyFull getCompany(AONContext ctx, Configuration config) {
		return config.getCompany().orElseGet( () -> {
			System.out.println( "ConfigurationHandler getCompany");
			CompanyFull company = CompanyHandler.getFull(ctx, config.getDomainId()) 
				.orElseThrow(() -> new AonCoreException(AonError.INVALID_COMPANY.getMessage()));
			config.setCompany(company);
			return company;
		});		
	}
	
	static Stream<Workplace> getWorkplaces(AONContext ctx, Configuration config) {
		if (!config.isWorkplacesResolved()) {
			System.out.println( "ConfigurationHandler getWorkplaces");
			WorkplaceHandler.stream(ctx, config.getDomainId())
				.forEach( config::addWorkplace );
		}
		return config.workplacesStream();		
	}
	
	static Optional<Workplace> getDefaultWorkplace(AONContext ctx, Configuration config) {
		if (!config.isWorkplacesResolved()) {
			System.out.println( "ConfigurationHandler getDefaultWorkplace");
			getWorkplaces( ctx, config );		
		}
		return config.getDefaultWorkplace();
	}

	static Stream<AccountPeriod> getPeriods(AONContext ctx, Configuration config) {
		if (!config.isPeriodsResolved()) {
			System.out.println( "ConfigurationHandler getPeriods");
			AccountPeriodHandler.stream(ctx, config.getDomainId())
				.forEach( config::addPeriod);
		}
		return config.periodsStream();		
	}

	static Optional<Scope> getDefaultScope(AONContext ctx, Configuration config) {
		Optional<Scope> os = config.getDefaultScope( );
		if (!config.isDefaultScopeResolved() && os.isEmpty()) {
			System.out.println( "ConfigurationHandler getDefaultScope");
			Scope scope = SecurityHandler.getDefaultScope(ctx, config ).orElse(null);
			config.setDefaultScope(scope);
			config.setDefaultScopeResolved(true);
		}
		return config.getDefaultScope();
	}

	static Optional<AccountPeriod> getDefaultAccountPeriod(AONContext ctx, Configuration config) {
		Optional<AccountPeriod> os = config.getDefaultAccountPeriod( );
		if (!config.isDefaultAccountPeriodResolved() && os.isEmpty()) {
			System.out.println( "ConfigurationHandler getDefaultAccountPeriod");
			getApplicationParameters(ctx, config)
				.getAccountingDefaultPeriod()
				.ifPresent( dp -> {
					AccountPeriod defPer = AccountPeriodHandler.get(ctx, config.getDomainId(), dp ).orElse(null);
					config.setDefaultAccountPeriod(defPer);
					config.setDefaultAccountPeriodResolved(true);
				});
		}
		return config.getDefaultAccountPeriod();
	}
	
	static Integer[] getInheritanceDomainIds(AONContext aonContext, Configuration config) {
		Domain domain = getDomain(aonContext, config);
		if (domain.isHeredityEnabled() && domain.getParentId() != null) {
			return new Integer[] { domain.getId(), domain.getParentId() };
		}
		return new Integer[] { domain.getId() };
	}

	static Integer[] getUserScopes(AONContext ctx, Configuration config) {
		return config
			.getUserScopes()
			.orElseGet( () -> {
				System.out.println( "ConfigurationHandler getUserScopes");
				Integer[] userScopes = SecurityHandler.getUserScopes(ctx, config)
					.orElseThrow(() -> new AonCoreException(AonError.NO_SCOPES_DEFINED_FOR_USER.getMessage()));
				config.setUserScopes ( userScopes );
				return userScopes;
			}
		);
	}

	static Stream<Activity> getActivities(AONContext ctx, Configuration config) {
		if (!config.isActivitiesResolved()) {
			System.out.println( "ConfigurationHandler getActivities");
			ActivityHandler.stream(ctx, config.getDomainId(), null)
				.forEach( config::addActivity );
			config.setActivitiesResolved(true);
		}
		return config.activityStream();
	}

	static Stream<InvestAsset> getInvestAssets(AONContext ctx, Configuration config) {
		if (!config.isInvestAssetsResolved()) {
			System.out.println( "ConfigurationHandler getInvestAssets");
			InvestAssetHandler.stream(ctx, config.getDomainId(), null)
				.forEach( config::addInvestAsset);
			config.setInvestAssetsResolved(true);
		}
		return config.investAssetStream(); 
	}

	static ApplicationParameters getApplicationParameters(AONContext ctx, Configuration config) {
		return config.getApplicationParameters()
			.orElseGet( () -> {
				System.out.println( "ConfigurationHandler getApplicationParameters");
				ApplicationParameters aps = AppParamHandler.getApplicationParameters(ctx, config.getDomainId());
				config.setApplicationParameters ( aps );
				return aps;
			}
		);
	}

	static TbaiConfiguration getTbaiConfiguration(AONContext ctx, Configuration config) {
		return config.getTbaiConfiguration()
			.orElseGet( () -> {
				TbaiConfiguration tb = TbaiConfigurationHandler.get(ctx, config.getDomainId());
				config.setTbaiConfiguration(tb);
				return tb;
			}
		);
	}

	static boolean hasConfidentialityRole(AONContext ctx, Configuration configuration) {
		return configuration.hasConfidentialityRole();
	}
	static boolean hasAccountingRole(AONContext ctx, Configuration configuration) {
		return configuration.hasAccountingRole();
	}

}

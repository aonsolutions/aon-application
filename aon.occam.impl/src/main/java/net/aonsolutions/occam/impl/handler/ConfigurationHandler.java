package net.aonsolutions.occam.impl.handler;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;

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
		return new Configuration()
			.setDomainId(domain)
			.setDomain( DomainHandler.get(ctx, domain).orElseThrow(() -> new AonCoreException(AonError.INVALID_DOMAIN.getMessage())))
		;
	}
	
	static Domain getDomain( AONContext ctx, Configuration config ) {
		return config.getDomain().orElseGet( () -> {
			Domain domain = DomainHandler.get(ctx, config.getDomainId())
				.orElseThrow(() -> new AonCoreException(AonError.INVALID_DOMAIN.getMessage()));
			config.setDomain( domain );
			return domain;
		});		
	}

	static User getUser( AONContext ctx, Configuration config ) {
		return config.getUser().orElseGet( () -> {
			User user = SecurityHandler.getUser(ctx, config)
				.orElseThrow(() -> new AonCoreException(AonError.INVALID_USER.getMessage()));
			config.setUser( user );
			return user;
		});
	}

	static CompanyFull getCompany(AONContext ctx, Configuration config) {
		return config.getCompany().orElseGet( () -> {
			CompanyFull company = CompanyHandler.getFull(ctx, config.getDomainId()) 
				.orElseThrow(() -> new AonCoreException(AonError.INVALID_COMPANY.getMessage()));
			config.setCompany(company);
			return company;
		});		
	}
	
	static Optional<Workplace> getDefaultWorkplace(AONContext ctx, Configuration config) {
		if (config.getWorkplaces().isEmpty()) {
			LinkedList<Workplace> workplaces = 
				WorkplaceHandler.stream(ctx, config.getDomainId())
					.collect( Collectors.toCollection( LinkedList::new));
			config.setWorkplaces ( workplaces );
		}
		return config.getDefaultWorkplace();
	}

	static Optional<Scope> getDefaultScope(AONContext ctx, Configuration config) {
		Optional<Scope> os = config.getDefaultScope( );
		if (!config.isDefaultScopeResolved() && os.isEmpty()) {
			Scope scope = SecurityHandler.getDefaultScope(ctx, config ).orElse(null);
			config.setDefaultScope(scope);
			config.setDefaultScopeResolved(true);
		}
		return config.getDefaultScope();
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
				Integer[] userScopes = SecurityHandler.getUserScopes(ctx, config)
					.orElseThrow(() -> new AonCoreException(AonError.NO_SCOPES_DEFINED_FOR_USER.getMessage()));
				config.setUserScopes ( userScopes );
				return userScopes;
			}
		);
	}

	static Stream<Activity> getActivities(AONContext ctx, Configuration config) {
		return config
			.getActivities()
			.map( a -> a.stream() )
			.orElseGet( () -> {
				LinkedList<Activity> activities = 
					ActivityHandler.stream(ctx, config.getDomainId(), null)
						.collect( Collectors.toCollection( LinkedList::new));
				config.setActivities ( activities );
				return AonCollectionUtils.stream(activities); 
			}
		);		
	}

	static Stream<InvestAsset> getInvestAssets(AONContext ctx, Configuration config) {
		return config
			.getInvestAssets()
			.map( a -> a.stream() )
			.orElseGet( () -> {
				LinkedList<InvestAsset> investAssets = 
					InvestAssetHandler.stream(ctx, config.getDomainId(), null)
						.collect( Collectors.toCollection( LinkedList::new));
				config.setInvestAssets ( investAssets );
				return AonCollectionUtils.stream(investAssets); 
			}
		);		
	}

	static ApplicationParameters getApplicationParameters(AONContext ctx, Configuration config) {
		return config.getApplicationParameters()
			.orElseGet( () -> {
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

}

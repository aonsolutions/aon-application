package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.AonCoreException;
import net.aonsolutions.occam.api.AonError;
import net.aonsolutions.occam.api.accounting.Account;
import net.aonsolutions.occam.api.config.AccountingConfiguration;
import net.aonsolutions.occam.api.config.ApplicationParameter;
import net.aonsolutions.occam.api.config.Configuration;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.constants.AppParam;
import net.aonsolutions.occam.api.filter.ConfigurationFacade.CompositeConfigurationBuilder;
import net.aonsolutions.occam.api.filter.ConfigurationFacade.ConfigurationBuilder;
import net.aonsolutions.occam.api.filter.ConfigurationFacade.ConfigurationBuilderFactory;
import net.aonsolutions.watson.client.util.AonNumberUtils;

public class ConfigurationDAO {
	private ConfigurationDAO() {
	}
	
	private static class ConfigurationBuilderDAO extends  CompositeConfigurationBuilder {
		
		private final Configuration conf;
		
		public ConfigurationBuilderDAO( AONContext ctx, Domain domain ) {
			conf = new Configuration().setUuid(ctx.getDomainName());
			addBuilder(new AccountingBuilder(ctx, domain, conf));
		}
		
		@Override
		public Configuration build() {
			return conf;
		}
	}

	private static class AccountingBuilder implements ConfigurationBuilder {
		private final Domain domain;
		private final Configuration conf;
		private final AONContext ctx;
		
		public AccountingBuilder(final AONContext ctx, Domain domain, final Configuration conf) {
			this.domain = domain;  
			this.conf = conf;
			this.ctx = ctx;
		}
		
		@Override
		public Configuration build() {
			return conf;
		}
		
		@Override
		public ConfigurationBuilder withAccountingConfiguration() {
			conf.setAccounting(new AccountingConfiguration()
				.setUuid(ctx.getDomainName()));
			fillAccountingParameters(ctx);
			return this;
		}
		
		private void fillAccountingParameters(AONContext ctx) {
			Pattern pattern = Pattern.compile(AppParam.ACCOUNT_PATTERN);
			Arrays.stream( AppParam.values() )
				.filter(p -> pattern.matcher(p.toString()).find())
				.forEach( appParam -> {
					Optional<ApplicationParameter> optAppParam =  getApplicationParameter(ctx,domain,appParam);
					if (optAppParam.isPresent()) {
						Integer accountId = AonNumberUtils.toInteger( optAppParam.get().getValue() );
						if (accountId != null) {
							Optional<Account> optAccount = AccountDAO.get(ctx,accountId);
							if (optAccount.isPresent()) {
								conf.accounting().get().setAccount(appParam, optAccount.get());
							}
						}
					}
				});
		}
		
		private Optional<ApplicationParameter> getApplicationParameter(AONContext ctx, Domain domain, AppParam param ) {
			return ctx.getDslContext()
				.select(APP_PARAM.ID,APP_PARAM.DOMAIN,APP_PARAM.NAME,APP_PARAM.VALUE)
				.from(APP_PARAM)
				.where(APP_PARAM.DOMAIN.eq(domain.getId()))
					.and(APP_PARAM.NAME.eq(param.toString()))
				.fetch()
				.stream()
				.map( rec -> new ApplicationParameter()
						.setId(rec.getValue(APP_PARAM.ID))
						.setDomain(rec.getValue(APP_PARAM.DOMAIN))
						.setName(param)
						.setValue(rec.getValue(APP_PARAM.VALUE)) )
				.findFirst();
			 
		}
	}
	
	public static Configuration getConfiguration(AONContext ctx, Domain domain, ConfigurationBuilderFactory factory) {
		ctx.checkRead();
		return factory.create( new ConfigurationBuilderDAO(ctx,domain) ).build();
		
	}

	public static Configuration getConfiguration(AONContext ctx, ConfigurationBuilderFactory factory) {
		Optional<Domain> optDomain = DomainDAO.get(ctx, ctx.getDomainName());
		if (optDomain.isPresent()) {
			return getConfiguration( ctx,optDomain.get(), factory);	
		}
		throw new AonCoreException(AonError.DOMAIN_NOT_FOUND.format( ctx.getDomainName()) );
	}
	
}

package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.accounting.Account;
import net.aonsolutions.occam.api.config.AccountingConfiguration;
import net.aonsolutions.occam.api.config.ApplicationParameter;
import net.aonsolutions.occam.api.config.Configuration;
import net.aonsolutions.occam.api.constants.AppParam;
import net.aonsolutions.occam.api.filter.ConfigurationFacade.CompositeConfigurationBuilder;
import net.aonsolutions.occam.api.filter.ConfigurationFacade.ConfigurationBuilder;
import net.aonsolutions.occam.api.filter.ConfigurationFacade.ConfigurationBuilderFactory;
import net.aonsolutions.watson.client.util.AonNumberUtils;

public class ConfigurationDAO {
	private ConfigurationDAO() {
	}
	
	private static class ConfigurationBuilderDAO extends  CompositeConfigurationBuilder<Configuration> {
		
		private final Configuration conf;
		public ConfigurationBuilderDAO( AONContext ctx ) {
			conf = new Configuration();
			addBuilder(new AccountingBuilder(ctx, conf));
		}
		
		@Override
		public Configuration build() {
			return conf;
		}
	}

	private static class AccountingBuilder implements ConfigurationBuilder<Configuration> {
		private final Configuration conf;
		private final AONContext ctx;
		public AccountingBuilder(final AONContext ctx, final Configuration conf) {
			this.conf = conf;
			this.ctx = ctx;
		}
		
		@Override
		public Configuration build() {
			return null;
		}
		
		@Override
		public ConfigurationBuilder<Configuration> withAccounting() {
			conf.setAccounting(new AccountingConfiguration());
			fillAccountingParameters(ctx);
			return null;
		}
		
		private void fillAccountingParameters(AONContext ctx) {
			Pattern pattern = Pattern.compile(AppParam.ACCOUNT_PATTERN);
			Arrays.stream( AppParam.values() )
				.filter(p -> pattern.matcher(p.toString()).find())
				.forEach( appParam -> {
					Optional<ApplicationParameter> optAppParam =  getApplicationParameter(ctx,appParam);
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
		
	}

	public static Configuration getConfiguration(final AONContext ctx, ConfigurationBuilderFactory factory) {
		ctx.checkRead();
		factory.create( new ConfigurationBuilderDAO(ctx) ).build();
		return factory.create( new ConfigurationBuilderDAO(ctx) ).build();
	}
	
	public static Optional<ApplicationParameter> getApplicationParameter(AONContext ctx, AppParam param ) {
		return ctx.getDslContext()
			.select(APP_PARAM.ID,APP_PARAM.DOMAIN,APP_PARAM.NAME,APP_PARAM.VALUE)
			.from(APP_PARAM)
			.innerJoin(DOMAIN).on(APP_PARAM.DOMAIN.eq(DOMAIN.ID))
			.where(DOMAIN.NAME.eq(ctx.getDomainName()))
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

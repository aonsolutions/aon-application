package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.accounting.Account;
import net.aonsolutions.occam.api.config.AccountingConfiguration;
import net.aonsolutions.occam.api.config.ApplicationParameter;
import net.aonsolutions.occam.api.config.Configuration;
import net.aonsolutions.occam.api.constants.AppParam;
import net.aonsolutions.watson.client.util.AonNumberUtils;

public class ConfigurationDAO {
	private ConfigurationDAO() {
	}

	public static Configuration getConfiguration(final AONContext ctx, Integer domainId) {
		ctx.checkRead();
		return getBasicConfiguration(ctx, domainId);
	}
	
	private static Configuration getBasicConfiguration(AONContext ctx, Integer domainId) {
		Configuration conf = new Configuration();
		fillAccountingParameters(ctx, conf, domainId);
		return conf;
	}
	
	private static void fillAccountingParameters(AONContext ctx, final Configuration config, Integer domainId) {
		if (!config.accounting().isPresent()) {
			config.setAccounting(new AccountingConfiguration());
		}
		Pattern pattern = Pattern.compile(AppParam.ACCOUNT_PATTERN);
		Arrays.stream( AppParam.values() )
			.filter(p -> pattern.matcher(p.toString()).find())
			.forEach( appParam -> {
				System.out.println( appParam );
				Optional<ApplicationParameter> optAppParam =  getApplicationParameter(ctx,domainId,appParam);
				if (optAppParam.isPresent()) {
					Integer accountId = AonNumberUtils.toInteger( optAppParam.get().getValue() );
					System.out.println( "\t"  + accountId );
					if (accountId != null) {
						Optional<Account> optAccount = getAccount(ctx,domainId,accountId);
						if (optAccount.isPresent()) {
							System.out.println( "\t\t"  + optAccount.get().getCode() );
							config.accounting().get().setAccount(appParam, optAccount.get());
						}
					}
				}
			});
	}
	
	private static Optional<ApplicationParameter> getApplicationParameter(AONContext ctx, Integer domainId, AppParam param ) {
		return ctx.getDslContext()
			.select(APP_PARAM.ID,APP_PARAM.DOMAIN,APP_PARAM.NAME,APP_PARAM.VALUE)
			.from(APP_PARAM)
			.where(APP_PARAM.DOMAIN.eq(domainId)
				.and(APP_PARAM.NAME.eq(param.toString())))
			.fetch()
			.stream()
			.map( rec -> new ApplicationParameter()
					.setId(rec.getValue(APP_PARAM.ID))
					.setDomain(rec.getValue(APP_PARAM.DOMAIN))
					.setName(rec.getValue(APP_PARAM.NAME))
					.setValue(rec.getValue(APP_PARAM.VALUE)) )
			.findFirst();
		 
	}
	private static Optional<Account> getAccount(AONContext ctx, Integer domainId, Integer accountId ) {
		return AccountDAO.get(ctx, f -> f.withId().eq(accountId), b -> b);
	}
}

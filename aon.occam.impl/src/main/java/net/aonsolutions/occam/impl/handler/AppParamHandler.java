package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;

import java.util.EnumMap;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.watson.error.AonCoreException;

import net.aonsolutions.occam.api.model.ApplicationParameter;
import net.aonsolutions.occam.api.model.ApplicationParameters;
import net.aonsolutions.occam.api.model.type.AppParam;
import net.aonsolutions.occam.impl.AONContext;

class AppParamHandler {
	
	private AppParamHandler() {
		
	}
	
	static class ApplicationParameterFiller extends Filler<ApplicationParameter> {

		@Override
		public ApplicationParameter apply(Record r) {
			return new ApplicationParameter()
				.setId(getValue(r, APP_PARAM.ID))
				.setDomain(getValue(r, APP_PARAM.DOMAIN))
				.setParam(AppParam.value( getValue(r, APP_PARAM.NAME)).orElse(null))
				.setValue(getValue(r, APP_PARAM.VALUE))
			;
		}
		
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, int domain) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select()
			.from(APP_PARAM)
			.where(APP_PARAM.DOMAIN.eq(domain))
		;
	}
	
	static Optional<ApplicationParameter> get(AONContext ctx, int domain, AppParam param) {
		return select( ctx, domain)
			.and(APP_PARAM.NAME.eq(param.name()))
			.fetch()
			.stream()
			.map( new ApplicationParameterFiller() )
			.findFirst()
		;
	}

	static ApplicationParameters getApplicationParameters(AONContext ctx, Integer domain) {
		return new ApplicationParameters()
			.setParams(
				select( ctx, domain)
					.fetch()
					.stream()
					.map( new ApplicationParameterFiller() )
					.collect(Collectors.toMap(
						ap -> ap.getParam(),
		                ap -> ap,
		                (l, r) -> l, // No debería sucedre. Se asume el primero.
		                () -> new EnumMap<>(AppParam.class)))			
		);
	}

	static ApplicationParameter save(AONContext ctx, ApplicationParameter param){
		get(ctx, param.getDomain(), param.getParam() )
			.ifPresentOrElse(
					a -> ctx.getDslContext()
						.update(APP_PARAM)
						.set(APP_PARAM.VALUE, param.getValue())
						.where(APP_PARAM.ID.eq(a.getId()))
						.execute()
					,
					() -> ctx.getDslContext()
						.insertInto(APP_PARAM)
						.set(APP_PARAM.DOMAIN, param.getDomain() )
						.set(APP_PARAM.NAME, AppParam.value(param.getParam()) )
						.set(APP_PARAM.VALUE, param.getValue())
						.execute() 
				);
		return get(ctx, param.getDomain(), param.getParam())
			.orElseThrow( () -> new AonCoreException("No se pudo recuperar el dato"));
	}
	
	
}

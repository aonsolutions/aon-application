package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Filter.ApplicationParameterFilter;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ApplicationParameterPropertiesDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AppParamDAO {
	
	private static final String DATE_PATTERN = "dd/MM/yyyy";
	private static final String[] DATE_PATTERNS = new String[]{DATE_PATTERN}; 
	private static final ApplicationParameterPropertiesDAO APPLICATION_PARAMETER_PROPERTIES = new ApplicationParameterPropertiesDAO();
	
	private AppParamDAO() {
		
	}
	
	// **********************************************************************
	// ************************************************************ [READ] **
	// **********************************************************************
	private static SelectJoinStep<Record> select(AONContext ctx) {
		return ctx.getDslContext().select()
				.from(APP_PARAM);
	}
	public static Optional<ApplicationParameter> get(AONContext ctx, Integer domainId, AppParam param) {
		if (param == null) return Optional.empty();
		return get(ctx, domainId, param.name());
	}
	public static Optional<ApplicationParameter> get(AONContext ctx, Integer domainId, String name) {
		return select(ctx)
			.where(APP_PARAM.DOMAIN.eq(domainId))
			.and( APP_PARAM.NAME.eq(name))
			.fetch().stream()
			.map( new ApplicationParameterFiller() )
			.findFirst();
	}
	public static Stream<ApplicationParameter> getByPattern(AONContext ctx, Integer domainId, String pattern) {
		return select(ctx)
			.where(APP_PARAM.DOMAIN.eq(domainId))
			.and( APP_PARAM.NAME.like(pattern))
			.fetch().stream()
			.map( new ApplicationParameterFiller() );
	}
	
	public static Optional<Integer> getInteger(AONContext ctx, Integer domainId, AppParam param) {
		return get(ctx, domainId, param).map(p -> AonNumberUtils.toInteger(p.getValue()));
	}
	public static boolean getBoolean(AONContext ctx, Integer domainId, AppParam param) {
		return get(ctx, domainId, param)
			.map(p -> p.trueValue())
			.orElse(false)
		;
	}

	
	
	// **********************************************************************
	// *********************************************************** [WRITE] **
	// **********************************************************************
	public static ApplicationParameter save(AONContext ctx, Integer domainId, AppParam param, String paramValue){
		if (param == null) new AonCoreException("Param is empty");
		return save(ctx, domainId, param.name(), paramValue);
	}
	public static ApplicationParameter save(AONContext ctx, Integer domainId, String paramName, String paramValue){
		ctx.checkWrite();
		ctx.getDslContext()
			.select()
			.from(APP_PARAM)
			.where(APP_PARAM.DOMAIN.eq(domainId))
			.and(APP_PARAM.NAME.eq(paramName))
			.fetch()
			.stream()
			.findFirst()
			.ifPresentOrElse(
				p -> {
					ctx.getDslContext()
						.update(APP_PARAM)
							.set(APP_PARAM.VALUE, paramValue)
						.where(APP_PARAM.DOMAIN.eq(domainId))
						.and(APP_PARAM.NAME.eq(paramName))
						.execute();
				}
				, () -> {
					ctx.getDslContext()
						.insertInto(APP_PARAM)
						.set(APP_PARAM.DOMAIN, domainId)
						.set(APP_PARAM.NAME, paramName)
						.set(APP_PARAM.VALUE , paramValue)
					.execute();
				});
		return get(ctx, domainId, paramName)
			.orElseThrow(() -> new AonCoreException("Param not saved or not found"));
	}
	
	// **********************************************************************
	// ********************************************************** [FILLER] **
	// **********************************************************************
	private static class ApplicationParameterFiller extends Filler implements Function<Record, ApplicationParameter> {

		@Override
		public ApplicationParameter apply(Record r) {
			return build(r);
		}
		
		public static ApplicationParameter build(Record r) {
			return new ApplicationParameter()
				.setId(getValue(r, APP_PARAM.ID))
				.setDomain(getValue(r, APP_PARAM.DOMAIN))
				.setName(getValue(r, APP_PARAM.NAME))
				.setValue(getValue(r, APP_PARAM.VALUE))
			;
		}
	}


	// **********************************************************************
	// ************************************************************* [OLD] **
	// **********************************************************************
	public static String fetchValue(AONContext ctx, AppParam param) {
		ApplicationParameter ap = fetchOne(ctx, param.getValue());
		return ap == null ? null : ap.getValue(); 
	}
	public static double fetchDoubleValue(AONContext ctx, AppParam param) {
		ApplicationParameter ap = fetchOne(ctx, param.getValue());
		return ap == null ? 0.0 : AonNumberUtils.todouble( ap.getValue()); 
	}
	public static int fetchIntValue(AONContext ctx, AppParam param) {
		ApplicationParameter ap = fetchOne(ctx, param.getValue());
		return ap == null ? 0 : AonNumberUtils.toint( ap.getValue()); 
	}
	public static Date fetchDateValue(AONContext ctx, AppParam param) {
		ApplicationParameter ap = fetchOne(ctx, param.getValue());
		if ( ap != null && AonStringUtils.isNotBlank(ap.getValue())) {
			try {
				return AonDateUtils.parseDateStrictly(ap.getValue(), DATE_PATTERNS);
			} catch ( ParseException e ) {
				ctx.log().error( e.getMessage() );
			}
		}
		return null;
	}
	
	public static Stream<ApplicationParameter> getApplicationParameterStream(AONContext ctx, ApplicationParameterFilter filter) {
		return APPLICATION_PARAMETER_PROPERTIES.build(ctx.getDslContext()
				.select()
				.from(APP_PARAM), filter)
				.fetch().stream().map(r -> new ApplicationParameter()
						.setDomain(r.getValue(APP_PARAM.DOMAIN))
						.setId(r.getValue(APP_PARAM.ID))
						.setName(r.getValue(APP_PARAM.NAME))
						.setValue(r.getValue(APP_PARAM.VALUE)));
	}
	
	public static List<ApplicationParameter> getApplicationParameters(AONContext ctx, Condition condition) {
		return ctx.getDslContext().selectFrom(APP_PARAM)
				.where(condition)
				.fetch().stream().map(r -> new ApplicationParameter()
						.setDomain(r.getValue(APP_PARAM.DOMAIN))
						.setId(r.getValue(APP_PARAM.ID))
						.setName(r.getValue(APP_PARAM.NAME))
						.setValue(r.getValue(APP_PARAM.VALUE))).collect(Collectors.toList());
	}

	public static void delete(AONContext ctx, Integer domainId, AppParam param) {
		ctx.getDslContext()
			.delete(APP_PARAM)
			.where(APP_PARAM.DOMAIN.eq(domainId))
			.and(APP_PARAM.NAME.eq(param.getValue()))
			.execute();
	}
	
	public static void deleteApplicationParameter(AONContext ctx, ApplicationParameterFilter filter) {
		ctx.getDslContext()
			.delete(APP_PARAM)
			.where(APPLICATION_PARAMETER_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	
	public static ApplicationParameter fetchOne(AONContext ctx, AppParam param) {
		return fetchOne(ctx, param.getValue());
	}
	
	public static ApplicationParameter fetchOne(AONContext ctx, String param) {
		ctx.checkRead();
		final ApplicationParameter ap = new ApplicationParameter();
		ctx.getDslContext()
			.select(APP_PARAM.ID,APP_PARAM.DOMAIN,APP_PARAM.NAME,APP_PARAM.VALUE)
			.from(APP_PARAM)
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())
				.and(APP_PARAM.NAME.eq(param)))
			.fetch()
			.stream()
			.findFirst()
			.ifPresent( record  -> ap
					.setId(record.getValue(APP_PARAM.ID))
					.setDomain(record.getValue(APP_PARAM.DOMAIN))
					.setName(record.getValue(APP_PARAM.NAME))
					.setValue(record.getValue(APP_PARAM.VALUE)) );
			;
		return ap.getId() != null ? ap : null;
	}
	
	public static ApplicationParameter fetchOne(AONContext ctx, Integer id) {
		ctx.checkRead();
		final ApplicationParameter ap = new ApplicationParameter();
		ctx.getDslContext()
			.select(APP_PARAM.ID,APP_PARAM.DOMAIN,APP_PARAM.NAME,APP_PARAM.VALUE)
			.from(APP_PARAM)
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId())
			.and(APP_PARAM.ID.eq(id)))
			.fetch()
			.stream()
			.findFirst()
			.ifPresent( record  -> ap
					.setId(record.getValue(APP_PARAM.ID))
					.setDomain(record.getValue(APP_PARAM.DOMAIN))
					.setName(record.getValue(APP_PARAM.NAME))
					.setValue(record.getValue(APP_PARAM.VALUE)) );
			;
		return ap.getId() != null ? ap : null;
	}
	

	public static Administration parseDefaultAdministration(String paramValue) {
		if (AonStringUtils.isNotBlank(paramValue)) {
			try {
				return Administration.values()[Integer.parseInt(paramValue)]; 
			} catch (NumberFormatException e) {
			} catch (IndexOutOfBoundsException e) {
			}
		}
		return null;
	}
	
	public static IRPFRegime getDefaultIRPFRegime(AONContext ctx) {
		ApplicationParameter ap  = AppParamDAO.fetchOne(ctx, AppParam.FS_TAX_REGIME.getValue());
		if (ap == null || AonStringUtils.isBlank(ap.getValue())) return null;
		int i = AonNumberUtils.toInteger( ap.getValue() );
		return IRPFRegime.safeValueOf(i);
	}
	public static boolean isPermAddressChanges(AONContext ctx) {
		ApplicationParameter ap  = AppParamDAO.fetchOne(ctx, AppParam.FS_PERM_ADDRESS_CHANGES.getValue());
		if (ap == null || AonStringUtils.isBlank(ap.getValue())) return false;
		return (AonNumberUtils.toInteger( ap.getValue() )==1);
	}
	
	public static ApplicationParameter insertApplicationParameter(AONContext ctx, String param, String value){
		if(ctx.getDslContext()
			.select()
			.from(APP_PARAM)
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
			.and(APP_PARAM.NAME.eq(param))
			.fetch()
			.isEmpty())
			ctx.getDslContext()
				.insertInto(APP_PARAM, APP_PARAM.DOMAIN, APP_PARAM.NAME, APP_PARAM.VALUE)
				.values(ctx.getDomainId(), param, value).execute();
		else ctx.getDslContext()
				.update(APP_PARAM)
				.set(APP_PARAM.VALUE, value)
				.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
				.and(APP_PARAM.NAME.eq(param)).execute();
		return fetchOne(ctx, param);
	}
	
	public static ApplicationParameter insertApplicationParameter(AONContext ctx, ApplicationParameter applicationParameter) {
		return ctx.getDslContext()
			.insertInto(
					APP_PARAM, 
					APP_PARAM.DOMAIN, 
					APP_PARAM.NAME, 
					APP_PARAM.VALUE)
			.values(
					null != applicationParameter.getDomain() ? applicationParameter.getDomain() : ctx.getDomainId(), 
					applicationParameter.getName(), 
					applicationParameter.getValue())
			.returning().fetch().stream().map(new ApplicationParameterFiller()).findFirst().orElse(new ApplicationParameter());
	}
	
	public static ApplicationParameter updateApplicationParameter(AONContext ctx, ApplicationParameter applicationParameter, ApplicationParameterFilter filter) {
		return ctx.getDslContext()
			.update(APP_PARAM)
			.set(APP_PARAM.DOMAIN, applicationParameter.getDomain())
			.set(APP_PARAM.NAME, applicationParameter.getName())
			.set(APP_PARAM.VALUE, applicationParameter.getValue())
			.where(APPLICATION_PARAMETER_PROPERTIES.getConditions(filter))
			.returning().fetch().stream().map(new ApplicationParameterFiller()).findFirst().orElse(new ApplicationParameter());
	}
	
	
	public static ApplicationParameter saveApplicationParameter(AONContext ctx, ApplicationParameter ap) {
		return insertApplicationParameter(ctx, ap.getName(),ap.getValue());
	}
	
}

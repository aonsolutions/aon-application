package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;

import java.sql.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Cnae;
import com.esferalia.aon.occam.api.model.Cnae2009;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseActivityFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Iae;
import com.esferalia.aon.occam.api.model.Properties.EnterpriseActivityProperties;
import com.esferalia.aon.occam.api.model.finance.VATExemptionCause;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO.IaeFiller;

public class ActivityDAO {
	
	// -------------------------------------- Constructor

	private ActivityDAO() {
		throw new IllegalStateException("Utility Class");
	}
	
	// -------------------------------------- Enterprise Properties
	
	private static final ActivityPropertiesDAO ENTERPRISE_ACTIVITY_PROPERTIES = new ActivityPropertiesDAO();
	protected static class ActivityPropertiesDAO implements EnterpriseActivityProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, EnterpriseActivityFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(EnterpriseActivityFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.DESCRIPTION);}
		@Override public Property<Integer> getEnterpriseProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.ENTERPRISE);}
		@Override public Property<Integer> getIaeProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.IAE);}
		@Override public Property<Integer> getCnaeProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.CNAE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.TYPE);}
		@Override public Property<Integer> getCnae2009Property() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.CNAE2009);}
		@Override public Property<Byte> getSurchargeProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.SURCHARGE);}
		@Override public Property<Integer> getVatTaxProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.VAT_TAX);}
		@Override public Property<Integer> getRetentionTaxProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.RETENTION_TAX);}
		@Override public Property<Byte> getVatRegimeProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.VAT_REGIME);}
		@Override public Property<Byte> getRetentionRegimeProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.RETENTION_REGIME);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.START_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.END_DATE);}
		@Override public Property<Double> getProrataProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.PRORATA);}
		@Override public Property<Byte> getProrataTypeProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.PRORATA_TYPE);}
		@Override public Property<Byte> getPrincipalProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_ACTIVITY.PRINCIPAL);}

	}
	
	// -------------------------------------- Enterprise Filler
	
	public static class EnterpriseActivityFiller extends Filler implements Function<Record, Activity> {

		@Override
		public Activity apply(Record r) {
			Activity activity = (Activity) new Activity()
					.setId(r.getValue(ENTERPRISE_ACTIVITY.ID) )
					.setDescription(r.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION) )
					.setPrincipal(getBoolean(r, ENTERPRISE_ACTIVITY.PRINCIPAL))
					.setIae(checkField(r, IAE.ID)
							? IaeFiller.build(r)
							: new Iae().setId(r.getValue(ENTERPRISE_ACTIVITY.IAE)))
					.setCnae(getValue(r, ENTERPRISE_ACTIVITY.CNAE2009) )
					.setCnaeCode(getValue(r, CNAE2009.CODE))
					.setCnaeDescription(getValue(r, CNAE2009.TITLE) )
					.setVatRegime(VATRegime.safeValueOf(getValue(r, ENTERPRISE_ACTIVITY.VAT_REGIME)))
					.setVatExemptionCause(VATExemptionCause.safeValueOf(getValue(r, ENTERPRISE_ACTIVITY.VAT_EXEMPTION_CAUSE)));
			
			activity.setDomain(getValue(r, ENTERPRISE_ACTIVITY.DOMAIN));
			activity.setEnterprise(getValue(r, ENTERPRISE_ACTIVITY.ENTERPRISE));
			activity.setStartDate(r.getValue(ENTERPRISE_ACTIVITY.START_DATE));
			activity.setEndDate(r.getValue(ENTERPRISE_ACTIVITY.END_DATE));
			
			return activity;
		}

	}
	
	// -------------------------------------- CRUD Methods
	
	public static List<Activity> getList(AONContext ctx, EnterpriseActivityFilter filter) {
		ctx.checkRead();
		
		List<Activity> activities = ctx.getDslContext()
				.select()
				.from(ENTERPRISE_ACTIVITY)
				.leftOuterJoin(CNAE2009).on(CNAE2009.ID.eq(ENTERPRISE_ACTIVITY.CNAE2009))
				.leftOuterJoin(IAE).on(IAE.ID.eq(ENTERPRISE_ACTIVITY.IAE))
				.where(ENTERPRISE_ACTIVITY_PROPERTIES.getConditions(filter))
				.fetch()
				.stream()
				.map(new EnterpriseActivityFiller())
				.collect(Collectors.toList());
		
		activities.forEach(activity -> {
			List<EnterpriseCCC> cccs = EnterpriseCCCDAO.getList(ctx, f -> f.getEnterpriseActivityProperty().eq(activity.getId()));
			activity.setCccs(cccs);
		});
		
		return activities;
	}
	
	public static Activity get(AONContext ctx, EnterpriseActivityFilter filter) {
		ctx.checkRead();
		
		Activity activity = ctx.getDslContext()
				.select()
				.from(ENTERPRISE_ACTIVITY)
				.leftOuterJoin(CNAE2009).on(CNAE2009.ID.eq(ENTERPRISE_ACTIVITY.CNAE2009))
				.leftOuterJoin(IAE).on(IAE.ID.eq(ENTERPRISE_ACTIVITY.IAE))
				.where(ENTERPRISE_ACTIVITY_PROPERTIES.getConditions(filter))
				.fetch()
				.stream()
				.map(new EnterpriseActivityFiller())
				.findFirst()
				.orElse(new Activity());
		
		List<EnterpriseCCC> cccs = EnterpriseCCCDAO.getList(ctx, f -> f.getEnterpriseActivityProperty().eq(activity.getId()));
		activity.setCccs(cccs);
		
		printEnterpriseActivity(activity);
		
		return activity;
	}
	
	public static void saveList(AONContext ctx, List<Activity> activities) {
		activities.forEach(activity -> {
			if(activity.getId() != null && activity.getId() > 0) update(ctx, activity); 
			else insert(ctx, activity);
		});
	}
	
	public static Activity save(AONContext ctx, Activity activity) {
		return activity.getId() != null && activity.getId() > 0  ? update(ctx, activity) : insert(ctx, activity);
	}
	
	private static Activity insert(AONContext ctx, Activity activity) {
		ctx.checkWrite();
		printEnterpriseActivity(activity);
		
		Cnae2009 cnae2009 = Cnae2009DAO.get(ctx, f -> f.getIdProperty().eq(activity.getCnae()));
		Cnae cnae = CnaeDAO.get(ctx, f -> f.getIdProperty().eq(activity.getCnae()));
		
		Integer enterpriseId = activity.getEnterprise();
		if(null == enterpriseId)
			enterpriseId = EnterpriseDAO.get(ctx, f -> f.getDomainProperty().eq(activity.getDomain())).getId();
		
		Integer id = ctx.getDslContext().insertInto(ENTERPRISE_ACTIVITY)
			.set(ENTERPRISE_ACTIVITY.DOMAIN, activity.getDomain())
			.set(ENTERPRISE_ACTIVITY.ENTERPRISE, enterpriseId)
			.set(ENTERPRISE_ACTIVITY.DESCRIPTION, activity.getDescription())
			.set(ENTERPRISE_ACTIVITY.TYPE, (byte)0)
			.set(ENTERPRISE_ACTIVITY.CNAE, null == cnae ? null : cnae.getId())
			.set(ENTERPRISE_ACTIVITY.CNAE2009, null == cnae2009 ? null : cnae2009.getId())
			.set(ENTERPRISE_ACTIVITY.START_DATE, parseToSqlDate(activity.getStartDate()))
			.set(ENTERPRISE_ACTIVITY.END_DATE, parseToSqlDate(activity.getEndDate()))
			.set(ENTERPRISE_ACTIVITY.PRINCIPAL, activity.isPrincipal() ? (byte)1 : (byte)0)
			.set(ENTERPRISE_ACTIVITY.VAT_REGIME, activity.getVatRegime().value())
			.set(ENTERPRISE_ACTIVITY.VAT_EXEMPTION_CAUSE, activity.getVatExemptionCause().value())
			.returning(ENTERPRISE_ACTIVITY.ID).fetchOne().getId();
			ctx.log().debug("INSERT ENTERPRISE ACTIVITY id: " + id);	
		
		activity.getCccs().forEach(ccc -> ccc.setEnterpriseActivity(id));
		EnterpriseCCCDAO.save(ctx, activity.getCccs());
		
		return (Activity) activity.setId(id);
	}

	private static Activity update(AONContext ctx, Activity activity) {
		ctx.checkWrite();
		printEnterpriseActivity(activity);
		
		Cnae2009 cnae2009 = Cnae2009DAO.get(ctx, f -> f.getIdProperty().eq(activity.getCnae()));
		Cnae cnae = CnaeDAO.get(ctx, f -> f.getIdProperty().eq(activity.getCnae()));
		
		ctx.getDslContext()
			.update(ENTERPRISE_ACTIVITY)
			.set(ENTERPRISE_ACTIVITY.DESCRIPTION, activity.getDescription())
			.set(ENTERPRISE_ACTIVITY.CNAE, null == cnae ? null : cnae.getId())
			.set(ENTERPRISE_ACTIVITY.CNAE2009, null == cnae2009 ? null : cnae2009.getId())
			.set(ENTERPRISE_ACTIVITY.START_DATE, parseToSqlDate(activity.getStartDate()))
			.set(ENTERPRISE_ACTIVITY.END_DATE, parseToSqlDate(activity.getEndDate()))
			.set(ENTERPRISE_ACTIVITY.PRINCIPAL, activity.isPrincipal() ? (byte)1 : (byte)0)
			.set(ENTERPRISE_ACTIVITY.VAT_REGIME, activity.getVatRegime().value())
			.set(ENTERPRISE_ACTIVITY.VAT_EXEMPTION_CAUSE, activity.getVatExemptionCause().value())
			.where(ENTERPRISE_ACTIVITY.ID.eq(activity.getId()))
			.execute();		
		ctx.log().debug("UPDATE ENTERPRISE ACTIVITY id: " + activity.getId());	
		
		EnterpriseCCCDAO.save(ctx, activity.getCccs());
		
		return activity;
	}
	
	private static Date parseToSqlDate(java.util.Date date){
		if(null == date) return null;
		return new Date(date.getTime());
	}
	
	private static void printEnterpriseActivity(Activity activity) {
		System.out.println("------- Activity : " + activity.getDescription() + " -------");
		System.out.println("Id: " + activity.getId() + "\nDomain: " + activity.getDomain() + "\nDescription: " + activity.getDescription());
		System.out.println("Cnae: " + activity.getCnae() + "\nCnae2009Code: " + activity.getCnaeCode() + "\nCnae2009Description: " + activity.getCnaeDescription());
		System.out.println("StartDate: " + activity.getStartDate() + "\nEndDate: " + activity.getEndDate() + "\nisPricipal: " + activity.isPrincipal());	
		
		if(!activity.getCccs().isEmpty()) {
			System.out.println("------- CCCs -------");
			activity.getCccs().forEach(ccc -> {
				System.out.println("Id : " + ccc.getId() + "\nDomain : " + ccc.getDomain() + "\nActivity : " + ccc.getEnterpriseActivity() + "\nType : " + ccc.getType());
				System.out.println("Account : " + ccc.getCcc() + "\nGeozone : " + ccc.getGeozone() + "\nGeozoneCode : " + ccc.getGeozoneCode() + "\nGeozoneDescrption : " + ccc.getGeozoneDescription());
			});
		}
	}
}

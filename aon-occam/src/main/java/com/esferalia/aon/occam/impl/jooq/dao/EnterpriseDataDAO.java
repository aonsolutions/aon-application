package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.payroll.Enterprise;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class EnterpriseDataDAO {

	private EnterpriseDataDAO() {
	}

	// ---------------------------------------------------------------
	// -------------------------------------------------------- [READ]
	// ---------------------------------------------------------------
	private static SelectJoinStep<Record> select(AONContext ctx){	
		return ctx.getDslContext()
			.select()
			.from(ENTERPRISE_DATA)
		;
	}
	public static Stream<EnterpriseData> streamByDomain(AONContext ctx, Integer domainId){	
		if (domainId == null ) return Stream.empty();
		return select(ctx)
			.where(ENTERPRISE_DATA.DOMAIN.eq(domainId))
			.fetch()
			.stream()
			.map( r -> new EnterpriseDataFiller<>().apply(r, EnterpriseData::new) );
	}
	public static Stream<EnterpriseData> streamByEnterprise(AONContext ctx, Integer enterpriseId){
		if (enterpriseId == null ) return Stream.empty();
		return select(ctx)
			.where(ENTERPRISE_DATA.ENTERPRISE.eq(enterpriseId))
			.fetch()
			.stream()
			.map( r -> new EnterpriseDataFiller<>().apply(r, EnterpriseData::new) );
	}
	
	public static Optional<EnterpriseData> get(AONContext ctx, Integer domainId, Integer id){
		if (domainId == null || id == null) return Optional.empty();
		return select(ctx)
			.where(ENTERPRISE_DATA.DOMAIN.eq(domainId))
			.and( ENTERPRISE_DATA.ID.eq( id ))
			.fetch()
			.stream()
			.map( r -> new EnterpriseDataFiller<>().apply(r, EnterpriseData::new) )
			.findFirst();
	}
	
	public static Optional<EnterpriseData> get(AONContext ctx, Integer domainId, EnterpriseDataNames name){
		if (domainId == null || name == null) return Optional.empty();
		return select(ctx)
			.where(ENTERPRISE_DATA.DOMAIN.eq(domainId))
			.and( ENTERPRISE_DATA.NAME.eq( name.name()))
			.fetch()
			.stream()
			.map( r -> new EnterpriseDataFiller<>().apply(r, EnterpriseData::new) )
			.findFirst();
	}
	
	// ---------------------------------------------------------------
	// ------------------------------------------------------- [WRITE]
	// ---------------------------------------------------------------
	public static void save(AONContext ctx, List<EnterpriseData> datas){
		AonCollectionUtils.stream( datas )
			.forEach( data -> save(ctx, data) );
	}
	
	public static EnterpriseData save(AONContext ctx, EnterpriseData enterpriseData){
		if (enterpriseData.getId() != null && enterpriseData.isDeleted()) {
			delete(ctx, enterpriseData.getId());
			return enterpriseData;
		} else {
			EnterprseDataAutoComplete.complete(ctx, ctx.getDomainId(), enterpriseData);
			return enterpriseData.getId() == null 
				? insert(ctx, enterpriseData)
				: update(ctx, enterpriseData);		
		}
	}
	
	public static EnterpriseData updateEndDate(AONContext ctx, EnterpriseData cc, Date endDate) {
		return save(ctx, setEndDate(cc, endDate));
	}
	
	public static <T extends EnterpriseData> T setEndDate(T cc, Date endDate) {
		Date startDate = cc.getStartDate();
		cc.setEndDate( endDate );
		cc.setDeleted( !AonDateUtils.isAfter( endDate, startDate) );		
		System.out.println( "Closing data " + cc.getDataName() + " with end date " + endDate + " (start date: " + startDate + ", deleted: " + cc.isDeleted() + ")" );
		return cc;
	}
	
	
	private static EnterpriseData insert(AONContext ctx, EnterpriseData enterpriseData) {
		ctx.checkWrite();
		EnterprseDataValidation.insert(ctx, enterpriseData);
		Integer id = ctx.getDslContext().insertInto(ENTERPRISE_DATA)
				.set(ENTERPRISE_DATA.DOMAIN, enterpriseData.getDomain())
				.set(ENTERPRISE_DATA.ENTERPRISE, enterpriseData.getEnterprise())
				.set(ENTERPRISE_DATA.NAME, enterpriseData.getName())
				.set(ENTERPRISE_DATA.EXPRESSION, enterpriseData.getExpression())
				.set(ENTERPRISE_DATA.START_DATE, AonDateUtils.toSql( enterpriseData.getStartDate()))
				.set(ENTERPRISE_DATA.END_DATE, AonDateUtils.toSql( enterpriseData.getEndDate()))
				.set(ENTERPRISE_DATA.CREATION_USER, ctx.getUser()) 
				.set(ENTERPRISE_DATA.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
				.set(ENTERPRISE_DATA.MODIFICATION_USER, ctx.getUser()) 
				.set(ENTERPRISE_DATA.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.returning(ENTERPRISE_DATA.ID)
			.fetchOne()
			.getId();
		enterpriseData.setId(id).setDirty(false);
		ctx.log().debug("INSERT ENTERPRISE_DATA id: " + id);		
		return enterpriseData;
	}
	
	private static EnterpriseData update(AONContext ctx, EnterpriseData enterpriseData) {
		ctx.checkWrite();
		if (enterpriseData.isNotDirty()) return enterpriseData;
		EnterprseDataValidation.update(ctx, enterpriseData);
		ctx.getDslContext().update(ENTERPRISE_DATA)
			.set(ENTERPRISE_DATA.DOMAIN, enterpriseData.getDomain())
			.set(ENTERPRISE_DATA.ENTERPRISE, enterpriseData.getEnterprise())
			.set(ENTERPRISE_DATA.NAME, enterpriseData.getName())
			.set(ENTERPRISE_DATA.EXPRESSION, enterpriseData.getExpression())
			.set(ENTERPRISE_DATA.START_DATE, AonDateUtils.toSql( enterpriseData.getStartDate()) )
			.set(ENTERPRISE_DATA.END_DATE, AonDateUtils.toSql( enterpriseData.getEndDate()) )
			.set(ENTERPRISE_DATA.MODIFICATION_USER,ctx.getUser())
			.set(ENTERPRISE_DATA.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
		.where(ENTERPRISE_DATA.ID.eq(enterpriseData.getId()))
		.execute();		
		ctx.log().debug("UPDATE ENTERPRISE_DATA id: " + enterpriseData.getId());		
		return enterpriseData.setDirty(false);
	}
	
	public static void delete(AONContext ctx, Integer id){
		ctx.getDslContext()
			.delete(ENTERPRISE_DATA)
			.where(ENTERPRISE_DATA.ID.eq(id))
			.execute();
		ctx.log().debug("DELETE ENTERPRISE_DATA id:" + id);
	}
	
	// ---------------------------------------------------------------
	// ------------------------------------------------------ [FILLER]
	// ---------------------------------------------------------------
	static class EnterpriseDataFiller<T extends EnterpriseData> extends Filler implements BiFunction<Record, Supplier<T>, T> {

		@Override
		public T apply(Record r, Supplier<T> supplier) {
			T t = supplier.get();
			t.setId(getValue(r,ENTERPRISE_DATA.ID));
			t.setDomain(getValue(r,ENTERPRISE_DATA.DOMAIN));
			t.setName(getValue(r,ENTERPRISE_DATA.NAME));
			t.setEnterprise(getValue(r,ENTERPRISE_DATA.ENTERPRISE));
			t.setExpression(getValue(r,ENTERPRISE_DATA.EXPRESSION));
			t.setStartDate(getValue(r,ENTERPRISE_DATA.START_DATE));
			t.setEndDate(getValue(r,ENTERPRISE_DATA.END_DATE));
			t.setCreationDate(r.getValue(ENTERPRISE_DATA.CREATION_DATE));
			t.setCreationUser(r.getValue(ENTERPRISE_DATA.CREATION_USER));
			t.setModificationDate(r.getValue(ENTERPRISE_DATA.MODIFICATION_DATE));
			t.setModificationUser(r.getValue(ENTERPRISE_DATA.MODIFICATION_USER));
			t.setDeleted(false);
			t.setDirty(false);
			return t;
		}
	}

	
	private static class EnterprseDataAutoComplete {

		private EnterprseDataAutoComplete() {
			
		}
		
		private static void complete(AONContext ctx, Integer domainId, EnterpriseData data) {
			if (data != null && data.getEnterprise() == null) {
				Company company = CompanyDAO.getByDomain(ctx, domainId);
				if (company != null) {
					Enterprise enterprise = EnterpriseDAO.get(ctx, f -> f.getDomainProperty().eq(domainId).and(f.getIdProperty().eq(company.getId())));
					if (enterprise != null) {
						data.setEnterprise( enterprise.getId() );
						if (data.getDomain() == null) {
							data.setDomain(enterprise.getDomain());
						}
					}
				}
			}
		}
	}
	
	private static class EnterprseDataValidation {
		private static final BiConsumer<AONContext,EnterpriseData> EMPTY_DOMAIN = (ctx, d) -> {
			if (d.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};

		private static final BiConsumer<AONContext,EnterpriseData> EMPTY_ENTERPRISE = (ctx, d) -> {
			if (d.getEnterprise() == null) 
				throw new AonCoreException(AonError.EMPTY_ENTERPRISE.getMessage());
		};

		private static final BiConsumer<AONContext,EnterpriseData> EMPTY_NAME = (ctx, d) -> {
			if (AonStringUtils.isBlank(d.getName())) 
				throw new AonCoreException(AonError.EMPTY_NAME.getMessage());
		};


		private static final BiConsumer<AONContext,EnterpriseData> EMPTY_START_DATE = (ctx, d) -> {
			if (!d.allowsNullStartDate() && d.getStartDate() == null)
				throw new AonCoreException(AonError.EMPTY_START_DATE.getMessage());
		};

		private static final BiConsumer<AONContext,EnterpriseData> WRONG_RANGE = (ctx,d) -> {
			if (AonDateUtils.isAfter( d.getStartDate(), d.getEndDate()))
				throw new AonCoreException(AonError.ACCOUNT_PERIOD_WRONG_RANGE.getMessage());
		};
		
		private static final BiConsumer<AONContext, EnterpriseData> OVERLAP = (AONContext ctx, EnterpriseData d) -> {
			if (d.allowsOverlap()) return;
		    java.sql.Date sqlStart = AonDateUtils.toSql( d.getStartDate());
		    java.sql.Date sqlEnd = AonDateUtils.toSql( d.getEndDate());
		    
		    Condition overlap =
            DSL.and(
                // existing.start <= new.end OR new.end IS NULL
                (sqlEnd == null)
                    ? DSL.trueCondition()
                    : ENTERPRISE_DATA.START_DATE.le(sqlEnd),

                // new.start <= existing.end OR existing.end IS NULL
                (sqlStart == null)
                    ? DSL.trueCondition()
                    : ENTERPRISE_DATA.END_DATE.ge(sqlStart).or(ENTERPRISE_DATA.END_DATE.isNull())
            );
		    
		    Condition notSelf  = (d.getId() == null) ? DSL.noCondition() : ENTERPRISE_DATA.ID.ne(d.getId());
		    Integer existingId = ctx.getDslContext()
	    		.select(ENTERPRISE_DATA.ID)
	    		.from(ENTERPRISE_DATA)
	    		.where(ENTERPRISE_DATA.DOMAIN.eq(d.getDomain()))
	    		.and(ENTERPRISE_DATA.NAME.eq(d.getName()))
		        .and(notSelf)
		        .and(overlap)
		        .limit(1)
		        .fetch()
		        .stream() 
		        .map( r -> r.getValue(ENTERPRISE_DATA.ID))
		        .findFirst()
		        .orElse(null);
		    if (existingId != null) {
		    	throw new AonCoreException(AonError.ACCOUNT_PERIOD_END_OVERLAP.format(d.getName()));
		    }
		};		
		
		private static void common(AONContext ctx, EnterpriseData enterpriseData) {
			EMPTY_DOMAIN
				.andThen( EMPTY_ENTERPRISE)
				.andThen( EMPTY_NAME)
				.andThen( EMPTY_START_DATE)
				.andThen( WRONG_RANGE ) 
				.andThen( OVERLAP )
				.accept(ctx, enterpriseData);
		}

		public static void insert(AONContext ctx, EnterpriseData enterpriseData) {
			common(ctx, enterpriseData);
		}
		public static void update(AONContext ctx, EnterpriseData enterpriseData) {
			common(ctx, enterpriseData);
		}
		
	}
	
}

package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseDataFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.EnterpriseDataProperties;

public class EnterpriseDataDAO {

	private EnterpriseDataDAO() {
		throw new IllegalStateException("Utility Class");
	}
	
	private static final EnterpriseDataPropertiesDAO ENTERPRISE_DATA_PROPERTIES = new EnterpriseDataPropertiesDAO();
	protected static class EnterpriseDataPropertiesDAO implements EnterpriseDataProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, EnterpriseDataFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(EnterpriseDataFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_DATA.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_DATA.DOMAIN);}
		@Override public Property<Integer> getEnterpriseProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_DATA.ENTERPRISE);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_DATA.NAME);}
		@Override public Property<String> getExpressionProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_DATA.EXPRESSION);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_DATA.START_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(ENTERPRISE_DATA.END_DATE);}
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, EnterpriseDataFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(ENTERPRISE_DATA)
				.where(ENTERPRISE_DATA_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<EnterpriseData> getStream(AONContext ctx, EnterpriseDataFilter filter){	
		return select(ctx, filter).fetch().stream().map(new EnterpriseDataFiller());
	}
	
	public static Stream<EnterpriseData> getStream(AONContext ctx, EnterpriseDataFilter filter, Integer page, Integer perPage){	
		return select(ctx, filter)
			.limit(perPage)
			.offset(perPage * (page -1))
			.fetch().stream().map(new EnterpriseDataFiller());
	}
	
	public static LinkedList<EnterpriseData> getList(AONContext ctx, EnterpriseDataFilter filter){	
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<EnterpriseData> getList(AONContext ctx, EnterpriseDataFilter filter, Integer page, Integer perPage){	
		return getStream(ctx, filter, page, perPage).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static EnterpriseData get(AONContext ctx, EnterpriseDataFilter filter) {
		ctx.checkRead();
		return select(ctx, filter).limit(1)
			.stream().map(new EnterpriseDataFiller())
			.findFirst().orElse(new EnterpriseData());
	}
	
	public static EnterpriseData save(AONContext ctx, EnterpriseData enterpriseData){
		if(enterpriseData.getId() != null && enterpriseData.isRemoved()) {
			delete(ctx, enterpriseData.getId());
			return enterpriseData;
		} else return enterpriseData.getId() == null 
			? insert(ctx, enterpriseData)
			: update(ctx, enterpriseData);		
	}
	
	public static void save(AONContext ctx, List<EnterpriseData> enterpriseData){
		for(EnterpriseData ctData: enterpriseData) {
			if(ctData.getId() != null && ctData.isRemoved())
				delete(ctx, ctData.getId());

			if (ctData.getId() == null) insert(ctx, ctData);
			else update(ctx, ctData);
		}	
	}
	
	public static EnterpriseData insert(AONContext ctx, EnterpriseData ctData) {
		ctx.checkWrite();
		LinkedList<EnterpriseData> list = new LinkedList<>();
		EnterpriseData exists = exists(ctx, ctData);
		if(ctData!=null && exists.getId()==null) {
			Integer id = ctx.getDslContext().insertInto(ENTERPRISE_DATA)
			.set(ENTERPRISE_DATA.DOMAIN, ctData.getDomain())
			.set(ENTERPRISE_DATA.ENTERPRISE, ctData.getEnterprise())
			.set(ENTERPRISE_DATA.NAME, ctData.getName())
			.set(ENTERPRISE_DATA.EXPRESSION, ctData.getExpression())
			.set(ENTERPRISE_DATA.START_DATE, converDateSql(ctData.getStartDate()))
			.set(ENTERPRISE_DATA.END_DATE, ctData.getEndDate()!=null ? converDateSql(ctData.getEndDate()) : null)
			.returning(ENTERPRISE_DATA.ID).fetchOne().getId();
			ctData.setId(id);
			list.add(ctData);
			ctx.log().debug("INSERT ENTERPRISE_DATA id: " + id);		
		} else {
			ctx.log().debug("YA EXISTEN ESTOS DATOS ENTERPRISE_DATA id: " + exists.getId());	
		}
		
		return ctData;
	}
	
	public static EnterpriseData update(AONContext ctx, EnterpriseData enterpriseData) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(ENTERPRISE_DATA)
			.set(ENTERPRISE_DATA.DOMAIN, enterpriseData.getDomain())
			.set(ENTERPRISE_DATA.ENTERPRISE, enterpriseData.getEnterprise())
			.set(ENTERPRISE_DATA.NAME, enterpriseData.getName())
			.set(ENTERPRISE_DATA.EXPRESSION, enterpriseData.getExpression())
			.set(ENTERPRISE_DATA.START_DATE, converDateSql(enterpriseData.getStartDate()) )
			.set(ENTERPRISE_DATA.END_DATE, converDateSql(enterpriseData.getEndDate()) )
			.where(ENTERPRISE_DATA.ID.eq(enterpriseData.getId()))
			.execute();		
		ctx.log().debug("UPDATE ENTERPRISE_DATA id: " + enterpriseData.getId());		
		return enterpriseData;
	}

	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getIdProperty().eq(id));
		ctx.log().debug("DELETE ENTERPRISE_DATA id:" + id);
	}
	
	private static void delete(AONContext ctx, EnterpriseDataFilter filter) {
		ctx.getDslContext()
			.delete(ENTERPRISE_DATA)
			.where(ENTERPRISE_DATA_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	private static EnterpriseData exists(AONContext ctx, EnterpriseData ctData) {
		return get(ctx, f->
			f.getNameProperty().eq(ctData.getName())
			.and(f.getEnterpriseProperty().eq(ctData.getEnterprise()))
			.and(f.getDomainProperty().eq(ctData.getDomain()))
			.and(f.getStartDateProperty().eq( converDateSql(ctData.getStartDate()) ))
			.and( ctData.getExpression()!=null ? f.getExpressionProperty().eq(ctData.getExpression()) : f.getExpressionProperty().isNull())
			.and( ctData.getEndDate()!=null ? f.getEndDateProperty().eq(converDateSql(ctData.getEndDate())) : f.getEndDateProperty().isNull() )
		);
	}
	
	
	public static class EnterpriseDataFiller extends Filler implements Function<Record, EnterpriseData> {

		@Override
		public EnterpriseData apply(Record r) {
			return new EnterpriseData()
				.setId(r.getValue(ENTERPRISE_DATA.ID))
				.setDomain(r.getValue(ENTERPRISE_DATA.DOMAIN))
				.setName(r.getValue(ENTERPRISE_DATA.NAME))
				.setEnterprise(r.getValue(ENTERPRISE_DATA.ENTERPRISE))
				.setExpression(r.getValue(ENTERPRISE_DATA.EXPRESSION))
				.setStartDate(r.getValue(ENTERPRISE_DATA.START_DATE))
				.setEndDate(r.getValue(ENTERPRISE_DATA.END_DATE))
				.setIsRemoved(false)
				;
		}
	}
	
	private static Date converDateSql(java.util.Date date) {
	    return date != null ? new Date(date.getTime()) : null;
	}

}

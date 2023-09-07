package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;

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
import com.esferalia.aon.occam.api.model.Filter.ContractDataFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.ContractDataProperties;
import com.esferalia.aon.occam.api.model.payroll.ContractData;

public class ContractDataDAO {

	private ContractDataDAO() {
		throw new IllegalStateException("Utility Class");
	}
	
	private static final ContractDataPropertiesDAO CONTRACT_DATA_PROPERTIES = new ContractDataPropertiesDAO();
	protected static class ContractDataPropertiesDAO implements ContractDataProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, ContractDataFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(ContractDataFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_DATA.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_DATA.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_DATA.NAME);}
		@Override public Property<Integer> getContractProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_DATA.CONTRACT);}
		@Override public Property<String> getExpressionProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_DATA.EXPRESSION);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_DATA.START_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_DATA.END_DATE);}
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, ContractDataFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<ContractData> getStream(AONContext ctx, ContractDataFilter filter){	
		return select(ctx, filter).fetch().stream().map(new ContractDataFiller());
	}
	
	public static Stream<ContractData> getStream(AONContext ctx, ContractDataFilter filter, Integer page, Integer perPage){	
		return select(ctx, filter)
			.limit(perPage)
			.offset(perPage * (page -1))
			.fetch().stream().map(new ContractDataFiller());
	}
	
	public static LinkedList<ContractData> getList(AONContext ctx, ContractDataFilter filter){	
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<ContractData> getList(AONContext ctx, ContractDataFilter filter, Integer page, Integer perPage){	
		return getStream(ctx, filter, page, perPage).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static ContractData get(AONContext ctx, ContractDataFilter filter) {
		ctx.checkRead();
		return select(ctx, filter).limit(1)
			.stream().map(new ContractDataFiller())
			.findFirst().orElse(new ContractData());
	}
	
	public static void save(AONContext ctx, List<ContractData> contractData) {
		ctx.checkWrite();
		for(ContractData ctData: contractData) {
			if(ctData.getId() == null) insert(ctx, ctData);
			else if(ctData.getModify()) update(ctx, ctData);
		}
	}
	
	public static LinkedList<ContractData> insert(AONContext ctx, ContractData ...contractData) {
		ctx.checkWrite();
		LinkedList<ContractData> list = new LinkedList<>();
		for(ContractData ctData: contractData) {
			ContractData exists = exists(ctx, ctData);
			if(ctData!=null && exists.getId()==null) {
				Integer id = ctx.getDslContext().insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, ctData.getDomain())
				.set(CONTRACT_DATA.NAME, ctData.getName())
				.set(CONTRACT_DATA.CONTRACT, ctData.getContract())
				.set(CONTRACT_DATA.EXPRESSION, ctData.getExpression())
				.set(CONTRACT_DATA.START_DATE, converDateSql(ctData.getStartDate()))
				.set(CONTRACT_DATA.END_DATE, ctData.getEndDate()!=null ? converDateSql(ctData.getEndDate()) : null)
				.returning(CONTRACT_DATA.ID).fetchOne().getId();
				ctData.setId(id);
				list.add(ctData);
				ctx.log().debug("INSERT CONTRACT_DATA id: " + id);		
			} else {
				ctx.log().debug("YA EXISTEN ESTOS DATOS CONTRACT_DATA id: " + exists.getId());	
			}
		}
		return list;
	}
	
	public static ContractData update(AONContext ctx, ContractData contractData) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, contractData.getDomain())
			.set(CONTRACT_DATA.NAME, contractData.getName())
			.set(CONTRACT_DATA.CONTRACT, contractData.getContract())
			.set(CONTRACT_DATA.EXPRESSION, contractData.getExpression())
			.set(CONTRACT_DATA.START_DATE, converDateSql(contractData.getStartDate()) )
			.set(CONTRACT_DATA.END_DATE, converDateSql(contractData.getEndDate()) )
			.where(CONTRACT_DATA.ID.eq(contractData.getId()))
			.execute();		
		ctx.log().debug("UPDATE CONTRACT_DATA id: " + contractData.getId());		
		return contractData;
	}

	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getIdProperty().eq(id));
		ctx.log().debug("DELETE CONTRACT_DATA id:" + id);
	}
	
	private static void delete(AONContext ctx, ContractDataFilter filter) {
		ctx.getDslContext()
			.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	private static ContractData exists(AONContext ctx, ContractData ctData) {
		return get(ctx, f->
			f.getNameProperty().eq(ctData.getName())
			.and(f.getContractProperty().eq(ctData.getContract()))
			.and(f.getDomainProperty().eq(ctData.getDomain()))
			.and(f.getStartDateProperty().eq( converDateSql(ctData.getStartDate()) ))
			.and( ctData.getExpression()!=null ? f.getExpressionProperty().eq(ctData.getExpression()) : f.getExpressionProperty().isNull())
			.and( ctData.getEndDate()!=null ? f.getEndDateProperty().eq(converDateSql(ctData.getEndDate())) : f.getEndDateProperty().isNull() )
		);
	}
	
	
	public static class ContractDataFiller extends Filler implements Function<Record, ContractData> {

		@Override
		public ContractData apply(Record r) {
			return new ContractData()
				.setId(r.getValue(CONTRACT_DATA.ID))
				.setDomain(r.getValue(CONTRACT_DATA.DOMAIN))
				.setName(r.getValue(CONTRACT_DATA.NAME))
				.setContract(r.getValue(CONTRACT_DATA.CONTRACT))
				.setExpression(r.getValue(CONTRACT_DATA.EXPRESSION))
				.setStartDate(r.getValue(CONTRACT_DATA.START_DATE))
				.setEndDate(r.getValue(CONTRACT_DATA.END_DATE))
				.setModify(false);
		}
	}
	
	private static Date converDateSql(java.util.Date date) {
	    return date != null ? new Date(date.getTime()) : null;
	}
}

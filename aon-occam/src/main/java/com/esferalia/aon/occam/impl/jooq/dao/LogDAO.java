package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.LogDataFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.LogData;
import com.esferalia.aon.occam.api.model.Properties.LogDataProperties;

import static com.esferalia.aon.jooq.tables.LogData.LOG_DATA;

import java.sql.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectJoinStep;

public class LogDAO {

	private LogDAO() {		
	}
	
	private static final LogDataPropertiesDAO LOG_DATA_PROPERTIES = new LogDataPropertiesDAO();
	
	protected static class LogDataPropertiesDAO implements LogDataProperties{
		protected Select<Record> build(SelectJoinStep<Record> select,LogDataFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(LogDataFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		
		@Override
		public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(LOG_DATA.ID);}
		@Override
		public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(LOG_DATA.DOMAIN);}
		@Override
		public Property<Date> getDateProperty() {return new FilterDAO.PropertyDAO<>(LOG_DATA.DATE);}
		@Override
		public Property<String> getMessageProperty() {return new FilterDAO.PropertyDAO<>(LOG_DATA.MESSAGE);}
	}
	
	public static class LogDataFiller extends Filler implements Function<Record, LogData>{

		@Override
		public LogData apply(Record r) {
			return build(r);
		}
		
		public static LogData build(Record r) {
			return new LogData()
					.setId(r.getValue(LOG_DATA.ID))
					.setDomain(r.getValue(LOG_DATA.DOMAIN))
					.setDate(r.getValue(LOG_DATA.DATE))
					.setMessage(r.getValue(LOG_DATA.MESSAGE));
		}
	}
	
	public static void insert(AONContext ctx, LogData data) {
		ctx.getDslContext().insertInto(LOG_DATA)
		.set(LOG_DATA.DOMAIN, data.getDomain())
		.set(LOG_DATA.DATE,new java.sql.Date(data.getDate().getTime()))
		.set(LOG_DATA.MESSAGE, data.getMessage())
		.returning(LOG_DATA.ID).fetchOne();
	}
	
	public static LogData select(AONContext ctx, LogDataFilter filter) {
		return ctx.getDslContext()
				.select()
				.from(LOG_DATA)
				.where(LOG_DATA_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new LogDataFiller())
				.findFirst()
				.orElse(new LogData());
	}
	
	public static Stream<LogData>selectAll(AONContext ctx){
		return ctx.getDslContext()
				.select()
				.from(LOG_DATA)
				.fetch()
				.stream()
				.map(new LogDataFiller());
	}
	
	public static void delete(AONContext ctx, LogDataFilter filter) {
		ctx.getDslContext()
		.delete(LOG_DATA)
		.where(LOG_DATA_PROPERTIES.getConditions(filter))
		.execute();
	}
}

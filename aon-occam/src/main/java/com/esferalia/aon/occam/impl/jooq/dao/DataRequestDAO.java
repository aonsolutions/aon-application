package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.DataRequest.DATA_REQUEST;

import java.sql.Timestamp;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.Filter.DataRequestFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.DataRequestProperties;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class DataRequestDAO {

	private DataRequestDAO() {

	}
	
	private static final DataRequestPropertiesDAO DATA_REQUEST_PROPERTIES = new DataRequestPropertiesDAO();

	public static class DataRequestPropertiesDAO implements DataRequestProperties {
		
		public Select<Record> build(SelectJoinStep<Record> select, DataRequestFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		public Condition[] getConditions(DataRequestFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(DATA_REQUEST.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(DATA_REQUEST.DOMAIN);}
		@Override public Property<Timestamp> getDateProperty() {return new FilterDAO.PropertyDAO<>(DATA_REQUEST.DATE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(DATA_REQUEST.TYPE);}
		@Override public Property<String> getBlackBoxProperty() {return new FilterDAO.PropertyDAO<>(DATA_REQUEST.BLACK_BOX);}
		@Override public Property<String> getMd5Property() {return new FilterDAO.PropertyDAO<>(DATA_REQUEST.MD5);}	
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, DataRequestFilter filter) {
		return ctx.getDslContext().select()
				.from(DATA_REQUEST)
				.where(DATA_REQUEST_PROPERTIES.getConditions(filter));	
	}
	
	public static Stream<DataRequest> getStream(AONContext ctx, DataRequestFilter filter) {
		return select(ctx, filter).fetch().stream().map(new DataRequestFiller());
	}
	
	public static DataRequest get(AONContext ctx, DataRequestFilter filter) {
		return select(ctx, filter).limit(1).fetch().stream().map(new DataRequestFiller()).findFirst().orElse(new DataRequest());
	}
	
	public static DataRequest save(AONContext ctx, DataRequest dataRequest) {
		Integer id = ctx.getDslContext().insertInto(DATA_REQUEST)
				.set(DATA_REQUEST.DOMAIN, dataRequest.getDomain())
				.set(DATA_REQUEST.DATE, AonDateUtils.toTimestamp(dataRequest.getDate()))	
				.set(DATA_REQUEST.TYPE, dataRequest.getType().value())
				.set(DATA_REQUEST.BLACK_BOX, dataRequest.getBlackBox())
				.set(DATA_REQUEST.MD5, dataRequest.getMd5())
			.returning(DATA_REQUEST.ID).fetchOne().getId();
		return dataRequest.setId(id);
	}
	
	public static class DataRequestFiller extends Filler implements Function<Record, DataRequest> {
		
		@Override
		public DataRequest apply(Record r) {
			return build(r);
		}
		
		public static DataRequest build(Record r) {
			return new DataRequest()
					.setId(r.getValue(DATA_REQUEST.ID))
					.setDomain(r.getValue(DATA_REQUEST.DOMAIN))
					.setDate(r.getValue(DATA_REQUEST.DATE))
					.setType(DataRequestType.safeValueOf(r.getValue(DATA_REQUEST.TYPE)))
					.setBlackBox(r.getValue(DATA_REQUEST.BLACK_BOX))
					.setMd5(r.getValue(DATA_REQUEST.MD5));
		}
	}
}

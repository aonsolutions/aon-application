package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Cnae.CNAE;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.records.CnaeRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Cnae;
import com.esferalia.aon.occam.api.model.Filter.Cnae2009Filter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.Cnae2009Properties;

public class CnaeDAO {
	
	private CnaeDAO() {
	}
	
	private static final CnaePropertiesDAO CNAE_PROPERTIES = new CnaePropertiesDAO();
	private static class CnaePropertiesDAO implements Cnae2009Properties {
		private Condition[] getConditions(Cnae2009Filter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(CNAE.ID);}
		@Override public Property<String> getCodeProperty() {return new FilterDAO.PropertyDAO<>(CNAE.CODE);}
		@Override public Property<String> getTitleProperty() {return new FilterDAO.PropertyDAO<>(CNAE.TITLE);}
	}
	
	public static class CnaeFiller implements Function<Record,Cnae> {
		@Override
		public Cnae apply(Record rec) {
			return new Cnae()
				.setId(rec.getValue(CNAE.ID))
				.setCode(rec.getValue(CNAE.CODE))
				.setTitle(rec.getValue(CNAE.TITLE))
				.setCode09(rec.getValue(CNAE.CNAE2009_CODE))
				.setTitle09(rec.getValue(CNAE.CNAE2009_TITLE))
				;
		}
	}
	
	private static SelectConditionStep<CnaeRecord> select(AONContext ctx, Cnae2009Filter filter) {
		return ctx.getDslContext()
				.selectFrom(CNAE)
				.where(CNAE_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<Cnae> getStream(AONContext ctx, Cnae2009Filter filter) {
		return select(ctx,filter)
			.orderBy(CNAE.CODE)
			.fetch()
			.stream()
			.map(new CnaeFiller());
	}
	
	public static Cnae get(AONContext ctx, Integer id) {
		return get(ctx, p -> p.getIdProperty().eq(id));
	}
	public static Cnae get(AONContext ctx, String epigraph) {
		return get(ctx, p -> p.getCodeProperty().eq(epigraph));
	}

	public static Cnae get(AONContext ctx, Cnae2009Filter filter) {
		ctx.checkRead();
		return getStream(ctx,filter)
			.findFirst()
			.orElse(new Cnae());
	}
}





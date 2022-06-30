package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.Cnae2009Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Cnae2009;
import com.esferalia.aon.occam.api.model.Filter.Cnae2009Filter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.Cnae2009Properties;
import com.esferalia.aon.watson.error.AonCoreException;

public class Cnae2009DAO {
	
	private Cnae2009DAO() {
	}
	
	private static final Cnae2009PropertiesDAO CNAE2009_PROPERTIES = new Cnae2009PropertiesDAO();
	private static class Cnae2009PropertiesDAO implements Cnae2009Properties {
		private Condition[] getConditions(Cnae2009Filter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(CNAE2009.ID);}
		@Override public Property<String> getCodeProperty() {return new FilterDAO.PropertyDAO<>(CNAE2009.CODE);}
		@Override public Property<String> getTitleProperty() {return new FilterDAO.PropertyDAO<>(CNAE2009.TITLE);}
	}
	
	public static class Cnae2009Filler implements Function<Record,Cnae2009> {
		@Override
		public Cnae2009 apply(Record rec) {
			return new Cnae2009()
				.setId(rec.getValue(CNAE2009.ID))
				.setCode(rec.getValue(CNAE2009.CODE))
				.setTitle(rec.getValue(CNAE2009.TITLE))
				;
		}
	}
	
	private static SelectConditionStep<Cnae2009Record> select(AONContext ctx, Cnae2009Filter filter) {
		return ctx.getDslContext()
				.selectFrom(CNAE2009)
				.where(CNAE2009_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<Cnae2009> getStream(AONContext ctx, Cnae2009Filter filter) {
		return select(ctx,filter)
			.orderBy(CNAE2009.CODE)
			.fetch()
			.stream()
			.map(new Cnae2009Filler());
	}
	
	public static Cnae2009 get(AONContext ctx, Integer id) {
		return get(ctx, p -> p.getIdProperty().eq(id));
	}
	public static Cnae2009 get(AONContext ctx, String epigraph) {
		return get(ctx, p -> p.getCodeProperty().eq(epigraph));
	}

	public static Cnae2009 get(AONContext ctx, Cnae2009Filter filter) {
		ctx.checkRead();
		return getStream(ctx,filter)
			.findFirst()
			.orElse(null);
	}
	
	public static Cnae2009 saveIfAbsent(AONContext ctx, Cnae2009 cnae2009) {
		if (cnae2009.getCode() == null) throw new AonCoreException("El código es una dato obligatorio");
		if (cnae2009.getTitle() == null) throw new AonCoreException("El título es una dato obligatorio");
		Cnae2009 saved = get(ctx, cnae2009.getCode()); 
		if ( saved == null) {
			Integer id = ctx.getDslContext()
				.insertInto(CNAE2009)
				.set(CNAE2009.CODE,cnae2009.getCode())
				.set(CNAE2009.TITLE,cnae2009.getTitle())
				.returning(CNAE2009.ID)
				.fetchOne()
				.getValue(CNAE2009.ID);
			saved = get(ctx, id);
		}
		return saved;
	}

	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static Cnae2009 getRandom(AONContext ctx, Cnae2009Filter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new Cnae2009Filler())
			.findFirst()
			.orElse(null);
	}

}





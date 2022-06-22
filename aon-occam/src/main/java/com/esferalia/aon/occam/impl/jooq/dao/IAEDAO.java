package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Iae.IAE;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.IaeRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.IAEFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Iae;
import com.esferalia.aon.occam.api.model.Properties.IAEProperties;
import com.esferalia.aon.watson.error.AonCoreException;

public class IAEDAO {
	
	private IAEDAO() {
	}
	
	private static final IAEPropertiesDAO IAE_PROPERTIES = new IAEPropertiesDAO();
	private static class IAEPropertiesDAO implements IAEProperties {
		private Condition[] getConditions(IAEFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(IAE.ID);}
		@Override public Property<String> getSectionProperty() {return new FilterDAO.PropertyDAO<>(IAE.SECTION);}
		@Override public Property<String> getEpigraphProperty() {return new FilterDAO.PropertyDAO<>(IAE.EPIGRAPH);}
		@Override public Property<String> getTitleProperty() {return new FilterDAO.PropertyDAO<>(IAE.TITLE);}
	}
	
	public static class IAEFiller  implements Function<Record,Iae> {
		@Override
		public Iae apply(Record rec) {
			return new Iae()
				.setId(rec.getValue(IAE.ID))
				.setSection(rec.getValue(IAE.SECTION))
				.setEpigraph(rec.getValue(IAE.EPIGRAPH))
				.setTitle(rec.getValue(IAE.TITLE))
				;
		}
	}
	
	private static SelectConditionStep<IaeRecord> select(AONContext ctx, IAEFilter filter) {
		return ctx.getDslContext()
				.selectFrom(IAE)
				.where(IAE_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<Iae> getStream(AONContext ctx, IAEFilter filter) {
		return select(ctx,filter)
			.orderBy(IAE.EPIGRAPH)
			.fetch()
			.stream()
			.map(new IAEFiller());
	}
	
	public static Iae get(AONContext ctx, Integer id) {
		return get(ctx, p -> p.getIdProperty().eq(id));
	}
	public static Iae get(AONContext ctx, String epigraph) {
		return get(ctx, p -> p.getEpigraphProperty().eq(epigraph));
	}

	public static Iae get(AONContext ctx, IAEFilter filter) {
		ctx.checkRead();
		return getStream(ctx,filter)
			.findFirst()
			.orElse(null);
	}
	
	public static Iae saveIfAbsent(AONContext ctx, Iae iae) {
		if (iae.getSection() == null) throw new AonCoreException("La sección es una dato obligatorio");
		if (iae.getEpigraph() == null) throw new AonCoreException("El epígrafe es una dato obligatorio");
		if (iae.getTitle() == null) throw new AonCoreException("El título es una dato obligatorio");
		Iae saved = get(ctx, iae.getEpigraph()); 
		if ( saved == null) {
			Integer id = ctx.getDslContext()
				.insertInto(IAE)
				.set(IAE.SECTION,iae.getSection())
				.set(IAE.EPIGRAPH,iae.getEpigraph())
				.set(IAE.TITLE,iae.getTitle())
				.returning(IAE.ID)
				.fetchOne()
				.getValue(IAE.ID);
			saved = get(ctx, id);
		}
		return saved;
	}

	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static Iae getRandom(AONContext ctx, IAEFilter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new IAEFiller())
			.findFirst()
			.orElse(null);
	}

}





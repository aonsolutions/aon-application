package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Iae.IAE;

import java.util.Optional;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

import net.aonsolutions.occam.api.model.Filter.IaeFilter;
import net.aonsolutions.occam.api.model.Filter.Property;
import net.aonsolutions.occam.api.model.Iae;
import net.aonsolutions.occam.api.model.Properties.IaeProperties;
import net.aonsolutions.occam.impl.AONContext;

class IaeHandler extends AbsHandler {
	
	private IaeHandler() {
		
	}
	
	private static final IaePropertiesHandler IAE_PROPERTIES = new IaePropertiesHandler();
	private static class IaePropertiesHandler implements IaeProperties {
		private Condition[] getConditions(IaeFilter filter) {
			if (filter == null) return new Condition[0];
			FilterHandler filterDAO = (FilterHandler) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> 	getIdProperty() 		{return new FilterHandler.PropertyDAO<>(IAE.ID);}
		@Override public Property<String> 	getSectionProperty() 	{return new FilterHandler.PropertyDAO<>(IAE.SECTION);}
		@Override public Property<String> 	getEpigraphProperty() 	{return new FilterHandler.PropertyDAO<>(IAE.EPIGRAPH);}
		@Override public Property<String> 	getTitleProperty() 		{return new FilterHandler.PropertyDAO<>(IAE.TITLE);}
	}
	
	static class IaeFiller extends Filler<Iae> {
		@Override
		public Iae apply(Record r) {
			return build(r);
		}
		
		public static Iae build(Record r) {
			if (isNull(r, IAE.ID)) return null;
			return new Iae()
				.setId(getValue(r, IAE.ID))
				.setSection(getValue(r, IAE.SECTION))
				.setEpigraph(getValue(r, IAE.EPIGRAPH))
				.setTitle(getValue(r, IAE.TITLE))
			;
		}
	}
	
	private static SelectJoinStep<Record> select(AONContext ctx) {
		ctx.checkRead();
		return ctx.getDslContext()
				.select().from(IAE)
		;
	}
	
	static Stream<Iae> getStream(AONContext ctx, IaeFilter filter) {
		return select(ctx)
			.where( IAE_PROPERTIES.getConditions(filter) )
			.orderBy(IAE.EPIGRAPH)
			.fetch()
			.stream()
			.map(new IaeFiller());
	}
	
	static Optional<Iae> get(AONContext ctx, Integer id) {
		return select(ctx)
			.where( IAE.ID.eq(id) )
			.fetch()
			.stream()
			.map(new IaeFiller())
			.findFirst();
	}
	static Optional<Iae> get(AONContext ctx, String epigraph) {
		return select(ctx)
			.where( IAE.EPIGRAPH.eq(epigraph) )
			.fetch()
			.stream()
			.map(new IaeFiller())
			.findFirst();
	}

	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	static Iae getRandom(AONContext ctx, IaeFilter filter) {
		return select(ctx)
			.where( IAE_PROPERTIES.getConditions(filter) )
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new IaeFiller())
			.findFirst()
			.orElse(null);
	}

}





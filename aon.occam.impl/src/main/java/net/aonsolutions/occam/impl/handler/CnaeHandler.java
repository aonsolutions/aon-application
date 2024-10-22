package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Cnae.CNAE;

import java.util.Optional;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

import net.aonsolutions.occam.api.model.Cnae;
import net.aonsolutions.occam.api.model.Filter.CnaeFilter;
import net.aonsolutions.occam.api.model.Filter.Property;
import net.aonsolutions.occam.api.model.Properties.CnaeProperties;
import net.aonsolutions.occam.impl.AONContext;


class CnaeHandler {
	
	private CnaeHandler() {
	}
	
	private static final CnaePropertiesHandler CNAE_PROPERTIES = new CnaePropertiesHandler();
	private static class CnaePropertiesHandler implements CnaeProperties {
		private Condition[] getConditions(CnaeFilter filter) {
			if (filter == null) return new Condition[0];
			FilterImpl filterDAO = (FilterImpl) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> 	getIdProperty() 	{return new FilterImpl.PropertyDAO<>(CNAE.ID);}
		@Override public Property<String> 	getCodeProperty() 	{return new FilterImpl.PropertyDAO<>(CNAE.CODE);}
		@Override public Property<String> 	getTitleProperty() 	{return new FilterImpl.PropertyDAO<>(CNAE.TITLE);}
	}
	
	static class CnaeFiller extends Filler<Cnae> {
		@Override
		public Cnae apply(Record r) {
			return build(r);
		}
		
		public static Cnae build(Record r) {
			if (isNull(r, CNAE.ID)) return null;
			return new Cnae()
				.setId(getValue(r, CNAE.ID))
				.setCode(getValue(r, CNAE.CODE))
				.setTitle(getValue(r, CNAE.TITLE))
			;
		}
	}
	
	private static SelectJoinStep<Record> select(AONContext ctx) {
		return ctx.getDslContext()
			.select()
			.from(CNAE)
		;
	}
	
	static Stream<Cnae> getStream(AONContext ctx, CnaeFilter filter) {
		return select(ctx)
			.where(CNAE_PROPERTIES.getConditions(filter))
			.orderBy(CNAE.CODE)
			.fetch()
			.stream()
			.map(new CnaeFiller());
	}
	
	static Optional<Cnae> get(AONContext ctx, Integer id) {
		return select(ctx)
			.where(CNAE.ID.eq(id))
			.fetch()
			.stream()
			.map(new CnaeFiller())
			.findFirst();
	}
	
	static Optional<Cnae> get(AONContext ctx, String code) {
		return select(ctx)
			.where(CNAE.CODE.eq(code))
			.fetch()
			.stream()
			.map(new CnaeFiller())
			.findFirst();
	}

	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	static Cnae getRandom(AONContext ctx, CnaeFilter filter) {
		return select(ctx)
			.where(CNAE_PROPERTIES.getConditions(filter))
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new CnaeFiller())
			.findFirst()
			.orElse(null);
	}

}





package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Tariff.TARIFF;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import net.aonsolutions.occam.api.model.Filter.Property;
import net.aonsolutions.occam.api.model.Filter.TariffFilter;
import net.aonsolutions.occam.api.model.Properties.TariffProperties;
import net.aonsolutions.occam.api.model.Tariff;
import net.aonsolutions.occam.impl.AONContext;

class TariffHandler {

	private TariffHandler() {
	}

	private static final TariffPropertiesHandler TARIFF_PROPERTIES = new TariffPropertiesHandler();
	protected static class TariffPropertiesHandler implements TariffProperties {
		protected Condition getCondition(TariffFilter filter) {
			if (filter == null) return DSL.trueCondition();
			FilterHandler filterHandler = (FilterHandler) filter.filter(this);
			if (filterHandler == null) return DSL.trueCondition();
			return filterHandler.getCondition();
		}

		@Override public Property<Integer> getIdProperty() {return new FilterHandler.PropertyDAO<>(TARIFF.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterHandler.PropertyDAO<>(TARIFF.DOMAIN);}
		@Override public Property<String> getCodeProperty() {return new FilterHandler.PropertyDAO<>(TARIFF.CODE);}
		@Override public Property<String> getNameProperty() {return new FilterHandler.PropertyDAO<>(TARIFF.NAME);}
		@Override public Property<Byte> getPurchaseProperty() {return new FilterHandler.PropertyDAO<>(TARIFF.PURCHASE);}
		@Override public Property<Double> getDiscountProperty() {return new FilterHandler.PropertyDAO<>(TARIFF.DISCOUNT);}
		@Override public Property<Byte> getActiveProperty() {return new FilterHandler.PropertyDAO<>(TARIFF.ACTIVE);}
	}

	static class TariffFiller extends Filler<Tariff> {
		@Override
		public Tariff apply(Record r) {
			return build(r);
		}

		public static Tariff build(Record r) {
			if (!checkField(r, TARIFF.ID)) return null;
			return new Tariff()
				.setId(getValue(r, TARIFF.ID))
				.setDomain(getValue(r, TARIFF.DOMAIN))
				.setCode(getValue(r, TARIFF.CODE))
				.setName(getValue(r, TARIFF.NAME))
				.setPurchase(getValue(r, TARIFF.PURCHASE)==1)
				.setDiscount(getValue(r, TARIFF.DISCOUNT))
				.setActive(getValue(r, TARIFF.ACTIVE)==1)
			;
		}
	}

	private static SelectConditionStep<Record> select(AONContext ctx, int domain) {
		return ctx.getDslContext()
			.select()
			.from(TARIFF)
			.where(TARIFF.DOMAIN.in(ctx.getInheritanceDomainIds(domain)))
		;
	}

	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	static Tariff getRandom(AONContext ctx, int domain, TariffFilter filter) {
		return select(ctx, domain)
			.and(TARIFF_PROPERTIES.getCondition(filter))
			.orderBy(DSL.rand())
			.fetch()
			.stream()
			.map(new TariffFiller())
			.findFirst()
			.orElse(null);
	}
}

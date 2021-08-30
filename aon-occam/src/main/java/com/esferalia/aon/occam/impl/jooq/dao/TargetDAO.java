package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Target.TARGET;

import java.sql.Timestamp;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.TargetFilter;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.type.TargetStatus;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.TargetPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;

public class TargetDAO {
	private static final TargetPropertiesDAO TARGET_PROPERTIES = new TargetPropertiesDAO();
	
	public static Stream<Target> getTargetStream(AONContext ctx, TargetFilter filter){
		return TARGET_PROPERTIES.build(ctx.getDslContext().select()
					.from(TARGET).join(SCOPE).on(TARGET.SCOPE.eq(SCOPE.ID))
					.join(REGISTRY).on(REGISTRY.ID.eq(TARGET.REGISTRY))
			,filter).fetch().stream().map(new TargetFiller());
	}
	
	public static Target save(AONContext ctx, Target target) {
		return target.getId() != null
			? update(ctx, target)
			: insert(ctx, target);
	}
	
	private static Target insert(AONContext ctx, Target target){
		ctx.getDslContext().insertInto(TARGET, TARGET.ADVERTISING, TARGET.DOMAIN, TARGET.REGISTRY, TARGET.SCOPE,
				TARGET.STATUS, TARGET.SURCHARGE, TARGET.TARIFF, TARGET.TRANSACTION, TARGET.WITHHOLDING,
				TARGET.CREATION_DATE, TARGET.CREATION_USER, TARGET.MODIFICATION_DATE, TARGET.MODIFICATION_USER)
			.values(target.getAdvertising().byteValue(), target.getDomain().getId(), target.getId(), target.getScope(), target.getStatus().value(),
					target.getSurcharge().byteValue(), target.getTariff(),target.getTransaction().byteValue(), target.getWithholding().byteValue(),
					new Timestamp(new Date().getTime()), ctx.getUser(), new Timestamp(new Date().getTime()), ctx.getUser()).execute();
		return target;
	}
	
	private static Target update(AONContext ctx, Target target){
		ctx.getDslContext()
			.update(TARGET)
			.set(TARGET.ADVERTISING, target.getAdvertising().byteValue())
			.set(TARGET.SCOPE, target.getScope())
			.set(TARGET.STATUS, target.getStatus().value())
			.set(TARGET.SURCHARGE, target.getSurcharge().byteValue())
			.set(TARGET.TARIFF, target.getTariff())
			.set(TARGET.TRANSACTION, target.getTransaction().byteValue())
			.set(TARGET.WITHHOLDING, target.getWithholding().byteValue())
			.set(TARGET.MODIFICATION_DATE, new Timestamp(new Date().getTime()))
			.set(TARGET.MODIFICATION_USER, ctx.getUser())
			.where(TARGET.REGISTRY.eq(target.getId()))
			.execute();
		return target;
	}
	
	public static class TargetFiller extends Filler implements Function<Record, Target> {
		@Override
		public Target apply(Record r) {
			return build(r, REGISTRY);				
		}
		
		public static Target build(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			return new Target()
				.copy(RegistryFiller.build(r, registry))
				.setScope(r.getValue(TARGET.SCOPE))
				.setAdvertising(r.getValue(TARGET.ADVERTISING).shortValue())
				.setSurcharge(r.getValue(TARGET.SURCHARGE).shortValue())
				.setTariff(r.getValue(TARGET.TARIFF))
				.setWithholding(r.getValue(TARGET.WITHHOLDING).shortValue())
				.setTransaction(r.getValue(TARGET.TRANSACTION).shortValue())
				.setStatus(TargetStatus.safeValueOf(r.getValue(TARGET.STATUS)))
				.setCreationDate(r.getValue(TARGET.CREATION_DATE))
				.setCreationUser(r.getValue(TARGET.CREATION_USER))
				.setModificationDate(r.getValue(TARGET.MODIFICATION_DATE))
				.setModificationUser(r.getValue(TARGET.MODIFICATION_USER));
		}
	}
	
}

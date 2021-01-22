package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Target.TARGET;

import java.sql.Timestamp;
import java.util.Date;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.TargetFilter;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.TargetFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.TargetPropertiesDAO;

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
	
	
}

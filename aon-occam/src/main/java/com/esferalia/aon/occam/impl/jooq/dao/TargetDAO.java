package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Target.TARGET;
import static com.esferalia.aon.jooq.tables.Tariff.TARIFF;

import java.sql.Timestamp;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Advertising;
import com.esferalia.aon.occam.api.model.Filter.TargetFilter;
import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.TargetStatus;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.TargetPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.TariffDAO.TariffFiller;
import com.esferalia.aon.occam.impl.jooq.validation.TargetAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.TargetValidation;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class TargetDAO {
	
	public static final com.esferalia.aon.jooq.tables.Registry TARGET_ALIAS = REGISTRY.as("registry_target");
	private static final TargetPropertiesDAO TARGET_PROPERTIES = new TargetPropertiesDAO();
	
	private static SelectConditionStep<Record> select(AONContext ctx, TargetFilter filter) {
		return ctx.getDslContext().select()
				.from(TARGET)
				.join(TARGET_ALIAS).on(TARGET_ALIAS.ID.eq(TARGET.REGISTRY))
				.join(SCOPE).on(SCOPE.ID.eq(TARGET.SCOPE))
				.where(TARGET_PROPERTIES.getConditions(filter));	
	}

	public static Target get(AONContext ctx, TargetFilter filter){
		return select(ctx, filter).limit(1).fetch().stream().map(new TargetFiller())
			.findFirst().orElse(new Target());
	}
	
	public static Target get(AONContext ctx, Integer id){
		return get(ctx, f -> f.getIdProperty().eq(id));
	}
	
	public static Stream<Target> getStream(AONContext ctx, TargetFilter filter){
		return select(ctx, filter)
			.orderBy(TARGET_ALIAS.NAME)
			.fetch().stream().map(new TargetFiller());
	}	
	
	public static Target save(AONContext ctx, Target target) {
		ctx.checkWrite();
		TargetAutoComplete.autoComplete(ctx, target);
		TargetValidation.validate(ctx, target);
		target = RegistryDAO.save(ctx, target);
		return target.getRegistry() != null
			? update(ctx, target)
			: insert(ctx, target);
	}
	
	private static Target insert(AONContext ctx, Target target){
		Integer id = ctx.getDslContext()
			.insertInto(TARGET)
			.set(TARGET.ADVERTISING, target.getAdvertising().value())
			.set(TARGET.DOMAIN, target.getDomain().getId()) 
			.set(TARGET.REGISTRY, target.getId()) 
			.set(TARGET.SCOPE, target.getScope().getId())
			.set(TARGET.STATUS, target.getStatus().value())
			.set(TARGET.SURCHARGE, AonEnumUtils.getByte(target.isSurcharge()))
			.set(TARGET.TARIFF, target.getTariff().getId())
			.set(TARGET.TRANSACTION, target.getTransaction().value())
			.set(TARGET.WITHHOLDING, AonEnumUtils.getByte(target.isWithholding()))
			.set(TARGET.CREATION_DATE, new Timestamp(new Date().getTime()))
			.set(TARGET.CREATION_USER, ctx.getUser())
			.set(TARGET.MODIFICATION_DATE, new Timestamp(new Date().getTime()))
			.set(TARGET.MODIFICATION_USER,  ctx.getUser())
			.returning(TARGET.REGISTRY).fetchOne().getRegistry();
	
		ctx.log().debug("INSERT TARGET id "+ id);
		
		return target.setId(id);
	}
	
	private static Target update(AONContext ctx, Target target){
		ctx.getDslContext()
			.update(TARGET)
			.set(TARGET.ADVERTISING, target.getAdvertising().value())
			.set(TARGET.SCOPE, target.getScope().getId())
			.set(TARGET.STATUS, target.getStatus().value())
			.set(TARGET.SURCHARGE, AonEnumUtils.getByte(target.isSurcharge()))
			.set(TARGET.TARIFF, target.getTariff().getId())
			.set(TARGET.TRANSACTION, target.getTransaction().value())
			.set(TARGET.WITHHOLDING, AonEnumUtils.getByte(target.isWithholding()))
			.set(TARGET.MODIFICATION_DATE, new Timestamp(new Date().getTime()))
			.set(TARGET.MODIFICATION_USER, ctx.getUser())
			.where(TARGET.REGISTRY.eq(target.getId()))
			.execute();
		ctx.log().debug("UPDATE TARGET id "+target.getId());		
		return target;
	}
	
	public static class TargetFiller extends Filler implements Function<Record, Target> {
		
		@Override
		public Target apply(Record r) {
			return build(r);				
		}
		
		public static Target build(Record r) {
			return build(r, TARGET_ALIAS);
		}
		
		public static Target build(Record r, Registry registry) {
			return new Target()
				.copy(RegistryFiller.build(r, registry))
				.setRegistry(r.getValue(TARGET.REGISTRY))
				.setScope(checkField(r, SCOPE.ID) 
					? ScopeFiller.buildScope(r)
					: new Scope().setId(r.getValue(TARGET.SCOPE)))
				.setAdvertising(Advertising.safeValueOf(r.getValue(TARGET.ADVERTISING)))
				.setSurcharge(getBoolean(r, TARGET.SURCHARGE))
				.setTariff(checkField(r, TARIFF.ID)
					? TariffFiller.build(r)
					: new Tariff().setId(r.getValue(TARGET.TARIFF)))
				.setWithholding(getBoolean(r, TARGET.WITHHOLDING))
				.setTransaction(InvoiceTransactionType.safeValueOf(r.getValue(TARGET.TRANSACTION)))
				.setStatus(TargetStatus.safeValueOf(r.getValue(TARGET.STATUS)))
				.setCreationDate(r.getValue(TARGET.CREATION_DATE))
				.setCreationUser(r.getValue(TARGET.CREATION_USER))
				.setModificationDate(r.getValue(TARGET.MODIFICATION_DATE))
				.setModificationUser(r.getValue(TARGET.MODIFICATION_USER));
		}
	}
}

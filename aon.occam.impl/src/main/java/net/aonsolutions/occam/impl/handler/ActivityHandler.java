package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.occam.api.model.Activity;
import net.aonsolutions.occam.api.model.type.IRPFRegime;
import net.aonsolutions.occam.api.model.type.VATExemptionCause;
import net.aonsolutions.occam.api.model.type.VATRegime;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.CnaeHandler.CnaeFiller;
import net.aonsolutions.occam.impl.handler.IaeHandler.IaeFiller;

class ActivityHandler {
	
	private ActivityHandler() {
	}
	
	static class ActivityFiller extends Filler<Activity> {
		@Override
		public Activity apply(Record r) {
			return build(r);
		}
		
		public static Activity build(Record r) {
			if (isNull(r, ENTERPRISE_ACTIVITY.ID)) return null;
			return new Activity()
				.setId(getValue(r, ENTERPRISE_ACTIVITY.ID) )
				.setDomain(getValue(r, ENTERPRISE_ACTIVITY.DOMAIN) )
				.setDescription(getValue(r, ENTERPRISE_ACTIVITY.DESCRIPTION) )
				.setMain(getBoolean(r, ENTERPRISE_ACTIVITY.PRINCIPAL))
				.setStartDate( getValue(r, ENTERPRISE_ACTIVITY.START_DATE)) 
				.setEndDate( getValue(r, ENTERPRISE_ACTIVITY.END_DATE))
				.setCnae(CnaeFiller.build(r))
				.setIae(IaeFiller.build(r))
				.setVatRegime(VATRegime.value(getValue(r,ENTERPRISE_ACTIVITY.VAT_REGIME)).orElse(null))
				.setSurcharge(getBoolean(r, ENTERPRISE_ACTIVITY.SURCHARGE))
				.setVatExemptionCause(VATExemptionCause.value(getValue(r, ENTERPRISE_ACTIVITY.VAT_EXEMPTION_CAUSE)).orElse(null))
				.setIrpfRegime(IRPFRegime.value(getValue(r, ENTERPRISE_ACTIVITY.RETENTION_REGIME)).orElse(null))
			;
		}
	}

	static Stream<Activity> stream(AONContext ctx,int domain, Date atDate) {
		Condition domainCondition = ENTERPRISE_ACTIVITY.DOMAIN.equal(domain);
		Condition c = getDateCondition( atDate )
			.map( domainCondition::and )
			.orElse(domainCondition);
		return ctx.getDslContext()
			.select()
			.from(ENTERPRISE_ACTIVITY)
			.leftOuterJoin(CNAE2009).on(CNAE2009.ID.eq(ENTERPRISE_ACTIVITY.CNAE2009))
			.leftOuterJoin(IAE).on(IAE.ID.eq(ENTERPRISE_ACTIVITY.IAE))
			.where( c )
			.fetch()
			.stream()
			.map(new ActivityFiller());
	}

	private static Optional<Condition> getDateCondition(Date atDate) {
		Condition c = null;
		if (atDate != null) {
			Condition startDate = ENTERPRISE_ACTIVITY.START_DATE.isNull()
				.or(ENTERPRISE_ACTIVITY.START_DATE.le(AonDateUtils.toSql(atDate)));
			Condition endDate = ENTERPRISE_ACTIVITY.END_DATE.isNull()
				.or(ENTERPRISE_ACTIVITY.END_DATE.ge(AonDateUtils.toSql(atDate)));
			c = startDate.and(endDate);	
		}
		return Optional.ofNullable(c); 
	}
}





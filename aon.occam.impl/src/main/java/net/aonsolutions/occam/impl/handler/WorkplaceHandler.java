package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectConditionStep;

import net.aonsolutions.occam.api.model.Workplace;
import net.aonsolutions.occam.impl.AONContext;

class WorkplaceHandler {
	
	private WorkplaceHandler() {
	
	}
	
	static SelectConditionStep<Record> select(AONContext ctx,int domain){
		return ctx.getDslContext()
			.select()
			.from(WORKPLACE)
			.where(WORKPLACE.DOMAIN.eq(domain))
			.and(WORKPLACE.SCOPE.in( ctx.getUserScopes(domain)))
		;
	}
	
	static Stream<Workplace> stream(AONContext ctx,int domain){
		return select(ctx, domain)
			.fetch()
			.stream()
			.map(new WorkplaceFiller())
		;	
	}
	
	public static class WorkplaceFiller extends Filler<Workplace> {
		
		@Override
		public Workplace apply(Record r) {
			return build(r);
		}
		
		public static Workplace build(Record r) {
			return new Workplace()
				.setId(getValue(r, WORKPLACE.ID))
				.setDomain(getValue(r, WORKPLACE.DOMAIN))
				.setActive(getBoolean(r, WORKPLACE.ACTIVE))
				.setAddress(getValue(r, WORKPLACE.ADDRESS))
				.setCustomer(getValue(r, WORKPLACE.CUSTOMER))
				.setDescription(getValue(r, WORKPLACE.DESCRIPTION))
				.setEconomicagreement(getValue(r, WORKPLACE.ECONOMICAGREEMENT))
				.setEnterprise(getValue(r, WORKPLACE.ENTERPRISE))
				.setScope(getValue(r, WORKPLACE.SCOPE))
			;
		}
	}
	
}

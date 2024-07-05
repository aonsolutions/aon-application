package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;

import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.PayrollWorkplaceFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.PayrollWorkplace;
import com.esferalia.aon.occam.api.model.Properties.PayrollWorkplaceProperties;

public class PayrollWorkplaceDAO {
	
	private PayrollWorkplaceDAO() {}
	
	private static final PayrollWorkplacePropertiesDAO PAYROLL_WORKPLACE_PROPERTIES = new PayrollWorkplacePropertiesDAO();

	protected static class PayrollWorkplacePropertiesDAO implements PayrollWorkplaceProperties {
		protected Condition[] getConditions(PayrollWorkplaceFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(PAYROLL_WORKPLACE.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(PAYROLL_WORKPLACE.DOMAIN);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(PAYROLL_WORKPLACE.WORKPLACE);}
		@Override public Property<Integer> getAgreementProperty() {return new FilterDAO.PropertyDAO<>(PAYROLL_WORKPLACE.AGREEMENT);}
		@Override public Property<Integer> getEnterpriseActivityProperty() {return new FilterDAO.PropertyDAO<>(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY);}
		@Override public Property<Integer> getCalendarProperty() {return new FilterDAO.PropertyDAO<>(PAYROLL_WORKPLACE.CALENDAR);}
			
	}

	public static PayrollWorkplace get(CloseableAONContext ctx, PayrollWorkplaceFilter filter) {
		return ctx.getDslContext().select().from(PAYROLL_WORKPLACE).where(PAYROLL_WORKPLACE_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(PAYROLL_WORKPLACE).stream().map(new PayrollWorkplaceFiller()).findFirst().orElse(null);
	}
	
	public static PayrollWorkplace save(AONContext ctx, PayrollWorkplace payrollWorkplace) {
		return payrollWorkplace.getId() != null 
			? update(ctx, payrollWorkplace)
			: insert(ctx, payrollWorkplace);
	}

	public static PayrollWorkplace insert(AONContext ctx, PayrollWorkplace payrollWorkplace) {
		Integer id = ctx.getDslContext().insertInto(PAYROLL_WORKPLACE)
			.set(PAYROLL_WORKPLACE.AGREEMENT, payrollWorkplace.getAgreement())
			.set(PAYROLL_WORKPLACE.CALENDAR, payrollWorkplace.getCalendar())
			.set(PAYROLL_WORKPLACE.DOMAIN, payrollWorkplace.getDomain())
			.set(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY, payrollWorkplace.getEnterpriseActivity())
			.set(PAYROLL_WORKPLACE.WORKPLACE, payrollWorkplace.getWorkplace())
			.returning(PAYROLL_WORKPLACE.ID).fetchOne().getValue(PAYROLL_WORKPLACE.ID);
		payrollWorkplace.setId(id);
		ctx.log().debug("INSERT PAYROLL WORKPLACE id: " + payrollWorkplace.getId());	
		return payrollWorkplace;
	}
	
	public static PayrollWorkplace update(AONContext ctx, PayrollWorkplace payrollWorkplace) {
		ctx.getDslContext().update(PAYROLL_WORKPLACE)
			.set(PAYROLL_WORKPLACE.AGREEMENT, payrollWorkplace.getAgreement())
			.set(PAYROLL_WORKPLACE.CALENDAR, payrollWorkplace.getCalendar())
			.set(PAYROLL_WORKPLACE.DOMAIN, payrollWorkplace.getDomain())
			.set(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY, payrollWorkplace.getEnterpriseActivity())
			.set(PAYROLL_WORKPLACE.WORKPLACE, payrollWorkplace.getWorkplace())
			.where(PAYROLL_WORKPLACE.ID.eq(payrollWorkplace.getId()))
			.execute();
		ctx.log().debug("UPDATE WORKPLACE id: " + payrollWorkplace.getId());	
		return payrollWorkplace;
	}
	
	public static class PayrollWorkplaceFiller implements Function<Record, PayrollWorkplace> {

		@Override
		public PayrollWorkplace apply(Record r) {
			return build(r);
		}
		
		public static PayrollWorkplace build(Record r) {
			return new PayrollWorkplace()
					.setAgreement(r.getValue(PAYROLL_WORKPLACE.AGREEMENT))
					.setCalendar(r.getValue(PAYROLL_WORKPLACE.CALENDAR))
					.setDomain(r.getValue(PAYROLL_WORKPLACE.DOMAIN))
					.setEnterpriseActivity(r.getValue(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY))
					.setWorkplace(r.getValue(PAYROLL_WORKPLACE.WORKPLACE));
		}
	}
	
}

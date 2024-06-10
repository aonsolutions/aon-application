package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;

import net.aonsolutions.db.up2date.Update;

public class CleanDuplicatesPayrollWorkplace implements Update {

	public static CleanDuplicatesPayrollWorkplace CLEAN_DUPLICATE_PAYROLL_WORKPLACE = new CleanDuplicatesPayrollWorkplace();
	
	private Integer deletes = 0;

	private CleanDuplicatesPayrollWorkplace() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		System.out.println("[START CLEAN DUPLICATES PAYROLL_WORKPLACE]");
		
		try {
			Result<DomainRecord> domainRecords = dslContext.selectFrom(DOMAIN).fetch();
			
			for(DomainRecord domainRecord : domainRecords) {
				Result<WorkplaceRecord> workplaceRecords = dslContext.selectFrom(WORKPLACE).where(WORKPLACE.DOMAIN.eq(domainRecord.getId())).fetch();
				
				for(WorkplaceRecord workplaceRecord : workplaceRecords) {
					Result<PayrollWorkplaceRecord> payrollWorkplaceRecords = 
							dslContext.selectFrom(PAYROLL_WORKPLACE)
								.where(PAYROLL_WORKPLACE.DOMAIN.eq(domainRecord.getId()))
								.and(PAYROLL_WORKPLACE.WORKPLACE.eq(workplaceRecord.getId()))
								.fetch();
					
					if(payrollWorkplaceRecords.isEmpty() || payrollWorkplaceRecords.size() == 1) continue;
					
					// Check if they are duplicates
					PayrollWorkplaceRecord firstPayrollWorkplaceRecord = payrollWorkplaceRecords.get(0);
					
					for(int i=1; i<payrollWorkplaceRecords.size(); i++) {
						PayrollWorkplaceRecord payrollWorkplaceIterator = payrollWorkplaceRecords.get(i);
						
						if(	
							equals(firstPayrollWorkplaceRecord.getDomain(), payrollWorkplaceIterator.getDomain()) &&
							equals(firstPayrollWorkplaceRecord.getWorkplace(), payrollWorkplaceIterator.getWorkplace()) &&
							equals(firstPayrollWorkplaceRecord.getAgreement(), payrollWorkplaceIterator.getAgreement()) &&
							equals(firstPayrollWorkplaceRecord.getEnterpriseActivity(), payrollWorkplaceIterator.getEnterpriseActivity()) &&
							equals(firstPayrollWorkplaceRecord.getCalendar(), payrollWorkplaceIterator.getCalendar())
						  ) {
							// Delete duplicate
							dslContext.delete(PAYROLL_WORKPLACE).where(PAYROLL_WORKPLACE.ID.eq(payrollWorkplaceIterator.getId())).execute();
							deletes++;
							System.out.println("\t Delete duplicate PayrollWorkplace --> " + payrollWorkplaceIterator.getId());
						}
					}
				}
				
			}
			
			System.out.println("\t Delete duplicates entries : " + deletes);
		} catch (Throwable t) {
			System.out.println("[CLEAN DUPLICATES PAYROLL_WORKPLACE FAILED!]");
		}
		System.out.println("[END CLEAN DUPLICATES PAYROLL_WORKPLACE]");
	}

	private boolean equals(Integer value, Integer value2) {
		if(null == value && null == value2) return true;
		else if(null != value && null != value2) return value.equals(value2);
		else return false;
	}
	
}

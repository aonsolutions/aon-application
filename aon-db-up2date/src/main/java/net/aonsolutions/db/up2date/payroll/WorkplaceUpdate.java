package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;

import net.aonsolutions.db.up2date.Update;

public class WorkplaceUpdate implements Update {


	public static WorkplaceUpdate WORKPLACE_UPDATE = new WorkplaceUpdate();

	private WorkplaceUpdate() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		System.out.println("[START]");
		System.out.println( "Update table `workplace`" );
		
		try {
			dslContext.select().from(ENTERPRISE)
			.leftJoin(WORKPLACE).on(WORKPLACE.ENTERPRISE.eq(ENTERPRISE.REGISTRY))
			.where(WORKPLACE.ENTERPRISE.isNull())
			.groupBy(ENTERPRISE.DOMAIN)
			.fetchStreamInto(ENTERPRISE).forEach(enterprise->
				dslContext.select()
				.from(ENTERPRISE_CCC)
				.where(ENTERPRISE_CCC.DOMAIN.eq(enterprise.getDomain()))
				.and(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.isNotNull())
				.and(ENTERPRISE_CCC.GEOZONE.isNotNull())
				.and(ENTERPRISE_CCC.CCC.isNotNull())
				.fetchStreamInto(ENTERPRISE_CCC).forEach(enterpriseCcc->{
					System.out.println("SET WORKPLACE DOMAIN "+enterpriseCcc.getDomain()+" CCC "+ enterpriseCcc.getCcc());
					setWorkplace(dslContext, enterpriseCcc.getDomain(), enterprise, enterpriseCcc.getEnterpriseActivity(), enterpriseCcc.getCcc(), enterpriseCcc.getGeozone());
				})
			);

			System.out.println("[table 'workplace' UPDATED!]");
		} catch (Throwable t) {
			System.out.println("[table 'workplace' NOT UPDATED!]");
		}
		System.out.println("[END]");
	}
	
	private static void setWorkplace(DSLContext dslContext, Integer domainId, EnterpriseRecord enterpriseRecord, Integer enterpriseActivityId, String ccc, Integer geozoneId){
		Integer registry = enterpriseRecord.getRegistry();
		
		///---RADDRESS
		RaddressRecord raddress = dslContext
		.select()
		.from(RADDRESS)
		.where(RADDRESS.DOMAIN.eq(domainId))
		.and(RADDRESS.GEOZONE.eq(geozoneId))
		.and(RADDRESS.REGISTRY.eq(registry))
		.fetchStreamInto(RADDRESS).findFirst()
		.orElseGet(() ->
			dslContext
			.insertInto(RADDRESS)
			.set(RADDRESS.DOMAIN, domainId)
			.set(RADDRESS.GEOZONE, geozoneId )
			.set(RADDRESS.TYPE, (byte)0)
			.set(RADDRESS.REGISTRY, registry)
			.returning()
			.fetchOne()
		);
		
		//---------WORKPLACE
		WorkplaceRecord workplaceRecord = dslContext
		.select()
		.from(WORKPLACE)
		.innerJoin(RADDRESS).onKey()
		.innerJoin(GEOZONE).onKey()
		.where(WORKPLACE.DOMAIN.eq(domainId))
		.and(GEOZONE.CODE.eq(ccc.substring(0,2)))
		.fetchStreamInto(WORKPLACE).findFirst()
		.orElseGet(() ->
			dslContext
			.insertInto(WORKPLACE)
			.set(WORKPLACE.DOMAIN, domainId)
			.set(WORKPLACE.ENTERPRISE, registry)
			.set(WORKPLACE.DESCRIPTION, "CT AUTOGENERADO")
			.set(WORKPLACE.ADDRESS, raddress.getId())
			.set(WORKPLACE.SCOPE, enterpriseRecord.getScope())
			.returning()
			.fetchOne()
		);
		
		//---------PAYROLL_WORKPLACE
		PayrollWorkplaceRecord payrollRecord = dslContext
		.select()
		.from(PAYROLL_WORKPLACE)
		.innerJoin(WORKPLACE).onKey()
		.where(WORKPLACE.DOMAIN.eq(domainId))
		.fetchStreamInto(PAYROLL_WORKPLACE).findFirst()
		.orElseGet(() ->
				dslContext
				.insertInto(PAYROLL_WORKPLACE)
				.set(PAYROLL_WORKPLACE.DOMAIN, domainId)
				.set(PAYROLL_WORKPLACE.WORKPLACE, workplaceRecord.getId())
				.set(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY, enterpriseActivityId)
				.returning()
				.fetchOne()
		);

		if(null == payrollRecord.getEnterpriseActivity()){
			dslContext
			.update(PAYROLL_WORKPLACE)
			.set(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY, enterpriseActivityId)
			.where(PAYROLL_WORKPLACE.ID.in(payrollRecord.getId()))
			.execute();
		}

	}
	
	

}

package com.esferalia.aon.in.payroll.tgss.idc;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.tgss.idc.SSBonusListener.SSBonus;
import com.esferalia.aon.occam.api.AONContext;

public class SSBonusSync {
	
	private void syncSSBonus(DSLContext dslContext, InputStream is) {
		SSBonusListener ssBonusListener = new SSBonusListener();
		try {
			IdcplcccParser.parse(is, ssBonusListener);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnknownPDFException e) {
			try {
				IdcParser.parse(is, ssBonusListener);
			} catch (IOException ex) {
				e.printStackTrace();
			} catch (UnknownPDFException ex) {
				ex.printStackTrace();
			}
		}
		
		if(!ssBonusListener.getSSBonus().isEmpty()) {
			ssBonusListener.getSSBonus().forEach(b -> System.out.println(b.toString()));
			for(SSBonus ssBonus : ssBonusListener.getSSBonus())
				deleteRepeatContractBonus(dslContext, ssBonus);
			
			for(SSBonus ssBonus : ssBonusListener.getSSBonus())
				insertContractBonus(dslContext, ssBonus);
		}
		
	}

	private void deleteRepeatContractBonus(DSLContext dslContext, SSBonus ssBonus) {
		String ssNum = ssBonus.getSsNum();
		String ccc = ssBonus.getCcc();
		
		Integer enterpriseCCCId = getEnterpriseCCCId(dslContext, ccc);
		Record contractRecord = getContractRecord(dslContext, ssNum, enterpriseCCCId);
		
		Integer contractId = contractRecord.get(CONTRACT.ID);
		Date startDate = new Date(ssBonus.getStartDate().getTime());
		Date endDate = null == ssBonus.getEndDate() ? null : new Date(ssBonus.getEndDate().getTime());
		
		dslContext.delete(CONTRACT_BONUS)
			.where(CONTRACT_BONUS.CONTRACT.eq(contractId))
			.and(CONTRACT_BONUS.START_DATE.equal(startDate))
			.and(CONTRACT_BONUS.END_DATE.equal(endDate))
			.execute();
	}
	
	private void insertContractBonus(DSLContext dslContext, SSBonus ssBonus) {
		String ssNum = ssBonus.getSsNum();
		String ccc = ssBonus.getCcc();
		
		Integer enterpriseCCCId = getEnterpriseCCCId(dslContext, ccc);
		Record contractRecord = getContractRecord(dslContext, ssNum, enterpriseCCCId);
		
		Integer contractId = contractRecord.get(CONTRACT.ID);
		Integer domainId = contractRecord.get(CONTRACT.DOMAIN);
		Date startDate = new Date(ssBonus.getStartDate().getTime());
		Date endDate = null == ssBonus.getEndDate() ? null : new Date(ssBonus.getEndDate().getTime());
		
		dslContext.insertInto(CONTRACT_BONUS)
			.set(CONTRACT_BONUS.DOMAIN, domainId)
			.set(CONTRACT_BONUS.CONTRACT, contractId)
			.set(CONTRACT_BONUS.DESCRIPTION, ssBonus.getDescription())
			.set(CONTRACT_BONUS.EXPRESSION, ssBonus.getFormula())
			.set(CONTRACT_BONUS.START_DATE, startDate)
			.set(CONTRACT_BONUS.END_DATE, endDate)
			.execute();
	}

	private Record getContractRecord(DSLContext dslContext, String ssNum, Integer enterpriseCCCId) {
		Record contractRecord = dslContext.select().from(CONTRACT)
				.where(CONTRACT.PERSON.eq(
						dslContext.select(PERSON.REGISTRY).from(PERSON)
							.where(PERSON.SOCIAL_SECURITY_NUM.eq(ssNum)).fetchOne(PERSON.REGISTRY)))
				.and(CONTRACT.ENTERPRISE_CCC.eq(enterpriseCCCId))
				.orderBy(CONTRACT.START_DATE.desc())
				.limit(1)
				.fetchOne();
		
		return contractRecord;
	}

	private Integer getEnterpriseCCCId(DSLContext dslContext, String ccc) {
		Result<Record1<Integer>> enterpriseCCCRecords = dslContext.select(ENTERPRISE_CCC.ID).from(ENTERPRISE_CCC)
				.where(ENTERPRISE_CCC.CCC.eq(ccc))
				.fetch();
		
		return enterpriseCCCRecords.get(0).get(ENTERPRISE_CCC.ID);
	}

	public static void main(String[] args) {
		try {
			Connection connection = DriverManager.getConnection(
					String.format(
							"jdbc:mysql://%s:%d/%s",
							"127.0.0.1",
							3306, 
							"ayudat-aonsolutions-net"),
					"root",
					"r00t");
			
			// Get dslContext for given connection
			DSLContext dslContext = new AONContext(connection).getDslContext();
			
			SSBonusSync ssBonusSync = new SSBonusSync();
			ssBonusSync.syncSSBonus(dslContext, null);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

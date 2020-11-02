package com.esferalia.aon.in.payroll.tgss.idc;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;

final class SSBonusListener extends DefaultListener implements Listener {
	
	static final int[] ssBonusCodes = new int[] { 1, 2, 13, 15, 16, 37, 41, 46, 48, 51, 52, 54, 55 };
	
	@SuppressWarnings("serial")
	static final Map<String, String> quotaFracctionMap = new HashMap<String, String>() {
		{
			put("01", "(IT_E + IMS_E + DESMPL_E + FOGASA_E + FP_E)");
			put("02", "DESMPL_E");
			put("03", "CGC_E");
			put("04", "(DESMPL + DESMPL_E)");
			put("05", "DESMPL");
			put("06", "(DESMPL + DESMPL_E + FP + FP_E + FOGASA + FOGASA_E)");
			put("07", "(CGC_E + DESMPL_E + FOGASA_E + FP_E)");
			put("08", "(CGC + IT + IMS + DESMPL + FOGASA + FP)");
			put("09", "CGC_E");
			put("10", "(CGC + CGC_E + DESMPL + DESMPL_E + FP + FP_E + FOGASA + FOGASA_E)");
			put("11", "");
			put("12", "(DESMPL + DESMPL_E + FOGASA + FOGASA_E)");
			put("13", "(FOGASA + FOGASA_E)");
			put("14", "");
			put("15", "");
			put("16", "");
			put("17", "");
			put("18", "");
			put("19", "");
			put("20", "");
			put("21", "");
			put("22", "");
			put("23", "");
			put("24", "(CGC + CGC_E + IT + IT_E)");
			put("25", "");
			put("26", "");
			put("27", "(IT + IT_E + IMS + IMS_E + DESMPL + DESMPL_E + FOGASA + FOGASA_E)");
			put("28", "(IT + IT_E + IMS + IMS_EFOGASA + FOGASA_E)");
			put("29", "");
			put("30", "");
			put("31", "");
			put("32", "");
			put("33", "");
			put("34", "");
			put("35", "");
			put("36", "");
			put("37", "");
			put("38", "(IT + IT_E + IMS + IMS_E)");
			put("39", "(IT + IT_E)");
			put("40", "(CGC + CGC_E + DESMPL + DESMPL_E)");
			put("41", "(IT + IT_E + IMS + IMS_E)");
			put("42", "");
			put("43", "(CGC + CGC_E)");
			put("44", "(IT + IT_E + CGC + CGC_E + DESMPL + DESMPL_E + FOGASA + FOGASA_E)");
			put("45", "(IT + IT_E + CGC + CGC_E + FOGASA + FOGASA_E)");
			put("46", "");
			put("47", "");
			put("48", "");
			put("49", "");
			put("50", "");
			put("51", "");
			put("52", "");
			put("53", "(DESMPL + DESMPL_E + FOGASA + FOGASA_E + FP + FP_E)");
			put("54", "CGC");
			put("55", "");
			put("56", "");
			put("57", "(CGC + IT + IMS + DESMPL + FOGASA + FP + CGC_E + IT_E + IMS_E + DESMPL_E + FOGASA_E + FP_E)");
			put("58", "");
			put("59", "");
			put("60", "");
			put("61", "");
			put("62", "");
			put("63", "");
			put("64", "");
			put("65", "");
			put("68", "(CGC + CGC_E + CGP + CGP_E)");
			put("69", "");
			put("70", "");
			put("71", "");
			put("72", "");
			put("73", "");
			put("74", "");
			put("75", "");
			put("76", "");
			put("77", "");
			put("78", "(FP + FP_E)");
			put("79", "(DESMPL + DESMPL_E + FP + FP_E)");
			put("80", "");
			put("81", "");
		}
	};
	
	private DSLContext dslContext;
	
	public void setDSLContext(DSLContext dSLContext) {
		this.dslContext = dSLContext;
	}
	
	// ------------------------------------------ LISTENER IMPLEMENTS METHODS --------------------------------------------------------
	
	@Override
	public void onEmployeePerido(String ssNum, String ccc, java.util.Date startDate, java.util.Date endDate) {
//		System.out.println("SS NUM: " + ssNum + " CCC: " + ccc + " DESDE: " + dateFormat.format(startDate) + " HASTA: " + dateFormat.format(endDate));
		deleteRepeatContractBonus(ssNum, ccc, startDate, endDate);
	}
	
	@Override
	public void onEmployeeQuotePEC(String ssNum, String ccc, String code, String description, String portTipo, String quota, java.util.Date start, java.util.Date end) {
		Integer codeInt = Integer.parseInt(code);
		if (IntStream.of(ssBonusCodes).anyMatch(x -> x == codeInt)) {
//			System.out.println("SS NUM: " + ssNum + " CCC: " + ccc + " TIPO DE PECULIARIDAD : " + code + " " + description + " - " + portTipo + "%" + " PORCENTAJE/TIPO: " + portTipo + " FRACCION DE CUOTA: " + quota + " DESDE: " + dateFormat.format(start) + " HASTA: " + dateFormat.format(end));
			SSBonus ssBonus = new SSBonus();
			ssBonus.setSsNum(ssNum);
			ssBonus.setCcc(ccc);
			ssBonus.setStartDate(new Date(start.getTime()));
			ssBonus.setEndDate(new Date(end.getTime()));
			ssBonus.setDescription(code + " " + description + " - " + portTipo + "%");
			String quotaCode = quota.trim().split(" ")[0];
			ssBonus.setFormula("/*idc*/" + SSBonusListener.quotaFracctionMap.get(quotaCode) + " * " + Double.parseDouble(portTipo.replace(",", ".")) / 100);
			insertContractBonus(ssBonus);
		}
	}
	
	@Override
	public void onEmployeeQuotePECList(List<EmployeeQuotePEC> employeeQuotePECList) {
		employeeQuotePECList.forEach(e -> {
			Integer codeInt = Integer.parseInt(e.getCode());
			if (IntStream.of(ssBonusCodes).anyMatch(x -> x == codeInt)) {
//				System.out.println("SS NUM: " + e.getSsNum() + " CCC: " + e.getEnterpriseCCC() + " TIPO DE PECULIARIDAD : " + e.getCode() + " " + e.getDescription() + " - " + e.getType() + "%" + " PORCENTAJE/TIPO: " + e.getType() + " FRACCION DE CUOTA: " + e.getQuota() + " DESDE: " + dateFormat.format(e.getStart()) + " HASTA: " + dateFormat.format(e.getEnd()));
				SSBonus ssBonus = new SSBonus();
				ssBonus.setSsNum(e.getSsNum());
				ssBonus.setCcc(e.getEnterpriseCCC());
				ssBonus.setStartDate(new Date(e.getStart().getTime()));
				ssBonus.setEndDate(new Date(e.getEnd().getTime()));
				ssBonus.setDescription(e.getCode() + " " + e.getDescription() + " - " + e.getType() + "%");
				String quotaCode = e.getQuota().trim().split(" ")[0];
				ssBonus.setFormula("/*idc*/" + SSBonusListener.quotaFracctionMap.get(quotaCode) + " * " + Double.parseDouble(e.getType().replace(",", ".")) / 100);
				insertContractBonus(ssBonus);
			}
		});
	}
	
	// ------------------------------------------- INSERT/DELETE METHODS DB ---------------------------------------------------------
	
	private void deleteRepeatContractBonus(String ssNum, String ccc, java.util.Date startDateJ, java.util.Date endDateJ) {
		Integer enterpriseCCCId = getEnterpriseCCCId(dslContext, ccc);
		Record contractRecord = getContractRecord(dslContext, ssNum, enterpriseCCCId);
		
		Integer contractId = contractRecord.get(CONTRACT.ID);
		Date startDate = new Date(startDateJ.getTime());
		Date endDate = null == endDateJ ? null : new Date(endDateJ.getTime());
		
		dslContext.delete(CONTRACT_BONUS)
			.where(CONTRACT_BONUS.CONTRACT.eq(contractId))
			.and(CONTRACT_BONUS.START_DATE.equal(startDate))
			.and(CONTRACT_BONUS.END_DATE.equal(endDate))
			.execute();
	}
	
	private void insertContractBonus(SSBonus ssBonus) {
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

}
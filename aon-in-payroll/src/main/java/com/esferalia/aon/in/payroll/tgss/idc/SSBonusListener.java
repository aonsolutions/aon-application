package com.esferalia.aon.in.payroll.tgss.idc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

final class SSBonusListener extends DefaultListener implements Listener {
	
	public static class SSBonus {

		private String ssNum;
		private String ccc;
		private Date startDate;
		private Date endDate;
		private String description;
		private Byte type; // Bonus.Type.values()
		private String formula;

		public SSBonus() {
			super();
		}

		public Date getStartDate() {
			return startDate;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Byte getType() {
			return type;
		}

		public void setType(Byte type) {
			this.type = type;
		}

		public String getFormula() {
			return formula;
		}

		public void setFormula(String formula) {
			this.formula = formula;
		}

		public String getSsNum() {
			return ssNum;
		}

		public void setSsNum(String ssNum) {
			this.ssNum = ssNum;
		}

		public String getCcc() {
			return ccc;
		}

		public void setCcc(String ccc) {
			this.ccc = ccc;
		}

		@Override
		public String toString() {
			return "SSBonus -> SS Number : " + getSsNum() + ", CCC : " + getCcc() + ", Description : " + getDescription() + ", Formula : " + getFormula() + ", Start : "
					+ getStartDate() + ", End : " + getEndDate();
		}
	}

	static final int[] ssBonusCodes = new int[] { 1, 2, 13, 15, 16, 37, 41, 46, 48, 51, 52, 54, 55 };
	
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
	
	private List<SSBonus> ssBonuses = new ArrayList<SSBonus>();

	@Override
	public void onEmployeeQuotePEC(String ssNum, String ccc, String code, String description, String portTipo, String quota, Date start, Date end) {
		Integer codeInt = Integer.parseInt(code);
		if (IntStream.of(ssBonusCodes).anyMatch(x -> x == codeInt)) {
//			System.out.println("TIPO DE PECULIARIDAD : " + code + " " + description + " PORCENTAJE/TIPO: " + portTipo + " FRACCION DE CUOTA: " + quota + " DESDE: " + start + " HASTA: " + end);
			SSBonus ssBonus = new SSBonus();
			ssBonus.setSsNum(ssNum);
			ssBonus.setCcc(ccc);
			ssBonus.setStartDate(start);
			ssBonus.setEndDate(end);
			ssBonus.setDescription(code + " " + description);
			String quotaCode = quota.trim().split(" ")[0];
			ssBonus.setFormula(SSBonusListener.quotaFracctionMap.get(quotaCode) + " * " + Double.parseDouble(portTipo.replace(",", ".")) / 100);
			ssBonuses.add(ssBonus);
		}
	}
	
	public List<SSBonus> getSSBonus(){
		return this.ssBonuses;
	}

}
package com.esferalia.aon.payroll.agreement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jooq.tools.StringUtils;

public class Agreement {
	
	public class AgreementLevelData{
		private String name;
		private String value;
		private Date startDate;
		private Date endDate;
		
		public AgreementLevelData(String name, String value, Date startDate) {
			this.name = name;
			this.value = value;
			this.startDate = startDate;
		}
		
		public AgreementLevelData(String name, String value, Date startDate, Date endDate) {
			this.name = name;
			this.value = value;
			this.startDate = startDate;
			this.endDate = endDate;
		}
		
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
		
		public Date getEndDate() {
			return this.endDate;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public Date getStartDate() {
			return startDate;
		}
		
	}
	
	public class AgreementLevel{
		
		private String code;
		private String description;
		private List<String> categories;
		private List<AgreementLevelData> levelDatas;
		
		public AgreementLevel(String code, String description) {
			this.code = code;
			this.description = description;
			this.categories = new ArrayList<>();
			this.levelDatas = new ArrayList<>();
		}
		
		public void addCategory(String category){
			this.categories.add(category);
		}
		
		public void addLevelData(String name, String value, Date startDate) {
			this.levelDatas.add(new AgreementLevelData(name, value, startDate));
		}
		
		public void addLevelData(String name, String value, Date startDate, Date endDate) {
			this.levelDatas.add(new AgreementLevelData(name, value, startDate, endDate));
		}
		
		public String getDescription() {
			return this.description;
		}
		
		public String getCode() {
			return this.code;
		}
		
		public void setEndDateToExistingLevelData(Date endDate) {
			for(AgreementLevelData levelData : this.levelDatas) {
				if(null == levelData.getEndDate())
					levelData.setEndDate(endDate);
			}
		}
		
		public List<String> getLevelCategories(){
			return this.categories;
		}
		
		public List<AgreementLevelData> getLevelDatas(){
			return this.levelDatas;
		}
		
		public void setLevelDatas(List<AgreementLevelData> levelDatas){
			this.levelDatas = levelDatas;
		}

		public List<String> getCategories() {
			return categories;
		}

		public void setCategories(List<String> categories) {
			this.categories = categories;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public void setDescription(String description) {
			this.description = description;
		}
		
	}
	
	private String description;
	private String ssCode;
	private String serviAgreementCode;
	private Date lastUpdate;
	private Date startDate;
	private List<AgreementLevel> levels;
	private List<String> agreementConcepts;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	public Agreement(String description, String ssCode, String serviAgreementCode, Date lastUpdate, Date startDate) {
		this.description = description;
		this.ssCode = ssCode;
		this.serviAgreementCode = serviAgreementCode;
		this.lastUpdate = lastUpdate;
		this.startDate = startDate;
		this.levels = new ArrayList<>();
		this.agreementConcepts = new ArrayList<>();
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSsCode() {
		return ssCode;
	}

	public void setSsCode(String ssCode) {
		this.ssCode = ssCode;
	}

	public List<AgreementLevel> getLevels() {
		return levels;
	}

	public void setLevels(List<AgreementLevel> levels) {
		this.levels = levels;
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public void setServiAgreementCode(String serviAgreementCode) {
		this.serviAgreementCode = serviAgreementCode;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setAgreementConcepts(List<String> agreementConcepts) {
		this.agreementConcepts = agreementConcepts;
	}

	public void addAgreementConcept(String concept){
		this.agreementConcepts.add(concept);
	}
	
	public void addAgreementLevel(String code, String description, String category) {
		AgreementLevel agreementLevel = new AgreementLevel(code, description);
		agreementLevel.addCategory(category);
		this.levels.add(agreementLevel);
		
		// TODO : esto es por si se crea un nivel con muchas categorias, pero de momento parece que no se va a poder...
//		AgreementLevel levelExist = checkLevelExist(description);
//		
//		// Si no existe se añade
//		if(null == levelExist) {
//			AgreementLevel agreementLevel = new AgreementLevel(code, description);
//			agreementLevel.addCategory(category);
//			this.levels.add(agreementLevel);
//		} else {
//			//Si existe el nivel, se añade la category
//			levelExist.addCategory(category);
//		}
	}
	
	public void addAgreementLevel(AgreementLevel agreementLevel) {
		this.levels.add(agreementLevel);
	}

	public AgreementLevel checkLevelExist(String description) {
		if(this.levels.isEmpty())
			return null;
		
		for(AgreementLevel lvl : this.levels) {
			if(StringUtils.equals(lvl.getDescription(), description))
				return lvl;
		}
		
		return null;
	}
	
	public void setEndDateToExistingLevelData(Date endDate) {
		for(AgreementLevel lvl : this.levels) {
			lvl.setEndDateToExistingLevelData(endDate);
		}
	}
	
	public AgreementLevel getAgreementLevel(String description, String category) {
		for(AgreementLevel lvl : this.levels) {
			if((lvl.description == description || lvl.description.equals(description)) && lvl.categories.contains(category))
				return lvl;
		}
		
		return null;
	}
	
	public String getServiAgreementCode() {
		return this.serviAgreementCode;
	}
	
	public String getSSCode() {
		return this.ssCode;
	}
	
	public String getAgreementDescription() {
		return this.description;
	}
	
	public List<String> getAgreementConcepts(){
		return this.agreementConcepts;
	}
	
	public List<AgreementLevel> getAgreementLevels(){
		return this.levels;
	}
	
	public Date getStartDate() {
		return this.startDate;
	}
	
	public Date getLastUpdate() {
		return this.lastUpdate;
	}
	
	@Override
	public String toString() {
		String result = "---------------------- AGREEMENT ---------------------- \n";
		result += "Description : " + this.description + "\n";
		result += "SSCode : " + this.ssCode + "\n";
		result += "ServiConvenios Code : " + this.serviAgreementCode + "\n";
		result += "Last Update : " + this.lastUpdate + "\n";
		result += "\n";
		result += "---------------------- AGREEMENT CONCEPTS ---------------------- \n";
		result += "Agreement Concepts -> [ \n";
		for(String concept : this.agreementConcepts) {
			result += "\t" + concept + "\n";
		}
		result += "]\n";
		result += "\n";
		result += "---------------------- AGREEMENT LEVELS ---------------------- \n";
		for(AgreementLevel lvl : this.levels) {
			result += "Level Desription : " + lvl.description + "\n";
			result += "Level Categories -> [";
			for(String category : lvl.categories)
				result += category + ", ";
			result = result.substring(0, result.length() - 2);
			result += "] \n";
			result += "Level Data -> [ \n";
			for(AgreementLevelData levelData : lvl.levelDatas) {
				result += "\t StartDate : " + dateFormat.format(levelData.startDate) + " EndDate : " + (null == levelData.endDate ? null : dateFormat.format(levelData.endDate)) + " Name : " + levelData.name + " Value : " + levelData.value + "\n";
			}
			result += "] \n";
			result += "\n";
		}
		
		return result;
	}

	public AgreementLevel createAgreementLevel(String code, String description) {
		return new AgreementLevel(code, description);
	}
}

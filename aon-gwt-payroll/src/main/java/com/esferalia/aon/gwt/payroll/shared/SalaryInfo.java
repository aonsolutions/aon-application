package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.google.gwt.view.client.ProvidesKey;

public class SalaryInfo implements Serializable {
	
	public enum AlcatrazPeriod {
		ENERO("Enero"),
		FEBRERO("Febrero"),
		MARZO("MArzo"),
		ABRIL("Abril"),
		MAYO("Mayo"),
		JUNIO("Junio"),
		JULIO("Julio"),
		AGOSTO("Agosto"),
		SEPTIEMBRE("Septiembre"),
		OCTUBRE("Octubre"),
		NOVIEMBRE("Noviembre"),
		DICIEMBRE("Diciembre"),
		TRIMESTRE_1("1\u00b0 Trimestre"),
		TRIMESTRE_2("2\u00b0 Trimestre"),
		TRIMESTRE_3("3\u00b0 Trimestre"),
		TRIMESTRE_4("4\u00b0 Trimestre")
		;
		
		private String description;
		
		AlcatrazPeriod(String description){
			this.description = description;
		}
		
		public String getDescription() {return description;}
	}
	
	public enum AlcatrazTerritory {
		ARABA("Araba/Alaba"),
		BIZKAIA("Bizkaia"),
		GIPUZKOA("Gipuzkoa"),
		NAVARRA("Navarra"),
		COMUN("Territorio Com\u00fan")
		;
		
		private String description;
		
		AlcatrazTerritory(String description){
			this.description = description;
		}
		
		public String getDescription() {return description;}
	}

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer contract;
	private String startDate;
	private String endDate;
	private Type type;
	private String enterpriseName;
	private Integer enterpriseId;
	private String workplaceName;
	private Integer workplaceId;
	private String employeeName;
	private Double totalPayment;
	private Double totalDeduction;
	private Double totalLiquid;
	
	private boolean isAlcatraz;
	private Integer alcatrazYear;
	private AlcatrazPeriod alcatrazPeriod;
	private AlcatrazTerritory alcatrazTerritory;
	
	private boolean isFinance; // Vencimiento
	
	/**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<SalaryInfo> KEY_PROVIDER = new ProvidesKey<SalaryInfo>() {
      @Override
      public Object getKey(SalaryInfo item) {
        return item == null ? null : item.getId();
      }
    };
	
	public SalaryInfo() {
		super();
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDomain() {
		return domain;
	}
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	public Integer getContract() {
		return contract;
	}
	public void setContract(Integer contract) {
		this.contract = contract;
	}
	public Date getStartDate() {
		return parse(startDate);
	}
	public void setStartDate(Date startDate) {
		this.startDate = format(startDate);
	}
	public Date getEndDate() {
		return parse(endDate);
	}
	public void setEndDate(Date endDate) {
		this.endDate = format(endDate);
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}
	public Integer getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Integer enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getWorkplaceName() {
		return workplaceName;
	}
	public void setWorkplaceName(String workplaceName) {
		this.workplaceName = workplaceName;
	}
	public Integer getWorkplaceId() {
		return workplaceId;
	}
	public void setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public Double getTotalPayment() {
		return totalPayment;
	}
	public void setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
	}
	public Double getTotalDecuction() {
		return totalDeduction;
	}
	public void setTotalDeduction(Double totalDeduction) {
		this.totalDeduction = totalDeduction;
	}
	public Double getTotalLiquid() {
		return totalLiquid;
	}
	public void setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
	}
	public boolean isAlcatraz() {
		return isAlcatraz;
	}
	public void setAlcatraz(boolean isAlcatraz) {
		this.isAlcatraz = isAlcatraz;
	}
	public Integer getAlcatrazYear() {
		return alcatrazYear;
	}
	public void setAlcatrazYear(Integer alcatrazYear) {
		this.alcatrazYear = alcatrazYear;
	}
	public AlcatrazPeriod getAlcatrazPeriod() {
		return alcatrazPeriod;
	}
	public void setAlcatrazPeriod(AlcatrazPeriod alcatrazPeriod) {
		this.alcatrazPeriod = alcatrazPeriod;
	}
	public AlcatrazTerritory getAlcatrazTerritory() {
		return alcatrazTerritory;
	}
	public void setAlcatrazTerritory(AlcatrazTerritory alcatrazTerritory) {
		this.alcatrazTerritory = alcatrazTerritory;
	}
	public boolean isFinance() {
		return isFinance;
	}
	public void setFinance(boolean isFinance) {
		this.isFinance = isFinance;
	}
	
}

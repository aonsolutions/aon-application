package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.view.client.ProvidesKey;

public class IT implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Byte typeLowPart;
	private Integer contract;
	private String description;
	private Date startDate;
	private Date endDate;
	private Double dailyCGCBase;
	private Double dailyCGPBase;
	private Integer parent; // Parent when is relapse
	private Double dailyREGBase;
	private Byte typeHighPart;
	private Boolean isParent;
	private Byte maternityType;
	private Byte maternityReason;
	private Double baseReg;
	private Double partialityCoef;
	
	private Date comunicationDate;
	private Boolean isComunicate;
	
	private Date directPayDate;
	
	private String fullName;
	
	private List<ITPart> itParts;
	
	/**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<IT> KEY_PROVIDER = new ProvidesKey<IT>() {
      @Override
      public Object getKey(IT item) {
        return item == null ? null : item.getId();
      }
    };
	
	public IT() {
		super();
		this.itParts = new ArrayList<ITPart>();
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

	public Byte getTypeLowPart() {
		return null == typeLowPart ? 0 : typeLowPart;
	}

	public void setTypeLowPart(Byte typeLowPart) {
		this.typeLowPart = typeLowPart;
	}

	public Integer getContract() {
		return contract;
	}

	public void setContract(Integer contract) {
		this.contract = contract;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Double getDailyCGCBase() {
		return dailyCGCBase;
	}

	public void setDailyCGCBase(Double dailyCGCBase) {
		this.dailyCGCBase = dailyCGCBase;
	}

	public Double getDailyCGPBase() {
		return dailyCGPBase;
	}

	public void setDailyCGPBase(Double dailyCGPBase) {
		this.dailyCGPBase = dailyCGPBase;
	}

	public Integer getParent() {
		return null == parent ? 0 : parent;
	}

	public void setParent(Integer parent) {
		this.parent = parent;
	}

	public Double getDailyREGBase() {
		return dailyREGBase;
	}

	public void setDailyREGBase(Double dailyREGBase) {
		this.dailyREGBase = dailyREGBase;
	}

	public Byte getTypeHighPart() {
		return typeHighPart;
	}

	public void setTypeHighPart(Byte typeHighPart) {
		this.typeHighPart = typeHighPart;
	}

	public List<ITPart> getITParts() {
		return itParts;
	}

	public void setITParts(List<ITPart> itParts) {
		this.itParts = itParts;
	}
	
	public void addITPart(ITPart itPart) {
		this.itParts.add(itPart);
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Byte getMaternityType() {
		return maternityType;
	}

	public void setMaternityType(Byte maternityType) {
		this.maternityType = maternityType;
	}

	public void setRegulationBase(Double baseReg) {
		this.baseReg = baseReg;
	}
	
	public Double getRegulationBase() {
		return this.baseReg;
	}
	
	public void setPartialityCoef(Double partialityCoef) {
		this.partialityCoef = partialityCoef;
	}
	
	public Double getPartialityCoef() {
		return this.partialityCoef;
	}
	
	public Byte getMaternityReason() {
		return maternityReason;
	}

	public void setMaternityReason(Byte maternityReason) {
		this.maternityReason = maternityReason;
	}

	public Boolean isComunicate() {
		return isComunicate;
	}

	public void setIsComunicate(Boolean isComunicate) {
		this.isComunicate = isComunicate;
	}

	public Date getComunicationDate() {
		return comunicationDate;
	}

	public void setComunicationDate(Date comunicationDate) {
		this.comunicationDate = comunicationDate;
	}

	public Date getDirectPayDate() {
		return directPayDate;
	}

	public void setDirectPayDate(Date directPayDate) {
		this.directPayDate = directPayDate;
	}
	
}

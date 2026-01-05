package com.esferalia.aon.occam.api.model.fiscal;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod190 implements IFiscalModel, HasAudit {

	private static final long serialVersionUID = -853765073923154482L;
	
	private Integer id;
	private int domain;
	private int enterprise;
	private int year;
	private Administration administration;
	private FiscalStatus status;
	private boolean confidential;
	private boolean replacement;
	private boolean complementary;
	private String receipt;
	private String replacedReceipt;
	private String comments;
	private String document;
	private String name;
	private String contactPerson;
	private String contactPhone;
	private String contactMail;
	private int receiverCountTotal;
	private double receiptTotal;
	private double retentionTotal;
	
	private LinkedList<Mod190Detail> details;
	
	private String domainName;

	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	private boolean useChargeDate;
	
	// Nuevos campos para el ejercicio 2025 (ARABA, BIZKAIA y GIPUZKOA):
	// Aportaciones a planes de previsión social preferentes y Contribuciones empresariales al resto de sistemas de empleo 
	private double preferredContributions;        	 	// Preferentes: Aportaciones
	private double preferredContributionsUnder36; 	 	// Preferentes: Contribuciones empresariales a favor de personas menores de 36 años
	private double preferredContributionsOver36;  	 	// Preferentes: Contribuciones empresariales a favor de personas de 36 años o más
//	private double preferredContributionsTotal; 	 	// Preferentes: Contribuciones empresariales totales
	private double preferredGrossAnnualSalary; 		 	// Preferentes: Salario bruto anual de la entidad	
//	private double preferredContributionsPercentage1; 	// Preferentes: Porcentaje de las aportaciones y contribuciones sobre el salario bruto anual	
//	private double preferredContributionsPercentage2; 	// Preferentes: Porcentaje de las contribuciones sobre el salario bruto anual
	private double otherContributionsUnder36; 		  	// Resto sistemas de empleo: Contribuciones empresariales a favor de personas menores de 36 años
	private double otherContributionsOver36; 		  	// Resto sistemas de empleo: Contribuciones empresariales a favor de personas de 36 años o más
//	private double otherContributionsTotal; 			// Resto sistemas de empleo: Contribuciones empresariales totales
	private double otherGrossAnnualSalary; 				// Resto sistemas de empleo: Salario bruto anual de la entidad
//	private double otherContributionsPercentage; 		// Resto sistemas de empleo: Porcentaje de las contribuciones sobre el salario bruto anual
	
	@Override
	public Integer getId() { 
		return id;
	}
	public Mod190 setId(Integer id) {
		this.id = id;
		return this;
	}
	public boolean isNew() {
		return id==null;
	}

	@Override
	public int getDomain() {
		return domain;
	}
	public Mod190 setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	@Override
	public String getDomainName() {
		return domainName;
	}
	public Mod190 setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public int getEnterprise() {
		return enterprise;
	}

	public Mod190 setEnterprise(int enterprise) {
		this.enterprise = enterprise;
		return this;
	}

	@Override
	public int getYear() {
		return year;
	}
	public Mod190 setYear(int year) {
		this.year = year;
		return this;
	}
	
	@Override
	public Administration getAdministration() {
		return administration;
	}
	public Mod190 setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}

	@Override
	public FiscalStatus getStatus() {
		return status;
	}
	public Mod190 setStatus(FiscalStatus status) {
		this.status = status;
		return this;
	}

	public boolean isConfidential() {
		return confidential;
	}
	public Mod190 setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}

	@Override
	public boolean isReplacement() {
		return replacement;
	}
	public Mod190 setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}
	
	@Override
	public boolean isComplementary() {
		return complementary;
	}
	public Mod190 setComplementary(boolean complementary) {
		this.complementary = complementary;
		return this;
	}

	public String getReceipt() {
		return receipt;
	}
	public Mod190 setReceipt(String receipt) {
		this.receipt = receipt;
		return this;
	}

	public String getReplacedReceipt() {
		return replacedReceipt;
	}
	public Mod190 setReplacedReceipt(String replacedReceipt) {
		this.replacedReceipt = replacedReceipt;
		return this;
	}

	public String getComments() {
		return comments;
	}
	public Mod190 setComments(String comments) {
		this.comments = comments;
		return this;
	}

	@Override
	public String getDocument() {
		return document;
	}
	public Mod190 setDocument(String document) {
		this.document = document;
		return this;
	}

	@Override
	public String getName() {
		return name;
	}
	public Mod190 setName(String name) {
		this.name = name;
		return this;
	}
	
	@Override
	public String getSurname() {
		return null;
	}

	@Override
	public String getFullName() {
		return name;
	}

	public String getContactPerson() {
		return contactPerson;
	}
	public Mod190 setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
		return this;
	}

	public String getContactPhone() {
		return contactPhone;
	}
	public Mod190 setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
		return this;
	}

	public String getContactMail() {
		return contactMail;
	}
	public Mod190 setContactMail(String contactMail) {
		this.contactMail = contactMail;
		return this;
	}

	public int getReceiverCountTotal() {
		return receiverCountTotal;
	}
	public Mod190 setReceiverCountTotal(int receiverCountTotal) {
		this.receiverCountTotal = receiverCountTotal;
		return this;
	}

	public double getReceiptTotal() {
		return receiptTotal;
	}
	public Mod190 setReceiptTotal(double receiptTotal) {
		this.receiptTotal = receiptTotal;
		return this;
	}

	public double getRetentionTotal() {
		return retentionTotal;
	}
	public Mod190 setRetentionTotal(double retentionTotal) {
		this.retentionTotal = retentionTotal;
		return this;
	}

	public LinkedList<Mod190Detail> getDetails() {
		if (details == null) {
			details = new LinkedList<Mod190Detail>();
		}
		return details;
	}
	public Mod190 setDetails(LinkedList<Mod190Detail> details) {
		this.details = details;
		return this;
	}

	@Override
	public IFiscalModelKey getDeclarationTypeKey() {
		return null;
	}
	
	@Override
	public double getResult() {
		return 0;
	}

	public boolean mustUseChargeDate() {
		return useChargeDate;
	}
	public void setUseChargeDate(boolean useChargeDate) {
		this.useChargeDate = useChargeDate;
	}

	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Mod190 setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public Mod190 setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Mod190 setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public Mod190 setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	@Override
	public FiscalModelType getModel() {
		return FiscalModelType.M190;
	}

	@Override
	public Period getPeriod() {
		return Period.YEAR;
	}

	@Override
	public Double getDeclarationResult() {
		return null;
	}
	@Override
	public FiscalModelDeclarationType getDeclarationResultType() {
		return null;
	}
	
	public double getPreferredContributions() {
		return preferredContributions;
	}
	public void setPreferredContributions(double preferredContributions) {
		this.preferredContributions = preferredContributions;
	}
	public double getPreferredContributionsUnder36() {
		return preferredContributionsUnder36;
	}
	public void setPreferredContributionsUnder36(double preferredContributionsUnder36) {
		this.preferredContributionsUnder36 = preferredContributionsUnder36;
	}
	public double getPreferredContributionsOver36() {
		return preferredContributionsOver36;
	}
	public void setPreferredContributionsOver36(double preferredContributionsOver36) {
		this.preferredContributionsOver36 = preferredContributionsOver36;
	}
	// Preferentes: Contribuciones empresariales totales
	public double getPreferredContributionsTotal() {
		// Campo calculado: contribuciones menores de 36 + contribuciones mayores de 36
		return preferredContributionsUnder36 + preferredContributionsOver36;
	}
//	public void setPreferredContributionsTotal(double preferredContributionsTotal) {
//		this.preferredContributionsTotal = preferredContributionsTotal;
//	}
	public double getPreferredGrossAnnualSalary() {
		return preferredGrossAnnualSalary;
	}
	public void setPreferredGrossAnnualSalary(double preferredGrossAnnualSalary) {
		this.preferredGrossAnnualSalary = preferredGrossAnnualSalary;
	}
	// Preferentes: Porcentaje de las aportaciones y contribuciones sobre el salario bruto anual	
	public double getPreferredContributionsPercentage1() {
		// Campo calculado: (aportaciones + contribuciones) / salario bruto anual * 100
		double result = 0.0;
		if (preferredGrossAnnualSalary != 0) {
			result = ( (preferredContributions + getPreferredContributionsTotal()) / preferredGrossAnnualSalary ) * 100;
			result = AonMathUtils.round(result); // redondeo a 2 decimales
		} 
		return result;
	}
//	public void setPreferredContributionsPercentage1(double preferredContributionsPercentage1) {
//		this.preferredContributionsPercentage1 = preferredContributionsPercentage1;
//	}
	// Preferentes: Porcentaje de las contribuciones sobre el salario bruto anual
	public double getPreferredContributionsPercentage2() {
		// Campo calculado: contribuciones / salario bruto anual * 100
		double result = 0.0;
		if (preferredGrossAnnualSalary != 0) {
			result = ( getPreferredContributionsTotal() / preferredGrossAnnualSalary ) * 100;
			result = AonMathUtils.round(result); // redondeo a 2 decimales
		}
		return result;
	}
//	public void setPreferredContributionsPercentage2(double preferredContributionsPercentage2) {
//		this.preferredContributionsPercentage2 = preferredContributionsPercentage2;
//	}
	public double getOtherContributionsUnder36() {
		return otherContributionsUnder36;
	}
	public void setOtherContributionsUnder36(double otherContributionsUnder36) {
		this.otherContributionsUnder36 = otherContributionsUnder36;
	}
	public double getOtherContributionsOver36() {
		return otherContributionsOver36;
	}
	public void setOtherContributionsOver36(double otherContributionsOver36) {
		this.otherContributionsOver36 = otherContributionsOver36;
	}
//	private double otherContributionsTotal; 			// Resto sistemas de empleo: Contribuciones empresariales totales
	public double getOtherContributionsTotal() {
		// Campo calculado: contribuciones menores de 36 + contribuciones mayores de 36
		return otherContributionsUnder36 + otherContributionsOver36;		
	}
//	public void setOtherContributionsTotal(double otherContributionsTotal) {
//		this.otherContributionsTotal = otherContributionsTotal;
//	}
	public double getOtherGrossAnnualSalary() {
		return otherGrossAnnualSalary;
	}
	public void setOtherGrossAnnualSalary(double otherGrossAnnualSalary) {
		this.otherGrossAnnualSalary = otherGrossAnnualSalary;
	}
	// Resto sistemas de empleo: Porcentaje de las contribuciones sobre el salario bruto anual
	public double getOtherContributionsPercentage() {
		// Campo calculado: contribuciones / salario bruto anual * 100
		double result = 0.0;
		if (otherGrossAnnualSalary != 0) {
			result = ( getOtherContributionsTotal() / otherGrossAnnualSalary ) * 100;
			result = AonMathUtils.round(result); // redondeo a 2 decimales
		}
		return result;
	}
//	public void setOtherContributionsPercentage(double otherContributionsPercentage) {
//		this.otherContributionsPercentage = otherContributionsPercentage;
//	}
		
}

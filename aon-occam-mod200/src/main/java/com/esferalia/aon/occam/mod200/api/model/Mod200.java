package com.esferalia.aon.occam.mod200.api.model;

import java.util.Date;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod200 implements IFiscalModel, HasAudit {

	private static final long serialVersionUID = -7631557093162872234L;
	
	private Integer id;
	private int domain;
	private String domainName;
	private int year;
	private Finance finance;
	private Administration administration;
	private FiscalStatus status;
	private boolean complementary;
	private boolean replacement;
	private String number;
	private String replacedNumber;
	private String comments;
	private String document;
	private String surname;
	private String name;
	private String resultType; // Cuota cero (N), Ingreso (I) o Devolución (D)

	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	private Integer fsModel;
	
	private Double amount;	
	
	private String nrc;
	
	@Override
	public Integer getId() {
		return id;
	}
	public Mod200 setId(Integer id) {
		this.id = id;
		return this;
	}
	
	@Override
	public int getDomain() {
		return domain;
	}
	public Mod200 setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	@Override
	public String getDomainName() {
		return domainName;
	}
	public Mod200 setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	
	@Override
	public FiscalModelType getModel() {
		return FiscalModelType.M200;
	}
	
	@Override
	public int getYear() {
		return year;
	}
	public Mod200 setYear(int year) {
		this.year = year;
		return this;
	}
	
	public Finance getFinance() {
		return finance;
	}
	public Mod200 setFinance(Finance finance) {
		this.finance = finance;
		return this;
	}
	
	@Override
	public Administration getAdministration() {
		return administration;
	}
	public Mod200 setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	
	public Mod200 setStatus(FiscalStatus status) {
		this.status = status;
		return this;
	}
	@Override
	public FiscalStatus getStatus() {
		return this.status;
	}
	public boolean isFinished() {
		return getStatus() == FiscalStatus.FINISHED;
	}
	public boolean isNotFinished() {
		return getStatus() != FiscalStatus.FINISHED;
	}

	@Override
	public boolean isReplacement() {
		return replacement;
	}
	public Mod200 setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}
	
	public boolean isComplementary() {
		return complementary;
	}
	public Mod200 setComplementary(boolean complementary) {
		this.complementary = complementary;
		return this;
	}

	@Override
	public String getDocument() {
		return document;
	}
	public Mod200 setDocument(String document) {
		this.document = document;
		return this;
	}
	
	@Override
	public String getName() {
		return name;
	}
	public Mod200 setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public String getSurname() {
		return surname;
	}
	public Mod200 setSurname(String surname) {
		this.surname = surname;
		return this;
	}
	
	@Override
	public String getFullName() {
		return getName(); 
	}
	
	public String getNumber() {
		return number;
	}
	public Mod200 setNumber(String number) {
		this.number = number;
		return this;
	}
	
	public String getReplacedNumber() {
		return replacedNumber;
	}
	public Mod200 setReplacedNumber(String replacedNumber) {
		this.replacedNumber = replacedNumber;
		return this;
	}
	
	public String getComments() {
		return comments;
	}
	public Mod200 setComments(String comments) {
		this.comments = comments;
		return this;
	}
	
	public String getResultType() {
		return resultType;
	}
	public Mod200 setResultType(String resultType) {
		this.resultType = resultType;
		return this;
	}
	public boolean isPayback() {
		return AonStringUtils.equalsIgnoreCase(resultType, "D");
	}
	public boolean isDeposit() {
		return AonStringUtils.equalsIgnoreCase(resultType, "I");
	}
	
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Mod200 setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public Mod200 setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Mod200 setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public Mod200 setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public Integer getFsModel() {
		return fsModel;
	}
	public Mod200 setFsModel(Integer fsModel) {
		this.fsModel = fsModel;
		return this;
	}
	
	public Double getAmount() {
		return amount;
	}

	public Mod200 setAmount(Double amount) {
		this.amount = amount;
		return this;
	}
	
	@Override
	public String getNrc() {		
		return nrc;
	}
	@Override
	public Mod200 setNrc(String nrc) {
		this.nrc = nrc;
		return this;
	}
	
	public boolean isNew() {
		return id==null;
	}
	
	@Override
	public Period getPeriod() {
		return Period.YEAR;
	}
	
	@Override
	public Double getDeclarationResult() {
		// Devuelve el resultado como importe negativo o positivo (recordar que en amount se guarda el importe a ingresar o a devolver, es decir se guarda en valor absoluto)
		return isPayback() ? amount * (-1) : amount;
	}
	
	@Override
	public FiscalModelDeclarationType getDeclarationResultType() {
		// TODO Debería devolver el dato según FiscalModelDeclarationType, si es que realmente se va a usar en algún sitio. En el modelo 200 se usan 3 campos, result_type, dev_type y pay_type
		return null;
	}
	
	@Deprecated
	@Override
	public IFiscalModelKey getDeclarationTypeKey() {
		return null;
	}
	
	@Deprecated	
	@Override
	public double getResult() {
		return getDeclarationResult();
	}
	
	
}

package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.CompanyAdministrator;
import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.Secretary;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod2002014 implements IFiscalModel {
	
	private static final long serialVersionUID = 4332206787096572914L;
	
	public static enum BalanceType implements Serializable {
		NORMAL,
		ABREVIADO,
		PYMES;
	}
	
	private boolean initializedFromLastYear;
	
	private Integer id;
	private int domain;
	private int year;
	private Administration administration = Administration.COMMON_TERRITORY;
	
	private String receipt;
	private boolean complementary;
	private String complementaryReceipt;
	private String cnae;
	
	private int periodType;
	private Date periodStart;
	private Date periodEnd;
	
	private int enterprise;
	private String enterpriseDocument;
	private String enterpriseName;
	private String enterprisePhone1;
	private String enterprisePhone2;
	
	private String fiscalGroup;
	private String dominantDocument;
	
	private BalanceType balanceType;
	private BalanceType pygType;
	
	private Secretary secretary = new Secretary();
	private List<LegalRepresentative> representatives = new LinkedList<LegalRepresentative>();
	private List<CompanyAdministrator> administrators = new LinkedList<CompanyAdministrator>();
	private List<CompanyParticipation> participationsIn = new LinkedList<CompanyParticipation>();
	private List<CompanyParticipation> participationsOut = new LinkedList<CompanyParticipation>();
	
	private String resultType;
	private String devType;
	private String payType;
	private Double amount;
	private String iban;

	private String comments;
	
	private EnumMap<Mod2002014Key,DoubleVariable2014> keysMap = new EnumMap<Mod2002014Key,DoubleVariable2014>(Mod2002014Key.class);
	private EnumMap<Mod2002014Key,DoubleVariable2014> draftMap = new EnumMap<Mod2002014Key,DoubleVariable2014>(Mod2002014Key.class);
	private EnumMap<Mod2002014Key,Boolean> visibleMap = new EnumMap<Mod2002014Key,Boolean>(Mod2002014Key.class);

	private List<ValidationMessage2014> messages;

	public boolean isInitializedFromLastYear() {
		return initializedFromLastYear;
	}
	public void setInitializedFromLastYear(boolean initializedFromLastYear) {
		this.initializedFromLastYear = initializedFromLastYear;
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Override
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	@Override
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public int getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(int enterprise) {
		this.enterprise = enterprise;
	}
	@Override
	public Administration getAdministration() {
		return administration;
	}
	public void setAdministration(Administration administration) {
		this.administration = administration;
	}
	public String getReceipt() {
		return receipt;
	}
	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}
	@Override
	public boolean isComplementary() {
		return complementary;
	}
	public void setComplementary(boolean complementary) {
		this.complementary = complementary;
	}
	public String getComplementaryReceipt() {
		return complementaryReceipt;
	}
	public void setComplementaryReceipt(String complementaryReceipt) {
		this.complementaryReceipt = complementaryReceipt;
	}
	public String getCnae() {
		return cnae;
	}
	public void setCnae(String cnae) {
		this.cnae = cnae;
	}
	public int getPeriodType() {
		return periodType;
	}
	public void setPeriodType(int periodType) {
		this.periodType = periodType;
	}
	public Date getPeriodStart() {
		return periodStart;
	}
	public void setPeriodStart(Date periodStart) {
		this.periodStart = periodStart;
	}
	public Date getPeriodEnd() {
		return periodEnd;
	}
	public void setPeriodEnd(Date periodEnd) {
		this.periodEnd = periodEnd;
	}
	public String getEnterpriseDocument() {
		return enterpriseDocument;
	}
	public void setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
	}
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}
	public String getEnterprisePhone1() {
		return enterprisePhone1;
	}
	public void setEnterprisePhone1(String enterprisePhone1) {
		this.enterprisePhone1 = enterprisePhone1;
	}
	public String getEnterprisePhone2() {
		return enterprisePhone2;
	}
	public void setEnterprisePhone2(String enterprisePhone2) {
		this.enterprisePhone2 = enterprisePhone2;
	}
	public String getFiscalGroup() {
		return fiscalGroup;
	}
	public void setFiscalGroup(String fiscalGroup) {
		this.fiscalGroup = fiscalGroup;
	}
	public String getDominantDocument() {
		return dominantDocument;
	}
	public void setDominantDocument(String dominantDocument) {
		this.dominantDocument = dominantDocument;
	}
	public BalanceType getBalanceType() {
		return balanceType;
	}
	public void setBalanceType(BalanceType balanceType) {
		this.balanceType = balanceType;
	}
	public void setBalanceType(int index) {
		this.balanceType = BalanceType.values()[index];
	}
	
	public BalanceType getPygType() {
		return pygType;
	}
	public void setPygType(BalanceType pygType) {
		this.pygType = pygType;
	}
	public void setPygType(int index) {
		this.pygType = BalanceType.values()[index];
	}
	public Secretary getSecretary() {
		return secretary;
	}
	public void setSecretary(Secretary secretary) {
		this.secretary = secretary;
	}
	public List<LegalRepresentative> getRepresentatives() {
		return representatives;
	}
	public void setRepresentatives(List<LegalRepresentative> representatives) {
		this.representatives = representatives;
	}
	public List<CompanyAdministrator> getAdministrators() {
		return administrators;
	}
	public void setAdministrators(List<CompanyAdministrator> administrators) {
		this.administrators = administrators;
	}
	public List<CompanyParticipation> getParticipationsIn() {
		return participationsIn;
	}
	public void setParticipationsIn(List<CompanyParticipation> participationsIn) {
		this.participationsIn = participationsIn;
	}
	public List<CompanyParticipation> getParticipationsOut() {
		return participationsOut;
	}
	public void setParticipationsOut(List<CompanyParticipation> participationsOut) {
		this.participationsOut = participationsOut;
	}
	public EnumMap<Mod2002014Key, DoubleVariable2014> getKeysMap() {
		return keysMap;
	}
	public void setKeysMap(EnumMap<Mod2002014Key, DoubleVariable2014> keysMap) {
		this.keysMap = keysMap;
	}
	public EnumMap<Mod2002014Key, DoubleVariable2014> getDraftMap() {
		return draftMap;
	}
	public void setDraftMap(EnumMap<Mod2002014Key, DoubleVariable2014> draftMap) {
		this.draftMap = draftMap;
	}
	public EnumMap<Mod2002014Key, Boolean> getVisibleMap() {
		return visibleMap;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getDevType() {
		return devType;
	}
	public void setDevType(String devType) {
		this.devType = devType;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getIban() {
		return iban;
	}
	public void setIban(String iban) {
		this.iban = iban;
	}
	public void setVisibleMap(EnumMap<Mod2002014Key, Boolean> visibleMap) {
		this.visibleMap = visibleMap;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public List<ValidationMessage2014> getMessages() {
		return messages;
	}
	public void setMessages(List<ValidationMessage2014> messages) {
		this.messages = messages;
	}
	public DoubleVariable2014 getKey(Mod2002014Key key) {
		return keysMap.get(key);
	}
	public boolean isChecked(Mod2002014Key key) {
		DoubleVariable2014 dv = keysMap.get(key); 
		return dv != null && (AonMathUtils.round(dv.getValue()) == 1.0);
	}
	
	public void addVariable(Mod2002014Key key, Number d) {
		DoubleVariable2014 dv = new DoubleVariable2014(key);
		dv.setValue(d==null?0:d.doubleValue());
		keysMap.put( dv.getKey(),dv );
	}

	public void addVariable(DoubleVariable2014 t) {
		keysMap.put( t.getKey(), t);
	}
	public void removeVariable(DoubleVariable2014 t) {
		keysMap.remove(t.getKey());
	}
	
	public void addDraftVariable(DoubleVariable2014 t) {
		draftMap.put( t.getKey(), t);
	}
	public void removeDraftariable(DoubleVariable2014 t) {
		draftMap.remove(t.getKey());
	}

	public DoubleVariable2014 getVariable(Mod2002014Key key) {
		DoubleVariable2014 var = getDraftMap().get(key);
		if (var == null ) {
			var = getKeysMap().get(key);
		}
		return var; 
	}

	public Double getDoubleValue(Mod2002014Key key) {
		if (!keysMap.containsKey(key)){
			return new Double(0);	
		}
		DoubleVariable2014 v = keysMap.get(key);
		if (v==null) {
			return new Double(0);
		}
		Object o = v.getValue();
		if (o != null && o instanceof Double) {
			return (Double) o;
		}
		return new Double(0);
	}
	public void listDraftVariables() {
		for (DoubleVariable2014 dv : getDraftMap().values()) {
			System.out.println(dv.getKey() + " --> " + dv.getValue() );
		}
		
	}
	@Override
	public FiscalModelType getModel() {
		return FiscalModelType.M200;
	}
	@Override
	public Period getPeriod() {
		return Period.YEAR;
	}
	@Override
	public FiscalStatus getStatus() {
		// TODO Soporte!!
		return FiscalStatus.PENDING;
	}
	@Override
	public boolean isReplacement() {
		return false;
	}
	@Override
	public String getDocument() {
		return enterpriseDocument;
	}
	@Override
	public String getName() {
		return enterpriseName;
	}
	@Override
	public String getFullName() {
		return getName();
	}
	@Override
	public String getSurname() {
		return null;
	}
	@Override
	public String getDomainName() {
		return null;
	}
}

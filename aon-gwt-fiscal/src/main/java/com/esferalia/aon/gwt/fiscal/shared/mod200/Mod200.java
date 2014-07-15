package com.esferalia.aon.gwt.fiscal.shared.mod200;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.common.shared.CommonEnum.Administration;
import com.esferalia.aon.gwt.common.shared.CompanyAdministrator;
import com.esferalia.aon.gwt.common.shared.CompanyParticipation;
import com.esferalia.aon.gwt.common.shared.LegalRepresentative;
import com.esferalia.aon.gwt.common.shared.Secretary;
import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class Mod200 implements Serializable, IsSerializable {
	
	public static enum BalanceType implements Serializable, IsSerializable {
		NORMAL,
		ABREVIADO,
		PYMES;
	}
	
	private Integer id;
	private int domain;
	private int year;
	private int administration = Administration.COMMON_TERRITORY.ordinal();
	
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
	
	private EnumMap<Mod200Key,DoubleVariable> keysMap = new EnumMap<Mod200Key,DoubleVariable>(Mod200Key.class);
	private EnumMap<Mod200Key,DoubleVariable> draftMap = new EnumMap<Mod200Key,DoubleVariable>(Mod200Key.class);
	private EnumMap<Mod200Key,Boolean> visibleMap = new EnumMap<Mod200Key,Boolean>(Mod200Key.class);

	private List<ValidationMessage> messages;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
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
	public int getAdministration() {
		return administration;
	}
	public void setAdministration(int administration) {
		this.administration = administration;
	}
	public String getReceipt() {
		return receipt;
	}
	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}
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
	public EnumMap<Mod200Key, DoubleVariable> getKeysMap() {
		return keysMap;
	}
	public void setKeysMap(EnumMap<Mod200Key, DoubleVariable> keysMap) {
		this.keysMap = keysMap;
	}
	public EnumMap<Mod200Key, DoubleVariable> getDraftMap() {
		return draftMap;
	}
	public void setDraftMap(EnumMap<Mod200Key, DoubleVariable> draftMap) {
		this.draftMap = draftMap;
	}
	public EnumMap<Mod200Key, Boolean> getVisibleMap() {
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
	public void setVisibleMap(EnumMap<Mod200Key, Boolean> visibleMap) {
		this.visibleMap = visibleMap;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public List<ValidationMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<ValidationMessage> messages) {
		this.messages = messages;
	}
	public DoubleVariable getKey(Mod200Key key) {
		return keysMap.get(key);
	}
	public boolean isChecked(Mod200Key key) {
		DoubleVariable dv = keysMap.get(key); 
		return dv != null && (AonUtil.round(dv.getValue()) == 1.0);
	}
	
	public void addVariable(DoubleVariable t) {
		keysMap.put( t.getKey(), t);
	}
	public void removeVariable(DoubleVariable t) {
		keysMap.remove(t.getKey());
	}
	public void addDraftVariable(DoubleVariable t) {
		draftMap.put( t.getKey(), t);
	}
	public void removeDraftariable(DoubleVariable t) {
		draftMap.remove(t.getKey());
	}

	public DoubleVariable getVariable(Mod200Key key) {
		DoubleVariable var = getDraftMap().get(key);
		if (var == null ) {
			var = getKeysMap().get(key);
		}
		return var; 
	}

	@SuppressWarnings("rawtypes")
	public Double getDoubleValue(Mod200Key key) {
		if (!keysMap.containsKey(key)){
			return new Double(0);	
		}
		Variable v = keysMap.get(key);
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
		for (DoubleVariable dv : getDraftMap().values()) {
			System.out.println(dv.getKey() + " --> " + dv.getValue() );
		}
		
	}
}

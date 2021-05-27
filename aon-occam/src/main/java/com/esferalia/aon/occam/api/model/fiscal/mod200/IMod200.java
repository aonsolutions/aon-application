package com.esferalia.aon.occam.api.model.fiscal.mod200;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.occam.api.model.CompanyAdministrator;
import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.Secretary;
import com.esferalia.aon.occam.api.model.type.Administration;

public interface IMod200<K extends IMod200Key> {
	
	public static enum BalanceType implements Serializable {
		NORMAL,
		ABREVIADO,
		PYMES;
	}

	public Integer getId();
	public void setId(Integer id);
	
	public int getYear();
	public void setYear(int year);

	public int getDomain();
	public void setDomain(int domain);
	
	public int getEnterprise();
	public void setEnterprise(int enterprise);
	
	public Administration getAdministration();
	public void setAdministration(Administration administration);
	
	public String getReceipt();
	public void setReceipt(String receipt);

	public boolean isComplementary();
	public void setComplementary(boolean complementary);
	
	public String getComplementaryReceipt();
	public void setComplementaryReceipt(String complementaryReceipt);
	
	public String getCnae();
	public void setCnae(String cnae);
	
	public int getPeriodType();
	public void setPeriodType(int periodType);
	
	public Date getPeriodStart();
	public void setPeriodStart(Date periodStart);
	
	public Date getPeriodEnd();
	public void setPeriodEnd(Date periodEnd);
	
	public String getEnterpriseDocument();
	public void setEnterpriseDocument(String enterpriseDocument);
	
	public String getEnterpriseName();
	public void setEnterpriseName(String enterpriseName);
	
	public String getEnterprisePhone1();
	public void setEnterprisePhone1(String enterprisePhone1);
	
	public String getEnterprisePhone2();
	public void setEnterprisePhone2(String enterprisePhone2);
	
	public String getFiscalGroup();
	public void setFiscalGroup(String fiscalGroup);
	
	public String getDominantDocument();
	public void setDominantDocument(String dominantDocument);
	
	public BalanceType getBalanceType();
	public void setBalanceType(BalanceType balanceType);
	public void setBalanceType(int index);
	
	public BalanceType getPygType();
	public void setPygType(BalanceType pygType);
	public void setPygType(int index);
	
	public Secretary getSecretary();
	public void setSecretary(Secretary secretary);
	
	public List<LegalRepresentative> getRepresentatives();
	public void setRepresentatives(List<LegalRepresentative> representatives);
	
	public List<CompanyAdministrator> getAdministrators();
	public void setAdministrators(List<CompanyAdministrator> administrators);
	
	public List<CompanyParticipation> getParticipationsIn();
	public void setParticipationsIn(List<CompanyParticipation> participationsIn);
	
	public List<CompanyParticipation> getParticipationsOut();
	public void setParticipationsOut(List<CompanyParticipation> participationsOut);
	
	public Map<K, DoubleVariable<K>> getKeysMap();
	public void setKeysMap(Map<K, DoubleVariable<K>> keysMap);
	
	public Map<K, DoubleVariable<K>> getDraftMap();
	public void setDraftMap(Map<K, DoubleVariable<K>> draftMap);
	
	public Map<K, Boolean> getVisibleMap();
	public void setVisibleMap(Map<K, Boolean> visibleMap);
	
	public String getResultType();
	public void setResultType(String resultType);
	
	public String getDevType();
	public void setDevType(String devType);
	
	public String getPayType();
	public void setPayType(String payType);
	
	public Double getAmount();
	public void setAmount(Double amount);
	
	public String getIban();
	public void setIban(String iban);
	
	public String getComments();
	public void setComments(String comments);
	
	public DoubleVariable<K> getKey(K key);
	
	public void addVariable(DoubleVariable<K> t);
	public void removeVariable(DoubleVariable<K> t);
	public void addDraftVariable(DoubleVariable<K> t);
	public void removeDraftariable(DoubleVariable<K> t);
	public DoubleVariable<K> getVariable(K key);
	public Double getDoubleValue(K key);

}

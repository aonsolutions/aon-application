package com.code.aon.registry;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.IAccount;
import com.code.aon.AonVersion;
import com.code.aon.config.IBankAccountContainer;
import com.esferalia.aon.entity.master.RegistryBankDB;

@Entity
@Table(name="rbank")
public class RegistryBank extends RegistryBankDB implements IBankAccountContainer, IAccount {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public RegistryBank(){
		setActive(true);
	}

	@Transient
	public String getBankAlias() {
		return getAlias();
	}

	@Transient
	public void setBankAlias(String bankAlias) {
		setAlias(bankAlias);
	}

	@Transient
	public String getFullName() {
		StringBuffer sb = new StringBuffer();
		if (getBankAccount() != null && !StringUtils.isBlank(getBankAccount().getBban())) {
			sb.append(getFullName(getBankAccount().toString()));
		}
		return sb.toString(); 
	}

	@Transient
	public String getMaskedFullName() {
		StringBuffer sb = new StringBuffer();
		if (getBankAccount() != null && !StringUtils.isBlank(getBankAccount().getBban())) {
			sb.append(getFullName(getBankAccount().getMaskedIban()));
		}
		return sb.toString(); 
	}

	@Transient
	private String getFullName(String bankAccount) {
		StringBuffer sb = new StringBuffer();
		sb.append(bankAccount);
		sb.append(" ");
		if (!StringUtils.isBlank(getBic())) {
			sb.append("[");
			sb.append(getBic());
			sb.append("] ");
		}
		if (!StringUtils.isBlank(getAlias())) {
			sb.append(getAlias());	
		}
		return sb.toString(); 
	}

}
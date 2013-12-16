package com.code.aon.registry;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.config.IBankAccountContainer;
import com.esferalia.aon.entity.master.RegistryBankDB;

@Entity
@Table(name="rbank")
public class RegistryBank extends RegistryBankDB implements IBankAccountContainer {

	private static final long serialVersionUID = 1L;

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
		if (!StringUtils.isBlank(getAlias())) {
			sb.append(getAlias());	
			sb.append(" ");
		}
		if (getBankAccount() != null && !StringUtils.isBlank(getBankAccount().getBban())) {
			sb.append("[");
			sb.append(getBankAccount().toString());
			sb.append("]");
		}
		if (!StringUtils.isBlank(getBic())) {
			sb.append("- [");
			sb.append(getBic());
			sb.append("]");
		}
		return sb.toString(); 
	}

}
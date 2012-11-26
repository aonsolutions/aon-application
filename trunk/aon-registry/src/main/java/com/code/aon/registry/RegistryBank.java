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
	
	@Transient
	public String getFullName() {
		StringBuffer sb = new StringBuffer();
		if (!StringUtils.isBlank(getAlias())) {
			sb.append(getAlias());	
		} else {
			if (getBank() != null && !StringUtils.isEmpty(getBank().getName()))  {
				sb.append(StringUtils.abbreviate(getBank().getName(), 30));
				sb.append(" ");
			}
			if (getBankAccount() != null) {
				sb.append("[");
				sb.append(getBankAccount().toString());
				sb.append("]");
			}
		}
		return sb.toString(); 
	}
}
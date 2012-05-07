package com.code.aon.accounting;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.common.ITransferObject;
import com.esferalia.aon.entity.master.BalanceDB;

@Entity
@Table(name="balance")
public class Balance extends BalanceDB {
		
	private static final long serialVersionUID = 1L;
	

	private Set<BalanceDetail> lines;
	
	@OneToMany(mappedBy = "balance", cascade={CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE})
	@OrderBy()
	public Set<BalanceDetail> getLines() {
		return this.lines;
	}
	public void setLines( Set<BalanceDetail> lines ) {
		this.lines = lines;
	}
	
	@Transient
	public void addBalanceDetail(BalanceDetail detail) {
		if (getLines() == null) {
			setLines( new HashSet<BalanceDetail>());
		}
		detail.setBalance(this);
		getLines().add(detail);
	}
	
	/**
	 * Método utilizado en la importación de balances, se utiliza en el fichero de 
	 * definición de reglas de disgester.
	 */
	@Transient
	public String getStringType() {
		return null;
	}
	public void setStringType(String stringType) {
		BalanceType bt = BalanceType.valueOf(stringType);
		setType(bt);
	}
}
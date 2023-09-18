package com.code.aon.accounting;


import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.BalanceDB;

@Entity
@Table(name="balance")
@Deprecated
public class Balance extends BalanceDB {
		
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	

	private Set<BalanceDetail> lines;

	public Balance() {
		setRemovable(true);
	}
	
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
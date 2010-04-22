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

/**
 * TransferObject that represents a Balance.
 */
@Entity
@Table(name = "balance")
public class Balance implements ITransferObject {
		
	private static final long serialVersionUID = -5577162507185474823L;
	
	private Integer id;
	private String name;
	private boolean removable;
	private BalanceType type;

	private Set<BalanceDetail> lines;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="name", length=64, nullable=false)
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name="removable")
	public boolean isRemovable() {
		return removable;
	}
	public void setRemovable(boolean removable) {
		this.removable = removable;
	}

	@Column(name="type")
	public BalanceType getType() {
		return type;
	}
	public void setType(BalanceType type) {
		this.type = type;
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() !=  getClass()) return false;
		final Balance o = (Balance) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.getName(), o.getName())
			.append(this.isRemovable(), o.isRemovable())
			.append(this.getType(), o.getType())
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getId())
			.append(this.getName())
			.append(this.isRemovable())
			.append(this.getType())
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
		.append(this.getId())
		.append(this.getName())
		.append(this.isRemovable())
		.append(this.getType()).toString();
	}

}
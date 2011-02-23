package com.code.aon.company;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.registry.RegistryAddress;
import com.esferalia.aon.calendar.Calendar;

@Entity
@Table(name="workplace")
public class WorkPlace implements ITransferObject {

	private static final long serialVersionUID = 5078033612665053464L;

	/** Working place identifier */
	private Integer id;
	/** Working place description */
	private String description;
	/** Indicates the enterprise that this Working place belongs to */
    private Enterprise enterprise; 	
	/** Working place address */
    private RegistryAddress address;
	/** Economic Agreement */
    private Administration economicAgreement;
    /** Indicates if the working place is currently active. */
	private boolean active;
	/** Calendar */
	private Calendar calendar;

    @Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="description", length = 64, nullable = false)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne
    @JoinColumn(name="enterprise", nullable = false, updatable = false)
    @ForeignKey(name = "FK_WORKPLACE_ENTERPRISE")
    @Index(name = "IDX_WORKPLACE_ENTERPRISE")    
    public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name="address", nullable = false)
    @ForeignKey(name = "FK_WORKPLACE_ADDRESS")
    @Index(name = "IDX_WORKPLACE_ADDRESS")
	public RegistryAddress getAddress() {
		return address;
	}
	public void setAddress(RegistryAddress address) {
		this.address = address;
	}

	@Column(nullable = true)
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Column(name="economicAgreement", nullable = true)
	public Administration getEconomicAgreement() {
		return economicAgreement;
	}
	public void setEconomicAgreement(Administration economicAgreement) {
		this.economicAgreement = economicAgreement;
	}
	
	@ManyToOne
    @JoinColumn( name="calendar")	
	@ForeignKey(name = "FK_WORKPLACE_CALENDAR")
	@Index(name = "FK_WORKPLACE_CALENDAR")
	public Calendar getCalendar() {
		return calendar;
	}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final WorkPlace o = (WorkPlace) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.address, o.address)			
				.append(this.description, o.description)
				.append(this.economicAgreement, o.economicAgreement)
				.append(this.enterprise, o.enterprise)
				.append(this.calendar, o.calendar)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(address)
			.append(description)
			.append(economicAgreement)
			.append(enterprise)
			.append(id)
			.append(calendar)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}

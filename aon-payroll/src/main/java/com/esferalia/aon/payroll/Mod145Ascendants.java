package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;

@Entity
@Table(name = "mod145_ascendants")
public class Mod145Ascendants implements ITransferObject {
	
	private static final long serialVersionUID = -4436169609427812691L;

	private Integer id;
	private Mod145 mod145;
	private Integer birthYear;
	private DisabilityLevel disabilityLevel;
	private boolean dependence;
	private boolean anotherDescendient;
	
	@Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    @ManyToOne
	@JoinColumn( name="mod145", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_MOD145_ASCENDIENTS_MOD145")
	@Index(name = "IDX_MOD145_ASCENDIENTS_MOD145")
	public Mod145 getMod145() {
		return mod145;
	}
	public void setMod145(Mod145 mod145) {
		this.mod145 = mod145;
	}
	
	@Column(name = "birth_year", length = 4)
	public Integer getBirthYear() {
		return birthYear;
	}
	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
	}
	
	@Column( name = "disability_level")
	public DisabilityLevel getDisabilityLevel() {
		return disabilityLevel;
	}
	public void setDisabilityLevel(DisabilityLevel disabilityLevel) {
		this.disabilityLevel = disabilityLevel;
	}
	
	@Column( name = "dependence" )
	public boolean isDependence() {
		return dependence;
	}
	public void setDependence(boolean dependence) {
		this.dependence = dependence;
	}
	
	@Column( name = "another_descendient" )
	public boolean isAnotherDescendient() {
		return anotherDescendient;
	}
	public void setAnotherDescendient(boolean anotherDescendient) {
		this.anotherDescendient = anotherDescendient;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Mod145Ascendants o = (Mod145Ascendants) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.mod145, o.mod145)			
				.append(this.birthYear, o.birthYear)			
				.append(this.disabilityLevel, o.disabilityLevel)			
				.append(this.dependence, o.dependence)			
				.append(this.anotherDescendient, o.anotherDescendient)			
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.mod145)		
			.append(this.birthYear)		
			.append(this.disabilityLevel)		
			.append(this.dependence)		
			.append(this.anotherDescendient)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}

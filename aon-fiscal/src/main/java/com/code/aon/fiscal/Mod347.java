package com.code.aon.fiscal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Mod347Status;

@Entity
@Table(name = "fs_mod347")
public class Mod347 implements ITransferObject, IConfidentialable {
	
	public static final Double MINIMUM_AMOUNT = 3005.36;
	
	private static final long serialVersionUID = -7918348181910780031L;
	
	private Integer id;
	private Integer year;
	private Administration administration;
	private String comments;
	private Mod347Status status;
	private SecurityLevel securityLevel;
	private boolean complementary;
	private boolean replacement;
	private Double minimumAmount;
	private Integer number;
	private Integer replacedNumber;
	
	private boolean generateLines;
	
	@Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(nullable = false)
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }
    
	@Column(name="administration")
	public Administration getAdministration() {
		return administration;
	}
	public void setAdministration(Administration administration) {
		this.administration = administration;
	}

	@Column(name="comments")
	@Lob
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Column(name="status")
    public Mod347Status getStatus() {
        return status;
    }
    public void setStatus(Mod347Status status) {
        this.status = status;
    }

	@Column(name="security_level")
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	@Transient
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	@Transient
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}
	
    @Transient
	public boolean isFinished() {
		return getStatus() == Mod347Status.FINISHED;
	}
	
	
	@Column(name="complementary")
	public boolean isComplementary() {
		return complementary;
	}
	public void setComplementary(boolean complementary) {
		this.complementary = complementary;
	}

	@Column(name="replacement")
	public boolean isReplacement() {
		return replacement;
	}
	public void setReplacement(boolean replacement) {
		this.replacement = replacement;
	}
	
	@Transient
	public boolean isExtraDeclaration() {
		return (isComplementary() || isReplacement() );
	}

    @Column(name = "number")
    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer number) {
        this.number = number;
    }

    @Column(name = "replaced_number")
    public Integer getReplacedNumber() {
        return replacedNumber;
    }
    public void setReplacedNumber(Integer replacedNumber) {
        this.replacedNumber = replacedNumber;
    }

    @Transient
    public Double getMinimumAmount() {
		return minimumAmount;
	}
	public void setMinimumAmount(Double minimumAmount) {
		this.minimumAmount = minimumAmount;
	}

    @Transient
	public boolean isGenerateLines() {
		return generateLines;
	}
	public void setGenerateLines(boolean generateLines) {
		this.generateLines = generateLines;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Mod347 o = (Mod347) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.year, o.year)			
				.append(this.comments, o.comments)			
				.append(this.status, o.status)
				.append(this.securityLevel, o.securityLevel)
				.append(this.complementary, o.complementary)
				.append(this.replacement, o.replacement)
				.append(this.number, o.number)
				.append(this.replacedNumber, o.replacedNumber)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.year)			
			.append(this.comments)			
			.append(this.status)
			.append(this.securityLevel)
			.append(this.complementary)
			.append(this.replacement)
			.append(this.number)
			.append(this.replacedNumber)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}

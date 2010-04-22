package com.code.aon.registry;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="rdir_staff")
public class RegistryDirStaff implements ITransferObject {

	private static final long serialVersionUID = -2674917501167731593L;

	private Integer id;
	
	private Registry registry;
	
	private String document;
	
	private String name;
	
	private boolean shareHolder;
	
	private boolean representative;
	
	private boolean director;
	
	private double percentShare;
	
	private int shareNumber;
	
	private double nominalValue;
	
	private Date dueDate;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="registry", nullable=false)
	@ForeignKey(name = "FK_RDIR_STAFF_REGISTRY")
	@Index(name = "IDX_RDIR_STAFF_REGISTRY")
	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Column(length=16, nullable=false)
	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	@Column(length=128, nullable=false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="shareholder", nullable=false)
	public boolean isShareHolder() {
		return shareHolder;
	}

	public void setShareHolder(boolean shareHolder) {
		this.shareHolder = shareHolder;
	}

	@Column(name="representative", nullable=false)
	public boolean isRepresentative() {
		return representative;
	}

	public void setRepresentative(boolean representative) {
		this.representative = representative;
	}

	@Column(name="director", nullable=false)
	public boolean isDirector() {
		return director;
	}

	public void setDirector(boolean director) {
		this.director = director;
	}

	@Column(name="percent_share")
	public double getPercentShare() {
		return percentShare;
	}

	public void setPercentShare(double percentShare) {
		this.percentShare = percentShare;
	}

	@Column(name="share_number")
	public int getShareNumber() {
		return shareNumber;
	}

	public void setShareNumber(int shareNumber) {
		this.shareNumber = shareNumber;
	}

	@Column(name="nominal_value", precision = 15, scale = 3)
	public double getNominalValue() {
		return nominalValue;
	}

	public void setNominalValue(double nominalValue) {
		this.nominalValue = nominalValue;
	}

	@Column(name="due_date")
	@Temporal(TemporalType.DATE)
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final RegistryDirStaff o = (RegistryDirStaff) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.director, o.director)
				.append(this.document, o.document)				
				.append(this.dueDate, o.dueDate)
				.append(this.name, o.name)
				.append(this.nominalValue, o.nominalValue)
				.append(this.percentShare, o.percentShare)				
				.append(this.registry, o.registry)
				.append(this.representative, o.representative)
				.append(this.shareHolder, o.shareHolder)
				.append(this.shareNumber, o.shareNumber)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(director)
			.append(document)
			.append(dueDate)
			.append(id)	
			.append(name)
			.append(nominalValue)				
			.append(percentShare)
			.append(registry)
			.append(representative)	
			.append(shareHolder)
			.append(shareNumber)				
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}    

}

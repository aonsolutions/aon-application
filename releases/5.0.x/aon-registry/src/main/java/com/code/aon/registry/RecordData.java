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
@Table(name="record_data")
public class RecordData implements ITransferObject {
	
	private static final long serialVersionUID = 364687326411697134L;

	private Integer id;
	
	private Registry registry;
	
	private Date creationDate;
	
	private String description;
	
	private String notary;
	
	private String number;
	
	private Date recordDate;
	
	private String volume;
	
	private String section;
	
	private String page;
	
	private String sheet;
	
	private String registration;
	
	private RegistryAttachment attach; 

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
	@ForeignKey(name = "FK_RECORD_DATA_REGISTRY")
	@Index(name = "IDX_RECORD_DATA_REGISTRY")
	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Column(name="creation_date")
	@Temporal(TemporalType.DATE)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Column(length=64)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(length=64)
	public String getNotary() {
		return notary;
	}

	public void setNotary(String notary) {
		this.notary = notary;
	}

	@Column(length=16)
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Column(name="record_date")
	@Temporal(TemporalType.DATE)
	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	@Column(length=16)
	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	@Column(length=16)
	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	@Column(length=16)
	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	@Column(length=16)
	public String getSheet() {
		return sheet;
	}

	public void setSheet(String sheet) {
		this.sheet = sheet;
	}

	@Column(length=64)
	public String getRegistration() {
		return registration;
	}

	public void setRegistration(String registration) {
		this.registration = registration;
	}

	@ManyToOne
	@JoinColumn(name="attach")
	@ForeignKey(name = "FK_RECORD_DATA_ATTACH")
	@Index(name = "IDX_RECORD_DATA_ATTACH")
	public RegistryAttachment getAttach() {
		return attach;
	}

	public void setAttach(RegistryAttachment attach) {
		this.attach = attach;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final RecordData o = (RecordData) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.attach, o.attach)
				.append(this.creationDate, o.creationDate)				
				.append(this.description, o.description)
				.append(this.notary, o.notary)				
				.append(this.number, o.number)
				.append(this.page, o.page)				
				.append(this.recordDate, o.recordDate)
				.append(this.registration, o.registration)				
				.append(this.registry, o.registry)
				.append(this.section, o.section)				
				.append(this.sheet, o.sheet)
				.append(this.volume, o.volume)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(attach)
			.append(creationDate)
			.append(description)	
			.append(id)			
			.append(notary)
			.append(number)
			.append(page)			
			.append(recordDate)
			.append(registration)
			.append(registry)			
			.append(section)
			.append(sheet)
			.append(volume)			
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
package com.code.aon.consultant;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.registry.Registry;

@Entity
@Table(name="rdir_staff")
public class RegistryDirStaff implements ITransferObject {

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

	@Column(name="nominal_value")
	public double getNominalValue() {
		return nominalValue;
	}

	public void setNominalValue(double nominalValue) {
		this.nominalValue = nominalValue;
	}

	@Column(name="due_date")
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
}

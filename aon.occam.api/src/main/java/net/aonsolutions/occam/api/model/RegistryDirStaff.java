package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public class RegistryDirStaff implements Serializable {
	
	private static final long serialVersionUID = -1378893419649521080L;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private String document;
	private String name;
	
	private boolean shareHolder;
	private boolean representative;
	private boolean director;
	private boolean representativeLabor;
	
	private Date dueDate;
	private Double percentShare;
	private Integer shareNumber;
	private Double nominalValue;
	private String chargeDescription;
	
	public Integer getId() {
		return id;
	}
	public RegistryDirStaff setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public RegistryDirStaff setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}
	public RegistryDirStaff setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public String getDocument() {
		return document;
	}
	public RegistryDirStaff setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}
	public RegistryDirStaff setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isShareHolder() {
		return shareHolder;
	}
	public RegistryDirStaff setShareHolder(boolean shareHolder) {
		this.shareHolder = shareHolder;
		return this;
	}

	public boolean isRepresentative() {
		return representative;
	}
	public RegistryDirStaff setRepresentative(boolean representative) {
		this.representative = representative;
		return this;
	}

	public boolean isDirector() {
		return director;
	}
	public RegistryDirStaff setDirector(boolean director) {
		this.director = director;
		return this;
	}

	public boolean isRepresentativeLabor() {
		return representativeLabor;
	}
	public RegistryDirStaff setRepresentativeLabor(boolean representativeLabor) {
		this.representativeLabor = representativeLabor;
		return this;
	}

	public Date getDueDate() {
		return dueDate;
	}
	public RegistryDirStaff setDueDate(Date dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	public Double getPercentShare() {
		return percentShare;
	}
	public RegistryDirStaff setPercentShare(Double percentShare) {
		this.percentShare = percentShare;
		return this;
	}

	public Integer getShareNumber() {
		return shareNumber;
	}
	public RegistryDirStaff setShareNumber(Integer shareNumber) {
		this.shareNumber = shareNumber;
		return this;
	}
	
	public Double getNominalValue() {
		return nominalValue;
	}
	public RegistryDirStaff setNominalValue(Double nominalValue) {
		this.nominalValue = nominalValue;
		return this;
	}
	
	public String getChargeDescription() {
		return chargeDescription;
	}
	public RegistryDirStaff setChargeDescription(String chargeDescription) {
		this.chargeDescription = chargeDescription;
		return this;
	}
	
}

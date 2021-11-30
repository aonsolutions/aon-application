package com.esferalia.aon.occam.api.model.payroll;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.ContractAttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;

public class ContractAttach implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer domain;
	private Integer contract;
	private MimeType mimeType;
	private String description;
	private byte[] data;
	private ContractAttachType type;
	private Integer scope;
	private Date attachDate;
	
	private Byte security_level;
    private String driveId;
    
    
	public Integer getId() {
		return id;
	}
	public ContractAttach setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public ContractAttach setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getContract() {
		return contract;
	}
	public ContractAttach setContract(Integer contract) {
		this.contract = contract;
		return this;
	}
	public MimeType getMimeType() {
		return mimeType;
	}
	
	public ContractAttach setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	public ContractAttach setDescription(String description) {
		this.description = description;
		return this;
	}
	public byte[] getData() {
		return data;
	}
	public ContractAttach setData(byte[] data) {
		this.data = data;
		return this;
	}
	public ContractAttachType getType() {
		return type;
	}
	public ContractAttach setType(ContractAttachType type) {
		this.type = type;
		return this;
	}
	public Integer getScope() {
		return scope;
	}
	public ContractAttach setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	public Date getAttachDate() {
		return attachDate;
	}
	public ContractAttach setAttachDate(Date attachDate) {
		this.attachDate = attachDate;
		return this;
	}
    
}

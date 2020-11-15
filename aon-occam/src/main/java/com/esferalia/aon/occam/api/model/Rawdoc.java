package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;

import es.translogia.tedi.ewok.TediInvoice;

public class Rawdoc implements Serializable {

	private static final long serialVersionUID = -3954007129622239737L;
	
	private Integer id;
	private Integer domain;
	private RawdocNature nature;
	private RawdocType type;
	private RawdocStatus status;
	
	private String json;
	private TediInvoice tediInvoice;
	private String log;
	private MimeType mimeType;
	private byte[] data;

	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	public Integer getId() {
		return id;
	}
	public Rawdoc setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Rawdoc setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public RawdocNature getNature() {
		return nature;
	}
	public Rawdoc setNature(RawdocNature nature) {
		this.nature = nature;
		return this;
	}
	
	public RawdocType getType() {
		return type;
	}
	public Rawdoc setType(RawdocType type) {
		this.type = type;
		return this;
	}
	
	public RawdocStatus getStatus() {
		return status;
	}
	public Rawdoc setStatus(RawdocStatus status) {
		this.status = status;
		return this;
	}
	
	public String getJson() {
		return json;
	}
	public Rawdoc setJson(String json) {
		this.json = json;
		return this;
	}
	
	public TediInvoice getTediInvoice() {
		return tediInvoice;
	}
	public Rawdoc setTediInvoice(TediInvoice tediInvoice) {
		this.tediInvoice = tediInvoice;
		return this;
	}
	
	public String getLog() {
		return log;
	}
	public Rawdoc setLog(String log) {
		this.log = log;
		return this;
	}
	
	public MimeType getMimeType() {
		return mimeType;
	}
	public Rawdoc setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
		return this;
	}
	
	public byte[] getData() {
		return data;
	}
	public Rawdoc setData(byte[] data) {
		this.data = data;
		return this;
	}
	
	public String getCreationUser() {
		return creationUser;
	}
	public Rawdoc setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	public Rawdoc setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	public String getModificationUser() {
		return modificationUser;
	}
	public Rawdoc setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
	public Date getModificationDate() {
		return modificationDate;
	}
	public Rawdoc setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
}

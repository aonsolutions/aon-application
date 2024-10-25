package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.sql.Timestamp;

import net.aonsolutions.occam.api.model.type.MimeType;
import net.aonsolutions.occam.api.model.type.RawdocNature;
import net.aonsolutions.occam.api.model.type.RawdocStatus;
import net.aonsolutions.occam.api.model.type.RawdocType;

public class Rawdoc implements Serializable, HasAudit {

	private static final long serialVersionUID = -3954007129622239737L;
	private static final String BUCKET = "aon-upload-post"; //"aon-rawdoc";

	private Integer id;
	private Integer domain;
	private RawdocNature nature;
	private RawdocType type;
	private RawdocStatus status;
	
	private String json;
	private String log;
	private MimeType mimeType;
	private byte[] data;
	private String s3Key;
	
	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;
	
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
	
	public String getS3Bucket() {
		return BUCKET;
	}
	
	public String getS3Key() {
		return s3Key;
	}
	
	public Rawdoc setS3Key(String s3Key) {
		this.s3Key = s3Key;
		return this;
	}
	
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Rawdoc setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	
	@Override
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public Rawdoc setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Rawdoc setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
	@Override
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public Rawdoc setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
}

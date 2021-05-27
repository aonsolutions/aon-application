package com.esferalia.aon.payroll;

import static com.code.aon.common.BlobObjectAction.READ;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.annotations.Formula;

import com.code.aon.AonVersion;
import com.code.aon.common.BlobObjectAction;
import com.code.aon.common.IBlobManager;
import com.code.aon.common.IBlobObject;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.dao.hibernate.HibernateBlobManager;
import com.esferalia.aon.entity.master.LeaveBatchDB;

@Entity
@Table(name="leave_batch")
@Heritable
public class LeaveBatch extends LeaveBatchDB implements IBlobObject {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<LeaveBatchDetail> lines = new HashSet<LeaveBatchDetail>();
	
	private byte[] outcomeFile;
	
	private Integer outcomeFileSize;
	
	private byte[] incomeFile;	
	
	private Integer incomeFileSize;
	
	@OneToMany(mappedBy = "leaveBatch", cascade={CascadeType.REMOVE})
	@OrderBy()
	public Set<LeaveBatchDetail> getLines() {
		return this.lines;
	}
	public void setLines(Set<LeaveBatchDetail> lines) {
		this.lines = lines;
	}

	@Formula("IFNULL(LENGTH(outcome_file),0)")
    public Integer getOutcomeFileSize() {
		return outcomeFileSize;
	}

	public void setOutcomeFileSize(Integer outcomeFileSize) {
		this.outcomeFileSize = outcomeFileSize;
	}

	@Transient
	@Column(name="outcome_file")	
	public byte[] getOutcomeFile() {
    	if ( outcomeFile != null ) {
    		return outcomeFile;
    	}
		return getManager(READ).getBlob(this, OUTCOME_FILE_PROPERTY);
	}

	public void setOutcomeFile(byte[] outcomeFile) {
		this.outcomeFile = outcomeFile;
		setOutcomeFileSize(ArrayUtils.getLength(outcomeFile));
	}

    @Transient
	@Column(name="income_file")	
	public byte[] getIncomeFile() {
    	if ( incomeFile != null ) {
    		return incomeFile;
    	}
		return getManager(READ).getBlob(this, INCOME_FILE_PROPERTY);    	
	}

	public void setIncomeFile(byte[] incomeFile) {
		this.incomeFile = incomeFile;
		setIncomeFileSize(ArrayUtils.getLength(outcomeFile));
	}
	
	@Formula("IFNULL(LENGTH(income_file),0)")
	public Integer getIncomeFileSize() {
		return incomeFileSize;
	}

	public void setIncomeFileSize(Integer incomeFileSize) {
		this.incomeFileSize = incomeFileSize;
	}

	@Override
	@Transient
	public String[] getBlobProperties() {
		return FILE_BLOB_PROPERTIES;
	}

	@Override
	@Transient
	public Serializable getReference( String property ) {
		return getId();
	}

	@Override
	@Transient
	public IBlobManager getManager( BlobObjectAction action ) {
		return HibernateBlobManager.getInstance();
	}

	@Override
	public void reset() {
		this.incomeFile = null;
		this.outcomeFile = null;
	}
	
}
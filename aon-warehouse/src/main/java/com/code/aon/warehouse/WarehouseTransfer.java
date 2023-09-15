package com.code.aon.warehouse;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.esferalia.aon.entity.master.WarehouseTransferDB;

@Entity
@Table(name="warehouse_transfer", uniqueConstraints = @UniqueConstraint(columnNames={"series", "number"}))
public class WarehouseTransfer extends WarehouseTransferDB implements IHeaderObject, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Set<WarehouseTransferDetail> details = new HashSet<WarehouseTransferDetail>();

	public WarehouseTransfer() {
		setIssueTime( new Date() );
	}

	@OneToMany(mappedBy="warehouseTransfer")
	public Set<WarehouseTransferDetail> getDetails() {
		return this.details;
	}
	public void setDetails(Set<WarehouseTransferDetail> details) {
		this.details = details;
	}

    @Transient
    public String getReferenceCode() {
    	String referenceCode = StringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
		if (!StringUtils.isEmpty(getSeries())) {
			referenceCode = getSeries() + "/" + referenceCode;
		}
    	return referenceCode;
    }
	
    @Transient
    public Date getDate() {
    	return getIssueTime();
    }

	@Override
	@Transient
	public SecurityLevel getSecurityLevel() {
		// Método necesario por implementar IHeaderObject. 
		// Al no dar soporte de confidencialidad, se devuelve siempre el mismo.
		return SecurityLevel.OFFICIAL;
	}
    @Override
	public void setSecurityLevel(SecurityLevel securityLevel) {
	}

}
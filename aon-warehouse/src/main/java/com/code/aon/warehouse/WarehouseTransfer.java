package com.code.aon.warehouse;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.esferalia.aon.entity.master.WarehouseTransferDB;

@Entity
@Table(name="warehouse_transfer", uniqueConstraints = @UniqueConstraint(columnNames={"series", "number"}))
public class WarehouseTransfer extends WarehouseTransferDB implements IHeaderObject {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public WarehouseTransfer() {
		setIssueTime( new Date() );
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
	public void setSecurityLevel(SecurityLevel securityLevel) {
	}

	@Override
	@Transient
	public SecurityLevel getSecurityLevel() {
		// Método necesario por implementar IHeaderObject. 
		// Al no dar soporte de confidencialidad, se devuelve siempre el mismo.
		return SecurityLevel.OFFICIAL;
	}
}
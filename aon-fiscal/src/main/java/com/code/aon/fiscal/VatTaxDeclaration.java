package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.VatTaxDeclarationStatus;
import com.esferalia.aon.entity.master.VatTaxDeclarationDB;

@Entity
@Table(name="fs_vat_declaration")
public class VatTaxDeclaration extends VatTaxDeclarationDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public VatTaxDeclaration() {
		setCompensable(true);		
	}
	
    @Transient
    public boolean isGenerated() {
    	return (getStatus() == VatTaxDeclarationStatus.GENERATED);
    }
    @Transient
	public double getDifference() {
		return CommonUtil.round( getQuota() - getPreviousYearCompensateQuota() );
	}

	@Transient
	public double getResult() {
		return CommonUtil.round( getDifference() - getDoneDeposits() + getDoneRefunds() );
	}

	@Transient
	public boolean isCompensateEnabled() {
		return getDeposit() == 0 && isCompensable() && !isDepositEnabled();
	}
	@Transient
	public boolean isPayBackEnabled() {
		return getDeposit() == 0 && !isCompensable();
	}
	@Transient
	public boolean isDepositEnabled() {
		return getDeposit() > 0 || (getDeposit() == 0 && getPayBack() == 0 && getCompensate() == 0);
	}

	@Transient
	public VatTaxDeclaration getDeclaration() {
		return this;
	}

	@Transient
	public String getBankAccount() {
		if (getRegistryBank() != null && getRegistryBank().getBankAccount() != null ) {
			return getRegistryBank().getBankAccount().getMaskedIban();
		}
		return null;
	}
	
	@Transient
	public String getBank() {
		if (getRegistryBank() != null) {
			return getRegistryBank().getAlias();
		}
		return null;
	}

	@Transient
	public boolean isFromCommonTerritory() {
		return (getAdministration() == Administration.COMMON_TERRITORY);
	}
	
}

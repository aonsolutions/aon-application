package com.code.aon.fiscal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.VatTaxDeclarationStatus;
import com.code.aon.fiscal.mod303.IMod303Declaration;
import com.esferalia.aon.entity.master.VatTaxDeclarationDB;

@Entity
@Table(name="fs_vat_declaration")
public class VatTaxDeclaration extends VatTaxDeclarationDB implements IMod303Declaration {

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

	@Override
	@Transient
	public int getYear() {
		return getVatTax()!=null?getVatTax().getYear():0;
	}

	@Override
	@Transient
	public Period getPeriod() {
		return getVatTax()!=null?getVatTax().getPeriod():null;
	}

	@Override
	@Transient
	public boolean isReplacement() {
		return getVatTax()!=null?getVatTax().isReplacement():false;
	}
	
	@Override
	@Transient
	public String getReplacedNumber() {
		return getVatTax()!=null?getVatTax().getReplacedNumber():null;
	}

	@Override
	@Transient
	public boolean isComplementary() {
		return getVatTax()!=null?getVatTax().isComplementary():false;
	}

	@Override
	@Transient
	public boolean isTaxRefundRegistry() {
		return getVatTax()!=null?getVatTax().isTaxRefundRegistry():false;
	}
	@Override
	@Transient
	public boolean isGeneralRegime() {
		return true;
	}

	@Override
	@Transient
	public double getProrata() {
		return getVatTax()!=null?getVatTax().getProrata():100.0;
	}

	@Override
	@Transient
	public IBankAccountContainer getBankAccountContainer() {
		return getRegistryBank();
	}
	
}

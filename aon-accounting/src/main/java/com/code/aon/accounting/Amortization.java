package com.code.aon.accounting;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.accounting.enumeration.AmortizationPeriod;
import com.esferalia.aon.entity.master.AmortizationDB;

@Entity
@Table(name="amortization")
public class Amortization extends AmortizationDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<AmortizationDetail> details;

	@Transient
	private AmortizationType amortizationType;
	
	@Transient
	public AmortizationType getAmortizationType() {
		return amortizationType;
	}
	public void setAmortizationType(AmortizationType amortizationType) {
		this.amortizationType = amortizationType;
	}
	
	@Transient
	private boolean detailsInitialized;

	@Transient
    public int getYears() {
		if (getPercentage()!= 0) {
			return (int) CommonUtil.round( 100 / getPercentage(),0);	
		}
		return 0;
	}

	public void setYears(int years) {
		if (years != 0) {
			setPercentage(CommonUtil.round( 100.0 / years));
		} else {
			setPercentage(0);	
		}
	}

	@OneToMany(mappedBy = "amortization", cascade={CascadeType.REMOVE})
	public List<AmortizationDetail> getDetails() {
		if (!isDetailsInitialized()) {
			calculateTotals(details);
			setDetailsInitialized(true);
		}
		return details;
	}

	public void setDetails(List<AmortizationDetail> details) {
		this.details = details;
	}

	
	@Transient
	public boolean isDetailsInitialized() {
		return detailsInitialized;
	}

	public void setDetailsInitialized(boolean detailsInitialized) {
		this.detailsInitialized = detailsInitialized;
	}

	@Transient
	public void calculateTotals(List<AmortizationDetail> list)  {
		if (list != null) {
			double accumulated = 0.0;
			double pending = 0.0;
			double fiscalAccumulated = 0.0;
			double fiscalPending = 0.0;
			boolean first = true;
			for (AmortizationDetail detail: list) {
				if (first) {
					pending = detail.getAmortization().getAmount();
					fiscalPending = detail.getAmortization().getAmount();
					first = false;
				}
				accumulated = CommonUtil.round(accumulated + detail.getAllocation());
				fiscalAccumulated = CommonUtil.round(fiscalAccumulated + detail.getFiscalAllocation());
	
				pending = CommonUtil.round(pending - detail.getAllocation());
				fiscalPending = CommonUtil.round(fiscalPending - detail.getFiscalAllocation());
	
				detail.setAccumulated(accumulated);
				detail.setFiscalAccumulated(fiscalAccumulated);
				detail.setPending(pending);
				detail.setFiscalPending(fiscalPending);
			}
		}
	}

	@Transient
	public boolean isYearly()  {
		return (getFeePeriod() == AmortizationPeriod.YEARLY);
	}
	
}

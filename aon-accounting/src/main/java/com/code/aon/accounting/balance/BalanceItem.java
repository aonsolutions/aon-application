/**
 * 
 */
package com.code.aon.accounting.balance;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.BalanceDetail;
import com.code.aon.common.AonVersion;
import com.code.aon.common.util.CommonUtil;

@Deprecated
public class BalanceItem implements Serializable{

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private BalanceDetail detail;
	private String notes;
	private Double amount;
	private Double previousAmount;

	public BalanceDetail getDetail() {
		return detail;
	}
	public void setDetail(BalanceDetail detail) {
		this.detail = detail;
	}

	public String getNotes() {
		return notes==null?getDetail().getNotes():notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getPreviousAmount() {
		return previousAmount;
	}
	public void setPreviousAmount(Double amount2) {
		this.previousAmount = amount2;
	}

	public Boolean isVisible() {
		if (getDetail().isVisible()) {
			if (getDetail().isZeroFlag() && getAmount() == 0 && getPreviousAmount() == 0) {
				return false;
			}
			return true;
		}
		return false;
	}

	public boolean isResolved() {
		return (getAmount() != null || getPreviousAmount() != null);
	}

	public String getReportDescription() {
		return  (StringUtils.isBlank(detail.getDescription())?"":
			StringUtils.leftPad(detail.getDescription(), (getLevel() * 5) + detail.getDescription().length()));
	}

	public String getAccounts() {
		return  getDetail().getAccounts();
	}

	public boolean isTitle() {
		return  (getDetail().isTitle() && StringUtils.isBlank(detail.getAccounts()));
	}

	public int getLevel() {
		if ( !StringUtils.isEmpty(detail.getCode())) {
			try {
				int c = Integer.parseInt(detail.getCode());
				if (c%10000 == 0) return 0;
				if (c%1000 == 0) return 1;
				if (c%100 == 0) return 2;
				if (c%10 == 0) return 3;
			} catch (NumberFormatException e) {
				// Nothing.
			}
		}
		return 0;
		
	}

	public void add(BalanceItem bi) {
		if (bi != null ) {
			if (bi.getAmount() != null ) {
				if (getAmount() == null) {
					setAmount(0.0);
				}
				setAmount( CommonUtil.round(getAmount() + bi.getAmount()));
			}
			if (bi.getPreviousAmount() != null ) {
				if (getPreviousAmount() == null) {
					setPreviousAmount(0.0);
				}
				setPreviousAmount( CommonUtil.round(getPreviousAmount() + bi.getPreviousAmount()));
			}
		}
	}
	
	public void subtract(BalanceItem bi) {
		if (bi != null ) {
			if (bi.getAmount() != null ) {
				if (getAmount() == null) {
					setAmount(0.0);
				}
				setAmount( CommonUtil.round(getAmount() - bi.getAmount()));
			}
			if (bi.getPreviousAmount() != null ) {
				if (getPreviousAmount() == null) {
					setPreviousAmount(0.0);
				}
				setPreviousAmount( CommonUtil.round(getPreviousAmount() - bi.getPreviousAmount()));
			}
		}
	}

	@Override
	public String toString() {
		return (detail.getSortKey() + "\t" + 
				detail.getCode() + "\t" +
				detail.isVisible() + "\t" +
				detail.isTitle() + "\t" +
				getAmount() + "\t" +
				getPreviousAmount() + "\t" +
				detail.getDescription() + "\t");
	}
	
}
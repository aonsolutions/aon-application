/**
 * 
 */
package com.code.aon.accounting.balance;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.BalanceDetail;
import com.code.aon.common.util.CommonUtil;

public class BalanceItem implements Serializable{

	private static final long serialVersionUID = 7537787806934473438L;
	
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
		return notes;
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

	public String getReportDescriptionWithAccounts() {
		return  getReportDescription() +(StringUtils.isBlank(getDetail().getAccounts())?"":
				(" ["+ getDetail().getAccounts() +"]"));
	}

	public boolean isTitle() {
		return  (getDetail().isTitle() && StringUtils.isBlank(detail.getAccounts()));
	}

	public int getLevel() {
		return detail.getCode() == null ? 0 : StringUtils.countMatches(detail.getCode(), ".");
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
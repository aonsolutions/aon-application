package com.code.aon.ui.accounting.controller.report;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.AnnualReportDetail;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class AnnualReportDetailController  extends LinesController  {
	
	private static final String EMPTY = "";
	private static final String PIPE = "|";
	private static final String ASTERISK = "*";
	private static final String COMMA = ",";
	
	private SummaryProviderParameters params;
	
	public void onExecute(ActionEvent event) {
		try {
			resolveList();
		} catch (ManagerBeanException e) {
			String msg = "Error al resolver la memoria";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void resolveList() throws ManagerBeanException {
		List<ITransferObject> list = (List<ITransferObject>) getModel().getWrappedData();
		for (ITransferObject to: list) {
			AnnualReportDetail detail = (AnnualReportDetail) to;
			if (StringUtils.contains(detail.getContent(), "#{")) {
				detail.setContentResolved(resolve( detail.getContent() )); 
			}
		}
	}

	private String resolve(String content) {
		String accounts = StringUtils.substringBetween(content, "#{","}");
		if (accounts!= null) {
			try {
				StringBuilder accountExp = new StringBuilder();
				String[] tokens = StringUtils.split(accounts,COMMA);
				for (String token:tokens) {
					token = token.trim();
					if (StringUtils.isNotBlank(token)) {
						accountExp.append(accountExp.length()>0?PIPE:EMPTY);
						accountExp.append(token);	
						accountExp.append(ASTERISK);
					}
				}
				Double amount = getAccountsAmount(accountExp.toString());
				DecimalFormat formatter = new DecimalFormat("#,##0.00");
				String value = null;
				try {
					value = formatter.format(amount);	
				} catch (NumberFormatException e) {
					
				}
				String before = StringUtils.substringBefore(content, "#{");
				String after  = StringUtils.substringAfter(content, "}");
				content = before + value + after;
				content = resolve(content);
			} catch (Throwable e) {
				String msg = "No se pudo realizar el Balance. " + e.getMessage();
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg, e);
			}
			
		}
		return content; 
	}

	private Double getAccountsAmount(String accountExp) throws ManagerBeanException {
		SummaryCollection summaryCollection = new SummaryCollection();
		SummaryProvider summaryProvider = new SummaryProvider();
		getParams().setAccountExpression(accountExp);
		summaryCollection = summaryProvider.getSummaryCollection(getParams(),false);
		return CommonUtil.round(summaryCollection.getCredit() - summaryCollection.getDebit());
	}

	private SummaryProviderParameters getParams() {
		if (params == null) {
			params = new SummaryProviderParameters();
			try {
				params .setPeriod(AccountingPeriodUtil.getDefaultPeriod());
			} catch (ManagerBeanException e) {
				params .setPeriod(null);
			}
			params.setFromDate(null);
			params.setToDate(null);
			params.setDate(new Date());
			params.setAccountExpression(null);
			params.setLowerLevelVisible(false);
			params.setNoTouchedAccountVisible(false);
			params.setRowsPerPage(20);
			params.setAccountLevel(5);
			params.setBudgeted(false);
			params.setPreviousPeriodVisible(true);
			
		}
		return params;
	}

}

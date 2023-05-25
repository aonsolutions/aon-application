package com.code.aon.ui.accounting.controller.report;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import jakarta.servlet.http.HttpServletResponse;

import org.dom4j.Element;

import com.code.aon.accounting.Balance;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.balance.BalanceManager;
import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.ui.accounting.controller.AccountingCollectionsController;
import com.code.aon.ui.util.AonUtil;

public class AnnualFinancialStatementsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Period period;
	
	private boolean coverEnabled;
	private boolean profitAndLostEnabled;
	private Balance profitAndLostBalance;
	private boolean situationEnabled;
	private Balance situationBalance;
	private boolean patrimonyAEnabled;
	private Balance patrimonyABalance;
	private boolean patrimonyBEnabled;
	private Balance patrimonyBBalance;
	private boolean reportEnabled;
	
	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}

	public boolean isCoverEnabled() {
		return coverEnabled;
	}
	public void setCoverEnabled(boolean coverEnabled) {
		this.coverEnabled = coverEnabled;
	}

	public boolean isProfitAndLostEnabled() {
		return profitAndLostEnabled;
	}
	public void setProfitAndLostEnabled(boolean profitAndLostEnabled) {
		this.profitAndLostEnabled = profitAndLostEnabled;
	}

	public Balance getProfitAndLostBalance() {
		return profitAndLostBalance;
	}
	public void setProfitAndLostBalance(Balance profitAndLostBalance) {
		this.profitAndLostBalance = profitAndLostBalance;
	}
	public boolean isSituationEnabled() {
		return situationEnabled;
	}
	public void setSituationEnabled(boolean situationEnabled) {
		this.situationEnabled = situationEnabled;
	}
	public Balance getSituationBalance() {
		return situationBalance;
	}
	public void setSituationBalance(Balance situationBalance) {
		this.situationBalance = situationBalance;
	}
	public boolean isPatrimonyAEnabled() {
		return patrimonyAEnabled;
	}
	public void setPatrimonyAEnabled(boolean patrimonyAEnabled) {
		this.patrimonyAEnabled = patrimonyAEnabled;
	}
	public Balance getPatrimonyABalance() {
		return patrimonyABalance;
	}
	public void setPatrimonyABalance(Balance patrimonyABalance) {
		this.patrimonyABalance = patrimonyABalance;
	}

	public boolean isPatrimonyBEnabled() {
		return patrimonyBEnabled;
	}
	public void setPatrimonyBEnabled(boolean patrimonyBEnabled) {
		this.patrimonyBEnabled = patrimonyBEnabled;
	}
	public Balance getPatrimonyBBalance() {
		return patrimonyBBalance;
	}
	public void setPatrimonyBBalance(Balance patrimonyBBalance) {
		this.patrimonyBBalance = patrimonyBBalance;
	}

	public boolean isReportEnabled() {
		return reportEnabled;
	}
	public void setReportEnabled(boolean reportEnabled) {
		this.reportEnabled = reportEnabled;
	}

	public List<SelectItem> getProfitAndLostBalances() throws ManagerBeanException {
		AccountingCollectionsController c = (AccountingCollectionsController) AonUtil
				.getRegisteredBean("accountingCollections");
		return c.getBalances(BalanceType.OPERATING);
	}

	public List<SelectItem> getSituationBalances() throws ManagerBeanException {
		AccountingCollectionsController c = (AccountingCollectionsController) AonUtil
		.getRegisteredBean("accountingCollections");
		return c.getBalances(BalanceType.CLOSING);
	}

	public List<SelectItem> getPatrimonyBalances() throws ManagerBeanException {
		AccountingCollectionsController c = (AccountingCollectionsController) AonUtil
		.getRegisteredBean("accountingCollections");
		return c.getBalances(BalanceType.PATRIMONY);
	}

	public void onReset(ActionEvent event) {
		setCoverEnabled(true);
		setProfitAndLostEnabled(true);
		setSituationEnabled(true);
		setPatrimonyAEnabled(true);
		setPatrimonyBEnabled(true);
		setReportEnabled(false);
	}

	public void onCreate(ActionEvent event) {
		check();
		try {
			BalanceManager bm = new BalanceManager();
			SummaryProviderParameters parameters = new SummaryProviderParameters(AonUtil.getDomainName());
			parameters = new SummaryProviderParameters(AonUtil.getDomainName());
			parameters.setPeriod(period);
			parameters.setFromDate(null);
			parameters.setToDate(null);
			parameters.setDate(new Date());
			parameters.setAccountExpression(null);
			parameters.setLowerLevelVisible(false);
			parameters.setNoTouchedAccountVisible(false);
			parameters.setRowsPerPage(20);
			parameters.setAccountLevel(4);
			Element root = bm.getIntecoModuleRoot(parameters);
			if (isCoverEnabled()) {
				bm.getIntecoCover(root, parameters);
			}
			if (isSituationEnabled()) {
				bm.getIntecoBalance(root,parameters, getSituationBalance());	
			}
			if (isProfitAndLostEnabled()) {
				bm.getIntecoBalance(root,parameters, getProfitAndLostBalance());
			}
			/*
			 //TODO
			if (isPatrimonyAEnabled()) {
				bm.getIntecoBalance(root,parameters, getPatrimonyABalance());
			}
			if (isPatrimonyBEnabled()) {
				bm.getIntecoBalance(root,parameters, getPatrimonyBBalance());
			}
			*/
			HttpServletResponse res = DownloadUtil.getResponse();
			DownloadUtil.initDownload(res, "pgc07.xml", MimeType.MIME_XML );
			bm.writeIntecoDocument(root.getDocument(), res.getOutputStream());
			DownloadUtil.finishDownload(res, res.getOutputStream());
			
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			String msg = "Imposible generar el informe. ["+ e.getMessage() +"]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (IOException e) {
			String msg = "Imposible generar el informe. ["+ e.getMessage() +"]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private void check() {
		boolean ok = true;
		if (isProfitAndLostEnabled() && getProfitAndLostBalance() == null) {
			AonUtil.addErrorMessage("Seleccione un Balance de Pérdidas y Ganacias.");
			ok = false;
		}
		if (isSituationEnabled() && getSituationBalance() == null) {
			AonUtil.addErrorMessage("Seleccione un Balance de Situación.");
			ok = false;
		}
		/*
		 //TODO 
		if (isPatrimonyAEnabled() && getPatrimonyABalance() == null) {
			AonUtil.addErrorMessage("Seleccione un Balance de Patrimonio.");
			ok = false;
		}
		if (isPatrimonyBEnabled() && getPatrimonyBBalance() == null) {
			AonUtil.addErrorMessage("Seleccione un Balance de Patrimonio.");
			ok = false;
		}
		*/
		if (!ok) {
			throw new AbortProcessingException( "Compruebe los parámetros." );
		}
	}

	public static void main(String[] args) {
		DecimalFormat df = new DecimalFormat("###.##");
		System.out.println( df.format(0));
	}
}

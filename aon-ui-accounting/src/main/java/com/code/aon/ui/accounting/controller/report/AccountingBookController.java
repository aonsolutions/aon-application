package com.code.aon.ui.accounting.controller.report;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.accounting.AnnualReport;
import com.code.aon.accounting.Balance;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.report.ReportException;
import com.code.aon.ui.accounting.controller.AccountingCollectionsController;
import com.code.aon.ui.accounting.controller.balance.BalanceSheetController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class AccountingBookController {
	private Period period;
	private boolean coverEnabled;
	private boolean journalEnabled;
	private boolean ledgerEnabled;
	private boolean trial1QuarterEnabled;
	private boolean trial2QuarterEnabled;
	private boolean trial3QuarterEnabled;
	private boolean trial4QuarterEnabled;
	private boolean profitAndLostEnabled;
	private Balance profitAndLostBalance;
	private boolean situationEnabled;
	private Balance situationBalance;
	private boolean patrimonyEnabled;
	private Balance patrimonyBalance;
	private boolean annualReportEnabled;
	private AnnualReport annualReport;

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

	public boolean isJournalEnabled() {
		return journalEnabled;
	}
	public void setJournalEnabled(boolean journalEnabled) {
		this.journalEnabled = journalEnabled;
	}

	public boolean isLedgerEnabled() {
		return ledgerEnabled;
	}
	public void setLedgerEnabled(boolean ledgerEnabled) {
		this.ledgerEnabled = ledgerEnabled;
	}

	public boolean isTrial1QuarterEnabled() {
		return trial1QuarterEnabled;
	}
	public void setTrial1QuarterEnabled(boolean trial1QuarterEnabled) {
		this.trial1QuarterEnabled = trial1QuarterEnabled;
	}

	public boolean isTrial2QuarterEnabled() {
		return trial2QuarterEnabled;
	}
	public void setTrial2QuarterEnabled(boolean trial2QuarterEnabled) {
		this.trial2QuarterEnabled = trial2QuarterEnabled;
	}

	public boolean isTrial3QuarterEnabled() {
		return trial3QuarterEnabled;
	}
	public void setTrial3QuarterEnabled(boolean trial3QuarterEnabled) {
		this.trial3QuarterEnabled = trial3QuarterEnabled;
	}

	public boolean isTrial4QuarterEnabled() {
		return trial4QuarterEnabled;
	}
	public void setTrial4QuarterEnabled(boolean trial4QuarterEnabled) {
		this.trial4QuarterEnabled = trial4QuarterEnabled;
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
	public boolean isPatrimonyEnabled() {
		return patrimonyEnabled;
	}
	public void setPatrimonyEnabled(boolean patrimonyEnabled) {
		this.patrimonyEnabled = patrimonyEnabled;
	}
	public Balance getPatrimonyBalance() {
		return patrimonyBalance;
	}
	public void setPatrimonyBalance(Balance patrimonyBalance) {
		this.patrimonyBalance = patrimonyBalance;
	}

	public boolean isAnnualReportEnabled() {
		return annualReportEnabled;
	}
	public void setAnnualReportEnabled(boolean annualReportEnabled) {
		this.annualReportEnabled = annualReportEnabled;
	}

	public AnnualReport getAnnualReport() {
		return annualReport;
	}
	public void setAnnualReport(AnnualReport annualReport) {
		this.annualReport = annualReport;
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
		setCoverEnabled(false);
		setJournalEnabled(true);
		setLedgerEnabled(true);
		setTrial1QuarterEnabled(true);
		setTrial2QuarterEnabled(true);
		setTrial3QuarterEnabled(true);
		setTrial4QuarterEnabled(true);
		setProfitAndLostEnabled(true);
		setSituationEnabled(true);
		setPatrimonyEnabled(true);
		setAnnualReportEnabled(true);
	}

	public void onCreate(ActionEvent event) {
		try {
			check();
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ec = ctx.getExternalContext();
			HttpServletResponse res = (HttpServletResponse) ec.getResponse();
			res.setContentType(MimeType.MIME_ZIP.getName());
			res.setHeader("Content-Disposition", "attachment; filename=\""+getPeriod().getId()+".zip\";");
			
		    ZipOutputStream zout = new ZipOutputStream(res.getOutputStream());
		    zout.setLevel(9);
		    
		    if (isCoverEnabled()) {
				
			}
			if (isJournalEnabled()) {
				addJournal(zout);
			}
			if (isLedgerEnabled()) {
				addLedger(zout);
			}
			if (isTrial1QuarterEnabled()) {
				addTrial1Quarter(zout);
			}
			if (isTrial2QuarterEnabled()) {
				addTrial2Quarter(zout);
			}
			if (isTrial3QuarterEnabled()) {
				addTrial3Quarter(zout);
			}
			if (isTrial4QuarterEnabled()) {
				addTrial4Quarter(zout);
			}
			if (isProfitAndLostEnabled()) {
				addProfitAndLost(zout);
			}
			if (isSituationEnabled()) {
				addSituation(zout);
			}
			if (isPatrimonyEnabled()) {
				addPatrimony(zout);
			}
			if (isAnnualReportEnabled()) {
				addAnnualReport(zout);
			}
			zout.flush();
			zout.finish();
		    res.flushBuffer();
			ctx.responseComplete();
		} catch (IOException e) {
			AonUtil.addErrorMessage("El fichero no es correcto");
			throw new AbortProcessingException( e );
		} catch (ReportException e) {
			AonUtil.addErrorMessage("Error al ejecutar el listado");
			throw new AbortProcessingException( e );
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error al ejecutar el listado");
			throw new AbortProcessingException( e );
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
		if (isPatrimonyEnabled() && getPatrimonyBalance() == null) {
			AonUtil.addErrorMessage("Seleccione un Balance de Patrimonio.");
			ok = false;
		}
		if (isAnnualReportEnabled() && getAnnualReport() == null) {
			AonUtil.addErrorMessage("Seleccione un modelo de Memoria Anual.");
			ok = false;
		}
		if (!ok) {
			throw new AbortProcessingException( "Compruebe los parámetros." );
		}
	}
	private void addJournal(ZipOutputStream zout) throws ReportException, IOException{
		ZipEntry ze = new ZipEntry("Diario.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        JournalReportController t = (JournalReportController) AonUtil.getRegisteredBean("journalReport");
        t.onEditSearch(null);
        t.onReset(null);
        t.setPeriod(getPeriod());
        t.setJournal(true);
        t.onSearch(null);
		manager.execute(zout, "journalBook");
		zout.closeEntry();
	}

	private void addLedger(ZipOutputStream zout) throws ReportException, IOException{
		ZipEntry ze = new ZipEntry("Mayores.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        LedgerReportController t = (LedgerReportController) AonUtil.getRegisteredBean("ledgerReport");
        t.onEditSearch(null);
        t.onReset(null);
        t.setPeriod(getPeriod());
        t.setOrder("3");
        t.onSearch(null);
		manager.execute(zout, "ledgerBook");
		zout.closeEntry();
	}

	private void addTrial1Quarter(ZipOutputStream zout) throws ReportException, IOException {
		ZipEntry ze = new ZipEntry("BalanceSumasSaldos-01-03.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        TrialBalanceController t = (TrialBalanceController) AonUtil.getRegisteredBean("trialBalance");
        t.getParameters().setFromDate(getPeriod().getInitiationDate());
        Calendar c = Calendar.getInstance();
        c.setTime(getPeriod().getInitiationDate());
        c.add(Calendar.MONTH, 3);
        c.add(Calendar.DAY_OF_MONTH, -1);
        t.getParameters().setToDate(c.getTime());
        t.onSearch(null);
		manager.execute(zout, "trialBalance");
		zout.closeEntry();
	}
	private void addTrial2Quarter(ZipOutputStream zout) throws IOException, ReportException {
		ZipEntry ze = new ZipEntry("BalanceSumasSaldos-01-06.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        TrialBalanceController t = (TrialBalanceController) AonUtil.getRegisteredBean("trialBalance");
        t.getParameters().setFromDate(getPeriod().getInitiationDate());
        Calendar c = Calendar.getInstance();
        c.setTime(getPeriod().getInitiationDate());
        c.add(Calendar.MONTH, 6);
        c.add(Calendar.DAY_OF_MONTH, -1);
        t.getParameters().setToDate(c.getTime());
        t.onSearch(null);
		manager.execute(zout, "trialBalance");
		zout.closeEntry();
	}
	private void addTrial3Quarter(ZipOutputStream zout) throws ReportException, IOException {
		ZipEntry ze = new ZipEntry("BalanceSumasSaldos-01-09.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        TrialBalanceController t = (TrialBalanceController) AonUtil.getRegisteredBean("trialBalance");
        t.getParameters().setFromDate(getPeriod().getInitiationDate());
        Calendar c = Calendar.getInstance();
        c.setTime(getPeriod().getInitiationDate());
        c.add(Calendar.MONTH, 9);
        c.add(Calendar.DAY_OF_MONTH, -1);
        t.getParameters().setToDate(c.getTime());
        t.onSearch(null);
		manager.execute(zout, "trialBalance");
		zout.closeEntry();
	}
	private void addTrial4Quarter(ZipOutputStream zout) throws ReportException, IOException {
		ZipEntry ze = new ZipEntry("BalanceSumasSaldos-01-12.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        TrialBalanceController t = (TrialBalanceController) AonUtil.getRegisteredBean("trialBalance");
        t.getParameters().setFromDate(getPeriod().getInitiationDate());
        t.getParameters().setToDate(getPeriod().getDeadline());
        t.onSearch(null);
		manager.execute(zout, "trialBalance");
		zout.closeEntry();
	}

	private void addProfitAndLost(ZipOutputStream zout) throws ReportException, IOException{
		ZipEntry ze = new ZipEntry("BalancePerdidasGanancias.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        BalanceSheetController t = (BalanceSheetController) AonUtil.getRegisteredBean("balanceSheet");
        t.setBalanceType(BalanceType.OPERATING);
        t.onReset(null);
        t.getParameters().setPeriod(getPeriod());
        t.setBalance(getProfitAndLostBalance());
        t.onBalance(null);
		manager.execute(zout, "officialBalance");
		zout.closeEntry();
	}

	private void addSituation(ZipOutputStream zout) throws ReportException, IOException{
		ZipEntry ze = new ZipEntry("BalanceSituacion.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        BalanceSheetController t = (BalanceSheetController) AonUtil.getRegisteredBean("balanceSheet");
        t.setBalanceType(BalanceType.CLOSING);
        t.onReset(null);
        t.getParameters().setPeriod(getPeriod());
        t.setBalance(getSituationBalance());
        t.onBalance(null);
		manager.execute(zout, "officialBalance");
		zout.closeEntry();
	}

	private void addPatrimony(ZipOutputStream zout) throws ReportException, IOException{
		ZipEntry ze = new ZipEntry("BalancePatrimonio.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        BalanceSheetController t = (BalanceSheetController) AonUtil.getRegisteredBean("balanceSheet");
        t.setBalanceType(BalanceType.PATRIMONY);
        t.onReset(null);
        t.getParameters().setPeriod(getPeriod());
        t.setBalance(getPatrimonyBalance());
        t.onBalance(null);
		manager.execute(zout, "officialBalance");
		zout.closeEntry();
	}

	private void addAnnualReport(ZipOutputStream zout) throws IOException, ManagerBeanException {
		ZipEntry ze = new ZipEntry("MemoriaAnual.pdf");
	    zout.putNextEntry(ze);
        AnnualReportLauncher c = (AnnualReportLauncher) AonUtil.getRegisteredBean("annualReportLauncher");
        c.onReset(null);
        c.setAnnualReport(getAnnualReport());
        c.getParams().setPeriod(getPeriod());
        c.pdf(zout);
		zout.closeEntry();
	}
	
}

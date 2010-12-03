package com.code.aon.ui.accounting.controller.report;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
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
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.fiscal.enumeration.InvoiceReportOrder;
import com.code.aon.fiscal.enumeration.VatType;
import com.code.aon.report.ReportException;
import com.code.aon.ui.accounting.controller.AccountingCollectionsController;
import com.code.aon.ui.accounting.controller.balance.BalanceSheetController;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.fiscal.controller.VatReportController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class AccountingBookController implements ICollectionProvider{
	private Period period;
	private boolean coverEnabled;
	private boolean journalEnabled;
	private boolean ledgerEnabled;
	private boolean trial1QuarterEnabled;
	private boolean trial2QuarterEnabled;
	private boolean trial3QuarterEnabled;
	private boolean trial4QuarterEnabled;
	private boolean outputVatEnabled;
	private boolean inputVatEnabled;
	private boolean investmentVatEnabled;
	private boolean profitAndLostEnabled;
	private Balance profitAndLostBalance;
	private boolean situationEnabled;
	private Balance situationBalance;
	private boolean patrimonyEnabled;
	private Balance patrimonyBalance;
	private boolean annualReportEnabled;
	private AnnualReport annualReport;
	private int generatedPages = 0;
	private String coverTitle;
	private String coverSubTitle;
	private Date constitutionDate;
	
	public Date getConstitutionDate() {
		if (constitutionDate == null) {
			CompanyController cc = (CompanyController) AonUtil.getRegisteredBean("company");
			try {
				constitutionDate = cc.getCompanyRecordData().getCreationDate();
			} catch (ManagerBeanException e) {
				
			}
		}
		return constitutionDate;
	}
	public void setConstitutionDate(Date constitutionDate) {
		this.constitutionDate = constitutionDate;
	}
	
	public String getCoverTitle() {
		return coverTitle;
	}
	public void setCoverTitle(String coverTitle) {
		this.coverTitle = coverTitle;
	}

	public String getCoverSubTitle() {
		return coverSubTitle;
	}
	public void setCoverSubTitle(String coverSubTitle) {
		this.coverSubTitle = coverSubTitle;
	}

	public int getGeneratedPages() {
		return generatedPages;
	}
	public void setGeneratedPages(int generatedPages) {
		this.generatedPages = generatedPages;
	}

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

	public boolean isOutputVatEnabled() {
		return outputVatEnabled;
	}
	public void setOutputVatEnabled(boolean outputVatEnabled) {
		this.outputVatEnabled = outputVatEnabled;
	}
	
	public boolean isInputVatEnabled() {
		return inputVatEnabled;
	}
	public void setInputVatEnabled(boolean inputVatEnabled) {
		this.inputVatEnabled = inputVatEnabled;
	}

	public boolean isInvestmentVatEnabled() {
		return investmentVatEnabled;
	}
	public void setInvestmentVatEnabled(boolean investmentVatEnabled) {
		this.investmentVatEnabled = investmentVatEnabled;
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
		setCoverEnabled(true);
		setJournalEnabled(true);
		setLedgerEnabled(true);
		setTrial1QuarterEnabled(true);
		setTrial2QuarterEnabled(true);
		setTrial3QuarterEnabled(true);
		setTrial4QuarterEnabled(true);
		setOutputVatEnabled(false);
		setInputVatEnabled(false);
		setInvestmentVatEnabled(false);
		setProfitAndLostEnabled(true);
		setSituationEnabled(true);
		setPatrimonyEnabled(true);
		setAnnualReportEnabled(false);
		setGeneratedPages(0);
		setCoverTitle("CUENTAS ANUALES");
		setCoverSubTitle("PLAN GENERAL DE CONTABILIDAD DE PEQUEÑAS Y MEDIANAS EMPRESAS");
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
		    int index = 1;
		    if (isCoverEnabled()) {
				addCover(zout,index);
				++index;
			}
			if (isJournalEnabled()) {
				addJournal(zout,index);
				++index;
			}
			if (isLedgerEnabled()) {
				addLedger(zout,index);
				++index;
			}
			if (isTrial1QuarterEnabled()) {
				addTrial1Quarter(zout,index);
				++index;
			}
			if (isTrial2QuarterEnabled()) {
				addTrial2Quarter(zout,index);
				++index;
			}
			if (isTrial3QuarterEnabled()) {
				addTrial3Quarter(zout,index);
				++index;
			}
			if (isTrial4QuarterEnabled()) {
				addTrial4Quarter(zout,index);
				++index;
			}
			if (isOutputVatEnabled()) {
				addOutputVat(zout,index);
				++index;
			}
			if (isInputVatEnabled()) {
				addInputVat(zout,index);
				++index;
			}
			if (isInvestmentVatEnabled()) {
				addInvestmentVat(zout,index);
				++index;
			}
			if (isProfitAndLostEnabled()) {
				addProfitAndLost(zout,index);
				++index;
			}
			if (isSituationEnabled()) {
				addSituation(zout,index);
				++index;
			}
			if (isPatrimonyEnabled()) {
				addPatrimony(zout,index);
				++index;
			}
			if (isAnnualReportEnabled()) {
				addAnnualReport(zout,index);
				++index;
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

	private void addCover(ZipOutputStream zout,int index) throws ReportException, IOException{
		NumberFormat formatter = new DecimalFormat("00");
		ZipEntry ze = new ZipEntry(formatter.format(index) + "-Portada.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
		String out = manager.execute(zout, "cover");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}

	private void addJournal(ZipOutputStream zout,int index) throws ReportException, IOException{
		NumberFormat formatter = new DecimalFormat("00");
		ZipEntry ze = new ZipEntry(formatter.format(index) + "-Diario.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        JournalReportController t = (JournalReportController) AonUtil.getRegisteredBean("journalReport");
        t.onEditSearch(null);
        t.onReset(null);
        t.setCoverVisible(true);
        t.setCounterVisible(true);
        t.setPageCounter(getGeneratedPages());
        t.setPeriod(getPeriod());
        t.setJournal(true);
        t.onSearch(null);
		String out = manager.execute(zout, "journalBook");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}

	private void addLedger(ZipOutputStream zout,int index) throws ReportException, IOException{
		NumberFormat formatter = new DecimalFormat("00");
		ZipEntry ze = new ZipEntry(formatter.format(index) + "-Mayor.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        LedgerReportController t = (LedgerReportController) AonUtil.getRegisteredBean("ledgerReport");
        t.onEditSearch(null);
        t.onReset(null);
        t.setCoverVisible(true);
        t.setCounterVisible(true);
        t.setPageCounter(getGeneratedPages());
        t.setPeriod(getPeriod());
        t.setOrder("3");
        t.onSearch(null);
		String out = manager.execute(zout, "ledgerBook");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}

	private void addTrial1Quarter(ZipOutputStream zout,int index) throws ReportException, IOException {
		NumberFormat formatter = new DecimalFormat("00");
		ZipEntry ze = new ZipEntry(formatter.format(index) + "-BalanceSumasSaldos-01-03.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        TrialBalanceController t = (TrialBalanceController) AonUtil.getRegisteredBean("trialBalance");
        t.getParameters().setFromDate(getPeriod().getInitiationDate());
        Calendar c = Calendar.getInstance();
        c.setTime(getPeriod().getInitiationDate());
        c.add(Calendar.MONTH, 3);
        c.add(Calendar.DAY_OF_MONTH, -1);
        t.getParameters().setToDate(c.getTime());
        t.getParameters().setCoverVisible(true);
        t.getParameters().setCounterVisible(true);
        t.getParameters().setExcludeClosingEntry(true);
        t.getParameters().setExcludeOperatingEntry(true);
        t.getParameters().setPageCounter(getGeneratedPages());
        t.onSearch(null);
		String out = manager.execute(zout, "trialBalance");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}
	private void addTrial2Quarter(ZipOutputStream zout,int index) throws IOException, ReportException {
		NumberFormat formatter = new DecimalFormat("00");
		ZipEntry ze = new ZipEntry(formatter.format(index) + "-BalanceSumasSaldos-01-06.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        TrialBalanceController t = (TrialBalanceController) AonUtil.getRegisteredBean("trialBalance");
        t.getParameters().setFromDate(getPeriod().getInitiationDate());
        Calendar c = Calendar.getInstance();
        c.setTime(getPeriod().getInitiationDate());
        c.add(Calendar.MONTH, 6);
        c.add(Calendar.DAY_OF_MONTH, -1);
        t.getParameters().setToDate(c.getTime());
        t.getParameters().setCoverVisible(true);
        t.getParameters().setCounterVisible(true);
        t.getParameters().setExcludeClosingEntry(true);
        t.getParameters().setExcludeOperatingEntry(true);
        t.getParameters().setPageCounter(getGeneratedPages());
        t.onSearch(null);
		String out = manager.execute(zout, "trialBalance");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}
	private void addTrial3Quarter(ZipOutputStream zout,int index) throws ReportException, IOException {
		NumberFormat formatter = new DecimalFormat("00");
		ZipEntry ze = new ZipEntry(formatter.format(index) + "-BalanceSumasSaldos-01-09.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        TrialBalanceController t = (TrialBalanceController) AonUtil.getRegisteredBean("trialBalance");
        t.getParameters().setFromDate(getPeriod().getInitiationDate());
        Calendar c = Calendar.getInstance();
        c.setTime(getPeriod().getInitiationDate());
        c.add(Calendar.MONTH, 9);
        c.add(Calendar.DAY_OF_MONTH, -1);
        t.getParameters().setToDate(c.getTime());
        t.getParameters().setCoverVisible(true);
        t.getParameters().setCounterVisible(true);
        t.getParameters().setExcludeClosingEntry(true);
        t.getParameters().setExcludeOperatingEntry(true);
        t.getParameters().setPageCounter(getGeneratedPages());
        t.onSearch(null);
		String out = manager.execute(zout, "trialBalance");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}
	private void addTrial4Quarter(ZipOutputStream zout,int index) throws ReportException, IOException {
		NumberFormat formatter = new DecimalFormat("00");
		ZipEntry ze = new ZipEntry(formatter.format(index) + "-BalanceSumasSaldos-01-12.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        TrialBalanceController t = (TrialBalanceController) AonUtil.getRegisteredBean("trialBalance");
        t.getParameters().setFromDate(getPeriod().getInitiationDate());
        t.getParameters().setToDate(getPeriod().getDeadline());
        t.getParameters().setCoverVisible(true);
        t.getParameters().setCounterVisible(true);
        t.getParameters().setExcludeClosingEntry(true);
        t.getParameters().setExcludeOperatingEntry(true);
        t.getParameters().setPageCounter(getGeneratedPages());
        t.onSearch(null);
		String out = manager.execute(zout, "trialBalance");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}

	private void addOutputVat(ZipOutputStream zout,int index) throws ReportException, IOException {
		NumberFormat formatter = new DecimalFormat("00");
		ZipEntry ze = new ZipEntry(formatter.format(index) + "-Libro IVA Repercutido.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        VatReportController t = (VatReportController) AonUtil.getRegisteredBean("vatReport");
        t.onReset(null);
        t.setAccountPeriod(getPeriod());
        t.setFromDate(getPeriod().getInitiationDate());
        t.setToDate(getPeriod().getDeadline());
        t.setCoverVisible(true);
        t.setCounterVisible(true);
        t.setVatType(VatType.OUTPUT);
        t.setOrder( InvoiceReportOrder.INVOICE_ORDER_NUMBER);
        t.setPageCounter(getGeneratedPages());
        t.onAccountingBookDetail(null);
		String out = manager.execute(zout, "vatBook");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}

	private void addInputVat(ZipOutputStream zout,int index) throws ReportException, IOException {
		NumberFormat formatter = new DecimalFormat("00");
		ZipEntry ze = new ZipEntry(formatter.format(index) + "-Libro IVA Soportado.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        VatReportController t = (VatReportController) AonUtil.getRegisteredBean("vatReport");
        t.onReset(null);
        t.setAccountPeriod(getPeriod());
        t.setFromDate(getPeriod().getInitiationDate());
        t.setToDate(getPeriod().getDeadline());
        t.setCoverVisible(true);
        t.setCounterVisible(true);
        t.setVatType(VatType.INPUT);
        t.setOrder( InvoiceReportOrder.INVOICE_ORDER_NUMBER);
        t.setPageCounter(getGeneratedPages());
        t.onAccountingBookDetail(null);
		String out = manager.execute(zout, "vatBook");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}

	private void addInvestmentVat(ZipOutputStream zout,int index) throws ReportException, IOException {
		NumberFormat formatter = new DecimalFormat("00");
		ZipEntry ze = new ZipEntry(formatter.format(index) + "-Libro IVA Inversion.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        VatReportController t = (VatReportController) AonUtil.getRegisteredBean("vatReport");
        t.onReset(null);
        t.setAccountPeriod(getPeriod());
        t.setFromDate(getPeriod().getInitiationDate());
        t.setToDate(getPeriod().getDeadline());
        t.setCoverVisible(true);
        t.setCounterVisible(true);
        t.setOrder( InvoiceReportOrder.INVOICE_ORDER_NUMBER);
        t.setVatType(VatType.INVESTMENT);
        t.setPageCounter(getGeneratedPages());
        t.onAccountingBookDetail(null);
		String out = manager.execute(zout, "vatBook");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}

	private void addProfitAndLost(ZipOutputStream zout,int index) throws ReportException, IOException{
		NumberFormat formatter = new DecimalFormat("00");
		ZipEntry ze = new ZipEntry(formatter.format(index) + "-BalancePerdidasGanancias.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        BalanceSheetController t = (BalanceSheetController) AonUtil.getRegisteredBean("balanceSheet");
        t.setBalanceType(BalanceType.OPERATING);
        t.onReset(null);
        t.getParameters().setPeriod(getPeriod());
        t.getParameters().setCoverVisible(true);
        t.getParameters().setCounterVisible(true);
        t.getParameters().setPageCounter(getGeneratedPages());
        t.getParameters().setExcludeClosingEntry(true);
        t.getParameters().setExcludeOperatingEntry(true);
        t.setBalance(getProfitAndLostBalance());
        t.onBalance(null);
		String out = manager.execute(zout, "officialBalance");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}

	private void addSituation(ZipOutputStream zout, int index) throws ReportException, IOException{
		NumberFormat formatter = new DecimalFormat("00");
		ZipEntry ze = new ZipEntry(formatter.format(index) + "-BalanceSituacion.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        BalanceSheetController t = (BalanceSheetController) AonUtil.getRegisteredBean("balanceSheet");
        t.setBalanceType(BalanceType.CLOSING);
        t.onReset(null);
        t.getParameters().setPeriod(getPeriod());
        t.getParameters().setCoverVisible(true);
        t.getParameters().setCounterVisible(true);
        t.getParameters().setPageCounter(getGeneratedPages());
        t.getParameters().setExcludeClosingEntry(true);
        t.getParameters().setExcludeOperatingEntry(true);
        t.setBalance(getSituationBalance());
        t.onBalance(null);
		String out = manager.execute(zout, "officialBalance");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}

	private void addPatrimony(ZipOutputStream zout, int index) throws ReportException, IOException{
		NumberFormat formatter = new DecimalFormat("00");
		ZipEntry ze = new ZipEntry(formatter.format(index) + "-BalancePatrimonio.pdf");
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        BalanceSheetController t = (BalanceSheetController) AonUtil.getRegisteredBean("balanceSheet");
        t.setBalanceType(BalanceType.PATRIMONY);
        t.onReset(null);
        t.getParameters().setPeriod(getPeriod());
        t.getParameters().setCoverVisible(true);
        t.getParameters().setCounterVisible(true);
        t.getParameters().setPageCounter(getGeneratedPages());
        t.setBalance(getPatrimonyBalance());
        t.onBalance(null);
		String out = manager.execute(zout, "officialBalance");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}

	private void addAnnualReport(ZipOutputStream zout,int index) throws IOException, ManagerBeanException {
		NumberFormat formatter = new DecimalFormat("00");
		ZipEntry ze = new ZipEntry(formatter.format(index) + "-MemoriaAnual.pdf");
	    zout.putNextEntry(ze);
        AnnualReportLauncher c = (AnnualReportLauncher) AonUtil.getRegisteredBean("annualReportLauncher");
        c.onReset(null);
        c.setAnnualReport(getAnnualReport());
        c.getParams().setPeriod(getPeriod());
        c.pdf(zout);
		zout.closeEntry();
	}

	@Override
	public Collection<?> getCollection() {
		try {
			return getCollection(false);
		} catch (ManagerBeanException e) {
			return null;
		}
	}
	@Override
	public Collection<?> getCollection(boolean forceRefresh) throws ManagerBeanException {
		List<Company> list = new LinkedList<Company>();
		CompanyController cc = (CompanyController) AonUtil.getRegisteredBean("company");
		Company company = cc.obtainCompany();
		list.add(company);
		return list;
	}
	
}

package com.code.aon.ui.accounting.controller.report;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Balance;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.annualReport.AnnualReportContext;
import com.code.aon.accounting.annualReport.AnnualReportParameters;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.fiscal.enumeration.InvoiceReportOrder;
import com.code.aon.fiscal.enumeration.VatType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.report.ReportException;
import com.code.aon.ui.accounting.controller.AccountRegeneratorController;
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
	private boolean reportTemplateEnabled;
	private Integer reportTemplate;
	private boolean reportEnabled;
	private Integer report;
	private int generatedPages = 0;
	private String coverTitle;
	private String coverSubTitle;
	private Date constitutionDate;
	private LinkedList<Book> bookList;
	private NumberFormat formatter = new DecimalFormat("000");
	private DateFormat dateFormatter = new SimpleDateFormat("ddMMyyyy");
	
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

	public boolean isReportTemplateEnabled() {
		return reportTemplateEnabled;
	}
	public void setReportTemplateEnabled(boolean reportTemplateEnabled) {
		this.reportTemplateEnabled = reportTemplateEnabled;
	}

	public Integer getReportTemplate() {
		return reportTemplate;
	}
	public void setReportTemplate(Integer reportTemplate) {
		this.reportTemplate = reportTemplate;
	}

	public boolean isReportEnabled() {
		return reportEnabled;
	}
	public void setReportEnabled(boolean reportEnabled) {
		this.reportEnabled = reportEnabled;
	}

	public Integer getReport() {
		return report;
	}
	public void setReport(Integer report) {
		this.report = report;
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
		setReportTemplateEnabled(false);
		setReportTemplate(null);
		setReportEnabled(false);
		setReport(null);
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
			
			CompanyController cc = (CompanyController) AonUtil.getRegisteredBean("company");
			String name = cc.getCompanyLabel();
			name = StringUtils.upperCase(name);
			name = StringUtils.deleteWhitespace(name);
			name = StringUtils.substring(name, 0, 8);
			String chars = "!\"·$%&/()=?¿'¡+`^*Ç{}[]-_";
			name = StringUtils.replaceChars(name, chars, "");
			if (StringUtils.isEmpty(name)) {
				name = getPeriod().getName();
			}
			res.setHeader("Content-Disposition", "attachment; filename=\""+name+".zip\";");
			bookList = new LinkedList<Book>();			
		    ZipOutputStream zout = new ZipOutputStream(res.getOutputStream());
		    zout.setLevel(9);
		    if (isCoverEnabled()) {
				addCover(zout);
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
			if (isOutputVatEnabled()) {
				addOutputVat(zout);
			}
			if (isInputVatEnabled()) {
				addInputVat(zout);
			}
			if (isInvestmentVatEnabled()) {
				addInvestmentVat(zout);
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
			if (isReportTemplateEnabled()) {
				addReportTemplate(zout);
			}
			if (isReportEnabled()) {
				addReport(zout);
			}
			addRequiredFiles(zout);
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
		if (!ok) {
			throw new AbortProcessingException( "Compruebe los parámetros." );
		}
	}

	private Book addBook(BookType type, String description) {
		return addBook(type,description,".pdf");
	}
	
	private Book addBook(BookType type, String description, String extension) {
		Book book = new Book(extension);
		book.setBookType(type);
		book.setDescription(description);
		int n = 1;
		for (Book b:bookList) {
			n = n + (b.getBookType() == type?1:0);
		}
		book.setNumber(n);
		bookList.add(book);
		return book;
	}

	private void addCover(ZipOutputStream zout) throws ReportException, IOException{
		BookType type = BookType.OTROS;
		Book book = addBook(type,"Portada");
		ZipEntry ze = new ZipEntry(book.getName());
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
	
	private void addJournal(ZipOutputStream zout) throws ReportException, IOException{
		BookType type = BookType.DIARIO;
		Book book = addBook(type,"Diario");
		ZipEntry ze = new ZipEntry(book.getName());
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        JournalReportController t = (JournalReportController) AonUtil.getRegisteredBean("journalReport");
        t.onEditSearch(null);
        t.onReset(null);
        t.setCoverVisible(true);
        t.setCounterVisible(true);
        t.setPageCounter(getGeneratedPages());
        t.setPeriod(getPeriod());
        t.setOrder(2);
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

	public boolean isJournalCorrect() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(AccountEntry.class);
			Criteria c = new Criteria();
			if (getPeriod() != null) {
				c.addEqualExpression( bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD_ID), getPeriod().getId());	
			}
			c.addNullExpression( bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_JOURNAL));
			int count = bean.getCount(c);
			return (count==0);
		} catch (ManagerBeanException e) {
			return false;
		}
	}
	
	public void onRegenerate(ActionEvent event) {
		AccountRegeneratorController arc = (AccountRegeneratorController) AonUtil.getRegisteredBean("accountRegenerator");
		arc.onEditSearch(event);
		arc.setJournal(true);
		arc.setPeriod(getPeriod());
		arc.setSecurityLevel(null);
		arc.regenerateAccount(event);
	}

	private void addLedger(ZipOutputStream zout) throws ReportException, IOException{
		BookType type = BookType.MAYOR;
		Book book = addBook(type,"Mayor de Cuentas");
		ZipEntry ze = new ZipEntry(book.getName());
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

	private void addTrial1Quarter(ZipOutputStream zout) throws ReportException, IOException {
		BookType type = BookType.BAL_SUMS;
		Book book = addBook(type,"Balance de Summas y Saldos 01-03");
		ZipEntry ze = new ZipEntry(book.getName());
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        TrialBalanceController t = (TrialBalanceController) AonUtil.getRegisteredBean("officialTrialBalance");
        t.getParameters().setFromDate(getPeriod().getInitiationDate());
        Calendar c = Calendar.getInstance();
        c.setTime(getPeriod().getInitiationDate());
        c.add(Calendar.MONTH, 3);
        c.add(Calendar.DAY_OF_MONTH, -1);
        t.getParameters().setPeriod(getPeriod());
        t.getParameters().setToDate(c.getTime());
        t.getParameters().setCoverVisible(true);
        t.getParameters().setCounterVisible(true);
        t.getParameters().setExcludeClosingEntry(true);
        t.getParameters().setExcludeOperatingEntry(true);
        t.getParameters().setPageCounter(getGeneratedPages());
        t.onSearch(null);
		String out = manager.execute(zout, "officialTrialBalance");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}
	private void addTrial2Quarter(ZipOutputStream zout) throws IOException, ReportException {
		BookType type = BookType.BAL_SUMS;
		Book book = addBook(type,"Balance de Summas y Saldos 01-06");
		ZipEntry ze = new ZipEntry(book.getName());
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        TrialBalanceController t = (TrialBalanceController) AonUtil.getRegisteredBean("officialTrialBalance");
        t.getParameters().setFromDate(getPeriod().getInitiationDate());
        Calendar c = Calendar.getInstance();
        c.setTime(getPeriod().getInitiationDate());
        c.add(Calendar.MONTH, 6);
        c.add(Calendar.DAY_OF_MONTH, -1);
        t.getParameters().setPeriod(getPeriod());
        t.getParameters().setToDate(c.getTime());
        t.getParameters().setCoverVisible(true);
        t.getParameters().setCounterVisible(true);
        t.getParameters().setExcludeClosingEntry(true);
        t.getParameters().setExcludeOperatingEntry(true);
        t.getParameters().setPageCounter(getGeneratedPages());
        t.onSearch(null);
		String out = manager.execute(zout, "officialTrialBalance");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}
	private void addTrial3Quarter(ZipOutputStream zout) throws ReportException, IOException {
		BookType type = BookType.BAL_SUMS;
		Book book = addBook(type,"Balance de Summas y Saldos 01-09");
		ZipEntry ze = new ZipEntry(book.getName());
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        TrialBalanceController t = (TrialBalanceController) AonUtil.getRegisteredBean("officialTrialBalance");
        t.getParameters().setFromDate(getPeriod().getInitiationDate());
        Calendar c = Calendar.getInstance();
        c.setTime(getPeriod().getInitiationDate());
        c.add(Calendar.MONTH, 9);
        c.add(Calendar.DAY_OF_MONTH, -1);
        t.getParameters().setPeriod(getPeriod());
        t.getParameters().setToDate(c.getTime());
        t.getParameters().setCoverVisible(true);
        t.getParameters().setCounterVisible(true);
        t.getParameters().setExcludeClosingEntry(true);
        t.getParameters().setExcludeOperatingEntry(true);
        t.getParameters().setPageCounter(getGeneratedPages());
        t.onSearch(null);
		String out = manager.execute(zout, "officialTrialBalance");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}
	private void addTrial4Quarter(ZipOutputStream zout) throws ReportException, IOException {
		BookType type = BookType.BAL_SUMS;
		Book book = addBook(type,"Balance de Summas y Saldos 01-12");
		ZipEntry ze = new ZipEntry(book.getName());
	    ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
	    zout.putNextEntry(ze);
        TrialBalanceController t = (TrialBalanceController) AonUtil.getRegisteredBean("officialTrialBalance");
        t.getParameters().setPeriod(getPeriod());
        t.getParameters().setFromDate(getPeriod().getInitiationDate());
        t.getParameters().setToDate(getPeriod().getDeadline());
        t.getParameters().setCoverVisible(true);
        t.getParameters().setCounterVisible(true);
        t.getParameters().setExcludeClosingEntry(true);
        t.getParameters().setExcludeOperatingEntry(true);
        t.getParameters().setPageCounter(getGeneratedPages());
        t.onSearch(null);
		String out = manager.execute(zout, "officialTrialBalance");
		int i = 0;
		try {
			i = Integer.parseInt(out);
		} catch (NumberFormatException e) {
		}
		setGeneratedPages(getGeneratedPages() + i);
		zout.closeEntry();
	}

	private void addOutputVat(ZipOutputStream zout) throws ReportException, IOException {
		BookType type = BookType.IVA;
		Book book = addBook(type,"IVA Repercutido");
		ZipEntry ze = new ZipEntry(book.getName());
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

	private void addInputVat(ZipOutputStream zout) throws ReportException, IOException {
		BookType type = BookType.IVA;
		Book book = addBook(type,"IVA Soportado");
		ZipEntry ze = new ZipEntry(book.getName());
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

	private void addInvestmentVat(ZipOutputStream zout) throws ReportException, IOException {
		BookType type = BookType.IVA;
		Book book = addBook(type,"IVA Inversion");
		ZipEntry ze = new ZipEntry(book.getName());
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

	private void addProfitAndLost(ZipOutputStream zout) throws ReportException, IOException{
		BookType type = BookType.PER_GAN;
		Book book = addBook(type,"Balance de Pérdidas y Ganancias");
		ZipEntry ze = new ZipEntry(book.getName());
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

	private void addSituation(ZipOutputStream zout) throws ReportException, IOException{
		BookType type = BookType.BALANCES;
		Book book = addBook(type,"Balance de Situación");
		ZipEntry ze = new ZipEntry(book.getName());
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

	private void addPatrimony(ZipOutputStream zout) throws ReportException, IOException{
		BookType type = BookType.BALANCES;
		Book book = addBook(type,"Balance de Cambios Patrimonio");
		ZipEntry ze = new ZipEntry(book.getName());
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

	private void addReportTemplate(ZipOutputStream zout) throws IOException, ManagerBeanException {
		BookType type = BookType.MEMORIA;
		Book book = addBook(type,"Memoria");
		ZipEntry ze = new ZipEntry(book.getName());
	    zout.putNextEntry(ze);
        ReportsLauncher c = (ReportsLauncher) AonUtil.getRegisteredBean("reportsLauncher");
        c.onReset(null);
        c.setUseAttachedTemplate((getReportTemplate() != null));
        c.setUseDefaultTemplate((getReportTemplate() == null));
        c.setReportTemplate(getReportTemplate());
        if (c.isUseAttachedTemplate()) {
        	c.onChangeReportTemplate(null);	
        }
        c.getParams().setPeriod(getPeriod());
        c.pdf(zout);
		zout.closeEntry();
	}

	private void addReport(ZipOutputStream zout) throws IOException, ManagerBeanException {
		if (getReport() != null) {
			IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_ID),getReport());
	        Iterator<?> iter = bean.getList(criteria).iterator();
			if (iter.hasNext()) {
				RegistryAttachment ra = (RegistryAttachment) iter.next();
				BookType type = BookType.OTROS;
				Book book = addBook(type,StringUtils.substring(ra.getDescription(), 0, 32),(ra.getMimeType()!=null?("." + ra.getMimeType().getExtension()):"") );
				ZipEntry ze = new ZipEntry(book.getName());
			    zout.putNextEntry(ze);
			    IOUtils.copy(new ByteArrayInputStream(ra.getData()), zout);
				zout.closeEntry();
			}
		}
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

	private void addRequiredFiles(ZipOutputStream zout) throws IOException {
		AnnualReportParameters params = new AnnualReportParameters();
		params.setParams(new SummaryProviderParameters());
		params.getParams().setPeriod(getPeriod());
		AnnualReportContext ctx = new AnnualReportContext(params);
		StringBuffer buf = new StringBuffer();
		buf.append("100");
		buf.append("");
		buf.append("\r\n");
		buf.append("101");
		buf.append(dateFormatter.format(new Date()));
		buf.append("\r\n");
		buf.append("102");
		buf.append(StringUtils.substring(ctx.empresa(),0, 32));
		buf.append("\r\n");
		buf.append("103");
		buf.append("");
		buf.append("\r\n");
		buf.append("104");
		buf.append("");
		buf.append("\r\n");
		buf.append("105");
		buf.append(StringUtils.substring(ctx.nifEmpresa(),0, 9));
		buf.append("\r\n");
		buf.append("106");
		buf.append(StringUtils.substring(ctx.domicilioEmpresa(),0, 32));
		buf.append("\r\n");
		buf.append("107");
		buf.append("");
		buf.append("\r\n");
		buf.append("108");
		buf.append("");
		buf.append("\r\n");
		buf.append("109");
		buf.append("0");
		buf.append("\r\n");
		buf.append("110");
		buf.append("0");
		buf.append("\r\n");
		buf.append("111");
		buf.append("");
		buf.append("\r\n");
		buf.append("201");
		buf.append(StringUtils.substring(ctx.tomoRegistroMercantil(),0, 6));
		buf.append("\r\n");
		buf.append("204");
		buf.append(StringUtils.substring(ctx.folioRegistroMercantil(),0, 6));
		buf.append("\r\n");
		buf.append("205");
		buf.append(StringUtils.substring(ctx.registroMercantil(),0, 6));
		buf.append("\r\n");
		buf.append("206");
		buf.append(StringUtils.substring(ctx.hojaRegistroMercantil(),0, 6));
		buf.append("\r\n");
		buf.append("207");
		buf.append("");
		buf.append("\r\n");
		buf.append("501");
		buf.append(bookList.size());
		buf.append("\r\n");

		StringBuffer buf2 = new StringBuffer();

		int i = 1;
		for (Book book : bookList) {
			buf.append(formatter.format(i));
			buf.append("01");
			buf.append(book.getDescription());
			buf.append("\r\n");
			buf.append(formatter.format(i));
			buf.append("02");
			buf.append(formatter.format(book.getNumber()));
			buf.append("\r\n");
			buf.append(formatter.format(i));
			buf.append("03");
			buf.append(dateFormatter.format(getPeriod().getInitiationDate()));
			buf.append("\r\n");
			buf.append(formatter.format(i));
			buf.append("04");
			buf.append(dateFormatter.format(getPeriod().getDeadline()));
			buf.append("\r\n");
			
			buf2.append(book.getName());
			buf2.append("\r\n");
			i++;
		}

		ZipEntry ze = new ZipEntry("DATOS.TXT");
	    zout.putNextEntry(ze);
	    StringReader reader = new StringReader(buf.toString());
	    IOUtils.copy(reader, zout);
		zout.closeEntry();

		ze = new ZipEntry("NOMBRES.TXT");
	    zout.putNextEntry(ze);
	    reader = new StringReader(buf2.toString());
	    IOUtils.copy(reader, zout);
		zout.closeEntry();

		ze = new ZipEntry("DESC.TXT");
	    zout.putNextEntry(ze);
	    reader = new StringReader( ctx.empresa());
	    IOUtils.copy(reader, zout);
		zout.closeEntry();
	}

	private enum BookType {
        DIARIO,
        INV_CUEN,
        BAL_SUMS,
        INVENTAR,
        BALANCES,
        MEMORIA,
        MAYOR,
        PER_GAN,
        IVA,
        FAC_EMIT,
        FAC_RECI,
        DET_DIA,
        ACCIONES,
        SOCIOS,
        OTROS;
	}
	
	private class Book {
		private BookType bookType;
		private String extension;
		private String description; 
		private int number;

		public Book(String extension) {
			setExtension( extension );
		}
		
		public BookType getBookType() {
			return bookType;
		}
		public void setBookType(BookType bookType) {
			this.bookType = bookType;
		}
		public String getExtension() {
			return extension;
		}
		public void setExtension(String extension) {
			this.extension = extension;
		}

		public String getName() {
			return bookType + "_" + formatter.format(getNumber())+ getExtension();
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public int getNumber() {
			return number;
		}
		public void setNumber(int number) {
			this.number = number;
		}
	}
}

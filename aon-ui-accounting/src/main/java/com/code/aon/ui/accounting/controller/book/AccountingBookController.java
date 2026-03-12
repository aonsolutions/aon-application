package com.code.aon.ui.accounting.controller.book;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.annualReport.AnnualReportContext;
import com.code.aon.accounting.annualReport.AnnualReportParameters;
import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.enumeration.TaxRegime;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.AccountRegeneratorController;
import com.code.aon.ui.accounting.controller.AccountingCollectionsController;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.drive.Drive;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.google.apis.drive.AonDrive;

public class AccountingBookController implements ICollectionProvider, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String DEFAULT_SUBTITLE = "PLAN GENERAL DE CONTABILIDAD DE PEQUEÑAS Y MEDIANAS EMPRESAS";
	private static final String DEFAULT_TITLE = "CUENTAS ANUALES";
	private static final String DATOS_TXT = "DATOS.TXT";
	private static final String NOMBRES_TXT = "NOMBRES.TXT";
	private static final String DESC_TXT = "DESC.TXT";
	
	private NumberFormat formatter = new DecimalFormat("000");
	private DateFormat dateFormatter = new SimpleDateFormat("ddMMyyyy");
	private boolean valid;
	
	private List<SelectItem> bookTypes;
	
	private DataModel model;
	
	private Period period;
	private int generatedPages = 0;
	
	private boolean coverEnabled;
	private String coverTitle;
	private String coverSubTitle;
	
	private Date constitutionDate;

	public DataModel getModel() {
		if (model == null) {
			initializeModel();
		}
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}
	
	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}

	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public int getGeneratedPages() {
		return generatedPages;
	}
	public void setGeneratedPages(int generatedPages) {
		this.generatedPages = generatedPages;
	}

	public boolean isCoverEnabled() {
		return coverEnabled;
	}
	public void setCoverEnabled(boolean coverEnabled) {
		this.coverEnabled = coverEnabled;
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
	private CompanyController getCompanyController() {
		CompanyController cc = (CompanyController) AonUtil.getRegisteredBean(IAccountingConstants.COMPANY_CONTROLLER);
		return cc;
	}
	
	public Date getConstitutionDate() {
		if (constitutionDate == null) {
			try {
				CompanyController cc = getCompanyController();
				RecordData data = cc.getCompanyRecordData();
				if (data != null) {
					setConstitutionDate( cc.getCompanyRecordData().getCreationDate());	
				}
			} catch (ManagerBeanException e) {
				// Nada. Se queda a null.
			}
		}
		return constitutionDate;
	}
	public void setConstitutionDate(Date constitutionDate) {
		this.constitutionDate = constitutionDate;
	}

	private void initializeModel() {
		FiscalParametersController fpc = (FiscalParametersController) AonUtil.getRegisteredBean(IAccountingConstants.FISCAL_PARAMETERS_CONTROLLER);
		TaxRegime taxRegime = fpc.getTaxRegime();
		List<AccountingBook> books = new LinkedList<AccountingBook>();  
		if (taxRegime == TaxRegime.EDS || taxRegime == TaxRegime.MODULES) {
			books.add( new AccountingBook( 1, 1, 1,MimeType.MIME_PDF,BookType.EXPENSES 	,true ));
			books.add( new AccountingBook( 1, 2, 1,MimeType.MIME_PDF,BookType.INCOMES	,true ));
			books.add( new AccountingBook( 1, 3, 1,MimeType.MIME_PDF,BookType.IVAI		,true ));
			books.add( new AccountingBook( 2, 4, 1,MimeType.MIME_PDF,BookType.PER_GAN	,true ));
		} else {
			// FALTA - AQUI DESMARCAR TODOS POR DEFECTO O AL MENOS LOS QUE INDICA LA NORMATIVA PARA QUE SE HAGAN SEPARADOS
//			books.add( new AccountingBook( 1, 0, 1,MimeType.MIME_PDF,BookType.DIARIO	,false)); // Diario
//			books.add( new AccountingBook( 2, 1, 1,MimeType.MIME_PDF,BookType.MAYOR		,true )); // Mayor
//			books.add( new AccountingBook( 2, 2, 1,MimeType.MIME_PDF,BookType.BAL_SUMS1	,true )); // Balances de comprobación (sumas y saldos)
//			books.add( new AccountingBook( 2, 3, 1,MimeType.MIME_PDF,BookType.BAL_SUMS2	,true )); // Balances de comprobación (sumas y saldos)
//			books.add( new AccountingBook( 2, 4, 1,MimeType.MIME_PDF,BookType.BAL_SUMS3	,true )); // Balances de comprobación (sumas y saldos)
//			books.add( new AccountingBook( 2, 5, 1,MimeType.MIME_PDF,BookType.BAL_SUMS4	,true )); // Balances de comprobación (sumas y saldos)
//			books.add( new AccountingBook( 2, 6, 1,MimeType.MIME_PDF,BookType.IVAR		,true )); // IVA o Facturas emitidas
//			books.add( new AccountingBook( 2, 7, 1,MimeType.MIME_PDF,BookType.IVAS		,true )); // IVA o Facturas recibidas
//			books.add( new AccountingBook( 2, 8, 1,MimeType.MIME_PDF,BookType.IVAI		,true )); // IVA
//			books.add( new AccountingBook( 2, 9, 1,MimeType.MIME_PDF,BookType.PER_GAN	,true )); // Libro de Pérdidas y Ganancias
//			books.add( new AccountingBook( 2,10, 1,MimeType.MIME_PDF,BookType.BALANCES	,true )); // Balances
//			books.add( new AccountingBook( 2,11, 1,MimeType.MIME_PDF,BookType.BALANCES	,true )); // Balances
			
			books.add( new AccountingBook( 1, 0, 1,MimeType.MIME_PDF,BookType.DIARIO	,false)); // Diario
			books.add( new AccountingBook( 2, 1, 1,MimeType.MIME_PDF,BookType.MAYOR		,false)); // Mayor
			books.add( new AccountingBook( 3, 2, 1,MimeType.MIME_PDF,BookType.BAL_SUMS1	,true)); // Balances de comprobación (sumas y saldos)
			books.add( new AccountingBook( 3, 3, 2,MimeType.MIME_PDF,BookType.BAL_SUMS2	,true)); // Balances de comprobación (sumas y saldos)
			books.add( new AccountingBook( 3, 4, 3,MimeType.MIME_PDF,BookType.BAL_SUMS3	,true)); // Balances de comprobación (sumas y saldos)
			books.add( new AccountingBook( 3, 5, 4,MimeType.MIME_PDF,BookType.BAL_SUMS4	,true)); // Balances de comprobación (sumas y saldos)
			books.add( new AccountingBook( 4, 6, 1,MimeType.MIME_PDF,BookType.IVAR		,true)); // IVA o Facturas emitidas
			books.add( new AccountingBook( 4, 7, 1,MimeType.MIME_PDF,BookType.IVAS		,true)); // IVA o Facturas recibidas
			books.add( new AccountingBook( 4, 8, 1,MimeType.MIME_PDF,BookType.IVAI		,true)); // IVA
			books.add( new AccountingBook( 5, 9, 1,MimeType.MIME_PDF,BookType.PER_GAN	,false)); // Libro de Pérdidas y Ganancias
			books.add( new AccountingBook( 6,10, 1,MimeType.MIME_PDF,BookType.BALANCES	,true)); // Balances
			books.add( new AccountingBook( 6,11, 1,MimeType.MIME_PDF,BookType.BALANCES	,true)); // Balances
			
		}
		setModel( new SerializableListDataModel(books));
	}
	
	public void onReset(ActionEvent event) {
		setValid(false);
		setModel(null);
		setPeriod(null);
		try {
			setPeriod(AccountingPeriodUtil.getDefaultPeriod());
		} catch (ManagerBeanException e) {
			// Nada. Se queda a NULL.
		} 
		setGeneratedPages(0);
		setCoverTitle(DEFAULT_TITLE);
		setCoverSubTitle(DEFAULT_SUBTITLE);
	}

	public List<SelectItem> getBookTypes() {
		if (bookTypes == null) {
			bookTypes = new LinkedList<SelectItem>();
			for (BookType type : BookType.values()) {
				if (type.getAonReportType() != null && type != BookType.PORTADA){
					bookTypes.add(new SelectItem(type, type.getDescription()));	
				}
			}
		}
		return bookTypes;
	}
	
	public boolean isJournalCorrect() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(AccountEntry.class);
			Criteria c = new Criteria();
			if (getPeriod() != null) {
				c.addEqualExpression( bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD_ID), getPeriod().getId());	
			}
			c.addNullExpression( bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_JOURNAL));
			int count = bean.getCount(c);
			return (count==0);
		} catch (ManagerBeanException e) {
			return false;
		}
	}
	
	public List<SelectItem> getBalances() {
		try {
			AccountingCollectionsController c = (AccountingCollectionsController) 
				AonUtil.getRegisteredBean( IAccountingConstants.ACCOUNTING_COLLECTIONS_CONTROLLER_NAME);
			List<SelectItem> list = new LinkedList<SelectItem>();
			list.addAll( c.getBalances(BalanceType.CLOSING) );
			list.addAll( c.getBalances(BalanceType.PATRIMONY) );
			list.addAll( c.getBalances(BalanceType.CUSTOM) );
			return list;
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Imposible obtener los balance de pérdidas y ganacias");
			return new LinkedList<SelectItem>();
		}
	}
	public List<SelectItem> getProfitAndLossBalances() {
		try {
			AccountingCollectionsController c = (AccountingCollectionsController) 
				AonUtil.getRegisteredBean( IAccountingConstants.ACCOUNTING_COLLECTIONS_CONTROLLER_NAME);
			return c.getBalances(BalanceType.OPERATING);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Imposible obtener los balance de pérdidas y ganacias");
			return new LinkedList<SelectItem>();
		}
	}
	
	public boolean isJournalEnabled() {
		List<AccountingBook> list = getBookList();
		for (AccountingBook book : list) {
			if (book.getBookType() == BookType.DIARIO) {
				return true;
			}
		}
		return false;
	}

	public void onRegenerate(ActionEvent event) {
		AccountRegeneratorController arc = (AccountRegeneratorController) 
			AonUtil.getRegisteredBean(IAccountingConstants.ACCOUNTING_REGENERATOR_CONTROLLER);
		arc.onEditSearch(event);
		arc.setJournal(true);
		arc.setPeriod(getPeriod());
		arc.setSecurityLevel(null);
		arc.regenerateAccount(event);
	}

	public void onNewBook(ActionEvent event) {
		List<AccountingBook> list = getBookList();
		int order = 0;
		for (AccountingBook book : list) {
			order = book.getOrder();	
		}
		AccountingBook book = new AccountingBook();
		book.setOrder(++order);
		book.setMergeOrder(0);
		book.setMergeable(false);
		book.setNumber(1);
		list.add(book);
	}
	public void onRemoveBook(ActionEvent event) {
		AccountingBook book = getSelectedBook();
		List<AccountingBook> list = getBookList();
		list.remove(book);
		setValid(false);
	}
	
	public void onValidate(ActionEvent event) {
		check();
		setValid(true);
	}
	
	public void onGenerate(ActionEvent event) {
		setShowBookWindow(true);
		setBookFile(false);
	}
	
	public void onCreate(ActionEvent event) {
		AON.deleteAttach(AonUtil.getDomainName(),getCompanyController().obtainCompany().getDomain(), "", 
				f -> f.getDomainProperty().eq(getCompanyController().obtainCompany().getDomain())
				.and(f.getDescriptionProperty().eq("ACCOUNTING_BOOK_" + getPeriod().getName())), AttachType.REGISTRY);
		
		try {
			List<IAccountingBookRunner> runners = getRunners();
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
			ZipOutputStream zout = new ZipOutputStream(baos);
		    zout.setLevel(9);
		    for (IAccountingBookRunner runner : runners) {
		    	AccountingBook book = runner.getAccountingContext().getAccountingBook();
				ZipEntry ze = new ZipEntry(book.getName());
			    zout.putNextEntry(ze);
				runner.run( zout );
	    		zout.closeEntry();
		    }
			addRequiredFiles(zout,runners);
			zout.flush();
			zout.finish();
			
			FileOutput bookOutput = new FileOutput();
			bookOutput.setContent(baos.toByteArray());
			saveRegistryAttach(bookOutput, "ACCOUNTING_BOOK_" + getPeriod().getName(), com.esferalia.aon.occam.api.model.type.MimeType.ZIP);
		} catch (IOException e) {
			AonUtil.addErrorMessage("El fichero no es correcto. " + e.getMessage() );
			throw new AbortProcessingException( e );
		} catch (AccountingBookException e) {
			AonUtil.addErrorMessage("Error al ejecutar el listado. " + e.getMessage() );
			throw new AbortProcessingException( e );
		} 
	}

	private List<IAccountingBookRunner> getRunners() throws AccountingBookException {
		List<AccountingBook> bookList = getBookList();
		Collections.sort( bookList );
		List<IAccountingBookRunner> runners = new LinkedList<IAccountingBookRunner>();
		IAccountingBookRunner coverAccountingBookRunner = getCoverAccountingBookRunner();
		PDFMergerBookRunner mergeRunner = getMergerBookRunner(BookType.OTROS, "Cuentas Anuales");
		if (isCoverEnabled()) {
			mergeRunner.addRunner(coverAccountingBookRunner);
		}
		
		// FALTA - SUMAS Y SALDOS, LIBROS DE IVA Y BALANCES, VAN EN UN SOLO PDF SEPARADOS DEL RESTO Y DE OTROS
		PDFMergerBookRunner mergeRunnerSum = getMergerBookRunner(BookType.BAL_SUMS1, "Balances de sumas y saldos");
		boolean addedSum = false;
		
		PDFMergerBookRunner mergeRunnerIva = getMergerBookRunner(BookType.IVAI, "Libros de IVA");
		boolean addedIva = false;
		
		PDFMergerBookRunner mergeRunnerBal = getMergerBookRunner(BookType.BALANCES, "Balances");
		boolean addedBal = false;
		// -----
		
		boolean added = false;
		for (AccountingBook book : bookList ) {
			
			AccountingBookRunnerManager manager = new AccountingBookRunnerManager();
			AccountingBookContext context = new AccountingBookContext(book,this);
			IAccountingBookRunner runner = manager.getRunner(context);
			
			if (book.isMergeable()) {
				if (book.getBookType() == BookType.BAL_SUMS1 || book.getBookType() == BookType.BAL_SUMS2 || book.getBookType() == BookType.BAL_SUMS3 || book.getBookType() == BookType.BAL_SUMS4) {
					if (!addedSum) {
						runners.add(mergeRunnerSum);
						addedSum = true;
					}
					mergeRunnerSum.addRunner(runner);
				} else if (book.getBookType() == BookType.IVAR || book.getBookType() == BookType.IVAS || book.getBookType() == BookType.IVAI) {
					if (!addedIva) {
						runners.add(mergeRunnerIva);
						addedIva = true;
					}
					mergeRunnerIva.addRunner(runner);
				} else if (book.getBookType() == BookType.BALANCES) {
					if (!addedBal) {
						runners.add(mergeRunnerBal);
						addedBal = true;
					}
					mergeRunnerBal.addRunner(runner);
				} else {
					if (!added) {
						runners.add(mergeRunner);
						added = true;
					}
					mergeRunner.addRunner(runner);
				}
			} else {
				if (isCoverEnabled()) {
					PDFMergerBookRunner bookMergeRunner = getMergerBookRunner(book.getBookType(), book.getDescription());
					bookMergeRunner.addRunner(coverAccountingBookRunner);
					bookMergeRunner.addRunner(runner);
					runners.add(bookMergeRunner);
				} else {
					runners.add(runner);	
				}
			}
		}
		return runners;
	}

	private IAccountingBookRunner getCoverAccountingBookRunner() throws AccountingBookException {
		AccountingBookRunnerManager manager = new AccountingBookRunnerManager();
		AccountingBookContext context = new AccountingBookContext(getCoverAccountingBook(),this);
		return manager.getRunner(context);
	}

	private AccountingBook getCoverAccountingBook() {
		AccountingBook book = new AccountingBook();
		book.setBookType(BookType.PORTADA);
		book.setMimeType(MimeType.MIME_PDF);
		book.setDescription("Portada");
		book.setNumber(1);
		return book;
	}

	private PDFMergerBookRunner getMergerBookRunner(BookType type, String description) {
		PDFMergerBookRunner mergeRunner = new PDFMergerBookRunner();
		AccountingBook mergeBook = new AccountingBook();
		mergeBook.setBookType(type);
		mergeBook.setNumber(1);
		mergeBook.setMimeType(MimeType.MIME_PDF);
		mergeBook.setDescription(description);
		mergeRunner.setAccountingContext(new AccountingBookContext(mergeBook,this));
		return mergeRunner;
	}
	
	public ReportManager getReportManager() {
		ReportManager manager = (ReportManager) AonUtil.getRegisteredBean(IAccountingConstants.REPORT_CONTROLLER);
		return manager;
	}
	@SuppressWarnings("unchecked")
	private List<AccountingBook> getBookList() {
		return (List<AccountingBook>) getModel().getWrappedData();
	}
	
	private String getCompanyAdaptedName() {
		CompanyController cc = getCompanyController();
		String name = cc.getCompanyLabel();
		name = StringUtils.upperCase(name);
		name = StringUtils.deleteWhitespace(name);
		name = StringUtils.substring(name, 0, 8);
		String chars = "!\"·$%&/()=?¿'¡+`^*Ç{}[]-_";
		name = StringUtils.replaceChars(name, chars, "");
		if (StringUtils.isEmpty(name)) {
			name = getPeriod().getName();
		}
		return name;
	}

	private void check() {
		List<AccountingBook> list = getBookList();
		boolean ok = true;
		for (AccountingBook book : list) {
			String msg = book.getErrorIfNotValid();
			if (msg != null) {
				AonUtil.addErrorMessage(msg);
				ok = false;	
			}
			for (AccountingBook b : list) {
				if (!b.equals(book)
					&& b.getNumber() == book.getNumber() 
					&& b.getBookType() == book.getBookType()) {
					if (!(b.isMergeable() && book.isMergeable())) {
						AonUtil.addErrorMessage(
								"El listado numero #" + book.getOrder() + " ( "+ book.getBookType().getDescription() +") y "
								+"el listado numero #" + b.getOrder() + " ( "+ b.getBookType().getDescription() +"), tienen el mismo tipo y numero, modifique el número de libro");
						ok = false;	
					}
				}
			}
		}
		if (!ok) {
			throw new AbortProcessingException( "Compruebe los parámetros." );
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
		CompanyController cc = getCompanyController();
		Company company = cc.obtainCompany();
		list.add(company);
		return list;
	}

	private void addRequiredFiles(ZipOutputStream zout, List<IAccountingBookRunner> runners) throws IOException {
		AnnualReportParameters params = new AnnualReportParameters();
		params.setParams(new SummaryProviderParameters(AonUtil.getDomainName()));
		params.getParams().setPeriod(getPeriod());
		AnnualReportContext ctx = new AnnualReportContext(params);
		StringBuffer buf = new StringBuffer();
		
		// FALTA - Nombre del Registro Mercantil
		buf.append(100);
//		buf.append(IAccountingConstants.EMPTY);
		buf.append(StringUtils.upperCase(ctx.nombreRegistroMercantil()));
		buf.append(IAccountingConstants.CR);
		// Fecha
		buf.append(101);
		buf.append(dateFormatter.format(new Date()));
		buf.append(IAccountingConstants.CR);
		// Nombre empresa
		buf.append(102);
		buf.append(StringUtils.substring(ctx.empresa(),0, 32));
		buf.append(IAccountingConstants.CR);
		// Apellido 1
		buf.append(103);
		buf.append(IAccountingConstants.EMPTY);
		buf.append(IAccountingConstants.CR);
		// Apellido 1
		buf.append(104);
		buf.append(IAccountingConstants.EMPTY);
		buf.append(IAccountingConstants.CR);
		// NIF
		buf.append(105);
		buf.append(StringUtils.substring(ctx.nifEmpresa(),0, 9));
		buf.append(IAccountingConstants.CR);
		// Calle
		buf.append(106);
		buf.append(StringUtils.substring(ctx.domicilioEmpresa(),0, 32));
		buf.append(IAccountingConstants.CR);
		// Localidad
//		buf.append(107);
//		buf.append(StringUtils.substring(ctx.localidadEmpresa(),0, 32));
//		buf.append(IAccountingConstants.CR);
		// FALTA - Municipio (SE ESTA GRABANDO EL CODIGO DE MUNICIPIO, SE LO PASO A VER SI LO COGE)
		buf.append(107);
		buf.append(StringUtils.substring(ctx.municipioEmpresa(),0, 32));
		buf.append(IAccountingConstants.CR);
		// C.P.
		buf.append(108);
		buf.append(StringUtils.substring(ctx.codigoPostalEmpresa(),0, 32));
		buf.append(IAccountingConstants.CR);
		// Provincia
		buf.append(109);
		buf.append(StringUtils.substring(ctx.codigoProvinciaEmpresa(),0, 5));
		buf.append(IAccountingConstants.CR);
		// Fax
		buf.append(110);
		buf.append(StringUtils.substring(ctx.faxEmpresa(),0, 10));
		buf.append(IAccountingConstants.CR);
		// Telefono
		buf.append(111);
		buf.append(StringUtils.substring(ctx.telefonoEmpresa(),0, 10));
		buf.append(IAccountingConstants.CR);
		// Tomo
		buf.append(201);
		buf.append(StringUtils.substring(ctx.tomoRegistroMercantil(),0, 6));
		buf.append(IAccountingConstants.CR);
		// Folio
		buf.append(204);
		buf.append(StringUtils.substring(ctx.folioRegistroMercantil(),0, 6));
		buf.append(IAccountingConstants.CR);
		// FALTA - DEJARLO POR DEFECTO PARA QUE LO PONGA POR DEFECTO EL PROGRAMA DEL REGISTRO MERCANTIL
		// Tipo Registro
//		buf.append(205);
//		buf.append(StringUtils.substring(ctx.registroMercantil(),0, 6));
//		buf.append(IAccountingConstants.CR);
		// Hoja Registral
		buf.append(206);
		buf.append(StringUtils.substring(ctx.hojaRegistroMercantil(),0, 6));
		buf.append(IAccountingConstants.CR);
		// Otros
		buf.append(207);
		buf.append(IAccountingConstants.EMPTY);
		buf.append(IAccountingConstants.CR);
		// FALTA - Código del Registro Mercantil
		buf.append(112);
		buf.append(StringUtils.substring(ctx.codigoRegistroMercantil(),0, 5));
		buf.append(IAccountingConstants.CR);
		// Número total de libros presentados
		buf.append(501);
		buf.append(runners.size());
		buf.append(IAccountingConstants.CR);

		StringBuffer buf2 = new StringBuffer();

		int i = 1;
		for (IAccountingBookRunner runner : runners) {
			AccountingBook book = runner.getAccountingContext().getAccountingBook();
			buf.append(formatter.format(i));
			buf.append("01");
			buf.append(book.getDescription());
			buf.append(IAccountingConstants.CR);
			buf.append(formatter.format(i));
			buf.append("02");
			buf.append(formatter.format(book.getNumber()));
			buf.append(IAccountingConstants.CR);
			buf.append(formatter.format(i));
			buf.append("03");
			buf.append(dateFormatter.format(getPeriod().getInitiationDate()));
			buf.append(IAccountingConstants.CR);
			buf.append(formatter.format(i));
			buf.append("04");
			buf.append(dateFormatter.format(getPeriod().getDeadline()));
			buf.append(IAccountingConstants.CR);
			
			buf2.append(book.getName());
			buf2.append(IAccountingConstants.CR);
			i++;
		}

		ZipEntry ze = new ZipEntry(DATOS_TXT);
	    zout.putNextEntry(ze);
	    StringReader reader = new StringReader(buf.toString());
	    IOUtils.copy(reader, zout);
		zout.closeEntry();

		ze = new ZipEntry(NOMBRES_TXT);
	    zout.putNextEntry(ze);
	    reader = new StringReader(buf2.toString());
	    IOUtils.copy(reader, zout);
		zout.closeEntry();
		
		// FALTA - Nombre empresa
		StringBuffer buf3 = new StringBuffer();
		buf3.append(ctx.empresa());
		buf3.append(IAccountingConstants.CR);
		
		// FALTA - IRUS		
		buf3.append("IRUS=" + StringUtils.trimToEmpty(ctx.irusRegistroMercantil()));
		buf3.append(IAccountingConstants.CR);

		ze = new ZipEntry(DESC_TXT);
	    zout.putNextEntry(ze);
//	    reader = new StringReader(ctx.empresa());
	    reader = new StringReader(buf3.toString());
	    IOUtils.copy(reader, zout);
		zout.closeEntry();
	}

	public boolean isNumberVisible() {
		AccountingBook book = getSelectedBook();
		if (book.isMergeable()) {
			for (AccountingBook b : getBookList()) {
				if (!book.equals(b) && b.isMergeable() && b.getMergeOrder() < book.getMergeOrder()) {
					return false;
				}
			}
		}
		return true;
	}
	
	private AccountingBook getSelectedBook() {
		return (AccountingBook) getModel().getRowData();
	}

	public void onMoveUp(ActionEvent event) throws ManagerBeanException {
    	move(getSelectedBook(), -1 );
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException {
    	move(getSelectedBook(), 1 );
    }	

    private void move(AccountingBook book, int increment ) {
		int oldPosition = book.getOrder();
		int newPosition = oldPosition + increment;
		for (AccountingBook b : getBookList()) {
			if (book.isMergeable() && b.isMergeable() ) {
				b.setOrder(newPosition);		
			} else {
				if ( !b.equals(book) && newPosition == b.getOrder()) {
					b.setOrder(b.getOrder() + (increment*(-1)));
				}
			}
		}
		book.setOrder(newPosition);
		Collections.sort(getBookList());
		setModel(new SerializableListDataModel( getBookList()));
    }
    
    public boolean isMaxOrder() {
		AccountingBook book = getSelectedBook();
		for (AccountingBook b : getBookList()) {
			if (book.getOrder() < b.getOrder() ) {
				return false;
			}
		}
		return true;
    	
    }

	public void onPDFMoveUp(ActionEvent event) throws ManagerBeanException {
    	movePDF(getSelectedBook(), -1 );
    }

    public void onPDFMoveDown(ActionEvent event) throws ManagerBeanException {
    	movePDF(getSelectedBook(), 1 );
    }	

    private void movePDF(AccountingBook book, int increment ) {
		int oldPosition = book.getMergeOrder();
		int newPosition = oldPosition + increment;
		for (AccountingBook b : getBookList()) {
			if ( !b.equals(book) && newPosition == b.getMergeOrder()) {
				b.setMergeOrder(b.getMergeOrder() + (increment*(-1)));
			}
		}
		book.setMergeOrder(newPosition);
		Collections.sort(getBookList());
		setModel(new SerializableListDataModel( getBookList()));
    }

    public boolean isMaxMergeOrder() {
		AccountingBook book = getSelectedBook();
		for (AccountingBook b : getBookList()) {
			if (b.isMergeable() && book.getMergeOrder() < b.getMergeOrder() ) {
				return false;
			}
		}
		return true;
    	
    }
    
    public void onMergebleChanged(ActionEvent event) {
    	AccountingBook book = getSelectedBook();
    	int order = 0;
    	int mergeOrder = 0;
    	if (book.isMergeable()) {
    		for (AccountingBook b : getBookList()) {
    			if (!b.equals(book) && b.isMergeable()) {
    				order = b.getOrder();
    				mergeOrder = b.getMergeOrder();
    			}
    		}
    		++mergeOrder;
    	} else {
    		for (AccountingBook b : getBookList()) {
    			if (!b.equals(book)) {
    				order = b.getOrder();
    			}
    		}
    		++order;
    	}
    	book.setOrder(order);
    	book.setMergeOrder(mergeOrder);
		Collections.sort(getBookList());
		setModel(new SerializableListDataModel( getBookList()));
    }
    
	private Boolean showBookWindow;
	private Boolean bookFile;
	private Boolean init;
	
	public Boolean getBookFile() {
		if(bookFile == null) {
			try {
				loadBookFile();
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
		}
		return bookFile;
	}
	
	public void setBookFile(Boolean bookFile) {
		this.bookFile = bookFile;
	}
	
	public Boolean getShowBookWindow() {
		if(showBookWindow == null) {
			setShowBookWindow(false);
		}
		return showBookWindow;
	}
	
	public void setShowBookWindow(Boolean showBookWindow) {
		this.showBookWindow = showBookWindow;
	}
	
	public Boolean getInit() {
		if(init == null) {
			setInit(false);
		}
		return init;
	}
	public void setInit(Boolean init) {
		this.init = init;
	}
	public void onShowBookWindow(ActionEvent event) throws AccountingBookException {
		setBookFile(false);
		setShowBookWindow(true);
	}
	
	public void onChangePeriod(ValueChangeEvent event) throws ManagerBeanException {
		setPeriod((Period) event.getNewValue());
		loadBookFile();
	}
	
	public void onReloadDisk(ActionEvent event) throws ManagerBeanException {
		if(!getInit()) {
			setInit(true); 
			onCreate(event);
		}
		loadBookFile();
	}
	
	public void loadBookFile() throws ManagerBeanException {
		if(getPeriod() == null) {
			AccountingCollectionsController acc = new AccountingCollectionsController();
			setPeriod(acc.getClassAllAccountPeriods().get(0));
		}

		Attach attach = AON.getAttach(AonUtil.getDomainName(),getCompanyController().obtainCompany().getDomain(), "", 
				f -> f.getDomainProperty().eq(getCompanyController().obtainCompany().getDomain())
				.and(f.getDescriptionProperty().eq("ACCOUNTING_BOOK_" + getPeriod().getName())), AttachType.REGISTRY, true);
		
		setBookFile(attach != null && (attach.getData() != null || attach.getDriveId() != null));		
	}

	public void downloadDisk(ActionEvent event) throws ManagerBeanException {
		getCompanyController().obtainCompany();
        try {
    		FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();

            Attach attach = AON.getAttach(AonUtil.getDomainName(), getCompany().getDomain(), "", 
    				f -> f.getDomainProperty().eq(getCompany().getDomain())
    				.and(f.getDescriptionProperty().eq("ACCOUNTING_BOOK_" + getPeriod().getName())), AttachType.REGISTRY, true);
            
            if(attach.getData() == null && attach.getDriveId() != null) {
            	DomainGserviceaccount g = AON.getDomainGserviceaccount(AonUtil.getDomainName(), getCompany().getDomain(), "");
        		Drive drive = AonDrive.getInstace().serviceInitialize(g);
            	attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
            }
            
	        response.setContentType(attach.getMimeType().getName());
	        response.setHeader("Content-disposition", "attachment; filename=\"" + attach.getDescription() + "." + attach.getMimeType().getExtension() +"\"");

	        ServletOutputStream output = response.getOutputStream();
	        InputStream input = new java.io.ByteArrayInputStream(attach.getData());
	      
	        int size = IOUtils.copy(input, output);
	        if (size > 0) {
		        response.setHeader("Content-Length", String.valueOf(size));
	        }
	        output.close();
	        input.close();
	        
	        response.flushBuffer();
	        faces.responseComplete();
        } catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	private Company getCompany() {
		return getCompanyController().obtainCompany();
	}
	
	private void saveRegistryAttach(FileOutput bookOutput, String name, com.esferalia.aon.occam.api.model.type.MimeType mimeType) {
		Domain domain = AON.getDomain(AonUtil.getDomainName(), getCompany().getDomain(), "");
		User user = new User().setLogin("");
		Attach attach = new Attach()
				.setAttachType(AttachType.REGISTRY)
				.setAttachModule(getCompany().getId())
				.setDescription(name)
				.setConfidential(false)
				.setDate(new Date())
				.setDomain(domain)
				.setData(bookOutput.getContent())
				.setType(RegistryAttachmentType.SYSTEM_MESSAGE.value())
				.setMimeType(mimeType)
				.setCreationDate(new Date())
				.setCreationUser("")
				.setModificationDate(new Date())
				.setModificationUser("");
		AON.insertAttach(domain.getName(), domain.getId(), user.getLogin(), attach);
	}
	
}

package com.code.aon.ui.fiscal.controller.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Finance;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.fiscal.FiscalActivity;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalModelStatus;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.model.FiscalModelManagerFactory;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import com.code.aon.fiscal.model.IFiscalModelManager;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public abstract class FiscalModelController extends BasicController {

	private IFiscalDeclaration declaration;
	private IFiscalModelManager manager;
	private FileOutput fileOutput;

	private boolean finalizePanelVisible;
	private RegistryBank registryBank;

	public IFiscalModelManager getFiscalModelManager() throws AonException {
		if (manager == null) {
			FiscalModelManagerFactory factory = new FiscalModelManagerFactory();
			manager = factory.getManager(getModelType());
		}
		return manager;
	}

	private Registry getAdmonCreditor() {
		FiscalParametersController fpc = (FiscalParametersController) AonUtil
				.getRegisteredBean(FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		Creditor creditor = fpc.getAdmonCreditor();
		return (creditor == null ? null : creditor.getRegistry());
	}

	public FileOutput getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}

	public boolean isFinalizePanelVisible() {
		return finalizePanelVisible;
	}

	public void setFinalizePanelVisible(boolean finalizePanelVisible) {
		this.finalizePanelVisible = finalizePanelVisible;
	}

	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public IFiscalDeclaration getDeclaration() {
		return declaration;
	}

	public void setDeclaration(IFiscalDeclaration fiscalModel) {
		this.declaration = fiscalModel;
	}

	public void initialize() throws AonException {
		FiscalModel to = (FiscalModel) getTo();
		FiscalParametersController fiscalParams = (FiscalParametersController) AonUtil
				.getRegisteredBean(FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		String defYear = fiscalParams.getDefaultYear();
		to.setYear(defYear == null ? null : Integer.parseInt(defYear));
		Administration admon = fiscalParams.getDefaultAdministration();
		to.setAdministration(admon == null ? null : admon);
		to.setModel(getModelType());
		setDeclaration((getFiscalModelManager().initializeFiscalModel(to)));
	}

	public void initializeDetails() throws AonException {
		setDeclaration((getFiscalModelManager()
				.initializeFiscalModelDetails(getDeclaration())));
		insertOrUpdateDetails();
	}

	public void load() throws AonException {
		FiscalModel to = (FiscalModel) getTo();
		setDeclaration((getFiscalModelManager().loadFiscalModel(to)));
	}

	public void unload() throws AonException {
		setDeclaration(null);
	}

	public void onShowFinalizePanel(ActionEvent event) {
		FiscalModel to = (FiscalModel) getTo();
		if (mustCreateFinance(to)) {
			setFinalizePanelVisible(true);
			setRegistryBank(null);
			to.getFinance().setRegistry(getAdmonCreditor());
		} else {
			finish(to, event);
		}
	}

	public void onHideFinalizePanel(ActionEvent event) {
		setFinalizePanelVisible(false);
		setRegistryBank(null);
	}

	public void onFinish(ActionEvent event) {
		try {
			FiscalModel to = (FiscalModel) getTo();
			if (mustCreateFinance(to)) {
				createFinance(to);
			}
			finish(to, event);
			onHideFinalizePanel(event);
		} catch (Exception e) {
			String msg = "No se pueden finalizar la declaración."
					+ e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private void finish(FiscalModel to, ActionEvent event) {
		to.setStatus(FiscalModelStatus.FINISHED);
		accept(event);
	}

	public List<SelectItem> getActiveBanks() throws ManagerBeanException {
		CompanyCollectionsController c = (CompanyCollectionsController) AonUtil
				.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		return c.getActiveCompanyBanks();
	}

	private boolean mustCreateFinance(FiscalModel to) {
		if (getDeclaration().isWithoutActivityDeclarationAvailable()
				&& to.isWithoutActivity()) {
			return false;
		}
		if (getDeclaration().isToDeductDeclarationAvailable()
				&& getDeclaration().isToDeduct()) {
			return false;
		}
		if (getDeclaration().isDeclarationNegativeAvailable()
				&& getDeclaration().isNegative()) {
			return false;
		}
		if (getDeclaration().getResult() == 0) {
			return false;
		}
		return true;
	}

	public boolean isBankAccountVisible() {
		FiscalModel to = (FiscalModel) getTo();
		Finance finance = to.getFinance();
		if (finance != null && finance.getPayMethod() != null
				&& finance.getPayMethod().getType() != PayMethodType.CASH_BASIS) {
			return true;
		}
		return false;
	}

	private void createFinance(FiscalModel to) throws ManagerBeanException {
		Finance finance = to.getFinance();
		Registry registry = finance.getRegistry();
		if (registry == null || registry.getId() == null) {
			throw new ManagerBeanException(
					"No se ha indicado un acreedor válido.");
		}
		finance.setRegistryDocument(registry.getDocument());
		finance.setRegistryDocumentCountry(registry.getDocumentCountry());
		finance.setRegistryDocumentType(registry.getDocumentType());
		finance.setRegistryName(registry.getName());
		finance.setPayment(true);
		finance.setSecurityLevel(to.getSecurityLevel());
		finance.setAmount(declaration.getResult());
		if (finance.getPayMethod() == null) {
			throw new ManagerBeanException(
					"No se ha indicado una forma de pago válida.");
		}
		if (finance.getPayMethod().getType() == PayMethodType.CASH_BASIS) {

		} else if (finance.getPayMethod().getType() == PayMethodType.NEGOTIABLE_DOCUMENT) {
			finance.setBank(getRegistryBank().getBank());
			finance.setBankAccount(getRegistryBank().getBankAccount());
		}
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		bean.restoreNullSubPOJOs(finance);
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setConcept(to.getModel() + " " + to.getYear() + "-"
				+ to.getPeriod());
		finance = (Finance) bean.insert(finance);
		to.setFinance(finance);
	}

	public void onReopen(ActionEvent event) {
		try {
			FiscalModel to = (FiscalModel) getTo();
			Finance finance = to.getFinance();
			if (finance.getId() != null
					&& finance.getFinanceStatus() != FinanceStatus.PENDING) {
				throw new ManagerBeanException(
						"No se pueden reabrir la declaración. El vencimiento asociado no está pendiente");
			}
			to.setStatus(FiscalModelStatus.PENDING);
			to.setFinance(null);
			accept(event);
			if (finance.getId() != null) {
				IManagerBean bean = BeanManager.getManagerBean(Finance.class);
				bean.remove(finance);
			}
		} catch (Exception e) {
			String msg = "No se pueden reabrir la declaración."
					+ e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

	}

	public void onRecalculate(ActionEvent event) {
		try {
			getDeclaration().calculate();
		} catch (AonException e) {
			String msg = "No se pudo recalcular la declaración";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void insertOrUpdateDetails() throws AonException {
		getDeclaration().calculate();
		FiscalModel to = (FiscalModel) getTo();
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		for (FiscalModelDetail detail : getDeclaration().getDetails()) {
			detail.setFiscalModel(to);
			detail = (FiscalModelDetail) bean.insertOrUpdate(detail);
		}
	}

	public void removeDetails() throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		for (FiscalModelDetail detail : getDeclaration().getDetails()) {
			bean.remove(detail);
		}
	}

	public boolean isDiskOk() {
		int errors = 0;
		if (getFileOutput() != null) {
			errors = getFileOutput().getErrors().size();
		}
		return (errors == 0);
	}
	
	public void downloadDisk(ActionEvent event) throws ManagerBeanException {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces
					.getExternalContext().getResponse();
			String fileName = getFileName();
			MimeType mimeType = getMimeType();
			response.setContentType(mimeType.getName());
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ fileName + "." + mimeType.getExtension() + "\";");
			ServletOutputStream output = response.getOutputStream();
			InputStream input = getFileOutput().getFile() != null ? new FileInputStream(
					getFileOutput().getFile()) : new ByteArrayInputStream(
					getFileOutput().getContent());
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

	protected void checkFiscalActivity(FiscalModelType type) {
		try {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			IManagerBean bean = BeanManager
					.getManagerBean(FiscalActivity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					bean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_YEAR), year);
			int count = bean.getCount(criteria);
			if (count == 0) {
				AonUtil.addErrorMessage("No se ha realizado la introducción de los datos previos de actividades, necesarios para la confección del impuesto.");
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	protected abstract FiscalModelType getModelType();

	public abstract boolean isDifEnabled();

	public abstract String getFileName();

	public abstract MimeType getMimeType();

	public abstract void onCreateDisk(ActionEvent event);

	private static String AEAT_PRINT_MODULE_SCRIPT_FOLDER = "/usr/share/java/aon.mipf";
	private static String AEAT_PRINT_MODULE_SCRIPT_PATH = AEAT_PRINT_MODULE_SCRIPT_FOLDER + "/mipf13pdf.sh";
	
	public boolean isAeatReportEnabled() {
		FiscalModel to = (FiscalModel) getTo();
		return ( !isNew() 
			&& to.isFinished() 
			&& !to.isModel111() 
			&& isScriptPresent());
	}
	public boolean isAeatOfficialReportEnabled() {
		FiscalModel to = (FiscalModel) getTo();
		return ( isAeatReportEnabled() 
			&& (to.isCashBasis() 
				|| getDeclaration().isNegative()
				|| getDeclaration().isToDeduct())
			);
	}
	public boolean isAeatDraftReportEnabled() {
		FiscalModel to = (FiscalModel) getTo();
		return ( isAeatReportEnabled() 
				&& !to.isCashBasis() 
				&& !getDeclaration().isNegative()
				&& !getDeclaration().isToDeduct());
	}
	
	public boolean isScriptPresent() {
		File file = new File(AEAT_PRINT_MODULE_SCRIPT_PATH);
		return file.canRead(); 
	}

	public String aeatReport() {
		/*
		 * mipf13pdf.sh 
		 * 	/E:nombrearchivodatos 			indica el fichero que contiene los datos de entrada que se 
		 * 									van a imprimir, de ser correctos. Es OBLIGATORIO y admite 
		 * 									ruta completa. 
		 * [/R:nombrearchivoerrores] 		indica el nombre del fichero que contiene la relación de 
		 * 									errores, si los hubiera. Es OPCIONAL y admite ruta completa. 
		 * 									Si no se especifica o no se puede abrir, el programa utiliza 
		 * 									el archivo ERRORES.TXT que es creado en el directorio de 
		 * 									ejecución del programa.
		 * [/P:nombreFicheroPDF] 			indica el nombre del fichero de salida PDF, que contiene la 
		 * 									declaración en caso que el fichero de entrada no contenga
		 * 									errores Es OPCIONAL y admite ruta completa. 
		 * [/V:{S|N}] 						Parámetro OPCIONAL para indicar que el módulo se comporte exclusivamente 
		 * 									como un módulo de VALIDACIÓN. Si se indica /V:S el programa NO IMPRIMIRÁ,
		 * 									simplemente verificará el contenido del fichero. 
		 * [/C:{S|N}] 						Parámetro OPCIONAL para indicar la generación o no de una copia 
		 * 									adicional (ejemplar para el declarante) de la declaración a 
		 * 									imprimir. Se imprime una copia más cuando se indica 
		 * /C:S [/F:nombrearchivoflag]		indica el nombre del fichero que se utiliza como flag de ejecución
		 * 									del programa. Se crea en el momento en que empieza la ejecución y
		 * 									desaparece cuando ésta finaliza. Es OPCIONAL y admite ruta completa.
		 * 									Si no se especifica o no se puede abrir, el programa utiliza el
		 * 									archivo FLAG.TXT que es creado en el directorio de ejecución del
		 * 									programa.
		 * 
		 * EJEMPLO: 
		 * 		mipf13pdf.sh /E:/tmp/310.TXT /R:/tmp/errores310.txt /P:/tmp/pruebaMIPF.pdf /C:S
		 */
		
		
		File errorFile = null;
		File pdfFile = null;
		File draftFile = null;
		Process process = null;
		File tempDataFile = null;
		FileInputStream fisError = null;
		FileInputStream fisPDF = null;
		try {
			errorFile = File.createTempFile("fs_", ".err");
			pdfFile = File.createTempFile("fs_", ".pdf");
			String pdfPath = pdfFile.getAbsolutePath();	
			String draftPath = StringUtils.substringBefore(pdfPath, ".pdf") + "Borrador.pdf"; 
			System.out.println( pdfPath );
			System.out.println( draftPath );
			
			tempDataFile = saveDiskFile();
			String[] options = { AEAT_PRINT_MODULE_SCRIPT_PATH
					, "/E:" + tempDataFile.getAbsolutePath()
					, "/R:" + errorFile.getAbsolutePath()
					, "/P:" + pdfPath 
					, "/C:S"};
			process = Runtime.getRuntime().exec(options,null,new File(AEAT_PRINT_MODULE_SCRIPT_FOLDER));
			int timeout = 30000;
			Worker worker = new Worker(process);
			worker.start();
			try {
				worker.join(timeout);
				if (worker.exit == null) {
					throw new TimeoutException();
				}
			} catch (InterruptedException ex) {
				worker.interrupt();
				Thread.currentThread().interrupt();
			} finally {
				process.destroy();
			}
			
			
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			boolean isDraft = false;
			if (!pdfFile.exists()) {
				pdfFile = new File(draftPath);
				isDraft = true;
			}
			if (pdfFile.exists()) {
				fisPDF = new FileInputStream( pdfFile );
				response.setContentType( MimeType.MIME_PDF.getName() );
				FiscalModel fiscalModel = (FiscalModel) getTo();
				CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
				String name = fiscalModel.getModel() 
						+ "_" + fiscalModel.getPeriod() 
						+ "_" + fiscalModel.getYear() 
						+ (isDraft?"_Borrador":"")
						+ "_" + companyController.getCompanyLabel();
				response.setHeader("Content-Disposition", "attachment; filename=\"" + name + ".pdf" + "\"");
				response.setHeader("Content-Length", String.valueOf(pdfFile.length()));
				IOUtils.copy(fisPDF, response.getOutputStream());
				fisPDF.close();
				context.responseComplete();
			} else {
				StringBuffer buf = new StringBuffer();
				if (errorFile.exists()) {
					fisError = new FileInputStream(errorFile);
					List<?> list = IOUtils.readLines(fisError,"UTF-8");
					fisError.close();
					for (Object o : list) {
						buf.append(o.toString());
					}
				}
				AonUtil.addErrorMessage("Se han producido errores al generar el fichero. (" + buf.toString() + ")");	
			}
		} catch (TimeoutException e) {
			String msg = "Tiempo de espera agotado.";
			AonUtil.addErrorMessage(msg);
		} catch (IOException e) {
			String msg = "No se pudo realizar la impresión del módulo." + e.getMessage();
			AonUtil.addErrorMessage(msg);
		} finally {
			FileUtils.deleteQuietly(errorFile);
			FileUtils.deleteQuietly(pdfFile);
			FileUtils.deleteQuietly(draftFile);
			FileUtils.deleteQuietly(tempDataFile);
			IOUtils.closeQuietly(fisError);
			IOUtils.closeQuietly(fisPDF);
		}
		return null;
	}

	private File saveDiskFile() throws IOException {
		OutputStream outputData = null;
		try {
			if (fileOutput == null) {
				onCreateDisk(null);
			}
			File tempDataFile = File.createTempFile("fs_", ".txt");
			outputData = new FileOutputStream(tempDataFile);
			IOUtils.write(fileOutput.getContent(), outputData);
			outputData.flush();
			outputData.close();
			return tempDataFile;
		} finally {
			IOUtils.closeQuietly(outputData);
		}
	}
	
	private class Worker extends Thread {
		private final Process process;
		private Integer exit;

		private Worker(Process process) {
			this.process = process;
		}

		public void run() {
			try {
				exit = process.waitFor();
			} catch (InterruptedException ignore) {
				return;
			}
		}
	}
}

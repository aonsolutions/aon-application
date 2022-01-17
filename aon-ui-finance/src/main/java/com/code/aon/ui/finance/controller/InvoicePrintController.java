package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_SEND_EMAIL_FNINISH;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.IAttachment;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.ProgressionState;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.facturae.FACeUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.common.LongProcessThread;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.company.controller.PrintParametersController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.finance.util.FinanceEmailUtil;
import com.code.aon.ui.finance.util.InvoicePrintProcess;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.IMailAccount;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.tbai.TbaiData;

public class InvoicePrintController extends InvoiceController implements IFinanceConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoicePrintController.class.getName());
	
	private IPriceStrategy priceStrategy;
	
	private ProgressionState progressionState;
	
	private File zipFile;
	
	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public double getInvoiceTotalPrice() throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getModel().getRowData();
		return getPriceStrategy().getTotalPrice(invoice, invoice);
	}
	
	public void onInitSendEmail( ActionEvent event ) {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onPrepareEmailWindow(event);
		if ( controller.isShowNewMessageWindow() ) {			
			controller.setAppendSignature(false);
			controller.setSaveSent(false);
			try {		
				controller.onNewMessage(event);
				InvoiceController invoiceController = (InvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
				FinanceEmailUtil emailUtil = invoiceController.getEmailController();
				controller.setSubject( emailUtil.getEmailSubject() );
				String body = emailUtil.getEmailBody();
				controller.updateMessageBody( emailUtil.getEmailContent(body) );
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
				AonUtil.addErrorMessage(th.getMessage());
				throw new AbortProcessingException(th.getMessage(), th);
			}
		}		
	}	

	@SuppressWarnings("unchecked")	
	public List<Integer> getInvoiceIds() throws ManagerBeanException {
		String idAlias = getManagerBean().getFieldName(IEntityAlias.INVOICE_ID);
		ProjectionList pl = new ProjectionList(Projection.property(idAlias));
		return getManagerBean().getList(pl, getCriteria());		
	}
	
	public void onSendInvoicesByEmail( ActionEvent event ) {
		LogPanelController logger = LogPanelController.getInstance();		
		InvoiceController controller = (InvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
		FinanceEmailUtil emailUtil = controller.getEmailController();
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		messageController.setShowNewMessageWindow(false);
		String subject = messageController.getSubject();
		String content = messageController.getContent();
		IMailAccount account = messageController.getSenderMailAccount();
		try {
			emailUtil.changeMailAccount(account);
			emailUtil.setNumberOfMessagesPerTransport(10);
    		List<Integer> ids = getInvoiceIds();
    		for( int i = 0; i < ids.size(); i++ ) {
				if ( logger.isActivePoll() ) {
	    			Invoice invoice = (Invoice) getManagerBean().get(ids.get(i));
	    			emailUtil.sendInvoice( i+1, invoice, subject, content, messageController.isSaveSent()  );
				} else {
					break;
				}    			
    		}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage(), th);
		} finally {
			emailUtil.close();
			logger.info( AonUtil.getMessage(FINANCE_INVOICE_SEND_EMAIL_FNINISH) );
			logger.finish();
		}
	}	
	
	public String getName( Invoice invoice, MimeType type ) {
		return getDescription(invoice) + "(" + invoice.getId() + ")." + type.getExtension();
	}
	
    private File getFacturaeZipFile() throws IOException, ManagerBeanException {
    	InvoiceController controller = (InvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
    	File file = File.createTempFile( "invoices", "." + MimeType.MIME_ZIP.getExtension());
		OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(file) );
		ZipOutputStream zipOut = new ZipOutputStream(fileOut);
		for( ITransferObject to : getManagerBean().getList(getCriteria()) ) {
			Invoice invoice = (Invoice) to;
			IAttachment attach = controller.getInvoiceFacturae(invoice);
			if ( (attach != null) && (!ArrayUtils.isEmpty(attach.getData())) ) {
				MimeType type = FACeUtil.isDefined(invoice) ? MimeType.MIME_XSIG : MimeType.MIME_XML;
				String name = getName(invoice, type); 
	            zipOut.putNextEntry(new ZipEntry(name));
	            zipOut.write(attach.getData());
	        	zipOut.closeEntry();				
			}
		}
		zipOut.close();
		return file;
    }
	
    public void onDownloadFacturaeZip( ActionEvent event ) {
    	File zipFile = null;
		try {    	
	    	zipFile = getFacturaeZipFile();
	    	downloadZip(zipFile);
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage(), th);
		} finally {
			FileUtils.deleteQuietly(zipFile);
		}
    }

    private void downloadZip( File zipFile ) throws FileNotFoundException {
    	InputStream in = null;
		try {    	
	    	in = new BufferedInputStream(new FileInputStream(zipFile));
	        DownloadUtil.downloadAttachment("invoices.zip", MimeType.MIME_ZIP, in, zipFile.length() );
		} finally {
			IOUtils.closeQuietly(in);
		}    	
    }
    
	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		setProgressionState(new ProgressionState());
	}

	public void onStartInvoicesZip(ActionEvent event) {
		PrintParametersController controller = (PrintParametersController) AonUtil
				.getRegisteredBean(ICompanyConstants.PRINT_PARAMETERS_CONTROLLER_NAME);
		getProgressionState().start();
		String report = controller.getSaleInvoiceParams().getInvoicePrintTemplateValue();
		InvoicePrintProcess ipp = new InvoicePrintProcess(this, report);
		LongProcessThread thread = new LongProcessThread(ipp);
		thread.start();
	}
	
	public String getMultipleDownloadUrl() throws ManagerBeanException {
		com.esferalia.aon.occam.api.model.Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		JSONArray ids = new JSONArray();
		getInvoiceIds().stream().forEach(id -> ids.put(id));
		JSONObject json = new JSONObject()
			.put("domain_id", domain.getId())
			.put("domain_name", domain.getName())
			.put("domain_login",  UserUtils.getInstance().getLoggedUser().getLogin())
			.put("ids", ids)
			.put("status", InvoiceStatus.SCORED.name());
		return "/ms/api/multiple_download/invoice?json=" + Base64.getEncoder().encodeToString(json.toString().getBytes(StandardCharsets.UTF_8));
	}
	
	public  void onDownloadMultipleInvoiceZip(ActionEvent event) throws ManagerBeanException, IOException {
		List<File> list = new LinkedList<>();
		com.esferalia.aon.occam.api.model.Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		getInvoiceIds().stream().forEach(id ->{
			try {
				com.esferalia.aon.occam.api.model.finance.Invoice invoice = AON_SOLUTIONS.getInvoice(domain.getName(), domain.getId(), login, id);
				if(InvoiceType.SALES.equals(invoice.getType())) {
					CompanyFull company = AON.getCompanyFull(domain.getName(), domain.getId(), login);
					Attach logo = new Attach();
					PrintInvoiceConfiguration config = AON_SOLUTIONS.getPrintInvoiceConfiguration(domain.getName(), domain.getId(), login, true);
					if(config.isLogo()) {
						Integer regId = company.getRegistry().getId();
						logo = AON.getAttach(domain.getName(), domain.getId(), login, f-> f.getAttachModuleProperty().eq(regId)
							.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);
					}
					File file = File.createTempFile("Factura " + invoice.getReferenceCode(), ".pdf");
					FileOutputStream out = new FileOutputStream(file);
					
					String qrUrl = domain.getName() + "/dip?source=invoice&id=" + id;  
					TbaiConfiguration tbai = AON.getTbaiConfiguration(domain.getName(), domain.getId(), login);
					String tbaiId = "";
					if(tbai.isActive()) {
						TbaiData tbaiData = TbaiData.getInstance(tbai);
						String tbaiUrl = tbaiData.getTbaiUrl(domain.getName(), domain.getId(), login, invoice.getId());
						qrUrl = AonStringUtils.isBlank(tbaiUrl) ? qrUrl : tbaiUrl;
						tbaiId = tbaiData.getTbaiId(domain.getName(), domain.getId(), login, invoice.getId());
					}
					PdfMaker.printInvoice(out, company, invoice, config, qrUrl, logo.getData(), tbaiId);
					list.add(file);	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
    	File file = File.createTempFile( "invoices", "." + MimeType.MIME_ZIP.getExtension());
		OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(file) );
		ZipOutputStream zos = new ZipOutputStream(fileOut);
		Closeable res = zos;
		try {
			for (File f : list) {
				String fileName = f.getName();
				zos.putNextEntry(new ZipEntry(fileName));
				copy(f, zos);
				zos.closeEntry();
			}
		} finally {
			res.close();
		}
		downloadZip(file);
	}

	private static void copy(File file, OutputStream out) throws IOException {
		InputStream in = new FileInputStream(file);
    	try {
    		copy(in, out);
    	} finally {
    		in.close();
    	}
	}

	private static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
    	while (true) {
    		int readCount = in.read(buffer);
    		if (readCount < 0) {
    			break;
    		}
    		out.write(buffer, 0, readCount);
    	}
	}

	public void onDownloadInvoicesZip(ActionEvent event) {
		if ( getProgressionState().isFinish() ) {
			if ( getZipFile() != null ) {
				try {    	
			    	downloadZip(getZipFile());
				} catch (Throwable th) {
					AonUtil.addErrorMessage(th.getMessage());
					throw new AbortProcessingException(th.getMessage(), th);
				} finally {
					FileUtils.deleteQuietly(getZipFile());
				}
			}
		}
		getProgressionState().finish();
	}
	
	public void onClosePanel(ActionEvent event) {
		getProgressionState().finish();
	}	
	
	public ProgressionState getProgressionState() {
		return progressionState;
	}

	public void setProgressionState(ProgressionState progressionState) {
		this.progressionState = progressionState;
	}

	public File getZipFile() {
		return zipFile;
	}

	public void setZipFile(File zipFile) {
		this.zipFile = zipFile;
	}
    
}
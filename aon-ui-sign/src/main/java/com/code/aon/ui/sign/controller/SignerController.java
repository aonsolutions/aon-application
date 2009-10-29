package com.code.aon.ui.sign.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import net.esle.sinadura.core.firma.exceptions.SinaduraCoreException;

import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class SignerController implements ISignConstants {

	private static final Logger LOGGER = Logger.getLogger(SignerController.class.getName());
	
	private ISignatureController signatureController;
	
	private String reportKey;
	
	private IManagerBean attachmentBean;

	public ISignatureController getSignatureController() {
		return signatureController;
	}

	public CertificateController getCertificateController() {
		return (CertificateController) AonUtil.getRegisteredBean(CERTIFICATE_CONTROLLER);
	}

	public void setSignatureController(ISignatureController signatureController) {
		this.signatureController = signatureController;
		this.attachmentBean = signatureController.getAttachmentBean();
	}
	
	public String getReportKey() {
		return reportKey;
	}

	public void setReportKey(String reportKey) {
		this.reportKey = reportKey;
	}
	
	public IAttachment getReport( ITransferObject to ) {
		return getReport(to, getReportKey(), OutputFormat.PDF);
	}	

	private MimeType getMimeType( OutputFormat outputFormat ) {
		if ( outputFormat == OutputFormat.PDF ) {
			return MimeType.MIME_PDF;
		} else if ( outputFormat == OutputFormat.HTML) {
			return MimeType.MIME_HTML;
		} else if ( outputFormat == OutputFormat.XML) {
			return MimeType.MIME_XML;
		} else if ( outputFormat == OutputFormat.XLS ) {
			return MimeType.MIME_MS_EXCEL;
		} else if ( outputFormat == OutputFormat.CSV ) {
			return MimeType.MIME_TXT;
		} else if ( outputFormat == OutputFormat.RTF ) {
			return MimeType.MIME_RTF;
		} else if ( outputFormat == OutputFormat.TXT ) {
			return MimeType.MIME_TXT;
		} else if ( outputFormat == OutputFormat.DOCX ) {
			return MimeType.MIME_MS_WORD_2007;
		}
		return null;
	}
	
	public IAttachment getReport( ITransferObject to, String report, OutputFormat outputFormat ) {
		IAttachment attachment = null;
		try {
			ReportManager reportManager = new ReportManager();
			reportManager.setCollectionProvider( new SingleCollectionProvider(to) );
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			reportManager.execute( out, report);
			attachment = signatureController.newAttachment(to);
			attachment.setData(out.toByteArray());
			attachment.setMimeType(getMimeType(outputFormat));
			attachment.setDescription( signatureController.getDescription(to));
		} catch (Throwable e) {
			LOGGER.severe(">>>> onReport " + e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}			
		return attachment;
	}	

	public IAttachment getSignedAttachment( Serializable parentId ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		String type = attachmentBean.getFieldName( signatureController.getAttachmentMimeTypeAlias() );
		criteria.addEqualExpression( type, MimeType.MIME_SIGNED_PDF );
		String parentAlias = attachmentBean.getFieldName( signatureController.getAttachmentParentAlias() );
		criteria.addEqualExpression( parentAlias, parentId );		
		List<ITransferObject> list = attachmentBean.getList(criteria);
		if (! list.isEmpty() ) {
			return (IAttachment) list.get(0);
		}
		return null;
	}	
	
	public String onReport() {
		try {
			ITransferObject to = signatureController.getTo();
			IAttachment attach = null;
			if ( signatureController.isSigned(to) ) {
				Serializable id = signatureController.getManagerBean().getId(to);
				attach = getSignedAttachment( id );
			} else {
				attach = signatureController.getUnsignedAttachment(to);
			}
			if ( attach != null ) {
				AttachmentUtil.downloadAttachment(attach);
			}
		} catch (Throwable e) {
			LOGGER.severe(">>>> onReport " + e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}			
		return null;
	}
	
	private void cancelSign( ITransferObject to ) throws ManagerBeanException {
		this.cancelSign(to, false);
	}

	public void cancelSign( ITransferObject to, boolean batch ) throws ManagerBeanException { 
		Serializable id = signatureController.getManagerBean().getId(to);
		IAttachment attachment = getSignedAttachment( id );
		if ( attachment != null ) {
			attachmentBean.remove( attachment );
		}
		updateSigned(to, false, batch);
	}

	private void sign( ITransferObject to, IAttachment attach ) throws ManagerBeanException, SinaduraCoreException, ReportException, IOException {
		this.sign(to, attach.getDescription(), attach.getData(), false);
	}
	
	public void sign( ITransferObject to, String description, byte[] pdfData, boolean batch ) throws ManagerBeanException, SinaduraCoreException, ReportException, IOException { 
		byte[] signedFileData = getCertificateController().getSignedFileData( pdfData, true );
		
		IAttachment attachment = signatureController.newAttachment(to);
		attachment.setData( signedFileData );
		attachment.setDescription( description );
		attachment.setMimeType( MimeType.MIME_SIGNED_PDF );
		attachmentBean.insert( attachment );

		updateSigned(to, true, batch);
	}
	
	private void updateSigned( ITransferObject to, boolean value, boolean batch ) throws ManagerBeanException {
		signatureController.setSigned(to, value);
		try {
			if (! batch ) {
				signatureController.getManagerBean().restoreNullSubPOJOs(to);	
			}
			signatureController.getManagerBean().update(to);
		} finally {
			if (! batch ) {
				signatureController.getManagerBean().initializePOJO(to);
			}
		}		
	}
	
	public void onSign( ActionEvent event ) {
		CertificateController cc = (CertificateController) AonUtil.getRegisteredBean(CERTIFICATE_CONTROLLER);
		if (! cc.resolveCertificado() ) {
			return;
		}
		try {
			ITransferObject to = signatureController.getTo();
			sign( to, signatureController.generateReportAttachment(to) );
		} catch (Throwable e) {
			LOGGER.severe(">>>> onSign " + e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			cc.setShowSignWindow(false);
		}
	}
	
	public void onCancelSign( ActionEvent event ) {
		try {
			cancelSign( signatureController.getTo() );
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>> onCancelSign " + e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
}
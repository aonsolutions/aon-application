package com.code.aon.ui.sign.controller;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.report.OutputFormat;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.watson.error.AonCoreException;

public class SignerController implements ISignConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(SignerController.class.getName());
	
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
		this.reportKey = StringUtils.trimToNull(reportKey);
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
			attachment = signatureController.newAttachment(to, MimeType.MIME_PDF);
			attachment.setData(out.toByteArray());
			attachment.setMimeType(getMimeType(outputFormat));
			attachment.setDescription( signatureController.getDescription(to));
		} catch (Throwable e) {
			LOGGER.error(">>>> onReport " + e.getMessage());
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
		List<ITransferObject> list = attachmentBean.getList(criteria, 0, 1);
		if (! list.isEmpty() ) {
			return (IAttachment) list.get(0);
		}
		return null;
	}	
	
	public IAttachment getSignedFacturae( Serializable parentId ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		String type = attachmentBean.getFieldName( signatureController.getAttachmentMimeTypeAlias() );
		criteria.addEqualExpression( type, MimeType.MIME_XSIG );
		String parentAlias = attachmentBean.getFieldName( signatureController.getAttachmentParentAlias() );
		criteria.addEqualExpression( parentAlias, parentId );		
		List<ITransferObject> list = attachmentBean.getList(criteria, 0, 1);
		if (! list.isEmpty() ) {
			return (IAttachment) list.get(0);
		}
		return null;
	}	
	
	public boolean hasSignedFacturae( Serializable parentId ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		String type = attachmentBean.getFieldName( signatureController.getAttachmentMimeTypeAlias() );
		criteria.addEqualExpression( type, MimeType.MIME_XSIG );
		String parentAlias = attachmentBean.getFieldName( signatureController.getAttachmentParentAlias() );
		criteria.addEqualExpression( parentAlias, parentId );		
		return attachmentBean.getCount(criteria) > 0;
	}		
	
	public String onReport() {
		try {
			ITransferObject to = signatureController.getTo();
			IAttachment attach = null;
			if ( signatureController.isSigned(to) ) {
				Serializable id = signatureController.getManagerBean().getId(to);
				attach = getSignedAttachment( id );
			}
			if ( attach == null) {
				attach = signatureController.getUnsignedAttachment(to, MimeType.MIME_PDF);
			}
			if ( attach != null ) {
				DownloadUtil.downloadAttachment(attach);
			}
		} catch (Throwable e) {
			LOGGER.error(">>>> onReport " + e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}			
		return null;
	}
	
	public String onDownloadFacturae() {
		try {
			ITransferObject to = signatureController.getTo();
			IAttachment attach = null;
			if ( signatureController.isSigned(to) ) {
				Serializable id = signatureController.getManagerBean().getId(to);
				attach = getSignedFacturae( id );
			}
			if ( attach == null) {
				attach = signatureController.getUnsignedAttachment(to, MimeType.MIME_XML);
			}
			if ( attach != null ) {
				DownloadUtil.downloadAttachment(attach);
			}
		} catch (Throwable e) {
			LOGGER.error(">>>> onReport " + e.getMessage());
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
		IAttachment facturae = getSignedFacturae( id );
		if ( facturae != null ) {
			attachmentBean.remove( facturae );
		}
		updateSigned(to, false, batch);
	}

	public void sign( ITransferObject to, IAttachment attachment, boolean batch ) throws AonCoreException, ManagerBeanException {
		getCertificateController().signAttachment(attachment);
		updateSigned( to, attachment, batch );
	}
	
	public void updateSigned( ITransferObject to, IAttachment attachment, boolean batch ) throws ManagerBeanException { 
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
			if ( cc.isUsingSmartCard() ) {
				updateSigned( to, cc.getAttachment(), false );
			} else {
				sign( to, signatureController.generateReportAttachment(to), false );	
				IAttachment attach = signatureController.getUnsignedAttachment(to, MimeType.MIME_XML);
				if ( attach != null ) {
					sign( to, attach, false );
				}
			}
		} catch (Throwable e) {
			LOGGER.error(">>>> onSign " + e.getMessage());
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
			LOGGER.error(">>>> onCancelSign " + e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
}
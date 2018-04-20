package com.code.aon.ui.commercial.util;

import static com.code.aon.ui.common.ICommonMessages.COMMERCIAL_OFFER_EMAIL_BODY;
import static com.code.aon.ui.common.ICommonMessages.COMMERCIAL_OFFER_EMAIL_SUBJECT;
import static com.code.aon.ui.common.ICommonMessages.COMMERCIAL_OFFER_SDD_MANDATE_EMAIL_BODY;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferAttachment;
import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.marketing.enumeration.MailProcessType;
import com.code.aon.ql.Criteria;
import com.code.aon.report.ReportException;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.finance.SddMandateObject;
import com.code.aon.ui.finance.util.FinanceEmailUtil;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.server.AonDateUtils;

public class CommercialEmailUtil extends CompanyEmailUtil {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void initMessageController( MessageController messageController, Offer offer ) throws ManagerBeanException, IOException, ReportException {
		initMessageController( messageController, offer, null, true, true, false);
	}
	
	public void initMessageController( MessageController messageController, Offer offer, SddMandateObject sddMandateObject, boolean includeOffer, boolean includeOfferAttach, boolean includeSddMandate) throws ManagerBeanException, IOException, ReportException {
		String[] emails = null;
		Target target = offer.getTarget();
		if ( target != null ) {
			emails = getCommercialEmails(target.getRegistry());
		}
		initMessageController(messageController, emails);
		messageController.setSubject( getEmailSubject(offer) );
	
		Boolean istemplate = messageController.initMessageController(getDomain(offer.getDomain()), "", getMap(offer), "AON_MAIL_PROCESS" + MailProcessType.OFFER.ordinal());
		
		String bodyMessage = "";
		if(includeOffer){
			bodyMessage += getEmailOfferBody(offer);
			messageController.addAttachment(getOfferFile(offer));
		}
		if(includeOfferAttach){
			for( AonFile aonFile : getOfferAttachemnts(offer) ) {
				messageController.addAttachment(aonFile);
			}		
		}
		
		if(includeSddMandate){
			bodyMessage += getEmailSddMandateBody(offer);
			FinanceEmailUtil emailUtil = new FinanceEmailUtil();
			messageController.addAttachment(emailUtil.getSddMandateReport(sddMandateObject));
		}
		
		if(!istemplate) {
			messageController.updateMessageBody( this.getEmailContent(bodyMessage) );
		}
	}
	
	private Map<String,String> getMap(Offer offer) {
		Map<String,String> map = new HashMap<String,String>();	
		map.put("comentarios", offer.getComments());
		map.put("comments", offer.getComments());
		
		map.put("nombre_commercial", offer.getSeller().getRegistry().getName());
		map.put("seller_name", offer.getSeller().getRegistry().getName());
		
		map.put("documento_commercial", offer.getSeller().getRegistry().getDocument());
		map.put("seller_document", offer.getSeller().getRegistry().getDocument());
		
		map.put("nombre_cliente_potencial", offer.getTarget().getRegistry().getName());
		map.put("target_name", offer.getTarget().getRegistry().getName());
		
		map.put("documento_cliente_potencial", offer.getTarget().getRegistry().getDocument());
		map.put("target_document", offer.getTarget().getRegistry().getDocument());

		map.put("nombre_proveedor", offer.getSupplier().getRegistry().getName());
		map.put("supplier_name", offer.getSupplier().getRegistry().getName());
		
		map.put("documento_proveedor", offer.getSupplier().getRegistry().getDocument());
		map.put("supplier_document", offer.getSupplier().getRegistry().getDocument());

		map.put("fecha", AonDateUtils.simpleFormat(offer.getDate()));
		map.put("date", AonDateUtils.simpleFormat(offer.getDate()));
		
		map.put("serie", offer.getSeries());
		map.put("numero", Integer.toString(offer.getNumber()));
		map.put("number", Integer.toString(offer.getNumber()));
		map.put("referencia", offer.getReferenceCode());
		map.put("reference", offer.getReferenceCode());
		
		map.put("referencia_externa", offer.getReferenceCode());
		map.put("external_reference", offer.getReferenceCode());
		
		map.put("estado", offer.getStatus().getName(AonUtil.getCurrentLocale()));
		map.put("status", offer.getStatus().getName(AonUtil.getCurrentLocale()));
				
		return map;
	}
	
	private Domain getDomain(Integer domainId) {
		return AON.getDomain(AonUtil.getDomainName(), domainId, "");
	}
	
	public String getEmailSubject( Offer offer ) {
		String message = AonUtil.getMessage(COMMERCIAL_OFFER_EMAIL_SUBJECT);
		return MessageFormat.format(message, offer.getReferenceCode() );
	}
	
	public String getEmailOfferBody( Offer offer ) throws UnsupportedEncodingException {
		String bodyMessage = AonUtil.getMessage(COMMERCIAL_OFFER_EMAIL_BODY); 
		return MessageFormat.format(bodyMessage, offer.getReferenceCode(), offer.getIssueDate() );
	}

	public String getEmailSddMandateBody( Offer offer ) throws UnsupportedEncodingException {
		String bodyMessage = AonUtil.getMessage(COMMERCIAL_OFFER_SDD_MANDATE_EMAIL_BODY); 
		return MessageFormat.format(bodyMessage, offer.getReferenceCode(), offer.getIssueDate() );
	}
	
	public AonFile getOfferFile( Offer offer ) throws IOException, ReportException, ManagerBeanException {
		SignerController signer = (SignerController) AonUtil.getRegisteredBean(ICommercialConstants.OFFER_SIGNER_CONTROLLER_NAME);
		File file = File.createTempFile( signer.getReportKey(), ".pdf" );
		IAttachment attach = null;
		if ( offer.isSigned() ) {
			attach = signer.getSignedAttachment(offer.getId());
		} else {
			attach = signer.getReport(offer);
		}
		FileUtils.writeByteArrayToFile(file, attach.getData());
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);	
		aonFile.setFileName( attach.getDescription() + ".pdf" );
		aonFile.setMimeType(MimeType.MIME_PDF);
		return aonFile;
	}

	public List<AonFile> getOfferAttachemnts( Offer offer ) throws ManagerBeanException, IOException {
		IManagerBean offerAttach = BeanManager.getManagerBean(OfferAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerAttach.getFieldName(IEntityAlias.OFFER_ATTACHMENT_OFFER_ID), offer.getId());
		criteria.addNotEqualExpression(offerAttach.getFieldName(IEntityAlias.OFFER_ATTACHMENT_MIME_TYPE), MimeType.MIME_SIGNED_PDF);
		List<ITransferObject> list = offerAttach.getList(criteria);
		if (! list.isEmpty() ) {
			List<AonFile> files = new LinkedList<AonFile>();
			for( ITransferObject to : list ) {
				OfferAttachment attach = (OfferAttachment) to;
				AonFile aonFile = new AonFile();
				String ext = "." + ( (attach.getMimeType() != null) ? attach.getMimeType().getExtension() : "tmp");
				File file = File.createTempFile( attach.getDescription(), ext );
				FileUtils.writeByteArrayToFile(file, attach.getData());
				aonFile.setFile(file);
				aonFile.setFileName( attach.getDescription() );
				aonFile.setMimeType( attach.getMimeType() );
				files.add( aonFile );
			}
			return files;
		}
		return Collections.emptyList();
	}
	
}

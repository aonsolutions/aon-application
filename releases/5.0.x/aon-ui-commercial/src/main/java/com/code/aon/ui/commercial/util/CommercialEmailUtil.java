package com.code.aon.ui.commercial.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferAttachment;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.report.ReportException;
import com.code.aon.ui.commercial.ICommercialMessages;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.AonFile;

public class CommercialEmailUtil extends CompanyEmailUtil implements ICommercialMessages {

	public void initMessageController( MessageController messageController, Offer offer ) throws ManagerBeanException, IOException, ReportException {
		String[] emails = null;
		Target target = offer.getTarget();
		if ( target != null ) {
			emails = getEmails(target.getRegistry());
		}
		initMessageController(messageController, emails, getEmailBody(offer));
		messageController.setSubject( getEmailSubject(offer) );
		messageController.addAttachment(getOfferFile(offer));
		for( AonFile aonFile : getOfferAttachemnts(offer) ) {
			messageController.addAttachment(aonFile);
		}		
	}
	
	public String getEmailSubject( Offer offer ) {
		String message = AonUtil.getMessage(BUNDLE_KEY, COMMERCIAL_OFFER_EMAIL_SUBJECT);
		return MessageFormat.format(message, offer.getReferenceCode() );
	}
	
	public String getEmailBody( Offer offer ) throws UnsupportedEncodingException {
		String bodyMessage = AonUtil.getMessage(BUNDLE_KEY, COMMERCIAL_OFFER_EMAIL_BODY); 
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
		return aonFile;
	}

	public List<AonFile> getOfferAttachemnts( Offer offer ) throws ManagerBeanException, IOException {
		IManagerBean offerAttach = BeanManager.getManagerBean(OfferAttachment.class);
		Criteria criteria = new Criteria();
		String offerIdAlias = offerAttach.getFieldName(ICommercialAlias.OFFER_ATTACHMENT_OFFER_ID);
		criteria.addEqualExpression(offerIdAlias, offer.getId());
		String typeAlias = offerAttach.getFieldName(ICommercialAlias.OFFER_ATTACHMENT_MIME_TYPE);
		Expression exp = ExpressionUtilities.getNotEqualExpression(typeAlias, MimeType.MIME_SIGNED_PDF);
		criteria.addExpression( exp );
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
				files.add( aonFile );
			}
			return files;
		}
		return Collections.emptyList();
	}
	
}

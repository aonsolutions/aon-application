package com.code.aon.ui.commercial.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;

import com.code.aon.commercial.Offer;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.company.Company;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.report.ReportException;
import com.code.aon.ui.commercial.ICommercialMessages;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.AonFile;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailUtil;

public class EmailUtilController implements ICommercialMessages, ICommercialConstants {

	private static final String OFFER_REPORT = "offer";

	private EmailSender sender;
	
	private Company company;

	private MailAccount getDefaultMailAccount( AuthPrincipal user ) {		
		String domain = user.getDomain();
		String login = user.getShortName();
		MailAccount mailAccount;
		try {
			mailAccount = WebmailUtil.getDefaultAccount(domain,login);
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException( e.getMessage(), e);
		}
		return mailAccount;
	}
	
	public Company getCompany() {
		if (company == null) {
			CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			this.company = companyController.obtainCompany();
		}
		return company;
	}

	public String[] getEmails( Registry registry ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		String type = bean.getFieldName( IRegistryAlias.REGISTRY_MEDIA_MEDIA_TYPE );
		criteria.addEqualExpression( type, MediaType.EMAIL );
		String registryField = bean.getFieldName( IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID );
		criteria.addEqualExpression( registryField, registry.getId() );		
		String administrative = bean.getFieldName( IRegistryAlias.REGISTRY_MEDIA_ADMINISTRATIVE );
		criteria.addEqualExpression( administrative, Boolean.TRUE );
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			String[] emails = new String[list.size()];
			for( int i = 0; i < list.size(); i++ ) {
				RegistryMedia rm = (RegistryMedia) list.get(i);
				emails[i] = rm.getValue();
			}
			return emails;
		}
		return null;
	}	

	public String getEmailSubject( Offer offer ) {
		String message = AonUtil.getMessage(BUNDLE_KEY, COMMERCIAL_OFFER_EMAIL_SUBJECT);
		return MessageFormat.format(message, offer.getReferenceCode() );
	}

	public String getEmailBody( Offer offer ) throws UnsupportedEncodingException {
		StringBuffer body = new StringBuffer();
		body.append( "<html><head>" );
		body.append( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" );
		body.append( "</head><body>" );
		body.append(AonUtil.getMessage(BUNDLE_KEY, COMMERCIAL_OFFER_EMAIL_BODY_HEADER) );
		String bodyPart = AonUtil.getMessage(BUNDLE_KEY, COMMERCIAL_OFFER_EMAIL_BODY); 
		body.append( MessageFormat.format(bodyPart, offer.getReferenceCode(), offer.getIssueDate()) );
		body.append(AonUtil.getMessage(BUNDLE_KEY, COMMERCIAL_OFFER_EMAIL_BODY_FOOTER) );
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		body.append( getCompany().getName() ).append( "<br/>" );
		RegistryMedia phone = companyController.getPhone();
		if ( phone != null ) {
			String phoneLabel = AonUtil.getMessage("registryBundle", "registry_phone");
			body.append( StringEscapeUtils.escapeHtml(phoneLabel));
			body.append( ": " ).append( phone.getValue()).append( "<br/>" );
		}
		RegistryMedia fax = companyController.getFax();
		if ( fax != null ) {
			String faxLabel = AonUtil.getMessage("registryBundle", "registry_fax");
			body.append(faxLabel).append( ": " ).append( fax.getValue() ).append( "<br/>" );
		}
		RegistryMedia web = companyController.getWeb();
		if ( web != null ) {
			body.append( "<a href=\"" ).append( web.getValue() ).append( "\">").append( web.getValue() ).append("</a>" );
		}
		return body.toString();
	}
	
	public EmailSender getEmailSender() throws UnsupportedEncodingException {
		if ( this.sender == null ) {
			AuthPrincipal user = UserUtils.getInstance().getPrincipal();
			MailAccount mailAccount = getDefaultMailAccount( user );
			if ( mailAccount != null ) {
				Address from = new InternetAddress( mailAccount.getEmail(), getCompany().getName() );
				this.sender = new EmailSender( from, mailAccount );							
			} else {
				String text = AonUtil.getMessage(BUNDLE_KEY, NOT_MAIL_ACCOUNT); 
				String message = MessageFormat.format(text, user.getShortName() );
				throw new AbortProcessingException( message );
			}
		}
		return this.sender;
	}
	
	public byte[] getOfferPDF( Offer offer ) throws IOException, ReportException, ManagerBeanException {
		ReportManager report = new ReportManager();
		report.setCollectionProvider( new SingleCollectionProvider(offer) );
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		report.execute( out, OFFER_REPORT);
		return out.toByteArray();
	}		
	
	public AonFile getOfferFile( Offer offer ) throws IOException, ReportException, ManagerBeanException {
		File file = File.createTempFile( OFFER_REPORT, ".pdf" );
		byte[] data = getOfferPDF(offer);
		FileUtils.writeByteArrayToFile(file, data);
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);	
		String fileName = "offer_" + offer.getSeries() + "-" + offer.getNumber() + ".pdf";
		aonFile.setFileName( fileName );
		return aonFile;
	}
	
}

package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.CREDITOR_REPORT;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.registry.report.CreditorReportPDF;
import net.aonsolutions.aon.registry.report.CreditorReportXLS;
import net.aonsolutions.aon.report.pdf.AonReportException;

public class CreditorController extends RegistryController implements IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean showAuditInfoWindow;
	
	public boolean isAccountSynchronizable() {
		return isAccountSynchronizable((Creditor)getTo());
	}

	protected boolean isAccountSynchronizable(Creditor creditor) {
		Account account = creditor.getAccount();
		System.out.println(account.getDomain() +" --- "+ creditor.getDomain());
		return (account != null 
			&& account.getId() != null 
			&& account.getDomain() == creditor.getDomain()
			&& !creditor.getRegistry().getFullName().equals(account.getDescription()));
	}

	public void onAccountSynchronize(ActionEvent event) {
		onAccountSynchronize((Creditor)getTo());
	}

	protected void onAccountSynchronize(Creditor creditor) {
		try {
			creditor.getAccount().setDescription(creditor.getRegistry().getFullName());
			creditor.getAccount().setAlias(creditor.getRegistry().getAlias());
			BeanManager.getManagerBean(Account.class).update(creditor.getAccount());
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo sincronizar la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

	public void onNewAccount(ActionEvent event) {
		onNewAccount((Creditor)getTo());
	}

	protected void onNewAccount(Creditor creditor) {
		try {
			AccountBridgeUtil accountBridgeUtil = new AccountBridgeUtil();
			creditor.setAccount(accountBridgeUtil.obtainNewCreditorAccount(creditor));
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo crear la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

    public String getReportTitle(){
    	return AonUtil.getMessage(CREDITOR_REPORT);
	}

	@Override
	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	@Override
	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
	
	
	public String onNewReport() throws ManagerBeanException {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			OutputStream out = response.getOutputStream();
			Occam occam = new Occam()
					.setDomainName(  AonUtil.getDomainName() )
					.setDomain(  DomainManager.getCurrentDomain() )
					.setUser(  AonUtil.getRemoteUser() )
					;
			new CreditorReportPDF( occam )
				.print(out,AonCollectionUtils.stream(getManagerBean().getList(getCriteria()))
					.map(to -> (Creditor) to)
					.map(this::toCreditorFull ));
			response.flushBuffer();
			response.setHeader("Content-disposition","attachment; filename=\"ACREEDORES."+MimeType.PDF.getExtension()+"\";");
			context.responseComplete();
			return null;
		} catch (IOException | AonReportException e) {
			throw new ManagerBeanException( e ); 
		}
	}
	
	public String onNewReportXLS() throws ManagerBeanException {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			
			CreditorReportXLS report = new CreditorReportXLS();
			report.printReport("Diario");

			Stream<CreditorFull> stream = AonCollectionUtils.stream(getManagerBean().getList(getCriteria()))
				.map(to -> (Creditor) to)
				.map(this::toCreditorFull);
			stream.forEach(report);
			response.setContentType(MimeType.MS_EXCEL.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"ACREEDORES."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			report.finalize(response.getOutputStream());
			response.flushBuffer();

			stream.close();
			context.responseComplete();
			return null;

		} catch (IOException e) {
			throw new ManagerBeanException( e ); 
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException( e ); 
		}

	}

	private CreditorFull toCreditorFull(Creditor creditor) {
		CreditorFull creditorFull = new CreditorFull();
		com.esferalia.aon.occam.api.model.registry.Creditor occamCreditor = new com.esferalia.aon.occam.api.model.registry.Creditor();
		
		occamCreditor.setDocument(creditor.getRegistry().getDocument());
		occamCreditor.setAlias(creditor.getRegistry().getAlias());
		occamCreditor.setDocumentCountry(toOccamCountry(creditor.getRegistry().getDocumentCountry()));
		occamCreditor.setId(creditor.getRegistry().getId());
		occamCreditor.setName(creditor.getRegistry().getName());
		creditorFull.setRegistry(occamCreditor);
		
		try {
			com.code.aon.registry.RegistryMedia phone = creditor.getRegistry().getPhone();
			if (phone != null) {
				creditorFull.addMedia(new RegistryMedia().setMedia(MediaType.FIXED_PHONE).setValue(phone.getValue()));
			}
		} catch (ManagerBeanException e) {
			// Sin telefono
		}
		
		occamCreditor.setStatus(creditorStatusToRegistryStatus(creditor.getStatus()));
		
		return creditorFull;

	}

	private Country toOccamCountry(com.code.aon.common.enumeration.Country documentCountry) {
		return Country.safeValueOf( documentCountry.getValue() );
	}
	
	
	
	private static RegistryStatus creditorStatusToRegistryStatus( CreditorStatus cs ) {
		if(cs== null) {
			return null;
		}
		return RegistryStatus.valueOf( cs.toString() );
	}
    
}
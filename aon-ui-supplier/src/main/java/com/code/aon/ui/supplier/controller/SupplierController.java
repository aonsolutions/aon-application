package com.code.aon.ui.supplier.controller;

import static com.code.aon.ui.common.ICommonMessages.SUPPLIER_REPORT;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.hibernateToOccam.registry.OccamSupplier;
import net.aonsolutions.aon.registry.report.SupplierReportPDF;
import net.aonsolutions.aon.registry.report.SupplierReportXLS;
import net.aonsolutions.aon.report.pdf.AonReportException;

public class SupplierController extends RegistryController implements IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean showAuditInfoWindow;
	
	public void onWithholdingChanged(ValueChangeEvent event) {
		Boolean value = (Boolean)event.getNewValue();
		if (!value) {
			((Supplier)getTo()).setWithholdingFarmer(false);
		}
	}

	public void onWithholdingFarmerChanged(ValueChangeEvent event) {
		Boolean value = (Boolean)event.getNewValue();
		if (value) {
			((Supplier)getTo()).setWithholding(true);
		}
	}

	public boolean isAccountSynchronizable() {
		return isAccountSynchronizable((Supplier)getTo());
	}

	protected boolean isAccountSynchronizable(Supplier supplier) {
		Account account = supplier.getAccount();
		return account != null 
			&& account.getId() != null 
			&& account.getDomain() == supplier.getDomain()
			&& !supplier.getRegistry().getFullName().equals(account.getDescription());
	}

	public void onAccountSynchronize(ActionEvent event) {
		onAccountSynchronize((Supplier)getTo());
	}

	protected void onAccountSynchronize(Supplier supplier) {
		try {
			supplier.getAccount().setDescription(supplier.getRegistry().getFullName());
			supplier.getAccount().setAlias(supplier.getRegistry().getAlias());
			BeanManager.getManagerBean(Account.class).update(supplier.getAccount());
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo sincronizar la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

	public void onNewAccount(ActionEvent event) {
		onNewAccount((Supplier)getTo());
	}

	protected void onNewAccount(Supplier supplier) {
		try {
			AccountBridgeUtil accountBridgeUtil = new AccountBridgeUtil();
			supplier.setAccount(accountBridgeUtil.obtainNewSupplierAccount(supplier));
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo crear la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

   public String getReportTitle(){
	   return AonUtil.getMessage(SUPPLIER_REPORT);
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
			new SupplierReportPDF( occam )
				.print(out,AonCollectionUtils.stream(getManagerBean().getList(getCriteria()))
					.map(to -> (Supplier) to)
					.map( OccamSupplier::from ));
			response.flushBuffer();
			response.setHeader("Content-disposition","attachment; filename=\"PROVEEDORES."+MimeType.PDF.getExtension()+"\";");
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
			
			SupplierReportXLS report = new SupplierReportXLS();
			report.printReport("Diario");

			Stream<SupplierFull> stream = AonCollectionUtils.stream(getManagerBean().getList(getCriteria()))
				.map(to -> (Supplier) to)
				.map(OccamSupplier::from);
			stream.forEach(report);
			response.setContentType(MimeType.MS_EXCEL.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"PROVEEDORES."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
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


}
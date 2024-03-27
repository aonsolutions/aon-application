package com.code.aon.ui.customer.controller;

import static com.code.aon.ui.common.ICommonMessages.CUSTOMER_REPORT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryNote;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.controller.RegistryObservationController;
import com.code.aon.ui.stat.controller.RegistryStatEngineController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.CompanyJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DomainLinked;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.hibernateToOccam.registry.OccamCustomer;
import net.aonsolutions.aon.registry.report.CustomerReportPDF;
import net.aonsolutions.aon.registry.report.CustomerReportXLS;
import net.aonsolutions.aon.report.pdf.AonReportException;

public class CustomerController extends CustomerListController implements ICustomerConstants, IAuditableController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CustomerController.class);
	
    private boolean showAlumnData;
    private boolean showAlumnUpdateConfirmWindow;
    private Integer courseAlumnCount;
	private boolean updateCourseAlumn;
	private boolean showAuditInfoWindow;
	
	public boolean isCeconsulting() {
		return AonUtil.getDomainName().contains("ceconsulting");
	}
	
	public boolean isSnapshot() {
		return AonUtil.getDomainName().contains("aonsolutions.org")
			|| AonUtil.getDomainName().contains("aibanez.net");
	}
	
	public boolean isUpdateCourseAlumn() {
		return updateCourseAlumn;
	}
	
	public void setUpdateCourseAlumn(boolean updateCourseAlumn) {
		this.updateCourseAlumn = updateCourseAlumn;
	}

	public boolean isShowAlumnData() {
		return showAlumnData;
	}

	public void setShowAlumnData(boolean showAlumnData) {
		this.showAlumnData = showAlumnData;
	}
	
	public boolean isShowAlumnUpdateConfirmWindow() {
		return showAlumnUpdateConfirmWindow;
	}

	public void setShowAlumnUpdateConfirmWindow(boolean showAlumnUpdateConfirmWindow) {
		this.showAlumnUpdateConfirmWindow = showAlumnUpdateConfirmWindow;
	}

	public Integer getCourseAlumnCount() {
		return courseAlumnCount;
	}

	public void setCourseAlumnCount(Integer courseAlumnCount) {
		this.courseAlumnCount = courseAlumnCount;
	}

	public boolean isAccountSynchronizable() {
		return isAccountSynchronizable((Customer)getTo());
	}

	protected boolean isAccountSynchronizable(Customer customer) {
		Account account = customer.getAccount();
		return account != null 
			&& account.getId() != null 
			&& account.getDomain() == customer.getDomain()
			&& !customer.getRegistry().getFullName().equals(account.getDescription());
	}

	public boolean isEdiSupportEnabled() {
		return StringUtils.isNotBlank(AppParamUtil.getValue(AppParam.EDI_SUPPORT));
	}
	
	public String getRowCustomerComments(){
		RegistryObservationController controller = (RegistryObservationController) AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_OBSERVATION_CONTROLLER_NAME);
		try {
			if(this.getModel().isRowAvailable()){
				Customer customer = (Customer) this.getModel().getRowData();
				RegistryNote rObservation = controller.getRegistryObservation(customer.getRegistry());
				if(rObservation!=null){
					return rObservation.getComments();
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	public void onAccountSynchronize(ActionEvent event) {
		onAccountSynchronize((Customer)getTo());
	}

	protected void onAccountSynchronize(Customer customer) {
		try {
			customer.getAccount().setDescription(customer.getRegistry().getFullName());
			customer.getAccount().setAlias(customer.getRegistry().getAlias());
			BeanManager.getManagerBean(Account.class).update(customer.getAccount());
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo sincronizar la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

	public void onNewAccount(ActionEvent event) {
		onNewAccount((Customer)getTo());
	}

	protected void onNewAccount(Customer customer) {
		try {
			AccountBridgeUtil accountBridgeUtil = new AccountBridgeUtil();
			customer.setAccount(accountBridgeUtil.obtainNewCustomerAccount(customer));
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo crear la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

	public boolean isInvoicingGroupInMyScopes() {
		Customer customer = (Customer)getTo();
		return UserUtils.getInstance().getCurrentUserScopes().contains(customer.getInvoicingGroup().getCustomer().getScope());
	}

	@Override
	public void accept(ActionEvent event) {
		setUpdateCourseAlumn(false);
		Customer customer = (Customer)getTo();
		if (customer.getStatus() == CustomerStatus.INACTIVE && getCourseAlumnCount() > 0) {
			setShowAlumnUpdateConfirmWindow(true);
		} else {
			super.accept(event);
		}
	}
	
	public void acceptOnly(ActionEvent event) {
		setUpdateCourseAlumn(false);
		super.accept(event);
	}
	
	public void acceptAndUpdate(ActionEvent event) {
		setUpdateCourseAlumn(true);
		super.accept(event);
	}
	
    public String getReportTitle(){
    	return AonUtil.getMessage(CUSTOMER_REPORT);
	}
    
    public void onAlumnEditSearch(ActionEvent event){
    	super.onEditSearch(event);
    	setShowAlumnData(true);
    }
    
	public void onCustomerHistory(ActionEvent e){
		RegistryStatEngineController controller =(RegistryStatEngineController)AonUtil.getRegisteredBean("registryStat");
		controller.setRegistry(((Customer)this.getTo()).getRegistry());
		controller.getRegistryData();
	}
	
	public void onSigCustomerDomainLink(ActionEvent e) throws IOException{
		Customer customer = (Customer) this.getTo();
		
		String request = "https://aon.solutions/ms/api/company/domain?document=" + customer.getRegistry().getDocument()
			 + "&page=1&perPage=30";
	
		
		URL url = new URL(request);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("session_id", "AONd95770f269e711eb94390242ac130002");
		connection.setRequestProperty("charset", "UTF-8");
	
		connection.setUseCaches(false);
		JSONObject json = new JSONObject()
			.put(IJsonNames.DOCUMENT, customer.getRegistry().getDocument())
			.put(IJsonNames.PAGE, 1)
			.put(IJsonNames.PER_PAGE, 30);
		
		OutputStream os = connection.getOutputStream();
		os.write(json.toString().getBytes());
		os.flush();
		
		
		BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
		String output;	
		String response = "";
		while ((output = br.readLine()) != null) {
			response = output; //.replace("'", "\'");	
		}	
		
		JSONArray array = new JSONArray(response);
		for(Integer i = 0; i < array.length(); i++) {
			JSONObject resp = (JSONObject) array.get(i);
			Company company = CompanyJSON.fromJSON(resp);

			DomainLinked domainLinked = new DomainLinked()
					.setId(company.getDomain().getId())
					.setName(company.getDomain().getName())
					.setSchema(JsonUtils.getString(resp ,IJsonNames.SCHEMA))
					.setRegistry(customer.getId())
					.setIndex(i)
					.setType(company.getDomain().getDomainType().name());

			AON.saveDomainLinked(AonUtil.getDomainName(), customer.getDomain(), "", domainLinked);
		}
	}

	public void onLoadInvoicingGroup(ActionEvent event) throws ManagerBeanException {
		Customer customer = (Customer)getTo();
		BasicController invoicingGroupController = (BasicController)AonUtil.getRegisteredBean(INVOICING_GROUP_CONTROLLER_NAME);
		invoicingGroupController.onLoad(event, customer.getInvoicingGroup().getId(), CUSTOMER_FORM_NAME, CUSTOMER_CONTROLLER_NAME + ".select");
	}
	
	public void eInvoiceChange(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue()!=null && (boolean) event.getNewValue()){
			setSelectedTab(CUSTOMER_EINVOICE_TAB);
		} else {
			setSelectedTab(null);
		}
	}
	
	public void ediChange(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue()!=null && (boolean) event.getNewValue()){
			setSelectedTab(CUSTOMER_EDI_TAB);
		} else {
			setSelectedTab(null);
		}
	}

	@Override
	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	@Override
	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
	
	public boolean isTarget() throws ManagerBeanException {
		return obtainTargetId() > 0;
	}
	
	public void onLoadTarget(ActionEvent event) throws ManagerBeanException {
		Integer targetId = obtainTargetId();
		BasicController targetController = (BasicController)AonUtil.getRegisteredBean(TARGET_CONTROLLER_NAME);
		targetController.onLoad(event, targetId, CUSTOMER_FORM_NAME, CUSTOMER_CONTROLLER_NAME + ".refresh");	
	}
	
	private int obtainTargetId() throws ManagerBeanException {
		Customer customer = (Customer)getTo();
		if(CustomerStatus.BLOCKED.equals(customer.getStatus()))
			return -1;
		Integer customerId = customer.getId();
		IManagerBean targetBean = BeanManager.getManagerBean(Target.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(targetBean.getFieldName(IEntityAlias.TARGET_REGISTRY_ID), customerId);
		List<ITransferObject> list = targetBean.getList(criteria);
		if(!list.isEmpty())
			return ((Target)list.get(0)).getId();
		return -1;
	}
	
	public List<DomainLinked> getDomainLinkedList() {
		Customer customer = (Customer)getTo();
		return AON.getDomainLinkedList(AonUtil.getDomainName(), customer.getDomain(), "", customer.getId());
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
			new CustomerReportPDF( occam )
				.print(out,AonCollectionUtils.stream(getManagerBean().getList(getCriteria()))
					.map(to -> (Customer) to)
					.map(OccamCustomer::from ));
			response.flushBuffer();
			response.setHeader("Content-disposition","attachment; filename=\"CLIENTES."+MimeType.PDF.getExtension()+"\";");
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
			
			CustomerReportXLS report = new CustomerReportXLS();
			report.printReport("Diario");

			Stream<CustomerFull> stream = AonCollectionUtils.stream(getManagerBean().getList(getCriteria()))
				.map(to -> (Customer) to)
				.map(OccamCustomer::from);
			stream.forEach(report);
			response.setContentType(MimeType.MS_EXCEL.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"CLIENTES."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
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
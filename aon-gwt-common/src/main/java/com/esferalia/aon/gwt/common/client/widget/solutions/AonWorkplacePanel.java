package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Agreement;
import com.esferalia.aon.occam.api.model.PayrollWorkplace;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.calendar.Calendar;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonWorkplacePanel extends HTMLPanel {
	
	// Callback
	
	public static interface AonWorkplacePanelCallback {
		void onAccept(Workplace workplace);
		void onCancel();
	}

	// CommonService
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// Variables
	
	private final static String EMPTY_STRING = "";
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private AonWorkplacePanelCallback callback;
	private Workplace workplace;
	
	// Wrokplace Info
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomTextBox description = new AonCustomTextBox("Descripci\u00f3n");
	private AonCustomToogleButton statusLB = new AonCustomToogleButton("Estado");
	
	private AonCustomListBox economicAgreementLB = new AonCustomListBox("C. Econ\u00f3mico");
	private AonCustomListBox enterpriseActivityLB = new AonCustomListBox("Actividad");
	
	private AonCustomListBox addressLB = new AonCustomListBox("Direcci\u00f3n");
	private AonCustomListBox scopeLB = new AonCustomListBox("Ambito");
	
	private AonCustomListBox agreementLB = new AonCustomListBox("Convenio");
	private AonCustomListBox calendarLB = new AonCustomListBox("Calendario");
	
	private List<Agreement> agreements;
	private List<Activity> activities;
	private List<Calendar> calendars;
	private List<RegistryAddress> addresses;
	private List<Scope> scopes;
	
	// Constructor
	
	public AonWorkplacePanel(String domainName, Integer domain, String user, Integer registry, List<Agreement> agreements, List<Activity> activities, List<Calendar> calendars, List<RegistryAddress> addresses, List<Scope> scopes, AonWorkplacePanelCallback callback) {
		super(EMPTY_STRING);
		
		this.workplace = new Workplace()
				.setDomain(domain)
				.setEnterprise(registry)
				.setPayrollWorkplace(new PayrollWorkplace().setDomain(domain));
		
		aonMediaPanel(domainName, domain, user, agreements, activities, calendars, addresses, scopes, callback);
	}
	
	public AonWorkplacePanel(String domainName, Integer domain, String user, Workplace workplace, List<Agreement> agreements, List<Activity> activities, List<Calendar> calendars, List<RegistryAddress> addresses, List<Scope> scopes, AonWorkplacePanelCallback callback) {
		super(EMPTY_STRING);
		
		this.workplace = workplace;
		
		if(null == this.workplace.getPayrollWorkplace() || null == this.workplace.getPayrollWorkplace().getId())
			this.workplace.setPayrollWorkplace(new PayrollWorkplace().setDomain(domain).setWorkplace(this.workplace.getId()));
		
		aonMediaPanel(domainName, domain, user, agreements, activities, calendars, addresses, scopes, callback);
	}
	
	private void aonMediaPanel(String domainName, Integer domain, String user, List<Agreement> agreements, List<Activity> activities, List<Calendar> calendars, List<RegistryAddress> addresses, List<Scope> scopes, AonWorkplacePanelCallback callback) {
		initializeCommonService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.agreements = agreements;
		this.activities = activities;
		this.calendars = calendars;
		this.addresses = addresses;
		this.scopes = scopes;
		
		this.callback = callback;
		
		show();
	}
	
	public void show() {
		// Message Panel
		setStyleName(AON.CSS.aonFlexColumn2());
		getElement().getStyle().setProperty("padding", "1rem 0");
		add(messagePanel);
		
		HTMLPanel container = new HTMLPanel(EMPTY_STRING);
		container.setStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("padding", "0 1rem");
		container.getElement().getStyle().setProperty("min-width", "25rem");
		
		// Row 1
		HTMLPanel row = new HTMLPanel(EMPTY_STRING);
		row.setStyleName(AON.CSS.aonItemFlex());
		
		row.add(description);
		row.add(statusLB);
		container.add(row);
		
		// Row 2
		HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		economicAgreementLB.clearItems();
		for(int i=0; i < Administration.values().length; i++)
			economicAgreementLB.addItem(Administration.values()[i].getDescription());
		
		enterpriseActivityLB.clearItems();
		enterpriseActivityLB.addItem("-", "");
		this.activities.forEach(a -> enterpriseActivityLB.addItem(a.getDescription(), a.getId().toString()));
		
		row2.add(economicAgreementLB);
		row2.add(enterpriseActivityLB);
		container.add(row2);
		
		// Third Row
		HTMLPanel row3 = new HTMLPanel(EMPTY_STRING);
		row3.setStyleName(AON.CSS.aonItemFlex());
		
		addressLB.clearItems();
		this.addresses.forEach(a -> addressLB.addItem(a.getFullAddress(), a.getId().toString()));
		
		scopeLB.clearItems();
		this.scopes.forEach(s -> scopeLB.addItem(s.getDescription(), s.getId().toString()));
		
		row3.add(addressLB);
		row3.add(scopeLB);
		container.add(row3);
		
		// Third Row
		HTMLPanel row4 = new HTMLPanel(EMPTY_STRING);
		row4.setStyleName(AON.CSS.aonItemFlex());
		
		calendarLB.clearItems();
		calendarLB.addItem("-", "");
		this.calendars.forEach(c -> calendarLB.addItem(c.getDescription(), c.getId().toString()));
		
		agreementLB.clearItems();
		agreementLB.addItem("-", "");
		this.agreements.forEach(a -> agreementLB.addItem(a.getDescription(), a.getId().toString()));
		
		row4.add(calendarLB);
		row4.add(agreementLB);
		container.add(row4);
		
		// Fill info
		if(workplace.getId() != null) {
			description.setValue(workplace.getDescription());
			statusLB.setValue(workplace.isActive());
			economicAgreementLB.setValue(workplace.getEconomicAgreement().getDescription());
			scopeLB.setValue(workplace.getScope().toString());
			addressLB.setValue(workplace.getAddress().toString());
			
			agreementLB.setValue(null != workplace.getPayrollWorkplace() && null != workplace.getPayrollWorkplace().getAgreement() ? workplace.getPayrollWorkplace().getAgreement().toString() : "");
			enterpriseActivityLB.setValue(null != workplace.getPayrollWorkplace() && null != workplace.getPayrollWorkplace().getEnterpriseActivity() ? workplace.getPayrollWorkplace().getEnterpriseActivity().toString() : "");
			calendarLB.setValue(null != workplace.getPayrollWorkplace() && null != workplace.getPayrollWorkplace().getCalendar() ? workplace.getPayrollWorkplace().getCalendar().toString() : "");
		}
		
		// Buttons
		container.add(createButtonsPanel());
		add(container);	
	}
	
	private Widget createButtonsPanel() {
		HTMLPanel buttonsPanel = new HTMLPanel(EMPTY_STRING);
		buttonsPanel.setStyleName(AON.CSS.aonTextCenter());
		buttonsPanel.getElement().getStyle().setProperty("margin-top", "1rem");
    	
    	Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
			
    		workplace
				.setDescription(description.getValue())
				.setActive(statusLB.getValue())
				.setEconomicAgreement(Administration.safeValueOf(economicAgreementLB.getValue()))
				.setScope(Integer.parseInt(scopeLB.getValue()))
				.setAddress(Integer.parseInt(addressLB.getValue()))
				;
    		
    		workplace.getPayrollWorkplace().setAgreement(AonStringUtils.isBlank(agreementLB.getValue()) ? null : Integer.parseInt(agreementLB.getValue()));
    		workplace.getPayrollWorkplace().setEnterpriseActivity(AonStringUtils.isBlank(enterpriseActivityLB.getValue()) ? null : Integer.parseInt(enterpriseActivityLB.getValue()));
    		workplace.getPayrollWorkplace().setCalendar(AonStringUtils.isBlank(calendarLB.getValue()) ? null : Integer.parseInt(calendarLB.getValue()));
			
			commonService.saveWorkplace(domainName, domainId, user, workplace, new AsyncCallback<Workplace>() {
				
				@Override
				public void onSuccess(Workplace result) {
					callback.onAccept(result);
				}
				
				@Override
				public void onFailure(Throwable error) {
					AonMessagePanel.showError(messagePanel, error.getMessage());
					okButton.setEnabled(true);
				}
				
			});
    	});
    	buttonsPanel.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addClickHandler(e -> {
    		cancelButton.setEnabled(false);
			callback.onCancel();
    	});
    	buttonsPanel.add(cancelButton);
    	
    	return buttonsPanel;
	}

}

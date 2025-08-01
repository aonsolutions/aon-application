package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonDomainScopePanel extends AonCustomDialog {
	
	public static interface AonDomainScopePanelCallback {
		void onAccept(Void result);
		void onCancel();
	}

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// Project Info
	private HTMLPanel content = new HTMLPanel("");
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonCustomListBox domainLB = new AonCustomListBox("Empresa (Dominio)");
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private Integer scopeId;
	
	private List<Domain> domains = new ArrayList<>();
	
	public AonDomainScopePanel(String domainName, int domain, String user, Integer scopeId, AonDomainScopePanelCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		this.scopeId = scopeId;
		
		this.getElement().getStyle().setProperty("min-width", "35rem");
		
		setCaption("Nueva Empresa");
		
		getDomains(domains -> {
			show(callback);
		});
		
	}
	
	public void show(AonDomainScopePanelCallback callback) {
		content.setStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("padding", "1rem 0");
		
		HTMLPanel container = new HTMLPanel("");
		container.setStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "1rem");
		
		content.add(messagePanel);
		
		domainLB.clearItems();
		domainLB.addItem("-", "");
		domains.forEach(domain -> domainLB.addItem(domain.getDescription(), domain.getId().toString()));
		
		container.add(createRowPanel(domainLB, null));
		
		content.add(container);
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
		Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
    		
    		if(AonStringUtils.isBlank(domainLB.getValue())) {
    			okButton.setEnabled(true);
    			AonMessagePanel.showWarning(messagePanel, "El campo empresa es obligatoria");
    		} else {
    			
    			commonService.saveDomainScope(domainName, domainId, user, Integer.parseInt(domainLB.getValue()), scopeId, new AsyncCallback<Void>() {

    				@Override
    				public void onSuccess(Void result) {
    					hide();
    					callback.onAccept(result);
    				}
    				@Override
    				public void onFailure(Throwable caught) {
    					AonMessagePanel.showError(messagePanel, caught.getMessage());
    					okButton.setEnabled(true);
    				}
    			});
    			
    		}
    		
    	});
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addClickHandler(e -> {
    		cancelButton.setEnabled(false);
    		hide();
			callback.onCancel();
    	});
    	buttons.add(cancelButton);
    	
		content.add(buttons);
		setWidget(content);
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				center();
				show();
				center();
			}
		});
		
	}
	
	private HTMLPanel createRowPanel(Widget w1, Widget w2) {
		HTMLPanel row = new HTMLPanel("");
		row.addStyleName(AON.CSS.aonItemFlex());
		
		row.add(w1);
		
		if(null != w2) row.add(w2);
		
		return row;
	}
	
	private void getDomains(Consumer<List<Domain>> success) {
		commonService.getDomains(domainName, domainId, user, new AsyncCallback<List<Domain>>() {
			
			@Override
			public void onSuccess(List<Domain> domainsDb) {
				domains.clear();
				domains.addAll(domainsDb);
				success.accept(domains);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obtenci\u00f3n dominios. " + caught.getMessage());
			}
			
		});
		
	}

}

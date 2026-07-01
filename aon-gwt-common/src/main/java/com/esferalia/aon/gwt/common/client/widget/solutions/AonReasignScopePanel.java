package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonReasignScopePanel extends AonCustomDialog {
	
	public static interface AonReasignScopePanelCallback {
		void onAccept();
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
	
	private AonCustomListBox originScope = new AonCustomListBox("Sustituir \u00c1mbito");
	private AonCustomListBox finalScope = new AonCustomListBox("Por \u00c1mbito");
	private AonCustomToogleButton deleteOriginScope = new AonCustomToogleButton("Borrar \u00c1mbito sustituido");
	
	private ArrayList<Scope> scopeList;
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	public AonReasignScopePanel(String domainName, int domain, String user, ArrayList<Scope> scopeList, AonReasignScopePanelCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.scopeList = scopeList;
		
		this.getElement().getStyle().setProperty("min-width", "35rem");
		
		setCaption("Reasignar \u00c1mbitos");
		
		show(new Scope(), callback);
		
	}
	
	public void show(Scope scope, AonReasignScopePanelCallback callback) {
		content.setStyleName(AON.CSS.aonFlexColumn2());
		content.getElement().getStyle().setProperty("padding", "1rem 0");
		
		HTMLPanel container = new HTMLPanel("");
		container.setStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("padding", "1rem");
		
		content.add(messagePanel);
		
		originScope.clearItems();
		originScope.addItem("-", "");
		this.scopeList.forEach(s -> originScope.addItem(s.getDomain().equals(domainId) ? ("(*) " + s.getDescription()) : s.getDescription(), s.getId().toString()));
		
		finalScope.clearItems();
		finalScope.addItem("-", "");
		this.scopeList.stream().filter(s -> s.getDomain().equals(domainId)).forEach(s -> finalScope.addItem(s.getDescription(), s.getId().toString()));
		
		container.add(createRowPanel(originScope, finalScope, deleteOriginScope));
		
		content.add(container);
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
		Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
    		
    		if(AonStringUtils.isBlank(originScope.getValue()) || AonStringUtils.isBlank(finalScope.getValue())) {
    			AonMessagePanel.showError(messagePanel, "Se debe seleccionar el origen y el destino del \u00c1mbito");
				okButton.setEnabled(true);
    		} else {
        		commonService.reassignScope(domainName, domainId, user, Integer.parseInt(originScope.getValue()), Integer.parseInt(finalScope.getValue()), deleteOriginScope.getValue(), new AsyncCallback<Void>() {

    				@Override
    				public void onSuccess(Void end) {
    					hide();
    					callback.onAccept();
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
	
	private HTMLPanel createRowPanel(Widget ...ws) {
		HTMLPanel row = new HTMLPanel("");
		row.addStyleName(AON.CSS.aonItemFlex());
		
		for(int i=0; i < ws.length; i++)
			row.add(ws[i]);
		
		return row;
	}

}

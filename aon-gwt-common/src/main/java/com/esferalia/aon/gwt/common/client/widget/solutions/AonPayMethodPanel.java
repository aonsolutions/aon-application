package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class AonPayMethodPanel extends SimplePanel {
	
	public static interface AonPayMethodPanelCallback {
		void onAccept(PayMethod payMethod);
		void onCancel();
	}

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// Seller Info
	private AonCustomTextBox name = new AonCustomTextBox("Nombre");
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	public AonPayMethodPanel(final String domainName,final int domain, final String user, PayMethod payMethod, final AonPayMethodPanelCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		if(payMethod.getId() == null) payMethod.setDomain(domainId);
		show(payMethod, callback);
	}
	
	public void show(PayMethod payMethod, AonPayMethodPanelCallback callback) {
		getElement().getStyle().setProperty("padding", "1rem 0");
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonFlexColumnBetween());
		
		HTMLPanel errorPanel = new HTMLPanel("");
		errorPanel.addStyleName(AON.CSS.aonMarginTop());
		rootPanel.add(errorPanel);
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.CSS.aonScrollArea());
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			}
		};

		FlexTable table1 = new FlexTable();
		table1.setStyleName(AON.CSS.aonTable());
		table1.setWidth("100%");
		
		name.setWidth("15rem");
		table1.setWidget(0, 0, name);
		
		type.clearItems();
		for (PayMethodType pmt : PayMethodType.values())
			type.addItem( pmt.getDescription(), pmt.value() + "");	
		table1.setWidget(0, 1, type);
		
		tablePanel.add( table1 );
		
		rootPanel.add( tablePanel );
		
		if(payMethod.getId() != null) {
			name.setValue(payMethod.getName());
			type.setValue(payMethod.getType().ordinal() + "");
		}
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler( keyUpHandler);
    	
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				
				if(AonStringUtils.isBlank(name.getValue())) {
					AonMessagePanel.showError(errorPanel, "El nombre es obligatorio");
					okButton.setEnabled(true);
				} else {
					payMethod.setName(name.getValue());
					payMethod.setType(PayMethodType.values()[Integer.parseInt(type.getValue())]);
					
					commonService.savePayMethod(domainName, domainId, user, payMethod, new AsyncCallback<PayMethod>() {

						@Override
						public void onSuccess(PayMethod payMethodDB) {
							callback.onAccept(payMethodDB);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(errorPanel, caught.getMessage());
							okButton.setEnabled(true);
						}
					});
				}
			}
		});
    	
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addKeyUpHandler( keyUpHandler);
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				callback.onCancel();
			}
		});
    	buttons.add(cancelButton);
    	rootPanel.add(buttons);
		setWidget(rootPanel);	
		
	}

	public void setNameFocus() {
		name.setFocus(true);
	}

}

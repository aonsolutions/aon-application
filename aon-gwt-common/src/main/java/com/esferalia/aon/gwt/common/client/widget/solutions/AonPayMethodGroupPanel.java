package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.occam.api.model.PayMethodParams;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class AonPayMethodGroupPanel extends SimplePanel {
	
	public static interface AonPayMethodGroupPanelCallback {
		void onAccept();
		void onCancel();
		void endView(AonPayMethodGroupPanel panel);
	}

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// Seller Info
	private AonCustomListBox payMethodLB = new AonCustomListBox("Forma de Pago");
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	public AonPayMethodGroupPanel(final String domainName,final int domain, final String user, List<PayMethod> selectedPaymethodList, final AonPayMethodGroupPanelCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		PayMethodParams params = new PayMethodParams()
		.setDomainName(domainName)
		.setDomain(domain)
		.setUser(user)
		.setOrderBy("name")
		.setAsc(true)
		.setOffset(0)
		.setLimit(Integer.MAX_VALUE)
		;
		
		commonService.getPayMethods(params, new AsyncCallback<LinkedList<PayMethod>>() {
			
			@Override
			public void onSuccess(LinkedList<PayMethod> aviablePayMethods) {
				show(aviablePayMethods, selectedPaymethodList, callback);
			}

			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
		
	}
	
	public void show(LinkedList<PayMethod> aviablePayMethods, List<PayMethod> selectedPaymethodList, AonPayMethodGroupPanelCallback callback) {
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
		
		Label message = new Label("Se van a agrupar " + selectedPaymethodList.size() + " en una unica forma de pago. Seleccione a continuacion la forma de pago que quiera que agrupe al resto:");
		message.setWidth("26rem");
		message.getElement().getStyle().setProperty("margin-bottom", "1rem");
		table1.setWidget(0, 0, message);
		
		aviablePayMethods.forEach(aviablePayMethod -> payMethodLB.addItem(aviablePayMethod.getName() + " (" + (null == aviablePayMethod.getType() ? "N/D" : aviablePayMethod.getType().getDescription()) + ")", aviablePayMethod.getId().toString()));
		table1.setWidget(1, 0, payMethodLB);
		
		tablePanel.add( table1 );
		
		rootPanel.add( tablePanel );
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler( keyUpHandler);
    	
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				okButton.setEnabled(false);
				
				Optional<PayMethod> selectPayMethod = aviablePayMethods.stream().filter(aviablePayMethod -> aviablePayMethod.getId().equals(Integer.parseInt(payMethodLB.getValue())) || aviablePayMethod.getId() == Integer.parseInt(payMethodLB.getValue())).findFirst();
				
				AonDialog dialog = new AonDialog("Agrupaci\u00f3n Forma de Pago",
						new HTML("Se va a proceder a agrupar <b>" + selectedPaymethodList.size() + "</b> formas de pago. Se van a actualizar todas las referencias de estas formas de pago, en la forma seleccionada <b>" + selectPayMethod.get().getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la actualizaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						okButton.setEnabled(true);
					}

					@Override
					public void onAccept() {
						commonService.groupPayMethod(domainName, domainId, user, selectedPaymethodList, selectPayMethod.get(), new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								callback.onAccept();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								AonMessagePanel.showError(errorPanel, caught.getMessage());
								okButton.setEnabled(true);
							}
						});
					}
				});
				
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
		
		long distinctTypes = selectedPaymethodList.stream()
                .map(PayMethod::getType)
                .distinct()
                .count();
		
		if(distinctTypes > 1) AonMessagePanel.showWarning(errorPanel, "Aviso. Hay distintas formas de pago seleccionadas para agrupar.");
		
		
		callback.endView(this);
		
	}

}

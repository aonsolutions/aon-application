package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCertificateListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonInvoiceSeriesListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;

class InvoiceFeeInvoicingPanel extends SimplePanel implements Focusable {
	interface InvoiceFeeInvoicingPanelCallback {
		void onAccept( FeeBillingParams params );
		void onCancel();
	}
	
	private FlowPanel messagePanel = new FlowPanel();
	private AonInvoiceSeriesListBox seriesBox;
	private AonDateBox issueDateBox;
	private TextArea commentsBox;
	private CheckBox communicateBox = new CheckBox(AON.MSG.communicateInvoices());
	private CheckBox saveAsProformaBox = new CheckBox(AON.MSG.saveAsProformas());
	private AonCertificateListBox certificateBox;
	
	InvoiceFeeInvoicingPanel( InvoiceModuleOptions opts, FeeBillingParams params ,InvoiceFeeInvoicingPanelCallback callback) {
		
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName(AON.CSS.aonPadding());
		setWidget( mainPanel );
		
		mainPanel.add(messagePanel);

		seriesBox = new AonInvoiceSeriesListBox(opts);
		seriesBox.addSelectionHandler(e -> 
			params.setInvoiceSeries(seriesBox.getSeries().orElse(null) ));
		
		issueDateBox = new AonDateBox();
		issueDateBox.addValueChangeHandler(e -> params.setInvoiceDate(issueDateBox.getValue()));
		
		commentsBox = new TextArea();
		commentsBox.addValueChangeHandler(e -> params.setInvoiceComments(commentsBox.getValue()));
		
		if (hasCommunication(opts )) {
			certificateBox = new AonCertificateListBox(opts);
			certificateBox.addSelectionHandler(e -> params.setCertId(certificateBox.getCertificateId().orElse(null)));
			
			communicateBox.getElement().getStyle().setProperty("align-items", "baseline");
			communicateBox.setValue(true);
			params.setCommunicable( true );
			
			communicateBox.addValueChangeHandler(e -> {
				params.setCommunicable( communicateBox.getValue() );
				certificateBox.setEnabled( communicateBox.getValue() );
				saveAsProformaBox.setEnabled( !communicateBox.getValue() );
			});
		} else {
			saveAsProformaBox.getElement().getStyle().setProperty("align-items", "baseline");
			saveAsProformaBox.setValue(false);
			params.setSaveAsProforma( false);
			saveAsProformaBox.addValueChangeHandler(e -> params.setSaveAsProforma( saveAsProformaBox.getValue() ));
		}
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addLabelWidgetRow(AON.MSG.series(), seriesBox);
		tab.addLabelWidgetRow(AON.MSG.issueDate(), issueDateBox);
		tab.addLabelWidgetRow(AON.MSG.comments(), commentsBox);
		if (hasCommunication(opts )) {
			tab.addLabelWidgetRow("", communicateBox);
			tab.addLabelWidgetRow(AON.MSG.certificate(), certificateBox);
		} else {
			tab.addLabelWidgetRow("", saveAsProformaBox);
		}
		mainPanel.add(tab);
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonBlockCenter());
		buttonsPanel.setStyleName(AON.CSS.aonTextCenter());
		buttonsPanel.addStyleName(AON.CSS.aonPaddingTop());

    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(event -> {
			okButton.setEnabled(false);
			if (validate( opts, params )) {
				callback.onAccept( params );
			} else {
				okButton.setEnabled(true);	
			}
		});
    	buttonsPanel.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addClickHandler(event -> {
			cancelButton.setEnabled(false);
			callback.onCancel();
		});
    	buttonsPanel.add(cancelButton);
    	mainPanel.add(buttonsPanel);
	}

	private boolean hasCommunication(InvoiceModuleOptions opts) {
		return (opts != null 
			&& opts.getConfiguration() != null 
			&& opts.getConfiguration().getCommunicationConfig() != null
			&& opts.getConfiguration().getCommunicationConfig().hasCommunication());
	}

	private boolean validate(InvoiceModuleOptions opts, FeeBillingParams params) {
		if (AonStringUtils.isBlank(params.getInvoiceSeries())) {
			AonMessagePanel.showError(messagePanel, "La series de facturaci\u00F3 no puede estar vac\u00EDa.");
			seriesBox.setFocus(true);
			return false;
		}
		if (params.getInvoiceDate() == null) {
			AonMessagePanel.showError(messagePanel, "La fecha de emisi\u00F3 de la factura no puede estar vac\u00EDa.");
			issueDateBox.setFocus(true);
			return false;
		}
		if (hasCommunication( opts ) &&  communicateBox.getValue().booleanValue() && params.getCertId() == null) {
			AonMessagePanel.showError(messagePanel, "El certificado no puede estar vac\u00EDo.");
			certificateBox.setFocus(true);
			return false;
		}
		
		return true;
	}

	@Override
	public int getTabIndex() {
		return seriesBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		seriesBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focus) {
		seriesBox.setFocus(focus);
	}

	@Override
	public void setTabIndex(int index) {
		seriesBox.setTabIndex(index);
	}

}

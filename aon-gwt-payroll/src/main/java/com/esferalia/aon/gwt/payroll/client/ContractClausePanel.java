package com.esferalia.aon.gwt.payroll.client;

import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextArea;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.payroll.shared.ContractClause;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class ContractClausePanel extends SimplePanel {
	
	public static interface AonContractClausePanelCallback {
		void onAccept();
		void onCancel();
	}

	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();

	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	
	private AonCustomTextBox line = new AonCustomTextBox("Linea");
	private AonCustomTextBox name = new AonCustomTextBox("Nombre");
	private AonCustomTextArea description = new AonCustomTextArea("Descripci\u00f3n");
	
	public ContractClausePanel(Integer domain, Integer contractId, Integer curentLine, final AonContractClausePanelCallback callback) {
		Short newLine = (short) (curentLine + 1);
		show(new ContractClause().setDomain(domain).setContract(contractId).setLineNumber(newLine), callback);
	}

	public ContractClausePanel(ContractClause contractClause, final AonContractClausePanelCallback callback) {
		show(contractClause, callback);
	}
	
	private void show(ContractClause contractClause, AonContractClausePanelCallback callback) {
		getElement().getStyle().setProperty("padding", "1rem");
		getElement().getStyle().setProperty("min-width", "35rem");
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonFlexColumnBetween());
		
		rootPanel.add(messagePanel);
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			}
		};
		
		line.setValue(contractClause.getLineNumber().toString());
		name.setValue(contractClause.getName());
		description.setValue(contractClause.getDescription());
		
		rootPanel.add(line);
		rootPanel.add(name);
		rootPanel.add(description);
		
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
				
				if(AonStringUtils.isBlank(line.getValue())) {
					okButton.setEnabled(true);
					AonMessagePanel.showError(messagePanel, "La linea no esta definida para esta clausula");
				} else {
					contractClause.setLineNumber(Short.parseShort(line.getValue()));
					contractClause.setName(name.getValue());
					contractClause.setDescription(description.getValue());
					
					saveContractClause(contractClause, 
						saved -> callback.onAccept(),
						failure -> {
							okButton.setEnabled(true);
							AonMessagePanel.showError(messagePanel, failure.getMessage());
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
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	name.setFocus(true);
	        }
	    });	
	}
	
	private void saveContractClause(ContractClause contractClause, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.saveContractClause(contractClause, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void finish) {
				success.accept(finish);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void focusName() {
		name.setFocus(true);
	}

}

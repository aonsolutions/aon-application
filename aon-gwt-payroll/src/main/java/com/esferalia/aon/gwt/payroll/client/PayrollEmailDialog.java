package com.esferalia.aon.gwt.payroll.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.richtexttoolbar.RichTextToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class PayrollEmailDialog extends AonCustomDialog {
	
	// ------------------------------------------------------- UiBinder

	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();

	interface PayrollEmailDialogUiBinder extends UiBinder<Widget, PayrollEmailDialog> {}

	private static final PayrollEmailDialogUiBinder binder = GWT.create(PayrollEmailDialogUiBinder.class);
	
	// ------------------------------------------------------- Type
	
	public enum Type implements Serializable {
		ENTERPRISE,
		ENTERPRISE_MANAGEMENT,
		EMPLOYEE
		;
	}
	
	// ------------------------------------------------------- UiFields
	
	@UiField
	ListBox mailAccountsListBox;
	
	@UiField
	TextBox sendTo;
	
	@UiField
	Label sendToMessage;
	
	@UiField
	Label sendToEnterpriseManagmentMessage;
	
	@UiField
	TextBox cc;
	
	@UiField
	TextBox cco;
	
	@UiField
	HTMLPanel textAreaPanel;
	
	@UiField
	HTMLPanel messageVariablesPanel;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------------------------- Variables
	
	private List<MailAccount> mailAccounts = new ArrayList<MailAccount>();
	private RichTextArea richTextArea = null;
	
	private Button closeBtnDialog;
	private Button acceptBtnDialog;
	
	// ------------------------------------------------------- Constructor
	
	public PayrollEmailDialog(Type type, HashMap<String, String> params) {
		
		setCaption("Envio n\u00F3minas por email");
		
		setWidget(binder.createAndBindUi(this));
		
		getButtonsPanel();
		
		switch (type) {
			case EMPLOYEE:
				sendTo.setVisible(false);
				sendToMessage.setVisible(true);
				sendToEnterpriseManagmentMessage.setVisible(false);
				messageVariablesPanel.setVisible(true);
				
				ArrayList<Integer> salaryIds = new ArrayList<Integer>();
				for(Entry<String, String> entry : params.entrySet())
					if(AonStringUtils.startsWithIgnoreCase(entry.getKey(), "id"))
						salaryIds.add(Integer.valueOf(entry.getValue()));
				
				impl.checkEmployeesEmails(salaryIds, new AsyncCallback<String>() {
					
					@Override
					public void onSuccess(String message) {
						if(AonStringUtils.isNotBlank(message)) {
							AonDialog dialog = new AonDialog("REVISAR EMAILS", new HTML(message));
							dialog.warning();
							return;
						}
						loadInfo(type, params);
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
				break;
			case ENTERPRISE:
				sendTo.setVisible(true);
				sendToMessage.setVisible(false);
				sendToEnterpriseManagmentMessage.setVisible(false);
				messageVariablesPanel.setVisible(false);
				loadInfo(type, params);
				break;
			case ENTERPRISE_MANAGEMENT:
				sendTo.setVisible(false);
				sendToMessage.setVisible(false);
				sendToEnterpriseManagmentMessage.setVisible(true);
				messageVariablesPanel.setVisible(true);
				
				HashSet<Integer> enterpriseIds = new HashSet<Integer>();
				for(Entry<String, String> entry : params.entrySet())
					if(AonStringUtils.containsIgnoreCase(entry.getKey(), "enterpriseId"))
						enterpriseIds.add(Integer.valueOf(entry.getValue()));
				
				impl.checkEnterprisesEmails(enterpriseIds, new AsyncCallback<String>() {
					
					@Override
					public void onSuccess(String message) {
						if(AonStringUtils.isNotBlank(message)) {
							AonDialog dialog = new AonDialog("REVISAR EMAILS", new HTML(message));
							dialog.warning();
							return;
						}
						loadInfo(type, params);
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
				
				break;
			default:
				break;
		}
	}

	private void loadInfo(Type type, HashMap<String, String> params) {
		impl.getDomainMailAccounts(new AsyncCallback<List<MailAccount>>() {
			
			@Override
			public void onSuccess(List<MailAccount> result) {
				mailAccounts = result;
				
				if(mailAccounts.isEmpty()) {
					AonDialog dialog = new AonDialog("ERROR", new HTML("No hay cuentas de correo desde las que enviar el email."));
					dialog.warning();
					return;
				}
				
				for(MailAccount mailAccount : mailAccounts) {
					mailAccountsListBox.addItem(mailAccount.getName() + " - ( " + mailAccount.getEmail() + " )");
				}
				
				impl.getPayrollEmailSendTo(new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) { }

					@Override
					public void onSuccess(String sendToEmail) {
						sendTo.setText(sendToEmail);
						
						impl.getPayrollEmailBody(type, params, new AsyncCallback<String>() {

							@Override
							public void onFailure(Throwable caught) { }

							@Override
							public void onSuccess(String emailBody) {
								initView(emailBody);
								center();
								show();
							}
						});
					}
				});
				
			}
			
			@Override
			public void onFailure(Throwable caught) {}
			
		});
	}

	// ------------------------------------------------------- Initialize view
	
	private void initView(String emailBody) {
		richTextArea = new RichTextArea();
		richTextArea.setWidth("650px");
		richTextArea.setHeight("345px");
		richTextArea.getElement().getStyle().setBorderWidth(1, Unit.PX);
		richTextArea.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		richTextArea.getElement().getStyle().setBorderColor("#a0a0a0");
		
		richTextArea.setHTML(emailBody);
		
		RichTextToolbar toolbar = new RichTextToolbar(richTextArea);
		toolbar.setWidth("650px");
		
		textAreaPanel.add(toolbar);
		textAreaPanel.add(richTextArea);
	}
	
	// ------------------------------------------------------- Getters

	public MailAccount getFromMAilAccount() {
		return this.mailAccounts.get(this.mailAccountsListBox.getSelectedIndex());
	}
	
	public String getSendTo() {
		return this.sendTo.getText();
	}
	
	public String getCC(){
		if(this.cc.getText().length() == 0)
			return null;
		
		return this.cc.getText();
	}
	
	public String getCCO() {
		if(this.cco.getText().length() == 0)
			return null;
		
		return this.cco.getText();
	}
	
	public String getBody() {
		return this.richTextArea.getHTML();
	}
	
	// ------------------------------------------------------- Buttons panel
	
	private void getButtonsPanel() {
		closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.addClickHandler(e -> {
			onCloseDialog(e);
		});
		
		buttonsPanel.add(closeBtnDialog);
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> {
			onAcceptDialog(e);
		});
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private void onCloseDialog(ClickEvent event) {
		hide();
	}
	
	private void onAcceptDialog(ClickEvent event) {
		hide();
		onAccept();
		
	}
	
	// ------------------------------------------------------- Abstract methods
	
	protected abstract void onAccept();
	
}

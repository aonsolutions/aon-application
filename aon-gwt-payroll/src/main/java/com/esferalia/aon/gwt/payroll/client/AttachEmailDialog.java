package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.richtexttoolbar.RichTextToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AttachEmailDialog extends AonCustomDialog {
	
	// ------------------------------------------------------- UiBinder

	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();

	interface AttachEmailDialogUiBinder extends UiBinder<Widget, AttachEmailDialog> {}

	private static final AttachEmailDialogUiBinder binder = GWT.create(AttachEmailDialogUiBinder.class);
	
	// ------------------------------------------------------- UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String sendButton();
	}
	
	@UiField
	ListBox mailAccountsListBox;
	
	@UiField
	TextBox sendTo;
	
	@UiField
	TextBox ccTo;
	
	@UiField
	TextBox bccTo;
	
	@UiField
	TextBox subject;
	
	@UiField
	HTMLPanel textAreaPanel;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------------------------- Variables
	
	private List<MailAccount> mailAccounts = new ArrayList<>();
	private RichTextArea richTextArea = null;
	
	// ------------------------------------------------------- Constructor
	
	protected AttachEmailDialog() {
		
		setCaption("Email documentos");
		setWidget(binder.createAndBindUi(this));
		showCloseButton(true);
		getButtonsPanel();
		createRichTextArea();
		loadEmailInfo();
	}

	private void loadEmailInfo() {
		impl.getDomainMailAccounts(new AsyncCallback<List<MailAccount>>() {
			
			@Override
			public void onSuccess(List<MailAccount> result) {
				mailAccounts = result;
				mailAccounts.forEach(mail -> mailAccountsListBox.addItem(mail.getName() + " - ( " + mail.getEmail() + " )"));
				
				impl.getPayrollEmailSendTo(new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						// Nothing to do here
					}

					@Override
					public void onSuccess(String sendToEmail) {
						sendTo.setValue(sendToEmail);
						subject.setValue("Documentos");
						showDialog();
					}
				});
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Nothing to do here
			}
			
		});
	}

	// ------------------------------------------------------- Initialize view
	
	private void createRichTextArea() {
		richTextArea = new RichTextArea();
		richTextArea.setWidth("650px");
		richTextArea.setHeight("345px");
		richTextArea.getElement().getStyle().setBorderWidth(1, Unit.PX);
		richTextArea.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		richTextArea.getElement().getStyle().setBorderColor("#a0a0a0");
		richTextArea.setHTML(
			"<p>Estimado cliente,</p>"
			+ "<p>Adjuntamos la siguiente documentaci\u00f3n relativa al \u00e1rea laboral.</p>"
			+ "<p>Quedamos a su disposici\u00f3n para cualquier aclaraci\u00f3n.<p><br>"
		);
		
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
		return this.sendTo.getValue();
	}
	
	public List<String> getCCTo() {
		List<String> ccs = new ArrayList<>();
		
		if(AonStringUtils.containsIgnoreCase(ccTo.getValue(), ",")) {
			String[] ccsSplit = ccTo.getValue().split(",");
			for(int i=0; i<ccsSplit.length; i++) ccs.add(ccsSplit[i].trim());
		} else if(AonStringUtils.isNotBlank(ccTo.getValue()))
			ccs.add(ccTo.getValue().trim());
		
		return ccs;
	}
	
	public List<String> getBCCTo() {
		MailAccount mailAccount = this.mailAccounts.get(this.mailAccountsListBox.getSelectedIndex());
		
		List<String> bccs = new ArrayList<>();
		bccs.add(mailAccount.getEmail());
		
		if(AonStringUtils.containsIgnoreCase(bccTo.getValue(), ",")) {
			String[] bccsSplit = bccTo.getValue().split(",");
			for(int i=0; i<bccsSplit.length; i++) bccs.add(bccsSplit[i].trim());
		} else if(AonStringUtils.isNotBlank(bccTo.getValue()))
			bccs.add(bccTo.getValue().trim());
		
		return bccs;
	}
	
	public String getSubject() {
		return this.subject.getValue();
	}
	
	public String getBody() {
		return this.richTextArea.getHTML();
	}
	
	// ------------------------------------------------------- Buttons panel
	
	private void getButtonsPanel() {
		Button sendBtn = new Button();
		sendBtn.setStyleName(style.sendButton());
		sendBtn.addStyleName(AON.CSS.aonIconSend());
		sendBtn.setText("Enviar email");
		sendBtn.addClickHandler(e -> onSend());
		
		buttonsPanel.add(sendBtn);
	}
	
	private void onSend() {
		hide();
		onSendEmail();	
	}
	
	// ------------------------------------------------------- Show dialog
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
	// ------------------------------------------------------- Abstract methods
	
	protected abstract void onSendEmail();
	
}

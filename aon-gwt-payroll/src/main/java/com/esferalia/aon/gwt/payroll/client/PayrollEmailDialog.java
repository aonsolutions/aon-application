package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.richtexttoolbar.RichTextToolbar;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

public abstract class PayrollEmailDialog extends CustomDialog {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();

	interface Binder extends UiBinder<Widget, PayrollEmailDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String w96();
	}
	
	@UiField
	ListBox mailAccountsListBox;
	
	@UiField
	TextBox sendTo;
	
	@UiField
	HTMLPanel textAreaPanel;
	
	@UiField
	Button acceptButton;
	
	@UiField
	Button cancelButton;
	
	private List<MailAccount> mailAccounts = new ArrayList<MailAccount>();
	private RichTextArea richTextArea = null;
	
	public PayrollEmailDialog(Integer enterpriseID, String paramsBase64) {
		
		setCaption("Envio nominas por email");
		
		setWidget(binder.createAndBindUi(this));
		
		sendTo.addStyleName(style.w96());
		
		impl.getDomainMailAccounts(new AsyncCallback<List<MailAccount>>() {
			
			@Override
			public void onSuccess(List<MailAccount> result) {
				mailAccounts = result;
				
				for(MailAccount mailAccount : mailAccounts) {
					mailAccountsListBox.addItem(mailAccount.getName() + " - ( " + mailAccount.getEmail() + " )");
				}
				
				impl.getPayrollEmailSendTo(enterpriseID, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) { }

					@Override
					public void onSuccess(String sendToEmail) {
						sendTo.setText(null == sendToEmail ? "" : sendToEmail);
						
						impl.getPayrollEmailBody(paramsBase64, new AsyncCallback<String>() {

							@Override
							public void onFailure(Throwable caught) { }

							@Override
							public void onSuccess(String emailBody) {
								initView(emailBody);
							}
						});
					}
				});
				
			}
			
			@Override
			public void onFailure(Throwable caught) { }
			
		});
		
		
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onAccept();
			}
		});		
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
	}

	private void initView(String emailBody) {
		richTextArea = new RichTextArea();
		richTextArea.setWidth("650px");
		richTextArea.setHeight("500px");
		
		richTextArea.setHTML(emailBody);
		
		RichTextToolbar toolbar = new RichTextToolbar(richTextArea);
		toolbar.setWidth("650px");
		
		textAreaPanel.add(toolbar);
		textAreaPanel.add(richTextArea);
	}

	protected abstract void onAccept();
	
	public MailAccount getFromMAilAccount() {
		if(this.mailAccountsListBox.getItemCount() == 0)
			return null;
		
		return this.mailAccounts.get(this.mailAccountsListBox.getSelectedIndex());
	}
	
	public String getSendTo() {
		return this.sendTo.getText();
	}
	
	public String getBody() {
		return this.richTextArea.getHTML();
	}
}

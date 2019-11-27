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

public abstract class PayrollEmailToEmployeesDialog extends CustomDialog {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();

	interface Binder extends UiBinder<Widget, PayrollEmailToEmployeesDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String w96();
	}
	
	@UiField
	ListBox mailAccountsListBox;
	
	@UiField
	TextBox cc;
	
	@UiField
	TextBox cco;
	
	@UiField
	HTMLPanel textAreaPanel;
	
	@UiField
	Button acceptButton;
	
	@UiField
	Button cancelButton;
	
	private List<MailAccount> mailAccounts = new ArrayList<MailAccount>();
	private RichTextArea richTextArea = null;
	
	public PayrollEmailToEmployeesDialog(Integer enterpriseID, String completeURL) {
		
		setCaption("Envio nominas por email");
		
		setWidget(binder.createAndBindUi(this));
		
		cc.addStyleName(style.w96());
		cco.addStyleName(style.w96());
		
		impl.getDomainMailAccounts(new AsyncCallback<List<MailAccount>>() {
			
			@Override
			public void onSuccess(List<MailAccount> result) {
				mailAccounts = result;
				
				for(MailAccount mailAccount : mailAccounts) {
					mailAccountsListBox.addItem(mailAccount.getName() + " - ( " + mailAccount.getEmail() + " )");
				}
				
				initView();
				
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

	private void initView() {
		richTextArea = new RichTextArea();
		richTextArea.setWidth("730px");
		richTextArea.setHeight("380px");
		
		richTextArea.setHTML(getDefaultMessage());
		
		RichTextToolbar toolbar = new RichTextToolbar(richTextArea);
		toolbar.setWidth("730px");
		
		textAreaPanel.add(toolbar);
		textAreaPanel.add(richTextArea);
	}

	protected abstract void onAccept();
	
	public MailAccount getFromMAilAccount() {
		if(this.mailAccountsListBox.getItemCount() == 0)
			return null;
		
		return this.mailAccounts.get(this.mailAccountsListBox.getSelectedIndex());
	}
	
	public String getBody() {
		return this.richTextArea.getHTML();
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
	
	public String getDefaultMessage() {
		String html = "";
		
		html += "<div style=\"font-family: \"Lucida Sans Unicode\", \"Lucida Grande\", sans-serif;font-size: 12px;letter-spacing: 2px;word-spacing: 0px;color: #000000;font-weight: normal;text-decoration: none;font-style: normal;font-variant: normal;text-transform: none;\">";
		html += 	"<p>Estimado NOMBRE_EMPLEADO :</p>";
		html += 	"<p>Le adjuntamos las n&oacute;minas que corresponden a los siguientes periodos :</p>";
		html += 	"<ul>";
		
		html += 		"<li>PERIODOS_NOMINA</li>";

		html += 	"</ul>";
		html += 	"<p>Para descargar y visualizar el documento adjunto, por favor haga click en el siguiente enlace:</p>";
		
		html += 	"<div style=\"width:200px;border: 1px solid gray;text-align:center;\">";
		html +=			"<a type=\"button\" href=\"URL_DOWNLOAD\" style=\"text-decoration:none;padding:5px;text-align:center;color: #153643;\">";
		html +=				"<img src=\"http://simpleicon.com/wp-content/uploads/cloud-download-2.png\" style=\"width:20px;vertical-align: middle;\" />";
		html +=				"<b style=\"color: black;padding-left: 4px;font-size: x-small;\">DESCARGAR NOMINAS</b>";
		html +=			"</a>";
		html += 	"</div>";

		html += 	"<p>Este archivo est&aacute; en formato PDF Adobe y se puede leer usando Acrobat Reader. Si no tiene instalado el Acrobat Reader pulse aqu&iacute; para conseguir su copia gratuita: http://get.adobe.com/es/reader. Para cualquier aclaraci&oacute;n sobre el documento adjunto p&oacute;ngase en contacto con nosotros.</p>";
		html += 	"<p>AON SOLUTIONS, S.L.<br/> Tel&eacute;fono: 902121009<br/> Fax: 945121011<br/> <a style=\"text-decoration: none; color: black;\" href=\"www.aonsolutions.es\">www.aonsolutions.es</a></p>";
		html += "</div>";
		
		return html;
	}
}

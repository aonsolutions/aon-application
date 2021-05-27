package com.esferalia.aon.gwt.common.client.widget;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.richtexttoolbar.RichTextToolbar;
import com.esferalia.aon.occam.api.model.Contact;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class SendEmailDialog extends CustomDialogB  {

	interface Binder extends UiBinder<Widget, SendEmailDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	


	@UiField(provided = true) FlexTable textEditor;
	@UiField(provided = true) FlexTable grid;
//	@UiField Button sendMailButton;
	//@UiField Button sendGmailButton;
	@UiField HorizontalPanel header;
	@UiField(provided = true) VerticalPanel vp;
	@UiField(provided = true) AbsolutePanel mailpanel;
	MailAccount ma;

	public SendEmailDialog(LinkedList<MailAccount> mailAccountList, LinkedList<Contact> contactList , Boolean isGoogle) {
		vp = new VerticalPanel();
		grid = new FlexTable();
		textEditor = new FlexTable();
		if(mailAccountList.size()==0){
			mailpanel = new AbsolutePanel();
			mailpanel.setVisible(false);
			mailpanel.setStyleName("aon-menuItem-no-iconClass");
			grid.setVisible(false);
			textEditor.setVisible(false);
			setCaption("ERROR");
			Label label = new Label("*Error: No tiene una cuenta de correo asociada.");
			label.getElement().getStyle().setColor("red");
			Button cancel = new Button("Aceptar");
			cancel.setStyleName("aon-check-button-send-email-dialog");
			cancel.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					onCancel();
				}
			});
			
			vp.add(label);
			vp.add(cancel);
			vp.setCellHorizontalAlignment(cancel,HasAlignment.ALIGN_RIGHT );
			setWidget(binder.createAndBindUi(this));

		}
		else{
			mailpanel = new AbsolutePanel();
			mailpanel.setVisible(true);
			setCaption("Nuevo Correo");
			table(mailAccountList, contactList);
			editor(mailAccountList);
			setWidget(binder.createAndBindUi(this));
			if(isGoogle){
				Button sendGmailButton = new Button("Enviar");
				sendGmailButton.setStyleName("aon-icon-google-gmail aon-finding-toolbar-item");
				sendGmailButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						onAccept("gmail");
					}
				});
				header.add(sendGmailButton);
			}
			Button sendMailButton = new Button("Enviar");
			sendMailButton.setStyleName("aon-icon-accept aon-finding-toolbar-item");
			sendMailButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onAccept("mail");
				}
			});
			header.add(sendMailButton);
		}
	}

	protected abstract void onAccept(String s);
	
	protected abstract void onCancel();
	
	private void editor(LinkedList<MailAccount> mailAccountList) {

			RichTextArea area = new RichTextArea();

			area.setSize("100%", "14em");
			ListBox lb = (ListBox) grid.getWidget(0,1);


			area.setHTML("<div>&nbsp;</div>"+mailAccountList.get(lb.getSelectedIndex()).getSignatureStr());

			ma = mailAccountList.get(lb.getSelectedIndex());
		    RichTextToolbar toolbar = new RichTextToolbar(area);
		    new RichTextToolbar(area);
		    toolbar.setWidth("100%");
		    textEditor.setWidth("400px");
		    textEditor.setCellSpacing(0);
		    textEditor.setWidget(0, 0, toolbar);
		    textEditor.setWidget(1, 0, area);
		    
	}
	
	private void table(final LinkedList<MailAccount> mailAccountList, LinkedList<Contact> contactList) {
		grid.setStyleName("aon-panelGrid");
		grid.setWidth("100%");
		grid.setBorderWidth(1);
		grid.setCellSpacing(0);

		ListBox lb0 = new ListBox();
		for(MailAccount ma : mailAccountList){
			lb0.addItem(ma.getName());
		}
		
		lb0.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) grid.getWidget(0,1);
				RichTextArea area = new RichTextArea();
			    area.setSize("100%", "14em");
			    area.setText("asfasf");
				area.setHTML("<div>&nbsp;</div>"+mailAccountList.get(lb.getSelectedIndex()).getSignatureStr());
				ma = mailAccountList.get(lb.getSelectedIndex());
				textEditor.setWidget(1, 0, area);
			}
		});
		grid.setWidget(0, 0, new Label("De"));
		grid.setWidget(0, 1, lb0);
		
		
		SuggestBox sb1 = new SuggestBox(createOracleContact(contactList));
		sb1.setStyleName("aon-inputText");
		sb1.setWidth("100%");
		grid.setWidget(1, 0, new Label("Para"));
		grid.setWidget(1, 1, sb1);
		
		SuggestBox sb2 = new SuggestBox(createOracleContact(contactList));
		sb2.setStyleName("aon-inputText");
		sb2.setWidth("100%");
		grid.setWidget(2, 0, new Label("Cc"));
		grid.setWidget(2, 1, sb2);
		
		SuggestBox sb3 = new SuggestBox(createOracleContact(contactList));
		sb3.setStyleName("aon-inputText");
		sb3.setWidth("100%");
		grid.setWidget(3, 0, new Label("Bcc"));
		grid.setWidget(3, 1, sb3);
		
		TextBox tb4 = new TextBox();tb4.setStyleName("aon-inputText");
		tb4.setWidth("100%");
		grid.setWidget(4, 0, new Label("Asunto"));
		grid.setWidget(4, 1, tb4);
		
		for (int i = 0; i < grid.getRowCount(); i++) {
			for (int j = 0; j < grid.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-odd");
				} else {
					grid.getCellFormatter().setStyleName(i, j,
							"aon-panelGrid-even");
				}
			}
		}
	}
	
	static AonSuggestOracle createOracleContact(LinkedList<Contact> contactList) {
		AonSuggestOracle oracleSons = new AonSuggestOracle();
		for (Contact c : contactList) {
			oracleSons.add(c.getEmail());
		}
		return oracleSons;
	}

	public FlexTable getTextEditor() {
		return textEditor;
	}

	public FlexTable getGrid() {
		return grid;
	}
	
}


package com.esferalia.aon.gwt.office.client.notification;

import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class NotificationDialog extends CustomDialogB 
{
	final INotificationAsync impl = GWT.create(INotification.class);
	interface Binder extends UiBinder<Widget, NotificationDialog>{}
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField(provided=true) protected FlexTable flex_table;
	@UiField Button accept_button;
	@UiField Button cancel_button;

	private static final String SAVE = "Guardar";
	private static final String CANCEL = "Cancelar";
	private static final String NOTIFY_CONFIGURATION = "Configurar Notificaciones";
	
	public NotificationDialog() {
		setCaption(NOTIFY_CONFIGURATION);
		flex_table = new FlexTable();
		impl.getNotificationInfo(getDomain(), new AsyncCallback<NotificationInfo>() {
			
			@Override
			public void onSuccess(NotificationInfo result) {
				buildTable(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Apéndice de método generado automáticamente
				
			}
		});
	
		
		setWidget(binder.createAndBindUi(this));

		accept_button.setText(SAVE);
		accept_button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onAccept();
			}
		});
		cancel_button.setText(CANCEL);
		cancel_button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onCancel();
			}
		});
	}
	
	protected abstract void onAccept();
	
	protected abstract void onCancel();
	
	private void buildTable(NotificationInfo ni) {
		flex_table.setStyleName("aon-panelGrid");
		flex_table.setWidth("400px");
		flex_table.setBorderWidth(1);
		flex_table.setCellSpacing(0);
		
		ListBox lb1 = new ListBox();
		lb1.addItem("-");
		for(MailAccount ma : ni.getMailAccountList()){
			if(ma.getId() != null){
				lb1.addItem(ma.getDisplayName(), ma.getId().toString());
				if(ni.getMailAccount() != null && ni.getMailAccount().getId() != null
						&& ni.getMailAccount().getId().equals(ma.getId()))
					lb1.setSelectedIndex(lb1.getItemCount()-1);
			}
		}
		lb1.setStyleName("aon-inputText");
	
		flex_table.setWidget(0, 0, new Label("Cuenta de correo"));
		flex_table.setWidget(0, 1, lb1);

		ListBox lb2 = new ListBox();
		lb2.addItem("-");
		for(Signature signature : ni.getSignatureList()){
			if(signature.getId() != null){
				lb2.addItem(signature.getName(), signature.getId().toString());
				if(ni.getSignature() != null && ni.getSignature().getId() != null
						&& ni.getSignature().getId().equals(signature.getId()))
					lb2.setSelectedIndex(lb1.getItemCount()-1);
			}
		}
		lb2.setStyleName("aon-inputText");
		flex_table.setWidget(1, 0, new Label("Firma de Correo"));
		flex_table.setWidget(1, 1, lb2);

		CheckBox cb1 = new CheckBox();
		cb1.setValue(ni.getNotify());
		flex_table.setWidget(2, 0, cb1);
		flex_table.setWidget(2, 1, new Label("Notificar autom\u00e1ticamente por correo electr\u00f3nico"));

		CheckBox cb2 = new CheckBox();
		cb2.setValue(ni.getHistory());
		flex_table.setWidget(3, 0, cb2);
		flex_table.setWidget(3, 1, new Label("Incluir historial completo en respuesta"));
		
		TextBox tb = new TextBox();
		tb.setStyleName("aon-inputText");
		tb.setValue(ni.getBcc() != null ? ni.getBcc() : "");
		flex_table.setWidget(4, 0, new Label("incluir en BCC"));
		flex_table.setWidget(4, 1, tb);
		
		flexTableCss();
	}
	
	public void flexTableCss(){
		for (int i = 0; i < flex_table.getRowCount(); i++) {
			for (int j = 0; j < flex_table.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					flex_table.getCellFormatter().setStyleName(i, j, "aon-panelGrid-odd");
				} else {
					flex_table.getCellFormatter().setStyleName(i, j, "aon-panelGrid-even");
				}
			}
		}
	}
	
	private Domain getDomain() {
		return new Domain().setId(JsNotification.getCurrentDomain()).setName(JsNotification.getCurrentDomainName());
	}
}

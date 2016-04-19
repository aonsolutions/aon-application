package com.esferalia.aon.gwt.office.client.notification;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.esferalia.aon.gwt.office.client.NumberSpinner;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class NotificationDialog extends CustomDialogB 
{
	final INotificationAsync impl = GWT.create(INotification.class);
	interface Binder extends UiBinder<Widget, NotificationDialog>{}
	private static final Binder binder = GWT.create(Binder.class);
	private static final NotificationMessages MSG = GWT.create(NotificationMessages.class);

	@UiField(provided=true) protected FlexTable flex_table;
	@UiField Button accept_button;
	@UiField Button cancel_button;

	
	public NotificationDialog() {
		setCaption(MSG.notificationConfiguration());
		flex_table = new FlexTable();
		impl.getNotificationInfo(getDomain(), new AsyncCallback<NotificationInfo>() {
			
			@Override
			public void onSuccess(NotificationInfo result) {
				buildTable(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
		
		setWidget(binder.createAndBindUi(this));

		accept_button.setText(AON.MSG.saveAction());
		accept_button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onAccept();
			}
		});
		cancel_button.setText(AON.MSG.cancelAction());
		cancel_button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onCancel();
			}
		});
	}
	
	protected abstract void onAccept();
	
	protected abstract void onCancel();
	
	CheckBox cb0;
	private void buildTable(NotificationInfo ni) {
		flex_table.setStyleName(AON.AON_CSS.aonPanelGrid());
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
		lb1.setStyleName(AON.AON_CSS.aonInputText());
	
		flex_table.setWidget(0, 0, new Label(MSG.emailAccount()));
		flex_table.setWidget(0, 1, lb1);

		cb0 = new CheckBox();
		cb0.setValue(ni.getIsLogo());
		
		cb0.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HorizontalPanel hp = new HorizontalPanel();
				if(cb0.getValue()){
					NumberSpinner sp = new NumberSpinner(20);
					sp.setStyleName(AON.AON_CSS.aonInputText());
					sp.setWidth("45px");
					hp.add(new Label(MSG.logoInclude()));
					hp.add(sp);
					//hp.add(new Label("Vista Previa"));
					hp.setSpacing(5);
				}else hp.add(new Label(MSG.logoInclude()));
				flex_table.setWidget(1, 1, hp);	
			}
		});
		
		flex_table.setWidget(1, 0, cb0);
		HorizontalPanel hp = new HorizontalPanel();
		if(cb0.getValue()){
			NumberSpinner sp = new NumberSpinner(ni.getLogoPercentage());
			sp.setStyleName(AON.AON_CSS.aonInputText());
			sp.setWidth("45px");
			hp.add(new Label(MSG.logoInclude()));
			hp.add(sp);
			//hp.add(new Label("Vista Previa"));
			hp.setSpacing(5);
		} else hp.add(new Label(MSG.logoInclude()));
		flex_table.setWidget(1, 1, hp);	
		
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
		lb2.setStyleName(AON.AON_CSS.aonInputText());
		flex_table.setWidget(2, 0, new Label(MSG.emailSign()));
		flex_table.setWidget(2, 1, lb2);

		CheckBox cb1 = new CheckBox();
		cb1.setValue(ni.getNotify());
		flex_table.setWidget(3, 0, cb1);
		flex_table.setWidget(3, 1, new Label(MSG.autoNotify()));

		CheckBox cb2 = new CheckBox();
		cb2.setValue(ni.getHistory());
		flex_table.setWidget(4, 0, cb2);
		flex_table.setWidget(4, 1, new Label(MSG.historyInclude()));
		
		TextBox tb = new TextBox();
		tb.setStyleName(AON.AON_CSS.aonInputText());
		tb.setValue(ni.getBcc() != null ? ni.getBcc() : "");
		flex_table.setWidget(5, 0, new Label(MSG.bccInclude()));
		flex_table.setWidget(5, 1, tb);
		
		ListBox lb3 = new ListBox();
		lb3.addItem(MSG.notSendNotify(), "0");
		lb3.addItem(MSG.testStatus(), "1");
		lb3.addItem(MSG.realStatus(), "2");
		lb3.setSelectedIndex(ni.getMode() != null ? ni.getMode() : 1);
		lb3.setStyleName(AON.AON_CSS.aonInputText());
		flex_table.setWidget(6, 0, new Label(MSG.mode()));
		flex_table.setWidget(6, 1, lb3);
		
		flexTableCss();
	}
	
	public void flexTableCss(){
		for (int i = 0; i < flex_table.getRowCount(); i++) {
			for (int j = 0; j < flex_table.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					flex_table.getCellFormatter().setStyleName(i, j, AON.AON_CSS.aonPanelGridOdd());
				} else {
					flex_table.getCellFormatter().setStyleName(i, j, AON.AON_CSS.aonPanelGridEven());
				}
			}
		}
	}
	
	private Domain getDomain() {
		return new Domain().setId(JsNotification.getCurrentDomain()).setName(JsNotification.getCurrentDomainName());
	}
}

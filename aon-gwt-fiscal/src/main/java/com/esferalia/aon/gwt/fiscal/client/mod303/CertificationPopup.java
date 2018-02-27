package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class CertificationPopup extends CustomDialog {
	public API getAPI() {
		return new API(GWT.getModuleBaseURL(), aonData.getMd5(),
			aonData.getDomain().getName(), aonData.getDomain().getId(),
			aonData.getUser().getLogin());
	}
	
	protected abstract void onAccept();
	
	protected abstract void onCancel();

	
	public String getName() {
		VerticalPanel vp = (VerticalPanel) getWidget();
		HorizontalPanel hp = (HorizontalPanel) vp.getWidget(0);
		TextBox tb = (TextBox) hp.getWidget(1);
		return tb.getValue();
	}
	
	public String getDocument() {
		VerticalPanel vp = (VerticalPanel) getWidget();
		HorizontalPanel hp = (HorizontalPanel) vp.getWidget(1);
		TextBox tb = (TextBox) hp.getWidget(1);
		return tb.getValue();
	}
	
	public String getCert() {
		VerticalPanel vp = (VerticalPanel) getWidget();
		HorizontalPanel hp = (HorizontalPanel) vp.getWidget(2);
		ListBox lb = (ListBox) hp.getWidget(1);
		return lb.getSelectedValue();
	}
	
	public String getPass() {
		VerticalPanel vp = (VerticalPanel) getWidget();
		HorizontalPanel hp = (HorizontalPanel) vp.getWidget(3);
		TextBox tb = (TextBox) hp.getWidget(1);
		return tb.getValue();
	}
	
	AonData aonData = new AonData();
	public CertificationPopup(AonData aonData, String name, String document) {
		this.aonData = aonData;
		setCaption("Certificado Digital");
		setGlassEnabled(true);
		setAnimationEnabled(true);

		VerticalPanel vp = new VerticalPanel();
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName(AON.AON_CSS.aonPaddingTop());
		Label l = new Label("Razon Social / Nombre");
		l.addStyleName(AON.AON_CSS.aonPaddingRight());
		hp.add(l);
		TextBox tb = new TextBox();
		tb.setText(name);
		tb.setStyleName(AON.AON_CSS.aonInputText());
		hp.add(tb);
		vp.add(hp);
		
		HorizontalPanel hp0 = new HorizontalPanel();
		hp0.addStyleName(AON.AON_CSS.aonPaddingTop());
		Label l0 = new Label("NIF");
		l0.addStyleName(AON.AON_CSS.aonPaddingRight());
		hp0.add(l0);
		TextBox tb0 = new TextBox();
		tb0.setStyleName(AON.AON_CSS.aonInputText());
		tb0.setText(document);
		hp0.add(tb0);
		vp.add(hp0);
	
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.addStyleName(AON.AON_CSS.aonPaddingTop());
		Label l1 = new Label("Certificado");
		l1.addStyleName(AON.AON_CSS.aonPaddingRight());
		hp1.add(l1);
		ListBox lb = new ListBox();
		getAPI().getAttachment().getCertificates(new AsyncCallback<JSON<JsAttach>>() {
			
			@Override
			public void onSuccess(JSON<JsAttach> result) {
				result.getData().stream().forEach(a -> {
					lb.addItem(a.getTitle(), a.getId() + "");
				});
			}

			@Override public void onFailure(Throwable caught) {}
		});
		hp1.add(lb);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.addStyleName(AON.AON_CSS.aonPaddingTop());
		Label l2 = new Label("Contrase\u00f1a");
		l2.addStyleName(AON.AON_CSS.aonPaddingRight());
		hp2.add(l2);
		PasswordTextBox ptb = new PasswordTextBox();
		ptb.setStyleName(AON.AON_CSS.aonInputText());
		hp2.add(ptb);
		vp.add(hp1);
		vp.add(hp2);

		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.AON_CSS.aonPadding());
		buttonsPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onAccept();
			}
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.AON_CSS.aonConfirmDialogCancelButton());
    	cancelButton.addStyleName(AON.AON_CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
				onCancel();
			}
			
		});
		buttonsPanel.add(cancelButton);
		vp.add(buttonsPanel);
		add(vp);
	}
}

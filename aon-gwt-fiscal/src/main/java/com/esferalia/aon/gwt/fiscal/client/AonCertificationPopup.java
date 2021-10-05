package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class AonCertificationPopup extends AonCustomDialog {
	
	private API api;
	public API getAPI() {
		return api;
	}
	
	protected abstract void onAccept();
	protected abstract void onCancel();

	
	public String getName() {
		VerticalPanel vp = (VerticalPanel) getWidget();
		HorizontalPanel hp = (HorizontalPanel) vp.getWidget(0);
		AonTextBox tb = (AonTextBox) hp.getWidget(1);
		return tb.getValue();
	}
	
	public String getDocument() {
		VerticalPanel vp = (VerticalPanel) getWidget();
		HorizontalPanel hp = (HorizontalPanel) vp.getWidget(1);
		AonTextBox tb = (AonTextBox) hp.getWidget(1);
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
		AonTextBox tb = (AonTextBox) hp.getWidget(1);
		return tb.getValue();
	}
	
	public String getNRC() {
		VerticalPanel vp = (VerticalPanel) getWidget();
		HorizontalPanel hp = (HorizontalPanel) vp.getWidget(4);
		AonTextBox tb = (AonTextBox) hp.getWidget(1);
		return tb.getValue();
	}
/*	
	public AonCertificationPopup(API api, boolean showNRC) {
		this.api = api;
		setWidth("400px");
		setCaption("Certificado Digital");
		setGlassEnabled(true);
		setAnimationEnabled(true);

		VerticalPanel vp = new VerticalPanel();
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVisible(false);
		vp.add(hp);
		
		HorizontalPanel hp0 = new HorizontalPanel();
		hp0.setVisible(false);
		vp.add(hp0);
		
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.addStyleName(AON.CSS.aonPaddingTop());
		Label l1 = new Label("Certificado");
		l1.addStyleName(AON.CSS.aonPaddingRight());
		hp1.add(l1);
		ListBox lb = new ListBox();
		getAPI().getAttachment().getCertificates(new AsyncCallback<JSON<JsAttach>>() {
			
			@Override
			public void onSuccess(JSON<JsAttach> result) {
				result.getData().stream().forEach(a -> lb.addItem(a.getTitle(), a.getId() + ""));
			}

			@Override public void onFailure(Throwable caught) {
				// Nothing
			}
		});
		hp1.add(lb);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.addStyleName(AON.CSS.aonPaddingTop());
		Label l2 = new Label("Contrase\u00f1a");
		l2.addStyleName(AON.CSS.aonPaddingRight());
		hp2.add(l2);
		PasswordTextBox ptb = new PasswordTextBox();
		ptb.setStyleName(AON.CSS.aonInputText());
		hp2.add(ptb);
		vp.add(hp1);
		vp.add(hp2);
		if (showNRC) {
			HorizontalPanel hpx = new HorizontalPanel();
			hpx.addStyleName(AON.CSS.aonPaddingTop());
			Label lx = new Label("NRC");
			lx.addStyleName(AON.CSS.aonPaddingRight());
			hpx.add(lx);
			AonTextBox tbx = new AonTextBox();
			tbx.setText("");
			hpx.add(tbx);
			vp.add(hpx);
		}
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler( event -> {
			hide();
			onAccept();
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> {
			hide();
			onCancel();
		});
		buttonsPanel.add(cancelButton);
		vp.add(buttonsPanel);
		add(vp);
	}
*/	
	protected AonCertificationPopup(API api, String name, String document, boolean showNRC) {
		this.api = api;
		setWidth("400px");
		setCaption("Certificado Digital");
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		FlowPanel rootPanel = new FlowPanel();  
		
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonWidthAlmostAll());
		table.addStyleName(AON.CSS.aonBlockCenter());
		
		Label l = new Label("Razon Social / Nombre");
		l.addStyleName(AON.CSS.aonTableLabel());
		AonTextBox tb = new AonTextBox();
		tb.setText(name);
		table.addRow()
			.addCell(l)
			.addCell(tb);
		
		Label l0 = new Label("NIF");
		l0.addStyleName(AON.CSS.aonTableLabel());
		AonTextBox tb0 = new AonTextBox();
		tb0.setText(document);
		table.addRow()
			.addCell(l0)
			.addCell(tb0);
	
		Label l1 = new Label("Certificado");
		l1.addStyleName(AON.CSS.aonTableLabel());
		ListBox lb1 = new ListBox();
		getAPI().getAttachment().getCertificates(new AsyncCallback<JSON<JsAttach>>() {
			
			@Override
			public void onSuccess(JSON<JsAttach> result) {
				result.getData().stream().forEach(a -> lb1.addItem(a.getTitle(), a.getId() + ""));
			}

			@Override 
			public void onFailure(Throwable caught) {
				// Nothing
			}
		});
		table.addRow()
			.addCell(l1)
			.addCell(lb1);
		
		Label l2 = new Label("Contrase\u00f1a");
		l2.addStyleName(AON.CSS.aonTableLabel());
		PasswordTextBox ptb = new PasswordTextBox();
		ptb.setStyleName(AON.CSS.aonInputText());
		table.addRow()
			.addCell(l2)
			.addCell(ptb);
		
		if (showNRC) {
			Label lx = new Label("NRC");
			lx.addStyleName(AON.CSS.aonTableLabel());
			AonTextBox tbx = new AonTextBox();
			tbx.setText("");
			table.addRow()
				.addCell(lx)
				.addCell(tbx);
		}
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler(event -> {
			hide();
			onAccept();
		});
		
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> {
			hide();
			onCancel();
		});
		buttonsPanel.add(cancelButton);
		
		rootPanel.add(table);
		rootPanel.add(buttonsPanel);
		add(rootPanel);
	}
}

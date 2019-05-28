package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeePeculiaritiesDialog extends CustomDialog {
	
	interface Binder extends UiBinder<Widget, EmployeePeculiaritiesDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String hidden();
	}
	
	@UiField
	ListBox peculiarities;
	
	@UiField
	TabLayoutPanel tabPanel;
	
	@UiField
	Button acceptButton;
	
	@UiField
	Button cancelButton;

	public EmployeePeculiaritiesDialog() {
		setCaption("Peculiaridades de cotizaci"+String.valueOf("\u00D3")+"n");
		
		setWidget(binder.createAndBindUi(this));
		
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
		
		this.tabPanel.setHeight("300px");
		this.tabPanel.setWidth("670px");
		
		initListBox();
	}

	protected abstract void onAccept();

	private void initListBox() {
		peculiarities.clear();
		peculiarities.addItem("MANUAL");
		peculiarities.addItem("CONTRATO TEMPORAL");
		peculiarities.addItem("JUBILACION ACTIVA");
		peculiarities.addItem("COOPERATIVAS");
		peculiarities.addItem("BECARIOS");
		peculiarities.addItem("REGIMEN GENERAL ASIMILADOS");
		peculiarities.addItem("MAYOR 65 A" + String.valueOf("\u00D1") + "OS > 38 A" + String.valueOf("\u00D1") + "OS COTIZADOS");
		peculiarities.addItem("MATERNIDAD/PATERNIDAD/R.EMBARAZO");
		peculiarities.addItem("COBRO DIRECTO IT");
		peculiarities.addItem("CONTRATOS < 6 DIAS");
	}
}

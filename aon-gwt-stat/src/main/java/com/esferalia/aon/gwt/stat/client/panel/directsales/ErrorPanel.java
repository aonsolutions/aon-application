package com.esferalia.aon.gwt.stat.client.panel.directsales;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ErrorPanel extends FlowPanel {

	private FlowPanel panel;
	
	public ErrorPanel() {
		this.setStyleName(AON.AON_CSS.aonErrorPanel());
		this.setVisible(false);
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonWidthAll());
		tab.getColumnFormatter().setStyleName(0, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().setStyleName(1, AON.AON_CSS.aonWidth20());
		tab.getColumnFormatter().addStyleName(1, AON.AON_CSS.aonVerticalAlignTop());
		tab.getColumnFormatter().addStyleName(1, AON.AON_CSS.aonTextRight());
		
		panel = new FlowPanel(); 
		tab.setWidget(0, 0, panel);
		Button hide = new Button();
		hide.setStyleName(AON.AON_CSS.aonIconClose());
		hide.addStyleName(AON.AON_CSS.aonIconCommandButton());
		hide.setTitle(AON.MSG.hide());
		add(hide);
		hide.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		tab.setWidget(0, 1, hide);
		this.add(tab);
	}
	
	public void hide() {
		panel.clear();
		ErrorPanel.this.setVisible(false);
	}
	
	public void initialize() {
		panel.clear();
	}

	public void addError(Throwable caught) {
		this.addError(caught.getMessage());
	}
	public void showError(Throwable caught) {
		initialize();
		addError(caught);
		setVisible(true);
	}
	public void showError(String msg) {
		initialize();
		addError(msg);
		setVisible(true);
	}
	public void addError(String msg) {
		Label error = new Label(msg);
		error.setStyleName(AON.AON_CSS.aonErrorPanelError());
		panel.add(error);
	}

	public void showWarning(Throwable caught) {
		initialize();
		addWarning(caught);
		setVisible(true);
	}
	public void showWarning(String msg) {
		initialize();
		addWarning(msg);
		setVisible(true);
	}
	public void addWarning(Throwable caught) {
		this.addWarning(caught.getMessage());
	}
	public void addWarning(String msg) {
		Label error = new Label(msg);
		error.setStyleName(AON.AON_CSS.aonErrorPanelWarn());
		panel.add(error);
	}
	
	public void showInfo(Throwable caught) {
		initialize();
		addInfo(caught);
		setVisible(true);
	}
	public void showInfo(String msg) {
		initialize();
		addInfo(msg);
		setVisible(true);
	}
	public void addInfo(Throwable caught) {
		this.addInfo(caught.getMessage());
	}
	public void addInfo(String msg) {
		Label error = new Label(msg);
		error.setStyleName(AON.AON_CSS.aonErrorPanelInfo());
		panel.add(error);
	}

}

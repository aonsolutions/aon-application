package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AonErrorPanel extends FlowPanel {

	private FlowPanel panel;
	
	public AonErrorPanel() {
		this.setStyleName(AON.CSS.aonFlexBlock());
		this.addStyleName(AON.CSS.aonBlockCenter());
		this.addStyleName(AON.CSS.aonBlockMessage());
		this.addStyleName(AON.CSS.aonBlockErrorMessage());
		this.getElement().getStyle().setWidth(90, Unit.PCT);
		
		this.setVisible(false);
		
		panel = new FlowPanel();
		panel .setStyleName(AON.CSS.aonFlexGrow1());
		
		add(panel);
		
		AonTableButton hide = new AonTableButton(AON.MSG.hide() ,AON.CSS.aonIconClose());
		hide.setTitle(AON.MSG.hide());
		add(hide);
		hide.addClickHandler(event -> hide());
		add(hide);
	}
	
	public void hide() {
		panel.clear();
		setVisible(false);
	}
	
	public void show() {
		setVisible(true);
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
		this.addStyleName(AON.CSS.aonBlockErrorMessage());
		this.removeStyleName(AON.CSS.aonBlockWarningMessage());
		this.removeStyleName(AON.CSS.aonBlockInfoMessage());
		Label error = new Label(msg);
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
		this.removeStyleName(AON.CSS.aonBlockErrorMessage());
		this.removeStyleName(AON.CSS.aonBlockInfoMessage());
		this.addStyleName(AON.CSS.aonBlockWarningMessage());
		Label error = new Label(msg);
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
		this.removeStyleName(AON.CSS.aonBlockErrorMessage());
		this.removeStyleName(AON.CSS.aonBlockWarningMessage());
		this.addStyleName(AON.CSS.aonBlockInfoMessage());
		Label error = new Label(msg);
		panel.add(error);
	}

}

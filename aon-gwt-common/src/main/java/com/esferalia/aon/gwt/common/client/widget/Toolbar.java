package com.esferalia.aon.gwt.common.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.paper.widget.PaperIconButton;

public abstract class Toolbar extends Composite {

	interface ToolbarBinder extends UiBinder<Widget, Toolbar> {
	}

	private static final ToolbarBinder binder = GWT.create(ToolbarBinder.class);
	
	@UiField Label label;
	
	@UiField Button back;
	@UiField Button reset;
	@UiField Button remove;
	@UiField Button print;
	@UiField Button email;
	
	@UiField Button excelDownload;
	@UiField Button pdfDownload;
	
	@UiField Button liqDownload;	
	
	@UiField PaperIconButton ant;
	@UiField PaperIconButton next;
	
	public Toolbar(String title) {
		initWidget(binder.createAndBindUi(this));
		label.setText(title);
	}

	// -------------------------------------------------------------- UiHandler

	protected abstract void back();
	protected abstract void reset();
	protected abstract void remove();
	protected abstract void print();
	protected abstract void email();
	
	protected abstract void excelDownload();
	protected abstract void liqDownload();
	protected abstract void pdfDownload();
	
	protected abstract void ant();
	protected abstract void next();


	@UiHandler("excelDownload")
	public void onExcelClick(ClickEvent event) {
		excelDownload();
	}
	
	public void setExcelVisible(Boolean visible){
		excelDownload.setVisible(visible);
	}
	
	@UiHandler("liqDownload")
	public void onLiqClick(ClickEvent event) {
		liqDownload();
	}
	
	public void setLiqVisible(Boolean visible){
		liqDownload.setVisible(visible);
	}
	
	@UiHandler("pdfDownload")
	public void onPdfClick(ClickEvent event) {
		pdfDownload();
	}
	
	public void setPdfVisible(Boolean visible){
		pdfDownload.setVisible(visible);
	}
	
	@UiHandler("back")
	public void onBackClick(ClickEvent event) {
		back();
	}
	
	public void setBackVisible(Boolean visible){
		back.setVisible(visible);
	}
	

	@UiHandler("reset")
	public void onResetClick(ClickEvent event) {
		reset();
	}

	public void setResetVisible(Boolean visible){
		reset.setVisible(visible);
	}
	
	@UiHandler("remove")
	public void onRemoveClick(ClickEvent event) {
		remove();
	}
	
	public void setRemoveVisible(Boolean visible){
		remove.setVisible(visible);
	}
	
	@UiHandler("print")
	public void onPrintClick(ClickEvent event) {
		print();
	}
	
	public void setPrintVisible(Boolean visible){
		print.setVisible(visible);
	}
	
	@UiHandler("email")
	public void onEmailClick(ClickEvent event) {
		email();
	}
	
	public void setEmailVisible(Boolean visible){
		email.setVisible(visible);
	}
	
	@UiHandler("ant")
	public void onAntClick(ClickEvent event) {
		ant();
	}
	
	public void setAntVisible(Boolean visible){
		ant.setVisible(visible);
		ant.setDisabled(!visible);
	}
	
	@UiHandler("next")
	public void onNextClick(ClickEvent event) {
		next();
	}
	
	public void setNextVisible(Boolean visible){
		next.setVisible(visible);
		next.setDisabled(!visible);
	}
	
	public void setAllVisible(Boolean visible){
		setNextVisible(visible);
		setAntVisible(visible);
		setBackVisible(visible);
		setEmailVisible(visible);
		setPrintVisible(visible);
		setResetVisible(visible);
		setRemoveVisible(visible);
		setExcelVisible(visible);
		setPdfVisible(visible);
		setLiqVisible(visible);
	}

}

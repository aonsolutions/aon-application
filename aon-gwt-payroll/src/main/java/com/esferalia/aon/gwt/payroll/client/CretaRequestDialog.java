package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.TextCell;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.Widget;

public class CretaRequestDialog extends SelectDialog<CCC> {

	public static interface Callback {
		boolean onAccept(CretaRequestDialog dialog);
	}

	interface Binder extends UiBinder<Widget, CretaRequestDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	
	
	@UiField
	LongBox authLongBox;

	@UiField
	MonthListBox monthListBox;
	
	@UiField
	Anchor downloadAnchor;
	
	@UiField
	Label selectLabel;
	
	@UiField
	Label messagesLabel;


	@UiField
	Element previousBasesTR;

	@UiField
	ListBox typeListBox;
	
	@UiField
	CheckBox previousBasesCheckBox;

	private Callback callback;
	
	public CretaRequestDialog(Callback callback) {
		
		this.callback = callback;
		
		setCaption("Sistema de Liquidaci\u00F3n Directa (Proyecto Cret@)");
		
		setWidget(binder.createAndBindUi(this));
		
		setVisiblePreviousBases(false);
		
		// Full CCC.
		Column<CCC, String> fullNameColumn = new Column<CCC, String>(
				new TextCell()) {
			@Override
			public String getValue(CCC ccc) {
				return getDescription(ccc);
			}
		};

		addColumn(fullNameColumn, "C\u00F3digo de Cuenta de Cotizaci\u00F3n");
		
		monthListBox.setSelectedMonth(DateUtils.getFirstDayOfMonth());
		
		acceptButton.setEnabled(enableAccept());
	}
	
	
	// ------------------------------------------------------------------------
	
	@UiHandler("acceptButton")
	void  onAcceptClicked(ClickEvent e){
		if ( callback.onAccept(this) )
			hide();
	}
	
	@UiHandler("acceptButton")
	void  onCancelClicked(ClickEvent e){
		hide();
	}
	
	@UiHandler("authLongBox")
	void onAuthChanged(ValueChangeEvent<Long> e) {
		acceptButton.setEnabled(enableAccept());
	}
	
	@UiHandler("authLongBox")
	void onAuthChanged(KeyUpEvent e ){
		acceptButton.setEnabled(enableAccept());
	}
	
	// ------------------------------------------------------------------------
	
	public String getType(){
		return typeListBox.getSelectedValue();
	}

	public Date getMonth(){
		return monthListBox.getSelected();
	}
	
	public Long getAuthorized() {
		return authLongBox.getValue();
	}
	
	public void setAuthorized(Long authorized){
		authLongBox.setValue(authorized);
	}
	
	public boolean previousBases(){
		return previousBasesCheckBox.getValue();
	}
	
	public String getDescription(CCC ccc) {
		return ccc.getCode();
	}
	
	// ------------------------------------------------------------------------
	
	public void download(String fileName,String url){
		downloadAnchor.setHref(url);
		downloadAnchor.setTarget(fileName);
		downloadAnchor.getElement().setAttribute("download", fileName);
		click(downloadAnchor.getElement());
		
	}
	// ------------------------------------------------------------------------
	
	protected void setVisiblePreviousBases(boolean visible){
		setVisible(visible, previousBasesTR);
	}
	
	
	protected void setVisible(boolean visible, Element el){
		if ( visible )
			el.getStyle().clearDisplay();
		else
			el.getStyle().setDisplay(Display.NONE);
		
	}

	// ------------------------------------------------------------------------
	
	@Override
	protected boolean enableAccept() {
		try {
			checkAuth();
			checkCCCs();
			hide(messagesLabel, true);
			return true;
		} catch ( Exception e ){
			hide(messagesLabel, false);
			messagesLabel.setText(e.getMessage());
			return false;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	

	private void checkCCCs() throws Exception{
		if ( getSelectedData().isEmpty() )
			throw new Exception("Debe seleccionar al menos un C\u00F3digo de Cuenta de Cotizaci\u00F3n (CCC).");
	}

	private void checkAuth() throws Exception{
		if ( authLongBox.getValue() == null )
			throw new Exception("Autorizado no v\u00E1lido. Debe ser un n\u00FAmero que contenga 8 d\u00EDgitos o menos.");
			
	}
	// ------------------------------------------------------------------------
	
	private static void hide(Widget widget, boolean hide){
		if ( hide )
			widget.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		else 
			widget.getElement().getStyle().clearVisibility();
	}
	

	private static native void click(Element a)/*-{
		a.click();
	}-*/;
	
}

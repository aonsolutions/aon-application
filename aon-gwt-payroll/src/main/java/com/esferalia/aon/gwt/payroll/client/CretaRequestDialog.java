package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.TextCell;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Anchor;
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
	
	
	private Callback callback;
	
	public CretaRequestDialog(Callback callback) {
		
		this.callback = callback;
		
		setCaption("Sistema de Liquidaci\u00F3n Directa (Proyecto Cret@)");
		
		setWidget(binder.createAndBindUi(this));
		
		
		// Full CCC.
		Column<CCC, String> fullNameColumn = new Column<CCC, String>(
				new TextCell()) {
			@Override
			public String getValue(CCC ccc) {
				return ccc.getCode();
			}
		};

		addColumn(fullNameColumn, "C\u00F3digo de Cuenta de Cotizaci\u00F3n");
		
		monthListBox.setSelectedMonth(DateUtils.getFirstDayOfMonth());
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
	void onAuthCahnged(ValueChangeEvent<Long> e) {
		acceptButton.setEnabled(e.getValue()!= null);
	}
	
	// ------------------------------------------------------------------------
	
	public Date getMonth(){
		return monthListBox.getSelected();
	}
	
	
	public Long getAuthorized() {
		return authLongBox.getValue();
	}
	
	public void setAuthorized(Long authorized){
		authLongBox.setValue(authorized);
	}
	
	// ------------------------------------------------------------------------
	
	public void download(String fileName,String url){
		downloadAnchor.setHref(url);
		downloadAnchor.setTarget(fileName);
		downloadAnchor.getElement().setAttribute("download", fileName);
		click(downloadAnchor.getElement());
		
	}
	
	// ------------------------------------------------------------------------

	private static native void click(Element a)/*-{
		a.click();
	}-*/;
	
}

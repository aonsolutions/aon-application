package com.esferalia.aon.gwt.fiscal.client.mod123;


import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class Model123Araba2016 extends Model123Base {

	public Model123Araba2016(IFiscalModelCallback<Mod123> callback) {
		super(callback);
	}

	@Override
	protected void paintParticularyRow(final IFiscalModelCallback<Mod123> callback, IModelScript<Mod123Key> script) {
		if (script.getKeys() == null) return;
		
		if (script.getKeys()[0] == Mod123Key.AR_907) {
			paintRow907(callback,script);
		} else if (script.getKeys()[0] == Mod123Key.AR_908) {
			paintRow908(callback,script);
		} else if (script.getKeys()[0] == Mod123Key.AR_909) {
			paintRow909(callback,script);
		}
	}

	private void paintRow907(final IFiscalModelCallback<Mod123> callback, IModelScript<Mod123Key> script) {
		int row = getTable().getRowCount();
		final FiscalModelDetail ar907 = callback.getFiscalModel().ensureDetail(Mod123Key.AR_907);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
		
		getTable().setWidget(row, 1, new BoxLabel(Mod123Key.AR_907.getBox()));
		final CheckBox w907 = new CheckBox();
		w907.setEnabled(callback.getFiscalModel().isNotFinished());
		w907.setValue(ar907.getAmount() == 1);
		w907.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ar907.setAmount(w907.getValue()?1.0:0.0);
				callback.markAsDirty();
			}
		});
		getTable().setWidget(row, 2, w907 );
	}
	private void paintRow908(final IFiscalModelCallback<Mod123> callback, IModelScript<Mod123Key> script) {
		int row = getTable().getRowCount();
		final  FiscalModelDetail ar908 = callback.getFiscalModel().ensureDetail(Mod123Key.AR_908);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
//		getTable().getFlexCellFormatter().setColSpan(row, 0, 5);
		
		getTable().setWidget(row, 1, new BoxLabel(Mod123Key.AR_908.getBox()));
		final ListBox w908 = new ListBox();
		w908.setEnabled(callback.getFiscalModel().isNotFinished());
		w908.addItem("---");
		w908.addItem(AON.MSG.preInsolvencyState());
		w908.addItem(AON.MSG.postInsolvencyState());
		w908.setSelectedIndex((int) ar908.getAmount());
		w908.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ar908.setAmount(w908.getSelectedIndex());
				callback.markAsDirty();
			}
		});
		getTable().setWidget(row, 2, w908 );
	}
	private void paintRow909(final IFiscalModelCallback<Mod123> callback, IModelScript<Mod123Key> script) {
		int row = getTable().getRowCount();
		final FiscalModelDetail ar909 = callback.getFiscalModel().ensureDetail(Mod123Key.AR_909);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
//		getTable().getFlexCellFormatter().setColSpan(row, 0, 5);
		
		getTable().setWidget(row, 1, new BoxLabel(Mod123Key.AR_909.getBox()));
		final DateBoxEx w909 = new DateBoxEx();
		w909.setEnabled(callback.getFiscalModel().isNotFinished());
		if (AonStringUtils.isNotEmpty( ar909.getDescription() ) ) {
			w909.setValue( w909.parse(ar909.getDescription() , false) );
		}
		w909.addValueChangeHandler( new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				ar909.setDescription(w909.format());
				callback.markAsDirty();
			}
		});
		getTable().setWidget(row, 2, w909 );
		paintEmptyRow();
	}

	@Override
	public Widget getInfoPanel(Mod123 mod123) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		panel.add(getAnchorPanel(mod123,"Formulario Papel." 
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader"
			+ "=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2"
			+ "=pragma&blobheadervalue1=attachment%3B+filename%3D123.pdf&blobheadervalue2"
			+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224075511587&ssbinary=true"));
		panel.add(getAnchorPanel(mod123,"Decreto Foral 14 de 29 de febrero de 2000."
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DDecreto+Foral+14+de+29+de+febrero+de+2000.pdf&"
			+ "blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224075511588&ssbinary=true"));
		panel.add(getAnchorPanel(mod123,"Orden Foral 673 de 18 de octubre de 2001."
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DOrden+Foral+673+de+18+de+octubre+de+2001.pdf&blobheadervalue2"
			+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224075511589&ssbinary=true"));
		return panel;
	}
}

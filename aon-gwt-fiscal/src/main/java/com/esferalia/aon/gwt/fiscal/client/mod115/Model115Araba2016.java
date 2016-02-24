package com.esferalia.aon.gwt.fiscal.client.mod115;


import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
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

public class Model115Araba2016 extends Model115Base {

	public Model115Araba2016(IFiscalModelCallback<Mod115> callback) {
		super(callback);
	}

	@Override
	protected void paintParticularyRow(Mod115 mod115, IModelScript<Mod115Key> script) {
		if (script.getKeys() == null) return;
		
		if (script.getKeys()[0] == Mod115Key.AR_907) {
			paintRow907(mod115,script);
		} else if (script.getKeys()[0] == Mod115Key.AR_908) {
			paintRow908(mod115,script);
		} else if (script.getKeys()[0] == Mod115Key.AR_909) {
			paintRow909(mod115,script);
		}
	}

	private void paintRow907(Mod115 mod115, IModelScript<Mod115Key> script) {
		int row = getTable().getRowCount();
		final FiscalModelDetail ar907 = mod115.ensureDetail(Mod115Key.AR_907);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
//		getTable().getFlexCellFormatter().setColSpan(row, 0, 5);
		
		getTable().setWidget(row, 1, new BoxLabel(Mod115Key.AR_907.getBox()));
		final CheckBox w907 = new CheckBox();
		w907.setEnabled(mod115.isNotFinished());
		w907.setValue(ar907.getAmount() == 1);
		w907.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ar907.setAmount(w907.getValue()?1.0:0.0);
				getCallback().markAsDirty();
			}
		});
		getTable().setWidget(row, 2, w907 );
	}
	private void paintRow908(Mod115 mod115, IModelScript<Mod115Key> script) {
		int row = getTable().getRowCount();
		final  FiscalModelDetail ar908 = mod115.ensureDetail(Mod115Key.AR_908);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
//		getTable().getFlexCellFormatter().setColSpan(row, 0, 5);
		
		getTable().setWidget(row, 1, new BoxLabel(Mod115Key.AR_908.getBox()));
		final ListBox w908 = new ListBox();
		w908.setEnabled(mod115.isNotFinished());
		w908.addItem("---");
		w908.addItem(AON.MSG.preInsolvencyState());
		w908.addItem(AON.MSG.postInsolvencyState());
		w908.setSelectedIndex((int) ar908.getAmount());
		w908.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ar908.setAmount(w908.getSelectedIndex());
				getCallback().markAsDirty();
			}
		});
		getTable().setWidget(row, 2, w908 );
	}
	private void paintRow909(Mod115 mod115, IModelScript<Mod115Key> script) {
		int row = getTable().getRowCount();
		final FiscalModelDetail ar909 = mod115.ensureDetail(Mod115Key.AR_909);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
//		getTable().getFlexCellFormatter().setColSpan(row, 0, 5);
		
		getTable().setWidget(row, 1, new BoxLabel(Mod115Key.AR_909.getBox()));
		final DateBoxEx w909 = new DateBoxEx();
		w909.setEnabled(mod115.isNotFinished());
		if (AonStringUtils.isNotEmpty( ar909.getDescription() ) ) {
			w909.setValue( w909.parse(ar909.getDescription() , false) );
		}
		w909.addValueChangeHandler( new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				ar909.setDescription(w909.format());
				getCallback().markAsDirty();
			}
		});
		getTable().setWidget(row, 2, w909 );
		paintEmptyRow();
	}

	@Override
	public Widget getInfoPanel(Mod115 mod115) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		panel.add(getAnchorPanel(mod115,"Formulario Papel. [pdf]" 
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3D115-A.pdf&blobheadervalue2=public&blobkey=id&blobtable="
			+ "MungoBlobs&blobwhere=1224091770466&ssbinary=true"));
		panel.add(getAnchorPanel(mod115,"Orden Foral 402 de 26 de marzo de 1998. [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf"
			+ "&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1"
			+ "=attachment%3B+filename%3DOrden+Foral+402+de+26+de+marzo+de+1998.pdf&blobheadervalue2"
			+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770467&ssbinary=true"));
		panel.add(getAnchorPanel(mod115,"Orden Foral 673 de 18 de octubre de 2001. [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf"
			+ "&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DOrden+Foral+673+de+18+de+octubre+de+2001.pdf&blobheadervalue2"
			+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770468&ssbinary=true"));
		panel.add(getAnchorPanel(mod115,"Orden Foral 39 de 3 de febrero de 2010 que regula la "
			+ "obligaci\u00F3n de algunos sujetos y entidades de presentar este modelo de forma "
			+ "telem\u00E1tica por Internet."
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DOrden+Foral+39+de+3+de+febrero+de+2010+que+regula+la+"
			+ "obligaci%C3%B3n+de+algunos+sujetos+y+entidades+de+presentar+este+modelo+de+forma"
			+ "+telem%C3%A1tica+por+Internet.pdf&blobheadervalue2=public&blobkey=id&blobtable="
			+ "MungoBlobs&blobwhere=1224091770469&ssbinary=true"));
		panel.add(getAnchorPanel(mod115,"Resoluci\u00F3n 21 de 12 de enero de 2016 . [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DResoluci%C3%B3n+21+de+12+de+enero+de+2016+.pdf&"
			+ "blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770470"
			+ "&ssbinary=true"));
		panel.add(getAnchorPanel(mod115,"Orden Foral 104 de 17 de febrero de 2014. [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DOrden+Foral+104+de+17+de+febrero+de+2014.pdf&blobheadervalue2"
			+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770471&ssbinary=true"));
		return panel;
	}
}

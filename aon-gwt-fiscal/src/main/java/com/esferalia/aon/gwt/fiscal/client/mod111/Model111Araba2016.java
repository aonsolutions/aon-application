package com.esferalia.aon.gwt.fiscal.client.mod111;


import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.mod111.IModelScript;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.watson.util.AonMathUtils;
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

public class Model111Araba2016 extends Model111Base {

	public Model111Araba2016(IFiscalModelCallback<Mod111> callback) {
		super(callback);
	}

	@Override
	protected void paintParticularyRow(Mod111 mod111, IModelScript script) {
		if (script.getKeys() == null) return;
		
		if (script.getKeys()[0] == Mod111Key.AR_907) {
			paintRow907(mod111,script);
		} else if (script.getKeys()[0] == Mod111Key.AR_909) {
			paintRow908(mod111,script);
		} else if (script.getKeys()[0] == Mod111Key.AR_909) {
			paintRow909(mod111,script);
		}
	}

	private void paintRow907(Mod111 mod111, IModelScript script) {
		int row = getTable().getRowCount();
		final FiscalModelDetail ar907 = mod111.ensureDetail(Mod111Key.AR_907);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
		getTable().getFlexCellFormatter().setColSpan(row, 0, 5);
		
		getTable().setWidget(row, 1, new BoxLabel(Mod111Key.AR_907.getBox()));
		final CheckBox w907 = new CheckBox();
		w907.setValue(ar907.getAmount() == 1);
		w907.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ar907.setAmount(w907.getValue()?1.0:0.0);
			}
		});
		getTable().setWidget(row, 2, w907 );
	}
	private void paintRow908(Mod111 mod111, IModelScript script) {
		int row = getTable().getRowCount();
		final  FiscalModelDetail ar908 = mod111.ensureDetail(Mod111Key.AR_908);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
		getTable().getFlexCellFormatter().setColSpan(row, 0, 5);
		
		getTable().setWidget(row, 1, new BoxLabel(Mod111Key.AR_908.getBox()));
		final ListBox w908 = new ListBox();
		w908.addItem("---");
		w908.addItem(AON.MSG.preInsolvencyState());
		w908.addItem(AON.MSG.postInsolvencyState());
		w908.setSelectedIndex((int) ar908.getAmount());
		w908.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ar908.setAmount(w908.getSelectedIndex());
			}
		});
		getTable().setWidget(row, 2, w908 );
	}
	private void paintRow909(Mod111 mod111, IModelScript script) {
		int row = getTable().getRowCount();
		final FiscalModelDetail ar909 = mod111.ensureDetail(Mod111Key.AR_909);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
		getTable().getFlexCellFormatter().setColSpan(row, 0, 5);
		
		getTable().setWidget(row, 1, new BoxLabel(Mod111Key.AR_909.getBox()));
		final DateBoxEx w909 = new DateBoxEx();
		if (AonStringUtils.isNotEmpty( ar909.getDescription() ) ) {
			w909.parse(ar909.getDescription() , false);
		}
		w909.addValueChangeHandler( new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				if (AonMathUtils.isNotZero(ar909.getAmount()) ) {
					ar909.setDescription(w909.format());
				} else {
					ar909.setDescription(null);
				}
			}
		});
		getTable().setWidget(row, 2, w909 );
		paintEmptyRow();
	}

	@Override
	public Widget getInfoPanel(Mod111 mod111) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		panel.add(getAnchorPanel(mod111,
				 "Formulario Papel. [pdf]" 
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3D110.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091668896&ssbinary=true"));
		panel.add(getAnchorPanel(mod111,
				"Orden Foral 54 de 31 de enero de 2007. [pdf]"
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+54+de+31+de+enero+de+2007.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091668897&ssbinary=true"));
		panel.add(getAnchorPanel(mod111,
				"Orden Foral 39 de 3 de febrero de 2010 que regula la obligaci\u00F3n de algunos sujetos y entidades de presentar este modelo de forma telem\u00E1tica por Internet. [pdf]"
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+39+de+3+de+febrero+de+2010+que+regula+la+obligaci%C3%B3n+de+algunos+sujetos+y+entidades+de+presentar+este+modelo+de+forma+telem%C3%A1tica+por+Internet.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091668898&ssbinary=true"));
		panel.add(getAnchorPanel(mod111,
				"Resoluci\u00F3n 135 de 27 de enero de 2015. [pdf]"
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DResoluci%C3%B3n+135+de+27+de+enero+de+2015.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091668899&ssbinary=true"));
		panel.add(getAnchorPanel(mod111,
				"Orden Foral 104 de 17 de febrero de 2014. [pdf]"
				,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1=attachment%3B+filename%3DOrden+Foral+104+de+17+de+febrero+de+2014.pdf&blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091668900&ssbinary=true"));
		return panel;
	}
	
}

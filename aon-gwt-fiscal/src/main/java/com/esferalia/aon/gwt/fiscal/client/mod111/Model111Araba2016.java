package com.esferalia.aon.gwt.fiscal.client.mod111;


import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class Model111Araba2016 extends Model111Base {

	public Model111Araba2016(IFiscalModelCallback<Mod111> callback) {
		super(callback);
	}

	@Override
	protected void paintParticularyRow(final IFiscalModelCallback<Mod111> callback, IModelScript<Mod111Key> script) {
		if (script.getKeys() == null) return;
		
		if (script.getKeys()[0] == Mod111Key.AR_907) {
			paintRow907(callback,script);
		} else if (script.getKeys()[0] == Mod111Key.AR_908) {
			paintRow908(callback,script);
		} else if (script.getKeys()[0] == Mod111Key.AR_909) {
			paintRow909(callback,script);
		}
	}

	private void paintRow907(final IFiscalModelCallback<Mod111> callback, IModelScript<Mod111Key> script) {
		int row = getTable().getRowCount();
		final FiscalModelDetail ar907 = callback.getFiscalModel().ensureDetail(Mod111Key.AR_907);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
		getTable().getFlexCellFormatter().setColSpan(row, 0, 5);
		
		getTable().setWidget(row, 1, new BoxLabel(Mod111Key.AR_907.getBox()));
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
	private void paintRow908(final IFiscalModelCallback<Mod111> callback, IModelScript<Mod111Key> script) {
		int row = getTable().getRowCount();
		final  FiscalModelDetail ar908 = callback.getFiscalModel().ensureDetail(Mod111Key.AR_908);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
		getTable().getFlexCellFormatter().setColSpan(row, 0, 5);
		
		getTable().setWidget(row, 1, new BoxLabel(Mod111Key.AR_908.getBox()));
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
	private void paintRow909(final IFiscalModelCallback<Mod111> callback, IModelScript<Mod111Key> script) {
		int row = getTable().getRowCount();
		final FiscalModelDetail ar909 = callback.getFiscalModel().ensureDetail(Mod111Key.AR_909);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
		getTable().getFlexCellFormatter().setColSpan(row, 0, 5);
		
		getTable().setWidget(row, 1, new BoxLabel(Mod111Key.AR_909.getBox()));
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
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Informaci\u00F3n tributaria" 
				,"http://www.araba.eus/cs/Satellite?pageid=1193046566413&language=es_ES&tipomodelo=1193045445346&pagename=DiputacionAlava%2FPage%2FDPA_B_listadoModelos&tipoimpuesto=-1&nmodelo=110&anio=2017&aniodesde=2007&aniohasta=2017&btnimpu=Buscar"));
		return list;
	}
}

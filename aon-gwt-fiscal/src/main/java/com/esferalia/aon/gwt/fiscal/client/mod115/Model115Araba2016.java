package com.esferalia.aon.gwt.fiscal.client.mod115;


import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class Model115Araba2016 extends Model115Base {
	
	private static final String DOWNLOAD_TEXT = "Descargar fichero para programa de ayuda.";

	public Model115Araba2016(IFiscalModelCallback<Mod115> callback, AonData aonData) {
		super(callback, aonData);
	}

	public FlowPanel getDeclarationPanel(){
		FlowPanel panel = new FlowPanel();
		panel.setWidth("98%");
		
		FlowPanel formContainer = new FlowPanel();
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(mod115Hidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		formContainer.add(diskForm);

		panel.add(formContainer);
		FlowPanel administrationPanel = getAdministrationPanel(); 
		panel.add(administrationPanel);
		FlowPanel informationPanel = getInformationPanel();
		panel.add(informationPanel);
		return panel;
	}
	
	protected FlowPanel getAdministrationPanel() {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.addStyleName(AON.AON_CSS.aonWidthAll());
		panel.addStyleName(AON.AON_CSS.aonMarginTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingLeft());
		 
		FlexTable tab = new FlexTable();
		tab.getColumnFormatter().setWidth(0, "30px");
		tab.getColumnFormatter().setWidth(1
				, "auto");
		tab.setStyleName(AON.AON_CSS.aonWidth90Percent());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		Label title = new Label("Presentaci\u00F3n del modelo");
		tab.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonMarginTop());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getModel().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;

		Label icon1 = new Label();
		icon1.addStyleName(FiscalModelUtils.getAdministrationIcon(getModel().getAdministration()));
		tab.setWidget(row, 0, icon1 );
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel p1 = new FlowPanel();
		p1.setStyleName(AON.AON_CSS.aonPadding2());
		Button button1 = new Button(DOWNLOAD_TEXT);
		button1.setStyleName(AON.AON_CSS.aonPaddingLeft());
		button1.addStyleName(AON.AON_CSS.aonBorderNone());
		button1.addStyleName(AON.AON_CSS.aonEvenBackground());
		button1.addStyleName(AON.AON_CSS.aonClickable());
		button1.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (getModel().isFinished() || getModel().isSent()) {
					submitForm(MODEL115_FILE);
				} else {
			// TODO		getCallback().showBreakdownPanel("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
				}
			}
		});
		p1.add(button1);
		tab.setWidget(row, 1, p1 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;
		
		panel.add(tab);
		return panel;
	}
	
	@Override
	protected void paintParticularyRow(final IFiscalModelCallback<Mod115> callback, IModelScript<Mod115Key> script) {
		if (script.getKeys() == null) return;
		
		if (script.getKeys()[0] == Mod115Key.AR_907) {
			paintRow907(callback,script);
		} else if (script.getKeys()[0] == Mod115Key.AR_908) {
			paintRow908(callback,script);
		} else if (script.getKeys()[0] == Mod115Key.AR_909) {
			paintRow909(callback,script);
		}
	}

	private void paintRow907(final IFiscalModelCallback<Mod115> callback, IModelScript<Mod115Key> script) {
		int row = getTable().getRowCount();
		final FiscalModelDetail ar907 = callback.getFiscalModel().ensureDetail(Mod115Key.AR_907);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
//		getTable().getFlexCellFormatter().setColSpan(row, 0, 5);
		
		getTable().setWidget(row, 1, new BoxLabel(Mod115Key.AR_907.getBox()));
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
	private void paintRow908(final IFiscalModelCallback<Mod115> callback, IModelScript<Mod115Key> script) {
		int row = getTable().getRowCount();
		final  FiscalModelDetail ar908 = callback.getFiscalModel().ensureDetail(Mod115Key.AR_908);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
//		getTable().getFlexCellFormatter().setColSpan(row, 0, 5);
		
		getTable().setWidget(row, 1, new BoxLabel(Mod115Key.AR_908.getBox()));
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
	
	private void paintRow909(final IFiscalModelCallback<Mod115> callback, IModelScript<Mod115Key> script) {
		int row = getTable().getRowCount();
		final FiscalModelDetail ar909 = callback.getFiscalModel().ensureDetail(Mod115Key.AR_909);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
		
		getTable().setWidget(row, 1, new BoxLabel(Mod115Key.AR_909.getBox()));
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
		list.add(new Pair<String, String>("Formulario Papel. [pdf]" 
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3D115-A.pdf&blobheadervalue2=public&blobkey=id&blobtable="
			+ "MungoBlobs&blobwhere=1224091770466&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 402 de 26 de marzo de 1998. [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf"
			+ "&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1"
			+ "=attachment%3B+filename%3DOrden+Foral+402+de+26+de+marzo+de+1998.pdf&blobheadervalue2"
			+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770467&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 673 de 18 de octubre de 2001. [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf"
			+ "&blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DOrden+Foral+673+de+18+de+octubre+de+2001.pdf&blobheadervalue2"
			+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770468&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 39 de 3 de febrero de 2010 que regula la "
			+ "obligaci\u00F3n de algunos sujetos y entidades de presentar este modelo de forma "
			+ "telem\u00E1tica por Internet."
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DOrden+Foral+39+de+3+de+febrero+de+2010+que+regula+la+"
			+ "obligaci%C3%B3n+de+algunos+sujetos+y+entidades+de+presentar+este+modelo+de+forma"
			+ "+telem%C3%A1tica+por+Internet.pdf&blobheadervalue2=public&blobkey=id&blobtable="
			+ "MungoBlobs&blobwhere=1224091770469&ssbinary=true"));
		list.add(new Pair<String, String>("Resoluci\u00F3n 21 de 12 de enero de 2016 . [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DResoluci%C3%B3n+21+de+12+de+enero+de+2016+.pdf&"
			+ "blobheadervalue2=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770470"
			+ "&ssbinary=true"));
		list.add(new Pair<String, String>("Orden Foral 104 de 17 de febrero de 2014. [pdf]"
			,"http://www.araba.eus/cs/Satellite?blobcol=urldata&blobheader=application%2Fpdf&"
			+ "blobheadername1=Content-disposition&blobheadername2=pragma&blobheadervalue1="
			+ "attachment%3B+filename%3DOrden+Foral+104+de+17+de+febrero+de+2014.pdf&blobheadervalue2"
			+ "=public&blobkey=id&blobtable=MungoBlobs&blobwhere=1224091770471&ssbinary=true"));
		return list;
	}
}

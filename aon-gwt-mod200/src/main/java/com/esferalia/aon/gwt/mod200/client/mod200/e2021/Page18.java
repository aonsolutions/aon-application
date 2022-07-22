// UTES
package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProvinceCountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Mod2002021Object.IMod200ChangeListener;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Model2002021.Model200PageCallback;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.mod200.api.model.UteBase;
import com.esferalia.aon.occam.mod200.api.model.UteParticipation;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;

public class Page18 extends PageAbs {
	
	private static final String FOOTER = "(*) La informaci\u00F3n sobre los datos a incluir en los apartados 6 a 9 anteriores, debe hacer referencia al importe total de las cantidades a imputar por la entidad declarante a las personas o entidades que ostenten los derechos inherentes o la cualidad de socio o de empresa miembro que sean residentes en territorio espa\u00F1ol o no residentes con establecimiento permanente en el mismo.";

	private FlowPanel panelB6;
	private FlowPanel panelB11; 

	public Page18( Model200PageCallback callback ) {
		super(callback);
		
		callback.getMod200Object().register( new IMod200ChangeListener() {
			
			@Override
			public void mod200Changed(Mod2002021 mod200) {
				paintB11Panel(); // La tabla del panel del apartado B11 lleva un dato calculado, por lo tanto es necesario repintarlo, si se recalcula el modelo por cualquier otra casilla
			}
		});
		
	}

	@Override
	protected void initializeTable() {
		paint();		
	}
	
//	@Override
//	protected void populate() {
//	}
	
	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable()
  		  && (callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0013) 
		   || callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0014));
		return av;
	}
	
	private void paint() {
		
		basePanel.clear();
		
		// A) Porcentaje de imputación de bases imponibles y demás conceptos liquidatorios
		
		if (callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0013)) {
			paintKey(addTable(AON.MSG.ute1()), Mod2002021Key.UT060, 0);
		}

		// B) Modelo de información (artículo 46 RIS)		
		
		FlexTable table1 = addTable(AON.MSG.ute2());
		int row = 0;
		paintKey(table1, Mod2002021Key.UT500 , row++);
		paintKey(table1, Mod2002021Key.UT1227, row++);
		paintKey(table1, Mod2002021Key.UT1228, row++);
		paintKey(table1, Mod2002021Key.UT552 , row++);
		paintKey(table1, Mod2002021Key.UT1330, row++);
		
		paintDescription(table1, AON.MSG.ute31(), row++, 0, true);
		
		panelB6 = new FlowPanel();
		paintB6Panel();
		table1.setWidget(row++, 0, panelB6);

		paintKey(table1, Mod2002021Key.UTC01, row++);
		paintDescription(table1, AON.MSG.ute4(), row++, 0, false);
		paintKey(table1, Mod2002021Key.UTC02, row++);
		table1.getCellFormatter().addStyleName(row-1, 0, AON.AON_CSS.aonPadding2Left());
		paintKey(table1, Mod2002021Key.UTC03, row++);
		table1.getCellFormatter().addStyleName(row-1, 0, AON.AON_CSS.aonPadding2Left());
		paintKey(table1, Mod2002021Key.UT062, row++);
		paintDescription(table1, AON.MSG.ute5(), row++, 0, false);
		paintKey(table1, Mod2002021Key.UTC04, row++);
		table1.getCellFormatter().addStyleName(row-1, 0, AON.AON_CSS.aonPadding2Left());
		paintKey(table1, Mod2002021Key.UTC05, row++);
		table1.getCellFormatter().addStyleName(row-1, 0, AON.AON_CSS.aonPadding2Left());
		
		paintDescription(table1, AON.MSG.ute6(), row++, 0, true);
		table1.getFlexCellFormatter().setColSpan(row, 0, 2);
		panelB11 = new FlowPanel();
		paintB11Panel();
		table1.setWidget(row, 0, panelB11);
		
		paintFooterNote(basePanel, FOOTER);
		
	}
	
	private void paintB6Panel() {

		panelB6.clear();
		
		AonDisplayTable tabB6 = new AonDisplayTable();
		tabB6.setWidth("60%");
		tabB6.addStyleName(AON.CSS.aonBlockCenter());
		
		tabB6.addRow()
			.addCell( new Label(AON.MSG.deductionBaseAbbrv()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label("% Participaci\u00F3n"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth40())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getUteBases().size(); i++) {
			final int idx = i;
			
			AonDoubleBox base = new AonDoubleBox();
			base.setValue(callback.getMod200Object().getMod200().getUteBases().get(idx).getBase());
			base.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteBases().get(idx).setBase(base.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(base);
			
			AonDoubleBox percent = new AonDoubleBox();
			percent.setMaxLength(6);
			percent.setVisibleLength(6);
			percent.setValue(callback.getMod200Object().getMod200().getUteBases().get(idx).getPercent());
			percent.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteBases().get(idx).setPercent(percent.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(percent);
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getUteBases().remove(idx);
				paintB6Panel();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);

			tabB6.addRow()
				.addCell(base)
				.addCell(percent)
				.addCell(deleteButton);
			
		}
		
		// Botón añadir 
		AonTableButton addButtonB6 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButtonB6.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getUteBases().add(new UteBase());
			paintB6Panel();
		});
		tabB6.addRow().addCell(addButtonB6);
		otherInputs.add(addButtonB6);
		
		panelB6.add(tabB6);
		
	}
	
	private void paintB11Panel() {
		
		panelB11.clear();
		
		AonDisplayTable tabB11 = new AonDisplayTable();
		tabB11.addStyleName(AON.CSS.aonWidthAlmostAll());
		tabB11.addStyleName(AON.CSS.aonBlockCenter());
		
		tabB11.addRow()
			.addCell( new Label(AON.MSG.document()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label("Rpte."),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20())
			.addCell( new Label(AON.MSG.companyName()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth300())
			.addCell( new Label(AON.MSG.province() + "/" + AON.MSG.country()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
			.addCell( new Label("Base imponible imputada"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label("% Partic."),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth40())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
	
		for (int i = 0; i < callback.getMod200Object().getMod200().getUteParticipations().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getUteParticipations().get(idx).getDocument());
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteParticipations().get(idx).setDocument(document.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(document);
			
			CheckBox rep = new CheckBox();
			rep.setValue(callback.getMod200Object().getMod200().getUteParticipations().get(idx).isRepresentative());
			rep.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					callback.getMod200Object().getMod200().getUteParticipations().get(idx).setRepresentative(rep.getValue());
					callback.markAsDirty();
				}
			});
			otherInputs.add(rep);
			
			AonTextBox name = new AonTextBox();
			name.setMaxLength(30);
			name.setVisibleLength(40);			
			name.setValue(callback.getMod200Object().getMod200().getUteParticipations().get(idx).getName());
			name.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteParticipations().get(idx).setName(name.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(name);
			
			ProvinceCountryListBox provinceCountry = new ProvinceCountryListBox();
			provinceCountry.setSelectedIndex(0);
			UteParticipation ca = callback.getMod200Object().getMod200().getUteParticipations().get(idx);
			int p = ca.getProvince();
			Country c = Country.safeValueOf(ca.getCountry());
			if (p > 0 && p < Province.values().length) {
				provinceCountry.setSelectedIndex(p);				
			} else if (c != null) {				
				provinceCountry.setSelectedIndex(Province.values().length + c.ordinal());
			}	
			provinceCountry.addChangeHandler(new ChangeHandler() {			
				@Override
				public void onChange(ChangeEvent event) {
					int index = provinceCountry.getSelectedIndex();
					if (index < Province.values().length) {
						callback.getMod200Object().getMod200().getUteParticipations().get(idx).setProvince(index);
						callback.getMod200Object().getMod200().getUteParticipations().get(idx).setCountry(null);
					} else {				
						callback.getMod200Object().getMod200().getUteParticipations().get(idx).setProvince(0);
						callback.getMod200Object().getMod200().getUteParticipations().get(idx).setCountry(Country.safeIso2(Country.values()[index-Province.values().length]));
					}
					callback.markAsDirty();
				}
			});
			otherInputs.add(provinceCountry);
			
			Label nominal = new Label();
			nominal.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			nominal.setText(AON.FMT.format(callback.getMod200Object().getMod200().getUteParticipations().get(idx).getBase()));
			nominal.addStyleName(AON.CSS.aonMarginRight());
			
			AonDoubleBox percent = new AonDoubleBox(8,4);
			percent.setMaxLength(8);
			percent.setVisibleLength(8);
			percent.setValue(callback.getMod200Object().getMod200().getUteParticipations().get(idx).getPercent());
			percent.addValueChangeHandler(event -> {
				double per = AonNumberUtils.todouble(percent.getValue());
				callback.getMod200Object().getMod200().getUteParticipations().get(idx).setPercent(per);
				
	    		// Recalcular Base según el porcentaje indicado
	    		double c1330 = callback.getMod200Object().getMod200().getVariable(Mod2002021Key.UT1330).getValue();
	    		double base = AonMathUtils.round(c1330 * per / 100); 
	    		callback.getMod200Object().getMod200().getUteParticipations().get(idx).setBase(base);
	    		nominal.setText(AON.FMT.format(callback.getMod200Object().getMod200().getUteParticipations().get(idx).getBase()));
				callback.markAsDirty();
			});
			otherInputs.add(percent);
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getUteParticipations().remove(idx);
				paintB11Panel();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);
	
			tabB11.addRow()
				.addCell(document)
				.addCell(rep)				
				.addCell(name)
				.addCell(provinceCountry)
				.addCell(nominal)
				.addCell(percent)				
				.addCell(deleteButton);
		}
		
		// Botón añadir 
		AonTableButton addButtonB11 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButtonB11.addStyleName(AON.CSS.aonMarginTop());
		addButtonB11.addStyleName(AON.CSS.aonMarginLeft());
		addButtonB11.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getUteParticipations().add(new UteParticipation());
			paintB11Panel();
		});
		otherInputs.add(addButtonB11);
		
		panelB11.add(tabB11);
		panelB11.add(addButtonB11);
		
	}
	
}

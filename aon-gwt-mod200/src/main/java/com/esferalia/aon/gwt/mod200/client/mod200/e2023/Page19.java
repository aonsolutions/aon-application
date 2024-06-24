// AIE Y UTES
package com.esferalia.aon.gwt.mod200.client.mod200.e2023;

import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Constants.UTE_KEYS_B7;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Constants.UTE_KEYS_B81;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Constants.UTE_KEYS_B82;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProvinceCountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.Model2002023.Model2002023PageCallback;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.UteParticipationBisPanel.UteParticipationBisPanelCallback;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.mod200.api.model.UteBase;
import com.esferalia.aon.occam.mod200.api.model.UteParticipation;
import com.esferalia.aon.occam.mod200.api.model.UteParticipationBis;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;

public class Page19 extends PageAbs {
	
	private static final String FOOTER = "(*) La informaci\u00F3n sobre los datos a incluir en los apartados 6 a 9 anteriores, debe hacer referencia al importe total de las cantidades a imputar por la entidad declarante a las personas o entidades que ostenten los derechos inherentes o la cualidad de socio o de empresa miembro que sean residentes en territorio espa\u00F1ol o no residentes con establecimiento permanente en el mismo.";

	private FlowPanel panelB6;    // 6).- Deducción para evitar la doble imposición
	private FlowPanel panelPar1;  // C) Relación de partícipes
	private FlowPanel panelPar2;  // Partícipes de agrupaciones de interés económico y UTES (cumplimentación voluntaria)
	
	private UteParticipationBisPanel uteParticipationBisPanel;

	public Page19( Model2002023PageCallback callback ) {
		super(callback);
		
		callback.getMod200Object().register( mod200 -> paintPar1Panel() ); // La tabla del panel del apartado C lleva un dato calculado, por lo tanto es necesario repintarlo, si se recalcula el modelo por cualquier otra casilla
		
		uteParticipationBisPanel = new UteParticipationBisPanel( new UteParticipationBisPanelCallback() {
			
			@Override
			public void onCancel() {
				// do nothing
			}
			
			@Override
			public void onAccept(int index, UteParticipationBis up, boolean modified) {
				if (index < 0) {
					callback.getMod200Object().getMod200().getUteParticipationsBis().add(up);
				} else {
					callback.getMod200Object().getMod200().getUteParticipationsBis().set(index, up);
				}
				if (modified) {
					calculate();
					callback.markAsDirty();
					paintPar2Panel();
				}
			}
		});

	}
	
	protected void calculate() {
		// Forzar recalculo del modelo completo
		callback.getMod200Object().calculate();
	}

	@Override
	protected void initializeTable() {
		paint();		
	}
	
	@Override
	protected boolean isAvailable() {
		return super.isAvailable() && isCheckedOr(Mod2002023Key.C0013, Mod2002023Key.C0085, Mod2002023Key.C0014, Mod2002023Key.C0089);				
	}
	
	private void paint() {
		
		basePanel.clear();
		
		// A) Porcentaje de imputación de bases imponibles y demás conceptos liquidatorios
		if (isCheckedOr(Mod2002023Key.C0013, Mod2002023Key.C0085)) {			
			paintKey(addTable(AON.MSG.ute1()), Mod2002023Key.UT060, 0);
		}

		// B) Datos Económicos
		if (isCheckedOr(Mod2002023Key.C0013, Mod2002023Key.C0085, Mod2002023Key.C0014)) {
		
			FlexTable table1 = addTable("B) Datos Econ\u00F3micos");
			int row = 0;
			paintKey(table1, Mod2002023Key.UT500 , row++);
			paintKey(table1, Mod2002023Key.UT1227, row++);
			paintKey(table1, Mod2002023Key.UT1228, row++);
			paintKey(table1, Mod2002023Key.UT552 , row++);
			paintKey(table1, Mod2002023Key.UT1330, row++);
			
			// Deducción para evitar la doble imposición
			paintDescription(table1, AON.MSG.ute31(), row++, 0, false);		
			panelB6 = new FlowPanel();
			paintB6Panel();
			table1.setWidget(row++, 0, panelB6);
	
			row = paintTable(table1, row, UTE_KEYS_B7 , "7.- Bonificaciones:", "Base de la bonificaci\u00F3n", "Importe de la bonificaci\u00F3n");
			row = paintTable(table1, row, UTE_KEYS_B81, "8.- Deducciones generadas en el periodo impositivo:", "Base de la deducci\u00F3n", "Importe de la deducci\u00F3n");
			row = paintTable(table1, row, UTE_KEYS_B82, "Informaci\u00F3n adicional para el c\u00E1lculo de l\u00EDmites de deducciones:");
			
			paintKey(table1, Mod2002023Key.UT062, row++);
			paintDescription(table1, AON.MSG.ute5(), row++, 0, false);
			paintKey(table1, Mod2002023Key.UT070, row++); table1.getCellFormatter().addStyleName(row-1, 0, AON.AON_CSS.aonPadding2Left());
			paintKey(table1, Mod2002023Key.UT072, row++); table1.getCellFormatter().addStyleName(row-1, 0, AON.AON_CSS.aonPadding2Left());
			
			paintFooterNote(basePanel, FOOTER);
			
			// Relación de Partícipes
			panelPar1 = new FlowPanel();
			paintPar1Panel();
			addTable("C) Relaci\u00F3n de Part\u00EDcipes").setWidget(0, 0, panelPar1);
		
		}
		
		// Partícipes de agrupaciones de interés económico y UTES (cumplimentación voluntaria) (solo si caracter 00089 marcado)
		if (isChecked(Mod2002023Key.C0089)) {
			panelPar2 = new FlowPanel();
			paintPar2Panel();
			addTable("Part\u00EDcipes de agrupaciones de inter\u00E9s econ\u00F3mico y UTES (cumplimentaci\u00F3n voluntaria)").setWidget(0, 0, panelPar2);
		}		
		
	}
	
	private int paintTable(FlexTable table1, int row, Mod2002023Key[][] keys, String title, String... headers) {
		
		paintDescription(table1, title, row, 0, false); 
		int col = 1;
		for (String header : headers) {
			addHeaderCell(table1, row, col++, header, true);	
		}		
		row++;
		
		for (Mod2002023Key[] key : keys) {
			paintKeyDescription(table1, key[0], row, 0);			
			table1.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPadding2Left()); 
			paintKeyField(table1, key[0], row, 1);
			paintKeyField(table1, key[1], row, 2);
			row++;
		}
		
		return row;
		
	}
	
	// 6.- Deducción para evitar la doble imposición
	private void paintB6Panel() {

		panelB6.clear();
		
		AonDisplayTable tabB6 = new AonDisplayTable();
		tabB6.setWidth("60%");
		tabB6.addStyleName(AON.CSS.aonBlockCenter());
		
		tabB6.addRow()
			.addCell( new Label(AON.MSG.deductionBaseAbbrv()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label("Importe de la deducci\u00F3n"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label("% Participaci\u00F3n"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth40())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getUteBases().size(); i++) {
			final int idx = i;
			
			AonDoubleBox base = new AonDoubleBox();
			base.setValue(callback.getMod200Object().getMod200().getUteBases().get(idx).getBase());
			base.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteBases().get(idx).setBase(base.getValue());
				calculate();
				callback.markAsDirty();
			});
			otherInputs.add(base);
			
			AonDoubleBox amount = new AonDoubleBox();
			amount.setValue(callback.getMod200Object().getMod200().getUteBases().get(idx).getAmount());
			amount.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteBases().get(idx).setAmount(amount.getValue());
				calculate();
				callback.markAsDirty();
			});
			otherInputs.add(amount);
			
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
				calculate();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);

			tabB6.addRow()
				.addCell(base)
				.addCell(amount)
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
	
	// C) Relación de partícipes
	private void paintPar1Panel() {
		
		panelPar1.clear();
		
        paintLabel(panelPar1, "Relaci\u00F3n de part\u00EDcipes existentes a la fecha de cierre del per\u00EDodo impositivo, que deban soportar las imputaciones, en orden decreciente de grado de participaci\u00F3n, con sus datos identificativos y grado de participaci\u00F3n en dicha fecha:", false);
		
		AonDisplayTable tabC = new AonDisplayTable();
		tabC.addStyleName(AON.CSS.aonWidthAlmostAll());
		tabC.addStyleName(AON.CSS.aonBlockCenter());
		
		tabC.addRow()
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
			rep.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getUteParticipations().get(idx).setRepresentative(rep.getValue());
				callback.markAsDirty();				
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
			provinceCountry.addChangeHandler(event -> {
				int index = provinceCountry.getSelectedIndex();
				if (index < Province.values().length) {
					callback.getMod200Object().getMod200().getUteParticipations().get(idx).setProvince(index);
					callback.getMod200Object().getMod200().getUteParticipations().get(idx).setCountry(null);
				} else {				
					callback.getMod200Object().getMod200().getUteParticipations().get(idx).setProvince(0);
					callback.getMod200Object().getMod200().getUteParticipations().get(idx).setCountry(Country.safeIso2(Country.values()[index-Province.values().length]));
				}
				callback.markAsDirty();
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
	    		double c1330 = callback.getMod200Object().getMod200().getVariable(Mod2002023Key.UT1330).getValue();
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
				paintPar1Panel();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);
	
			tabC.addRow()
				.addCell(document)
				.addCell(rep)				
				.addCell(name)
				.addCell(provinceCountry)
				.addCell(nominal)
				.addCell(percent)				
				.addCell(deleteButton);
		}
		
		// Botón añadir 
		AonTableButton addButtonC = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButtonC.addStyleName(AON.CSS.aonMarginTop());
		addButtonC.addStyleName(AON.CSS.aonMarginLeft());
		addButtonC.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getUteParticipations().add(new UteParticipation());
			paintPar1Panel();
		});
		otherInputs.add(addButtonC);
		
		panelPar1.add(tabC);
		panelPar1.add(addButtonC);
		
	}
	
	private void paintPar2Panel() {
		
		panelPar2.clear();
		
		AonDisplayGrid grid = new AonDisplayGrid();
//		grid.addStyleName(AON.CSS.aonWidthAlmostAll());
//		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.getElement().getStyle().setProperty("margin-left", "1%");
		
		grid.addHeaderRow()
			.addCell(new Label(AON.MSG.document()),AON.CSS.aonWidth120())
			.addCell(new Label("Nombre o raz\u00F3n social"),AON.CSS.aonWidthAuto())
			.addCell(new Label("Provincia/Pais"),AON.CSS.aonWidth120())
			.addCell(new Label("Valoracion Comienzo"),AON.CSS.aonWidth150())
			.addCell(new Label("Valoracion Final"),AON.CSS.aonWidth150())
			.addCell(new Label("Ingresos financieros"),AON.CSS.aonWidth150())
			.addCell(new Label(""),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getUteParticipationsBis().size(); i++) {
			final int idx = i;
			
			Label documentLabel = new Label();
			documentLabel.setText(callback.getMod200Object().getMod200().getUteParticipationsBis().get(idx).getDocument());
			
			Label nameLabel = new Label();
			nameLabel.setText(callback.getMod200Object().getMod200().getUteParticipationsBis().get(idx).getName());

			Label provinceLabel = new Label();
			int idProvince = callback.getMod200Object().getMod200().getUteParticipationsBis().get(idx).getProvince();
			if (idProvince > 0 && idProvince < Province.values().length) {
				provinceLabel.setText(Province.safeValueOf(idProvince).getName());
			} else {
				Country c = Country.safeValueOf(callback.getMod200Object().getMod200().getUteParticipationsBis().get(idx).getCountry());
				if (c != null) {
					provinceLabel.setText(c.getName());
				}
			}
			
			Label value1Label = new Label();
			value1Label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			value1Label.setText(AON.FMT.format(callback.getMod200Object().getMod200().getUteParticipationsBis().get(idx).getC01279()));
			
			Label value2Label = new Label();
			value2Label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			value2Label.setText(AON.FMT.format(callback.getMod200Object().getMod200().getUteParticipationsBis().get(idx).getC01455()));
			
			Label value3Label = new Label();
			value3Label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			value3Label.setText(AON.FMT.format(callback.getMod200Object().getMod200().getUteParticipationsBis().get(idx).getC01456()));
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getUteParticipationsBis().remove(idx);
				paintPar2Panel();
				calculate();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);
			
			grid.addRow()
				.addCell(documentLabel)
				.addCell(nameLabel)
				.addCell(provinceLabel)
				.addCell(value1Label)
				.addCell(value2Label)
				.addCell(value3Label)
				.addCell(deleteButton)			
			    .addClickHandler( event -> {
					uteParticipationBisPanel.dump(idx, callback.getMod200Object().getMod200().getUteParticipationsBis().get(idx), isEditable());
					uteParticipationBisPanel.center();
					uteParticipationBisPanel.show();
			    });	
		}
		
		// Botón añadir 
		AonTableButton addButton = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton.addStyleName(AON.CSS.aonMarginTop());
		addButton.addStyleName(AON.CSS.aonMarginLeft());
		addButton.addClickHandler(event -> {
			UteParticipationBis up = new UteParticipationBis();			
			uteParticipationBisPanel.dump(-1, up, true);
			uteParticipationBisPanel.center();
			uteParticipationBisPanel.show();
		});
		otherInputs.add(addButton);
		
		panelPar2.add(grid);
		panelPar2.add(addButton);
	
	}
	
}


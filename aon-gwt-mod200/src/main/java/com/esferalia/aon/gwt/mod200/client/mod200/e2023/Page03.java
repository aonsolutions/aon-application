// PARTICIPACIONES, ENTIDADES MENORES, EP, SOCIOS DE SICAV 
package com.esferalia.aon.gwt.mod200.client.mod200.e2023;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceCountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.Model2002023.Model2002023PageCallback;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.ParticipationPanel.ParticipationPanelCallback;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.mod200.api.model.MinorEntity;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyParticipation;
import com.esferalia.aon.occam.mod200.api.model.UteForeign;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;

public class Page03 extends PageAbs {
	
	private ParticipationPanel participationPanel;

	public Page03( Model2002023PageCallback callback ) {
		super(callback);	
		
		participationPanel = new ParticipationPanel( new ParticipationPanelCallback() {
			
			@Override
			public void onCancel() {
				// do nothing
			}
			
			@Override
			public void onAccept(int index, Mod200CompanyParticipation cp, boolean modified) {
				if (index < 0) {
					callback.getMod200Object().getMod200().getParticipationsOut().add(cp);
				} else {
					callback.getMod200Object().getMod200().getParticipationsOut().set(index, cp);
				}
				if (modified) {
					calculate();
					callback.markAsDirty();
					paint();
				}
			}
		});
	}

	protected void calculate() {
		// Forzar recalculo del modelo completo
		callback.getMod200Object().doubleValueChanged(Mod2002023Key.P1501, callback.getMod200Object().getDoubleValue(Mod2002023Key.P1501));
	}

	@Override
	protected void initializeTable() {
		paint();
	}

	private void paint() {
		
		otherInputs.clear();
		basePanel.clear();
		
		// B.1. PARTICIPACIONES DE LA DECLARANTE EN OTRAS ENTIDADES		
		
		basePanel.add(getTitle(AON.MSG.participationsOut()));
		
		addLabel(PARTICIPATIONS_LABEL, true);

		AonDisplayGrid grid = new AonDisplayGrid();
//		grid.addStyleName(AON.CSS.aonWidthAlmostAll());
//		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.getElement().getStyle().setProperty("margin-left", "1%");
		basePanel.add(grid);
		
		grid.addHeaderRow()
			.addCell(new Label(AON.MSG.document()),AON.CSS.aonWidth100())
			.addCell(new Label(AON.MSG.companyName()),AON.CSS.aonWidthAuto())
			.addCell(new Label("%"),AON.CSS.aonWidth40())
			.addCell(new Label(AON.MSG.nominalValue()),AON.CSS.aonWidth100())
			.addCell(new Label(""),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getParticipationsOut().size(); i++) {
			final int idx = i;
			
			Label documentLabel = new Label();
			documentLabel.setText(callback.getMod200Object().getMod200().getParticipationsOut().get(idx).getDocument());
			Label nameLabel = new Label();
			nameLabel.setText(callback.getMod200Object().getMod200().getParticipationsOut().get(idx).getName());
			Label percentLabel = new Label();
			percentLabel.setText(AON.FMT.format(callback.getMod200Object().getMod200().getParticipationsOut().get(idx).getPercent()));
			Label nominalLabel = new Label();
			nominalLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			nominalLabel.setText(AON.FMT.format(callback.getMod200Object().getMod200().getParticipationsOut().get(idx).getNominalValue()));
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsOut().remove(idx);
				paint();
				calculate();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);
			
			grid.addRow()
				.addCell(documentLabel)
				.addCell(nameLabel)
				.addCell(percentLabel)
				.addCell(nominalLabel)				
				.addCell(deleteButton)			
			    .addClickHandler( event -> {
					participationPanel.dump(idx, callback.getMod200Object().getMod200().getParticipationsOut().get(idx), isEditable());
					participationPanel.center();
					participationPanel.show();
			    });	
		}
		
		// Botón añadir participationOut
		AonTableButton addButton = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton.addStyleName(AON.CSS.aonMarginTop());
		addButton.addStyleName(AON.CSS.aonMarginLeft());
		addButton.addClickHandler(event -> {
			Mod200CompanyParticipation cp = new Mod200CompanyParticipation();
			participationPanel.dump(-1, cp, true);
			participationPanel.center();
			participationPanel.show();
		});
		otherInputs.add(addButton);
		basePanel.add(addButton);
		
		basePanel.add(getSubtitle(AON.MSG.totals()));
		
		FlexTable tab1 = new FlexTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);
		
		int row = 0;
		for (final Mod2002023Key key : Mod2002023Constants.PARTICIPATION_KEYS) {
			if (callback.getMod200Object().isVisible(key)) {
				row = paintKey(tab1, key, row);
			}
		}
		
		// B.2. PARTICIPACIONES DE PERSONAS O ENTIDADES EN LA DECLARANTE
		
		basePanel.add(getTitle(AON.MSG.participationsIn()));

		addLabel(PARTICIPATIONS_LABEL, true);
		addLabel("En caso de sociedades de responsabilidad limitada (SL) se deber\u00E1n cumplimentar, al menos, los datos correspondientes a uno de los socios aunque el porcentaje de participaci\u00F3n sea inferior al indicado.");
		
		AonDisplayTable tab2 = addRegistryTable(AON.MSG.document(), "Rpte.", "F/J/Otra", AON.MSG.companyName(), AON.MSG.province() + "/" + AON.MSG.country(), "% Particip.", AON.MSG.nominalValue());	
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getParticipationsIn().size(); i++) {
			final int idx = i;
			
			AonTextBox fjo = new AonTextBox();
			AonDocumentTextBox document = new AonDocumentTextBox();
			document.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).getDocument());
			document.addValueChangeHandler(event -> {				
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setDocument(document.getValue());
				
				// Poner por defecto el valor de F/J/O según validaciones del documento PADIS
				// el código J para contribuyentes cuyo NIF comience por A, B, C, D, F, J, P, Q, R y S 
				// el código O para contribuyentes cuyo NIF comience por E, H, U, N y W
				// el supuesto de NIF que empiece por V o G, la casilla F/J/O queda en blanco para que el declarante cumplimente J u O
				// para el resto se asume F (persona física)
				if (AonStringUtils.isNotEmpty(document.getValue())) {
					if (document.getValue().matches("^(A|B|C|D|F|J|P|Q|R|S).*")) {
						fjo.setValue("J", true);
					}
					else if (document.getValue().matches("^(E|H|U|N|W).*")) {
						fjo.setValue("O", true);
					}
					else if (document.getValue().matches("^(G|V).*")) {
						fjo.setValue("", true);
					}
					else { 
						fjo.setValue("F", true);
					}
				}
				
				callback.markAsDirty();
			});
			otherInputs.add(document);
			
			CheckBox rep = new CheckBox();			
			rep.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).isRepresentative());
			rep.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setRepresentative(rep.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(rep);
			
			// El valor de "fjo" se guarda en el campo notary de la tabla 
			//AonTextBox fjo = new AonTextBox();
			fjo.setMaxLength(1);
			fjo.setVisibleLength(1);			
			fjo.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).getNotary());
			fjo.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setNotary(fjo.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(fjo);
			
			AonTextBox name = new AonTextBox();
			name.setMaxLength(37);
			name.setVisibleLength(37);			
			name.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).getName());
			name.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setName(name.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(name);
			
			ProvinceCountryListBox provinceCountry = new ProvinceCountryListBox();
			provinceCountry.setSelectedIndex(0);
			Mod200CompanyParticipation ca = callback.getMod200Object().getMod200().getParticipationsIn().get(idx);
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
					callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setProvince(index);
					callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setCountry(null);
				} else {				
					callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setProvince(0);
					callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setCountry(Country.safeIso2(Country.values()[index-Province.values().length]));
				}
				callback.markAsDirty();				
			});
			otherInputs.add(provinceCountry);
			
			AonDoubleBox percent = new AonDoubleBox();
			percent.setMaxLength(6);
			percent.setVisibleLength(6);
			percent.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).getPercent());
			percent.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setPercent(percent.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(percent);
			
			AonDoubleBox nominal = new AonDoubleBox();
			nominal.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).getNominalValue());
			nominal.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setNominalValue(nominal.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(nominal);
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().remove(idx);
				paint();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);

			tab2.addRow()
				.addCell(document)
				.addCell(rep)
				.addCell(fjo)
				.addCell(name)
				.addCell(provinceCountry)
				.addCell(percent)
				.addCell(nominal)
				.addCell(deleteButton);
		}
		
		// Botón añadir participationIn
		AonTableButton addButton2 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton2.addStyleName(AON.CSS.aonMarginTop());
		addButton2.addStyleName(AON.CSS.aonMarginLeft());
		addButton2.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getParticipationsIn().add(new Mod200CompanyParticipation());
			paint();
		});
		otherInputs.add(addButton2);
		basePanel.add(addButton2);
		
		FlexTable tab3 = new FlexTable();
		tab3.setStyleName(AON.CSS.aonMarginTop());
		tab3.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab3.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab3);
		
		paintKey(tab3, Mod2002023Key.POR51, 0);
		paintKey(tab3, Mod2002023Key.PORES, 1);
		
		// C. ENTIDADES MENORES
		
		basePanel.add(getTitle(AON.MSG.minorEntities()));
		
		AonDisplayTable tab4 = addRegistryTable(AON.MSG.document(), AON.MSG.companyName());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getMinorEntities().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();
			document.setMaxLength(9);
			document.setValue(callback.getMod200Object().getMod200().getMinorEntities().get(idx).getDocument());
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getMinorEntities().get(idx).setDocument(document.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(document);
			
			AonTextBox name = new AonTextBox();
			name.setMaxLength(40);
			name.setVisibleLength(40);			
			name.setValue(callback.getMod200Object().getMod200().getMinorEntities().get(idx).getName());
			name.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getMinorEntities().get(idx).setName(name.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(name);
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getMinorEntities().remove(idx);
				paint();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);

			tab4.addRow()
				.addCell(document)
				.addCell(name)
				.addCell(deleteButton);
		}
		
		// Botón añadir minor
		AonTableButton addButton4 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton4.addStyleName(AON.CSS.aonMarginTop());
		addButton4.addStyleName(AON.CSS.aonMarginLeft());
		addButton4.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getMinorEntities().add(new MinorEntity());
			paint();
		});
		otherInputs.add(addButton4);
		basePanel.add(addButton4);
		
		// D. INFORMACIÓN DE DETALLE DE EP QUE OPERE EN EL EXTRANJERO
		
		basePanel.add(getTitle("Informaci\u00F3n de detalle de establecimiento permanente que opere en el extranjero"));
		
		AonDisplayTable tabForeign = addRegistryTable();
		
		tabForeign.addRow()
				.addCell( new Label(AON.MSG.identification()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom())
				.addCell( new Label(AON.MSG.utefor1()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
				.addCell( new Label(AON.MSG.utefor2()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
				.addCell( new Label(AON.MSG.utefor3()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth200())
				.addCell( new Label(AON.MSG.utefor4()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth200())
				.addCell( new Label(AON.MSG.utefor5()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth200())
				.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getUteForeign().size(); i++) {
			final int idx = i;
			
			AonTextBox identification = new AonTextBox();
			identification.setMaxLength(30);
			identification.setVisibleLength(30);
			identification.setValue(callback.getMod200Object().getMod200().getUteForeign().get(idx).getIdentification());
			identification.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteForeign().get(idx).setIdentification(identification.getValue());
				callback.markAsDirty();
			});			
			otherInputs.add(identification);
			
			CountryListBox country = new CountryListBox();
			country.setWidth("140px");
			country.setValue(Country.safeValueOf(callback.getMod200Object().getMod200().getUteForeign().get(idx).getCountry()));
			country.addChangeHandler(event -> {				
				callback.getMod200Object().getMod200().getUteForeign().get(idx).setCountry(Country.safeIso2(country.getValue()));
				callback.markAsDirty();				
			});
			otherInputs.add(country);
			
			AonDoubleBox volume = new AonDoubleBox();
			volume.setValue(callback.getMod200Object().getMod200().getUteForeign().get(idx).getVolume());
			volume.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteForeign().get(idx).setVolume(volume.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(volume);
			
			AonDoubleBox pyg = new AonDoubleBox();
			pyg.setValue(callback.getMod200Object().getMod200().getUteForeign().get(idx).getPyg());
			pyg.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteForeign().get(idx).setPyg(pyg.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(pyg);
			
			AonDoubleBox adjust = new AonDoubleBox();
			adjust.setValue(callback.getMod200Object().getMod200().getUteForeign().get(idx).getAdjust());
			adjust.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteForeign().get(idx).setAdjust(adjust.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(adjust);
			
			AonDoubleBox deduction = new AonDoubleBox();
			deduction.setValue(callback.getMod200Object().getMod200().getUteForeign().get(idx).getDeduction());
			deduction.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteForeign().get(idx).setDeduction(deduction.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(deduction);
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getUteForeign().remove(idx);
				paint();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);
	
			tabForeign.addRow()
				.addCell(identification)
				.addCell(country)				
				.addCell(volume)
				.addCell(pyg)
				.addCell(adjust)
				.addCell(deduction)				
				.addCell(deleteButton);
		}
		
		// Botón añadir 
		AonTableButton addButtonForeign = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButtonForeign.addStyleName(AON.CSS.aonMarginTop());
		addButtonForeign.addStyleName(AON.CSS.aonMarginLeft());
		addButtonForeign.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getUteForeign().add(new UteForeign());
			paint();			
		});		
		otherInputs.add(addButtonForeign);
		basePanel.add(addButtonForeign);
		
		// E. SOCIOS DE SICAV EN RÉGIMEN ESPECIAL DE DISOLUCIÓN Y LIQUIDACIÓN (DT 41ª LIS)
		
		basePanel.add(getTitle("SOCIOS DE SICAV EN R\u00C9GIMEN ESPECIAL DE DISOLUCI\u00D3N Y LIQUIDACI\u00D3N (DT 41\u00AA LIS)"));
		addLabel("Los socios de SICAV en r\u00E9gimen especial de disoluci\u00F3n y liquidaci\u00F3n (DT 41\u00AA LIS) consignar\u00E1n, a continuaci\u00F3n, los siguientes datos:");
		
		// NIF de la/las sociedad/es disuelta/s
		
		AonDisplayTable tabSicav1 = new AonDisplayTable();
		tabSicav1.setWidth("30%");
		tabSicav1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tabSicav1);
				
		tabSicav1.addRow()
			.addCell( new Label("NIF de la/las sociedad/es disuelta/s"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())			
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());		
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getSicav1().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getSicav1().get(idx));
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getSicav1().set(idx, document.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(document);
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getSicav1().remove(idx);
				paint();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);

			tabSicav1.addRow()
				.addCell(document)
				.addCell(deleteButton);
			
		}
		
		// Botón añadir 
		AonTableButton addButtonSicav1 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButtonSicav1.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getSicav1().add("");
			paint();
		});
		otherInputs.add(addButtonSicav1);
		tabSicav1.addRow().addCell(addButtonSicav1);
		
		// NIF de la/las IIC donde reinvierte
		
		AonDisplayTable tabSicav2 = new AonDisplayTable();
		tabSicav2.setWidth("30%");
		tabSicav2.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tabSicav2);
				
		tabSicav2.addRow()
			.addCell( new Label("NIF de la/las IIC donde reinvierte"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())			
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());		
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getSicav2().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getSicav2().get(idx));
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getSicav2().set(idx, document.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(document);
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getSicav2().remove(idx);
				paint();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);

			tabSicav2.addRow()
				.addCell(document)
				.addCell(deleteButton);
			
		}
		
		// Botón añadir 
		AonTableButton addButtonSicav2 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButtonSicav2.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getSicav2().add("");
			paint();
		});
		otherInputs.add(addButtonSicav2);
		tabSicav2.addRow().addCell(addButtonSicav2);
		
	}

}


// PARTICIPACIONES
package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProvinceCountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Model2002021.Model200PageCallback;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.ParticipationPanel.ParticipationPanelCallback;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.mod200.api.model.MinorEntity;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyParticipation;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;

public class Page02 extends PageAbs {
	
	private ParticipationPanel participationPanel;

	public Page02( Model200PageCallback callback ) {
		super(callback);
		
		participationPanel = new ParticipationPanel( new ParticipationPanelCallback() {
			
			@Override
			public void onCancel() {
				paint();
			}
			
			@Override
			public void onAccept(int index, Mod200CompanyParticipation cp) {
				if (index < 0) {
					callback.getMod200Object().getMod200().getParticipationsOut().add(cp);
				} else {
					callback.getMod200Object().getMod200().getParticipationsOut().set(index,cp);
				}
				paint();
				calculate();
				callback.markAsDirty();
			}
		});
		
		addBasePanel();
		initializeTable();
		
	}

	protected void calculate() {
		populate();
		callback.getMod200Object().doubleValueChanged(Mod2002021Key.P1501,callback.getMod200Object().getDoubleValue(Mod2002021Key.P1501));
	}

	@Override
	public void dump() {
		super.dump();
	}

	@Override
	protected void initializeTable() {
		paint();
	}

	@Override
	public void populate() {
	}
	
	private void paint() {
		
		// PARTICIPACIONES DE LA DECLARANTE EN OTRAS SOCIEDADES
		basePanel.clear();
		
		basePanel.add(getTitle(AON.MSG.participationsOut()));
		
		AonDisplayGrid grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonWidthAlmostAll());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.addStyleName(AON.CSS.aonMarginTop());
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
				callback.markAsDirty();
			});
			
			grid.addRow()
				.addCell(documentLabel)
				.addCell(nameLabel)
				.addCell(percentLabel)
				.addCell(nominalLabel)				
				.addCell(deleteButton)			
			    .addClickHandler( event -> {					
					participationPanel.dump(idx, callback.getMod200Object().getMod200().getParticipationsOut().get(idx));
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
			participationPanel.dump(-1,cp);
			participationPanel.center();
			participationPanel.show();
		});
		basePanel.add(addButton);
		
		basePanel.add(getSubtitle(AON.MSG.totals()));
		
		FlexTable tab1 = new FlexTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);
		
		int row = 0;
		for (final Mod2002021Key key : Mod2002021Constants.PARTICIPATION_KEYS) {
			if (callback.getMod200Object().isVisible(key)) {
				row = paintKey(tab1, key, row);
			}
		}
		
		// PARTICIPACIONES DE PERSONAS O ENTIDADES EN LA DECLARANTE
		
		basePanel.add(getTitle(AON.MSG.participationsIn()));
		
		AonDisplayTable tab2 = new AonDisplayTable();
		tab2.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab2);
		
		tab2.addRow()
			.addCell( new Label(AON.MSG.document()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label("Rpte."),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20())
			.addCell( new Label("F/J/Otra"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20())
			.addCell( new Label(AON.MSG.companyName()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth300())
			.addCell( new Label(AON.MSG.province() + "/" + AON.MSG.country()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
			.addCell( new Label("%"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth40())
			.addCell( new Label(AON.MSG.nominalValue()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getParticipationsIn().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).getDocument());
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setDocument(document.getValue());
				callback.markAsDirty();
			});
			
			CheckBox rep = new CheckBox();
			rep.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).isRepresentative());
			rep.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setRepresentative(rep.getValue());
					callback.markAsDirty();
				}
			});
			
			// El valor de "fjo" se guarda en el campo notary de la tabla 
			AonTextBox fjo = new AonTextBox();
			fjo.setMaxLength(1);
			fjo.setVisibleLength(1);			
			fjo.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).getNotary());
			fjo.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setNotary(fjo.getValue());
				callback.markAsDirty();
			});
			
			AonTextBox name = new AonTextBox();
			name.setMaxLength(45);
			name.setVisibleLength(45);			
			name.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).getName());
			name.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setName(name.getValue());
				callback.markAsDirty();
			});
			
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
			provinceCountry.addChangeHandler(new ChangeHandler() {			
				@Override
				public void onChange(ChangeEvent event) {
					int index = provinceCountry.getSelectedIndex();
					if (index < Province.values().length) {
						callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setProvince(index);
						callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setCountry(null);
					} else {				
						callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setProvince(0);
						callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setCountry(Country.safeIso2(Country.values()[index-Province.values().length]));
					}
					callback.markAsDirty();
				}
			});
			
			AonDoubleBox percent = new AonDoubleBox();
			percent.setMaxLength(6);
			percent.setVisibleLength(6);
			percent.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).getPercent());
			percent.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setPercent(percent.getValue());
				callback.markAsDirty();
			});
			
			AonDoubleBox nominal = new AonDoubleBox();
			nominal.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).getNominalValue());
			nominal.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setNominalValue(nominal.getValue());
				callback.markAsDirty();
			});
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().remove(idx);
				paint();
				callback.markAsDirty();
			});

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
		basePanel.add(addButton2);
		
		FlexTable tab3 = new FlexTable();
		tab3.setStyleName(AON.CSS.aonMarginTop());
		tab3.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab3.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab3);
		
		paintKey(tab3, Mod2002021Key.POR51, 0);
		paintKey(tab3, Mod2002021Key.PORES, 1);
		
		// ENTIDADES MENORES
		
		basePanel.add(getTitle(AON.MSG.minorEntities()));
		
		AonDisplayTable tab4 = new AonDisplayTable();
		tab4.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab4.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab4);
		
		tab4.addRow()
			.addCell( new Label(AON.MSG.document()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label(AON.MSG.companyName()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth300())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getMinorEntities().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getMinorEntities().get(idx).getDocument());
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getMinorEntities().get(idx).setDocument(document.getValue());
				callback.markAsDirty();
			});
			
			AonTextBox name = new AonTextBox();
			name.setMaxLength(40);
			name.setVisibleLength(40);			
			name.setValue(callback.getMod200Object().getMod200().getMinorEntities().get(idx).getName());
			name.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getMinorEntities().get(idx).setName(name.getValue());
				callback.markAsDirty();
			});
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getMinorEntities().remove(idx);
				paint();
				callback.markAsDirty();
			});

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
		basePanel.add(addButton4);		
		
	}

}

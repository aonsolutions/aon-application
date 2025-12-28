// DATOS ESTADÍSTICOS
package com.esferalia.aon.gwt.fiscal.client.mod425.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod425.e2025.Model4252025.Model4252025Callback;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Activity425;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page01 extends PageAbs {

	private AonTextBox mainActivityDescription = new AonTextBox(); 				// Descripción	
	private ActivityTypeListBox mainActivityType = new ActivityTypeListBox(); 	// Clave
	private AonTextBox mainActivityEpigraph = new AonTextBox(); 				// Epígrafe IAE
	private RegimeTypeListBox mainRegimeType = new RegimeTypeListBox();			// Régimen aplicable (Código)
	private AonDoubleBox mainProvisionalProrate = new AonDoubleBox(); 			// % provisional prorrata general
	private AonDoubleBox mainFinalProrate = new AonDoubleBox(); 				// % definitivo prorrata general
	private CheckBox mainSpecialProrate = new CheckBox();  						// Prorrata especial
	
	private AonTextBox activity1Description = new AonTextBox();
	private ActivityTypeListBox activity1Type = new ActivityTypeListBox();
	private AonTextBox activity1Epigraph = new AonTextBox();
	private RegimeTypeListBox activity1RegimeType = new RegimeTypeListBox(); 
	private AonDoubleBox activity1ProvisionalProrate = new AonDoubleBox(); 
	private AonDoubleBox activity1FinalProrate = new AonDoubleBox(); 
	private CheckBox activity1SpecialProrate = new CheckBox();  

	private AonTextBox activity2Description = new AonTextBox();
	private ActivityTypeListBox activity2Type = new ActivityTypeListBox();
	private AonTextBox activity2Epigraph = new AonTextBox();
	private RegimeTypeListBox activity2RegimeType = new RegimeTypeListBox();
	private AonDoubleBox activity2ProvisionalProrate = new AonDoubleBox(); 
	private AonDoubleBox activity2FinalProrate = new AonDoubleBox(); 
	private CheckBox activity2SpecialProrate = new CheckBox();  

	private AonTextBox activity3Description = new AonTextBox();
	private ActivityTypeListBox activity3Type = new ActivityTypeListBox();
	private AonTextBox activity3Epigraph = new AonTextBox();
	private RegimeTypeListBox activity3RegimeType = new RegimeTypeListBox();
	private AonDoubleBox activity3ProvisionalProrate = new AonDoubleBox(); 
	private AonDoubleBox activity3FinalProrate = new AonDoubleBox(); 
	private CheckBox activity3SpecialProrate = new CheckBox();  
	
	private AonTextBox activity4Description = new AonTextBox();
	private ActivityTypeListBox activity4Type = new ActivityTypeListBox();
	private AonTextBox activity4Epigraph = new AonTextBox();
	private RegimeTypeListBox activity4RegimeType = new RegimeTypeListBox();
	private AonDoubleBox activity4ProvisionalProrate = new AonDoubleBox(); 
	private AonDoubleBox activity4FinalProrate = new AonDoubleBox(); 
	private CheckBox activity4SpecialProrate = new CheckBox();  
	
	private AonTextBox activity5Description = new AonTextBox();
	private ActivityTypeListBox activity5Type = new ActivityTypeListBox();
	private AonTextBox activity5Epigraph = new AonTextBox();
	private RegimeTypeListBox activity5RegimeType = new RegimeTypeListBox();
	private AonDoubleBox activity5ProvisionalProrate = new AonDoubleBox(); 
	private AonDoubleBox activity5FinalProrate = new AonDoubleBox(); 
	private CheckBox activity5SpecialProrate = new CheckBox();  
	
	private CheckBox mod415 = new CheckBox();  // Obligado a presentar el modelo 415  
	private CheckBox accrual = new CheckBox(); // Destinatario de operaciones RECC
	private AonDocumentTextBox mergedDeclarationDocument = new AonDocumentTextBox(); // Declaración de sujeto pasivo incluido en autoliquidaciones conjuntas - NIF 
	private AonTextBox mergedDeclarationName = new AonTextBox(); 					 // Declaración de sujeto pasivo incluido en autoliquidaciones conjuntas - Razón social
	
	public Page01(Model4252025Callback callback) {
		super(callback);
		paint();
		setValue();
	}
	
	@Override
	protected void setValue() {
		
		setActivityBoxValues(getModel().getMainActivity(), mainActivityDescription, mainActivityType, mainActivityEpigraph, mainRegimeType, mainProvisionalProrate, mainFinalProrate, mainSpecialProrate);
		setActivityBoxValues(getModel().getActivity1(), activity1Description, activity1Type, activity1Epigraph, activity1RegimeType, activity1ProvisionalProrate, activity1FinalProrate, activity1SpecialProrate);
		setActivityBoxValues(getModel().getActivity2(), activity2Description, activity2Type, activity2Epigraph, activity2RegimeType, activity2ProvisionalProrate, activity2FinalProrate, activity2SpecialProrate);
		setActivityBoxValues(getModel().getActivity3(), activity3Description, activity3Type, activity3Epigraph, activity3RegimeType, activity3ProvisionalProrate, activity3FinalProrate, activity3SpecialProrate);
		setActivityBoxValues(getModel().getActivity4(), activity4Description, activity4Type, activity4Epigraph, activity4RegimeType, activity4ProvisionalProrate, activity4FinalProrate, activity4SpecialProrate);
		setActivityBoxValues(getModel().getActivity5(), activity5Description, activity5Type, activity5Epigraph, activity5RegimeType, activity5ProvisionalProrate, activity5FinalProrate, activity5SpecialProrate);

		mod415.setValue(getModel().isMod415(), false);
		accrual.setValue(getModel().isAccrualRegimeTarget(), false);
		mergedDeclarationDocument.setValue(getModel().getMergedDeclarationDocument(), false);
		mergedDeclarationName.setValue(getModel().getMergedDeclarationName(), false);
		
	}

	private void setActivityBoxValues(Activity425 activity, AonTextBox activityDescriptionBox, ActivityTypeListBox activityTypeBox, AonTextBox activityEpigraphBox, 
			RegimeTypeListBox regimeTypeBox, AonDoubleBox provisionalProrateBox, AonDoubleBox finalProrateBox, CheckBox specialProrateBox) {
		
		if (activity != null) {
			activityDescriptionBox.setValue(activity.getDescription());	
			activityTypeBox.setSelectedValue(activity.getKey());
			activityEpigraphBox.setValue(activity.getEpigraph());
			regimeTypeBox.setSelectedValue(activity.getRegime());
			provisionalProrateBox.setValue(activity.getProvisionalProrate());
			finalProrateBox.setValue(activity.getFinalProrate());
			specialProrateBox.setValue(activity.isSpecialProrate());			
		} else {
			activityDescriptionBox.setValue(null);	
			activityTypeBox.setSelectedValue(null);
			activityEpigraphBox.setValue(null);
			regimeTypeBox.setSelectedValue(null);
			provisionalProrateBox.setValue(null);
			finalProrateBox.setValue(null);
			specialProrateBox.setValue(false);
		}
		
	}

	private void paint() {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);
		
		// Actividad principal
		
		basePanel.add(getTitle(AON.MSG.stadisticalData()));
		basePanel.add(getSubtitle(AON.MSG.activities()));
		AonDisplayGrid grid1 = addGrid(basePanel, AON.MSG.mainActivity());
		paintActivityBoxes(getModel().getMainActivity(), mainActivityDescription, mainActivityType, mainActivityEpigraph, mainRegimeType, mainProvisionalProrate, mainFinalProrate, mainSpecialProrate, grid1);
		
		// Otras actividades
		
		AonDisplayGrid grid2 = addGrid(basePanel, AON.MSG.otherActivities());		
		paintActivityBoxes(getModel().getActivity1(), activity1Description, activity1Type, activity1Epigraph, activity1RegimeType, activity1ProvisionalProrate, activity1FinalProrate, activity1SpecialProrate, grid2);
		paintActivityBoxes(getModel().getActivity2(), activity2Description, activity2Type, activity2Epigraph, activity2RegimeType, activity2ProvisionalProrate, activity2FinalProrate, activity2SpecialProrate, grid2);
		paintActivityBoxes(getModel().getActivity3(), activity3Description, activity3Type, activity3Epigraph, activity3RegimeType, activity3ProvisionalProrate, activity3FinalProrate, activity3SpecialProrate, grid2);
		paintActivityBoxes(getModel().getActivity4(), activity4Description, activity4Type, activity4Epigraph, activity4RegimeType, activity4ProvisionalProrate, activity4FinalProrate, activity4SpecialProrate, grid2);
		paintActivityBoxes(getModel().getActivity5(), activity5Description, activity5Type, activity5Epigraph, activity5RegimeType, activity5ProvisionalProrate, activity5FinalProrate, activity5SpecialProrate, grid2);
		
		// Resto de datos
		
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonMarginTop());
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);
		
		tab1.addRow()
			.addCell(new Label("Obligado a presentar el modelo 415 por realizar op. con terceras personas por importe superior a 3.005,06 \u20AC"), AON.CSS.aonWidth600())
		 	.addCell(mod415);
		
		tab1.addRow()
			.addCell(new Label("Destinatario de operaciones a las que se aplique el r\u00E9gimen especial del criterio de caja"), AON.CSS.aonWidth600())
			.addCell(accrual);

		// Declaración de sujeto pasivo incluido en autoliquidaciones conjuntas
		
		basePanel.add(getSubtitle(AON.MSG.mergedDeclarationLabel()));
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab);

		tab.addRow()
			.addCell(new Label(AON.MSG.document()), AON.CSS.aonWidth100()) 
			.addCell(mergedDeclarationDocument);
		tab.addRow()
			.addCell(new Label(AON.MSG.enterpriseName()), AON.CSS.aonWidth100()) 
			.addCell(mergedDeclarationName);
		
		// Handlers
		
		mod415.addClickHandler(event -> {
			getModel().setMod415(mod415.getValue());
			markAsDirty();
		});
		
		accrual.addClickHandler(event -> {
			getModel().setAccrualRegimeTarget(accrual.getValue());
			markAsDirty();
		});
		
		mergedDeclarationDocument.addValueChangeHandler(event -> {
			getModel().setMergedDeclarationDocument(mergedDeclarationDocument.getValue());
			markAsDirty();
		});
		
		mergedDeclarationName.addValueChangeHandler(event -> {
			getModel().setMergedDeclarationName(mergedDeclarationName.getValue());
			markAsDirty();
		});

	}
	
	private AonDisplayGrid addGrid(FlowPanel basePanel, String title) {
		
		basePanel.add(getSubsubtitle(title));
		
		AonDisplayGrid grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonWidthAlmostAll());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.addStyleName(AON.CSS.aonMarginTop());
		basePanel.add(grid);
		
		Label provProrateLabel = new Label("% provisional prorrata");
		provProrateLabel.setTitle("% provisional prorrata general");
		Label finalProrateLabel = new Label("% definitivo prorrata");
		finalProrateLabel.setTitle("% definitivo prorrata general");
		
		grid.addHeaderRow()
			.addCell(new Label(""))
			.addCell(new Label(AON.MSG.epigraph()))
			.addCell(new Label(AON.MSG.key()))
			.addCell(new Label(AON.MSG.description()))
			.addCell(new Label("R\u00E9gimen aplicable"))
			.addCell(provProrateLabel)
			.addCell(finalProrateLabel)
			.addCell(new Label("Prorrata especial"))
			.addCell(new Label(""));
		
		return grid;
		
	}

	private void paintActivityBoxes(final Activity425 activity, AonTextBox activityDescriptionBox, ActivityTypeListBox activityTypeBox, AonTextBox activityEpigraphBox, 
			RegimeTypeListBox regimeTypeBox, AonDoubleBox provisionalProrateBox, AonDoubleBox finalProrateBox, CheckBox specialProrateBox, AonDisplayGrid grid) {
		
		// No se deja modificar los datos del epígrafe, debe seleccionarse de la lista
		activityEpigraphBox.setEnabled(false);
		activityTypeBox.setEnabled(false);
		activityDescriptionBox.setEnabled(false);
		
		activityEpigraphBox.setMaxLength(5);
		activityEpigraphBox.addValueChangeHandler(e-> {
			activity.setEpigraph(activityEpigraphBox.getValue());
			markAsDirty();
		});
		
		activityTypeBox.addChangeHandler(e -> {
			activity.setKey(activityTypeBox.getSelectedValue());
			markAsDirty();
		});
		
		activityDescriptionBox.addValueChangeHandler(e-> {
			activity.setDescription(activityDescriptionBox.getValue());
			markAsDirty();
		});
		
		regimeTypeBox.addChangeHandler(e -> {
			activity.setRegime(regimeTypeBox.getSelectedValue());
			markAsDirty();
		});
		
		provisionalProrateBox.addValueChangeHandler(e-> {
			activity.setProvisionalProrate(provisionalProrateBox.getValue());
			markAsDirty();		
		});
		
		finalProrateBox.addValueChangeHandler(e-> {
			activity.setFinalProrate(finalProrateBox.getValue());
			markAsDirty();		
		});
		
		specialProrateBox.addClickHandler(e -> {
			activity.setSpecialProrate(specialProrateBox.getValue());
			markAsDirty();		
		});
		
		// Botón eliminar actividad
		
		AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(event -> {
			activityDescriptionBox.setValue(null);	
			activityTypeBox.setSelectedValue(null);
			activityEpigraphBox.setValue(null);
			regimeTypeBox.setSelectedValue(null);
			provisionalProrateBox.setValue(null);
			finalProrateBox.setValue(null);
			specialProrateBox.setValue(false);
			activity.setDescription(null);
			activity.setKey(null);
			activity.setEpigraph(null);
			activity.setRegime(null);
			activity.setProvisionalProrate(0.0);
			activity.setFinalProrate(0.0);
			activity.setSpecialProrate(false);
			markAsDirty();
			event.stopPropagation();
		});
		
		// Botón seleccionar actividad
		
		AonTableButton selectButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconRight());
		selectButton.addClickHandler( evenmt -> {
			IAECanariasPanel activityPanel = new IAECanariasPanel();
			activityPanel.addSelectionHandler(event ->  {
					Activity425 act = event.getSelectedItem();
					activityDescriptionBox.setValue(act.getDescription());
					activityTypeBox.setSelectedValue(act.getKey());
					activityEpigraphBox.setValue(act.getEpigraph());
					activity.setDescription(act.getDescription());
					activity.setKey(act.getKey());
					activity.setEpigraph(act.getEpigraph());
					markAsDirty();
			});
			activityPanel.center();
			activityPanel.show();
		});
		
		activityEpigraphBox.setWidth("40px");       // Epígrafe (Código)
		activityTypeBox.setWidth("350px");          // Clave
		activityDescriptionBox.setWidth("350px");   // Descripción
		regimeTypeBox.setWidth("350px");            // Régimen aplicable
		provisionalProrateBox.setWidth("60px");     // Prorrata provisional
		finalProrateBox.setWidth("60px");		  	// Prorrata definitiva
		specialProrateBox.setWidth("40px"); 	  	// Prorrata especial
		
		grid.addRow()
			.addCell(selectButton, AON.CSS.aonWidth20())
			.addCell(activityEpigraphBox, AON.CSS.aonWidth40())
			.addCell(activityTypeBox, AON.CSS.aonWidth300())
			.addCell(activityDescriptionBox, AON.CSS.aonWidth300())
			.addCell(regimeTypeBox, AON.CSS.aonWidth300())
			.addCell(provisionalProrateBox, AON.CSS.aonWidth60())
			.addCell(finalProrateBox, AON.CSS.aonWidth60())
			.addCell(specialProrateBox, AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(deleteButton, AON.CSS.aonWidth20());
		
	}
	
}

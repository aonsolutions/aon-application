package com.esferalia.aon.gwt.fiscal.client.mod390.e2023;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.AEATCNAEPanel;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2023.Model3902023.Model3902023Callback;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Activity;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page01 extends PageAbs {

	private AonTextBox mainActivityDescription = new AonTextBox();
	private ActivityTypeListBox mainActivityType = new ActivityTypeListBox();
	private AonTextBox mainActivityEpigraph = new AonTextBox();
	
	private AonTextBox activity1Description = new AonTextBox();
	private ActivityTypeListBox activity1Type = new ActivityTypeListBox();
	private AonTextBox activity1Epigraph = new AonTextBox();

	private AonTextBox activity2Description = new AonTextBox();
	private ActivityTypeListBox activity2Type = new ActivityTypeListBox();
	private AonTextBox activity2Epigraph = new AonTextBox();

	private AonTextBox activity3Description = new AonTextBox();
	private ActivityTypeListBox activity3Type = new ActivityTypeListBox();
	private AonTextBox activity3Epigraph = new AonTextBox();

	private AonTextBox activity4Description = new AonTextBox();
	private ActivityTypeListBox activity4Type = new ActivityTypeListBox();
	private AonTextBox activity4Epigraph = new AonTextBox();

	private AonTextBox activity5Description = new AonTextBox();
	private ActivityTypeListBox activity5Type = new ActivityTypeListBox();
	private AonTextBox activity5Epigraph = new AonTextBox();
	
	private CheckBox mod347 = new CheckBox(AON.MSG.mod347Check());
	private AonDocumentTextBox mergedDeclarationDocument = new AonDocumentTextBox();
	private AonTextBox mergedDeclarationName = new AonTextBox();
	
	public Page01(Model3902023Callback callback) {
		super(callback);
		paint();
		setValue();
	}
	
	@Override
	protected void setValue() {
		if (getModel().getMainActivity() != null) {
			mainActivityDescription.setValue(getModel().getMainActivity().getDescription());	
			mainActivityType.setValue( getModel().getMainActivity().getType());
			mainActivityEpigraph.setValue(getModel().getMainActivity().getEpigraph());
		} else {
			mainActivityDescription.setValue(null);	
			mainActivityType.setValue(null);
			mainActivityEpigraph.setValue(null);
		}
		if (getModel().getActivity1() != null) {
			activity1Description.setValue(getModel().getActivity1().getDescription());	
			activity1Type.setValue( getModel().getActivity1().getType());
			activity1Epigraph.setValue(getModel().getActivity1().getEpigraph());
		} else {
			activity1Description.setValue(null);	
			activity1Type.setValue( null);
			activity1Epigraph.setValue(null);
		}
		if (getModel().getActivity2() != null) {
			activity2Description.setValue(getModel().getActivity2().getDescription());	
			activity2Type.setValue( getModel().getActivity2().getType());
			activity2Epigraph.setValue(getModel().getActivity2().getEpigraph());
		} else {
			activity2Description.setValue(null);	
			activity2Type.setValue( null);
			activity2Epigraph.setValue(null);
		}
		if (getModel().getActivity3() != null) {
			activity3Description.setValue(getModel().getActivity3().getDescription());	
			activity3Type.setValue( getModel().getActivity3().getType());
			activity3Epigraph.setValue(getModel().getActivity3().getEpigraph());
		} else {
			activity3Description.setValue(null);	
			activity3Type.setValue( null);
			activity3Epigraph.setValue(null);
		}
		if (getModel().getActivity4() != null) {
			activity4Description.setValue(getModel().getActivity4().getDescription());	
			activity4Type.setValue( getModel().getActivity4().getType());
			activity4Epigraph.setValue(getModel().getActivity4().getEpigraph());
		} else {
			activity4Description.setValue(null);	
			activity4Type.setValue( null);
			activity4Epigraph.setValue(null);
		}
		if (getModel().getActivity5() != null) {
			activity5Description.setValue(getModel().getActivity5().getDescription());	
			activity5Type.setValue( getModel().getActivity5().getType());
			activity5Epigraph.setValue(getModel().getActivity5().getEpigraph());
		} else {
			activity5Description.setValue(null);	
			activity5Type.setValue( null);
			activity5Epigraph.setValue(null);
		}
		mod347.setValue(getModel().isMod347(),false);
		mergedDeclarationDocument.setValue(getModel().getMergedDeclarationDocument(),false);
		mergedDeclarationName.setValue(getModel().getMergedDeclarationName(),false);
	}

	private void paint() {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);
		
		mainActivityDescription.setVisibleLength(90);
		mainActivityDescription.addValueChangeHandler(e-> {
			if (getModel().getMainActivity() == null) getModel().setMainActivity(new Activity()); 
			getModel().getMainActivity().setDescription(mainActivityDescription.getValue());
			markAsDirty();
		});
		
		mainActivityType.addChangeHandler(e -> {
			if (getModel().getMainActivity() == null) getModel().setMainActivity(new Activity());
			getModel().getMainActivity().setType(mainActivityType.getValue());
			markAsDirty();
		});
		
		mainActivityEpigraph.setVisibleLength(5);
		mainActivityEpigraph.setMaxLength(4);
		mainActivityEpigraph.addValueChangeHandler(e-> {
			if (getModel().getMainActivity() == null) getModel().setMainActivity(new Activity());
			getModel().getMainActivity().setEpigraph(mainActivityEpigraph.getValue());
			markAsDirty();
		});

		activity1Description.setVisibleLength(90);
		activity1Description.addValueChangeHandler(e-> {
			if (getModel().getActivity1() == null) getModel().setActivity1(new Activity());
			getModel().getActivity1().setDescription(activity1Description.getValue());
			markAsDirty();
		});
		
		activity1Type.addChangeHandler(e -> {
			if (getModel().getActivity1() == null) getModel().setActivity1(new Activity());
			getModel().getActivity1().setType(activity1Type.getValue());
			markAsDirty();
		});
		
		activity1Epigraph.setVisibleLength(5);
		activity1Epigraph.setMaxLength(4);
		activity1Epigraph.addValueChangeHandler(e-> {
			if (getModel().getActivity1() == null) getModel().setActivity1(new Activity());
			getModel().getActivity1().setEpigraph(activity1Epigraph.getValue());
			markAsDirty();
		});
		
		activity2Description.setVisibleLength(90);
		activity2Description.addValueChangeHandler(e-> {
			if (getModel().getActivity2() == null) getModel().setActivity2(new Activity());
			getModel().getActivity2().setDescription(activity2Description.getValue());
			markAsDirty();
		});
		
		activity2Type.addChangeHandler(e -> {
			if (getModel().getActivity2() == null) getModel().setActivity2(new Activity());
			getModel().getActivity2().setType(activity2Type.getValue());
			markAsDirty();
		});
		
		activity2Epigraph.setVisibleLength(5);
		activity2Epigraph.setMaxLength(4);
		activity2Epigraph.addValueChangeHandler(e-> {
			if (getModel().getActivity2() == null) getModel().setActivity2(new Activity());
			getModel().getActivity2().setEpigraph(activity2Epigraph.getValue());
			markAsDirty();
		});
		
		activity3Description.setVisibleLength(90);
		activity3Description.addValueChangeHandler(e-> {
			if (getModel().getActivity3() == null) getModel().setActivity3(new Activity());
			getModel().getActivity3().setDescription(activity3Description.getValue());
			markAsDirty();
		});
		
		activity3Type.addChangeHandler(e -> {
			if (getModel().getActivity3() == null) getModel().setActivity3(new Activity());
			getModel().getActivity3().setType(activity3Type.getValue());
			markAsDirty();
		});
		
		activity3Epigraph.setVisibleLength(5);
		activity3Epigraph.setMaxLength(4);
		activity3Epigraph.addValueChangeHandler(e-> {
			if (getModel().getActivity3() == null) getModel().setActivity3(new Activity());
			getModel().getActivity3().setEpigraph(activity3Epigraph.getValue());
			markAsDirty();
		});
		
		activity4Description.setVisibleLength(90);
		activity4Description.addValueChangeHandler(e-> {
			if (getModel().getActivity4() == null) getModel().setActivity4(new Activity());
			getModel().getActivity4().setDescription(activity4Description.getValue());
			markAsDirty();
		});
		
		activity4Type.addChangeHandler(e -> {
			if (getModel().getActivity4() == null) getModel().setActivity4(new Activity());
			getModel().getActivity4().setType(activity4Type.getValue());
			markAsDirty();
		});
		
		activity4Epigraph.setVisibleLength(5);
		activity4Epigraph.setMaxLength(4);
		activity4Epigraph.addValueChangeHandler(e-> {
			if (getModel().getActivity4() == null) getModel().setActivity4(new Activity());
			getModel().getActivity4().setEpigraph(activity4Epigraph.getValue());
			markAsDirty();
		});
		
		activity5Description.setVisibleLength(90);
		activity5Description.addValueChangeHandler(e-> {
			if (getModel().getActivity5() == null) getModel().setActivity5(new Activity());
			getModel().getActivity5().setDescription(activity5Description.getValue());
			markAsDirty();
		});

		activity5Type.addChangeHandler(e -> {
			if (getModel().getActivity5() == null) getModel().setActivity5(new Activity());
			getModel().getActivity5().setType(activity5Type.getValue());
			markAsDirty();
		});
		
		activity5Epigraph.setVisibleLength(5);
		activity5Epigraph.setMaxLength(4);
		activity5Epigraph.addValueChangeHandler(e-> {
			if (getModel().getActivity5() == null) getModel().setActivity5(new Activity());
			getModel().getActivity5().setEpigraph(activity5Epigraph.getValue());
			markAsDirty();
		});

		basePanel.add(getTitle(AON.MSG.stadisticalData()));
		basePanel.add(getSubtitle(AON.MSG.activities() + " " + AON.MSG.activitiesNote()));
		basePanel.add(getSubsubtitle(AON.MSG.mainActivity()));
		
		AonDisplayGrid grid0 = new AonDisplayGrid();
		grid0.addStyleName(AON.CSS.aonWidthAlmostAll());
		grid0.addStyleName(AON.CSS.aonBlockCenter());
		grid0.addStyleName(AON.CSS.aonMarginTop());
		basePanel.add(grid0);
		
		grid0.addHeaderRow()
			.addCell(new Label(""),AON.CSS.aonWidth20())
			.addCell(new Label(AON.MSG.epigraph()),AON.CSS.aonWidth100())
			.addCell(new Label(AON.MSG.key()),AON.CSS.aonWidth80())
			.addCell(new Label(AON.MSG.description()),AON.CSS.aonWidthAuto())
			.addCell(new Label(""),AON.CSS.aonWidth20());
		
		AonTableButton mainActivityDeleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		mainActivityDeleteButton.addClickHandler(event -> {
			mainActivityDescription.setValue(null);	
			mainActivityType.setValue(null);
			mainActivityEpigraph.setValue(null);
			getModel().setMainActivity(null);
			markAsDirty();
			event.stopPropagation();
		});
		AonTableButton mainSelectButton = new AonTableButton(AON.MSG.selectAction(),AON.CSS.aonIconRight());
		mainSelectButton.addClickHandler( evenmt -> {
			AEATCNAEPanel activityPanel = new AEATCNAEPanel();
			activityPanel.addSelectionHandler(event ->  {
					Activity activity = event.getSelectedItem();
					mainActivityDescription.setValue( activity.getDescription() );
					mainActivityType.setValue( activity.getType() );
					mainActivityEpigraph.setValue( activity.getEpigraph() );
					getModel().setMainActivity(activity);
					markAsDirty();
			});
			activityPanel.center();
			activityPanel.show();
		});
		grid0.addRow()
			.addCell(mainSelectButton)
			.addCell(mainActivityEpigraph)
			.addCell(mainActivityType)
			.addCell(mainActivityDescription)
			.addCell(mainActivityDeleteButton);
		
		basePanel.add(getSubsubtitle(AON.MSG.otherActivities()));
		
		AonDisplayGrid grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonWidthAlmostAll());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.addStyleName(AON.CSS.aonMarginTop());
		basePanel.add(grid);
		
		grid.addHeaderRow()
			.addCell(new Label(""),AON.CSS.aonWidth20())
			.addCell(new Label(AON.MSG.epigraph()),AON.CSS.aonWidth100())
			.addCell(new Label(AON.MSG.key()),AON.CSS.aonWidth80())
			.addCell(new Label(AON.MSG.description()),AON.CSS.aonWidthAuto())
			.addCell(new Label(""),AON.CSS.aonWidth20());
		
		AonTableButton activity1DeleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		activity1DeleteButton.addClickHandler(event -> {
			activity1Description.setValue(null);	
			activity1Type.setValue(null);
			activity1Epigraph.setValue(null);
			getModel().setActivity1(null);
			markAsDirty();
			event.stopPropagation();
		});
		AonTableButton activity1SelectButton = new AonTableButton(AON.MSG.selectAction(),AON.CSS.aonIconRight());
		activity1SelectButton.addClickHandler( evenmt -> {
			AEATCNAEPanel activityPanel = new AEATCNAEPanel();
			activityPanel.addSelectionHandler(event ->  {
				Activity activity = event.getSelectedItem();
				activity1Description.setValue( activity.getDescription() );
				activity1Type.setValue( activity.getType() );
				activity1Epigraph.setValue( activity.getEpigraph() );
				getModel().setActivity1(activity);
				markAsDirty();
			});
			activityPanel.center();
			activityPanel.show();
		});
		grid.addRow()
			.addCell(activity1SelectButton)
			.addCell(activity1Epigraph)
			.addCell(activity1Type)
			.addCell(activity1Description)
			.addCell(activity1DeleteButton);
		
		AonTableButton activity2DeleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		activity2DeleteButton.addClickHandler(event -> {
			activity2Description.setValue(null);	
			activity2Type.setValue(null);
			activity2Epigraph.setValue(null);
			getModel().setActivity2(null);
			markAsDirty();
			event.stopPropagation();
		});
		
		AonTableButton activity2SelectButton = new AonTableButton(AON.MSG.selectAction(),AON.CSS.aonIconRight());
		activity2SelectButton.addClickHandler( evenmt -> {
			AEATCNAEPanel activityPanel = new AEATCNAEPanel();
			activityPanel.addSelectionHandler(event ->  {
				Activity activity = event.getSelectedItem();
				activity2Description.setValue( activity.getDescription() );
				activity2Type.setValue( activity.getType() );
				activity2Epigraph.setValue( activity.getEpigraph() );
				getModel().setActivity2(activity);
				markAsDirty();
			});
			activityPanel.center();
			activityPanel.show();
		});
		grid.addRow()
			.addCell(activity2SelectButton)
			.addCell(activity2Epigraph)
			.addCell(activity2Type)
			.addCell(activity2Description)
			.addCell(activity2DeleteButton);

		AonTableButton activity3DeleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		activity3DeleteButton.addClickHandler(event -> {
			activity3Description.setValue(null);	
			activity3Type.setValue(null);
			activity3Epigraph.setValue(null);
			getModel().setActivity3(null);
			markAsDirty();
			event.stopPropagation();
		});
		AonTableButton activity3SelectButton = new AonTableButton(AON.MSG.selectAction(),AON.CSS.aonIconRight());
		activity3SelectButton.addClickHandler( evenmt -> {
			AEATCNAEPanel activityPanel = new AEATCNAEPanel();
			activityPanel.addSelectionHandler(event ->  {
				Activity activity = event.getSelectedItem();
				activity3Description.setValue( activity.getDescription() );
				activity3Type.setValue( activity.getType() );
				activity3Epigraph.setValue( activity.getEpigraph() );
				getModel().setActivity3(activity);
				markAsDirty();
			});
			activityPanel.center();
			activityPanel.show();
		});
		grid.addRow()
			.addCell(activity3SelectButton)
			.addCell(activity3Epigraph)
			.addCell(activity3Type)
			.addCell(activity3Description)
			.addCell(activity3DeleteButton);

		AonTableButton activity4DeleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		activity4DeleteButton.addClickHandler(event -> {
			activity4Description.setValue(null);	
			activity4Type.setValue(null);
			activity4Epigraph.setValue(null);
			getModel().setActivity4(null);
			markAsDirty();
			event.stopPropagation();
		});
		AonTableButton activity4SelectButton = new AonTableButton(AON.MSG.selectAction(),AON.CSS.aonIconRight());
		activity4SelectButton.addClickHandler( evenmt -> {
			AEATCNAEPanel activityPanel = new AEATCNAEPanel();
			activityPanel.addSelectionHandler(event ->  {
				Activity activity = event.getSelectedItem();
				activity4Description.setValue( activity.getDescription() );
				activity4Type.setValue( activity.getType() );
				activity4Epigraph.setValue( activity.getEpigraph() );
				getModel().setActivity4(activity);
				markAsDirty();
			});
			activityPanel.center();
			activityPanel.show();
		});
		grid.addRow()
			.addCell(activity4SelectButton)
			.addCell(activity4Epigraph)
			.addCell(activity4Type)
			.addCell(activity4Description)
			.addCell(activity4DeleteButton);

		AonTableButton activity5DeleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		activity5DeleteButton.addClickHandler(event -> {
			activity5Description.setValue(null);	
			activity5Type.setValue(null);
			activity5Epigraph.setValue(null);
			getModel().setActivity5(null);
			markAsDirty();
			event.stopPropagation();
		});
		AonTableButton activity5SelectButton = new AonTableButton(AON.MSG.selectAction(),AON.CSS.aonIconRight());
		activity5SelectButton.addClickHandler( evenmt -> {
			AEATCNAEPanel activityPanel = new AEATCNAEPanel();
			activityPanel.addSelectionHandler(event ->  {
				Activity activity = event.getSelectedItem();
				activity5Description.setValue( activity.getDescription() );
				activity5Type.setValue( activity.getType() );
				activity5Epigraph.setValue( activity.getEpigraph() );
				getModel().setActivity5(activity);
				markAsDirty();
			});
			activityPanel.center();
			activityPanel.show();
		});
		grid.addRow()
			.addCell(activity5SelectButton)
			.addCell(activity5Epigraph)
			.addCell(activity5Type)
			.addCell(activity5Description)
			.addCell(activity5DeleteButton);
		
		FlowPanel m347 = new FlowPanel();
		m347.setStyleName(AON.CSS.aonMarginTop());
		m347.add(mod347);
		basePanel.add(m347);

		basePanel.add(getTitle(AON.MSG.mergedDeclarationLabel()));
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab);

		tab.addRow()
			.addCell(new Label(AON.MSG.document()), AON.CSS.aonTableLabel() )
			.addCell(mergedDeclarationDocument);
		tab.addRow()
			.addCell(new Label(AON.MSG.companyName()), AON.CSS.aonTableLabel())
			.addCell(mergedDeclarationName);
		
		mod347.addClickHandler(event -> {
			getModel().setMod347( mod347.getValue() );
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
/*	
	protected void populate() {
		if (!AonStringUtils.isEmpty( mainActivityEpigraph.getValue() ) ) {
			Activity mainActivity = new Activity();
			mainActivity.setType(mainActivityType.getValue());
			mainActivity.setDescription(mainActivityDescription.getValue());
			mainActivity.setEpigraph(mainActivityEpigraph.getValue());
			getModel().setMainActivity(mainActivity);
		} else {
			getModel().setMainActivity(null);
		}
		
		if (!AonStringUtils.isEmpty( activity1Epigraph.getValue() ) ) {
			Activity activity1 = new Activity();	
			activity1.setType(activity1Type.getValue());
			activity1.setDescription(activity1Description.getValue());
			activity1.setEpigraph(activity1Epigraph.getValue());
			getModel().setActivity1(activity1);
		} else {
			getModel().setActivity1(null);
		}
		
		if (!AonStringUtils.isEmpty( activity2Epigraph.getValue() ) ) {
			Activity activity2 = new Activity();	
			activity2.setType(activity2Type.getValue());
			activity2.setDescription(activity2Description.getValue());
			activity2.setEpigraph(activity2Epigraph.getValue());
			getModel().setActivity2(activity2);
		} else {
			getModel().setActivity2(null);
		}
		
		if (!AonStringUtils.isEmpty( activity3Epigraph.getValue() ) ) {
			Activity activity3 = new Activity();	
			activity3.setType(activity3Type.getValue());
			activity3.setDescription(activity3Description.getValue());
			activity3.setEpigraph(activity3Epigraph.getValue());
			getModel().setActivity3(activity3);
		} else {
			getModel().setActivity3(null);
		}
		
		if (!AonStringUtils.isEmpty( activity4Epigraph.getText() ) ) {
			Activity activity4 = new Activity();	
			activity4.setType(activity4Type.getValue());
			activity4.setDescription(activity4Description.getText());
			activity4.setEpigraph(activity4Epigraph.getText());
			getModel().setActivity4(activity4);
		} else {
			getModel().setActivity4(null);
		}
		
		if (!AonStringUtils.isEmpty( activity5Epigraph.getText() ) ) {
			Activity activity5 = new Activity();	
			activity5.setType(activity5Type.getValue());
			activity5.setDescription(activity5Description.getText());
			activity5.setEpigraph(activity5Epigraph.getText());
			getModel().setActivity5(activity5);
		} else {
			getModel().setActivity5(null);
		}
		
		getModel().setMod347(mod347.getValue());
		getModel().setMergedDeclarationDocument(mergedDeclarationDocument.getValue());
		getModel().setMergedDeclarationName(mergedDeclarationName.getValue());
	}
*/	
	
}



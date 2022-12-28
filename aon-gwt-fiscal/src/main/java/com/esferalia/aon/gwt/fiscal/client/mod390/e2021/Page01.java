package com.esferalia.aon.gwt.fiscal.client.mod390.e2021;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.AonActivityPanel;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2021.Model3902021.Model3902021Callback;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Activity;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page01 extends PageAbs {

	private Label mainActivityDescription = new Label();
	private Label mainActivityKey = new Label();
	private Label mainActivityEpigraph = new Label();
	
	private Label activity1Description = new Label();
	private Label activity1Key = new Label();
	private Label activity1Epigraph = new Label();

	private Label activity2Description = new Label();
	private Label activity2Key = new Label();
	private Label activity2Epigraph = new Label();

	private Label activity3Description = new Label();
	private Label activity3Key = new Label();
	private Label activity3Epigraph = new Label();

	private Label activity4Description = new Label();
	private Label activity4Key = new Label();
	private Label activity4Epigraph = new Label();

	private Label activity5Description = new Label();
	private Label activity5Key = new Label();
	private Label activity5Epigraph = new Label();
	
	private CheckBox mod347 = new CheckBox(AON.MSG.mod347Check());
	private AonDocumentTextBox mergedDeclarationDocument = new AonDocumentTextBox();
	private AonTextBox mergedDeclarationName = new AonTextBox();
	
	public Page01(Model3902021Callback callback) {
		super(callback);
		paint();
		setValue();
	}
	
	@Override
	protected void setValue() {
		if (getModel().getMainActivity() != null) {
			mainActivityDescription.setText(getModel().getMainActivity().getDescription());	
			mainActivityKey.setText(getModel().getMainActivity().getKey());
			mainActivityEpigraph.setText(getModel().getMainActivity().getEpigraph());
		} else {
			mainActivityDescription.setText(null);	
			mainActivityKey.setText(null);
			mainActivityEpigraph.setText(null);
		}
		if (getModel().getActivity1() != null) {
			activity1Description.setText(getModel().getActivity1().getDescription());	
			activity1Key.setText(getModel().getActivity1().getKey());
			activity1Epigraph.setText(getModel().getActivity1().getEpigraph());
		} else {
			activity1Description.setText(null);	
			activity1Key.setText(null);
			activity1Epigraph.setText(null);
		}
		if (getModel().getActivity2() != null) {
			activity2Description.setText(getModel().getActivity2().getDescription());	
			activity2Key.setText(getModel().getActivity2().getKey());
			activity2Epigraph.setText(getModel().getActivity2().getEpigraph());
		} else {
			activity2Description.setText(null);	
			activity2Key.setText(null);
			activity2Epigraph.setText(null);
		}
		if (getModel().getActivity3() != null) {
			activity3Description.setText(getModel().getActivity3().getDescription());	
			activity3Key.setText(getModel().getActivity3().getKey());
			activity3Epigraph.setText(getModel().getActivity3().getEpigraph());
		} else {
			activity3Description.setText(null);	
			activity3Key.setText(null);
			activity3Epigraph.setText(null);
		}
		if (getModel().getActivity4() != null) {
			activity4Description.setText(getModel().getActivity4().getDescription());	
			activity4Key.setText(getModel().getActivity4().getKey());
			activity4Epigraph.setText(getModel().getActivity4().getEpigraph());
		} else {
			activity4Description.setText(null);	
			activity4Key.setText(null);
			activity4Epigraph.setText(null);
		}
		if (getModel().getActivity5() != null) {
			activity5Description.setText(getModel().getActivity5().getDescription());	
			activity5Key.setText(getModel().getActivity5().getKey());
			activity5Epigraph.setText(getModel().getActivity5().getEpigraph());
		} else {
			activity5Description.setText(null);	
			activity5Key.setText(null);
			activity5Epigraph.setText(null);
		}
		mod347.setValue(getModel().isMod347(),false);
		mergedDeclarationDocument.setValue(getModel().getMergedDeclarationDocument(),false);
		mergedDeclarationName.setValue(getModel().getMergedDeclarationName(),false);
	}

	protected void populate() {
		if (!AonStringUtils.isEmpty( mainActivityKey.getText() ) ) {
			Activity mainActivity = new Activity();
			mainActivity.setKey(mainActivityKey.getText());
			mainActivity.setDescription(mainActivityDescription.getText());
			mainActivity.setEpigraph(mainActivityEpigraph.getText());
			getModel().setMainActivity(mainActivity);
		} else {
			getModel().setMainActivity(null);
		}
		
		if (!AonStringUtils.isEmpty( activity1Key.getText() ) ) {
			Activity activity1 = new Activity();	
			activity1.setKey(activity1Key.getText());
			activity1.setDescription(activity1Description.getText());
			activity1.setEpigraph(activity1Epigraph.getText());
			getModel().setActivity1(activity1);
		} else {
			getModel().setActivity1(null);
		}
		
		if (!AonStringUtils.isEmpty( activity2Key.getText() ) ) {
			Activity activity2 = new Activity();	
			activity2.setKey(activity2Key.getText());
			activity2.setDescription(activity2Description.getText());
			activity2.setEpigraph(activity2Epigraph.getText());
			getModel().setActivity2(activity2);
		} else {
			getModel().setActivity2(null);
		}
		
		if (!AonStringUtils.isEmpty( activity3Key.getText() ) ) {
			Activity activity3 = new Activity();	
			activity3.setKey(activity3Key.getText());
			activity3.setDescription(activity3Description.getText());
			activity3.setEpigraph(activity3Epigraph.getText());
			getModel().setActivity3(activity3);
		} else {
			getModel().setActivity3(null);
		}
		
		if (!AonStringUtils.isEmpty( activity4Key.getText() ) ) {
			Activity activity4 = new Activity();	
			activity4.setKey(activity4Key.getText());
			activity4.setDescription(activity4Description.getText());
			activity4.setEpigraph(activity4Epigraph.getText());
			getModel().setActivity4(activity4);
		} else {
			getModel().setActivity4(null);
		}
		
		if (!AonStringUtils.isEmpty( activity5Key.getText() ) ) {
			Activity activity5 = new Activity();	
			activity5.setKey(activity5Key.getText());
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

	private void paint() {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);
		
		basePanel.add(getTitle(AON.MSG.stadisticalData()));
		basePanel.add(getSubtitle(AON.MSG.activities() + " " + AON.MSG.activitiesNote()));
		basePanel.add(getSubsubtitle(AON.MSG.mainActivity()));
		
		AonDisplayGrid grid0 = new AonDisplayGrid();
		grid0.addStyleName(AON.CSS.aonWidthAlmostAll());
		grid0.addStyleName(AON.CSS.aonBlockCenter());
		grid0.addStyleName(AON.CSS.aonMarginTop());
		basePanel.add(grid0);
		
		grid0.addHeaderRow()
			.addCell(new Label(AON.MSG.epigraph()),AON.CSS.aonWidth100())
			.addCell(new Label(AON.MSG.key()),AON.CSS.aonWidth80())
			.addCell(new Label(AON.MSG.description()),AON.CSS.aonWidthAuto())
			.addCell(new Label(""),AON.CSS.aonWidth20());
		
		AonTableButton mainActivityDeleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		mainActivityDeleteButton.addClickHandler(event -> {
			mainActivityDescription.setText(null);	
			mainActivityKey.setText(null);
			mainActivityEpigraph.setText(null);
			getModel().setMainActivity(null);
			markAsDirty();
			event.stopPropagation();
		});
		AonDisplayGridRow row = grid0.addRow()
			.addCell(mainActivityEpigraph)
			.addCell(mainActivityKey)
			.addCell(mainActivityDescription)
			.addCell(mainActivityDeleteButton);
		row.addClickHandler( evenmt -> {
			AonActivityPanel activityPanel = new AonActivityPanel();
			activityPanel.addSelectionHandler(event ->  {
					Activity activity = event.getSelectedItem();
					mainActivityDescription.setText( activity.getDescription() );
					mainActivityKey.setText( activity.getKey() );
					mainActivityEpigraph.setText( activity.getEpigraph() );
					getModel().setMainActivity(activity);
					markAsDirty();
			});
			activityPanel.center();
			activityPanel.show();
		});
		
		basePanel.add(getSubsubtitle(AON.MSG.otherActivities()));
		
		AonDisplayGrid grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonWidthAlmostAll());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.addStyleName(AON.CSS.aonMarginTop());
		basePanel.add(grid);
		
		grid.addHeaderRow()
			.addCell(new Label(AON.MSG.epigraph()),AON.CSS.aonWidth100())
			.addCell(new Label(AON.MSG.key()),AON.CSS.aonWidth80())
			.addCell(new Label(AON.MSG.description()),AON.CSS.aonWidthAuto())
			.addCell(new Label(""),AON.CSS.aonWidth20());
		
		AonTableButton activity1DeleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		activity1DeleteButton.addClickHandler(event -> {
			activity1Description.setText(null);	
			activity1Key.setText(null);
			activity1Epigraph.setText(null);
			getModel().setActivity1(null);
			markAsDirty();
			event.stopPropagation();
		});
		AonDisplayGridRow row1 = grid.addRow()
			.addCell(activity1Epigraph)
			.addCell(activity1Key)
			.addCell(activity1Description)
			.addCell(activity1DeleteButton);
		row1.addClickHandler( evenmt -> {
			AonActivityPanel activityPanel = new AonActivityPanel();
			activityPanel.addSelectionHandler(event ->  {
				Activity activity = event.getSelectedItem();
				activity1Description.setText( activity.getDescription() );
				activity1Key.setText( activity.getKey() );
				activity1Epigraph.setText( activity.getEpigraph() );
				getModel().setActivity1(activity);
				markAsDirty();
			});
			activityPanel.center();
			activityPanel.show();
		});
		
		AonTableButton activity2DeleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		activity2DeleteButton.addClickHandler(event -> {
			activity2Description.setText(null);	
			activity2Key.setText(null);
			activity2Epigraph.setText(null);
			getModel().setActivity2(null);
			markAsDirty();
			event.stopPropagation();
		});
		AonDisplayGridRow row2 = grid.addRow()
			.addCell(activity2Epigraph)
			.addCell(activity2Key)
			.addCell(activity2Description)
			.addCell(activity2DeleteButton);
		row2.addClickHandler( evenmt -> {
			AonActivityPanel activityPanel = new AonActivityPanel();
			activityPanel.addSelectionHandler(event ->  {
				Activity activity = event.getSelectedItem();
				activity2Description.setText( activity.getDescription() );
				activity2Key.setText( activity.getKey() );
				activity2Epigraph.setText( activity.getEpigraph() );
				getModel().setActivity2(activity);
				markAsDirty();
			});
			activityPanel.center();
			activityPanel.show();
		});

		AonTableButton activity3DeleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		activity3DeleteButton.addClickHandler(event -> {
			activity3Description.setText(null);	
			activity3Key.setText(null);
			activity3Epigraph.setText(null);
			getModel().setActivity3(null);
			markAsDirty();
			event.stopPropagation();
		});
		AonDisplayGridRow row3 = grid.addRow()
			.addCell(activity3Epigraph)
			.addCell(activity3Key)
			.addCell(activity3Description)
			.addCell(activity3DeleteButton);
		row3.addClickHandler( evenmt -> {
			AonActivityPanel activityPanel = new AonActivityPanel();
			activityPanel.addSelectionHandler(event ->  {
				Activity activity = event.getSelectedItem();
				activity3Description.setText( activity.getDescription() );
				activity3Key.setText( activity.getKey() );
				activity3Epigraph.setText( activity.getEpigraph() );
				getModel().setActivity3(activity);
				markAsDirty();
			});
			activityPanel.center();
			activityPanel.show();
		});

		AonTableButton activity4DeleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		activity4DeleteButton.addClickHandler(event -> {
			activity4Description.setText(null);	
			activity4Key.setText(null);
			activity4Epigraph.setText(null);
			getModel().setActivity4(null);
			markAsDirty();
			event.stopPropagation();
		});
		AonDisplayGridRow row4 = grid.addRow()
			.addCell(activity4Epigraph)
			.addCell(activity4Key)
			.addCell(activity4Description)
			.addCell(activity4DeleteButton);
		row4.addClickHandler( evenmt -> {
			AonActivityPanel activityPanel = new AonActivityPanel();
			activityPanel.addSelectionHandler(event ->  {
				Activity activity = event.getSelectedItem();
				activity4Description.setText( activity.getDescription() );
				activity4Key.setText( activity.getKey() );
				activity4Epigraph.setText( activity.getEpigraph() );
				getModel().setActivity4(activity);
				markAsDirty();
			});
			activityPanel.center();
			activityPanel.show();
		});

		AonTableButton activity5DeleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		activity5DeleteButton.addClickHandler(event -> {
			activity5Description.setText(null);	
			activity5Key.setText(null);
			activity5Epigraph.setText(null);
			getModel().setActivity5(null);
			markAsDirty();
			event.stopPropagation();
		});
		AonDisplayGridRow row5 = grid.addRow()
			.addCell(activity5Epigraph)
			.addCell(activity5Key)
			.addCell(activity5Description)
			.addCell(activity5DeleteButton);
		row5.addClickHandler( evenmt -> {
			AonActivityPanel activityPanel = new AonActivityPanel();
			activityPanel.addSelectionHandler(event ->  {
				Activity activity = event.getSelectedItem();
				activity5Description.setText( activity.getDescription() );
				activity5Key.setText( activity.getKey() );
				activity5Epigraph.setText( activity.getEpigraph() );
				getModel().setActivity5(activity);
				markAsDirty();
			});
			activityPanel.center();
			activityPanel.show();
		});
		
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
}

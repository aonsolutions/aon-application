package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;

public class ActivityDraft extends Composite{

	private class ActivityImplementation extends Activity{

		@Override
		public void onActivityDescriptionChange() {
			activityDraftObject.setActivityDescription(activityDescription.getValue());
		}

		@Override
		public void onActivityCNAE2009Change() {
			activityDraftObject.setActivityCNAE2009(activityCNAE2009.getValue());
		}

		@Override
		public void onActivityStartDateChange() {
			activityDraftObject.setActivityStartDate(startDate.getValue());
		}

		@Override
		public void onActivityEndDateChange() {
			activityDraftObject.setActivityEndDate(endDate.getValue());
		}

		@Override
		public void onActivityActiveChange() {
			activityDraftObject.setActivityActive(activityActive.getValue());
		}
		
		@Override
		public void onInsertRows() {
			for(CCCInfo cccInfo : activityDraftObject.getCCCs().values()) {
				this.cccWidget.insertRow(cccInfo);
			}
			
			activity.hideActivityColumn();
		}

		@Override
		public void onDeleteCCC(Integer cccId) {
			activityDraftObject.deleteCCC(cccId);
		}

		@Override
		public void onInsertCCC(Integer cccId, int activityId, byte cccRegime, String cccRegimeCode, String account, String province, String provinceCode) {
			activityDraftObject.insertCCC(cccId, account, cccRegimeCode, account, cccRegime, province, provinceCode, false);
		}

		@Override
		public Set<Entry<Integer, String>> getActivities() {
			return null;
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface ActivityDraftUiBinder extends UiBinder<Widget, ActivityDraft> {}
	
	private static ActivityDraftUiBinder uiBinder = GWT.create(ActivityDraftUiBinder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String container();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	HTMLPanel centerContainer;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private ActivityDraftObject activityDraftObject;
	private Integer newId;
	
	private Activity activity;
	
	private AonToolbar toolbar;
	private AonToolbarButton accept;
	private AonToolbarButton newCCC;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public ActivityDraft() {	
		activity = new ActivityImplementation();
		toolbar = getToolbarPanel();
		
		// Inicializamos la vista de la actividad
		initWidget(uiBinder.createAndBindUi(this));
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		dockLayoutPanel.addStyleName(style.container());
		
		centerContainer.add(activity);
		
	}
	
	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------

	public void setActivityDraftObject(ActivityDraftObject activityDraftObject) {
		this.activityDraftObject = activityDraftObject;
		this.newId = -1;
		
		this.activityDraftObject.initializeActivity(
				r -> {
					initSuggestBox();
					fillActivityInfo();
					activity.cccWidget.resetPreview();
					activity.onInsertRows();
					
				}, t -> {}
			);
		
	}
	
	private void initSuggestBox() {
		List<String> cnae2009Suggest = new ArrayList<String>();
		for(Entry<String, String> entry : activityDraftObject.getAllCNAE2009().entrySet())
			cnae2009Suggest.add(entry.getKey() + " - " + entry.getValue());
	
		MultiWordSuggestOracle orclCNAE2009 = (MultiWordSuggestOracle) activity.activityCNAE2009.getSuggestOracle();
		orclCNAE2009.addAll(cnae2009Suggest);
		activity.activityCNAE2009.setAutoSelectEnabled(true);
	}
	
	private void fillActivityInfo() {
		activity.activityDescription.setValue(activityDraftObject.getActivityDescription());
		activity.activityCNAE2009.setValue(activityDraftObject.getActivityCNAE2009());
		activity.activityRegime.setText(activityDraftObject.getActivityRegime());
		activity.startDate.setValue(activityDraftObject.getActivityStartDate());
		activity.endDate.setValue(activityDraftObject.getActivityEndDate());
		activity.activityActive.setValue(activityDraftObject.getActivityActive());
	}
	
	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("Actividad");

		newCCC = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		newCCC.setAccessKey('N');
		newCCC.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onNewCCC(event);
			}
		});
		toolbar.add(newCCC);
		
		accept = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		accept.setAccessKey('G');
		accept.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAccept(event);
			}
		});
		toolbar.add(accept);

		return toolbar;

	}
	
	private void onAccept(ClickEvent event) {
		if(checkIfSaveIsPossible()){
			activityDraftObject.updateActivity(
					s -> {
						this.activityDraftObject.initializeActivity(
								r -> {
									initSuggestBox();
									fillActivityInfo();
									activity.cccWidget.resetPreview();
									activity.onInsertRows();
									
								}, t -> {}
							);
					},
					f -> {}
			);
		}else{
			WarningDialog dialog = new WarningDialog("Aviso", "Hay que rellenar los campos azules obligatoriamente.");
			dialog.center();
			dialog.show();
		}
	}
	
	private boolean checkIfSaveIsPossible() {
		if(!AonStringUtils.isBlank(activity.activityDescription.getValue()) && !AonStringUtils.isBlank(activity.activityCNAE2009.getValue())){
			return true;
		}else
			return false;
	}
	
	private void onNewCCC(ClickEvent event) {
		if(0 != activity.cccWidget.getRowCount()) {
			Label firstGeozone = (Label) activity.cccWidget.getWidget(0, 3);
			if(null != firstGeozone && "" != firstGeozone.getText()) {
				this.newId = activity.cccWidget.insertNewRow(this.newId);
			}
		}else
			this.newId = activity.cccWidget.insertNewRow(this.newId);
		
		activity.hideActivityColumn();
	}

}

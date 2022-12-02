package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;

public class ActivityDraft extends Composite{
	
	// ---------------------------------------------- Activity

	private class ActivityImplementation extends Activity{

		@Override
		public void onActivityDescriptionChange() {
			activityDraftObject.setActivityDescription(activityDescription.getValue());
		}

		@Override
		public void onActivityCNAE2009Change() {
			String cnae2009Value = activityCNAE2009.getValue();
			Optional<Entry<Integer, String>> cnae2009Opt = activityDraftObject.getAllCNAE2009().entrySet().stream().filter(entry -> AonStringUtils.equalsIgnoreCase(entry.getValue(), cnae2009Value)).findAny();
			if(cnae2009Opt.isPresent()) activityDraftObject.setActivityCNAE2009(cnae2009Opt.get());
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
			activityDraftObject.setActivityIsPrincipal(activityActive.getValue());
		}
		
		@Override
		public void onInsertRow() {
			activity.hideActivityColumn();
		}
		
		@Override
		public void onInsertRows() {
			activityDraftObject.getCCCs().forEach(ccc -> this.cccWidget.insertRow(ccc));
			activity.hideActivityColumn();
		}

		@Override
		public void onDeleteCCC(Integer cccId) {
			activityDraftObject.deleteCCC(cccId);
		}

		@Override
		public void onInsertCCC(EnterpriseCCC ccc) {
			activityDraftObject.insertCCC(ccc);
		}

		@Override
		public Set<Entry<Integer, String>> getActivities() {
			return activityDraftObject.getActivities();
		}

		@Override
		public void fireWarningMessage(Map<String, String> warningMap) {
			AonMessagePanel.showWarning(messagePanel, warningMap);
		}
		
	}
	
	// ---------------------------------------------- UiBinder
	
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
	
	@UiField
	HTMLPanel messagePanel;
	
	// ---------------------------------------------- Variables
	
	private ActivityDraftObject activityDraftObject;
	
	private Activity activity;
	
	private AonToolbar toolbar;
	
	// ---------------------------------------------- Constructor

	public ActivityDraft() {	
		activity = new ActivityImplementation();
		getToolbarPanel();
		
		// Inicializamos la vista de la actividad
		initWidget(uiBinder.createAndBindUi(this));
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		dockLayoutPanel.addStyleName(style.container());
		
		activity.setActivityDraftCCCHeight();
		
		centerContainer.add(activity);
		centerContainer.getElement().getStyle().setMarginTop(40, Unit.PX);
	}
	
	// ---------------------------------------------- setActivityDraftObject

	public void setActivityDraftObject(ActivityDraftObject activityDraftObject) {
		this.activityDraftObject = activityDraftObject;
		initializeActivity();
	}
	
	private void initializeActivity() {
		activityDraftObject.initializeActivity(
				r -> {
					initSuggestBox();
					fillActivityInfo();
					activity.cccWidget.setDomain(activityDraftObject.getDomain());
					activity.cccWidget.resetPreview();
					activity.onInsertRows();
				}, t -> {}
			);
	}
	
	private void initSuggestBox() {
		List<String> cnae2009Suggest = new ArrayList<>();
		for(Entry<Integer, String> entry : activityDraftObject.getAllCNAE2009().entrySet())
			cnae2009Suggest.add(entry.getValue());
	
		MultiWordSuggestOracle orclCNAE2009 = (MultiWordSuggestOracle) activity.activityCNAE2009.getSuggestOracle();
		orclCNAE2009.addAll(cnae2009Suggest);
		activity.activityCNAE2009.setAutoSelectEnabled(true);
	}
	
	private void fillActivityInfo() {
		activity.activityDescription.setValue(activityDraftObject.getActivityDescription());
		activity.activityCNAE2009.setValue(activityDraftObject.getActivityCNAE2009());
		activity.startDate.setValue(activityDraftObject.getActivityStartDate());
		activity.endDate.setValue(activityDraftObject.getActivityEndDate());
		activity.activityActive.setValue(activityDraftObject.getActivityPrincipal());
	}
	
	// ---------------------------------------------- Toolbar
	
	private void getToolbarPanel() {
		
		toolbar = new AonToolbar("Actividad");

		AonToolbarButton accept = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		accept.addClickHandler(e -> onAccept());
		toolbar.add(accept);
		
		AonToolbarButton checkUpdateCert = new AonToolbarButton("Cert. de estar al corriente con TGSS", AON.CSS.aonIconTgss() );
		checkUpdateCert.addClickHandler(e -> onCheckUpdateCert());
		toolbar.add(checkUpdateCert);
	}
	
	// ---------------------------------------------- Toolbar.Methods

	private void onAccept() {
		if(checkIfSaveIsPossible()) updateActivity();
	}

	private void updateActivity() {
		activityDraftObject.updateActivity(
			s -> reloadActivity(),
			f -> {}
		);
	}
	
	private void reloadActivity() {
		initializeActivity();
		Map<String, String> successMap = new HashMap<>();
		successMap.put("Actividad actualizada", "Los cambios realizados se han guardado correctamente");
		AonMessagePanel.showSuccess(messagePanel, successMap);
	}

	private boolean checkIfSaveIsPossible() {
		return !AonStringUtils.isBlank(activity.activityDescription.getValue()) && 
			!AonStringUtils.isBlank(activity.activityCNAE2009.getValue()) && 
			!AonStringUtils.equalsIgnoreCase(activity.activityCNAE2009.getValue(), "-");
	}
	
	// ---------------------------------------------- Toolbar.Methods TGSS
	
	private void onCheckUpdateCert() {
		submitForm(0);
	}
	
	private void submitForm(int type) {
		Pair<String, String> completeCCC = activityDraftObject.getPrincipalAccount();
		
		String fileDownloadURL = GWT.getModuleBaseURL() + "sistema_red_ccc";

		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(fileDownloadURL);
		formPanel.setMethod(FormPanel.METHOD_GET);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden("ccc", completeCCC.getValue()));
		flowPanel.add(new Hidden("regime", completeCCC.getKey()));
		flowPanel.add(new Hidden("type", Integer.toString(type)));
		flowPanel.add(new Hidden("login", Wnd.getCurrentUser()));
		flowPanel.add(new Hidden("domain", Wnd.getCurrentDomainNameURL()));
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> centerContainer.remove(formPanel));

		centerContainer.add(formPanel);

		formPanel.submit();
	}
	
}

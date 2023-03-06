package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ActivityDialog extends AonCustomDialog {

	private class ActivityImplementation extends Activity{

		@Override
		public void onActivityDescriptionChange() {
			activityDialogObject.setActivityDescription(activityDescription.getValue());
		}

		@Override
		public void onActivityCNAE2009Change(Integer cnaeId, String cnaeCode, String cnaeTitle) {
			activityDialogObject.setActivityCNAE2009(cnaeId, cnaeCode, cnaeTitle);
		}

		@Override
		public void onActivityStartDateChange() {
			activityDialogObject.setActivityStartDate(startDate.getValue());
		}

		@Override
		public void onActivityEndDateChange() {
			activityDialogObject.setActivityEndDate(endDate.getValue());
		}

		@Override
		public void onActivityActiveChange() {
			activityDialogObject.setActivityActive(activityActive.getValue());
		}
		
		@Override
		public void onInsertRow() {
			activity.hideActivityColumn();
		}
		
		@Override
		public void onInsertRows() {
			activityDialogObject.getCCCs().forEach(ccc -> this.cccWidget.insertRow(ccc));
			activity.hideActivityColumn();
		}

		@Override
		public void onDeleteCCC(Integer cccId) {
			activityDialogObject.deleteCCC(cccId);
		}

		@Override
		public void onInsertCCC(EnterpriseCCC ccc) {
			activityDialogObject.insertCCC(ccc);
		}

		@Override
		public Set<Entry<Integer, String>> getActivities() {
			return Collections.emptySet();
		}
		
		@Override
		public List<EnterpriseCCC> getEnterpriseCCCs() {
			return activityDialogObject.getActiveCCCs();
		}

		@Override
		public void fireErrorMessage(Map<String, String> messages) {
			AonMessagePanel.showError(messagePanel, messages);
		}
		
		@Override
		public void fireWarningMessage(Map<String, String> messages) {
			AonMessagePanel.showWarning(messagePanel, messages);
		}
		
		@Override
		public void fireInfoMessage(Map<String, String> messages) {
			AonMessagePanel.showInfo(messagePanel, messages);
		}

		@Override
		protected void fireLoadingMessage(String message) {
			AonMessagePanel.showLoading(messagePanel, message);
		}

		@Override
		protected void fireHideMessage() {
			AonMessagePanel.hideMessage(messagePanel);
		}

		@Override
		public void showPDF(String dataURI, boolean isLaboralLife) {
			// Nothing to do here
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface ActivityDraftUiBinder extends UiBinder<Widget, ActivityDialog> {}
	
	private static ActivityDraftUiBinder binder = GWT.create(ActivityDraftUiBinder.class);
	
	@UiField (provided = true)
	AonToolbar toolbar;
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField (provided = true)
	Activity activity;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private ActivityDialogObject activityDialogObject;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public ActivityDialog() {	
		activity = new ActivityImplementation();
		activity.cccWidget.setDialogHeight();
		
		createToolbar();
		
		setCaption("Nueva Actividad");
		setWidget(binder.createAndBindUi(this));
		
		getButtonsPanel();
	}

	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------

	public void setActivityDialogObject(ActivityDialogObject activityDialogObject) {
		this.activityDialogObject = activityDialogObject;
		activity.cccWidget.setDomain(activityDialogObject.getDomain());
		activity.hideActivityColumn();
		showDialog();
	}
	
	private void getButtonsPanel() {
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.addClickHandler(e -> onCloseDialog());
		buttonsPanel.add(closeBtnDialog);
		
		Button acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> onAcceptDialog());
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private void onCloseDialog() {
		hide();
	}
	
	private void onAcceptDialog() {
		if(checkIfSaveIsPossible()){
			activityDialogObject.createActivity(
				s -> {
					hide();
					EmployeeTree.invokeRefreshEnterprise();
				},
				f -> {}
			);
		}else {
			Map<String, String> warningMap = new HashMap<>();
			warningMap.put("CUIDADO", "Hay que rellenar los campos azules obligatoriamente.");
			AonMessagePanel.showWarning(messagePanel, warningMap);
		}
	}
	
	private boolean checkIfSaveIsPossible() {
		return !AonStringUtils.isBlank(activity.activityDescription.getValue());
	}
	
	private void createToolbar() {
		toolbar = new AonToolbar();
		
		AonToolbarButton addCCCBtn = new AonToolbarButton(AON.MSG.newAction() + " CCC", AON.CSS.aonIconAdd());
		addCCCBtn.ensureDebugId("addCCCBtn");
		addCCCBtn.addClickHandler(e -> activity.onAddNewCCC());
		toolbar.add(addCCCBtn);
	}
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

}

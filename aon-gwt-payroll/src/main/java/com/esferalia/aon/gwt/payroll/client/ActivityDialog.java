package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;

public class ActivityDialog extends AonCustomDialog {

	private class ActivityImplementation extends Activity{

		protected ActivityImplementation(com.esferalia.aon.occam.api.model.payroll.Activity activity) {
			super(activity);
		}

		@Override
		public void showPDF(String dataURI, boolean isLaboralLife) {
			// Nothing to do here
		}

		@Override
		public void onActivityDescriptionChange(String description) {
			activityDialogObject.setActivityDescription(description);
		}

		@Override
		public void onActivityCNAE2009Change(Integer cnaeId, String cnaeCode, String cnaeTitle) {
			activityDialogObject.setActivityCNAE2009(cnaeId, cnaeCode, cnaeTitle);
		}

		@Override
		public void onActivityStartDateChange(Date startDate) {
			activityDialogObject.setActivityStartDate(startDate);
		}

		@Override
		public void onActivityEndDateChange(Date endDate) {
			activityDialogObject.setActivityEndDate(endDate);
		}

		@Override
		public void onActivityActiveChange(Boolean principal) {
			activityDialogObject.setActivityActive(principal);
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	private HTMLPanel content = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	private Activity activity;
	private HTMLPanel buttonsPanel = new HTMLPanel(AonStringUtils.EMPTY);
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private ActivityDialogObject activityDialogObject;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public ActivityDialog() {	
		setCaption("Nueva Actividad");
	}

	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------

	public void setActivityDialogObject(ActivityDialogObject activityDialogObject) {
		this.activityDialogObject = activityDialogObject;
		
		activity = new ActivityImplementation(activityDialogObject.getActivity());
		activity.hideCCCCard();
		
		getButtonsPanel();
		
		content.addStyleName(AON.CSS.aonFlexColumn());
		content.add(messagePanel);
		content.add(activity);
		content.add(buttonsPanel);
		
		add(content);
		showDialog();
	}
	
	private void getButtonsPanel() {
		buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		buttonsPanel.getElement().getStyle().setProperty("margin", "1rem");
		
		Button closeBtnDialog = createButton("Cancelar");
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);
		
		Button acceptBtnDialog = createButton("Crear");
		acceptBtnDialog.getElement().getStyle().setProperty("color", "green");
		acceptBtnDialog.addClickHandler(e -> onAcceptDialog());
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private Button createButton(String text) {
		Button button = new Button(text);
		button.getElement().getStyle().setProperty("background", "none");
		button.getElement().getStyle().setProperty("background-color", "#fafafa");
		button.getElement().getStyle().setProperty("padding", "5px");
		button.getElement().getStyle().setProperty("height", "auto");
		button.getElement().getStyle().setProperty("font-size", "12px");
//		button.getElement().getStyle().setProperty("font-family", "Arial Unicode MS, Arial, sans-serif");
		button.getElement().getStyle().setProperty("text-transform", "inherit");
		button.getElement().getStyle().setProperty("font-weight", "bold");
		button.getElement().getStyle().setProperty("border", "1px solid #d0d0d0");
		button.getElement().getStyle().setProperty("border-radius", "5px");
		
		return button;
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
		}else
			AonMessagePanel.showWarning(messagePanel, "El campo descripci\u00f3n es obligatorio.");
	}
	
	private boolean checkIfSaveIsPossible() {
		return !AonStringUtils.isBlank(activityDialogObject.getActivity().getDescription());
	}
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

}

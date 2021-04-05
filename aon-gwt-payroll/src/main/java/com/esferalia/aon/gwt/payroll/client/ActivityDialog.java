package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;

public class ActivityDialog extends AonCustomDialog {

	private class ActivityImplementation extends Activity{

		@Override
		public void onActivityDescriptionChange() {
			activityDialogObject.setActivityDescription(activityDescription.getValue());
		}

		@Override
		public void onActivityCNAE2009Change() {
			activityDialogObject.setActivityCNAE2009(activityCNAE2009.getValue());
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
			for(CCCInfo cccInfo : activityDialogObject.getCCCs().values()) {
				this.cccWidget.insertRow(cccInfo);
			}
		}

		@Override
		public void onDeleteCCC(Integer cccId) {
			activityDialogObject.deleteCCC(cccId);
		}

		@Override
		public void onInsertCCC(Integer cccId, int activityId, byte cccRegime, String cccRegimeCode, String account, String province, String provinceCode) {
			activityDialogObject.insertCCC(cccId, account, cccRegimeCode, account, cccRegime, province, provinceCode, false, false);
		}

		@Override
		public Set<Entry<Integer, String>> getActivities() {
			return null;
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface ActivityDraftUiBinder extends UiBinder<Widget, ActivityDialog> {}
	
	private static ActivityDraftUiBinder binder = GWT.create(ActivityDraftUiBinder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {}
	
	@UiField (provided = true)
	Activity activity;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private ActivityDialogObject activityDialogObject;
	
	private Button closeBtnDialog;
	private Button acceptBtnDialog;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public ActivityDialog() {	
		activity = new ActivityImplementation();
		activity.cccWidget.setDialogHeight();
		
		setCaption("Nueva Actividad");
		setWidget(binder.createAndBindUi(this));
		
		getButtonsPanel();
	}
	
	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------

	public void setActivityDialogObject(ActivityDialogObject activityDialogObject) {
		this.activityDialogObject = activityDialogObject;
		activityDialogObject.getCNAE2009(
				s -> {
					activity.activityRegime.setText(activityDialogObject.getActivityRegime());
					initSuggestBox();
					activity.hideActivityColumn();
				},
				f -> {}
		);	
	}
	
	private void initSuggestBox() {
		List<String> cnae2009Suggest = new ArrayList<String>();
		for(Entry<String, String> entry : activityDialogObject.getAllCNAE2009().entrySet())
			cnae2009Suggest.add(entry.getKey() + " - " + entry.getValue());
	
		MultiWordSuggestOracle orclCNAE2009 = (MultiWordSuggestOracle) activity.activityCNAE2009.getSuggestOracle();
		orclCNAE2009.addAll(cnae2009Suggest);
		activity.activityCNAE2009.setAutoSelectEnabled(false);
	}
	
	private void getButtonsPanel() {
		closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.setAccessKey('C');
		closeBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCloseDialog(event);
			}
		});
		
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		
		buttonsPanel.add(closeBtnDialog);
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.setAccessKey('A');
		acceptBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAcceptDialog(event);
			}
		});
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private void onCloseDialog(ClickEvent event) {
		hide();
	}
	
	private void onAcceptDialog(ClickEvent event) {
		if(checkIfSaveIsPossible()){
			activityDialogObject.createActivity(
				s -> {
					hide();
					EmployeeTree.invokeRefreshEnterprise();
				},
				f -> {}
			);
		}else{
			AonDialog dialog = new AonDialog("CUIDADO", new HTML("Hay que rellenar los campos azules obligatoriamente."));
			dialog.warning();
		}
	}
	
	private boolean checkIfSaveIsPossible() {
		if(!AonStringUtils.isBlank(activity.activityDescription.getValue()) && !AonStringUtils.isBlank(activity.activityCNAE2009.getValue())){
			return true;
		}else
			return false;
	}

}

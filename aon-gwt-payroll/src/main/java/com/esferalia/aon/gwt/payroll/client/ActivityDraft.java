package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAcceptDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAcceptDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.PayrollPrintService;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.XMLHttpRequest;

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
		public void onInsertRow() {
			activity.hideActivityColumn();
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
			activityDraftObject.insertCCC(cccId, account, cccRegimeCode, account, cccRegime, province, provinceCode, false, false);
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
	
	private Activity activity;
	
	private AonToolbar toolbar;
	private AonToolbarButton accept;
	private AonToolbarButton checkUpdateCert;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public ActivityDraft() {	
		activity = new ActivityImplementation();
		toolbar = getToolbarPanel();
		
		// Inicializamos la vista de la actividad
		initWidget(uiBinder.createAndBindUi(this));
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		dockLayoutPanel.addStyleName(style.container());
		
		centerContainer.add(activity);
		centerContainer.getElement().getStyle().setMarginTop(40, Unit.PX);
		
	}
	
	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------

	public void setActivityDraftObject(ActivityDraftObject activityDraftObject) {
		this.activityDraftObject = activityDraftObject;
		initializeActivity();
	}
	
	private void initializeActivity() {
		activityDraftObject.initializeActivity(
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

		accept = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		accept.addClickHandler(e -> {
			onAccept(e);
		});
		toolbar.add(accept);
		
		checkUpdateCert = new AonToolbarButton("Cert. de estar al corriente con TGSS", AON.CSS.aonIconTgss() );
		checkUpdateCert.addClickHandler(e -> {
			onCheckUpdateCert(e);
		});
		toolbar.add(checkUpdateCert);

		return toolbar;

	}

	private void onAccept(ClickEvent event) {
		if(checkIfSaveIsPossible()){
			Map<Integer, CCCInfo> deleteCCCs = activityDraftObject.getDeleteCCCs();
			if(!deleteCCCs.isEmpty()) {
				boolean hasContractsOrCras = hasContractOrCra(deleteCCCs);
				if(hasContractsOrCras) {
					activityDraftObject.getDeleteCCCMessage(deleteCCCs.keySet(),
							message -> {
								new AonAcceptDialog("BORRADO", new HTML(message), new AonAcceptDialogCallback() {
									
									@Override
									public void onCancel() {
										initializeActivity();
									}
									
									@Override
									public void onAccept() {
										updateActivity();
									}
								});
							},
							f -> {});
				} else 
					updateActivity();
			} else
				updateActivity();
			
		}else{
			WarningDialog dialog = new WarningDialog("Aviso", "Hay que rellenar los campos azules obligatoriamente.");
			dialog.center();
			dialog.show();
		}
	}
	
	private boolean hasContractOrCra(Map<Integer, CCCInfo> deleteCCCs) {
		for(CCCInfo cccInfo : deleteCCCs.values())
			if(cccInfo.isUseByContracts() || cccInfo.isUseByCRAs())
				return true;
		return false;
	}

	private void updateActivity() {
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
	}
	
	private boolean checkIfSaveIsPossible() {
		if(!AonStringUtils.isBlank(activity.activityDescription.getValue()) && !AonStringUtils.isBlank(activity.activityCNAE2009.getValue())){
			return true;
		}else
			return false;
	}
	
	private void onCheckUpdateCert(ClickEvent e) {
		submitForm(0);
	}
	
	private void __submitForm(int type) {
		Pair<String, String> completeCCC = activityDraftObject.getPrincipalAccount();
		
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("POST", GWT.getModuleBaseURL() + "sistema_red_ccc");
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

		StringBuffer requestDataBuffer = new StringBuffer();

		requestDataBuffer
		.append("domainName" + "=" + Wnd.getCurrentDomainNameURL())
		.append("&userLogin" + "=" + Wnd.getCurrentUser())
		.append("&type" + "=" + type)
		.append("&regime" + "=" + completeCCC.getKey())
		.append("&ccc" + "=" + completeCCC.getValue())
		;
		
		xhr.send(requestDataBuffer.toString());
	}
	
	private void submitForm(int type) {
		Pair<String, String> completeCCC = activityDraftObject.getPrincipalAccount();
		
		String fileDownloadURL = GWT.getModuleBaseURL() + "sistema_red_ccc";

		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(fileDownloadURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden("ccc", completeCCC.getValue()));
		flowPanel.add(new Hidden("regime", completeCCC.getKey()));
		flowPanel.add(new Hidden("type", Integer.toString(type)));
		flowPanel.add(new Hidden("userLogin", Wnd.getCurrentUser()));
		flowPanel.add(new Hidden("domainName", Wnd.getCurrentDomainNameURL()));
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> {
			centerContainer.remove(formPanel);
		});

		centerContainer.add(formPanel);

		formPanel.submit();
		
	}
	

}

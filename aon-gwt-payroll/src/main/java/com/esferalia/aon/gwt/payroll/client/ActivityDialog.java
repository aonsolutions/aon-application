package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.payroll.client.EmployeeDialog.Callback;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ActivityDialog extends CustomDialog {

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
		public void onActivityNewAccountChange() {
			if(0 != cccDataTable.getRowCount()) {
				Label firstGeozone = (Label) cccDataTable.getWidget(0, 3);
				if(null != firstGeozone && "" != firstGeozone.getText()) {
					insertNewRow();
				}
			}else
				insertNewRow();
		}
		
		private void insertNewRow() {
			int row = cccDataTable.insertRow(0);
			
			Label id = new Label("");
			
			ListBox types = createCCCRegimeListBox();
			
			Label geozone = new Label("");
			geozone.addStyleName(style.elementWidth80());
			
			HorizontalPanel hPanel = new HorizontalPanel();
			Label typeCode = new Label("0111");
			typeCode.addStyleName(style.paddingTop());
			Label accountStatus = new Label();
			TextBox account = new TextBox();
			account.setMaxLength(11);
			account.setValue("");
			account.addStyleName("aon-inputText");
			account.addStyleName(style.elementWidth95());
			account.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					if(account.getValue().length() >= 2) {
						String province = ProvinceContract.getName(account.getValue().substring(0, 2));
						if(null != province && checkCCC(account.getValue())) {
							geozone.setText(province);
							accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
							accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
							newId--;
							activityDialogObject.insertCCC(newId, account.getValue(), typeCode.getText(), account.getValue(), (byte) types.getSelectedIndex(), province, false);
							initPreview();
						}else {
							accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
							accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
						}
						
					}
				}
			});
			hPanel.add(typeCode);
			hPanel.add(account);
			hPanel.add(accountStatus);
			
			types.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					String newCCCRegimeCode = getCCCRegimeCode((byte)types.getSelectedIndex());
					typeCode.setText(newCCCRegimeCode);
				}
			});
			
			Button delete = new Button();
			delete.setStyleName("aon-editDataTable-button aon-icon-cancel");
			delete.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					cccDataTable.removeRow(0);
				}
			});
			
			cccDataTable.setWidget(row, 0, id);
			cccDataTable.setWidget(row, 1, types);
			cccDataTable.setWidget(row, 2, hPanel);
			cccDataTable.setWidget(row, 3, geozone);
			cccDataTable.setWidget(row, 4, delete);
		}
		
	}
	
	interface Callback {
		void onAccept(ActivityDialog dialog);
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface ActivityDraftUiBinder extends UiBinder<Widget, ActivityDialog> {
	
	}
	
	private static ActivityDraftUiBinder binder = GWT.create(ActivityDraftUiBinder.class);
	
	@UiField (provided = true)
	Activity activity;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private Callback cb;
	private ActivityDialogObject activityDialogObject;
	private Integer newId;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public ActivityDialog() {	
		activity = new ActivityImplementation();
		
		setCaption("Actividad");
		setWidget(binder.createAndBindUi(this));
	}
	
	public void show(Callback cb) {
		this.cb = cb;
		super.show();
	}
	
	public void setPopupPositionAndShow(PositionCallback positionCallback, Callback callback) {
		this.cb = callback;
		super.setPopupPositionAndShow(positionCallback);
	}
	
	// -------------------------------------------------- UiHandlers --------------------------------------------------
	
	@UiHandler("cancelButton")
	public void onCancelClick(ClickEvent event) {
		hide();
	}
	
	@UiHandler("acceptButton")
	public void onSaveClick(ClickEvent event) {
		if(checkIfSaveIsPossible()){
			if(checkIfCCCSaveIsPossible())
				activityDialogObject.createActivity(
					s -> {
						hide();
						EmployeeTree.invokeRefreshEnterprise();
//						cb.onAccept(this);
					},
					f -> {}
				);
			else {
				WarningDialog dialog = new WarningDialog("Aviso", "Compruebe que las cuentas de cotizacion son correctas.");
				dialog.center();
				dialog.show();
			}
		}else{
			WarningDialog dialog = new WarningDialog("Aviso", "Hay que rellenar los campos azules obligatoriamente.");
			dialog.center();
			dialog.show();
		}
	}
	
	private boolean checkIfSaveIsPossible() {
		if(
			"" != activity.activityDescription.getValue() &&
			"" != activity.activityCNAE2009.getValue() &&
			"" != activity.activityRegime.getText()
		){
			return true;
		}else
			return false;
	}
	
	private boolean checkIfCCCSaveIsPossible() {
		if(activity.cccDataTable.getRowCount() == 0)
			return true;
		else{
			for(int i = 0; i < activity.cccDataTable.getRowCount(); i++){
				HorizontalPanel hPanel = (HorizontalPanel) activity.cccDataTable.getWidget(i, 2);
				TextBox ccc = (TextBox) hPanel.getWidget(1);
				String province = ProvinceContract.getName(ccc.getValue().substring(0, 2));
				if(null == province || !checkCCC(ccc.getValue()))
					return false;
			}
			return true;
		}
	}

	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------

	public void setActivityDialogObject(ActivityDialogObject activityDialogObject) {
		this.activityDialogObject = activityDialogObject;
		this.newId = 0;
		activityDialogObject.getCNAE2009(
				s -> {
					activity.activityRegime.setText(activityDialogObject.getActivityRegime());
					initSuggestBox();
					initPreview();	
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
	
	private void initPreview() {
		activity.cccDataTableHeader.clear();
		activity.cccDataTableHeader.resize(0, 0);
		activity.cccDataTable.clear();
		activity.cccDataTable.resize(0, 0);
		activity.cccDataTableHeader.resizeColumns(5);
		activity.cccDataTable.resizeColumns(5);
		paintHeader();
		insertRows();
		calculateScrollPanelHeight();
		hideFirstColumn();
		setColumnWidth();
		center();
	}
	
	private void paintHeader() {
		int row = activity.cccDataTableHeader.insertRow(activity.cccDataTableHeader.getRowCount());
		Label id = new Label("");
		Label type = new Label("TIPO");
		Label account = new Label("CUENTA");
		Label geozone = new Label("PROVINCIA");
		Label blank = new Label("");
		
		id.addStyleName(activity.style.headerStyle());
		type.addStyleName(activity.style.headerStyle());
		account.addStyleName(activity.style.headerStyle());
		geozone.addStyleName(activity.style.headerStyle());
		
		activity.cccDataTableHeader.setWidget(row, 0, id);
		activity.cccDataTableHeader.setWidget(row, 1, type);
		activity.cccDataTableHeader.setWidget(row, 2, account);
		activity.cccDataTableHeader.setWidget(row, 3, geozone);
		activity.cccDataTableHeader.setWidget(row, 4, blank);
	}
	
	private void insertRows() {
		for(CCCInfo cccInfo : activityDialogObject.getCCCs().values()) {
			int row = activity.cccDataTable.insertRow(activity.cccDataTable.getRowCount());
			
			Label id = new Label();
			id.setText(cccInfo.getCccId().toString());
			
			ListBox types = createCCCRegimeListBox();
			types.setSelectedIndex(cccInfo.getType());
			
			Label geozone = new Label(cccInfo.getGeozone());
			geozone.addStyleName(activity.style.elementWidth80());
			
			HorizontalPanel hPanel = new HorizontalPanel();
			Label typeCode = new Label(cccInfo.getCccRegimeCode());
			typeCode.addStyleName(activity.style.paddingTop());
			Label accountStatus = new Label();
			TextBox account = new TextBox();
			account.setMaxLength(11);
			account.setValue(cccInfo.getCcc());
			account.addStyleName("aon-inputText");
			account.addStyleName(activity.style.elementWidth95());
			account.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					if(account.getValue().length() >= 2) {
						String province = ProvinceContract.getName(account.getValue().substring(0, 2));
						if(null != province && checkCCC(account.getValue())) {
							geozone.setText(province);
							accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
							accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
							activityDialogObject.insertCCC(cccInfo.getCccId(), account.getValue(), typeCode.getText(), account.getValue(), (byte) types.getSelectedIndex(), province);
						}else {
							accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
							accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
						}
						
					}
				}
			});
			hPanel.add(typeCode);
			hPanel.add(account);
			hPanel.add(accountStatus);
			String province = ProvinceContract.getName(account.getValue().substring(0, 2));
			if(null != province && checkCCC(account.getValue())) {
				accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
				accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
			}else {
				accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
				accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
			}
			
			types.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					String newCCCRegimeCode = getCCCRegimeCode((byte)types.getSelectedIndex());
					typeCode.setText(newCCCRegimeCode);
					activityDialogObject.insertCCC(cccInfo.getCccId(), account.getValue(), typeCode.getText(), account.getValue(), (byte) types.getSelectedIndex(), province);
				}
			});
			
			Button delete = new Button();
			delete.setStyleName("aon-editDataTable-button aon-icon-delete");
			delete.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if(cccInfo.isUseByContracts()) {
						WarningDialog warnignDialog = new WarningDialog("AVISO", "No se puede eliminar una cuenta de cotización que esta "
								+ "siendo usada por un centro de trabajo y/o por un contrato");
						warnignDialog.center();
						warnignDialog.show();
					}else {
//						Window.alert("Borrar Id : " + cccInfo.getCccId());
						activityDialogObject.deleteCCC(cccInfo.getCccId());
						initPreview();
					}
				}
			});
		
			activity.cccDataTable.setWidget(row, 0, id);
			activity.cccDataTable.setWidget(row, 1, types);
			activity.cccDataTable.setWidget(row, 2, hPanel);
			activity.cccDataTable.setWidget(row, 3, geozone);
			activity.cccDataTable.setWidget(row, 4, delete);
		}
	}
	
	private void calculateScrollPanelHeight() {
		Integer height = 100;
		Integer extra = 30;
		int rows = activity.cccDataTable.getRowCount();
		if(rows < 4) {
			int mod = rows%4;
			activity.scrollPanel.setHeight((mod*extra+extra)+"px");
		}else {
			int div = rows/4;
			int mod = rows%4;
			if(div < 2)
				activity.scrollPanel.setHeight(((height*div)+(extra*mod)+extra)+"px");
			else
				activity.scrollPanel.setHeight("220px");
		}
	}
	
	private void hideFirstColumn() {
		for(int row = 0; row < activity.cccDataTableHeader.getRowCount(); row++)
			activity.cccDataTableHeader.getWidget(row, 0).addStyleName(activity.style.hide());
		
		for(int row = 0; row < activity.cccDataTable.getRowCount(); row++)
			activity.cccDataTable.getWidget(row, 0).addStyleName(activity.style.hide());
	}
	
	private void setColumnWidth() {
		activity.cccDataTableHeader.getCellFormatter().addStyleName(0, 0, activity.style.hide());
		activity.cccDataTableHeader.getCellFormatter().addStyleName(0, 0, activity.style.widthO());
		activity.cccDataTableHeader.getCellFormatter().addStyleName(0, 1, activity.style.columnWidth());
		activity.cccDataTableHeader.getCellFormatter().addStyleName(0, 2, activity.style.columnWidth2());
		activity.cccDataTableHeader.getCellFormatter().addStyleName(0, 3, activity.style.columnWidth2());
		
		activity.cccDataTable.getColumnFormatter().addStyleName(1, activity.style.columnWidth());
		activity.cccDataTable.getColumnFormatter().addStyleName(2, activity.style.columnWidth2());
		activity.cccDataTable.getColumnFormatter().addStyleName(3, activity.style.columnWidth2());
	}
	
	// ----------------------------------------------- METODOS AUXILIARES ------------------------------------------------
	
	private ListBox createCCCRegimeListBox(){
		ListBox cccRegime = new ListBox();
		cccRegime.addItem("Principal");
		cccRegime.addItem("Formacion y aprendizaje");
		cccRegime.addItem("Aprendizaje");
		cccRegime.addItem("Representantes de comercio");
		cccRegime.addItem("Asimilados R.General");
		cccRegime.addItem("Becarios");
		cccRegime.addItem("Emploead@s de hogar");
		cccRegime.addItem("Trabajadores cuenta ajena agrarios");
		cccRegime.getElement().getElementsByTagName("option").getItem(2).setAttribute("disabled", "disabled");
		cccRegime.addStyleName("aon-selectOneMenu");
		cccRegime.addStyleName(activity.style.elementWidth80());
	
		return cccRegime;
	}
	
	private boolean checkCCC(String ccc) {
		if(ccc.length() == 11) {
			String code = ccc.substring(ccc.length()-2, ccc.length());
			Integer codeInt = Integer.parseInt(code);
			Integer cccInt = Integer.parseInt(ccc.substring(0, ccc.length()-2));
//			Window.alert("CCC : " + cccInt + ", Code : " + codeInt + ", MOD : " + cccInt % 97);
			if(cccInt % 97 == codeInt)
				return true;
			else
				return false;
		}else
			return false;
	}
	
	private static String getCCCRegimeCode(Byte cccRegime) {
		switch (cccRegime) {
		case 0:
			return "0111";
		case 1:
			return "0111";
		case 2:
			return "0111";
		case 3:
			return "0111";
		case 4:
			return "0111";
		case 5:
			return "0111";
		case 6:
			return "0138";
		case 7:
			return "0163";
		default:
			return "0111";
		}
	}

}

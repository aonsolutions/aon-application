package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SSBonusDraft extends CustomDialog {
	
	// -------------------------------------------------- UiBinder --------------------------------------------------

	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	interface Binder extends UiBinder<Widget, SSBonusDraft> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// -------------------------------------------------- UiFields --------------------------------------------------
			
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String hide();
		String buttonTable();
		String bold();
		String formulaStyle();
		String descriptionStyle();
		String dateStyle();
		String selected();
		String selectedBG();
		String unSelected();
	}
	
	//ELEMENTOS HTML
	
	@UiField
	Button addBonusesButton;
	
//	@UiField
//	Button saveButton;
	
	@UiField
	TextBox idBonus;
	
	@UiField
	DateBoxEx startDateBonus;
	
	@UiField
	DateBoxEx endDateBonus;
	
	@UiField
	SuggestBox descriptionBonus;
	
	@UiField
	ListBox typeBonus;
	
	@UiField
	TextBox formulaBonus;
	
	@UiField
	HTMLPanel checksPanel;
	
	@UiField
	TextBox percentBonus;
	
	@UiField
	CheckBox checkCommonC;
	
	@UiField
	CheckBox checkAccidentC;
	
	@UiField
	CheckBox checkUnemploymentC;
	
	@UiField
	CheckBox checkFogasaC;
	
	@UiField
	CheckBox checkFormationC;
	
	@UiField
	Button applyChecks;
	
	@UiField
	HTMLPanel amountPanel;
	
	@UiField
	TextBox amountBonus;
	
	@UiField
	Button applyAmount;
	
	@UiField
	HTMLPanel listBonusesPanel;
	
	@UiField
	Grid listBonusesTable;
	
	@UiField
	VerticalPanel ssBonusDataTable;
	
	//BUTTONS ACCEPT AND CANCEL
	
	@UiField
	Button acceptButton;
	
	@UiField
	Button cancelButton;
	
// ------------------------------------------------------------ VARIABLES DE LA CLASE ----------------------------------------------------
		
//	private SSBonusDraftObject ssBonusDraftObject;
	private Integer contractId;
	private List<SSBonusData> ssBonuses;
	private List<SSBonusData> bonusConcepts;
	
	
// ---------------------------------------------------------------- CONSTRUCTOR ----------------------------------------------------------
	
	public SSBonusDraft(Integer contractId) {
		
		setCaption("Bonificaciones");
		
		setWidget(binder.createAndBindUi(this));
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAccept(
						s -> {
							showTable();
						},
						f -> {}
						);
				
			}
		});		
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showTable();
			}
		});
		
		this.ssBonuses = new ArrayList<>();
		this.bonusConcepts = new ArrayList<>();
		this.contractId = contractId;
		
		impl.getEmployeeSSBonuses(this.contractId, new AsyncCallback<List<SSBonusData>>() {
			
			@Override
			public void onSuccess(List<SSBonusData> result) {
				ssBonuses = result;
				impl.getBonusConcepts(new AsyncCallback<List<SSBonusData>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub	
					}

					@Override
					public void onSuccess(List<SSBonusData> result) {
						bonusConcepts = result;
						showTable();
					}
					
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				
			}
		});

	}
	
	private void showTable() {
		cleanSelected();
		initializePage();
		listBonusesPanel.removeStyleName(style.hide());
		ssBonusDataTable.addStyleName(style.hide());
	}
	
	private void hideTable() {
		cleanSelected();
		initializePage();
		listBonusesPanel.addStyleName(style.hide());
		ssBonusDataTable.removeStyleName(style.hide());
	}
	
	private void onAccept(Consumer<List<SSBonusData>> success, Consumer<Throwable> failure) {
		Date starDate_bonus = startDateBonus.getValue();
		Date endDate_bonus = endDateBonus.getValue();
		String description_bonus = descriptionBonus.getValue();
		Byte type_bonus = (byte) typeBonus.getSelectedIndex();
		String formula_bonus = formulaBonus.getValue();
		
		if(null == starDate_bonus || "" == description_bonus || "" == formula_bonus){
			WarningDialog warning = new WarningDialog("Warning", "Faltan campos por rellenar");
			warning.show();
			warning.center();
		}else{
			if(idBonus.getText() != ""){
				Integer id_bonus = Integer.parseInt(idBonus.getText());
				modifyBonus(id_bonus, starDate_bonus, endDate_bonus, description_bonus, type_bonus, formula_bonus);
			}else{
				Integer nextId = getLastBonusesId() + 1;
				newBonus(nextId, starDate_bonus, endDate_bonus, description_bonus, type_bonus, formula_bonus);
			}
			
			// UPDATE SSBONUS
			impl.setEmployeeSSBonuses(contractId, ssBonuses, new AsyncCallback<List<SSBonusData>>() {
				
				@Override
				public void onSuccess(List<SSBonusData> result) {
					ssBonuses = result;
					success.accept(result);
//					initializePage();
//					hide();
				}

				@Override
				public void onFailure(Throwable caught) {
					
				}
			});

		}
	}
	

// ----------------------------------------------------------------- UiHandlers ----------------------------------------------------------
	
	@UiHandler("listBonusesTable")
	public void onBonusesTableClick(ClickEvent event) {
		event.preventDefault();
		
		int row = listBonusesTable.getCellForEvent(event).getRowIndex();
		
		Label idLabel = (Label) listBonusesTable.getWidget(row, 0);
		String idString = idLabel.getText();
		Integer id = Integer.parseInt(idString);
		
		boolean canEdit = enableEditable(id);
		
		if(canEdit){
			cleanSelected();
			setSelected(row);
			listBonusesPanel.addStyleName(style.hide());
			ssBonusDataTable.removeStyleName(style.hide());
		}else {
			cleanSelected();
			initializePage();
		}
	}
	
	private void setSelected(int row) {
		//Set Selected
		this.listBonusesTable.getWidget(row, 6).addStyleName(style.selectedBG());
		this.listBonusesTable.getRowFormatter().addStyleName(row, style.selected());
	}

	private void cleanSelected() {
		//Set Unselected
		int rows = this.listBonusesTable.getRowCount();
		for(int itRow = 0; itRow < rows; itRow++){
			this.listBonusesTable.getWidget(itRow, 6).removeStyleName(style.selectedBG());
			this.listBonusesTable.getRowFormatter().removeStyleName(itRow, style.selected());
		}
	}


	@UiHandler("endDateBonus")
	void onEndDateBonusChangeValue(ValueChangeEvent<Date> event) {
		if(startDateBonus.getValue() != null)
			if(endDateBonus.getValue().before(startDateBonus.getValue()))
				endDateBonus.setValue(null);
	}
	
	@UiHandler("typeBonus")
	void onTypeBonusChangeValue(ChangeEvent event) {
		int selectedItemIndex = typeBonus.getSelectedIndex();
		switch (selectedItemIndex) {
		case 1:
			hideChecksPanel();
			this.formulaBonus.setEnabled(false);
			this.formulaBonus.setValue("");
			break;
		case 2:
			hideChecksPanel();
			this.formulaBonus.setEnabled(false);
			this.formulaBonus.setValue("");
			break;
		case 3:
			hideAmountPanel();
			clearSelectedChecks();
			this.formulaBonus.setEnabled(false);
			this.formulaBonus.setValue("");
			break;
		default:
			hideAllPanels();
			this.formulaBonus.setEnabled(false);
			this.formulaBonus.setValue("");
		}
	}
	
	@UiHandler("formulaBonus")
	void onFormulaBonusChangeValue(ChangeEvent event) {
		if("" == formulaBonus.getValue())
			this.formulaBonus.setEnabled(false);
	}
	
	@UiHandler("applyChecks")
	void onApplyChecksButtonClick(ClickEvent event) {
		if("" == percentBonus.getValue()){
			WarningDialog warning = new WarningDialog("Warning", "Faltan el porcentaje por rellenar");
			warning.show();
			warning.center();
		}else{
			ArrayList<String> listCheckTrue = new ArrayList<>();
			
			if(checkCommonC.getValue() == true)
				listCheckTrue.add("CGC_E");
			if(checkAccidentC.getValue() == true)
				listCheckTrue.add("(IT_E + IMS_E)");
			if(checkUnemploymentC.getValue() == true)
				listCheckTrue.add("DESMPL_E");
			if(checkFogasaC.getValue() == true)
				listCheckTrue.add("FOGASA_E");
			if(checkFormationC.getValue() == true)
				listCheckTrue.add("FP_E");
			
			if(!listCheckTrue.isEmpty()){
				String result = "(";
				for(int i=0; i<listCheckTrue.size()-1; i++)
					result += listCheckTrue.get(i) + " + ";
				
				result += listCheckTrue.get(listCheckTrue.size()-1) + ") * (" + percentBonus.getValue() + " / 100)";
				this.formulaBonus.setValue(result);
			}
			this.formulaBonus.setEnabled(true);
		}
	}

	@UiHandler("applyAmount")
	void onApplyAmountButtonClick(ClickEvent event) {
		if("" != amountBonus.getValue()){
			int type = typeBonus.getSelectedIndex();
			if(type == 1){
				this.formulaBonus.setValue(amountBonus.getValue());
			}else if(type == 2){
				this.formulaBonus.setValue("("+amountBonus.getValue()+" / DIAS_MES) * DIAS_NOMINA");
			}
		}
		this.formulaBonus.setEnabled(true);
		this.amountBonus.setValue("");
	}
	
	@UiHandler("addBonusesButton")
	void onListBonusesButtonClick(ClickEvent event) {
		hideTable();
	}
	
//	@UiHandler("saveButton")
//	void onSaveButtonClick(ClickEvent event) {
//		Date starDate_bonus = startDateBonus.getValue();
//		Date endDate_bonus = endDateBonus.getValue();
//		String description_bonus = descriptionBonus.getValue();
//		Byte type_bonus = (byte) typeBonus.getSelectedIndex();
//		String formula_bonus = formulaBonus.getValue();
//		
//		if(null == starDate_bonus || "" == description_bonus || "" == formula_bonus){
//			WarningDialog warning = new WarningDialog("Warning", "Faltan campos por rellenar");
//			warning.show();
//			warning.center();
//		}else{
//			if(idBonus.getText() != ""){
//				Integer id_bonus = Integer.parseInt(idBonus.getText());
//				modifyBonus(id_bonus, starDate_bonus, endDate_bonus, description_bonus, type_bonus, formula_bonus);
//			}else{
//				Integer nextId = getLastBonusesId() + 1;
//				newBonus(nextId, starDate_bonus, endDate_bonus, description_bonus, type_bonus, formula_bonus);
//			}
//			
//			// UPDATE SSBONUS
//			impl.setEmployeeSSBonuses(contractId, ssBonuses, new AsyncCallback<List<SSBonusData>>() {
//				
//				@Override
//				public void onSuccess(List<SSBonusData> result) {
//					ssBonuses = result;
//					initializePage();
//				}
//
//				@Override
//				public void onFailure(Throwable caught) {
//					
//				}
//			});
//
//		}	
//	}
	
	@SuppressWarnings("deprecation")
	private void initializePage() {
		cleanPage();
		
		//DOCUMENT
		List<String> ssBonusConcepts = getBonusConceptsDescription();
		List<String> ssBonusConceptsSuggest = new ArrayList<String>();
		for(String ssBonusConcept : ssBonusConcepts)
			ssBonusConceptsSuggest.add(ssBonusConcept+"");
		MultiWordSuggestOracle oracleBonusConcepts = (MultiWordSuggestOracle) this.descriptionBonus.getSuggestOracle();
		oracleBonusConcepts.addAll(ssBonusConceptsSuggest);
		this.descriptionBonus.setAutoSelectEnabled(true);
		
		TextBox txt = (TextBox) this.descriptionBonus.getTextBox();
		txt.setMaxLength(60);
		
		initializeListBox();
		hideAllPanels();
		showListBonuses();
	}

	private void cleanPage() {
		this.idBonus.setText("");
		this.startDateBonus.setValue(null);
		this.endDateBonus.setValue(null);
		this.descriptionBonus.setText("");
		this.typeBonus.clear();
		this.formulaBonus.setText("");
		this.formulaBonus.setEnabled(false);
	}

	private void initializeListBox() {
		typeBonus.addItem("Otros");
		typeBonus.addItem("Importe fijo");
		typeBonus.addItem("Importe / d" + String.valueOf("\u00ED") + "as n" + String.valueOf("\u00F3") + "mina");
		typeBonus.addItem("Porcentaje sobre cuotas");
	}
	
	private void hideChecksPanel() {
		this.checksPanel.addStyleName(style.hide());
		this.amountPanel.removeStyleName(style.hide());
		this.listBonusesPanel.addStyleName(style.hide());
	}

	private void hideAmountPanel() {
		this.checksPanel.removeStyleName(style.hide());
		this.amountPanel.addStyleName(style.hide());
		this.listBonusesPanel.addStyleName(style.hide());
	}
	
	private void hideAllPanels() {
		this.checksPanel.addStyleName(style.hide());
		this.amountPanel.addStyleName(style.hide());
	}
	
	private void clearListBonuses() {
		this.listBonusesTable.clear();
	}
	
	private void clearSelectedChecks() {
		checkCommonC.setValue(false);
		checkAccidentC.setValue(false);
		checkUnemploymentC.setValue(false);
		checkFogasaC.setValue(false);
		checkFormationC.setValue(false);
		
		this.percentBonus.setValue("");
	}
	
	private void showListBonuses() {
		hideAllPanels();
		clearListBonuses();
		if(this.formulaBonus.getValue() == "")
			this.typeBonus.setSelectedIndex(0);
		this.listBonusesPanel.removeStyleName(style.hide());
		
		this.listBonusesTable.resize(getBonuses().size()+1, 8);
		
		//Cabecera
		this.listBonusesTable.setWidget(0, 0, new Label("Id"));
		this.listBonusesTable.getWidget(0, 0).addStyleName(style.hide());
		this.listBonusesTable.setWidget(0, 1, new Label(""));
		this.listBonusesTable.setWidget(0, 2, new Label("Fecha Incio"));
		this.listBonusesTable.getWidget(0, 2).addStyleName(style.dateStyle());
		this.listBonusesTable.setWidget(0, 3, new Label("Fecha Fin"));
		this.listBonusesTable.getWidget(0, 3).addStyleName(style.dateStyle());
		this.listBonusesTable.setWidget(0, 4, new Label("Descripci"+String.valueOf("\u00F3")+"n"));
		this.listBonusesTable.getWidget(0, 4).addStyleName(style.descriptionStyle());
		this.listBonusesTable.setWidget(0, 5, new Label("Tipo"));
		this.listBonusesTable.setWidget(0, 6, new Label("F"+String.valueOf("\u00F3")+"rmula"));
		this.listBonusesTable.setWidget(0, 7, new Label(""));
		
		for(int i = 0; i<8; i++)
			this.listBonusesTable.getWidget(0, i).addStyleName(style.bold());
		
		int row = 1;
		
		for(SSBonusData bonus : getBonuses()){
			listBonusesTable.setWidget(row, 0, new Label(bonus.getId().toString()));
			listBonusesTable.getWidget(row, 0).addStyleName(style.hide());
			Label system = new Label();
			if(bonus.isSystem())
				system.setStyleName("aon-icon-rowSelector-S aon-editDataTable-button");
			else
				system.setStyleName("aon-icon-rowSelector aon-editDataTable-button");
			listBonusesTable.setWidget(row, 1, system);
			listBonusesTable.setWidget(row, 2, new Label((null == bonus.getStartDate()) ? "" : parseDate(bonus.getStartDate())));
			listBonusesTable.setWidget(row, 3, new Label((null == bonus.getEndDate()) ? "" : parseDate(bonus.getEndDate())));
			listBonusesTable.setWidget(row, 4, new Label(bonus.getDescription().toString()));
			listBonusesTable.setWidget(row, 5, new Label((null == bonus.getType()) ? "" : bonus.getType().toString()));
			TextBox formula = new TextBox();
			formula.setValue(bonus.getFormula().toString());
			formula.setMaxLength(500);
			formula.addStyleName(style.formulaStyle());
			formula.setEnabled(false);
			formula.addStyleName(style.unSelected());
			listBonusesTable.setWidget(row, 6, formula);
			
			Image deleteImage = new Image();
			deleteImage.setStyleName("aon-editDataTable-button aon-icon-delete");
			deleteImage.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					DeleteDialog dialog = new DeleteDialog("Eliminar Bonificaci"+String.valueOf("\u00F3")+"n",
							String.valueOf("\u00BF")+"Est"+String.valueOf("\u00E1")+" seguro que desea eliminar la bonificaci"+String.valueOf("\u00F3")+"n?") {
						
						@Override
						protected void onAccept() {
							cleanPage();
							deleteBonus(bonus.getId());
							
							// UPDATE SSBONUS
							impl.setEmployeeSSBonuses(contractId, ssBonuses, new AsyncCallback<List<SSBonusData>>() {
								
								@Override
								public void onSuccess(List<SSBonusData> result) {
									ssBonuses = result;
									showTable();
								}

								@Override
								public void onFailure(Throwable caught) {
									
								}
							});
							
						}
					};
					dialog.show();
					dialog.center();
				}	
			});
			
			listBonusesTable.setWidget(row, 7, deleteImage);
			
			row++;
		}
		cleanSelected();
	}
	
	@SuppressWarnings("deprecation")
	private String parseDate(Date date) {
		String dateStr = "";
		
		String year = (date.getYear()+1900)+"-";
		
		Integer month = date.getMonth()+1;
		String monthStr = "";
		if(month < 10)
			monthStr = "0"+month+"-";
		else
			monthStr = month+"-";
		
		Integer day = date.getDate();
		String dayStr = "";
		if(day < 10)
			dayStr = "0"+day;
		else
			dayStr = day+"";
		
		dateStr = year+monthStr+dayStr;
		
		return dateStr;
	}


	private boolean enableEditable(Integer id){
		SSBonusData bonus = getBonus(id);
//		Window.alert("Id : " + id + ", Bonus : " + bonus.isSystem());
		if(bonus.isSystem()){
			cleanPage();
			WarningDialog warning = new WarningDialog("Aviso", "No se puede modificar una boificaci" + String.valueOf("\u00F3") + "n del sistema.");
			warning.center();
			warning.show();
			return false;
		}else{
//			Window.alert("Id : " + id + ", startDate : " + bonus.getStartDate() + ", endDate : " + bonus.getEndDate() + ", description : " + bonus.getDescription());
			idBonus.setValue(bonus.getId().toString());
			startDateBonus.setValue(bonus.getStartDate());
			endDateBonus.setValue(bonus.getEndDate());
			descriptionBonus.setValue(bonus.getDescription());
			typeBonus.setSelectedIndex((null == bonus.getType()) ? 0 : bonus.getType());
			formulaBonus.setValue(bonus.getFormula());
			formulaBonus.setEnabled(true);
			return true;
		}
	}
	
	// ---------------------------------------------------------------------------------------------------------------------------------------------
	// 														PRIVATE METHODS
	// ---------------------------------------------------------------------------------------------------------------------------------------------
	
	public List<SSBonusData> getBonuses(){
		return this.ssBonuses;
	}
	
	public SSBonusData getBonus(Integer id){
		for(SSBonusData bonus : this.ssBonuses){
			if(bonus.getId() == id)
				return bonus;
		}
		return null;
	}
	
	public void newBonus(Integer id, Date startDate, Date endDate, String description, Byte type, String expression){
//		Window.alert("ID : " + id + ", startDate : " + startDate + ", endDate : " + endDate + 
//					", description : " + description + ", type : " + type + ", expression : " + expression);
		SSBonusData newBonus = new SSBonusData(id, false, startDate, endDate, description, type, expression);
		this.ssBonuses.add(newBonus);
	}
	
	public void modifyBonus(Integer id, Date startDate, Date endDate, String description, Byte type, String expression) {
//		Window.alert("ID : " + id + ", startDate : " + startDate + ", endDate : " + endDate + 
//				", description : " + description + ", type : " + type + ", expression : " + expression);
		for(SSBonusData bonus : ssBonuses){
			if(id == bonus.getId()){
				bonus.setStartDate(startDate);
				bonus.setEndDate(endDate);
				bonus.setDescription(description);
				bonus.setType(type);
				bonus.setFormula(expression);
			}
		}
	}
	
	public void deleteBonus(Integer id_bonus) {
		SSBonusData bonus = getBonus(id_bonus);
		if(null != bonus)
			ssBonuses.remove(bonus);
	}

	public int getLastBonusesId() {
		int index = -1;
		for(SSBonusData bonus : ssBonuses){
			if(index < bonus.getId())
				index = bonus.getId();
		}
		return index;
	}
	
	public List<String> getBonusConceptsDescription(){
		List<String> bonusConceptsDescription = new ArrayList<String>();
		
		for(SSBonusData bonusConcept : this.bonusConcepts) {
			bonusConceptsDescription.add(bonusConcept.getDescription());
		}
	
		return bonusConceptsDescription;
	}

	
}

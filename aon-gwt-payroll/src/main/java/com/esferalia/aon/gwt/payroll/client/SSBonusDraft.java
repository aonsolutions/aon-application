package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
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
	
	
	//@UiField
	//HTMLPanel listBonusesPanel;
	
	@UiField
	Grid listBonusesTable;

	//BUTTONS ACCEPT AND CANCEL
	
	@UiField
	Button acceptButton;
	
	@UiField
	Button cancelButton;
	
	@UiField
	FormPanel idcFormPanel;
	
	@UiField
	FileUpload idcFileUpload;
	
	@UiField
	Hidden idcUserNameHidden;
	
	@UiField
	Hidden idcDomainNameHidden;
// ------------------------------------------------------------ VARIABLES DE LA CLASE ----------------------------------------------------
		
//	private SSBonusDraftObject ssBonusDraftObject;
	private Integer contractId;
	private List<SSBonusData> ssBonuses;
	
	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("dd/MM/yyyy");

	
// ---------------------------------------------------------------- CONSTRUCTOR ----------------------------------------------------------
	
	public SSBonusDraft(Integer contractId) {
		
		setCaption("Bonificaciones...");
		
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
		this.contractId = contractId;

		impl.getEmployeeSSBonuses(this.contractId, new AsyncCallback<List<SSBonusData>>() {
			
			@Override
			public void onSuccess(List<SSBonusData> result) {
				ssBonuses = result;
				showTable();
			}

			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
		
		this.idcUserNameHidden.setValue(Wnd.getCurrentUser());
		this.idcDomainNameHidden.setValue(Wnd.getCurrentDomainNameURL());

	}
	
	private void showTable() {
		cleanSelected();
		showListBonuses();
	}
	
	
	private void onAccept(Consumer<List<SSBonusData>> success, Consumer<Throwable> failure) {
//		impl.setEmployeeSSBonuses(contractId, ssBonuses, new AsyncCallback<List<SSBonusData>>() {
//			
//			@Override
//			public void onSuccess(List<SSBonusData> result) {
//				ssBonuses = result;
//				success.accept(result);
//			}
//
//			@Override
//			public void onFailure(Throwable caught) {
//				
//			}
//		});
	}
	

// ----------------------------------------------------------------- UiHandlers ----------------------------------------------------------
	
	@UiHandler("idcButton")
	void onClickIdcButton(ClickEvent e ) {
		idcFileUpload.click();
	}

	@UiHandler("idcFormPanel") 
	void onIdcSubmitComplete(SubmitCompleteEvent e) {
	}

	private void cleanSelected() {
		//Set Unselected
		int rows = this.listBonusesTable.getRowCount();
		for(int itRow = 0; itRow < rows; itRow++){
			this.listBonusesTable.getRowFormatter().removeStyleName(itRow, style.selected());
		}
	}



	private void clearListBonuses() {
		this.listBonusesTable.clear();
	}
	

	private void showListBonuses() {
		clearListBonuses();
		
		this.listBonusesTable.resize(getBonuses().size() + 1, this.listBonusesTable.getColumnCount());
		
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
		this.listBonusesTable.setWidget(0, 5, new Label(""));
		
		for(int i = 0; i < this.listBonusesTable.getColumnCount(); i++)
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
			listBonusesTable.setWidget(row, 2, new Label((null == bonus.getStartDate()) ? "" : DATE_FORMAT.format(bonus.getStartDate())));
			listBonusesTable.setWidget(row, 3, new Label((null == bonus.getEndDate()) ? "" : DATE_FORMAT.format(bonus.getEndDate())));
			listBonusesTable.setWidget(row, 4, new Label(bonus.getDescription().toString()));
						
			row++;
		}
		cleanSelected();
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
	

	
}

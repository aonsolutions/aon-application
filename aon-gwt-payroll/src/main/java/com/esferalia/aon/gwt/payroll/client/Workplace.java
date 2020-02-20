package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class Workplace extends ResizeComposite{
	
	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static WorkplaceUiBinder uiBinder = GWT.create(WorkplaceUiBinder.class);

	interface WorkplaceUiBinder extends UiBinder<Widget, Workplace> {
	}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String warningColor();
	}

	// TABLA DATOS CENTRO DE TRABAJO
	
	@UiField
	TableElement generalDataTable;

	@UiField
	TextBox workplaceDescription;
	
	@UiField
	HorizontalPanel workplaceAddressPanel;
	
	@UiField
	ListBox workplaceEconomicConcert;

	// TABLA DATOS CENTRO DE TRABAJO (LABORAL)
	
	@UiField
	HorizontalPanel workplaceCalendarPanel;

	@UiField
	ListBox workpalceAgreement;

	@UiField
	HorizontalPanel workplaceActivityPanel;

	// ------------------------------------------------ CONSTRUCTOR ------------------------------------------------------

	public Workplace() {
		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		initializeView();
	}

	// ------------------------------------------------- UiHandlers ------------------------------------------------------
	
	@UiHandler("workplaceDescription")
	void onWorkplaceDescriptionChangeValue(ChangeEvent event) {
		onWorkplaceDescriptionChange();
	}

	@UiHandler("workplaceEconomicConcert")
	void onWorkplaceEconomicConcertChangeValue(ChangeEvent event) {
		onWorkplaceEconomicConcertChange();
	}
	
	@UiHandler("workpalceAgreement")
	void onContractAgreementChangeValue(ChangeEvent event) {
		onWorkplaceAgreementChange();
	}
	
	// ------------------------------------------------------------------------
	//							Abstraact Methods
	// ------------------------------------------------------------------------
	
	// TABLA DATOS CENTRO DE TRABAJO
	
	public abstract void onWorkplaceDescriptionChange();
	public abstract void onWorkplaceEconomicConcertChange();
	
	// TABLA DATOS CENTRO DE TRABAJO (LABORAL)
	
	public abstract void onWorkplaceAgreementChange();

	// ------------------------------------------------------ METODOS DE LA CLASE --------------------------------------------------

	private void initializeView() {
		resetElements();
		initializeListBox();
	}

	private void resetElements() {
		// Clear general elements
		this.workplaceDescription.setValue("");
		this.workplaceAddressPanel.clear();
		this.workplaceEconomicConcert.clear();

		// Clear payroll elements
		this.workplaceCalendarPanel.clear();
		this.workpalceAgreement.clear();
		this.workplaceActivityPanel.clear();	
	}

	private void initializeListBox() {
		//CONCIERTO ECONOMICO
		this.workplaceEconomicConcert.addItem("-", "-1");
		this.workplaceEconomicConcert.addItem(String.valueOf("\u00C1")+"lava", "0");
		this.workplaceEconomicConcert.addItem("Bizkaia", "1");
		this.workplaceEconomicConcert.addItem("Gipuzkoa", "2");
		this.workplaceEconomicConcert.addItem("Navarra", "3");
		this.workplaceEconomicConcert.addItem("Territorio Com"+ String.valueOf("\u00FA") +"n", "4");
	}

}

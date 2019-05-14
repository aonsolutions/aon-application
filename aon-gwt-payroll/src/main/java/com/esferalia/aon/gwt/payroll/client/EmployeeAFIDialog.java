package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ContractTypeRecord;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeAFIDialog extends CustomDialog {
	
	interface Binder extends UiBinder<Widget, EmployeeAFIDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		
	}
	
	@UiField
	CheckBox startContractCkBox;
	
	@UiField
	DateBoxEx startDate;
	
	@UiField
	CheckBox endContractCkBox;
	
	@UiField
	DateBoxEx endDate;
	
	@UiField
	DateBoxEx newDate;
	
	@UiField
	CheckBox changeContractCkBox;
	
	@UiField
	ListBox tc2;
	
	@UiField
	CheckBox quoteContractCkBox;
	
	@UiField
	ListBox quoteGroup;
	
	@UiField
	CheckBox ocupationContractCkBox;
	
	@UiField
	ListBox ocupation;
	
	@UiField
	CheckBox generationAFICkBox;
	
	@UiField
	Button acceptButton;
	
	@UiField
	Button cancelButton;
	
	private ContractType contractType;
	private Date payrollDate;

	public EmployeeAFIDialog() {
		setCaption("Generador fichero AFI");
		
		setWidget(binder.createAndBindUi(this));
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onAccept();
			}
		});		
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				
			}
		});
		
		//EnsureDebugID para TEST
		this.acceptButton.ensureDebugId("input_accept");
	}

	public EmployeeAFIDialog(Date startDate, Date endDate, int tc2Idx, int quoteGroupIdx, int ocupationIdx, Date payrollDate) {
		setCaption("Generador fichero AFI");
		
		setWidget(binder.createAndBindUi(this));
		
		this.contractType = new ContractType();
		
		initListBox();
		
		this.startDate.setValue(startDate);
		this.startDate.setEnabled(false);
		this.endDate.setValue(endDate);
		this.endDate.setEnabled(false);
		this.tc2.setSelectedIndex(tc2Idx);
		this.tc2.setEnabled(false);
		this.quoteGroup.setSelectedIndex(quoteGroupIdx);
		this.quoteGroup.setEnabled(false);
		this.ocupation.setSelectedIndex(ocupationIdx);
		this.ocupation.setEnabled(false);
		
		this.payrollDate = payrollDate;
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onAccept();
			}
		});		
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				
			}
		});
		
		//EnsureDebugID para TEST
		this.acceptButton.ensureDebugId("input_accept");
	}
	
	@UiHandler("startContractCkBox")
	void onStartDateValueChange(ValueChangeEvent<Boolean> event) {
		this.startDate.setEnabled(event.getValue());
	}
	
	@UiHandler("endContractCkBox")
	void onEntDateValueChange(ValueChangeEvent<Boolean> event) {
		this.endDate.setEnabled(event.getValue());
	}
	
	@UiHandler("newDate")
	void onNewDateValueChange(ValueChangeEvent<Date> event) {
		if(null != payrollDate) {
			if(payrollDate.after(event.getValue())) {
				this.newDate.setValue(null);
			}
		}
	}
	
	@UiHandler("changeContractCkBox")
	void onTc2ValueChange(ValueChangeEvent<Boolean> event) {
		this.tc2.setEnabled(event.getValue());
	}
	
	@UiHandler("quoteContractCkBox")
	void onQuoteGroupValueChange(ValueChangeEvent<Boolean> event) {
		this.quoteGroup.setEnabled(event.getValue());
	}
	
	@UiHandler("ocupationContractCkBox")
	void onOcupationValueChange(ValueChangeEvent<Boolean> event) {
		this.ocupation.setEnabled(event.getValue());
	}

	private void initListBox() {
		// TC2
		this.tc2.addItem("-");
		for (Entry<Integer, ContractTypeRecord> entry : contractType.getContractTypes().entrySet())
			this.tc2.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription());
		
		this.quoteGroup.addItem("-");
		this.quoteGroup.addItem("01. Alta direcci" + String.valueOf("\u00F3") + "n y personal no incluido en el E.T.");
		this.quoteGroup.addItem("02. Ingenieros t" + String.valueOf("\u00E9") + "cnicos, peritos y ayudantes titulados");
		this.quoteGroup.addItem("03. Jefes administrativos y de taller");
		this.quoteGroup.addItem("04. Ayudantes no titulados");
		this.quoteGroup.addItem("05. Oficiales administrativos");
		this.quoteGroup.addItem("06. Subalternos");
		this.quoteGroup.addItem("07. Axiliares administrativos");
		this.quoteGroup.addItem("08. Oficiales de primera y segunda");
		this.quoteGroup.addItem("09. Oficiales de tercera y especialista");
		this.quoteGroup.addItem("10. Peones");
		this.quoteGroup.addItem("11. Trabajadores menos de dieciocho a" + String.valueOf("\u00F1") + "os");

		// OCUPACION
		this.ocupation.addItem("-");
		this.ocupation.addItem("a. Personal en trabajos exclusivos de oficina");
		this.ocupation.addItem("b. Tipo de cotizaci" + String.valueOf("\u00F3") + "n para todos los trabajadores que deban desplazarse habitalmente");
		this.ocupation.addItem("d. Personal de oficios en instalaciones y reparaciones en edificios, obras y trabajos de construcci" + String.valueOf("\u00F3") + "n en general");
		this.ocupation.addItem("e. Conductores de veh" + String.valueOf("\u00ED") + "culo autom" + String.valueOf("\u00F3") + "vil de transporte de pasajeros en general (taxis, autom" + String.valueOf("\u00F3") + "viles, autobuses, etc)");
		this.ocupation.addItem("f. Conductores de veh" + String.valueOf("\u00ED") + "culo autom" + String.valueOf("\u00F3") + "vil de transporte de mercanc" + String.valueOf("\u00ED") + "as que tengan una capacidad de carga " + String.valueOf("\u00FA") + "til superior a 3,5 Tm.");
		this.ocupation.addItem("g. Personal de limpieza en general. Limpieza de edificios y de todo tipo de establecimientos. Limpieza de calles");
		this.ocupation.addItem("h. Vigilantes, guardas, guardas jurados y personal de seguridad");
	}

	protected abstract void onAccept();
	
	public boolean isStartContract() {
		return startContractCkBox.isChecked();
	}
	
	public boolean isEndContract() {
		return endContractCkBox.isChecked();
	}
	
	public boolean isChangeContract() {
		return changeContractCkBox.isChecked();
	}
	
	public boolean isQuoteContract() {
		return quoteContractCkBox.isChecked();
	}
	
	public boolean isOcupationContract() {
		return ocupationContractCkBox.isChecked();
	}
	
	public boolean isGenerationAFI() {
		return generationAFICkBox.isChecked();
	}
	
	public Date getStartDate() {
		return this.startDate.getValue();
	}
	
	public Date getEndDate() {
		return this.endDate.getValue();
	}
	
	public Date getNewDate() {
		return this.newDate.getValue();
	}
	
	public Integer getTC2Idx() {
		return this.tc2.getSelectedIndex();
	}
	
	public Integer getQuoteGroupdx() {
		return this.quoteGroup.getSelectedIndex();
	}
	
	public Integer getOcupationIdx() {
		return this.ocupation.getSelectedIndex();
	}

}

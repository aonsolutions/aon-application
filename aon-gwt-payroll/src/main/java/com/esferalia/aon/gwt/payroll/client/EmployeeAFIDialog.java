package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ContractTypeRecord;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeAFIDialog extends CustomDialog {
	
	interface Binder extends UiBinder<Widget, EmployeeAFIDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String hidden();
	}
	
	@UiField
	Label startContractLabel;
	
	@UiField
	CheckBox startContractCkBox;
	
	@UiField
	Label endContractLabel;
	
	@UiField
	CheckBox endContractCkBox;
	
	@UiField
	DateBoxEx newDate;
	
	@UiField
	ListBox tc2;
	
	@UiField
	ListBox quoteGroup;
	
	@UiField
	ListBox ocupation;
	
	@UiField
	CheckBox generationAFICkBox;
	
	@UiField
	Button acceptButton;
	
	@UiField
	Button cancelButton;
	
	private ContractType contractType;
	
	private Date contractStartDate;
	private Date payrollDate;
	
	private Date currentDate;
	private Date currentDateP3;
	private Date currentStartDateM60;
	private Date currentEndDateM60;
	private Date currentEndDateP3;
	
	private Integer tc2IdxOriginal;
	private Integer quoteGroupIdxOriginal;
	private Integer ocupationIdxOriginal;

	public EmployeeAFIDialog() {
		setCaption("Datos AFI");
		
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
		setCaption("Datos AFI");
		
		setWidget(binder.createAndBindUi(this));
		
		this.contractType = new ContractType();
		
		initListBox();
		
		tc2IdxOriginal = tc2Idx;
		quoteGroupIdxOriginal = quoteGroupIdx;
		ocupationIdxOriginal = ocupationIdx;
		
		contractStartDate = new Date();
		contractStartDate = DateUtils.copyDateOnly(startDate);
		DateUtils.resetTime(contractStartDate);
		
		currentDate = new Date();
		DateUtils.resetTime(currentDate);
		currentDateP3 = DateUtils.copyDateOnly(currentDate);
		currentDateP3 = DateUtils.addDays2Date(currentDateP3, 3);
		
		if(null != startDate) {
			currentStartDateM60 = DateUtils.copyDateOnly(startDate);
			currentStartDateM60 = DateUtils.addDays2Date(currentStartDateM60, -60);
		}
		
		if(null != endDate) {
			currentEndDateM60 = DateUtils.copyDateOnly(endDate);
			currentEndDateM60 = DateUtils.addDays2Date(currentEndDateM60, -60);
			currentEndDateP3 = DateUtils.copyDateOnly(endDate);
			currentEndDateP3 = DateUtils.addDays2Date(currentEndDateP3, 3);
		}
		
		if(null == startDate) {
			this.startContractCkBox.addStyleName(style.hidden());
			this.startContractLabel.addStyleName(style.hidden());
		}else if( (currentDate.before(startDate) || currentDate.equals(startDate)) &&
			(currentDate.after(currentStartDateM60) || currentDate.equals(currentStartDateM60)) ) {
			
			this.startContractCkBox.removeStyleName(style.hidden());
			this.startContractLabel.removeStyleName(style.hidden());
			
		}else {
			this.startContractCkBox.addStyleName(style.hidden());
			this.startContractLabel.addStyleName(style.hidden());
		}
		
		
		if(null == endDate) {
			this.endContractCkBox.addStyleName(style.hidden());
			this.endContractLabel.addStyleName(style.hidden());
		} else if( (currentDate.before(currentEndDateP3) || currentDate.equals(currentEndDateP3)) &&
			(currentDate.after(currentEndDateM60) || currentDate.equals(currentEndDateM60)) ) {
			
			this.endContractCkBox.removeStyleName(style.hidden());
			this.endContractLabel.removeStyleName(style.hidden());
			
		}else {
			this.endContractCkBox.addStyleName(style.hidden());
			this.endContractLabel.addStyleName(style.hidden());
		}
		
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
				if(hasChange()) {
					hide();
					onAccept();
				}	
			}
		});		
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		this.acceptButton.setEnabled(false);
		this.generationAFICkBox.setEnabled(false);
		
		//EnsureDebugID para TEST
		this.acceptButton.ensureDebugId("input_accept");
	}
	
	@UiHandler("startContractCkBox")
	void onStartDateClick(ValueChangeEvent<Boolean> event) {
		if(hasChange()) {
			acceptButton.setEnabled(true);
			generationAFICkBox.setEnabled(true);
			generationAFICkBox.setChecked(true);
			this.newDate.setValue(contractStartDate, true);
		}else {
			acceptButton.setEnabled(false);
			generationAFICkBox.setEnabled(false);
			generationAFICkBox.setChecked(false);
			this.newDate.setValue(null, true);
		}
	}
	
	@UiHandler("endContractCkBox")
	void onEndDateClick(ValueChangeEvent<Boolean> event) {
		if(hasChange()) {
			acceptButton.setEnabled(true);
			generationAFICkBox.setEnabled(true);
			generationAFICkBox.setChecked(true);
		}else {
			acceptButton.setEnabled(false);
			generationAFICkBox.setEnabled(false);
			generationAFICkBox.setChecked(false);
		}
	}
	
	@UiHandler("newDate")
	void onNewDateValueChange(ValueChangeEvent<Date> event) {
		Date eventDate = event.getValue();
		
		if(null != eventDate)
			DateUtils.resetTime(eventDate);
		
		if(null != payrollDate) {
			if(payrollDate.after(eventDate) || currentDate.after(eventDate)) {
				this.newDate.setValue(null);
				this.tc2.setEnabled(false);
				this.quoteGroup.setEnabled(false);
				this.ocupation.setEnabled(false);
			}else {
				this.tc2.setEnabled(true);
				this.quoteGroup.setEnabled(true);
				this.ocupation.setEnabled(true);
			}
		}else {
			this.tc2.setEnabled(true);
			this.quoteGroup.setEnabled(true);
			this.ocupation.setEnabled(true);
		}
		
		if(null == eventDate) {
			generationAFICkBox.setEnabled(false);
			generationAFICkBox.setChecked(false);
			this.tc2.setEnabled(false);
			this.quoteGroup.setEnabled(false);
			this.ocupation.setEnabled(false);
		}else if( (currentDate.before(eventDate) || currentDate.equals(eventDate)) &&
			(currentDateP3.after(eventDate) || currentDateP3.equals(eventDate)) ) {

			generationAFICkBox.setEnabled(true);
		
		}else {
			generationAFICkBox.setEnabled(false);
		}
		
	}
	
	@UiHandler("tc2")
	void onTC2Change(ChangeEvent event) {
		if(hasChange()) {
			this.acceptButton.setEnabled(true);
			this.generationAFICkBox.setEnabled(true);
		}else {
			this.acceptButton.setEnabled(false);
			this.generationAFICkBox.setEnabled(false);
			generationAFICkBox.setChecked(false);
		}
	}
	
	@UiHandler("quoteGroup")
	void onQuoteGroupChange(ChangeEvent event) {
		if(hasChange()) {
			this.acceptButton.setEnabled(true);
			this.generationAFICkBox.setEnabled(true);
		}else {
			this.acceptButton.setEnabled(false);
			this.generationAFICkBox.setEnabled(false);
			generationAFICkBox.setChecked(false);
		}
	}
	
	@UiHandler("ocupation")
	void onOcupationChange(ChangeEvent event) {
		if(hasChange()) {
			this.acceptButton.setEnabled(true);
			this.generationAFICkBox.setEnabled(true);
		}else {
			this.acceptButton.setEnabled(false);
			this.generationAFICkBox.setEnabled(false);
			generationAFICkBox.setChecked(false);
		}
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
		return (null == startContractCkBox) ? false : startContractCkBox.isChecked();
	}
	
	public boolean isEndContract() {
		return (null == endContractCkBox) ? false : endContractCkBox.isChecked();
	}
	
	public boolean isChangeContract() {
		if(tc2.getSelectedIndex() != tc2IdxOriginal)
			return true;
		else
			return false;
	}
	
	public boolean isQuoteContract() {
		if(quoteGroup.getSelectedIndex() != quoteGroupIdxOriginal)
			return true;
		else
			return false;
	}
	
	public boolean isOcupationContract() {
		if(ocupation.getSelectedIndex() != ocupationIdxOriginal)
			return true;
		else 
			return false;
	}
	
	public boolean isGenerationAFI() {
		return generationAFICkBox.isChecked();
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
	
	public boolean hasChange() {
		if(isStartContract())
			return true;
		
		if(isEndContract())
			return true;
		
		if(tc2.getSelectedIndex() != tc2IdxOriginal)
			return true;
		
		if(quoteGroup.getSelectedIndex() != quoteGroupIdxOriginal)
			return true;
		
		if(ocupation.getSelectedIndex() != ocupationIdxOriginal)
			return true;
		
		return false;
	}

}

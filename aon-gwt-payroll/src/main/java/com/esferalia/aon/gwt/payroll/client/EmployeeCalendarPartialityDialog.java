package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarPartialityDialog extends CustomDialog {

	// ------------------------------------ UiBinder
	
	interface Binder extends UiBinder<Widget, EmployeeCalendarPartialityDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ------------------------------------ UiFields
		
	@UiField
	TextBox percentBox;
	
	@UiField
	HTMLPanel errorMessage;
	
	@UiField
	DateBoxEx startDateDB;
	
	@UiField
	DateBoxEx endDateDB;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	@UiField
	HTMLPanel reasonPanel;
	
	@UiField
	ListBox reasonBox;
	
	// ------------------------------------ Variables
	
	private Double percent = 1.00;
	private Date contractStartDate;
	private Date contractEndDate;

	// ------------------------------------ Constructor
	
	protected EmployeeCalendarPartialityDialog(String caption, List<Date> selectedDates, Date contractStartDate, Date contractEndDate) {
		setCaption(caption);
		setWidget(binder.createAndBindUi(this));
		this.hideClose();
		errorMessage.getElement().getStyle().setDisplay(Display.NONE);
		acceptButton.setEnabled(false);
		
		reasonPanel.setVisible(isPaidLeave());
		if(isPaidLeave()) {
			reasonBox.setWidth("22rem");
			reasonBox.addItem("Permisos por maternidad/paternidad, adopci\u00f3n o acogimiento de hijos");
			reasonBox.addItem("Permisos por lactancia");
			reasonBox.addItem("Consultas m\u00e9dicas propias o de familiares que requieran acompa\u00f1amiento");
			reasonBox.addItem("Pruebas diagn\u00f3sticas");
			reasonBox.addItem("Cuidado enfermedad grave, accidente, hospitalizaci\u00f3n o intervenci\u00f3n quir\u00fargica de familiares");
			reasonBox.addItem("Cuidado de hijos menores o personas dependientes.");
			reasonBox.addItem("Permisos por fallecimiento familiar.");
			reasonBox.addItem("Causa de fuerza mayor");
			reasonBox.addItem("Matrimonio");
			reasonBox.addItem("Traslado de domicilio (Mudanza)");
			reasonBox.addItem("Cumplimiento de deberes p\u00fablicos: Asistencia a juicios como jurado/testigo. Mesa electoral");
			reasonBox.addItem("Permisos para formaci\u00f3n");
			reasonBox.addItem("Asistencia a ex\u00e1menes.");
			reasonBox.addItem("Vacaciones Pendientes");
			reasonBox.addItem("Cat\u00e1strofes naturales. Restricciones de movilidad");
			reasonBox.addItem("Riesgo clim\u00e1tico");
			reasonBox.addItem("Horas sindicales");
			reasonBox.addItem("Horas sindicales");
			reasonBox.addItem("Asuntos propios o de libre disposici\u00f3n");
			reasonBox.addItem("Otras causas no detalladas");
		}
		
		if(!selectedDates.isEmpty()) {
			selectedDates.sort(null);
			startDateDB.setValue(selectedDates.get(0));
			endDateDB.setValue(selectedDates.get(selectedDates.size() - 1));
		}
		
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		
		startDateDB.getTextBox().addClickHandler(e -> startDateDB.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; "));
		endDateDB.getTextBox().addClickHandler(e -> endDateDB.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; "));
		
		cancelButton.addClickHandler(e -> hide());
		acceptButton.addClickHandler(e -> {
			if(null != startDateDB.getValue())
				onAccept();
			hide();
		});
		
		percentBox.addBlurHandler(e -> {
			try {
				percent = Double.parseDouble(percentBox.getValue());
				errorMessage.getElement().getStyle().setDisplay(Display.NONE);
				acceptButton.setEnabled(true);
				
				if(percent == 0.00)
					percent = 1.00;
				else {
					percent = percent / 100;
				}
			
			} catch (NumberFormatException ex) {
				errorMessage.getElement().getStyle().clearDisplay();
				acceptButton.setEnabled(false);
				percentBox.setValue("");
				percent = 1.00;
			}
		});
		
		showDialog();
	}
	
	private boolean isPaidLeave() {
		return AonStringUtils.equalsIgnoreCase(getCaption(), "PERMISO RETRIBUIDO");
	}
	
	// ------------------------------------ Abstract methods

	protected abstract void onAccept();
	
	// ------------------------------------ UiHandlers
	
	@UiHandler("startDateDB")
	public void onStartDateDBChange(ValueChangeEvent<Date> event) {
		Date date = event.getValue();
		if(null != date && date.before(contractStartDate))
			startDateDB.setValue(contractStartDate);
	}
	
	@UiHandler("endDateDB")
	public void onEndDateDBChange(ValueChangeEvent<Date> event) {
		Date date = event.getValue();
		if(null != date && null != contractEndDate && date.after(contractEndDate))
			endDateDB.setValue(contractEndDate);
	}
	
	// ------------------------------------ Auxiliar methods
	
	public double getPercentValue() {
		Math.abs(percent);
		return this.percent;
	}
	
	public String getPaidLeaveExression() {
		String percentStr = "1.00";
	    try {
	        double value = Double.parseDouble(percentBox.getValue());

	        if (value == 0.00) {
	            percentStr = "1.00";
	        } else {
	            double result = value / 100;

	            // Usamos NumberFormat para formatear con 4 decimales
	            NumberFormat fmt = NumberFormat.getFormat("0.####");
	            percentStr = fmt.format(result);
	            percentStr = percentStr.replace(',', '.');
	        }
	    } catch (Exception e) {
	        percentStr = "1.00";
	    }
	    
		return "/*inherit*/" + reasonBox.getSelectedValue() + "/**/" + percentStr;
	}
	
	public Date getStartDate() {
		return this.startDateDB.getValue();
	}
	
	public void setStartDate(Date date) {
		this.startDateDB.setValue(date);
	}
	
	public Date getEndDate() {
		return this.endDateDB.getValue();
	}
	
	public void setEndDate(Date date) {
		this.endDateDB.setValue(date);
	}
	
	// ------------------------------------ Show dialog
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

}

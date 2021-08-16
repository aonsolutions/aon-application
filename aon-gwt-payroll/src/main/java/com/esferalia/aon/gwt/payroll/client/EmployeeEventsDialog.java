package com.esferalia.aon.gwt.payroll.client;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData.EmployeeEventsVariable;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.TextTransform;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeEventsDialog extends AonCustomDialog {

	// ------------------------------------------------------ UiBiner
	
	interface Binder extends UiBinder<Widget, EmployeeEventsDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ------------------------------------------------------ UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String rowPanel();
	}
	
	@UiField
	HTMLPanel containerHeader;
	
	@UiField
	HTMLPanel containerNewPeriod;
	
	@UiField
	HTMLPanel container;
	
	@UiField
	DeckPanel errorDeckPanel;
	
	@UiField
	Label contractErrorDateL;
	
	@UiField
	HTMLPanel errorMessage;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------------------------ Variables
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private Date contractStartDate;
	private Date contractEndDate;
	
	private ArrayList<EmployeeEventsVariable> employeeEventsVariables;
	
	private Button closeBtnDialog;
	private Button acceptBtnDialog;

	// ------------------------------------------------------ Constructor
	
	public EmployeeEventsDialog(
			String variableName, 
			ArrayList<EmployeeEventsVariable> employeeEventsVariables,
			Date contractStartDate, Date contractEndDate) {
		
		setWidget(binder.createAndBindUi(this));
		
		setCaption(variableName);

		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.employeeEventsVariables = employeeEventsVariables;
		
		initErrorPanel();
		getButtonsPanel();
		
		createHeader();
		createNewEmptyRow();
		sortEmployeeEventsVariablesDesc();
		createEmployeeEventsVariablesTable();
		
	}

	// ------------------------------------------------------ List sorter

	private void sortEmployeeEventsVariablesDesc() {
		/* Sorting in decreasing order*/
		Collections.sort(employeeEventsVariables, Collections.reverseOrder(new Comparator<EmployeeEventsVariable>() {
			@Override
			public int compare(EmployeeEventsVariable o1, EmployeeEventsVariable o2) {
				return o1.getStartDate().compareTo(o2.getStartDate());
			}
		}));
	}
	
	private void sortEmployeeEventsVariablesAsc() {
		employeeEventsVariables.sort(new Comparator<EmployeeEventsVariable>() {
			@Override
			public int compare(EmployeeEventsVariable o1, EmployeeEventsVariable o2) {
				return o1.getStartDate().compareTo(o2.getStartDate());
			}
		});
	}
	
	// ------------------------------------------------------ Table (Header)
	
	private void createHeader() {
		containerHeader.clear();
		
		HTMLPanel rowPanel = new HTMLPanel("");
		rowPanel.addStyleName(style.rowPanel());
		
		Label startDateL = new Label("Fecha Inicio");
		startDateL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		startDateL.getElement().getStyle().setTextTransform(TextTransform.UPPERCASE);
		Label endDateL = new Label("Fecha Fin");
		endDateL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		endDateL.getElement().getStyle().setTextTransform(TextTransform.UPPERCASE);
		Label valueL = new Label("Valor");
		valueL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		valueL.getElement().getStyle().setTextTransform(TextTransform.UPPERCASE);
		Label optionL = new Label("");
		
		rowPanel.add(startDateL);
		rowPanel.add(endDateL);
		rowPanel.add(valueL);
		rowPanel.add(optionL);
		
		containerHeader.add(rowPanel);
	}
	
	private void createNewEmptyRow() {
		containerNewPeriod.clear();
		
		HTMLPanel rowPanel = new HTMLPanel("");
		rowPanel.addStyleName(style.rowPanel());
		
		EmployeeEventsVariable newEmployeeEventsVariable = new EmployeeEventsVariable();
		
		DateBoxEx startDateTB = new DateBoxEx();
		startDateTB.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		startDateTB.getElement().getStyle().setWidth(100, Unit.PCT);
		startDateTB.addValueChangeHandler(e -> {
			newEmployeeEventsVariable.setStartDate(e.getValue());
		});
		
		DateBoxEx endDateTB = new DateBoxEx();
		endDateTB.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		endDateTB.getElement().getStyle().setWidth(100, Unit.PCT);
		endDateTB.addValueChangeHandler(e -> {
			newEmployeeEventsVariable.setEndDate(e.getValue());
		});
		
		DoubleBox valueTB = new DoubleBox();
		valueTB.getElement().getStyle().setWidth(100, Unit.PCT);
		valueTB.addBlurHandler(e -> {
			try {
				Double value = valueTB.getValueOrThrow();
				newEmployeeEventsVariable.setValue(value);
				
				hideErrorPanel();
			} catch (ParseException exception) {
				showNumberError();
			}
		});
		
		AonToolbarSmallButton savePeriod = new AonToolbarSmallButton("Crear tramo", AON.CSS.aonIconSave());
		savePeriod.addClickHandler(e -> {
			if(null == newEmployeeEventsVariable.getValue())
				showNumberError();
			else {
				if(canSavePeriod(newEmployeeEventsVariable)) {
					hideErrorPanel();
					
					createNewEmptyRow();
					sortEmployeeEventsVariablesDesc();
					createEmployeeEventsVariablesTable();
				} else
					showDateError();
			}
		});
		
		rowPanel.add(startDateTB);
		rowPanel.add(endDateTB);
		rowPanel.add(valueTB);
		rowPanel.add(savePeriod);
		
		containerNewPeriod.add(rowPanel);
	}

	// ------------------------------------------------------ Table
	
	private void createEmployeeEventsVariablesTable() {
		container.clear();
		
		this.employeeEventsVariables.forEach(employeeEventsVariable -> {
			
			HTMLPanel rowPanel = new HTMLPanel("");
			rowPanel.addStyleName(style.rowPanel());
			
			DateBoxEx startDateTB = new DateBoxEx();
			startDateTB.getElement().getStyle().setWidth(100, Unit.PCT);
			startDateTB.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			startDateTB.setValue(employeeEventsVariable.getStartDate());
			startDateTB.addValueChangeHandler(e -> {
				if(checkIfStartDateCanBeChange(e.getValue(), employeeEventsVariable)) {
					hideErrorPanel();
					
					sortEmployeeEventsVariablesDesc();
					createEmployeeEventsVariablesTable();
				} else
					showDateError();
			});
			
			DateBoxEx endDateTB = new DateBoxEx();
			endDateTB.getElement().getStyle().setWidth(100, Unit.PCT);
			endDateTB.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			endDateTB.setValue(employeeEventsVariable.getEndDate());
			endDateTB.addValueChangeHandler(e -> {
				if(checkIfEndDateCanBeChange(e.getValue(), employeeEventsVariable)) {
					hideErrorPanel();
					
					sortEmployeeEventsVariablesDesc();
					createEmployeeEventsVariablesTable();
				} else
					showDateError();
			});
			
			DoubleBox valueTB = new DoubleBox();
			valueTB.getElement().getStyle().setWidth(100, Unit.PCT);
			valueTB.setValue(employeeEventsVariable.getValue());
			valueTB.addBlurHandler(e -> {
				try {
					Double value = valueTB.getValueOrThrow();
					employeeEventsVariable.setValue(value);
					
					hideErrorPanel();
					
					sortEmployeeEventsVariablesDesc();
					createEmployeeEventsVariablesTable();
				} catch (ParseException exception) {
					showNumberError();
				}
			});
			
			AonToolbarSmallButton deletePeriod = new AonToolbarSmallButton("Borrar tramo", AON.CSS.aonIconDelete());
			deletePeriod.addClickHandler(e -> {
				deletePeriod(employeeEventsVariable);
				sortEmployeeEventsVariablesDesc();
				createEmployeeEventsVariablesTable();
			});
			
			rowPanel.add(startDateTB);
			rowPanel.add(endDateTB);
			rowPanel.add(valueTB);
			rowPanel.add(deletePeriod);
			
			container.add(rowPanel);
			
		});
	}

	// ------------------------------------------------------ Table methods
	
	private boolean checkIfStartDateCanBeChange(Date newStartDate, EmployeeEventsVariable employeeEventsVariable) {
		if(null == newStartDate || newStartDate.before(this.contractStartDate) || (null != this.contractEndDate && newStartDate.after(this.contractEndDate)))
			return false;
		
		for(EmployeeEventsVariable iterator : this.employeeEventsVariables) {
			if(DateUtils.isAfterOrEquals(newStartDate, iterator.getStartDate()) && (null == iterator.getEndDate() || DateUtils.isBeforeOrEquals(newStartDate, iterator.getEndDate())))
				if(!isSamePeriod(iterator, employeeEventsVariable))
					return false;
		}
		
		employeeEventsVariable.setStartDate(newStartDate);
		return true;
	}
	
	private boolean checkIfEndDateCanBeChange(Date newEndDate, EmployeeEventsVariable employeeEventsVariable) {
		if(null != this.contractEndDate && (newEndDate == null || newEndDate.before(this.contractStartDate) || newEndDate.after(this.contractEndDate)))
			return false;
		
		
		if(null == newEndDate) {
			createNewEmployeeEventVariables(newEndDate, employeeEventsVariable);
			return true;
		} else {
			for(EmployeeEventsVariable iterator : this.employeeEventsVariables) {
				if(DateUtils.isAfterOrEquals(newEndDate, iterator.getStartDate()) && DateUtils.isBeforeOrEquals(newEndDate, iterator.getEndDate()))
					if(!isSamePeriod(iterator, employeeEventsVariable))
						return false;
			}
			
			employeeEventsVariable.setEndDate(newEndDate);
			return true;
		}
	}

	private void createNewEmployeeEventVariables(Date newEndDate, EmployeeEventsVariable employeeEventsVariable) {
		sortEmployeeEventsVariablesAsc();
		ArrayList<EmployeeEventsVariable> tmpEmployeeEventsVariables = new ArrayList<EmployeeEventsVariable>();
		
		for(EmployeeEventsVariable iterator : this.employeeEventsVariables) {
			if(DateUtils.isAfterOrEquals(employeeEventsVariable.getStartDate(), iterator.getStartDate()) && !DateUtils.equals(employeeEventsVariable.getStartDate(), iterator.getStartDate())) {
				tmpEmployeeEventsVariables.add(iterator);
			} else if(DateUtils.equals(employeeEventsVariable.getStartDate(), iterator.getStartDate())) {
				iterator.setEndDate(newEndDate);
				tmpEmployeeEventsVariables.add(iterator);
				break;
			}
		}
		
		this.employeeEventsVariables = tmpEmployeeEventsVariables;
	}
	
	private void deletePeriod(EmployeeEventsVariable employeeEventsVariable) {
		sortEmployeeEventsVariablesAsc();
		ArrayList<EmployeeEventsVariable> tmpEmployeeEventsVariables = new ArrayList<EmployeeEventsVariable>();
		
		for(EmployeeEventsVariable iterator : this.employeeEventsVariables) {
//			Window.alert("StartDate -> " + employeeEventsVariable.getStartDate() + " == " + iterator.getStartDate() + " -> " + DateUtils.equals(employeeEventsVariable.getStartDate(), iterator.getStartDate()));
//			Window.alert("EndDate -> " + employeeEventsVariable.getEndDate() + " == " + iterator.getEndDate() + " -> " + DateUtils.equals(employeeEventsVariable.getEndDate(), iterator.getEndDate()));
			if(!DateUtils.equals(employeeEventsVariable.getStartDate(), iterator.getStartDate()) && !DateUtils.equals(employeeEventsVariable.getEndDate(), iterator.getEndDate()))
				tmpEmployeeEventsVariables.add(iterator);
		}
		
		this.employeeEventsVariables = tmpEmployeeEventsVariables;
	}
	
	private boolean canSavePeriod(EmployeeEventsVariable newEmployeeEventsVariable) {
		if(null == newEmployeeEventsVariable.getStartDate() || null == newEmployeeEventsVariable.getValue() || 
				newEmployeeEventsVariable.getStartDate().before(this.contractStartDate))
			return false;
		
		if(null != this.contractEndDate && (newEmployeeEventsVariable.getEndDate() == null || newEmployeeEventsVariable.getEndDate().after(this.contractEndDate)))
			return false;
			
		sortEmployeeEventsVariablesAsc();
		
		for(EmployeeEventsVariable iterator : this.employeeEventsVariables) {
			if(DateUtils.isAfterOrEquals(newEmployeeEventsVariable.getStartDate(), iterator.getStartDate()) && (null == iterator.getEndDate() || DateUtils.isBeforeOrEquals(newEmployeeEventsVariable.getStartDate(), iterator.getEndDate())))
				return false;
			if(DateUtils.isAfterOrEquals(newEmployeeEventsVariable.getEndDate(), iterator.getStartDate()) && DateUtils.isBeforeOrEquals(newEmployeeEventsVariable.getEndDate(), iterator.getEndDate()))
				return false;
		}
		
		this.employeeEventsVariables.add(newEmployeeEventsVariable);
		return true;
	}

	private boolean isSamePeriod(EmployeeEventsVariable employeeEventsVariable1, EmployeeEventsVariable employeeEventsVariable2) {
		return DateUtils.equals(employeeEventsVariable1.getStartDate(), employeeEventsVariable2.getStartDate()) &&
				DateUtils.equals(employeeEventsVariable1.getEndDate(), employeeEventsVariable2.getEndDate());
	}

	// -------------------------------------------------------------------------------
	// ------------------------------ ABSTRACT METHODS -------------------------------
	// -------------------------------------------------------------------------------

	protected abstract void onAccept();
	
	// -------------------------------------------------------------------------------
	// --------------------------------- AUX METHODS ---------------------------------
	// -------------------------------------------------------------------------------
	
	public ArrayList<EmployeeEventsVariable> getEmployeeEventsVariables() {
		return this.employeeEventsVariables;
	}
	
	// ------------------------------------------------- ButtonsPanel
	
	private void getButtonsPanel() {
		closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		closeBtnDialog.addClickHandler(e -> {
			hide();
		});
		
		buttonsPanel.add(closeBtnDialog);
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> {
			hide();
			onAccept();
		});
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	// ------------------------------------------------- ErrorPanel
	
	private void initErrorPanel() {
		contractErrorDateL.setText("La fecha introducida debe se durante el periodo de contrataci\u00F3n (" 
				+ formatDate.format(contractStartDate) + " - "
				+ (null == contractEndDate ? "N/D" : formatDate.format(contractEndDate))
				+ ")");
		errorMessage.getElement().getStyle().setDisplay(Display.NONE);
		errorDeckPanel.showWidget(0);
	}
	
	private void showDateError() {
		errorMessage.getElement().getStyle().clearDisplay();
		errorDeckPanel.showWidget(0);
		acceptBtnDialog.setEnabled(false);
	}
	
	private void showNumberError() {
		errorMessage.getElement().getStyle().clearDisplay();
		errorDeckPanel.showWidget(1);
		acceptBtnDialog.setEnabled(false);
	}
	
	private void hideErrorPanel() {
		errorMessage.getElement().getStyle().setDisplay(Display.NONE);
		acceptBtnDialog.setEnabled(true);
	}

}

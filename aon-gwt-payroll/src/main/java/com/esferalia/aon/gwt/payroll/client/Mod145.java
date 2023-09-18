package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.occam.api.model.mod145.IrpfDataAscendants;
import com.esferalia.aon.occam.api.model.mod145.IrpfDataDescendients;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class Mod145 extends Composite {

	// ----------------------------------------------- UiBinder 
	
	private static EmployeeContractIrpfUiBinder uiBinder = GWT.create(EmployeeContractIrpfUiBinder.class);

	interface EmployeeContractIrpfUiBinder extends UiBinder<Widget, Mod145> {}
	
	// ----------------------------------------------- UiField 
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String fsMaxWidth();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	HTMLPanel mainPanel;
	
	@UiField
	DateBoxEx startDateBx;
	
	@UiField
	DateBoxEx endDateBx;
	
	@UiField
	Button ceutaMelillaB;
	
	@UiField
	Button fiscalExclusionB;
	
	@UiField
	Button irpfRequestB;
	
	@UiField
	HTMLPanel irpfPercentPanel;
	
	@UiField
	DoubleBox irpfPercentDB;
	
	@UiField
	ListBox familySituationLB;
	
	@UiField
	HTMLPanel spouseDocumentPanel;
	
	@UiField
	TextBox spouseDocumentTB;
	
	@UiField
	ListBox disabilityLevelLB;
	
	@UiField
	HTMLPanel dependencePanel;
	
	@UiField
	Button dependenceB;
	
	@UiField
	Button labourProlongationB;
	
	@UiField
	DateBoxEx movingDateBx;
	
	@UiField
	DoubleBox spousalSupportDB;
	
	@UiField
	DoubleBox foodAnnuityDB;
	
	@UiField
	Button deductionHomeLoanB;
	
	@UiField
	Grid descendientsHeader;
	
	@UiField
	ScrollPanel descendientsScrollPanel;
	
	@UiField
	Grid descendientsGrid;
	
	@UiField
	HTMLPanel descendientsToolbar;
	
	@UiField
	Grid ascendantsHeader;
	
	@UiField
	ScrollPanel ascendantsScrollPanel;
	
	@UiField
	Grid ascendantsGrid;
	
	@UiField
	HTMLPanel ascendantsToolbar;
	
	// ----------------------------------------------- Variables 
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private Mod145Object mod145Object;
	
	private AonToolbar toolbar;
	private AonToolbarButton addButton;
	private AonToolbarButton cancelButton;
	private AonToolbarButton saveButton;
	private ListBox datesLB;
	private AonToolbarButton deleteButton;
	private AonToolbarButton printPDFButton;
	
	private com.esferalia.aon.occam.api.model.mod145.Mod145 mod145;
	
	// ----------------------------------------------- Constructor 
	
	protected Mod145() {
		initializeToolbarPanel();
		initWidget(uiBinder.createAndBindUi(this));
		
		getElement().getStyle().setHeight(100, Unit.PCT);
		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		
		scrollPanel.setHeight((Window.getClientHeight() - 260) + "px");
		
		familySituationLB.addStyleName(style.fsMaxWidth());
		
		createDescendients();
		createAscendants();
	}
	
	private void createDescendients() {
		descendientsHeader.clear();
		descendientsHeader.resize(0, 6);
		
		Label birthDayHeader = new Label("A\u00f1o de nacimiento");
		Label adoptionHeader = new Label("A\u00f1o de adopci\u00f3n");
		Label disabilityLevelHeader = new Label("Discapacidad (grado de minusval\u00eda reconocido)");
		Label dependenceHeader = new Label("Necesidad de ayuda de terceras personas o tiene movilidad reducida");
		Label uniqueParentHeader = new Label("C\u00f3mputo por entero de hijos");
		Label actions = new Label();
		
		int row = descendientsHeader.insertRow(descendientsHeader.getRowCount());
		descendientsHeader.setWidget(row, 0, birthDayHeader);
		descendientsHeader.setWidget(row, 1, adoptionHeader);
		descendientsHeader.setWidget(row, 2, disabilityLevelHeader);
		descendientsHeader.setWidget(row, 3, dependenceHeader);
		descendientsHeader.setWidget(row, 4, uniqueParentHeader);
		descendientsHeader.setWidget(row, 5, actions);
		
		descendientsHeader.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
		descendientsHeader.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
		descendientsHeader.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_CENTER);
		descendientsHeader.getCellFormatter().setHorizontalAlignment(row, 3, HasHorizontalAlignment.ALIGN_CENTER);
		descendientsHeader.getCellFormatter().setHorizontalAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER);
		descendientsHeader.getCellFormatter().setHorizontalAlignment(row, 5, HasHorizontalAlignment.ALIGN_CENTER);
		
		descendientsToolbar.clear();
		AonTableButton newDescendientsB = new AonTableButton(AON.MSG.newAction() + " descendiente",  AON.CSS.aonIconAdd());
		newDescendientsB.addClickHandler(e -> createDescendient());
		descendientsToolbar.add(newDescendientsB);
	}

	private void createAscendants() {
		ascendantsHeader.clear();
		ascendantsHeader.resize(0, 5);
		
		Label birthDayHeader = new Label("A\u00f1o de nacimiento");
		Label disabilityLevelHeader = new Label("Discapacidad (grado de minusval\u00eda reconocido)");
		Label dependenceHeader = new Label("Necesidad de ayuda de terceras personas o tiene movilidad reducida");
		Label anotherDescendientHeader = new Label("Convivencia con otros descendientes");
		Label actions = new Label();
		
		int row = ascendantsHeader.insertRow(ascendantsHeader.getRowCount());
		ascendantsHeader.setWidget(row, 0, birthDayHeader);
		ascendantsHeader.setWidget(row, 1, disabilityLevelHeader);
		ascendantsHeader.setWidget(row, 2, dependenceHeader);
		ascendantsHeader.setWidget(row, 3, anotherDescendientHeader);
		ascendantsHeader.setWidget(row, 4, actions);
		
		ascendantsHeader.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
		ascendantsHeader.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
		ascendantsHeader.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_CENTER);
		ascendantsHeader.getCellFormatter().setHorizontalAlignment(row, 3, HasHorizontalAlignment.ALIGN_CENTER);
		ascendantsHeader.getCellFormatter().setHorizontalAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER);
		
		ascendantsToolbar.clear();
		AonTableButton newAscendantsB = new AonTableButton(AON.MSG.newAction() + " ascendente",  AON.CSS.aonIconAdd());
		newAscendantsB.addClickHandler(e -> createAscendant());
		ascendantsToolbar.add(newAscendantsB);
	}

	// ----------------------------------------------- setMod145Object 
	
	public void setMod145Object(Mod145Object mod145Object) {
		this.mod145Object = mod145Object;
		
		this.mod145Object.getMod145List(mod145List -> {
			initializeYearLB(datesLB);
			
			this.mod145 = this.mod145Object.getRecentMod145();
			if(null == this.mod145) { 
				onAdd();
				cancelButton.setVisible(false);
				showWarningMessage("Mod145", "No existe ning\u00fan Modelo 145 para este contrato. Rellene esta pantalla para generarlo.");
			} else showMod145Buttons();
			
			fillMod145();
			
		}, f -> showErrorMessage("Carga Mod145", f.getMessage()));
	}
	
	// ----------------------------------------------- abstractMethods

	protected abstract void showErrorMessage(String title, String message);
	protected abstract void showSuccessMessage(String title, String message);
	protected abstract void showWarningMessage(String title, String message);
	protected abstract void showLoadingMessage(String message);
	protected abstract void createViewer();
	protected abstract void printPDF(String dataURI);
	
	// ----------------------------------------------- fillMod145
	
	private void fillMod145() {
		resetView();
		
		this.startDateBx.setValue(this.mod145.getStartDate());
		this.endDateBx.setValue(this.mod145.getEndDate());
		getEnableDisableButton(this.ceutaMelillaB, this.mod145.isCeutaMelillaPalma());
		getEnableDisableButton(this.fiscalExclusionB, this.mod145.isFiscalExclusion());
		getEnableDisableButton(this.irpfRequestB, null != this.mod145.getIrpfPercent());
		
		irpfPercentPanel.setVisible(null != this.mod145.getIrpfPercent());
		this.irpfPercentDB.setValue(this.mod145.getIrpfPercent());
		
		setSelectedValueLB(this.familySituationLB, this.mod145.getFamilySituation() + "");
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), familySituationLB);
		this.spouseDocumentTB.setValue(this.mod145.getSpouseDocument());
		setSelectedValueLB(this.disabilityLevelLB, this.mod145.getDisabilityLevel() + "");
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), disabilityLevelLB);
		getEnableDisableButton(this.dependenceB, this.mod145.isDependence());
		getEnableDisableButton(this.labourProlongationB, this.mod145.isLabourProlongation());
		this.movingDateBx.setValue(this.mod145.getMovingDate());
		
		this.spousalSupportDB.setValue(this.mod145.getSpousalSupport());
		this.foodAnnuityDB.setValue(this.mod145.getFoodAnnuity());
		getEnableDisableButton(this.deductionHomeLoanB, this.mod145.isDeductionHomeLoan());
		
		fillDescendients();
		fillAscendants();
	}

	private void fillDescendients() {
		descendientsGrid.clear();
		descendientsGrid.resize(0, 6);
		
		this.mod145.getDescendients().stream().filter(descendient -> !descendient.isDeleted()).forEach(descendient -> fillDescendient(descendient));
	}

	private void fillDescendient(IrpfDataDescendients descendient) {
		TextBox birthDay = new TextBox();
		birthDay.setWidth("100px");
		birthDay.setValue(null == descendient.getBirthYear() ? "" : descendient.getBirthYear() + "");
		birthDay.addValueChangeHandler(e -> descendient.setBirthYear(Integer.parseInt(e.getValue())));
		
		TextBox adoption = new TextBox();
		adoption.setWidth("100px");
		adoption.setValue(null == descendient.getAdoptionYear() ? "" : descendient.getAdoptionYear() + "");
		adoption.addValueChangeHandler(e -> descendient.setAdoptionYear(Integer.parseInt(e.getValue())));
		
		ListBox disabilityLevel =  createDisabilityLevelLB();
		setSelectedValueLB(disabilityLevel, descendient.getDisabilityLevel() + "");
		disabilityLevel.addChangeHandler(e -> descendient.setDisabilityLevel(Byte.parseByte(disabilityLevel.getSelectedValue())));
		
		Button dependence = new Button();
		getEnableDisableButton(dependence, descendient.isDependence());
		dependence.addClickHandler(e -> {
			Boolean oldValue = isActiveToggleButton(dependence);
			Boolean value = !oldValue;
			getEnableDisableButton(dependence, value);
			descendient.setDependence(value);
		});
		
		Button uniqueParent = new Button();
		getEnableDisableButton(uniqueParent, descendient.isUniqueParent());
		uniqueParent.addClickHandler(e -> {
			Boolean oldValue = isActiveToggleButton(uniqueParent);
			Boolean value = !oldValue;
			getEnableDisableButton(uniqueParent, value);
			descendient.setUniqueParent(value);
		});
		
		AonToolbarSmallButton delete = new AonToolbarSmallButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		delete.addClickHandler(e -> {
			AonDialog deleteDialog = new AonDialog("Eliminar Descendiente", new HTML("\u00BFDesea eliminar el descendiente seleccionado\u003F"));
			deleteDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					descendient.setDeleted(true);
					checkDescendientCount();
					fillDescendients();
				}
			});
		});
		
		int row = descendientsGrid.insertRow(descendientsGrid.getRowCount());
		descendientsGrid.setWidget(row, 0, birthDay);
		descendientsGrid.setWidget(row, 1, adoption);
		descendientsGrid.setWidget(row, 2, disabilityLevel);
		descendientsGrid.setWidget(row, 3, dependence);
		descendientsGrid.setWidget(row, 4, uniqueParent);
		descendientsGrid.setWidget(row, 5, delete);
		
		descendientsGrid.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
		descendientsGrid.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
		descendientsGrid.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_CENTER);
		descendientsGrid.getCellFormatter().setHorizontalAlignment(row, 3, HasHorizontalAlignment.ALIGN_CENTER);
		descendientsGrid.getCellFormatter().setHorizontalAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER);
		descendientsGrid.getCellFormatter().setHorizontalAlignment(row, 5, HasHorizontalAlignment.ALIGN_CENTER);
	}

	private void fillAscendants() {
		ascendantsGrid.clear();
		ascendantsGrid.resize(0, 5);
		
		this.mod145.getAscendants().stream().filter(ascendant -> !ascendant.isDeleted()).forEach(ascendant -> fillAscendant(ascendant));
	}

	private void fillAscendant(IrpfDataAscendants ascendant) {
		TextBox birthDay = new TextBox();
		birthDay.setWidth("100px");
		birthDay.setValue(null == ascendant.getBirthYear() ? "" : ascendant.getBirthYear() + "");
		birthDay.addValueChangeHandler(e -> ascendant.setBirthYear(Integer.parseInt(e.getValue())));
		
		ListBox disabilityLevel =  createDisabilityLevelLB();
		setSelectedValueLB(disabilityLevel, ascendant.getDisabilityLevel() + "");
		disabilityLevel.addChangeHandler(e -> ascendant.setDisabilityLevel(Byte.parseByte(disabilityLevel.getSelectedValue())));
		
		Button dependence = new Button();
		getEnableDisableButton(dependence, ascendant.isDependence());
		dependence.addClickHandler(e -> {
			Boolean oldValue = isActiveToggleButton(dependence);
			Boolean value = !oldValue;
			getEnableDisableButton(dependence, value);
			ascendant.setDependence(value);
		});
		
		Button anotherDescendient = new Button();
		getEnableDisableButton(anotherDescendient, ascendant.isAnotherDescendient());
		anotherDescendient.addClickHandler(e -> {
			Boolean oldValue = isActiveToggleButton(anotherDescendient);
			Boolean value = !oldValue;
			getEnableDisableButton(anotherDescendient, value);
			ascendant.setAnotherDescendient(value);
		});
		
		AonToolbarSmallButton delete = new AonToolbarSmallButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		delete.addClickHandler(e -> {
			AonDialog deleteDialog = new AonDialog("Eliminar Ascendente", new HTML("\u00BFDesea eliminar el ascendente seleccionado\u003F"));
			deleteDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					ascendant.setDeleted(true);
					fillAscendants();
				}
			});
		});
		
		int row = ascendantsGrid.insertRow(ascendantsGrid.getRowCount());
		ascendantsGrid.setWidget(row, 0, birthDay);
		ascendantsGrid.setWidget(row, 1, disabilityLevel);
		ascendantsGrid.setWidget(row, 2, dependence);
		ascendantsGrid.setWidget(row, 3, anotherDescendient);
		ascendantsGrid.setWidget(row, 4, delete);
		
		ascendantsGrid.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
		ascendantsGrid.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
		ascendantsGrid.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_CENTER);
		ascendantsGrid.getCellFormatter().setHorizontalAlignment(row, 3, HasHorizontalAlignment.ALIGN_CENTER);
		ascendantsGrid.getCellFormatter().setHorizontalAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER);
	}

	private void resetView() {
		startDateBx.setValue(null);
		endDateBx.setValue(null);
		
		getEnableDisableButton(ceutaMelillaB, false);
		getEnableDisableButton(fiscalExclusionB, false);
		getEnableDisableButton(irpfRequestB, false);
		
		irpfPercentPanel.setVisible(true);
		irpfPercentDB.setValue(null);
		
		familySituationLB.clear();
		familySituationLB.addItem("Soltero/a, viudo/a, divorciado/a o separado/a legalmente con hijos solteros menores de 18 a\u00f1os o incapacitados judicialmente que conviven exclusivamente con Vd., sin convivir tambi\u00e9n con el otro progenitor", "0");
		familySituationLB.addItem("Casado/a y no separado/a legalmente cuyo c\u00f3nyuge no obtiene rentas superiores a 1.500 euros anuales, excluidas las exentas", "1");
		familySituationLB.addItem("Situaci\u00f3n familiar distinta de las dos anteriores (solteros sin hijos, casados cuyos c\u00f3nyuge obtiene rentas superiores a 1500 euros anuales, etc.)", "2");
	
		spouseDocumentTB.setValue(null);
		
		disabilityLevelLB.clear();
		disabilityLevelLB.addItem("-", "");
		disabilityLevelLB.addItem("Igual o superior al 33% e inferior al 65%", "0");
		disabilityLevelLB.addItem("Igual o superior al 65%", "2");
		
		getEnableDisableButton(dependenceB, false);
		getEnableDisableButton(labourProlongationB, false);
		
		movingDateBx.setValue(null);
		
		spousalSupportDB.setValue(null);
		foodAnnuityDB.setValue(null);
		getEnableDisableButton(deductionHomeLoanB, false);
		
		descendientsGrid.clear();
		descendientsGrid.resize(0, 6);
		
		ascendantsGrid.clear();
		ascendantsGrid.resize(0, 5);
		
		setColumnWidth();
	}
	
	private void setColumnWidth() {
		descendientsHeader.getColumnFormatter().getElement(0).getStyle().setWidth(10, Unit.PCT);
		descendientsHeader.getColumnFormatter().getElement(1).getStyle().setWidth(10, Unit.PCT);
		descendientsHeader.getColumnFormatter().getElement(2).getStyle().setWidth(25, Unit.PCT);
		descendientsHeader.getColumnFormatter().getElement(3).getStyle().setWidth(35, Unit.PCT);
		descendientsHeader.getColumnFormatter().getElement(4).getStyle().setWidth(15, Unit.PCT);
		descendientsHeader.getColumnFormatter().getElement(5).getStyle().setWidth(5, Unit.PCT);
		
		descendientsGrid.getColumnFormatter().getElement(0).getStyle().setWidth(10, Unit.PCT);
		descendientsGrid.getColumnFormatter().getElement(1).getStyle().setWidth(10, Unit.PCT);
		descendientsGrid.getColumnFormatter().getElement(2).getStyle().setWidth(25, Unit.PCT);
		descendientsGrid.getColumnFormatter().getElement(3).getStyle().setWidth(35, Unit.PCT);
		descendientsGrid.getColumnFormatter().getElement(4).getStyle().setWidth(15, Unit.PCT);
		descendientsGrid.getColumnFormatter().getElement(5).getStyle().setWidth(5, Unit.PCT);
		
		ascendantsHeader.getColumnFormatter().getElement(0).getStyle().setWidth(10, Unit.PCT);
		ascendantsHeader.getColumnFormatter().getElement(1).getStyle().setWidth(25, Unit.PCT);
		ascendantsHeader.getColumnFormatter().getElement(2).getStyle().setWidth(40, Unit.PCT);
		ascendantsHeader.getColumnFormatter().getElement(3).getStyle().setWidth(20, Unit.PCT);
		ascendantsHeader.getColumnFormatter().getElement(4).getStyle().setWidth(5, Unit.PCT);
		
		ascendantsGrid.getColumnFormatter().getElement(0).getStyle().setWidth(10, Unit.PCT);
		ascendantsGrid.getColumnFormatter().getElement(1).getStyle().setWidth(25, Unit.PCT);
		ascendantsGrid.getColumnFormatter().getElement(2).getStyle().setWidth(40, Unit.PCT);
		ascendantsGrid.getColumnFormatter().getElement(3).getStyle().setWidth(20, Unit.PCT);
		ascendantsGrid.getColumnFormatter().getElement(4).getStyle().setWidth(5, Unit.PCT);
	}

	// ----------------------------------------------- setMod145Object.Methods
	
	public void initializeYearLB(ListBox datesLB) {
		datesLB.clear();
		this.mod145Object.getDateList().forEach(date -> datesLB.addItem(formatDate.format(date), formatDate.format(date)));
		
		datesLB.addChangeHandler(e -> {
			String datesLBValue = datesLB.getSelectedValue();
			this.mod145 = this.mod145Object.getMod145ByDate(formatDate.parse(datesLBValue));
			fillMod145();
		});
	}
	
	private void createMod145() {
		this.mod145 = new com.esferalia.aon.occam.api.model.mod145.Mod145()
				.setId(generateId())
				.setDomain(mod145Object.getDomainId())
				.setContract(mod145Object.getContractId())
				.setIssueDate(new Date())
				.setDeleted(false);
	}
	
	private void createDescendient() {
		if(this.mod145.getDescendients().stream().filter(descendient -> !descendient.isDeleted()).collect(Collectors.toList()).size() < 4) {
			IrpfDataDescendients descendient = new IrpfDataDescendients()
					.setId(generateId())
					.setDomain(mod145Object.getDomainId())
					.setIrpfData(this.mod145.getId())
					.setDeleted(false);
			
			this.mod145.getDescendients().add(descendient);
			checkDescendientCount();
			fillDescendient(descendient);
		} else
			showWarningMessage("Descendientes", "No se pueden introducir m\u00e1s de cuatro descendientes");
	}

	private void createAscendant() {
		if(this.mod145.getAscendants().stream().filter(ascendant -> !ascendant.isDeleted()).collect(Collectors.toList()).size() < 2) {
			IrpfDataAscendants ascendant = new IrpfDataAscendants()
					.setId(generateId())
					.setDomain(mod145Object.getDomainId())
					.setIrpfData(this.mod145.getId())
					.setDeleted(false);
			
			this.mod145.getAscendants().add(ascendant);
			fillAscendant(ascendant);
		} else
			showWarningMessage("Ascendentes", "No se pueden introducir m\u00e1s de dos ascendentes");
	}
	
	private Integer generateId() {
		int id = new Random().nextInt();
		return id < 0 ? id : (id * -1);
	}
	
	private void checkDescendientCount() {
		List<IrpfDataDescendients> descendientList = this.mod145.getDescendients().stream().filter(descendient -> !descendient.isDeleted()).collect(Collectors.toList());
		this.mod145.setDescendientCount((byte) descendientList.size());
	}
	
	// ----------------------------------------------- UIFields
	
	@UiHandler("startDateBx")
	void onStartDateBxChange(ValueChangeEvent<Date> event) {
		List<Date> dateList = this.mod145Object.getDateList();
		if(dateList.isEmpty() || startDateBx.getValue().after(dateList.get(0)))
			this.mod145.setStartDate(startDateBx.getValue());
		else
			showWarningMessage("Fecha inicio", "La fecha de inicio no puede ser anterior al \u00faltimo Mod145 creado");
	}
	
	@UiHandler("endDateBx")
	void onEndDateBxChange(ValueChangeEvent<Date> event) {
		this.mod145.setEndDate(endDateBx.getValue());
	}
	
	@UiHandler("ceutaMelillaB")
	void onCeutaMelillaBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(ceutaMelillaB);
		Boolean value = !oldValue;
		getEnableDisableButton(ceutaMelillaB, value);
		this.mod145.setCeutaMelillaPalma(value);
	}
	
	@UiHandler("fiscalExclusionB")
	void onFiscalExclusionBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(fiscalExclusionB);
		Boolean value = !oldValue;
		getEnableDisableButton(fiscalExclusionB, value);
		this.mod145.setFiscalExclusion(value);
		
		if(Boolean.TRUE.equals(value)) {
			irpfPercentPanel.setVisible(false);
			this.mod145.setIrpfPercent(null);
			getEnableDisableButton(irpfRequestB, false);
		}
	}
	
	@UiHandler("irpfRequestB")
	void onIrpfPercentBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(irpfRequestB);
		Boolean value = !oldValue;
		getEnableDisableButton(irpfRequestB, value);
		
		if(Boolean.TRUE.equals(value)) {
			irpfPercentPanel.setVisible(true);
			this.mod145.setFiscalExclusion(false);
			getEnableDisableButton(fiscalExclusionB, false);
		} else {
			irpfPercentPanel.setVisible(false);
			this.mod145.setIrpfPercent(null);
		}
	}
	
	@UiHandler("irpfPercentDB")
	void onIrpfPercentDBChange(ValueChangeEvent<Double> event) {
		this.mod145.setIrpfPercent(irpfPercentDB.getValue());
	}
	
	@UiHandler("familySituationLB")
	void onFamilySituationLBChange(ChangeEvent event) {
		String selectedValue = familySituationLB.getSelectedValue();
		this.mod145.setFamilySituation(Byte.parseByte(selectedValue));
		
		if(AonStringUtils.equalsIgnoreCase(selectedValue, "1"))
			spouseDocumentPanel.setVisible(true);
		else {
			spouseDocumentPanel.setVisible(false);
			this.mod145.setSpouseDocument(null);
		}
	}
	
	@UiHandler("spouseDocumentTB")
	void onSpouseDocumentTBChange(ValueChangeEvent<String> event) {
		this.mod145.setSpouseDocument(spouseDocumentTB.getValue());
	}
	
	@UiHandler("disabilityLevelLB")
	void onDisabilityLevelLBChange(ChangeEvent event) {
		String selectedValue = disabilityLevelLB.getSelectedValue();
		if(AonStringUtils.isNotBlank(selectedValue) && AonStringUtils.equalsIgnoreCase(selectedValue, "0"))
			dependencePanel.setVisible(true);
		else {
			dependencePanel.setVisible(false);
			this.mod145.setDependence(false);
		}
		
		this.mod145.setDisabilityLevel(AonStringUtils.isBlank(selectedValue) ? null : Byte.parseByte(selectedValue));
	}
	
	@UiHandler("dependenceB")
	void onDependenceBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(dependenceB);
		Boolean value = !oldValue;
		getEnableDisableButton(dependenceB, value);
		this.mod145.setDependence(value);
	}
	
	@UiHandler("labourProlongationB")
	void onLabourProlongationBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(labourProlongationB);
		Boolean value = !oldValue;
		getEnableDisableButton(labourProlongationB, value);
		this.mod145.setLabourProlongation(value);
	}
	
	@UiHandler("movingDateBx")
	void onMovingDateBxChange(ValueChangeEvent<Date> event) {
		this.mod145.setMovingDate(movingDateBx.getValue());
	}
	
	@UiHandler("spousalSupportDB")
	void onSpousalSupportDBChange(ValueChangeEvent<Double> event) {
		this.mod145.setSpousalSupport(spousalSupportDB.getValue());
	}
	
	@UiHandler("foodAnnuityDB")
	void onFoodAnnuityDBChange(ValueChangeEvent<Double> event) {
		this.mod145.setFoodAnnuity(foodAnnuityDB.getValue());
	}
	
	@UiHandler("deductionHomeLoanB")
	void onDeductionHomeLoanBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(deductionHomeLoanB);
		Boolean value = !oldValue;
		getEnableDisableButton(deductionHomeLoanB, value);
		this.mod145.setDeductionHomeLoan(value);
	}
	
	// ----------------------------------------------- auxiliarMethods

	private ListBox createDisabilityLevelLB() {
		ListBox listBox = new ListBox();
		listBox.addItem("-", "0");
		listBox.addItem("Igual o superior al 33% e inferior al 65%", "1");
		listBox.addItem("Igual o superior al 65%", "2");
		return listBox;
	}

	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}
	
	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}

	// ----------------------------------------------- Toolbar
	
	private void initializeToolbarPanel() {
		
		this.toolbar = new AonToolbar("Irpf");
		
		addButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		addButton.addClickHandler(e -> onAdd());
		toolbar.add(addButton);
		
		saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveButton.addClickHandler(e -> onSave());
		toolbar.add(saveButton);
		
		cancelButton = new AonToolbarButton( AON.MSG.cancelAction(), AON.CSS.aonIconCancel() );
		cancelButton.addClickHandler(e -> onCancel());
		toolbar.add(cancelButton);
		
		this.datesLB = new ListBox();
		this.toolbar.add(datesLB);
		
		deleteButton = new AonToolbarButton( AON.MSG.deleteAction(), AON.CSS.aonIconDelete() );
		deleteButton.addClickHandler(e -> onDelete());
		toolbar.add(deleteButton);
		
		printPDFButton = new AonToolbarButton( AON.MSG.printPDF(), AON.CSS.aonIconPdf() );
		printPDFButton.addClickHandler(e -> onPrintPDF());
		toolbar.add(printPDFButton);
	}

	// ----------------------------------------------- Toolbar.Methods
	
	public void onAdd() {
		createMod145();
		fillMod145();
		showEmptyMod145Buttons();
	}
	
	public void onCancel() {
		showMod145Buttons();
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), datesLB);
	}

	public void onSave() {
		Date startDate = startDateBx.getValue();
		if(null == startDate)
			showErrorMessage("Mod145", "Para poder grabar un Mod145 es necesario indicar la fecha de inicio");
		else {
			showLoadingMessage("Guardando Mod145...");
			this.mod145Object.saveMod145(
					this.mod145, 
					s -> {
						showSuccessMessage("Mod 145", "Mod 145 guardado correctamente");
						setMod145Object(this.mod145Object);
					},
					f -> showErrorMessage("Mod 145", f.getMessage()));
		}
	}
	
	public void onDelete() {
		AonDialog deleteDialog = new AonDialog("Eliminar Mod145", new HTML("\u00BFDesea eliminar el modelo 145 seleccionado\u003F"));
		deleteDialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {
				// Nothing to do here
			}
			
			@Override
			public void onAccept() {
				mod145.setDeleted(true);
				onSave();
			}
		});
	}
	
	public void onPrintPDF() {
		showLoadingMessage("Exportando Mod145 PDF...");
		createViewer();
		this.mod145Object.printMod145(mod145, 
			dataURI -> printPDF(dataURI), 
			f -> showErrorMessage("Error PDF Mod 145", f.getMessage()));
	}
	
	private void showMod145Buttons() {
		addButton.setVisible(true);
		cancelButton.setVisible(false);
		saveButton.setVisible(true);
		datesLB.setVisible(true);
		deleteButton.setVisible(true);
		printPDFButton.setVisible(true);
	}
	
	private void showEmptyMod145Buttons() {
		addButton.setVisible(false);
		cancelButton.setVisible(true);
		saveButton.setVisible(true);
		datesLB.setVisible(false);
		deleteButton.setVisible(false);
		printPDFButton.setVisible(false);
	}
	
	// -------------------------------------------------- ContrataEmployee.Methods
	
	public void setToolbarTitle(String employeeName) {
		toolbar.setTitle(employeeName);
	}
	
	public void addMainMT() {
		scrollPanel.getElement().getStyle().setMarginTop(50, Unit.PX);
	}
	
	public Integer getContractId(){
		return this.mod145Object.getContractId();
	}
	
	public void hideToolbar(){
		dockLayoutPanel.remove(toolbar);
		mainPanel.getElement().getStyle().setMarginTop(0, Unit.PX);
	}
	
	public void setAddButton(AonToolbarButton addButton) {
		this.addButton = addButton;
	}
	
	public void setCancelButton(AonToolbarButton cancelButton) {
		this.cancelButton = cancelButton;
	}
	
	public void setSaveButton(AonToolbarButton saveButton) {
		this.saveButton = saveButton;
	}
	
	public void setMod145DatesLB(ListBox datesLB) {
		this.datesLB = datesLB;
	}
	
	public void setDeleteButton(AonToolbarButton deleteButton) {
		this.deleteButton = deleteButton;
	}
	
	public void setPrintPDFButton(AonToolbarButton printPDFButton) {
		this.printPDFButton = printPDFButton;
	}
	
}

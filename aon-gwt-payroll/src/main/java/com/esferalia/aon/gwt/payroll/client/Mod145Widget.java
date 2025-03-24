package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.mod145.IrpfDataAscendants;
import com.esferalia.aon.occam.api.model.mod145.IrpfDataDescendients;
import com.esferalia.aon.occam.api.model.mod145.Mod145;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class Mod145Widget extends AonCustomDockLayout {

	// ----------------------------------------------- UI 
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private DeckPanel deckPanel;
	
	private ScrollPanel scrollPanel;
	
	private HTMLPanel cardsPanel;
	
	private AonToolbarButton addButton;
	private AonToolbarButton cancelButton;
	private AonToolbarButton saveButton;
	private AonToolbarButton deleteButton;
	private AonToolbarButton printPDFButton;
	private AonToolbarButton closePDFButton;
	
	private AonCustomListBox datesLB = new AonCustomListBox("Periodo");
	
	private AonCustomDateBox startDate;
	private AonCustomDateBox endDate;
	private AonCustomCheckBox ceutaMelillaCB;
	private AonCustomCheckBox fiscalExclusionCB;
	private AonCustomCheckBox irpfRequestCB;
	private AonCustomNumberBox irpfPercentDB;
	private AonCustomListBox familySituationLB;
	private AonCustomTextBox spouseDocumentTB;
	private AonCustomListBox disabilityLevelLB;
	private AonCustomCheckBox dependenceCB;
	private AonCustomCheckBox labourProlongationCB;
	private AonCustomDateBox movingDateBx;
	
	private AonCustomNumberBox spousalSupportDB;
	private AonCustomNumberBox foodAnnuityDB;
	private AonCustomCheckBox deductionHomeLoanCB;
	
	private AonCustomCard descendientsCard;
	private AonCustomTable descendientsTable;
	
	private AonCustomCard ascendantsCard;
	private AonCustomTable ascendantsTable;
	
	private HTMLPanel viewerPanel;
	private FullViewer viewer = new FullViewer();
	
	// ----------------------------------------------- Variables 
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private com.esferalia.aon.occam.api.model.mod145.Mod145 mod145;
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private Integer contractId;
	private Integer domainId;
	private List<Mod145> mod145List = new ArrayList<>();
	
	// ----------------------------------------------- Descendients 
	
	private static enum DESCENDIENTS_COLS {
		  BIR("A\u00f1o Nacimiento"										,"7rem"  			,"")
		, ADO("A\u00f1o Adopci\u00f3n"									,"7rem"  			,"")
		, DIS("Discapacidad (grado de minusvalia reconocido)"			,"20rem"  			,"")
		, AYU("Ayuda terceras personas o movilidad reducida"			,"19rem"  			,"")
		, COM("C\u00f3mputo por entero de hijos"						,"12rem"  			,"")
		, BUT(AonStringUtils.EMPTY										,"3rem" 			,"")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private DESCENDIENTS_COLS(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}
	
	// ----------------------------------------------- scendants 
	
	private static enum ASCENDANTS_COLS {
		  BIR("A\u00f1o Nacimiento"										,"7rem"  			,"")
		, DIS("Discapacidad (grado de minusvalia reconocido)"			,"20rem"  			,"")
		, AYU("Ayuda terceras personas o movilidad reducida"			,"19rem"  			,"")
		, CON("Convivencia con otros descendientes"						,"15rem"  			,"")
		, BUT(AonStringUtils.EMPTY										,"3rem" 			,"")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private ASCENDANTS_COLS(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}
	
	// ----------------------------------------------- Constructor 
	
	protected Mod145Widget() {
		super("Modelo 145");
		
		hideToolbarFilterMessages();
		hideFilterButton();
		
		addButtonsToolbar();
		
		insertWidgetAfterSearchButton(datesLB);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());

		container.add(messagePanel);
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setHeight("100%");
		
		cardsPanel = new HTMLPanel("");
		cardsPanel.addStyleName(AON.CSS.aonFlexColumn());
		cardsPanel.getElement().getStyle().setProperty("padding", "1rem 0");
		cardsPanel.getElement().getStyle().setProperty("align-items", "center");
		
		scrollPanel.setWidget(cardsPanel);
		
		viewerPanel = new HTMLPanel("");
		viewerPanel.addStyleName(AON.CSS.aonFlexColumn());
		viewerPanel.getElement().getStyle().setProperty("padding", "1rem 0");
		viewerPanel.getElement().getStyle().setProperty("align-items", "center");
		
		viewerPanel.add(viewer);
		
		deckPanel = new DeckPanel();
		deckPanel.setHeight("100%");
		
		deckPanel.add(scrollPanel);
		deckPanel.add(viewerPanel);
		
		showMod145();
		
		container.add(deckPanel);
		
		add(container);
	}
	
	@Override
	protected void onClearFilter() {}
	
	private void showMod145() {
		addButton.setVisible(true);
		cancelButton.setVisible(false);
		saveButton.setVisible(true);
		datesLB.setVisible(true);
		deleteButton.setVisible(true);
		printPDFButton.setVisible(true);
		
		closePDFButton.setVisible(false);
		
		deckPanel.showWidget(0);
	}
	
	private void showPDF() {
		addButton.setVisible(false);
		cancelButton.setVisible(false);
		saveButton.setVisible(false);
		datesLB.setVisible(false);
		deleteButton.setVisible(false);
		printPDFButton.setVisible(false);
		
		closePDFButton.setVisible(true);
		
		deckPanel.showWidget(1);
	}

	private void addButtonsToolbar() {		
		addButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		addButton.addClickHandler(e -> onAdd());
		addToolbarButton(addButton);
		
		saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveButton.addClickHandler(e -> onSave());
		addToolbarButton(saveButton);
		
		cancelButton = new AonToolbarButton( AON.MSG.cancelAction(), AON.CSS.aonIconCancel() );
		cancelButton.addClickHandler(e -> onCancel());
		addToolbarButton(cancelButton);
		
		deleteButton = new AonToolbarButton( AON.MSG.deleteAction(), AON.CSS.aonIconDelete() );
		deleteButton.addClickHandler(e -> onDelete());
		addToolbarButton(deleteButton);
		
		printPDFButton = new AonToolbarButton( AON.MSG.printPDF(), AON.CSS.aonIconPdf() );
		printPDFButton.addClickHandler(e -> onPrintPDF());
		addToolbarButton(printPDFButton);
		
		closePDFButton = new AonToolbarButton( AON.MSG.close(), AON.CSS.aonIconClose() );
		closePDFButton.addClickHandler(e -> showMod145());
		addToolbarButton(closePDFButton);
	}

	// ----------------------------------------------- setMod145Object 
	
	public void setMod145Object(Integer domainId, Integer contractId) {
		this.domainId = domainId;
		this.contractId = contractId;
		
		getMod145List(mod145List -> {
			initializeYearLB();
			
			this.mod145 = getRecentMod145();
			if(null == this.mod145) { 
				onAdd();
				cancelButton.setVisible(false);
				AonMessagePanel.showWarning(messagePanel, "No existe ning\u00fan Modelo 145 para este contrato. Rellene esta pantalla para generarlo.");
			} else showMod145Buttons();
			
			fillMod145();
			
		}, f -> AonMessagePanel.showError(messagePanel, f.getMessage()));
	}
	
	public void initializeYearLB() {
		datesLB.clearItems();
		getDateList().forEach(date -> datesLB.addItem(formatDate.format(date), formatDate.format(date)));
		
		datesLB.addChangeHandler(e -> {
			this.mod145 = getMod145ByDate(formatDate.parse(datesLB.getValue()));
			fillMod145();
		});
	}
	
	private void fillMod145() {
		cardsPanel.clear();
		
		cardsPanel.add(createGeneralCard());
		cardsPanel.add(createEconomicCard());
		cardsPanel.add(createDescendientsCard());
		cardsPanel.add(createAscendantsCard());
	}

	private Widget createGeneralCard() {
		AonCustomCard generalCard = new AonCustomCard("Informaci\u00f3n General");
		generalCard.addStyleName(AON.CSS.aonContractLargeCard());
		
		FlowPanel table = createFlexColumnPanel();
		
		startDate = new AonCustomDateBox("Vigencia Desde");
		endDate = new AonCustomDateBox("Vigencia hasta");
		
		startDate.setValue(null == this.mod145 ? null : this.mod145.getStartDate());
		endDate.setValue(null == this.mod145 ? null : this.mod145.getEndDate());
		
		table.add(createRow(startDate, endDate));
		
		ceutaMelillaCB = new AonCustomCheckBox("Residencia habitual y efectiva en Ceuta, Melilla o La Palma");
		fiscalExclusionCB = new AonCustomCheckBox("Exclusi\u00f3n a la obligaci\u00f3n de tributar");
		
		ceutaMelillaCB.setValue(null == this.mod145 ? false : this.mod145.isCeutaMelillaPalma());
		fiscalExclusionCB.setValue(null == this.mod145 ? false : this.mod145.isFiscalExclusion());
		
		table.add(createRow(ceutaMelillaCB, fiscalExclusionCB));
		
		irpfRequestCB = new AonCustomCheckBox("IRPF Solicitado por el trabajador");
		irpfPercentDB = new AonCustomNumberBox("Porcentaje IRPF");
		irpfPercentDB.hideNearBy();
		
		irpfRequestCB.setValue(null == this.mod145 ? false : null != this.mod145.getIrpfPercent());
		irpfPercentDB.setVisible(null != this.mod145 && null != this.mod145.getIrpfPercent());
		irpfPercentDB.setValue(null == this.mod145 ? 0.00 : (null != this.mod145.getIrpfPercent() ? this.mod145.getIrpfPercent() : 0.00));
		
		table.add(createRow(irpfRequestCB, irpfPercentDB));
		
		familySituationLB = new AonCustomListBox("Situaci\u00f3n familiar");
		spouseDocumentTB = new AonCustomTextBox("NIF del c\u00f3nyuge");
		
		familySituationLB.clearItems();
		familySituationLB.addItem("Soltero/a, viudo/a, divorciado/a o separado/a legalmente con hijos solteros menores de 18 a\u00f1os o incapacitados judicialmente que conviven exclusivamente con Vd., sin convivir tambi\u00e9n con el otro progenitor", "0");
		familySituationLB.addItem("Casado/a y no separado/a legalmente cuyo c\u00f3nyuge no obtiene rentas superiores a 1.500 euros anuales, excluidas las exentas", "1");
		familySituationLB.addItem("Situaci\u00f3n familiar distinta de las dos anteriores (solteros sin hijos, casados cuyos c\u00f3nyuge obtiene rentas superiores a 1500 euros anuales, etc.)", "2");
		
		familySituationLB.setValue(null == this.mod145 || null == this.mod145.getFamilySituation() ? "2" : this.mod145.getFamilySituation().toString());
		spouseDocumentTB.setValue(null == this.mod145 ? null : this.mod145.getSpouseDocument());
		
		table.add(createRow(familySituationLB, spouseDocumentTB));
		
		disabilityLevelLB = new AonCustomListBox("Discapacidad (grado de minusvalia reconocido)");
		dependenceCB = new AonCustomCheckBox("Necesidad de ayuda de terceras personas o tiene movilidad reducida");
		
		disabilityLevelLB.clearItems();
		disabilityLevelLB.addItem("-", "");
		disabilityLevelLB.addItem("Igual o superior al 33% e inferior al 65%", "0");
		disabilityLevelLB.addItem("Igual o superior al 65%", "2");
		
		disabilityLevelLB.setValue(null == this.mod145 || null == this.mod145.getDisabilityLevel()  ? null : this.mod145.getDisabilityLevel().toString());
		dependenceCB.setVisible(null != this.mod145 && null != this.mod145.getDisabilityLevel() && this.mod145.getDisabilityLevel() == (byte)0);
		dependenceCB.setValue(null == this.mod145 ? null : this.mod145.isDependence());
		
		table.add(createRow(disabilityLevelLB, dependenceCB));
		
		labourProlongationCB = new AonCustomCheckBox("Prolongaci\u00f3n de la actividad laboral a partir de los 65 a\u00f1os de edad");
		movingDateBx = new AonCustomDateBox("Fecha de traslado");
		
		labourProlongationCB.setValue(null == this.mod145 ? null : this.mod145.isLabourProlongation());
		movingDateBx.setValue(null == this.mod145 ? null : this.mod145.getMovingDate());
		
		table.add(createRow(labourProlongationCB, movingDateBx));
		
		generalCard.add(table);
		
		addGeneralHandlers();
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), familySituationLB.getListBox()); 
		
		return generalCard;
	}

	private void addGeneralHandlers() {
		startDate.addValueChangeHandler(e -> {
			List<Date> dateList = getDateList();
			if(dateList.isEmpty() || startDate.getValue().after(dateList.get(0)))
				this.mod145.setStartDate(startDate.getValue());
			else
				AonMessagePanel.showWarning(messagePanel, "Fecha inicio : La fecha de inicio no puede ser anterior al \u00faltimo Mod145 creado");
		});
		
		endDate.addValueChangeHandler(e -> this.mod145.setEndDate(endDate.getValue()));
		
		ceutaMelillaCB.addValueChangeHandler(e -> this.mod145.setCeutaMelillaPalma(ceutaMelillaCB.getValue()));
		fiscalExclusionCB.addValueChangeHandler(e -> {
			this.mod145.setFiscalExclusion(fiscalExclusionCB.getValue());
		
			if(Boolean.TRUE.equals(fiscalExclusionCB.getValue())) {
				irpfRequestCB.setValue(false);
				irpfPercentDB.setVisible(false);
				this.mod145.setIrpfPercent(null);
			}
		});
		
		irpfRequestCB.addValueChangeHandler(e -> {
			if(Boolean.TRUE.equals(irpfRequestCB.getValue())) {
				irpfPercentDB.setVisible(true);
				this.mod145.setFiscalExclusion(false);
				fiscalExclusionCB.setValue(false);
			} else {
				irpfPercentDB.setVisible(false);
				this.mod145.setIrpfPercent(null);
			}
		});
		
		irpfPercentDB.addValueChangeHandler(e -> this.mod145.setIrpfPercent(irpfPercentDB.getValue()));
		
		familySituationLB.addChangeHandler(e -> {
			this.mod145.setFamilySituation(Byte.parseByte(familySituationLB.getValue()));
			
			if(AonStringUtils.equalsIgnoreCase(familySituationLB.getValue(), "1")){
				spouseDocumentTB.setVisible(true);
				familySituationLB.getElement().getStyle().setProperty("max-width", "37rem");
			} else {
				familySituationLB.getElement().getStyle().setProperty("max-width", "none");
				spouseDocumentTB.setVisible(false);
				this.mod145.setSpouseDocument(null);
			}
		});
		
		spouseDocumentTB.addValueChangeHandler(e -> this.mod145.setSpouseDocument(spouseDocumentTB.getValue()));
		
		disabilityLevelLB.addChangeHandler(e -> {
			String selectedValue = disabilityLevelLB.getValue();
			if(AonStringUtils.isNotBlank(selectedValue) && AonStringUtils.equalsIgnoreCase(selectedValue, "0"))
				dependenceCB.setVisible(true);
			else {
				dependenceCB.setVisible(false);
				this.mod145.setDependence(false);
			}
			
			this.mod145.setDisabilityLevel(AonStringUtils.isBlank(selectedValue) ? null : Byte.parseByte(selectedValue));
		});
		
		dependenceCB.addValueChangeHandler(e -> this.mod145.setDependence(dependenceCB.getValue()));
		
		labourProlongationCB.addValueChangeHandler(e -> this.mod145.setLabourProlongation(labourProlongationCB.getValue()));
		
		movingDateBx.addValueChangeHandler(e -> this.mod145.setMovingDate(movingDateBx.getValue()));
	}

	private Widget createEconomicCard() {
		AonCustomCard economicCard = new AonCustomCard("Datos Econ\u00f3micos");
		economicCard.addStyleName(AON.CSS.aonContractLargeCard());
		
		FlowPanel table = createFlexColumnPanel();
		
		spousalSupportDB = new AonCustomNumberBox("Pensi\u00f3n compensatoria a favor del c\u00f3nyuge. Importe fijado judicialmente");
		spousalSupportDB.hideNearBy();
		
		foodAnnuityDB = new AonCustomNumberBox("Anualidades por alimentos en favor de los hijos. Importe fijado judicialmente");
		foodAnnuityDB.hideNearBy();
		
		spousalSupportDB.setValue(null == this.mod145 ? null : this.mod145.getSpousalSupport());
		foodAnnuityDB.setValue(null == this.mod145 ? null : this.mod145.getFoodAnnuity());
		
		table.add(createRow(spousalSupportDB, foodAnnuityDB));
		
		deductionHomeLoanCB = new AonCustomCheckBox("Comunicaci\u00f3n de pagos por la adquisici\u00f3n o rehabilitaci\u00f3n de la vivienda habitual utilizando financiaci\u00f3n ajena");

		deductionHomeLoanCB.setValue(null == this.mod145 ? null : this.mod145.isDeductionHomeLoan());
		
		table.add(createRow(deductionHomeLoanCB, null));
		
		economicCard.add(table);
		
		addEconomicHandlers();
		
		return economicCard;
	}

	private void addEconomicHandlers() {
		spousalSupportDB.addValueChangeHandler(e -> this.mod145.setSpousalSupport(spousalSupportDB.getValue()));
		foodAnnuityDB.addValueChangeHandler(e -> this.mod145.setFoodAnnuity(foodAnnuityDB.getValue()));
		deductionHomeLoanCB.addValueChangeHandler(e -> this.mod145.setDeductionHomeLoan(deductionHomeLoanCB.getValue()));
	}

	private Widget createDescendientsCard() {
		AonTableButton newDescendientsB = new AonTableButton(AON.MSG.newAction() + " descendiente",  AON.CSS.aonIconAdd());
		newDescendientsB.addClickHandler(e -> createDescendient());
		
		descendientsCard = new AonCustomCard("Hijos y otros descendientes menores de 25 a\u00f1os, o mayores de dicha edad si son discapacitados, que conviven con el perceptor", newDescendientsB);
		descendientsCard.addStyleName(AON.CSS.aonContractLargeCard());
		
		descendientsTable = new AonCustomTable();
		
		descendientsTable.createHeader();
		for ( DESCENDIENTS_COLS col : DESCENDIENTS_COLS.values()) 
			descendientsTable.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
		
		this.mod145.getDescendients().stream().filter(descendient -> !descendient.isDeleted()).forEach(descendient -> createDescendientRow(descendient));
		
		descendientsCard.add(descendientsTable);
		
		return descendientsCard;
	}

	private void createDescendientRow(IrpfDataDescendients descendient) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton deleteBtn = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		deleteBtn.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteBtn.addClickHandler(e -> {
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
					fillMod145();
				}
			});
		});
		buttonContainer.add(deleteBtn);
		
		HTMLPanel row = descendientsTable.createRow();
		
		HTMLPanel birthDayPanel = new HTMLPanel("");
		TextBox birthDay = new TextBox();
		birthDay.setWidth("5rem");
		birthDay.setValue(null == descendient.getBirthYear() ? "" : descendient.getBirthYear() + "");
		birthDay.addValueChangeHandler(e -> descendient.setBirthYear(Integer.parseInt(e.getValue())));
		birthDayPanel.add(birthDay);
		
		HTMLPanel adoptionPanel = new HTMLPanel("");
		TextBox adoption = new TextBox();
		adoption.setWidth("5rem");
		adoption.setValue(null == descendient.getAdoptionYear() ? "" : descendient.getAdoptionYear() + "");
		adoption.addValueChangeHandler(e -> descendient.setAdoptionYear(Integer.parseInt(e.getValue())));
		adoptionPanel.add(adoption);
		
		HTMLPanel disabilityLevelPanel = new HTMLPanel("");
		ListBox disabilityLevel =  createDisabilityLevelLB();
		setSelectedValueLB(disabilityLevel, descendient.getDisabilityLevel() + "");
		disabilityLevel.addChangeHandler(e -> descendient.setDisabilityLevel(Byte.parseByte(disabilityLevel.getSelectedValue())));
		disabilityLevelPanel.add(disabilityLevel);
		
		HTMLPanel dependencePanel = new HTMLPanel("");
		Button dependence = new Button();
		getEnableDisableButton(dependence, descendient.isDependence());
		dependence.addClickHandler(e -> {
			Boolean oldValue = isActiveToggleButton(dependence);
			Boolean value = !oldValue;
			getEnableDisableButton(dependence, value);
			descendient.setDependence(value);
		});
		dependencePanel.add(dependence);
		
		HTMLPanel uniqueParentPanel = new HTMLPanel("");
		Button uniqueParent = new Button();
		getEnableDisableButton(uniqueParent, descendient.isUniqueParent());
		uniqueParent.addClickHandler(e -> {
			Boolean oldValue = isActiveToggleButton(uniqueParent);
			Boolean value = !oldValue;
			getEnableDisableButton(uniqueParent, value);
			descendient.setUniqueParent(value);
		});
		uniqueParentPanel.add(uniqueParent);
		
		descendientsTable.addRow(row, birthDayPanel, DESCENDIENTS_COLS.BIR.getColWidth());
		descendientsTable.addRow(row, adoptionPanel, DESCENDIENTS_COLS.ADO.getColWidth());
		descendientsTable.addRow(row, disabilityLevelPanel, DESCENDIENTS_COLS.DIS.getColWidth());
		descendientsTable.addRow(row, dependencePanel, DESCENDIENTS_COLS.AYU.getColWidth());
		descendientsTable.addRow(row, uniqueParentPanel, DESCENDIENTS_COLS.COM.getColWidth());
		descendientsTable.addRow(row, buttonContainer, DESCENDIENTS_COLS.BUT.getColWidth());
	}

	private Widget createAscendantsCard() {
		AonTableButton newAscendantsB = new AonTableButton(AON.MSG.newAction() + " ascendente",  AON.CSS.aonIconAdd());
		newAscendantsB.addClickHandler(e -> createAscendant());
		
		ascendantsCard = new AonCustomCard("Ascendientes mayores de 65 a\u00f1os, o menores de dicha edad si son discapacitados, que conviven con el perceptor", newAscendantsB);
		ascendantsCard.addStyleName(AON.CSS.aonContractLargeCard());
		
		ascendantsTable = new AonCustomTable();
		
		ascendantsTable.createHeader();
		for ( ASCENDANTS_COLS col : ASCENDANTS_COLS.values()) 
			ascendantsTable.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
		
		this.mod145.getAscendants().stream().filter(ascendant -> !ascendant.isDeleted()).forEach(ascendant -> createAscendantRow(ascendant));
		
		ascendantsCard.add(ascendantsTable);
		
		return ascendantsCard;
	}
	
	private void createAscendantRow(IrpfDataAscendants ascendant) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton deleteBtn = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		deleteBtn.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteBtn.addClickHandler(e -> {
			AonDialog deleteDialog = new AonDialog("Eliminar Ascendente", new HTML("\u00BFDesea eliminar el ascendente seleccionado\u003F"));
			deleteDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					ascendant.setDeleted(true);
					fillMod145();
				}
			});
		});
		buttonContainer.add(deleteBtn);
		
		HTMLPanel row = ascendantsTable.createRow();
		
		HTMLPanel birthDayPanel = new HTMLPanel("");
		TextBox birthDay = new TextBox();
		birthDay.setWidth("5rem");
		birthDay.setValue(null == ascendant.getBirthYear() ? "" : ascendant.getBirthYear() + "");
		birthDay.addValueChangeHandler(e -> ascendant.setBirthYear(Integer.parseInt(e.getValue())));
		birthDayPanel.add(birthDay);
		
		HTMLPanel disabilityLevelPanel = new HTMLPanel("");
		ListBox disabilityLevel =  createDisabilityLevelLB();
		setSelectedValueLB(disabilityLevel, ascendant.getDisabilityLevel() + "");
		disabilityLevel.addChangeHandler(e -> ascendant.setDisabilityLevel(Byte.parseByte(disabilityLevel.getSelectedValue())));
		disabilityLevelPanel.add(disabilityLevel);
		
		HTMLPanel dependencePanel = new HTMLPanel("");
		Button dependence = new Button();
		getEnableDisableButton(dependence, ascendant.isDependence());
		dependence.addClickHandler(e -> {
			Boolean oldValue = isActiveToggleButton(dependence);
			Boolean value = !oldValue;
			getEnableDisableButton(dependence, value);
			ascendant.setDependence(value);
		});
		dependencePanel.add(dependence);
		
		HTMLPanel anotherDescendientPanel = new HTMLPanel("");
		Button anotherDescendient = new Button();
		getEnableDisableButton(anotherDescendient, ascendant.isAnotherDescendient());
		anotherDescendient.addClickHandler(e -> {
			Boolean oldValue = isActiveToggleButton(anotherDescendient);
			Boolean value = !oldValue;
			getEnableDisableButton(anotherDescendient, value);
			ascendant.setAnotherDescendient(value);
		});
		anotherDescendientPanel.add(anotherDescendient);
		
		ascendantsTable.addRow(row, birthDayPanel, ASCENDANTS_COLS.BIR.getColWidth());
		ascendantsTable.addRow(row, disabilityLevelPanel, ASCENDANTS_COLS.DIS.getColWidth());
		ascendantsTable.addRow(row, dependencePanel, ASCENDANTS_COLS.AYU.getColWidth());
		ascendantsTable.addRow(row, anotherDescendientPanel, ASCENDANTS_COLS.CON.getColWidth());
		ascendantsTable.addRow(row, buttonContainer, ASCENDANTS_COLS.BUT.getColWidth());
	}

	private FlowPanel createFlexColumnPanel() {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName(AON.CSS.aonItemFlex());
        panel.addStyleName(AON.CSS.aonFlexColumn());
        panel.setWidth("100%");
        return panel;
    }
	
	private FlowPanel createRow(Widget widget1, Widget widget2) {
        FlowPanel row = createFlexPanel();
        row.add(widget1);
        if (widget2 != null) {
            row.add(widget2);
        }
        return row;
    }

	private FlowPanel createFlexPanel() {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName(AON.CSS.aonItemFlex());
        panel.setWidth("100%");
        return panel;
    }
	
	// ----------------------------------------------- fillMod145
	
	private void createMod145() {
		this.mod145 = new com.esferalia.aon.occam.api.model.mod145.Mod145()
				.setId(generateId())
				.setDomain(this.domainId)
				.setContract(this.contractId)
				.setIssueDate(new Date())
				.setDeleted(false);
	}
	
	private void createDescendient() {
		if(this.mod145.getDescendients().stream().filter(descendient -> !descendient.isDeleted()).collect(Collectors.toList()).size() < 4) {
			IrpfDataDescendients descendient = new IrpfDataDescendients()
					.setId(generateId())
					.setDomain(this.domainId)
					.setIrpfData(this.mod145.getId())
					.setDeleted(false);
			
			this.mod145.getDescendients().add(descendient);
			checkDescendientCount();
			createDescendientRow(descendient);
		} else
			AonMessagePanel.showWarning(messagePanel, "Descendientes : No se pueden introducir m\u00e1s de cuatro descendientes");
	}

	private void createAscendant() {
		if(this.mod145.getAscendants().stream().filter(ascendant -> !ascendant.isDeleted()).collect(Collectors.toList()).size() < 2) {
			IrpfDataAscendants ascendant = new IrpfDataAscendants()
					.setId(generateId())
					.setDomain(this.domainId)
					.setIrpfData(this.mod145.getId())
					.setDeleted(false);
			
			this.mod145.getAscendants().add(ascendant);
			createAscendantRow(ascendant);
		} else
			AonMessagePanel.showWarning(messagePanel, "Ascendentes : No se pueden introducir m\u00e1s de dos ascendentes");
	}
	
	private Integer generateId() {
		int id = new Random().nextInt();
		return id < 0 ? id : (id * -1);
	}
	
	private void checkDescendientCount() {
		List<IrpfDataDescendients> descendientList = this.mod145.getDescendients().stream().filter(descendient -> !descendient.isDeleted()).collect(Collectors.toList());
		this.mod145.setDescendientCount((byte) descendientList.size());
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
		Date startDateValue = startDate.getValue();
		if(null == startDateValue)
			AonMessagePanel.showError(messagePanel, "Para poder grabar un Mod145 es necesario indicar la fecha de inicio");
		else {
			AonMessagePanel.showLoading(messagePanel, "Guardando Mod145...");
			saveMod145(
					this.mod145, 
					s -> {
						AonMessagePanel.showSuccess(messagePanel, "Mod 145 guardado correctamente");
						setMod145Object(this.domainId, this.contractId);
					},
					f -> AonMessagePanel.showError(messagePanel, "Error Mod 145 : " + f.getMessage()));
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
		AonMessagePanel.showLoading(messagePanel, "Exportando Mod145 PDF...");
		printMod145(mod145, 
			dataURI ->{
				AonMessagePanel.hideMessage(messagePanel);
				showPDF();
				viewer.open(dataURI);
			}, 
			f -> AonMessagePanel.showError(messagePanel, "Error PDF Mod 145 : " + f.getMessage()));
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
	
	// ----------------------------------------------- DataBase.Methods
	
	private void getMod145List(Consumer<List<Mod145>> success, Consumer<Throwable> failure) {
		impl.getMod145List(this.contractId, new AsyncCallback<List<Mod145>>() {
			
			@Override
			public void onSuccess(List<Mod145> mod145ListIn) {
				mod145List = mod145ListIn;
				success.accept(mod145ListIn);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	private void saveMod145(Mod145 mod145, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.saveMod145(mod145, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	private void printMod145(Mod145 mod145, Consumer<String> success, Consumer<Throwable> failure) {
		impl.printMod145(mod145, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String dataURI) {
				success.accept(dataURI);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	private List<Date> getDateList() {
		List<Date> dateList = this.mod145List.stream().map(mod145 -> mod145.getStartDate()).collect(Collectors.toList());
		if(!dateList.isEmpty() && dateList.size() > 1) {
			dateList.sort((o1, o2) -> o1.compareTo(o2));
			Collections.reverse(dateList);
		}
		return dateList;
	}

	private Mod145 getMod145ByDate(Date date) {
		Optional<Mod145> mod145Opt = this.mod145List.stream().filter(mod145 -> mod145.getStartDate().equals(date)).findFirst();
		return mod145Opt.isPresent() ? mod145Opt.get() : null;
	}

	private Mod145 getRecentMod145() {
		List<Date> dateList = getDateList();
		if(dateList.isEmpty()) return null;
		
		return getMod145ByDate(dateList.get(0));
	}
	
}

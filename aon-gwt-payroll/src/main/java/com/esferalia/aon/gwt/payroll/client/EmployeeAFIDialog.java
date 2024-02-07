package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.AFIChanges;
import com.esferalia.aon.gwt.payroll.shared.AFIChanges.AFIChange;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.SettleReason;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.occam.api.model.type.Occupation;
import com.esferalia.aon.occam.api.model.type.QuoteGroup;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public abstract class EmployeeAFIDialog extends AonCustomDialog {

	// ------------------------------------------------- UIBinder

	interface EmployeeAFIDialogUIBinder extends UiBinder<Widget, EmployeeAFIDialog> {}

	private static final EmployeeAFIDialogUIBinder binder = GWT.create(EmployeeAFIDialogUIBinder.class);

	// ------------------------------------------------- UIFileds

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String tabSelectedStyle();

		String tabStyle();

		String tabSelected();
		
		String tabUnSelected();

		String tabIconSelected();

		String deleteButtonUp();

		String datePickerPanel();

		String moreButton();
		
		String sendBtn();
		
		String tabsPanel();
		
		String flex();
	}
	
	@UiField
	ListBox afiTypeLB;

	@UiField
	DeckPanel deckPanel;

	@UiField
	ListBox settleReasonLB;
	
	@UiField
	HTMLPanel settleHolidayData;

	@UiField
	TextBox holidaysTB;
	
	@UiField
	DateBoxEx holidaysEndDB;
	
	@UiField
	Label holidaysTypeTitle;
	
	@UiField
	ListBox holidaysTypeLB;
	
	@UiField
	HTMLPanel tabsPanel;

	@UiField
	ListBox tc2;

	@UiField
	ListBox quoteGroup;

	@UiField
	ListBox ocupation;

	@UiField
	Label partialityCoefL;

	@UiField
	DoubleBox partialityCoef;
	
	@UiField
	SuggestBox cno;
	
	@UiField
	HTMLPanel afiFilePanel;

	@UiField
	Button generationAFITB;

	@UiField
	HTMLPanel buttonsPanel;

	// ------------------------------------------------- Variables

	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();

	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private static final Date TODAY = new Date();
	
	private HTMLPanel tabsDates = new HTMLPanel("");

	private ContractType contractType;
	private SettleReason settleReason;

	private Date contractStartDate;
	private Date contractEndDate;
	private Integer contractId;
	private Integer domainId;
	private Integer workplaceId;

	private String tc2Original;
	private String quoteGroupOriginal;
	private String ocupationOriginal;
	private Double partialityCoefOriginal;
	private String cnoOriginal;

	private ArrayList<Date> dateList;
	private AFIChanges afiChangesMap;

	private Button acceptBtnDialog;
	
	private Date selectedDate;
	
	private Date holidayDate;
	private String saa;
	
	private boolean isComunication = false;
	private boolean isTransform = false;
	private boolean hasSettle = false;
	
	private Map<String, CNO> cnoMap;

	// ------------------------------------------------- Constructor

	protected EmployeeAFIDialog(Date contractStartDate, Date contractEndDate, String tc2, String quoteGroup, String ocupation, Double partialityCoef, String cno,
			Integer contractId, Integer domainId, Integer workplaceId, Date holidayDate, String saa, boolean hasSettle, boolean isTransform, boolean isComunication) {

		setCaption(isComunication ? "Notificaci\u00f3n TGSS (AFI)" : "Datos AFI");

		setWidget(binder.createAndBindUi(this));
		
		this.isComunication = isComunication;
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.isTransform = isTransform;
		this.hasSettle = hasSettle;
		
		getButtonsPanel();
		initToggleButtons();
		
		afiFilePanel.setVisible(!isComunication);
		tabsDates.addStyleName(style.tabsPanel());
		tabsDates.ensureDebugId("tabsDates");

		this.contractType = new ContractType();
		this.settleReason = new SettleReason();

		this.tc2Original = tc2;
		this.quoteGroupOriginal = quoteGroup;
		this.ocupationOriginal = ocupation;
		this.partialityCoefOriginal = partialityCoef;
		this.cnoOriginal = cno;

		this.contractId = contractId;
		this.domainId = domainId;
		this.workplaceId = workplaceId;
		
		this.settleHolidayData.setVisible(false);
		
		this.holidayDate = holidayDate;
		this.saa = saa;

		impl.getEmployeeAFIChanges(contractId, new AsyncCallback<AFIChanges>() {

			@Override
			public void onFailure(Throwable caught) {
				// Not use in this case
			}

			@Override
			public void onSuccess(AFIChanges afiChanges) {
				
				impl.getCNOs(new AsyncCallback<Map<String,CNO>>() {
					
					@Override
					public void onSuccess(Map<String, CNO> cnoMapIn) {
						afiChangesMap = afiChanges;
						cnoMap = cnoMapIn;
						
						initializeCNOSuggest();
						
						dateList = new ArrayList<>();
						dateList.addAll(afiChangesMap.getAFIChanges().keySet());
						Collections.reverse(dateList);
						
						selectedDate = dateList.get(0);
						initView();
						showDialog();
					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});

		// EnsureDebugID para TEST
		this.acceptBtnDialog.ensureDebugId("input_accept");
	}
	
	private void initializeCNOSuggest() {
		List<String> cnoSuggest = new ArrayList<>();
		for(Entry<String, CNO> entry : cnoMap.entrySet())
			cnoSuggest.add(entry.getKey() + " - " + entry.getValue().getTitle());
		
		cnoSuggest.sort((o1, o2) -> o1.compareTo(o2));

		MultiWordSuggestOracle orclCnaes = (MultiWordSuggestOracle) cno.getSuggestOracle();
		orclCnaes.addAll(cnoSuggest);
		orclCnaes.setDefaultSuggestionsFromText(cnoSuggest);
		cno.setAutoSelectEnabled(false);
		cno.getElement().setPropertyString("placeholder", "C\u00f3digo CNO... (Ctrl + espacio para ver sugerencias)");
		
		cno.getValueBox().addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				cno.setText("");
				cno.showSuggestionList();
			} else if(e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
				cno.hideSuggestionList();
		});
		
		cno.addSelectionHandler(e -> {
			String cnoValue = cno.getValue();
			if(!AonStringUtils.isBlank(cnoValue))
				cnoValue = cnoValue.split(" -")[0];
			
			// Do something with cnoValue
			if (AonStringUtils.isBlank(cnoValue))
				afiChangesMap.addAFIChangeByDate(selectedDate, "CNO", null);
			else
				afiChangesMap.addAFIChangeByDate(selectedDate, "CNO", cnoValue);
		});
	}

	// ------------------------------------------------- Constructor Methods

	private void initToggleButtons() {
		getEnableDisableButton(generationAFITB, false);
	}

	private void initView() {
		initListBox();
		initTabs();
		initPeculiaritiesTable();
	}

	// ------------------------------------------------- Initialize View

	private void initListBox() {
		// AFI Type
		this.afiTypeLB.clear();
		this.afiTypeLB.addItem("ALTA", "ALTA");
		this.afiTypeLB.addItem("MOVIMIENTOS CONTRATO", "MOV");
		this.afiTypeLB.addItem("BAJA", "BAJA");
		this.afiTypeLB.addChangeHandler(e -> {
			String afiTypeValue = afiTypeLB.getSelectedValue();
			if(AonStringUtils.equals(afiTypeValue, "ALTA")) showAlta();
			else if(AonStringUtils.equals(afiTypeValue, "BAJA")) showBaja();
			else if(AonStringUtils.equals(afiTypeValue, "MOV")) showMovs();
		});
		this.afiTypeLB.setSelectedIndex(1); // Movs by default
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), afiTypeLB);
		
		// Settle Reason
		this.settleReasonLB.clear();
		for (Entry<Integer, String> settleReasonEntry : this.settleReason.getSettleReasonEntries())
			this.settleReasonLB.addItem(settleReasonEntry.getKey() + " - " + settleReasonEntry.getValue(),
					settleReasonEntry.getKey().toString());
		
		this.holidaysTypeLB.clear();
		this.holidaysTypeLB.addItem("Retribuidas y no disfrutadas", "001");
		this.holidaysTypeLB.addItem("No disfrutadas, retribuidas y cotizadas", "015");

		// TC2
		this.tc2.clear();
		this.tc2.addItem("-", "-1");
		for (Entry<Integer, ContractTypeRecord> entry : contractType.getContractTypes().entrySet())
			this.tc2.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeShortDescription(),
					AonStringUtils.leftPad(entry.getKey().toString(), 3, '0'));

		// Quote Group
		this.quoteGroup.clear();
		QuoteGroup.getQuoteGroup().entrySet()
				.forEach(entry -> this.quoteGroup.addItem(entry.getKey(), entry.getValue()));

		// Ocupation
		this.ocupation.clear();
		Occupation.getOccupation().entrySet()
				.forEach(entry -> this.ocupation.addItem(entry.getKey(), entry.getValue()));
		
		// HasSettle then only sendBaja
		if(Boolean.TRUE.equals(this.hasSettle)) {
			this.afiTypeLB.setSelectedIndex(2);
			if(null != this.holidayDate) {
				holidaysEndDB.setEnabled(false);
				holidaysTB.setEnabled(false);
				holidaysTypeLB.setEnabled(false);
				
				this.settleHolidayData.setVisible(true);
				long days = DateUtils.getDaysBetween(this.contractEndDate, this.holidayDate);
				holidaysEndDB.setValue(this.holidayDate);
				holidaysTB.setValue(days + "");
				
				if(this.contractEndDate.equals(this.holidayDate)) {
					this.holidaysTypeLB.setVisible(false);
					this.holidaysTypeTitle.setVisible(false);
				}
				setSelectedValueLB(this.holidaysTypeLB, this.saa);
			}
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), afiTypeLB);
		}
	}
	
	// ------------------------------------------------- Tabs AFI Movs

	private void initTabs() {
		tabsPanel.clear();
		tabsDates.clear();
		
		// New date
		Button newButton = addMoreButton();
		tabsPanel.add(newButton);

		if (dateList.isEmpty())
			resetListBox();
		else {
			ScrollPanel scrollPanel = new ScrollPanel();
			scrollPanel.setHeight("35px");
			for(Date date : dateList) {
				HTMLPanel hPanel = new HTMLPanel("");
				hPanel.addStyleName(style.flex());

				Label dateL = new Label(formatFullDate.format(date));
				dateL.getElement().getStyle().setPadding(3.00, Unit.PX);
				dateL.addClickHandler(e -> clickButton(date));
				hPanel.add(dateL);
				
				AonTableButton deleteButton = new AonTableButton("Borrar tramo", AON.CSS.aonIconDelete());
				deleteButton.addClickHandler(e -> deletePeriodButton(date));
				hPanel.add(deleteButton);

				if(selectedDate.equals(date)) {
					hPanel.addStyleName(style.tabSelected());
					hPanel.removeStyleName(style.tabUnSelected());
					deleteButton.addStyleName(style.tabIconSelected());
					setWidgetVisible(deleteButton, true);
				} else {
					hPanel.addStyleName(style.tabUnSelected());
					hPanel.removeStyleName(style.tabSelected());
					deleteButton.removeStyleName(style.tabIconSelected());
					setWidgetVisible(deleteButton, false);
				}
				
				if(selectedDate.equals(contractStartDate))
					setWidgetVisible(deleteButton, false);
				
				tabsDates.add(hPanel);
			}
			
			scrollPanel.add(tabsDates);
			tabsPanel.add(scrollPanel);
		}
		
	}

	private void clickButton(Date date) {
		if(!date.equals(selectedDate)) {
			selectedDate = date;
			checkButtonsStyle();
			initPeculiaritiesTable();
		}
	}

	private void checkButtonsStyle() {
		for(int i=0; i < tabsDates.getWidgetCount(); i++) {
			HTMLPanel hPanel = (HTMLPanel) tabsDates.getWidget(i);
			Label dateL = (Label) hPanel.getWidget(0);
			Button deleteButton = (Button) hPanel.getWidget(1);
			
			Date date = formatFullDate.parse(dateL.getText());
			
			if(selectedDate.equals(date)) {
				hPanel.addStyleName(style.tabSelected());
				hPanel.removeStyleName(style.tabUnSelected());
				deleteButton.addStyleName(style.tabIconSelected());
				setWidgetVisible(deleteButton, true);
			} else {
				hPanel.addStyleName(style.tabUnSelected());
				hPanel.removeStyleName(style.tabSelected());
				deleteButton.removeStyleName(style.tabIconSelected());
				setWidgetVisible(deleteButton, false);
			}
			
			if(selectedDate.equals(contractStartDate))
				setWidgetVisible(deleteButton, false);
		}
	}

	private void deletePeriodButton(Date date) {
		ArrayList<AFIChange> afiChangeList = afiChangesMap.getAFIChangessByDate(date);
		try {
			Optional<AFIChange> currectTc2 = afiChangeList.stream().filter(afiChange -> AonStringUtils.equalsIgnoreCase(afiChange.getName(), "TC2")).findFirst();
			if(currectTc2.isPresent()) {
				String tc2Value = currectTc2.get().getValue();
				ContractTypeRecord contractTypeRecord = AonStringUtils.isBlank(tc2Value) ? null : contractType.getContractType(Integer.parseInt(tc2Value));
				if(null != contractTypeRecord && contractTypeRecord.isTransform()) {
					AonDialog dialog = new AonDialog("Eliminar Transformaci\u00f3n", new HTML("La transformaciones de contrato se deben eliminar desde la pesta\u00f1a <b>Datos Sepe</b>"));
					dialog.warning();
					return;
				}
			}
		} catch (Exception e) {
			// Nothing to do
		}
		
		AonConfirmDialog confirmDialog = new AonConfirmDialog();
		confirmDialog.confirm("BORRADO", "\u00BFDesea eliminar este tramo?", new AonConfirmDialogCallback() {

			@Override
			public void onAccept() {
				// Delete strech and update dates
				afiChangesMap.deleteAFIChangeByDate(date);
				dateList.clear();
				dateList.addAll(afiChangesMap.getAFIChanges().keySet());
				dateList.sort((d1, d2) -> d1.compareTo(d2));
				Collections.reverse(dateList);
				selectedDate = dateList.get(0);
				initView();
			}

			@Override
			public void onCancel() {
				// Cancel delete
			}
		});
	}

	private Button addMoreButton() {
		Button moreButton = new Button("+");
		moreButton.addClickHandler(click -> {
			PopupPanel popup = new PopupPanel(true); // auto-hide
			DatePicker picker = new DatePicker();
			picker.setValue(TODAY);
			picker.setYearAndMonthDropdownVisible(true);

			picker.addValueChangeHandler(e -> {
				popup.hide();
				Date newDate = e.getValue();
				if(null != contractEndDate && DateUtils.isAfterOrEquals(newDate, contractEndDate)){
					AonDialog error = new AonDialog("Error fechas", new HTML("No se puede elegir una fecha posterior o igual a la fecha fin de contrato."));
					error.warning();
				} else
					createNewPeriod(newDate);
			});

			popup.setWidget(picker);
			popup.setStyleName(style.datePickerPanel());
			popup.showRelativeTo(moreButton);

			popup.ensureDebugId("morePopupPanel");
			picker.ensureDebugId("moreDatePicker");
		});

		moreButton.addStyleName(style.moreButton());
		moreButton.ensureDebugId("moreButton");

		return moreButton;
	}

	private void createNewPeriod(Date date) {
		if ((date.after(contractStartDate) || date.equals(contractStartDate))) {
			if (!dateList.contains(date)) {
				dateList.add(date);
				dateList.sort((d1, d2) -> d1.compareTo(d2));
				afiChangesMap.addAFIChange(date);
				Collections.reverse(dateList);
				selectedDate = date;
				initView();
			}
		} else {
			AonDialog dialog = new AonDialog("AVISO: Error fecha",
					new HTML("La fecha seleccionada es anterior a la fecha de inicio de contrato ("
							+ formatFullDate.format(contractStartDate)
							+ ")"));
			dialog.warning();
		}
	}

	private void resetListBox() {
		// Set default values
		this.tc2.setSelectedIndex(0);
		this.tc2.setEnabled(false);
		this.quoteGroup.setSelectedIndex(0);
		this.quoteGroup.setEnabled(false);
		this.ocupation.setSelectedIndex(0);
		this.ocupation.setEnabled(false);
		this.partialityCoef.setValue(null);
		this.cno.setValue(null);
	}

	// ------------------------------------------------- Table AFI Movs

	private void initPeculiaritiesTable() {
		unblockListbox();

		if (!dateList.isEmpty()) {
			ArrayList<AFIChange> afiChangeList = afiChangesMap.getAFIChangessByDate(selectedDate);

			tc2.setSelectedIndex(0);
			quoteGroup.setSelectedIndex(0);
			ocupation.setSelectedIndex(0);
			partialityCoef.setValue(null);
			cno.setValue("");
			
			for (AFIChange afiChange : afiChangeList) {
				switch (afiChange.getName()) {
				case "TC2":
					String value = afiChange.getValue();
					setSelectedValueLB(tc2, AonStringUtils.leftPad(value, 3, '0'));
					ContractTypeRecord contractTypeRecord = AonStringUtils.isBlank(value) ? null : contractType.getContractType(Integer.parseInt(value));
					checkPartialityVisibility(contractTypeRecord);
					break;
				case "GRUPO_COTIZACION":
					setSelectedValueLB(quoteGroup, afiChange.getValue());
					break;
				case "OCUPACION":
					setSelectedValueLB(ocupation, afiChange.getValue());
					break;
				case "COEFICIENTE_PARCIALIDAD":
					String valueP = afiChange.getValue();
					partialityCoef.setValue(AonStringUtils.isBlank(valueP) ? null : Double.parseDouble(valueP));
					break;
				case "CNO":
					String cnoCode = afiChange.getValue();
					CNO cnoObj = cnoMap.get(cnoCode);
					if(null != cnoObj)
						cno.setText(cnoObj.getCode() + " - " + cnoObj.getTitle());
					break;
				default:
					break;
				}
			}

		}
	}

	private void checkPartialityVisibility(ContractTypeRecord contractTypeRecord) {
		if(null != contractTypeRecord) {
			if(AonStringUtils.equalsIgnoreCase(contractTypeRecord.getJourneyType(), "P")) {
				partialityCoefL.getElement().getStyle().clearDisplay();
				partialityCoef.getElement().getStyle().clearDisplay();
			} else {
				partialityCoefL.getElement().getStyle().setDisplay(Display.NONE);
				partialityCoef.getElement().getStyle().setDisplay(Display.NONE);
			}
		}
	}

	// ------------------------------------------------- Auxiliar Methods

	private void unblockListbox() {
		this.tc2.setEnabled(true);
		this.quoteGroup.setEnabled(true);
		this.ocupation.setEnabled(true);
		this.partialityCoef.setEnabled(true);
	}

	// ------------------------------------------------- UiHandlers

	@UiHandler("generationAFITB")
	void onGenerationAFITBClick(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(generationAFITB);
		Boolean value = !oldValue;
		getEnableDisableButton(generationAFITB, value);
	}

	@UiHandler("tc2")
	void onTC2Change(ChangeEvent event) {
		if (tc2.getSelectedIndex() == 0)
			afiChangesMap.addAFIChangeByDate(selectedDate, "TC2", null);
		else {
			ContractTypeRecord contractTypeRecord = contractType.getContractType(Integer.parseInt(tc2.getSelectedValue()));
			
			if(contractTypeRecord.isTransform()) {
				ArrayList<AFIChange> afiChangeList = afiChangesMap.getAFIChangessByDate(selectedDate);
				Optional<AFIChange> currectTc2 = afiChangeList.stream().filter(afiChange -> AonStringUtils.equalsIgnoreCase(afiChange.getName(), "TC2")).findFirst();
				if(currectTc2.isPresent()) {
					ContractTypeRecord contractCurrentTypeRecord = contractType.getContractType(Integer.parseInt(currectTc2.get().getValue()));
					if(contractCurrentTypeRecord.isTransform()) {
						checkPartialityVisibility(contractTypeRecord);
						if(!AonStringUtils.equalsIgnoreCase(contractTypeRecord.getJourneyType(), "P"))
							afiChangesMap.addAFIChangeByDate(selectedDate, "COEFICIENTE_PARCIALIDAD", null);
						afiChangesMap.addAFIChangeByDate(selectedDate, "TC2", tc2.getSelectedValue());
					} else {
						AonDialog dialog = new AonDialog("Transformaci\u00f3n", new HTML("La transformaci\u00f3n del contrato debe hacerse desde la pesta\u00f1a <b>Datos Sepe</b>"));
						dialog.warning();
						setSelectedValueLB(tc2, AonStringUtils.leftPad(currectTc2.get().getValue(), 3, '0'));
					}	
				}
			} else {
				checkPartialityVisibility(contractTypeRecord);
				if(!AonStringUtils.equalsIgnoreCase(contractTypeRecord.getJourneyType(), "P"))
					afiChangesMap.addAFIChangeByDate(selectedDate, "COEFICIENTE_PARCIALIDAD", null);
				afiChangesMap.addAFIChangeByDate(selectedDate, "TC2", tc2.getSelectedValue());
			}
		}
	}

	@UiHandler("quoteGroup")
	void onQuoteGroupChange(ChangeEvent event) {
		if (quoteGroup.getSelectedIndex() == 0)
			afiChangesMap.addAFIChangeByDate(selectedDate, "GRUPO_COTIZACION", null);
		else
			afiChangesMap.addAFIChangeByDate(selectedDate, "GRUPO_COTIZACION",
					quoteGroup.getSelectedItemText().split("\\.")[0]);
	}

	@UiHandler("ocupation")
	void onOcupationChange(ChangeEvent event) {
		if (ocupation.getSelectedIndex() == 0)
			afiChangesMap.addAFIChangeByDate(selectedDate, "OCUPACION", null);
		else
			afiChangesMap.addAFIChangeByDate(selectedDate, "OCUPACION",
					ocupation.getSelectedItemText().split("\\.")[0]);
	}

	@UiHandler("partialityCoef")
	void onPartialityCoefValueChange(ValueChangeEvent<Double> event) {
		Double value = event.getValue();
		afiChangesMap.addAFIChangeByDate(selectedDate, "COEFICIENTE_PARCIALIDAD",
				null != value ? value.toString() : null);
	}

	// ------------------------------------------------- Check what to update

	public boolean isStartContract() {
		String afiTypeValue = this.afiTypeLB.getSelectedValue();
		return AonStringUtils.equals(afiTypeValue, "ALTA");
	}

	public boolean isEndContract() {
		String afiTypeValue = this.afiTypeLB.getSelectedValue();
		return AonStringUtils.equals(afiTypeValue, "BAJA");
	}
	
	public boolean isChangeContract() {
//		Window.alert("onChangeContract : " + afiChangesMap.hasChange("TC2", tc2Original));
		return afiChangesMap.hasChange("TC2", tc2Original);
	}

	public boolean isQuoteContract() {
//		Window.alert("onQuoteContract : " + afiChangesMap.hasChange("GRUPO_COTIZACION", quoteGroupOriginal));
		return afiChangesMap.hasChange("GRUPO_COTIZACION", quoteGroupOriginal);
	}

	public boolean isOcupationContract() {
//		Window.alert("onOcupationContract : " + afiChangesMap.hasChange("OCUPACION", ocupationOriginal));
		return afiChangesMap.hasChange("OCUPACION", ocupationOriginal);
	}

	public boolean isPartialityCoefContract() {
//		Window.alert("onPartialityCoefContract : " + afiChangesMap.hasChange("COEFICIENTE_PARCIALIDAD", partialityCoefOriginal == null ? "" : partialityCoefOriginal.toString()));
		return afiChangesMap.hasChange("COEFICIENTE_PARCIALIDAD", partialityCoefOriginal == null ? "" : partialityCoefOriginal.toString());
	}
	
	public boolean isCnoContract() {
//		Window.alert("onPartialityCoefContract : " + afiChangesMap.hasChange("COEFICIENTE_PARCIALIDAD", partialityCoefOriginal == null ? "" : partialityCoefOriginal.toString()));
		return afiChangesMap.hasChange("CNO", cnoOriginal);
	}

	// ------------------------------------------------- ToggleButton

	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);

		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}

	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
	}

	// ------------------------------------------------- Auxiliar Methods

	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	private void showAlta() {
		deckPanel.setVisible(false);
	}
	
	private void showBaja() {
		deckPanel.setVisible(true);
		deckPanel.showWidget(0);
	}

	private void showMovs() {
		deckPanel.setVisible(true);
		deckPanel.showWidget(1);
	}

	private void setWidgetVisible(Widget widget, boolean visible) {
		widget.setVisible(visible);
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

	// ------------------------------------------------- ButtonsPanel

	private void getButtonsPanel() {
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText(AON.MSG.cancelAction());
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		closeBtnDialog.addClickHandler(e -> hide());

		buttonsPanel.add(closeBtnDialog);

		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(this.isComunication ? AON.CSS.aonIconSend() : AON.CSS.aonOkButtonSmall());
		if(this.isComunication) acceptBtnDialog.addStyleName(style.sendBtn());
		acceptBtnDialog.setText(this.isComunication ? "Guardar y comunicar" : AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> onAcceptDialog());

		buttonsPanel.add(acceptBtnDialog);
	}

	private void onAcceptDialog() {
		onAccept(s -> {
			hide();
			
			if (isComunication) {
				if (isStartContract())
					onStartContract();
				if (isEndContract())
					onEndContract(settleReasonLB.getSelectedValue());
				if (isChangeContract()) {
					onChangeContract(afiChangesMap.getChangeValue("TC2"), afiChangesMap.getChangeDate());
					// For transform comunications ¿why?
//					if(!isPartialityCoefContract() && afiChangesMap.getChangeValue("TC2").endsWith("9"))
//						onPartialityCoefContract(afiChangesMap.getChangeValue("COEFICIENTE_PARCIALIDAD"), afiChangesMap.getChangeDate());
				} if (isQuoteContract())
					onQuoteContract(afiChangesMap.getChangeValue("GRUPO_COTIZACION"), afiChangesMap.getChangeDate());
				if (isOcupationContract())
					onOcupationContract(afiChangesMap.getChangeValue("OCUPACION"), afiChangesMap.getChangeDate());
				if (isPartialityCoefContract())
					onPartialityCoefContract(afiChangesMap.getChangeValue("COEFICIENTE_PARCIALIDAD"), afiChangesMap.getChangeDate());
				if (isCnoContract())
					onCnoContract(afiChangesMap.getChangeValue("CNO"), afiChangesMap.getChangeDate());
				
				// Solo para las transformaciones que tienen una pestaña y necesitan comunicar el cambio de tc2
				if (!isChangeContract() && isTransform && dateList != null && dateList.size() == 1)
					onChangeContract(this.tc2Original, afiChangesMap.getChangeDate());
			}
			
			// Solo recargar la informacion del empleado si la fecha de modificacion es
			// anterior o igual al dia actual
			if (DateUtils.isBeforeOrEquals(afiChangesMap.getChangeDate(), new Date()))
				onAcceptCB();
		});
	}

	private void onAccept(Consumer<Void> success) {
		saveAFIChanges(
			s -> {
				if (!isComunication && isGenerationAFI()) {
					
					String fileDownloadURL = GWT.getModuleBaseURL() + "employee_afi/" + "?domainId=" + domainId
							+ "&contractId=" + contractId + "&workplaceId=" + workplaceId + "&isStartContract="
							+ (isStartContract() ? 1 : 0) + "&isEndContract=" + (isEndContract() ? 1 : 0)
							+ "&isChangeContract=" + (isChangeContract() ? 1 : 0) + "&isQuoteContract="
							+ (isQuoteContract() ? 1 : 0) + "&isOcupationContract=" + (isOcupationContract() ? 1 : 0)
							+ "&isPartialityCoefContract=" + (isPartialityCoefContract() ? 1 : 0)
							+ "&isCnoContract=" + (isCnoContract() ? 1 : 0)
							+ "&settleReason=" + settleReasonLB.getSelectedValue();
	
					Window.open(fileDownloadURL, "_blank", null);
				}
				
				success.accept(null);
			}, 
			f -> {});
	}

	private void saveAFIChanges(Consumer<Void> success, Consumer<Throwable> failure) {
		impl.setEmployeeAFIChanges(contractId, afiChangesMap, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}

		});
	}

	public boolean isGenerationAFI() {
		return isActiveToggleButton(generationAFITB);
	}

	// ------------------------------------------------- Abstract Methods

	protected abstract void onAcceptCB();

	protected abstract void onPartialityCoefContract(String partialityCoef, Date date);

	protected abstract void onOcupationContract(String ocupation, Date date);
	
	protected abstract void onCnoContract(String cno, Date date);

	protected abstract void onQuoteContract(String quoteGroup, Date date);

	protected abstract void onChangeContract(String contract, Date date);

	protected abstract void onEndContract(String settleReason);

	protected abstract void onStartContract();

}

package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.CCCType;
import com.esferalia.aon.gwt.payroll.shared.ContractJourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ContractTypeRecord;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ModelRecord;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.Municipalities;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.gwt.payroll.shared.StreetType;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeDraft extends Composite implements ContextMenuHandler {

	private class EmployeeImplementation extends Employee{
		ContractType contractTypeClass = new ContractType();
		
		@Override
		public void onClearEmployeeClick() {}

		@Override
		public void onEmployeeDocumentSuggestionChange() {}

		@Override
		public void onEmployeeDocumentChange() {
			checkValidationDocument();	
		}

		@Override
		public void onEmployeeNationalityChange() {
			String countryIso2 = getIso2(this.nationality.getValue());
			employeeDraftObject.setNationality(countryIso2);
			saving();
		}

		@Override
		public void onEmployeeSSNumSuggestionChange() {}

		@Override
		public void onEmployeeSSNumChange() {
			checkValidationSSNumber();
		}

		@Override
		public void onEmployeeNameSuggestionChange() {}

		@Override
		public void onEmployeeNameChange() {}

		@Override
		public void onEmployeeFirstSurnameSuggestionChange() {}

		@Override
		public void onEmployeeFirstSurnameChange() {}

		@Override
		public void onEmployeeSecondSurnameChange() {}

		@Override
		public void onContractSSRegimenChange() {
			int ssRegime = this.ssRegimeType.getSelectedIndex();
			employeeDraftObject.setSSRegime(ssRegime);
			
			if(1 == ssRegime){
				this.showElementsFreelancerTable();
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.journeyType);
			}
			else
				this.hideElementsFreelancerTable();
			
			saving();
		}

		@Override
		public void onContractActiviesCCCChange() {
			if(0 == this.activityCCC.getSelectedIndex()) {
				cantSaveWithOutIt();
				return;
			}
			
			String activityCCC = this.activityCCC.getSelectedItemText();
			if(activityCCC.equals("-")){
				employeeDraftObject.setContractActivityId(null);
				employeeDraftObject.setContractCCCId(null);
				employeeDraftObject.setContractCCCType((Byte)null);
			}else{
				String activityStr = activityCCC.split(" -")[0];
				String cccStr = activityCCC.split("\\[")[1].split("\\]")[0];
				String cccTypeStr = activityCCC.split("- ")[1].split("\\[")[0];
				String cccGeozoneStr = activityCCC.split("- ")[2];
				int cccTypeInt = -1;
				for(int i=0; i< CCCType.values().length; i++){
					if(CCCType.values()[i].name().equals(cccTypeStr)){
						cccTypeInt = i;
						break;
					}
				}
				byte cccType = (byte) cccTypeInt;
				
				Integer activityId = employeeDraftObject.getActivityIdByName(activityStr);
				Integer cccId = employeeDraftObject.getCCCIdByNumber(cccStr, cccType, cccGeozoneStr);
				employeeDraftObject.setContractActivityId(activityId);
				employeeDraftObject.setContractCCCId(cccId);
				employeeDraftObject.setContractCCCType(cccType);
			}
			saving();
		}

		@Override
		public void onContractWorkplaceChange() {
			String workplaceName = this.workplace.getSelectedItemText();
			Integer workplaceId = employeeDraftObject.getWorkplaceIdByName(workplaceName);
			employeeDraftObject.setContractWorkplaceId(workplaceId);
			saving();
		}

		@Override
		public void onContractTypeChange() {
			if(0 == this.contractType.getSelectedIndex()) {
				Integer contractTypeId = employeeDraftObject.getContractType();
				Integer idx = getContractTypeIdx(contractTypeId);
				this.contractType.setSelectedIndex(1 + idx);
				cantSaveWithOutIt();
				return;
			}
			
			this.modality.clear();
			this.modality.addItem("-");
			Integer contractTypeId = -1;
			if (this.contractType.getSelectedIndex() != 0) {
				String contract_type_id_str = this.contractType.getSelectedItemText().split(" -")[0];
				contractTypeId = Integer.parseInt(contract_type_id_str);
				if((contractTypeId >= 200 && contractTypeId<300) || (contractTypeId >= 500 && contractTypeId<600)) {
					this.showElementsPartialTimeContract();
					this.journeyDuration.addStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
					this.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
					this.journeyDuration.setText("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
				}else{
					this.showElementsFullTimeContract();
					employeeDraftObject.setContractJourneyDuration(new TreeMap<Date, ArrayList<JourneyDuration>>());
				}
			}

			List<ModelRecord> contractTypeModels = contractTypeClass.getModelsContractType(contractTypeId);
			
			for (ModelRecord m : contractTypeModels)
				this.modality.addItem(m.getModelDescription());
						
			if (this.contractType.getSelectedIndex() == 0) {
				employeeDraftObject.setContractType(null);
				employeeDraftObject.setContractModel(null);
			} else {
				String contract_type_id_str = this.contractType.getSelectedItemText().split(" -")[0];
				employeeDraftObject.setContractType(contract_type_id_str);
			}
			saving();
		}

		@Override
		public void onContractModalityChange() {
			if (this.contractType.getSelectedIndex() == 0 || this.modality.getSelectedIndex() == 0) {
				employeeDraftObject.setContractModel(null);
			} else {
				String contract_type_id_str = this.contractType.getSelectedItemText().split(" -")[0];
				Integer contractTypeId = Integer.parseInt(contract_type_id_str);

				String contractModelDescription = modality.getSelectedItemText();
				Integer contractModelEnum = contractTypeClass.getContractModelId(contractTypeId, contractModelDescription);
				employeeDraftObject.setContractModel(contractModelEnum); // GET String of enum in JooqEmployee.java
			}
			saving();
		}

		@Override
		public void onContractStartDateChange() {
			if(null == this.start_date.getValue()){
				this.start_date.setValue(employeeDraftObject.getContractStartDate());
				WarningDialog warning = new WarningDialog("Error", "La fecha de inicio es obligatoria");
				warning.center();
				warning.show();
			}else{
				employeeDraftObject.setContractStartDate(this.start_date.getValue());
				saving();
			}
		}

		@Override
		public void onContractEndDateChange() {
			employeeDraftObject.setContractEndDate(this.end_date.getValue());
			saving();
		}

		@Override
		public void onContractSeniorityDateChange() {
			employeeDraftObject.setContractSeniorityDate(this.seniority_date.getValue());
			if(null == this.start_date.getValue() || null == this.seniority_date.getValue()) {
				this.seniority_dateStatus.setStyleName("aon-finding-toolbar-item aon-icon-info aon-finding-toolbar-item-no-border");
				this.seniority_dateStatus.removeStyleName(style.hide());
				this.seniority_dateStatus.addStyleName(style.marginTop());
				this.seniority_dateStatus.setTitle("La fecha de inicio no coincide con la de antig" + String.valueOf("\u00FC") + "edad.");
			}else {
				Date startDate = this.start_date.getValue();
				DateUtils.resetTime(startDate);
				Date seniorityDate = this.seniority_date.getValue();
				DateUtils.resetTime(seniorityDate);
				
				if(startDate.equals(seniorityDate)) {
					this.seniority_dateStatus.addStyleName(style.hide());
				}else {
					this.seniority_dateStatus.setStyleName("aon-finding-toolbar-item aon-icon-info aon-finding-toolbar-item-no-border");
					this.seniority_dateStatus.removeStyleName(style.hide());
					this.seniority_dateStatus.addStyleName(style.marginTop());
					this.seniority_dateStatus.setTitle("La fecha de inicio no coincide con la de antig" + String.valueOf("\u00FC") + "edad.");
				}
			}
			saving();
		}

		@Override
		public void onContractAgreementChange() {
			this.level.clear();
			String agreementName = this.agreement.getSelectedItemText();
			List<Agreement> agreements = employeeDraftObject.getAgreements();
			this.level.addItem("-");
			for (Agreement a : agreements) {
				if (a.getId() > 0 && a.getDescription() == agreementName) {
					Set<Level> levels = a.getLevels();
					for (Level levelRecord : levels) {
						Set<String> categories = a.getCategoriesMap().get(levelRecord.getId());
						for (String categoryRecord : categories) {
							this.level.addItem(levelRecord.getDescription() + " - " + categoryRecord);
						}
					}
				}
			}
			
			if (this.agreement.getSelectedIndex() == 0) {
				employeeDraftObject.setContractAgreementId(null);
				employeeDraftObject.setContractAgreementLevelId(null);
				this.category.setEnabled(false);
				this.category.setValue("");
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.category);
			} else {
				Integer agreementId = employeeDraftObject.getAgreementId(this.agreement.getSelectedItemText());
				employeeDraftObject.setContractAgreementId(agreementId);
			}
			saving();
		}

		@Override
		public void onContractAgreementLevelChange() {
			if (this.agreement.getSelectedIndex() == 0 || this.level.getSelectedIndex() == 0) {
				employeeDraftObject.setContractAgreementLevelId(null);
				this.category.setEnabled(false);
				this.category.setValue("");
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.category);
			} else {
				Integer agreementLevelId = employeeDraftObject.getAgreementLevelId(this.agreement.getSelectedItemText(),
						this.level.getSelectedItemText());
				employeeDraftObject.setContractAgreementLevelId(agreementLevelId);
				String levelDescription = (this.level.getSelectedItemText() == null
						|| this.level.getSelectedItemText() == "-") ? null
								: this.level.getSelectedItemText().split("- ")[1];
				this.category.setValue(levelDescription);
				employeeDraftObject.setContractCategory(levelDescription);
				this.category.setEnabled(true);
			}
			saving();
		}

		@Override
		public void onContractCategoryChange() {}

		@Override
		public void onContractQuoteGroupChange() {
			if (quote_group.getSelectedIndex() == 0)
				employeeDraftObject.setContractQuoteGroup(null);
			else
				employeeDraftObject.setContractQuoteGroup(this.quote_group.getSelectedIndex());
			saving();
		}

		@Override
		public void onContractOccupationChange() {
			if (this.occupation.getSelectedIndex() == 0)
				employeeDraftObject.setContractOccupation(null);
			else
				employeeDraftObject.setContractOccupation(this.occupation.getSelectedIndex());
			saving();
		}

		@Override
		public void onContractJourneyTypeChange() {
			 Boolean journey_type = (this.journeyType.getSelectedIndex() == 0) ? true : false;
			 employeeDraftObject.setContractJourneyType(journey_type);
			 if(this.journeyType.getSelectedIndex() == 0)
				 employee.showElementsFullTimeContract();
			 else
				 employee.showElementsPartialTimeContract();
			 saving();
		}
		
		@Override
		public void onContractJourneyDurationClick() {
			ContractJourneyDialog dialog = new ContractJourneyDialog(employeeDraftObject.getContractStartDate(), employeeDraftObject.getContractEndDate(),
					employeeDraftObject.getContractJourneyDuration()) {

				@Override
				protected void onSave() {
					ContractJourneyDuration contractJourneyDuration = this.getContractJourneyDuration();
					if(contractJourneyDuration.getJourniesSize() != 0) {
						String result = "Desde ";
						Double hours = 0.0;
						for(JourneyDuration journeyDuration : contractJourneyDuration.getContractJourneyDuration().descendingMap().entrySet().iterator().next().getValue()) {
							hours += Double.parseDouble(((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()));
							if("HORAS_LUNES" == journeyDuration.getName()) result += formatDate(journeyDuration.getStartDate()) + " L : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
							if("HORAS_MARTES" == journeyDuration.getName()) result += ", M : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
							if("HORAS_MIERCOLES" == journeyDuration.getName()) result += ", X : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
							if("HORAS_JUEVES" == journeyDuration.getName()) result += ", J : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
							if("HORAS_VIERNES" == journeyDuration.getName()) result += ", V : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
							if("HORAS_SABADO" == journeyDuration.getName()) result += ", S : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
							if("HORAS_DOMINGO" == journeyDuration.getName()) result += ", D : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ( " + hours + " horas semanales )";
						}
						employee.journeyDuration.setText(result);
						employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
						employee.journeyDuration.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
					}else {
						employee.journeyDuration.addStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
						employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
						employee.journeyDuration.setText("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
					}
					employeeDraftObject.setContractJourneyDuration(contractJourneyDuration.getContractJourneyDuration());
					saving();
				}				
			};
			dialog.center();
			dialog.show();		
		}
		
		//EMPLOYEE TABLE

		@Override
		public void onEmployeeBirthDateChange() {
			employeeDraftObject.setEmployeeBirthDate(this.birth_date.getValue());
			saving();
		}

		@Override
		public void onEmployeeGenderChange() {
			employeeDraftObject.setEmployeeGender(this.gender.getSelectedIndex());
			saving();
		}

		@Override
		public void onEmployeeStreetTypeChange() {
			Integer streetTypeIdx = this.street_type.getSelectedIndex();
			String shortCode = StreetType.values()[streetTypeIdx].getShortCode();
			employeeDraftObject.setEmployeeStreetType(shortCode);
			saving();
		}

		@Override
		public void onEmployeeAddressChange() {}

		@Override
		public void onEmployeeAddressNumChange() {}

		@Override
		public void onEmployeeAddressZipChange() {}

		@Override
		public void onEmployeeAddressMunicipalityChange() {
			employeeDraftObject.setEmployeeAddressCity(municipalities.getZipByMunicipalityName(this.addressMunicipality.getSelectedItemText()).toString());
			saving();
		}

		@Override
		public void onEmployeeAddressProvinceChange() {
			employeeDraftObject.setEmployeeAddressProvince(this.addressProvince.getSelectedItemText());
			updateMunicipalities();
			employeeDraftObject.setEmployeeAddressCity("-1");
			saving();
		}

		@Override
		public void onEmployeeMobileChange() {}

		@Override
		public void onEmployeePhoneChange() {}

		@Override
		public void onEmployeeEmailChange() {}

		@Override
		public void onEmployeePayMethodChange() {
			String methodPay = this.payMethod.getSelectedItemText(); 
			employeeDraftObject.setEmployeePayMethod(methodPay);
			if("TRANSFERENCEIA" != methodPay){
				this.account.setValue(null);
				onEmployeeAccountChange();
				this.bic.setValue(null);
				onEmployeeBICChange();
			}
			saving();
		}

		@Override
		public void onEmployeeBICChange() {}

		@Override
		public void onEmployeeAccountChange() {}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface EmployeeDraftUiBinder extends UiBinder<Widget, EmployeeDraft> {
	
	}
	
	private static EmployeeDraftUiBinder uiBinder = GWT.create(EmployeeDraftUiBinder.class);

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String hide();
	}
	
	@UiField (provided = true)
	Employee employee;
	
	@UiField
	Label saveStatus;
	
	@UiField
	Button afiButton;
	
	@UiField
	Button redoButton;

	@UiField
	Button undoButton;

	@UiField
	Button undoAllButton;
	
	// ------------------------------------------------------ VARIABLES DE LA CLASE -------------------------------------------------

	private EmployeeDraftObject employeeDraftObject;
	public ContractType contractType;
	private Municipalities municipalities;
	
	private Timer saveTimer;
	private Consumer<EmployeeContractInfo> onSaved ;

	// --------------------------------------------------------- CONSTRUCTOR --------------------------------------------------------

	public EmployeeDraft() {
		employee = new EmployeeImplementation();
		
		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		
		//Hide clean employee, only used in New Employee
		employee.clear_employee.addStyleName(employee.style.hide());
		
		//Set save status
		saveStatus.setTitle("Cada cambio que hagas se guarda autom\u00E1ticamente");	
		onSaved = this::onSavedNoop;
	}
	
	public EmployeeDraft setOnSaved(Consumer<EmployeeContractInfo> onSaved) {
		this.onSaved = onSaved;
		return this;
	}
	
	// ------------------------------------------------------- UiHandlers --------------------------------------------------------

	@UiHandler("afiButton")
	void onAFIButtonClick(ClickEvent event) {
		
		EmployeeAFIDialog dialog = new EmployeeAFIDialog(
				this.employee.start_date.getValue(),
				this.employee.end_date.getValue(),
				this.employee.contractType.getSelectedIndex(),
				this.employee.quote_group.getSelectedIndex(),
				this.employee.occupation.getSelectedIndex(),
				this.employeeDraftObject.getPayrollDate()
				){

			@Override
			protected void onAccept() {
				employeeDraftObject.saveAFIChanges(
						getNewDate(),
						isChangeContract(), isChangeContract() ? employee.contractType.getValue(getTC2Idx()).split(" -")[0] : null,
						isQuoteContract(), isQuoteContract() ? getQuoteGroupdx() : null,
						isOcupationContract(), isOcupationContract() ? employeeDraftObject.getOcupationByIndex(getOcupationIdx()) : null,
						s -> {
							
							if(isGenerationAFI()) {

								String fileDownloadURL = GWT.getModuleBaseURL()+ "/employee_afi/"
										+ "?domainId=" + employeeDraftObject.getDomainId()
										+ "&contractId=" + employeeDraftObject.getContractId()
							            + "&workplaceId=" + employeeDraftObject.getWorkplaceId()
							            + "&isStartContract=" + (isStartContract() ? 1 : 0)
							            + "&isEndContract=" + (isEndContract() ? 1 : 0)
							            + "&isChangeContract=" + (isChangeContract() ? 1 : 0)
							            + "&isQuoteContract=" + (isQuoteContract() ? 1 : 0)
							            + "&isOcupationContract=" + (isOcupationContract() ? 1 : 0)
								        ;
								
								Window.open(fileDownloadURL, "_blank", null);
								
							}
							
							setEmployeeDraftObject(employeeDraftObject);
							
						},
						f -> {}
				);
			}
		};
			
		dialog.center();
		dialog.show();
		
	}
	
	@UiHandler("undoButton")
	void onUndoButtonClick(ClickEvent event) {
		employeeDraftObject.undo();
		initializeView();
		saving();
	}

	@UiHandler("undoAllButton")
	void onUndoAllButtonClick(ClickEvent event) {
		while ( employeeDraftObject.canUndo() )
			employeeDraftObject.undo();
		initializeView();
		saving();
	}

	@UiHandler("redoButton")
	void onRedoButtonClick(ClickEvent event) {
		employeeDraftObject.redo();
		initializeView();
		saving();
	}
	
	private void save() {
		saveStatus.setText("Guardando...");
		employeeDraftObject.updateEmployee(
				r -> { 
					saved();
				}, 
				t -> {
					saveStatus.setText("Error, los cambios no se han guardado");
				}
		);
	}
		
	// --------------------------------------------------- METODOS DE LA CLASE ----------------------------------------------------

	public void setEmployeeDraftObject(EmployeeDraftObject employeeDraftObject) {
		this.employeeDraftObject = employeeDraftObject;
		this.contractType = new ContractType();
		this.municipalities = new Municipalities();
		
		this.employeeDraftObject.initializeEmployee(
			r -> {
					initializeView();
					initializeUndoRedo();
					initializeScheduler();
			}, t -> {}
		);
	}
	
	private void initializeScheduler() {
		saveStatus.setText("");
		saveTimer = new Timer() {
			@Override
			public void run() {
				save();
			}
		};	
	}
	
	private void initializeUndoRedo() {
		undoButton.setEnabled(employeeDraftObject.canUndo());
		undoAllButton.setEnabled(employeeDraftObject.canUndo());
		redoButton.setEnabled(employeeDraftObject.canRedo());

		employeeDraftObject.addUndoManagerListener( (undoManager) -> {
			undoButton.setEnabled(undoManager.canUndo());
			undoAllButton.setEnabled(undoManager.canUndo());
			redoButton.setEnabled(undoManager.canRedo());
		});
	}

	private void initializeView() {
		resetElements();
		initActivitiesCCC();
		initWorkplaces();
		initContractType();
		initAgreements();
		initHandlers();
		
		if (employeeDraftObject.getContractSSRegimen() == 3) {
			afiButton.addStyleName(style.hide());
			this.employee.showElementsFreelancerTable();
			fillContractFreelancerTable();
		} else {
			afiButton.removeStyleName(style.hide());
			this.employee.hideElementsFreelancerTable();
			fillContractTable();
		}
		
		if(employeeDraftObject.hasPayroll()){
			this.employee.ssRegimeType.setEnabled(false);
			this.employee.activityCCC.setEnabled(false);
			this.employee.workplace.setEnabled(false);
			this.employee.contractType.setEnabled(false);
			this.employee.modality.setEnabled(false);
			this.employee.start_date.setEnabled(false);
			this.employee.quote_group.setEnabled(false);
			this.employee.occupation.setEnabled(false);
		}else{
			this.employee.ssRegimeType.setEnabled(true);
			this.employee.activityCCC.setEnabled(true);
			this.employee.workplace.setEnabled(true);
			this.employee.contractType.setEnabled(true);
			this.employee.modality.setEnabled(true);
			this.employee.start_date.setEnabled(true);
			this.employee.quote_group.setEnabled(true);
			this.employee.occupation.setEnabled(true);
		}
		
		fillEmployeeTable();
	}

	private void initHandlers() {
		employee.document.addKeyUpHandler(e-> {
			
			String value = employee.document.getValue();
			String saved = employeeDraftObject.getEmployeeDocument();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			employeeDraftObject.setEmployeeDocument(value);
			saving();
		});
		
		employee.security_social_num.addKeyUpHandler(e-> {
			
			String value = employee.security_social_num.getValue();
			String saved = employeeDraftObject.getEmployeeSSNumber();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			employeeDraftObject.setEmployeeSocialSecurityNum(value);
			saving();
		});
		
		employee.name.addKeyUpHandler(e-> {
			
			String value = employee.name.getValue();
			String saved = employeeDraftObject.getEmployeeName();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			if(value.length() == 0) {
				cantSaveWithOutIt();
				return;
			}
			
			employeeDraftObject.setEmployeeName(value);
			saving();
		});
		
		employee.first_surname.addKeyUpHandler(e-> {
			
			String value = employee.first_surname.getValue();
			String saved = employeeDraftObject.getEmployeeSurname();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			employeeDraftObject.setEmployeeFirstSurname(value);
			saving();
		});
		
		employee.second_surname.addKeyUpHandler(e-> {
			
			String value = employee.second_surname.getValue();
			String saved = employeeDraftObject.getEmployeeSecondSurname();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			employeeDraftObject.setEmployeeSecondSurname(value);
			saving();
		});
		
		employee.category.addKeyUpHandler(e-> {
			
			String value = employee.category.getValue();
			String saved = employeeDraftObject.getContractAgreementCategory();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			employeeDraftObject.setContractCategory(value);
			saving();
		});
		
		employee.address.addKeyUpHandler(e-> {
			
			String value = employee.address.getValue();
			String saved = employeeDraftObject.getEmployeeAddress();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			employeeDraftObject.setEmployeeAddress(value);
			saving();
		});
	
		employee.addressNum.addKeyUpHandler(e-> {
			
			String value = employee.addressNum.getValue();
			String saved = employeeDraftObject.getEmployeeAddressNumber();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			employeeDraftObject.setEmployeeAddressNumber(value);
			saving();
		});
		
		employee.addressZip.addKeyUpHandler(e-> {
			
			String value = employee.addressZip.getValue();
			String saved = employeeDraftObject.getEmployeeAddressZip();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			employeeDraftObject.setEmployeeAddressZip(value);
			saving();
		});
		
		employee.mobile.addKeyUpHandler(e-> {
			
			String value = employee.mobile.getValue();
			String saved = employeeDraftObject.getEmployeeMobile();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			employeeDraftObject.setEmployeeMobile(value);
			saving();
		});
		
		employee.phone.addKeyUpHandler(e-> {
			
			String value = employee.phone.getValue();
			String saved = employeeDraftObject.getEmployeePhone();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			employeeDraftObject.setEmployeePhone(value);
			saving();
		});
		
		employee.email.addKeyUpHandler(e-> {
			
			String value = employee.email.getValue();
			String saved = employeeDraftObject.getEmployeeEmail();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			employeeDraftObject.setEmployeeEmail(value);
			saving();
		});
		
		employee.bic.addKeyUpHandler(e-> {
			
			String value = employee.bic.getValue();
			String saved = employeeDraftObject.getEmployeeBIC();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			employeeDraftObject.setEmployeeBIC(value);
			saving();
		});
		
		employee.account.addKeyUpHandler(e-> {
			
			String value = employee.account.getValue();
			String saved = employeeDraftObject.getEmployeeAccount();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			employeeDraftObject.setEmployeeAccount(value);
			saving();
		});
		
	}

	private void resetElements() {
		this.employee.activityCCC.clear();
		this.employee.workplace.clear();
		this.employee.contractType.clear();
		this.employee.agreement.clear();
	}

	private void initActivitiesCCC() {
		//ACTIVITY - CCC
		this.employee.activityCCC.addItem("-");
		if(null != this.employeeDraftObject.getActivities())
			for(Entry<Integer,String> entry : this.employeeDraftObject.getActivities().entrySet())
				for(CCCInfo cccInfo :  this.employeeDraftObject.getCCCs().values())
					if(cccInfo.getActivityId() == entry.getKey()) 
						this.employee.activityCCC.addItem(entry.getValue() + " - " + CCCType.values()[cccInfo.getType()] + "[" + cccInfo.getCcc() + "] - " +  cccInfo.getGeozone());
	}
	
	private void initWorkplaces() {
		//WORKPLACE
		for(Workplace workplace : employeeDraftObject.getWorkplaces())
			this.employee.workplace.addItem(workplace.getDescription());
	}
	
	private void initContractType() {
		// TIPO DE CONTRATO
		this.employee.contractType.addItem("-");
		for (Entry<Integer, ContractTypeRecord> entry : contractType.getContractTypes().entrySet())
			this.employee.contractType.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription());	
	}
	
	private void initAgreements() {
		// CONVENIO
		this.employee.agreement.addItem("-");
		List<Agreement> agreements = employeeDraftObject.getActiveAgreements();
		for (Agreement a : agreements)
			this.employee.agreement.addItem(a.getDescription());
	}

	private void fillContractFreelancerTable() {
		//Documents
		this.employee.document.setValue(employeeDraftObject.getEmployeeDocument());
		checkValidationDocument();
		this.employee.nationality.setValue(employeeDraftObject.getEmployeeNationality());
		this.employee.security_social_num.setValue(employeeDraftObject.getEmployeeSSNumber());
		checkValidationSSNumber();
		//Fullname
		this.employee.name.setValue(employeeDraftObject.getEmployeeName());
		this.employee.first_surname.setValue(employeeDraftObject.getEmployeeSurname());
		this.employee.second_surname.setValue(employeeDraftObject.getEmployeeSecondSurname());
		//Contract
		this.employee.ssRegimeType.setSelectedIndex(1);
		this.employee.workplace.setSelectedIndex(employeeDraftObject.getContractWorkplace());
		this.employee.start_date.setValue(employeeDraftObject.getContractStartDate());
		this.employee.seniority_date.setValue(employeeDraftObject.getContractSeniorityDate());
		checkValidationSeniorityDate();
		this.employee.end_date.setValue(employeeDraftObject.getContractEndDate());
		this.employee.agreement.setSelectedIndex(employeeDraftObject.getContractAgreement() + 1);
		getAgreementLevels(employeeDraftObject.getContractAgreementId());
		this.employee.level.setSelectedIndex(employeeDraftObject.getAgreementLevel() + 1);
		this.employee.category.setValue(employeeDraftObject.getContractAgreementCategory());
		this.employee.journeyType.setSelectedIndex(employeeDraftObject.getContractJourneyType());
		//Contract Partial Journey
		if(1 == employeeDraftObject.getContractJourneyType()) {
			this.employee.showElementsPartialTimeContract();
			ContractJourneyDuration contractJourneyDuration = employeeDraftObject.getContractJourneyDuration();
			if(contractJourneyDuration.getJourniesSize() != 0) {
				String result = "Desde ";
				Double hours = 0.0;
				for(JourneyDuration journeyDuration : contractJourneyDuration.getContractJourneyDuration().descendingMap().entrySet().iterator().next().getValue()) {
					hours += Double.parseDouble(((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()));
					if("HORAS_LUNES" == journeyDuration.getName()) result += formatDate(journeyDuration.getStartDate()) + " L : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
					if("HORAS_MARTES" == journeyDuration.getName()) result += ", M : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
					if("HORAS_MIERCOLES" == journeyDuration.getName()) result += ", X : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
					if("HORAS_JUEVES" == journeyDuration.getName()) result += ", J : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
					if("HORAS_VIERNES" == journeyDuration.getName()) result += ", V : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
					if("HORAS_SABADO" == journeyDuration.getName()) result += ", S : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
					if("HORAS_DOMINGO" == journeyDuration.getName()) result += ", D : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ( " + hours + " horas semanales )";
				}
				employee.journeyDuration.setText(result);
				employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
				employee.journeyDuration.removeStyleName("aon-icon-exception aon-finding-toolbar-item-no-border");
			}else {
				employee.journeyDuration.addStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
				employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
				employee.journeyDuration.setText("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
			}
		}else {
			this.employee.showElementsFullTimeContract();
		}
	}

	private void fillContractTable() {
		//Documents
		this.employee.document.setValue(employeeDraftObject.getEmployeeDocument());
		checkValidationDocument();
		this.employee.nationality.setValue(employeeDraftObject.getEmployeeNationality());
		this.employee.security_social_num.setValue(employeeDraftObject.getEmployeeSSNumber());
		checkValidationSSNumber();
		//Fullname
		this.employee.name.setValue(employeeDraftObject.getEmployeeName());
		this.employee.first_surname.setValue(employeeDraftObject.getEmployeeSurname());
		this.employee.second_surname.setValue(employeeDraftObject.getEmployeeSecondSurname());
		//Contract
		this.employee.ssRegimeType.setSelectedIndex(employeeDraftObject.getContractSSRegimen());
		this.employee.activityCCC.setSelectedIndex(employeeDraftObject.getContractActivityCCC());
		this.employee.workplace.setSelectedIndex(employeeDraftObject.getContractWorkplace());
		//Partial Journey
		Integer contractTypeId = employeeDraftObject.getContractType();
		this.employee.contractType.setSelectedIndex(contractType.getContractTypeIndex(contractTypeId) + 1);
		if((contractTypeId >= 200 && contractTypeId<300) || (contractTypeId >= 500 && contractTypeId<600)) {
			this.employee.showElementsPartialTimeContract();
			ContractJourneyDuration contractJourneyDuration = employeeDraftObject.getContractJourneyDuration();
			if(contractJourneyDuration.getJourniesSize() != 0) {
				String result = "Desde ";
				Double hours = 0.0;
				for(JourneyDuration journeyDuration : contractJourneyDuration.getContractJourneyDuration().descendingMap().entrySet().iterator().next().getValue()) {
					hours += Double.parseDouble(((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()));
					if("HORAS_LUNES" == journeyDuration.getName()) result += formatDate(journeyDuration.getStartDate()) + " L : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
					if("HORAS_MARTES" == journeyDuration.getName()) result += ", M : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
					if("HORAS_MIERCOLES" == journeyDuration.getName()) result += ", X : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
					if("HORAS_JUEVES" == journeyDuration.getName()) result += ", J : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
					if("HORAS_VIERNES" == journeyDuration.getName()) result += ", V : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
					if("HORAS_SABADO" == journeyDuration.getName()) result += ", S : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
					if("HORAS_DOMINGO" == journeyDuration.getName()) result += ", D : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ( " + hours + " horas semanales )";
				}
				employee.journeyDuration.setText(result);
				employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
				employee.journeyDuration.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
			}else {
				employee.journeyDuration.addStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
				employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
				employee.journeyDuration.setText("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
			}
		}else
			this.employee.showElementsFullTimeContract();
		
		this.employee.modality.clear();
		this.employee.modality.addItem("-");
		
		List<ModelRecord> contractTypeModels = EmployeeDraft.this.contractType.getModelsContractType(employeeDraftObject.getContractType());
		
		for (ModelRecord m : contractTypeModels)
			this.employee.modality.addItem(m.getModelDescription());
		
		Integer modelIndex = this.contractType.getContractModelIndex(employeeDraftObject.getContractType(), employeeDraftObject.getContractModel());
		if (null == modelIndex) {
			this.employee.modality.setSelectedIndex(0);
		} else {
			this.employee.modality.setSelectedIndex(modelIndex + 1);
		}
		
		this.employee.start_date.setValue(employeeDraftObject.getContractStartDate());
		this.employee.seniority_date.setValue(employeeDraftObject.getContractSeniorityDate());
		checkValidationSeniorityDate();
		this.employee.end_date.setValue(employeeDraftObject.getContractEndDate());
		
		this.employee.agreement.setSelectedIndex(employeeDraftObject.getContractAgreement() + 1);
		getAgreementLevels(employeeDraftObject.getContractAgreementId());
		this.employee.level.setSelectedIndex(employeeDraftObject.getAgreementLevel() + 1);
		this.employee.category.setValue(employeeDraftObject.getContractAgreementCategory());
		
		this.employee.quote_group.setSelectedIndex(employeeDraftObject.getContractQuoteGroup());
		this.employee.occupation.setSelectedIndex(employeeDraftObject.getContractOcupation());
	}
	
	private void getAgreementLevels(Integer agreementId) {
		this.employee.level.clear();
		List<Agreement> agreements = employeeDraftObject.getActiveAgreements();
		this.employee.level.addItem("-");
		for (Agreement a : agreements) {
			if (a.getId() > 0 && a.getId().equals(agreementId)) {
				Set<Level> levels = a.getLevels();
				for (Level levelRecord : levels) {
					Set<String> categories = a.getCategoriesMap().get(levelRecord.getId());
					for (String categoryRecord : categories) {
						this.employee.level.addItem(levelRecord.getDescription() + " - " + categoryRecord);
					}
				}
			}
		}
	}

	private void fillEmployeeTable() {
		this.employee.birth_date.setValue(employeeDraftObject.getEmployeeBirthDate());
		this.employee.gender.setSelectedIndex(employeeDraftObject.getEmployeeGender());
		this.employee.street_type.setSelectedIndex(employeeDraftObject.getEmployeeAddressStreetTypeIndex());
		this.employee.address.setValue(employeeDraftObject.getEmployeeAddress());
		this.employee.addressNum.setValue(employeeDraftObject.getEmployeeAddressNumber());
		this.employee.addressZip.setValue(employeeDraftObject.getEmployeeAddressZip());
		//		this.employee.addressCity.setValue(employeeDraftObject.getEmployeeAddressCity());
		this.employee.addressProvince.setSelectedIndex( ProvinceContract.getProvinceIndex(employeeDraftObject.getEmployeeAddressProvince()));
		updateMunicipalities();
		this.employee.addressMunicipality.setSelectedIndex(municipalities.getMunicipalityIndex(ProvinceContract.getProvinceCode(employeeDraftObject.getEmployeeAddressProvince()), employeeDraftObject.getEmployeeAddressCity())+1);
		this.employee.mobile.setValue(employeeDraftObject.getEmployeeMobile());
		this.employee.phone.setValue(employeeDraftObject.getEmployeePhone());
		this.employee.email.setValue(employeeDraftObject.getEmployeeEmail());
		this.employee.payMethod.setSelectedIndex(employeeDraftObject.getEmployeePayMethod());
		this.employee.bic.setValue(employeeDraftObject.getEmployeeBIC());
		this.employee.account.setValue(employeeDraftObject.getEmployeeAccount());
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// TODO Auto-generated method stub
	}

	// --------------------------------------------------- CHECK DOCUMENT TYPE -------------------------------------------------------

	private void checkValidationSeniorityDate() {
		if(null == employee.start_date.getValue() || null == employee.seniority_date.getValue()) {
			employee.seniority_dateStatus.setStyleName("aon-finding-toolbar-item aon-icon-info aon-finding-toolbar-item-no-border");
			employee.seniority_dateStatus.removeStyleName(employee.style.hide());
			employee.seniority_dateStatus.addStyleName(employee.style.marginTop());
			employee.seniority_dateStatus.setTitle("La fecha de inicio no coincide con la de antig" + String.valueOf("\u00FC") + "edad.");
		}else {
			Date startDate = employee.start_date.getValue();
			DateUtils.resetTime(startDate);
			Date seniorityDate = employee.seniority_date.getValue();
			DateUtils.resetTime(seniorityDate);
			
			if(startDate.equals(seniorityDate)) {
				employee.seniority_dateStatus.addStyleName(employee.style.hide());
			}else {
				employee.seniority_dateStatus.setStyleName("aon-finding-toolbar-item aon-icon-info aon-finding-toolbar-item-no-border");
				employee.seniority_dateStatus.removeStyleName(employee.style.hide());
				employee.seniority_dateStatus.addStyleName(employee.style.marginTop());
				employee.seniority_dateStatus.setTitle("La fecha de inicio no coincide con la de antig" + String.valueOf("\u00FC") + "edad.");
			}
		}
	}

	private void checkValidationSSNumber() {
		String ssNum = employee.security_social_num.getValue();
		if(employeeDraftObject.checkSSNumValidation(ssNum)) {
			employee.ssNumberStatus.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
			employee.ssNumberStatus.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
		}else {
			employee.ssNumberStatus.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
			employee.ssNumberStatus.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
		}
	}

	private void checkValidationDocument() {
		String document = employee.document.getValue();
		String document_type = checkDocumentType(document);
		employee.document_type.setText(document_type);
		
		if(employeeDraftObject.checkDocumentValidation(document_type, document)) {
			employee.documentStatus.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
			employee.documentStatus.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
		}else {
			employee.documentStatus.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
			employee.documentStatus.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
		}

		showNationality(document_type);		
	}
	
	public String checkDocumentType(String document) {
		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");
		RegExp niePattern = RegExp.compile("[A-Z]{1}\\d{7}[A-Z]{1}");
		RegExp cifPattern = RegExp.compile("[A-Z]{1}\\d{8}");

		if (dniPattern.test(document.toUpperCase()))
			return "DNI";
		else if (niePattern.test(document.toUpperCase()))
			return "NIE";
		else if (cifPattern.test(document.toUpperCase()))
			return "CIF";
		else
			return "Pasaporte";
	}
	
	private void showNationality(String document_type_str) {
		if (document_type_str == "CIF" || document_type_str == "Pasaporte" || document_type_str == "NIE") {
			this.employee.nationalityLabelCell.getStyle().clearDisplay();
			this.employee.nationalityCell.getStyle().clearDisplay();
		} else {
			this.employee.nationalityLabelCell.getStyle().setDisplay(Display.NONE);
			this.employee.nationalityCell.getStyle().setDisplay(Display.NONE);
			this.employee.nationality.setValue("ESPA\u00D1A");
		}
	}
	
	private String getIso2(String country) {
		for (int i = 0; i < Country.values().length; i++) {
			if (Country.values()[i].getName() == country)
				return Country.values()[i].getIso2();
		}
		return null;
	}

	private String formatDate(Date date) {
		return date.getDate() + "/" + (date.getMonth()+1) + "/" + (date.getYear()+1900);
	}
	
	private void saving() {
		saveStatus.setText("Guardando...");
		saveTimer.schedule(2500);
	}
	
	private void saved() {
		saveStatus.setText("Todos los cambios guardados");	
		onSaved.accept(employeeDraftObject.getEmployeeContractInfo());
	}

	protected void onSavedNoop(EmployeeContractInfo employeeContractInfo) {
		
	}
	
	private void cantSaveWithOutIt() {
		WarningDialog warning = new WarningDialog("AVISO", "No se puede dejar este campo sin valor.");
		warning.center();
		warning.show();
	}
	
	public Integer getContractTypeIdx(Integer contractTypeId) {
		return contractType.getContractTypeIndex(contractTypeId);
	}
	
	public void updateMunicipalities() {
		String provinceCode = ProvinceContract.getProvinceCode(employee.addressProvince.getSelectedItemText());
		employee.addressMunicipality.clear();
		employee.addressMunicipality.addItem("-");;
		ArrayList<String> municipalitiesOfProvince = municipalities.getMunicipalitiesByProvinceCode(provinceCode);
		municipalitiesOfProvince.forEach(m -> {employee.addressMunicipality.addItem(m);});
	}

}

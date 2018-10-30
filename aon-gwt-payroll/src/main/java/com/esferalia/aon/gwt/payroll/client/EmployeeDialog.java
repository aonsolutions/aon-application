package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.CCCType;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ContractTypeRecord;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ModelRecord;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.gwt.payroll.shared.StreetType;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeDialog extends CustomDialog {
	
	private class EmployeeImplementation extends Employee{
		ContractType contractTypeClass = new ContractType();
		
		// TABLA DATOS CONTRATO
		
		@Override
		public void onEmployeeDocumentSuggestionChange() {
			String document = this.document.getValue();
			EmployeeInfo employeeData = employeeDialogObject.getEmployeeDataByDocument(document);
			if(null == employeeData.getEmployeeId())
				employeeDialogObject.setEmployeeDocument(document);
			else
				fillExistingEmployee(employeeData);	
		}
		
		@Override
		public void onEmployeeDocumentChange() {
			String document = this.document.getValue();
			String document_type = checkDocumentType(document);
			
			this.document_type.setText(document_type);
			if(employeeDialogObject.checkDocumentValidation(document_type, document)) {
				this.document.removeStyleName(this.style.warning());
				employeeDialogObject.setEmployeeDocument(this.document.getValue());
				employeeDialogObject.setEmployeeDocumentType(document_type);
			}else
				this.document.addStyleName(this.style.warning());

			showNationality(document_type);				
		}
		
		@Override
		public void onEmployeeNationalityChange() {
			String countryIso2 = getIso2(this.nationality.getValue());
			employeeDialogObject.setNationality(countryIso2);
		}
		
		@Override
		public void onEmployeeSSNumSuggestionChange() {
			String ssNum = this.security_social_num.getValue();
			EmployeeInfo employeeData = employeeDialogObject.getEmployeeDataBySSNum(ssNum);
			if(null == employeeData.getEmployeeId())
				employeeDialogObject.setEmployeeSocialSecurityNum(ssNum);
			else
				fillExistingEmployee(employeeData);	
		}

		@Override
		public void onEmployeeSSNumChange() {
			String ssNum = this.security_social_num.getValue();
			if(employeeDialogObject.checkSSNumValidation(ssNum)) {
				employeeDialogObject.setEmployeeSocialSecurityNum(ssNum);
				security_social_num.removeStyleName(style.warning());
			}else
				security_social_num.addStyleName(style.warning());
		}
		
		@Override
		public void onEmployeeNameSuggestionChange() {
			String name = this.name.getValue();
			EmployeeInfo employeeData = employeeDialogObject.getEmployeeDataByName(name);
			if(null == employeeData.getEmployeeId())
				employeeDialogObject.setEmployeeName(name);
			else
				fillExistingEmployee(employeeData);				
		}
		
		@Override
		public void onEmployeeNameChange() {
			employeeDialogObject.setEmployeeName(this.name.getValue());	
		}

		@Override
		public void onEmployeeFirstSurnameSuggestionChange() {
			String surName = this.first_surname.getValue();
			EmployeeInfo employeeData = employeeDialogObject.getEmployeeDataBySurName(surName);
			if(null == employeeData.getEmployeeId())
				employeeDialogObject.setEmployeeFirstSurname(surName);
			else
				fillExistingEmployee(employeeData);	
		}
		
		@Override
		public void onEmployeeFirstSurnameChange() {
			employeeDialogObject.setEmployeeFirstSurname(this.first_surname.getValue());
		}

		@Override
		public void onEmployeeSecondSurnameChange() {
			employeeDialogObject.setEmployeeSecondSurname(this.second_surname.getValue());
		}

		@Override
		public void onContractSSRegimenChange() {
			int ssRegime = this.ssRegimeType.getSelectedIndex();
			employeeDialogObject.setSSRegime(ssRegime);
			
			if(1 == ssRegime)
				this.showElementsFreelancerTable();
			else
				this.hideElementsFreelancerTable();
		}
		
		@Override
		public void onContractActiviesCCCChange() {
			String activityCCC = this.activityCCC.getSelectedItemText();
			if(activityCCC.equals("-")){
				employeeDialogObject.setContractActivityId(null);
				employeeDialogObject.setContractCCCId(null);
				employeeDialogObject.setContractCCCType((Byte)null);
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
				
				Integer activityId = employeeDialogObject.getActivityIdByName(activityStr);
				Integer cccId = employeeDialogObject.getCCCIdByNumber(cccStr, cccType, cccGeozoneStr);
				employeeDialogObject.setContractActivityId(activityId);
				employeeDialogObject.setContractCCCId(cccId);
				employeeDialogObject.setContractCCCType(cccType);
			}
		}
		
		@Override
		public void onContractWorkplaceChange() {
			String workplaceName = this.workplace.getSelectedItemText();
			Integer workplaceId = employeeDialogObject.getWorkplaceIdByName(workplaceName);
			employeeDialogObject.setContractWorkplaceId(workplaceId);
		}

		@Override
		public void onContractTypeChange() {
			this.modality.clear();
			this.modality.addItem("-");
			Integer contractTypeId = -1;
			if (this.contractType.getSelectedIndex() != 0) {
				String contract_type_id_str = this.contractType.getSelectedItemText().split(" -")[0];
				contractTypeId = Integer.parseInt(contract_type_id_str);
			}

			List<ModelRecord> contractTypeModels = contractTypeClass.getModelsContractType(contractTypeId);
			
			for (ModelRecord m : contractTypeModels)
				this.modality.addItem(m.getModelDescription());
						
			if (this.contractType.getSelectedIndex() == 0) {
				employeeDialogObject.setContractType(null);
				employeeDialogObject.setContractModel(null);
			} else {
				String contract_type_id_str = this.contractType.getSelectedItemText().split(" -")[0];
				employeeDialogObject.setContractType(contract_type_id_str);
			}
		}
		
		@Override
		public void onContractModalityChange() {
			//TODO: MIRAR SETCONTRACTMODEL CON EL GUARDAR DEL JOOQEMPLOYEE
			if (this.contractType.getSelectedIndex() == 0 || this.modality.getSelectedIndex() == 0) {
				employeeDialogObject.setContractModel(null);
			} else {
				String contract_type_id_str = this.contractType.getSelectedItemText().split(" -")[0];
				Integer contractTypeId = Integer.parseInt(contract_type_id_str);

				String contractModelDescription = modality.getSelectedItemText();
				Integer contractModelEnum = contractTypeClass.getContractModelId(contractTypeId, contractModelDescription);
				employeeDialogObject.setContractModel(contractModelEnum); // GET String of enum in JooqEmployee.java
			}
		}
		
		@Override
		public void onContractStartDateChange() {
			employeeDialogObject.setContractStartDate(this.start_date.getValue());
		}

		@Override
		public void onContractEndDateChange() {
			employeeDialogObject.setContractEndDate(this.end_date.getValue());
		}

		@Override
		public void onContractSeniorityDateChange() {
			employeeDialogObject.setContractSeniorityDate(this.seniority_date.getValue());
		}

		@Override
		public void onContractAgreementChange() {
			this.level.clear();
			String agreementName = this.agreement.getSelectedItemText();
			List<Agreement> agreements = employeeDialogObject.getAgreements();
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
				employeeDialogObject.setContractAgreementId(null);
				employeeDialogObject.setContractAgreementDescription(null);
				employeeDialogObject.setContractAgreementLevelId(null);
				employeeDialogObject.setContractAgreementLevelDescription(null);
				this.category.setEnabled(false);
				this.category.setValue("");
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.category);
			} else {
				Integer agreementId = employeeDialogObject.getAgreementId(this.agreement.getSelectedItemText());
				employeeDialogObject.setContractAgreementId(agreementId);
				employeeDialogObject.setContractAgreementDescription(this.agreement.getSelectedItemText());
			}
		}

		@Override
		public void onContractAgreementLevelChange() {
			if (this.agreement.getSelectedIndex() == 0 || this.level.getSelectedIndex() == 0) {
				employeeDialogObject.setContractAgreementLevelId(null);
				employeeDialogObject.setContractAgreementLevelDescription(null);
				this.category.setEnabled(false);
				this.category.setValue("");
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.category);
			} else {
				Integer agreementLevelId = employeeDialogObject.getAgreementLevelId(this.agreement.getSelectedItemText(),
						this.level.getSelectedItemText());
				employeeDialogObject.setContractAgreementLevelId(agreementLevelId);
				employeeDialogObject.setContractAgreementLevelDescription(this.level.getSelectedItemText());
				String levelDescription = (this.level.getSelectedItemText() == null
						|| this.level.getSelectedItemText() == "-") ? null
								: this.level.getSelectedItemText().split("- ")[1];
				this.category.setValue(levelDescription);
				employeeDialogObject.setContractCategory(levelDescription);
				this.category.setEnabled(true);
			}
		}
		
		@Override
		public void onContractCategoryChange() {
			employeeDialogObject.setContractCategory(this.category.getValue());
		}
		
		@Override
		public void onContractQuoteGroupChange() {
			if (quote_group.getSelectedIndex() == 0)
				employeeDialogObject.setContractQuoteGroup(null);
			else
				employeeDialogObject.setContractQuoteGroup(this.quote_group.getSelectedIndex());
		}

		@Override
		public void onContractOccupationChange() {
			if (this.occupation.getSelectedIndex() == 0)
				employeeDialogObject.setContractOccupation(null);
			else
				employeeDialogObject.setContractOccupation(this.occupation.getSelectedIndex());
		}

		@Override
		public void onContractJourneyTypeChange() {
			 Boolean journey_type = (this.journeyType.getSelectedIndex() == 0) ? true : false;
			 employeeDialogObject.setContractJourneyType(journey_type);
		}
		
		// TABLA DATOS EMPLEADO

		@Override
		public void onEmployeeBirthDateChange() {
			employeeDialogObject.setEmployeeBirthDate(this.birth_date.getValue());
		}

		@Override
		public void onEmployeeGenderChange() {
			employeeDialogObject.setEmployeeGender(this.gender.getSelectedIndex());
		}
		
		@Override
		public void onEmployeeStreetTypeChange() {
			Integer streetTypeIdx = this.street_type.getSelectedIndex();
			String shortCode = StreetType.values()[streetTypeIdx].getShortCode();
			employeeDialogObject.setEmployeeStreetType(shortCode);
		}

		@Override
		public void onEmployeeAddressChange() {
			employeeDialogObject.setEmployeeAddress(this.address.getValue());
		}

		@Override
		public void onEmployeeAddressNumChange() {
			employeeDialogObject.setEmployeeAddressNumber(this.addressNum.getValue());
		}

		@Override
		public void onEmployeeAddressZipChange() {
			employeeDialogObject.setEmployeeAddressZip(this.addressZip.getValue());
		}

		@Override
		public void onEmployeeAddressCityChange() {
			employeeDialogObject.setEmployeeAddressCity(this.addressCity.getValue());
		}

		@Override
		public void onEmployeeAddressProvinceChange() {
			employeeDialogObject.setEmployeeAddressProvince(this.addressProvince.getSelectedItemText());
		}
		
		@Override
		public void onEmployeeMobileChange() {
			employeeDialogObject.setEmployeeMobile(this.mobile.getValue());
		}

		@Override
		public void onEmployeePhoneChange() {
			employeeDialogObject.setEmployeePhone(this.phone.getValue());
		}

		@Override
		public void onEmployeeEmailChange() {
			employeeDialogObject.setEmployeeEmail(this.email.getValue());
		}

		@Override
		public void onEmployeePayMethodChange() {
			employeeDialogObject.setEmployeePayMethod(this.payMethod.getSelectedItemText());
		}

		@Override
		public void onEmployeeAccountChange() {
			employeeDialogObject.setEmployeeAccount(this.account.getValue());
		}

		@Override
		public void onEmployeeBICChange() {
			employeeDialogObject.setEmployeeBIC(this.bic.getValue());
		}
		
		// ------------------------------------------------------------------------
		//								Aux Methods
		// ------------------------------------------------------------------------
		
		private void fillExistingEmployee(EmployeeInfo employeeData) {
			this.document.setValue(employeeData.getDocument());
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.document);
			this.nationality.setValue(Country.valueOf(employeeData.getNationality()).getName());
			this.security_social_num.setValue(employeeData.getSsNumber());
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.security_social_num);
			this.name.setValue(employeeData.getName());
			this.first_surname.setValue(employeeData.getSurName());
			this.second_surname.setValue(employeeData.getSecondSurName());
			this.birth_date.setValue(employeeData.getBirthdate());
			this.gender.setSelectedIndex(employeeData.getGender());
			this.street_type.setSelectedIndex(getStreetTypeIndex(employeeData.getStreetType()));
			this.address.setValue(employeeData.getAddress());
			this.addressNum.setValue(employeeData.getAddresNum());
			this.addressZip.setValue(employeeData.getAddressZip());
			this.addressCity.setValue(employeeData.getAddressCity());
			Integer provinceIndex = ProvinceContract.getProvinceIndex(employeeData.getAddressProvinces());
			this.addressProvince.setSelectedIndex(provinceIndex);
			this.mobile.setValue(employeeData.getMobile());
			this.phone.setValue(employeeData.getPhone());
			this.email.setValue(employeeData.getEmail());
			this.payMethod.setSelectedIndex(getPayMethodIndex(employeeData.getPayMethodType()));
			this.account.setValue(employeeData.getAccount());
			this.bic.setValue(employeeData.getBic());
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
		
		public void showNationality(String document_type_str) {
			if (document_type_str == "CIF" || document_type_str == "Pasaporte" || document_type_str == "NIE") {
				this.nationalityLabelCell.getStyle().clearDisplay();
				this.nationalityCell.getStyle().clearDisplay();
				security_social_num.addStyleName(style.nssWidht());
			} else {
				security_social_num.removeStyleName(style.nssWidht());
				this.nationalityLabelCell.getStyle().setDisplay(Display.NONE);
				this.nationalityCell.getStyle().setDisplay(Display.NONE);
				nationality.setValue("ESPA\u00D1A");
			}
		}
		
		private String getIso2(String country) {
			for (int i = 0; i < Country.values().length; i++)
				if (Country.values()[i].getName() == country)
					return Country.values()[i].getIso2();
			
			return null;
		}

		private int getStreetTypeIndex(String streetType) {
			for(int i=0; i<StreetType.values().length; i++)
				if(streetType == StreetType.values()[i].getShortCode())
					return i;
			
			return -1;
		}
		
		private int getPayMethodIndex(String payMethodType) {
			switch (payMethodType) {
			case "EFECTIVO":
				return 1;
			case "GIRO":
				return 2;
			case "CHEQUE":
				return 3;
			case "TRANSFERENCIA":
				return 4;
			default:
				return 0;
			}
		}

	}
	
	interface Callback {
		void onAccept(EmployeeDialog dialog);
	}
	
	interface Binder extends UiBinder<Widget, EmployeeDialog> {

	}
	private static final Binder binder = GWT.create(Binder.class);
	
	
	@UiField (provided = true)
	Employee employee;
	
	private Callback cb;
	private EmployeeDialogObject employeeDialogObject;
	private ContractType contractType;
	
	public EmployeeDialog() {
		employee = new EmployeeImplementation();
		
		setCaption("Trabajador");
		setWidget(binder.createAndBindUi(this));
		
	}

	public void show(Callback cb) {
		this.cb = cb;
		super.show();
	}
	
	public void setPopupPositionAndShow(PositionCallback positionCallback, Callback callback) {
		this.cb = callback;
		super.setPopupPositionAndShow(positionCallback);
	}
	
	public void setEmployeeDialogObject(EmployeeDialogObject employeeDialogObject) {
		this.employeeDialogObject = employeeDialogObject;
		this.contractType = new ContractType();
		
		this.employeeDialogObject.getWorkplaceEmployees(
				r -> { initLogicWindow();}, 
				t -> {}
		);
	}

	// ------------------------------------------------------------------------
	//						Initialize Logic Window
	// ------------------------------------------------------------------------
		
	private void initLogicWindow() {
		initSuggestBox();
		initActivitiesCCC();
		initWorkplaces();
		initContractType();
		initAgreements();
		fillDefaultFields();
		initFocus();
	}
	
	private void initSuggestBox() {
		WorkplaceEmployees workplaceEmployees = employeeDialogObject.getWorkplaceEmployees();
		
		//DOCUMENT
		List<String> employeesDocuments = workplaceEmployees.getWorkplaceEmployeesDocument();
		List<String> employeesDocumentsSuggest = new ArrayList<String>();
		for(String document : employeesDocuments)
			employeesDocumentsSuggest.add(document+"");
		MultiWordSuggestOracle orclDocuments = (MultiWordSuggestOracle) this.employee.document.getSuggestOracle();
		orclDocuments.addAll(employeesDocumentsSuggest);
//		orclDocuments.setDefaultSuggestionsFromText(employeesDocumentsSuggest);
		
		//SS_NUMBER
		List<String> employeesSSNumbers = workplaceEmployees.getWorkplaceEmployeesSSNumber();
		List<String> employeesSSNumbersSuggest = new ArrayList<String>();
		for(String ssNumber : employeesSSNumbers)
			employeesSSNumbersSuggest.add(ssNumber+"");
		MultiWordSuggestOracle orclSSNumbers = (MultiWordSuggestOracle) this.employee.security_social_num.getSuggestOracle();
		orclSSNumbers.addAll(employeesSSNumbersSuggest);
//		orclSSNumbers.setDefaultSuggestionsFromText(employeesSSNumbersSuggest);
		
		//NAMES
		List<String> employeesNames = workplaceEmployees.getWorkplaceEmployeesName();
		List<String> employeesNamesSuggest = new ArrayList<String>();
		for(String name : employeesNames)
			employeesNamesSuggest.add(name+"");
		MultiWordSuggestOracle orclNames = (MultiWordSuggestOracle) this.employee.name.getSuggestOracle();
		orclNames.addAll(employeesNamesSuggest);
//		orclNames.setDefaultSuggestionsFromText(employeesNamesSuggest);
		
		//SURNAME
		List<String> employeesSurNames = workplaceEmployees.getWorkplaceEmployeesSurName();
		List<String> employeesSurNamesSuggest = new ArrayList<String>();
		for(String surName : employeesSurNames)
			employeesSurNamesSuggest.add(surName+"");
		MultiWordSuggestOracle orclSurNames = (MultiWordSuggestOracle) this.employee.first_surname.getSuggestOracle();
		orclSurNames.addAll(employeesSurNamesSuggest);
//		orclSurNames.setDefaultSuggestionsFromText(employeesSurNamesSuggest);
	}

	private void initActivitiesCCC() {
		//ACTIVITY - CCC
		this.employee.activityCCC.addItem("-");
		if(null != this.employeeDialogObject.getActivities())
			for(Entry<Integer,String> entry : this.employeeDialogObject.getActivities().entrySet())
				for(CCCInfo cccInfo :  this.employeeDialogObject.getCCCs().values())
					if(cccInfo.getActivityId() == entry.getKey()) 
						this.employee.activityCCC.addItem(entry.getValue() + " - " + CCCType.values()[cccInfo.getType()] + "[" + cccInfo.getCcc() + "] - " +  cccInfo.getGeozone());
	}
	
	private void initWorkplaces() {
		//WORKPLACE
		for(Workplace workplace : employeeDialogObject.getWorkplaces())
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
		List<Agreement> agreements = employeeDialogObject.getActiveAgreements();
		for (Agreement a : agreements)
			this.employee.agreement.addItem(a.getDescription());
	}
	
	private void fillDefaultFields() {
		//ACTIVITY CCC
		if(this.employee.activityCCC.getItemCount() == 2) {
			this.employee.activityCCC.setSelectedIndex(1);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.employee.activityCCC);
		}
		
		//WORKPLACE
		Integer workplaceIndex = this.employeeDialogObject.getWorkplaceIndex();
		this.employee.workplace.setSelectedIndex(workplaceIndex);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.employee.workplace);
		
		//AGREEMENT
		Integer agreementIndex = this.employeeDialogObject.getAgreementIndex(this.employeeDialogObject.getWorkplaceAgreement());
		this.employee.agreement.setSelectedIndex(agreementIndex + 1);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.employee.agreement);
		
		//STREET_TYPE
		this.employee.street_type.setSelectedIndex(14); //Calle
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.employee.street_type);
	}
	
	private void initFocus() {
		//FOCUS DOCUMENT
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand () {
	        public void execute () {
	        	employee.document.setFocus(true);
	        }
		});
	}

	// ------------------------------------------------------------------------
	//							UiHandler Accept/Cancel
	// ------------------------------------------------------------------------
	
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent clickEvent) {
		hide();
	}
	
	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent clickEvent) {
		hide();
		cb.onAccept(this);
	}
	
	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------
	
}

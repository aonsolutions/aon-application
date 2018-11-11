package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.CCCType;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ContractTypeRecord;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ModelRecord;
import com.esferalia.aon.gwt.payroll.shared.StreetType;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeDraft extends Composite implements ContextMenuHandler {

	private class EmployeeImplementation extends Employee{
		ContractType contractTypeClass = new ContractType();

		@Override
		public void onEmployeeDocumentSuggestionChange() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onEmployeeDocumentChange() {
			String document = this.document.getValue();
			String document_type = checkDocumentType(document);
			
			this.document_type.setText(document_type);
			if(employeeDraftObject.checkDocumentValidation(document_type, document)) {
				this.document.removeStyleName(this.style.warning());
				employeeDraftObject.setEmployeeDocument(this.document.getValue());
				employeeDraftObject.setEmployeeDocumentType(document_type);
			}else
				this.document.addStyleName(this.style.warning());

			showNationality(document_type);		
		}

		@Override
		public void onEmployeeNationalityChange() {
			String countryIso2 = getIso2(this.nationality.getValue());
			employeeDraftObject.setNationality(countryIso2);
		}

		@Override
		public void onEmployeeSSNumSuggestionChange() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onEmployeeSSNumChange() {
			String ssNum = this.security_social_num.getValue();
			if(employeeDraftObject.checkSSNumValidation(ssNum)) {
				employeeDraftObject.setEmployeeSocialSecurityNum(ssNum);
				security_social_num.removeStyleName(style.warning());
			}else
				security_social_num.addStyleName(style.warning());
		}

		@Override
		public void onEmployeeNameSuggestionChange() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onEmployeeNameChange() {
			employeeDraftObject.setEmployeeName(this.name.getValue());	
		}

		@Override
		public void onEmployeeFirstSurnameSuggestionChange() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onEmployeeFirstSurnameChange() {
			employeeDraftObject.setEmployeeFirstSurname(this.first_surname.getValue());
		}

		@Override
		public void onEmployeeSecondSurnameChange() {
			employeeDraftObject.setEmployeeSecondSurname(this.second_surname.getValue());
		}

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
		}

		@Override
		public void onContractActiviesCCCChange() {
			String activityCCC = this.activityCCC.getSelectedItemText();
			if(activityCCC.equals("-")){
				employeeDraftObject.setContractActivityId(null);
				employeeDraftObject.setContractCCCId(null);
				employeeDraftObject.setContractCCCType((Byte)null);
			}else{
//				Window.alert(activityCCC);
				String activityStr = activityCCC.split(" -")[0];
				String cccStr = activityCCC.split("\\[")[1].split("\\]")[0];
				String cccTypeStr = activityCCC.split("- ")[1].split("\\[")[0];
				String cccGeozoneStr = activityCCC.split("- ")[2];
//				Window.alert("ActivityStr : " + activityStr + ", CCCStr : " + cccStr + ", CCCTypeStr : " + cccTypeStr + ", CCCGeozoneStr : "+ cccGeozoneStr);
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
		}

		@Override
		public void onContractWorkplaceChange() {
			String workplaceName = this.workplace.getSelectedItemText();
			Integer workplaceId = employeeDraftObject.getWorkplaceIdByName(workplaceName);
			employeeDraftObject.setContractWorkplaceId(workplaceId);
		}

		@Override
		public void onContractTypeChange() {
			this.modality.clear();
			this.modality.addItem("-");
			Integer contractTypeId = -1;
			if (this.contractType.getSelectedIndex() != 0) {
				String contract_type_id_str = this.contractType.getSelectedItemText().split(" -")[0];
				contractTypeId = Integer.parseInt(contract_type_id_str);
				if((contractTypeId >= 200 && contractTypeId<300) || (contractTypeId >= 500 && contractTypeId<600)) {
					this.showElementsPartialTimeContract();
					this.journeyDuration.addStyleName(style.journeyDurationWarning());
					this.journeyDuration.setText("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
				}else
					this.showElementsFullTimeContract();
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
		}

		@Override
		public void onContractModalityChange() {
			//TODO: MIRAR SETCONTRACTMODEL CON EL GUARDAR DEL JOOQEMPLOYEE
			if (this.contractType.getSelectedIndex() == 0 || this.modality.getSelectedIndex() == 0) {
				employeeDraftObject.setContractModel(null);
			} else {
				String contract_type_id_str = this.contractType.getSelectedItemText().split(" -")[0];
				Integer contractTypeId = Integer.parseInt(contract_type_id_str);

				String contractModelDescription = modality.getSelectedItemText();
				Integer contractModelEnum = contractTypeClass.getContractModelId(contractTypeId, contractModelDescription);
				employeeDraftObject.setContractModel(contractModelEnum); // GET String of enum in JooqEmployee.java
			}
		}

		@Override
		public void onContractStartDateChange() {
			employeeDraftObject.setContractStartDate(this.start_date.getValue());
		}

		@Override
		public void onContractEndDateChange() {
			employeeDraftObject.setContractEndDate(this.end_date.getValue());
		}

		@Override
		public void onContractSeniorityDateChange() {
			employeeDraftObject.setContractSeniorityDate(this.seniority_date.getValue());
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
		}

		@Override
		public void onContractCategoryChange() {
			employeeDraftObject.setContractCategory(this.category.getValue());
		}

		@Override
		public void onContractQuoteGroupChange() {
			if (quote_group.getSelectedIndex() == 0)
				employeeDraftObject.setContractQuoteGroup(null);
			else
				employeeDraftObject.setContractQuoteGroup(this.quote_group.getSelectedIndex());
		}

		@Override
		public void onContractOccupationChange() {
			if (this.occupation.getSelectedIndex() == 0)
				employeeDraftObject.setContractOccupation(null);
			else
				employeeDraftObject.setContractOccupation(this.occupation.getSelectedIndex());
		}

		@Override
		public void onContractJourneyTypeChange() {
			 Boolean journey_type = (this.journeyType.getSelectedIndex() == 0) ? true : false;
			 employeeDraftObject.setContractJourneyType(journey_type);
		}
		
		@Override
		public void onContractJourneyDurationClick() {
			ContractJourneyDialog dialog = new ContractJourneyDialog();
			dialog.center();
			dialog.show();
//			Window.alert("MOSTRAT HORAS");			
		}
		
		//EMPLOYEE TABLE

		@Override
		public void onEmployeeBirthDateChange() {
			employeeDraftObject.setEmployeeBirthDate(this.birth_date.getValue());
		}

		@Override
		public void onEmployeeGenderChange() {
			employeeDraftObject.setEmployeeGender(this.gender.getSelectedIndex());
		}

		@Override
		public void onEmployeeStreetTypeChange() {
			Integer streetTypeIdx = this.street_type.getSelectedIndex();
			String shortCode = StreetType.values()[streetTypeIdx].getShortCode();
			employeeDraftObject.setEmployeeStreetType(shortCode);
		}

		@Override
		public void onEmployeeAddressChange() {
			employeeDraftObject.setEmployeeAddress(this.address.getValue());
		}

		@Override
		public void onEmployeeAddressNumChange() {
			employeeDraftObject.setEmployeeAddressNumber(this.addressNum.getValue());
		}

		@Override
		public void onEmployeeAddressZipChange() {
			employeeDraftObject.setEmployeeAddressZip(this.addressZip.getValue());
		}

		@Override
		public void onEmployeeAddressCityChange() {
			employeeDraftObject.setEmployeeAddressCity(this.addressCity.getValue());
		}

		@Override
		public void onEmployeeAddressProvinceChange() {
			employeeDraftObject.setEmployeeAddressProvince(this.addressProvince.getSelectedItemText());
		}

		@Override
		public void onEmployeeMobileChange() {
			employeeDraftObject.setEmployeeMobile(this.mobile.getValue());
		}

		@Override
		public void onEmployeePhoneChange() {
			employeeDraftObject.setEmployeePhone(this.phone.getValue());
		}

		@Override
		public void onEmployeeEmailChange() {
			employeeDraftObject.setEmployeeEmail(this.email.getValue());
		}

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
		}

		@Override
		public void onEmployeeBICChange() {
			employeeDraftObject.setEmployeeBIC(this.bic.getValue());
		}

		@Override
		public void onEmployeeAccountChange() {
			employeeDraftObject.setEmployeeAccount(this.account.getValue());
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface EmployeeDraftUiBinder extends UiBinder<Widget, EmployeeDraft> {
	
	}
	
	private static EmployeeDraftUiBinder uiBinder = GWT.create(EmployeeDraftUiBinder.class);

	@UiField (provided = true)
	Employee employee;
	
	@UiField
	Button saveButton;
	
	// ------------------------------------------------------ VARIABLES DE LA CLASE -------------------------------------------------

	private EmployeeDraftObject employeeDraftObject;
	private ContractType contractType;

	// --------------------------------------------------------- CONSTRUCTOR --------------------------------------------------------

	public EmployeeDraft() {
		employee = new EmployeeImplementation();
		
		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	// ------------------------------------------------------- UiHandlers --------------------------------------------------------
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		if(checkIfSaveIsPossible())
			if(checkPayMethod())
				employeeDraftObject.updateEmployee(
					r -> {
//						setEmployeeDraftObject(employeeDraftObject);
					}, 
					t -> {}
				);
			else {
				WarningDialog dialog = new WarningDialog("Aviso", "Si el metodo de pago es transferencia, debe rellenar obligatoriamente los campos de BIC y cuenta.");
				dialog.center();
				dialog.show();
			}
				
		else{
			WarningDialog dialog = new WarningDialog("Aviso", "Hay que rellenar los campos azules obligatoriamente.");
			dialog.center();
			dialog.show();
		}
	}

	
	// --------------------------------------------------- METODOS DE LA CLASE ----------------------------------------------------

	public void setEmployeeDraftObject(EmployeeDraftObject employeeDraftObject) {
		this.employeeDraftObject = employeeDraftObject;
		this.contractType = new ContractType();
		
		this.employeeDraftObject.initializeEmployee(r -> {
			initializeView();
		}, t -> {});
	}

	private void initializeView() {
		resetElements();
		initActivitiesCCC();
		initWorkplaces();
		initContractType();
		initAgreements();
		
		if (employeeDraftObject.getContractSSRegimen() == 3) { //RETA, había algo mas que determinaba si era o no RETA
			this.employee.showElementsFreelancerTable();
			fillContractFreelancerTable();
		} else {
			this.employee.hideElementsFreelancerTable();
			fillContractTable();
		}
		
		fillEmployeeTable();
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
		this.employee.document.setValue(employeeDraftObject.getEmployeeDocument());
		showNationality(checkDocumentType(employeeDraftObject.getEmployeeDocument()));
		this.employee.nationality.setValue(employeeDraftObject.getEmployeeNationality());
		this.employee.security_social_num.setValue(employeeDraftObject.getEmployeeSSNumber());
		this.employee.name.setValue(employeeDraftObject.getEmployeeName());
		this.employee.first_surname.setValue(employeeDraftObject.getEmployeeSurname());
		this.employee.second_surname.setValue(employeeDraftObject.getEmployeeSecondSurname());
		this.employee.ssRegimeType.setSelectedIndex(1);
		this.employee.workplace.setSelectedIndex(employeeDraftObject.getContractWorkplace());
		this.employee.start_date.setValue(employeeDraftObject.getContractStartDate());
		this.employee.seniority_date.setValue(employeeDraftObject.getContractSeniorityDate());
		this.employee.end_date.setValue(employeeDraftObject.getContractEndDate());
		this.employee.agreement.setSelectedIndex(employeeDraftObject.getContractAgreement() + 1);
		getAgreementLevels(employeeDraftObject.getContractAgreementId());
		this.employee.level.setSelectedIndex(employeeDraftObject.getAgreementLevel() + 1);
		this.employee.category.setValue(employeeDraftObject.getContractAgreementCategory());
		this.employee.journeyType.setSelectedIndex(employeeDraftObject.getContractJourneyType());
	}

	private void fillContractTable() {
		this.employee.document.setValue(employeeDraftObject.getEmployeeDocument());
		showNationality(checkDocumentType(employeeDraftObject.getEmployeeDocument()));
		this.employee.nationality.setValue(employeeDraftObject.getEmployeeNationality());
		this.employee.security_social_num.setValue(employeeDraftObject.getEmployeeSSNumber());
		this.employee.name.setValue(employeeDraftObject.getEmployeeName());
		this.employee.first_surname.setValue(employeeDraftObject.getEmployeeSurname());
		this.employee.second_surname.setValue(employeeDraftObject.getEmployeeSecondSurname());
		this.employee.ssRegimeType.setSelectedIndex(employeeDraftObject.getContractSSRegimen());
		this.employee.activityCCC.setSelectedIndex(employeeDraftObject.getContractActivityCCC());
		this.employee.workplace.setSelectedIndex(employeeDraftObject.getContractWorkplace());
		Integer contractTypeId = employeeDraftObject.getContractType();
		this.employee.contractType.setSelectedIndex(contractType.getContractTypeIndex(contractTypeId) + 1);
//		if((contractTypeId >= 200 && contractTypeId<300) || (contractTypeId >= 500 && contractTypeId<600)) {
//			this.employee.showElementsPartialTimeContract();
//			this.employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
//			this.employee.journeyDuration.setText("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
//		}else
//			this.employee.showElementsFullTimeContract();
		
		
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
		this.employee.addressCity.setValue(employeeDraftObject.getEmployeeAddressCity());
		this.employee.addressProvince.setSelectedIndex(employeeDraftObject.getEmployeeAddressProvince());
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
	
	private boolean checkPayMethod() {
		if(4 == this.employee.payMethod.getSelectedIndex()) {
			if("" == this.employee.bic.getValue() || "" == this.employee.account.getValue())
				return false;
			else
				return true;
		}else
			return true;
	}

	private boolean checkIfSaveIsPossible() {
		if(
		   "" != this.employee.document.getValue() &&
		   "" != this.employee.nationality.getValue() &&
		   "" != this.employee.security_social_num.getValue() &&
		   "" != this.employee.name.getValue() &&
		   "" != this.employee.first_surname.getValue() &&
		   null != this.employee.start_date.getValue() &&
		   0 != this.employee.agreement.getSelectedIndex() &&
		   0 != this.employee.level.getSelectedIndex() &&
		   null != this.employee.birth_date.getValue()   
		)
			if(1 == this.employee.ssRegimeType.getSelectedIndex())
				return true;
			else if(
			   0 != this.employee.activityCCC.getSelectedIndex() &&
			   0 != this.employee.contractType.getSelectedIndex() &&
			   0 != this.employee.modality.getSelectedIndex() &&
			   0 != this.employee.quote_group.getSelectedIndex()	
			)	
				return true;
			else
				return false;
		else
			return false;
	
	}

}

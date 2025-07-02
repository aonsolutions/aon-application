package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractJourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public abstract class EmployeeDialog extends AonCustomDialog {
	
	private class EmployeeImplementation extends EmployeeWidget{
		
		// TABLA DATOS CONTRATO
		
		@Override
		public void onClearEmployeeClick() {
			employeeDialogObject.resetEmptyInfo();
			this.initializeView();
			this.unblockVariablesExistingContract();
			
			hideEmployeeTable();
		}
		
		@Override
		public void onEmployeeDocumentSuggestionChange(String document) {
			EmployeeInfo existingEmployee = employeeDialogObject.getEmployeeDataByDocument(document);
			
			if(null == existingEmployee)
				employeeDialogObject.setEmployeeDocument(document);
			else
				initializeExistingEmployee(existingEmployee.getContractId(), existingEmployee.getContractActive());
			
		}

		@Override
		public void onEmployeeDocumentChange(String document, String documentType) {
			employeeDialogObject.setEmployeeDocument(document);
			employeeDialogObject.setEmployeeDocumentType(documentType);
		}
		
		@Override
		public void onEmployeeNationalityChange(String countryIso2) {
			employeeDialogObject.setNationality(countryIso2);
		}
		
		@Override
		public void onEmployeeSSNumSuggestionChange(String ssNumber) {
			EmployeeInfo existingEmployee = employeeDialogObject.getEmployeeDataBySSNum(ssNumber);
			
			if(null == existingEmployee.getEmployeeId())
				employeeDialogObject.setEmployeeSocialSecurityNum(ssNumber);
			else
				initializeExistingEmployee(existingEmployee.getContractId(), existingEmployee.getContractActive());	
				
		}

		@Override
		public void onEmployeeSSNumChange(String ssNumber) {
			employeeDialogObject.setEmployeeSocialSecurityNum(ssNumber);
		}
		
		@Override
		public void onEmployeeNameSuggestionChange(String nameSurname) {
			EmployeeInfo existingEmployee = employeeDialogObject.getEmployeeDataByNameSurname(nameSurname);
			
			if(null == existingEmployee.getEmployeeId()) 
				employeeDialogObject.setEmployeeName(nameSurname);
			else
				initializeExistingEmployee(existingEmployee.getContractId(), existingEmployee.getContractActive());	
			
		}
		
		@Override
		public void onEmployeeNameChange(String name) {
			employeeDialogObject.setEmployeeName(name);
		}

		@Override
		public void onEmployeeFirstSurnameSuggestionChange(String nameSurname) {
			EmployeeInfo existingEmployee = employeeDialogObject.getEmployeeDataByNameSurname(nameSurname);
			
			if(null == existingEmployee.getEmployeeId()) 
				employeeDialogObject.setEmployeeFirstSurname(nameSurname);
			else
				initializeExistingEmployee(existingEmployee.getContractId(), existingEmployee.getContractActive());		
			
		}
		
		@Override
		public void onEmployeeFirstSurnameChange(String surname) {
			employeeDialogObject.setEmployeeFirstSurname(surname);
		}

		@Override
		public void onEmployeeSecondSurnameChange(String secondSurname) {
			employeeDialogObject.setEmployeeSecondSurname(secondSurname);
		}

		@Override
		public void onContractSSRegimenChange(byte ssRegime) {
			employeeDialogObject.setSSRegime(ssRegime);
		}
	
		@Override
		public void onContractMdTBThange(String tbtType) {
			employeeDialogObject.setMdTBT(Byte.parseByte(tbtType));
		}
		
		@Override
		public void onContractActiviesCCCChange(String activityCCC) {
			employeeDialogObject.setActivityInfo(activityCCC);
		}
		
		@Override
		public void onContractMdCTZhange(String mdCtz) {
			employeeDialogObject.setMdCtzInfo(mdCtz);
		}
		
		@Override
		public void onContractWorkplaceChange(Integer workplaceId) {
			employeeDialogObject.setContractWorkplaceId(workplaceId);
		}

		@Override
		public void onContractTypeChange(String contractType) {
			employeeDialogObject.setContractType(contractType);
			employeeDialogObject.setContractModel(null);
		}
		
		@Override
		public void onContractModalityChange(Integer contractModel) {
			employeeDialogObject.setContractModel(contractModel); // GET String of enum in JooqEmployee.java
		}
		
		@Override
		public void onContractStartDateChange(Date startDate) {
			employeeDialogObject.setContractStartDate(startDate);
		}

		@Override
		public void onContractEndDateChange(Date endDate) {
			employeeDialogObject.setContractEndDate(endDate);
		}

		@Override
		public void onContractSeniorityDateChange(Date seniorityDate) {
			employeeDialogObject.setContractSeniorityDate(seniorityDate);	
		}

		@Override
		public void onContractAgreementChange(Integer agreementId, String agreementSSNumber) {
			employeeDialogObject.setContractAgreementId(agreementId);
			employeeDialogObject.setContractAgreementLevelId(null);
			employeeDialogObject.setContractCategory(null);
			employeeDialogObject.setAgreementSSNumber(agreementSSNumber);
			
			if(null != agreementId)
				getAgreementLevels(agreementId, s -> {}, f -> {});
		}

		@Override
		public void onContractAgreementLevelChange(Integer agreementLevelId) {
			employeeDialogObject.setContractAgreementLevelId(agreementLevelId);
		}
		
		@Override
		public void onContractCategoryChange(String agreementCategory) {
			employeeDialogObject.setContractCategory(agreementCategory);
		}
		
		@Override
		public void onContractQuoteGroupChange(String quoteGroup) {
			employeeDialogObject.setContractQuoteGroup(quoteGroup);
		}
		
		@Override
		public void onContractQuoteGroupIdx(boolean quoteGroupMonth) {
			employeeDialogObject.setContractQuoteIdxMonth(quoteGroupMonth);
		}

		@Override
		public void onContractOccupationChange(String occupation) {
			employeeDialogObject.setContractOccupation(occupation);
		}
		
		@Override
		public void onContractRLCEChange(String rlce) {
			employeeDialogObject.setContractRlce(rlce);
		}
		
		@Override
		public void onEmployeeCnoSuggestionChange(String cno) {
			employeeDialogObject.setContractCno(cno);
		}
		
		@Override
		public void onContractEmployeesColectiveChange(String employeesColective) {
			employeeDialogObject.setContractEmployeesColective(employeesColective);
		}

		@Override
		public void onContractJourneyTypeChange(Boolean journeyType) {
			 employeeDialogObject.setContractJourneyType(journeyType);
		}
		
		@Override
		public void onContractPartialityChange(Double partialityCoef) {
			employeeDialogObject.setPartialityCoef(partialityCoef);
		}

		@Override
		public void onContractJourneyDurationClick() {
			ContractJourneyDialog dialog = new ContractJourneyDialog(
					employeeDialogObject.getContractStartDate(), 
					employeeDialogObject.getContractEndDate()) {

				@Override
				protected void onSave(Double partialityCoef) {
					ContractJourneyDuration contractJourneyDuration = this.getContractJourneyDuration();
					if(contractJourneyDuration.getJourniesSize() != 0) {
						String result = "Desde ";
						for(JourneyDuration journeyDuration : contractJourneyDuration.getContractJourneyDuration().descendingMap().entrySet().iterator().next().getValue()) {
							if("HORAS_LUNES" == journeyDuration.getName()) result += formatFullDate.format(journeyDuration.getStartDate()) + " ( L : " + journeyDuration.getExpression() + " ";
							if("HORAS_MARTES" == journeyDuration.getName()) result += ", M : " + journeyDuration.getExpression() + " ";
							if("HORAS_MIERCOLES" == journeyDuration.getName()) result += ", X : " + journeyDuration.getExpression() + " ";
							if("HORAS_JUEVES" == journeyDuration.getName()) result += ", J : " + journeyDuration.getExpression() + " ";
							if("HORAS_VIERNES" == journeyDuration.getName()) result += ", V : " + journeyDuration.getExpression() + " ";
							if("HORAS_SABADO" == journeyDuration.getName()) result += ", S : " + journeyDuration.getExpression() + " ";
							if("HORAS_DOMINGO" == journeyDuration.getName()) result += ", D : " + journeyDuration.getExpression() + " )";
						}
						
						employee.createJourneyDurationInfo(result);
		
					}else {
						employee.createJourneyDurationWarning();
					}
					employeeDialogObject.setContractJourneyDuration(contractJourneyDuration.getContractJourneyDuration());
					employee.partialityCoef.setValue(partialityCoef);
					employeeDialogObject.setPartialityCoef(partialityCoef);
				}	
			};
			dialog.center();
			dialog.show();			
		}
		
		// TABLA DATOS EMPLEADO

		@Override
		public void onEmployeeBirthDateChange(Date birthDate) {
			employeeDialogObject.setEmployeeBirthDate(birthDate);	
		}

		@Override
		public void onEmployeeGenderChange(byte gender) {
			employeeDialogObject.setEmployeeGender(gender);
		}
		
		@Override
		public void onEmployeeCivilStatusChange(byte civilStatus) {
			employeeDialogObject.setEmployeeCivilStatus(civilStatus);	
		}
		
		@Override
		public void onEmployeeStreetTypeChange(String streetType) {
			employeeDialogObject.setEmployeeStreetType(streetType);
		}

		@Override
		public void onEmployeeAddressChange(String address) {
			employeeDialogObject.setEmployeeAddress(address);
		}

		@Override
		public void onEmployeeAddressNumChange(String addressNum) {
			employeeDialogObject.setEmployeeAddressNumber(addressNum);
		}
		
		@Override
		public void onEmployeeAddressInfoChange(String addressInfo) {
			employeeDialogObject.setEmployeeAddressInfo(addressInfo);
		}

		@Override
		public void onEmployeeAddressZipChange(String addressZip) {
			employeeDialogObject.setEmployeeAddressZip(addressZip);
		}
		
		@Override
		public void onEmployeeAddressProvinceChange(Integer geozoneId) {
			employeeDialogObject.setEmployeeAddressProvince(geozoneId);
			employeeDialogObject.setEmployeeAddressCity(null, null);
		}
		
		@Override
		public void onEmployeeAddressMunicipalityChange(String cityName, String cityCode) {
			employeeDialogObject.setEmployeeAddressCity(cityName, cityCode);
		}
		
		@Override
		public void onEmployeeMobileChange(String mobile) {
			employeeDialogObject.setEmployeeMobile(mobile);
		}

		@Override
		public void onEmployeePhoneChange(String phone) {
			employeeDialogObject.setEmployeePhone(phone);
		}

		@Override
		public void onEmployeeEmailChange(String email) {
			employeeDialogObject.setEmployeeEmail(email);
		}

		@Override
		public void onEmployeePayMethodChange(Integer payMethodId) {
			employeeDialogObject.setEmployeePayMethodId(payMethodId);
		}

		@Override
		public void onEmployeeBICChange(String bic) {
			employeeDialogObject.setEmployeeBIC(bic);
		}
		
		@Override
		public void onEmployeeAccountChange(String account, String bankAlias, String bankSwift) {
			employeeDialogObject.setEmployeeAccount(account);
			employeeDialogObject.setEmployeeBankAlias(bankAlias);
			employeeDialogObject.setEmployeeBIC(bankSwift);
		}

		@Override
		public void fireError(String title, String message) {
			showError(title, message);
		}
		
		private void initializeExistingEmployee(Integer contractId, boolean contractActive) {
			employeeDialogObject.initializeEmployee(contractId,
					s -> fillExistingEmployee(contractActive), 
					f -> {}
			);	
		}

		@Override
		public void onUploadDni() {}
		
		@Override
		public void onLoadEnd() {
			onLoaded();
		}
		
	}	

	// ------------------------------------------------- UiFields
	
	private HTMLPanel content = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel messageContainer = new HTMLPanel(AonStringUtils.EMPTY);
	private ScrollPanel scrollPanel = new ScrollPanel();
	private EmployeeWidget employee;
	private HTMLPanel buttonsPanel = new HTMLPanel(AonStringUtils.EMPTY);
	
	// ------------------------------------------------- Class variables
	
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private EmployeeDialogObject employeeDialogObject;
	private Boolean hideEmployeePanel;
	
	private Button acceptBtnDialog;
	
	// ------------------------------------------------- Constructor
	
	protected EmployeeDialog(Boolean hideEmployeePanel) {
		setCaption("Nuevo Contrato");
		
		this.hideEmployeePanel = hideEmployeePanel;
		
		content.addStyleName(AON.CSS.aonFlexColumn());
		employee = new EmployeeImplementation();
		
		getButtonsPanel();
		
		content.add(messageContainer);
		
		scrollPanel.setWidget(employee);
		scrollPanel.setHeight("30rem");
		scrollPanel.getElement().getStyle().setProperty("padding", "1rem");
		
		content.add(scrollPanel);
		
		content.add(buttonsPanel);
		
		Scheduler.get().scheduleDeferred(() -> {
			setWidget(content);
		});
	}
	
	// ------------------------------------------------- Abstract Methods

	public void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messageContainer, errorMap);
	}

	protected abstract void onAccept(Integer contractId);
	protected abstract void onLoaded();
	
	// ------------------------------------------------- setEmployeeDialogObject
	
	public void setEmployeeDialogObject(EmployeeDialogObject employeeDialogObject) {
		this.employeeDialogObject = employeeDialogObject;
		this.employeeDialogObject.getWorkplaceEmployees(
				r -> initLogicWindow(), 
				t -> {}
		);
	}
	
	// ------------------------------------------------- Initialize view
	
	private void initLogicWindow() {
		initSuggestBox();
		initActivitiesCCC();
		initWorkplaces();
		initContractType();
		initAgreements();
		initPayMethods();
		fillDefaultFields();
		initFocus();
		showDialog();
	}
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			if(Boolean.TRUE.equals(hideEmployeePanel))
				employee.hideEmployeeTable();
			
			center();
			show();
		});
	}

	private void initSuggestBox() {
		WorkplaceEmployees workplaceEmployees = employeeDialogObject.getWorkplaceEmployees();
		employee.initSuggestBox(workplaceEmployees);
	}
	
	private void initActivitiesCCC() {
		employee.initActivitiesCCC(employeeDialogObject.getActivities(), employeeDialogObject.getCCCs());
	}

	private void initWorkplaces() {
		employee.initWorkplaces(employeeDialogObject.getWorkplaces());
	}

	private void initContractType() {
		employee.initContractType();
	}
	
	private void initAgreements() {
		employee.initAgreements(employeeDialogObject.getActiveAgreements());
	}
	
	private void initPayMethods() {
		employee.initPayMethods(employeeDialogObject.getPayMethods());
	}
	
	private void fillDefaultFields() {
		employee.fillDefaultFields();
		
		//WORKPLACE
		employee.workplace.setValue(null == this.employeeDialogObject.getWorkplaceId() ? "" : this.employeeDialogObject.getWorkplaceId().toString());
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.employee.workplace.getListBox());
	}
	
	private void initFocus() {
		//FOCUS DOCUMENT
		Scheduler.get().scheduleDeferred(() -> employee.document.setFocus(true));
	}
	
	// ------------------------------------------------- Initialize existing employee
	
	
	
	public void fillExistingEmployee( boolean isContractActive){
		employee.cleanErrorStyles();
		fillExistingEmployee();
		fillExistingContract();
		if(isContractActive)
		   employee.blockVariablesExistingContract();
	}

	private void fillExistingEmployee() {
		EmployeeInfo employeeData = employeeDialogObject.getEmployeeData();
		
		employee.document.setValue(employeeData.getDocument(), true);
		employee.nationality.setValue(employeeData.getNationality());
		employee.securitySocialNum.setValue(employeeData.getSsNumber(), true);
		
		employee.name.setValue(employeeData.getName());
		employee.firstSurname.setValue(employeeData.getSurName());
		employee.secondSurname.setValue(employeeData.getSecondSurName());
		
		employee.birthDate.setValue(employeeData.getBirthdate());
		employee.gender.setValue(String.valueOf(employeeData.getGender()));
		employee.civilStatus.setValue(null == employeeData.getCivilStatus() ? "" : employeeData.getCivilStatus().toString());
		
		employee.streetType.setValue(employeeData.getStreetType());
		employee.address.setValue(employeeData.getAddress());
		employee.addressNum.setValue(employeeData.getAddresNum());
		employee.addressZip.setValue(employeeData.getAddressZip());
		
		employee.selectProvince(employeeData.getAddressProvinces());
		employee.updateMunicipalities();
		employee.addressMunicipality.setValue(employeeData.getAddressCity());

		employee.mobile.setValue(employeeData.getMobile());
		employee.phone.setValue(employeeData.getPhone());
		employee.email.setValue(employeeData.getEmail());
		
		employee.payMethod.setValue(null == employeeData.getPaymethodId() ? "" : employeeData.getPaymethodId().toString());
		employee.account.setValue(employeeData.getAccount());
		employee.bic.setValue(employeeData.getBic());
		employee.reformatAccount(employee.account);
	}
	
	private void fillExistingContract() {
		ContractInfo contractData = employeeDialogObject.getContractData();
		
		if (null != contractData.getSsRegimen() && contractData.getSsRegimen() == 3) { 
			employee.showElementsFreelancerTable();
			fillContractFreelancerTable(contractData);
		} else {
			employee.hideElementsFreelancerTable();
			fillContractTable(contractData);
		}
	}

	private void fillContractFreelancerTable(ContractInfo contractData) {
		// RETA
		employee.ssRegimeType.setValue(null == contractData.getSsRegimen() ? "" : contractData.getSsRegimen().toString());
		employee.workplace.setValue(null == contractData.getWorkplaceId() ? "" : contractData.getWorkplaceId().toString());
		
		employee.seniorityDate.setValue(contractData.getSeniorityDate());
		
		Integer agreementId = contractData.getAgreementId();
		employee.agreement.setValue(employeeDialogObject.getAgreementDescription());
		if(null != agreementId) {
			getAgreementLevels(agreementId, s -> {
				employee.level.setValue(null == contractData.getAgreementLevelId() ? "" : contractData.getAgreementLevelId().toString());
				employee.category.setValue(contractData.getAgreementCategory());
			}, f -> {});
		}
		
		employee.journeyType.setValue(null == contractData.getJourneyType() ? "" : contractData.getJourneyType().toString());
	}
	
	private void fillContractTable(ContractInfo contractData) {	
		employee.ssRegimeType.setValue(null == contractData.getSsRegimen() ? "" : contractData.getSsRegimen().toString());
		employee.activityCCC.setValue(contractData.getActivityId()+"/"+contractData.getCccId()+"/"+contractData.getCccType());
		
		if(contractData.getCccType() == (byte)7) {
			employee.showMdCtzContract();
			employee.mdCTZLB.setValue(contractData.getMdctz());
		} else
			employee.hideMdCtzContract();
		
		employee.workplace.setValue(null == contractData.getWorkplaceId() ? "" : contractData.getWorkplaceId().toString());
		
		employee.contractTypeLB.setValue(contractData.getContractType());
		
		Integer contractTypeInt =  Integer.parseInt(contractData.getContractType());
		if(AonNumberUtils.between(contractTypeInt, 200, 300) || AonNumberUtils.between(contractTypeInt, 500, 599) || AonNumberUtils.equals(contractTypeInt, 0)) {
			if(employeeDialogObject.getContractData().getContractJourneyDuration().getContractJourneyDuration().entrySet().isEmpty()) {
				employee.createJourneyDurationWarning();
			} else {
				employee.createJourneyDurationInfo(employeeDialogObject.getContractData().getContractJourneyDuration().getJourneyText());
			}
		}
		
		if(AonNumberUtils.equals(contractTypeInt, 402) || AonNumberUtils.equals(contractTypeInt, 502)) {
			employee.showEmployeesColective();
			employee.employeesColective.setValue(contractData.getEmployeesColective());
		} else {
			employee.hideEmployeesColective();
			contractData.setEmployeesColective(null);
		}
		
		employee.updateModality(contractTypeInt);
		employee.modality.setValue(null == contractData.getContractModel() ? "" : contractData.getContractModel().toString());
		
		employee.seniorityDate.setValue(contractData.getSeniorityDate());
		
		Integer agreementId = contractData.getAgreementId();
		employee.agreement.setValue(employeeDialogObject.getAgreementDescription());
		if(null != agreementId) {
			getAgreementLevels(agreementId, s -> {
				employee.level.setValue(null == contractData.getAgreementLevelId() ? "" : contractData.getAgreementLevelId().toString());
				employee.category.setValue(contractData.getAgreementCategory());
			}, f -> {});
		}
		
		employee.quoteGroup.setValue(contractData.getQuoteGroup());
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.quoteGroup.getListBox());
		employee.quoteGroupCotizB.setValue(contractData.getQuoteGroupIdxMonth());
		employee.occupation.setValue(contractData.getOcupation());
		employee.rlce.setValue(contractData.getRlce());
		employee.partialityCoef.setValue(contractData.getPartialityCoef());
	}
	
	// ------------------------------------------------- Auxiliar Methods
	
	private void getAgreementLevels(Integer agreementId, Consumer<Agreement> success, Consumer<Throwable> failure) {
		employee.level.clearItems();
		employee.level.addItem("-", "-1");
		
		employeeDialogObject.getAgreement(agreementId,  
		agreement -> {
			for (Level levelRecord : agreement.getLevels())
				for (String categoryRecord : agreement.getCategoriesMap().get(levelRecord.getId()))
					employee.level.addItem(levelRecord.getDescription() + " - " + categoryRecord, String.valueOf(levelRecord.getId()));

			employeeDialogObject.setContractAgreementId(agreement.getId());
			success.accept(agreement);
		},
		throwable -> {
			employeeDialogObject.setContractAgreementId(null);
			employeeDialogObject.setContractAgreementLevelId(null);
			employee.category.setEnable(false);
			employee.category.setValue("");
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.category.getTextBox());
			failure.accept(throwable);
		});
	}
	
	// ------------------------------------------------- Buttons panel
		
	private void getButtonsPanel() {
		buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		buttonsPanel.getElement().getStyle().setProperty("margin", "1rem");
		
		Button closeBtnDialog = createButton("Cancelar");
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);
		
		acceptBtnDialog = createButton("Crear");
		acceptBtnDialog.getElement().getStyle().setProperty("color", "green");
		acceptBtnDialog.addClickHandler(e -> {
			onAcceptDialog();
		});
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private Button createButton(String text) {
		Button button = new Button(text);
		button.getElement().getStyle().setProperty("background", "none");
		button.getElement().getStyle().setProperty("background-color", "#fafafa");
		button.getElement().getStyle().setProperty("padding", "5px");
		button.getElement().getStyle().setProperty("height", "auto");
		button.getElement().getStyle().setProperty("font-size", "12px");
//		button.getElement().getStyle().setProperty("font-family", "Arial Unicode MS, Arial, sans-serif");
		button.getElement().getStyle().setProperty("text-transform", "inherit");
		button.getElement().getStyle().setProperty("font-weight", "bold");
		button.getElement().getStyle().setProperty("border", "1px solid #d0d0d0");
		button.getElement().getStyle().setProperty("border-radius", "5px");
		
		return button;
	}
	
	private void onAcceptDialog() {
		acceptBtnDialog.setEnabled(false);
		if(employee.checkIfNewEmployeeIsPossible()) {
			Map<String, String> messageMap = employee.checkSaveAndGetErrors();
			if(messageMap.isEmpty())
				this.employeeDialogObject.createEmployeeContract(
						contractId -> { 
								hide();
								
								if(null != employeeDialogObject.getWorkplaceObj()) 
									EmployeeTree.invokeRefreshWorkplace();
				
								onAccept(contractId);
							 }, 
						t -> AonMessagePanel.showError(messageContainer, t.getMessage())
				);
			else {
				AonMessagePanel.showError(messageContainer, messageMap);
			}
		} else
			acceptBtnDialog.setEnabled(true);
	}
	
}

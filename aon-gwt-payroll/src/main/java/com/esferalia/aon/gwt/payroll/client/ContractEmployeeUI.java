package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractJourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.occam.api.model.ContractParams;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public abstract class ContractEmployeeUI extends ScrollPanel {

	// ------------------------------------------------- EmployeeImpl
	
	private class EmployeeImplementation extends EmployeeWidget{
		
		// TABLA DATOS CONTRATO
		
		@Override
		public void onClearEmployeeClick() {
			// Not use in this case
		}
		
		@Override
		public void onEmployeeDocumentSuggestionChange(String document) {
			// Not use in this case
		}
		
		@Override
		public void onEmployeeDocumentChange(String document, String documentType) {
			contrataEmployeeObject.setEmployeeDocument(document);
			contrataEmployeeObject.setEmployeeDocumentType(documentType);
		}
		
		@Override
		public void onEmployeeNationalityChange(String countryIso2) {
			contrataEmployeeObject.setNationality(countryIso2);
		}
		
		@Override
		public void onEmployeeSSNumSuggestionChange(String ssNumber) {
			// Not use in this case
		}

		@Override
		public void onEmployeeSSNumChange(String ssNumber) {
			contrataEmployeeObject.setEmployeeSocialSecurityNum(ssNumber);
		}
		
		@Override
		public void onEmployeeNameSuggestionChange(String nameSurname) {
			// Not use in this case
		}
		
		@Override
		public void onEmployeeNameChange(String name) {
			contrataEmployeeObject.setEmployeeName(name);
		}

		@Override
		public void onEmployeeFirstSurnameSuggestionChange(String nameSurname) {
			// Not use in this case
		}
		
		@Override
		public void onEmployeeFirstSurnameChange(String surname) {
			contrataEmployeeObject.setEmployeeFirstSurname(surname);
		}

		@Override
		public void onEmployeeSecondSurnameChange(String secondSurname) {
			contrataEmployeeObject.setEmployeeSecondSurname(secondSurname);
		}

		@Override
		public void onContractSSRegimenChange(byte ssRegime) {
			contrataEmployeeObject.setSSRegime(ssRegime);
		}

		@Override
		public void onContractMdTBThange(String tbtType) {
			contrataEmployeeObject.setMdTBT(tbtType);
		}
		
		@Override
		public void onContractActiviesCCCChange(String activityCCC) {
			contrataEmployeeObject.setActivityInfo(activityCCC);
		}
		
		@Override
		public void onContractMdCTZhange(String mdCtz) {
			contrataEmployeeObject.setContractMdCtz(mdCtz);
		}
		
		@Override
		public void onContractWorkplaceChange(Integer workplaceId) {
			contrataEmployeeObject.setContractWorkplaceId(workplaceId);
		}

		@Override
		public void onContractTypeChange(String contractType) {
			contrataEmployeeObject.setContractType(contractType);
			contrataEmployeeObject.setContractModel(null);
		}
		
		@Override
		public void onContractModalityChange(Integer contractModel) {
			contrataEmployeeObject.setContractModel(contractModel); // GET String of enum in JooqEmployee.java
		}
		
		@Override
		public void onContractStartDateChange(Date startDate) {
			contrataEmployeeObject.setContractStartDate(startDate);
		}

		@Override
		public void onContractEndDateChange(Date endDate) {
			contrataEmployeeObject.setContractEndDate(endDate);
		}

		@Override
		public void onContractSeniorityDateChange(Date seniorityDate) {
			contrataEmployeeObject.setContractSeniorityDate(seniorityDate);	
		}

		@Override
		public void onContractAgreementChange(Integer agreementId, String agreementSSNumber) {
			contrataEmployeeObject.setContractAgreementId(agreementId);
			contrataEmployeeObject.setContractAgreementLevelId(null);
			contrataEmployeeObject.setContractCategory(null);
			contrataEmployeeObject.setAgreementSSNumber(agreementSSNumber);
			
			if(null != agreementId)
				getAgreementLevels(agreementId, s -> {}, f -> {});
		}

		@Override
		public void onContractAgreementLevelChange(Integer agreementLevelId) {
			contrataEmployeeObject.setContractAgreementLevelId(agreementLevelId);
		}
		
		@Override
		public void onContractCategoryChange(String agreementCategory) {
			contrataEmployeeObject.setContractCategory(agreementCategory);
		}
		
		@Override
		public void onContractQuoteGroupChange(String quoteGroup) {
			contrataEmployeeObject.setContractQuoteGroup(quoteGroup);
		}
		
		@Override
		public void onContractQuoteGroupIdx(boolean quoteGroupMonth) {
			contrataEmployeeObject.setContractQuoteIdxMonth(quoteGroupMonth);
		}

		@Override
		public void onContractOccupationChange(String occupation) {
			contrataEmployeeObject.setContractOccupation(occupation);
		}
		
		@Override
		public void onContractRLCEChange(String rlce) {
			contrataEmployeeObject.setContractRlce(rlce);
		}

		@Override
		public void onEmployeeCnoSuggestionChange(String cno) {
			contrataEmployeeObject.setContractCno(cno);
		}
		
		@Override
		public void onContractEmployeesColectiveChange(String employeesColective) {
			contrataEmployeeObject.setContractEmployeesColective(employeesColective);
		}

		@Override
		public void onContractJourneyTypeChange(Boolean journeyType) {
			contrataEmployeeObject.setContractJourneyType(journeyType);
		}
		
		@Override
		public void onContractPartialityChange(Double partialityCoef) {
			contrataEmployeeObject.setPartialityCoef(partialityCoef);
		}

		@Override
		public void onContractJourneyDurationClick() {
			ContractJourneyDialog dialog = new ContractJourneyDialog(
					contrataEmployeeObject.getContractStartDate(), 
					contrataEmployeeObject.getContractEndDate(),
					contrataEmployeeObject.getContractJourneyDuration()) {

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
					contrataEmployeeObject.setContractJourneyDuration(contractJourneyDuration.getContractJourneyDuration());
					employee.partialityCoef.setValue(partialityCoef);
					contrataEmployeeObject.setPartialityCoef(partialityCoef);
				}	
			};
			dialog.center();
			dialog.show();			
		}
		
		// TABLA DATOS EMPLEADO

		@Override
		public void onEmployeeBirthDateChange(Date birthDate) {
			contrataEmployeeObject.setEmployeeBirthDate(birthDate);
		}

		@Override
		public void onEmployeeGenderChange(byte gender) {
			contrataEmployeeObject.setEmployeeGender(gender);
		}
		
		@Override
		public void onEmployeeCivilStatusChange(byte civilStatus) {
			contrataEmployeeObject.setEmployeeCivilStatus(civilStatus);	
		}
		
		@Override
		public void onEmployeeStreetTypeChange(String streetType) {
			contrataEmployeeObject.setEmployeeStreetType(streetType);
		}

		@Override
		public void onEmployeeAddressChange(String address) {
			contrataEmployeeObject.setEmployeeAddress(address);
		}

		@Override
		public void onEmployeeAddressNumChange(String addressNum) {
			contrataEmployeeObject.setEmployeeAddressNumber(addressNum);
		}
		
		@Override
		public void onEmployeeAddressInfoChange(String addressInfo) {
			contrataEmployeeObject.setEmployeeAddressInfo(addressInfo);
		}

		@Override
		public void onEmployeeAddressZipChange(String addressZip) {
			contrataEmployeeObject.setEmployeeAddressZip(addressZip);
		}

		@Override
		public void onEmployeeAddressProvinceChange(Integer geozoneId) {
			contrataEmployeeObject.setEmployeeAddressProvince(geozoneId);
			contrataEmployeeObject.setEmployeeAddressCity(null);
		}
		
		@Override
		public void onEmployeeAddressMunicipalityChange(String cityName, String cityCode) {
			contrataEmployeeObject.setEmployeeAddressCity(cityName, cityCode);
		}
		
		@Override
		public void onEmployeeMobileChange(String mobile) {
			contrataEmployeeObject.setEmployeeMobile(mobile);
		}

		@Override
		public void onEmployeePhoneChange(String phone) {
			contrataEmployeeObject.setEmployeePhone(phone);
		}

		@Override
		public void onEmployeeEmailChange(String email) {
			contrataEmployeeObject.setEmployeeEmail(email);
		}

		@Override
		public void onEmployeePayMethodChange(Integer payMethodId) {
			contrataEmployeeObject.setEmployeePayMethodId(payMethodId);
		}

		@Override
		public void onEmployeeBICChange(String bic) {
			contrataEmployeeObject.setEmployeeBIC(bic);
		}
		
		@Override
		public void onEmployeeAccountChange(String account, String bankAlias, String bankSwift) {
			contrataEmployeeObject.setEmployeeAccount(account);
			contrataEmployeeObject.setEmployeeBankAlias(bankAlias);
			contrataEmployeeObject.setEmployeeBIC(bankSwift);
		}

		@Override
		public void fireError(String title, String message) {
			showError2(title, message);
		}

		@Override
		public void onUploadDni() {
			// TODO Auto-generated method stub
			
		}
	}

	// ------------------------------------------------- UiFields

	EmployeeWidget employee;
	
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private ContrataEmployeeObject contrataEmployeeObject;
	
	// ------------------------------------------------- Constructor

	protected ContractEmployeeUI() {
		this.setHeight("100%");
		employee = new EmployeeImplementation();
		setWidget(employee);
	}
	
	// ------------------------------------------------- setContrataEmployeeObject
	
	public void setContrataEmployeeObject(ContrataEmployeeObject contrataEmployeeObject, Integer contractId, Consumer<EmployeeContractInfo> success) {
		// Set variables
		this.contrataEmployeeObject = contrataEmployeeObject;		
		
	    // Select first tab and init view 
	    getTabLayoutPanel().selectTab(0, false);
		employee.initializeView();
		employee.cleanErrorStyles();
		
		// Load info and fill fields
		ContractParams newContractParams = new ContractParams( getContractParams() );
		
		// For new create contracts
		if(getPosition() == -1) {
			newContractParams
				.setContract(contractId)
				.setOffset(0)
				.setLimit(1);
			
			this.contrataEmployeeObject.getContract(newContractParams,
					employeeContractInfo -> {
						// Init toolbar
						contrataEmployeeObject.setEmployeeContractInfo(employeeContractInfo);
						getToolbar().setTitle(this.contrataEmployeeObject.getEmployeeFullName());		
						employee.initializeView();
						initLogicWindow();
						initializeIdcMonthListBox();
						initExistingEmployee(this.contrataEmployeeObject.getContractData().hasPayroll());
						success.accept(this.contrataEmployeeObject.getContractEmployeeInfo());
					}
			);
		} 
		// Contracts
		else {
			newContractParams
				.setOffset(getPosition())
				.setLimit(1); 
			
			this.contrataEmployeeObject.getContract(newContractParams,
					employeeContractInfo -> {
						// Init toolbar
						contrataEmployeeObject.setEmployeeContractInfo(employeeContractInfo);
						getToolbar().setTitle(this.contrataEmployeeObject.getEmployeeFullName());		
						employee.initializeView();
						initLogicWindow();
						initializeIdcMonthListBox();
						initExistingEmployee(this.contrataEmployeeObject.getContractData().hasPayroll());
						success.accept(this.contrataEmployeeObject.getContractEmployeeInfo());
					}
			);
		}
		
	}
	
	// ------------------------------------------------- Initialize view

	private void initLogicWindow() {
		initActivitiesCCC();
		initWorkplaces();
		initContractType();
		initAgreements();
		initPayMethods();
		initFocus();
	}
	
	private void initActivitiesCCC() {
		employee.initActivitiesCCC(contrataEmployeeObject.getActivities(), contrataEmployeeObject.getCCCs());
	}
	
	private void initWorkplaces() {
		employee.initWorkplaces(contrataEmployeeObject.getWorkplaces());
	}
	
	private void initContractType() {
		employee.initContractType();
	}
	
	private void initAgreements() {
		employee.initAgreements(contrataEmployeeObject.getActiveAgreements());
	}
	
	private void initPayMethods() {
		employee.initPayMethods(contrataEmployeeObject.getPayMethods());
	}
	
	private void initFocus() {
		//FOCUS DOCUMENT
		Scheduler.get().scheduleDeferred(() -> employee.document.setFocus(true));
	}
	
	private void initializeIdcMonthListBox() {
		Date firstMonth = null == contrataEmployeeObject.getContractStartDate() ? DateUtils.getFirstDayOfYear() : DateUtils.getFirstDayOfMonth(contrataEmployeeObject.getContractStartDate());
		Date lastMonth = DateUtils.getFirstDayOfMonth();
		getIDCMonthListBox().setFirstMonth(firstMonth);
		getIDCMonthListBox().setLastMonth(lastMonth);
		int months = DateUtils.getMonths(lastMonth, firstMonth);
		if(months >= 0)
			getIDCMonthListBox().setVisibleRange(0, months+1);
		getIDCMonthListBox().ensureDebugId("idcMonthListBox");
	}
	
	// ------------------------------------------------- Initialize Existing employee
	
	public void initExistingEmployee(boolean hasPayroll){
		fillExistingEmployee();
		fillExistingContract();
		if(hasPayroll) {
		   employee.blockVariablesExistingContract();
		   employee.blockFieldsExistingPayroll();
		} else {
		   employee.unblockVariablesExistingContract();
		   employee.unblockFieldsExistingPayroll();
		}
	}
	
	private void fillExistingEmployee() {
		EmployeeInfo employeeData = contrataEmployeeObject.getEmployeeData();
		
		employee.document.setValue(employeeData.getDocument(), true);
		employee.nationality.setValue(employeeData.getNationality());
		employee.securitySocialNum.setValue(employeeData.getSsNumber(), true);
		
		employee.name.setValue(employeeData.getName());
		employee.firstSurname.setValue(employeeData.getSurName());
		employee.secondSurname.setValue(employeeData.getSecondSurName());
		
		employee.birthDate.setValue(employeeData.getBirthdate(), true);
		employee.gender.setValue(String.valueOf(employeeData.getGender()));
		employee.civilStatus.setValue(employeeData.getCivilStatus()+"");
		
		employee.streetType.setValue(employeeData.getStreetType());
		employee.address.setValue(employeeData.getAddress());
		employee.addressNum.setValue(employeeData.getAddresNum());
		employee.addressInfo.setValue(employeeData.getAddressInfo());
		employee.addressZip.setValue(employeeData.getAddressZip());
		
		employee.selectProvince(employeeData.getAddressProvinces());
		employee.updateMunicipalities();
		employee.addressMunicipality.setValue(employeeData.getAddressCityDescription());

		employee.mobile.setValue(employeeData.getMobile());
		employee.phone.setValue(employeeData.getPhone());
		employee.email.setValue(employeeData.getEmail());
		
		employee.payMethod.setValue(employeeData.getPaymethodId()+"");
		employee.account.setValue(employeeData.getAccount());
		employee.bic.setValue(employeeData.getBic());
		employee.reformatAccount(employee.account);
	}
	
	private void fillExistingContract() {
		ContractInfo contractData = contrataEmployeeObject.getContractData();
		
		//RETA, había algo mas que determinaba si era o no RETA
		if (null != contractData.getSsRegimen() && contractData.getSsRegimen() == 3) { 
			employee.showElementsFreelancerTable();
			fillContractFreelancerTable(contractData);
			hideAfiOption();
			showHideContractOtherData(-1);
		} else {
			employee.hideElementsFreelancerTable();
			fillContractTable(contractData);
			showAfiOption();
			if(AonStringUtils.isNotBlank(contractData.getContractType()))
				showHideContractOtherData(Integer.parseInt(contractData.getContractType()));
		}
	}

	private void fillContractFreelancerTable(ContractInfo contractData) {
		// RETA
		employee.ssRegimeType.setValue(contractData.getSsRegimen()+"");
		employee.mdTBTLB.setValue(contractData.getMdTBT()+"");
		employee.workplace.setValue(contractData.getWorkplaceId()+"");
		
		employee.startDate.setValue(contractData.getStartDate());
		employee.seniorityDate.setValue(contractData.getSeniorityDate());
		employee.endDate.setValue(contractData.getEndDate());
		
		Integer agreementId = contractData.getAgreementId();
		employee.agreement.setValue(contrataEmployeeObject.getAgreementDescription());
		if(null != agreementId) {
			getAgreementLevels(agreementId, s-> {
				employee.level.setValue(contractData.getAgreementLevelId()+"");
				employee.category.setValue(contractData.getAgreementCategory());
			}, f -> {});
		}
		
		employee.rlce.setValue(contractData.getRlce());
		
		employee.journeyType.setValue((null == contractData.getJourneyType() || contractData.getJourneyType() == 0) ? "false" : "true");
//		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.journeyType);
		
		Double partialityCoef = contractData.getPartialityCoef();
		if( (null == partialityCoef || partialityCoef == 0.00)) {
			partialityCoef = calculatePartialityCoef();
			contractData.setPartialityCoef(partialityCoef);
		}
		employee.partialityCoef.setValue(contractData.getPartialityCoef());
		
		Boolean journeyTypeStr = Boolean.valueOf(employee.journeyType.getValue());
		
		if (Boolean.TRUE.equals(journeyTypeStr))
			employee.showElementsFullTimeJourneyTypeContract();
		else {
			if(null != contractData.getContractJourneyDuration() && null != contractData.getContractJourneyDuration().getContractJourneyDuration() && 
			!contractData.getContractJourneyDuration().getContractJourneyDuration().isEmpty()) {
				employee.journeyDuration.clear();
				AonCustomTextBox journeyDur = new AonCustomTextBox("Duraci\u00f3n de la jornada");
				journeyDur.setValue(contractData.getContractJourneyDuration().getJourneyText());
				journeyDur.setEnable(false);
				employee.journeyDuration.add(journeyDur);
				employee.showElementsPartialTimeContract();
			} else
				employee.showPartialTimeContract();
		}
	}
	
	private void fillContractTable(ContractInfo contractData) {
		if(contractData.getCccType() != null) {
			employee.checkCCCType(contractData.getCccType());
			employee.activityCCC.setValue(contractData.getActivityId()+"/"+contractData.getCccId()+"/"+contractData.getCccType());
		
			if(contractData.getCccType() == (byte)7) {
				employee.showMdCtzContract();
				employee.mdCTZLB.setValue(contractData.getMdctz());
			} else
				employee.hideMdCtzContract();
		}
		
		employee.ssRegimeType.setValue(contractData.getSsRegimen()+"");
		employee.mdTBTLB.setValue(contractData.getMdTBT()+"");
		
		employee.workplace.setValue(contractData.getWorkplaceId()+"");
		
		employee.contractTypeLB.setValue(contractData.getContractType());
		employee.contractFireEventsWithOutValue();
		
		if(!isCompleteJourneyContract(contractData.getContractType())) {
			employee.showPartialTimeContract();
			if(contrataEmployeeObject.getContractData().getContractJourneyDuration().getContractJourneyDuration().entrySet().isEmpty())
				employee.createJourneyDurationWarning();
			else
				employee.createJourneyDurationInfo(contrataEmployeeObject.getContractData().getContractJourneyDuration().getJourneyText());
			
			if(null != contractData.getJourneyType()) {
				employee.journeyType.setValue(contractData.getJourneyType() == 0 ? "false" : "true");
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.journeyType.getListBox()); 
			}
			
			if(null != contractData.getContractJourneyDuration() && null != contractData.getContractJourneyDuration().getContractJourneyDuration() && 
					!contractData.getContractJourneyDuration().getContractJourneyDuration().isEmpty()) {
				employee.journeyDuration.clear();
				AonCustomTextBox journeyDur = new AonCustomTextBox("Duraci\u00f3n de la jornada");
				journeyDur.setValue(contractData.getContractJourneyDuration().getJourneyText());
				journeyDur.setEnable(false);
				employee.journeyDuration.add(journeyDur);
			}
				
			Double partialityCoef = contractData.getPartialityCoef();
			if(null == partialityCoef) partialityCoef = calculatePartialityCoef();
			contractData.setPartialityCoef(partialityCoef);
			employee.partialityCoef.setValue(contractData.getPartialityCoef());	
			
		} else
			employee.showElementsFullTimeContract();
		
		try {
			Integer contractTypeInt = Integer.parseInt(contractData.getContractType());
			if(AonNumberUtils.equals(contractTypeInt, 402) || AonNumberUtils.equals(contractTypeInt, 502)) {
				employee.showEmployeesColective();
				employee.employeesColective.setValue(contractData.getEmployeesColective());
			} else {
				employee.hideEmployeesColective();
				contractData.setEmployeesColective(null);
			}
		} catch (Exception e) {
			// Nothing to do here
		}
		
		employee.modality.setValue(contractData.getContractModel()+"");
		
		employee.startDate.setValue(contractData.getStartDate());
		employee.seniorityDate.setValue(contractData.getSeniorityDate());
		employee.endDate.setValue(contractData.getEndDate());
		
		Integer agreementId = contractData.getAgreementId();
		employee.agreement.setValue(contrataEmployeeObject.getAgreementDescription());
		if(null != agreementId) {
			getAgreementLevels(agreementId, s -> {
				employee.level.setValue(contractData.getAgreementLevelId()+"");
				employee.category.setValue(contractData.getAgreementCategory());
			}, f -> {});
		}
		
		employee.quoteGroup.setValue(contractData.getQuoteGroup());
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.quoteGroup.getListBox());
		employee.quoteGroupCotizB.setValue(contractData.getQuoteGroupIdxMonth());
		employee.occupation.setValue(contractData.getOcupation());
		employee.rlce.setValue(contractData.getRlce());
		
		CNO cno = employee.getCNOByCode(contractData.getCno());
		if(cno != null) employee.cnoSB.setValue(cno.getCode() + " - " + cno.getTitle());
		if(!employee.isCnoSelected()) showWarningMessage("CNO", "El CNO es obligatorio para todas las altas a partir del 01/01/2023");
	}
	
	private static boolean isCompleteJourneyContract(String contractTypeCodeStr) {
		if(AonStringUtils.isBlank(contractTypeCodeStr)) return false;
		
		ContractType contractTypeObj = new ContractType();
		ContractTypeRecord contractType = contractTypeObj.getContractType(Integer.parseInt(contractTypeCodeStr));
		return AonStringUtils.equalsIgnoreCase(contractType.getJourneyType(), "C");
	}
	
	private Double calculatePartialityCoef() {
		Double hours = 0.00;
		if(null != contrataEmployeeObject.getContractData().getContractJourneyDuration() && null != contrataEmployeeObject.getContractData().getContractJourneyDuration().getContractJourneyDuration() && 
				!contrataEmployeeObject.getContractData().getContractJourneyDuration().getContractJourneyDuration().isEmpty()) {
			for(JourneyDuration journeyDuration : contrataEmployeeObject.getContractData().getContractJourneyDuration().getContractJourneyDuration().descendingMap().entrySet().iterator().next().getValue()) {
				if(AonStringUtils.isNotBlank(journeyDuration.getExpression()) && !AonStringUtils.equals(journeyDuration.getExpression(), "NL")){
					String expression = journeyDuration.getExpression();
					expression = expression.replace(",", ".");
					hours += Double.parseDouble(expression);
				}
			}
			
			hours = hours / 40;
		}
				
		return Math.round(hours * 100.0) / 100.0;
	}

	// ------------------------------------------------- Auxiliar Methods
	
	private void getAgreementLevels(Integer agreementId, Consumer<Agreement> success, Consumer<Throwable> failure) {
		employee.level.clearItems();
		employee.level.addItem("-", "-1");
		
		contrataEmployeeObject.getAgreement(agreementId,  
		agreement -> {
			for (Level levelRecord : agreement.getLevels()) {
				employee.level.addItem(levelRecord.getDescription(), String.valueOf(levelRecord.getId()));
				
				for (String categoryRecord : agreement.getCategoriesMap().get(levelRecord.getId()))
					employee.level.addItem(levelRecord.getDescription() + " - " + categoryRecord, String.valueOf(levelRecord.getId()));
			}

			contrataEmployeeObject.setContractAgreementId(agreement.getId());
			success.accept(agreement);
		},
		throwable -> {
			contrataEmployeeObject.setContractAgreementId(null);
			contrataEmployeeObject.setContractAgreementLevelId(null);
			employee.category.setEnable(false);
			employee.category.setValue("");
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.category.getTextBox());
			failure.accept(throwable);
		});
	}
	
	private void showHideContractOtherData(Integer contractType) {
		if((-1 != contractType))
			getTabLayoutPanel().getTabWidget(1).getElement().getStyle().clearDisplay();
		else
			getTabLayoutPanel().getTabWidget(1).getElement().getStyle().setDisplay(Display.NONE);
	}

	public Map<String, String> checkSaveAndGetErrors() {
		return employee.checkSaveAndGetErrors();
	}

	public void showError2(String title, String message) {
		showErrorMessage(title, message);
	}
	
	// ------------------------------------------------- Getters
	
	public Date getStartDate() {
		return this.employee.startDate.getValue();
	}

	public Date getEndDate() {
		return this.employee.endDate.getValue();
	}

	public String getContractType() {
		return this.employee.contractTypeLB.getValue();
	}

	public String getQuoteGroup() {
		return this.employee.quoteGroup.getValue();
	}

	public String getOccupation() {
		return this.employee.occupation.getValue();
	}

	public Double getPartialityCoef() {
		return this.employee.partialityCoef.getValue();
	}
	
	// ------------------------------------------------- Abstract Methods
	
	protected abstract TabLayoutPanel getTabLayoutPanel();
	protected abstract AonToolbar getToolbar();
	protected abstract MonthListBox getIDCMonthListBox();
	protected abstract void showErrorMessage(String title, String message);
	protected abstract void showWarningMessage(String title, String message);
	protected abstract void showAfiOption();
	protected abstract void hideAfiOption();
	protected abstract ContractParams getContractParams();
	protected abstract Integer getPosition();
	
}

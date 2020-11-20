package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.MultiFileUpload;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx.DefaultFormat;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractJourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ContractTypeRecord;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ModelRecord;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsITEmployee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.FIEService;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.Iban;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.Municipalities;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeDialog extends AonCustomDialog {
	
	@SuppressWarnings("deprecation")
	private class EmployeeImplementation extends Employee{
		ContractType contractTypeClass = new ContractType();
		
		// TABLA DATOS CONTRATO
		
		@Override
		public void onClearEmployeeClick() {
			employeeDialogObject.resetEmptyInfo();
			fillExistingEmployee();
			unblockVariablesExistingContract();
			employee.clear_employee.getElement().getStyle().setDisplay(Display.NONE);
		}
		
		@Override
		public void onEmployeeDocumentSuggestionChange() {
			String document = this.document.getValue();
			if(!StringUtils.isBlank(document)) {
				EmployeeInfo existingEmployee = employeeDialogObject.getEmployeeDataByDocument(document);
				if(null == existingEmployee)
					employeeDialogObject.setEmployeeDocument(document);
				else {
					employeeDialogObject.initializeEmployee(
							existingEmployee.getContractId(),
							s -> { 
								initExistingEmployee(existingEmployee.getContractActive());
							}, 
							f -> {}
					);	
				}
			}
		}
		
		@Override
		public void onEmployeeDocumentChange() {
			String document = this.document.getValue();
			if(!StringUtils.isBlank(document)) {
				document = document.trim();
				this.document.setValue(document);
				
				String document_type = checkDocumentType(document);
				this.document_type.setText(document_type);
				
				employeeDialogObject.setEmployeeDocument(document);
				employeeDialogObject.setEmployeeDocumentType(document_type);
				
				if(employeeDialogObject.checkDocumentValidation(document_type, document))
					addSuccessStyle(this.documentStatus);
				else
					addWarningStyle(this.documentStatus);
					
				showNationality(document_type);	
			}
		}
		
		@Override
		public void onEmployeeNationalityChange() {
			String countryIso2 = getIso2(this.nationality.getValue());
			employeeDialogObject.setNationality(countryIso2);
		}
		
		@Override
		public void onEmployeeSSNumSuggestionChange() {
			String ssNum = this.security_social_num.getValue();
			if(!StringUtils.isBlank(ssNum)) {
				EmployeeInfo existingEmployee = employeeDialogObject.getEmployeeDataBySSNum(ssNum);
				if(null == existingEmployee.getEmployeeId())
					employeeDialogObject.setEmployeeSocialSecurityNum(ssNum);
				else
					employeeDialogObject.initializeEmployee(
							existingEmployee.getContractId(),
							s -> { 
								initExistingEmployee(existingEmployee.getContractActive());
							}, 
							f -> {}
					);		
			}	
		}

		@Override
		public void onEmployeeSSNumChange() {
			String ssNum = this.security_social_num.getValue();
			
			if(!StringUtils.isBlank(ssNum)) {
				ssNum = ssNum.trim();
				this.security_social_num.setValue(ssNum);
				
				employeeDialogObject.setEmployeeSocialSecurityNum(ssNum);
				if(employeeDialogObject.checkSSNumValidation(ssNum))
					addSuccessStyle(this.ssNumberStatus);
				else
					addWarningStyle(this.ssNumberStatus);
			}
		}
		
		@Override
		public void onEmployeeNameSuggestionChange() {
			String nameSurname = this.name.getValue();
			
			if(!StringUtils.isBlank(nameSurname)) {
				EmployeeInfo existingEmployee = employeeDialogObject.getEmployeeDataByNameSurname(nameSurname);
				
				if(null == existingEmployee.getEmployeeId()) {
					String name = nameSurname.split(", ")[0];
					name = name.trim();
					this.name.setValue(name);
					employeeDialogObject.setEmployeeName(name);
				}else
					employeeDialogObject.initializeEmployee(
							existingEmployee.getContractId(),
							r -> { 
								initExistingEmployee(existingEmployee.getContractActive());
							}, 
							t -> {}
					);
			}
		}
		
		@Override
		public void onEmployeeNameChange() {
			String name = this.name.getValue();
			if(!StringUtils.isBlank(name)) {
				name = name.trim();
				this.name.setValue(name);
				employeeDialogObject.setEmployeeName(name);	
			}
		}

		@Override
		public void onEmployeeFirstSurnameSuggestionChange() {
			String nameSurname = this.first_surname.getValue();
			
			if(!StringUtils.isBlank(nameSurname)) {
				EmployeeInfo existingEmployee = employeeDialogObject.getEmployeeDataByNameSurname(nameSurname);
				
				if(null == existingEmployee.getEmployeeId()) {
					String surName = nameSurname.split(", ")[1];
					employeeDialogObject.setEmployeeFirstSurname(surName);
				}else
					employeeDialogObject.initializeEmployee(
							existingEmployee.getContractId(),
							r -> { 
								initExistingEmployee(existingEmployee.getContractActive());
							}, 
							t -> {}
					);	
			}
		}
		
		@Override
		public void onEmployeeFirstSurnameChange() {
			String surname = this.first_surname.getValue();
			if(!StringUtils.isBlank(surname)) {
				surname = surname.trim();
				this.first_surname.setValue(surname);
				employeeDialogObject.setEmployeeFirstSurname(surname);
			}
		}

		@Override
		public void onEmployeeSecondSurnameChange() {
			String secondSurname = this.second_surname.getValue();
			if(!StringUtils.isBlank(secondSurname)) {
				secondSurname = secondSurname.trim();
				this.second_surname.setValue(secondSurname);
				employeeDialogObject.setEmployeeSecondSurname(secondSurname);
			}
		}

		@Override
		public void onContractSSRegimenChange() {
			byte ssRegime = Byte.valueOf(this.ssRegimeType.getSelectedValue()).byteValue();
			employeeDialogObject.setSSRegime(ssRegime);
			
			if(1 == ssRegime){
				this.showElementsFreelancerTable();
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.journeyType);
			}
			else
				this.hideElementsFreelancerTable();
		}
		
		@Override
		public void onContractActiviesCCCChange() {
			String activityCCC = String.valueOf(this.activityCCC.getSelectedValue());
			
			if(this.activityCCC.getSelectedIndex() == 0)
				employeeDialogObject.setActivityInfo(null);
			else
				employeeDialogObject.setActivityInfo(activityCCC);
		}
		
		@Override
		public void onContractWorkplaceChange() {
			Integer workplaceId = Integer.parseInt(this.workplace.getSelectedValue());
			employeeDialogObject.setContractWorkplaceId(workplaceId);
		}

		@Override
		public void onContractTypeChange() {
			this.modality.clear();
			this.modality.addItem("-");
			
			String contractType = null;
			
			if(this.contractType.getSelectedIndex() == 0) {
				employeeDialogObject.setContractType(contractType);
				employeeDialogObject.setContractModel(null);
			}else {
				contractType = String.valueOf(this.contractType.getSelectedValue());
				Integer contractTypeInt = Integer.parseInt(contractType);
				
				if((contractTypeInt >= 200 && contractTypeInt<300) || (contractTypeInt >= 500 && contractTypeInt<600) || contractTypeInt == 0)
					showPartialTimeContract();
				else
					this.showElementsFullTimeContract();
				
				List<ModelRecord> contractTypeModels = contractTypeClass.getModelsContractType(contractTypeInt);
				for (ModelRecord model : contractTypeModels)
					this.modality.addItem(model.getModelDescription(), model.getEnumeration().toString());
				
				employeeDialogObject.setContractType(contractType);
			}
		}
		
		@Override
		public void onContractModalityChange() {
			if (this.modality.getSelectedIndex() == 0)
				employeeDialogObject.setContractModel(null);
			else {
				Integer contractModel = Integer.parseInt(this.modality.getSelectedValue());
				employeeDialogObject.setContractModel(contractModel); // GET String of enum in JooqEmployee.java
			}
		}
		
		@Override
		public void onContractStartDateChange() {
			if(null != this.start_date.getValue()) {
				DefaultFormat format = new DefaultFormat();
				Date date = format.parse(this.start_date, this.start_date.getTextBox().getValue(), false);
				this.start_date.setValue(date);
				employeeDialogObject.setContractStartDate(date);
				
				this.seniority_date.setValue(date, true);
			} else
				employeeDialogObject.setContractStartDate(this.start_date.getValue());
		}

		@Override
		public void onContractEndDateChange() {
			if(null != this.end_date.getValue()) {
				DefaultFormat format = new DefaultFormat();
				Date date = format.parse(this.end_date, this.end_date.getTextBox().getValue(), false);
				this.end_date.setValue(date);
				employeeDialogObject.setContractEndDate(date);
			} else
				employeeDialogObject.setContractEndDate(this.end_date.getValue());
		}

		@Override
		public void onContractSeniorityDateChange() {
			Date startDate = this.start_date.getValue();
			Date seniorityDate = this.seniority_date.getValue();
			
			if(null == startDate)
				addWarningDateStyle(this.seniority_dateStatus);
			else if(null != seniorityDate) {
				DateUtils.resetTime(startDate);
				DateUtils.resetTime(seniorityDate);
				
				if(startDate.equals(seniorityDate))
					this.seniority_dateStatus.getElement().getStyle().setDisplay(Display.NONE);
				else
					addWarningDateStyle(this.seniority_dateStatus);
				
				DefaultFormat format = new DefaultFormat();
				Date date = format.parse(this.seniority_date, this.seniority_date.getTextBox().getValue(), false);
				this.seniority_date.setValue(date);
			} else
				this.seniority_dateStatus.getElement().getStyle().setDisplay(Display.NONE);
			
			employeeDialogObject.setContractSeniorityDate(seniorityDate);	
		}

		@Override
		public void onContractAgreementChange() {
			
			if (this.agreement.getSelectedIndex() == 0 ) {
				employeeDialogObject.setContractAgreementId(null);
				employeeDialogObject.setContractAgreementLevelId(null);
				this.level.clear();
				this.category.setValue("");
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.category);
				return;
			}
			
			Integer agreementId =  Integer.valueOf(this.agreement.getSelectedValue()); 
			getAgreementLevels(agreementId);
			
		}

		@Override
		public void onContractAgreementLevelChange() {
			if (this.agreement.getSelectedIndex() == 0 || this.level.getSelectedIndex() == 0) {
				employeeDialogObject.setContractAgreementLevelId(null);
				this.category.setEnabled(false);
				this.category.setValue("");
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.category);
			} else {
				Integer agreementLevelId = Integer.parseInt(this.level.getSelectedValue());
				employeeDialogObject.setContractAgreementLevelId(agreementLevelId);
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
			else {
				String quoteGroup = String.valueOf(this.quote_group.getSelectedValue()); 
				employeeDialogObject.setContractQuoteGroup(quoteGroup);
			}
		}

		@Override
		public void onContractOccupationChange() {
			if (this.occupation.getSelectedIndex() == 0)
				employeeDialogObject.setContractOccupation(null);
			else {
				String occupation = String.valueOf(this.occupation.getSelectedValue());
				employeeDialogObject.setContractOccupation(occupation);
			}
		}

		@Override
		public void onContractJourneyTypeChange() {
			 Boolean journey_type = (this.journeyType.getSelectedIndex() == 0) ? true : false;
			 employeeDialogObject.setContractJourneyType(journey_type);
			 if(this.journeyType.getSelectedIndex() == 0)
				 employee.showElementsFullTimeContract();
			 else
				 showPartialTimeContract();
		}

		@Override
		public void onContractJourneyDurationClick() {
			ContractJourneyDialog dialog = new ContractJourneyDialog(employeeDialogObject.getContractStartDate(), employeeDialogObject.getContractEndDate()) {

				@Override
				protected void onSave() {
					ContractJourneyDuration contractJourneyDuration = this.getContractJourneyDuration();
					if(contractJourneyDuration.getJourniesSize() != 0) {
						String result = "Desde ";
						for(JourneyDuration journeyDuration : contractJourneyDuration.getContractJourneyDuration().descendingMap().entrySet().iterator().next().getValue()) {
							if("HORAS_LUNES" == journeyDuration.getName()) result += formatDate(journeyDuration.getStartDate()) + " ( L : " + journeyDuration.getExpression() + " ";
							if("HORAS_MARTES" == journeyDuration.getName()) result += ", M : " + journeyDuration.getExpression() + " ";
							if("HORAS_MIERCOLES" == journeyDuration.getName()) result += ", X : " + journeyDuration.getExpression() + " ";
							if("HORAS_JUEVES" == journeyDuration.getName()) result += ", J : " + journeyDuration.getExpression() + " ";
							if("HORAS_VIERNES" == journeyDuration.getName()) result += ", V : " + journeyDuration.getExpression() + " ";
							if("HORAS_SABADO" == journeyDuration.getName()) result += ", S : " + journeyDuration.getExpression() + " ";
							if("HORAS_DOMINGO" == journeyDuration.getName()) result += ", D : " + journeyDuration.getExpression() + " )";
						}
						employee.journeyDuration.setText(result);
						employee.journeyDuration.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
					}else {
						employee.journeyDuration.addStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
						employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
						employee.journeyDuration.setText("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
					}
					employeeDialogObject.setContractJourneyDuration(contractJourneyDuration.getContractJourneyDuration());
				}	
			};
			dialog.center();
			dialog.show();			
		}
		
		// TABLA DATOS EMPLEADO

		@Override
		public void onEmployeeBirthDateChange() {
			Date birthDate = this.birth_date.getValue();
			
			if(null != birthDate) {
				Date actualDay = new Date();
				Integer age = getYears(actualDay, birthDate);
				this.age.setText("( " + (age) + " a" + String.valueOf("\u00F1") + "os )");
				
				DefaultFormat format = new DefaultFormat();
				Date date = format.parse(this.birth_date, this.birth_date.getTextBox().getValue(), false);
				this.birth_date.setValue(date);
			} else
				this.age.setText("");
			
			employeeDialogObject.setEmployeeBirthDate(this.birth_date.getValue());
			
		}

		@Override
		public void onEmployeeGenderChange() {
			byte gender = Byte.valueOf(this.gender.getSelectedValue()).byteValue();
			employeeDialogObject.setEmployeeGender(gender);
		}
		
		@Override
		public void onEmployeeCivilStatusChange() {
			byte civilStatus = Byte.valueOf(this.civilStatus.getSelectedValue()).byteValue();
			employeeDialogObject.setEmployeeCivilStatus(civilStatus);	
		}
		
		@Override
		public void onEmployeeStreetTypeChange() {
			String streetType = String.valueOf(this.street_type.getSelectedValue());
			employeeDialogObject.setEmployeeStreetType(streetType);
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
		public void onEmployeeAddressInfoChange() {
			employeeDialogObject.setEmployeeAddressInfo(this.addressInfo.getValue());
		}

		@Override
		public void onEmployeeAddressZipChange() {
			employeeDialogObject.setEmployeeAddressZip(this.addressZip.getValue());
			if(this.addressZip.getValue().length() >= 2) {
				String zip = this.addressZip.getValue().substring(0, 2);
				setSelectedValueLB(addressProvince, zip); 
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), addressProvince);
			}
		}

		@Override
		public void onEmployeeAddressMunicipalityChange() {
			employeeDialogObject.setEmployeeAddressCity(municipalities.getZipByMunicipalityName(this.addressMunicipality.getSelectedItemText()).toString());
		}

		@Override
		public void onEmployeeAddressProvinceChange() {
			String addressProvinceCode = this.addressProvince.getSelectedValue();
			employeeDialogObject.setEmployeeAddressProvince(addressProvinceCode);
			employeeDialogObject.setEmployeeAddressCity("-1");
			updateMunicipalities();
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
			Integer payMethodId = Integer.parseInt(this.payMethod.getSelectedValue());
			employeeDialogObject.setEmployeePayMethodId(-1 == payMethodId ? null : payMethodId);
			
//			byte methodPay = Byte.valueOf(this.payMethod.getSelectedValue()).byteValue();
//			employeeDialogObject.setEmployeePayMethod(methodPay);
//			
//			this.account.setValue(null);
//			this.bic.setValue(null);
			
//			if(this.payMethod.getSelectedIndex() == 3) { //TRANFERENCIA
//				this.account.setEnabled(true);
//				this.bic.setEnabled(true);
//				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), account);
//				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), bic);
//			} else {
//				this.account.setEnabled(false);
//				this.bic.setEnabled(false);
//			}
		}

		@Override
		public void onEmployeeBICChange() {
			employeeDialogObject.setEmployeeBIC(this.bic.getValue());
		}
		
		@Override
		public void onEmployeeAccountChange() {
			String account = this.account.getValue();
			account = account.replaceAll("\\W+", "");
			if(account.length() > 0) {
				if(Iban.validateIBAN(account))
					addSuccessStyle(this.accountStatus);
				else
					addWarningStyle(this.accountStatus);
			}
			
			employeeDialogObject.setEmployeeAccount(account);
		}
		
	}	

	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface Binder extends UiBinder<Widget, EmployeeDialog> {}
	
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField (provided = true)
	Employee employee;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
//	interface Callback {
//		void onAccept(EmployeeDialog dialog);
//	}
	
//	private Callback cb;
	private EmployeeDialogObject employeeDialogObject;
	private ContractType contractType;
	private Municipalities municipalities;
	
	private AonButton closeBtnDialog;
	private AonButton acceptBtnDialog;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public EmployeeDialog() {
		employee = new EmployeeImplementation();
		
		setCaption("Trabajador");
		setWidget(binder.createAndBindUi(this));
		
		getButtonsPanel();
		
		employee.clear_employee.getElement().getStyle().setDisplay(Display.NONE);
		employee.account.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reformatAccount(employee.account);
			}
		});
	}
	
	public EmployeeDialog(Boolean isContrataCall) {
		employee = new EmployeeImplementation();
		
		setCaption("Trabajador");
		setWidget(binder.createAndBindUi(this));
		
		getButtonsPanel();
		
		employee.clear_employee.getElement().getStyle().setDisplay(Display.NONE);
		employee.account.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reformatAccount(employee.account);
			}
		});
		
		if(isContrataCall)
			hideEmployeeTable();
	}
	
	// -------------------------------------------------- UiHandlers --------------------------------------------------
	
//	@UiHandler("cancelButton")
//	void onCancelButtonClick(ClickEvent clickEvent) {
//		hide();
//	}
//	
//	@UiHandler("acceptButton")
//	void onAcceptButtonClick(ClickEvent clickEvent) {
//		int ssRegime = employee.ssRegimeType.getSelectedIndex();
//		employeeDialogObject.setSSRegime(ssRegime);
//		
//		if(checkIfSaveIsPossible())
//			if(checkDates())
////				if(checkPayMethod())
//				this.employeeDialogObject.createEmployeeContract(
//						r -> { 
//								hide();
//								if(null != employeeDialogObject.getWorkplaceObj()) 
//									EmployeeTree.invokeRefreshWorkplace();
//				
//								onAccept();
////									cb.onAccept(this);
//								
//							 }, 
//						t -> {}
//				);
////				else {
////					WarningDialog dialog = new WarningDialog("Aviso", "Si el metodo de pago es transferencia, debe rellenar obligatoriamente los campos de BIC y cuenta.");
////					dialog.center();
////					dialog.show();
////				}
//					
//			else{
//				WarningDialog dialog = new WarningDialog("Aviso", "La fecha de inicio no puede ser posterior a la fecha de fin.");
//				dialog.center();
//				dialog.show();
//			}
//		else {
//			WarningDialog dialog = new WarningDialog("Aviso", "Hay que rellenar los campos azules correcta y obligatoriamente.");
//			dialog.center();
//			dialog.show();
//		}
//	}
	
	protected abstract void onAccept();
	
	private boolean checkIfSaveIsPossible() {
		//Check name, birthDate and contract startDate
		if( "" == this.employee.name.getValue() || null == this.employee.start_date.getValue())
			return false;
		
		//Check SSRegime RETA
		if(1 == this.employee.ssRegimeType.getSelectedIndex())
			return true;
		
		//Check Contract type
		if(0 == this.employee.contractType.getSelectedIndex())
			return false;
		
		//Check Activity if SSRegime not RETA
		if( 0 == this.employee.activityCCC.getSelectedIndex())
			return false;
		
		if(null != this.employeeDialogObject.getWorkplaceObj()) {
			if(StringUtils.isBlank(this.employee.addressZip.getValue()))
				return false;
			
			if(this.employee.addressMunicipality.getSelectedIndex() == 0)
				return false;
			
			if(this.employee.addressProvince.getSelectedIndex() == 0)
				return false;
		}
		
		return true;
	}
	
	private boolean checkDates() {
		if(null == this.employee.end_date.getValue())
			return true;
		else if(this.employee.end_date.getValue().after(this.employee.start_date.getValue()))
			return true;
		else
			return false;
	}
	
//	private boolean checkPayMethod() {
//		if(4 == this.employee.payMethod.getSelectedIndex()) {
//			if("" == this.employee.bic.getValue() || "" == this.employee.account.getValue())
//				return false;
//			else
//				return true;
//		} else
//			return true;
//	}
	
	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------
	
	public void setEmployeeDialogObject(EmployeeDialogObject employeeDialogObject) {
		this.employeeDialogObject = employeeDialogObject;
		this.contractType = new ContractType();
		this.municipalities = new Municipalities();
		
		this.employeeDialogObject.getWorkplaceEmployees(
				r -> {
					initLogicWindow();
				}, 
				t -> {}
		);
	}
	
	private void initLogicWindow() {
		initSuggestBox();
		initActivitiesCCC();
		initWorkplaces();
		initContractType();
		initAgreements();
		initPayMethods();
		fillDefaultFields();
		initFocus();	
	}

	private void hideEmployeeTable() {
		employee.employeeTablePanel.getElement().getStyle().setDisplay(Display.NONE);
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
		this.employee.document.setAutoSelectEnabled(false);
		
		//SS_NUMBER
		List<String> employeesSSNumbers = workplaceEmployees.getWorkplaceEmployeesSSNumber();
		List<String> employeesSSNumbersSuggest = new ArrayList<String>();
		for(String ssNumber : employeesSSNumbers)
			employeesSSNumbersSuggest.add(ssNumber+"");
		MultiWordSuggestOracle orclSSNumbers = (MultiWordSuggestOracle) this.employee.security_social_num.getSuggestOracle();
		orclSSNumbers.addAll(employeesSSNumbersSuggest);
		this.employee.security_social_num.setAutoSelectEnabled(false);
		
		//NAMES
		List<String> employeesNames = workplaceEmployees.getWorkplaceEmployeesName();
		List<String> employeesNamesSuggest = new ArrayList<String>();
		for(String name : employeesNames)
			employeesNamesSuggest.add(name+"");
		MultiWordSuggestOracle orclNames = (MultiWordSuggestOracle) this.employee.name.getSuggestOracle();
		orclNames.addAll(employeesNamesSuggest);
		this.employee.name.setAutoSelectEnabled(false);
		
		//SURNAME
		List<String> employeesSurNames = workplaceEmployees.getWorkplaceEmployeesSurName();
		List<String> employeesSurNamesSuggest = new ArrayList<String>();
		for(String surName : employeesSurNames)
			employeesSurNamesSuggest.add(surName+"");
		MultiWordSuggestOracle orclSurNames = (MultiWordSuggestOracle) this.employee.first_surname.getSuggestOracle();
		orclSurNames.addAll(employeesSurNamesSuggest);
		this.employee.first_surname.setAutoSelectEnabled(false);
	}
	
	private void initActivitiesCCC() {
		//ACTIVITY - CCC
		this.employee.activityCCC.addItem("-");
		if(null != this.employeeDialogObject.getActivities())
			for(Entry<Integer,String> entry : this.employeeDialogObject.getActivities().entrySet())
				for(CCCInfo cccInfo :  this.employeeDialogObject.getCCCs().values())
					if(cccInfo.getActivityId() == entry.getKey())
						this.employee.activityCCC.addItem(entry.getValue() + " - " + getCCCType(cccInfo.getType()) + "[" + cccInfo.getCcc() + "] - " +  cccInfo.getGeozone(), cccInfo.getActivityId() + "/" + cccInfo.getCccId() + "/" + cccInfo.getType());
//						this.employee.activityCCC.addItem(entry.getValue() + " - " + CCCType.values()[cccInfo.getType()] + "[" + cccInfo.getCcc() + "] - " +  cccInfo.getGeozone(), cccInfo.getActivityId() + "/" + cccInfo.getCccId() + "/" + cccInfo.getType());
	}

	private void initWorkplaces() {
		//WORKPLACE
		for(Workplace workplace : employeeDialogObject.getWorkplaces())
			this.employee.workplace.addItem(workplace.getDescription(), workplace.getId().toString());
	}

	private void initContractType() {
		// TIPO DE CONTRATO
		this.employee.contractType.addItem("-", "-1");
		for (Entry<Integer, ContractTypeRecord> entry : contractType.getContractTypes().entrySet())
			this.employee.contractType.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription(), entry.getKey().toString());	
	}
	
	private void initAgreements() {
		// CONVENIO
		this.employee.agreement.addItem("-", "-1");
		List<Agreement> agreements = employeeDialogObject.getActiveAgreements();
		for (Agreement agreement : agreements)
			this.employee.agreement.addItem(agreement.getDescription(), String.valueOf(agreement.getId()));
	}
	
	private void initPayMethods() {
		this.employee.payMethod.clear();
		this.employee.payMethod.addItem("-", "-1");
		for(Entry<String, String> entry : employeeDialogObject.getPayMethods().entrySet()) {
			this.employee.payMethod.addItem(entry.getKey(), entry.getValue());
		}
	}
	
	private void fillDefaultFields() {
		//SS REGIME
		this.employee.ssRegimeType.setSelectedIndex(0);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.employee.ssRegimeType);
		
		//ACTIVITY CCC
//		if(this.employee.activityCCC.getItemCount() == 2) {
//			this.employee.activityCCC.setSelectedIndex(1);
//			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.employee.activityCCC);
//		}
		
		//WORKPLACE
		if(null != employeeDialogObject.getWorkplaceObj()) {
			setSelectedValueLB(employee.workplace, this.employeeDialogObject.getWorkplaceId().toString());
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.employee.workplace);
		}else {
			employee.workplace.setSelectedIndex(0);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.employee.workplace);
		}
		
		//AGREEMENT
		setSelectedValueLB(employee.agreement, this.employeeDialogObject.getWorkplaceAgreement().toString());
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.employee.agreement);
		
		//GENDER
		this.employee.gender.setSelectedIndex(0);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.employee.gender);
		
		//CIVIL STATUS
		this.employee.civilStatus.setSelectedIndex(5);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.employee.civilStatus);
		
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
	
	// ----------------------------------------------- METODOS AUXILIARES ------------------------------------------------
	
	private void reformatAccount(SuggestBox accountField) {
	    String accountText = accountField.getText();
	    accountText = accountText.replaceAll("\\W+", "");
	    if (accountText.length() >= 24) {
	    	accountField.setText(accountText.substring(0, 4) + "  " + accountText.substring(4, 8) + "  " + accountText.substring(8, 12) + "  " + accountText.substring(12, 16)
	    	+ "  " + accountText.substring(16, 20) + "  " + accountText.substring(20, 24));
	    }
	}
	
	// ----------------------------------------- METODOS AUXILIARES (EMPLOYEE) -------------------------------------------
	
	public String checkDocumentType(String document) {
		
		if(null == document)
			return "Pasaporte";

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
	
	public void initExistingEmployee( boolean isContractActive){
		fillExistingEmployee();
		if(isContractActive)
		   blockVariablesExistingContract();
		employee.clear_employee.getElement().getStyle().clearDisplay();
	}
	
	private void fillExistingEmployee() {
		EmployeeInfo employeeData = employeeDialogObject.getEmployeeData();
		ContractInfo contractData = employeeDialogObject.getContractData();
		
		if (null != contractData.getSsRegimen() && contractData.getSsRegimen() == 3) { //RETA, había algo mas que determinaba si era o no RETA
			employee.showElementsFreelancerTable();
			fillContractFreelancerTable();
		} else {
			employee.hideElementsFreelancerTable();
			fillContractTable();
		}
		
		employee.document.setValue(employeeData.getDocument());
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.document);
		
		employee.nationality.setValue(employeeData.getNationality());
		
		employee.security_social_num.setValue(employeeData.getSsNumber());
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.security_social_num);
		
		employee.name.setValue(employeeData.getName());
		employee.first_surname.setValue(employeeData.getSurName());
		employee.second_surname.setValue(employeeData.getSecondSurName());
		
		employee.birth_date.setValue(employeeData.getBirthdate());
		employee.gender.setSelectedIndex(employeeData.getGender());
		
		setSelectedValueLB(employee.street_type, employeeData.getStreetType());
		
		employee.address.setValue(employeeData.getAddress());
		employee.addressNum.setValue(employeeData.getAddresNum());
		employee.addressZip.setValue(employeeData.getAddressZip());
		
		setSelectedValueLB(employee.addressProvince, employeeData.getAddressProvinces());
		if(null != employeeData.getAddressProvinces()) {
			updateMunicipalities();
			employee.addressMunicipality.setSelectedIndex(getMunicipalityIndex(employeeData.getAddressProvinces(), employeeData.getAddressCity()));
		}
		
		employee.mobile.setValue(employeeData.getMobile());
		employee.phone.setValue(employeeData.getPhone());
		employee.email.setValue(employeeData.getEmail());
		
		setSelectedValueLB(employee.payMethod, null == employeeData.getPaymethodId() ? "-1" : employeeData.getPaymethodId().toString());
//		employee.payMethod.setSelectedIndex(getPayMethodIndex(employeeData.getPayMethodTypeB()));
		employee.account.setValue(employeeData.getAccount());
		employee.bic.setValue(employeeData.getBic());
		reformatAccount(this.employee.account);
	}

	private void fillContractFreelancerTable() {
		ContractInfo contractData = employeeDialogObject.getContractData();
		
		employee.ssRegimeType.setSelectedIndex(1); // RETA
		
		setSelectedValueLB(employee.workplace, contractData.getWorkplaceId().toString());
		
		employee.start_date.setValue(contractData.getStartDate());
		employee.seniority_date.setValue(contractData.getSeniorityDate());
		employee.end_date.setValue(contractData.getEndDate());
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.seniority_date);

		if(null != contractData.getAgreementId()) {
			setSelectedValueLB(employee.agreement, contractData.getAgreementId().toString());
			getAgreementLevelsAndSetLevel(contractData.getAgreementId(), contractData.getAgreementLevelId(), contractData.getAgreementCategory());
		} else {
			employee.agreement.setSelectedIndex(0);
			employee.level.clear();
			employee.level.addItem("-", "-1");
		}
		
		employee.category.setValue(contractData.getAgreementCategory());
		employee.category.setEnabled(true);
		
		employee.journeyType.setSelectedIndex(contractData.getJourneyType());
	}
	
	private void fillContractTable() {
		ContractInfo contractData = employeeDialogObject.getContractData();
		
		employee.ssRegimeType.setSelectedIndex(employeeDialogObject.getContractSSRegimen());
		
		setSelectedValueLB(employee.activityCCC, contractData.getActivityId()+"/"+contractData.getCccId()+"/"+contractData.getCccType());
		setSelectedValueLB(employee.workplace, contractData.getWorkplaceId().toString());
		
		setSelectedValueLB(employee.contractType, contractData.getContractType());
		
		Integer contractTypeId = employeeDialogObject.getContractType();
		if((contractTypeId >= 200 && contractTypeId<300) || (contractTypeId >= 500 && contractTypeId<600)) {
			employee.showElementsPartialTimeContract();
			employee.journeyDuration.addStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
			employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
			employee.journeyDuration.setText("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
		} else
			employee.showElementsFullTimeContract();
		
		employee.modality.clear();
		employee.modality.addItem("-");
		
		List<ModelRecord> contractTypeModels = this.contractType.getModelsContractType(contractTypeId);
		for (ModelRecord model : contractTypeModels)
			employee.modality.addItem(model.getModelDescription(), model.getEnumeration().toString());
		
		if(null != contractData.getContractModel())
			setSelectedValueLB(employee.modality, contractData.getContractModel().toString());
		
		employee.start_date.setValue(contractData.getStartDate());
		employee.seniority_date.setValue(contractData.getSeniorityDate());
		employee.end_date.setValue(contractData.getEndDate());
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.seniority_date);
		
		if(null != contractData.getAgreementId()) {
			setSelectedValueLB(employee.agreement, contractData.getAgreementId().toString());
			getAgreementLevelsAndSetLevel(contractData.getAgreementId(), contractData.getAgreementLevelId(), contractData.getAgreementCategory());
		}

		employee.category.setValue(contractData.getAgreementCategory());
		employee.category.setEnabled(true);
		
		employee.quote_group.setSelectedIndex(employeeDialogObject.getContractQuoteGroup());
		employee.occupation.setSelectedIndex(employeeDialogObject.getContractOcupation());
	}
	
	public void blockVariablesExistingContract(){
		employee.document.setEnabled(false);
		employee.nationality.setEnabled(false);
		employee.security_social_num.setEnabled(false);
		employee.name.setEnabled(false);
		employee.first_surname.setEnabled(false);
		employee.second_surname.setEnabled(false);
		
		employee.birth_date.setEnabled(false);
		employee.gender.setEnabled(false);
		employee.street_type.setEnabled(false);
		employee.address.setEnabled(false);
		employee.addressNum.setEnabled(false);
		employee.addressZip.setEnabled(false);
		employee.addressMunicipality.setEnabled(false);
		employee.addressProvince.setEnabled(false);
		employee.mobile.setEnabled(false);
		employee.phone.setEnabled(false);
		employee.email.setEnabled(false);
		employee.payMethod.setEnabled(false);
		employee.bic.setEnabled(false);
		employee.account.setEnabled(false);
	}
	
	public void unblockVariablesExistingContract(){
		employee.document.setEnabled(true);
		employee.nationality.setEnabled(true);
		employee.security_social_num.setEnabled(true);
		employee.name.setEnabled(true);
		employee.first_surname.setEnabled(true);
		employee.second_surname.setEnabled(true);
		
		employee.birth_date.setEnabled(true);
		employee.gender.setEnabled(true);
		employee.street_type.setEnabled(true);
		employee.address.setEnabled(true);
		employee.addressNum.setEnabled(true);
		employee.addressZip.setEnabled(true);
		employee.addressMunicipality.setEnabled(true);
		employee.addressProvince.setEnabled(true);
		employee.mobile.setEnabled(true);
		employee.phone.setEnabled(true);
		employee.email.setEnabled(true);
		employee.payMethod.setEnabled(true);
		employee.bic.setEnabled(true);
		employee.account.setEnabled(true);
	}
	
	public void updateMunicipalities() {
		String provinceCode = employee.addressProvince.getSelectedValue();
		employee.addressMunicipality.clear();
		employee.addressMunicipality.addItem("-");;
		ArrayList<String> municipalitiesOfProvince = municipalities.getMunicipalitiesByProvinceCode(provinceCode);
		municipalitiesOfProvince.forEach(m -> {employee.addressMunicipality.addItem(m);});
	}
	
	private void addSuccessStyle(Widget widget) {
		widget.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
		widget.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
	}
	
	private void addWarningStyle(Widget widget) {
		widget.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
		widget.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
	}
	
	private void addWarningDateStyle(Widget widget) {
		widget.getElement().getStyle().clearDisplay();
		widget.setTitle("La fecha de inicio no coincide con la de antig" + String.valueOf("\u00FC") + "edad.");
		
		widget.setStyleName("aon-finding-toolbar-item aon-icon-info aon-finding-toolbar-item-no-border");widget.getElement().getStyle().setMarginTop(3.00, Unit.PX);
		widget.getElement().getStyle().setMarginTop(3.00, Unit.PX);
	}
	
	private void showPartialTimeContract() {
		employee.showElementsPartialTimeContract();
		employee.journeyDuration.addStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
		employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
		employee.journeyDuration.setText("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
		employee.journeyDuration.setTitle("Las horas se deben definir en el calendario del empleado.");
	}
	
	@SuppressWarnings("deprecation")
	private Integer getYears(Date actualDay, Date birthDate) {
		DateUtils.resetTime(actualDay);
		DateUtils.resetTime(birthDate);
		
		Integer age = actualDay.getYear() - birthDate.getYear() - 1;
		
		if(actualDay.getMonth() >= birthDate.getMonth()) {
			age++;
			
			if(actualDay.getDate() < birthDate.getDate())
				age--;
		}
		
		return age;
	}
	
	private void getAgreementLevels(Integer agreementId) {
		employee.level.clear();
		employee.level.addItem("-", "-1");
		
		employeeDialogObject.getAgreement(agreementId,  
		(agreement) -> {
			for (Level levelRecord : agreement.getLevels())
				for (String categoryRecord : agreement.getCategoriesMap().get(levelRecord.getId()))
					employee.level.addItem(levelRecord.getDescription() + " - " + categoryRecord, String.valueOf(levelRecord.getId()));

			employeeDialogObject.setContractAgreementId(agreement.getId());
			employeeDialogObject.setContractAgreementLevelId(null);
		},
		(throwable) -> {
			employeeDialogObject.setContractAgreementId(null);
			employeeDialogObject.setContractAgreementLevelId(null);
			employee.category.setEnabled(false);
			employee.category.setValue("");
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.category);
		});
	}
	
	private void getAgreementLevelsAndSetLevel(Integer agreementId, Integer agreementLevelId, String category_description) {
		employee.level.clear();
		employee.level.addItem("-", "-1");
		
		employeeDialogObject.getAgreement(agreementId,  
		(agreement) -> {
			for (Level levelRecord : agreement.getLevels())
				for (String categoryRecord : agreement.getCategoriesMap().get(levelRecord.getId()))
					employee.level.addItem(levelRecord.getDescription() + " - " + categoryRecord, String.valueOf(levelRecord.getId()));

			employeeDialogObject.setContractAgreementId(agreement.getId());
			Integer agreementIdx = getAgreementLevelIdx(agreement, agreement.getLevels(), agreementId, agreementLevelId, category_description) + 1;
			employee.level.setSelectedIndex(agreementIdx);
		},
		(throwable) -> {
			employeeDialogObject.setContractAgreementId(null);
			employeeDialogObject.setContractAgreementLevelId(null);
			employee.category.setEnabled(false);
			employee.category.setValue("");
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.category);
		});
	}
	
	private int getAgreementLevelIdx(Agreement agreement, Set<Level> levels, Integer agreementId, Integer agreementLevelId,
			String category_description) {
		
		Integer result = 0;
		for (Level levelRecord : levels) {
			Set<String> categories = agreement.getCategoriesMap().get(levelRecord.getId());
			for (String categoryRecord : categories) {
				if (levelRecord.getId().equals(agreementLevelId) && (categoryRecord == category_description || category_description.contains(categoryRecord)))
					return result;
				else
					result++;
			}
		}
		return -1;
	}

	public void showNationality(String document_type_str) {
		if (document_type_str == "CIF" || document_type_str == "Pasaporte" || document_type_str == "NIE") {
			employee.nationalityLabelCell.getStyle().clearDisplay();
			employee.nationalityCell.getStyle().clearDisplay();
		} else {
			employee.nationalityLabelCell.getStyle().setDisplay(Display.NONE);
			employee.nationalityCell.getStyle().setDisplay(Display.NONE);
			employee.nationality.setValue("ESPA" + String.valueOf("\u00D1") + "A");
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.nationality);
		}
	}
	
	private String getIso2(String country) {
		for (int i = 0; i < Country.values().length; i++)
			if (Country.values()[i].getName() == country)
				return Country.values()[i].getIso2();
		
		return null;
	}
	
	private int getPayMethodIndex(byte payMethodType) {
		switch (payMethodType) {
		case (byte) 0: //EFECTIVO
			return 1;
		case (byte) 4: //CHEQUE
			return 2;
		case (byte) 5: //TRANSFERENCIA
			return 3;
		default:
			return 0;
		}
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
	
	private int getMunicipalityIndex(String province, String city) {
		return municipalities.getMunicipalityIndex(province, city) + 1;
	}
	
	private String getCCCType(Byte type) {
		switch (type) {
			case (byte) 0:
				return "PRINCIPAL";
			case (byte) 1:
				return "FORMACION Y APRENDIZAJE";
			case (byte) 3:
				return "REPRESENTANTES DE COMERCIO";
			case (byte) 4:
				return "ASIMILADOS R.GENERAL";
			case (byte) 5:
				return "BECARIOS";
			case (byte) 6:
				return "EMPLEADOS DE HOGAR";
			case (byte) 7:
				return "TRABAJADOR CUENTA AJENA";
			case (byte) 8:
				return "ARTISTA";
			default:
				return "PRINCIPAL";
		}
	}
	
	// ----------------------------------------------- CALLBACK TO SAVE ------------------------------------------------
	
//	public void show(Callback cb) {
//		this.cb = cb;
//		super.show();
//	}
//	
//	public void setPopupPositionAndShow(PositionCallback positionCallback, Callback callback) {
//		this.cb = callback;
//		super.setPopupPositionAndShow(positionCallback);
//	}
	
	private void getButtonsPanel() {
		closeBtnDialog = new AonButton("Cerrar", AON.CSS.aonIconClose());
		closeBtnDialog.setAccessKey('C');
		closeBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCloseDialog(event);
			}
		});
		
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		
		buttonsPanel.add(closeBtnDialog);
		
		acceptBtnDialog = new AonButton("Aceptar", AON.CSS.aonIconAccept());
		acceptBtnDialog.setAccessKey('A');
		acceptBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAcceptDialog(event);
			}
		});
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private void onCloseDialog(ClickEvent event) {
		hide();
	}
	
	private void onAcceptDialog(ClickEvent event) {
		int ssRegime = employee.ssRegimeType.getSelectedIndex();
		employeeDialogObject.setSSRegime(ssRegime);
		
		if(checkIfSaveIsPossible())
			if(checkDates())
				this.employeeDialogObject.createEmployeeContract(
						r -> { 
								hide();
								
								if(null != employeeDialogObject.getWorkplaceObj()) 
									EmployeeTree.invokeRefreshWorkplace();
				
								onAccept();
							 }, 
						t -> {}
				);	
			else {
				WarningDialog dialog = new WarningDialog("Aviso", "La fecha de inicio no puede ser posterior a la fecha de fin.");
				dialog.center();
				dialog.show();
			}
		else {
			WarningDialog dialog = new WarningDialog("Aviso", "Hay que rellenar los campos azules correcta y obligatoriamente.");
			dialog.center();
			dialog.show();
		}
	}
	
}

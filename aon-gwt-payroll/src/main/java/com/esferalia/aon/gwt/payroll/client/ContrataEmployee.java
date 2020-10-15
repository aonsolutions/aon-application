package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.widget.DateBoxEx.DefaultFormat;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
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
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.Iban;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.Municipalities;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContrataEmployee extends ResizeComposite {
	
	@SuppressWarnings("deprecation")
	private class EmployeeImplementation extends Employee{
		ContractType contractTypeClass = new ContractType();
		
		// TABLA DATOS CONTRATO
		
		@Override
		public void onClearEmployeeClick() {}
		
		@Override
		public void onEmployeeDocumentSuggestionChange() {}
		
		@Override
		public void onEmployeeDocumentChange() {
			String document = this.document.getValue();
			if(!StringUtils.isBlank(document)) {
				document = document.trim();
				this.document.setValue(document);
				
				String document_type = checkDocumentType(document);
				this.document_type.setText(document_type);
				
				contrataEmployeeObject.setEmployeeDocument(document);
				contrataEmployeeObject.setEmployeeDocumentType(document_type);
				
				if(contrataEmployeeObject.checkDocumentValidation(document_type, document))
					addSuccessStyle(this.documentStatus);
				else
					addWarningStyle(this.documentStatus);
					
				showNationality(document_type);	
			}
		}
		
		@Override
		public void onEmployeeNationalityChange() {
			String countryIso2 = getIso2(this.nationality.getValue());
			contrataEmployeeObject.setNationality(countryIso2);
		}
		
		@Override
		public void onEmployeeSSNumSuggestionChange() {}

		@Override
		public void onEmployeeSSNumChange() {
			String ssNum = this.security_social_num.getValue();
			
			if(!StringUtils.isBlank(ssNum)) {
				ssNum = ssNum.trim();
				this.security_social_num.setValue(ssNum);
				
				contrataEmployeeObject.setEmployeeSocialSecurityNum(ssNum);
				
				if(contrataEmployeeObject.checkSSNumValidation(ssNum))
					addSuccessStyle(this.ssNumberStatus);
				else
					addWarningStyle(this.ssNumberStatus);
			}
		}
		
		@Override
		public void onEmployeeNameSuggestionChange() {}
		
		@Override
		public void onEmployeeNameChange() {
			String name = this.name.getValue();
			if(!StringUtils.isBlank(name)) {
				name = name.trim();
				this.name.setValue(name);
				contrataEmployeeObject.setEmployeeName(name);	
			}
		}

		@Override
		public void onEmployeeFirstSurnameSuggestionChange() {}
		
		@Override
		public void onEmployeeFirstSurnameChange() {
			String surname = this.first_surname.getValue();
			if(!StringUtils.isBlank(surname)) {
				surname = surname.trim();
				this.first_surname.setValue(surname);
				contrataEmployeeObject.setEmployeeFirstSurname(surname);
			}
		}

		@Override
		public void onEmployeeSecondSurnameChange() {
			String secondSurname = this.second_surname.getValue();
			if(!StringUtils.isBlank(secondSurname)) {
				secondSurname = secondSurname.trim();
				this.second_surname.setValue(secondSurname);
				contrataEmployeeObject.setEmployeeSecondSurname(secondSurname);
			}
		}

		@Override
		public void onContractSSRegimenChange() {
			byte ssRegime = Byte.valueOf(this.ssRegimeType.getSelectedValue()).byteValue();
			contrataEmployeeObject.setSSRegime(ssRegime);
			
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
				contrataEmployeeObject.setActivityInfo(null);
			else
				contrataEmployeeObject.setActivityInfo(activityCCC);
		}
		
		@Override
		public void onContractWorkplaceChange() {
			Integer workplaceId = Integer.parseInt(this.workplace.getSelectedValue());
			contrataEmployeeObject.setContractWorkplaceId(workplaceId);
		}

		@Override
		public void onContractTypeChange() {
			this.modality.clear();
			this.modality.addItem("-");
			
			String contractType = null;
			
			if(this.contractType.getSelectedIndex() == 0) {
				contrataEmployeeObject.setContractType(contractType);
				contrataEmployeeObject.setContractModel(null);
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
				
				contrataEmployeeObject.setContractType(contractType);
			}
		}
		
		@Override
		public void onContractModalityChange() {
			if (this.modality.getSelectedIndex() == 0)
				contrataEmployeeObject.setContractModel(null);
			else {
				Integer contractModel = Integer.parseInt(this.modality.getSelectedValue());
				contrataEmployeeObject.setContractModel(contractModel); // GET String of enum in JooqEmployee.java
			}
		}
		
		@Override
		public void onContractStartDateChange() {
			if(null != this.start_date.getValue()) {
				DefaultFormat format = new DefaultFormat();
				Date date = format.parse(this.start_date, this.start_date.getTextBox().getValue(), false);
				this.start_date.setValue(date);
				contrataEmployeeObject.setContractStartDate(date);
				
				this.seniority_date.setValue(date, true);
			} else
				contrataEmployeeObject.setContractStartDate(this.start_date.getValue());
		}

		@Override
		public void onContractEndDateChange() {
			if(null != this.end_date.getValue()) {
				DefaultFormat format = new DefaultFormat();
				Date date = format.parse(this.end_date, this.end_date.getTextBox().getValue(), false);
				this.end_date.setValue(date);
				contrataEmployeeObject.setContractEndDate(date);
			} else
				contrataEmployeeObject.setContractEndDate(this.end_date.getValue());
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
			
			contrataEmployeeObject.setContractSeniorityDate(seniorityDate);	
		}

		@Override
		public void onContractAgreementChange() {
			
			if (this.agreement.getSelectedIndex() == 0 ) {
				contrataEmployeeObject.setContractAgreementId(null);
				contrataEmployeeObject.setContractAgreementLevelId(null);
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
				contrataEmployeeObject.setContractAgreementLevelId(null);
				this.category.setEnabled(false);
				this.category.setValue("");
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.category);
			} else {
				Integer agreementLevelId = Integer.parseInt(this.level.getSelectedValue());
				contrataEmployeeObject.setContractAgreementLevelId(agreementLevelId);
				String levelDescription = (this.level.getSelectedItemText() == null
						|| this.level.getSelectedItemText() == "-") ? null
								: this.level.getSelectedItemText().split("- ")[1];
				this.category.setValue(levelDescription);
				contrataEmployeeObject.setContractCategory(levelDescription);
				this.category.setEnabled(true);
			}
		}
		
		@Override
		public void onContractCategoryChange() {
			contrataEmployeeObject.setContractCategory(this.category.getValue());
		}
		
		@Override
		public void onContractQuoteGroupChange() {
			if (quote_group.getSelectedIndex() == 0)
				contrataEmployeeObject.setContractQuoteGroup(null);
			else {
				String quoteGroup = String.valueOf(this.quote_group.getSelectedValue()); 
				contrataEmployeeObject.setContractQuoteGroup(quoteGroup);
			}
		}

		@Override
		public void onContractOccupationChange() {
			if (this.occupation.getSelectedIndex() == 0)
				contrataEmployeeObject.setContractOccupation(null);
			else {
				String occupation = String.valueOf(this.occupation.getSelectedValue());
				contrataEmployeeObject.setContractOccupation(occupation);
			}
		}

		@Override
		public void onContractJourneyTypeChange() {
			 Boolean journey_type = (this.journeyType.getSelectedIndex() == 0) ? true : false;
			 contrataEmployeeObject.setContractJourneyType(journey_type);
			 if(this.journeyType.getSelectedIndex() == 0)
				 employee.showElementsFullTimeContract();
			 else
				 showPartialTimeContract();
		}

		@Override
		public void onContractJourneyDurationClick() {
			ContractJourneyDialog dialog = new ContractJourneyDialog(contrataEmployeeObject.getContractStartDate(), contrataEmployeeObject.getContractEndDate()) {

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
					contrataEmployeeObject.setContractJourneyDuration(contractJourneyDuration.getContractJourneyDuration());
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
			
			contrataEmployeeObject.setEmployeeBirthDate(this.birth_date.getValue());
			
		}

		@Override
		public void onEmployeeGenderChange() {
			byte gender = Byte.valueOf(this.gender.getSelectedValue()).byteValue();
			contrataEmployeeObject.setEmployeeGender(gender);
		}
		
		@Override
		public void onEmployeeCivilStatusChange() {
			byte civilStatus = Byte.valueOf(this.civilStatus.getSelectedValue()).byteValue();
			contrataEmployeeObject.setEmployeeCivilStatus(civilStatus);	
		}
		
		@Override
		public void onEmployeeStreetTypeChange() {
			String streetType = String.valueOf(this.street_type.getSelectedValue());
			contrataEmployeeObject.setEmployeeStreetType(streetType);
		}

		@Override
		public void onEmployeeAddressChange() {
			contrataEmployeeObject.setEmployeeAddress(this.address.getValue());
		}

		@Override
		public void onEmployeeAddressNumChange() {
			contrataEmployeeObject.setEmployeeAddressNumber(this.addressNum.getValue());
		}
		
		@Override
		public void onEmployeeAddressInfoChange() {
			contrataEmployeeObject.setEmployeeAddressInfo(this.addressInfo.getValue());
		}

		@Override
		public void onEmployeeAddressZipChange() {
			contrataEmployeeObject.setEmployeeAddressZip(this.addressZip.getValue());
			if(this.addressZip.getValue().length() >= 2) {
				String zip = this.addressZip.getValue().substring(0, 2);
				setSelectedValueLB(addressProvince, zip); 
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), addressProvince);
			}
		}

		@Override
		public void onEmployeeAddressMunicipalityChange() {
			contrataEmployeeObject.setEmployeeAddressCity(municipalities.getZipByMunicipalityName(this.addressMunicipality.getSelectedItemText()).toString());
		}

		@Override
		public void onEmployeeAddressProvinceChange() {
			String addressProvinceCode = this.addressProvince.getSelectedValue();
			contrataEmployeeObject.setEmployeeAddressProvince(addressProvinceCode);
			contrataEmployeeObject.setEmployeeAddressCity("-1");
			updateMunicipalities();
		}
		
		@Override
		public void onEmployeeMobileChange() {
			contrataEmployeeObject.setEmployeeMobile(this.mobile.getValue());
		}

		@Override
		public void onEmployeePhoneChange() {
			contrataEmployeeObject.setEmployeePhone(this.phone.getValue());
		}

		@Override
		public void onEmployeeEmailChange() {
			contrataEmployeeObject.setEmployeeEmail(this.email.getValue());
		}

		@Override
		public void onEmployeePayMethodChange() {
			byte methodPay = Byte.valueOf(this.payMethod.getSelectedValue()).byteValue();
			contrataEmployeeObject.setEmployeePayMethod(methodPay);
			
			this.account.setValue(null);
			this.bic.setValue(null);
			
			if(this.payMethod.getSelectedIndex() == 3) { //TRANFERENCIA
				this.account.setEnabled(true);
				this.bic.setEnabled(true);
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), account);
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), bic);
			} else {
				this.account.setEnabled(false);
				this.bic.setEnabled(false);
			}
		}

		@Override
		public void onEmployeeBICChange() {
			contrataEmployeeObject.setEmployeeBIC(this.bic.getValue());
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
			
			contrataEmployeeObject.setEmployeeAccount(account);
		}
		
	}	

	private class ContractOtherDataImplementation extends ContractOtherData{

		// ------------------------------------------------------- Indefinite Table
		
		@Override
		protected void onEnterpriseAgentTBChange() {
			String value = contractOtherData.enterpriseAgentTB.getValue();
			setContractOtherData("ENTERPRISE_DIR_STAFF_NAME", value);
		}

		@Override
		protected void onEnterpriseAgentNIFTBChange() {
			String value = contractOtherData.enterpriseAgentNIFTB.getValue();
			setContractOtherData("ENTERPRISE_DIR_STAFF_NIF", value);
		}

		@Override
		protected void onEnterpriseAgentPositionTBChange() {
			String value = contractOtherData.enterpriseAgentPositionTB.getValue();
			setContractOtherData("ENTERPRISE_DIR_STAFF_CHARGE", value);
		}

		@Override
		protected void onMinorAgentTBChange() {
			String value = contractOtherData.minorAgentTB.getValue();
			setContractOtherData("LEGAL_REPRESENTATIVE_NAME", value);
		}

		@Override
		protected void onMinorAgentNIFTBChange() {
			String value = contractOtherData.minorAgentNIFTB.getValue();
			setContractOtherData("LEGAL_REPRESENTATIVE_NIF", value);
		}

		@Override
		protected void onMinorAgentQualityOfTBChange() {
			String value = contractOtherData.minorAgentQualityOfTB.getValue();
			setContractOtherData("LEGAL_REPRESENTATIVE_CHARGE", value);
		}

		@Override
		protected void onDoingFunctionsTBChange() {
			String value = contractOtherData.doingFunctionsTB.getValue();
			setContractOtherData("FUNCTIONS", value);
		}

		@Override
		protected void onDistanceCBChange() {
			Boolean value = contractOtherData.distanceCB.getValue();
			if(value)
				setContractOtherData("EMPLOYEE_CONTRACT_DISTANCE", "true");
			else
				setContractOtherData("EMPLOYEE_CONTRACT_DISTANCE", "");
		}

		@Override
		protected void onDistanceAddressTBChange() {
			String value = contractOtherData.distanceAddressTB.getValue();
			setContractOtherData("EMPLOYEE_CONTRACT_DIST_ADDR", value);
		}

		@Override
		protected void onDiscontinuousWorkTBChange() {
			String value = contractOtherData.discontinuousWorkTB.getValue();
			setContractOtherData("DISC_WORK_DESCRIPTION", value);
		}

		@Override
		protected void onIntermittentCyclicalActivityTBChange() {
			String value = contractOtherData.intermittentCyclicalActivityTB.getValue();
			setContractOtherData("DISC_WORK_ACTIVITY", value);
		}

		@Override
		protected void onDurationFDTBChange() {
			String value = contractOtherData.durationFDTB.getValue();
			setContractOtherData("DISC_WORK_DURATION", value);
		}

		@Override
		protected void onActivityStimationDurationFDTBChange() {
			String value = contractOtherData.activityStimationDurationFDTB.getValue();
			setContractOtherData("DISC_WORK_ESTIMATED_DURATION", value);
		}

		@Override
		protected void onJourneyHoursFDTBChange() {
			String value = contractOtherData.journeyHoursFDTB.getValue();
			setContractOtherData("DISC_WORK_ESTIM_JOURNAL_HOURS", value);
		}

		@Override
		protected void onJourneyPeriodFDTBChange() {
			String value = contractOtherData.journeyPeriodFDTB.getValue();
			setContractOtherData("DISC_WORK_ESTIM_JOURNAL_PERIOD", value);
		}

		@Override
		protected void onTimeDistributionFDTBChange() {
			String value = contractOtherData.timeDistributionFDTB.getValue();
			setContractOtherData("DISC_WORK_ESTIM_SCHEDULE", value);
		}

		@Override
		protected void onPartialTimeLBChange() {
			String selectedValue = contractOtherData.partialTimeLB.getSelectedValue();
			setContractOtherData("DISC_AGREEMENT_COLLECTIVE", selectedValue);
		}

		@Override
		protected void onJourneyHoursTCTBChange() {
			String value = contractOtherData.journeyHoursTCTB.getValue();
			setContractOtherData("FULL_TIME_WEEK_HOURS", value);
		}

		@Override
		protected void onStartJourneyTCTBChange() {
			String value = contractOtherData.startJourneyTCTB.getValue();
			setContractOtherData("FULL_TIME_START_TIME", value);
		}

		@Override
		protected void onEndJourneyTCTBChange() {
			String value = contractOtherData.endJourneyTCTB.getValue();
			setContractOtherData("FULL_TIME_END_TIME", value);
		}

		@Override
		protected void onJourneyHoursTPTBChange() {
			String value = contractOtherData.journeyHoursTPTB.getValue();
			setContractOtherData("PARTIALLY_TIME_HOURS", value);
		}

		@Override
		protected void onAgreementJourneyHoursTBChange() {
			String value = contractOtherData.agreementJourneyHoursTB.getValue();
			setContractOtherData("DEFAULT_JOURNAL_HOURS", value);
		}

		@Override
		protected void onComplementaryHoursLBChange() {
			String selectedValue = contractOtherData.complementaryHoursLB.getSelectedValue();
			setContractOtherData("COMPLEMENTARY_HOURS", selectedValue);
		}

		@Override
		protected void onTrialPeriodTBChange() {
			String value = contractOtherData.trialPeriodTB.getValue();
			setContractOtherData("TRIAL_DURATION", value);
		}

		@Override
		protected void onSalaryAmountTBChange() {
			String value = contractOtherData.salaryAmountTB.getValue();
			setContractOtherData("SALARY_AMOUNT", value);
		}

		@Override
		protected void onSalaryPeriodTBChange() {
			String value = contractOtherData.salaryPeriodTB.getValue();
			setContractOtherData("SALARY_PERIOD", value);
		}

		@Override
		protected void onSalaryConceptTBChange() {
			String value = contractOtherData.salaryConceptTB.getValue();
			setContractOtherData("SALARY_CONCEPT", value);
		}

		@Override
		protected void onHolidaysTBChange() {
			String value = contractOtherData.holidaysTB.getValue();
			setContractOtherData("HOLIDAYS", value);
		}

		@Override
		protected void onSepeOfficeTBChange() {
			String value = contractOtherData.sepeOfficeTB.getValue();
			setContractOtherData("SEPE_MUNICIPALITY", value);
		}

		@Override
		protected void onAccreditedDisabilityTBChange() {
			String value = contractOtherData.accreditedDisabilityTB.getValue();
			setContractOtherData("I_OPT2_SEPE_MUNICIPALITY", value);
		}

		@Override
		protected void onWithoutDisabilitySevereLBChange() {
			String selectedValue = contractOtherData.withoutDisabilitySevereLB.getSelectedValue();
			setContractOtherData("I_OPT2_DISABILITY_NO_SEVERE", selectedValue);
		}

		@Override
		protected void onDisabilitySevereLBChange() {
			String selectedValue = contractOtherData.disabilitySevereLB.getSelectedValue();
			setContractOtherData("I_OPT2_DISABILITY_SEVERE", selectedValue);
		}

		@Override
		protected void onSubsidyTBChange() {
			String value = contractOtherData.subsidyTB.getValue();
			setContractOtherData("I_OPT2_REDUCTION", value);
		}

		@Override
		protected void onFourthLawLBChange() {
			String selectedValue = contractOtherData.fourthLawLB.getSelectedValue();
			setContractOtherData("I_OPT5_BONUS_ART4_RDL3_2012", selectedValue);
		}

		@Override
		protected void onUnemploymentLBChange() {
			String selectedValue = contractOtherData.unemploymentLB.getSelectedValue();
			setContractOtherData("I_OPT5_UNEMPLOYED_BT_16_30", selectedValue);
		}

		@Override
		protected void onUnemploymentOldLBChange() {
			String selectedValue = contractOtherData.unemploymentOldLB.getSelectedValue();
			setContractOtherData("I_OPT5_UNEMPLOYED_GT_45", selectedValue);
		}

		@Override
		protected void onBenefitsPerceptorCBChange() {
			Boolean value = contractOtherData.benefitsPerceptorCB.getValue();
			if(value)
				setContractOtherData("I_OPT5_UNEMPL_3_MONTH_BENEFIT", "true");
			else
				setContractOtherData("I_OPT5_UNEMPL_3_MONTH_BENEFIT", "");
		}

		@Override
		protected void onFirstEmployeeCBChange() {
			Boolean value = contractOtherData.firstEmployeeCB.getValue();
			if(value)
				setContractOtherData("I_OPT5_FIRST_EMPLOYEE_AND_LT_30", "true");
			else
				setContractOtherData("I_OPT5_FIRST_EMPLOYEE_AND_LT_30", "");
		}

		@Override
		protected void onEmployeeLBChange() {
			String selectedValue = contractOtherData.employeeLB.getSelectedValue();
			setContractOtherData("I_OPT6_AGE", selectedValue);
		}

		@Override
		protected void onAgreementLineOneTBChange() {
			String value = contractOtherData.agreementLineOneTB.getValue();
			setContractOtherData("I_OPT6_AGREEMENT_COLLECTIVE1", value);
		}

		@Override
		protected void onAgreementLineTwoTBChange() {
			String value = contractOtherData.agreementLineTwoTB.getValue();
			setContractOtherData("I_OPT6_AGREEMENT_COLLECTIVE2", value);
		}

		@Override
		protected void onContactHoursLBChange() {
			String selectedValue = contractOtherData.contactHoursLB.getSelectedValue();
			setContractOtherData("I_OPT15_ONSITE_HOURS", selectedValue);
		}

		@Override
		protected void onHoursTBChange() {
			String value = contractOtherData.hoursTB.getValue();
			setContractOtherData("I_OPT15_ONSITE_WEEK_HOURS", value);
		}

		@Override
		protected void onRemunerationFormLBChange() {
			String selectedValue = contractOtherData.remunerationFormLB.getSelectedValue();
			setContractOtherData("I_OPT15_SALARY", selectedValue);
		}

		@Override
		protected void onOvernightAgreementLBChange() {
			String selectedValue = contractOtherData.overnightAgreementLB.getSelectedValue();
			setContractOtherData("I_OPT15_OVERNIGHT", selectedValue);
		}

		@Override
		protected void onOvernightRegimeTBChange() {
			String value = contractOtherData.overnightRegimeTB.getValue();
			setContractOtherData("I_OPT15_OVERNIGHT_WEEK_DAYS", value);
		}

		@Override
		protected void onQuoteReductionTCLBChange() {
			String selectedValue = contractOtherData.quoteReductionTCLB.getSelectedValue();
			setContractOtherData("I_OPT17_FULL_TIME_QUOTE_BONUS", selectedValue);
		}

		@Override
		protected void onQuoteReductionFDLBChange() {
			String selectedValue = contractOtherData.quoteReductionFDLB.getSelectedValue();
			setContractOtherData("I_OPT17_DISCONT_TIME_QUOTE_BONUS", selectedValue);
		}

		@Override
		protected void onSepeOfficeCOTBChange() {
			String value = contractOtherData.sepeOfficeCOTB.getValue();
			setContractOtherData("I_OPT17_SRC_CONTRACT_SEPE_MUNIC", value);
		}
		
		// ------------------------------------------------------- Temporal Table

		@Override
		protected void onEnterpriseAgentTempTBChange() {
			String value = contractOtherData.enterpriseAgentTempTB.getValue();
			setContractOtherData("T_ENTERPRISE_DIR_STAFF_NAME", value);
		}

		@Override
		protected void onEnterpriseAgentNIFTempTBChange() {
			String value = contractOtherData.enterpriseAgentNIFTempTB.getValue();
			setContractOtherData("T_ENTERPRISE_DIR_STAFF_NIF", value);
		}

		@Override
		protected void onEnterpriseAgentPositionTempTBChange() {
			String value = contractOtherData.enterpriseAgentPositionTempTB.getValue();
			setContractOtherData("T_ENTERPRISE_DIR_STAFF_CHARGE", value);
		}

		@Override
		protected void onMinorAgentTempTBChange() {
			String value = contractOtherData.minorAgentTempTB.getValue();
			setContractOtherData("T_LEGAL_REPRESENTATIVE_NAME", value);
		}

		@Override
		protected void onMinorAgentNIFTempTBChange() {
			String value = contractOtherData.minorAgentNIFTempTB.getValue();
			setContractOtherData("T_LEGAL_REPRESENTATIVE_NIF", value);
		}

		@Override
		protected void onMinorAgentQualityOfTempTBChange() {
			String value = contractOtherData.minorAgentQualityOfTempTB.getValue();
			setContractOtherData("T_LEGAL_REPRESENTATIVE_CHARGE", value);
		}

		@Override
		protected void onDoingFunctionsTempTBChange() {
			String value = contractOtherData.doingFunctionsTempTB.getValue();
			setContractOtherData("T_FUNCTIONS", value);
		}

		@Override
		protected void onDistanceTempCBChange() {
			Boolean value = contractOtherData.distanceTempCB.getValue();
			if(value)
				setContractOtherData("T_EMPLOYEE_CONTRACT_DISTANCE", "true");
			else
				setContractOtherData("T_EMPLOYEE_CONTRACT_DISTANCE", "");
		}

		@Override
		protected void onDistanceAddressTempTBChange() {
			String value = contractOtherData.distanceAddressTempTB.getValue();
			setContractOtherData("T_EMPLOYEE_CONTRACT_DIST_ADDR", value);
		}

		@Override
		protected void onJourneyHoursTCTempTBChange() {
			String value = contractOtherData.journeyHoursTCTempTB.getValue();
			setContractOtherData("T_FULL_TIME_WEEK_HOURS", value);
		}

		@Override
		protected void onStartJourneyTCTempTBChange() {
			String value = contractOtherData.startJourneyTCTempTB.getValue();
			setContractOtherData("T_FULL_TIME_START_TIME", value);
		}

		@Override
		protected void onEndJourneyTCTempTBChange() {
			String value = contractOtherData.endJourneyTCTempTB.getValue();
			setContractOtherData("T_FULL_TIME_END_TIME", value);
		}

		@Override
		protected void onLowJourneyTempTBChange() {
			String value = contractOtherData.lowJourneyTempTB.getValue();
			setContractOtherData("T_PARTIALLY_TIME_JOB_LOWER_THAN", value);
		}

		@Override
		protected void onTimeDistributionTempTBChange() {
			String value = contractOtherData.timeDistributionTempTB.getValue();
			setContractOtherData("T_PARTIALLY_TIME_JOB_DISTRIB", value);
		}

		@Override
		protected void onEndContractTempTBChange() {
			String value = contractOtherData.endContractTempTB.getValue();
			setContractOtherData("T_END_DATE_TEXT", value);
		}

		@Override
		protected void onTrialPeriodTempTBChange() {
			String value = contractOtherData.trialPeriodTempTB.getValue();
			setContractOtherData("T_TRIAL_DURATION", value);
		}

		@Override
		protected void onPermitedHighDurationTempCBChange() {
			Boolean value = contractOtherData.permitedHighDurationTempCB.getValue();
			if(value)
				setContractOtherData("T_GREATER_DURATION_AGREEMENT_COL", "true");
			else
				setContractOtherData("T_GREATER_DURATION_AGREEMENT_COL", "");
		}

		@Override
		protected void onSalaryAmountTempTBChange() {
			String value = contractOtherData.salaryAmountTempTB.getValue();
			setContractOtherData("T_SALARY_AMOUNT", value);
		}

		@Override
		protected void onSalaryPeriodTempTBChange() {
			String value = contractOtherData.salaryPeriodTempTB.getValue();
			setContractOtherData("T_SALARY_PERIOD", value);
		}

		@Override
		protected void onSalaryConceptTempTBChange() {
			String value = contractOtherData.salaryConceptTempTB.getValue();
			setContractOtherData("T_SALARY_CONCEPT", value);
		}

		@Override
		protected void onHolidaysTempTBChange() {
			String value = contractOtherData.holidaysTempTB.getValue();
			setContractOtherData("T_HOLIDAYS", value);
		}

		@Override
		protected void onSepeOfficeTempTBChange() {
			String value = contractOtherData.sepeOfficeTempTB.getValue();
			setContractOtherData("T_SEPE_MUNICIPALITY", value);
		}

		@Override
		protected void onWorkTempTBChange() {
			String value = contractOtherData.workTempTB.getValue();
			setContractOtherData("T_OPT1_WORK_DESCRIPTION1", value);
		}

		@Override
		protected void onWorkMoreTempTBChange() {
			String value = contractOtherData.workMoreTempTB.getValue();
			setContractOtherData("T_OPT1_WORK_DESCRIPTION2", value);
		}

		@Override
		protected void onTaskTempTBChange() {
			String value = contractOtherData.taskTempTB.getValue();
			setContractOtherData("T_OPT2_WORK_DESCRIPTION1", value);
		}

		@Override
		protected void onTaskMoreTempTBChange() {
			String value = contractOtherData.taskMoreTempTB.getValue();
			setContractOtherData("T_OPT2_WORK_DESCRIPTION2", value);
		}

		@Override
		protected void onSustituteEmployeeTempTBChange() {
			String value = contractOtherData.sustituteEmployeeTempTB.getValue();
			setContractOtherData("T_OPT3_REPLACED_WORKER_NAME", value);
		}

		@Override
		protected void onRequirementsTempLBChange() {
			String selectedValue = contractOtherData.requirementsTempLB.getSelectedValue();
			setContractOtherData("T_OPT10_REQUIREMENTS_OPT", selectedValue);
		}

		@Override
		protected void onFormationTempLBChange() {
			String selectedValue = contractOtherData.formationTempLB.getSelectedValue();
			setContractOtherData("T_OPT10_FORMATION_OPT", selectedValue);
		}

		@Override
		protected void onFormationWillTempLBChange() {
			String selectedValue = contractOtherData.formationWillTempLB.getSelectedValue();
			setContractOtherData("T_OPT10_FORMATION_TYPE_OPT", selectedValue);
		}

		@Override
		protected void onOfficeSPEmployeeTempTBChange() {
			String value = contractOtherData.officeSPEmployeeTempTB.getValue();
			setContractOtherData("T_OPT10_FORMATION_TYPE_OPT1_TEXT", value);
		}

		@Override
		protected void onLenguageFormationTempTBChange() {
			String value = contractOtherData.lenguageFormationTempTB.getValue();
			setContractOtherData("T_OPT10_FORMATION_TYPE_OPT2_TEXT", value);
		}

		@Override
		protected void onHoursDealTempLBChange() {
			String selectedValue = contractOtherData.hoursDealTempLB.getSelectedValue();
			setContractOtherData("T_OPT12_ONSITE_HOURS", selectedValue);
		}

		@Override
		protected void onPresentHoursTempTBChange() {
			String value = contractOtherData.presentHoursTempTB.getValue();
			setContractOtherData("T_OPT12_ONSITE_WEEK_HOURS", value);
		}

		@Override
		protected void onDistributionHoursTempTBChange() {
			String value = contractOtherData.distributionHoursTempTB.getValue();
			setContractOtherData("T_OPT12_ONSITE_HOURS_DISTRIB", value);
		}

		@Override
		protected void onTimeCompensationTempLBChange() {
			String selectedValue = contractOtherData.timeCompensationTempLB.getSelectedValue();
			setContractOtherData("T_OPT12_SALARY_OPT", selectedValue);
		}

		@Override
		protected void onDealOvernightLBChange() {
			String selectedValue = contractOtherData.dealOvernightLB.getSelectedValue();
			setContractOtherData("T_OPT12_OVERNIGHT", selectedValue);
		}

		@Override
		protected void onOvernightRegimeTempTBChange() {
			String value = contractOtherData.overnightRegimeTempTB.getValue();
			setContractOtherData("T_OPT12_OVERNIGHT_WEEK_DAYS", value);
		}

		@Override
		protected void onOfficialOrganismTempTBChange() {
			String value = contractOtherData.officialOrganismTempTB.getValue();
			setContractOtherData("T_OPT13_DISABILITY_ISSUED_BY", value);
		}

		@Override
		protected void onWithoutSevereDisTempLBChange() {
			String selectedValue = contractOtherData.withoutSevereDisTempLB.getSelectedValue();
			setContractOtherData("T_OPT13_DISABILITY", selectedValue);
		}

		@Override
		protected void onSevereDisTempLBChange() {
			String selectedValue = contractOtherData.severeDisTempLB.getSelectedValue();
			setContractOtherData("T_OPT13_SEVERE_DISABILITY", selectedValue);
		}

		@Override
		protected void onAdaptationPeriodTempTBChange() {
			String value = contractOtherData.adaptationPeriodTempTB.getValue();
			setContractOtherData("T_OPT14_TRIAL_PERIOD", value);
		}

		@Override
		protected void onAdaptationConditionsTempTBChange() {
			String value = contractOtherData.adaptationConditionsTempTB.getValue();
			setContractOtherData("T_OPT14_TRIAL_TERMS", value);
		}

		@Override
		protected void onAdaptationWorkTempLBChange() {
			String selectedValue = contractOtherData.adaptationWorkTempLB.getSelectedValue();
			setContractOtherData("T_OPT14_PROFESSION", selectedValue);
		}

		@Override
		protected void onSocialPersonalAdjustTempTBChange() {
			String value = contractOtherData.socialPersonalAdjustTempTB.getValue();
			setContractOtherData("T_OPT14_DISTANCE_ADJUSTMENT", value);
		}

		@Override
		protected void onSocialPersonalAdjustMoreTempTBChange() {
			String value = contractOtherData.socialPersonalAdjustMoreTempTB.getValue();
			setContractOtherData("T_OPT14_DISTANCE_ADJUSTMENT_MORE", value);
		}

		@Override
		protected void onColectiveAgreementTempTBChange() {
			String value = contractOtherData.colectiveAgreementTempTB.getValue();
			setContractOtherData("T_OPT14_COLLECTIVE_AGREEMENT", value);
		}
		
		// ------------------------------------------------------- Formation Table

		@Override
		protected void onEnterpriseAgentFormTBChange() {
			String value = contractOtherData.enterpriseAgentFormTB.getValue();
			setContractOtherData("L_ENTERPRISE_DIR_STAFF_NAME", value);
		}

		@Override
		protected void onEnterpriseAgentNIFFormTBChange() {
			String value = contractOtherData.enterpriseAgentNIFFormTB.getValue();
			setContractOtherData("L_ENTERPRISE_DIR_STAFF_NIF", value);
		}

		@Override
		protected void onEnterpriseAgentPositionFormTBChange() {
			String value = contractOtherData.enterpriseAgentPositionFormTB.getValue();
			setContractOtherData("L_ENTERPRISE_DIR_STAFF_CHARGE", value);
		}

		@Override
		protected void onMinorAgentFormTBChange() {
			String value = contractOtherData.minorAgentFormTB.getValue();
			setContractOtherData("L_LEGAL_REPRESENTATIVE_NAME", value);
		}

		@Override
		protected void onMinorAgentNIFFormTBChange() {
			String value = contractOtherData.minorAgentNIFFormTB.getValue();
			setContractOtherData("L_LEGAL_REPRESENTATIVE_NIF", value);
		}

		@Override
		protected void onMinorAgentQualityOfFormTBChange() {
			String value = contractOtherData.minorAgentQualityOfFormTB.getValue();
			setContractOtherData("L_LEGAL_REPRESENTATIVE_CHARGE", value);
		}

		@Override
		protected void onSsReductionFormLBChange() {
			String selectedValue = contractOtherData.ssReductionFormLB.getSelectedValue();
			setContractOtherData("L_QUOTE_BONUS", selectedValue);
		}

		@Override
		protected void onEmployeeFormLBChange() {
			String selectedValue = contractOtherData.employeeFormLB.getSelectedValue();
			setContractOtherData("L_EMPLOYEE_OPT", selectedValue);
		}

		@Override
		protected void onWorkplaceFormTBChange() {
			String value = contractOtherData.workplaceFormTB.getValue();
			setContractOtherData("L_CONTRACT_WORKPLACE_ADDRESS", value);
		}

		@Override
		protected void onTutorFormTBChange() {
			String value = contractOtherData.tutorFormTB.getValue();
			setContractOtherData("L_FORMATION_TEACHER", value);
		}

		@Override
		protected void onEfectiveWorkHoursFormTBChange() {
			String value = contractOtherData.efectiveWorkHoursFormTB.getValue();
			setContractOtherData("L_HORARIO_LABORAL", value);
		}

		@Override
		protected void onActivityHoursFormTBChange() {
			String value = contractOtherData.activityHoursFormTB.getValue();
			setContractOtherData("L_HORARIO_LECTIVO", value);
		}

		@Override
		protected void onTrialPeriodFormTBChange() {
			String value = contractOtherData.trialPeriodFormTB.getValue();
			setContractOtherData("L_TRIAL_DURATION", value);
		}

		@Override
		protected void onAgreementTrialFormCBChange() {
			Boolean value = contractOtherData.agreementTrialFormCB.getValue();
			if(value)
				setContractOtherData("L_TRIAL_DURATION_INCREASE", "true");
			else
				setContractOtherData("L_TRIAL_DURATION_INCREASE", "");
		}

		@Override
		protected void onSalaryAmountFormTBChange() {
			String value = contractOtherData.salaryAmountFormTB.getValue();
			setContractOtherData("L_SALARY_AMOUNT", value);
		}

		@Override
		protected void onSalaryPeriodFormTBChange() {
			String value = contractOtherData.salaryPeriodFormTB.getValue();
			setContractOtherData("L_SALARY_PERIOD", value);
		}

		@Override
		protected void onHolidaysFormTBChange() {
			String value = contractOtherData.holidaysFormTB.getValue();
			setContractOtherData("L_HOLIDAYS", value);
		}

		@Override
		protected void onDegreeExistFormCBChange() {
			Boolean value = contractOtherData.degreeExistFormCB.getValue();
			if(value)
				setContractOtherData("L_ANNEX_I_CHECK", "true");
			else
				setContractOtherData("L_ANNEX_I_CHECK", "");
		}

		@Override
		protected void onDegreeExist2FormCBChange() {
			Boolean value = contractOtherData.degreeExist2FormCB.getValue();
			if(value)
				setContractOtherData("L_ANNEX_II_CHECK", "true");
			else
				setContractOtherData("L_ANNEX_II_CHECK", "");
		}
		
		// ------------------------------------------------------- Practice Table

		@Override
		protected void onEnterpriseAgentPracTBChange() {
			String value = contractOtherData.enterpriseAgentPracTB.getValue();
			setContractOtherData("P_ENTERPRISE_DIR_STAFF_NAME", value);
		}

		@Override
		protected void onEnterpriseAgentNIFPracTBChange() {
			String value = contractOtherData.enterpriseAgentNIFPracTB.getValue();
			setContractOtherData("P_ENTERPRISE_DIR_STAFF_NIF", value);
		}

		@Override
		protected void onEnterpriseAgentPositionPracTBChange() {
			String value = contractOtherData.enterpriseAgentPositionPracTB.getValue();
			setContractOtherData("P_ENTERPRISE_DIR_STAFF_CHARGE", value);
		}

		@Override
		protected void onMinorAgentPracTBChange() {
			String value = contractOtherData.minorAgentPracTB.getValue();
			setContractOtherData("P_LEGAL_REPRESENTATIVE_NAME", value);
		}

		@Override
		protected void onMinorAgentNIFPracTBChange() {
			String value = contractOtherData.minorAgentNIFPracTB.getValue();
			setContractOtherData("P_LEGAL_REPRESENTATIVE_NIF", value);
		}

		@Override
		protected void onMinorAgentQualityOfPracTBChange() {
			String value = contractOtherData.minorAgentQualityOfPracTB.getValue();
			setContractOtherData("P_LEGAL_REPRESENTATIVE_CHARGE", value);
		}

		@Override
		protected void onProfesionalCertPracTBChange() {
			String value = contractOtherData.profesionalCertPracTB.getValue();
			setContractOtherData("P_PROFESSIONAL_CERT", value);
		}

		@Override
		protected void onObtainingDatePracTBChange() {
			String value = contractOtherData.obtainingDatePracTB.getValue();
			setContractOtherData("P_PROFESSIONAL_CERT_OBTAIN_DATE", value);
		}

		@Override
		protected void onDisabilityCertPracTBChange() {
			String value = contractOtherData.disabilityCertPracTB.getValue();
			setContractOtherData("P_DISABILITY_ISSUE_ENTITY", value);
		}

		@Override
		protected void onDisabilityCertMorePracTBChange() {
			String value = contractOtherData.disabilityCertMorePracTB.getValue();
			setContractOtherData("P_DISABILITY_ISSUE_ENTITY_MORE", value);
		}

		@Override
		protected void onFirstContractPracLBChange() {
			String selectedValue = contractOtherData.firstContractPracLB.getSelectedValue();
			setContractOtherData("P_FIRST_CONTRACT", selectedValue);
		}

		@Override
		protected void onJourneyHoursPracTBChange() {
			String value = contractOtherData.journeyHoursPracTB.getValue();
			setContractOtherData("P_FULL_TIME_WEEK_HOURS", value);
		}

		@Override
		protected void onStartJourneyPracTBChange() {
			String value = contractOtherData.startJourneyPracTB.getValue();
			setContractOtherData("P_FULL_TIME_START_TIME", value);
		}

		@Override
		protected void onEndJourneyPracTBChange() {
			String value = contractOtherData.endJourneyPracTB.getValue();
			setContractOtherData("P_FULL_TIME_END_TIME", value);
		}

		@Override
		protected void onDistributionJourneyPracTBChange() {
			String value = contractOtherData.distributionJourneyPracTB.getValue();
			setContractOtherData("P_JOB_TIME_DISTRIBUTION2", value);
		}

		@Override
		protected void onTrialPeriodPracTBChange() {
			String value = contractOtherData.trialPeriodPracTB.getValue();
			setContractOtherData("P_TRIAL_DURATION", value);
		}

		@Override
		protected void onSalaryAmountPracTBChange() {
			String value = contractOtherData.salaryAmountPracTB.getValue();
			setContractOtherData("P_SALARY_AMOUNT", value);
		}

		@Override
		protected void onSalaryPeriodPracTBChange() {
			String value = contractOtherData.salaryPeriodPracTB.getValue();
			setContractOtherData("P_SALARY_PERIOD", value);
		}

		@Override
		protected void onSalaryConceptPracTBChange() {
			String value = contractOtherData.salaryConceptPracTB.getValue();
			setContractOtherData("P_SALARY_CONCEPT", value);
		}

		@Override
		protected void onHolidaysPracTBChange() {
			String value = contractOtherData.holidaysPracTB.getValue();
			setContractOtherData("P_HOLIDAYS", value);
		}

		@Override
		protected void onSepeComunicationPracTBChange() {
			String value = contractOtherData.sepeComunicationPracTB.getValue();
			setContractOtherData("P_SEPE_START_COMMUNICATION", value);
		}

		@Override
		protected void onEndSepeComunicationPracTBChange() {
			String value = contractOtherData.endSepeComunicationPracTB.getValue();
			setContractOtherData("P_SEPE_END_COMMUNICATION", value);
		}

		@Override
		protected void onUnemploymentSubsidyPracLBChange() {
			String selectedValue = contractOtherData.unemploymentSubsidyPracLB.getSelectedValue();
			setContractOtherData("P_OPT3_UNEMPLOYMENT", selectedValue);
		}

		@Override
		protected void onAdaptationPeriodPracTBChange() {
			String value = contractOtherData.adaptationPeriodPracTB.getValue();
			setContractOtherData("P_OPT4_TRIAL_DURATION", value);
		}

		@Override
		protected void onAdaptationConditionsPracTBChange() {
			String value = contractOtherData.adaptationConditionsPracTB.getValue();
			setContractOtherData("P_OPT4_TRIAL_DURATION_CONDITIONS", value);
		}

		@Override
		protected void onAdaptationWorkPracTBChange() {
			String value = contractOtherData.adaptationWorkPracTB.getValue();
			setContractOtherData("P_OPT4_WORK_PLACE_ADAPTATIONS", value);
		}

		@Override
		protected void onPersonalSocialAdjustPracTBChange() {
			String value = contractOtherData.personalSocialAdjustPracTB.getValue();
			setContractOtherData("P_OPT4_STAFF_ADJUSTMENT", value);
		}

		@Override
		protected void onPersonalSocialAdjustMorePracTBChange() {
			String value = contractOtherData.personalSocialAdjustMorePracTB.getValue();
			setContractOtherData("P_OPT4_STAFF_ADJUSTMENT_MORE", value);
		}

		@Override
		protected void onMotivationPracLBChange() {
			String selectedValue = contractOtherData.motivationPracLB.getSelectedValue();
			setContractOtherData("P_OPT5_MOTIVATION", selectedValue);
		}

		@Override
		protected void onEmployerPracLBChange() {
			String selectedValue = contractOtherData.employerPracLB.getSelectedValue();
			setContractOtherData("P_OPT5_EMPLOYER", selectedValue);
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	private static ContrataEmployeeDraftUiBinder uiBinder = GWT.create(ContrataEmployeeDraftUiBinder.class);

	interface ContrataEmployeeDraftUiBinder extends UiBinder<Widget, ContrataEmployee> {}
	
	@UiField
	SplitLayoutPanel splitLayoutPanel;
	
	@UiField (provided = true)
	Employee employee;
	
	@UiField (provided = true)
	ContractOtherData contractOtherData;
	
	@UiField (provided = true)
	ContractClauseAndAttach contractClauseAndAttach;
	
	@UiField
	Button saveContract;
	
	@UiField
	TabLayoutPanel tabLayOutPanel;
	
	@UiField
	ScrollPanel scrolledPanel;
	
	@UiField
	ScrollPanel scrolledPanelContractOtherData;
	
	@UiField
	Button listEmployees;
	
	@UiField
	ScrollPanel scrolledPanelClausesAndAttach;
	
	@UiField
	MinimizePanel footPanel;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------

	private ContrataEmployeeObject contrataEmployeeObject;
	private ContractType contractType;
	private Municipalities municipalities;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------
	
	public ContrataEmployee() {
		employee = new EmployeeImplementation();
		contractOtherData = new ContractOtherDataImplementation();
		contractClauseAndAttach = new ContractClauseAndAttach();
		
		initWidget(uiBinder.createAndBindUi(this));
		
		employee.clear_employee.getElement().getStyle().setDisplay(Display.NONE);
		employee.account.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reformatAccount(employee.account);
			}
		});
		
		int height = Window.getClientHeight(); 
		scrolledPanel.setHeight((height-220)+"px");
		scrolledPanelContractOtherData.setHeight((height-220)+"px");
		scrolledPanelClausesAndAttach.setHeight((height-220)+"px");
		
		tabLayOutPanel.selectTab(0);
		tabLayOutPanel.setAnimationDuration(1000);
//		hideEmployeeTable();
		
		footPanel.addMaximizeHandler((e) -> {
			splitLayoutPanel.setWidgetSize(footPanel, 200);
		});
		
		footPanel.addMinimizeHandler((e) -> {
			splitLayoutPanel.setWidgetSize(footPanel, 10);
		});
	}
	
	// -------------------------------------------------- UiHandlers --------------------------------------------------
	
	@UiHandler("listEmployees")
	void onListButtonClick(ClickEvent clickEvent) {
		onListShow(true);
	}
	
	protected abstract void onListShow(boolean reloadEmployees);

	@UiHandler("saveContract")
	void onAcceptButtonClick(ClickEvent clickEvent) {
		int ssRegime = employee.ssRegimeType.getSelectedIndex();
		contrataEmployeeObject.setSSRegime(ssRegime);
		
		if(checkIfSaveIsPossible())
			if(checkDates())
				if(checkPayMethod())
					//TODO: UPDATE
					contrataEmployeeObject.updateEmployee(
							r -> { 
								onListShow(true);
							}, 
							t -> {}
					);
				else {
					WarningDialog dialog = new WarningDialog("Aviso", "Si el metodo de pago es transferencia, debe rellenar obligatoriamente los campos de BIC y cuenta.");
					dialog.center();
					dialog.show();
				}
					
			else{
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
//	
//	protected abstract void onAccept();
	
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
		
		if(null != this.contrataEmployeeObject.getWorkplaceObj()) {
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
	
	private boolean checkPayMethod() {
		if(4 == this.employee.payMethod.getSelectedIndex()) {
			if("" == this.employee.bic.getValue() || "" == this.employee.account.getValue())
				return false;
			else
				return true;
		}else
			return true;
	}
	
	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------
	
	public void setContrataEmployeeObject(ContrataEmployeeObject contrataEmployeeObject, EmployeeContractInfo employeeContractInfo) {
		this.contrataEmployeeObject = contrataEmployeeObject;
		this.contractType = new ContractType();
		this.municipalities = new Municipalities();
		
		final Element e = scrolledPanel.getElement();

	    new Animation() {

	        @Override
	        protected void onUpdate( double progress ) {
	            e.getStyle().setOpacity( progress );
	        }

	        @Override
	        protected void onComplete() {
	        	 e.getStyle().setOpacity( 1.0 );
	        }
	    }.run( 1000 );
		
	    tabLayOutPanel.selectTab(0);
		employee.restartEmployee();
		
		this.contrataEmployeeObject.getAgreements(
				r -> {
					contrataEmployeeObject.setEmployeeContractInfo(employeeContractInfo);
					initLogicWindow();
					initExistingEmployee(employeeContractInfo.getContractInfo().hasPayroll());
					contractClauseAndAttach.setEmployeeContractInfo(employeeContractInfo);
				}, 
				t -> {}
		);
	}
	
	private void initLogicWindow() {
		initActivitiesCCC();
		initWorkplaces();
		initContractType();
		initAgreements();
		initFocus();
	}
	
	private void initActivitiesCCC() {
		//ACTIVITY - CCC
		this.employee.activityCCC.clear();
		this.employee.activityCCC.addItem("-");
		if(null != this.contrataEmployeeObject.getActivities())
			for(Entry<Integer,String> entry : this.contrataEmployeeObject.getActivities().entrySet())
				for(CCCInfo cccInfo :  this.contrataEmployeeObject.getCCCs().values())
					if(cccInfo.getActivityId() == entry.getKey())
						this.employee.activityCCC.addItem(entry.getValue() + " - " + getCCCType(cccInfo.getType()) + "[" + cccInfo.getCcc() + "] - " +  cccInfo.getGeozone(), cccInfo.getActivityId() + "/" + cccInfo.getCccId() + "/" + cccInfo.getType());
//						this.employee.activityCCC.addItem(entry.getValue() + " - " + CCCType.values()[cccInfo.getType()] + "[" + cccInfo.getCcc() + "] - " +  cccInfo.getGeozone(), cccInfo.getActivityId() + "/" + cccInfo.getCccId() + "/" + cccInfo.getType());
	}

	private void initWorkplaces() {
		//WORKPLACE
		this.employee.workplace.clear();
		for(Workplace workplace : contrataEmployeeObject.getWorkplaces())
			this.employee.workplace.addItem(workplace.getDescription(), workplace.getId().toString());
	}

	private void initContractType() {
		// TIPO DE CONTRATO
		this.employee.contractType.clear();
		this.employee.contractType.addItem("-", "-1");
		for (Entry<Integer, ContractTypeRecord> entry : contractType.getContractTypes().entrySet())
			this.employee.contractType.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription(), entry.getKey().toString());	
	}
	
	private void initAgreements() {
		// CONVENIO
		this.employee.agreement.clear();
		this.employee.agreement.addItem("-", "-1");
		List<Agreement> agreements = contrataEmployeeObject.getActiveAgreements();
		for (Agreement agreement : agreements)
			this.employee.agreement.addItem(agreement.getDescription(), String.valueOf(agreement.getId()));
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
	
	public void initExistingEmployee( boolean hasPayroll){
		fillExistingEmployee();
		if(hasPayroll)
		   blockVariablesExistingContract();
		else
		   unblockVariablesExistingContract();
	}
	
	private void fillExistingEmployee() {
		EmployeeInfo employeeData = contrataEmployeeObject.getEmployeeData();
		ContractInfo contractData = contrataEmployeeObject.getContractData();
		
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
		
		setSelectedValueLB(employee.civilStatus, employeeData.getCivilStatus().toString());
		
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
		
		employee.payMethod.setSelectedIndex(getPayMethodIndex(employeeData.getPayMethodTypeB()));
		employee.account.setValue(employeeData.getAccount());
		employee.bic.setValue(employeeData.getBic());
		reformatAccount(this.employee.account);
		
		if (null != contractData.getSsRegimen() && contractData.getSsRegimen() == 3) { //RETA, había algo mas que determinaba si era o no RETA
			employee.showElementsFreelancerTable();
			fillContractFreelancerTable();
			fillContractOtherData(-1);
		} else {
			employee.hideElementsFreelancerTable();
			fillContractTable();
			fillContractOtherData(contrataEmployeeObject.getContractType());
		}
	}

	private void fillContractFreelancerTable() {
		ContractInfo contractData = contrataEmployeeObject.getContractData();
		
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
		ContractInfo contractData = contrataEmployeeObject.getContractData();
		
		employee.ssRegimeType.setSelectedIndex(contrataEmployeeObject.getContractSSRegimen());
		
		setSelectedValueLB(employee.activityCCC, contractData.getActivityId()+"/"+contractData.getCccId()+"/"+contractData.getCccType());
		setSelectedValueLB(employee.workplace, contractData.getWorkplaceId().toString());
		
		setSelectedValueLB(employee.contractType, contractData.getContractType());
		
		Integer contractTypeId = contrataEmployeeObject.getContractType();
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
		
		employee.quote_group.setSelectedIndex(contrataEmployeeObject.getContractQuoteGroup());
		employee.occupation.setSelectedIndex(contrataEmployeeObject.getContractOcupation());
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
	
	private void fillContractOtherData(Integer contractType) {
		if(-1 == contractType)
			tabLayOutPanel.getTabWidget(1).setVisible(false);
		else
			tabLayOutPanel.getTabWidget(1).setVisible(true);
		
		if(contractType >= 100 && contractType <= 400) {
			contractOtherData.showIndefiniteTable();
			fillContractOtherData();
		} else if (contractType == 421) {
			contractOtherData.showFormationTable();
			fillContractOtherDataFormation();
		} else if (contractType == 420 || contractType == 520) {
			contractOtherData.showPracticeTable();
			fillContractOtherDataPractice();
		} else {
			contractOtherData.showTemporalTable();
			fillContractOtherDataTemp();
		}
	}

	private void fillContractOtherData() {
		contractOtherData.enterpriseAgentTB.setValue(contrataEmployeeObject.getContractOtherData("ENTERPRISE_DIR_STAFF_NAME"));
		contractOtherData.enterpriseAgentNIFTB.setValue(contrataEmployeeObject.getContractOtherData("ENTERPRISE_DIR_STAFF_NIF"));
		contractOtherData.enterpriseAgentPositionTB.setValue(contrataEmployeeObject.getContractOtherData("ENTERPRISE_DIR_STAFF_CHARGE"));
		contractOtherData.minorAgentTB.setValue(contrataEmployeeObject.getContractOtherData("LEGAL_REPRESENTATIVE_NAME"));
		contractOtherData.minorAgentNIFTB.setValue(contrataEmployeeObject.getContractOtherData("LEGAL_REPRESENTATIVE_NIF"));
		contractOtherData.minorAgentQualityOfTB.setValue(contrataEmployeeObject.getContractOtherData("LEGAL_REPRESENTATIVE_CHARGE"));
		contractOtherData.doingFunctionsTB.setValue(contrataEmployeeObject.getContractOtherData("FUNCTIONS"));
		contractOtherData.distanceCB.setValue(contrataEmployeeObject.getContractOtherDataCB("EMPLOYEE_CONTRACT_DISTANCE"));
		contractOtherData.distanceAddressTB.setValue(contrataEmployeeObject.getContractOtherData("EMPLOYEE_CONTRACT_DIST_ADDR"));
		contractOtherData.discontinuousWorkTB.setValue(contrataEmployeeObject.getContractOtherData("DISC_WORK_DESCRIPTION"));
		contractOtherData.intermittentCyclicalActivityTB.setValue(contrataEmployeeObject.getContractOtherData("DISC_WORK_ACTIVITY"));
		contractOtherData.durationFDTB.setValue(contrataEmployeeObject.getContractOtherData("DISC_WORK_DURATION"));
		contractOtherData.activityStimationDurationFDTB.setValue(contrataEmployeeObject.getContractOtherData("DISC_WORK_ESTIMATED_DURATION"));
		contractOtherData.journeyHoursFDTB.setValue(contrataEmployeeObject.getContractOtherData("DISC_WORK_ESTIM_JOURNAL_HOURS"));
		contractOtherData.journeyPeriodFDTB.setValue(contrataEmployeeObject.getContractOtherData("DISC_WORK_ESTIM_JOURNAL_PERIOD"));
		contractOtherData.timeDistributionFDTB.setValue(contrataEmployeeObject.getContractOtherData("DISC_WORK_ESTIM_SCHEDULE"));
		setSelectedValueLB(contractOtherData.partialTimeLB, contrataEmployeeObject.getContractOtherData("DISC_AGREEMENT_COLLECTIVE"));
		contractOtherData.journeyHoursTCTB.setValue(contrataEmployeeObject.getContractOtherData("FULL_TIME_WEEK_HOURS"));
		contractOtherData.startJourneyTCTB.setValue(contrataEmployeeObject.getContractOtherData("FULL_TIME_START_TIME"));
		contractOtherData.endJourneyTCTB.setValue(contrataEmployeeObject.getContractOtherData("FULL_TIME_END_TIME"));
		contractOtherData.journeyHoursTPTB.setValue(contrataEmployeeObject.getContractOtherData("PARTIALLY_TIME_HOURS"));
		contractOtherData.agreementJourneyHoursTB.setValue(contrataEmployeeObject.getContractOtherData("DEFAULT_JOURNAL_HOURS"));
		setSelectedValueLB(contractOtherData.complementaryHoursLB, contrataEmployeeObject.getContractOtherData("COMPLEMENTARY_HOURS"));
		contractOtherData.trialPeriodTB.setValue(contrataEmployeeObject.getContractOtherData("TRIAL_DURATION"));
		contractOtherData.salaryAmountTB.setValue(contrataEmployeeObject.getContractOtherData("SALARY_AMOUNT"));
		contractOtherData.salaryPeriodTB.setValue(contrataEmployeeObject.getContractOtherData("SALARY_PERIOD"));
		contractOtherData.salaryConceptTB.setValue(contrataEmployeeObject.getContractOtherData("SALARY_CONCEPT"));
		contractOtherData.holidaysTB.setValue(contrataEmployeeObject.getContractOtherData("HOLIDAYS"));
		contractOtherData.sepeOfficeTB.setValue(contrataEmployeeObject.getContractOtherData("SEPE_MUNICIPALITY"));
		contractOtherData.accreditedDisabilityTB.setValue(contrataEmployeeObject.getContractOtherData("I_OPT2_SEPE_MUNICIPALITY"));
		setSelectedValueLB(contractOtherData.withoutDisabilitySevereLB, contrataEmployeeObject.getContractOtherData("I_OPT2_DISABILITY_NO_SEVERE"));
		setSelectedValueLB(contractOtherData.disabilitySevereLB, contrataEmployeeObject.getContractOtherData("I_OPT2_DISABILITY_SEVERE"));
		contractOtherData.subsidyTB.setValue(contrataEmployeeObject.getContractOtherData("I_OPT2_REDUCTION"));
		setSelectedValueLB(contractOtherData.fourthLawLB, contrataEmployeeObject.getContractOtherData("I_OPT5_BONUS_ART4_RDL3_2012"));
		setSelectedValueLB(contractOtherData.unemploymentLB, contrataEmployeeObject.getContractOtherData("I_OPT5_UNEMPLOYED_BT_16_30"));
		setSelectedValueLB(contractOtherData.unemploymentOldLB, contrataEmployeeObject.getContractOtherData("I_OPT5_UNEMPLOYED_GT_45"));
		contractOtherData.benefitsPerceptorCB.setValue(contrataEmployeeObject.getContractOtherDataCB("I_OPT5_UNEMPL_3_MONTH_BENEFIT"));
		contractOtherData.firstEmployeeCB.setValue(contrataEmployeeObject.getContractOtherDataCB("I_OPT5_FIRST_EMPLOYEE_AND_LT_30"));
		setSelectedValueLB(contractOtherData.employeeLB, contrataEmployeeObject.getContractOtherData("I_OPT6_AGE"));
		contractOtherData.agreementLineOneTB.setValue(contrataEmployeeObject.getContractOtherData("I_OPT6_AGREEMENT_COLLECTIVE1"));
		contractOtherData.agreementLineTwoTB.setValue(contrataEmployeeObject.getContractOtherData("I_OPT6_AGREEMENT_COLLECTIVE2"));
		setSelectedValueLB(contractOtherData.contactHoursLB, contrataEmployeeObject.getContractOtherData("I_OPT15_ONSITE_HOURS"));
		contractOtherData.hoursTB.setValue(contrataEmployeeObject.getContractOtherData("I_OPT15_ONSITE_WEEK_HOURS"));
		setSelectedValueLB(contractOtherData.remunerationFormLB, contrataEmployeeObject.getContractOtherData("I_OPT15_SALARY"));
		setSelectedValueLB(contractOtherData.overnightAgreementLB, contrataEmployeeObject.getContractOtherData("I_OPT15_OVERNIGHT"));
		contractOtherData.overnightRegimeTB.setValue(contrataEmployeeObject.getContractOtherData("I_OPT15_OVERNIGHT_WEEK_DAYS"));
		setSelectedValueLB(contractOtherData.quoteReductionTCLB, contrataEmployeeObject.getContractOtherData("I_OPT17_FULL_TIME_QUOTE_BONUS"));
		setSelectedValueLB(contractOtherData.quoteReductionFDLB, contrataEmployeeObject.getContractOtherData("I_OPT17_DISCONT_TIME_QUOTE_BONUS"));
		contractOtherData.sepeOfficeCOTB.setValue(contrataEmployeeObject.getContractOtherData("I_OPT17_SRC_CONTRACT_SEPE_MUNIC"));
	}
	
	private void fillContractOtherDataTemp() {
		contractOtherData.enterpriseAgentTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_ENTERPRISE_DIR_STAFF_NAME"));
		contractOtherData.enterpriseAgentNIFTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_ENTERPRISE_DIR_STAFF_NIF"));
		contractOtherData.enterpriseAgentPositionTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_ENTERPRISE_DIR_STAFF_CHARGE"));
		contractOtherData.minorAgentTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_LEGAL_REPRESENTATIVE_NAME"));
		contractOtherData.minorAgentNIFTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_LEGAL_REPRESENTATIVE_NIF"));
		contractOtherData.minorAgentQualityOfTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_LEGAL_REPRESENTATIVE_CHARGE"));
		contractOtherData.doingFunctionsTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_FUNCTIONS"));
		contractOtherData.distanceTempCB.setValue(contrataEmployeeObject.getContractOtherDataCB("T_EMPLOYEE_CONTRACT_DISTANCE"));
		contractOtherData.distanceAddressTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_EMPLOYEE_CONTRACT_DIST_ADDR"));
		contractOtherData.journeyHoursTCTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_FULL_TIME_WEEK_HOURS"));
		contractOtherData.startJourneyTCTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_FULL_TIME_START_TIME"));
		contractOtherData.endJourneyTCTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_FULL_TIME_END_TIME"));
		contractOtherData.lowJourneyTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_PARTIALLY_TIME_JOB_LOWER_THAN"));
		contractOtherData.timeDistributionTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_PARTIALLY_TIME_JOB_DISTRIB"));
		contractOtherData.endContractTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_END_DATE_TEXT"));
		contractOtherData.trialPeriodTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_TRIAL_DURATION"));
		contractOtherData.permitedHighDurationTempCB.setValue(contrataEmployeeObject.getContractOtherDataCB("T_GREATER_DURATION_AGREEMENT_COL"));
		contractOtherData.salaryAmountTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_SALARY_AMOUNT"));
		contractOtherData.salaryPeriodTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_SALARY_PERIOD"));
		contractOtherData.salaryConceptTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_SALARY_CONCEPT"));
		contractOtherData.holidaysTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_HOLIDAYS"));
		contractOtherData.sepeOfficeTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_SEPE_MUNICIPALITY"));
		contractOtherData.workTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT1_WORK_DESCRIPTION1"));
		contractOtherData.workMoreTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT1_WORK_DESCRIPTION2"));
		contractOtherData.taskTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT2_WORK_DESCRIPTION1"));
		contractOtherData.taskMoreTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT2_WORK_DESCRIPTION2"));
		contractOtherData.sustituteEmployeeTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT3_REPLACED_WORKER_NAME"));
		setSelectedValueLB(contractOtherData.requirementsTempLB, contrataEmployeeObject.getContractOtherData("T_OPT10_REQUIREMENTS_OPT"));
		setSelectedValueLB(contractOtherData.formationTempLB, contrataEmployeeObject.getContractOtherData("T_OPT10_FORMATION_OPT"));
		setSelectedValueLB(contractOtherData.formationWillTempLB, contrataEmployeeObject.getContractOtherData("T_OPT10_FORMATION_TYPE_OPT"));
		contractOtherData.officeSPEmployeeTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT10_FORMATION_TYPE_OPT1_TEXT"));
		contractOtherData.lenguageFormationTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT10_FORMATION_TYPE_OPT2_TEXT"));
		setSelectedValueLB(contractOtherData.hoursDealTempLB, contrataEmployeeObject.getContractOtherData("T_OPT12_ONSITE_HOURS"));
		contractOtherData.presentHoursTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT12_ONSITE_WEEK_HOURS"));
		contractOtherData.distributionHoursTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT12_ONSITE_HOURS_DISTRIB"));
		setSelectedValueLB(contractOtherData.timeCompensationTempLB, contrataEmployeeObject.getContractOtherData("T_OPT12_SALARY_OPT"));
		setSelectedValueLB(contractOtherData.dealOvernightLB, contrataEmployeeObject.getContractOtherData("T_OPT12_OVERNIGHT"));
		contractOtherData.overnightRegimeTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT12_OVERNIGHT_WEEK_DAYS"));
		contractOtherData.officialOrganismTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT13_DISABILITY_ISSUED_BY"));
		setSelectedValueLB(contractOtherData.withoutSevereDisTempLB, contrataEmployeeObject.getContractOtherData("T_OPT13_DISABILITY"));
		setSelectedValueLB(contractOtherData.severeDisTempLB, contrataEmployeeObject.getContractOtherData("T_OPT13_SEVERE_DISABILITY"));
		contractOtherData.adaptationPeriodTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT14_TRIAL_PERIOD"));
		contractOtherData.adaptationConditionsTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT14_TRIAL_TERMS"));
		setSelectedValueLB(contractOtherData.adaptationWorkTempLB, contrataEmployeeObject.getContractOtherData("T_OPT14_PROFESSION"));
		contractOtherData.socialPersonalAdjustTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT14_DISTANCE_ADJUSTMENT"));
		contractOtherData.socialPersonalAdjustMoreTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT14_DISTANCE_ADJUSTMENT_MORE"));
		contractOtherData.colectiveAgreementTempTB.setValue(contrataEmployeeObject.getContractOtherData("T_OPT14_COLLECTIVE_AGREEMENT"));
	}
	
	private void fillContractOtherDataFormation() {
		contractOtherData.enterpriseAgentFormTB.setValue(contrataEmployeeObject.getContractOtherData("L_ENTERPRISE_DIR_STAFF_NAME"));
		contractOtherData.enterpriseAgentNIFFormTB.setValue(contrataEmployeeObject.getContractOtherData("L_ENTERPRISE_DIR_STAFF_NIF"));
		contractOtherData.enterpriseAgentPositionFormTB.setValue(contrataEmployeeObject.getContractOtherData("L_ENTERPRISE_DIR_STAFF_CHARGE"));
		contractOtherData.minorAgentFormTB.setValue(contrataEmployeeObject.getContractOtherData("L_LEGAL_REPRESENTATIVE_NAME"));
		contractOtherData.minorAgentNIFFormTB.setValue(contrataEmployeeObject.getContractOtherData("L_LEGAL_REPRESENTATIVE_NIF"));
		contractOtherData.minorAgentQualityOfFormTB.setValue(contrataEmployeeObject.getContractOtherData("L_LEGAL_REPRESENTATIVE_CHARGE"));
		setSelectedValueLB(contractOtherData.ssReductionFormLB, contrataEmployeeObject.getContractOtherData("L_QUOTE_BONUS"));
		setSelectedValueLB(contractOtherData.employeeFormLB, contrataEmployeeObject.getContractOtherData("L_EMPLOYEE_OPT"));
		contractOtherData.workplaceFormTB.setValue(contrataEmployeeObject.getContractOtherData("L_CONTRACT_WORKPLACE_ADDRESS"));
		contractOtherData.tutorFormTB.setValue(contrataEmployeeObject.getContractOtherData("L_FORMATION_TEACHER"));
		contractOtherData.efectiveWorkHoursFormTB.setValue(contrataEmployeeObject.getContractOtherData("L_HORARIO_LABORAL"));
		contractOtherData.activityHoursFormTB.setValue(contrataEmployeeObject.getContractOtherData("L_HORARIO_LECTIVO"));
		contractOtherData.trialPeriodFormTB.setValue(contrataEmployeeObject.getContractOtherData("L_TRIAL_DURATION"));
		contractOtherData.agreementTrialFormCB.setValue(contrataEmployeeObject.getContractOtherDataCB("L_TRIAL_DURATION_INCREASE"));
		contractOtherData.salaryAmountFormTB.setValue(contrataEmployeeObject.getContractOtherData("L_SALARY_AMOUNT"));
		contractOtherData.salaryPeriodFormTB.setValue(contrataEmployeeObject.getContractOtherData("L_SALARY_PERIOD"));
		contractOtherData.holidaysFormTB.setValue(contrataEmployeeObject.getContractOtherData("L_HOLIDAYS"));
		contractOtherData.degreeExistFormCB.setValue(contrataEmployeeObject.getContractOtherDataCB("L_ANNEX_I_CHECK"));
		contractOtherData.degreeExist2FormCB.setValue(contrataEmployeeObject.getContractOtherDataCB("L_ANNEX_II_CHECK"));
	}
	
	private void fillContractOtherDataPractice() {
		contractOtherData.enterpriseAgentPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_ENTERPRISE_DIR_STAFF_NAME"));
		contractOtherData.enterpriseAgentNIFPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_ENTERPRISE_DIR_STAFF_NIF"));
		contractOtherData.enterpriseAgentPositionPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_ENTERPRISE_DIR_STAFF_CHARGE"));
		contractOtherData.minorAgentPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_LEGAL_REPRESENTATIVE_NAME"));
		contractOtherData.minorAgentNIFPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_LEGAL_REPRESENTATIVE_NIF"));
		contractOtherData.minorAgentQualityOfPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_LEGAL_REPRESENTATIVE_CHARGE"));
		contractOtherData.profesionalCertPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_PROFESSIONAL_CERT"));
		contractOtherData.obtainingDatePracTB.setValue(contrataEmployeeObject.getContractOtherData("P_PROFESSIONAL_CERT_OBTAIN_DATE"));
		contractOtherData.disabilityCertPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_DISABILITY_ISSUE_ENTITY"));
		contractOtherData.disabilityCertMorePracTB.setValue(contrataEmployeeObject.getContractOtherData("P_DISABILITY_ISSUE_ENTITY_MORE"));
		setSelectedValueLB(contractOtherData.firstContractPracLB, contrataEmployeeObject.getContractOtherData("P_FIRST_CONTRACT"));
		contractOtherData.journeyHoursPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_FULL_TIME_WEEK_HOURS"));
		contractOtherData.startJourneyPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_FULL_TIME_START_TIME"));
		contractOtherData.endJourneyPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_FULL_TIME_END_TIME"));
		contractOtherData.distributionJourneyPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_JOB_TIME_DISTRIBUTION2"));
		contractOtherData.trialPeriodPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_TRIAL_DURATION"));
		contractOtherData.salaryAmountPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_SALARY_AMOUNT"));
		contractOtherData.salaryPeriodPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_SALARY_PERIOD"));
		contractOtherData.salaryConceptPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_SALARY_CONCEPT"));
		contractOtherData.holidaysPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_HOLIDAYS"));
		contractOtherData.sepeComunicationPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_SEPE_START_COMMUNICATION"));
		contractOtherData.endSepeComunicationPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_SEPE_END_COMMUNICATION"));
		setSelectedValueLB(contractOtherData.unemploymentSubsidyPracLB, contrataEmployeeObject.getContractOtherData("P_OPT3_UNEMPLOYMENT"));
		contractOtherData.adaptationPeriodPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_OPT4_TRIAL_DURATION"));
		contractOtherData.adaptationConditionsPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_OPT4_TRIAL_DURATION_CONDITIONS"));
		contractOtherData.adaptationWorkPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_OPT4_WORK_PLACE_ADAPTATIONS"));
		contractOtherData.personalSocialAdjustPracTB.setValue(contrataEmployeeObject.getContractOtherData("P_OPT4_STAFF_ADJUSTMENT"));
		contractOtherData.personalSocialAdjustMorePracTB.setValue(contrataEmployeeObject.getContractOtherData("P_OPT4_STAFF_ADJUSTMENT_MORE"));
		setSelectedValueLB(contractOtherData.motivationPracLB, contrataEmployeeObject.getContractOtherData("P_OPT5_MOTIVATION"));
		setSelectedValueLB(contractOtherData.employerPracLB, contrataEmployeeObject.getContractOtherData("P_OPT5_EMPLOYER"));
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
		
		contrataEmployeeObject.getAgreement(agreementId,  
		(agreement) -> {
			for (Level levelRecord : agreement.getLevels())
				for (String categoryRecord : agreement.getCategoriesMap().get(levelRecord.getId()))
					employee.level.addItem(levelRecord.getDescription() + " - " + categoryRecord, String.valueOf(levelRecord.getId()));

			contrataEmployeeObject.setContractAgreementId(agreement.getId());
			contrataEmployeeObject.setContractAgreementLevelId(null);
		},
		(throwable) -> {
			contrataEmployeeObject.setContractAgreementId(null);
			contrataEmployeeObject.setContractAgreementLevelId(null);
			employee.category.setEnabled(false);
			employee.category.setValue("");
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.category);
		});
	}
	
	private void getAgreementLevelsAndSetLevel(Integer agreementId, Integer agreementLevelId, String category_description) {
		employee.level.clear();
		employee.level.addItem("-", "-1");
		
		contrataEmployeeObject.getAgreement(agreementId,  
		(agreement) -> {
			for (Level levelRecord : agreement.getLevels())
				for (String categoryRecord : agreement.getCategoriesMap().get(levelRecord.getId()))
					employee.level.addItem(levelRecord.getDescription() + " - " + categoryRecord, String.valueOf(levelRecord.getId()));

			contrataEmployeeObject.setContractAgreementId(agreement.getId());
			Integer agreementIdx = getAgreementLevelIdx(agreement, agreement.getLevels(), agreementId, agreementLevelId, category_description) + 1;
			employee.level.setSelectedIndex(agreementIdx);
		},
		(throwable) -> {
			contrataEmployeeObject.setContractAgreementId(null);
			contrataEmployeeObject.setContractAgreementLevelId(null);
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
	
	private void setContractOtherData(String name, String value) {
		this.contrataEmployeeObject.addContractOtherData(name, value);
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
	
}

package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractJourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ContractTypeRecord;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ModelRecord;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.Iban;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.Municipalities;
import com.esferalia.aon.gwt.payroll.shared.Rbank;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.Viewer;

public class EmployeeDraft extends Composite {
	
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
			byte ssRegime = Byte.valueOf(this.ssRegimeType.getSelectedValue()).byteValue();
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
			
			String activityCCC = String.valueOf(this.activityCCC.getSelectedValue());
			
			if(this.activityCCC.getSelectedIndex() == 0)
				employeeDraftObject.setActivityInfo(null);
			else
				employeeDraftObject.setActivityInfo(activityCCC);
			
			saving();
			
//			String activityCCC = this.activityCCC.getSelectedItemText();
//			if(activityCCC.equals("-")){
//				employeeDraftObject.setContractActivityId(null);
//				employeeDraftObject.setContractCCCId(null);
//				employeeDraftObject.setContractCCCType((Byte)null);
//			}else{
//				String activityStr = activityCCC.split(" -")[0];
//				String cccStr = activityCCC.split("\\[")[1].split("\\]")[0];
//				String cccTypeStr = activityCCC.split("- ")[1].split("\\[")[0];
//				String cccGeozoneStr = activityCCC.split("- ")[2];
//				int cccTypeInt = -1;
//				for(int i=0; i< CCCType.values().length; i++){
//					if(CCCType.values()[i].name().equals(cccTypeStr)){
//						cccTypeInt = i;
//						break;
//					}
//				}
//				byte cccType = (byte) cccTypeInt;
//				
//				Integer activityId = employeeDraftObject.getActivityIdByName(activityStr);
//				Integer cccId = employeeDraftObject.getCCCIdByNumber(cccStr, cccType, cccGeozoneStr);
//				employeeDraftObject.setContractActivityId(activityId);
//				employeeDraftObject.setContractCCCId(cccId);
//				employeeDraftObject.setContractCCCType(cccType);
//			}
//			saving();
		}

		@Override
		public void onContractWorkplaceChange() {
			Integer workplaceId = Integer.parseInt(this.workplace.getSelectedValue());
			employeeDraftObject.setContractWorkplaceId(workplaceId);
			saving();
			
//			String workplaceName = this.workplace.getSelectedItemText();
//			Integer workplaceId = employeeDraftObject.getWorkplaceIdByName(workplaceName);
//			employeeDraftObject.setContractWorkplaceId(workplaceId);
//			saving();
		}

		@Override
		public void onContractTypeChange() {
			if(0 == this.contractType.getSelectedIndex()) {
				cantSaveWithOutIt();
				return;
			}
			
			this.modality.clear();
			this.modality.addItem("-");
			
			String contractType = null;
			
			contractType = String.valueOf(this.contractType.getSelectedValue());
			Integer contractTypeInt = Integer.parseInt(contractType);
			
			if((contractTypeInt >= 200 && contractTypeInt<300) || (contractTypeInt >= 500 && contractTypeInt<600 || contractTypeInt == 0)) {
				showElementsPartialTimeContract();
				
				ContractJourneyDuration contractJourneyDuration = employeeDraftObject.getContractJourneyDuration();
				if(contractJourneyDuration.getJourniesSize() != 0) {
					String result = contractJourneyDuration.getJourneyText();

					employee.journeyDuration.setText(result);
					employee.journeyDuration.setText(contractJourneyDuration.getJourneyText());
					employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
					employee.journeyDuration.removeStyleName("aon-icon-exception aon-finding-toolbar-item-no-border");
				}else {
					employee.journeyDuration.addStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
					employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
					employee.journeyDuration.setText("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
					employeeDraftObject.setContractJourneyDuration(new TreeMap<Date, ArrayList<JourneyDuration>>());
				}
		    } else {
				this.showElementsFullTimeContract();
				employeeDraftObject.setContractJourneyDuration(new TreeMap<Date, ArrayList<JourneyDuration>>());
		    }
			
			List<ModelRecord> contractTypeModels = contractTypeClass.getModelsContractType(contractTypeInt);
			for (ModelRecord model : contractTypeModels)
				this.modality.addItem(model.getModelDescription(), model.getEnumeration().toString());
			
			employeeDraftObject.setContractType(contractType);
			
			saving();
			
//			this.modality.clear();
//			this.modality.addItem("-");
//			Integer contractTypeId = -1;
//			if (this.contractType.getSelectedIndex() != 0) {
//				String contract_type_id_str = this.contractType.getSelectedItemText().split(" -")[0];
//				contractTypeId = Integer.parseInt(contract_type_id_str);
//				if((contractTypeId >= 200 && contractTypeId<300) || (contractTypeId >= 500 && contractTypeId<600)) {
//					this.showElementsPartialTimeContract();
//					this.journeyDuration.addStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
//					this.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
//					this.journeyDuration.setText("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
//				}else{
//					this.showElementsFullTimeContract();
//					employeeDraftObject.setContractJourneyDuration(new TreeMap<Date, ArrayList<JourneyDuration>>());
//				}
//			}
//
//			List<ModelRecord> contractTypeModels = contractTypeClass.getModelsContractType(contractTypeId);
//			
//			for (ModelRecord m : contractTypeModels)
//				this.modality.addItem(m.getModelDescription());
//						
//			if (this.contractType.getSelectedIndex() == 0) {
//				employeeDraftObject.setContractType(null);
//				employeeDraftObject.setContractModel(null);
//			} else {
//				String contract_type_id_str = this.contractType.getSelectedItemText().split(" -")[0];
//				employeeDraftObject.setContractType(contract_type_id_str);
//			}
//			saving();
		}

		@Override
		public void onContractModalityChange() {
			if (this.modality.getSelectedIndex() == 0)
				employeeDraftObject.setContractModel(null);
			else {
				Integer contractModel = Integer.parseInt(this.modality.getSelectedValue());
				employeeDraftObject.setContractModel(contractModel); // GET String of enum in JooqEmployee.java
			}
			saving();
			
//			if (this.contractType.getSelectedIndex() == 0 || this.modality.getSelectedIndex() == 0) {
//				employeeDraftObject.setContractModel(null);
//			} else {
//				String contract_type_id_str = this.contractType.getSelectedItemText().split(" -")[0];
//				Integer contractTypeId = Integer.parseInt(contract_type_id_str);
//
//				String contractModelDescription = modality.getSelectedItemText();
//				Integer contractModelEnum = contractTypeClass.getContractModelId(contractTypeId, contractModelDescription);
//				employeeDraftObject.setContractModel(contractModelEnum); // GET String of enum in JooqEmployee.java
//			}
//			saving();
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
			} else
				this.seniority_dateStatus.getElement().getStyle().setDisplay(Display.NONE);
			
			employeeDraftObject.setContractSeniorityDate(this.seniority_date.getValue());
			saving();
			
//			if(null == this.start_date.getValue() || null == this.seniority_date.getValue()) {
//				this.seniority_dateStatus.setStyleName("aon-finding-toolbar-item aon-icon-info aon-finding-toolbar-item-no-border");
//				this.seniority_dateStatus.removeStyleName(style.hide());
//				this.seniority_dateStatus.addStyleName(style.marginTop());
//				this.seniority_dateStatus.setTitle("La fecha de inicio no coincide con la de antig" + String.valueOf("\u00FC") + "edad.");
//			}else {
//				Date startDate = this.start_date.getValue();
//				DateUtils.resetTime(startDate);
//				Date seniorityDate = this.seniority_date.getValue();
//				DateUtils.resetTime(seniorityDate);
//				
//				if(startDate.equals(seniorityDate)) {
//					this.seniority_dateStatus.addStyleName(style.hide());
//				}else {
//					this.seniority_dateStatus.setStyleName("aon-finding-toolbar-item aon-icon-info aon-finding-toolbar-item-no-border");
//					this.seniority_dateStatus.removeStyleName(style.hide());
//					this.seniority_dateStatus.addStyleName(style.marginTop());
//					this.seniority_dateStatus.setTitle("La fecha de inicio no coincide con la de antig" + String.valueOf("\u00FC") + "edad.");
//				}
//			}
//			saving();
		}

		@Override
		public void onContractAgreementChange() {
			this.level.clear();
			
			if (this.agreement.getSelectedIndex() == 0 ) {
				employeeDraftObject.setContractAgreementId(null);
				employeeDraftObject.setContractAgreementLevelId(null);
				this.category.setValue("");
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.category);
				saving();
				return;
			}
			
			Integer agreementId = 
			Integer.valueOf(this.agreement.getSelectedValue()); 
			
			this.level.addItem("-", "-1");
			
			employeeDraftObject.getAgreement(agreementId,  
			(agreement) -> {
				for (Level levelRecord : agreement.getLevels())
					for (String categoryRecord : agreement.getCategoriesMap().get(levelRecord.getId()))
						this.level.addItem(levelRecord.getDescription() + " - " + categoryRecord, String.valueOf(levelRecord.getId()));

				employeeDraftObject.setContractAgreementId(agreement.getId());
				employeeDraftObject.setContractAgreementLevelId(null);
				saving();
			},
			(throwable) -> {
				employeeDraftObject.setContractAgreementId(null);
				employeeDraftObject.setContractAgreementLevelId(null);
				this.category.setEnabled(false);
				this.category.setValue("");
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.category);
				saving();
			});
		}

		@Override
		public void onContractAgreementLevelChange() {
			if (this.agreement.getSelectedIndex() == 0 || this.level.getSelectedIndex() == 0) {
				employeeDraftObject.setContractAgreementLevelId(null);
				this.category.setEnabled(false);
				this.category.setValue("");
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.category);
			} else {
				Integer agreementLevelId = Integer.parseInt(this.level.getSelectedValue());
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
			else {
				String quoteGroup = String.valueOf(this.quote_group.getSelectedValue()); 
				employeeDraftObject.setContractQuoteGroup(quoteGroup);
			}
			saving();
			
//			if (quote_group.getSelectedIndex() == 0)
//				employeeDraftObject.setContractQuoteGroup(null);
//			else
//				employeeDraftObject.setContractQuoteGroup(this.quote_group.getSelectedIndex());
//			saving();
		}

		@Override
		public void onContractOccupationChange() {
			if (this.occupation.getSelectedIndex() == 0)
				employeeDraftObject.setContractOccupation(null);
			else {
				String occupation = String.valueOf(this.occupation.getSelectedValue());
				employeeDraftObject.setContractOccupation(occupation);
			}
			saving();
			
//			if (this.occupation.getSelectedIndex() == 0)
//				employeeDraftObject.setContractOccupation(null);
//			else
//				employeeDraftObject.setContractOccupation(this.occupation.getSelectedIndex());
//			saving();
		}

		@Override
		public void onContractJourneyTypeChange() {
			 Boolean journey_type = (this.journeyType.getSelectedIndex() == 0) ? true : false;
			 employeeDraftObject.setContractJourneyType(journey_type);
			 if(this.journeyType.getSelectedIndex() == 0)
				 employee.showElementsFullTimeContract();
			 else
				 showElementsPartialTimeContract();
			 saving();
			
//			Boolean journey_type = (this.journeyType.getSelectedIndex() == 0) ? true : false;
//			 employeeDraftObject.setContractJourneyType(journey_type);
//			 if(this.journeyType.getSelectedIndex() == 0)
//				 employee.showElementsFullTimeContract();
//			 else
//				 employee.showElementsPartialTimeContract();
//			 saving();
		}
		
		@Override
		public void onContractJourneyDurationClick() {
			EmployeeTree.showEmployeeCalendar(employeeDraftObject.getEmployeeCalendar());
			
//			ContractJourneyDialog dialog = new ContractJourneyDialog(employeeDraftObject.getContractStartDate(), employeeDraftObject.getContractEndDate(),
//					employeeDraftObject.getContractJourneyDuration()) {
//
//				@Override
//				protected void onSave() {
//					ContractJourneyDuration contractJourneyDuration = this.getContractJourneyDuration();
//					if(contractJourneyDuration.getJourniesSize() != 0) {
//						String result = contractJourneyDuration.getJourneyText();
////						String result = "Desde ";
////						Double hours = 0.0;
////						for(JourneyDuration journeyDuration : contractJourneyDuration.getContractJourneyDuration().descendingMap().entrySet().iterator().next().getValue()) {
////							hours += Double.parseDouble(((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()));
////							if("HORAS_LUNES" == journeyDuration.getName()) result += formatDate(journeyDuration.getStartDate()) + " L : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
////							if("HORAS_MARTES" == journeyDuration.getName()) result += ", M : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
////							if("HORAS_MIERCOLES" == journeyDuration.getName()) result += ", X : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
////							if("HORAS_JUEVES" == journeyDuration.getName()) result += ", J : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
////							if("HORAS_VIERNES" == journeyDuration.getName()) result += ", V : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
////							if("HORAS_SABADO" == journeyDuration.getName()) result += ", S : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ";
////							if("HORAS_DOMINGO" == journeyDuration.getName()) result += ", D : " + ((null == journeyDuration.getExpression() || "" == journeyDuration.getExpression()) ? "0" : journeyDuration.getExpression()) + " ( " + hours + " horas semanales )";
////						}
//						employee.journeyDuration.setText(result);
//						employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
//						employee.journeyDuration.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
//					}else {
//						employee.journeyDuration.addStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
//						employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
//						employee.journeyDuration.setText("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
//					}
//					employeeDraftObject.setContractJourneyDuration(contractJourneyDuration.getContractJourneyDuration());
//					saving();
//				}				
//			};
//			dialog.center();
//			dialog.show();		
		}
		
		//EMPLOYEE TABLE

		@Override
		public void onEmployeeBirthDateChange() {
			Date birthDate = this.birth_date.getValue();
			
			if(null != birthDate) {
				Date actualDay = new Date();
				Integer age = getYears(actualDay, birthDate);
				this.age.setText("( " + (age) + " a" + String.valueOf("\u00F1") + "os )");
			} else
				this.age.setText("");
			
			employeeDraftObject.setEmployeeBirthDate(this.birth_date.getValue());
			saving();
		}

		@Override
		public void onEmployeeGenderChange() {
			byte gender = Byte.valueOf(this.gender.getSelectedValue()).byteValue();
			employeeDraftObject.setEmployeeGender(gender);
			
//			employeeDraftObject.setEmployeeGender(this.gender.getSelectedIndex());
			saving();
		}

		@Override
		public void onEmployeeCivilStatusChange() {
			byte civilStatus = Byte.valueOf(this.civilStatus.getSelectedValue()).byteValue();
			employeeDraftObject.setEmployeeCivilStatus(civilStatus);
			
			saving();
		}

		@Override
		public void onEmployeeStreetTypeChange() {
			String streetType = String.valueOf(this.street_type.getSelectedValue());
			employeeDraftObject.setEmployeeStreetType(streetType);
			saving();
			
//			Integer streetTypeIdx = this.street_type.getSelectedIndex();
//			String shortCode = StreetType.values()[streetTypeIdx].getShortCode();
//			employeeDraftObject.setEmployeeStreetType(shortCode);
//			saving();
		}

		@Override
		public void onEmployeeAddressChange() {}

		@Override
		public void onEmployeeAddressNumChange() {}
		
		@Override
		public void onEmployeeAddressInfoChange() {}

		@Override
		public void onEmployeeAddressZipChange() {
			if(this.addressZip.getValue().length() >= 2) {
				String zip = this.addressZip.getValue().substring(0, 2);
				setSelectedValueLB(addressProvince, zip); 
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), addressProvince);
			}
		}

		@Override
		public void onEmployeeAddressMunicipalityChange() {
//			employeeDraftObject.setEmployeeAddressCity(municipalities.getZipByMunicipalityName(this.addressMunicipality.getSelectedItemText()).toString());
			employeeDraftObject.setEmployeeAddressCity(municipalities.getZipByMunicipalityName(this.addressMunicipality.getSelectedItemText()).toString());
			saving();
		}

		@Override
		public void onEmployeeAddressProvinceChange() {
			String province = this.addressProvince.getSelectedItemText();
			String addressProvinceCode = this.addressProvince.getSelectedValue();
			employeeDraftObject.setEmployeeAddressProvince(addressProvinceCode);
			employeeDraftObject.setEmployeeAddressCity("-1");
			updateMunicipalities();
			saving();
			
//			employeeDraftObject.setEmployeeAddressProvince(this.addressProvince.getSelectedItemText());
//			updateMunicipalities();
//			employeeDraftObject.setEmployeeAddressCity("-1");
//			saving();
		}

		@Override
		public void onEmployeeMobileChange() {}

		@Override
		public void onEmployeePhoneChange() {}

		@Override
		public void onEmployeeEmailChange() {}

		@Override
		public void onEmployeePayMethodChange() {
			byte methodPay = Byte.valueOf(this.payMethod.getSelectedValue()).byteValue();
			employeeDraftObject.setEmployeePayMethod(methodPay);
			
			if(this.payMethod.getSelectedIndex() == 3) { //TRANFERENCIA
				this.account.setEnabled(true);
				this.bic.setEnabled(true);
			} else {
				this.account.setValue(null);
				this.bic.setValue(null);
				this.account.setEnabled(false);
				this.bic.setEnabled(false);
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), account);
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), bic);
			}
			
			saving();
			
//			String methodPay = this.payMethod.getSelectedItemText(); 
//			employeeDraftObject.setEmployeePayMethod(methodPay);
//			if("TRANSFERENCEIA" != methodPay){
//				this.account.setValue(null);
//				onEmployeeAccountChange();
//				this.bic.setValue(null);
//				onEmployeeBICChange();
//			}
//			saving();
		}

		@Override
		public void onEmployeeBICChange() {
			String value = employee.bic.getValue();
			employeeDraftObject.setEmployeeBIC(value);
			saving();
		}

		@Override
		public void onEmployeeAccountChange() {
			String value = employee.account.getValue();
			
			if(value.length() >= 24) {
				reformatAccount(employee.account);
				
				if(Iban.validateIBAN(value)) {
					employee.accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
					employee.accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
				}else {
					employee.accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
					employee.accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
				}
				
				Rbank rbank = employeeDraftObject.getRbank(value);
				if(null != rbank) {
					employee.bic.setValue(rbank.getBic(), false);
					employeeDraftObject.setEmployeeBIC(rbank.getBic());
					employeeDraftObject.setEmployeeRbankId(rbank.getId());
				}
				
				
				employeeDraftObject.setEmployeeAccount(value);
				saving();
			}
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface EmployeeDraftUiBinder extends UiBinder<Widget, EmployeeDraft> {}
	
	private static EmployeeDraftUiBinder uiBinder = GWT.create(EmployeeDraftUiBinder.class);
	
	@UiField (provided = true)
	Employee employee;
	
	@UiField
	Label saveStatus;
	
	@UiField
	Button afiButton;
	
	@UiField
	Button peculiaritiesButton;
	
	@UiField
	Button bonificationsButton;
	
	@UiField
	Button redoButton;

	@UiField
	Button undoButton;

	@UiField
	Button undoAllButton;
	
	@UiField
	DeckPanel deckPanel;

	@UiField
	Button idcButton;
	
	@UiField
	Button closePdfButton;
	
	@UiField
	Viewer pdfViewer;
	
	@UiField
	ListBox zoomListBox;
	
	@UiField
	Button downloadButton;
	// ------------------------------------------------------ VARIABLES DE LA CLASE -------------------------------------------------
	private int zoom;

	private EmployeeDraftObject employeeDraftObject;
	public ContractType contractType;
	private Municipalities municipalities;
	
	private Timer saveTimer;
	private Consumer<EmployeeContractInfo> onSaved ;

	// --------------------------------------------------------- CONSTRUCTOR --------------------------------------------------------

	public EmployeeDraft() {
		
		this.zoom = Constants.DEFAULT_ZOOM;
		
		employee = new EmployeeImplementation();
		
		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		
		//Hide clean employee, only used in New Employee
		employee.clear_employee.getElement().getStyle().setDisplay(Display.NONE);
		
		//Set save status
		saveStatus.setTitle("Cada cambio que hagas se guarda autom\u00E1ticamente");	
		onSaved = this::onSavedNoop;
		
		showEmployee();		
		initZoomList();
	}
	
	// -------------------------------------------------- UiHandlers --------------------------------------------------
	
	@UiHandler("afiButton")
	void onAFIButtonClick(ClickEvent event) {
		
		EmployeeAFIDialog dialog = new EmployeeAFIDialog(
				this.employee.start_date.getValue(),
				this.employee.end_date.getValue(),
				this.employee.contractType.getSelectedIndex(),
				this.employee.quote_group.getSelectedIndex(),
				this.employee.occupation.getSelectedIndex(),
				this.employeeDraftObject.getPayrollDate(),
				this.employeeDraftObject.getContractId(),
				this.employeeDraftObject.getDomainId(),
				this.employeeDraftObject.getWorkplaceId()
				){};
			
		dialog.center();
		dialog.show();
		
	}
	
	@UiHandler("peculiaritiesButton")
	void onPeculiaritiesButtonClick(ClickEvent event) {
		EmployeePeculiaritiesDialog dialog = new EmployeePeculiaritiesDialog(this.employeeDraftObject.getContractId(), this.employeeDraftObject.getContractStartDate());
		dialog.center();
		dialog.show();
	}
	
	@UiHandler("bonificationsButton")
	void onBonificationsButtonClick(ClickEvent event) {
		SSBonusDraft dialog = new SSBonusDraft(this.employeeDraftObject.getContractId());
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
	
	@UiHandler("closePdfButton")
	void onClosePdfButtonClick(ClickEvent event) {
		showEmployee();
	}
	
	@UiHandler("idcButton")
	void onIdcButtonClick(ClickEvent event) {
		showIdc();
	}
	
	@UiHandler("zoomListBox")
	void onZoomListBoxChange(ChangeEvent event) {
		int index =zoomListBox.getSelectedIndex();
		String text = zoomListBox.getItemText(index);
		zoom = (int) (Constants.PERCENT_FORMAT.parse(text));
		pdfViewer.scale(zoom / 100.00);
	}	
	
	@UiHandler("downloadButton")
	void onDownloadClick(ClickEvent event) {
		String fileName = employeeDraftObject.getEmployeeFullName() + " IDC.pdf";
		pdfViewer.download(fileName);
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
		
		employee.addressInfo.addKeyUpHandler(e-> {
			
			String value = employee.addressInfo.getValue();
			String saved = employeeDraftObject.getEmployeeAddressInfo();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			employeeDraftObject.setEmployeeAddressInfo(value);
			saving();
		});
		
		employee.addressZip.addKeyUpHandler(e-> {
			
			String value = employee.addressZip.getValue();
			String saved = employeeDraftObject.getEmployeeAddressZip();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			employeeDraftObject.setEmployeeAddressZip(value);
			// saving();
		});
		
//		employee.addressZip.addValueChangeHandler(new ValueChangeHandler<String>() {
//			
//			@Override
//			public void onValueChange(ValueChangeEvent<String> event) {
//				if(employee.addressZip.getValue().length() >= 2) {
//					String zip = employee.addressZip.getValue().substring(0, 2);
//					employee.addressProvince.setSelectedIndex( ProvinceContract.getProvinceIndex(ProvinceContract.getName(zip)));
//					employee.onEmployeeAddressProvinceChange();
//					.
//				}
//			}
//		});
		
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
		
//		employee.bic.addKeyUpHandler(e-> {
//			
//			String value = employee.bic.getValue();
//			String saved = employeeDraftObject.getEmployeeBIC();
//			if ( AonStringUtils.equals(value, saved))
//				return;
//			
//			employeeDraftObject.setEmployeeBIC(value);
//			saving();
//		});
//		
//		employee.account.addKeyUpHandler(e-> {
//			String value = employee.account.getValue();
//			value = value.replaceAll("\\W+", "");
//			
//			String saved = employeeDraftObject.getEmployeeAccount();
//			
//			if ( AonStringUtils.equals(value, saved))
//				return;
//			
//			if(value.length() >= 24) {
//				reformatAccount(employee.account);
//				
//				if(Iban.validateIBAN(value)) {
//					employee.accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
//					employee.accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
//				}else {
//					employee.accountStatus.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
//					employee.accountStatus.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
//				}
//				
//				employeeDraftObject.setEmployeeAccount(value);
//				saving();
//			}
//		});
		
	}
		
	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------

	public void setEmployeeDraftObject(EmployeeDraftObject employeeDraftObject) {
		showEmployee();
		this.employeeDraftObject = employeeDraftObject;
		this.contractType = new ContractType();
		this.municipalities = new Municipalities();
		
		this.employeeDraftObject.initializeEmployee(
			r -> {
					initializeUndoRedo();
					initializeScheduler();
					initializeView();
			}, t -> {}
		);
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
		initIbans();
		initHandlers();
		
		if (employeeDraftObject.getContractSSRegimen() == 3) {
			afiButton.getElement().getStyle().setDisplay(Display.NONE);
			this.employee.showElementsFreelancerTable();
			fillContractFreelancerTable();
		} else {
			afiButton.getElement().getStyle().clearDisplay();
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

	private void resetElements() {
		this.employee.document.setValue("");
		this.employee.nationality.setValue("");
		this.employee.security_social_num.setValue("");
		
		this.employee.activityCCC.clear();
		this.employee.workplace.clear();
		this.employee.contractType.clear();
		this.employee.modality.clear();
		
		this.employee.start_date.setValue(null);
		this.employee.end_date.setValue(null);
		this.employee.seniority_date.setValue(null);
		
		this.employee.agreement.clear();
		this.employee.level.clear();
		this.employee.category.setValue("");
		
		this.employee.birth_date.setValue(null);
		
		this.employee.address.setValue("");
		this.employee.addressNum.setValue("");
		this.employee.addressInfo.setValue("");
		this.employee.addressZip.setValue("");
		this.employee.addressMunicipality.clear();
		
		this.employee.mobile.setValue("");
		this.employee.phone.setValue("");
		this.employee.email.setValue("");
		
		this.employee.account.setValue("");
		this.employee.bic.setValue("");
		
	}

	private void initActivitiesCCC() {
		//ACTIVITY - CCC
		this.employee.activityCCC.addItem("-");
		if(null != this.employeeDraftObject.getActivities())
			for(Entry<Integer,String> entry : this.employeeDraftObject.getActivities().entrySet())
				for(CCCInfo cccInfo :  this.employeeDraftObject.getCCCs().values())
					if(cccInfo.getActivityId() == entry.getKey()) 
						this.employee.activityCCC.addItem(entry.getValue() + " - " + getCCCType(cccInfo.getType()) + "[" + cccInfo.getCcc() + "] - " +  cccInfo.getGeozone(), cccInfo.getActivityId() + "/" + cccInfo.getCccId() + "/" + cccInfo.getType());
	}
	
	private void initWorkplaces() {
		for(Workplace workplace : employeeDraftObject.getWorkplaces())
			this.employee.workplace.addItem(workplace.getDescription(), workplace.getId().toString());
	}
	
	private void initContractType() {
		this.employee.contractType.addItem("-");
		for (Entry<Integer, ContractTypeRecord> entry : contractType.getContractTypes().entrySet())
			this.employee.contractType.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription(), StringUtils.leftPad(entry.getKey().toString(), 3, '0'));	
	}
	
	private void initAgreements() {
		this.employee.agreement.addItem("-", "-1");
		List<Agreement> agreements = employeeDraftObject.getActiveAgreements();
		for (Agreement agreement : agreements)
			this.employee.agreement.addItem(agreement.getDescription(), String.valueOf(agreement.getId()));
	}
	
	private void initIbans() {
		//DOCUMENT
		List<String> employeeIbans = employeeDraftObject.getExistingIban();
		List<String> employeesIbanSuggest = new ArrayList<String>();
		for(String iban : employeeIbans)
			employeesIbanSuggest.add(iban);
		MultiWordSuggestOracle orclIbans = (MultiWordSuggestOracle) this.employee.account.getSuggestOracle();
		orclIbans.addAll(employeesIbanSuggest);
		this.employee.document.setAutoSelectEnabled(true);
	}

	private void fillContractFreelancerTable() {
		//Contract
		this.employee.ssRegimeType.setSelectedIndex(1);
		
		setSelectedValueLB(employee.workplace, employeeDraftObject.getWorkplaceId().toString());
		
		this.employee.start_date.setValue(employeeDraftObject.getContractStartDate());
		Date seniorityDate = employeeDraftObject.getContractSeniorityDate();
		if(null == seniorityDate)
			seniorityDate = employeeDraftObject.getContractStartDate();
		this.employee.seniority_date.setValue(seniorityDate);
		this.employee.end_date.setValue(employeeDraftObject.getContractEndDate());
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.seniority_date);
		
		if(null != employeeDraftObject.getContractAgreement()) {
			setSelectedValueLB(employee.agreement, employeeDraftObject.getContractAgreement().toString());
			getAgreementLevelsAndSetLevel(employeeDraftObject.getContractAgreement(), employeeDraftObject.getContractAgreementLevelId(), employeeDraftObject.getContractAgreementCategory());
		} else {
			employee.agreement.setSelectedIndex(0);
			employee.level.clear();
			employee.level.addItem("-", "-1");
		}
		
		employee.category.setValue(employeeDraftObject.getContractAgreementCategory());
		
		this.employee.journeyType.setSelectedIndex(employeeDraftObject.getContractJourneyType());
		
		//Contract Partial Journey
		if(1 == employeeDraftObject.getContractJourneyType()) {
			this.employee.showElementsPartialTimeContract();
			ContractJourneyDuration contractJourneyDuration = employeeDraftObject.getContractJourneyDuration();
			if(contractJourneyDuration.getJourniesSize() != 0) {
				String result = contractJourneyDuration.getJourneyText();

				employee.journeyDuration.setText(result);
				employee.journeyDuration.setText(contractJourneyDuration.getJourneyText());
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
		//Contract
		this.employee.ssRegimeType.setSelectedIndex(employeeDraftObject.getContractSSRegimen());
		
		setSelectedValueLB(employee.activityCCC, employeeDraftObject.getActivityId()+"/"+employeeDraftObject.getCccId()+"/"+employeeDraftObject.getCccType());
		setSelectedValueLB(employee.workplace, employeeDraftObject.getWorkplaceId().toString());
		
		setSelectedValueLB(employee.contractType, employeeDraftObject.getContractType());
		
		Integer contractTypeId = employeeDraftObject.getContractTypeN();
		if((contractTypeId >= 200 && contractTypeId<300) || (contractTypeId >= 500 && contractTypeId<600 || contractTypeId == 0)) {
			employee.showElementsPartialTimeContract();
			ContractJourneyDuration contractJourneyDuration = employeeDraftObject.getContractJourneyDuration();
			if(contractJourneyDuration.getJourniesSize() != 0) {
				String result = contractJourneyDuration.getJourneyText();

				employee.journeyDuration.setText(result);
				employee.journeyDuration.setText(contractJourneyDuration.getJourneyText());
				employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
				employee.journeyDuration.removeStyleName("aon-icon-exception aon-finding-toolbar-item-no-border");
			}else {
				employee.journeyDuration.addStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
				employee.journeyDuration.addStyleName(employee.style.journeyDurationWarning());
				employee.journeyDuration.setText("ESPECIFICAR HORAS JORNADA EN EL CALENDARIO");
			}
		} else
			employee.showElementsFullTimeContract();
		
		employee.modality.clear();
		employee.modality.addItem("-");
		
		List<ModelRecord> contractTypeModels = this.contractType.getModelsContractType(contractTypeId);
		for (ModelRecord model : contractTypeModels)
			employee.modality.addItem(model.getModelDescription(), model.getEnumeration().toString());
		
		if(null != employeeDraftObject.getContractModel())
			setSelectedValueLB(employee.modality, employeeDraftObject.getContractModel().toString());
		
		this.employee.start_date.setValue(employeeDraftObject.getContractStartDate());
		Date seniorityDate = employeeDraftObject.getContractSeniorityDate();
		if(null == seniorityDate)
			seniorityDate = employeeDraftObject.getContractStartDate();
		this.employee.seniority_date.setValue(seniorityDate);
		this.employee.end_date.setValue(employeeDraftObject.getContractEndDate());
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.seniority_date);
		
		if(null != employeeDraftObject.getContractAgreement()) {
			setSelectedValueLB(employee.agreement, employeeDraftObject.getContractAgreement().toString());
			getAgreementLevelsAndSetLevel(employeeDraftObject.getContractAgreement(), employeeDraftObject.getContractAgreementLevelId(), employeeDraftObject.getContractAgreementCategory());
		}

		this.employee.category.setValue(employeeDraftObject.getContractAgreementCategory());
		
		this.employee.quote_group.setSelectedIndex(employeeDraftObject.getContractQuoteGroup());
		this.employee.occupation.setSelectedIndex(employeeDraftObject.getContractOcupation());
	}

	private void fillEmployeeTable() {
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
		
		this.employee.birth_date.setValue(employeeDraftObject.getEmployeeBirthDate());
		if(null != employeeDraftObject.getEmployeeBirthDate()) {
			Integer year = employeeDraftObject.getEmployeeBirthDate().getYear();
			Integer month = employeeDraftObject.getEmployeeBirthDate().getMonth();
			Integer currentYear = new Date().getYear();
			Integer currentMonth = new Date().getMonth();
			
			Integer age = currentYear - year;
			
			if(month > currentMonth)
				age--;
			
			this.employee.age.setText("( " + (age) + " a" + String.valueOf("\u00F1") + "os )");
		}else 
			this.employee.age.setText("");
		
		this.employee.gender.setSelectedIndex(employeeDraftObject.getEmployeeGender());
		
		setSelectedValueLB(employee.civilStatus, employeeDraftObject.getEmployeeCivilStatus().toString());
		
		setSelectedValueLB(employee.street_type, employeeDraftObject.getEmployeeAddressStreetType());
		
		this.employee.address.setValue(employeeDraftObject.getEmployeeAddress());
		this.employee.addressNum.setValue(employeeDraftObject.getEmployeeAddressNumber());
		this.employee.addressInfo.setValue(employeeDraftObject.getEmployeeAddressInfo());
		
		this.employee.addressZip.setValue(employeeDraftObject.getEmployeeAddressZip());
		
		setSelectedValueLB(employee.addressProvince, employeeDraftObject.getEmployeeAddressProvince());
		if(null != employeeDraftObject.getEmployeeAddressProvince()) {
			updateMunicipalities();
			employee.addressMunicipality.setSelectedIndex(getMunicipalityIndex(employeeDraftObject.getEmployeeAddressProvince(), employeeDraftObject.getEmployeeAddressCity()));
		}

		this.employee.mobile.setValue(employeeDraftObject.getEmployeeMobile());
		this.employee.phone.setValue(employeeDraftObject.getEmployeePhone());
		this.employee.email.setValue(employeeDraftObject.getEmployeeEmail());

		this.employee.payMethod.setSelectedIndex(employeeDraftObject.getEmployeePayMethod());
		
		if(null != employeeDraftObject.getEmployeeAccount()) {
			this.employee.bic.setValue(employeeDraftObject.getEmployeeBIC());
			this.employee.account.setValue(employeeDraftObject.getEmployeeAccount());
			reformatAccount(this.employee.account);
			
			if(employeeDraftObject.getEmployeeAccount().length() > 0) {
				if(Iban.validateIBAN(employeeDraftObject.getEmployeeAccount()))
					addSuccessStyle(employee.accountStatus);
				else
					addWarningStyle(employee.accountStatus);
			}
		}
	}
	
	// ----------------------------------------------- AUX PAGE METHODS ------------------------------------------------

	private void checkValidationSSNumber() {
		String ssNum = employee.security_social_num.getValue();
		if(employeeDraftObject.checkSSNumValidation(ssNum))
			addSuccessStyle(employee.ssNumberStatus);
		else
			addWarningStyle(employee.ssNumberStatus);
	}

	private void checkValidationDocument() {
		String document = employee.document.getValue();
		String document_type = checkDocumentType(document);
		employee.document_type.setText(document_type);
		
		if(employeeDraftObject.checkDocumentValidation(document_type, document))
			addSuccessStyle(employee.documentStatus);
		else
			addWarningStyle(employee.documentStatus);

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
	
	public Integer getContractTypeIdx(Integer contractTypeId) {
		return contractType.getContractTypeIndex(contractTypeId);
	}
	
	private void getAgreementLevelsAndSetLevel(Integer agreementId, Integer agreementLevelId, String category_description) {
		employee.level.clear();
		employee.level.addItem("-", "-1");
		
		employeeDraftObject.getAgreement(agreementId,  
		(agreement) -> {
			for (Level levelRecord : agreement.getLevels())
				for (String categoryRecord : agreement.getCategoriesMap().get(levelRecord.getId()))
					employee.level.addItem(levelRecord.getDescription() + " - " + categoryRecord, String.valueOf(levelRecord.getId()));

			employeeDraftObject.setContractAgreementId(agreement.getId());
			Integer agreementIdx = getAgreementLevelIdx(agreement, agreement.getLevels(), agreementId, agreementLevelId, category_description) + 1;
			employee.level.setSelectedIndex(agreementIdx);
		},
		(throwable) -> {
			employeeDraftObject.setContractAgreementId(null);
			employeeDraftObject.setContractAgreementLevelId(null);
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
		
		result = 0;
		
		if(null != agreementLevelId) {
			for (Level levelRecord : levels) {
				if(Integer.compare(agreementLevelId,levelRecord.getId()) != 0) {
					Set<String> categories = agreement.getCategoriesMap().get(levelRecord.getId());
					for (String categoryRecord : categories) {
						result++;
					}
				} else {
					return result;
				}
			}
		}
		return -1;
	}
	
	public void updateMunicipalities() {
		String provinceCode = employee.addressProvince.getSelectedValue();
		employee.addressMunicipality.clear();
		employee.addressMunicipality.addItem("-");;
		ArrayList<String> municipalitiesOfProvince = municipalities.getMunicipalitiesByProvinceCode(provinceCode);
		municipalitiesOfProvince.forEach(m -> {employee.addressMunicipality.addItem(m);});
	}
	
	private int getMunicipalityIndex(String province, String city) {
		return municipalities.getMunicipalityIndex(province, city) + 1;
	}
	
	// ----------------------------------------------- AUX METHODS ------------------------------------------------
	
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
	
	private void addWarningDateStyle(Widget widget) {
		widget.getElement().getStyle().clearDisplay();
		widget.setTitle("La fecha de inicio no coincide con la de antig" + String.valueOf("\u00FC") + "edad.");
		
		widget.setStyleName("aon-finding-toolbar-item aon-icon-info aon-finding-toolbar-item-no-border");
		widget.getElement().getStyle().setMarginTop(3.00, Unit.PX);
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
	
	private void reformatAccount(SuggestBox accountField) {
	    String accountText = accountField.getText();
	    accountText = accountText.replaceAll("\\W+", "");
	    if (accountText.length() >= 24) {
	    	accountField.setText(accountText.substring(0, 4) + "  " + accountText.substring(4, 8) + "  " + accountText.substring(8, 12) + "  " + accountText.substring(12, 16)
	    	+ "  " + accountText.substring(16, 20) + "  " + accountText.substring(20, 24));
	    }
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
	
	private void addSuccessStyle(Widget widget) {
		widget.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
		widget.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
	}
	
	private void addWarningStyle(Widget widget) {
		widget.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
		widget.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
	}
	
	// ----------------------------------------------- CALLBACK TO SAVE ------------------------------------------------
	
	public EmployeeDraft setOnSaved(Consumer<EmployeeContractInfo> onSaved) {
		this.onSaved = onSaved;
		return this;
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
	
	private void saving() {
		saveStatus.setText("Guardando...");
		saveTimer.schedule(2500);
	}
	
	private void cantSaveWithOutIt() {
		WarningDialog warning = new WarningDialog("AVISO", "No se puede dejar este campo sin valor.");
		warning.center();
		warning.show();
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
	
	private void saved() {
		saveStatus.setText("Todos los cambios guardados");	
		onSaved.accept(employeeDraftObject.getEmployeeContractInfo());
	}
	
	private void showIdc() {

		employeeDraftObject.downloadIdc(
		(dataURI) -> {
				showPdf();
				pdfViewer.setDocument(dataURI, zoom / 100.00);
		}, 
		(trowable)-> {
			
		}
		);
	}
	
	
	private void showPdf() {
		afiButton.setVisible(false);
		peculiaritiesButton.setVisible(false);
		bonificationsButton.setVisible(false);
		idcButton.setVisible(false);
		undoButton.setVisible(false);
		redoButton.setVisible(false);
		undoAllButton.setVisible(false);

		zoomListBox.setVisible(true);
		closePdfButton.setVisible(true);
		downloadButton.setVisible(true);

		showWidget(pdfViewer);
	}

	private void showEmployee() {
		afiButton.setVisible(true);
		peculiaritiesButton.setVisible(true);
		bonificationsButton.setVisible(true);
		idcButton.setVisible(true);
		undoButton.setVisible(true);
		redoButton.setVisible(true);
		undoAllButton.setVisible(true);
		try {
			idcButton.setVisible(Wnd.getCurrentDomainNameURL().toLowerCase().endsWith("aonsolutions.org"));
		}
		catch ( Exception e ) {
			
		}
		zoomListBox.setVisible(false);
		closePdfButton.setVisible(false);
		downloadButton.setVisible(false);
		
		showWidget(employee);
	}
	
	private void showWidget(Widget widget) {
		deckPanel.showWidget(deckPanel.getWidgetIndex(widget));
	}
	
	private void initZoomList() {

		for (int zoom = Constants.MIN_ZOOM; zoom < Constants.DEFAULT_ZOOM; zoom += Constants.ZOOM_STEP)
			zoomListBox.addItem(Constants.PERCENT_FORMAT.format((double) zoom / 100));
		int selectedIndex = zoomListBox.getItemCount();
		for (int zoom = Constants.DEFAULT_ZOOM; zoom < Constants.MAX_ZOOM; zoom += Constants.ZOOM_STEP)
			zoomListBox.addItem(Constants.PERCENT_FORMAT.format((double) zoom / 100));
		zoomListBox.addItem(Constants.PERCENT_FORMAT.format((double) Constants.MAX_ZOOM / 100));
		zoomListBox.setSelectedIndex(selectedIndex);
		
	}

	protected void onSavedNoop(EmployeeContractInfo employeeContractInfo) {}
	
}

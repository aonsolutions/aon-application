package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.CCCType;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.StreetType;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ContractTypeRecord;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ModelRecord;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
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
import com.google.gwt.user.client.ui.Widget;

public class EmployeeDialog extends CustomDialog {
	
	private class EmployeeImplementation extends Employee{
		ContractType contractTypeClass = new ContractType();
		
		@Override
		public void onEmployeeNameChange() {
			employeeDialogObject.setEmployeeName(this.name.getValue());	
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
		public void onContractCategoryChange() {
			employeeDialogObject.setContractCategory(this.category.getValue());
		}

		@Override
		public void onEmployeeBirthDateChange() {
			employeeDialogObject.setEmployeeBirthDate(this.birth_date.getValue());
		}

		@Override
		public void onEmployeeGenderChange() {
			employeeDialogObject.setEmployeeGender(this.gender.getSelectedIndex());
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
		public void onEmployeePhoneChange() {
			employeeDialogObject.setEmployeePhone(this.phone.getValue());
		}

		@Override
		public void onEmployeeMobileChange() {
			employeeDialogObject.setEmployeeMobile(this.mobile.getValue());
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

		@Override
		public void onEmployeeDocumentChange() {
			String document_string = this.document.getValue();
			String document_type_string = checkDocumentType(document_string);
			
			this.document_type.setText(document_type_string);
			if(employeeDialogObject.checkDocumentValidation(document_type_string, document_string)) {
				this.document.removeStyleName(this.style.warning());
				employeeDialogObject.setEmployeeDocument(this.document.getValue());
				employeeDialogObject.setEmployeeDocumentType(document_type_string);
			}else
				this.document.addStyleName(this.style.warning());

			showNationality(document_type_string);				
		}

		@Override
		public void onEmployeeSSNumChange() {
			String ssNum_string = this.security_social_num.getValue();
			if(employeeDialogObject.checkSSNumValidation(ssNum_string)) {
				employeeDialogObject.setEmployeeSocialSecurityNum(ssNum_string);
				security_social_num.removeStyleName(style.warning());
			}else
				security_social_num.addStyleName(style.warning());
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

		@Override
		public void onEmployeeStreetTypeChange() {
			Integer streetTypeIdx = this.street_type.getSelectedIndex();
			String shortCode = StreetType.values()[streetTypeIdx].getShortCode();
			employeeDialogObject.setEmployeeStreetType(shortCode);
		}

		@Override
		public void onContractModalityChange() {
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
//				Window.alert("ActivityStr : " + activityStr + ", cccStr : " + cccStr + ", cccType : " + cccType + ", cccGeozone : " + cccGeozoneStr);
				
				Integer activityId = employeeDialogObject.getActivityIdByName(activityStr);
				Integer cccId = employeeDialogObject.getCCCIdByNumber(cccStr, cccType, cccGeozoneStr);
				employeeDialogObject.setContractActivityId(activityId);
				employeeDialogObject.setContractCCCId(cccId);
				employeeDialogObject.setContractCCCType(cccType);
				
//				Window.alert("ActivityId : " + activityId + ", cccId : " + cccId + ", cccType : " + cccType);
			}
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
		public void onEmployeeNationalityChange() {
			String countryIso2 = getIso2(nationality.getValue());
			employeeDialogObject.setNationality(countryIso2);
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
		
		private String getIso2(String country) {
			for (int i = 0; i < Country.values().length; i++) {
				if (Country.values()[i].getName() == country)
					return Country.values()[i].getIso2();
			}
			return null;
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
		initDisplayNoneElements();
		initWorkplaces();
		initActivitiesCCC();
		initContractType();
		initAgreements();
		initFocus();
	}

	private void initAgreements() {
		// CONVENIO
		this.employee.agreement.addItem("-");
		List<Agreement> agreements = employeeDialogObject.getActiveAgreements();
		for (Agreement a : agreements)
			this.employee.agreement.addItem(a.getDescription());
	}

	private void initDisplayNoneElements() {
		//DISPLAY NONE RETA
		this.employee.contractFreelancerNode.getStyle().setDisplay(Display.NONE);
		//DISPLAY NONE TIEMPO COMPLETO/PARCIAL
		this.employee.contractDataTable.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
	}
	
	private void initWorkplaces() {
		//WORKPLACE
		for(Workplace workplace : employeeDialogObject.getWorkplaces())
			this.employee.workplace.addItem(workplace.getDescription());
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

	private void initContractType() {
		// TIPO DE CONTRATO
		this.employee.contractType.addItem("-");
		for (Entry<Integer, ContractTypeRecord> entry : contractType.getContractTypes().entrySet())
			this.employee.contractType.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription());	
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

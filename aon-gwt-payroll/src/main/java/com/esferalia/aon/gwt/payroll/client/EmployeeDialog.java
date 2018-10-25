package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.shared.Dni;
import com.esferalia.aon.gwt.common.shared.SocialSecurity;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.CCCType;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ContractTypeRecord;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ModelRecord;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeDialog extends CustomDialog {
	
//	private class EmployeeImplementation extends Employee{
//		@Override
//		public void onEmployeeNameChange() {
//			Window.alert("Nombre : " + this.name.getValue());
//			employeeDialogObject.setEmployeeName(this.name.getValue());	
//		}
//
//		@Override
//		public void onEmployeeFirstSurnameChange() {
//			employeeDialogObject.setEmployeeFirstSurname(this.first_surname.getValue());
//		}
//
//		@Override
//		public void onEmployeeSecondSurnameChange() {
//			employeeDialogObject.setEmployeeSecondSurname(this.second_surname.getValue());
//		}
//		
//	}
	
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
//		employee = new EmployeeImplementation();
		
		employee = new Employee() {
			
			@Override
			public void onEmployeeNameChange() {
				Window.alert("Nombre : " + this.name.getValue());
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
				String document_type_string = this.checkDocumentType(document_string);
				
				this.document_type.setText(document_type_string);
				if(employeeDialogObject.checkDocumentValidation(document_type_string, document_string)) {
					this.document.removeStyleName(this.style.warning());
					employeeDialogObject.setEmployeeDocument(this.document.getValue());
					employeeDialogObject.setEmployeeDocumentType(document_type_string);
				}else
					this.document.addStyleName(this.style.warning());

				this.showNationality(document_type_string);				
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
				ContractType contractType = new ContractType();
				modality.clear();
				modality.addItem("-");
				Integer contractTypeId = -1;
				if (this.contractType.getSelectedIndex() != 0) {
					String contract_type_id_str = this.contractType.getSelectedItemText().split(" -")[0];
					contractTypeId = Integer.parseInt(contract_type_id_str);
				}

				Window.alert("Tipo contrato : " + contractTypeId);
				
				List<ModelRecord> contractTypeModels = contractType.getModelsContractType(contractTypeId);
				
				Window.alert("Contract Type VAR : " + contractType);
				Window.alert("Contract Model Size : " + contractTypeModels.size());
				
				for (ModelRecord m : contractTypeModels) {
					modality.addItem(m.getModelDescription());
				}
				
				
				if (this.contractType.getSelectedIndex() == 0) {
					employeeDialogObject.setContractType(null);
					employeeDialogObject.setContractModel(null);
				} else {
					String contract_type_id_str = this.contractType.getSelectedItemText().split(" -")[0];
					employeeDialogObject.setContractType(contract_type_id_str);
				}
				
			}
		};
		
		
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
		
		this.employeeDialogObject.getWorkplaceEmployees(r -> {
			//DISPLAY NONE RETA
			this.employee.contractFreelancerNode.getStyle().setDisplay(Display.NONE);
			
			//WORKPLACE
			for(Workplace workplace : employeeDialogObject.getWorkplaces())
				this.employee.workplace.addItem(workplace.getDescription());
			
			//FOCUS DOCUMENT
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand () {
		        public void execute () {
		        	employee.document.setFocus(true);
		        }
			});
		
			//ACTIVITY - CCC
			this.employee.activityCCC.addItem("-");
			if(null != this.employeeDialogObject.getActivities())
				for(Entry<Integer,String> entry : this.employeeDialogObject.getActivities().entrySet())
					for(CCCInfo cccInfo :  this.employeeDialogObject.getCCCs().values())
						if(cccInfo.getActivityId() == entry.getKey()) 
							this.employee.activityCCC.addItem(entry.getValue() + " - " + CCCType.values()[cccInfo.getType()] + "[" + cccInfo.getCcc() + "] - " +  cccInfo.getGeozone());

			// TIPO DE CONTRATO
			this.employee.contractType.addItem("-");
			for (Entry<Integer, ContractTypeRecord> entry : contractType.getContractTypes().entrySet()) {
				this.employee.contractType.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription());
			}
			
			// MODALIDAD
			this.employee.modality.addItem("-");
		
		}, t -> {});
		
	}
	
	
	// ------------------------------------------------------------------------
	//
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

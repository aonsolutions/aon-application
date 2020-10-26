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
import com.esferalia.aon.gwt.payroll.shared.Messages;
import com.esferalia.aon.gwt.payroll.shared.Messages.Message;
import com.esferalia.aon.gwt.payroll.shared.Municipalities;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.Viewer;

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

	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	private static ContrataEmployeeDraftUiBinder uiBinder = GWT.create(ContrataEmployeeDraftUiBinder.class);

	interface ContrataEmployeeDraftUiBinder extends UiBinder<Widget, ContrataEmployee> {}
	
	@UiField
	SplitLayoutPanel splitLayoutPanel;
	
	@UiField
	Label title;
	
	@UiField (provided = true)
	Employee employee;
	
	@UiField (provided = true)
	ContractSpecificData contractSpecificData;
	
	@UiField (provided = true)
	ContractOtherData contractOtherData;
	
	@UiField (provided = true)
	ContractClauseUI contractClauseUI;
	
	@UiField (provided = true)
	ContractAttachUI contractAttachUI;
	
	@UiField
	Button saveContract;
	
	@UiField
	Button deleteContract;
	
	@UiField
	TabLayoutPanel tabLayOutPanel;
	
	@UiField
	ScrollPanel scrolledPanel;
	
	@UiField
	ScrollPanel scrolledPanelContractOtherData;
	
	@UiField
	ScrollPanel scrolledPanelContractSpecificData;
	
	@UiField
	Button listEmployees;
	
	@UiField
	ScrollPanel scrolledPanelClauses;
	
	@UiField
	ScrollPanel scrolledPanelAttach;
	
	@UiField
	MinimizePanel footPanel;
	
	@UiField
	TabLayoutPanel footTabPanel;

	@UiField
	MenuItem taButton;
	
	@UiField
	MenuItem idcButton;

	@UiField
	Button closePdfButton;
	
	@UiField
	Viewer pdfViewer;
	
	@UiField
	ListBox zoomListBox;
	
	@UiField
	Button downloadButton;
	// -------------------------------------------- Variables de la clase---------------------------------------------
	private int zoom;
	private ContrataEmployeeObject contrataEmployeeObject;
	private ContractType contractType;
	private Municipalities municipalities;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------
	
	public ContrataEmployee() {
		this.zoom = Constants.DEFAULT_ZOOM;
		employee = new EmployeeImplementation();
		contractSpecificData = new ContractSpecificData();
		contractOtherData = new ContractOtherData();
		contractClauseUI = new ContractClauseUI();
		contractAttachUI = new ContractAttachUI() {
			@Override
			protected void fireMessagesResults(Messages messages) {
				paintMessagesResult(messages);
			}
		};
		
		initWidget(uiBinder.createAndBindUi(this));
		
		employee.clear_employee.getElement().getStyle().setDisplay(Display.NONE);
		employee.account.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				reformatAccount(employee.account);
			}
		});
		
		showEmployee();		
		initZoomList();
		
		int height = Window.getClientHeight(); 
		scrolledPanel.setHeight((height-260)+"px");
		scrolledPanelContractOtherData.setHeight((height-260)+"px");
		scrolledPanelClauses.setHeight((height-265)+"px");
		scrolledPanelAttach.setHeight((height-265)+"px");
		scrolledPanelContractSpecificData.setHeight((height-260)+"px");
		
		tabLayOutPanel.selectTab(0, false);
		tabLayOutPanel.setAnimationDuration(1000);
		
		tabLayOutPanel.addBeforeSelectionHandler(e -> {
			Integer itemIdx = tabLayOutPanel.getSelectedIndex();
			switch (itemIdx) {
			case 1:
				contrataEmployeeObject.setContractSpecificData(s -> {}, f -> {});
				break;
			case 2:
				contrataEmployeeObject.setContractOtherInfo(s -> {}, f -> {});
				break;
			case 3:
				contrataEmployeeObject.setContractClauses(s -> {}, f -> {});
				break;
			case 4:
				contrataEmployeeObject.setContractAttachments(s -> {}, f -> {});
				break;
			default:
				break;
			}
		});
		
		tabLayOutPanel.addSelectionHandler(e -> {
			Integer itemIdx = tabLayOutPanel.getSelectedIndex();
			switch (itemIdx) {
			case 1:
				contrataEmployeeObject.getContractSpecificData(s -> {
					contractSpecificData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
				}, f -> {});
				break;
			case 2:
				contrataEmployeeObject.getContractOtherInfo(s -> {
					contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
				}, f -> {});
				break;
			case 3:
				contrataEmployeeObject.getContractClauses(s -> {
					contractClauseUI.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
				}, f -> {});
				break;
			case 4:
				contrataEmployeeObject.getContractAttachments(s -> {
					contractAttachUI.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
				}, f -> {});
				break;
			default:
				break;
			}
		});
		
		footPanel.addMaximizeHandler((e) -> {
			splitLayoutPanel.setWidgetSize(footPanel, 150);
		});
		
		footPanel.addMinimizeHandler((e) -> {
			splitLayoutPanel.setWidgetSize(footPanel, 25);
		});
		
		taButton.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				showTa();
			}
		});
		
		idcButton.setScheduledCommand(new Command() {
			@Override
			public void execute() {
				showIdc();
			}
		});
	}
	
	// -------------------------------------------------- UiHandlers --------------------------------------------------
	
	@UiHandler("listEmployees")
	void onListButtonClick(ClickEvent clickEvent) {
		onListShow(true);
	}
	
	@UiHandler("deleteContract")
	void onDeleteContractButtonClick(ClickEvent clickEvent) {
		AcceptCancelDialog dialog = new AcceptCancelDialog("AVISO", String.valueOf("\u00BF") + "Realmente desea eliminar este contrato?") {
			@Override
			protected void onAccept() {
				contrataEmployeeObject.deleteContract(s -> {
					onListShow(true);
				}, f-> {});
			}
		};
		
		dialog.center();
		dialog.show();
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
//								onListShow(true);
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
	
	@UiHandler("closePdfButton")
	void onClosePdfButtonClick(ClickEvent event) {
		showEmployee();
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
		String fileName = contrataEmployeeObject.getEmployeeFullName() + " IDC.pdf";
		pdfViewer.download(fileName);
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
		
	    tabLayOutPanel.selectTab(0, false);
		employee.restartEmployee();
		
		footTabPanel.clear();
		splitLayoutPanel.setWidgetSize(footPanel, 25);
		
		title.setText(employeeContractInfo.getEmployeeInfo().getFullName());
		
		this.contrataEmployeeObject.getAgreements(
				r -> {
					contrataEmployeeObject.setEmployeeContractInfo(employeeContractInfo);
					initLogicWindow();
					initExistingEmployee(employeeContractInfo.getContractInfo().hasPayroll());
//					contractSpecificData.setEmployeeContractInfo(employeeContractInfo);
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
			this.employee.contractType.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription(), StringUtils.leftPad(entry.getKey().toString(), 3, '0'));	
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
		String document_type = checkDocumentType(employeeData.getDocument());
		employee.document_type.setText(document_type);
		
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
	
	private void fillContractOtherData(Integer contractType) {
		if(-1 == contractType)
			tabLayOutPanel.getTabWidget(1).setVisible(false);
		else
			tabLayOutPanel.getTabWidget(1).setVisible(true);
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
	
	// ----------------------------------------------- MESSAGES RESULT ------------------------------------------------
	
	private void paintMessagesResult(Messages messages) {
		Tree treeErrorMessages = new Tree();
		treeErrorMessages.setAnimationEnabled(true);
		
		//Errors
		for(Message errorMessage : messages.getErrorMessages()) {
			Label errorLabel =  new Label();
			errorLabel.setText(errorMessage.getDescription() + " -> " + errorMessage.getMessage());
			treeErrorMessages.add(errorLabel);
		}
		
		footTabPanel.add(treeErrorMessages, "Errores");
		splitLayoutPanel.setWidgetSize(footPanel, 200);
	}
	
	// ------------------------------------------------ PDF -------------------------------------------------------------
	private void showTa() {

		contrataEmployeeObject.downloadTa(
		(dataURI) -> {
				showPdf();
				pdfViewer.setDocument(dataURI, zoom / 100.00);
		}, 
		(trowable)-> {
			
		}
		);
	}

	private void showIdc() {

		contrataEmployeeObject.downloadIdc(
		(dataURI) -> {
				showPdf();
				pdfViewer.setDocument(dataURI, zoom / 100.00);
		}, 
		(trowable)-> {
			
		}
		);
	}
	
	
	private void showPdf() {
		taButton.setVisible(false);
		idcButton.setVisible(false);
		saveContract.setVisible(false);
		deleteContract.setVisible(false);
		listEmployees.setVisible(false);

		zoomListBox.setVisible(true);
		closePdfButton.setVisible(true);
		downloadButton.setVisible(true);

		tabLayOutPanel.getElement().getStyle().setDisplay(Display.NONE);
		pdfViewer.getElement().getStyle().clearDisplay();
	}

	private void showEmployee() {
		saveContract.setVisible(true);
		deleteContract.setVisible(true);
		listEmployees.setVisible(true);
		taButton.setVisible(true);
		idcButton.setVisible(true);
		zoomListBox.setVisible(false);
		closePdfButton.setVisible(false);
		downloadButton.setVisible(false);
		
		pdfViewer.getElement().getStyle().setDisplay(Display.NONE);
		tabLayOutPanel.getElement().getStyle().clearDisplay();
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
	
}

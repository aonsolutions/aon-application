package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.ifSistemaREDEnabled;
import static com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.ifSistemaREDError;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractJourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.ContractSalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.Messages;
import com.esferalia.aon.gwt.payroll.shared.Messages.Message;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.Viewer;

public abstract class ContrataEmployee extends ResizeComposite {
	
	private class EmployeeImplementation extends Employee{
		
		// TABLA DATOS CONTRATO
		
		@Override
		public void onClearEmployeeClick() {}
		
		@Override
		public void onEmployeeDocumentSuggestionChange(String document) {}
		
		@Override
		public void onEmployeeDocumentChange(String document, String document_type) {
			contrataEmployeeObject.setEmployeeDocument(document);
			contrataEmployeeObject.setEmployeeDocumentType(document_type);
		}
		
		@Override
		public void onEmployeeNationalityChange(String countryIso2) {
			contrataEmployeeObject.setNationality(countryIso2);
		}
		
		@Override
		public void onEmployeeSSNumSuggestionChange(String ssNumber) {}

		@Override
		public void onEmployeeSSNumChange(String ssNumber) {
			contrataEmployeeObject.setEmployeeSocialSecurityNum(ssNumber);
		}
		
		@Override
		public void onEmployeeNameSuggestionChange(String nameSurname) {}
		
		@Override
		public void onEmployeeNameChange(String name) {
			contrataEmployeeObject.setEmployeeName(name);
		}

		@Override
		public void onEmployeeFirstSurnameSuggestionChange(String nameSurname) {}
		
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
		public void onContractOccupationChange(String occupation) {
			contrataEmployeeObject.setContractOccupation(occupation);
		}

		@Override
		public void onContractJourneyTypeChange(Boolean journey_type) {
			contrataEmployeeObject.setContractJourneyType(journey_type);
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
					employee.partiality_coef.setValue(partialityCoef);
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
		public void onEmployeeAddressProvinceChange(String addressProvinceCode) {
			contrataEmployeeObject.setEmployeeAddressProvince(addressProvinceCode);
			contrataEmployeeObject.setEmployeeAddressCity("-1");
		}
		
		@Override
		public void onEmployeeAddressMunicipalityChange(String addressMunicipality) {
			contrataEmployeeObject.setEmployeeAddressCity(addressMunicipality);
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
	}	

	// ------------------------------------------------- UiBinder
	
	private static ContrataEmployeeDraftUiBinder uiBinder = GWT.create(ContrataEmployeeDraftUiBinder.class);

	interface ContrataEmployeeDraftUiBinder extends UiBinder<Widget, ContrataEmployee> {}
	
	// ------------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String flex();
	}
	
	
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
	
	@UiField (provided = true)
	ContractBonusUI contractBonusUI;
	
	@UiField (provided = true)
	EmployeeSalary employeeSalary;
	
	@UiField (provided = true)
	EmployeeCalendarDraftNew employeeCalendar;
	
	@UiField (provided = true)
	EmployeeEventsDraft employeeEvents;
	
	@UiField (provided = true)
	SalaryDraft salaryDraft;
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	SplitLayoutPanel splitLayoutPanel;
	
	@UiField
	TabLayoutPanel tabLayOutPanel;
	
	@UiField
	ScrollPanel scrolledPanel;
	
	@UiField
	ScrollPanel scrolledPanelContractSpecificData;
	
	@UiField
	ScrollPanel scrolledPanelContractOtherData;
	
	@UiField
	ScrollPanel scrolledPanelClauses;
	
	@UiField
	ScrollPanel scrolledPanelAttach;
	
	@UiField
	ScrollPanel scrolledPanelBonus;
	
	@UiField
	ScrollPanel scrolledPDFPanel;
	
	@UiField
	Viewer pdfViewer;
	
	@UiField
	MinimizePanel footPanel;
	
	@UiField
	TabLayoutPanel footTabPanel;
	
	ResultsPanel resultsPanel;
	
	// ------------------------------------------------- Class variables
	
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private ContrataEmployeeObject contrataEmployeeObject;
	
	private AonToolbar toolbar;

	private AonToolbarButton listEmployees;
	private HTMLPanel employeeContractButtons;
	private AonToolbarButton saveContract;
	private AonToolbarButton deleteContract;
	private AonToolbarButton exportContract;
	private AonToolbarButton ta;
	private AonToolbarButton idc;
	private AonToolbarButton cbc;
	private AonToolbarButton cto;
	private AonToolbarButton afi;
	private AonToolbarButton closePDF;
	private AonToolbarButton downloadPDF;
	private ListBox zoomListBox;
	private int zoom;
	
	// EmployeeSalary
	private HTMLPanel employeeSalaryButtons;
	private AonToolbarButton deleteButton;
	private AonToolbarButton pdfButton;
	private AonToolbarButton pdfSettleButton;
	private AonToolbarButton publishButton;
	private AonToolbarButton bidoqPublishButton;
	private AonToolbarButton email;
	
	// EmployeeCalendar
	private HTMLPanel employeeCalendarButtons;
	private AonToolbarButton undoAllButton;
	private AonToolbarButton saveButton;
	private AonToolbarButton definitionButton;
	private AonToolbarButton utilityButton;
	private ListBox yearLB;
	
	// EmployeeEvents
	private HTMLPanel employeeEventsButtons;
	private AonToolbarButton undoAllEventsButton;
	private AonToolbarButton saveEventsButton;
	private AonToolbarButton newValueButton;
	private AonToolbarButton visibilityButton;
	private ListBox yearLBEvents;
	
	// SalaryDraft
	private HTMLPanel salaryDraftButtos;
	private AonToolbarButton acceptButton;
	private AonToolbarButton salaryButton;
	private AonToolbarButton settleButton;
	private AonToolbarButton printPreviewButton;
	private AonToolbarButton irpfPreviewButton;
	private SalarySelect salarySelect;
	private ListBox zoomSalaryListBox;
	private AonToolbarButton saveSalaryButton;
	private AonToolbarButton closePreviewButton;
	private AonToolbarButton fxButton;
	private AonToolbarButton undoSalaryAllButton;
	private AonToolbarButton undoButton;
	private AonToolbarButton redoButton;
	private CheckBox tgssCheck; 
	private CheckBox costsCheck;
	private CheckBox dbSalaryCheck;
	private CheckBox eventsCheck;
	private ListBox settlePreviewListBox; 
	
	private boolean hasCertificateSEPE;
	
	// ------------------------------------------------- Constructor
	
	public ContrataEmployee() {
		this.zoom = Constants.DEFAULT_ZOOM;
		this.hasCertificateSEPE = false;
		
		employee = new EmployeeImplementation();
		contractSpecificData = new ContractSpecificData();
		contractOtherData = new ContractOtherData();
		contractClauseUI = new ContractClauseUI();
		contractAttachUI = new ContractAttachUI() {
			@Override
			protected void fireMessagesResults(Messages messages) {
				paintMessagesResult(messages);
			}

			@Override
			protected void onExportPDF() {
				contrataEmployeeObject.getContractOtherInfo(s -> {
					if(AonStringUtils.isBlank(contrataEmployeeObject.getFormativeLevel()))
						contrataEmployeeObject.getContractSpecificData(su -> {
							contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
							contrataEmployeeObject.setContractOtherData(contractOtherData.getContractOtherData());
							contrataEmployeeObject.saveContractExport(
									a -> {
										contractAttachUI.setContractAttachments(a);
										this.refreshPage();
									},
									e -> {}
							);
						}, f -> {});
					else {
						contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
						contrataEmployeeObject.setContractOtherData(contractOtherData.getContractOtherData());
						contrataEmployeeObject.saveContractExport(
								a -> {
									contractAttachUI.setContractAttachments(a);
									this.refreshPage();
								},
								e -> {}
						);
					}
				}, f -> {});
			}
		};
		contractBonusUI = new ContractBonusUI() {
			@Override
			protected void fireMessagesResults(Messages messages) {
				paintMessagesResult(messages);
			}
		};
		
		employeeSalary = new EmployeeSalary();
		employeeSalary.hideToolbar();
		
		employeeCalendar = new EmployeeCalendarDraftNew();
		employeeCalendar.hideToolbar();
		employeeCalendar.setContrataEmployeeCalendarHeight();
		
		employeeEvents = new EmployeeEventsDraft();
		employeeEvents.hideToolbar();
		
		salaryDraft = new SalaryDraft();
		salaryDraft.hideToolbar();
		
		initWidget(uiBinder.createAndBindUi(this));
		
		toolbar = getToolbarPanel();
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		
		showContractButtons();
		setDefaultEmployeeView();
		showEmployee();		
		initZoomList();
		setScrollPanelsHeight();
		initTabLayOutPanel();
		initFootPanel();
		initResultsPanel();
	}
	
	protected void getContractBonus(Consumer<List<SSBonusData>> success, Consumer<Throwable> failure) {
		contrataEmployeeObject.getSSBonus( success, failure);
	}
	
	// ------------------------------------------------- Initialize preview

	private void setDefaultEmployeeView() {
		employee.hideClearEmployee();
	}
	
	private void showEmployee() {
		saveContract.setVisible(true);
		deleteContract.setVisible(true);
		listEmployees.setVisible(true);
		ta.setVisible(true);
		idc.setVisible(true);
		afi.setVisible(true);
		cbc.setVisible(true);
		cto.setVisible(true);
		
		zoomListBox.setVisible(false);
		closePDF.setVisible(false);
		downloadPDF.setVisible(false);
		
		pdfViewer.getElement().getStyle().setDisplay(Display.NONE);
		tabLayOutPanel.getElement().getStyle().clearDisplay();
	}
	
	private void initZoomList() {
		zoomListBox = new ListBox();
		for (int zoom = Constants.MIN_ZOOM; zoom < Constants.DEFAULT_ZOOM; zoom += Constants.ZOOM_STEP)
			zoomListBox.addItem(Constants.PERCENT_FORMAT.format((double) zoom / 100));
		int selectedIndex = zoomListBox.getItemCount();
		for (int zoom = Constants.DEFAULT_ZOOM; zoom < Constants.MAX_ZOOM; zoom += Constants.ZOOM_STEP)
			zoomListBox.addItem(Constants.PERCENT_FORMAT.format((double) zoom / 100));
		zoomListBox.addItem(Constants.PERCENT_FORMAT.format((double) Constants.MAX_ZOOM / 100));
		zoomListBox.setSelectedIndex(selectedIndex);	
	}
	
	private void setScrollPanelsHeight() {
		int height = Window.getClientHeight(); 
		scrolledPanel.setHeight((height-220)+"px");
		scrolledPanelContractOtherData.setHeight((height-220)+"px");
		scrolledPanelClauses.setHeight((height-220)+"px");
		scrolledPanelAttach.setHeight((height-220)+"px");
		scrolledPanelBonus.setHeight((height-220)+"px");
		scrolledPanelContractSpecificData.setHeight((height-220)+"px");
		scrolledPDFPanel.setHeight((height-220)+"px");
	}
	
	private void initTabLayOutPanel() {
		tabLayOutPanel.selectTab(0, false);
		tabLayOutPanel.setAnimationDuration(1000);
		
		tabLayOutPanel.addBeforeSelectionHandler(e -> {
			Integer itemIdx = tabLayOutPanel.getSelectedIndex();
			switch (itemIdx) {
			case 0:
				contrataEmployeeObject.setEmployeeContract(s -> {}, f -> {});
				break;
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
			case 0:
				contrataEmployeeObject.getEmployeeContract(s -> {
					showContractButtons();
					this.setContrataEmployeeObject(this.contrataEmployeeObject, s);
				}, f -> {});
				break;
			case 1:
				contrataEmployeeObject.getContractSpecificData(s -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showContractButtons();
					contractSpecificData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
				}, f -> {});
				break;
			case 2:
				contrataEmployeeObject.getContractOtherInfo(s -> {
					if(AonStringUtils.isBlank(contrataEmployeeObject.getFormativeLevel()))
						contrataEmployeeObject.getContractSpecificData(su -> {
							exportContract.getElement().getStyle().clearDisplay();
							showContractButtons();
							contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
						}, f -> {});
					else {
						exportContract.getElement().getStyle().clearDisplay();
						showContractButtons();
						contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
					}
				}, f -> {});
				break;
			case 3:
				contrataEmployeeObject.getContractClauses(s -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showContractButtons();
					contractClauseUI.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
				}, f -> {});
				break;
			case 4:
				contrataEmployeeObject.getContractAttachments(s -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showContractButtons();
					contractAttachUI.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
				}, f -> {});
				break;
			case 5:
				getContractBonus(s -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showContractButtons();
					contractBonusUI.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
				}, f -> {});
				break;
			case 6:
				contrataEmployeeObject.getEmployeeSalaryObject(employeeSalaryObject -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showSalariesButtons();
					employeeSalary.setEmployeeSalaryObject(employeeSalaryObject);
				}, f -> {});
				break;
			case 7:
				contrataEmployeeObject.getEmployeeCalendarObject(employeeCalendarObject -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showCalendarButtons();
					employeeCalendar.setEmployeeCalendarDraftObject(employeeCalendarObject);
				}, f -> {});
				break;
			case 8:
				contrataEmployeeObject.getEmployeeEventsObject(employeeEventsObject -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showEventsButtons();
					employeeEvents.setEmployeeEventsDraftObject(employeeEventsObject);
				}, f -> {});
				break;
			case 9:
				contrataEmployeeObject.getSalaryDraftObject(salaryDraftObject -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showSalaryDraftButtons();
					salaryDraft.setSalaryDraftObject(salaryDraftObject);
				}, f -> {});
				break;
			default:
				break;
			}
		});
	}

	private void initFootPanel() {
		footPanel.addMaximizeHandler((e) -> {
			showFootPanel();
		});
		
		footPanel.addMinimizeHandler((e) -> {
			hideFootPanel();
		});	
	}

	private void initResultsPanel () {
		resultsPanel = new ResultsPanel();		
	}

	// ------------------------------------------------- Abstract methods
	
	protected abstract void onListShow(boolean reloadEmployees);
	
	// ------------------------------------------------- setContrataEmployeeObject
	
	public void setContrataEmployeeObject(ContrataEmployeeObject contrataEmployeeObject, EmployeeContractInfo employeeContractInfo) {
		this.contrataEmployeeObject = contrataEmployeeObject;
		this.contrataEmployeeObject.setEmployeeContractInfo(employeeContractInfo);		
		
		new Animation() {

	        @Override
	        protected void onUpdate( double progress ) {
	        	scrolledPanel.getElement().getStyle().setOpacity( progress );
	        }

	        @Override
	        protected void onComplete() {
	        	scrolledPanel.getElement().getStyle().setOpacity( 1.0 );
	        }
	    }.run( 1000 );
		
	    tabLayOutPanel.selectTab(0, false);
		employee.initializeView();
		employee.cleanWarningIcons();
		
		footTabPanel.clear();
		splitLayoutPanel.setWidgetSize(footPanel, 25);
		
		toolbar.setTitle(employeeContractInfo.getEmployeeInfo().getFullName());		
		exportContract.getElement().getStyle().setDisplay(Display.NONE);
		
		this.contrataEmployeeObject.getAgreements(
				r -> {
					employee.initializeView();
					initLogicWindow();
					initExistingEmployee(employeeContractInfo.getContractInfo().hasPayroll());
				}, 
				t -> {}
		);
		
		checkStatus(this.contrataEmployeeObject);
		checkCertificateSEPE();
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
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand () {
	        public void execute () {
	        	employee.document.setFocus(true);
	        }
		});
	}

	// ------------------------------------------------- Initialize existing employee
	
	public void initExistingEmployee( boolean hasPayroll){
		fillExistingEmployee();
		fillExistingContract();
		if(hasPayroll)
		   employee.blockVariablesExistingContract();
		else
		   employee.unblockVariablesExistingContract();
	}
	
	private void fillExistingEmployee() {
		EmployeeInfo employeeData = contrataEmployeeObject.getEmployeeData();
		
		employee.document.setValue(employeeData.getDocument(), true);
		employee.nationality.setValue(employeeData.getNationality());
		employee.security_social_num.setValue(employeeData.getSsNumber(), true);
		
		employee.name.setValue(employeeData.getName());
		employee.first_surname.setValue(employeeData.getSurName());
		employee.second_surname.setValue(employeeData.getSecondSurName());
		
		employee.birth_date.setValue(employeeData.getBirthdate(), true);
		setSelectedValueLB(employee.gender, String.valueOf(employeeData.getGender()));
		setSelectedValueLB(employee.civilStatus, employeeData.getCivilStatus()+"");
		
		setSelectedValueLB(employee.street_type, employeeData.getStreetType());
		employee.address.setValue(employeeData.getAddress());
		employee.addressNum.setValue(employeeData.getAddresNum());
		employee.addressZip.setValue(employeeData.getAddressZip());
		
		setSelectedValueLB(employee.addressProvince, employeeData.getAddressProvinces());
		employee.updateMunicipalities();
		setSelectedValueLB(employee.addressMunicipality, employeeData.getAddressCity());

		employee.mobile.setValue(employeeData.getMobile());
		employee.phone.setValue(employeeData.getPhone());
		employee.email.setValue(employeeData.getEmail());
		
		setSelectedValueLB(employee.payMethod, employeeData.getPaymethodId()+"");
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
			fillContractOtherData(-1);
		} else {
			employee.hideElementsFreelancerTable();
			fillContractTable(contractData);
			fillContractOtherData(Integer.parseInt(contractData.getContractType()));
		}
	}
	
	private void fillContractFreelancerTable(ContractInfo contractData) {
		// RETA
		setSelectedValueLB(employee.ssRegimeType, contractData.getSsRegimen()+"");
		setSelectedValueLB(employee.workplace, contractData.getWorkplaceId()+"");
		
		employee.start_date.setValue(contractData.getStartDate());
		employee.seniority_date.setValue(contractData.getSeniorityDate());
		employee.end_date.setValue(contractData.getEndDate());
		
		Integer agreementId = contractData.getAgreementId();
		setSelectedValueLB(employee.agreement, agreementId+"/"+contractData.getAgreementColective());
		if(null != agreementId) {
			getAgreementLevels(agreementId, s-> {
				setSelectedValueLB(employee.level, contractData.getAgreementLevelId()+"");
				employee.category.setValue(contractData.getAgreementCategory());
			}, f -> {});
		}
		
		setSelectedValueLB(employee.journeyType, contractData.getJourneyType()+"");
	}
	
	private void fillContractTable(ContractInfo contractData) {
		setSelectedValueLB(employee.ssRegimeType, contractData.getSsRegimen()+"");
		
		setSelectedValueLB(employee.activityCCC, contractData.getActivityId()+"/"+contractData.getCccId()+"/"+contractData.getCccType());
		
		if(contractData.getCccType() == (byte)7) {
			employee.showMdCtzContract();
			setSelectedValueLB(employee.mdCTZLB, contractData.getMdctz());
		} else
			employee.hideMdCtzContract();
		
		setSelectedValueLB(employee.workplace, contractData.getWorkplaceId()+"");
		
		setSelectedValueLB(employee.contractTypeLB, contractData.getContractType());
		
		Integer contractTypeInt = Integer.parseInt(contractData.getContractType());
		if(AonNumberUtils.between(contractTypeInt, 200, 300) || AonNumberUtils.between(contractTypeInt, 500, 599) || AonNumberUtils.equals(contractTypeInt, 0)) {
			employee.showPartialTimeContract();
			if(contrataEmployeeObject.getContractData().getContractJourneyDuration().getContractJourneyDuration().entrySet().size() == 0) {
				employee.createJourneyDurationWarning();
			} else {
				employee.createJourneyDurationInfo(contrataEmployeeObject.getContractData().getContractJourneyDuration().getJourneyText());
			}
		} else
			employee.showElementsFullTimeContract();

		employee.updateModality(contractTypeInt);
		setSelectedValueLB(employee.modality, contractData.getContractModel()+"");
		
		employee.start_date.setValue(contractData.getStartDate());
		employee.seniority_date.setValue(contractData.getSeniorityDate());
		employee.end_date.setValue(contractData.getEndDate());
		
		Integer agreementId = contractData.getAgreementId();
		setSelectedValueLB(employee.agreement, agreementId+"/"+contractData.getAgreementColective());
		if(null != agreementId) {
			getAgreementLevels(agreementId, s -> {
				setSelectedValueLB(employee.level, contractData.getAgreementLevelId()+"");
				employee.category.setValue(contractData.getAgreementCategory());
			}, f -> {});
		}
		
		setSelectedValueLB(employee.quote_group, contractData.getQuoteGroup());
		setSelectedValueLB(employee.occupation, contractData.getOcupation());
		employee.partiality_coef.setValue(contractData.getPartialityCoef());	
	}
	
	// ------------------------------------------------- Auxiliar Methods
	
	private void getAgreementLevels(Integer agreementId, Consumer<Agreement> success, Consumer<Throwable> failure) {
		employee.level.clear();
		employee.level.addItem("-", "-1");
		
		contrataEmployeeObject.getAgreement(agreementId,  
		(agreement) -> {
			for (Level levelRecord : agreement.getLevels())
				for (String categoryRecord : agreement.getCategoriesMap().get(levelRecord.getId()))
					employee.level.addItem(levelRecord.getDescription() + " - " + categoryRecord, String.valueOf(levelRecord.getId()));

			contrataEmployeeObject.setContractAgreementId(agreement.getId());
			success.accept(agreement);
		},
		(throwable) -> {
			contrataEmployeeObject.setContractAgreementId(null);
			contrataEmployeeObject.setContractAgreementLevelId(null);
			employee.category.setEnabled(false);
			employee.category.setValue("");
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.category);
			failure.accept(throwable);
		});
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
	
	private void fillContractOtherData(Integer contractType) {
		if(-1 == contractType)
			tabLayOutPanel.getTabWidget(1).setVisible(false);
		else
			tabLayOutPanel.getTabWidget(1).setVisible(true);
	}
	
	private void showSalariesButtons() {
		employeeSalaryButtons.setVisible(true);
		employeeContractButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeEventsButtons.setVisible(false);
		salaryDraftButtos.setVisible(false);
	}
	
	private void showContractButtons() {
		employeeContractButtons.setVisible(true);
		employeeSalaryButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeEventsButtons.setVisible(false);
		salaryDraftButtos.setVisible(false);
	}
	
	private void showCalendarButtons() {
		employeeCalendarButtons.setVisible(true);
		employeeContractButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeEventsButtons.setVisible(false);
		salaryDraftButtos.setVisible(false);
	}
	
	private void showEventsButtons() {
		employeeEventsButtons.setVisible(true);
		employeeCalendarButtons.setVisible(false);
		employeeContractButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		salaryDraftButtos.setVisible(false);
	}
	
	private void showSalaryDraftButtons() {
		salaryDraftButtos.setVisible(true);
		employeeEventsButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeContractButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
	}
	
	// ------------------------------------------------- Toolbar panel
	
	private AonToolbar getToolbarPanel() {
		AonToolbar toolbar = new AonToolbar("Contrato");
		
		listEmployees = new AonToolbarButton( "Volver a contratos", AON.CSS.aonIconBack() );
		listEmployees.setAccessKey('B');
		listEmployees.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onListEmployees(event);
			}
		});
		toolbar.add(listEmployees);
		
		employeeContractButtons = new HTMLPanel("");
		employeeContractButtons.addStyleName(style.flex());
		
		saveContract = new AonToolbarButton( AON.MSG.saveAction() + " Contrato", AON.CSS.aonIconSave() );
		saveContract.setAccessKey('G');
		saveContract.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSaveContract(event);
			}
		});
		employeeContractButtons.add(saveContract);
		
		deleteContract = new AonToolbarButton( AON.MSG.deleteAction() + " Contrato", AON.CSS.aonIconDelete() );
		deleteContract.setAccessKey('D');
		deleteContract.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onDeleteContract(event);
			}
		});
		employeeContractButtons.add(deleteContract);
		
		exportContract = new AonToolbarButton( AON.MSG.export() + "Contrato", AON.CSS.aonIconPdf() );
		exportContract.setAccessKey('E');
		exportContract.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onExportContract(event);
			}
		});
		employeeContractButtons.add(exportContract);
		
		afi = new AonToolbarButton( "Cambios AFI", AON.CSS.aonIconTgssAfi() );
		afi.setAccessKey('A');
		afi.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAFI(event);
			}
		});
		employeeContractButtons.add(afi);
		
		ta = new AonToolbarButton( "Duplicados de Documentos TA", AON.CSS.aonIconTgssTa() );
		ta.setAccessKey('T');
		ta.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onTA(event);
			}
		});
		employeeContractButtons.add(ta);
		
		idc = new AonToolbarButton( "Informe de Cotizaci" + String.valueOf("\u00F3") + "n IDC", AON.CSS.aonIconTgssIdc() );
		idc.setAccessKey('I');
		idc.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onIDC(event);
			}
		});
		employeeContractButtons.add(idc);
		
		cbc = new AonToolbarButton( "Copia B" + String.valueOf("\u00E1") + "sica", AON.CSS.aonIconSepeCbc() );
		cbc.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCBC(event);
			}
		});
		employeeContractButtons.add(cbc);
		
		cto = new AonToolbarButton( "Copia Contrato", AON.CSS.aonIconSepeCto() );
		cto.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCTO(event);
			}
		});
		employeeContractButtons.add(cto);
		
		closePDF = new AonToolbarButton( AON.MSG.closed(), AON.CSS.aonIconClose() );
		closePDF.setAccessKey('I');
		closePDF.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onClosePDF(event);
			}
		});
		employeeContractButtons.add(closePDF);
		
		initZoomList();
		zoomListBox.addChangeHandler(e -> {
			int index =zoomListBox.getSelectedIndex();
			String text = zoomListBox.getItemText(index);
			zoom = (int) (Constants.PERCENT_FORMAT.parse(text));
			pdfViewer.scale(zoom / 100.00);
		});
		employeeContractButtons.add(zoomListBox);
		
		downloadPDF = new AonToolbarButton( AON.MSG.download(), AON.CSS.aonIconPdf() );
		downloadPDF.setAccessKey('D');
		downloadPDF.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onDownloadPDF(event);
			}
		});
		employeeContractButtons.add(downloadPDF);
		
		toolbar.add(employeeContractButtons);
		
		// EmployeeSalary
		
		employeeSalaryButtons = new HTMLPanel("");
		employeeSalaryButtons.addStyleName(style.flex());
		
		deleteButton = new AonToolbarButton( "Borrar N\u00F3mina", AON.CSS.aonIconDeleteList() );
		deleteButton.addClickHandler(e -> {
			employeeSalary.onDelete(e);
		});
		employeeSalaryButtons.add(deleteButton);
		
		pdfButton = new AonToolbarButton( AON.MSG.printPDF() + " N\u00F3mina", AON.CSS.aonIconPdf());
		pdfButton.addClickHandler(e -> {
			employeeSalary.onPDF(e);
		});	
		employeeSalaryButtons.add(pdfButton);
		
		pdfSettleButton = new AonToolbarButton( "Carta Finiquito", AON.CSS.aonIconPdf());
		pdfSettleButton.addClickHandler(e -> {
			employeeSalary.onPDFSettle(e);
		});	
		pdfSettleButton.setVisible(false);
		employeeSalaryButtons.add(pdfSettleButton);
		
		publishButton = new AonToolbarButton( "Drive", AON.CSS.aonIconDrive());
		publishButton.addClickHandler(e -> {
			employeeSalary.onPublish(e);
		});	
		employeeSalaryButtons.add(publishButton);
		
		bidoqPublishButton = new AonToolbarButton( "Bidow", "aon-icon-bidoq");
		bidoqPublishButton.addClickHandler(e -> {
			employeeSalary.onBidoqPublish(e);
		});	
		bidoqPublishButton.setVisible(false);
		employeeSalaryButtons.add(bidoqPublishButton);
		
		email = new AonToolbarButton(AON.MSG.email() +  " N\u00F3mina", AON.CSS.aonIconEmail());
		email.addClickHandler(e -> {
			employeeSalary.onEmail(e);
		});	
		employeeSalaryButtons.add(email);
		
		toolbar.add(employeeSalaryButtons);
		
		// EmployeeCalendar
		
		employeeCalendarButtons = new HTMLPanel("");
		employeeCalendarButtons.addStyleName(style.flex());
		
		undoAllButton = new AonToolbarButton( "Deshacer todo", AON.CSS.aonIconUndoAll());
		undoAllButton.addClickHandler(e -> {
			employeeCalendar.onUndoAll(e);
		});
		employeeCalendarButtons.add(undoAllButton);
		
		saveButton = new AonToolbarButton( AON.MSG.saveAction() + " Calendario", AON.CSS.aonIconSave() );
		saveButton.addClickHandler(e -> {
			employeeCalendar.onSave(e);
		});
		employeeCalendarButtons.add(saveButton);
		
		definitionButton = new AonToolbarButton( "Definicion", AON.CSS.aonIconEditCalendar() );
		definitionButton.addClickHandler(e -> {
			employeeCalendar.onDefinition(e);
		});
		employeeCalendarButtons.add(definitionButton);
		
		utilityButton = new AonToolbarButton( "Utilidades", AON.CSS.aonIconSettings() );
		utilityButton.addClickHandler(e -> {
			employeeCalendar.onUtility(e);
		});
		employeeCalendarButtons.add(utilityButton);
		
		yearLB = new ListBox();
		employeeCalendar.initializeYearLB(yearLB);
		employeeCalendar.setYearLB(yearLB);
		employeeCalendarButtons.add(yearLB);
		
		toolbar.add(employeeCalendarButtons);
		
		// EmployeeEvents
		
		employeeEventsButtons = new HTMLPanel("");
		employeeEventsButtons.addStyleName(style.flex());
		
		undoAllEventsButton = new AonToolbarButton( "Restaurar últimos valores guardados", AON.CSS.aonIconUndo() );
		undoAllEventsButton.addClickHandler(e -> {
			employeeEvents.onUndo(e);
		});
		employeeEventsButtons.add(undoAllEventsButton);
		
		saveEventsButton = new AonToolbarButton( AON.MSG.saveAction() + " Incidencias", AON.CSS.aonIconSave() );
		saveEventsButton.addClickHandler(e -> {
			employeeEvents.onSave(e);
		});
		employeeEventsButtons.add(saveEventsButton);
		
		newValueButton = new AonToolbarButton( "Nuevo valor", AON.CSS.aonIconAdd() );
		newValueButton.addClickHandler(e -> {
			employeeEvents.onNewValue(e);
		});
		employeeEventsButtons.add(newValueButton);
		
		visibilityButton = new AonToolbarButton( "Visualizaci\u00F3n", AON.CSS.aonIconVisibility() );
		visibilityButton.addClickHandler(e -> {
			employeeEvents.onVisibility(e);
		});
		employeeEventsButtons.add(visibilityButton);
		
		yearLBEvents = new ListBox();
		employeeEvents.initializeYearLB(yearLBEvents);
		employeeEvents.setYearLB(yearLBEvents);
		employeeEventsButtons.add(yearLBEvents);
		
		toolbar.add(employeeEventsButtons);
		
		// SalaryDrat
		
		salaryDraftButtos = new HTMLPanel("");
		salaryDraftButtos.addStyleName(style.flex());
		
		undoSalaryAllButton = new AonToolbarButton( "Deshacer todo", AON.CSS.aonIconUndoAll() );
		undoSalaryAllButton.addClickHandler(e -> {
			salaryDraft.onUndoAll(e);
		});	
		undoSalaryAllButton.ensureDebugId("undoAllButton");
		salaryDraft.setUndoAllButton(undoSalaryAllButton);
		salaryDraftButtos.add(undoSalaryAllButton);
		
		undoButton = new AonToolbarButton( "Deshacer", AON.CSS.aonIconUndo() );
		undoButton.addClickHandler(e -> {
			salaryDraft.onUndo(e);
		});	
		undoButton.ensureDebugId("undoButton");
		salaryDraft.setUndoButton(undoButton);
		salaryDraftButtos.add(undoButton);
		
		redoButton = new AonToolbarButton( "Rehacer", AON.CSS.aonIconRedo() );
		redoButton.addClickHandler(e -> {
			salaryDraft.onRedo(e);
		});	
		redoButton.ensureDebugId("redoButton");
		salaryDraft.setRedoButton(redoButton);
		salaryDraftButtos.add(redoButton);
		
		acceptButton = new AonToolbarButton( AON.MSG.saveAction() + " Borrador", AON.CSS.aonIconSave() );
		acceptButton.addClickHandler(e -> {
			salaryDraft.onAccept(e);
		});	
		acceptButton.ensureDebugId("acceptButton");
		acceptButton.setEnabled(false);
		salaryDraft.setAcceptButton(acceptButton);
		salaryDraftButtos.add(acceptButton);
		
		salaryButton = new AonToolbarButton( "Emitir nomina", AON.CSS.aonIconEmit() );
		salaryButton.addClickHandler(e -> {
			salaryDraft.onSalary(e);
		});	
		salaryButton.ensureDebugId("salaryButton");
		salaryDraft.setSalaryButton(salaryButton);
		salaryDraftButtos.add(salaryButton);

		settleButton = new AonToolbarButton( "Emitir finiquito", AON.CSS.aonIconAccept() );
		settleButton.addClickHandler(e -> {
			salaryDraft.onSettle(e);
		});	
		settleButton.ensureDebugId("settleButton");
		settleButton.setVisible(false);
		salaryDraft.setSettleButton(settleButton);
		salaryDraftButtos.add(settleButton);
		
		fxButton = new AonToolbarButton( "FX", AON.CSS.aonIconFx() );
		fxButton.addClickHandler(e -> {
			salaryDraft.onFx(e);
		});	
		fxButton.ensureDebugId("fxButton");
		fxButton.setEnabled(false);
		salaryDraft.setFxButton(fxButton);
		salaryDraftButtos.add(fxButton);
		
		tgssCheck = new CheckBox("SILTRA"); 
		tgssCheck.addValueChangeHandler(e -> {
			salaryDraft.onTgssCheckChange(e);
		});
		tgssCheck.ensureDebugId("tgssCheck");
		tgssCheck.setValue(false);
		salaryDraft.setTgssCheck(tgssCheck);
		salaryDraftButtos.add(tgssCheck);
		
		costsCheck = new CheckBox("COSTES Y BONIF."); 
		costsCheck.addValueChangeHandler(e -> {
			salaryDraft.onCostsCheck2Change(e);
		});
		costsCheck.ensureDebugId("costsCheck");
		costsCheck.setValue(false);
		salaryDraft.setCostsCheck(costsCheck);
		salaryDraftButtos.add(costsCheck);
		
		dbSalaryCheck = new CheckBox("DIFERENCIAS"); 
		dbSalaryCheck.addValueChangeHandler(e -> {
			salaryDraft.onDbSalaryCheckChange(e);
		});
		dbSalaryCheck.ensureDebugId("dbSalaryCheck");
		dbSalaryCheck.setValue(false);
		salaryDraft.setDBSalaryCheck(dbSalaryCheck);
		salaryDraftButtos.add(dbSalaryCheck);
		
		eventsCheck = new CheckBox("AVISOS Y NOTIF."); 
		eventsCheck.addValueChangeHandler(e -> {
			salaryDraft.onEventsCheckChange(e);
		});
		eventsCheck.ensureDebugId("eventsCheck");
		eventsCheck.setValue(false);
		salaryDraft.setEvenstCheck(eventsCheck);
		salaryDraftButtos.add(eventsCheck);
		
		printPreviewButton = new AonToolbarButton( "Vista preliminar", AON.CSS.aonIconPdf() );
		printPreviewButton.addClickHandler(e -> {
			salaryDraft.onPrintPreview(e);
		});	
		printPreviewButton.ensureDebugId("printPreviewButton");
		salaryDraft.setPrintPreviewButton(printPreviewButton);
		salaryDraftButtos.add(printPreviewButton);
		
		irpfPreviewButton = new AonToolbarButton( "IRPF", "aon-icon-irpfPreview");
		irpfPreviewButton.addClickHandler(e -> {
			salaryDraft.onIRPFPreview(e);
		});	
		irpfPreviewButton.ensureDebugId("irpfPreviewButton");
		salaryDraft.setIrpfPreviewButton(irpfPreviewButton);
		salaryDraftButtos.add(irpfPreviewButton);

		salarySelect = new SalarySelect();
		salaryDraft.setSalarySelect(salarySelect);
		salaryDraftButtos.add(salarySelect);
		
		zoomSalaryListBox = new ListBox();
		salaryDraft.initPrintPreview(zoomSalaryListBox);
		salaryDraftButtos.add(zoomListBox);
		
		saveSalaryButton = new AonToolbarButton( "Descargar", AON.CSS.aonIconPdf() );
		saveSalaryButton.addClickHandler(e -> {
			salaryDraft.onSave(e);
		});	
		saveSalaryButton.ensureDebugId("saveButton");
		salaryDraft.setSaveButton(saveSalaryButton);
		salaryDraftButtos.add(saveSalaryButton);
		
		closePreviewButton = new AonToolbarButton( "Cerrar preliminar", AON.CSS.aonIconClose() );
		closePreviewButton.addClickHandler(e -> {
			salaryDraft.onClosePreview(e);
		});	
		closePreviewButton.ensureDebugId("closePreviewButton");
		salaryDraft.setClosePreviewButton(closePreviewButton);
		salaryDraftButtos.add(closePreviewButton);
		
		settlePreviewListBox = new ListBox(); 
		settlePreviewListBox.addItem("ESTANDAR", SalaryDraft.JASPER);
		settlePreviewListBox.addItem("CARTA (&Beta;)", SalaryDraft.LETTER);
		settlePreviewListBox.addChangeHandler(e -> {
			salaryDraft.onSettlePreviewLBChange(e);
		});
		settlePreviewListBox.ensureDebugId("settlePreviewListBox");
		settlePreviewListBox.setVisible(false);
		salaryDraft.setSettlePreviewListBox(settlePreviewListBox);
		salaryDraftButtos.add(settlePreviewListBox);
		
		toolbar.add(salaryDraftButtos);
		
		return toolbar;

	}
	
	private void onListEmployees(ClickEvent event) {
		onListShow(true);
	}

	private void onSaveContract(ClickEvent event) {
		
		Integer itemIdx = tabLayOutPanel.getSelectedIndex();
		switch (itemIdx) {
		case 0:
			if(employee.checkIfSaveEmployeeIsPossible())
				contrataEmployeeObject.setEmployeeContract(s -> {}, f -> {});
			break;
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
	}

	private void onDeleteContract(ClickEvent event) {
		AonDialog dialog = new AonDialog("BORRADO", getMessageWidget());
		dialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAccept() {
				contrataEmployeeObject.deleteContract(s -> {
					onListShow(true);
				}, f-> {});
			}
		});
	}
	
	private void onExportContract(ClickEvent event) {
		contrataEmployeeObject.setContractOtherData(contractOtherData.getContractOtherData());
		contrataEmployeeObject.setContractOtherInfo(s -> {
			String fileDownloadURL = GWT.getModuleBaseURL()+ "contract_export/";
			String query = "?domainName=" + Wnd.getCurrentDomainNameURL()
		            + "&contractId=" + contrataEmployeeObject.getContractData().getContractId()
		            + "&contractType=" + contrataEmployeeObject.getContractData().getContractType()
		            + "&formativeLevel=" + contrataEmployeeObject.getFormativeLevel()
		            + "&employeeFullName=" + contrataEmployeeObject.getEmployeeFullName();				
			
			Window.open(fileDownloadURL+query, "ContractExporter", "resizable=yes,scrollbars=yes,status=yes");
		}, f -> {});
	}

	private void onAFI(ClickEvent event) {
		EmployeeAFIDialog dialog = new EmployeeAFIDialog(
				this.employee.start_date.getValue(),
				this.employee.end_date.getValue(),
				this.employee.contractTypeLB.getSelectedValue(),
				this.employee.quote_group.getSelectedValue(),
				this.employee.occupation.getSelectedValue(),
				this.employee.partiality_coef.getValue(),
				this.contrataEmployeeObject.getContractData().getPayrollDate(),
				this.contrataEmployeeObject.getContractData().getContractId(),
				this.contrataEmployeeObject.getEmployeeData().getDomain(),
				this.contrataEmployeeObject.getContractData().getWorkplaceId()
				){

					@Override
					protected void onAcceptCB() {
						contrataEmployeeObject.getEmployeeContract(s -> {
							setContrataEmployeeObject(contrataEmployeeObject, s);
						}, f -> {});
					}
			
					@Override
					protected void onPartialityCoefContract(String partialityCoef, Date date) {}

					@Override
					protected void onOcupationContract(String ocupation, Date date) {
						contrataEmployeeObject.cambioOcupacion(ocupation, date, s -> {
							AonConfirmDialog dialog = new AonConfirmDialog();
							dialog.info("AVISO: Ocupacion", "El cambio de ocupacion ha sido notificado a la Seguridad Social.");
						}, f -> {});
					}

					@Override
					protected void onQuoteContract(String quoteGroup, Date date) {
						contrataEmployeeObject.cambioGrupCtz(quoteGroup, date, s -> {
							AonConfirmDialog dialog = new AonConfirmDialog();
							dialog.info("AVISO: Grupo cotizacion", "El cambio de grupo de cotizacion ha sido notificado a la Seguridad Social.");
						}, f -> {});
					}

					@Override
					protected void onChangeContract(String contract, Date date) {
						contrataEmployeeObject.cambioCatProf(contract, date, s -> {
							AonConfirmDialog dialog = new AonConfirmDialog();
							dialog.info("AVISO: Tipo contrato", "El cambio de tipo de contrato ha sido notificado a la Seguridad Social.");
						}, f -> {});
					}

					@Override
					protected void onEndContract() {
						contrataEmployeeObject.sendEmployeeBaja(s -> {
							AonConfirmDialog dialog = new AonConfirmDialog();
							dialog.info("AVISO: Baja", "La baja de este trabajador ha sido notificada a la Seguridad Social.");
						}, f -> {});
					}

					@Override
					protected void onStartContract() {
						contrataEmployeeObject.sendEmployeeAlta(s -> {
							AonConfirmDialog dialog = new AonConfirmDialog();
							dialog.info("AVISO: Alta", "El alta de este trabajador ha sido notificado a la Seguridad Social.");
						}, f -> {});
					}
					
				};
			
		dialog.show();
		dialog.center();
	}

	private void onTA(ClickEvent event) {
		showTa();
	}
	
	private void onIDC(ClickEvent event) {
		showIdc();
	}
	
	private void onCBC(ClickEvent event) {
		showCbc();
	}
	
	private void onCTO(ClickEvent event) {
		showCto();
	}

	private void onClosePDF(ClickEvent event) {
		showEmployee();
	}
	
	private void onDownloadPDF(ClickEvent event) {
		String fileName = contrataEmployeeObject.getEmployeeFullName() + " IDC.pdf";
		pdfViewer.download(fileName);
	}
	
	// ------------------------------------------------- Toolbar panel auxiliar methods
	
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
	
	private void showCbc() {

		contrataEmployeeObject.downloadCbc(
		(dataURI) -> {
				showPdf();
				pdfViewer.setDocument(dataURI, zoom / 100.00);
		}, 
		(trowable)-> {
			
		}
		);
	}
	
	private void showCto() {

		contrataEmployeeObject.downloadCto(
		(dataURI) -> {
				showPdf();
				pdfViewer.setDocument(dataURI, zoom / 100.00);
		}, 
		(trowable)-> {
			
		}
		);
	}
	
	private void showPdf() {
		ta.setVisible(false);
		idc.setVisible(false);
		cbc.setVisible(false);
		cto.setVisible(false);
		afi.setVisible(false);
		saveContract.setVisible(false);
		deleteContract.setVisible(false);
		listEmployees.setVisible(false);

		zoomListBox.setVisible(true);
		closePDF.setVisible(true);
		downloadPDF.setVisible(true);

		tabLayOutPanel.getElement().getStyle().setDisplay(Display.NONE);
		pdfViewer.getElement().getStyle().clearDisplay();
	}
	
	// ------------------------------------------------- CheckStatus(contrataEmployeeObject)
	
	private void checkStatus(ContrataEmployeeObject contrataEmployeeObject) {
		contrataEmployeeObject.checkStatus(employeeStatus -> {
			SistemaREDResults sistemaREDResults = new SistemaREDResults() {

				@Override
				public void run() {
					contrataEmployeeObject.checkStatus(employeeStatus -> {
						removeAll();
						employeeStatus.visit(this);
					}, throwable -> {
					});
				}
				
				@Override
				protected void saltraCredentialsFound() {
					contrataEmployeeObject.checkStatus(employeeStatus -> {
						removeAll();
						employeeStatus.visit(this);
						selectResultsPanel();
						showFootPanel();
						ifSistemaREDEnabled(employeeStatus, () -> {
							ContrataEmployee.this.setTGSSVisible(true);
							//ContrataEmployee.this.setOnSaved(e -> run());
						}, () -> {
							ContrataEmployee.this.setTGSSVisible(false);

						});
						ifSistemaREDError(employeeStatus, 
								ContrataEmployee.this::showFootPanel, 
								ContrataEmployee.this::closeFootPanel);

					}, throwable -> {
						closeFootPanel();
						ContrataEmployee.this.setTGSSVisible(false);

					});
				}
			};

			employeeStatus.visit(sistemaREDResults);
			resultsPanel.setWidget(sistemaREDResults);
			selectResultsPanel();

			ifSistemaREDEnabled(employeeStatus, () -> {
				ContrataEmployee.this.setTGSSVisible(true);
			}, () -> {
				ContrataEmployee.this.setTGSSVisible(false);
			});
			
			ifSistemaREDError(employeeStatus, this::showFootPanel, this::closeFootPanel);

		}, throwable -> {
			closeFootPanel();
			ContrataEmployee.this.setTGSSVisible(false);
		});
	}
	
	// ------------------------------------------------- CheckStatus auxiliar methods
	
	private void selectResultsPanel() {
		InlineLabel tab = new InlineLabel("Resultados");
		tab.addStyleName(AON.AON_ICON_TIME);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		footTabPanel.add(resultsPanel, tab);
		footTabPanel.selectTab(resultsPanel);
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}

	private void hideFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 15);
	}

	private void showFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 200);
	}
	
	
	private void setTGSSVisible( boolean visible ) {
		idc.setVisible(visible);
		ta.setVisible(visible);
	}
	
	// ------------------------------------------------- SEPE status
	
	public void setHasCertificateSEPE(boolean hasCertificateSEPE) {
		this.hasCertificateSEPE = hasCertificateSEPE;
	}
	
	private void checkCertificateSEPE() {
		setVisible(cbc.getElement(), hasCertificateSEPE);
		setVisible(cto.getElement(), hasCertificateSEPE);
	}
	
	// ------------------------------------------------- Messages panel
	
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
	
	private Widget getMessageWidget() {
		ContractInfo contractData = contrataEmployeeObject.getContractData();
		EmployeeInfo employeeData = contrataEmployeeObject.getEmployeeData();
		
		String message = "Este contrato ser" + String.valueOf("\u00E1") + " eliminado de forma permanente.<br>" + String.valueOf("\u00BF") + "Desea eliminar el contrato de <b>" + employeeData.getFullName() + "</b>?";
		
		if(null != contractData.getSalariesCount() && contractData.getSalariesCount() > 0) {
			message = "Este contrato contiene n" + String.valueOf("\u00F3") + "minas existentes. Si lo elimina se enviar" + String.valueOf("\u00E1") + " a la papelera.<br>" + String.valueOf("\u00BF") + "Desea eliminar el contrato de <b>" + employeeData.getFullName() + "</b>? <br><br>";
			message += "<b>N" + String.valueOf("\u00F3") + "minas:</b><br><br>";
			for(ContractSalaryInfo salaryInfo : contractData.getContractSalariesInfo())
				message += "&emsp;" + salaryInfo.getType() + "&emsp;(" + formatFullDate.format(salaryInfo.getStart()) + " - " + formatFullDate.format(salaryInfo.getEnd()) + ")&emsp;Percibido : " + salaryInfo.getTotalLiquid() + String.valueOf("\u20AC") + "<br>";
		}
		
		HTML label = new HTML(message);
		return label;
	}

}

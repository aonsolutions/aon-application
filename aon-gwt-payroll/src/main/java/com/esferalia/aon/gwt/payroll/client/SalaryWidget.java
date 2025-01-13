package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type;
import com.esferalia.aon.gwt.payroll.shared.Mail;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryParams;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class SalaryWidget extends AonCustomDockLayout {
	
	// --------------------------------------------- Listener to Publish Salaries
	
	static interface Listener {
		void onPublishSalaries(SalaryInfo salary, String type);
	}
	
	// --------------------------------------------- EmailContextMenu
	
	class NewEmailEmployeesCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onEmailEmployees();
		}
		
		private void onEmailEmployees() {
			sendEmail(com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type.EMPLOYEE);
		}
	}
	
	class NewEmailEnterpriseCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onEmailEnterprise();
		}
		
		private void onEmailEnterprise() {
			sendEmail(com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type.ENTERPRISE);
		}
	}
	
	class NewEmailEnterpriseManagementCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onEmailEnterpriseManagement();
		}
		
		private void onEmailEnterpriseManagement() {
			sendEmail(com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type.ENTERPRISE_MANAGEMENT);
		}
	}

	private void sendEmail(com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type type) {
		Integer enterpriseID = null;
		
		if(!type.equals(com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type.ENTERPRISE_MANAGEMENT))
			enterpriseID = salaryTable.getEnterpriseId();
		
		HashMap<String, String> params = new HashMap<>();
		params.put("url", GWT.getModuleBaseURL()+ "salary_connor_macleod/");
		params.put("type", "salary");
		params.put("name", "salaries.pdf");
		params.put("enterprise", String.valueOf(enterpriseID));
		params.put("domain", Wnd.getCurrentDomainNameURL());
		params.put("user", Wnd.getCurrentUser());
		
		for(int i=0; i<salaryTable.getSelectedSalaries().size(); i++) {
			params.put("id" + i, ""+salaryTable.getSelectedSalaries().get(i).getId());
			params.put("enterpriseId" + i, ""+salaryTable.getSelectedSalaries().get(i).getEnterpriseId());
		}
		
		new PayrollEmailDialog(type, params) {
			
			@Override
			protected void onAccept() {
				Mail mail = new Mail()
						.setFrom(this.getFromMAilAccount().getId().toString())
						.setTo(this.getSendTo())
						.setCc(this.getCC())
						.setCco(this.getCCO())
						.setBodyHTML(this.getBody())
						.setPassword(this.isPassword());
				
				sendPayrollEmail(type, params, mail,
					status -> {
						AonMessagePanel.showSuccess(messagePanel, status);
						hide();
					},f -> {
						AonMessagePanel.showError(messagePanel, f.getMessage());
						hide();
					}
				);
			}
		};
	}
	
	class EmailContextMenu extends ContextMenu {
				
		private MenuItem newEmailEmployees = null;
		private MenuItem newEmailEnterprise = null;
		private MenuItem newEmailEnterpriseManagement = null;
		
		public EmailContextMenu() {
			
			newEmailEmployees = addItem("Email empleados", new NewEmailEmployeesCommand(), 
					AON.CSS.aonIconEmail(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			newEmailEmployees.ensureDebugId("newEmailEmployees");
			
			newEmailEnterprise = addItem("Email empresa", new NewEmailEnterpriseCommand(), 
					AON.CSS.aonIconEmail(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			newEmailEnterprise.ensureDebugId("newEmailEnterprise");
			
			newEmailEnterpriseManagement = addItem("Email empresa", new NewEmailEnterpriseManagementCommand(), 
					AON.CSS.aonIconEmail(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			newEmailEnterpriseManagement.ensureDebugId("newEmailEnterprise");
			newEmailEnterpriseManagement.setVisible(false);
		}
		
		private void setEnterpriseManagement() {
			newEmailEnterprise.setVisible(false);
			newEmailEnterpriseManagement.setVisible(true);
		}
	}	

	// --------------------------------------------- Variables
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonToolbarButton backPDFButton;
	private AonToolbarButton deleteButton;
	private AonToolbarButton pdfButton;
	private AonToolbarButton bidoqPublishButton;
	private AonToolbarButton email;
	
	private EmailContextMenu contextMenu;
	
	private AonCustomListBox period = new AonCustomListBox("Periodo");
	private AonCustomDateBox start = new AonCustomDateBox("Desde");
	private AonCustomDateBox end = new AonCustomDateBox("Hasta");
	
	private AonCustomMultiSelectBox salaryType = new AonCustomMultiSelectBox("Tipo Recibo");

	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private List<Listener> listeners = new LinkedList<>();
	
	private DeckLayoutPanel deckPanel;
	
	private SimpleLayoutPanel centerPanel;
	private SalaryTable salaryTable;
	
	private FullViewer pdfViewer = new FullViewer();
	
	private DomainEnterprisesServiceAsync service = DomainEnterprisesServiceAsync.newInstance();
	private DomainUserRoles dur;
	
	private SalaryParams params;
	
	private boolean isEnterprise = false;
	private boolean isWorkplace = false;
	private boolean isEmployee = false;
	private boolean isEmployeeTree = false;
	
	private Integer contractId;
	private Integer workplaceId;
	
	// --------------------------------------------- Constructor

	protected SalaryWidget() {
		super("N\u00f3minas");
		
		service.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
			
			@Override
			public void onSuccess(DomainUserRoles result) {
				dur = result;
				
				contextMenu = new EmailContextMenu();
				listeners = new LinkedList<>();
				params = new SalaryParams();
				
				addButtonsToolbar();
				
				hideToolbarFilterMessages();
				setSearchPlaceholder("Busque por Empresa / CT / Tabajador...");
				addKeyUpHandler(e -> {
					String value = getSearchTextBox().getValue();
					if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
						onSearch();
					} else if(AonStringUtils.isBlank(value)) {
						onSearch();
					}
				});
				
				period.addItem("Mes actual");
		        period.addItem("\u00daltimos dos meses");
		        period.addItem("\u00daltimo trimestre");
		        period.addItem("\u00daltimo cuatrimestre");
		        period.addItem("\u00daltimo a\u00f1o");
		        period.addItem("Personalizado");
				period.getListBox().setSelectedIndex(1);
		        period.addChangeHandler(e -> {
		        	updateDates();
		        	onSearch();
		        });
				addFilterWidget(period);
				
				HTMLPanel datesPanel = new HTMLPanel(AonStringUtils.EMPTY);
				datesPanel.addStyleName(AON.CSS.aonItemFlex());
				start.setValue(DateUtils.getFirstDayOfMonth(DateUtils.addMonths2Date(new Date(), -1)));
				end.setValue(DateUtils.getLastDayOfMonth());
				start.addValueChangeHandler(e -> onSearch());
				end.addValueChangeHandler(e -> onSearch());
				datesPanel.add(start);
				datesPanel.add(end);
				addFilterWidget(datesPanel);
				
				// salaryType
				Set<String> salaryOptions = new LinkedHashSet<String>();
				salaryOptions.add("Nomina");
				salaryOptions.add("Extra");
				salaryOptions.add("Finiquito");
				salaryOptions.add("Atrasos");
				salaryType.setOptions(salaryOptions);
				salaryType.addBlurHandler(new BlurHandler() {
		            @Override
		            public void onBlur(BlurEvent event) {
		            	onSearch();
		            }
		        });
				salaryType.setSelectedOptions(salaryOptions);
				addFilterWidget(salaryType);
				
				sort.addItem("Empresa", "enterprise");
				sort.addItem("C. Trabajo", "workplace");
				sort.addItem("Trabajador", "employee");
				sort.addItem("Tipo", "type");
				sort.addItem("F. Inicio", "start");
				sort.addItem("F. Fin", "end");
				sort.getListBox().addChangeHandler(event -> onSearch());

				asc.addItem("Ascendente", "true");
				asc.addItem("Descendete", "false");
				asc.getListBox().addChangeHandler(event -> onSearch());

				addSortWidget(sort);
				addSortWidget(asc);
				
				container = new HTMLPanel("");
				container.addStyleName(AON.CSS.aonFlexColumn());

				container.add(messagePanel);
				
				deckPanel = new DeckLayoutPanel();
				deckPanel.setHeight("100%");
				
				centerPanel = new SimpleLayoutPanel();
				centerPanel.setHeight("100%");
				deckPanel.add(centerPanel);
				
				deckPanel.add(pdfViewer);
				
				container.add(deckPanel);
				
				add(container);
				
				showSalary();
				enableDisableButtons(false);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error DUR: " + caught.getMessage());
			}
			
		});
		
	}
	
	private void updateDates() {
		int selectedIndex = period.getListBox().getSelectedIndex();
        Date startDate;
        Date endDate = DateUtils.getLastDayOfMonth();

        switch (selectedIndex) {
            case 0: // Mes actual
                startDate = DateUtils.getFirstDayOfMonth();
                break;
            case 1: // Último dos meses
                startDate = DateUtils.getFirstDayOfMonth(DateUtils.addMonths2Date(new Date(), -1));
                break;
            case 2: // Último trimestre
            	startDate = DateUtils.getFirstDayOfMonth(DateUtils.addMonths2Date(new Date(), -3));
                break;
            case 3: // Último cuatrimestre
            	startDate = DateUtils.getFirstDayOfMonth(DateUtils.addMonths2Date(new Date(), -4));
                break;
            case 4: // Último año
            	startDate = DateUtils.getFirstDayOfMonth(DateUtils.addMonths2Date(new Date(), -12));
                break;
            case 5: // Personalizado
            	startDate = DateUtils.getFirstDayOfMonth();
                break;
            default:
            	startDate = DateUtils.getFirstDayOfMonth();
                break;
        }

        start.setValue(startDate);
        end.setValue(endDate);
        
        start.setEnable(selectedIndex > 4);
        end.setEnable(selectedIndex > 4);
	}
	
	@Override
	protected void onClearFilter() {
		getSearchTextBox().setValue(null, false);
		
		Set<String> salaryOptions = new LinkedHashSet<String>();
		salaryOptions.add("Nomina");
		salaryOptions.add("Extra");
		salaryOptions.add("Finiquito");
		salaryOptions.add("Atrasos");
		salaryType.setSelectedOptions(salaryOptions);
		
		//onSearch();
		period.getListBox().setSelectedIndex(1);
		period.getListBox().fireEvent(new com.google.gwt.event.dom.client.ChangeEvent() {});
	}

	private void addButtonsToolbar() {
		backPDFButton = new AonToolbarButton("Cerrar PDF", AON.CSS.aonIconClose());
		backPDFButton.setVisible(false);
		backPDFButton.addClickHandler(e -> onClosePDF());
		
		deleteButton = new AonToolbarButton("Eliminar", AON.CSS.aonIconDelete());
		deleteButton.setEnabled(false);
		deleteButton.addClickHandler(e -> {
			deleteButton.setEnabled(false);
			salaryTable.deleteSalaries();
		});
		
		pdfButton = new AonToolbarButton("PDF", AON.CSS.aonIconPdf());
		pdfButton.setEnabled(false);
		pdfButton.addClickHandler(e -> onPDF(null));
		
		email = new AonToolbarButton(AON.MSG.email(), AON.CSS.aonIconEmail());
		email.setEnabled(false);
		email.addClickHandler(e -> {
			NativeEvent nativeEvent = e.getNativeEvent();
			contextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
			contextMenu.show();
		});	
		
		bidoqPublishButton = new AonToolbarButton( "Bidoq", "aon-icon-bidoq");
		bidoqPublishButton.setEnabled(false);
		bidoqPublishButton.setVisible(false);
		bidoqPublishButton.addClickHandler(e -> onBidoqPublish());
		
		addToolbarButton(backPDFButton);
		addToolbarButton(deleteButton);
		addToolbarButton(pdfButton);
		addToolbarButton(email);
		addToolbarButton(bidoqPublishButton);
		
	}
	
	private void onPDF(SalaryInfo salary) {
		AonMessagePanel.showLoading(messagePanel, "Cargando n\u00f3mina(s)...");
		
		List<Integer> salaryIds = new ArrayList<>();
		Integer enterpriseId = null;
		
		if(null != salary) {
			salaryIds.add(salary.getId());
			enterpriseId = salary.getEnterpriseId();
		} else {
			for(int i=0; i<salaryTable.getSelectedSalaries().size(); i++)
				salaryIds.add(((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[i]).getId());
			enterpriseId = ((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[0]).getEnterpriseId();
		}
		
		getPDFSalaries(enterpriseId, salaryIds,
				dataURI -> {
					AonMessagePanel.hideMessage(messagePanel);
					showPdf();
					pdfViewer.open(dataURI);
				},
				f -> AonMessagePanel.hideMessage(messagePanel));
	}
	
	private void onSettlePDF(SalaryInfo salary) {
		AonMessagePanel.showLoading(messagePanel, "Cargando carta finiquito...");
		getPDFSettleSalary(salary.getId(),
				dataURI -> {
					AonMessagePanel.hideMessage(messagePanel);
					showPdf();
					pdfViewer.open(dataURI);
				},
				f -> AonMessagePanel.hideMessage(messagePanel));
	}

	private void onBidoqPublish() {
		onPublish("bidoq");
	}
	
	// --------------------------------------------- onSearch
	
	public void onSearch(  ) {
		getWidgetParams();
		salaryTable = new SalaryTable(params) {

			@Override
			protected void onSelectionSalaryChange(boolean isSomethingSelected, boolean hasSettleSelected) {
				enableDisableButtons(isSomethingSelected);
			}
			
			@Override
			protected void onShowPDF(SalaryInfo salary) {
				onPDF(salary);
			}
			
			@Override
			protected void onShowSettlePDF(SalaryInfo salary) {
				onSettlePDF(salary);
			}
			
			@Override
			protected boolean isEmployeeTree() {
				return isEmployeeTree;
			}
			
			@Override
			protected boolean isEnterprise() {
				return isEnterprise;
			}
			
			@Override
			protected boolean isWorkplace() {
				return isWorkplace;
			}
			
			@Override
			protected boolean isEmployee() {
				return isEmployee;
			}

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}

			@Override
			protected void onShowLoadingMessage(String loadingMessage) {
				AonMessagePanel.showLoading(messagePanel, loadingMessage);
			}

			@Override
			protected void onShowSuccessMessage(String successMessage) {
				AonMessagePanel.showSuccess(messagePanel, successMessage);
			}
			
			@Override
			protected void onHideMessage() {
				AonMessagePanel.hideMessage(messagePanel);
			}

		};
			
		centerPanel.setWidget(salaryTable);
	}

	private void getWidgetParams() {
		this.params = new SalaryParams()
				.setDescription(getSearchTextBox().getValue())
				.setStart(start.getValue())
				.setEnd(end.getValue())
				.setSalary(salaryType.getSelectedOptions().contains("Nomina"))
				.setExtra(salaryType.getSelectedOptions().contains("Extra"))
				.setSettle(salaryType.getSelectedOptions().contains("Finiquito"))
				.setDelay(salaryType.getSelectedOptions().contains("Atrasos"))
				.setOrderBy(sort.getValue())
				.setAsc(Boolean.parseBoolean(asc.getValue()))
				
				.setContract(contractId)
				.setWorkplace(workplaceId)
				;
	}

	// --------------------------------------------- Auxiliar Methods
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	void onPublish(String type) {
		for (Listener listener : listeners)
			for(SalaryInfo salary : salaryTable.getSelectedSalaries())
				listener.onPublishSalaries(salary, type);
	}

	public void hideEnterpriseSiteButtons() {
		deleteButton.getElement().getStyle().setDisplay(Display.NONE);
		email.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void enableDisableButtons(boolean isSomethingSelected) {
		deleteButton.setEnabled(isSomethingSelected);
    	pdfButton.setEnabled(isSomethingSelected);
    	
    	if(null != this.dur && this.dur.isBidoq()){
    		bidoqPublishButton.setVisible(isSomethingSelected && null != this.dur && this.dur.isBidoq());
    		bidoqPublishButton.setEnabled(isSomethingSelected && null != this.dur && this.dur.isBidoq());
    	}
    	
    	email.setEnabled(isSomethingSelected);
	}
	
	// ------------------------------------------------- Show/Hide Employee/PDF

	private void showSalary() {
		deckPanel.showWidget(0);
		
		deleteButton.setVisible(true);
		pdfButton.setVisible(true);
		email.setVisible(true);
		
		boolean isSomethingSelected = !salaryTable.getSelectedSalaries().isEmpty();
		if(null != this.dur && this.dur.isBidoq()){
    		bidoqPublishButton.setVisible(isSomethingSelected && null != this.dur && this.dur.isBidoq());
    		bidoqPublishButton.setEnabled(isSomethingSelected && null != this.dur && this.dur.isBidoq());
    	}
		
		backPDFButton.setVisible(false);
	}

	private void showPdf() {
		deckPanel.showWidget(1);
		
		deleteButton.setVisible(false);
		pdfButton.setVisible(false);
		email.setVisible(false);
		bidoqPublishButton.setVisible(false);
		
		backPDFButton.setVisible(true);
	}
	
	private void onClosePDF() {
		showSalary();
	}
	
	public void loadSalaries() {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo n\u00f3minas de los trabajadores...");
		onSearch();
	}
	
	public void setEnterprisesManagement() {
		contextMenu.setEnterpriseManagement();
	}
	
	public SalaryWidget setIsEnterprise() {
		isEnterprise = true;
		return this;
	}
	
	public SalaryWidget setIsWorkplace() {
		isWorkplace = true;
		return this;
	}
	
	public SalaryWidget setIsEmployee() {
		isEmployee = true;
		return this;
	}
	
	public SalaryWidget setIsEmployeeTree() {
		isEmployeeTree = true;
		return this;
	}
	
	public SalaryWidget setContractId(Integer contractId, Date date) {
		this.contractId = contractId;
		
		if( date != null && (date.before(new Date()) || date.equals(new Date())) ) {
			period.getListBox().setSelectedIndex(5);
			
			start.setValue(DateUtils.getFirstDayOfMonth(DateUtils.addMonths2Date(DateUtils.copyDateOnly(date), -1)));
			end.setValue(DateUtils.getLastDayOfMonth(date));
		}
		
		return this;
	}
	
	public SalaryWidget setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
		return this;
	}
	
	// DataBase methods
	
	public void sendPayrollEmail(Type type, HashMap<String, String> params, Mail mail, Consumer<String> success, Consumer<Throwable> failure) {
		service.sendPayrollEmail(type, params, mail, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(String result) {
				success.accept(result);
			}
			
		});
	}
	
	public void getPDFSalaries(Integer enterpriseId, List<Integer> salaryIds, Consumer<String> success, Consumer<Throwable> failure) {
		service.getSalariesPDF(enterpriseId, salaryIds, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(String dataURI) {
				success.accept(dataURI);
			}
		});
	}
	
	public void getPDFSettleSalary(Integer settleId, Consumer<String> success, Consumer<Throwable> failure) {
		service.getSettlePDF(settleId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(String dataURI) {
				success.accept(dataURI);
			}
		});
	}
	
}

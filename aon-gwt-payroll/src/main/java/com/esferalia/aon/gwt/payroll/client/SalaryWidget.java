package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
import com.esferalia.aon.gwt.payroll.shared.ShareService;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

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
	
	private static String SHARE_URL = URL.encode(GWT.getModuleBaseURL() + "share");
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonToolbarButton backPDFButton;
	private AonToolbarButton deleteButton;
	private AonToolbarButton pdfButton;
	private AonToolbarButton downloadButton;
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
	
	private List<SalaryInfo> selectPdfSalaries = new ArrayList<SalaryInfo>();
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
				salaryOptions.add("Sal. Tramitaci\u00f3n");
				salaryOptions.add("Liquidaciones");
				salaryType.setOptions(salaryOptions);
				salaryType.addBlurHandler(new BlurHandler() {
		            @Override
		            public void onBlur(BlurEvent event) {
		            	onSearch();
		            }
		        });
				Set<String> defaultOptions = salaryOptions.stream().filter(option -> !option.contains("Liquidaciones")).collect(Collectors.toSet());
				salaryType.setSelectedOptions(defaultOptions); // Select all except "Liquidaciones" by default
				
				salaryOptions.add("Liquidaciones");
				
				addFilterWidget(salaryType);
				
				sort.addItem("Empresa", "enterprise");
				sort.addItem("C. Trabajo", "workplace");
				sort.addItem("Trabajador", "employee");
				sort.addItem("Tipo", "type");
				sort.addItem("F. Inicio", "start");
				sort.addItem("F. Fin", "end");
				sort.setValue("start");
				sort.getListBox().addChangeHandler(event -> onSearch());

				asc.addItem("Ascendente", "true");
				asc.addItem("Descendete", "false");
				asc.setValue("false");
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
		salaryOptions.add("Sal. Tramitaci\u00f3n");
		salaryOptions.add("Liquidaciones");
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
		
		downloadButton = new AonToolbarButton("Descargar", AON.CSS.aonIconDownload());
		downloadButton.setEnabled(false);
		downloadButton.addClickHandler(e -> onDownload());
		
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
		addToolbarButton(downloadButton);
		addToolbarButton(email);
		addToolbarButton(bidoqPublishButton);
		
	}
	
	private void onPDF(SalaryInfo salary) {
		AonMessagePanel.showLoading(messagePanel, "Cargando n\u00f3mina(s)...");
		
		List<Integer> salaryIds = new ArrayList<>();
		Integer enterpriseId = null;
		
		this.selectPdfSalaries.clear();
		
		if(null != salary) {
			salaryIds.add(salary.getId());
			enterpriseId = salary.getEnterpriseId();
			
			this.selectPdfSalaries.add(salary);
		} else {
			for(int i=0; i<salaryTable.getSelectedSalaries().size(); i++)
				salaryIds.add(((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[i]).getId());
			enterpriseId = ((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[0]).getEnterpriseId();
			
			this.selectPdfSalaries.addAll(salaryTable.getSelectedSalaries());
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
	
	private void onDownload() {
		AonMessagePanel.showLoading(messagePanel, "Descargando n\u00f3mina(s)...");
		
		List<Integer> salaryIds = new ArrayList<>();
		Integer enterpriseId = null;
		
		this.selectPdfSalaries.clear();
		
		for(int i=0; i<salaryTable.getSelectedSalaries().size(); i++)
			salaryIds.add(((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[i]).getId());
		enterpriseId = ((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[0]).getEnterpriseId();
		
		String salaryIdsStr = salaryIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

		String salaryDownloadURL = URL.encode(GWT.getModuleBaseURL() + "salary_download/");
		
		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(salaryDownloadURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		FlowPanel flowPanel = new FlowPanel();
		
		flowPanel.add(new Hidden("domainName", Wnd.getCurrentDomainNameURL()));
		flowPanel.add(new Hidden("user", Wnd.getCurrentUser()));
		
		flowPanel.add(new Hidden("enterpriseId", String.valueOf(enterpriseId)));
		flowPanel.add(new Hidden("salaryIds", salaryIdsStr));
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> {
			getToolbar().remove(formPanel);
			AonMessagePanel.hideMessage(messagePanel);
		});
		
		getToolbar().add(formPanel);
		
		formPanel.submit();
		
		new Timer() {
			@Override public void run() { AonMessagePanel.hideMessage(messagePanel); }
		}.schedule(3500);
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
				.setProcedural(salaryType.getSelectedOptions().contains("Sal. Tramitaci\u00f3n"))
				.setLiquidations(salaryType.getSelectedOptions().contains("Liquidaciones"))
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
		AonMessagePanel.showLoading(messagePanel, "Exportando n\u00f3minas a Bidoq");
		publishBidow(salaryTable.getSelectedSalaries(), 0);
			
		
//		for (Listener listener : listeners)
//			for(SalaryInfo salary : salaryTable.getSelectedSalaries())
//				listener.onPublishSalaries(salary, type);
	}

	private void publishBidow(List<SalaryInfo> selectedSalaries, int index) {
		if(selectedSalaries.size() == index) {
			AonMessagePanel.showSuccess(messagePanel, "N\u00f3minas exportadas correctamente");
		} else {
			SalaryInfo salary = selectedSalaries.get(index);
			
			AonMessagePanel.showLoading(messagePanel, "Exportando n\u00f3mina " + salary.getEmployeeName() + " a Bidoq");
			
			StringBuffer requestDataBuffer = new StringBuffer();

			requestDataBuffer.append("&" + ShareService.SALARY + "=" + salary.getId()).append("&type=bidoq");

			// Send request to server and catch any errors.
			XMLHttpRequest xhr = XMLHttpRequest.create();
			xhr.open("POST", SHARE_URL);
			xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {

				private int loaded = 0;

				@Override
				public void onReadyStateChange(XMLHttpRequest xhr) {
					int state = xhr.getReadyState();

					if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {

						String text = xhr.getResponseText();

						try {
							for (JsShareResult result = read(text); text != null; result = read(text)) {
								if(null != result.getError() && AonStringUtils.startsWith(result.getError(), "succes")) {
									String message = result.getDescription();
									message = AonStringUtils.isNotBlank(message) && AonStringUtils.containsIgnoreCase(message, "-") ? message.split("-")[1].trim() : message;
									AonMessagePanel.showSuccess(messagePanel, message);
								} else {
									AonMessagePanel.showError(messagePanel, result.getError());
								}
								
								Timer timmer = new Timer() {
									
									@Override
									public void run() {
										publishBidow(selectedSalaries, index + 1);											
									}
								};
								
								timmer.schedule(2800);
							}
						} catch (IndexOutOfBoundsException e) {}
					}
				}

				private JsShareResult read(String text) {
					for (int begin = loaded; begin < text.length(); begin++) {
						if (text.charAt(begin) == '{') {
							loaded = findEnd(text, begin + 1) + 1;
							String json = text.substring(begin, loaded);
							return JsonUtils.safeEval(json);
						}
					}
					throw new IndexOutOfBoundsException();
				}

				private int findEnd(String text, int start) {
					for (int end = start; end < text.length(); end++) {
						switch (text.charAt(end)) {
						case '}':
							return end;
						case '{':
							end = findEnd(text, end + 1);
						}
					}
					throw new IndexOutOfBoundsException();
				}

			});

			xhr.send(requestDataBuffer.toString());
			
		}
		
	}

	public void hideEnterpriseSiteButtons() {
		deleteButton.getElement().getStyle().setDisplay(Display.NONE);
		email.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void enableDisableButtons(boolean isSomethingSelected) {
		deleteButton.setEnabled(isSomethingSelected);
    	pdfButton.setEnabled(isSomethingSelected);
    	downloadButton.setEnabled(isSomethingSelected);
    	
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
		downloadButton.setVisible(true);
		email.setVisible(true);
		
		boolean isSomethingSelected = null != salaryTable && !salaryTable.getSelectedSalaries().isEmpty();
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
		downloadButton.setVisible(false);
		email.setVisible(false);
		bidoqPublishButton.setVisible(false);
		
		backPDFButton.setVisible(true);
	}
	
	private void onClosePDF() {
		this.selectPdfSalaries.clear();
		showSalary();
	}
	
	public void loadSalaries() {
		if(this.selectPdfSalaries != null && !this.selectPdfSalaries.isEmpty()) {
			AonMessagePanel.showLoading(messagePanel, "Cargando n\u00f3mina(s)...");
			
			List<Integer> salaryIds = new ArrayList<>();
			Integer enterpriseId = null;
			
			for(int i=0; i<selectPdfSalaries.size(); i++)
				salaryIds.add(((SalaryInfo)selectPdfSalaries.toArray()[i]).getId());
			enterpriseId = ((SalaryInfo)selectPdfSalaries.toArray()[0]).getEnterpriseId();
			
			getPDFSalaries(enterpriseId, salaryIds,
					dataURI -> {
						AonMessagePanel.hideMessage(messagePanel);
						showPdf();
						pdfViewer.open(dataURI);
					},
					f -> AonMessagePanel.hideMessage(messagePanel));
		} else {
			showSalary();
			AonMessagePanel.showLoading(messagePanel, "Obteniendo n\u00f3minas de los trabajadores...");
			onSearch();
		}
	}
	
	public void setEnterprisesManagement() {
		contextMenu.setEnterpriseManagement();
	}
	
	public SalaryWidget setIsEnterprise() {
		isEnterprise = true;
		setSearchPlaceholder("Busque por CT / Tabajador...");
		return this;
	}
	
	public SalaryWidget setIsWorkplace() {
		isWorkplace = true;
		setSearchPlaceholder("Busque por Tabajador...");
		return this;
	}
	
	public SalaryWidget setIsEmployee() {
		isEmployee = true;
		showSeachButton();
		return this;
	}
	
	public SalaryWidget setIsEmployeeTree() {
		isEmployeeTree = true;
		return this;
	}
	
	public SalaryWidget setContractId(Integer contractId, Date contractEndDate) {
		// If new contract selected clear selectPdfSalaries
		if(null != this.contractId && !this.contractId.equals(contractId))
			selectPdfSalaries.clear();
		
		this.contractId = contractId;
		
		if( contractEndDate != null && (contractEndDate.before(new Date()) || contractEndDate.equals(new Date())) ) {
			period.getListBox().setSelectedIndex(5);
			
			start.setValue(DateUtils.getFirstDayOfMonth(DateUtils.addMonths2Date(DateUtils.copyDateOnly(contractEndDate), -12)));
			end.setValue(DateUtils.getLastDayOfMonth(contractEndDate));
		} else {
			period.getListBox().setSelectedIndex(4);
			updateDates();
		}
		
		return this;
	}
	
	public SalaryWidget setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
		return this;
	}

	public void resetSelectPdfSalary() {
		this.selectPdfSalaries = new ArrayList<SalaryInfo>();
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

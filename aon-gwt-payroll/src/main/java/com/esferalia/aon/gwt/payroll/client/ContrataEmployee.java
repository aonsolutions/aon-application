package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.ifSistemaREDEnabled;
import static com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.ifSistemaREDError;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateListBox;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractSalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.Messages;
import com.esferalia.aon.gwt.payroll.shared.Messages.Message;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.Viewer;

public abstract class ContrataEmployee extends ResizeComposite {

	// ------------------------------------------------- UiBinder
	
	private static ContrataEmployeeDraftUiBinder uiBinder = GWT.create(ContrataEmployeeDraftUiBinder.class);

	interface ContrataEmployeeDraftUiBinder extends UiBinder<Widget, ContrataEmployee> {}
	
	// ------------------------------------------------- ContractEmployeeUIImpl
	
	public class ContractEmployeeUIImpl extends ContractEmployeeUI {

		@Override
		protected ScrollPanel getScrollPanel() {
			return scrolledPanel;
		}

		@Override
		protected TabLayoutPanel getTabLayoutPanel() {
			return tabLayOutPanel;
		}

		@Override
		protected TabLayoutPanel getFootTabPanel() {
			return footTabPanel;
		}

		@Override
		protected SplitLayoutPanel getSplitLayoutPanel() {
			return splitLayoutPanel;
		}

		@Override
		protected AonToolbar getToolbar() {
			return toolbar;
		}

		@Override
		protected AonToolbarButton getExportContract() {
			return exportContract;
		}

		@Override
		protected MinimizePanel getFootPanel() {
			return footPanel;
		}

		@Override
		protected MonthListBox getIDCMonthListBox() {
			return idcMonthListBox;
		}
		
	}
	
	// ------------------------------------------------- ContractAttachUIImpl
	
	public class ContractAttachUIImpl extends ContractAttachUI {
		
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
	}
	
	// ------------------------------------------------- ContractAttachUIImpl
	
	public class ContractBonusUIImpl extends ContractBonusUI {
		
		@Override
		protected void fireMessagesResults(Messages messages) {
			paintMessagesResult(messages);
		}
		
	}

	// ------------------------------------------------- ScheduledCommand (TGSS)
	
	class AFICommand implements ScheduledCommand {

		@Override
		public void execute() {
			onAFIChanges();
		}
	}
	
	class IDCCommand implements ScheduledCommand {

		@Override
		public void execute() {
			showIdc();
		}
	}
	
	class IDCPlNssCommand implements ScheduledCommand {

		@Override
		public void execute() {
			showIdcPlNss();
		}
	}
	
	class TACommand implements ScheduledCommand {

		@Override
		public void execute() {
			showTa();
		}
	}
	
	class PeculiaritiesCommand implements ScheduledCommand {

		@Override
		public void execute() {
			EmployeePeculiaritiesDialog dialog = new EmployeePeculiaritiesDialog(contrataEmployeeObject.getContractId(), contrataEmployeeObject.getContractStartDate());
			dialog.center();
			dialog.show();
		}
	}
	
	class MovPrevDeleteCommand implements ScheduledCommand {

		@Override
		public void execute() {
			movPrevDelete();
		}
	}
	
	class AltaConsolidadaDeleteCommand implements ScheduledCommand {

		@Override
		public void execute() {
			altaConsolidadaDelete();
		}
	}
	
	class NewTGSSContextMenu extends ContextMenu {
		
		private MenuItem ta;
		private MenuItem afi;
		private MenuItem idc;
		private MenuItem idcPlNss;		
		private MenuItem peculiarities = null;
		
		private MenuItem movPrevDelete = null;
		private MenuItem altaConsolidadaDelete = null;
		
		public NewTGSSContextMenu() {
			
			afi = addItem("Cambios AFI", new AFICommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			afi.ensureDebugId("afi");
			
			peculiarities = addItem("Peculiaridades de cotizaci" + String.valueOf("\u00F3") + "n", new PeculiaritiesCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			peculiarities.ensureDebugId("peculiarities");
			
			ta = addItem("Duplicados de Documentos TA", new TACommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			ta.ensureDebugId("ta");
			
			idc = addItem("Informe de Cotizaci\u00F3n-Trab Cuenta Ajena", new IDCCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			idc.ensureDebugId("idc");
			
			idcPlNss = addItem("Informe de Cotizaci\u00F3n/Periodo iquidaci\u00F3n-NSS", new IDCPlNssCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			idcPlNss.ensureDebugId("idcPlNss");
			
			addSeparator();
			
			movPrevDelete = addItem("Eliminar movimiento previo", new IDCPlNssCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			movPrevDelete.ensureDebugId("movPrevDelete");
			
			altaConsolidadaDelete = addItem("Eliminar alta consolidada", new IDCPlNssCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			altaConsolidadaDelete.ensureDebugId("altaConsolidadaDelete");
			
		}

		public MenuItem getTa() {
			return ta;
		}

		public MenuItem getAfi() {
			return afi;
		}

		public MenuItem getIdc() {
			return idc;
		}

		public MenuItem getIdcPlNss() {
			return idcPlNss;
		}

		public MenuItem getPeculiarities() {
			return peculiarities;
		}
		
		public MenuItem getMovPrevDelete() {
			return movPrevDelete;
		}
		
		public MenuItem getAltaConsolidadaDelete() {
			return altaConsolidadaDelete;
		}
		
	}
	
	// ------------------------------------------------- ScheduledCommand (SEPE)
	
	class CTOCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onCTO();
		}
	}
	
	class CBCCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onCBC();
		}
	}
	
	class Certifica2Command implements ScheduledCommand {

		@Override
		public void execute() {
			new Certifica2Dialog(contrataEmployeeObject.getContractId());
		}
	}
	
	class Certifica2PDFCommand implements ScheduledCommand {

		@Override
		public void execute() {
			showCertifica2PDF();
		}
	}
	
	class NewSEPEContextMenu extends ContextMenu {
		
		private MenuItem cto;
		private MenuItem cbc;
		private MenuItem cetifica2;
		private MenuItem cetifica2PDF;
		
		public NewSEPEContextMenu() {
			
			cto = addItem("Copia Contrato", new CTOCommand(), 
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			cto.ensureDebugId("cto");
			
			cbc = addItem("Copia B\u00E1sica", new CTOCommand(), 
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			cbc.ensureDebugId("cbc");
			
			cetifica2 = addItem("Cetifica2", new Certifica2Command(), 
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			cetifica2.ensureDebugId("cetifica2");
			
			cetifica2PDF = addItem("Cetifica2 PDF", new Certifica2PDFCommand(), 
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			cetifica2PDF.ensureDebugId("cetifica2PDF");
			
		}

		public MenuItem getCto() {
			return cto;
		}

		public MenuItem getCbc() {
			return cbc;
		}
		
	}
	
	// ------------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String flex();
		String cmd_btn();
	}
	
	@UiField (provided = true)
	ContractEmployeeUI contractEmployeeUI;
	
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
	private AonExpandButton tgss;
	private AonExpandButton sepe;
	private AonToolbarButton closePDF;
	private AonToolbarButton downloadPDF;
	private DateListBox idcDateListBox;
	private MonthListBox idcMonthListBox;
	private ListBox zoomListBox;
	private int zoom = Constants.DEFAULT_ZOOM;
	
	private NewTGSSContextMenu tgssContextMenu;
	private NewSEPEContextMenu sepeContextMenu;
	
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
	private AonToolbarButton extraButton;
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
	
	private boolean hasCertificateSEPE = false;;
	
	// ------------------------------------------------- Constructor
	
	public ContrataEmployee() {
		// Init Tabs Elements
		contractEmployeeUI = new ContractEmployeeUIImpl();
		contractSpecificData = new ContractSpecificData();
		contractOtherData = new ContractOtherData();
		contractClauseUI = new ContractClauseUI();
		contractAttachUI = new ContractAttachUIImpl();
		contractBonusUI = new ContractBonusUIImpl();
		
		employeeSalary = new EmployeeSalary();
		employeeSalary.hideToolbar();
		
		employeeCalendar = new EmployeeCalendarDraftNew();
		employeeCalendar.hideToolbar();
		employeeCalendar.setContrataEmployeeCalendarHeight();
		
		employeeEvents = new EmployeeEventsDraft();
		employeeEvents.hideToolbar();
		
		salaryDraft = new SalaryDraft();
		salaryDraft.hideToolbar();
		
		// Init Widget
		initWidget(uiBinder.createAndBindUi(this));
		
		// Init toolbar
		toolbar = getToolbarPanel();
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		showContractButtons();
		
		// Init ContextMenu
		tgssContextMenu = new NewTGSSContextMenu();
		sepeContextMenu =  new NewSEPEContextMenu();
		
		// Init view
		setScrollPanelsHeight();
		initZoomList();
		initFootPanel();
		initResultsPanel();
		initTabLayOutPanel();
		showEmployee();	
	}
	
	// ------------------------------------------------- Initialize View

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
				contrataEmployeeObject.setContractOtherData(contractOtherData.getContractOtherData());
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
				contrataEmployeeObject.getEmployeeContract(employeeContractInfoIn -> {
					showContractButtons();
					contractEmployeeUI.setContrataEmployeeObject(this.contrataEmployeeObject, employeeContractInfoIn);
					checkStatus(this.contrataEmployeeObject);
					checkCertificateSEPE();
					checkTGSSStatus();
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
	
	// ------------------------------------------------- Show/Hide Employee/PDF
	
	private void showEmployee() {
		saveContract.setVisible(true);
		deleteContract.setVisible(true);
		listEmployees.setVisible(true);
		tgss.setVisible(true);
		sepe.setVisible(true);
		
		zoomListBox.setVisible(false);
		closePDF.setVisible(false);
		downloadPDF.setVisible(false);
		idcDateListBox.setVisible(false);
		idcMonthListBox.setVisible(false);
		
		pdfViewer.getElement().getStyle().setDisplay(Display.NONE);
		tabLayOutPanel.getElement().getStyle().clearDisplay();
	}
	
	private void showPdf() {
		tgss.setVisible(false);
		sepe.setVisible(false);
		saveContract.setVisible(false);
		deleteContract.setVisible(false);
		listEmployees.setVisible(false);

		zoomListBox.setVisible(true);
		closePDF.setVisible(true);
		downloadPDF.setVisible(true);
		idcDateListBox.setVisible(true);
		idcMonthListBox.setVisible(true);

		tabLayOutPanel.getElement().getStyle().setDisplay(Display.NONE);
		pdfViewer.getElement().getStyle().clearDisplay();
	}
	
	// ------------------------------------------------- Initialize View (Auxiliar Method)
	
	protected void getContractBonus(Consumer<List<SSBonusData>> success, Consumer<Throwable> failure) {
		contrataEmployeeObject.getSSBonus( success, failure);
	}
	
	// ------------------------------------------------- setContrataEmployeeObject
	
	public void setContrataEmployeeObject(ContrataEmployeeObject contrataEmployeeDialogObject, EmployeeContractInfo employeeContractInfo) {
		this.contrataEmployeeObject = contrataEmployeeDialogObject;
		contractEmployeeUI.setContrataEmployeeObject(contrataEmployeeDialogObject, employeeContractInfo);
		checkStatus(this.contrataEmployeeObject);
		checkCertificateSEPE();
		checkTGSSStatus();
	}
	
	// ------------------------------------------------- Show/Hide Toolbar methods
	
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
	
	// ------------------------------------------------- Abstract methods
	
	protected abstract void onListShow(boolean reloadEmployees);
	
	// ------------------------------------------------- Toolbar panel
	
	private AonToolbar getToolbarPanel() {
		AonToolbar toolbar = new AonToolbar("Contrato");
		
		listEmployees = new AonToolbarButton( "Volver a contratos", AON.CSS.aonIconBack() );
		listEmployees.addClickHandler(e -> {
			onListEmployees();
		});
		toolbar.add(listEmployees);
		
		// EmployeeContractButtons
		
		employeeContractButtons = initEmployeeContractButtons();
		toolbar.add(employeeContractButtons);
		
		// EmployeeSalary
		
		employeeSalaryButtons = initEmployeeSalaryButtons();
		toolbar.add(employeeSalaryButtons);
				
		// EmployeeCalendar
		
		employeeCalendarButtons = initEmployeeCalendarButtons();
		toolbar.add(employeeCalendarButtons);
		
		// EmployeeEvents
		
		employeeEventsButtons = initEmployeeEventsButtons();
		toolbar.add(employeeEventsButtons);
		
		// SalaryDrat
		
		salaryDraftButtos = initSalaryDraftButtons();
		toolbar.add(salaryDraftButtos);
		
		return toolbar;

	}

	// ------------------------------------------------- Toolbar panel (Auxiliar Methods)
	
	private void onListEmployees() {
		onListShow(true);
	}
	
	// ------------------------------------------------- EmployeeContractButtons
	
	private HTMLPanel initEmployeeContractButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());
		
		saveContract = new AonToolbarButton( AON.MSG.saveAction() + " Contrato", AON.CSS.aonIconSave() );
		saveContract.addClickHandler(e -> {
			onSaveContract();
		});
		hPanel.add(saveContract);
		
		deleteContract = new AonToolbarButton( AON.MSG.deleteAction() + " Contrato", AON.CSS.aonIconDelete() );
		deleteContract.addClickHandler(e -> {
			onDeleteContract();
		});
		hPanel.add(deleteContract);
		
		exportContract = new AonToolbarButton( AON.MSG.export() + "Contrato", AON.CSS.aonIconPdf() );
		exportContract.addClickHandler(e -> {
			onExportContract();
		});
		hPanel.add(exportContract);
		
		tgss = new AonExpandButton("TGSS",  AON.CSS.aonIconTgss()) {
			
			@Override
			public void onExpandClick(ClickEvent event) {
				NativeEvent nativeEvent = event.getNativeEvent();
				tgssContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
				tgssContextMenu.show();
			}
			
			@Override
			public void onDefaultClick(ClickEvent evet) {
				onAFIChanges();
			}
		};
		hPanel.add(tgss);
		
		sepe = new AonExpandButton("SEPE", AON.CSS.aonIconSepe()) {
			
			@Override
			public void onExpandClick(ClickEvent event) {
				NativeEvent nativeEvent = event.getNativeEvent();
				sepeContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
				sepeContextMenu.show();
			}
			
			@Override
			public void onDefaultClick(ClickEvent evet) {
				onCTO();
			}
		};
		hPanel.add(sepe);
		
		idcMonthListBox = new MonthListBox();
		idcMonthListBox.addChangeHandler(e -> {
			showIdcPlNss(idcMonthListBox.getSelectedMonth());
		});
		hPanel.add(idcMonthListBox);
		
		idcDateListBox = new DateListBox();
		idcDateListBox.addChangeHandler(e -> {
			showIdc(idcDateListBox.getSelectedDate());
		});
		hPanel.add(idcDateListBox);
		
		closePDF = new AonToolbarButton( AON.MSG.closed(), AON.CSS.aonIconClose() );
		closePDF.addClickHandler(e -> {
			onClosePDF();
		});
		hPanel.add(closePDF);
		
		initZoomList();
		zoomListBox.addChangeHandler(e -> {
			int index =zoomListBox.getSelectedIndex();
			String text = zoomListBox.getItemText(index);
			zoom = (int) (Constants.PERCENT_FORMAT.parse(text));
			pdfViewer.scale(zoom / 100.00);
		});
		hPanel.add(zoomListBox);
		
		downloadPDF = new AonToolbarButton( AON.MSG.download(), AON.CSS.aonIconPdf() );
		downloadPDF.addClickHandler(e -> {
			onDownloadPDF();
		});
		hPanel.add(downloadPDF);
		
		return hPanel;
	}
	
	// ------------------------------------------------- EmployeeContractButtons (Auxiliar methods)
	
	private void onSaveContract() {
		
		Integer itemIdx = tabLayOutPanel.getSelectedIndex();
		switch (itemIdx) {
		case 0:
			if(contractEmployeeUI.checkIfSaveEmployeeIsPossible())
				contrataEmployeeObject.setEmployeeContract(s -> {}, f -> {});
			break;
		case 1:
			contrataEmployeeObject.setContractSpecificData(s -> {}, f -> {});
			break;
		case 2:
			contrataEmployeeObject.setContractOtherData(contractOtherData.getContractOtherData());
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
	
	private void onDeleteContract() {
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
	
	private void onExportContract() {
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
	
	private void movPrevDelete() {
		contrataEmployeeObject.movPrevDelete(
				s -> {
					contrataEmployeeObject.getEmployeeContract(employeeContractInfoIn -> {
						contractEmployeeUI.setContrataEmployeeObject(contrataEmployeeObject, employeeContractInfoIn);
						checkStatus(contrataEmployeeObject);
						checkCertificateSEPE();
						checkTGSSStatus();
					}, f -> {});
				}, 
				f -> {
					AonDialog dialog = new AonDialog("Error", new HTML(f.getMessage()));
					dialog.warning();
				}
		);
	}

	private void altaConsolidadaDelete() {
		contrataEmployeeObject.altaConsolidadaDelete(
				s -> {
					contrataEmployeeObject.getEmployeeContract(employeeContractInfoIn -> {
						contractEmployeeUI.setContrataEmployeeObject(contrataEmployeeObject, employeeContractInfoIn);
						checkStatus(contrataEmployeeObject);
						checkCertificateSEPE();
						checkTGSSStatus();
					}, f -> {});
				}, 
				f -> {
					AonDialog dialog = new AonDialog("Error", new HTML(f.getMessage()));
					dialog.warning();
				}
		);
	}
	
	private void onAFIChanges() {
		new EmployeeAFIDialog(
				contractEmployeeUI.getStartDate(),
				contractEmployeeUI.getEndDate(),
				contractEmployeeUI.getContractType(),
				contractEmployeeUI.getQuoteGroup(),
				contractEmployeeUI.getOccupation(),
				contractEmployeeUI.getPartialityCoef(),
				this.contrataEmployeeObject.getContractData().getPayrollDate(),
				this.contrataEmployeeObject.getContractData().getContractId(),
				this.contrataEmployeeObject.getEmployeeData().getDomain(),
				this.contrataEmployeeObject.getContractData().getWorkplaceId()
				){

					@Override
					protected void onAcceptCB() {
						contrataEmployeeObject.getEmployeeContract(employeeContractInfoIn -> {
							contractEmployeeUI.setContrataEmployeeObject(contrataEmployeeObject, employeeContractInfoIn);
							checkStatus(contrataEmployeeObject);
							checkCertificateSEPE();
							checkTGSSStatus();
						}, f -> {});
					}
			
					@Override
					protected void onPartialityCoefContract(String partialityCoef, Date date) {
						// TODO: esperar a Ray
					}

					@Override
					protected void onOcupationContract(String ocupation, Date date) {
						contrataEmployeeObject.cambioOcupacion(ocupation, date, s -> {
							AonConfirmDialog dialog = new AonConfirmDialog();
							dialog.info("AVISO: Ocupacion", "El cambio de ocupacion ha sido notificado a la Seguridad Social.");
						}, f -> {
							AonDialog dialog = new AonDialog("Error", new HTML(f.getMessage()));
							dialog.warning();
						});
					}

					@Override
					protected void onQuoteContract(String quoteGroup, Date date) {
						contrataEmployeeObject.cambioGrupCtz(quoteGroup, date, s -> {
							AonConfirmDialog dialog = new AonConfirmDialog();
							dialog.info("AVISO: Grupo cotizacion", "El cambio de grupo de cotizacion ha sido notificado a la Seguridad Social.");
						}, f -> {
							AonDialog dialog = new AonDialog("Error", new HTML(f.getMessage()));
							dialog.warning();
						});
					}

					@Override
					protected void onChangeContract(String contract, Date date) {
						contrataEmployeeObject.cambioCatProf(contract, date, s -> {
							AonConfirmDialog dialog = new AonConfirmDialog();
							dialog.info("AVISO: Tipo contrato", "El cambio de tipo de contrato ha sido notificado a la Seguridad Social.");
						}, f -> {
							AonDialog dialog = new AonDialog("Error", new HTML(f.getMessage()));
							dialog.warning();
						});
					}

					@Override
					protected void onEndContract(String settleReason) {
						contrataEmployeeObject.sendEmployeeBaja(settleReason, s -> {
							AonConfirmDialog dialog = new AonConfirmDialog();
							dialog.info("AVISO: Baja", "La baja de este trabajador ha sido notificada a la Seguridad Social.");
						}, f -> {
							AonDialog dialog = new AonDialog("Error", new HTML(f.getMessage()));
							dialog.warning();
						});
					}

					@Override
					protected void onStartContract() {
						contrataEmployeeObject.sendEmployeeAlta(s -> {
							AonConfirmDialog dialog = new AonConfirmDialog();
							dialog.info("AVISO: Alta", "El alta de este trabajador ha sido notificado a la Seguridad Social.");
							
							downloadTA();
						}, f -> {
							AonDialog dialog = new AonDialog("Error", new HTML(f.getMessage()));
							dialog.warning();
						});
					}

				};
	}
	
	private void downloadTA() {
		String fileDownloadURL = GWT.getModuleBaseURL()+ "comunica_file/";
		String query = "?currentUser=" + Wnd.getCurrentUser()
				+ "&currentDomain=" + Wnd.getCurrentDomainNameURL()
				+ "&token=" + Wnd.getToken()
	            + "&contractId=" + contrataEmployeeObject.getContractData().getContractId()
	            + "&document=" + contrataEmployeeObject.getEmployeeData().getDocument()
	            + "&fileType=TA";
		
		Window.open(fileDownloadURL+query, "ComunicaFileExporter", "resizable=yes,scrollbars=yes,status=yes");
		
		downloadIDC();
	}
	
	private void downloadIDC() {
		String fileDownloadURL = GWT.getModuleBaseURL()+ "comunica_file/";
		String query = "?currentUser=" + Wnd.getCurrentUser()
				+ "&currentDomain=" + Wnd.getCurrentDomainNameURL()
				+ "&token=" + Wnd.getToken()
	            + "&contractId=" + contrataEmployeeObject.getContractData().getContractId()
	            + "&document=" + contrataEmployeeObject.getEmployeeData().getDocument()
	            + "&fileType=IDC";
		
		Window.open(fileDownloadURL+query, "ComunicaFileExporter", "resizable=yes,scrollbars=yes,status=yes");
	}

	private void showTa() {
		contrataEmployeeObject.downloadTa((dataURI) -> {
				showPdf();
				pdfViewer.setDocument(dataURI, zoom / 100.00);
		}, (trowable)-> {});
	}
	
	private void showIdc() {
		showIdc(idcDateListBox.getSelected());
	}

	private void showIdc( Date date) {
		contrataEmployeeObject.downloadIdc(date,
		(dataURI) -> {
				showPdf();
				idcDateListBox.setVisible(true);
				idcDateListBox.setSelected(date, true);
				pdfViewer.setDocument(dataURI, zoom / 100.00);
		}, (trowable) -> {});
	}

	private void showIdcPlNss() {
		showIdcPlNss(DateUtils.getFirstDayOfMonth());
	}
	
	private void showIdcPlNss( Date month) {
		contrataEmployeeObject.downloadIdcPlNss(month,
		(dataURI) -> {
				showPdf();
				idcMonthListBox.setVisible(true);
				idcMonthListBox.setSelected(month, true);
				pdfViewer.setDocument(dataURI, zoom / 100.00);
		}, (trowable) -> {});
	}

	private void onCTO() {
		showCto();
	}
	
	private void showCto() {
		contrataEmployeeObject.downloadCto((dataURI) -> {
				showPdf();
				pdfViewer.setDocument(dataURI, zoom / 100.00);
		}, (trowable)-> {});
	}

	private void onCBC() {
		showCbc();
	}
	
	private void showCbc() {
		contrataEmployeeObject.downloadCbc((dataURI) -> {
				showPdf();
				pdfViewer.setDocument(dataURI, zoom / 100.00);
		}, (trowable)-> {});
	}
	
	private void showCertifica2PDF() {
		contrataEmployeeObject.getCertifica2PDF((dataURI) -> {
			showPdf();
			pdfViewer.setDocument(dataURI, zoom / 100.00);
	}, (trowable)-> {});
	}
	
	private void onClosePDF() {
		showEmployee();
	}
	
	private void onDownloadPDF() {
		String fileName = contrataEmployeeObject.getEmployeeFullName() + " IDC.pdf";
		pdfViewer.download(fileName);
	}
	
	// ------------------------------------------------- EmployeeSalaryButtons
	
	private HTMLPanel initEmployeeSalaryButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());
		
		deleteButton = new AonToolbarButton( "Borrar N\u00F3mina", AON.CSS.aonIconDeleteList() );
		deleteButton.addClickHandler(e -> {
			employeeSalary.onDelete(e);
		});
		hPanel.add(deleteButton);
		
		pdfButton = new AonToolbarButton( AON.MSG.printPDF() + " N\u00F3mina", AON.CSS.aonIconPdf());
		pdfButton.addClickHandler(e -> {
			employeeSalary.onPDF(e);
		});	
		hPanel.add(pdfButton);
		
		pdfSettleButton = new AonToolbarButton( "Carta Finiquito", AON.CSS.aonIconPdf());
		pdfSettleButton.addClickHandler(e -> {
			employeeSalary.onPDFSettle(e);
		});	
		pdfSettleButton.setVisible(false);
		hPanel.add(pdfSettleButton);
		
		publishButton = new AonToolbarButton( "Drive", AON.CSS.aonIconDrive());
		publishButton.addClickHandler(e -> {
			employeeSalary.onPublish(e);
		});	
		hPanel.add(publishButton);
		
		bidoqPublishButton = new AonToolbarButton( "Bidow", "aon-icon-bidoq");
		bidoqPublishButton.addClickHandler(e -> {
			employeeSalary.onBidoqPublish(e);
		});	
		bidoqPublishButton.setVisible(false);
		hPanel.add(bidoqPublishButton);
		
		email = new AonToolbarButton(AON.MSG.email() +  " N\u00F3mina", AON.CSS.aonIconEmail());
		email.addClickHandler(e -> {
			employeeSalary.onEmail(e);
		});	
		hPanel.add(email);
		
		return hPanel;
	}
	
	// ------------------------------------------------- EmployeeCalendarButtons
	
	private HTMLPanel initEmployeeCalendarButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());
		
		undoAllButton = new AonToolbarButton( "Deshacer todo", AON.CSS.aonIconUndoAll());
		undoAllButton.addClickHandler(e -> {
			employeeCalendar.onUndoAll(e);
		});
		hPanel.add(undoAllButton);
		
		saveButton = new AonToolbarButton( AON.MSG.saveAction() + " Calendario", AON.CSS.aonIconSave() );
		saveButton.addClickHandler(e -> {
			employeeCalendar.onSave(e);
		});
		hPanel.add(saveButton);
		
		definitionButton = new AonToolbarButton( "Definicion", AON.CSS.aonIconEditCalendar() );
		definitionButton.addClickHandler(e -> {
			employeeCalendar.onDefinition(e);
		});
		hPanel.add(definitionButton);
		
		utilityButton = new AonToolbarButton( "Utilidades", AON.CSS.aonIconSettings() );
		utilityButton.addClickHandler(e -> {
			employeeCalendar.onUtility(e);
		});
		hPanel.add(utilityButton);
		
		yearLB = new ListBox();
		employeeCalendar.initializeYearLB(yearLB);
		employeeCalendar.setYearLB(yearLB);
		hPanel.add(yearLB);
		
		return hPanel;
	}
	
	// ------------------------------------------------- EmployeeEventsButtons
	
	private HTMLPanel initEmployeeEventsButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());
		
		undoAllEventsButton = new AonToolbarButton( "Restaurar últimos valores guardados", AON.CSS.aonIconUndo() );
		undoAllEventsButton.addClickHandler(e -> {
			employeeEvents.onUndo(e);
		});
		hPanel.add(undoAllEventsButton);
		
		saveEventsButton = new AonToolbarButton( AON.MSG.saveAction() + " Incidencias", AON.CSS.aonIconSave() );
		saveEventsButton.addClickHandler(e -> {
			employeeEvents.onSave(e);
		});
		hPanel.add(saveEventsButton);
		
		newValueButton = new AonToolbarButton( "Nuevo valor", AON.CSS.aonIconAdd() );
		newValueButton.addClickHandler(e -> {
			employeeEvents.onNewValue(e);
		});
		hPanel.add(newValueButton);
		
		visibilityButton = new AonToolbarButton( "Visualizaci\u00F3n", AON.CSS.aonIconVisibility() );
		visibilityButton.addClickHandler(e -> {
			employeeEvents.onVisibility(e);
		});
		hPanel.add(visibilityButton);
		
		yearLBEvents = new ListBox();
		employeeEvents.initializeYearLB(yearLBEvents);
		employeeEvents.setYearLB(yearLBEvents);
		hPanel.add(yearLBEvents);
		
		return hPanel;
	}
	
	// ------------------------------------------------- EmployeeSalaryButtons
	
	private HTMLPanel initSalaryDraftButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());
		
		undoSalaryAllButton = new AonToolbarButton( "Deshacer todo", AON.CSS.aonIconUndoAll() );
		undoSalaryAllButton.addClickHandler(e -> {
			salaryDraft.onUndoAll(e);
		});	
		undoSalaryAllButton.ensureDebugId("undoAllButton");
		salaryDraft.setUndoAllButton(undoSalaryAllButton);
		hPanel.add(undoSalaryAllButton);
		
		undoButton = new AonToolbarButton( "Deshacer", AON.CSS.aonIconUndo() );
		undoButton.addClickHandler(e -> {
			salaryDraft.onUndo(e);
		});	
		undoButton.ensureDebugId("undoButton");
		salaryDraft.setUndoButton(undoButton);
		hPanel.add(undoButton);
		
		redoButton = new AonToolbarButton( "Rehacer", AON.CSS.aonIconRedo() );
		redoButton.addClickHandler(e -> {
			salaryDraft.onRedo(e);
		});	
		redoButton.ensureDebugId("redoButton");
		salaryDraft.setRedoButton(redoButton);
		hPanel.add(redoButton);
		
		acceptButton = new AonToolbarButton( AON.MSG.saveAction() + " Borrador", AON.CSS.aonIconSave() );
		acceptButton.addClickHandler(e -> {
			salaryDraft.onAccept(e);
		});	
		acceptButton.ensureDebugId("acceptButton");
		acceptButton.setEnabled(false);
		salaryDraft.setAcceptButton(acceptButton);
		hPanel.add(acceptButton);
		
		salaryButton = new AonToolbarButton( "Emitir nomina", AON.CSS.aonIconEmit() );
		salaryButton.addClickHandler(e -> {
			salaryDraft.onSalary(e);
		});	
		salaryButton.ensureDebugId("salaryButton");
		salaryDraft.setSalaryButton(salaryButton);
		hPanel.add(salaryButton);

		settleButton = new AonToolbarButton( "Emitir finiquito", AON.CSS.aonIconAccept() );
		settleButton.addClickHandler(e -> {
			salaryDraft.onSettle(e);
		});	
		settleButton.ensureDebugId("settleButton");
		settleButton.setVisible(false);
		salaryDraft.setSettleButton(settleButton);
		hPanel.add(settleButton);
		
		extraButton = new AonToolbarButton( "Emitir extra", AON.CSS.aonIconAccept() );
		extraButton.addClickHandler(e -> {
			salaryDraft.onExtra(e);
		});	
		extraButton.ensureDebugId("extraButton");
		extraButton.setVisible(false);
		salaryDraft.setExtraButton(extraButton);
		salaryDraftButtos.add(extraButton);

		fxButton = new AonToolbarButton( "FX", AON.CSS.aonIconFx() );
		fxButton.addClickHandler(e -> {
			salaryDraft.onFx(e);
		});	
		fxButton.ensureDebugId("fxButton");
		fxButton.setEnabled(false);
		salaryDraft.setFxButton(fxButton);
		hPanel.add(fxButton);
		
		tgssCheck = new CheckBox("SILTRA"); 
		tgssCheck.addValueChangeHandler(e -> {
			salaryDraft.onTgssCheckChange(e);
		});
		tgssCheck.ensureDebugId("tgssCheck");
		tgssCheck.setValue(false);
		salaryDraft.setTgssCheck(tgssCheck);
		hPanel.add(tgssCheck);
		
		costsCheck = new CheckBox("COSTES Y BONIF."); 
		costsCheck.addValueChangeHandler(e -> {
			salaryDraft.onCostsCheck2Change(e);
		});
		costsCheck.ensureDebugId("costsCheck");
		costsCheck.setValue(false);
		salaryDraft.setCostsCheck(costsCheck);
		hPanel.add(costsCheck);
		
		dbSalaryCheck = new CheckBox("DIFERENCIAS"); 
		dbSalaryCheck.addValueChangeHandler(e -> {
			salaryDraft.onDbSalaryCheckChange(e);
		});
		dbSalaryCheck.ensureDebugId("dbSalaryCheck");
		dbSalaryCheck.setValue(false);
		salaryDraft.setDBSalaryCheck(dbSalaryCheck);
		hPanel.add(dbSalaryCheck);
		
		eventsCheck = new CheckBox("AVISOS Y NOTIF."); 
		eventsCheck.addValueChangeHandler(e -> {
			salaryDraft.onEventsCheckChange(e);
		});
		eventsCheck.ensureDebugId("eventsCheck");
		eventsCheck.setValue(false);
		salaryDraft.setEvenstCheck(eventsCheck);
		hPanel.add(eventsCheck);
		
		printPreviewButton = new AonToolbarButton( "Vista preliminar", AON.CSS.aonIconPdf() );
		printPreviewButton.addClickHandler(e -> {
			salaryDraft.onPrintPreview(e);
		});	
		printPreviewButton.ensureDebugId("printPreviewButton");
		salaryDraft.setPrintPreviewButton(printPreviewButton);
		hPanel.add(printPreviewButton);
		
		irpfPreviewButton = new AonToolbarButton( "IRPF", "aon-icon-irpfPreview");
		irpfPreviewButton.addClickHandler(e -> {
			salaryDraft.onIRPFPreview(e);
		});	
		irpfPreviewButton.ensureDebugId("irpfPreviewButton");
		salaryDraft.setIrpfPreviewButton(irpfPreviewButton);
		hPanel.add(irpfPreviewButton);

		salarySelect = new SalarySelect();
		salaryDraft.setSalarySelect(salarySelect);
		hPanel.add(salarySelect);
		
		zoomSalaryListBox = new ListBox();
		salaryDraft.initPrintPreview(zoomSalaryListBox);
		hPanel.add(zoomListBox);
		
		saveSalaryButton = new AonToolbarButton( "Descargar", AON.CSS.aonIconPdf() );
		saveSalaryButton.addClickHandler(e -> {
			salaryDraft.onSave(e);
		});	
		saveSalaryButton.ensureDebugId("saveButton");
		salaryDraft.setSaveButton(saveSalaryButton);
		hPanel.add(saveSalaryButton);
		
		closePreviewButton = new AonToolbarButton( "Cerrar preliminar", AON.CSS.aonIconClose() );
		closePreviewButton.addClickHandler(e -> {
			salaryDraft.onClosePreview(e);
		});	
		closePreviewButton.ensureDebugId("closePreviewButton");
		salaryDraft.setClosePreviewButton(closePreviewButton);
		hPanel.add(closePreviewButton);
		
		settlePreviewListBox = new ListBox(); 
		settlePreviewListBox.addItem("ESTANDAR", SalaryDraft.JASPER);
		settlePreviewListBox.addItem("CARTA (&Beta;)", SalaryDraft.LETTER);
		settlePreviewListBox.addChangeHandler(e -> {
			salaryDraft.onSettlePreviewLBChange(e);
		});
		settlePreviewListBox.ensureDebugId("settlePreviewListBox");
		settlePreviewListBox.setVisible(false);
		salaryDraft.setSettlePreviewListBox(settlePreviewListBox);
		hPanel.add(settlePreviewListBox);
		
		return hPanel;
	}

	// ------------------------------------------------- CheckStatus (contrataEmployeeObject)
	
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
							ContrataEmployee.this.setTaVisible(true);
							ContrataEmployee.this.setIdcVisible(true);
						}, () -> {
							ContrataEmployee.this.setTaVisible(false);
							ContrataEmployee.this.setIdcVisible(false);

						});
						ifSistemaREDError(employeeStatus, 
								ContrataEmployee.this::showFootPanel, 
								ContrataEmployee.this::closeFootPanel);

					}, throwable -> {
						closeFootPanel();
						ContrataEmployee.this.setTaVisible(false);
						ContrataEmployee.this.setIdcVisible(false);

					});
				}
			};

			employeeStatus.visit(sistemaREDResults);
			resultsPanel.setWidget(sistemaREDResults);
			selectResultsPanel();

			ifSistemaREDEnabled(employeeStatus, () -> {
				ContrataEmployee.this.setTaVisible(true);
				ContrataEmployee.this.setIdcVisible(true);
			}, () -> {
				ContrataEmployee.this.setTaVisible(false);
				ContrataEmployee.this.setIdcVisible(false);
			});
			
			ifSistemaREDError(employeeStatus, this::showFootPanel, this::closeFootPanel);

		}, throwable -> {
			closeFootPanel();
			ContrataEmployee.this.setTaVisible(false);
			ContrataEmployee.this.setIdcVisible(false);
		});
	}
	
	// ------------------------------------------------- CheckStatus (Auxiliar methods)
	
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
	
	public void setTaVisible(boolean visible ) {
		tgssContextMenu.getTa().setVisible(visible);
	}

	public void setIdcVisible(boolean visible ) {
		initializeIdcDateListBox();
		tgssContextMenu.getIdcPlNss().setVisible(visible);
	}
	
	private void initializeIdcDateListBox() {
		tgssContextMenu.getIdc().setEnabled(false);
		tgssContextMenu.getIdc().setVisible(false);
		contrataEmployeeObject.getIdcDates(
		(dates) -> {
			int count = dates.size();
			idcDateListBox.setRowCount(count, true);
			idcDateListBox.setRowData(0, dates);
			idcDateListBox.setVisibleRange(0, count+1);
			idcDateListBox.setSelected(count-1, true);
			idcDateListBox.onResizeDropDownPopup();
			tgssContextMenu.getIdc().setEnabled(true);
			tgssContextMenu.getIdc().setVisible(true);
		}, 
		(error) -> {
		} );
	}
	
	// ------------------------------------------------- SEPE status
	
	public void setHasCertificateSEPE(boolean hasCertificateSEPE) {
		this.hasCertificateSEPE = hasCertificateSEPE;
	}
	
	private void checkCertificateSEPE() {
//		setVisible(sepe.getElement(), hasCertificateSEPE);
		setVisible(sepeContextMenu.getCto().getElement(), hasCertificateSEPE);
		setVisible(sepeContextMenu.getCbc().getElement(), hasCertificateSEPE);
	}
	
	// ------------------------------------------------- TGSS status
	
	private void checkTGSSStatus() {
		boolean isTGSSActive = contrataEmployeeObject.getContractData().isTGSSActive();
		Date startDate = contrataEmployeeObject.getContractData().getStartDate();
		Date endDate = contrataEmployeeObject.getContractData().getEndDate();
		
		if(isTGSSActive && DateUtils.isAfterOrEquals(new Date(), startDate))
			setVisible(tgssContextMenu.getAltaConsolidadaDelete().getElement(), true);
		else
			setVisible(tgssContextMenu.getAltaConsolidadaDelete().getElement(), false);
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

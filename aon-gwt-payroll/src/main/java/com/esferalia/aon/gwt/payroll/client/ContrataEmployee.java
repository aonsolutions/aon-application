package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.ifSistemaREDEnabled;
import static com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.ifSistemaREDError;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateListBox;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractSalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

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

		@Override
		protected MenuItem getAFIEnd() {
			return tgssContextMenu.getAfiEnd();
		}
		
	}
	
	// ------------------------------------------------- ContractAttachUIImpl
	
	public class ContractAttachUIImpl extends ContractAttachUI {
		
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
	
	// ------------------------------------------------- EmployeeEventsImpl
	
	public class EmployeeEventsImpl extends EmployeeEventsDraft {

		@Override
		protected void onShowCalendar() {
			tabLayOutPanel.selectTab(7, true);
		}
		
	}

	// ------------------------------------------------- EmployeeIrpfImpl
	
	public class EmployeeIrpfImpl extends EmployeeContractIrpf{

		@Override
		protected void fireSSNumberErrorMessage() {
			Map<String, String> messageErrorMap = new HashMap<>();
			messageErrorMap.put("Error n\u00FAmero Seguridad Social", "El contrato " + contrataEmployeeObject.getEmployeeFullName() + " no tiene definido el n\u00FAmero de la Seguridad Social. Def\u00EDnalo antes de rellas los IRPFs");
			AonMessagePanel.showError(messageContainer, messageErrorMap);
		}
		
	}
	
	// ------------------------------------------------- ScheduledCommand (TGSS)
	
	class AFICommand implements ScheduledCommand {

		@Override
		public void execute() {
			onAFIChanges();
		}
	}
	
	class AFIEndCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onAFIEndChanges();
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
		private MenuItem afiEnd;
		private MenuItem idc;
		private MenuItem idcPlNss;		
		private MenuItem peculiarities = null;
		
		private MenuItem movPrevDelete = null;
		private MenuItem altaConsolidadaDelete = null;
		
		public NewTGSSContextMenu() {
			
			afi = addItem("Cambios AFI", new AFICommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			afi.ensureDebugId("afi");
			
			afiEnd = addItem("Cambios AFI (Baja)", new AFIEndCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			afiEnd.ensureDebugId("afiEnd");
			
			peculiarities = addItem("Peculiaridades de cotizaci\u00F3n", new PeculiaritiesCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			peculiarities.ensureDebugId("peculiarities");
			
			ta = addItem("Duplicados de Documentos TA", new TACommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			ta.ensureDebugId("ta");
			
			idc = addItem("Informe de Cotizaci\u00F3n-Trab Cuenta Ajena", new IDCCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			idc.ensureDebugId("idc");
			
			idcPlNss = addItem("Informe de Cotizaci\u00F3n/Periodo iquidaci\u00F3n-NSS", new IDCPlNssCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			idcPlNss.ensureDebugId("idcPlNss");
			
			addSeparator();
			
			movPrevDelete = addItem("Eliminar movimiento previo", new MovPrevDeleteCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			movPrevDelete.ensureDebugId("movPrevDelete");
			
			altaConsolidadaDelete = addItem("Eliminar alta consolidada", new AltaConsolidadaDeleteCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			altaConsolidadaDelete.ensureDebugId("altaConsolidadaDelete");
			
		}

		public MenuItem getTa() {
			return ta;
		}

		public MenuItem getAfi() {
			return afi;
		}
		
		public MenuItem getAfiEnd() {
			return afiEnd;
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
	
	class SendBasicCopyCommand implements ScheduledCommand {

		@Override
		public void execute() {
			sendBasicCopy();
		}
	}
	
	class SendContractCommand implements ScheduledCommand {

		@Override
		public void execute() {
			sendContract();
		}
	}
	
	class SepeIDEContractCommand implements ScheduledCommand {

		@Override
		public void execute() {
			sepeIDEContract();
		}
	}
	
	class ContractExtensionCommand implements ScheduledCommand {

		@Override
		public void execute() {
			contractExtension();
		}

	}
	
	class DeleteContractExtensionCommand implements ScheduledCommand {

		@Override
		public void execute() {
			deleteContractExtension();
		}

	}
	
	class ContractTransformCommand implements ScheduledCommand {

		@Override
		public void execute() {
			contractTransform();
		}

	}
	
	class RemoveContractCommand implements ScheduledCommand {

		@Override
		public void execute() {
			removeContract();
		}

	}
	
	class NewSEPEContextMenu extends ContextMenu {
		
		private MenuItem cto;
		private MenuItem cbc;
		private MenuItem cetifica2;
		private MenuItem cetifica2PDF;
		
		private MenuItem contractExtension;
		private MenuItem contractTransform;
		private MenuItem removeContractExtension;
		
		private MenuItem sendBasicCopy;
		private MenuItem sendContract;
		private MenuItem sepeIDE;
		
		private MenuItem removeContract;
		
		public NewSEPEContextMenu() {
			
			cto = addItem("Copia Contrato", new CTOCommand(), 
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			cto.ensureDebugId("cto");
			
			cbc = addItem("Copia B\u00E1sica", new CBCCommand(), 
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			cbc.ensureDebugId("cbc");
			
			addSeparator();
			
			cetifica2 = addItem("Cetifica2", new Certifica2Command(), 
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			cetifica2.ensureDebugId("cetifica2");
			
			cetifica2PDF = addItem("Cetifica2 PDF", new Certifica2PDFCommand(), 
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			cetifica2PDF.ensureDebugId("cetifica2PDF");
			
			addSeparator();
			
			contractExtension = addItem("Pr\u00F3rroga Contrato", new ContractExtensionCommand(), 
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			contractExtension.ensureDebugId("contractExtension");
			
			contractTransform = addItem("Transformaci\u00F3n Contrato", new ContractTransformCommand(), 
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			contractTransform.ensureDebugId("contractTransform");
			
			removeContractExtension = addItem("Eliminar Pr\u00F3rroga Contrato", new DeleteContractExtensionCommand(), 
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			removeContractExtension.ensureDebugId("removeContractExtension");
			
			addSeparator();
			
			sendBasicCopy = addItem("Notificar Copia B\u00E1sica", new SendBasicCopyCommand(), 
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			sendBasicCopy.ensureDebugId("sendBasicCopy");
			
			sendContract = addItem("Notificar Contrato", new SendContractCommand(), 
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			sendContract.ensureDebugId("sendContract");
			
			sepeIDE = addItem("Ver IDE Contrato", new SepeIDEContractCommand(), 
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			sepeIDE.ensureDebugId("sepeIDE");
			
			removeContract = addItem("Eliminar Contrato", new RemoveContractCommand(), 
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			removeContract.ensureDebugId("removeContract");
			
		}

		public MenuItem getCto() {
			return cto;
		}

		public MenuItem getCbc() {
			return cbc;
		}
		
		public MenuItem getSendBasicCopy() {
			return sendBasicCopy;
		}
		
		public MenuItem getSendContract() {
			return sendContract;
		}
		
		public MenuItem getSepeIDE() {
			return sepeIDE;
		}
		
		public MenuItem getRemoveContract() {
			return removeContract;
		}
		
		public MenuItem getContractExtension() {
			return contractExtension;
		}
		
		public MenuItem getContractTransform() {
			return contractTransform;
		}
		
		public MenuItem getRemoveContractExtension() {
			return removeContractExtension;
		}
		
	}
	
	// ------------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String flex();
		String cmdBtn();
		String loadingPanel();
		String container();
	}
	
	@UiField (provided = true)
	AonToolbar toolbar;
	
	@UiField
	HTMLPanel messageContainer;
	
	@UiField
	HTMLPanel loadingPanel;
	
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
	EmployeeContractPayments employeeContractPayments;
	
	@UiField (provided = true)
	EmployeeContractIrpf employeeContractIrpf;
	
	@UiField (provided = true)
	EmployeeContractVariables employeeContractVariables;
	
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
	SimpleLayoutPanel scrolledPDFPanel;
	
	@UiField
	FullViewer pdfViewer;
	
	@UiField
	MinimizePanel footPanel;
	
	@UiField
	TabLayoutPanel footTabPanel;
	
	ResultsPanel resultsPanel;
	
	// ------------------------------------------------- Class variables
	
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private ContrataEmployeeObject contrataEmployeeObject;
	private Integer contractId;
	
	private AonToolbarButton listEmployees;
	private HTMLPanel employeeContractButtons;
	private AonToolbarButton saveContract;
	private AonToolbarButton deleteContract;
	private AonToolbarButton exportContract;
	private AonExpandButton tgss;
	private AonExpandButton sepe;
	private AonToolbarButton closePDF;
	private DateListBox idcDateListBox;
	private MonthListBox idcMonthListBox;
	
	private NewTGSSContextMenu tgssContextMenu;
	private NewSEPEContextMenu sepeContextMenu;
	
	// EmployeeSalary
	private HTMLPanel employeeSalaryButtons;
	
	// EmployeeCalendar
	private HTMLPanel employeeCalendarButtons;
	
	// EmployeeEvents
	private HTMLPanel employeeEventsButtons;
	
	// EmployeeContractPayments
	private HTMLPanel employeeContractPaymentsButtons;
	
	// EmployeeContractIrpf
	private HTMLPanel employeeContractIrpfButtons;
	
	// EmployeeContractVariables
	private HTMLPanel employeeContractVariablesButtons;
	
	// SalaryDraft
	private HTMLPanel salaryDraftButtos;
	
	private AonToolbarButton previusContract;
	private AonToolbarButton nextContract;
	private Label employeeCounter;
	
	private boolean hasCertificateSEPE = false;
	
	// ------------------------------------------------- Constructor
	
	protected ContrataEmployee() {
		// Init Tabs Elements
		contractEmployeeUI = new ContractEmployeeUIImpl();
		contractSpecificData = new ContractSpecificData();
		contractOtherData = new ContractOtherData();
		contractClauseUI = new ContractClauseUI();
		contractAttachUI = new ContractAttachUIImpl();
		contractBonusUI = new ContractBonusUI();
		
		employeeSalary = new EmployeeSalary();
		employeeSalary.hideToolbar();
		
		employeeCalendar = new EmployeeCalendarDraftNew();
		employeeCalendar.hideToolbar();
		employeeCalendar.setContrataEmployeeCalendarHeight();
		
		employeeEvents = new EmployeeEventsImpl();
		employeeEvents.hideToolbar();
		
		employeeContractPayments = new EmployeeContractPayments();
		employeeContractPayments.hideToolbar();
		
		employeeContractIrpf = new EmployeeIrpfImpl();
		employeeContractIrpf.hideToolbar();
		
		salaryDraft = new SalaryDraft();
		salaryDraft.hideToolbar();
		
		employeeContractVariables = new EmployeeContractVariables();
		employeeContractVariables.hideToolbar();
		
		// Init toolbar
		getToolbarPanel();
		
		// Init Widget
		initWidget(uiBinder.createAndBindUi(this));
		
		// Show contract buttons
		showContractButtons();
		
		// Init ContextMenu
		tgssContextMenu = new NewTGSSContextMenu();
		sepeContextMenu =  new NewSEPEContextMenu();
		
		// Init view
		initLoadingPanel();
		setScrollPanelsHeight();
		initFootPanel();
		initResultsPanel();
		initTabLayOutPanel();
		showEmployee();
	}
	
	private void checkBetaAlphaUser() {
		if(!getDomainUserRole().isBeta() && !getDomainUserRole().isAlpha())
			tabLayOutPanel.remove(12);
	}

	private void initLoadingPanel() {
		AonTableButton loadingBtn = new AonTableButton("", AON.CSS.aonIconRenew());
		loadingBtn.addStyleName(style.loadingPanel());
		
		Label loadingL = new Label("Cargando datos contrato ...");
		
		loadingPanel.add(loadingBtn);
		loadingPanel.add(loadingL);
		loadingPanel.setVisible(false);
	}
	
	private void showLoadingPanel(){
		Label loadingL = (Label) loadingPanel.getWidget(1);
		loadingL.setText(getLoadingText());
		
		loadingPanel.setVisible(true);
	}

	private void hideLoadingPanel(){
		loadingPanel.setVisible(false);
	}
	
	private String getLoadingText() {
		switch (tabLayOutPanel.getSelectedIndex()) {
		case 1:
			return "Cargando datos SEPE ...";
		case 2:
			return "Cargando datos contrato ...";
		case 3:
			return "Cargando clausulas ...";
		case 4:
			return "Cargando documentos ...";
		case 5:
			return "Cargando bonificaciones ...";
		case 6:
			return "Cargando n\u00F3minas ...";
		case 7:
			return "Cargando calendario ...";
		case 8:
			return "Cargando variables de calculo ...";
		case 9:
			return "Cargando conceptos de calculo ...";
		case 10:
			return "Cargando datos IRPF ...";
		case 11:
			return "Cargando borrador n\u00F3mina ...";
		case 12:
			return "Cargando variables contrato ...";
		default:
			return "Cargando datos afiliaci\u00F3n ...";
		}
	}
	
	// ------------------------------------------------- Initialize View
	
	private void setScrollPanelsHeight() {
		scrolledPanel.getElement().getStyle().setHeight(Window.getClientHeight() - 230.00, Unit.PX);
		scrolledPanelContractOtherData.getElement().getStyle().setHeight(Window.getClientHeight() - 250.00, Unit.PX);
		scrolledPanelClauses.getElement().getStyle().setHeight(Window.getClientHeight() - 230.00, Unit.PX);
		scrolledPanelAttach.getElement().getStyle().setHeight(Window.getClientHeight() - 230.00, Unit.PX);
		scrolledPanelBonus.getElement().getStyle().setHeight(Window.getClientHeight() - 230.00, Unit.PX);
		scrolledPanelContractSpecificData.getElement().getStyle().setHeight(Window.getClientHeight() - 230.00, Unit.PX);
		scrolledPDFPanel.getElement().getStyle().setHeight(Window.getClientHeight() - 170.00, Unit.PX);
	}
	
	private void initFootPanel() {
		footPanel.addMaximizeHandler(e -> showFootPanel());
		footPanel.addMinimizeHandler(e -> hideFootPanel());	
	}
	
	private void initResultsPanel () {
		resultsPanel = new ResultsPanel();		
	}
	
	private void initTabLayOutPanel() {
		tabLayOutPanel.selectTab(0, false);
		tabLayOutPanel.addStyleName(style.container());
//		tabLayOutPanel.setAnimationDuration(1000);
		
		tabLayOutPanel.addBeforeSelectionHandler(e -> {
			Integer itemIdx = tabLayOutPanel.getSelectedIndex();
			switch (itemIdx) {
			case 0:
				Map<String, String> messageMap = contractEmployeeUI.checkSaveAndGetErrors();
				if(messageMap.isEmpty())
					contrataEmployeeObject.setEmployeeContract(s -> {
						if(null == this.contrataEmployeeObject.getContractData().getEndDate())
							tgssContextMenu.getAfiEnd().getElement().getStyle().setDisplay(Display.NONE);
						else
							tgssContextMenu.getAfiEnd().getElement().getStyle().clearDisplay();
					}, f -> {});
				break;
			case 1:
				contrataEmployeeObject.setContractSpecificData(contractSpecificData.getContractSpecificData(), s -> {}, f -> {});
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
				showLoadingPanel();
				showContractButtons();
				contractEmployeeUI.setContrataEmployeeObject(this.contrataEmployeeObject, this.contrataEmployeeObject.getContractId(),
						success -> {
							// Check SS only if not RETA
							Byte ssRegime = contrataEmployeeObject.getContractData().getSsRegimen();
							if (null == ssRegime || ssRegime != 3) 
								checkStatus(this.contrataEmployeeObject);
							
							checkCertificateSEPE();
							checkTGSSStatus();
							checkContractExtension();
							checkContractTransform();
							hideLoadingPanel();
							
							if(AonStringUtils.isBlank(contrataEmployeeObject.getContractData().getSepeId()))
								sepeContextMenu.getSepeIDE().getElement().getStyle().setDisplay(Display.NONE);
							else
								sepeContextMenu.getSepeIDE().getElement().getStyle().clearDisplay();
						});
				break;
			case 1:
				showLoadingPanel();
				contrataEmployeeObject.getContractSpecificData(s -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showContractButtons();
					contractSpecificData.setEmployeeContractInfo(
							contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getContractType(), 
							contrataEmployeeObject.getContractEmployeeInfo().getContractSpecificData());
					hideLoadingPanel();
				}, f -> {});
				break;
			case 2:
				showLoadingPanel();
				contrataEmployeeObject.getContractOtherInfo(s -> {
					if(AonStringUtils.isBlank(contrataEmployeeObject.getFormativeLevel()))
						contrataEmployeeObject.getContractSpecificData(su -> {
							exportContract.getElement().getStyle().clearDisplay();
							showContractButtons();
							contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
							hideLoadingPanel();
						}, f -> {});
					else {
						exportContract.getElement().getStyle().clearDisplay();
						showContractButtons();
						contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
						hideLoadingPanel();
					}
				}, f -> {});
				break;
			case 3:
				showLoadingPanel();
				contrataEmployeeObject.getContractClauses(s -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showContractButtons();
					contractClauseUI.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
					hideLoadingPanel();
				}, f -> {});
				break;
			case 4:
				showLoadingPanel();
				contrataEmployeeObject.getContractAttachments(s -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showContractButtons();
					contractAttachUI.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
					hideLoadingPanel();
				}, f -> {});
				break;
			case 5:
				showLoadingPanel();
				getContractBonus(s -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showContractButtons();
					contractBonusUI.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
					hideLoadingPanel();
				}, f -> {});
				break;
			case 6:
				showLoadingPanel();
				contrataEmployeeObject.getEmployeeSalaryObject(employeeSalaryObject -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showSalariesButtons();
					employeeSalary.setEmployeeSalaryObject(employeeSalaryObject);
					employeeSalary.removeMainMT();
					hideLoadingPanel();
				});
				break;
			case 7:
				showLoadingPanel();
				contrataEmployeeObject.getEmployeeCalendarObject(employeeCalendarObject -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showCalendarButtons();
					employeeCalendar.setEmployeeCalendarDraftObject(employeeCalendarObject);
					hideLoadingPanel();
				});
				break;
			case 8:
				showLoadingPanel();
				contrataEmployeeObject.getEmployeeEventsObject(employeeEventsObject -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showEventsButtons();
					employeeEvents.setEmployeeEventsDraftObject(employeeEventsObject);
					hideLoadingPanel();
				});
				break;
			case 9:
				showLoadingPanel();
				contrataEmployeeObject.getEmployeeContractPaymentsObject(employeeContractPaymentsObject -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showContractPaymentsButtons();
					employeeContractPayments.setEmployeeContractPaymentsObject(employeeContractPaymentsObject);
					hideLoadingPanel();
				});
				break;
			case 10:
				showLoadingPanel();
				contrataEmployeeObject.getEmployeeContractIrpfObject(employeeContractIrpfObject -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showContractIrpfButtons();
					employeeContractIrpf.setEmployeeContractIrpfObject(employeeContractIrpfObject);
					hideLoadingPanel();
				});
				break;
			case 11:
				showLoadingPanel();
				contrataEmployeeObject.getSalaryDraftObject(salaryDraftObject -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showSalaryDraftButtons();
					salaryDraft.setSalaryDraftObject(salaryDraftObject);
					hideLoadingPanel();
				}, f -> {});
				break;
			case 12:
				showLoadingPanel();
				contrataEmployeeObject.getEmployeeContractVariablesObject(employeeContractVariablesObject -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showContractVariablesButtons();
					employeeContractVariables.setEmployeeContractVariablesObject(employeeContractVariablesObject);
					hideLoadingPanel();
				});
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
		previusContract.setVisible(true);
		employeeCounter.setVisible(true);
		nextContract.setVisible(true);
		tgss.setVisible(true);
		sepe.setVisible(true);
		
		closePDF.setVisible(false);
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
		previusContract.setVisible(false);
		employeeCounter.setVisible(false);
		nextContract.setVisible(false);

		closePDF.setVisible(true);

		tabLayOutPanel.getElement().getStyle().setDisplay(Display.NONE);
		pdfViewer.getElement().getStyle().clearDisplay();
	}
	
	// ------------------------------------------------- Initialize View (Auxiliar Method)
	
	protected void getContractBonus(Consumer<List<SSBonusData>> success, Consumer<Throwable> failure) {
		contrataEmployeeObject.getSSBonus(success, failure);
	}
	
	// ------------------------------------------------- setContrataEmployeeObject
	
	public void setContrataEmployeeObject(ContrataEmployeeObject contrataEmployeeDialogObject, Integer contractId, Integer selectedEmployeeIdx, int employeesSize, Consumer<String> success) {
		this.contractId = contractId;
		this.contrataEmployeeObject = contrataEmployeeDialogObject;
    	contractEmployeeUI.setContrataEmployeeObject(contrataEmployeeDialogObject, contractId,
				s -> {
					employeeCounter.setText(selectedEmployeeIdx + " de " + employeesSize);
					showContractButtons();
					
					// Check SS only if not RETA
					Byte ssRegime = contrataEmployeeObject.getContractData().getSsRegimen();
					if (null == ssRegime || ssRegime != 3) 
						checkStatus(this.contrataEmployeeObject);
					
					checkCertificateSEPE();
					checkTGSSStatus();
					checkContractExtension();
					checkContractTransform();
					
					if(AonStringUtils.isBlank(contrataEmployeeObject.getContractData().getSepeId()))
						sepeContextMenu.getSepeIDE().getElement().getStyle().setDisplay(Display.NONE);
					else
						sepeContextMenu.getSepeIDE().getElement().getStyle().clearDisplay();
					
					checkBetaAlphaUser();
					
					success.accept("");
				});
	}
	
	public void setContrataEmployeeObject(ContrataEmployeeObject contrataEmployeeDialogObject, Integer contractId, Integer selectedEmployeeIdx, int employeesSize, int selectedTab, Consumer<String> success) {
		this.contractId = contractId;
		this.contrataEmployeeObject = contrataEmployeeDialogObject;
		contractEmployeeUI.setContrataEmployeeObject(contrataEmployeeDialogObject, contractId,
				s -> {
					employeeCounter.setText(selectedEmployeeIdx + " de " + employeesSize);
					tabLayOutPanel.selectTab(selectedTab, true);
					checkBetaAlphaUser();
					success.accept("");
				});
	}
	
	// ------------------------------------------------- Show/Hide Toolbar methods
	
	private void showSalariesButtons() {
		employeeSalaryButtons.setVisible(true);
		employeeContractButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeEventsButtons.setVisible(false);
		employeeContractPaymentsButtons.setVisible(false);
		salaryDraftButtos.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
		employeeContractVariablesButtons.setVisible(false);
	}
	
	private void showContractButtons() {
		employeeContractButtons.setVisible(true);
		employeeSalaryButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeEventsButtons.setVisible(false);
		employeeContractPaymentsButtons.setVisible(false);
		salaryDraftButtos.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
		employeeContractVariablesButtons.setVisible(false);
	}
	
	private void showCalendarButtons() {
		employeeCalendarButtons.setVisible(true);
		employeeContractButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeEventsButtons.setVisible(false);
		employeeContractPaymentsButtons.setVisible(false);
		salaryDraftButtos.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
		employeeContractVariablesButtons.setVisible(false);
	}
	
	private void showEventsButtons() {
		employeeEventsButtons.setVisible(true);
		employeeContractPaymentsButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeContractButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		salaryDraftButtos.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
		employeeContractVariablesButtons.setVisible(false);
	}
	
	private void showContractPaymentsButtons() {
		employeeContractPaymentsButtons.setVisible(true);
		employeeEventsButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeContractButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		salaryDraftButtos.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
		employeeContractVariablesButtons.setVisible(false);
	}
	
	private void showContractIrpfButtons() {
		employeeContractIrpfButtons.setVisible(true);
		employeeContractPaymentsButtons.setVisible(false);
		employeeEventsButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeContractButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		salaryDraftButtos.setVisible(false);
		employeeContractVariablesButtons.setVisible(false);
	}
	
	private void showSalaryDraftButtons() {
		salaryDraftButtos.setVisible(true);
		employeeEventsButtons.setVisible(false);
		employeeContractPaymentsButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeContractButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
		employeeContractVariablesButtons.setVisible(false);
	}
	
	private void showContractVariablesButtons() {
		employeeContractVariablesButtons.setVisible(true);
		employeeEventsButtons.setVisible(false);
		employeeContractPaymentsButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeContractButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
		salaryDraftButtos.setVisible(false);
	}
	
	// ------------------------------------------------- Abstract methods
	
	protected abstract void onListShow(boolean reloadEmployees);
	protected abstract void onPreviusContract(Integer contractId);
	protected abstract void onNextContract(Integer contractId);
	protected abstract void onTransformContract(Integer newContractId);
	protected abstract DomainUserRoles getDomainUserRole();
	
	// ------------------------------------------------- Toolbar panel
	
	private AonToolbar getToolbarPanel() {
		this.toolbar = new AonToolbar("Contrato");
		
		listEmployees = new AonToolbarButton( "Volver a contratos", AON.CSS.aonIconBack() );
		listEmployees.addClickHandler(e -> onListEmployees());
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
		
		// EmployeePayments
		
		employeeContractPaymentsButtons = initEmployeeContractPaymentsButtons();
		toolbar.add(employeeContractPaymentsButtons);
		
		// EmployeeIrpf
		
		employeeContractIrpfButtons = initEmployeeContractIrpfButtons();
		toolbar.add(employeeContractIrpfButtons);
		
		// SalaryDrat
		
		salaryDraftButtos = initSalaryDraftButtons();
		toolbar.add(salaryDraftButtos);
		
		// EmployeeContractVariables
		
		employeeContractVariablesButtons = initEmployeeContractVariablesButtons();
		toolbar.add(employeeContractVariablesButtons);
		
		previusContract = new AonToolbarButton("Contrato anterior", AON.CSS.aonIconLeft());
		previusContract.addClickHandler(e -> onPreviusContract(contractId));
		toolbar.add(previusContract);
		
		employeeCounter = new Label();
		toolbar.add(employeeCounter);
		
		nextContract = new AonToolbarButton("Contrato siguiente", AON.CSS.aonIconRight());
		nextContract.addClickHandler(e -> onNextContract(contractId));
		toolbar.add(nextContract);
		
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
		saveContract.addClickHandler(e -> onSaveContract());
		hPanel.add(saveContract);
		
		deleteContract = new AonToolbarButton( AON.MSG.deleteAction() + " Contrato", AON.CSS.aonIconDelete() );
		deleteContract.addClickHandler(e -> onDeleteContract());
		hPanel.add(deleteContract);
		
		exportContract = new AonToolbarButton( AON.MSG.export() + "Contrato", AON.CSS.aonIconPdf() );
		exportContract.addClickHandler(e -> onExportContract());
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
		idcMonthListBox.addChangeHandler(e -> showIdcPlNss(idcMonthListBox.getSelectedMonth()));
		hPanel.add(idcMonthListBox);
		
		idcDateListBox = new DateListBox();
		idcDateListBox.addChangeHandler(e -> showIdc(idcDateListBox.getSelectedDate()));
		hPanel.add(idcDateListBox);
		
		closePDF = new AonToolbarButton( AON.MSG.closed(), AON.CSS.aonIconClose() );
		closePDF.addClickHandler(e -> onClosePDF());
		hPanel.add(closePDF);
		
		return hPanel;
	}
	
	// ------------------------------------------------- EmployeeContractButtons (Auxiliar methods)
	
	private void onSaveContract() {
		
		Integer itemIdx = tabLayOutPanel.getSelectedIndex();

		switch (itemIdx) {
		case 0:
			Map<String, String> messageMap = contractEmployeeUI.checkSaveAndGetErrors();
			if(messageMap.isEmpty())
				contrataEmployeeObject.setEmployeeContract(s -> {
					Map<String, String> messageSuccessMap = new HashMap<>();
					messageSuccessMap.put("Guardado", "El contrato " + contrataEmployeeObject.getEmployeeFullName() + " ha sido actualizado correctamente");
					AonMessagePanel.showSuccess(messageContainer, messageSuccessMap);
					
					if(null == this.contrataEmployeeObject.getContractData().getEndDate())
						tgssContextMenu.getAfiEnd().getElement().getStyle().setDisplay(Display.NONE);
					else
						tgssContextMenu.getAfiEnd().getElement().getStyle().clearDisplay();
				}, f -> {});
			else
				AonMessagePanel.showError(messageContainer, messageMap);
			break;
		case 1:
			contrataEmployeeObject.setContractSpecificData(contractSpecificData.getContractSpecificData(), s -> 
				contrataEmployeeObject.getContractSpecificData(su -> 
					contractSpecificData.setEmployeeContractInfo(
							contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getContractType(), 
							contrataEmployeeObject.getContractEmployeeInfo().getContractSpecificData())
				, fa -> {})
			, f -> {});
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
			public void onCancel() {
				// Nothing to do here, only hide dialog
			}
			
			@Override
			public void onAccept() {
				contrataEmployeeObject.delete4EverContract(s -> onListShow(true), f-> {});
//				contrataEmployeeObject.deleteContract(s -> {
//					onListShow(true);
//				}, f-> {});
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
		showLoading("Borrando movimiento previo...");
		contrataEmployeeObject.movPrevDelete(
				s -> {
					hideMessage();
					contractEmployeeUI.setContrataEmployeeObject(contrataEmployeeObject, this.contrataEmployeeObject.getContractId(),
						success -> {
							// Check SS only if not RETA
							Byte ssRegime = contrataEmployeeObject.getContractData().getSsRegimen();
							if (null == ssRegime || ssRegime != 3) 
								checkStatus(this.contrataEmployeeObject);
							
							checkCertificateSEPE();
							checkTGSSStatus();
							checkContractExtension();
							checkContractTransform();
						});
				}, f -> showError("Error borrado movimiento previo", f.getMessage())
		);
	}

	private void altaConsolidadaDelete() {
		showLoading("Borrando alta consolidad...");
		contrataEmployeeObject.altaConsolidadaDelete(
				s -> {
					hideMessage();
					contractEmployeeUI.setContrataEmployeeObject(
						contrataEmployeeObject, 
						this.contrataEmployeeObject.getContractId(),
						success -> {
							// Check SS only if not RETA
							Byte ssRegime = contrataEmployeeObject.getContractData().getSsRegimen();
							if (null == ssRegime || ssRegime != 3) 
								checkStatus(this.contrataEmployeeObject);
							
							checkCertificateSEPE();
							checkTGSSStatus();
							checkContractExtension();
							checkContractTransform();
						});
				}, f -> showError("Error borrado alta consolidada", f.getMessage())
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
				this.contrataEmployeeObject.getContractData().getContractId(),
				this.contrataEmployeeObject.getEmployeeData().getDomain(),
				this.contrataEmployeeObject.getContractData().getWorkplaceId(),
				false
				){

					@Override
					protected void onAcceptCB() {
						contractEmployeeUI.setContrataEmployeeObject(contrataEmployeeObject, contrataEmployeeObject.getContractId(),
							success -> {
								// Check SS only if not RETA
								Byte ssRegime = contrataEmployeeObject.getContractData().getSsRegimen();
								if (null == ssRegime || ssRegime != 3) 
									checkStatus(contrataEmployeeObject);
								
								checkCertificateSEPE();
								checkTGSSStatus();
								checkContractExtension();
								checkContractTransform();
							});
					}
			
					@Override
					protected void onPartialityCoefContract(String partialityCoef, Date date) {
						contrataEmployeeObject.cambioCoef(partialityCoef, date, 
								s -> showSuccess("AVISO: Parcialidad", "El coeficiente de parcialidad ha sido notificado a la Seguridad Social."), 
								f -> showError("Error comunicaci\u00F3n", f.getMessage()));
					}

					@Override
					protected void onOcupationContract(String ocupation, Date date) {
						contrataEmployeeObject.cambioOcupacion(ocupation, date,
							s -> showSuccess("AVISO: Ocupaci\u00F3n", "El cambio de ocupaci\u00F3n ha sido notificado a la Seguridad Social."), 
							f -> showError("Error comunicaci\u00F3n", f.getMessage()));
					}

					@Override
					protected void onQuoteContract(String quoteGroup, Date date) {
						contrataEmployeeObject.cambioGrupCtz(quoteGroup, date, 
							s -> showSuccess("AVISO: Grupo cotizaci\u00F3n", "El cambio de grupo de cotizaci\u00F3n ha sido notificado a la Seguridad Social."), 
							f -> showError("Error comunicaci\u00F3n", f.getMessage()));								
					}

					@Override
					protected void onChangeContract(String contract, Date date) {
						contrataEmployeeObject.cambioContrato(contract, date,
							s -> showSuccess("AVISO: Tipo contrato", "El cambio de tipo de contrato ha sido notificado a la Seguridad Social."), 
							f -> showError("Error comunicaci\u00F3n", f.getMessage()));
					}

					@Override
					protected void onEndContract(String settleReason) {
						contrataEmployeeObject.sendEmployeeBaja(settleReason,
							s -> showSuccess("AVISO: Baja", "La baja de este trabajador ha sido notificada a la Seguridad Social."), 
							f -> showError("Error comunicaci\u00F3n", f.getMessage()));
					}

					@Override
					protected void onStartContract() {
						contrataEmployeeObject.sendEmployeeAlta(
							s -> {
								showSuccess("AVISO: Alta", "El alta de este trabajador ha sido notificado a la Seguridad Social.");
								downloadTAAndIDC();
							}, f -> showError("Error comunicaci\u00F3n", f.getMessage()));
					}
				};
	}
	
	private void onAFIEndChanges() {
		new EmployeeAFIDialog(
				contractEmployeeUI.getStartDate(),
				contractEmployeeUI.getEndDate(),
				contractEmployeeUI.getContractType(),
				contractEmployeeUI.getQuoteGroup(),
				contractEmployeeUI.getOccupation(),
				contractEmployeeUI.getPartialityCoef(),
				this.contrataEmployeeObject.getContractData().getContractId(),
				this.contrataEmployeeObject.getEmployeeData().getDomain(),
				this.contrataEmployeeObject.getContractData().getWorkplaceId(),
				true
				){

					@Override
					protected void onAcceptCB() {
						// Nothing to do here
					}
			
					@Override
					protected void onPartialityCoefContract(String partialityCoef, Date date) {
						// Nothing to do here
					}

					@Override
					protected void onOcupationContract(String ocupation, Date date) {
						// Nothing to do here
					}

					@Override
					protected void onQuoteContract(String quoteGroup, Date date) {
						// Nothing to do here
					}

					@Override
					protected void onChangeContract(String contract, Date date) {
						// Nothing to do here
					}

					@Override
					protected void onEndContract(String settleReason) {
						contrataEmployeeObject.sendEmployeeBaja(settleReason, 
							s -> showSuccess("AVISO: Baja", "La baja de este trabajador ha sido notificada a la Seguridad Social."), 
							f -> showError("Error comunicaci\u00F3n", f.getMessage()));
					}

					@Override
					protected void onStartContract() {
						// Nothing to do here
					}
				};
	}
	
	private void downloadTAAndIDC() {
		contrataEmployeeObject.downloadTAAndIDC(
				s -> showSuccess("IDC y TA", "Se han descargado el IDC y el TA del trabajador. Ambos documentos se encuentran en el apartado de Adjuntos"),
				f -> showError("Error comunicaci\u00F3n", f.getMessage())
		);
	}

	private void showTa() {
		showLoading("Obteniendo TA...");
		contrataEmployeeObject.downloadTa(dataURI -> {
				hideMessage();
				showPdf();
				pdfViewer.open(dataURI);
		}, f -> showError("Error TA", f.getMessage()));
	}
	
	private void showIdc() {
		showIdc(idcDateListBox.getSelected());
	}

	private void showIdc( Date date) {
		showLoading("Obteniendo IDC...");
		contrataEmployeeObject.downloadIdc(date,
		dataURI -> {
				hideMessage();
				showPdf();
				idcDateListBox.setVisible(true);
				idcDateListBox.setSelected(date, true);
				pdfViewer.open(dataURI);
		}, f -> showError("Error IDC", f.getMessage()));
	}

	private void showIdcPlNss() {
		showIdcPlNss(DateUtils.getFirstDayOfMonth());
	}
	
	private void showIdcPlNss( Date month) {
		showLoading("Obteniendo IDC PL NSS...");
		contrataEmployeeObject.downloadIdcPlNss(month,
		dataURI -> {
				hideMessage();
				showPdf();
				idcMonthListBox.setVisible(true);
				idcMonthListBox.setSelected(month, true);
				pdfViewer.open(dataURI);
		}, f -> showError("Error IDC PL NSS", f.getMessage()));
	}

	private void onCTO() {
		showCto();
	}
	
	private void showCto() {
		showLoading("Obteniendo CTO...");
		contrataEmployeeObject.downloadCto(dataURI -> {
				hideMessage();
				showPdf();
				pdfViewer.open(dataURI);
		}, f -> showError("Error CTO", f.getMessage()));
	}

	private void onCBC() {
		showCbc();
	}
	
	private void showCbc() {
		showLoading("Obteniendo CBC...");
		contrataEmployeeObject.downloadCbc(dataURI -> {
				hideMessage();
				showPdf();
				pdfViewer.open(dataURI);
		}, f -> showError("Error CBC", f.getMessage()));
	}
	
	private void showCertifica2PDF() {
		showLoading("Obteniendo Certific@2...");
		contrataEmployeeObject.getCertifica2PDF(dataURI -> {
			hideMessage();
			showPdf();
			pdfViewer.open(dataURI);
		}, f -> showError("Error Certific@2", f.getMessage()));
	}
	
	private void onClosePDF() {
		showEmployee();
	}
	
	private void sendBasicCopy() {
		showLoading("Notificando copia basica...");
		contrataEmployeeObject.sendBasicCopy(
				s -> {
					showSuccess("Comunicaci\u00F3n", "La copia basica ha sido notificada correctamente del SEPE");
					
					contractEmployeeUI.setContrataEmployeeObject(contrataEmployeeObject, this.contrataEmployeeObject.getContractId(),
						success -> {
							// Check SS only if not RETA
							Byte ssRegime = contrataEmployeeObject.getContractData().getSsRegimen();
							if (null == ssRegime || ssRegime != 3) 
								checkStatus(this.contrataEmployeeObject);
							
							checkCertificateSEPE();
							checkTGSSStatus();
							checkContractExtension();
							checkContractTransform();
						});
				},
				f -> showError("Error comunicaci\u00F3n", f.getMessage()));
	}

	private void sendContract() {
		showLoading("Notificando contrato...");
		contrataEmployeeObject.sendContract(
				s -> {
					showSuccess("Comunicaci\u00F3n", "El contrato ha sido notificado correctamente del SEPE");
					
					contractEmployeeUI.setContrataEmployeeObject(contrataEmployeeObject, this.contrataEmployeeObject.getContractId(),
						success -> {
							// Check SS only if not RETA
							Byte ssRegime = contrataEmployeeObject.getContractData().getSsRegimen();
							if (null == ssRegime || ssRegime != 3) 
								checkStatus(this.contrataEmployeeObject);
							
							checkCertificateSEPE();
							checkTGSSStatus();
							checkContractExtension();
							checkContractTransform();
							
							if(AonStringUtils.isBlank(contrataEmployeeObject.getContractData().getSepeId()))
								sepeContextMenu.getSepeIDE().getElement().getStyle().setDisplay(Display.NONE);
							else
								sepeContextMenu.getSepeIDE().getElement().getStyle().clearDisplay();
					});
				},
				f -> showError("Error comunicaci\u00F3n", f.getMessage()));
	}
	
	private void sepeIDEContract() {
		showInfo("IDE Sepe", "El IDE del SEPE generado para este contrato es " + contrataEmployeeObject.getContractData().getSepeId());
	}
	
	private void removeContract() {
		showLoading("Eliminando contrato...");
		contrataEmployeeObject.removeContract(
				s -> {
					showSuccess("Comunicaci\u00F3n", "El contrato ha sido eliminado correctamente del SEPE");
					
					contractEmployeeUI.setContrataEmployeeObject(contrataEmployeeObject, this.contrataEmployeeObject.getContractId(),
						success -> {
							// Check SS only if not RETA
							Byte ssRegime = contrataEmployeeObject.getContractData().getSsRegimen();
							if (null == ssRegime || ssRegime != 3) 
								checkStatus(this.contrataEmployeeObject);
							
							checkCertificateSEPE();
							checkTGSSStatus();
							checkContractExtension();
							checkContractTransform();
							
							if(AonStringUtils.isBlank(contrataEmployeeObject.getContractData().getSepeId()))
								sepeContextMenu.getSepeIDE().getElement().getStyle().setDisplay(Display.NONE);
							else
								sepeContextMenu.getSepeIDE().getElement().getStyle().clearDisplay();
						});
				},	
				f -> showError("Error comunicaci\u00F3n", f.getMessage()));
	}
	
	private void contractExtension() {
		new ContractExtensionDialog(this.contrataEmployeeObject.getContractEmployeeInfo()) {
			@Override
			protected void onExtensionDone() {
				showSuccess("Pr\u00F3rroga", "La pr\u00F3rroga del trabajador " + contrataEmployeeObject.getEmployeeFullName() + " ha sido realizada correctamente");
				
				contractEmployeeUI.setContrataEmployeeObject(contrataEmployeeObject, contrataEmployeeObject.getContractId(),
						success -> {
							// Check SS only if not RETA
							Byte ssRegime = contrataEmployeeObject.getContractData().getSsRegimen();
							if (null == ssRegime || ssRegime != 3) 
								checkStatus(contrataEmployeeObject);
							
							checkCertificateSEPE();
							checkTGSSStatus();
							checkContractExtension();
							checkContractTransform();
						});
			}

			@Override
			protected void fireError(Map<String, String> errorMap) {
				AonMessagePanel.showError(messageContainer, errorMap);
			}
		};
	}
	
	private void contractTransform() {
		new ContractTransformDialog(this.contrataEmployeeObject.getContractEmployeeInfo()) {
			@Override
			protected void onTransformDone(Integer newContractId) {
				showSuccess("Pr\u00F3rroga", "La transformaci\u00F3n del trabajador " + contrataEmployeeObject.getEmployeeFullName() + " ha sido realizada correctamente");
				onTransformContract(newContractId);
			}

			@Override
			protected void fireError(Map<String, String> errorMap) {
				AonMessagePanel.showError(messageContainer, errorMap);
			}
		};
	}
	
	private void deleteContractExtension() {
		contrataEmployeeObject.deleteContractExtension(
				s -> {
					showSuccess("Borrado Pr\u00F3rroga", "La pr\u00F3rroga del trabajador " + contrataEmployeeObject.getEmployeeFullName() + " ha sido eliminada correctamente");
					
					contractEmployeeUI.setContrataEmployeeObject(contrataEmployeeObject, contrataEmployeeObject.getContractId(),
							success -> {
								// Check SS only if not RETA
								Byte ssRegime = contrataEmployeeObject.getContractData().getSsRegimen();
								if (null == ssRegime || ssRegime != 3) 
									checkStatus(this.contrataEmployeeObject);
								
								checkCertificateSEPE();
								checkTGSSStatus();
								checkContractExtension();
								checkContractTransform();
							});
				},
				f -> {}
		);
	}
	
	// ------------------------------------------------- EmployeeSalaryButtons
	
	private HTMLPanel initEmployeeSalaryButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());
		
		AonToolbarButton deleteButton = new AonToolbarButton( "Borrar N\u00F3mina", AON.CSS.aonIconDeleteList() );
		deleteButton.addClickHandler(e -> employeeSalary.onDelete());
		hPanel.add(deleteButton);
		
		AonToolbarButton pdfButton = new AonToolbarButton( AON.MSG.printPDF() + " N\u00F3mina", AON.CSS.aonIconPdf());
		pdfButton.addClickHandler(e -> employeeSalary.onPDF());	
		hPanel.add(pdfButton);
		
		AonToolbarButton pdfSettleButton = new AonToolbarButton( "Carta Finiquito", AON.CSS.aonIconPdf());
		pdfSettleButton.addClickHandler(e -> employeeSalary.onPDFSettle());	
		pdfSettleButton.setVisible(false);
		hPanel.add(pdfSettleButton);
		
		AonToolbarButton publishButton = new AonToolbarButton( "Drive", AON.CSS.aonIconDrive());
		publishButton.addClickHandler(e -> employeeSalary.onPublish());	
		hPanel.add(publishButton);
		
		AonToolbarButton bidoqPublishButton = new AonToolbarButton( "Bidow", "aon-icon-bidoq");
		bidoqPublishButton.addClickHandler(e -> employeeSalary.onBidoqPublish());	
		bidoqPublishButton.setVisible(false);
		hPanel.add(bidoqPublishButton);
		
		AonToolbarButton email = new AonToolbarButton(AON.MSG.email() +  " N\u00F3mina", AON.CSS.aonIconEmail());
		email.addClickHandler(e -> employeeSalary.onEmail(e));	
		hPanel.add(email);
		
		return hPanel;
	}
	
	// ------------------------------------------------- EmployeeCalendarButtons
	
	private HTMLPanel initEmployeeCalendarButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());
		
		AonToolbarButton undoAllButton = new AonToolbarButton( "Deshacer todo", AON.CSS.aonIconUndoAll());
		undoAllButton.addClickHandler(e -> employeeCalendar.onUndoAll());
		hPanel.add(undoAllButton);
		
		AonToolbarButton saveButton = new AonToolbarButton( AON.MSG.saveAction() + " Calendario", AON.CSS.aonIconSave() );
		saveButton.addClickHandler(e -> employeeCalendar.onSave());
		hPanel.add(saveButton);
		
		AonToolbarButton definitionButton = new AonToolbarButton( "Definicion", AON.CSS.aonIconEditCalendar() );
		definitionButton.addClickHandler(e -> employeeCalendar.onDefinition(e));
		hPanel.add(definitionButton);
		
		AonToolbarButton utilityButton = new AonToolbarButton( "Utilidades", AON.CSS.aonIconSettings() );
		utilityButton.addClickHandler(e -> employeeCalendar.onUtility(e));
		hPanel.add(utilityButton);
		
		ListBox yearLB = new ListBox();
		employeeCalendar.initializeYearLB(yearLB);
		employeeCalendar.setYearLB(yearLB);
		hPanel.add(yearLB);
		
		return hPanel;
	}
	
	// ------------------------------------------------- EmployeeEventsButtons
	
	private HTMLPanel initEmployeeEventsButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());
		
		AonToolbarButton undoAllEventsButton = new AonToolbarButton( "Restaurar últimos valores guardados", AON.CSS.aonIconUndo() );
		undoAllEventsButton.addClickHandler(e -> employeeEvents.onUndo());
		hPanel.add(undoAllEventsButton);
		
		AonToolbarButton saveEventsButton = new AonToolbarButton( AON.MSG.saveAction() + " Incidencias", AON.CSS.aonIconSave() );
		saveEventsButton.addClickHandler(e -> employeeEvents.onSave());
		hPanel.add(saveEventsButton);
		
		AonToolbarButton newValueButton = new AonToolbarButton( "Nuevo valor", AON.CSS.aonIconAdd() );
		newValueButton.addClickHandler(e -> employeeEvents.onNewValue());
		hPanel.add(newValueButton);
		
		AonToolbarButton visibilityButton = new AonToolbarButton( "Visualizaci\u00F3n", AON.CSS.aonIconVisibility() );
		visibilityButton.addClickHandler(e -> employeeEvents.onVisibility(e));
		hPanel.add(visibilityButton);
		
		ListBox yearLBEvents = new ListBox();
		employeeEvents.initializeYearLB(yearLBEvents);
		employeeEvents.setYearLB(yearLBEvents);
		hPanel.add(yearLBEvents);
		
		return hPanel;
	}
	
	// ------------------------------------------------- EmployeeContractPaymentsButtons
	
	private HTMLPanel initEmployeeContractPaymentsButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());
		
		AonToolbarButton saveContractPaymentsButton = new AonToolbarButton( AON.MSG.saveAction() + " Conceptos Calculo", AON.CSS.aonIconSave() );
		saveContractPaymentsButton.addClickHandler(e -> employeeContractPayments.onSave());
		hPanel.add(saveContractPaymentsButton);
		
		AonToolbarButton addContractPaymentsButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		addContractPaymentsButton.addClickHandler(e -> employeeContractPayments.openEditor());
		hPanel.add(addContractPaymentsButton);
		
		ListBox yearLBContractPayments = new ListBox();
		employeeContractPayments.initializeYearLB(yearLBContractPayments);
		employeeContractPayments.setYearLB(yearLBContractPayments);
		hPanel.add(yearLBContractPayments);
		
		return hPanel;
	}
	
	// ------------------------------------------------- EmployeeContractIrpfButtons
	
	private HTMLPanel initEmployeeContractIrpfButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());
		
		AonToolbarButton saveContractIrpfButton = new AonToolbarButton( AON.MSG.saveAction() + " Irpf", AON.CSS.aonIconSave() );
		saveContractIrpfButton.addClickHandler(e -> employeeContractIrpf.onSave());
		hPanel.add(saveContractIrpfButton);
		
		ListBox yearLBContractIrpf = new ListBox();
		employeeContractIrpf.setYearLB(yearLBContractIrpf);
		hPanel.add(yearLBContractIrpf);
		
		return hPanel;
	}
	
	// ------------------------------------------------- EmployeeSalaryButtons
	
	private HTMLPanel initSalaryDraftButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());
		
		AonToolbarButton undoSalaryAllButton = new AonToolbarButton( "Deshacer todo", AON.CSS.aonIconUndoAll() );
		undoSalaryAllButton.addClickHandler(e -> salaryDraft.onUndoAll());	
		undoSalaryAllButton.ensureDebugId("undoAllButton");
		salaryDraft.setUndoAllButton(undoSalaryAllButton);
		hPanel.add(undoSalaryAllButton);
		
		AonToolbarButton undoButton = new AonToolbarButton( "Deshacer", AON.CSS.aonIconUndo() );
		undoButton.addClickHandler(e -> salaryDraft.onUndo());	
		undoButton.ensureDebugId("undoButton");
		salaryDraft.setUndoButton(undoButton);
		hPanel.add(undoButton);
		
		AonToolbarButton redoButton = new AonToolbarButton( "Rehacer", AON.CSS.aonIconRedo() );
		redoButton.addClickHandler(e -> salaryDraft.onRedo());	
		redoButton.ensureDebugId("redoButton");
		salaryDraft.setRedoButton(redoButton);
		hPanel.add(redoButton);
		
		AonToolbarButton acceptButton = new AonToolbarButton( AON.MSG.saveAction() + " Borrador", AON.CSS.aonIconSave() );
		acceptButton.addClickHandler(e -> salaryDraft.onAccept());	
		acceptButton.ensureDebugId("acceptButton");
		acceptButton.setEnabled(false);
		salaryDraft.setAcceptButton(acceptButton);
		hPanel.add(acceptButton);
		
		AonToolbarButton salaryButton = new AonToolbarButton( "Emitir nomina", AON.CSS.aonIconEmit() );
		salaryButton.addClickHandler(e -> salaryDraft.onSalary());	
		salaryButton.ensureDebugId("salaryButton");
		salaryDraft.setSalaryButton(salaryButton);
		hPanel.add(salaryButton);

		AonToolbarButton settleButton = new AonToolbarButton( "Emitir finiquito", AON.CSS.aonIconAccept() );
		settleButton.addClickHandler(e -> salaryDraft.onSettle());	
		settleButton.ensureDebugId("settleButton");
		settleButton.setVisible(false);
		salaryDraft.setSettleButton(settleButton);
		hPanel.add(settleButton);
		
		AonToolbarButton extraButton = new AonToolbarButton( "Emitir extra", AON.CSS.aonIconAccept() );
		extraButton.addClickHandler(e -> salaryDraft.onExtra());	
		extraButton.ensureDebugId("extraButton");
		extraButton.setVisible(false);
		salaryDraft.setExtraButton(extraButton);
		hPanel.add(extraButton);

		AonToolbarButton fxButton = new AonToolbarButton( "FX", AON.CSS.aonIconFx() );
		fxButton.addClickHandler(e -> salaryDraft.onFx());	
		fxButton.ensureDebugId("fxButton");
		fxButton.setEnabled(false);
		salaryDraft.setFxButton(fxButton);
		hPanel.add(fxButton);
		
		CheckBox tgssCheck = new CheckBox("SILTRA"); 
		tgssCheck.addValueChangeHandler(e -> salaryDraft.onTgssCheckChange());
		tgssCheck.ensureDebugId("tgssCheck");
		tgssCheck.setValue(false);
		salaryDraft.setTgssCheck(tgssCheck);
		hPanel.add(tgssCheck);
		
		CheckBox costsCheck = new CheckBox("COSTES Y BONIF."); 
		costsCheck.addValueChangeHandler(e -> salaryDraft.onCostsCheck2Change());
		costsCheck.ensureDebugId("costsCheck");
		costsCheck.setValue(false);
		salaryDraft.setCostsCheck(costsCheck);
		hPanel.add(costsCheck);
		
		CheckBox dbSalaryCheck = new CheckBox("DIFERENCIAS"); 
		dbSalaryCheck.addValueChangeHandler(e -> salaryDraft.onDbSalaryCheckChange(e));
		dbSalaryCheck.ensureDebugId("dbSalaryCheck");
		dbSalaryCheck.setValue(false);
		salaryDraft.setDBSalaryCheck(dbSalaryCheck);
		hPanel.add(dbSalaryCheck);
		
		CheckBox eventsCheck = new CheckBox("AVISOS Y NOTIF."); 
		eventsCheck.addValueChangeHandler(e -> salaryDraft.onEventsCheckChange(e));
		eventsCheck.ensureDebugId("eventsCheck");
		eventsCheck.setValue(false);
		salaryDraft.setEvenstCheck(eventsCheck);
		hPanel.add(eventsCheck);
		
		AonToolbarButton printPreviewButton = new AonToolbarButton( "Vista preliminar", AON.CSS.aonIconPdf() );
		printPreviewButton.addClickHandler(e -> salaryDraft.onPrintPreview());	
		printPreviewButton.ensureDebugId("printPreviewButton");
		salaryDraft.setPrintPreviewButton(printPreviewButton);
		hPanel.add(printPreviewButton);
		
		AonToolbarButton irpfPreviewButton = new AonToolbarButton( "IRPF", "aon-icon-irpfPreview");
		irpfPreviewButton.addClickHandler(e -> salaryDraft.onIRPFPreview());	
		irpfPreviewButton.ensureDebugId("irpfPreviewButton");
		salaryDraft.setIrpfPreviewButton(irpfPreviewButton);
		hPanel.add(irpfPreviewButton);

		SalarySelect salarySelect = new SalarySelect();
		salaryDraft.setSalarySelect(salarySelect);
		hPanel.add(salarySelect);
		
		AonToolbarButton saveSalaryButton = new AonToolbarButton( "Descargar", AON.CSS.aonIconPdf() );
		saveSalaryButton.addClickHandler(e -> salaryDraft.onSave());	
		saveSalaryButton.ensureDebugId("saveButton");
		salaryDraft.setSaveButton(saveSalaryButton);
		hPanel.add(saveSalaryButton);
		
		AonToolbarButton closePreviewButton = new AonToolbarButton( "Cerrar preliminar", AON.CSS.aonIconClose() );
		closePreviewButton.addClickHandler(e -> salaryDraft.onClosePreview());	
		closePreviewButton.ensureDebugId("closePreviewButton");
		salaryDraft.setClosePreviewButton(closePreviewButton);
		hPanel.add(closePreviewButton);
		
		ListBox settlePreviewListBox = new ListBox(); 
		settlePreviewListBox.addItem("ESTANDAR", SalaryDraft.JASPER);
		settlePreviewListBox.addItem("CARTA (&Beta;)", SalaryDraft.LETTER);
		settlePreviewListBox.addChangeHandler(e -> salaryDraft.onSettlePreviewLBChange());
		settlePreviewListBox.ensureDebugId("settlePreviewListBox");
		settlePreviewListBox.setVisible(false);
		salaryDraft.setSettlePreviewListBox(settlePreviewListBox);
		hPanel.add(settlePreviewListBox);
		
		return hPanel;
	}
	
	// ------------------------------------------------- EmployeeContractVariablesButtons
	
	private HTMLPanel initEmployeeContractVariablesButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());
		
		AonToolbarButton saveContractPaymentsButton = new AonToolbarButton( AON.MSG.saveAction() + " Variables Contrato", AON.CSS.aonIconSave() );
		saveContractPaymentsButton.addClickHandler(e -> employeeContractVariables.onSave());
		hPanel.add(saveContractPaymentsButton);
		
		ListBox yearLBContractVariables = new ListBox();
		employeeContractVariables.initializeYearLB(yearLBContractVariables);
		employeeContractVariables.setYearLB(yearLBContractVariables);
		hPanel.add(yearLBContractVariables);
		
		ListBox variableTypeLB = new ListBox();
		employeeContractVariables.initializeVariableTypeLB(variableTypeLB);
		employeeContractVariables.setVariableTypeLB(variableTypeLB);
		hPanel.add(variableTypeLB);
		
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
		splitLayoutPanel.setWidgetSize(footPanel, 20);
	}

	private void hideFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 20);
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
			dates -> {
				int count = dates.size();
				idcDateListBox.setRowCount(count, true);
				idcDateListBox.setRowData(0, dates);
				idcDateListBox.setVisibleRange(0, count+1);
				idcDateListBox.setSelected(count-1, true);
				idcDateListBox.onResizeDropDownPopup();
				tgssContextMenu.getIdc().setEnabled(true);
				tgssContextMenu.getIdc().setVisible(true);
			}, 
			error -> {}
		);
	}
	
	// ------------------------------------------------- SEPE status
	
	public void setHasCertificateSEPE(boolean hasCertificateSEPE) {
		this.hasCertificateSEPE = hasCertificateSEPE;
	}
	
	private void checkCertificateSEPE() {
		setVisible(sepeContextMenu.getCto().getElement(), hasCertificateSEPE);
		setVisible(sepeContextMenu.getCbc().getElement(), hasCertificateSEPE);
		
		String sepeId = contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getSepeId();
		
		setVisible(sepeContextMenu.getSendBasicCopy().getElement(), hasCertificateSEPE && AonStringUtils.isBlank(sepeId));
		setVisible(sepeContextMenu.getSendContract().getElement(), hasCertificateSEPE && AonStringUtils.isBlank(sepeId));
		setVisible(sepeContextMenu.getRemoveContract().getElement(), hasCertificateSEPE && AonStringUtils.isNotBlank(sepeId));
	}
	
	private void checkContractExtension() {
		 String contractTypeStr = contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getContractType();
		 Date endDate =  contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getEndDate();
		 boolean hasExtension = contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().isHasExtension();
		 
		 if(AonStringUtils.isNotBlank(contractTypeStr)) {
			 Integer contractTypeValue = Integer.parseInt(contractTypeStr);
			 setVisible(sepeContextMenu.getContractExtension().getElement(), !hasExtension && contractTypeValue >= 400 && null != endDate);
			 setVisible(sepeContextMenu.getRemoveContractExtension().getElement(), hasExtension);
		 }
	}
	
	private void checkContractTransform() {
		 String contractTypeStr = contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getContractType();
		 
		 if(AonStringUtils.isNotBlank(contractTypeStr)) {
			 Integer contractTypeValue = Integer.parseInt(contractTypeStr);
			 setVisible(sepeContextMenu.getContractExtension().getElement(), contractTypeValue >= 400);
		 }
	}
	
	// ------------------------------------------------- TGSS status
	
	private void checkTGSSStatus() {
		boolean isTGSSActive = contrataEmployeeObject.getContractData().isTGSSActive();
		Date startDate = contrataEmployeeObject.getContractData().getStartDate();
		
		setVisible(tgssContextMenu.getAltaConsolidadaDelete().getElement(), isTGSSActive && DateUtils.isAfterOrEquals(new Date(), startDate));
	}
	
	// ------------------------------------------------- Messages panel
	
	private Widget getMessageWidget() {
		ContractInfo contractData = contrataEmployeeObject.getContractData();
		EmployeeInfo employeeData = contrataEmployeeObject.getEmployeeData();
		
		String message = "Este contrato ser\u00E1 eliminado de forma permanente.<br> \u00BFDesea eliminar el contrato de <b>" + employeeData.getFullName() + "</b>?";
		
		if(null != contractData.getSalariesCount() && contractData.getSalariesCount() > 0) {
			message = "Este contrato contiene n\u00F3minas existentes. Si lo elimina, se borrar\u00E1n todos los datos de este contrato incluidas las n\u00F3minas.<br> \u00BFDesea eliminar el contrato de <b>" + employeeData.getFullName() + "</b>? <br><br>";
			message += "<b>N\u00F3minas:</b><br><br>";
			for(ContractSalaryInfo salaryInfo : contractData.getContractSalariesInfo())
				message += "&emsp;" + salaryInfo.getType() + "&emsp;(" + formatFullDate.format(salaryInfo.getStart()) + " - " + formatFullDate.format(salaryInfo.getEnd()) + ")&emsp;Percibido : " + salaryInfo.getTotalLiquid() + "\u20AC<br>";
		}
		
//		String message = "Este contrato ser" + String.valueOf("\u00E1") + " eliminado de forma permanente.<br>" + String.valueOf("\u00BF") + "Desea eliminar el contrato de <b>" + employeeData.getFullName() + "</b>?";
//		
//		if(null != contractData.getSalariesCount() && contractData.getSalariesCount() > 0) {
//			message = "Este contrato contiene n" + String.valueOf("\u00F3") + "minas existentes. Si lo elimina se enviar" + String.valueOf("\u00E1") + " a la papelera.<br>" + String.valueOf("\u00BF") + "Desea eliminar el contrato de <b>" + employeeData.getFullName() + "</b>? <br><br>";
//			message += "<b>N" + String.valueOf("\u00F3") + "minas:</b><br><br>";
//			for(ContractSalaryInfo salaryInfo : contractData.getContractSalariesInfo())
//				message += "&emsp;" + salaryInfo.getType() + "&emsp;(" + formatFullDate.format(salaryInfo.getStart()) + " - " + formatFullDate.format(salaryInfo.getEnd()) + ")&emsp;Percibido : " + salaryInfo.getTotalLiquid() + String.valueOf("\u20AC") + "<br>";
//		}
		
		return new HTML(message);
	}
	
	
	// ------------------------------------------------- Aon Messages panel

	private void showSuccess(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(messageContainer, successMap);
	}
	
	private void showInfo(String title, String message) {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put(title, message);
		AonMessagePanel.showInfo(messageContainer, infoMap);
	}
	
	private void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messageContainer, errorMap);
	}
	
	private void showLoading(String message) {
		AonMessagePanel.showLoading(messageContainer, message);
	}
	
	private void hideMessage() {
		AonMessagePanel.hideMessage(messageContainer);
	}
}

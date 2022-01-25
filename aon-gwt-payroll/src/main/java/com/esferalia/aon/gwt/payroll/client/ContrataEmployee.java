package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.ifSistemaREDEnabled;
import static com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.ifSistemaREDError;

import java.util.Date;
import java.util.HashMap;
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
		
		@Override
		protected MenuItem getTaEnd() {
			return tgssContextMenu.getTaEnd();
		}
		
	}
	
	// ------------------------------------------------- ContractAttachUIImpl
	
	public class ContractAttachUIImpl extends ContractAttachUI {
		
		@Override
		protected void onExportPDF() {
			showLoading("Generando borrador de contrato");
			contrataEmployeeObject.getContractOtherInfo(s -> {
				if(AonStringUtils.isBlank(contrataEmployeeObject.getFormativeLevel()))
					contrataEmployeeObject.getContractSpecificData(su -> {
						contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
						contrataEmployeeObject.setContractOtherData(contractOtherData.getContractOtherData());
						contrataEmployeeObject.saveContractExport(
								a -> {
									Map<String, String> successrMap = new HashMap<>();
									successrMap.put("Generaci\u00F3n Contrato", "El borrador de contrato se ha generado correctamente");
									AonMessagePanel.showSuccess(messageContainer, successrMap);
									contractAttachUI.setContractAttachments(a);
									this.refreshPage();
								},
								e -> {
									Map<String, String> errorMap = new HashMap<>();
									errorMap.put("Generaci\u00F3n Contrato", e.getMessage());
									AonMessagePanel.showError(messageContainer, errorMap);
								}
						);
					}, f -> {
						Map<String, String> errorMap = new HashMap<>();
						errorMap.put("Generaci\u00F3n Contrato", f.getMessage());
						AonMessagePanel.showError(messageContainer, errorMap);
					});
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

		@Override
		protected void showError(Map<String, String> errorMap) {
			AonMessagePanel.showError(messageContainer, errorMap);
		}

		@Override
		protected void showSuccess(Map<String, String> successMap) {
			AonMessagePanel.showSuccess(messageContainer, successMap);
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
	
	class TAEndCommand implements ScheduledCommand {

		@Override
		public void execute() {
			showTaEnd();
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
		private MenuItem taEnd;
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
			
			taEnd = addItem("Duplicados de Documentos TA (Baja)", new TAEndCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			taEnd.ensureDebugId("taEnd");
			
			idc = addItem("Informe de Cotizaci\u00F3n-Trab Cuenta Ajena", new IDCCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			idc.ensureDebugId("idc");
			
			idcPlNss = addItem("Informe de Cotizaci\u00F3n/Periodo Liquidaci\u00F3n-NSS", new IDCPlNssCommand(), 
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
		
		public MenuItem getTaEnd() {
			return taEnd;
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
	EmployeeSalary employeeSalary;
	
	@UiField (provided = true)
	EmployeeCalendarDraftNew employeeCalendar;
	
	@UiField (provided = true)
	EmployeeContractIrpf employeeContractIrpf;
	
	@UiField (provided = true)
	EmployeeContractVariables employeeContractVariables;
	
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
	
	// EmployeeContractIrpf
	private HTMLPanel employeeContractIrpfButtons;
	
	// EmployeeContractVariables
	private HTMLPanel employeeContractVariablesButtons;
	
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
		
		employeeSalary = new EmployeeSalary();
		employeeSalary.hideToolbar();
		
		employeeCalendar = new EmployeeCalendarDraftNew();
		employeeCalendar.hideToolbar();
		employeeCalendar.setContrataEmployeeCalendarHeight();
		
		employeeContractIrpf = new EmployeeIrpfImpl();
		employeeContractIrpf.hideToolbar();
		
		employeeContractVariables = new EmployeeContractVariables();
		employeeContractVariables.hideToolbar();
		
		this.toolbar = new AonToolbar("Contrato");
		
		// Init Widget
		initWidget(uiBinder.createAndBindUi(this));

		// Init toolbar
		getToolbarPanel();
		
		
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
			tabLayOutPanel.remove(8);
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
			return "Cargando n\u00F3minas ...";
		case 6:
			return "Cargando calendario ...";
		case 7:
			return "Cargando datos IRPF ...";
		case 8:
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
						if(null == this.contrataEmployeeObject.getContractData().getEndDate()) {
							tgssContextMenu.getAfiEnd().getElement().getStyle().setDisplay(Display.NONE);
							tgssContextMenu.getTaEnd().getElement().getStyle().setDisplay(Display.NONE);
						} else {
							tgssContextMenu.getAfiEnd().getElement().getStyle().clearDisplay();
							tgssContextMenu.getTaEnd().getElement().getStyle().clearDisplay();
						}
					}, f -> {});
				break;
			case 1:
				contrataEmployeeObject.setContractSpecificData(contractSpecificData.getContractSpecificData(), 
						s -> {}, 
						f -> showError("Error guardando Datos SEPE", f.getMessage()));
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
							showTgssOption();
							hideSepeOption();
							
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
					hideTgssOption();
					showSepeOption();
					contractSpecificData.setEmployeeContractInfo(
							contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getContractType(), 
							contrataEmployeeObject.getContractEmployeeInfo().getContractSpecificData());
					hideLoadingPanel();
				}, f -> showError("Error obtenci\u00f3n Datos SEPE", f.getMessage()));
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
							hideTgssOption();
							hideSepeOption();
						}, f -> {});
					else {
						exportContract.getElement().getStyle().clearDisplay();
						showContractButtons();
						contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
						hideLoadingPanel();
						hideTgssOption();
						hideSepeOption();
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
					hideTgssOption();
					hideSepeOption();
				}, f -> {});
				break;
			case 4:
				showLoadingPanel();
				contrataEmployeeObject.getContractAttachments(s -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showContractButtons();
					contractAttachUI.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
					hideLoadingPanel();
					hideTgssOption();
					hideSepeOption();
				}, f -> {});
				break;
			case 5:
				showLoadingPanel();
				contrataEmployeeObject.getEmployeeSalaryObject(employeeSalaryObject -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showSalariesButtons();
					employeeSalary.setEmployeeSalaryObject(employeeSalaryObject);
					employeeSalary.removeMainMT();
					hideLoadingPanel();
				});
				break;
			case 6:
				showLoadingPanel();
				contrataEmployeeObject.getEmployeeCalendarObject(employeeCalendarObject -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showCalendarButtons();
					employeeCalendar.setEmployeeCalendarDraftObject(employeeCalendarObject);
					hideLoadingPanel();
				});
				break;
			case 7:
				showLoadingPanel();
				contrataEmployeeObject.getEmployeeContractIrpfObject(employeeContractIrpfObject -> {
					exportContract.getElement().getStyle().setDisplay(Display.NONE);
					showContractIrpfButtons();
					employeeContractIrpf.setEmployeeContractIrpfObject(employeeContractIrpfObject);
					hideLoadingPanel();
				});
				break;
			case 8:
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
		if(tabLayOutPanel.getSelectedIndex() == 0)
			tgss.setVisible(true);
		else if(tabLayOutPanel.getSelectedIndex() == 1)
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
					hideSepeOption();
					
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
		employeeContractIrpfButtons.setVisible(false);
		employeeContractVariablesButtons.setVisible(false);
	}
	
	private void showContractButtons() {
		employeeContractButtons.setVisible(true);
		employeeSalaryButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
		employeeContractVariablesButtons.setVisible(false);
	}
	
	private void showCalendarButtons() {
		employeeCalendarButtons.setVisible(true);
		employeeContractButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
		employeeContractVariablesButtons.setVisible(false);
	}
	
	private void showContractIrpfButtons() {
		employeeContractIrpfButtons.setVisible(true);
		employeeCalendarButtons.setVisible(false);
		employeeContractButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeContractVariablesButtons.setVisible(false);
	}
	
	private void showContractVariablesButtons() {
		employeeContractVariablesButtons.setVisible(true);
		employeeCalendarButtons.setVisible(false);
		employeeContractButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
	}
	
	// ------------------------------------------------- Abstract methods
	
	protected abstract void onListShow(boolean reloadEmployees);
	protected abstract void onPreviusContract(Integer contractId);
	protected abstract void onNextContract(Integer contractId);
	protected abstract void onTransformContract(Integer newContractId);
	protected abstract DomainUserRoles getDomainUserRole();
	
	// ------------------------------------------------- Toolbar panel
	
	private AonToolbar getToolbarPanel() {
		
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
		
		// EmployeeIrpf
		
		employeeContractIrpfButtons = initEmployeeContractIrpfButtons();
		toolbar.add(employeeContractIrpfButtons);
		
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
					
					if(null == this.contrataEmployeeObject.getContractData().getEndDate()) {
						tgssContextMenu.getAfiEnd().getElement().getStyle().setDisplay(Display.NONE);
						tgssContextMenu.getTaEnd().getElement().getStyle().setDisplay(Display.NONE);
					} else {
						tgssContextMenu.getAfiEnd().getElement().getStyle().clearDisplay();
						tgssContextMenu.getTaEnd().getElement().getStyle().clearDisplay();
					}
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
				, fa -> showError("Error obtenci\u00f3n Datos SEPE", fa.getMessage()))
			, f -> showError("Error guardando Datos SEPE", f.getMessage()));
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
	
	private void showTaEnd() {
		showLoading("Obteniendo TA (Baja)...");
		contrataEmployeeObject.downloadTaEnd(dataURI -> {
				hideMessage();
				showPdf();
				pdfViewer.open(dataURI);
		}, f -> showError("Error TA (Baja)", f.getMessage()));
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
							ContrataEmployee.this.setTaEndVisible(true);
							ContrataEmployee.this.setIdcVisible(true);
						}, () -> {
							ContrataEmployee.this.setTaVisible(false);
							ContrataEmployee.this.setTaEndVisible(false);
							ContrataEmployee.this.setIdcVisible(false);

						});
						ifSistemaREDError(employeeStatus, 
								ContrataEmployee.this::showFootPanel, 
								ContrataEmployee.this::closeFootPanel);

					}, throwable -> {
						closeFootPanel();
						ContrataEmployee.this.setTaVisible(false);
						ContrataEmployee.this.setTaEndVisible(false);
						ContrataEmployee.this.setIdcVisible(false);

					});
				}
			};

			employeeStatus.visit(sistemaREDResults);
			resultsPanel.setWidget(sistemaREDResults);
			selectResultsPanel();

			ifSistemaREDEnabled(employeeStatus, () -> {
				ContrataEmployee.this.setTaVisible(true);
				ContrataEmployee.this.setTaEndVisible(true);
				ContrataEmployee.this.setIdcVisible(true);
			}, () -> {
				ContrataEmployee.this.setTaVisible(false);
				ContrataEmployee.this.setTaEndVisible(false);
				ContrataEmployee.this.setIdcVisible(false);
			});
			
			ifSistemaREDError(employeeStatus, this::showFootPanel, this::closeFootPanel);

		}, throwable -> {
			closeFootPanel();
			ContrataEmployee.this.setTaVisible(false);
			ContrataEmployee.this.setTaEndVisible(false);
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
	
	public void setTaEndVisible(boolean visible ) {
		tgssContextMenu.getTaEnd().setVisible(visible);
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
	
	private void hideSepeOption() {
		setVisible(sepe.getElement(), false);
	}
	
	private void showSepeOption() {
		setVisible(sepe.getElement(), true);
	}
	
	private void hideTgssOption() {
		setVisible(tgss.getElement(), false);
	}
	
	private void showTgssOption() {
		setVisible(tgss.getElement(), true);
	}
	
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
		
		StringBuilder message = new StringBuilder();
		
		message.append("Este contrato ser\u00E1 eliminado de forma permanente.<br> \u00BFDesea eliminar el contrato de <b>" + employeeData.getFullName() + "</b>?");
		
		if(null != contractData.getSalariesCount() && contractData.getSalariesCount() > 0) {
			message = new StringBuilder();
			message.append("Este contrato contiene n\u00F3minas existentes. Si lo elimina, se borrar\u00E1n todos los datos de este contrato incluidas las n\u00F3minas.<br> \u00BFDesea eliminar el contrato de <b>" + employeeData.getFullName() + "</b>? <br><br>");
			message.append("<b>N\u00F3minas:</b><br><br>");
			for(ContractSalaryInfo salaryInfo : contractData.getContractSalariesInfo())
				message.append("&emsp;" + salaryInfo.getType() + "&emsp;(" + formatFullDate.format(salaryInfo.getStart()) + " - " + formatFullDate.format(salaryInfo.getEnd()) + ")&emsp;Percibido : " + salaryInfo.getTotalLiquid() + "\u20AC<br>");
		}
		
		return new HTML(message.toString());
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

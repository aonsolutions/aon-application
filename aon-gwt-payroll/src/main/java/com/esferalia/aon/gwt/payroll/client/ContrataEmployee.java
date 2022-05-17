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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractSalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractTransform;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedContractType;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedOccupation;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedPartialFactor;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedQuoteGroup;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedStartDate;
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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
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
		protected MinimizePanel getFootPanel() {
			return footPanel;
		}

		@Override
		protected MonthListBox getIDCMonthListBox() {
			return idcMonthListBox;
		}

		@Override
		protected MenuItem getTaEnd() {
			return tgssContextMenu.getTaEnd();
		}

		@Override
		protected void showErrorMessage(String title, String message) {
			showError(title, message);
		}

	}

	// ------------------------------------------------- ContractSpecificDataImpl

	public class ContractSpecificDataImpl extends ContractSpecificData {

		@Override
		protected void showErrorMessage(String title, String message) {
			showError(title, message);
		}

		@Override
		protected void showSuccessMessage(String title, String message) {
			showSuccess(title, message);
		}

		@Override
		protected void showLoadingMessage(String message) {
			showLoading(message);
		}

		@Override
		protected void downloadCtoDocument() {
			downloadCto();
		}
	}

	// ------------------------------------------------- ContractClausesUIImpl

	public class ContractClauseUIImpl extends ContractClauseUI {

		@Override
		protected void showErrorMessage(String title, String message) {
			showError(title, message);
		}

		@Override
		protected void showSuccessMessage(String title, String message) {
			showSuccess(title, message);
		}
		
		@Override
		protected void showLoadingMessage(String message) {
			showLoading(message);
		}
	}

	// ------------------------------------------------- ContractAttachUIImpl

	public class ContractAttachUIImpl extends ContractAttachUI {

		@Override
		protected void onExportPDF(Consumer<String> consumer, Consumer<Throwable> failure) {
			showLoading("Generando borrador de contrato");
			contrataEmployeeObject.getContractOtherInfo(s -> {
				if (AonStringUtils.isBlank(contrataEmployeeObject.getFormativeLevel()))
					contrataEmployeeObject.getContractSpecificData(su -> {
						contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
						contrataEmployeeObject.setContractOtherData(contractOtherData.getContractOtherData());
						contrataEmployeeObject.saveContractExport(
								a -> consumer.accept("El borrador de contrato se ha generado correctamente"),
								e -> failure.accept(e));
					}, f -> failure.accept(f));
				else {
					contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
					contrataEmployeeObject.setContractOtherData(contractOtherData.getContractOtherData());
					contrataEmployeeObject.saveContractExport(
							a -> consumer.accept("El borrador de contrato se ha generado correctamente"),
							e -> failure.accept(e));
				}
			}, f -> failure.accept(f));
		}

		@Override
		protected void showErrorMessage(String title, String message) {
			showError(title, message);
		}

		@Override
		protected void showSuccessMessage(String title, String message) {
			showSuccess(title, message);
		}
	}

	// ------------------------------------------------- EmployeeIrpfImpl

	public class EmployeeIrpfImpl extends EmployeeContractIrpf {

		@Override
		protected void showErrorMessage(String title, String message) {
			showError(title, message);
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

	class TAEndCommand implements ScheduledCommand {

		@Override
		public void execute() {
			showTaEnd();
		}
	}

	class PeculiaritiesCommand implements ScheduledCommand {

		@Override
		public void execute() {
			new EmployeePeculiaritiesDialog(contrataEmployeeObject.getContractId(), contrataEmployeeObject.getContractStartDate());
		}
	}

	class MovPrevDeleteCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AonDialog dialog = new AonDialog("Movimiento Previo", new HTML("\u00bfDesea realmente eliminar el movimiento previo\u003f"));
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					movPrevDelete();
				}
			});
		}
	}

	class AltaConsolidadaDeleteCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AonDialog dialog = new AonDialog("Alta Consolidada", new HTML("\u00bfDesea realmente eliminar el alta consolidada\u003f"));
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					altaConsolidadaDelete();
				}
			});
		}
	}

	class ComunicateAFICommand implements ScheduledCommand {

		@Override
		public void execute() {
			onComunicateAFI();
		}
	}

	class NewTGSSContextMenu extends ContextMenu {

		private MenuItem ta;
		private MenuItem taEnd;
		private MenuItem afi;
		private MenuItem idc;
		private MenuItem idcPlNss;
		private MenuItem peculiarities = null;

//		private MenuItem movPrevDelete = null;
		MenuItemSeparator separator;
		private MenuItem altaConsolidadaDelete = null;
		private MenuItem comunicateAFI = null;

		public NewTGSSContextMenu() {

			afi = addItem("Cambios AFI", new AFICommand(), AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON,
					style.cmdBtn());
			afi.ensureDebugId("afi");

			peculiarities = addItem("Peculiaridades de cotizaci\u00F3n", new PeculiaritiesCommand(),
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			peculiarities.ensureDebugId("peculiarities");

			addSeparator();

			ta = addItem("Duplicados de Documentos TA", new TACommand(), AON.CSS.aonIconPdf(), AON.AON_ICON_CMD_BUTTON,
					style.cmdBtn());
			ta.ensureDebugId("ta");

			taEnd = addItem("Duplicados de Documentos TA (Baja)", new TAEndCommand(), AON.CSS.aonIconPdf(),
					AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			taEnd.ensureDebugId("taEnd");

			idc = addItem("IDC-Trab Cuenta Ajena", new IDCCommand(), AON.CSS.aonIconPdf(), AON.AON_ICON_CMD_BUTTON,
					style.cmdBtn());
			idc.ensureDebugId("idc");

			idcPlNss = addItem("IDC/Periodo Liquidaci\u00F3n-NSS", new IDCPlNssCommand(), AON.CSS.aonIconPdf(),
					AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			idcPlNss.ensureDebugId("idcPlNss");

			addSeparator();

//			movPrevDelete = addItem("Eliminar movimiento previo", new MovPrevDeleteCommand(), 
//					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
//			movPrevDelete.ensureDebugId("movPrevDelete");

			altaConsolidadaDelete = addItem("Eliminar alta consolidada", new AltaConsolidadaDeleteCommand(),
					AON.CSS.aonIconSend(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			altaConsolidadaDelete.ensureDebugId("altaConsolidadaDelete");

			comunicateAFI = addItem("Notificaci\u00f3n AFI (TGSS)", new ComunicateAFICommand(), AON.CSS.aonIconSend(),
					AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			comunicateAFI.ensureDebugId("comunicateAFI");

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

		public MenuItem getIdc() {
			return idc;
		}

		public MenuItem getIdcPlNss() {
			return idcPlNss;
		}

		public MenuItem getPeculiarities() {
			return peculiarities;
		}

//		public MenuItem getMovPrevDelete() {
//			return movPrevDelete;
//		}

		public MenuItem getAltaConsolidadaDelete() {
			return altaConsolidadaDelete;
		}

		public MenuItem getComunicateAFI() {
			return comunicateAFI;
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
			AonDialog dialog = new AonDialog("Notificar copia basica", new HTML("\u00bfDesea realmente notificar la copia basica\u003f"));
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					sendBasicCopy();
				}
			});
		}
	}

	class SendContractCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AonDialog dialog = new AonDialog("Notificar contrato", new HTML("\u00bfDesea realmente notificar el contrato\u003f"));
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					sendContract();
				}
			});
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
			AonDialog dialog = new AonDialog("Pr\u00F3rroga contrato", new HTML("\u00bfDesea realmente eliminar la pr\u00f3 del contrato\u003f"));
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					deleteContractExtension();
				}
			});
		}

	}

	class ContractTransformCommand implements ScheduledCommand {

		@Override
		public void execute() {
			contractTransform();
		}

	}

	class SendContractTransformCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AonDialog dialog = new AonDialog("Notifici\u00f3n transformaci\u00F3n contrato", new HTML("\u00bfDesea realmente notificar la transformaci\u00f3n del contrato\u003f"));
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					sendContractTransform();
				}
			});
		}

	}

	class RemoveContractCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AonDialog dialog = new AonDialog("Notifici\u00f3n contrato", new HTML("\u00bfDesea realmente eliminar el contrato del SEPE\u003f"));
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					removeContract();
				}
			});
		}

	}

	class RemoveContractTransformCommand implements ScheduledCommand {

		@Override
		public void execute() {
			removeContractTransform();
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
		private MenuItem sendContractTransform;
		private MenuItem sepeIDE;

		private MenuItem removeContract;
		private MenuItem removeContractTransform;

		public NewSEPEContextMenu() {

			cto = addItem("Copia Contrato", new CTOCommand(), AON.CSS.aonIconPdf(), AON.AON_ICON_CMD_BUTTON,
					style.cmdBtn());
			cto.ensureDebugId("cto");

			cbc = addItem("Copia B\u00E1sica", new CBCCommand(), AON.CSS.aonIconPdf(), AON.AON_ICON_CMD_BUTTON,
					style.cmdBtn());
			cbc.ensureDebugId("cbc");

			addSeparator();

			cetifica2 = addItem("Cetifica2", new Certifica2Command(), AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON,
					style.cmdBtn());
			cetifica2.ensureDebugId("cetifica2");

			cetifica2PDF = addItem("Cetifica2 PDF", new Certifica2PDFCommand(), AON.CSS.aonIconPdf(),
					AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			cetifica2PDF.ensureDebugId("cetifica2PDF");

			addSeparator();

			contractExtension = addItem("Pr\u00F3rroga Contrato", new ContractExtensionCommand(), AON.CSS.aonIconSepe(),
					AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			contractExtension.ensureDebugId("contractExtension");

			contractTransform = addItem("Transformaci\u00F3n Contrato", new ContractTransformCommand(),
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			contractTransform.ensureDebugId("contractTransform");

			removeContractExtension = addItem("Eliminar Pr\u00F3rroga Contrato", new DeleteContractExtensionCommand(),
					AON.CSS.aonIconSepe(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			removeContractExtension.ensureDebugId("removeContractExtension");

			addSeparator();

			sendBasicCopy = addItem("Notificar Copia B\u00E1sica", new SendBasicCopyCommand(), AON.CSS.aonIconSend(),
					AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			sendBasicCopy.ensureDebugId("sendBasicCopy");

			sendContract = addItem("Notificar Contrato", new SendContractCommand(), AON.CSS.aonIconSend(),
					AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			sendContract.ensureDebugId("sendContract");

			sendContractTransform = addItem("Notificar Transformaci\u00f3n Contrato",
					new SendContractTransformCommand(), AON.CSS.aonIconSend(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			sendContractTransform.ensureDebugId("sendContractTransform");

			sepeIDE = addItem("Ver IDE Contrato", new SepeIDEContractCommand(), AON.CSS.aonIconInfo(),
					AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			sepeIDE.ensureDebugId("sepeIDE");

			removeContract = addItem("Eliminar Contrato", new RemoveContractCommand(), AON.CSS.aonIconSend(),
					AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			removeContract.ensureDebugId("removeContract");

			removeContractTransform = addItem("Eliminar Transformaci\u00f3n Contrato",
					new RemoveContractTransformCommand(), AON.CSS.aonIconSend(), AON.AON_ICON_CMD_BUTTON,
					style.cmdBtn());
			removeContractTransform.ensureDebugId("removeContractTransform");

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

		public MenuItem getSendContractTransform() {
			return sendContractTransform;
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

		public MenuItem getRemoveContractTransform() {
			return removeContractTransform;
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

	@UiField(provided = true)
	AonToolbar toolbar;

	@UiField
	HTMLPanel messageContainer;

	@UiField(provided = true)
	ContractEmployeeUI contractEmployeeUI;

	@UiField(provided = true)
	ContractSpecificData contractSpecificData;

	@UiField(provided = true)
	ContractOtherData contractOtherData;

	@UiField(provided = true)
	ContractClauseUI contractClauseUI;

	@UiField(provided = true)
	ContractAttachUI contractAttachUI;

	@UiField(provided = true)
	EmployeeSalary employeeSalary;

	@UiField(provided = true)
	EmployeeCalendarDraftNew employeeCalendar;

	@UiField(provided = true)
	EmployeeContractIrpf employeeContractIrpf;

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
	private AonExpandButton tgss;
	private AonExpandButton sepe;
	private AonToolbarButton closePDF;
	private DateListBox idcDateListBox;
	private MonthListBox idcMonthListBox;

	private NewTGSSContextMenu tgssContextMenu;
	private NewSEPEContextMenu sepeContextMenu;

	// EmployeeClause
	private HTMLPanel employeeClauseButtons;

	// EmployeeAttach
	private HTMLPanel employeeAttachButtons;

	// EmployeeSalary
	private HTMLPanel employeeSalaryButtons;

	// EmployeeCalendar
	private HTMLPanel employeeCalendarButtons;

	// EmployeeContractIrpf
	private HTMLPanel employeeContractIrpfButtons;

	private AonToolbarButton previusContract;
	private AonToolbarButton nextContract;
	private Label employeeCounter;

	private boolean hasCertificateSEPE = false;

	private boolean changes = false;

	private boolean isComunica = false;

	// ------------------------------------------------- Constructor

	protected ContrataEmployee() {

		// Init Tabs Elements
		contractEmployeeUI = new ContractEmployeeUIImpl();
		contractSpecificData = new ContractSpecificDataImpl();
		contractOtherData = new ContractOtherData();
		contractClauseUI = new ContractClauseUIImpl();
		contractAttachUI = new ContractAttachUIImpl();

		employeeSalary = new EmployeeSalary();
		employeeSalary.hideToolbar();

		employeeCalendar = new EmployeeCalendarDraftNew();
		employeeCalendar.hideToolbar();
		employeeCalendar.setContrataEmployeeCalendarHeight();

		employeeContractIrpf = new EmployeeIrpfImpl();
		employeeContractIrpf.hideToolbar();

		this.toolbar = new AonToolbar("Contrato");

		// Init Widget
		initWidget(uiBinder.createAndBindUi(this));

		// Init toolbar
		getToolbarPanel();

		// Show contract buttons
		showContractButtons();

		// Init ContextMenu
		tgssContextMenu = new NewTGSSContextMenu();
		sepeContextMenu = new NewSEPEContextMenu();

		// Init view
		setScrollPanelsHeight();
		initTabLayOutPanel();
		initFootPanel();
		initResultsPanel();
		showEmployee();
	}

	private void showLoadingPanel() {
		showLoading(getLoadingText());
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

	private void initResultsPanel() {
		resultsPanel = new ResultsPanel();
	}

	private void initTabLayOutPanel() {
		tabLayOutPanel.selectTab(0, false);
		tabLayOutPanel.addStyleName(style.container());

		addBeforeSelectionHandler();
		addSelectionHandler();
	}

	private void addBeforeSelectionHandler() {
		tabLayOutPanel.addBeforeSelectionHandler(e -> {
			Integer itemIdx = tabLayOutPanel.getSelectedIndex();
			switch (itemIdx) {
			case 0:
				Map<String, String> messageMap = contractEmployeeUI.checkSaveAndGetErrors();
				if (messageMap.isEmpty())
					contrataEmployeeObject.setEmployeeContract(s -> checkTGSSStatus(), f -> {
					});
				break;
			case 1:
				if (isNotTransformation())
					contrataEmployeeObject.setContractSpecificData(contractSpecificData.getContractSpecificData(),
							s -> {
							}, f -> showError("Error guardando Datos SEPE", f.getMessage()));
				break;
			case 2:
				contrataEmployeeObject.setContractOtherData(contractOtherData.getContractOtherData());
				contrataEmployeeObject.setContractOtherInfo(s -> {
				}, f -> {
				});
				break;
			default:
				break;
			}
		});
	}

	private boolean isNotTransformation() {
		return Boolean.FALSE.equals(this.contrataEmployeeObject.getContractData().isHasTransformation());
	}

	private void addSelectionHandler() {
		tabLayOutPanel.addSelectionHandler(e -> loadWindow(s -> {}));
	}

	private void loadWindow(Consumer<Void> finish) {
		showLoadingPanel();
		loadData(s -> {
			loadToolbar();
			checkButtonsToolbar();
			hideMessage();
			finish.accept(null);
		});	
	}

	private void loadData(Consumer<Void> finish) {
		Integer tabIdx = tabLayOutPanel.getSelectedIndex();
		switch (tabIdx) {
		case 0:
			contractEmployeeUI.setContrataEmployeeObject(this.contrataEmployeeObject,
					this.contractId, success -> {
						// Check SS only if not RETA
						Byte ssRegime = contrataEmployeeObject.getContractData().getSsRegimen();
						if (null == ssRegime || ssRegime != 3)
							checkStatus(this.contrataEmployeeObject);

						if (AonStringUtils.isBlank(contrataEmployeeObject.getContractData().getSepeId()))
							sepeContextMenu.getSepeIDE().getElement().getStyle().setDisplay(Display.NONE);
						else
							sepeContextMenu.getSepeIDE().getElement().getStyle().clearDisplay();

						finish.accept(null);
					});
			break;
		case 1:
			contrataEmployeeObject.getContractSpecificData(s -> {
				contractSpecificData.setEmployeeContractInfo(
						contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getContractType(),
						contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().isHasTransformation(),
						hasCertificateSEPE,
						contrataEmployeeObject.getContractEmployeeInfo().getEmployeeInfo().getDocument(),
						contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getStartDate(),
						contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getContractId(),
						contrataEmployeeObject.getContractEmployeeInfo().getContractSpecificData());

				finish.accept(null);
			}, f -> showError("Error obtenci\u00f3n Datos SEPE", f.getMessage()));
			break;
		case 2:
			contrataEmployeeObject.getContractOtherInfo(s -> {
				if (AonStringUtils.isBlank(contrataEmployeeObject.getFormativeLevel()))
					contrataEmployeeObject.getContractSpecificData(su -> {
						contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
						finish.accept(null);
					}, f -> {
					});
				else {
					contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
					finish.accept(null);
				}
			}, f -> {
			});
			break;
		case 3:
			contractClauseUI.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
			finish.accept(null);
			break;
		case 4:
			contractAttachUI.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
			finish.accept(null);
			break;
		case 5:
			contrataEmployeeObject.getEmployeeSalaryObject(salaryObject -> {
				employeeSalary.setEmployeeSalaryObject(salaryObject);
				employeeSalary.removeMainMT();
				finish.accept(null);
			});
			break;
		case 6:
			contrataEmployeeObject.getEmployeeCalendarObject(
					calendarObject -> employeeCalendar.setEmployeeCalendarDraftObject(calendarObject));
			finish.accept(null);
			break;
		case 7:
			contrataEmployeeObject.getEmployeeContractIrpfObject(
					irpfObject -> employeeContractIrpf.setEmployeeContractIrpfObject(irpfObject));
			finish.accept(null);
			break;
		default:
			finish.accept(null);
			break;
		}
	}

	private void loadToolbar() {
		Integer tabIdx = tabLayOutPanel.getSelectedIndex();
		switch (tabIdx) {
		case 0:
			showContractButtons();
			break;
		case 1:
			showContractButtons();
			break;
		case 2:
			showContractButtons();
			break;
		case 3:
			showContractClauseButtons();
			break;
		case 4:
			showContractAttachButtons();
			break;
		case 5:
			showSalariesButtons();
			break;
		case 6:
			showCalendarButtons();
			break;
		case 7:
			showContractIrpfButtons();
			break;
		default:
			break;
		}
	}

	private void checkButtonsToolbar() {
		Integer tabIdx = tabLayOutPanel.getSelectedIndex();
		switch (tabIdx) {
		case 0:
			checkCertificateSEPE();
			checkTGSSStatus();
			checkContractExtension();
			checkContractTransform();
			showTgssOption();
			hideSepeOption();
			showContractButtons();
			if (AonStringUtils.isBlank(contrataEmployeeObject.getContractData().getSepeId()))
				sepeContextMenu.getSepeIDE().getElement().getStyle().setDisplay(Display.NONE);
			else
				sepeContextMenu.getSepeIDE().getElement().getStyle().clearDisplay();
			break;
		case 1:
			checkCertificateSEPE();
			checkContractExtension();
			checkContractTransform();
			hideTgssOption();
			showSepeOption();
			break;
		case 2:
		case 3:
		case 4:
			hideComunicaOpts();
			break;
		default:
			break;
		}
	}

	private void hideComunicaOpts() {
		hideTgssOption();
		hideSepeOption();
	}

	// ------------------------------------------------- Show/Hide Employee/PDF

	private void showEmployee() {
		saveContract.setVisible(true);
		deleteContract.setVisible(true);
		listEmployees.setVisible(true);
		previusContract.setVisible(true);
		employeeCounter.setVisible(true);
		nextContract.setVisible(true);
		if (tabLayOutPanel.getSelectedIndex() == 0)
			tgss.setVisible(true);
		else if (tabLayOutPanel.getSelectedIndex() == 1)
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

	public void setContrataEmployeeObject(ContrataEmployeeObject contrataEmployeeDialogObject, Integer contractId,
			Integer selectedEmployeeIdx, int employeesSize, Consumer<String> success) {

		this.contractId = contractId;
		this.contrataEmployeeObject = contrataEmployeeDialogObject;
		this.tabLayOutPanel.selectTab(0, false);
		
		loadWindow(s -> {
			employeeCounter.setText(selectedEmployeeIdx + " de " + employeesSize);
			success.accept("");
		});
	}

	public void setContrataEmployeeObject(ContrataEmployeeObject contrataEmployeeDialogObject, Integer contractId,
			Integer selectedEmployeeIdx, int employeesSize, int selectedTab, Consumer<String> success) {

		this.contractId = contractId;
		this.contrataEmployeeObject = contrataEmployeeDialogObject;
		this.tabLayOutPanel.selectTab(0, false);
		
		loadWindow(s -> {
			employeeCounter.setText(selectedEmployeeIdx + " de " + employeesSize);
			tabLayOutPanel.selectTab(selectedTab, true);
			success.accept("");
		});
	}

	// ------------------------------------------------- Show/Hide Toolbar methods

	private void showSalariesButtons() {
		employeeSalaryButtons.setVisible(true);
		employeeContractButtons.setVisible(false);
		employeeAttachButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
		employeeClauseButtons.setVisible(false);
	}

	private void showContractButtons() {
		employeeContractButtons.setVisible(true);
		employeeAttachButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
		employeeClauseButtons.setVisible(false);
	}

	private void showCalendarButtons() {
		employeeCalendarButtons.setVisible(true);
		employeeContractButtons.setVisible(false);
		employeeAttachButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
		employeeClauseButtons.setVisible(false);
	}

	private void showContractIrpfButtons() {
		employeeContractIrpfButtons.setVisible(true);
		employeeCalendarButtons.setVisible(false);
		employeeContractButtons.setVisible(false);
		employeeAttachButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeClauseButtons.setVisible(false);
	}

	private void showContractAttachButtons() {
		employeeAttachButtons.setVisible(true);
		employeeCalendarButtons.setVisible(false);
		employeeContractButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
		employeeClauseButtons.setVisible(false);
	}

	private void showContractClauseButtons() {
		employeeClauseButtons.setVisible(true);
		employeeAttachButtons.setVisible(false);
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

		listEmployees = new AonToolbarButton("Volver a contratos", AON.CSS.aonIconBack());
		listEmployees.addClickHandler(e -> onListEmployees());
		toolbar.add(listEmployees);

		// EmployeeContractButtons

		employeeContractButtons = initEmployeeContractButtons();
		toolbar.add(employeeContractButtons);

		// EmployeeClause

		employeeClauseButtons = initEmployeeClauseButtons();
		toolbar.add(employeeClauseButtons);

		// EmployeeAttach

		employeeAttachButtons = initEmployeeAttachButtons();
		toolbar.add(employeeAttachButtons);

		// EmployeeSalary

		employeeSalaryButtons = initEmployeeSalaryButtons();
		toolbar.add(employeeSalaryButtons);

		// EmployeeCalendar

		employeeCalendarButtons = initEmployeeCalendarButtons();
		toolbar.add(employeeCalendarButtons);

		// EmployeeIrpf

		employeeContractIrpfButtons = initEmployeeContractIrpfButtons();
		toolbar.add(employeeContractIrpfButtons);

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

	// ------------------------------------------------- Toolbar panel (Auxiliar
	// Methods)

	private void onListEmployees() {
		onListShow(true);
	}

	// ------------------------------------------------- EmployeeContractButtons

	private HTMLPanel initEmployeeContractButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());

		saveContract = new AonToolbarButton(AON.MSG.saveAction() + " Contrato", AON.CSS.aonIconSave());
		saveContract.addClickHandler(e -> onSaveContract());
		hPanel.add(saveContract);

		deleteContract = new AonToolbarButton(AON.MSG.deleteAction() + " Contrato", AON.CSS.aonIconDelete());
		deleteContract.addClickHandler(e -> onDeleteContract());
		hPanel.add(deleteContract);

		tgss = new AonExpandButton("TGSS", AON.CSS.aonIconTgss()) {

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

		closePDF = new AonToolbarButton(AON.MSG.closed(), AON.CSS.aonIconClose());
		closePDF.addClickHandler(e -> onClosePDF());
		hPanel.add(closePDF);

		return hPanel;
	}

	// ------------------------------------------------- EmployeeContractButtons
	// (Auxiliar methods)

	public void setChanges(boolean changes) {
		this.changes = changes;
	}

	public boolean getChanges() {
		return changes;
	}

	private void onSaveContract() {
		setChanges(true);

		Integer tabIdx = tabLayOutPanel.getSelectedIndex();

		switch (tabIdx) {
		case 0:
			Map<String, String> messageMap = contractEmployeeUI.checkSaveAndGetErrors();
			if (messageMap.isEmpty())
				contrataEmployeeObject.setEmployeeContract(s -> {
					showSuccess("Guardado", "El contrato " + contrataEmployeeObject.getEmployeeFullName()
							+ " ha sido actualizado correctamente");
					checkStatus(contrataEmployeeObject);
					checkTGSSStatus();
				}, f -> {
				});
			else
				AonMessagePanel.showError(messageContainer, messageMap);
			break;
		case 1:
			if (isNotTransformation())
				contrataEmployeeObject.setContractSpecificData(contractSpecificData.getContractSpecificData(), s -> {
					showSuccess("Guardado", "Los datos SEPE han sido actualizados correctamente");

					contrataEmployeeObject.getContractSpecificData(su -> contractSpecificData.setEmployeeContractInfo(
							contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getContractType(),
							contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().isHasTransformation(),
							hasCertificateSEPE,
							contrataEmployeeObject.getContractEmployeeInfo().getEmployeeInfo().getDocument(),
							contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getStartDate(),
							contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getContractId(),
							contrataEmployeeObject.getContractEmployeeInfo().getContractSpecificData()),
							fa -> showError("Error obtenci\u00f3n Datos SEPE", fa.getMessage()));
				}, f -> showError("Error guardando Datos SEPE", f.getMessage()));
			break;
		case 2:
			contrataEmployeeObject.setContractOtherData(contractOtherData.getContractOtherData());
			contrataEmployeeObject.setContractOtherInfo(s -> {
			}, f -> {
			});
			break;
		default:
			break;
		}
	}

	private void onDeleteContract() {
		AonDialog dialog = new AonDialog("BORRADO", getDeleteMessageWidget());
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {
				// Nothing to do here, only hide dialog
			}

			@Override
			public void onAccept() {
				contrataEmployeeObject.delete4EverContract(s -> {
					setChanges(true);
					onListEmployees();
				}, f -> {
				});
			}
		});
	}

	private void movPrevDelete() {
		showLoading("Borrando movimiento previo...");
		contrataEmployeeObject.movPrevDelete(s -> {
			hideMessage();
			loadWindow(su -> {
			});
		}, f -> showError("Error borrado movimiento previo", f.getMessage()));
	}

	private void altaConsolidadaDelete() {
		showLoading("Borrando alta consolidad...");
		contrataEmployeeObject.altaConsolidadaDelete(s -> {
			hideMessage();
			loadWindow(su -> {
			});
		}, f -> showError("Error borrado alta consolidada", f.getMessage()));
	}

	private void onAFIChanges() {
		new EmployeeAFIDialog(contractEmployeeUI.getStartDate(), contractEmployeeUI.getContractType(),
				contractEmployeeUI.getQuoteGroup(), contractEmployeeUI.getOccupation(),
				contractEmployeeUI.getPartialityCoef(), this.contrataEmployeeObject.getContractData().getContractId(),
				this.contrataEmployeeObject.getEmployeeData().getDomain(),
				this.contrataEmployeeObject.getContractData().getWorkplaceId(), false) {

			@Override
			protected void onAcceptCB() {
				loadWindow(su -> {});
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
				// Nothing to do here
			}

			@Override
			protected void onStartContract() {
				// Nothing to do here
			}

		};
	}

	private void onComunicateAFI() {
		new EmployeeAFIDialog(contractEmployeeUI.getStartDate(), contractEmployeeUI.getContractType(),
				contractEmployeeUI.getQuoteGroup(), contractEmployeeUI.getOccupation(),
				contractEmployeeUI.getPartialityCoef(), this.contrataEmployeeObject.getContractData().getContractId(),
				this.contrataEmployeeObject.getEmployeeData().getDomain(),
				this.contrataEmployeeObject.getContractData().getWorkplaceId(), true) {

			@Override
			protected void onAcceptCB() {
				// Nothing to do here
			}

			@Override
			protected void onPartialityCoefContract(String partialityCoef, Date date) {
				showLoading("Comunicando coeficiente parcialidad (TGSS) ...");
				contrataEmployeeObject.cambioCoef(partialityCoef, date,
						s -> showSuccess("AVISO: Parcialidad",
								"El coeficiente de parcialidad ha sido notificado a la Seguridad Social."),
						f -> showError("Error comunicaci\u00F3n", f.getMessage()));
			}

			@Override
			protected void onOcupationContract(String ocupation, Date date) {
				showLoading("Comunicando ocupaci\u00f3n (TGSS) ...");
				contrataEmployeeObject.cambioOcupacion(ocupation, date,
						s -> showSuccess("AVISO: Ocupaci\u00F3n",
								"El cambio de ocupaci\u00F3n ha sido notificado a la Seguridad Social."),
						f -> showError("Error comunicaci\u00F3n", f.getMessage()));
			}

			@Override
			protected void onQuoteContract(String quoteGroup, Date date) {
				showLoading("Comunicando grupo cotizaci\u00f3n (TGSS) ...");
				contrataEmployeeObject.cambioGrupCtz(quoteGroup, date,
						s -> showSuccess("AVISO: Grupo cotizaci\u00F3n",
								"El cambio de grupo de cotizaci\u00F3n ha sido notificado a la Seguridad Social."),
						f -> showError("Error comunicaci\u00F3n", f.getMessage()));
			}

			@Override
			protected void onChangeContract(String contract, Date date) {
				showLoading("Comunicando cambio TC2 (TGSS) ...");
				contrataEmployeeObject.cambioContrato(contract, date,
						s -> showSuccess("AVISO: Tipo contrato",
								"El cambio de tipo de contrato ha sido notificado a la Seguridad Social."),
						f -> showError("Error comunicaci\u00F3n", f.getMessage()));
			}

			@Override
			protected void onEndContract(String settleReason) {
				showLoading("Comunicando baja (TGSS) ...");
				contrataEmployeeObject.sendEmployeeBaja(settleReason, s -> {
					showSuccess("AVISO: Baja", "La baja de este trabajador ha sido notificada a la Seguridad Social.");
					downloadTAEnd();
				}, f -> showError("Error comunicaci\u00F3n", f.getMessage()));
			}

			@Override
			protected void onStartContract() {
				showLoading("Comunicando alta (TGSS) ...");
				contrataEmployeeObject.sendEmployeeAlta(s -> {
					showSuccess("AVISO: Alta", "El alta de este trabajador ha sido notificado a la Seguridad Social.");
					downloadStartDocuments();
				}, f -> showError("Error comunicaci\u00F3n", f.getMessage()));
			}
		};
	}

	private void downloadStartDocuments() {
		showLoading("Descargando TA (Alta) ....");
		contrataEmployeeObject.downloadTa(s -> {
			showSuccess("TA (Alta)",
					"Se ha descargado el TA (Alta) del trabajador. El documento se encuentran en el apartado de Documentos");
			downloadStartIdc();
		}, f -> {
			showError("Error obtenci\u00F3n TA (Alta)", f.getMessage());
			downloadStartIdc();
		}, "ALTA");
	}

	private void downloadStartIdc() {
		Timer timer = new Timer() {
			@Override
			public void run() {
				showLoading("Descargando IDC....");
				contrataEmployeeObject.downloadIdc(null, s -> showSuccess("IDC",
						"Se ha descargado el IDC del trabajador. El documento se encuentran en el apartado de Documentos"),
						f -> showError("Error obtenci\u00F3n IDC", f.getMessage()));
			}
		};
		timer.schedule(2500);
	}

	private void downloadTAEnd() {
		showLoading("Descargando TA...");
		contrataEmployeeObject.downloadTa(s -> showSuccess("TA (Baja)",
				"Se han descargado el TA (Baja) del trabajador. El documento se encuentran en el apartado de Documentos"),
				f -> showError("Error obtenci\u00F3n TA (Baja)", f.getMessage()), "BAJA");
	}

	private void showTa() {
		showLoading("Obteniendo TA...");
		contrataEmployeeObject.downloadTa(dataURI -> {
			hideMessage();
			showPdf();
			pdfViewer.open(dataURI);
		}, f -> showError("Error TA", f.getMessage()), "ALTA");
	}

	private void showTaEnd() {
		showLoading("Obteniendo TA (Baja)...");
		contrataEmployeeObject.downloadTa(dataURI -> {
			hideMessage();
			showPdf();
			pdfViewer.open(dataURI);
		}, f -> showError("Error TA (Baja)", f.getMessage()), "BAJA");
	}

	private void showIdc() {
		showIdc(idcDateListBox.getSelected());
	}

	private void showIdc(Date date) {
		showLoading("Obteniendo IDC...");
		showPdf();
		idcDateListBox.setVisible(true);
		idcDateListBox.getElement().getStyle().setWidth(100, Unit.PCT);
		idcDateListBox.setSelected(date, true);
		contrataEmployeeObject.downloadIdc(date, dataURI -> {
			hideMessage();
//			showPdf();
//			idcDateListBox.setVisible(true);
//			idcDateListBox.getElement().getStyle().setWidth(100, Unit.PCT);
//			idcDateListBox.setSelected(date, true);
			pdfViewer.open(dataURI);
		}, f -> showError("Error IDC", f.getMessage()));
	}

	private void showIdcPlNss() {
		showIdcPlNss(DateUtils.getFirstDayOfMonth());
	}

	private void showIdcPlNss(Date month) {
		showLoading("Obteniendo IDC PL NSS...");
		showPdf();
		idcMonthListBox.setVisible(true);
		idcMonthListBox.getElement().getStyle().setWidth(100, Unit.PCT);
		idcMonthListBox.setSelected(month, true);
		contrataEmployeeObject.downloadIdcPlNss(month, dataURI -> {
			hideMessage();
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
		contrataEmployeeObject.sendBasicCopy(s -> {
			showSuccess("Comunicaci\u00F3n", "La copia basica ha sido notificada correctamente del SEPE");
			downloadCbc();
			loadWindow(su -> {
			});
		}, f -> showError("Error comunicaci\u00F3n", f.getMessage()));
	}

	private void downloadCbc() {
		Timer timer = new Timer() {
			@Override
			public void run() {
				showLoading("Obteniendo CBC....");
				contrataEmployeeObject.downloadCbc(s -> showSuccess("CBC",
						"Se ha descargado el CBC del trabajador. El documento se encuentran en el apartado de Documentos"),
						f -> showError("Error obtenci\u00F3n CBC", f.getMessage()));
			}
		};
		timer.schedule(2500);
	}

	private void sendContract() {
		showLoading("Notificando contrato...");
		contrataEmployeeObject.sendContract(s -> {
			showSuccess("Comunicaci\u00F3n", "El contrato ha sido notificado correctamente del SEPE");
			setVisible(sepeContextMenu.getRemoveContract().getElement(), true);
			downloadCto();
			loadWindow(su -> {
			});
		}, f -> showError("Error comunicaci\u00F3n", f.getMessage()));
	}

	private void downloadCto() {
		Timer timer = new Timer() {
			@Override
			public void run() {
				showLoading("Obteniendo CTO....");
				contrataEmployeeObject.downloadCto(s -> showSuccess("CTO",
						"Se ha descargado el CTO del trabajador. El documento se encuentran en el apartado de Documentos"),
						f -> showError("Error obtenci\u00F3n CTO", f.getMessage()));
			}
		};
		timer.schedule(2500);
	}

	private void sepeIDEContract() {
		showInfo("IDE Sepe", "El IDE del SEPE generado para este contrato es "
				+ contrataEmployeeObject.getContractData().getSepeId());
	}

	private void removeContract() {
		showLoading("Eliminando contrato...");
		contrataEmployeeObject.removeContract(s -> {
			showSuccess("Comunicaci\u00F3n", "El contrato ha sido eliminado correctamente del SEPE");
			loadWindow(su -> {
			});
		}, f -> showError("Error comunicaci\u00F3n", f.getMessage()));
	}

	private void contractExtension() {
		new ContractExtensionDialog(this.contrataEmployeeObject.getContractEmployeeInfo()) {
			@Override
			protected void onExtensionDone() {
				showSuccess("Pr\u00F3rroga", "La pr\u00F3rroga del trabajador "
						+ contrataEmployeeObject.getEmployeeFullName() + " ha sido realizada correctamente");
				loadWindow(su -> {
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
				showSuccess("Transformaci\u00F3n", "La transformaci\u00F3n del trabajador "
						+ contrataEmployeeObject.getEmployeeFullName() + " ha sido realizada correctamente");
				onTransformContract(newContractId);
			}

			@Override
			protected void fireError(Map<String, String> errorMap) {
				AonMessagePanel.showError(messageContainer, errorMap);
			}
		};
	}

	private void sendContractTransform() {
		new ContractTransformSepeDialog() {
			@Override
			protected void onTransformAccept(ContractTransform contractTransform) {
				hide();
				showLoading("Notificando transformaci\u00f3n contrato...");

				contrataEmployeeObject.sendContractTransform(contractTransform,
						s -> showSuccess("Transformaci\u00F3n Contrato",
								"La transformaci\u00F3n del trabajador " + contrataEmployeeObject.getEmployeeFullName()
										+ " ha sido notificada al SEPE correctamente"),
						f -> showError("Error Transformaci\u00F3n Contrato", f.getMessage()));
			}
		};
	}

	private void removeContractTransform() {
		contrataEmployeeObject.removeContractTransform(
				s -> showSuccess("Transformaci\u00F3n Contrato",
						"La transformaci\u00F3n del trabajador " + contrataEmployeeObject.getEmployeeFullName()
								+ " ha sido eliminada del SEPE correctamente"),
				f -> showError("Error Transformaci\u00F3n Contrato", f.getMessage()));
	}

	private void deleteContractExtension() {
		contrataEmployeeObject.deleteContractExtension(s -> {
			showSuccess("Borrado Pr\u00F3rroga", "La pr\u00F3rroga del trabajador "
					+ contrataEmployeeObject.getEmployeeFullName() + " ha sido eliminada correctamente");
			loadWindow(su -> {
			});
		}, f -> {
		});
	}

	// ------------------------------------------------- EmployeeAttachButtons

	private HTMLPanel initEmployeeClauseButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());

		AonToolbarButton saveClauses = new AonToolbarButton(AON.MSG.saveAction() + " Clausulas", AON.CSS.aonIconSave());
		saveClauses.addClickHandler(e -> contractClauseUI.saveClauses());
		hPanel.add(saveClauses);

		AonToolbarButton newClause = new AonToolbarButton(AON.MSG.newAction() + " Clausula", AON.CSS.aonIconAdd());
		newClause.addClickHandler(e -> contractClauseUI.newClause());
		hPanel.add(newClause);

		AonToolbarButton importClause = new AonToolbarButton("Importar Clausula", AON.CSS.aonIconDownload());
		importClause.addClickHandler(e -> contractClauseUI.importClause());
		hPanel.add(importClause);
		
		return hPanel;
	}

	// ------------------------------------------------- EmployeeAttachButtons

	private HTMLPanel initEmployeeAttachButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());

		AonToolbarButton newAttachment = new AonToolbarButton(AON.MSG.newAction() + " Documento", AON.CSS.aonIconAdd());
		newAttachment.addClickHandler(e -> contractAttachUI.newAttachment());
		hPanel.add(newAttachment);

		AonToolbarButton pdfExportBtn = new AonToolbarButton("Generar Borrador Contrato", AON.CSS.aonIconPdf());
		pdfExportBtn.addClickHandler(e -> contractAttachUI.exportContract());
		hPanel.add(pdfExportBtn);

		return hPanel;
	}

	// ------------------------------------------------- EmployeeSalaryButtons

	private HTMLPanel initEmployeeSalaryButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());

		AonToolbarButton deleteButton = new AonToolbarButton("Borrar N\u00F3mina", AON.CSS.aonIconDeleteList());
		deleteButton.addClickHandler(e -> employeeSalary.onDelete());
		hPanel.add(deleteButton);

		AonToolbarButton pdfButton = new AonToolbarButton(AON.MSG.printPDF() + " N\u00F3mina", AON.CSS.aonIconPdf());
		pdfButton.addClickHandler(e -> employeeSalary.onPDF());
		hPanel.add(pdfButton);

		AonToolbarButton pdfSettleButton = new AonToolbarButton("Carta Finiquito", AON.CSS.aonIconPdf());
		pdfSettleButton.addClickHandler(e -> employeeSalary.onPDFSettle());
		pdfSettleButton.setVisible(false);
		hPanel.add(pdfSettleButton);

		AonToolbarButton publishButton = new AonToolbarButton("Drive", AON.CSS.aonIconDrive());
		publishButton.addClickHandler(e -> employeeSalary.onPublish());
		hPanel.add(publishButton);

		AonToolbarButton bidoqPublishButton = new AonToolbarButton("Bidow", "aon-icon-bidoq");
		bidoqPublishButton.addClickHandler(e -> employeeSalary.onBidoqPublish());
		bidoqPublishButton.setVisible(false);
		hPanel.add(bidoqPublishButton);

		AonToolbarButton email = new AonToolbarButton(AON.MSG.email() + " N\u00F3mina", AON.CSS.aonIconEmail());
		email.addClickHandler(e -> employeeSalary.onEmail(e));
		hPanel.add(email);

		return hPanel;
	}

	// ------------------------------------------------- EmployeeCalendarButtons

	private HTMLPanel initEmployeeCalendarButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());

		AonToolbarButton undoAllButton = new AonToolbarButton("Deshacer todo", AON.CSS.aonIconUndoAll());
		undoAllButton.addClickHandler(e -> employeeCalendar.onUndoAll());
		hPanel.add(undoAllButton);

		AonToolbarButton saveButton = new AonToolbarButton(AON.MSG.saveAction() + " Calendario", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> employeeCalendar.onSave());
		hPanel.add(saveButton);

		AonToolbarButton definitionButton = new AonToolbarButton("Definicion", AON.CSS.aonIconEditCalendar());
		definitionButton.addClickHandler(e -> employeeCalendar.onDefinition(e));
		hPanel.add(definitionButton);

		AonToolbarButton utilityButton = new AonToolbarButton("Utilidades", AON.CSS.aonIconSettings());
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

		AonToolbarButton saveContractIrpfButton = new AonToolbarButton(AON.MSG.saveAction() + " Irpf",
				AON.CSS.aonIconSave());
		saveContractIrpfButton.addClickHandler(e -> employeeContractIrpf.onSave());
		hPanel.add(saveContractIrpfButton);

		ListBox yearLBContractIrpf = new ListBox();
		employeeContractIrpf.setYearLB(yearLBContractIrpf);
		hPanel.add(yearLBContractIrpf);

		return hPanel;
	}

	// ------------------------------------------------- CheckStatus
	// (contrataEmployeeObject)

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
						ifSistemaREDError(employeeStatus, ContrataEmployee.this::showFootPanel,
								ContrataEmployee.this::closeFootPanel);

					}, throwable -> {
						closeFootPanel();
						ContrataEmployee.this.setTaVisible(false);
						ContrataEmployee.this.setIdcVisible(false);

					});
				}

				@Override
				protected void cleanEndDate() {
					contractEmployeeUI.employee.setEndDate(null);
				}

				@Override
				protected void cleanOcupation() {
					contractEmployeeUI.employee.setOcupation(null);
				}

				@Override
				protected void updateStartDate(MismatchedStartDate mismatchedStartDate) {
					contractEmployeeUI.employee.setStartDate(mismatchedStartDate.getSsStartDate());
				}

				@Override
				protected void updateOccupation(MismatchedOccupation mismatchedOccupation) {
					if (contractEmployeeUI.employee.occupation.isEnabled())
						contractEmployeeUI.setSelectedValueLBChange(contractEmployeeUI.employee.occupation,
								mismatchedOccupation.getSsOccupation());
					else
						optionNotAllowed();
				}

				@Override
				protected void updateQuoteGroup(MismatchedQuoteGroup mismatchedQuoteGroup) {
					if (contractEmployeeUI.employee.quoteGroup.isEnabled())
						contractEmployeeUI.setSelectedValueLBChange(contractEmployeeUI.employee.quoteGroup,
								mismatchedQuoteGroup.getSsQuoteGroup());
					else
						optionNotAllowed();
				}

				@Override
				protected void updateContractType(MismatchedContractType mismatchedContractType) {
					if (contractEmployeeUI.employee.contractTypeLB.isEnabled())
						contractEmployeeUI.setSelectedValueLBChange(contractEmployeeUI.employee.contractTypeLB,
								mismatchedContractType.getSsContractType());
					else
						optionNotAllowed();
				}

				@Override
				protected void updatePartialFactor(MismatchedPartialFactor mismatchedPartialFactor) {
					// Noting to do here
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

	// ------------------------------------------------- CheckStatus (Auxiliar
	// methods)

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

	private void optionNotAllowed() {
		AonDialog dialog = new AonDialog("Informaci\u00f3n", new HTML("Opci\u00f3n no permitida"));
		dialog.info();
	}

	private void hideFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 20);
	}

	private void showFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 200);
	}

	public void setTaVisible(boolean visible) {
		tgssContextMenu.getTa().setVisible(visible);
	}

	public void setTaEndVisible(boolean visible) {
		tgssContextMenu.getTaEnd().setVisible(visible);
	}

	public void setIdcVisible(boolean visible) {
		initializeIdcDateListBox();
		tgssContextMenu.getIdcPlNss().setVisible(visible);
	}

	private void initializeIdcDateListBox() {
		tgssContextMenu.getIdc().setEnabled(false);
		tgssContextMenu.getIdc().setVisible(false);
		contrataEmployeeObject.getIdcDates(dates -> {
			int count = dates.size();
			idcDateListBox.setRowCount(count, true);
			idcDateListBox.setRowData(0, dates);
			idcDateListBox.setVisibleRange(0, count + 1);
			idcDateListBox.setSelected(count - 1, true);
			idcDateListBox.onResizeDropDownPopup();
			tgssContextMenu.getIdc().setEnabled(true);
			tgssContextMenu.getIdc().setVisible(true);
		}, error -> {
		});
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

		setVisible(sepeContextMenu.getSendBasicCopy().getElement(),
				hasCertificateSEPE && AonStringUtils.isBlank(sepeId));
		setVisible(sepeContextMenu.getSendContract().getElement(),
				hasCertificateSEPE && AonStringUtils.isBlank(sepeId));
		setVisible(sepeContextMenu.getRemoveContract().getElement(),
				hasCertificateSEPE && AonStringUtils.isNotBlank(sepeId));
	}

	private void checkContractExtension() {
		String contractTypeStr = contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getContractType();
		Date endDate = contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getEndDate();
		boolean hasExtension = contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().isHasExtension();

		if (AonStringUtils.isNotBlank(contractTypeStr)) {
			Integer contractTypeValue = Integer.parseInt(contractTypeStr);
			setVisible(sepeContextMenu.getContractExtension().getElement(),
					!hasExtension && contractTypeValue >= 400 && null != endDate);
			setVisible(sepeContextMenu.getRemoveContractExtension().getElement(), hasExtension);
		}
	}

	private void checkContractTransform() {
		String contractTypeStr = contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getContractType();

		if (AonStringUtils.isNotBlank(contractTypeStr)) {
			Integer contractTypeValue = Integer.parseInt(contractTypeStr);
			setVisible(sepeContextMenu.getContractExtension().getElement(), contractTypeValue >= 400);
		}

		setVisible(sepeContextMenu.getContractExtension().getElement(), !Boolean.TRUE
				.equals(contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().isHasTransformation()));
		setVisible(sepeContextMenu.getRemoveContractExtension().getElement(), !Boolean.TRUE
				.equals(contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().isHasTransformation()));
		setVisible(sepeContextMenu.getContractTransform().getElement(), !Boolean.TRUE
				.equals(contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().isHasTransformation()));
		setVisible(sepeContextMenu.getSendBasicCopy().getElement(), !Boolean.TRUE
				.equals(contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().isHasTransformation()));
		setVisible(sepeContextMenu.getSendContract().getElement(), !Boolean.TRUE
				.equals(contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().isHasTransformation()));
		setVisible(sepeContextMenu.getSendContractTransform().getElement(), Boolean.TRUE
				.equals(contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().isHasTransformation()));
		setVisible(sepeContextMenu.getRemoveContractTransform().getElement(), Boolean.TRUE
				.equals(contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().isHasTransformation()));

	}

	// ------------------------------------------------- TGSS status

	private void checkTGSSStatus() {
		Date startDate = contrataEmployeeObject.getContractData().getStartDate();
		Date endDate = contrataEmployeeObject.getContractData().getEndDate();

		setVisible(tgssContextMenu.getTaEnd().getElement(), null != endDate);
		setVisible(tgssContextMenu.getAltaConsolidadaDelete().getElement(),
				DateUtils.isAfterOrEquals(new Date(), startDate));
		setVisible(tgssContextMenu.getComunicateAFI().getElement(), isComunica);
	}

	public void setIsComunica(boolean isComunica) {
		this.isComunica = isComunica;
	}

	// ------------------------------------------------- Delete Message Panel

	private Widget getDeleteMessageWidget() {
		ContractInfo contractData = contrataEmployeeObject.getContractData();
		EmployeeInfo employeeData = contrataEmployeeObject.getEmployeeData();

		StringBuilder message = new StringBuilder();

		message.append(
				"Este contrato ser\u00E1 eliminado de forma permanente.<br> \u00BFDesea eliminar el contrato de <b>"
						+ employeeData.getFullName() + "</b>?");

		if (null != contractData.getSalariesCount() && contractData.getSalariesCount() > 0) {
			message = new StringBuilder();
			message.append(
					"Este contrato contiene n\u00F3minas existentes. Si lo elimina, se borrar\u00E1n todos los datos de este contrato incluidas las n\u00F3minas.<br> \u00BFDesea eliminar el contrato de <b>"
							+ employeeData.getFullName() + "</b>? <br><br>");
			message.append("<b>N\u00F3minas:</b><br><br>");
			for (ContractSalaryInfo salaryInfo : contractData.getContractSalariesInfo())
				message.append(
						"&emsp;" + salaryInfo.getType() + "&emsp;(" + formatFullDate.format(salaryInfo.getStart())
								+ " - " + formatFullDate.format(salaryInfo.getEnd()) + ")&emsp;Percibido : "
								+ salaryInfo.getTotalLiquid() + "\u20AC<br>");
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

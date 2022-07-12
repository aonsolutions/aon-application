package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.ifSistemaREDError;

import java.util.Collections;
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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckPanel;
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
		protected void hideMessagePanel() {
			hideMessage();
		}

		@Override
		protected void downloadCtoDocument() {
			downloadCto(s -> {}, f -> {});
		}
	}

	// ------------------------------------------------- ContractClausesUIImpl

	public class ContractOtherDataImpl extends ContractOtherData {

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
		protected void onContractPDF() {
			contrataEmployeeObject.setContractOtherInfo(s -> onExportContractPDF(), f -> {
			});
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
		
		@Override
		protected void showLoadingMessage(String message) {
			showLoading(message);
		}

		@Override
		protected void showAttachPDf(Integer attachIdIn, String dataURIIn) {
			showPdf();
			hideMessage();
			attachId = attachIdIn;
			dataURI = dataURIIn;
			pdfViewer.open(dataURI);
			idcDateListBox.setVisible(false);
			idcMonthListBox.setVisible(false);
//			saveDocument.setVisible(true);
			
		}

		@Override
		protected void showSuccessMessagePDF(String title, String message) {
			showSuccessPDF(title, message);
		}

		@Override
		protected void showLoadingMessagePDF(String message) {
			showLoadingPDF(message);
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
			new EmployeePeculiaritiesDialog(contrataEmployeeObject.getContractId(), contrataEmployeeObject.getContractStartDate()) {
					@Override
					protected void onAccept() {
						// Nothing to refresh
					}
			};
		}
	}

	class MovPrevDeleteCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AonDialog dialog = new AonDialog("Movimiento Previo",
					new HTML("\u00bfDesea realmente eliminar el movimiento previo\u003f"));
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
			AonDialog dialog = new AonDialog("Alta Consolidada",
					new HTML("\u00bfDesea realmente eliminar el alta consolidada\u003f"));
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

	class TGSSContextMenu extends ContextMenu {

		private MenuItem afi;
		private MenuItem peculiarities;
		
		private MenuItem ta;
		private MenuItem taEnd;
		
		private MenuItem idc;
		private MenuItem idcPlNss;

		MenuItemSeparator separatorComunicate;
		
		private MenuItem altaConsolidadaDelete;
		private MenuItem comunicateAFI;
		
		public TGSSContextMenu() {

			afi = addMenuItem("Cambios AFI", new AFICommand(), AON.CSS.aonIconTgss(), "afi");
			peculiarities = addMenuItem("Peculiaridades de cotizaci\u00F3n", new PeculiaritiesCommand(), AON.CSS.aonIconTgss(), "peculiarities");
			
			addSeparator();

			ta = addMenuItem("Duplicados de Documentos TA", new TACommand(), AON.CSS.aonIconPdf(), "ta");
			taEnd = addMenuItem("Duplicados de Documentos TA (Baja)", new TAEndCommand(), AON.CSS.aonIconPdf(), "taEnd");

			idc = addMenuItem("IDC-Trab Cuenta Ajena", new IDCCommand(), AON.CSS.aonIconPdf(), "idc");;
			idcPlNss = addMenuItem("IDC/Periodo Liquidaci\u00F3n-NSS", new IDCPlNssCommand(), AON.CSS.aonIconPdf(), "idcPlNss");

			separatorComunicate = addSeparator();

			altaConsolidadaDelete = addMenuItem("Eliminar alta consolidada", new AltaConsolidadaDeleteCommand(), AON.CSS.aonIconSend(), "altaConsolidadaDelete");
			comunicateAFI = addMenuItem("Notificaci\u00f3n AFI (TGSS)", new ComunicateAFICommand(), AON.CSS.aonIconSend(), "comunicateAFI");
			
		}
		
		private MenuItem addMenuItem(String title, ScheduledCommand command, String iconStyle, String debugId) {
			MenuItem item = addItem(title, command, iconStyle, AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			item.ensureDebugId(debugId);
			return item;
		}
		
		public void setIsComunica(boolean isComunica) {
			separatorComunicate.setVisible(isComunica);
			altaConsolidadaDelete.setVisible(isComunica);
			comunicateAFI.setVisible(isComunica);
		}
		
		public void setEndDate(Date endDate) {
			taEnd.setVisible(endDate != null);
		}
		
		public void setStartDate(Date startDate) {
			altaConsolidadaDelete.setVisible(DateUtils.isAfterOrEquals(new Date(), startDate));
		}
		
		public void setPrevAlta(boolean prevAlta) {
			idcPlNss.setVisible(!prevAlta);
		}

		public MenuItem getTa() {
			return ta;
		}

		public MenuItem getTaEnd() {
			return taEnd;
		}

		public MenuItem getIdc() {
			return idc;
		}

		public MenuItem getIdcPlNss() {
			return idcPlNss;
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
			AonDialog dialog = new AonDialog("Notificar copia basica",
					new HTML("\u00bfDesea realmente notificar la copia basica\u003f"));
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
			AonDialog dialog = new AonDialog("Notificar contrato",
					new HTML("\u00bfDesea realmente notificar el contrato\u003f"));
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

	class ContractExtensionCommand implements ScheduledCommand {

		@Override
		public void execute() {
			contractExtension();
		}

	}

	class DeleteContractExtensionCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AonDialog dialog = new AonDialog("Pr\u00F3rroga contrato",
					new HTML("\u00bfDesea realmente eliminar la pr\u00f3 del contrato\u003f"));
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
			AonDialog dialog = new AonDialog("Notifici\u00f3n transformaci\u00F3n contrato",
					new HTML("\u00bfDesea realmente notificar la transformaci\u00f3n del contrato\u003f"));
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					// Nothing to do here
				}

				@Override
				public void onAccept() {
					showLoading("Notificando transformaci\u00f3n contrato...");

					contrataEmployeeObject.sendContractTransform(
							s -> {
								showSuccess("Transformaci\u00F3n Contrato",
									"La transformaci\u00F3n del trabajador " + contrataEmployeeObject.getEmployeeFullName()
											+ " ha sido notificada al SEPE correctamente");
								contrataEmployeeObject.getComunicationInfo();
							}, f -> showError("Error Transformaci\u00F3n Contrato", f.getMessage()));
				}
			});
		}

	}

	class RemoveContractCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AonDialog dialog = new AonDialog("Notifici\u00f3n contrato",
					new HTML("\u00bfDesea realmente eliminar el contrato del SEPE\u003f"));
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

	class SEPEContextMenu extends ContextMenu {

		private MenuItem cto;
		private MenuItem cbc;
		
		MenuItemSeparator separatorCertifica;
		
		private MenuItem cetifica2;
		private MenuItem cetifica2PDF;
		
		MenuItemSeparator separatorAdds;

		private MenuItem contractExtension;
		private MenuItem contractTransform;
		private MenuItem removeContractExtension;

		MenuItemSeparator separatorComunicate;
		
		private MenuItem sendBasicCopy;
		private MenuItem sendContract;
		private MenuItem sendContractTransform;

		private MenuItem removeContract;
		private MenuItem removeContractTransform;

		public SEPEContextMenu() {

			cto = addMenuItem("Copia Contrato", new CTOCommand(), AON.CSS.aonIconPdf(), "cto");;
			cbc = addMenuItem("Copia B\u00E1sica", new CBCCommand(), AON.CSS.aonIconPdf(), "cbc");;
			
			separatorCertifica = addSeparator();

			cetifica2 = addMenuItem("Cetifica2", new Certifica2Command(), AON.CSS.aonIconSepe(), "cetifica2");
			cetifica2PDF = addMenuItem("Cetifica2 PDF", new Certifica2PDFCommand(), AON.CSS.aonIconPdf(), "cetifica2PDF");

			separatorAdds = addSeparator();

			contractExtension = addMenuItem("Pr\u00F3rroga Contrato", new ContractExtensionCommand(), AON.CSS.aonIconSepe(), "contractExtension");
			removeContractExtension = addMenuItem("Eliminar Pr\u00F3rroga Contrato", new DeleteContractExtensionCommand(), AON.CSS.aonIconSepe(), "removeContractExtension");
			contractTransform = addMenuItem("Transformaci\u00F3n Contrato", new ContractTransformCommand(), AON.CSS.aonIconSepe(), "contractTransform");

			separatorComunicate = addSeparator();

			sendBasicCopy = addMenuItem("Notificar Copia B\u00E1sica", new SendBasicCopyCommand(), AON.CSS.aonIconSend(), "sendBasicCopy");
			sendContract = addMenuItem("Notificar Contrato", new SendContractCommand(), AON.CSS.aonIconSend(), "sendContract");
			sendContractTransform = addMenuItem("Notificar Transformaci\u00f3n Contrato", new SendContractTransformCommand(), AON.CSS.aonIconSend(), "sendContractTransform");

			removeContract = addMenuItem("Eliminar Contrato", new RemoveContractCommand(), AON.CSS.aonIconSend(), "removeContract");
			removeContractTransform = addMenuItem("Eliminar Transformaci\u00f3n Contrato", new RemoveContractTransformCommand(), AON.CSS.aonIconSend(), "removeContractTransform");
		}
		
		private MenuItem addMenuItem(String title, ScheduledCommand command, String iconStyle, String debugId) {
			MenuItem item = addItem(title, command, iconStyle, AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			item.ensureDebugId(debugId);
			return item;
		}
		
		public void setIsComunica(boolean isComunica) {
			separatorComunicate.setVisible(isComunica);
			sendBasicCopy.setVisible(isComunica);
			sendContract.setVisible(isComunica);
			sendContractTransform.setVisible(isComunica);
			removeContract.setVisible(isComunica);
			removeContractTransform.setVisible(isComunica);
		}

		public void setIsExtension() {
			contractExtension.setVisible(true);
			removeContractExtension.setVisible(true);
			sendBasicCopy.setVisible(true);
			sendContract.setVisible(true);
			
			contractTransform.setVisible(false);
			sendContractTransform.setVisible(false);
			removeContractTransform.setVisible(false);
		}
		
		public void setCanExtension(boolean canExtension) {
			contractExtension.setVisible(canExtension);
		}
		
		public void setCanTransform(boolean canTranform) {
			contractTransform.setVisible(canTranform);
		}
		
		public void setIsTransform() {
			contractExtension.setVisible(false);
			removeContractExtension.setVisible(false);
			sendContract.setVisible(false);
			
			contractTransform.setVisible(false);
			sendBasicCopy.setVisible(true);
			sendContractTransform.setVisible(true);
			removeContractTransform.setVisible(true);
		}
		
		public void setDafaultContract() {
			contractExtension.setVisible(true);
			removeContractExtension.setVisible(true);
			contractTransform.setVisible(false);
			sendContractTransform.setVisible(false);
			removeContractTransform.setVisible(false);
			sendBasicCopy.setVisible(true);
			sendContract.setVisible(true);
		}
		
		public void setEndDate(Date endDate) {
			separatorCertifica.setVisible(endDate != null);
			cetifica2.setVisible(endDate != null);
			cetifica2PDF.setVisible(endDate != null);
		}
		
		public void setHasCTO(boolean hasCTO) {
			sendContract.setVisible(!hasCTO);
			removeContract.setVisible(hasCTO);
		}
		
		public void setHasCBC(boolean hasCBC) {
			sendBasicCopy.setVisible(!hasCBC);
		}
		
		public MenuItemSeparator getSeparatorAdds() {
			return separatorAdds;
		}

	}

	// ------------------------------------------------- ScheduledCommand (ContractAttach)
	
	class ExportContractCommand implements ScheduledCommand {

		@Override
		public void execute() {
			contractAttachUI.exportContract();
		}
	}
	
	class ModificationPDFCommand implements ScheduledCommand {

		@Override
		public void execute() {
			contractAttachUI.modificationPDF();
		}
	}

	class AttachContextMenu extends ContextMenu {

		private MenuItem exportContract;
		private MenuItem modificationPDF;
		
		public AttachContextMenu() {
			exportContract = addMenuItem("Borrador Contrato", new ExportContractCommand(), AON.CSS.aonIconPdf(), "exportContract");
			modificationPDF = addMenuItem("Notificaci\u00f3n Laboral", new ModificationPDFCommand(), AON.CSS.aonIconPdf(), "modificationPDF");	
		}
		
		private MenuItem addMenuItem(String title, ScheduledCommand command, String iconStyle, String debugId) {
			MenuItem item = addItem(title, command, iconStyle, AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			item.ensureDebugId(debugId);
			return item;
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

	@UiField
	DeckPanel toolbarDeckPanel;
	
	@UiField(provided = true)
	AonToolbar toolbar;
	
	@UiField(provided = true)
	AonToolbar toolbarPDFViewer;

	@UiField
	DeckPanel mainDeckPanel;
	
	@UiField
	HTMLPanel messageContainer;
	
	@UiField
	HTMLPanel messagePDFContainer;

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
	
	private static String BLANK_PAGE = "data:@file/pdf;base64,JVBERi0xLjYNJeLjz9MNCjI0IDAgb2JqDTw8L0ZpbHRlci9GbGF0ZURlY29kZS9GaXJzdCA0L0xlbmd0aCAyMTYvTiAxL1R5cGUvT2JqU3RtPj5zdHJlYW0NCmjePI9RS8MwFIX/yn1bi9jepCQ6GYNpFBTEMsW97CVLbjWYNpImmz/fVsXXcw/f/c4SEFarepPTe4iFok8dU09DgtDBQx6TMwT74vaLTE7uSPDUdXM0Xe/73r1FnVwYYEtHR6d9WdY3kX4ipRMV6oojSmxQMoGyac5RLBAXf63p38aGA7XPorLewyvFcYaJile8rB+D/YcwiRdMMGScszO8/IW0MdhsaKKYGA46gXKTr/cUQVY4We/cYMNpnLVeXPJUXHs9fECr7kAFk+eZ5Xr9LcAAfKpQrA0KZW5kc3RyZWFtDWVuZG9iag0yNSAwIG9iag08PC9GaWx0ZXIvRmxhdGVEZWNvZGUvRmlyc3QgNC9MZW5ndGggNDkvTiAxL1R5cGUvT2JqU3RtPj5zdHJlYW0NCmjeslAwULCx0XfOL80rUTDU985MKY42NAIKBsXqh1QWpOoHJKanFtvZAQQYAN/6C60NCmVuZHN0cmVhbQ1lbmRvYmoNMjYgMCBvYmoNPDwvRmlsdGVyL0ZsYXRlRGVjb2RlL0ZpcnN0IDkvTGVuZ3RoIDQyL04gMi9UeXBlL09ialN0bT4+c3RyZWFtDQpo3jJTMFAwVzC0ULCx0fcrzS2OBnENFIJi7eyAIsH6LnZ2AAEGAI2FCDcNCmVuZHN0cmVhbQ1lbmRvYmoNMjcgMCBvYmoNPDwvRmlsdGVyL0ZsYXRlRGVjb2RlL0ZpcnN0IDUvTGVuZ3RoIDEyMC9OIDEvVHlwZS9PYmpTdG0+PnN0cmVhbQ0KaN4yNFIwULCx0XfOzytJzSspVjAyBgoE6TsX5Rc45VdEGwB5ZoZGCuaWRrH6vqkpmYkYogGJRUCdChZgfUGpxfmlRcmpxUAzA4ryk4NTS6L1A1zc9ENSK0pi7ez0g/JLEktSFQz0QyoLUoF601Pt7AACDADYoCeWDQplbmRzdHJlYW0NZW5kb2JqDTIgMCBvYmoNPDwvTGVuZ3RoIDM1MjUvU3VidHlwZS9YTUwvVHlwZS9NZXRhZGF0YT4+c3RyZWFtDQo8P3hwYWNrZXQgYmVnaW49Iu+7vyIgaWQ9Ilc1TTBNcENlaGlIenJlU3pOVGN6a2M5ZCI/Pgo8eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIiB4OnhtcHRrPSJBZG9iZSBYTVAgQ29yZSA1LjQtYzAwNSA3OC4xNDczMjYsIDIwMTIvMDgvMjMtMTM6MDM6MDMgICAgICAgICI+CiAgIDxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOnBkZj0iaHR0cDovL25zLmFkb2JlLmNvbS9wZGYvMS4zLyIKICAgICAgICAgICAgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIgogICAgICAgICAgICB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIKICAgICAgICAgICAgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIj4KICAgICAgICAgPHBkZjpQcm9kdWNlcj5BY3JvYmF0IERpc3RpbGxlciA2LjAgKFdpbmRvd3MpPC9wZGY6UHJvZHVjZXI+CiAgICAgICAgIDx4bXA6Q3JlYXRlRGF0ZT4yMDA2LTAzLTA2VDE1OjA2OjMzLTA1OjAwPC94bXA6Q3JlYXRlRGF0ZT4KICAgICAgICAgPHhtcDpDcmVhdG9yVG9vbD5BZG9iZVBTNS5kbGwgVmVyc2lvbiA1LjIuMjwveG1wOkNyZWF0b3JUb29sPgogICAgICAgICA8eG1wOk1vZGlmeURhdGU+MjAxNi0wNy0xNVQxMDoxMjoyMSswODowMDwveG1wOk1vZGlmeURhdGU+CiAgICAgICAgIDx4bXA6TWV0YWRhdGFEYXRlPjIwMTYtMDctMTVUMTA6MTI6MjErMDg6MDA8L3htcDpNZXRhZGF0YURhdGU+CiAgICAgICAgIDx4bXBNTTpEb2N1bWVudElEPnV1aWQ6ZmYzZGNmZDEtMjNmYS00NzZmLTgzOWEtM2U1Y2FlMmRhMmViPC94bXBNTTpEb2N1bWVudElEPgogICAgICAgICA8eG1wTU06SW5zdGFuY2VJRD51dWlkOjM1OTM1MGIzLWFmNDAtNGQ4YS05ZDZjLTAzMTg2YjRmZmIzNjwveG1wTU06SW5zdGFuY2VJRD4KICAgICAgICAgPGRjOmZvcm1hdD5hcHBsaWNhdGlvbi9wZGY8L2RjOmZvcm1hdD4KICAgICAgICAgPGRjOnRpdGxlPgogICAgICAgICAgICA8cmRmOkFsdD4KICAgICAgICAgICAgICAgPHJkZjpsaSB4bWw6bGFuZz0ieC1kZWZhdWx0Ij5CbGFuayBQREYgRG9jdW1lbnQ8L3JkZjpsaT4KICAgICAgICAgICAgPC9yZGY6QWx0PgogICAgICAgICA8L2RjOnRpdGxlPgogICAgICAgICA8ZGM6Y3JlYXRvcj4KICAgICAgICAgICAgPHJkZjpTZXE+CiAgICAgICAgICAgICAgIDxyZGY6bGk+RGVwYXJ0bWVudCBvZiBKdXN0aWNlIChFeGVjdXRpdmUgT2ZmaWNlIG9mIEltbWlncmF0aW9uIFJldmlldyk8L3JkZjpsaT4KICAgICAgICAgICAgPC9yZGY6U2VxPgogICAgICAgICA8L2RjOmNyZWF0b3I+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgCjw/eHBhY2tldCBlbmQ9InciPz4NCmVuZHN0cmVhbQ1lbmRvYmoNMTEgMCBvYmoNPDwvTWV0YWRhdGEgMiAwIFIvUGFnZUxhYmVscyA2IDAgUi9QYWdlcyA4IDAgUi9UeXBlL0NhdGFsb2c+Pg1lbmRvYmoNMjMgMCBvYmoNPDwvRmlsdGVyL0ZsYXRlRGVjb2RlL0xlbmd0aCAxMD4+c3RyZWFtDQpIiQIIMAAAAAABDQplbmRzdHJlYW0NZW5kb2JqDTI4IDAgb2JqDTw8L0RlY29kZVBhcm1zPDwvQ29sdW1ucyA0L1ByZWRpY3RvciAxMj4+L0ZpbHRlci9GbGF0ZURlY29kZS9JRFs8REI3Nzc1Q0NFMjI3RjZCMzBDNDQwREY0MjIxREMzOTA+PEJGQ0NDRjNGNTdGNjEzNEFCRDNDMDRBOUU0Q0ExMDZFPl0vSW5mbyA5IDAgUi9MZW5ndGggODAvUm9vdCAxMSAwIFIvU2l6ZSAyOS9UeXBlL1hSZWYvV1sxIDIgMV0+PnN0cmVhbQ0KaN5iYgACJjDByGzIwPT/73koF0wwMUiBWYxA4v9/EMHA9I/hBVCxoDOQeH8DxH2KrIMIglFwIpD1vh5IMJqBxPpArHYgwd/KABBgAP8bEC0NCmVuZHN0cmVhbQ1lbmRvYmoNc3RhcnR4cmVmDQo0NTc2DQolJUVPRg0K";

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
	private AonToolbarButton saveDocument;

	private TGSSContextMenu tgssContextMenu;
	private SEPEContextMenu sepeContextMenu;
	private AttachContextMenu attachContextMenu;

	// ContractOtherData
	private HTMLPanel employeeSepeButtons;

	// ContractOtherData
	private HTMLPanel employeeOtherDataButtons;

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
	private boolean hasPayroll = false;
	
	// PDF Save
	Integer attachId;
	String dataURI;

	// ------------------------------------------------- Constructor

	protected ContrataEmployee() {

		// Init Tabs Elements
		contractEmployeeUI = new ContractEmployeeUIImpl();
		contractSpecificData = new ContractSpecificDataImpl();
		contractOtherData = new ContractOtherDataImpl();
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
		this.toolbarPDFViewer = new AonToolbar("Contrato");

		// Init Widget
		initWidget(uiBinder.createAndBindUi(this));

		// Init toolbar
		getToolbarPanel();
		getToolbarPDFViewerPanel();

		// Show contract buttons
		showContractButtons();

		// Init ContextMenu
		tgssContextMenu = new TGSSContextMenu();
		sepeContextMenu = new SEPEContextMenu();
		attachContextMenu = new AttachContextMenu();

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

		addSelectionHandler();
	}

	private void addSelectionHandler() {
		tabLayOutPanel.addSelectionHandler(e -> loadWindow(s -> {}));
	}

	private void loadWindow(Consumer<Void> finish) {
		checkPayrollTabs();
		showLoadingPanel();
		loadData(s -> {
			loadToolbar();
			checkButtonsToolbar();
			hideMessage();
			finish.accept(null);
		});
	}

	private void checkPayrollTabs() {
		if(hasPayroll) return;
		
		tabLayOutPanel.remove(7); // IRPF Tab
		tabLayOutPanel.remove(6); // Calendar Tab
		tabLayOutPanel.remove(5); // Nominas Tab
	}

	private void loadData(Consumer<Void> finish) {
		Integer tabIdx = tabLayOutPanel.getSelectedIndex();
		switch (tabIdx) {
		case 0:
			contractEmployeeUI.setContrataEmployeeObject(this.contrataEmployeeObject, this.contractId, success -> {
				// Check SS only if not RETA
				Byte ssRegime = contrataEmployeeObject.getContractData().getSsRegimen();
				if (null == ssRegime || ssRegime != 3)
					checkStatus(this.contrataEmployeeObject);

				finish.accept(null);
			});
			break;
		case 1:
			contractSpecificData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo(), hasCertificateSEPE);
			finish.accept(null);
			break;
		case 2:
			contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
			finish.accept(null);
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
			contrataEmployeeObject.getEmployeeCalendarObject(calendarObject -> employeeCalendar.setEmployeeCalendarDraftObject(calendarObject));
			finish.accept(null);
			break;
		case 7:
			contrataEmployeeObject.getEmployeeContractIrpfObject(irpfObject -> employeeContractIrpf.setEmployeeContractIrpfObject(irpfObject));
			finish.accept(null);
			break;
		default:
			finish.accept(null);
			break;
		}
	}

	private void loadToolbar() {
		switch (tabLayOutPanel.getSelectedIndex()) {
			case 1:
				showSepeButtons();
				break;
			case 2:
				showContractOtherDataButtons();
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
				showContractButtons();
		}
	}

	private void checkButtonsToolbar() {
		Integer tabIdx = tabLayOutPanel.getSelectedIndex();
		if(tabIdx == 0) {
			checkTgssContextMenu();
		} else if (tabIdx == 1) {
			checkSepeContextMenu();
		}
	}

	// ------------------------------------------------- Show/Hide Employee/PDF

	private void showEmployee() {
		toolbarDeckPanel.showWidget(0);
		mainDeckPanel.showWidget(0);
		showMessageContainer();
	}

	private void showPdf() {
		toolbarDeckPanel.showWidget(1);
		mainDeckPanel.showWidget(1);
		idcDateListBox.setVisible(false);
		idcMonthListBox.setVisible(false);
		saveDocument.setVisible(false);
		dataURI = null;
		attachId = null;
		showMessagePDFContainer();
	}
	
	private void showBlakPdf() {
		pdfViewer.open(BLANK_PAGE);
	}
	
	private void onClosePDF() {
		showEmployee();
	}
	
	private void showMessageContainer() {
		messageContainer.setVisible(true);
		messagePDFContainer.setVisible(false);
	}

	private void showMessagePDFContainer() {
		messagePDFContainer.setVisible(true);
		messageContainer.setVisible(false);
	}

	// ------------------------------------------------- setContrataEmployeeObject

	public void setContrataEmployeeObject(ContrataEmployeeObject contrataEmployeeDialogObject, Integer contractId,
			Integer selectedEmployeeIdx, int employeesSize, Consumer<String> success) {

		this.contractId = contractId;
		this.contrataEmployeeObject = contrataEmployeeDialogObject;
		this.tabLayOutPanel.selectTab(0, false);
		
		// Get Idc Dates
		initializeIdcDateListBox();

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
		
		// Get Idc Dates
		initializeIdcDateListBox();

		loadWindow(s -> {
			employeeCounter.setText(selectedEmployeeIdx + " de " + employeesSize);
			tabLayOutPanel.selectTab(selectedTab, true);
			success.accept("");
		});
	}

	// ------------------------------------------------- Show/Hide Toolbar methods

	private void showContractButtons() {
		employeeContractButtons.setVisible(true);
		employeeSepeButtons.setVisible(false);
		employeeOtherDataButtons.setVisible(false);
		employeeClauseButtons.setVisible(false);
		employeeAttachButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
	}

	private void showSepeButtons() {
		employeeContractButtons.setVisible(false);
		employeeSepeButtons.setVisible(true);
		employeeOtherDataButtons.setVisible(false);
		employeeClauseButtons.setVisible(false);
		employeeAttachButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
	}

	private void showContractOtherDataButtons() {
		employeeContractButtons.setVisible(false);
		employeeSepeButtons.setVisible(false);
		employeeOtherDataButtons.setVisible(true);
		employeeClauseButtons.setVisible(false);
		employeeAttachButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
	}

	private void showContractClauseButtons() {
		employeeContractButtons.setVisible(false);
		employeeSepeButtons.setVisible(false);
		employeeOtherDataButtons.setVisible(false);
		employeeClauseButtons.setVisible(true);
		employeeAttachButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
	}

	private void showContractAttachButtons() {
		employeeContractButtons.setVisible(false);
		employeeSepeButtons.setVisible(false);
		employeeOtherDataButtons.setVisible(false);
		employeeClauseButtons.setVisible(false);
		employeeAttachButtons.setVisible(true);
		employeeSalaryButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
	}

	private void showSalariesButtons() {
		employeeContractButtons.setVisible(false);
		employeeSepeButtons.setVisible(false);
		employeeOtherDataButtons.setVisible(false);
		employeeClauseButtons.setVisible(false);
		employeeAttachButtons.setVisible(false);
		employeeSalaryButtons.setVisible(true);
		employeeCalendarButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(false);
	}

	private void showCalendarButtons() {
		employeeContractButtons.setVisible(false);
		employeeSepeButtons.setVisible(false);
		employeeOtherDataButtons.setVisible(false);
		employeeClauseButtons.setVisible(false);
		employeeAttachButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeCalendarButtons.setVisible(true);
		employeeContractIrpfButtons.setVisible(false);
	}

	private void showContractIrpfButtons() {
		employeeContractButtons.setVisible(false);
		employeeSepeButtons.setVisible(false);
		employeeOtherDataButtons.setVisible(false);
		employeeClauseButtons.setVisible(false);
		employeeAttachButtons.setVisible(false);
		employeeSalaryButtons.setVisible(false);
		employeeCalendarButtons.setVisible(false);
		employeeContractIrpfButtons.setVisible(true);
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

		// EmployeeSepeButtons

		employeeSepeButtons = initEmployeeSepeButtons();
		toolbar.add(employeeSepeButtons);

		// EmployeeOtherData

		employeeOtherDataButtons = initEmployeeOtherDataButtons();
		toolbar.add(employeeOtherDataButtons);

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
	
	// ------------------------------------------------- Toolbar PDFViewer panel
	
	private AonToolbar getToolbarPDFViewerPanel() {

		closePDF = new AonToolbarButton(AON.MSG.closed(), AON.CSS.aonIconClose());
		closePDF.addClickHandler(e -> onClosePDF());
		toolbarPDFViewer.add(closePDF);
		
		saveDocument = new AonToolbarButton("Guardar documento", AON.CSS.aonIconSave());
		saveDocument.addClickHandler(e -> contractAttachUI.setAttachData(attachId, pdfViewer.getData()));
		toolbarPDFViewer.add(saveDocument);
		
		idcMonthListBox = new MonthListBox();
		idcMonthListBox.addChangeHandler(e -> showIdcPlNss(idcMonthListBox.getSelectedMonth()));
		toolbarPDFViewer.add(idcMonthListBox);

		idcDateListBox = new DateListBox();
		idcDateListBox.addChangeHandler(e -> showIdc(idcDateListBox.getSelectedDate()));
		toolbarPDFViewer.add(idcDateListBox);

		return toolbarPDFViewer;
	}

	// ------------------------------------------------- Toolbar panel (Auxiliar Methods)

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

		return hPanel;
	}

	// ------------------------------------------------- EmployeeContractButtons (Auxiliar methods)

	public void setChanges(boolean changes) {
		this.changes = changes;
	}

	public boolean getChanges() {
		return changes;
	}

	private void onSaveContract() {
		setChanges(true);

		Map<String, String> messageMap = contractEmployeeUI.checkSaveAndGetErrors();
		if (messageMap.isEmpty())
			contrataEmployeeObject.setEmployeeContract(s -> {
				showSuccess("Guardado", "El contrato " + contrataEmployeeObject.getEmployeeFullName()
						+ " ha sido actualizado correctamente");
				loadWindow(su -> {
					checkButtonsToolbar();
					checkStatus(contrataEmployeeObject);
				});
			}, f -> {
			});
		else
			AonMessagePanel.showError(messageContainer, messageMap);
	}

	private void onAFIChanges() {
		new EmployeeAFIDialog(contractEmployeeUI.getStartDate(), contractEmployeeUI.getContractType(),
				contractEmployeeUI.getQuoteGroup(), contractEmployeeUI.getOccupation(),
				contractEmployeeUI.getPartialityCoef(), this.contrataEmployeeObject.getContractData().getContractId(),
				this.contrataEmployeeObject.getEmployeeData().getDomain(),
				this.contrataEmployeeObject.getContractData().getWorkplaceId(),
				this.contrataEmployeeObject.getContractData().isHasTransformation(),
				false) {

			@Override
			protected void onAcceptCB() {
				loadWindow(su -> {
				});
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
				this.contrataEmployeeObject.getContractData().getWorkplaceId(), 
				this.contrataEmployeeObject.getContractData().isHasTransformation(),
				true) {

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
		contrataEmployeeObject.downloadTa(
			s -> {
				showSuccess("TA (Alta)", "Se ha descargado el TA (Alta) del trabajador. El documento se encuentran en el apartado de Documentos");
				downloadStartIdc();
			}, 
			f -> {
				showError("Error obtenci\u00F3n TA (Alta)", f.getMessage());
				downloadStartIdc();
			}, 
			"ALTA");
	}

	private void downloadStartIdc() {
		Timer timer = new Timer() {
			@Override
			public void run() {
				showLoading("Descargando IDC....");
				contrataEmployeeObject.downloadIdc(
						null, 
						s -> showSuccess("IDC", "Se ha descargado el IDC del trabajador. El documento se encuentran en el apartado de Documentos"),
						f -> showError("Error obtenci\u00F3n IDC", f.getMessage()));
			}
		};
		timer.schedule(2500);
	}

	private void downloadTAEnd() {
		showLoading("Descargando TA...");
		contrataEmployeeObject.downloadTa(
				s -> showSuccess("TA (Baja)", "Se han descargado el TA (Baja) del trabajador. El documento se encuentran en el apartado de Documentos"),
				f -> showError("Error obtenci\u00F3n TA (Baja)", f.getMessage()), 
				"BAJA");
	}

	private void showTa() {
		showLoading("Obteniendo TA...");
		contrataEmployeeObject.downloadTa(dataURI -> {
			hideMessage();
			showPdf();
			pdfViewer.open(dataURI);
		}, f -> {
			showBlakPdf();
			showError("Error TA", f.getMessage());
		}, "ALTA");
	}

	private void showTaEnd() {
		showLoading("Obteniendo TA (Baja)...");
		contrataEmployeeObject.downloadTa(dataURI -> {
			hideMessage();
			showPdf();
			pdfViewer.open(dataURI);
		}, f -> {
			showBlakPdf();
			showError("Error TA (Baja)", f.getMessage());
		}, "BAJA");
	}

	private void showIdc() {
		showIdc(idcDateListBox.getSelected());
	}

	private void showIdc(Date date) {
		showPdf();
		showLoadingPDF("Obteniendo IDC...");
		idcDateListBox.setVisible(true);
		idcMonthListBox.setVisible(false);
		idcDateListBox.getElement().getStyle().setWidth(100, Unit.PCT);
		idcDateListBox.setSelected(date, true);
		contrataEmployeeObject.downloadIdc(date, dataURI -> {
			hideMessagePDF();
			pdfViewer.open(dataURI);
		}, f -> {
			showBlakPdf();
			showErrorPDF("Error IDC", f.getMessage());
		});
	}

	private void showIdcPlNss() {
		showIdcPlNss(DateUtils.getFirstDayOfMonth());
	}

	private void showIdcPlNss(Date month) {
		showPdf();
		showLoadingPDF("Obteniendo IDC PL NSS...");
		idcMonthListBox.setVisible(true);
		idcDateListBox.setVisible(false);
		idcMonthListBox.getElement().getStyle().setWidth(100, Unit.PCT);
		idcMonthListBox.setSelected(month, true);
		contrataEmployeeObject.downloadIdcPlNss(month, dataURI -> {
			hideMessagePDF();
			pdfViewer.open(dataURI);
		}, f -> {
			showBlakPdf();
			showErrorPDF("Error IDC PL NSS", f.getMessage());
		});
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
				}, f -> {});
			}
		});
	}

	private void movPrevDelete() {
		showLoading("Borrando movimiento previo...");
		contrataEmployeeObject.movPrevDelete(s -> {
			hideMessage();
			loadWindow(su -> {});
		}, f -> showError("Error borrado movimiento previo", f.getMessage()));
	}

	private void altaConsolidadaDelete() {
		showLoading("Borrando alta consolidad...");
		contrataEmployeeObject.altaConsolidadaDelete(s -> {
			hideMessage();
			loadWindow(su -> {});
		}, f -> showError("Error borrado alta consolidada", f.getMessage()));
	}
	
	private boolean checkPrevAlta() {
		Date currentDate = new Date();
		Date startDate = contrataEmployeeObject.getContractStartDate();
		return DateUtils.isAfterOrEquals(startDate, currentDate) && !DateUtils.equals(startDate, currentDate);
	}

	// ------------------------------------------------- EmployeeSepeButtons

	private HTMLPanel initEmployeeSepeButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());

		AonToolbarButton saveSepe = new AonToolbarButton(AON.MSG.saveAction() + " Datos Sepe", AON.CSS.aonIconSave());
		saveSepe.addClickHandler(e -> contractSpecificData.saveSepe());
		hPanel.add(saveSepe);

//		hPanel.add(deleteContract);

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

		return hPanel;
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
		}, f -> {
			showBlakPdf();
			showError("Error CTO", f.getMessage());
		});
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
		}, f -> {
			showBlakPdf();
			showError("Error CBC", f.getMessage());
		});
	}

	private void showCertifica2PDF() {
		showLoading("Obteniendo Certific@2...");
		contrataEmployeeObject.getCertifica2PDF(dataURI -> {
			hideMessage();
			showPdf();
			pdfViewer.open(dataURI);
		}, f -> {
			showBlakPdf();
			showError("Error Certific@2", f.getMessage());
		});
	}
	
	private void sendBasicCopyTimer(Consumer<Void> success, Consumer<Void> failure) {
		Timer timer = new Timer() {
			@Override
			public void run() {
				showLoading("Notificando copia basica...");
				contrataEmployeeObject.sendBasicCopy(s -> {
					showSuccess("Comunicaci\u00F3n", "La copia basica ha sido notificada correctamente del SEPE");
					success.accept(null);
				}, f -> {
					showError("Error comunicaci\u00F3n", f.getMessage());
					failure.accept(null);
				});
			}
		};
		timer.schedule(2500);
	}

	private void sendBasicCopy() {
		showLoading("Notificando copia basica...");
		contrataEmployeeObject.sendBasicCopy(s -> {
			showSuccess("Comunicaci\u00F3n", "La copia basica ha sido notificada correctamente del SEPE");
			downloadCbc(su -> loadWindow(suc -> {}));
		}, f -> showError("Error comunicaci\u00F3n", f.getMessage()));
	}

	private void downloadCbc(Consumer<Void> finish) {
		Timer timer = new Timer() {
			@Override
			public void run() {
				showLoading("Obteniendo CBC....");
				contrataEmployeeObject.downloadCbc(
						s -> {
							showSuccess("CBC","Se ha descargado el CBC del trabajador. El documento se encuentran en el apartado de Documentos");
							finish.accept(null);
						},
						f -> {
							showError("Error obtenci\u00F3n CBC", f.getMessage());
							finish.accept(null);
						});
			}
		};
		timer.schedule(2500);
	}

	private void sendContract() {
		showLoading("Notificando contrato...");
		contrataEmployeeObject.sendContract(s -> {
			showSuccess("Comunicaci\u00F3n", "El contrato ha sido notificado correctamente del SEPE");
			downloadCto(
				su -> sendBasicCopyTimer(
						success -> downloadCbc(suc -> loadWindow(succe -> {})),
						failure -> loadWindow(succe -> {})), 
				fa -> loadWindow(succe -> {}));
			contrataEmployeeObject.getComunicationInfo();
		}, f -> showError("Error comunicaci\u00F3n", f.getMessage()));
		
//		contrataEmployeeObject.sendContract(s -> {
//			showSuccess("Comunicaci\u00F3n", "El contrato ha sido notificado correctamente del SEPE");
//			sendBasicCopyTimer(su -> downloadCto(suc -> downloadCbc(succ -> loadWindow(succe -> {}))));
//		}, f -> showError("Error comunicaci\u00F3n", f.getMessage()));
	}

	private void downloadCto(Consumer<Void> succes, Consumer<Void> failure) {
		Timer timer = new Timer() {
			@Override
			public void run() {
				showLoading("Obteniendo CTO....");
				contrataEmployeeObject.downloadCto(
						s -> {
							showSuccess("CTO", "Se ha descargado el CTO del trabajador. El documento se encuentran en el apartado de Documentos");
							succes.accept(null);
						},
						f -> {
							showError("Error obtenci\u00F3n CTO", f.getMessage());
							failure.accept(null);
						});
			}
		};
		timer.schedule(2500);
	}

	private void removeContract() {
		showLoading("Eliminando contrato...");
		contrataEmployeeObject.removeContract(s -> {
			showSuccess("Comunicaci\u00F3n", "El contrato ha sido eliminado correctamente del SEPE");
			loadWindow(su -> {});
		}, f -> showError("Error comunicaci\u00F3n", f.getMessage()));
	}

	private void contractExtension() {
		new ContractExtensionDialog(this.contrataEmployeeObject.getContractEmployeeInfo()) {
			@Override
			protected void onExtensionDone() {
				showSuccess("Pr\u00F3rroga", "La pr\u00F3rroga del trabajador " + contrataEmployeeObject.getEmployeeFullName() + " ha sido realizada correctamente");
				loadWindow(su -> {});
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
				showSuccess("Transformaci\u00F3n", "La transformaci\u00F3n del trabajador " + contrataEmployeeObject.getEmployeeFullName() + " ha sido realizada correctamente");
				onTransformContract(newContractId);
			}

			@Override
			protected void fireError(Map<String, String> errorMap) {
				AonMessagePanel.showError(messageContainer, errorMap);
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
			loadWindow(su -> {});
		}, f -> {});
	}

	// ------------------------------------------------- EmployeeOtherDataButtons

	private HTMLPanel initEmployeeOtherDataButtons() {
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.addStyleName(style.flex());

		AonToolbarButton saveClauses = new AonToolbarButton(AON.MSG.saveAction() + " Otros Datos",
				AON.CSS.aonIconSave());
		saveClauses.addClickHandler(e -> contractOtherData.saveOhterData());
		hPanel.add(saveClauses);

		AonToolbarButton exportContractPDF = new AonToolbarButton("Borrador Contrato", AON.CSS.aonIconPdf());
		exportContractPDF.addClickHandler(e -> contractOtherData.onContractPDF());
		hPanel.add(exportContractPDF);

		return hPanel;
	}

	private void onExportContractPDF() {
		showLoading("Generando borrador de contrato");
		contrataEmployeeObject.getContractOtherInfo(s -> {
			if (AonStringUtils.isBlank(contrataEmployeeObject.getFormativeLevel()))
				contrataEmployeeObject.getContractSpecificData(su -> {
					contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
					contrataEmployeeObject.setContractOtherData(contractOtherData.getContractOtherData());
					contrataEmployeeObject.saveContractExport(a -> showSuccess("Borrador Contrato",
							"El borrador de contrato se ha generado correctamente. Se encuentra en la pesta\u00f1a Documentos."),
							e -> showError("Borrador Contrato", e.getMessage()));
				}, f -> showError("Borrador Contrato", f.getMessage()));
			else {
				contractOtherData.setEmployeeContractInfo(contrataEmployeeObject.getContractEmployeeInfo());
				contrataEmployeeObject.setContractOtherData(contractOtherData.getContractOtherData());
				contrataEmployeeObject.saveContractExport(a -> showSuccess("Borrador Contrato",
						"El borrador de contrato se ha generado correctamente. Se encuentra en la pesta\u00f1a Documentos."),
						e -> showError("Borrador Contrato", e.getMessage()));
			}
		}, f -> showError("Borrador Contrato", f.getMessage()));
	}

	// ------------------------------------------------- EmployeeClausesButtons

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
		
		AonExpandButton pdfAttachment = new AonExpandButton("Documento PDF", AON.CSS.aonIconPdf()) {

			@Override
			public void onExpandClick(ClickEvent event) {
				NativeEvent nativeEvent = event.getNativeEvent();
				attachContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
				attachContextMenu.show();
			}

			@Override
			public void onDefaultClick(ClickEvent evet) {
				if(contractAttachUI.existContract())
					contractAttachUI.modificationPDF();
				else
					contractAttachUI.exportContract();
			}
		};
		hPanel.add(pdfAttachment);
		
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
//						ifSistemaREDEnabled(employeeStatus, () -> {
//							ContrataEmployee.this.setTaVisible(true);
//							ContrataEmployee.this.setIdcVisible(true);
//						}, () -> {
//							ContrataEmployee.this.setTaVisible(false);
//							ContrataEmployee.this.setIdcVisible(false);
//
//						});
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

//			ifSistemaREDEnabled(employeeStatus, () -> {
//				ContrataEmployee.this.setTaVisible(true);
//				ContrataEmployee.this.setIdcVisible(true);
//			}, () -> {
//				ContrataEmployee.this.setTaVisible(false);
//				ContrataEmployee.this.setTaEndVisible(false);
//				ContrataEmployee.this.setIdcVisible(false);
//			});

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
		tgssContextMenu.getIdcPlNss().setVisible(visible);
	}

	private void initializeIdcDateListBox() {
		tgssContextMenu.getIdc().setVisible(false);
		contrataEmployeeObject.getIdcDates(contractId, dates -> {
			Collections.reverse(dates);
			int count = dates.size();
			idcDateListBox.setRowCount(count, true);
			idcDateListBox.setRowData(0, dates);
			idcDateListBox.setVisibleRange(0, count + 1);
			idcDateListBox.setSelected(0, true);
			idcDateListBox.onResizeDropDownPopup();
			tgssContextMenu.getIdc().setVisible(true);
		}, error -> {
			tgssContextMenu.getIdc().setVisible(false);
//			showWarning("Fechas Idc", error.getMessage());
		});
	}

	// ------------------------------------------------- SEPE status

	public void setHasCertificateSEPE(boolean hasCertificateSEPE) {
		this.hasCertificateSEPE = hasCertificateSEPE;
	}
	
	private void checkSepeContextMenu() {
		Date endDate = contrataEmployeeObject.getContractData().getEndDate();
		
		sepeContextMenu.setDafaultContract();
		sepeContextMenu.setIsComunica(this.hasCertificateSEPE);
		
		// Extension
		boolean hasExtension = contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().isHasExtension();
		if(Boolean.TRUE.equals(hasExtension)) sepeContextMenu.setIsExtension();
		
		// Can Transform
		String contractTypeStr = contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().getContractType();

		if (AonStringUtils.isNotBlank(contractTypeStr)) {
			Integer contractType = Integer.parseInt(contractTypeStr);
			sepeContextMenu.setCanTransform(contractType >= 400);
			sepeContextMenu.setCanExtension(contractType == 402 || contractType == 420 || contractType == 421 || contractType == 502 || contractType == 520 || contractType == 521 );
		}
		
		// Transform
		boolean hasTransform = contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().isHasTransformation();
		if(Boolean.TRUE.equals(hasTransform)) sepeContextMenu.setIsTransform();
		
		if(Boolean.TRUE.equals(hasExtension) && Boolean.TRUE.equals(hasTransform)) sepeContextMenu.getSeparatorAdds().setVisible(false);
		
		sepeContextMenu.setEndDate(endDate);
		if(!hasTransform && !hasExtension) {
			sepeContextMenu.setHasCTO(contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().hasCto());
			sepeContextMenu.setHasCBC(contrataEmployeeObject.getContractEmployeeInfo().getContractInfo().hasCbc());
		}
	}

	// ------------------------------------------------- TGSS status

	private void checkTgssContextMenu() {
		Date startDate = contrataEmployeeObject.getContractData().getStartDate();
		Date endDate = contrataEmployeeObject.getContractData().getEndDate();
		
		tgssContextMenu.setIsComunica(this.isComunica);
		tgssContextMenu.setStartDate(startDate);
		tgssContextMenu.setEndDate(endDate);
		tgssContextMenu.setPrevAlta(checkPrevAlta());
	}

	public void setIsComunica(boolean isComunica) {
		this.isComunica = isComunica;
	}
	
	public void setHasPayroll(boolean hasPayroll) {
		this.hasPayroll = hasPayroll;
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

	private void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messageContainer, errorMap);
	}
	
	private void showWarning(String title, String message) {
		Map<String, String> warningMap = new HashMap<>();
		warningMap.put(title, message);
		AonMessagePanel.showWarning(messageContainer, warningMap);
	}

	private void showLoading(String message) {
		AonMessagePanel.showLoading(messageContainer, message);
	}

	private void hideMessage() {
		AonMessagePanel.hideMessage(messageContainer);
	}
	
	private void showSuccessPDF(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(messagePDFContainer, successMap);
	}

	private void showErrorPDF(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePDFContainer, errorMap);
	}

	private void showLoadingPDF(String message) {
		AonMessagePanel.showLoading(messagePDFContainer, message);
	}

	private void hideMessagePDF() {
		AonMessagePanel.hideMessage(messagePDFContainer);
	}

}

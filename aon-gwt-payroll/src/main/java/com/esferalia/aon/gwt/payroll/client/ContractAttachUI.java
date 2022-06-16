package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContractAttachUI extends ResizeComposite {

	// -------------------------------------------------- UiBinder

	private static ContractAttachUIBinder uiBinder = GWT.create(ContractAttachUIBinder.class);

	interface ContractAttachUIBinder extends UiBinder<Widget, ContractAttachUI> {}

	// -------------------------------------------------- UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String headerLabelStyle();
		String maxWidthTB();
		String maxWidthLB();
		String clauseTD();
		String flex();
		String loading();
	}

	@UiField
	VerticalPanel attachmentsTable;

	@UiField
	Grid attachmentsDataTableHeader;

	@UiField
	DeckPanel deckPanel;

	@UiField
	ScrollPanel attachmentsScrollPanel;

	@UiField
	Grid attachmentsDataTable;

	@UiField
	HTMLPanel loadingAttachPanel;

	// ------------------------------------------------------ Variables

	private static final String CREATEURL = GWT.getModuleBaseURL() + "attach/create/";
	private static final String UPDATEURL = GWT.getModuleBaseURL() + "attach/update/";
	private static final String DOWNLOADURL = GWT.getModuleBaseURL() + "attach/download/";

	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private EmployeeContractInfo employeeContractInfo;

	// ------------------------------------------------------ Constructor

	protected ContractAttachUI() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	// ------------------------------------------------------ setEmployeeContractInfo

	public void setEmployeeContractInfo(EmployeeContractInfo employeeContractInfoIn) {
		this.employeeContractInfo = employeeContractInfoIn;
		initializeView();

		getContractAttachments(s -> {
			if(employeeContractInfo.getContractAttachments().isEmpty())
				showEmptyTable();
			else {
				showMainTable();
				for (Attach attach : employeeContractInfo.getContractAttachments())
					paintContractAttach(attach);
			}
		}, f -> {});
	}

	// ------------------------------------------------------ Initialize View

	private void initializeView() {
		initAttachmentsTable();
		paintHeaderAttachmentsTable();

		setScrollPanelsHeight();
		setColumnsWidth();

		showLoadingPanel();
	}

	private void initAttachmentsTable() {
		attachmentsDataTableHeader.clear();
		attachmentsDataTableHeader.resize(0, 0);
		attachmentsDataTableHeader.resizeColumns(6);
		attachmentsDataTable.clear();
		attachmentsDataTable.resize(0, 0);
		attachmentsDataTable.resizeColumns(6);
	}

	private void paintHeaderAttachmentsTable() {
		int row = attachmentsDataTableHeader.insertRow(attachmentsDataTableHeader.getRowCount());

		Label description = new Label("DESCRIPCI\u00D3N");
		Label type = new Label("TIPO");
		AonTableButton confidential = new AonTableButton("Confidencial", AON.CSS.aonIconLock());
		Label date = new Label("FECHA");
		Label scope = new Label("\u00C1MBITO");
		Label file = new Label("ARCHIVO");
		Label action = new Label("");

		description.addStyleName(style.headerLabelStyle());
		type.addStyleName(style.headerLabelStyle());
		date.addStyleName(style.headerLabelStyle());
		scope.addStyleName(style.headerLabelStyle());
		file.addStyleName(style.headerLabelStyle());

		attachmentsDataTableHeader.setWidget(row, 0, description);
		attachmentsDataTableHeader.setWidget(row, 1, type);
		attachmentsDataTableHeader.setWidget(row, 2, confidential);
		attachmentsDataTableHeader.setWidget(row, 3, date);
		attachmentsDataTableHeader.setWidget(row, 4, scope);
		attachmentsDataTableHeader.setWidget(row, 5, action);
	}

	private void setScrollPanelsHeight() {
		attachmentsScrollPanel.setHeight((Window.getClientHeight() - 390) + "px");
	}

	private void setColumnsWidth() {
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(270, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(240, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setWidth(85, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_CENTER);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setWidth(95, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 4).getStyle().setWidth(95, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 5).getStyle().setWidth(100, Unit.PX);

		attachmentsDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(270, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(240, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(85, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(95, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(4).getStyle().setWidth(95, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(5).getStyle().setWidth(100, Unit.PX);
	}

	private void showLoadingPanel() {
		AonToolbarSmallButton loadingBtn = new AonToolbarSmallButton("Cargando Documentos", AON.CSS.aonIconRenew());
		loadingBtn.addStyleName(style.loading());
		Label loadingLabel = new Label("Cargando documentos ...");

		loadingAttachPanel.clear();
		loadingAttachPanel.add(loadingBtn);
		loadingAttachPanel.add(loadingLabel);

		deckPanel.showWidget(1);
	}

	private void showMainTable() {
		deckPanel.showWidget(0);
	}
	
	private void showEmptyTable() {
		deckPanel.showWidget(2);
	}

	// ------------------------------------------------------ PaintContractAttach

	private void paintContractAttach(Attach attach) {
		
		// Hiddens
		Hidden userLoginHidden = new Hidden("login", Wnd.getCurrentUser());
		Hidden currentDomainHidden = new Hidden("domain", Wnd.getCurrentDomainNameURL());
		Hidden tokenHidden = new Hidden("token", Wnd.getToken());
		Hidden attachIdHidden = new Hidden("attachId", attach.getId() + "");
		Hidden contractIdHidden = new Hidden("contractId", employeeContractInfo.getContractInfo().getContractId() + "");
		Hidden descriptionHidden = new Hidden("description", attach.getDescription());
		Hidden typeHidden = new Hidden("type", attach.getType() + "");
		Hidden securityHidden = new Hidden("security", attach.getConfidential() + "");
		Hidden dateHidden = new Hidden("date", null == attach.getDate() ? "" : formatDate.format(attach.getDate()));
		Hidden scopeHidden = new Hidden("scope", attach.getScope() + "");
		Hidden mimeTypeHidden = new Hidden("mimeType", null == attach.getMimeType() ? "" : attach.getMimeType().getExtension());
		
		// Create Form Panel
		FormPanel form = new FormPanel();
		form.setAction(null == attach.getId() ? CREATEURL : UPDATEURL);
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.addSubmitCompleteHandler(e -> {
			try {
				String jsonStr = e.getResults().split(">")[1].split("<")[0];
				JSONValue json = JSONParser.parseStrict(jsonStr);
				parseJSON(json.isObject());
			} catch (NullPointerException | IllegalArgumentException err){
				showErrorMessage("Formato", "Error formateando la informaci\u00f3n");
			}
		});

		// Insert new row
		int row = this.attachmentsDataTable.insertRow(attachmentsDataTable.getRowCount());
		
		// Description TextBox
		TextBox descriptionTB = new TextBox();
		descriptionTB.setValue(attach.getDescription());
		descriptionTB.addValueChangeHandler(e -> descriptionHidden.setValue(e.getValue()));

		// Type ListBox
		ListBox typeLB = initTypeListBox();
		setSelectedValueLB(typeLB, attach.getType() + "");
		typeLB.addChangeHandler(e -> typeHidden.setValue(typeLB.getSelectedValue()));
		typeLB.setEnabled(null == attach.getType() || !isComunicationCreated(attach.getType()));
		
		// Confidential CheckBox
		String securityTitle = attach.isConfidential() ? "Privado: S\u00f3lo visible para usuarios de la empresa" : "P\u00fablico: Visible para todos los usuarios";
		String securityIcon = attach.isConfidential() ? AON.CSS.aonIconLock() : AON.CSS.aonIconUnLock();
		AonTableButton confidentialB = new AonTableButton(securityTitle, securityIcon);
		confidentialB.addClickHandler(e -> {
			Boolean oldValue = isActiveToggleButton(confidentialB);
			Boolean value = !oldValue;
			getEnableDisableButton(confidentialB, value);
			securityHidden.setValue(Boolean.TRUE.equals(value) ? "true" : "false");
			
		});

		// Attach DateBoxEx
		DateBoxEx dateBox = new DateBoxEx();
		dateBox.setValue(attach.getDate());
		dateBox.addValueChangeHandler(e -> dateHidden.setValue(null == e.getValue() ? "" : formatDate.format(e.getValue())));
		
		// Scope ListBox
		ListBox scopeLB = new ListBox();
		initScopeListBox(scopeLB);
		scopeLB.addChangeHandler(e -> scopeHidden.setValue(scopeLB.getSelectedValue()));

		// FileUpload
		FileUpload fileUpload = new FileUpload();
		fileUpload.setName("uploader");
		fileUpload.getElement().setPropertyString("multiple", "multiple");
		fileUpload.getElement().getStyle().setDisplay(Display.NONE);

		fileUpload.addChangeHandler(e -> {
			String filename = getFileName(fileUpload.getFilename());
			String fileExt = getFileExtension(fileUpload.getFilename());

			if (filename.length() == 0)
				Window.alert("Cant upload file - Try again");
			else {
				mimeTypeHidden.setValue(fileExt);
				descriptionHidden.setValue(filename);
				descriptionTB.setValue(filename);

				final int size = getFileSize(fileUpload.getElement());
				if (size > 15000000)
					showErrorMessage("Tama\u00F1o fichero", "El archivo adjunto no puede ser superior a 10 MB");

			}
		});
		
		// Attach File Button
		AonTableButton fileAttach = new AonTableButton("Subir Documento", AON.CSS.aonIconAttach());
		fileAttach.addClickHandler(e -> fileUpload.click());
		fileAttach.setVisible(null == attach.getId());

		// Attach Download Button
		AonTableButton downloadAttach = new AonTableButton("Descargar Documento", AON.CSS.aonIconDownload());
		downloadAttach.addClickHandler(e -> {
			form.setAction(DOWNLOADURL);
			form.submit();
		});
		downloadAttach.setVisible(null != attach.getId() && !attach.getMimeType().isPDF());
		
		// Attach Download Button
		AonTableButton viewAttach = new AonTableButton("Visualizar Documento", AON.CSS.aonIconVisibility());
		viewAttach.addClickHandler(e -> {
			showLoadingMessage("Cargando archivo...");
			impl.getAttachData(attach.getId(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String dataURI) {
					showAttachPDf(attach.getId(), dataURI);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					// Nothing to do here
				}
				
			});
		});
		viewAttach.setVisible(null != attach.getId() && attach.getMimeType().isPDF());

		// Attach Delete Button
		AonTableButton deleteAttach = new AonTableButton("Eliminar", AON.CSS.aonIconDelete());
		deleteAttach.addClickHandler(e ->  {
			AonDialog dialog = new AonDialog("BORRADO DOCUMENTO", new Label("\u00bfDesea eliminar el documento " + (AonStringUtils.isBlank(attach.getDescription()) ? "" : attach.getDescription()) + "?"));
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here, only hide dialog
				}
				
				@Override
				public void onAccept() {
					deleteContractAttach(attach.getId(), row,
						s -> {
							showSuccessMessage("Borrado", "El documento ha sido eliminado correctamente");
							refreshPage();
						}, f -> {});
				}
			});
		
		});

		// Attach Save Button
		AonTableButton saveAttach = new AonTableButton("Guardar", AON.CSS.aonIconSave());
		saveAttach.addClickHandler(e -> form.submit());
		
		// Add Styles
		descriptionTB.addStyleName(style.maxWidthTB());
		typeLB.addStyleName(style.maxWidthLB());
		scopeLB.addStyleName(style.maxWidthLB());

		// Buttons Panel
		HTMLPanel buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(style.flex());

		// Add all to FlowPanel to add to FormPanel
		HTMLPanel flowFormPanel = new HTMLPanel("");
		flowFormPanel.add(userLoginHidden);
		flowFormPanel.add(currentDomainHidden);
		flowFormPanel.add(tokenHidden);
		flowFormPanel.add(contractIdHidden);
		flowFormPanel.add(attachIdHidden);
		flowFormPanel.add(descriptionHidden);
		flowFormPanel.add(typeHidden);
		flowFormPanel.add(mimeTypeHidden);
		flowFormPanel.add(securityHidden);
		flowFormPanel.add(dateHidden);
		flowFormPanel.add(scopeHidden);
		flowFormPanel.add(fileUpload);
		form.add(flowFormPanel);
		buttonsPanel.add(fileAttach);
		buttonsPanel.add(downloadAttach);
		buttonsPanel.add(viewAttach);
		buttonsPanel.add(deleteAttach);
		buttonsPanel.add(saveAttach);
		buttonsPanel.add(form);
		
		// Add to table
		attachmentsDataTable.setWidget(row, 0, descriptionTB);
		attachmentsDataTable.setWidget(row, 1, typeLB);
		attachmentsDataTable.setWidget(row, 2, confidentialB);
		attachmentsDataTable.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_CENTER);
		attachmentsDataTable.setWidget(row, 3, dateBox);
		attachmentsDataTable.setWidget(row, 4, scopeLB);
		attachmentsDataTable.setWidget(row, 5, buttonsPanel);
		attachmentsDataTable.getCellFormatter().setHorizontalAlignment(row, 5, HasHorizontalAlignment.ALIGN_RIGHT);

	}

	// ------------------------------------------------------ Auxiliar Methods

	private void parseJSON(JSONObject json) {
		JSONValue type = json.get("type");
		if(null != type && AonStringUtils.isNotBlank(type.toString())) {
			if(AonStringUtils.containsIgnoreCase(type.toString(), "create")) {
				showSuccessMessage("Creaci\u00f3n documento", "El documento se ha generado correctamente");
				refreshPage();
			} else if(AonStringUtils.containsIgnoreCase(type.toString(), "update")) {
				showSuccessMessage("Actualizaci\u00f3n documento", "El documento se ha actualizado correctamente");
				refreshPage();
			} else {
				JSONValue message = json.get("message");
				showErrorMessage("Certificado", message.toString());
			}
		}
	}
	
	private ListBox initTypeListBox() {
		ListBox typeLB = new ListBox();
		typeLB.addItem("-", "-1");
		typeLB.addItem("Borrador del contrato", "0");
		typeLB.addItem("Copia Contrato laboral", "1");
		typeLB.addItem("Domiciliacion bancaria", "7");
		typeLB.addItem("Anexo I", "8");
		typeLB.addItem("Anexo II", "9");
		typeLB.addItem("Borrador prorroga", "10");
		typeLB.addItem("Prorroga", "11");
		typeLB.addItem("Borrador del certificado de empresa", "22");
		typeLB.addItem("TA (Alta)", "98");
		typeLB.addItem("TA (Baja)", "99");
		typeLB.addItem("Contrato (Comunicaci\u00f3n SEPE)", "101");
		typeLB.addItem("Copia basica (Comunicaci\u00f3n SEPE)", "102");
		typeLB.addItem("Certific\u00402 (Pdf)", "103");
		typeLB.addItem("IDC", "104");
		typeLB.addItem("IDCPlNss", "105");
		typeLB.addItem("Modificaci\u00f3n Contrato", "107");
		typeLB.addItem("Otros", "106");
		return typeLB;
	}
	
	private boolean isComunicationCreated(byte attachType) {
		return attachType == ((byte)98) || attachType == ((byte)99) || attachType == ((byte)101) || attachType == ((byte)102) || attachType == ((byte)103);
	}

	private void initScopeListBox(ListBox scopeLB) {
		scopeLB.clear();
		scopeLB.addItem("-", "-1");
		for (Entry<String, String> entry : employeeContractInfo.getScopeMap().entrySet()) {
			scopeLB.addItem(entry.getKey(), entry.getValue());
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

	// ------------------------------------------------------ FileUpload.Methods

	private String getFileName(String filename) {
		String[] splits = filename.split("\\\\");
		return splits[splits.length - 1].contains("\\.") ? splits[splits.length - 1].split("\\.")[0]
				: splits[splits.length - 1];
	}

	private String getFileExtension(String filename) {
		String[] splits = filename.split("\\.");
		return splits[splits.length - 1];
	}

	private native int getFileSize(final Element data) /*-{
		return data.files[0].size;
	}-*/;

	// ------------------------------------------------------ ToogleButton.Methods

	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.CSS.aonIconUnLock() : AON.CSS.aonIconLock());
		button.addStyleName(!disabled ? AON.CSS.aonIconUnLock() : AON.CSS.aonIconLock() );
		button.setTitle(!disabled ? "P\u00fablico: Visible para todos los usuarios" : "Privado: S\u00f3lo visible para usuarios de la empresa");
	}

	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.CSS.aonIconLock());
	}

	// ------------------------------------------------------ ContractAttach.CRUD
	// Methods

	private void deleteContractAttach(Integer attachId, Integer row, Consumer<Void> success, Consumer<Throwable> failure) {
		if(null == attachId) {
			attachmentsDataTable.removeRow(row);
			success.accept(null);
		} else {		
			impl.deleteContractAttach(attachId, new AsyncCallback<Void>() {
	
				@Override
				public void onSuccess(Void result) {
					success.accept(result);
				}
	
				@Override
				public void onFailure(Throwable caught) {
					failure.accept(caught);
				}
			});
		}
	}

	private void getContractAttachments(Consumer<List<Attach>> success, Consumer<Throwable> failure) {
		Integer contractId = employeeContractInfo.getContractInfo().getContractId();
		impl.getContractAttachments(contractId, new AsyncCallback<List<Attach>>() {

			@Override
			public void onSuccess(List<Attach> contractAttachments) {
				employeeContractInfo.setContractAttachments(contractAttachments);
				success.accept(contractAttachments);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}

	// ------------------------------------------------------ Toolbar methods

	public void newAttachment() {
		showMainTable();
		paintContractAttach(new Attach().setDate(new Date()));
	}

	public void exportContract() {
		onExportPDF(e -> {
			showSuccessMessage("Contrato", "El contrato se ha generado correctamente");
			refreshPage();
		}, f -> showErrorMessage("Contrato", f.getMessage()));
	}
	
	public void setAttachData(Integer attachId, byte[] data) {
		if(null == attachId || null == data) return;
		
		showLoadingMessagePDF("Guardando documento...");
		impl.setAttachData(attachId, data, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				showSuccessMessagePDF("Documentos", "Documento guardado");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Nothing to do here
			}
			
		});
	}

	// ------------------------------------------------------ Abstract Methods

	protected abstract void onExportPDF(Consumer<String> consumer, Consumer<Throwable> failure);
	
	protected abstract void showAttachPDf(Integer attachId, String dataURI);

	protected abstract void showErrorMessage(String title, String message);

	protected abstract void showSuccessMessage(String title, String message);
	
	protected abstract void showSuccessMessagePDF(String title, String message);
	
	protected abstract void showLoadingMessage(String message);
	
	protected abstract void showLoadingMessagePDF(String message);

	// ------------------------------------------------------ Refresh table

	public void refreshPage() {
		resetAttachDataTableStructure();
		showLoadingPanel();
		getContractAttachments(s -> {
			if(employeeContractInfo.getContractAttachments().isEmpty())
				showEmptyTable();
			else {
				showMainTable();
				for (Attach attach : employeeContractInfo.getContractAttachments())
					paintContractAttach(attach);
			}
		}, f -> {});

	}

	private void resetAttachDataTableStructure() {
		attachmentsDataTable.clear();
		attachmentsDataTable.resize(0, 0);
		attachmentsDataTable.resizeColumns(7);

		setColumnsWidth();
	}

}

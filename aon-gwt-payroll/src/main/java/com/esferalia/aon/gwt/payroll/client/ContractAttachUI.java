package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.payroll.shared.ContractAttach;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.Messages;
import com.esferalia.aon.gwt.payroll.shared.Messages.Message;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContractAttachUI extends ResizeComposite {

	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static ContractAttachUIBinder uiBinder = GWT.create(ContractAttachUIBinder.class);

	interface ContractAttachUIBinder extends UiBinder<Widget, ContractAttachUI> {}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String headerLabelStyle();
		String maxWidthTB();
		String maxWidthLB();
		String clauseTD();
	}

	@UiField
	VerticalPanel attachmentsTable;

	@UiField
	Grid attachmentsDataTableHeader;
	
	@UiField
	ScrollPanel attachmentsScrollPanel;
	
	@UiField
	Grid attachmentsDataTable;

	@UiField
	HTMLPanel footerOptionsToolbar;
	
	// ------------------------------------------------------ Constructor ---------------------------------------------------------

	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private EmployeeContractInfo employeeContractInfo;
	private Messages messages;
	
	public ContractAttachUI() {
		initWidget(uiBinder.createAndBindUi(this));
		messages = new Messages();
		initFooterOptionsToolbar();
	}
	
	public void setEmployeeContractInfo(EmployeeContractInfo employeeContractInfoIn) {
		this.employeeContractInfo = employeeContractInfoIn;
		initializeView();
		
		for(ContractAttach contractAttach : employeeContractInfo.getContractAttachments())
			paintContractAttach(contractAttach);
	}
	
	private void initFooterOptionsToolbar() {
		footerOptionsToolbar.clear();
		
		AonTableButton newAttachmentBtn = new AonTableButton(AON.MSG.newAction(),  AON.CSS.aonIconAdd());
		newAttachmentBtn.addClickHandler(e -> {
			onAddNewAttachment(e);
		});
		
		Label newAttachmentL = new Label("A" + String.valueOf("\u00F1") + "adir Documento");
		newAttachmentL.getElement().getStyle().setMarginRight(5, Unit.PX);
		
		footerOptionsToolbar.add(newAttachmentBtn);
		footerOptionsToolbar.add(newAttachmentL);
		
		AonTableButton pdfExportBtn = new AonTableButton("Generar Borrador Contrato",  AON.CSS.aonIconPdf());
		pdfExportBtn.addClickHandler(e -> {
			onExportPDF();
		});
		
		Label pdfExportL = new Label("Generar Contrato");
		
		footerOptionsToolbar.add(pdfExportBtn);
		footerOptionsToolbar.add(pdfExportL);
	}

	protected abstract void onExportPDF();

	private void onAddNewAttachment(ClickEvent e) {
		ContractAttach contractAttach = new ContractAttach();
		contractAttach.setDomain(employeeContractInfo.getEmployeeInfo().getDomain());
		contractAttach.setContract(employeeContractInfo.getContractInfo().getContractId());
		
		createContractAttach(contractAttach,
				s -> {
					
					resetAttachDataTableStructure();
					
					for(ContractAttach contractAttachIn : employeeContractInfo.getContractAttachments())
						paintContractAttach(contractAttachIn);
					
				}, f -> {});
	}

	// --------------------------------------------------------- UiHandlers --------------------------------------------------------
	
	// --------------------------------------------------- UiHandlers (Aux Methods) -------------------------------------------------
	
	public void refreshPage() {
		resetAttachDataTableStructure();
		
		for(ContractAttach contractAttachIn : employeeContractInfo.getContractAttachments())
			paintContractAttach(contractAttachIn);
	}
	
	private void resetAttachDataTableStructure() {
		attachmentsDataTable.clear();
		attachmentsDataTable.resize(0, 0);
		attachmentsDataTable.resizeColumns(7);
		
		setColumnsWidth();
	}
	
	// -------------------------------------------------- Paint Table Header Methods --------------------------------------------------
	
	private void paintHeaderAttachmentsTable() {
		int row = attachmentsDataTableHeader.insertRow(attachmentsDataTableHeader.getRowCount());
		
		Label description = new Label("DESCRIPCI" + String.valueOf("\u00D3") + "N");
		Label type = new Label("TIPO");
		AonTableButton confidential = new AonTableButton("Confidencial", AON.CSS.aonIconLock());
		Label date = new Label("FECHA");
		Label scope = new Label(String.valueOf("\u00C1") + "MBITO");
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
		attachmentsDataTableHeader.setWidget(row, 5, file);
		attachmentsDataTableHeader.setWidget(row, 6, action);
	}
	
	// ------------------------------------------------------ Paint Table Methods -----------------------------------------------------

	private void paintContractAttach(ContractAttach contractAttach) {
		// Insert new row
		int row = this.attachmentsDataTable.insertRow(attachmentsDataTable.getRowCount());
		
		// Description TextBox
		TextBox descriptionTB = new TextBox();
		descriptionTB.addValueChangeHandler((e) -> {
			contractAttach.setDescription(e.getValue());;
		});
		
		// Type ListBox
		ListBox typeLB = new ListBox();
		initTypeListBox(typeLB);
		typeLB.addChangeHandler((e) -> {
			Byte type = Byte.parseByte(typeLB.getSelectedValue());
			contractAttach.setType(type);
		});
		
		// Confidential CheckBox
		Button confidentialB = new Button();
		getEnableDisableButton(confidentialB, false);
		confidentialB.addClickHandler((e) -> {
			Boolean oldValue = isActiveToggleButton(confidentialB);
			Boolean value = !oldValue;
			getEnableDisableButton(confidentialB, value);
			if(value)
				contractAttach.setSecurityLevel((byte)1);
			else
				contractAttach.setSecurityLevel((byte)0);
		});
		
		// Attach DateBoxEx
		DateBoxEx dateBox = new DateBoxEx();
		dateBox.addValueChangeHandler((e) -> {
			contractAttach.setAttachDate(e.getValue());
		});
		
		// Scope ListBox
		ListBox scopeLB = new ListBox();
		initScopeListBox(scopeLB);
		scopeLB.addChangeHandler((e) -> {
			Integer scope = Integer.parseInt(scopeLB.getSelectedValue());
			contractAttach.setScope(scope);
		});
		
		// Upload & Download FormPanel
		Widget formPanel = createFormPanel(contractAttach);
		
		// Attach Delete Button
		AonTableButton deleteBTN = new AonTableButton("Eliminar", AON.CSS.aonIconDelete());
		deleteBTN.getElement().getStyle().setMarginTop(5, Unit.PX);
		deleteBTN.addClickHandler((e) -> {
			deleteContractAttach(contractAttach, 
					s -> {
						resetAttachDataTableStructure();
						for(ContractAttach contractAttachIn : employeeContractInfo.getContractAttachments())
							paintContractAttach(contractAttachIn);
					}, f -> {});
		});
		
		// Add Styles
		descriptionTB.addStyleName(style.maxWidthTB());
		typeLB.addStyleName(style.maxWidthLB());
		scopeLB.addStyleName(style.maxWidthLB());
		
		// If id != null exists then fill the fields
		if(null != contractAttach.getId()) {
			descriptionTB.setText(contractAttach.getDescription());
			setSelectedValueLB(typeLB, contractAttach.getType().toString());
			getEnableDisableButton(confidentialB, contractAttach.getSecurityLevel() == (byte)1);
			dateBox.setValue(contractAttach.getAttachDate());
			setSelectedValueLB(scopeLB, contractAttach.getScope().toString());
		}
		
		// It null == contract ? global_attachs cant be deleted
		if(null == contractAttach.getContract()) {
			descriptionTB.setReadOnly(true);
			typeLB.setEnabled(false);
			confidentialB.setEnabled(false);
			dateBox.setEnabled(false);
			scopeLB.setEnabled(false);
		}
		
		//Add to table
		attachmentsDataTable.setWidget(row, 0, descriptionTB);
		attachmentsDataTable.setWidget(row, 1, typeLB);
		attachmentsDataTable.setWidget(row, 2, confidentialB);
		attachmentsDataTable.setWidget(row, 3, dateBox);
		attachmentsDataTable.setWidget(row, 4, scopeLB);
		attachmentsDataTable.setWidget(row, 5, formPanel);
		if(null != contractAttach.getContract())
			attachmentsDataTable.setWidget(row, 6, deleteBTN);
		else
			attachmentsDataTable.setWidget(row, 6, new Label());
	}	
	
	// -------------------------------------------------- Paint Table Auxiliar Methods ---------------------------------------------------

	private Widget createFormPanel(ContractAttach contractAttach) {
		HorizontalPanel mainFlowPanel = new HorizontalPanel();
		mainFlowPanel.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		mainFlowPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		Label fileNameL = new Label();
		fileNameL.getElement().getStyle().setMarginLeft(5, Unit.PX);
		
		//Create formPanel to UploadFiles
		FlowPanel flowPanel = new FlowPanel();
		
		FormPanel formPanel = new FormPanel();
		formPanel.setAction(GWT.getModuleBaseURL()+ "attach/");
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		Hidden attachId = new Hidden("attachId", contractAttach.getId().toString());
		Hidden extension = new Hidden("extension", "");
		Hidden fileName = new Hidden("filename", "");
		
		FileUpload fileU = new FileUpload();
		fileU.setName("uploader");
		fileU.getElement().setPropertyString("multiple", "multiple");
		fileU.getElement().setPropertyString("accept", ".pdf|image/*");
		fileU.getElement().getStyle().setDisplay(Display.NONE);
		fileU.addChangeHandler((e) -> {
			String filename = getFileName(fileU.getFilename().toString());
			String fileExt = getFileExtension(fileU.getFilename());

            if(filename.length() == 0) {
            	Message errorMessage = messages.new Message();
            	errorMessage.setDescription("Error subir archivo");
            	errorMessage.setMessage("El archivo seleccionado no se ha podido subir");
            	messages.addErrorMessage(errorMessage);
            	fireMessagesResults(messages);
            } else {
            	contractAttach.setMimeType(getMimeTypeToByte(fileExt));
            	contractAttach.setDescription(filename);
            	extension.setValue(fileExt);
            	fileName.setValue(filename);
            	fileNameL.setText(filename);
            	
            	final int size = getFileSize(fileU.getElement());
            	if(size < 15000000)
            		formPanel.submit();
            	else {
            		fileNameL.setText("");
            		Message errorMessage = messages.new Message();
                	errorMessage.setDescription("Error tama" + String.valueOf("\u00F1") + "o archivo");
                	errorMessage.setMessage("El tama" + String.valueOf("\u00F1") + "o soportado es de 10 MB");
                	messages.addErrorMessage(errorMessage);
                	fireMessagesResults(messages);
            	}
            }
		});
		
		formPanel.addSubmitCompleteHandler((e) -> {
	        if(e.getResults().length() == 0) {
                Window.alert("Something went wrong - Try again");
            } else {
            	getContractAttachments( 
    					s -> {
    						resetAttachDataTableStructure();
    						for(ContractAttach contractAttachIn : employeeContractInfo.getContractAttachments())
    							paintContractAttach(contractAttachIn);
    					}, f -> {});
            }
	    });
		
		AonTableButton fileButton = new AonTableButton("Subir Documento", AON.CSS.aonIconAttach());
		fileButton.addClickHandler(e -> {
			fileU.click();
		});
	
		flowPanel.add(attachId);
		flowPanel.add(extension);
		flowPanel.add(fileName);
		flowPanel.add(fileButton);
		flowPanel.add(fileU);
		flowPanel.add(fileNameL);
		
		formPanel.add(flowPanel);
			
		mainFlowPanel.add(formPanel);
		if(null != contractAttach.getData()) {
			mainFlowPanel.add(getDownloadButton(contractAttach));
			fileNameL.setText(contractAttach.getDescription());
		}
		mainFlowPanel.add(fileNameL);
		return mainFlowPanel;
	}
	
	protected abstract void fireMessagesResults(Messages messages2);

	private Button getDownloadButton(ContractAttach contractAttach) {
		// Create download button
		AonTableButton downloadBtn = new AonTableButton("Descargar Documento", AON.CSS.aonIconDownload());
		downloadBtn.addClickHandler((e) -> {
//			Window.alert("Name : " + contractAttach.getDescription() + ", Ext : " + parseMimeTypeToString(contractAttach.getMimeType()));
			String fileDownloadURL = GWT.getModuleBaseURL()+ "attach/"
		            + "?attachId=" + contractAttach.getId() + "&extension=" + parseMimeTypeToString(contractAttach.getMimeType())
		            + "$filename=" + contractAttach.getDescription();
			
			Window.open(fileDownloadURL, "_blank", null);
		});
		
		return downloadBtn;
	}
	
	// ------------------------------------------------------ Auxiliar Methods ----------------------------------------------------
	
	private void initializeView() {
		initAttachmentsTable();
		paintHeaderAttachmentsTable();
		
		setScrollPanelsHeight();
		setColumnsWidth();
	}
	
	private void initAttachmentsTable() {
		attachmentsDataTableHeader.clear();
		attachmentsDataTableHeader.resize(0, 0);
		attachmentsDataTableHeader.resizeColumns(7);
		attachmentsDataTable.clear();
		attachmentsDataTable.resize(0, 0);
		attachmentsDataTable.resizeColumns(7);
	}
	
	private void setScrollPanelsHeight() {
		attachmentsScrollPanel.setHeight((Window.getClientHeight() - 390) + "px");
	}
	
	private void setColumnsWidth() {
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(270, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(190, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setWidth(35, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setWidth(95, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 4).getStyle().setWidth(95, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 5).getStyle().setWidth(225, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 6).getStyle().setWidth(20, Unit.PX);
		
		attachmentsDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(270, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(190, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(35, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(95, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(4).getStyle().setWidth(95, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(5).getStyle().setWidth(225, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(6).getStyle().setWidth(20, Unit.PX);
	}
	
	private void initScopeListBox(ListBox scopeLB) {
		scopeLB.clear();
		scopeLB.addItem("-", "-1");
		for(Entry<String, String> entry : employeeContractInfo.getScopeMap().entrySet()) {
			scopeLB.addItem(entry.getKey(), entry.getValue());
		}
	}

	private void initTypeListBox(ListBox typeLB) {
		typeLB.addItem("-", "-1");
		typeLB.addItem("Borrador del contrato", "0");
		typeLB.addItem("Contrato laboral", "1");
		typeLB.addItem("Borrador de copia basica", "2");
		typeLB.addItem("Copia basica", "3");
		typeLB.addItem("Domiciliacion bancaria", "7");
		typeLB.addItem("Anexo I", "8");
		typeLB.addItem("Anexo II", "9");
		typeLB.addItem("Borrador prorroga", "10");
		typeLB.addItem("Prorroga", "11");
		typeLB.addItem("Borrador del certificado de empresa", "22");
	}
	
	private String getFileName(String filename) {
		String[] splits = filename.split("\\\\");
		return splits[splits.length-1].contains("\\.") ? splits[splits.length-1].split("\\.")[0] : splits[splits.length-1];
	}
	
	private String getFileExtension(String filename) {
		String[] splits = filename.split("\\.");
		return splits[splits.length-1];
	}
	
	private String parseMimeTypeToString(Byte mimeType) {
		switch (mimeType) {
		case (byte)0:
			return "jpg";
		case (byte)6:
			return "png";
		case (byte)22:
			return "pdf";
		default:
			return "pdf";
		}
	}
	
	private byte getMimeTypeToByte(String extension) {
		if(extension.equals("pdf"))
			return (byte)22;
		else if(extension.equals("png")) {
			return (byte)6;
		} else if(extension.equals("jpg") || extension.equals("jpeg")) {
			return (byte)0;
		} else {
			return (byte)22;
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
	
	private native int getFileSize(final Element data) /*-{
    	return data.files[0].size;
	}-*/;
	
	// --------------------------------------------------------------------------------------------------------
	
	private void createContractAttach(ContractAttach contractAttach, Consumer<List<ContractAttach>> success, Consumer<Throwable> failure) {
		impl.createContractAttach(contractAttach, new AsyncCallback<List<ContractAttach>>() {
			
			@Override
			public void onSuccess(List<ContractAttach> contractAttachments) {
				employeeContractInfo.setContractAttachments(contractAttachments);
				success.accept(contractAttachments);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}

	private void deleteContractAttach(ContractAttach contractAttach, Consumer<List<ContractAttach>> success, Consumer<Throwable> failure) {
		impl.deleteContractAttach(contractAttach, new AsyncCallback<List<ContractAttach>>() {
			
			@Override
			public void onSuccess(List<ContractAttach> contractAttachments) {
				employeeContractInfo.setContractAttachments(contractAttachments);
				success.accept(contractAttachments);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	private void getContractAttachments(Consumer<List<ContractAttach>> success, Consumer<Throwable> failure) {
		Integer contractId = employeeContractInfo.getContractInfo().getContractId();
		impl.getContractAttachments(contractId, new AsyncCallback<List<ContractAttach>>() {
			
			@Override
			public void onSuccess(List<ContractAttach> contractAttachments) {
				employeeContractInfo.setContractAttachments(contractAttachments);
				success.accept(contractAttachments);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}

	public void setContractAttachments(List<ContractAttach> contractAttachments) {
		employeeContractInfo.setContractAttachments(contractAttachments);
	}
	
	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}
	
	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
	}

}

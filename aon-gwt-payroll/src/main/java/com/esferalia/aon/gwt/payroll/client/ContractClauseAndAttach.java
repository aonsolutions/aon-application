package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.payroll.shared.ContractAttach;
import com.esferalia.aon.gwt.payroll.shared.ContractClause;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContractClauseAndAttach extends ResizeComposite {

	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static ContractOtherDataUiBinder uiBinder = GWT.create(ContractOtherDataUiBinder.class);

	interface ContractOtherDataUiBinder extends UiBinder<Widget, ContractClauseAndAttach> {}

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
	VerticalPanel clausesTable;

	@UiField
	Grid clausesDataTableHeader;
	
	@UiField
	ScrollPanel clausesScrollPanel;
	
	@UiField
	Grid clausesDataTable;

	@UiField
	Label newClause;

	@UiField
	VerticalPanel attachmentsTable;

	@UiField
	Grid attachmentsDataTableHeader;
	
	@UiField
	ScrollPanel attachmentsScrollPanel;
	
	@UiField
	Grid attachmentsDataTable;

	@UiField
	Label newAttachment;
	
	// ------------------------------------------------------ Constructor ---------------------------------------------------------

	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private EmployeeContractInfo employeeContractInfo;
	
	public ContractClauseAndAttach() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setEmployeeContractInfo(EmployeeContractInfo employeeContractInfoIn) {
		this.employeeContractInfo = employeeContractInfoIn;
		initializeView();
		
		for(ContractClause contractClause : employeeContractInfo.getContractClauses())
			paintContractClause(contractClause);
		
		for(ContractAttach contractAttach : employeeContractInfo.getContractAttachments())
			paintContractAttach(contractAttach);
	}
	
	// --------------------------------------------------------- UiHandlers --------------------------------------------------------
	
	@UiHandler("newClause")
	public void onNewClauseClick(ClickEvent event) {
		ContractClause contractClause = new ContractClause();
		contractClause.setDomain(employeeContractInfo.getEmployeeInfo().getDomain());
		contractClause.setContract(employeeContractInfo.getContractInfo().getContractId());
		contractClause.setLineNumber((short)(employeeContractInfo.getContractClauses().size()+1));
		contractClause.setDescription("");
		
		createContractClause(contractClause,
				s -> {
					
					resetClauseDataTableStructure();
					
					for(ContractClause contractClauseIn : employeeContractInfo.getContractClauses())
						paintContractClause(contractClauseIn);
					
				}, f -> {});
	}

	@UiHandler("newAttachment")
	public void onNewAttachmentPartClick(ClickEvent event) {
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
	
	// --------------------------------------------------- UiHandlers (Aux Methods) -------------------------------------------------
	
	private void resetClauseDataTableStructure() {
		clausesDataTable.clear();
		clausesDataTable.resize(0, 0);
		clausesDataTable.resizeColumns(4);
		
		setColumnsWidth();
	}
	
	private void resetAttachDataTableStructure() {
		attachmentsDataTable.clear();
		attachmentsDataTable.resize(0, 0);
		attachmentsDataTable.resizeColumns(7);
		
		setColumnsWidth();
	}
	
	// -------------------------------------------------- Paint Table Header Methods --------------------------------------------------

	private void paintHeaderClausesTable() {
		int row = clausesDataTableHeader.insertRow(clausesDataTableHeader.getRowCount());
		
		Label line = new Label("L" + String.valueOf("\u00CD") + "NEA");
		Label name = new Label("NOMBRE");
		Label description = new Label("DESCRIPCI" + String.valueOf("\u00D3") + "N");
		Label action = new Label("");
		
		line.addStyleName(style.headerLabelStyle());
		name.addStyleName(style.headerLabelStyle());
		description.addStyleName(style.headerLabelStyle());
		action.addStyleName(style.headerLabelStyle());
		
		clausesDataTableHeader.setWidget(row, 0, line);
		clausesDataTableHeader.setWidget(row, 1, name);
		clausesDataTableHeader.setWidget(row, 2, description);
		clausesDataTableHeader.setWidget(row, 3, action);
	}
	
	private void paintHeaderAttachmentsTable() {
		int row = attachmentsDataTableHeader.insertRow(attachmentsDataTableHeader.getRowCount());
		
		Label description = new Label("DESCRIPCI" + String.valueOf("\u00D3") + "N");
		Label type = new Label("TIPO");
		Label confidential = new Label();
		confidential.setStyleName("aon-editDataTable-button aon-icon-confidential");
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
		CheckBox confidentialCB = new CheckBox();
		confidentialCB.addValueChangeHandler((e) -> {
			contractAttach.setSecurityLevel(e.getValue() ? (byte)0 : (byte)1);
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
		Button deleteBTN = new Button();
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
		deleteBTN.setStyleName("aon-editDataTable-button aon-icon-delete");
		
		// If id != null exists then fill the fields
		if(null != contractAttach.getId()) {
			descriptionTB.setText(contractAttach.getDescription());
			setSelectedValueLB(typeLB, contractAttach.getType().toString());
			confidentialCB.setValue(contractAttach.getSecurityLevel() == (byte)1 ? true : false);
			dateBox.setValue(contractAttach.getAttachDate());
			setSelectedValueLB(scopeLB, contractAttach.getScope().toString());
		}
		
		// It null == contract ? global_attachs cant be deleted
		if(null == contractAttach.getContract()) {
			descriptionTB.setReadOnly(true);
			typeLB.setEnabled(false);
			confidentialCB.setEnabled(false);
			dateBox.setEnabled(false);
			scopeLB.setEnabled(false);
		}
		
		//Add to table
		attachmentsDataTable.setWidget(row, 0, descriptionTB);
		attachmentsDataTable.setWidget(row, 1, typeLB);
		attachmentsDataTable.setWidget(row, 2, confidentialCB);
		attachmentsDataTable.setWidget(row, 3, dateBox);
		attachmentsDataTable.setWidget(row, 4, scopeLB);
		attachmentsDataTable.setWidget(row, 5, formPanel);
		if(null != contractAttach.getContract())
			attachmentsDataTable.setWidget(row, 6, deleteBTN);
		else
			attachmentsDataTable.setWidget(row, 6, new Label());
	}

	private void paintContractClause(ContractClause contractClause) {
		// Insert new row
		int row = this.clausesDataTable.insertRow(clausesDataTable.getRowCount());
		
		// Line TextBox
		TextBox lineTB = new TextBox();
		lineTB.addValueChangeHandler((e) -> {
			contractClause.setLineNumber(Short.parseShort(e.getValue()));
		});
		
		// Name TextBox
		TextBox nameTB = new TextBox();
		nameTB.addValueChangeHandler((e) -> {
			contractClause.setName(e.getValue());
		});
		
		// Description TextBox
		TextArea descriptionTA = new TextArea();
		descriptionTA.addValueChangeHandler((e) -> {
			contractClause.setDescription(e.getValue());
		});
		
		// Delete Button
		Button deleteBTN = new Button();
		deleteBTN.addClickHandler((e) -> {
			deleteContractClause(contractClause, 
					s -> {
						resetClauseDataTableStructure();
						for(ContractClause contractClauseIn : employeeContractInfo.getContractClauses())
							paintContractClause(contractClauseIn);
					}, f -> {});
		});
		
		// Add Styles
		lineTB.addStyleName(style.maxWidthTB());
		nameTB.addStyleName(style.maxWidthTB());
		descriptionTA.addStyleName(style.maxWidthTB());
		descriptionTA.setHeight("50px");
		deleteBTN.setStyleName("aon-editDataTable-button aon-icon-delete");
		
		// If id != null exists then fill the fields
		if(null != contractClause.getId()) {
			lineTB.setText(contractClause.getLineNumber() + "");
			nameTB.setText(contractClause.getName());
			descriptionTA.setValue(contractClause.getDescription());
		}
		
		// It null == contract ? global_attachs cant be deleted
		if(null == contractClause.getContract()) {
			lineTB.setReadOnly(true);
			nameTB.setReadOnly(true);
			descriptionTA.setReadOnly(true);
		}
		
		clausesDataTable.setWidget(row, 0, lineTB);
		clausesDataTable.setWidget(row, 1, nameTB);
		clausesDataTable.setWidget(row, 2, descriptionTA);
		if(null != contractClause.getContract())
			clausesDataTable.setWidget(row, 3, deleteBTN);
		else
			clausesDataTable.setWidget(row, 3, new Label());
		
		clausesDataTable.getCellFormatter().addStyleName(row, 0, style.clauseTD());
		clausesDataTable.getCellFormatter().addStyleName(row, 1, style.clauseTD());
		clausesDataTable.getCellFormatter().addStyleName(row, 3, style.clauseTD());
	}	
	
	// -------------------------------------------------- Paint Table Auxiliar Methods ---------------------------------------------------

	private Widget createFormPanel(ContractAttach contractAttach) {
		HorizontalPanel mainFlowPanel = new HorizontalPanel();
		mainFlowPanel.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		
		if(null != contractAttach.getData()) {
			mainFlowPanel.add(getDownloadButton(contractAttach));
		}
		
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
		fileU.addChangeHandler((e) -> {
			String filename = getFileName(fileU.getFilename().toString());
			String fileExt = getFileExtension(fileU.getFilename());

            if(filename.length() == 0) {
                Window.alert("File Upload failed");
            } else {
//            	Window.alert("Name : " + filename + ", Ext : " + fileExt);
            	contractAttach.setMimeType(getMimeTypeToByte(fileExt));
            	contractAttach.setDescription(filename);
            	extension.setValue(fileExt);
            	fileName.setValue(filename);
            	
            	final int size = getFileSize(fileU.getElement());
            	if(size < 15000000)
            		formPanel.submit();
            }
		});
		
		formPanel.addSubmitCompleteHandler((e) -> {
	        if(e.getResults().length() == 0) {
                Window.alert("Something went wrong - Try again");
            } else {
            	contractAttach.setData("".getBytes());
            	mainFlowPanel.clear();
            	mainFlowPanel.add(getDownloadButton(contractAttach));
            	mainFlowPanel.add(formPanel);
            }
	    });
	
		flowPanel.add(attachId);
		flowPanel.add(extension);
		flowPanel.add(fileName);
		flowPanel.add(fileU);
		
		formPanel.add(flowPanel);
			
		mainFlowPanel.add(formPanel);	
		return mainFlowPanel;
	}
	
	private Button getDownloadButton(ContractAttach contractAttach) {
		// Create download button
		Button downloadBtn = new Button();
		downloadBtn.setStyleName("aon-editDataTable-button aon-icon-mail-save");
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
		initClausesTable();
		paintHeaderClausesTable();
		
		initAttachmentsTable();
		paintHeaderAttachmentsTable();
		
		setScrollPanelsHeight();
		setColumnsWidth();
	}

	private void initClausesTable() {
		clausesDataTableHeader.clear();
		clausesDataTableHeader.resize(0, 0);
		clausesDataTableHeader.resizeColumns(4);
		clausesDataTable.clear();
		clausesDataTable.resize(0, 0);
		clausesDataTable.resizeColumns(4);
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
		clausesScrollPanel.setHeight((Window.getClientHeight()/2 - 150) + "px");
		attachmentsScrollPanel.setHeight((Window.getClientHeight()/2 - 150) + "px");
	}
	
	private void setColumnsWidth() {
		clausesDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(100, Unit.PX);
		clausesDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(200, Unit.PX);
		clausesDataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setWidth(585, Unit.PX);
		clausesDataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setWidth(50, Unit.PX);
		
		clausesDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(100, Unit.PX);
		clausesDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(200, Unit.PX);
		clausesDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(585, Unit.PX);
		clausesDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(50, Unit.PX);
		
		clausesDataTable.getColumnFormatter().addStyleName(0, style.clauseTD());
		clausesDataTable.getColumnFormatter().addStyleName(1, style.clauseTD());
		clausesDataTable.getColumnFormatter().addStyleName(3, style.clauseTD());
		
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(170, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(190, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setWidth(30, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setWidth(95, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 4).getStyle().setWidth(95, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 5).getStyle().setWidth(320, Unit.PX);
		attachmentsDataTableHeader.getCellFormatter().getElement(0, 6).getStyle().setWidth(30, Unit.PX);
		
		attachmentsDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(170, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(190, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(30, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(95, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(4).getStyle().setWidth(95, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(5).getStyle().setWidth(320, Unit.PX);
		attachmentsDataTable.getColumnFormatter().getElement(6).getStyle().setWidth(30, Unit.PX);
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
//		Window.alert("Complete file path : " + filename);
		String[] splits = filename.split("\\\\");
//		Window.alert("File path" + splits[splits.length-1]);
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
	
	private void createContractAttach(ContractAttach contractAttach, Consumer<ContractAttach> success, Consumer<Throwable> failure) {
		impl.createContractAttach(contractAttach, new AsyncCallback<ContractAttach>() {
			
			@Override
			public void onSuccess(ContractAttach result) {
				employeeContractInfo.addContractAttach(result);
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}

	private void deleteContractAttach(ContractAttach contractAttach, Consumer<ContractAttach> success, Consumer<Throwable> failure) {
		impl.deleteContractAttach(contractAttach, new AsyncCallback<ContractAttach>() {
			
			@Override
			public void onSuccess(ContractAttach result) {
				List<ContractAttach> newContractAttachs = new ArrayList<ContractAttach>();
				
				for(ContractAttach contractAttachAux : employeeContractInfo.getContractAttachments()) {
					if(result.getId() == contractAttachAux.getId() || result.getId().equals(contractAttachAux.getId()))
						continue;
					newContractAttachs.add(contractAttachAux);
				}
				
				employeeContractInfo.setContractAttachments(newContractAttachs);
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	private void createContractClause(ContractClause contractClause, Consumer<ContractClause> success, Consumer<Throwable> failure) {
		impl.createContractClause(contractClause, new AsyncCallback<ContractClause>() {
			
			@Override
			public void onSuccess(ContractClause result) {
				employeeContractInfo.addContractClause(result);
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}

	private void deleteContractClause(ContractClause contractClause, Consumer<ContractClause> success, Consumer<Throwable> failure) {
		impl.deleteContractClause(contractClause, new AsyncCallback<ContractClause>() {
			
			@Override
			public void onSuccess(ContractClause result) {
				List<ContractClause> newContractClauses = new ArrayList<ContractClause>();
				
				for(ContractClause contractClauseAux : employeeContractInfo.getContractClauses()) {
					if(result.getId() == contractClauseAux.getId() || result.getId().equals(contractClauseAux.getId()))
						continue;
					newContractClauses.add(contractClauseAux);
				}
				
				employeeContractInfo.setContractClauses(newContractClauses);
				
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}

}

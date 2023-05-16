package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.payroll.shared.Attach;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

public abstract class ContractAttachUI extends ResizeComposite {

	// -------------------------------------------------- UiBinder

	private static ContractAttachUIBinder uiBinder = GWT.create(ContractAttachUIBinder.class);

	interface ContractAttachUIBinder extends UiBinder<Widget, ContractAttachUI> {}

	// -------------------------------------------------- UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {}
	
	@UiField
	HTMLPanel mainContainer;

	@UiField (provided = true)
	DataGrid<Attach> contractAttachDG;

	// ------------------------------------------------------ Variables

	private static final String DOWNLOADURL = GWT.getModuleBaseURL() + "attach/download/";

	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private EmployeeContractInfo employeeContractInfo;
	
	private MultiSelectionModel<Attach> selectionModel;
	
	private List<Attach> contractAttachList;

	// ------------------------------------------------------ Constructor

	protected ContractAttachUI() {
		provideContractAttachDG();
		initWidget(uiBinder.createAndBindUi(this));
		setDataGridHeight();
	}

	// ----------------------------------------------- Auxiliar Methods (Constructor & DataGrid) 
	
	private void setDataGridHeight() {
		this.getElement().getStyle().setWidth((Window.getClientWidth() - 50), Unit.PX);
		contractAttachDG.setHeight((Window.getClientHeight() - 220) + "px");
	}
	
	// ----------------------------------------------- ProvideContractConceptCalcDG
	
	private void provideContractAttachDG() {
		contractAttachList = Collections.emptyList();
		
		// Resource Style CellTable
		contractAttachDG = new CustomDataGrid<>(Integer.MAX_VALUE, Attach.KEY_PROVIDER);
		
		//Do not refresh the headers every time the dataGrid is updated.
		contractAttachDG.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		contractAttachDG.setEmptyTableWidget(new Label(("No existen documentos").toUpperCase()));
		
		this.selectionModel = new MultiSelectionModel<>(Attach.KEY_PROVIDER);
		contractAttachDG.setSelectionModel(this.selectionModel, DefaultSelectionEventManager.<Attach> createCheckboxManager());
		
		// Initialize the columns.
	    addContractAttachColumns();
	    
	    new ListDataProvider<Attach>(Collections.emptyList()).addDataDisplay(contractAttachDG);
	    
	}
	
	private void addContractAttachColumns() {
		selectionModel.addSelectionChangeHandler(e -> onSelectionAttachChange(!selectionModel.getSelectedSet().isEmpty()));
	    
		Column<Attach, Boolean> checkColumn = new Column<Attach, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(Attach attach) {
				return selectionModel.isSelected(attach);
			}
	    };
    
	    CheckboxCell selectAllHeaderCB = new CheckboxCell(true,true);
	    Header<Boolean> selectAllHeader = new Header<Boolean>(selectAllHeaderCB) {
	    	@Override
	    	public Boolean getValue() {
	    		if(null == contractAttachList) return false;
	    		return selectionModel.getSelectedSet().size() == contractAttachList.size();
	    	}
	    	
	    };
	    
	    selectAllHeader.setUpdater(value -> contractAttachList.forEach(attach -> selectionModel.setSelected(attach, value)));
	    
	    contractAttachDG.addColumn(checkColumn,selectAllHeader);
	    contractAttachDG.setColumnWidth(checkColumn, 5, Unit.PCT);
	    checkColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		// Edit column.
	    ActionCell<Attach> editActionCell = new ActionCell<>("", selectedAttach -> openDialog(selectedAttach));
	    
	    Column<Attach, Attach> editColumn = new Column<Attach, Attach>(editActionCell) {

			@Override
			public Attach getValue(Attach attach) {
				return attach;
			}
			
			@Override
			public void render(Context context, Attach attach, SafeHtmlBuilder sb) {
				if(null != attach) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_right\" style=\"border: none !important; height: 20px;\" title=\"Editar\"></button>");
				}
			}
		};
		
		editColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contractAttachDG.setColumnWidth(editColumn, 5, Unit.PCT);		
	
		// Description columns.
		Column<Attach, String> descriptionColumn = new Column<Attach, String>(new TextCell()) {
			@Override
	        public String getValue(Attach attach) {
				return attach.getDescription();
	        }
		};

		descriptionColumn.setSortable(true);
		
		// Type columns.
		Column<Attach, String> typeColumn = new Column<Attach, String>(new TextCell()) {
			@Override
	        public String getValue(Attach attach) {
				return getAttachType(attach.getType());
	        }
		};

		typeColumn.setSortable(true);
		contractAttachDG.setColumnWidth(typeColumn, 20, Unit.PCT);
		
		// Visibility column.
	    ActionCell<Attach> visibilityActionCell = new ActionCell<>("", attach -> {});
	    
	    Column<Attach, Attach> visibilityColumn = new Column<Attach, Attach>(visibilityActionCell) {

			@Override
			public Attach getValue(Attach attach) {
				return attach;
			}
			
			@Override
			public void render(Context context, Attach attach, SafeHtmlBuilder sb) {
				if(null != attach) {
					sb.appendHtmlConstant(attach.getConfidential() ? "<button type=\"button\" class=\"aon_button aon_table_button aon_icon_lock\" style=\"border: none !important; height: 20px; cursor: default;\" title=\"Oculto\"></button>"
							: "<button type=\"button\" class=\"aon_button aon_table_button aon_icon_unlock\" style=\"border: none !important; height: 20px; cursor: default;\" title=\"Visible\"></button>");
				}
			}
		};
		
		visibilityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contractAttachDG.setColumnWidth(visibilityColumn, 10, Unit.PCT);
			
		// Date columns.
		Column<Attach, String> dateColumn = new Column<Attach, String>(new TextCell()) {
			@Override
	        public String getValue(Attach attach) {
				return null == attach.getDate() ? "" : formatDate.format(attach.getDate());
	        }
		};

		dateColumn.setSortable(true);
		dateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contractAttachDG.setColumnWidth(dateColumn, 10, Unit.PCT);
		
		// Scope columns.
		Column<Attach, String> scopeColumn = new Column<Attach, String>(new TextCell()) {
			@Override
	        public String getValue(Attach attach) {
				return null == attach.getScope() ? "N/DF" : getScope(attach.getScope());
	        }
		};

		scopeColumn.setSortable(true);
		scopeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contractAttachDG.setColumnWidth(scopeColumn, 10, Unit.PCT);
		
	    // Download column.
	    ActionCell<Attach> downloadActionCell = new ActionCell<>("", attach -> {
	    	if(isPDFAttach(attach)) {
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
	    	} else
	    		createDownloadForm(attach);
	    	
	    }); 
	    
	    Column<Attach, Attach> downloadColumn = new Column<Attach, Attach>(downloadActionCell) {

			@Override
			public Attach getValue(Attach attach) {
				return attach;
			}
			
			@Override
			public void render(Context context, Attach attach, SafeHtmlBuilder sb) {
				if(null != attach) {
					sb.appendHtmlConstant(isPDFAttach(attach) ? "<button type=\"button\" class=\"aon_button aon_table_button aon_icon_visibility\" style=\"border: none !important; height: 20px;\" title=\"Visualizar\"></button>"
							: "<button type=\"button\" class=\"aon_button aon_table_button aon_icon_download\" style=\"border: none !important; height: 20px;\" title=\"Descargar\"></button>");
				}
			}
		};
		
		downloadColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contractAttachDG.setColumnWidth(downloadColumn, 5, Unit.PCT);
	    
	    // Delete column.
	    ActionCell<Attach> deleteActionCell = new ActionCell<>("", attach -> {
	    	AonDialog deleteDialog = new AonDialog("Eliminar Documento", new HTML("\u00BFDesea eliminar el documento seleccionado\u003F"));
			deleteDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
			    	deleteContractAttach(attach.getId(), s -> {
			    		showSuccessMessage("Documento borrado", "El documento ha sido borrado correctamente");
			    		refreshPage();
			    	}, f -> {});
				}
			});
	    }); 
	    
	    Column<Attach, Attach> deleteColumn = new Column<Attach, Attach>(deleteActionCell) {

			@Override
			public Attach getValue(Attach attach) {
				return attach;
			}
			
			@Override
			public void render(Context context, Attach attach, SafeHtmlBuilder sb) {
				if(null != attach) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_delete\" style=\"border: none !important; height: 20px;\" title=\"Eliminar\"></button>");
				}
			}
		};
		
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		contractAttachDG.setColumnWidth(deleteColumn, 5, Unit.PCT);
		
	    // Add the columns.
		contractAttachDG.addColumn(editColumn, "");
		contractAttachDG.addColumn(descriptionColumn, "Descripci\u00F3n");
		contractAttachDG.addColumn(typeColumn, "Tipo");
		contractAttachDG.addColumn(visibilityColumn, "Visibilidad"); 
		contractAttachDG.addColumn(dateColumn, "Fecha");
		contractAttachDG.addColumn(scopeColumn, "Ambito");
		contractAttachDG.addColumn(downloadColumn, "");  
		contractAttachDG.addColumn(deleteColumn, "");  
	}

	private void openDialog(Attach selectedAttach) {
		new ContractAttachDialog(selectedAttach, employeeContractInfo.getScopeMap()) {

			@Override
			protected void onSuccess(String message) {
				showSuccessMessage("Modificaci\u00f3n PDF", message);
				refreshPage();
			}

			@Override
			protected void onError(String message) {
				showErrorMessage("Error Modificaci\u00f3n PDF", message);
			}};
	}
	
	private String getAttachType(Byte type) {
		switch (type) {
			case (byte)0:
				return "Borrador del contrato";
			case (byte)1:
				return "Copia Contrato laboral";
			case (byte)7:
				return "Domiciliacion bancaria";
			case (byte)8:
				return "Anexo I";
			case (byte)9:
				return "Anexo II";
			case (byte)10:
				return "Borrador prorroga";
			case (byte)11:
				return "Prorroga";
			case (byte)22:
				return "Borrador Certific\u00402";
			case (byte)98:
				return "TA (Alta)";
			case (byte)99:
				return "TA (Baja)";
			case (byte)101:
				return "Contrato (Comunicaci\u00f3n SEPE)";
			case (byte)102:
				return "Copia basica (Comunicaci\u00f3n SEPE)";
			case (byte)103:
				return "Certific\u00402 (Pdf)";
			case (byte)104:
				return "IDC";
			case (byte)105:
				return "IDCPlNss";
			case (byte)106:
				return "Otros";
			case (byte)107:
				return "Notificaci\u00f3n Laboral";
			case (byte)108:
				return "Transformaci\u00f3n (Comunicaci\u00f3n SEPE)";
			case (byte)109:
				return "Borrador trasnformaci\u00f3n contrato";
			case (byte)110:
				return "Borrador pr\u00f3rroga contrato";
			default:
				return "";
		}
	}
	
	private String getScope(Integer scope) {
		for(Entry<String, String> entry : employeeContractInfo.getScopeMap().entrySet())
			if(AonStringUtils.equalsIgnoreCase(entry.getValue(), scope.toString()))
				return entry.getKey();
				
		return "";
	}
	
	private boolean isPDFAttach(Attach attach) {
		return null != attach.getId() && attach.getMimeType().isPDF();
	}
	
	private void createDownloadForm(Attach attach) {
		// Hiddens
		Hidden userLoginHidden = new Hidden("login", Wnd.getCurrentUser());
		Hidden currentDomainHidden = new Hidden("domain", Wnd.getCurrentDomainNameURL());
		Hidden attachIdHidden = new Hidden("attachId", attach.getId() + "");
		
		// Create Form Panel
		FormPanel form = new FormPanel();
		form.setAction(DOWNLOADURL);
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.addSubmitCompleteHandler(e -> mainContainer.remove(form));
		
		// Add all to FlowPanel to add to FormPanel
		HTMLPanel flowFormPanel = new HTMLPanel("");
		flowFormPanel.add(userLoginHidden);
		flowFormPanel.add(currentDomainHidden);
		flowFormPanel.add(attachIdHidden);
		form.add(flowFormPanel);
		mainContainer.add(form);
		
		form.submit();
	}
	
	// ----------------------------------------------- InitContractConceptCalcs

	public void initContractAttachTable() {	
		// Create a data provider.
		ListDataProvider<Attach> contractAttachDataProvider = new ListDataProvider<>();

	    // Connect the table to the data provider.
		contractAttachDataProvider.addDataDisplay(contractAttachDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<Attach> contractAttachListAux = contractAttachDataProvider.getList();
	    contractAttachListAux.clear();
	    
	    this.contractAttachList = employeeContractInfo.getContractAttachments();
	    
	    for (Attach attach : this.contractAttachList) {
	    	contractAttachListAux.add(attach);
	    }   
		
		addSortColums(contractAttachListAux);
	    
		// Set page size
		contractAttachDG.setPageSize(contractAttachListAux.size());
		
		setDataGridHeight();
		
	}

	private void addSortColums(List<Attach> contractAttachList) {
		ListHandler<Attach> columnSortHandler = new ListHandler<>(contractAttachList);
		
		columnSortHandler.setComparator(contractAttachDG.getColumn(1),
			(o1, o2) -> compareString(o1, o2, o1.getDescription(), o2.getDescription()));
		
		columnSortHandler.setComparator(contractAttachDG.getColumn(2),
				(o1, o2) -> compareString(o1, o2, getAttachType(o1.getType()), getAttachType(o2.getType())));
		
		columnSortHandler.setComparator(contractAttachDG.getColumn(4), 
		    	(o1, o2) -> compareDates(o1, o2, o1.getDate(), o2.getDate()));
	    
		contractAttachDG.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
		contractAttachDG.getColumn(1).setDefaultSortAscending(false);
		contractAttachDG.getColumnSortList().push(contractAttachDG.getColumn(1));   
	}
	
	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return s1.compareTo(s2);
	}
	
	private int compareDates(Object o1, Object o2, Date d1, Date d2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return d1.compareTo(d2);
	}
	
	// ------------------------------------------------------ setEmployeeContractInfo

	public void setEmployeeContractInfo(EmployeeContractInfo employeeContractInfoIn) {
		this.employeeContractInfo = employeeContractInfoIn;
		refreshPage();
	}

	// ------------------------------------------------------ ContractAttach.CRUD (Methods)

	private void deleteContractAttach(Integer attachId, Consumer<Void> success, Consumer<Throwable> failure) {
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
		Integer contractId = employeeContractInfo.getContractInfo().getContractId();
		
		new ContractAttachDialog(new Attach().setDate(new Date()).setAttachModule(contractId), employeeContractInfo.getScopeMap()) {

			@Override
			protected void onSuccess(String message) {
				showSuccessMessage("Modificaci\u00f3n PDF", message);
				refreshPage();
			}

			@Override
			protected void onError(String message) {
				showErrorMessage("Error Modificaci\u00f3n PDF", message);
			}};
	}

	public void exportContract() {
		if(existContract()) {
			AonDialog confirm = new AonDialog("Generar borrador contrato", new HTMLPanel("Ya existe un borrador del contrato generado. \u00bfRealmente desea sobreescribirlo\u003f"));
			confirm.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do
				}
				
				@Override
				public void onAccept() {
					exportContractPDF();
				}
			});
		} else
			exportContractPDF();
		
	}
	
	public void exportTransformContract() {
		if(existContractTransform()) {
			AonDialog confirm = new AonDialog("Generar borrador trasnfromaci\u00f3n contrato", new HTMLPanel("Ya existe un borrador de la transformaci\u00f3n del contrato generado. \u00bfRealmente desea sobreescribirlo\u003f"));
			confirm.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do
				}
				
				@Override
				public void onAccept() {
					exportContractTransformPDF();
				}
			});
		} else
			exportContractTransformPDF();
		
	}
	
	public void exportExtensionContract() {
		if(existContractExtension()) {
			AonDialog confirm = new AonDialog("Generar borrador pr\u00f3rroga contrato", new HTMLPanel("Ya existe un borrador de la pr\u00f3rroga del contrato generado. \u00bfRealmente desea sobreescribirlo\u003f"));
			confirm.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do
				}
				
				@Override
				public void onAccept() {
					exportContractExtensionPDF();
				}
			});
		} else
			exportContractExtensionPDF();
		
	}
	
	private void exportContractPDF() {
		onExportPDF(e -> {
			showSuccessMessage("Contrato", "El contrato se ha generado correctamente");
			refreshPage();
		}, f -> showErrorMessage("Contrato", f.getMessage()));
	}
	
	private void exportContractTransformPDF() {
		onExportTransformPDF(e -> {
			showSuccessMessage("Contrato Transformaci\u00f3n", "El PDF trasformaci\u00f3n contrato se ha generado correctamente");
			refreshPage();
		}, f -> showErrorMessage("Contrato Transformaci\u00f3n", f.getMessage()));
	}
	
	private void exportContractExtensionPDF() {
		onExportExtensionPDF(e -> {
			showSuccessMessage("Contrato Pr\u00f3rroga", "El PDF pr\u00f3rroga contrato se ha generado correctamente");
			refreshPage();
		}, f -> showErrorMessage("Contrato Pr\u00f3rroga", f.getMessage()));
	}
	
	public void setAttachData(Integer attachId, String base64) {
		if(null == attachId || null == base64) return;
		
		showLoadingMessagePDF("Guardando documento...");
		impl.setAttachData(attachId, base64, new AsyncCallback<Void>() {
			
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
	
	public void sendAttachEmail() {
		new AttachEmailDialog() {
			
			@Override
			protected void onSendEmail() {
				showLoadingMessage("Enviando email ...");
				
				List<Integer> attachIds = selectionModel.getSelectedSet().stream().map(attach -> attach.getId()).collect(Collectors.toList());
				MailAccount emailFrom = getFromMAilAccount();
				String emailTo = getSendTo();
				List<String> ccTo = getCCTo();
				List<String> bccTo = getBCCTo();
				String subject = getSubject();
				String emailBody = getBody();
				
				impl.sendAttachEmail(emailFrom, emailTo, ccTo, bccTo, subject, emailBody, attachIds, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage("Erro mail", caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						showSuccessMessage("Mail documentos", "El email se ha enviado correctamente");
					}
					
				});
			}
		};
	}

	// ------------------------------------------------------ Abstract Methods

	protected abstract void onExportPDF(Consumer<String> consumer, Consumer<Throwable> failure);
	
	protected abstract void onExportTransformPDF(Consumer<String> consumer, Consumer<Throwable> failure);
	
	protected abstract void onExportExtensionPDF(Consumer<String> consumer, Consumer<Throwable> failure);
	
	protected abstract void showAttachPDf(Integer attachId, String dataURI);

	protected abstract void showErrorMessage(String title, String message);

	protected abstract void showSuccessMessage(String title, String message);
	
	protected abstract void showSuccessMessagePDF(String title, String message);
	
	protected abstract void showLoadingMessage(String message);
	
	protected abstract void showLoadingMessagePDF(String message);
	
	protected abstract void onSelectionAttachChange(boolean isSomethingSelected);
	
	// ------------------------------------------ Setter Methods
	
	public Set<Attach> getSelectedSalaries() {
		return this.selectionModel.getSelectedSet();
	}

	// ------------------------------------------------------ Refresh table

	public void refreshPage() {
		getContractAttachments(s -> initContractAttachTable(), f -> {});
	}

	public void modificationPDF() {
		new ModificationPDFDialog(employeeContractInfo.getContractInfo().getContractId()) {

			@Override
			protected void onSuccess(String message) {
				showSuccessMessage("Modificaci\u00f3n PDF", message);
				refreshPage();
			}

			@Override
			protected void onError(String message) {
				showErrorMessage("Error Modificaci\u00f3n PDF", message);
			}};
	}

	public boolean existContract() {
		for (Attach attach : employeeContractInfo.getContractAttachments())
			if(attach.getType().equals((byte)0))
				return true;
		return false;
	}
	
	public boolean existContractTransform() {
		for (Attach attach : employeeContractInfo.getContractAttachments())
			if(attach.getType().equals((byte)109))
				return true;
		return false;
	}
	
	public boolean existContractExtension() {
		for (Attach attach : employeeContractInfo.getContractAttachments())
			if(attach.getType().equals((byte)110))
				return true;
		return false;
	}

}

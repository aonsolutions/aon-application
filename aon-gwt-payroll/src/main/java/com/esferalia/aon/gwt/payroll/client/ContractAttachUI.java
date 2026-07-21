package com.esferalia.aon.gwt.payroll.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.payroll.client.ContractAttachPanel.AonContractAttachPanelCallback;
import com.esferalia.aon.gwt.payroll.shared.Attach;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class ContractAttachUI extends ScrollPanel {

	// ------------------------------------------------------ Variables

	private static final String DOWNLOADURL = GWT.getModuleBaseURL() + "attach/download/";

	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private EmployeeContractInfo employeeContractInfo;
	
	private SimplePanel container;
	private AonCustomTable tab;
	
	private Map<Integer, Attach> rowAttachs = new HashMap<>();
	private Map<Integer, AonTableButton> selectedItems = new HashMap<>();
	
	private Map<Integer, String> scopes = new HashMap<Integer, String>();
	
	private static enum COLUMN {
		  CHK(AonStringUtils.EMPTY					,"2rem"				,"")
		, DES("Descripci\u00f3n"					,"-moz-available"  	,"min-width: 10rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, TYP("Tipo"								,"15rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, DAT("Fecha"								,"5rem"  			,"")
		, SCO("Ambito"								,"20rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUT(AonStringUtils.EMPTY					,"5rem" 			,"")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLUMN(String headerLabel,String colWidth) {
			this(headerLabel, colWidth, null);
		}

		private COLUMN(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}

	// ------------------------------------------------------ Constructor

	protected ContractAttachUI() {
		this.setHeight("100%");
		
		container = new SimplePanel();
		container.getElement().getStyle().setProperty("padding", "0 1rem");
		setWidget(container);
	}
	
	private void onSearch() {
		search();
	}

	private void search() {
		container.clear();
		selectedItems.clear();
		tab = new AonCustomTable();
		
		paintHeader();
		container.setWidget(tab);
		searchData();
	}
	
	private void paintHeader() {
		HTMLPanel header = tab.createHeader();
		header.getElement().getStyle().setProperty("top", "0px");
		
		for ( COLUMN col : COLUMN.values()) 
			if(col == COLUMN.CHK) {
				AonTableButton checkAllButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
				checkAllButton.addClickHandler(e -> {
					List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
					if (selectedItemList.size() == rowAttachs.size() || AonStringUtils.containsIgnoreCase(checkAllButton.getStyleName(), AON.CSS.aonIconChecked())) {
						checkAllButton.addStyleName(AON.CSS.aonIconCheck());
						checkAllButton.removeStyleName(AON.CSS.aonIconChecked());
						selectedItems.values().forEach(check ->{
							check.addStyleName(AON.CSS.aonIconCheck());
							check.removeStyleName(AON.CSS.aonIconChecked());
						});
						onSelectionAttachChange(false);
					} else {
						checkAllButton.addStyleName(AON.CSS.aonIconChecked());
						checkAllButton.removeStyleName(AON.CSS.aonIconCheck());
						selectedItems.values().forEach(check ->{
							check.addStyleName(AON.CSS.aonIconChecked());
							check.removeStyleName(AON.CSS.aonIconCheck());
						});
						onSelectionAttachChange(true);
					}
				});
				
				tab.addHeader(checkAllButton, col.getColWidth());
			} else 
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
				
	}
	
	private void searchData() {
		showLoadingMessage("Obteniendo documentos ...");
		getList(attachs -> {
			onHideMessage();
			
			boolean something = false;
			
			for(Attach attach : attachs) {
				something = true;
				paintRow(attach);
			}
			
			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				container.clear();
				container.add(line);
			}
			
		}, f -> showErrorMessage("Obtenci\u00f3n documentos", f.getMessage()));
	}
	
	private void paintRow(Attach attach) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton downloadButton = new AonTableButton(isPDFAttach(attach) ? "Ver documento" : "Descargar", isPDFAttach(attach) ? AON.CSS.aonIconPdf() : AON.CSS.aonIconDownload());
		downloadButton.addStyleName(AON.CSS.aonCustomRowButtom());
		downloadButton.addClickHandler(e -> {
			e.stopPropagation();
			downloadButton.setEnabled(false);
			
			if(isPDFAttach(attach)) {
	    		showLoadingMessage("Cargando archivo...");
				impl.getAttachData(attach.getId(), new AsyncCallback<String>() {
					
					@Override
					public void onSuccess(String dataURI) {
						showAttachPDf(attach.getId(), dataURI);
						downloadButton.setEnabled(true);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						downloadButton.setEnabled(true);
					}
					
				});
	    	} else
	    		createDownloadForm(attach, buttonContainer);
			
		});
		buttonContainer.add(downloadButton);
		
		AonTableButton deleteButton = new AonTableButton("Borrar Documento", AON.CSS.aonIconDelete());
		deleteButton.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteButton.addClickHandler(e -> {
			e.stopPropagation();
			deleteButton.setEnabled(false);
			
			AonDialog deleteDialog = new AonDialog("Eliminaci\u00f3n Documentos",
					new HTML("Se va a proceder a eliminar el documento <b>" + attach.getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			deleteDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}
				
				@Override
				public void onAccept() {
					deleteContractAttach(attach.getId(), s -> {
			    		showSuccessMessage("Documento borrado", "El documento ha sido borrado correctamente");
			    		onSearch();
			    	}, f -> {});
				}
			});
			
		});
		buttonContainer.add(deleteButton);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> openDialog(attach), ClickEvent.getType());
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
		checkButton.addClickHandler(e -> {
			e.stopPropagation();
			if (AonStringUtils.containsIgnoreCase(checkButton.getStyleName(), AON.CSS.aonIconChecked())) {
				checkButton.addStyleName(AON.CSS.aonIconCheck());
				checkButton.removeStyleName(AON.CSS.aonIconChecked());
			} else {
				checkButton.addStyleName(AON.CSS.aonIconChecked());
				checkButton.removeStyleName(AON.CSS.aonIconCheck());
			}
			List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
			onSelectionAttachChange(!selectedItemList.isEmpty());
		});
		tab.addRow(row, checkButton, COLUMN.CHK.getColWidth());
		
		Label description = new Label(attach.getDescription());
		description.setTitle(attach.getDescription());
		tab.addInlineStyle(description, COLUMN.DES.getCellStyleClass());
		tab.addRow(row, description, COLUMN.DES.getColWidth());
		
		Label type = new Label(getAttachType(attach.getType()));
		type.setTitle(getAttachType(attach.getType()));
		tab.addInlineStyle(type, COLUMN.TYP.getCellStyleClass());
		tab.addRow(row, type, COLUMN.TYP.getColWidth());
		
		tab.addRow(row, new Label(null == attach.getDate() ? "" : formatDate.format(attach.getDate())), COLUMN.DAT.getColWidth());
		
		Label scope = new Label(null == attach.getScope() ? "N/DF" : getScope(attach.getScope()));
		scope.setTitle(null == attach.getScope() ? "N/DF" : getScope(attach.getScope()));
		tab.addInlineStyle(scope, COLUMN.SCO.getCellStyleClass());
		tab.addRow(row, scope, COLUMN.SCO.getColWidth());
		
		tab.addRow(row, buttonContainer, COLUMN.BUT.getColWidth());
		
		rowAttachs.put(attach.getId(), attach);
		selectedItems.put(attach.getId(), checkButton);
	}

	private void openDialog(Attach selectedAttach) {
		AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "DOCUMENTO" );
		ContractAttachPanel contractAttachPanel = new ContractAttachPanel( selectedAttach, scopes, new AonContractAttachPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept() {
				dialog.hide();
				onSearch();
			}
		});
		
		dialog.setWidget(contractAttachPanel);
		dialog.showLoaded();
	}
	
	private String getAttachType(Byte type) {
		switch (type) {
			case (byte)0:
				return "Borrador del contrato";
			case (byte)1:
				return "Borrador de la copia basica";
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
			case (byte)111:
				return "Borrador propuesta recolocaci\u00f3n";
			default:
				return "";
		}
	}
	
	private String getScope(Integer scope) {
		return scopes.getOrDefault(scope, "N/DF");
	}
	
	private boolean isPDFAttach(Attach attach) {
		return null != attach.getId() && attach.getMimeType().isPDF();
	}
	
	private void createDownloadForm(Attach attach, FlowPanel buttonContainer) {
		// Hiddens
		Hidden userLoginHidden = new Hidden("login", Wnd.getCurrentUser());
		Hidden currentDomainHidden = new Hidden("domain", Wnd.getCurrentDomainNameURL());
		Hidden attachIdHidden = new Hidden("attachId", attach.getId() + "");
		
		// Create Form Panel
		FormPanel form = new FormPanel();
		form.setAction(DOWNLOADURL);
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.addSubmitCompleteHandler(e -> buttonContainer.remove(form));
		
		// Add all to FlowPanel to add to FormPanel
		HTMLPanel flowFormPanel = new HTMLPanel("");
		flowFormPanel.add(userLoginHidden);
		flowFormPanel.add(currentDomainHidden);
		flowFormPanel.add(attachIdHidden);
		form.add(flowFormPanel);
		buttonContainer.add(form);
		
		form.submit();
	}
	
	// ------------------------------------------------------ setEmployeeContractInfo

	public void setEmployeeContractInfo(EmployeeContractInfo employeeContractInfoIn) {
		this.employeeContractInfo = employeeContractInfoIn;
		getScopes(finish -> onSearch());
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

	private void getList(Consumer<List<Attach>> success, Consumer<Throwable> failure) {
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
	
	private void getScopes(Consumer<Map<Integer, String>> success) {
		impl.getScopes(new AsyncCallback<Map<Integer, String>>() {
			
			@Override
			public void onSuccess(Map<Integer, String> scopesDB) {
				scopes = scopesDB;
				success.accept(scopesDB);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage("Error ambitos", caught.getMessage() );
			}
		});
	}

	// ------------------------------------------------------ Toolbar methods

	public void newAttachment() {
		AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "DOCUMENTO" );
		ContractAttachPanel contractAttachPanel = new ContractAttachPanel( employeeContractInfo.getEmployeeInfo().getDomain(), employeeContractInfo.getContractInfo().getContractId(), scopes, new AonContractAttachPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept() {
				dialog.hide();
				onSearch();
			}
		});
		
		dialog.setWidget( contractAttachPanel );
		dialog.showLoaded();
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
	
	public void exportBasicCopy() {
		if(existBasicCopy()) {
			AonDialog confirm = new AonDialog("Generar borrador copia basica", new HTMLPanel("Ya existe un borrador de la copia basica generado. \u00bfRealmente desea sobreescribirlo\u003f"));
			confirm.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do
				}
				
				@Override
				public void onAccept() {
					exportBasicCopyPDF();
				}
			});
		} else
			exportBasicCopyPDF();
		
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
	
	public void exportRelocationContract() {
		if(existContractRelocation()) {
			AonDialog confirm = new AonDialog("Borrador propuesta recolocaci\u00f3n", new HTMLPanel("Ya existe un borrador de la propuesta de recolocaci\u00f3n del contrato generado. \u00bfRealmente desea sobreescribirlo\u003f"));
			confirm.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do
				}
				
				@Override
				public void onAccept() {
					exportContractRelocationPDF();
				}
			});
		} else
			exportContractRelocationPDF();
		
	}
	
	private void exportContractPDF() {
		onExportPDF(e -> {
			showSuccessMessage("Contrato", "El contrato se ha generado correctamente");
			onSearch();
		}, f -> showErrorMessage("Contrato", f.getMessage()));
	}
	
	private void exportBasicCopyPDF() {
		onExportBasicCopyPDF(e -> {
			showSuccessMessage("Copia Basica", "La Copia Basica se ha generado correctamente");
			onSearch();
		}, f -> showErrorMessage("Copia Basica", f.getMessage()));
	}
	
	private void exportContractTransformPDF() {
		onExportTransformPDF(e -> {
			showSuccessMessage("Contrato Transformaci\u00f3n", "El PDF trasformaci\u00f3n contrato se ha generado correctamente");
			onSearch();
		}, f -> showErrorMessage("Contrato Transformaci\u00f3n", f.getMessage()));
	}
	
	private void exportContractExtensionPDF() {
		onExportExtensionPDF(e -> {
			showSuccessMessage("Contrato Pr\u00f3rroga", "El PDF pr\u00f3rroga contrato se ha generado correctamente");
			onSearch();
		}, f -> showErrorMessage("Contrato Pr\u00f3rroga", f.getMessage()));
	}
	
	private void exportContractRelocationPDF() {
		new ContractRelocationDialog(employeeContractInfo.getContractInfo().getContractType()) {
			
			@Override
			protected void onAccept(Map<String, String> contractRelocationInfo) {
				onExportRelocationPDF(contractRelocationInfo, e -> {
					showSuccessMessage("Propuesta Recolocaci\u00f3n", "El PDF Propuesta Recolocaci\u00f3n se ha generado correctamente");
					onSearch();
				}, f -> showErrorMessage("Propuesta Cecolocaci\u00f3n", f.getMessage()));
			}
			
		};
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
				showErrorMessage("Error guardado", caught.getMessage());
			}
			
		});
	}
	
	public void sendAttachEmail() {
		new AttachEmailDialog() {
			
			@Override
			protected void onSendEmail() {
				showLoadingMessage("Enviando email ...");
				
				List<Integer> selectedAttachIds = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).map(entry -> entry.getKey()).collect(Collectors.toList());
				
				MailAccount emailFrom = getFromMAilAccount();
				String emailTo = getSendTo();
				List<String> ccTo = getCCTo();
				List<String> bccTo = getBCCTo();
				String subject = getSubject();
				String emailBody = getBody();
				
				impl.sendAttachEmail(emailFrom, emailTo, ccTo, bccTo, subject, emailBody, selectedAttachIds, new AsyncCallback<Void>() {

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
	
	protected abstract void onExportBasicCopyPDF(Consumer<String> consumer, Consumer<Throwable> failure);
	
	protected abstract void onExportTransformPDF(Consumer<String> consumer, Consumer<Throwable> failure);
	
	protected abstract void onExportExtensionPDF(Consumer<String> consumer, Consumer<Throwable> failure);
	
	protected abstract void onExportRelocationPDF(Map<String, String> contractRelocationInfo, Consumer<String> consumer, Consumer<Throwable> failure);
	
	protected abstract void showAttachPDf(Integer attachId, String dataURI);

	protected abstract void showErrorMessage(String title, String message);

	protected abstract void showSuccessMessage(String title, String message);
	
	protected abstract void showSuccessMessagePDF(String title, String message);
	
	protected abstract void showLoadingMessage(String message);
	
	protected abstract void showLoadingMessagePDF(String message);
	
	protected abstract void onHideMessage();
	
	protected abstract void onSelectionAttachChange(boolean isSomethingSelected);
	
	// ------------------------------------------ Setter Methods
	
	public List<Attach> getSelectedSalaries() {
		List<Integer> selectedAttachIds = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).map(entry -> entry.getKey()).collect(Collectors.toList());
		List<Attach> selectedAttachs = rowAttachs.entrySet().stream().filter(entry -> selectedAttachIds.contains(entry.getKey())).map(entry -> entry.getValue()).collect(Collectors.toList());
		
		return selectedAttachs;
	}

	// ------------------------------------------------------ Refresh table

	public void modificationPDF() {
		new ModificationPDFDialog(employeeContractInfo.getContractInfo().getContractId()) {

			@Override
			protected void onSuccess(String message) {
				showSuccessMessage("Modificaci\u00f3n PDF", message);
				onSearch();
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
	
	public boolean existBasicCopy() {
		for (Attach attach : employeeContractInfo.getContractAttachments())
			if(attach.getType().equals((byte)1))
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
	
	public boolean existContractRelocation() {
		for (Attach attach : employeeContractInfo.getContractAttachments())
			if(attach.getType().equals((byte)111))
				return true;
		return false;
	}

}

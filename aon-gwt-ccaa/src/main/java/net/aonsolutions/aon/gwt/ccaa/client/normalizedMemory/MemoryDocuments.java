package net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory;

import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Upload;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIcon;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.DepositType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.aon.gwt.ccaa.client.Deposit2;
import net.aonsolutions.aon.gwt.ccaa.shared.MemoryFiles;

public class MemoryDocuments extends PageAbs {

	private static final String D2_FILE_MEMORY = "Memoria";
	private static final String D2_FILE_AUTOCARTERA_MODEL = "Modelo de Autocartera";
	private static final String D2_FILE_GESTION = "Informe de Gesti\u00f3n";
	private static final String D2_FILE_AUDIT = "Informe de Auditor\u00eda";
	private static final String D2_FILE_TITULAR_REAL = "Informe de Titular Real";
	private static final String D2_FILE_NO_FINANCIERA= "Informe sobre Informaci\u00f3n no financiera";
	private static final String D2_FILE_CONVOC = "Anuncios de Convocatoria";
	private static final String D2_FILE_SICAV = "Certificaci\u00f3n SICAV";
	
	@UiField InlineLabel title1;
	
	interface MemoryDocumentsBinder extends UiBinder<Widget, MemoryDocuments> {
	}

	private static final MemoryDocumentsBinder binder = GWT
			.create(MemoryDocumentsBinder.class);

	Vector<MemoryFiles> memoryFiles = new Vector<MemoryFiles>();
//	TreeNode<Enterprise> memory;
//	TreeNode<Enterprise> autocartera;
	
	public MemoryDocuments(Deposit2 deposit) {
		super(deposit);
		
//		this.memory = memory;
//		this.autocartera = autocartera;
		
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
		title1.setText("Documentos");
		
		initializeTable();
	}

	@Override
	protected void initializeTable() {
		getDeposit().getInma().getMemoryFiles(getAonData(), getYear(), new AsyncCallback<Vector<MemoryFiles>>() {
			
			@Override
			public void onSuccess(Vector<MemoryFiles> result) {
				defineDocuments(table, result);				
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
		
	}

	protected void defineDocuments( FlexTable tab, Vector<MemoryFiles> ms){
		memoryFiles = new Vector<MemoryFiles>();
		if(ms.get(0).getBool()) memoryFiles.add(ms.get(0));
		if(ms.get(1).getBool()) memoryFiles.add(ms.get(1));
		if(ms.get(2).getBool()) memoryFiles.add(ms.get(2));
		if(ms.get(3).getBool()) memoryFiles.add(ms.get(3));
		if(ms.get(4).getBool()) memoryFiles.add(ms.get(4));
		if(ms.get(5).getBool()) memoryFiles.add(ms.get(5));
		if(ms.get(6).getBool()) memoryFiles.add(ms.get(6));
		
		tab.setWidth("100%");
		tab.setCellSpacing(0);
		tab.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().addStyleName(1, AON.AON_CSS.aonWidth130());
		int row = 0;
		tab.setWidget(row, 0, new Label("Descripci\u00f3n"));
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		tab.setWidget(row, 1, new Label("Acciones"));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextLeft());
		
		paintKey(tab, D2_FILE_MEMORY, 1, ms.get(0));
		paintKey(tab, D2_FILE_AUTOCARTERA_MODEL, 2, ms.get(1));
		paintKey(tab, D2_FILE_GESTION, 3, ms.get(2));
		paintKey(tab, D2_FILE_AUDIT, 4, ms.get(3));
		if(getYear() >= 2017) {
			paintKey(tab, D2_FILE_NO_FINANCIERA, 5, ms.get(4));
		} else {
			paintKey(tab, D2_FILE_TITULAR_REAL, 5, ms.get(4));

		}
		paintKey(tab, D2_FILE_CONVOC, 6, ms.get(5));
		paintKey(tab, D2_FILE_SICAV, 7, ms.get(6));
	}
	
	protected void paintKey(FlexTable tab, String description, int row, MemoryFiles mf) {
		paintKeyDescription(tab, description, row, 0);
		paintKeyFieldActions(tab, row, 1,mf);
	}
	
	protected void paintKeyDescription(FlexTable tab, String description, int row,int col) {
		Label desc = new Label( description );
		tab.setWidget(row, col, desc);
		tab.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalBorderBottom());
	}
	
	
	MemoryFiles mfAux;
	Integer rowAux;
	
	protected void paintKeyFieldActions(FlexTable tab,int row, int col, MemoryFiles mf) {

		HorizontalPanel panel = new HorizontalPanel();
		final AonIcon upload = new AonIcon("upload");
		final AonIcon download = new AonIcon("download");
		final AonIcon delete = new AonIcon("delete");

		mfAux = mf;
		rowAux = row;

		download.getElement().getStyle().setMarginTop(10, Unit.PX);
		download.getElement().getStyle().setMarginRight(10, Unit.PX);
		if(mf.getBool()) {
			download.getElement().getStyle().setCursor(Cursor.POINTER);
			download.addDomHandler(new ClickHandler() {
				MemoryFiles mf = mfAux;
				@Override
				public void onClick(ClickEvent event) {
					// TODO DOWNLOAD ACTION
					String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_memory/"
						+ "?file_id=" + mf.getId()
						+ "&domain_id=" + getAonData().getDomain().getId()
						+ "&name=" + mf.getName();
					Window.open( fileDownloadURL, "_blank",null);//"status=0,toolbar=0,menubar=0,location=0");
				}
			}, ClickEvent.getType());
		} else {
			download.getElement().getStyle().setColor("lightgrey");
		}
		
		
		delete.getElement().getStyle().setMarginTop(10, Unit.PX);
		delete.getElement().getStyle().setMarginRight(10, Unit.PX);
		if(mf.getBool()) {
			delete.getElement().getStyle().setCursor(Cursor.POINTER);
			delete.addDomHandler(new ClickHandler() {
				MemoryFiles mf = mfAux;
				@Override
				public void onClick(ClickEvent event) {
					AonDialog dialog = new AonDialog("Borrar Documento", new Label("Est\u00e1 seguro de eliminar el documento."));

					dialog.confirm(new AonAcceptDialogCallback() {
			
						@Override
						public void onCancel() {
						
						}
			
						@Override
						public void onAccept() {
							getDeposit().getInma().deleteMemoryFile(getAonData(), mf.getId(), new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {}

								@Override
								public void onSuccess(Void result){
									if(mf.getName().equals("Memoria" + getYear())){
										String type = getDeposit().getDeposit().get(D2DepositConstants.DEPOSIT_TYPE);
										D2DepositFooterKey key= type.equals(DepositType.ABREVIADO.getLabel()) 
												? D2DepositFooterKey.PR8080805
												: D2DepositFooterKey.PR8080852;
										onEdit(key.getCode(), "1", false);
										getDeposit().getInma().updateSchemaMemory(getAonData(), false, key.getCode(), getYear(), new AsyncCallback<Void>() {
											@Override
											public void onFailure(Throwable caught) {}
											@Override
											public void onSuccess(Void result) {

											}
										});
									}
									if(mf.getName().equals("Modelo de Autocartera" + getYear())){
										onEdit(D2DepositFooterKey.PR8080809.getCode(), "0", false);
										getDeposit().getInma().updateSchemaMemory(getAonData(), false, D2DepositFooterKey.PR8080809.getCode(), getYear(), new AsyncCallback<Void>() {
											@Override
											public void onFailure(Throwable caught) {}
											@Override
											public void onSuccess(Void result) {}
										});
									}
									if(mf.getName().equals(D2_FILE_GESTION + getYear())){
										onEdit(D2DepositFooterKey.PR8080807.getCode(), "0", false);
										getDeposit().getInma().updateSchemaMemory(getAonData(), false, D2DepositFooterKey.PR8080807.getCode(), getYear(), new AsyncCallback<Void>() {
											@Override
											public void onFailure(Throwable caught) {}
											@Override
											public void onSuccess(Void result) {}
										});
									}
									if(mf.getName().equals(D2_FILE_AUDIT + getYear())){
										onEdit(D2DepositFooterKey.PR8080817.getCode(), "0", false);
										getDeposit().getInma().updateSchemaMemory(getAonData(), false, D2DepositFooterKey.PR8080817.getCode(), getYear(), new AsyncCallback<Void>() {
											@Override
											public void onFailure(Throwable caught) {}
											@Override
											public void onSuccess(Void result) {}
										});
									}
									if(mf.getName().equals(D2_FILE_NO_FINANCIERA + getYear())){
										// TODO NEW CODE!!!!!!!!!
										onEdit(D2DepositFooterKey.PR8080825.getCode(), "0", false);
										getDeposit().getInma().updateSchemaMemory(getAonData(), false, D2DepositFooterKey.PR8080825.getCode(), getYear(), new AsyncCallback<Void>() {
											@Override
											public void onFailure(Throwable caught) {}
											@Override
											public void onSuccess(Void result) {}
										});
									}
									if(mf.getName().equals(D2_FILE_TITULAR_REAL + getYear())){
										// TODO NEW CODE!!!!!!!!! 
										onEdit(D2DepositFooterKey.PR8080827.getCode(), "0", false);
										getDeposit().getInma().updateSchemaMemory(getAonData(), false, D2DepositFooterKey.PR8080827.getCode(), getYear(), new AsyncCallback<Void>() {
											@Override
											public void onFailure(Throwable caught) {}
											@Override
											public void onSuccess(Void result) {}
										});
									}
									if(mf.getName().equals(D2_FILE_CONVOC)){
										onEdit(D2DepositFooterKey.PR8080823.getCode(), "0", false);
										getDeposit().getInma().updateSchemaMemory(getAonData(), false, D2DepositFooterKey.PR8080823.getCode(), getYear(),new AsyncCallback<Void>() {
											@Override
											public void onFailure(Throwable caught) {}
											@Override
											public void onSuccess(Void result) {}
										});
									}
									if(mf.getName().equals(D2_FILE_SICAV + getYear())){
										onEdit(D2DepositFooterKey.PR8080821.getCode(), "0", false);
										getDeposit().getInma().updateSchemaMemory(getAonData(), false, D2DepositFooterKey.PR8080821.getCode(), getYear(), new AsyncCallback<Void>() {
											@Override
											public void onFailure(Throwable caught) {}
											@Override
											public void onSuccess(Void result) {}
										});
									}
									
									getDeposit().refreshPage();
								}
				
							});
						}
					});
				}
			}, ClickEvent.getType());
		} else {
			delete.getElement().getStyle().setColor("lightgrey");
		}

		upload.getElement().getStyle().setMarginLeft(10, Unit.PX);
		upload.getElement().getStyle().setMarginTop(10, Unit.PX);
		upload.getElement().getStyle().setMarginRight(10, Unit.PX);
		upload.getElement().getStyle().setCursor(Cursor.POINTER);
		upload.addDomHandler(new ClickHandler() {
			MemoryFiles mf = mfAux;
			
			@Override
			public void onClick(ClickEvent event) {
				Upload upload = new Upload() {
					
					@Override
					protected void onUpload(String data, String type) {
						getDeposit().getInma().uploadDocument(getAonData(), mf, data, type, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								if(mf.getName().equals(D2_FILE_MEMORY + getYear())){
									String type = getDeposit().getDeposit().get(D2DepositConstants.DEPOSIT_TYPE);
									D2DepositFooterKey key= type.equals(DepositType.ABREVIADO.getLabel()) 
											? D2DepositFooterKey.PR8080805
											: D2DepositFooterKey.PR8080852;
									
									onEdit(key.getCode(), "0", false);	
									getDeposit().getInma().updateSchemaMemory(getAonData(), true, key.getCode(), getYear(), new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {}
										@Override
										public void onSuccess(Void result) {}
									});
								} else if(mf.getName().equals(D2_FILE_AUTOCARTERA_MODEL + getYear())){
									onEdit(D2DepositFooterKey.PR8080809.getCode(), "1", false);	
									getDeposit().getInma().updateSchemaMemory(getAonData(), true, D2DepositFooterKey.PR8080809.getCode(), getYear(), new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {}
										@Override
										public void onSuccess(Void result) {}
									});
								} else if(mf.getName().equals(D2_FILE_GESTION + getYear())){
									onEdit(D2DepositFooterKey.PR8080807.getCode(), "1", false);	
									getDeposit().getInma().updateSchemaMemory(getAonData(), true, D2DepositFooterKey.PR8080807.getCode(), getYear(), new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {}
										@Override
										public void onSuccess(Void result) {}
									});
								} else if(mf.getName().equals(D2_FILE_AUDIT + getYear())){
									onEdit(D2DepositFooterKey.PR8080817.getCode(), "1", false);	
									getDeposit().getInma().updateSchemaMemory(getAonData(), true, D2DepositFooterKey.PR8080817.getCode(), getYear(), new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {}
										@Override
										public void onSuccess(Void result) {}
									});
								} else if(mf.getName().equals(D2_FILE_NO_FINANCIERA + getYear())){
									onEdit(D2DepositFooterKey.PR8080825.getCode(), "1", false);	
									getDeposit().getInma().updateSchemaMemory(getAonData(), true, D2DepositFooterKey.PR8080825.getCode(), getYear(), new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {}
										@Override
										public void onSuccess(Void result) {}
									});
								} else if(mf.getName().equals(D2_FILE_TITULAR_REAL + getYear())){
									onEdit(D2DepositFooterKey.PR8080827.getCode(), "1", false);	
									getDeposit().getInma().updateSchemaMemory(getAonData(), true, D2DepositFooterKey.PR8080827.getCode(), getYear(), new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {}
										@Override
										public void onSuccess(Void result) {}
									});
								} else if(mf.getName().equals(D2_FILE_CONVOC + getYear())){
									onEdit(D2DepositFooterKey.PR8080823.getCode(), "1", false);
									getDeposit().getInma().updateSchemaMemory(getAonData(), true, D2DepositFooterKey.PR8080823.getCode(), getYear(), new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {}
										@Override
										public void onSuccess(Void result) {}
									});
								} else if(mf.getName().equals(D2_FILE_SICAV + getYear())){
									onEdit(D2DepositFooterKey.PR8080821.getCode(), "1", false);		
									getDeposit().getInma().updateSchemaMemory(getAonData(), true, D2DepositFooterKey.PR8080821.getCode(), getYear(), new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {}
										@Override
										public void onSuccess(Void result) {}
									});
								}
								getDeposit().refreshPage();
							}
							
							@Override
							public void onFailure(Throwable caught) {
							
							}
						});
					}
				};
				upload.upload();
			}
			
		}, ClickEvent.getType());
		
		panel.add(upload);
		panel.add(download);
		panel.add(delete);
		tab.setWidget(row, col, panel);
	}
}

package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory;

import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.Deposit;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryFiles;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.DepositType;
import com.google.gwt.core.client.GWT;
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
import com.vaadin.polymer.vaadin.widget.VaadinUpload;

public class MemoryDocuments extends PageAbs {

	private static final String D2_FILE_MEMORY = "Memoria";
	private static final String D2_FILE_AUTOCARTERA_MODEL = "Modelo de Autocartera";
	private static final String D2_FILE_GESTION = "Informe de Gesti\u00f3n";
	private static final String D2_FILE_AUDIT = "Informe de Auditor\u00eda";
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
	
	public MemoryDocuments(Deposit deposit) {
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
		paintKey(tab, D2_FILE_CONVOC, 5, ms.get(4));
		paintKey(tab, D2_FILE_SICAV, 6, ms.get(5));
		
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
		final Button upload = new Button();
		final Button download = new Button();
		final Button delete = new Button();
		final Button view = new Button();
		mfAux = mf;
		rowAux = row;
		
		download.setStyleName("aon-finding-toolbar-item aon-icon-mail-save");
		download.setEnabled(mf.getBool());
		download.addClickHandler(new ClickHandler() {
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
		});
		
		delete.setStyleName("aon-finding-toolbar-item aon-icon-delete");
		delete.setEnabled(mf.getBool());
		delete.addClickHandler(new ClickHandler() {
			MemoryFiles mf = mfAux;
			@Override
			public void onClick(ClickEvent event) {
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
						download.setEnabled(false);
						delete.setEnabled(false);
					}
	
				});
			}
		});
		
		upload.setStyleName("aon-finding-toolbar-item aon-icon-file-upload");
		upload.addClickHandler(new ClickHandler() {
			MemoryFiles mf = mfAux;
			@Override
			public void onClick(ClickEvent event) {
				VaadinUpload up = new VaadinUpload();
				String dataRequest = "?domain_name="+ getAonData().getDomain().getName() 
						+ "&domain_id="+ getAonData().getDomain().getId()
						+ "&login="+ getAonData().getUser().getLogin()
						+ "&name="+ mf.getName()
						+ "&id=" + (mf.getId() != null ? "0" : mf.getId().toString());
 				up.setTarget(GWT.getModuleBaseURL() + "uploadMemoryDocuments" + dataRequest);
				AonDialog dialog = new AonDialog("Importar Archivo", up) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						hide();
						if(mf.getName().equals(D2_FILE_MEMORY + getYear())){
							String type = getDeposit().getDeposit().get(D2DepositConstants.DEPOSIT_TYPE);
							D2DepositFooterKey key= type.equals(DepositType.ABREVIADO.getLabel()) 
									? D2DepositFooterKey.PR8080805
									: D2DepositFooterKey.PR8080852;
							onEdit(key.getCode(), "0", false);		
						} else if(mf.getName().equals(D2_FILE_AUTOCARTERA_MODEL + getYear())){
							onEdit(D2DepositFooterKey.PR8080809.getCode(), "1", false);	
						} else if(mf.getName().equals(D2_FILE_GESTION + getYear())){
							onEdit(D2DepositFooterKey.PR8080807.getCode(), "1", false);	
						} else if(mf.getName().equals(D2_FILE_AUDIT + getYear())){
							onEdit(D2DepositFooterKey.PR8080817.getCode(), "1", false);	
						} else if(mf.getName().equals(D2_FILE_CONVOC + getYear())){
							onEdit(D2DepositFooterKey.PR8080823.getCode(), "1", false);
						} else if(mf.getName().equals(D2_FILE_SICAV + getYear())){
							onEdit(D2DepositFooterKey.PR8080821.getCode(), "1", false);		
						}
						download.setEnabled(true);
						delete.setEnabled(true);
						
						getDeposit().refreshPage();
					}
				};
				dialog.setAutoHideEnabled(true);
				dialog.getElement().getStyle().setWidth(310, Unit.PX);
				dialog.center();
			}			
		});
		
		
		view.setStyleName("aon-finding-toolbar-item aon-icon-audit");
		view.setEnabled(mf.getBool());
		view.addClickHandler(new ClickHandler() {
			MemoryFiles mf = mfAux;
			Integer row = rowAux;
			@Override
			public void onClick(ClickEvent event) {
				
			}
		});
		view.setEnabled(false);
		panel.add(upload);
		panel.add(download);
		panel.add(delete);
		panel.add(view);
		tab.setWidget(row, col, panel);
	}
}

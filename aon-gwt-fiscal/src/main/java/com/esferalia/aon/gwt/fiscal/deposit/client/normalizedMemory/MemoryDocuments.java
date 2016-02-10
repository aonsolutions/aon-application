package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.fiscal.deposit.client.TreeNode;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryFiles;
import com.esferalia.aon.gwt.viewer.client.Viewer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.google.gwt.core.client.GWT;
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

public class MemoryDocuments extends PageAbs {

	private static final String D2_FILE_MEMORY = "Memoria";
	private static final String D2_FILE_AUTOCARTERA_MODEL = "Modelo de Autocartera";
	private static final String D2_FILE_GESTION = "Informe de Gesti\u00f3n";
	private static final String D2_FILE_AUDIT = "Informe de Auditor\u00eda";
	private static final String D2_FILE_CONVOC = "Anuncios de Convocatoria";
	private static final String D2_FILE_SICAV = "Certificaci\u00f3n SICAV";
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);

	@UiField InlineLabel title1;
	
	interface MemoryDocumentsBinder extends UiBinder<Widget, MemoryDocuments> {
	}

	private static final MemoryDocumentsBinder binder = GWT
			.create(MemoryDocumentsBinder.class);



	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	Vector<MemoryFiles> memoryFiles = new Vector<MemoryFiles>();
	TreeNode<Enterprise> memory;
	TreeNode<Enterprise> autocartera;
	Integer year;
	public MemoryDocuments( Enterprise enterprise, NormalizedMemory nm, TreeNode<Enterprise> memory, TreeNode<Enterprise> autocartera, Integer year) {
		
		
		super();
		
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();

		RESOURCES.css().ensureInjected();
		
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		this.memory = memory;
		this.autocartera = autocartera;
		this.year = year;
		
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
		title1.setText("Documentos");

	}

	@Override
	protected void initializeTable() {
		inma.getMemoryFiles(enterprise.getDomain(), new AsyncCallback<Vector<MemoryFiles>>() {
			
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
						+ "&domain_id="+enterprise.getDomain()
						+ "&name="+mf.getName();
				Window.open( fileDownloadURL, "_blank",null);//"status=0,toolbar=0,menubar=0,location=0");

			}
		});
		
		delete.setStyleName("aon-finding-toolbar-item aon-icon-delete");
		delete.setEnabled(mf.getBool());
		delete.addClickHandler(new ClickHandler() {
			MemoryFiles mf = mfAux;
			@Override
			public void onClick(ClickEvent event) {
				inma.deleteMemoryFile(enterprise.getDomain(), mf.getId(), new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {}

					@Override
					public void onSuccess(Void result){
						if(mf.getName().equals("Memoria")){
							normalizedMemory.getDigitalDepositTreeNode().setIsMemory(false);
							normalizedMemory.getDigitalDepositTreeNode().removeItems();
							normalizedMemory.getDigitalDepositTreeNode().items();
							memory = normalizedMemory.getDigitalDepositTreeNode().getMemory();
							onEdit(D2DepositFooterKey.PR8080805.getCode(), "1");
							inma.updateSchemaMemory(false, enterprise.getDomain(), D2DepositFooterKey.PR8080805.getCode(), year, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {}
								@Override
								public void onSuccess(Void result) {
								}
							});
						}
						if(mf.getName().equals("Modelo de Autocartera")){
							normalizedMemory.getDigitalDepositTreeNode().setIsMa(false);
							normalizedMemory.getDigitalDepositTreeNode().removeItems();
							normalizedMemory.getDigitalDepositTreeNode().items();
							autocartera = normalizedMemory.getDigitalDepositTreeNode().getMa();
							onEdit(D2DepositFooterKey.PR8080809.getCode(), "0");
							inma.updateSchemaMemory(false, enterprise.getDomain(), D2DepositFooterKey.PR8080809.getCode(), year, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {}
								@Override
								public void onSuccess(Void result) {}
							});
						}
						if(mf.getName().equals(D2_FILE_GESTION)){
							onEdit(D2DepositFooterKey.PR8080807.getCode(), "0");
							inma.updateSchemaMemory(false, enterprise.getDomain(), D2DepositFooterKey.PR8080807.getCode(), year, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {}
								@Override
								public void onSuccess(Void result) {}
							});
						}
						if(mf.getName().equals(D2_FILE_AUDIT)){
							onEdit(D2DepositFooterKey.PR8080817.getCode(), "0");
							inma.updateSchemaMemory(false, enterprise.getDomain(), D2DepositFooterKey.PR8080817.getCode(), year, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {}
								@Override
								public void onSuccess(Void result) {}
							});
						}
						if(mf.getName().equals(D2_FILE_CONVOC)){
							onEdit(D2DepositFooterKey.PR8080823.getCode(), "0");
							inma.updateSchemaMemory(false, enterprise.getDomain(), D2DepositFooterKey.PR8080823.getCode(), year,new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {}
								@Override
								public void onSuccess(Void result) {}
							});
						}
						if(mf.getName().equals(D2_FILE_SICAV)){
							onEdit(D2DepositFooterKey.PR8080821.getCode(), "0");
							inma.updateSchemaMemory(false, enterprise.getDomain(), D2DepositFooterKey.PR8080821.getCode(), year, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {}
								@Override
								public void onSuccess(Void result) {}
							});
						}
						download.setEnabled(false);
						delete.setEnabled(false);
						//normalizedMemory.update();
					}
	
				});
			}
		});
		
		upload.setStyleName("aon-finding-toolbar-item aon-icon-file-upload");
		upload.addClickHandler(new ClickHandler() {
			MemoryFiles mf = mfAux;
			@Override
			public void onClick(ClickEvent event) {
				// TODO UPLOAD ACTION

				DepositDialog popup = new DepositDialog("Importar Archivo","import", enterprise,GWT.getModuleBaseURL(),null, false, null, year) {
						
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						hide();
						inma.insertMemoryFile(enterprise.getDomain(), mf, new AsyncCallback<MemoryFiles>() {

							@Override
							public void onFailure(Throwable caught) {}

							@Override
							public void onSuccess(MemoryFiles result) {
								mf = result;
								if(memory != null && mf.getName().equals(D2_FILE_MEMORY)){
									memory.remove();
									normalizedMemory.getDigitalDepositTreeNode().setIsMemory(true);
									onEdit(D2DepositFooterKey.PR8080805.getCode(), "0");		
									inma.updateSchemaMemory(true, enterprise.getDomain(),D2DepositFooterKey.PR8080805.getCode() , year, new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {}
										@Override
										public void onSuccess(Void result) {}
									});
								}
								if(autocartera != null && mf.getName().equals(D2_FILE_AUTOCARTERA_MODEL)){
									autocartera.remove();
									normalizedMemory.getDigitalDepositTreeNode().setIsMa(true);
									onEdit(D2DepositFooterKey.PR8080809.getCode(), "1");		
									inma.updateSchemaMemory(true, enterprise.getDomain(),D2DepositFooterKey.PR8080809.getCode() , year, new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {}
										@Override
										public void onSuccess(Void result) {}
									});
								}
								if(mf.getName().equals(D2_FILE_GESTION)){
									onEdit(D2DepositFooterKey.PR8080807.getCode(), "1");		
									inma.updateSchemaMemory(true, enterprise.getDomain(),D2DepositFooterKey.PR8080807.getCode() , year, new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {}
										@Override
										public void onSuccess(Void result) {}
									});
								}
								if(mf.getName().equals(D2_FILE_AUDIT)){
									onEdit(D2DepositFooterKey.PR8080817.getCode(), "1");		
									inma.updateSchemaMemory(true, enterprise.getDomain(),D2DepositFooterKey.PR8080817.getCode() , year, new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {}
										@Override
										public void onSuccess(Void result) {}
									});
								}
								if(mf.getName().equals(D2_FILE_CONVOC)){
									onEdit(D2DepositFooterKey.PR8080823.getCode(), "1");		
									inma.updateSchemaMemory(true, enterprise.getDomain(),D2DepositFooterKey.PR8080823.getCode() , year, new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {}
										@Override
										public void onSuccess(Void result) {}
									});
								}
								if(mf.getName().equals(D2_FILE_CONVOC)){
									onEdit(D2DepositFooterKey.PR8080821.getCode(), "1");		
									inma.updateSchemaMemory(true, enterprise.getDomain(),D2DepositFooterKey.PR8080821.getCode() , year, new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {}
										@Override
										public void onSuccess(Void result) {}
									});
								}
				
								download.setEnabled(true);
								delete.setEnabled(true);
								normalizedMemory.update();
							}
						});
					}
				};
				popup.addStyleName("gwt-PopupPanel-template");
				popup.setGlassEnabled(true);
				popup.show();
			}			
		});
		
		
		view.setStyleName("aon-finding-toolbar-item aon-icon-audit");
		view.setEnabled(mf.getBool());
		view.addClickHandler(new ClickHandler() {
			MemoryFiles mf = mfAux;
			Integer row = rowAux;
			@Override
			public void onClick(ClickEvent event) {
				getViewer(mf, row-1, memoryFiles);
			}
		});
		
		panel.add(upload);
		panel.add(download);
		panel.add(delete);
		panel.add(view);
		tab.setWidget(row, col, panel);
	}
	
	private  void getViewer(MemoryFiles mf, final Integer index, final List<MemoryFiles> viewList) {
		LinkedList<Attach> attachList = new LinkedList<Attach>();
		for (MemoryFiles memoryFiles : viewList) {
			attachList.add(new Attach().setId(memoryFiles.getId())
					.setDescription(mf.getName())
					.setMimeType(MimeType.values()[mf.getMimeTypeNumber()])
					.setDomain(getDomain())
					.setAttachType(AttachType.REGISTRY));
		}
		Viewer.getViewer(attachList.get(index), index, attachList);
	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	private Domain getDomain(){
		return new Domain().setId(getCurrentDomain()).setName(getCurrentDomainName());
	}
}

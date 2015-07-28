package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.fiscal.client.tree.node.TreeNode;
import com.esferalia.aon.gwt.fiscal.shared.MemoryFiles;
import com.esferalia.aon.occam.api.model.Enterprise;
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
	private static final String D2_FILE_GESTION = "Informe de Gestion";
	private static final String D2_FILE_AUDIT = "Informe de Auditoria";
	private static final String D2_FILE_CONVOC = "Anuncios de Convocatoria";
	private static final String D2_FILE_SICAV = "Certificacion SICAV";
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);

	@UiField InlineLabel title1;
	
	interface MemoryDocumentsBinder extends UiBinder<Widget, MemoryDocuments> {
	}

	private static final MemoryDocumentsBinder binder = GWT
			.create(MemoryDocumentsBinder.class);



	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	TreeNode<Enterprise> memory;
	TreeNode<Enterprise> autocartera;
	public MemoryDocuments( Enterprise enterprise, NormalizedMemory nm, TreeNode<Enterprise> memory, TreeNode<Enterprise> autocartera) {
		
		
		super();
		
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();

		RESOURCES.css().ensureInjected();
		
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		this.memory = memory;
		this.autocartera = autocartera;
		
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
		tab.setWidth("100%");
		tab.setCellSpacing(0);
		tab.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().addStyleName(1, AON.AON_CSS.aonWidth130());
		int row = 0;
		tab.setWidget(row, 0, new Label("Descripcion"));
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

	
	protected void paintKeyFieldActions(FlexTable tab,int row, int col, MemoryFiles mf) {

		HorizontalPanel panel = new HorizontalPanel();
		final Button upload = new Button();
		final Button download = new Button();
		final Button delete = new Button();
		final Button view = new Button();
		mfAux = mf;
		
		
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
							Window.alert(normalizedMemory.getDigitalDepositTreeNode().getIsMemory()+"");
							Window.alert(normalizedMemory.getDigitalDepositTreeNode().getIsMa()+"");
							normalizedMemory.getDigitalDepositTreeNode().removeItems();
							normalizedMemory.getDigitalDepositTreeNode().items();
							memory = normalizedMemory.getDigitalDepositTreeNode().getMemory();
						}
						if(mf.getName().equals("Modelo de Autocartera")){
							normalizedMemory.getDigitalDepositTreeNode().setIsMa(false);
							Window.alert(normalizedMemory.getDigitalDepositTreeNode().getIsMemory()+"");
							Window.alert(normalizedMemory.getDigitalDepositTreeNode().getIsMa()+"");
							normalizedMemory.getDigitalDepositTreeNode().removeItems();
							normalizedMemory.getDigitalDepositTreeNode().items();
							autocartera = normalizedMemory.getDigitalDepositTreeNode().getMa();
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

				DepositDialog popup = new DepositDialog("Importar Archivo","import", enterprise,GWT.getModuleBaseURL(),null) {
						
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
								if(memory != null && mf.getName().equals("Memoria")){
									memory.remove();
									normalizedMemory.getDigitalDepositTreeNode().setIsMemory(true);
								}
								if(autocartera != null && mf.getName().equals("Modelo de Autocartera")){
									autocartera.remove();
									normalizedMemory.getDigitalDepositTreeNode().setIsMa(true);
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
		
		
		panel.add(upload);
		panel.add(download);
		panel.add(delete);
		tab.setWidget(row, col, panel);
	}
	
	
}

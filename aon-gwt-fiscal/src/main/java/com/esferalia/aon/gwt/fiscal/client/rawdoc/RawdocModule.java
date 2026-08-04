package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomain;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomainName;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentUser;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getParameter;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getRootPanel;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonChat.CommentHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.RawdocService;
import com.esferalia.aon.gwt.fiscal.client.RawdocServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.RawdocServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.rawdoc.RawdocRightPanel.RawdocRightPanelCallback;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class RawdocModule  implements EntryPoint {
	private static final Logger LOGGER = Logger.getLogger(RawdocModule.class.getName());

	private static final String RAWDOC_STATUS_PARAM = "rawdocStatus";
	private static final RawdocStatus DEFAULT_RAWDOC_STATUS = RawdocStatus.PROCESSED;
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	static final RawdocServiceAsync RAWDOC_SERVICE;
	static {
		RawdocServiceAsync rawdocServiceRaw = GWT.create(RawdocService.class);
		RAWDOC_SERVICE = new RawdocServiceAsyncDecorator(rawdocServiceRaw);
	}
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}
	
	private DockLayoutPanel dockLayoutPanel;
	 
	private SplitLayoutPanel splitLayoutPanel;
	private SimpleLayoutPanel centerLayoutPanel;

	private RawdocRightPanel rightPanel;
	
	private AonToolbar toolbar;

	private LinkedHashSet<Integer> selectedItems = new LinkedHashSet<>();
	private InlineLabel selectedCount;

	private boolean recording;


	class RawdocCallback {
	
		private RawdocRightPanelCallback rightPanelCallback = new RawdocRightPanelCallback() {
			@Override
			public void close() {
				splitLayoutPanel.setWidgetSize(rightPanel, 0);
			}
			
			@Override
			public boolean open() {
				double from = splitLayoutPanel.getWidgetSize(rightPanel) == null? 0 : splitLayoutPanel.getWidgetSize(rightPanel);
				int to = Window.getClientWidth() / 2;
				if (from < to) {
					splitLayoutPanel.setWidgetSize(rightPanel, to);
					return true;
				}
				return false;
			}
		};
		
		void showError(String msg) {
			if (AonStringUtils.isBlank(msg)) {
				msg = "Se ha producido un error no codificado.";
			}
			toolbar.showErrorMessage(msg);
		}
		
		public void showViewer( MimeType mimeType, String url ) {
			rightPanel.showViewer( mimeType, url, rightPanelCallback);
		}
		
		public void showChat(String workflowJson, CommentHandler commentHandler) {
			rightPanel.showChat(workflowJson, commentHandler, rightPanelCallback);
		}

		public void clearRightPanel() {
			rightPanel.clear(rightPanelCallback);
		}

		
		public InlineLabel getSelectedCount() {
			return selectedCount;
		}
		
		public LinkedHashSet<Integer> getSelectedItems() {
			return selectedItems;
		}

		public void manageSelection( Rawdoc rawdoc) {
			if (rawdoc.isSelected()) {
				getSelectedItems().add(rawdoc.getId());
			} else {
				getSelectedItems().remove(rawdoc.getId());
			}
			refreshCounterLabel();
		}
		
		private void refreshCounterLabel() {
			selectedCount.setText( (!selectedItems.isEmpty())?  AonNumberUtils.toString(selectedItems.size()) :"");
		}

		public void resetCounters() {
			selectedItems = new LinkedHashSet<>();
			refreshCounterLabel();
		}
		
	}

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		RawdocModuleOptions options = new RawdocModuleOptions()
			.setParentWidget(root)
			.setDomainName(getCurrentDomainName())
			.setDomain(getCurrentDomain())
			.setUser(getCurrentUser())
			.setParams(new RawdocParams()
				.setDomain(getCurrentDomain())
				.setDomainName(getCurrentDomainName())
				.setStatus(getRawdocStatusParam()))
		;
		this.onModuleLoad( options );
	}

	/**
	 * Estado por el que se filtra al arrancar el m&oacute;dulo. Se recibe como par&aacute;metro
	 * 'rawdocStatus' en la url del nocache (ver GWT.iLoad). Si no viene, se aplica el estado por
	 * defecto; si viene un valor no reconocido (p.e. 'ALL'), no se filtra por estado.
	 */
	private static RawdocStatus getRawdocStatusParam() {
		String status = getParameter(GWT.getModuleName(), RAWDOC_STATUS_PARAM);
		if (AonStringUtils.isBlank(status)) return DEFAULT_RAWDOC_STATUS;
		return RawdocStatus.safeValueOf(status);
	}
	
	public void onModuleLoad( final RawdocModuleOptions opt ) {
		AON.ensureInjected();
		selectedCount = new InlineLabel();
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		opt.getParentWidget().add(dockLayoutPanel);
		if ( opt.getConfiguration() == null) {
			COMMON_SERVICE.getAonConfiguration(opt.getDomainName(),opt.getDomain(),opt.getUser(),new AsyncCallback<AonConfiguration>() {
				@Override
				public void onSuccess(AonConfiguration result) {
					opt.setConfiguration(result);
					loadModule( opt );					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					dockLayoutPanel.add(new Label("[Error: " + caught.getMessage()+ "]"));
				}
			});
		} else {
			loadModule( opt );
		}
	}
	
	private void checkConfiguration(AonConfiguration config) {
		LinkedList<String> messages = new LinkedList<>();
		if ( config.accounting().getDefaultChargedVatAccount() == null ) {
			messages.add( "Cuenta contable de IVA soportado por defecto." );	
		}
		if (config.accounting().getDefaultPaidVatAccount() == null ) {
			messages.add( "Cuenta contable de IVA repercutido por defecto." );	
		}
		if (config.accounting().getDefaultSalesAccount() == null ) {
			messages.add( "Cuenta contable de ventas por defecto." );
		}
		if (config.accounting().getDefaultPurchaseAccount() == null) {
			messages.add( "Cuenta contable de compras por defecto." );
		}
		if (config.accounting().getDefaultPrepayment() == null) {
			messages.add( "Cuenta contable de suplidos por defecto." );
		}
		if (config.accounting().getDefaultChargedRetAccount() == null) {
			messages.add( "Cuenta contable de retenci\u00F3n para facturas recibidas por defecto." );
		}
		if (config.accounting().getDefaultPaidRetAccount() == null) {
			messages.add( "Cuenta contable de retenci\u00F3n para facturas emitidas por defecto." );
		}
		if (AonCollectionUtils.isNotEmpty(messages)) {
			final PopupPanel infoPanel = new PopupPanel( true, true );
			infoPanel.setWidth( "600px");
			infoPanel.setHeight("400px");
			
			FlowPanel configCheck = new FlowPanel();
			configCheck.setStyleName(AON.CSS.aonWidthAll());
			configCheck.addStyleName(AON.CSS.aonPadding());
			
			Label msg = new Label("Defina correctamente los siguientes valores en configuraci\u00F3n de empresa:");
			msg.setStyleName(AON.CSS.aonMargin());
			msg.addStyleName(AON.CSS.aonBold());
			msg.addStyleName(AON.CSS.aonFontMedium());
			msg.addStyleName(AON.CSS.aonColorRed());
			configCheck.add(msg);
			
			AonDisplayGrid errors = new AonDisplayGrid();
			errors.addStyleName(AON.CSS.aonWidthAlmostAll());
			errors.addStyleName(AON.CSS.aonBlockCenter());
			errors.addStyleName(AON.CSS.aonMarginTop());
			AonCollectionUtils.stream(messages)
				.forEach( e -> errors.addRow().addCell(new Label(e), AON.CSS.aonWidthAuto()))
			;
			configCheck.add(errors);
			
			ScrollPanel scroll = new ScrollPanel();
			scroll.setWidget(configCheck);
			infoPanel.add(scroll);
			infoPanel.center();
			infoPanel.show();
		}
	}
	
	private void loadModule( final RawdocModuleOptions opt ) {
		dockLayoutPanel.addNorth(getToolbarPanel( opt ), AonToolbar.HEIGTH );

		splitLayoutPanel = new SplitLayoutPanel();
		dockLayoutPanel.add(splitLayoutPanel);
		
		rightPanel = new RawdocRightPanel( );
		splitLayoutPanel.addEast(rightPanel, 0);

		centerLayoutPanel = new SimpleLayoutPanel();
		splitLayoutPanel.add(centerLayoutPanel);
		search(opt);
		checkConfiguration( opt.getConfiguration() );
	}



	private Widget getToolbarPanel(final RawdocModuleOptions opt) {
		toolbar = new AonToolbar(AON.MSG.rawdocModule());

		AonToolbarButton refreshButton = new AonToolbarButton( AON.MSG.refresh(), AON.CSS.aonIconRefresh() );
		refreshButton.addClickHandler(event -> search( opt ));
		toolbar.add(refreshButton);

		AonToolbarButton allInboxButton = new AonToolbarButton( AON.MSG.all(), AON.CSS.aonIconAllInbox() );
		allInboxButton.addClickHandler(event -> search( opt , null));
		toolbar.add(allInboxButton);

		AonToolbarButton processedButton = new AonToolbarButton( AON.MSG.inbox(), AON.CSS.aonIconInbox() );
		processedButton.addClickHandler(event -> search( opt , RawdocStatus.PROCESSED));
		toolbar.add(processedButton);

		AonToolbarButton inboxButton = new AonToolbarButton( AON.MSG.draft(), AON.CSS.aonIconEditCalendar() );
		inboxButton.addClickHandler(event -> search( opt , RawdocStatus.INBOX));
		toolbar.add(inboxButton);

		AonToolbarButton rejectedButton = new AonToolbarButton( AON.MSG.rejectedDocs(), AON.CSS.aonIconReject() );
		rejectedButton.addClickHandler(event -> search( opt , RawdocStatus.REJECTED));
		toolbar.add(rejectedButton);

		AonToolbarButton draftButton = new AonToolbarButton( AON.MSG.draftDocs(), AON.CSS.aonIconDraft() );
		draftButton.addClickHandler(event -> search( opt , RawdocStatus.TRASH));
		toolbar.add(draftButton);

		AonToolbarButton recordButton = new AonToolbarButton( AON.MSG.record(), AON.CSS.aonIconAddTask() );
		recordButton.addClickHandler(event -> recordDoc( opt ));
		toolbar.add(recordButton);

		FlowPanel queryPanel = new FlowPanel();
		queryPanel.setStyleName( AON.CSS.aonDisplayFlexCenter());
		queryPanel.addStyleName( AON.CSS.aonMarginRight());
		InlineLabel queryLabel = new InlineLabel("Filtro:");
		queryLabel.setStyleName( AON.CSS.aonMarginLeft());
		queryLabel.addStyleName( AON.CSS.aonMarginRight());
		queryLabel.addStyleName( AON.CSS.aonItalic());
		AonTextBox queryBox = new AonTextBox();
		queryBox.addValueChangeHandler( e -> {
			opt.getParams().setQuery( queryBox.getValue() );
			search( opt );
		});
		AonToolbarButton queryDeleteButton = new AonToolbarButton( AON.MSG.deleteAction(), AON.CSS.aonIconCancel() );
		queryDeleteButton.setStyleName(AON.CSS.aonButton());
		queryDeleteButton.addStyleName(AON.CSS.aonTabIcon());
		queryDeleteButton.addStyleName(AON.CSS.aonIconCancel());
		queryDeleteButton.addClickHandler( e -> {
			if (AonStringUtils.isNotBlank(queryBox.getValue())) {
				queryBox.setValue(null , false);
				opt.getParams().setQuery( null );
				search( opt );
			}
		});
		queryPanel.add(queryLabel);
		queryPanel.add(queryBox);
		queryPanel.add(queryDeleteButton);
		toolbar.showFilterPanel( queryPanel );
		return toolbar;
	}
	
	private void recordDoc(RawdocModuleOptions opt) {
		if (recording) return;
		recording = true;
		if ( AonCollectionUtils.isEmpty( selectedItems) ) {
			AonMessageDialog.error( "No se ha seleccionado ning\u00FAn documento" );
			recording = false;
		} else {
			AonConfirmDialog.showConfirm( "PREGUNTA", "\u00BFDesea contabliizar los documentos pendientes seleccionados?"
				, new AonConfirmDialogCallback() {
					@Override
					public void onCancel() {
						recording = false;
					}
					
					@Override
					public void onAccept() {
						final PopupPanel popup = new PopupPanel(false, true);
						popup.add( new AonSplash());
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();
						
						RAWDOC_SERVICE.saveToAccounting( opt.getOccam(), selectedItems, new AsyncCallback<LinkedList<String>>() {
							
							@Override
							public void onSuccess(LinkedList<String> ret) {
								popup.hide();
								if ( AonCollectionUtils.isNotEmpty(ret) ) {
									final PopupPanel infoPanel = new PopupPanel( true, true );
									infoPanel.setWidth( "600px");
									infoPanel.setHeight("400px");
									FlowPanel configCheck = new FlowPanel();
									configCheck.setStyleName(AON.CSS.aonWidthAll());
									configCheck.addStyleName(AON.CSS.aonPadding());
									
									Label msg = new Label("Resultado de la contabilizaci\u00F3n masiva:");
									msg.setStyleName(AON.CSS.aonMargin());
									msg.addStyleName(AON.CSS.aonBold());
									msg.addStyleName(AON.CSS.aonFontMedium());
									msg.addStyleName(AON.CSS.aonColorRed());
									configCheck.add(msg);
									
									AonDisplayGrid errors = new AonDisplayGrid();
									errors.addStyleName(AON.CSS.aonWidthAlmostAll());
									errors.addStyleName(AON.CSS.aonBlockCenter());
									errors.addStyleName(AON.CSS.aonMarginTop());
									AonCollectionUtils.stream(ret)
									.forEach( e -> errors.addRow().addCell(new Label(e), AON.CSS.aonWidthAuto()))
									;
									configCheck.add(errors);
									
									infoPanel.add(configCheck);
									infoPanel.center();
									infoPanel.show();
								} else {
									AonMessageDialog.info("Proceso terminado correctamente");
								}
								recording = false;
								search( opt );
							}
							
							@Override
							public void onFailure(Throwable t) {
								toolbar.showErrorMessage(t.getMessage());
								popup.hide();
								recording = false;
							}
						}
					);
				}
					
			});
		}
	}

	private void search(RawdocModuleOptions opt, RawdocStatus status) {
		splitLayoutPanel.setWidgetSize(rightPanel, 20);
		opt.setParams(
			new RawdocParams()
				.setDomain(opt.getDomain())
				.setDomainName(opt.getDomainName())
				.setStatus(status)
		);
		search(opt);
	}

	protected void search(final RawdocModuleOptions opt) {
		centerLayoutPanel.clear();
		RawdocTable table = new RawdocTable(opt, new RawdocCallback());
		centerLayoutPanel.setWidget( table );
	}
	

	// ------------------------------- [LAUNCHER]		
	public static void run() {
		GWT.runAsync(RawdocModule.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("RawdocModuleNew"));
			}
			
			@Override
			public void onSuccess() {
				RawdocModule rawdocModule= new RawdocModule();
				rawdocModule.onModuleLoad();
			}
		});
	}
	
}		

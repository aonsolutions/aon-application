package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.tedi.TediCenter.TediCenterCallback;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class TediUploaderPanel extends DockLayoutPanel {

	private static final Logger LOGGER = Logger.getLogger(TediUploaderPanel.class.getName()); 
	private static TediServiceAsync SERVICE;

	private String domainName;
	private int domain;
	private String user;
	private AonConfiguration configuration;
	private TediCenterCallback callback;
	
	private SimpleLayoutPanel content = new SimpleLayoutPanel();

	private DeckLayoutPanel southContent = new DeckLayoutPanel();
	private FileUpload fileUpload;
	private FullViewer viewer = new FullViewer();
	private SimpleLayoutPanel imageContainer = new SimpleLayoutPanel();
	
	
	public TediUploaderPanel(String currentDomainName, int currentDomain, String currentUser,
			TediCenterCallback callback) {
		super(Unit.PX);
		
		TediServiceAsync serviceRaw = GWT.create(TediService.class);
		SERVICE = new TediServiceAsyncDecorator(serviceRaw);
		
		this.domainName = currentDomainName;
		this.domain = currentDomain;
		this.user = currentUser;
		this.callback = callback;

		if (callback == null) {
			MessageDialog.error("No TEDI CENTER detected");
			throw new IllegalArgumentException("No TEDI CENTER detected");
		}
		if (canUse()) {
			SERVICE.getAonConfiguration(this.domainName, this.user, this.domain,
					new AsyncCallback<AonConfiguration>() {
						@Override
						public void onSuccess(AonConfiguration result) {
							configuration = result;
							paintWidget();
						}

						@Override
						public void onFailure(Throwable caught) {
							MessageDialog.error(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
						}
					});
		}
	}

	private Widget getToolbarPanel() {
		FlowPanel toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label("Carga de facturas"));
		toolbar.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFindingToolbar());

		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}

	private boolean isTediSnapshot() {
		return callback.isSnapshot();
	}

	private void paintWidget() {
		this.addNorth(getToolbarPanel(), 25);
		
		fileUpload = new FileUpload();
		fileUpload.ensureDebugId("fileselect");
		// fileUpload.getElement().getStyle().setDisplay(Style.Display.NONE);
		fileUpload.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				
				fileSelectHandler(fileUpload.getElement() ,event.getNativeEvent());
			}
		});
		
		
		SimpleLayoutPanel westPanel = new SimpleLayoutPanel();
		this.addWest(westPanel, 200);
		
		FlowPanel filedrag = new FlowPanel();
		filedrag.add(fileUpload);
		Label dropZone = new Label( "Arrastre aqu\u00ED el archivo o click para seleccionar" );
		dropZone.setStyleName(AON.AON_CSS.aonDropZone());
		dropZone.getElement().getStyle().setCursor(Style.Cursor.POINTER);
		filedrag.add(dropZone);
		westPanel.add(filedrag);
		
		SplitLayoutPanel splitPanel = new SplitLayoutPanel();
		this.add( splitPanel );

		
		viewer = new FullViewer();		
		southContent.add(viewer);
		southContent.add(imageContainer);
		clearPage();
		splitPanel.addSouth(southContent,300);
		splitPanel.add( content );
	}

	private void clearPage() {
		imageContainer.clear();
		southContent.showWidget(1);
	}

	private void setDocument(String doc, String type, String name) {
		clearPage();
		MimeType mimeType = MimeType.safeValueFromContenType(type);
		if (mimeType == null) {
			mimeType = MimeType.guessFromFileName(name);	
		}
		if (mimeType != null && (mimeType.isPDF() || mimeType.isImage())) {
			if (mimeType.isPDF()) {
				southContent.showWidget(0);
				viewer.open(doc);
			} if (mimeType.isImage()) {
				southContent.showWidget(1);
				Image image = new Image( doc );
				imageContainer.setWidget(image);
			}
			SERVICE.parseInvoice(domainName, user, domain, callback.isSnapshot(), name, doc, new AsyncCallback<TediResult>() {
				
				@Override
				public void onSuccess(TediResult result) {
					content.clear();
					paintAccountEntryModule(content, result);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
				}
			});		
		}
	}

	private void paintAccountEntryModule(SimplePanel container, TediResult result) {
		AccountEntryModule module = new AccountEntryModule();
		AccountingInvoice ai = result.getAccountingInvoice();
		module.onModuleLoad(new AccountEntryModuleOptions().setParentWidget(container)
				.setDomainName(domainName).setUser(this.user).setDomain(this.domain)
				.setConfiguration(this.configuration).setAccountingInvoice(ai).setBackButtonVisible(false)
				.setSessionLogTabVisible(false).setPreviewSectionVisible(true).setBalancesSectionVisible(false)
				.setStatementTabVisible(false).setJournalTabVisible(false).setExtraInfoTabVisible(false)
				.setExternalCallback(new ModuleCallback() {

					@Override
					public void onRemove(IAccountEntryWrapper removed) {
					}

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onExit() {
					}

					@Override
					public void onChange(IAccountEntryWrapper changed) {
						result.setAon((AccountingInvoice) changed);
						// TODO clear ALL
					}
				}));
	}
	

	private static native boolean canUse() /*-{
		return (window.File && window.FileList && window.FileReader);
	}-*/;
	
	private native void fileSelectHandler(Element fileselect, NativeEvent event) /*-{
		var self = this;		
		event.preventDefault();
		var file = fileselect.files[0];
		var reader = new FileReader();
		reader.addEventListener("load", function () {
			self.@com.esferalia.aon.gwt.fiscal.client.tedi.TediUploaderPanel::setDocument(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(reader.result,file.name,file.type); 
			}, false);
		reader.readAsDataURL( file );
	}-*/;

}

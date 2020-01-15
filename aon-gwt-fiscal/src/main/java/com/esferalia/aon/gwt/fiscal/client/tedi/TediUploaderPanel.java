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
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.tedi.ICallback;
import com.esferalia.aon.occam.api.model.tedi.TediError;
import com.esferalia.aon.occam.api.model.tedi.TediLevel;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.DataTransfer.DropEffect;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
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
	
	private SimpleLayoutPanel viewerContainer = new SimpleLayoutPanel();
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
		fileUpload.getElement().getStyle().setDisplay(Style.Display.NONE);
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
		dropZone.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				fileUpload.click();		
			}
		});
		
		dropZone.addDragOverHandler(new DragOverHandler() {
			
			@Override
			public void onDragOver(DragOverEvent event) {
				dropZone.addStyleName(AON.AON_CSS.aonDropZoneHover());
				event.stopPropagation();
				event.preventDefault();
		        DataTransfer dataTransfer = event.getDataTransfer();
		        dataTransfer.setDropEffect(DropEffect.COPY);				
			}
		});
		dropZone.addDragLeaveHandler(new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				dropZone.removeStyleName(AON.AON_CSS.aonDropZoneHover());
				event.preventDefault();
			}
		});
		dropZone.addDropHandler(new DropHandler() {
			
			@Override
			public void onDrop(DropEvent event) {
				dropZone.removeStyleName(AON.AON_CSS.aonDropZoneHover());
				LOGGER.info("File Droped!");
				event.stopPropagation();
				event.preventDefault();
				fileDrop(fileUpload.getElement() ,event.getNativeEvent());
			}
		});
		
		filedrag.add(dropZone);
		westPanel.add(filedrag);
		
		SplitLayoutPanel splitPanel = new SplitLayoutPanel();
		this.add( splitPanel );

		
		southContent.add(viewerContainer);
		southContent.add(imageContainer);
		clearPage();
		
		splitPanel.addSouth(southContent,300);
		splitPanel.add( content );
	}

	private void clearPage() {
		viewerContainer.clear();
		imageContainer.clear();
		southContent.showWidget(1);
	}

	private void setDocument(final String doc, final String name, String type) {
		clearPage();
		MimeType mimeType = MimeType.safeValueFromContenType(type);
		if (mimeType == null) {
			mimeType = MimeType.guessFromFileName(name);	
		}
		LOGGER.info("MimeType ..: " + (mimeType==null?"NULL":mimeType.getName()));
		if (mimeType != null && (mimeType.isPDF() || mimeType.isImage())) {
			if (mimeType.isPDF()) {
				southContent.showWidget(0);
				FullViewer viewer = new FullViewer();
				viewer.addLoadHandler( new LoadHandler() {
					
					@Override
					public void onLoad(LoadEvent event) {
						viewer.open(doc);
					}
				});
				viewerContainer.setWidget(viewer);
			} if (mimeType.isImage()) {
				southContent.showWidget(1);
				ScrollPanel scrollpanel = new ScrollPanel();
				scrollpanel.setStyleName(AON.AON_CSS.aonScrollArea());
				scrollpanel.addStyleName(AON.AON_CSS.aonTextCenter());
				Image image = new Image( doc );
				scrollpanel.setWidget(image);
				imageContainer.setWidget(scrollpanel);
			}
			
			content.setWidget(getSplashWidget());
		
			final MimeType attachMimeType = mimeType;
			SERVICE.parseInvoice(domainName, user, domain, callback.isSnapshot(), name, doc, new AsyncCallback<TediResult>() {
				
				@Override
				public void onSuccess(TediResult result) {
					AccountingInvoice ai = result.getAccountingInvoice();
					Attach attach = new Attach();
					attach.setAttachType(AttachType.INVOICE);
					attach.setMimeType(attachMimeType);
					attach.setData(doc.getBytes());
					attach.setDescription(name);
					ai.setAttach(attach);
					content.clear();
					if (result.isImportable()) {
						paintAccountEntryModule(content, result);
					} else {
						paintProblemsWidget(content, result);
					}
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
				}
			});		
		}
	}

	private Widget getSplashWidget() {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setStyleName(AON.AON_CSS.aonBlockCenter());
		hp.addStyleName(AON.AON_CSS.aonSimpleBorder() );
		hp.addStyleName(AON.AON_CSS.aonMarginTop() );
		Label iconWaitLabel = new Label();
		iconWaitLabel.setStyleName(AON.AON_CSS.aonLoader());
		iconWaitLabel.addStyleName(AON.AON_CSS.aonMargin());
		hp.add(iconWaitLabel);
		Label textWaitLabel = new Label("Procesando el reconocimiento del archivo. Conectando con tEDI Center. Un  momento, por favor.....");
		textWaitLabel.setStyleName(AON.AON_CSS.aonMargin());
		textWaitLabel.addStyleName(AON.AON_CSS.aonBold());
		hp.add(textWaitLabel);
		return hp;
	}

	private void paintAccountEntryModule(SimplePanel container, TediResult result) {
		AccountEntryModule module = new AccountEntryModule();
		AccountingInvoice ai = result.getAccountingInvoice();
		module.onModuleLoad(new AccountEntryModuleOptions()
				.setParentWidget(container)
				.setDomainName(domainName)
				.setUser(this.user)
				.setDomain(this.domain)
				.setConfiguration(this.configuration)
				.setAccountingInvoice(ai).setBackButtonVisible(false)
				.setSessionLogTabVisible(false)
				.setPreviewSectionVisible(true)
				.setBalancesSectionVisible(false)
				.setStatementTabVisible(false)
				.setJournalTabVisible(false)
				.setExtraInfoTabVisible(false)
				.setEmbedded(true)
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

	private native void fileDrop(Element fileselect, NativeEvent event) /*-{
		fileselect.files = e.target.files || e.dataTransfer.files;
		if (e.dataTransfer.items) {
    		e.dataTransfer.items.clear();
  		} else {
    		e.dataTransfer.clearData();
  		}		
		fileselect.click();
	}-*/;	



	private void paintProblemsWidget(SimpleLayoutPanel contentPanel, TediResult result) {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		FlowPanel mainPanel = new FlowPanel();
		scrollPanel.setWidget(mainPanel);
		if (result.getMessages() != null && result.getMessages().size() > 0) {
			mainPanel.setStyleName(AON.AON_CSS.aonMarginTop5());
			mainPanel.addStyleName(AON.AON_CSS.aonMarginLeft());
			mainPanel.addStyleName(AON.AON_CSS.aonSimpleBorder());
			mainPanel.addStyleName(AON.AON_CSS.aonFixedFont());
			for (TediError error : result.getMessages()) {
				FlowPanel flowPanel = new FlowPanel();
				InlineLabel colorLabel = new InlineLabel("");
				colorLabel.setStyleName(AON.AON_CSS.aonPaddingLeft());
				colorLabel.addStyleName(AON.AON_CSS.aonPaddingRight());
				colorLabel.getElement().getStyle().setBackgroundColor(getBackgroundColor(error.getLevel()));
				flowPanel.add(colorLabel);

				InlineLabel errLabel = new InlineLabel(error.getLevel().getLabel());
				errLabel.setStyleName(AON.AON_CSS.aonClickableLabel());
				errLabel.addStyleName(AON.AON_CSS.aonPaddingLeft());
				errLabel.addStyleName(AON.AON_CSS.aonPaddingRight());
				errLabel.addStyleName(AON.AON_CSS.aonBold());
				flowPanel.add(errLabel);

				InlineLabel msgLabel = new InlineLabel(error.getMessage());
				msgLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
				msgLabel.addStyleName(AON.AON_CSS.aonBorderBottomImportant());
				flowPanel.add(msgLabel);

				if (error.canBeFixed()) {
					SimplePanel container = new SimplePanel();
					container.setStyleName(AON.AON_CSS.aonMarginTop());
					container.addStyleName(AON.AON_CSS.aonMarginBottom());
					flowPanel.add(container);
					TediContextVisitor tediContextVisitor = new TediContextVisitor(this.domainName,
							this.domain, this.configuration, container);
					error.getContext().getKey().visit(tediContextVisitor, new ICallback() {

						@Override
						public TediResult getResult() {
							return result;
						}

						@Override
						public AonConfiguration getConfiguration() {
							return configuration;
						}

						@Override
						public void onCancel() {

						}

						@Override
						public void onAccept(TediResult result) {
							SERVICE.validateInvoice(TediUploaderPanel.this.domainName,
									TediUploaderPanel.this.user, TediUploaderPanel.this.domain,
									isTediSnapshot(), result, new AsyncCallback<TediResult>() {

										@Override
										public void onSuccess(TediResult result) {
											content.clear();
											paintAccountEntryModule(content, result);
										}

										@Override
										public void onFailure(Throwable caught) {
											MessageDialog
													.error(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
										}
									});
						}
					});
				}
				mainPanel.add(flowPanel);
			}
		}
		contentPanel.setWidget(scrollPanel);
	}
	
	private String getBackgroundColor(TediLevel curLevel) {
		String color = null;
		if (curLevel == null) {
			color = "#e6ffe6";
		} else if (curLevel == TediLevel.INF) {
			color = "#e7f5fe";
		} else if (curLevel == TediLevel.WRN) {
			color = "#ffbf80";
		} else if (curLevel == TediLevel.ERR) {
			color = "#ffc2b3";
		}
		return color;
	}

}
 



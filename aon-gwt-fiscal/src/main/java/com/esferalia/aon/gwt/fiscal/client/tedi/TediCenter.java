package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.tedi.TediError;
import com.esferalia.aon.occam.api.model.tedi.TediLevel;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

import es.translogia.tedi.ewok.TediInvoiceStatus;
import net.aonsolutions.gwt.pdfjs.client.Viewer;

public class TediCenter extends MainEntryPoint {

	private static class TediResultProvidesKey implements ProvidesKey<TediResult> {
		@Override
		public Object getKey(TediResult result) {
			return (result != null && result.getTedi() != null) ? result.getTedi().getUuid() : null;
		}
	}

	private static TediServiceAsync SERVICE;

	private String currentDomainName;
	private int currentDomain;
	private String currentUser;

	private AonConfiguration configuration;
	private DockLayoutPanel dockLayoutPanel;

	private SplitLayoutPanel mainSplitLayoutPanel;
	private SimpleLayoutPanel sidebarContent;

	private TabLayoutPanel tabContent;

	private FlowPanel toolbarPanel;
	private Button acceptAll;

	private Viewer pdfViewer = new Viewer();
	private LinkedList<TediResult> resultList;
	private TediInvoiceTable table = new TediInvoiceTable(new TediResultProvidesKey());
//	private PopupPanel popup;
	
	interface SafeTemplate extends SafeHtmlTemplates {
		@Template("<span class=\"gwt-InlineLabel .aon-padding-right aon-padding-left-20 {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}

	private static final SafeTemplate template = GWT.create(SafeTemplate.class);

	public TediCenter(String domainName, int domain, String user) {
		currentDomainName = domainName;
		currentDomain = domain;
		currentUser = user;

	}

	private int getDomain() {
		return currentDomain;
	}

	private String getUser() {
		return currentUser;
	}

	private String getDomainName() {
		return currentDomainName;
	}

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		TediServiceAsync serviceRaw = GWT.create(TediService.class);
		SERVICE = new TediServiceAsyncDecorator(serviceRaw);

		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel.addNorth(getToolbarPanel(), 25);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(dockLayoutPanel);

		SERVICE.getAonConfiguration(getDomainName(), getUser(), getDomain(), new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration result) {
				configuration = result;
				mainSplitLayoutPanel = new SplitLayoutPanel();

				sidebarContent = new SimpleLayoutPanel();
				mainSplitLayoutPanel.addNorth(sidebarContent, 150);
				tabContent = new TabLayoutPanel(26, Unit.PX);

				SimpleLayoutPanel tabContentcontainer = new SimpleLayoutPanel();
				tabContentcontainer.setStyleName(AON.AON_CSS.aonSelector());
				tabContentcontainer.add(tabContent);
				mainSplitLayoutPanel.add(tabContentcontainer);

				dockLayoutPanel.add(mainSplitLayoutPanel);

				ScrollPanel tableScrollPanel = new ScrollPanel();
				tableScrollPanel.setStyleName(AON.AON_CSS.aonScrollArea());
				sidebarContent.add(tableScrollPanel);
				tableScrollPanel.add(table);
				
				table.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						paintInvoice(table.getSelected());
					}
				});
				table.addRangeChangeHandler( new RangeChangeEvent.Handler() {
					
					@Override
					public void onRangeChange(RangeChangeEvent event) {
//						boolean cp = false;
//						if (popup == null) {
//							popup = showPopupPanel();
//							cp = true;
//						}
//						final boolean closePopup = cp;
						SERVICE.getVerifiedInvoices(getDomainName(), getUser(), getDomain(),
								new AsyncCallback<LinkedList<TediResult>>() {

									@Override
									public void onSuccess(LinkedList<TediResult> results) {
										resultList = results;
										if (resultList == null) {
											resultList  = new LinkedList<TediResult>();
										}
										table.setRowData(resultList);
										if (resultList.size() > 0 ) {
											paintInvoice(resultList.get(0));
										}
//										if (closePopup && popup != null) {
//											popup.hide();
//											popup = null;
//										}
									}

									@Override
									public void onFailure(Throwable caught) {
//										if (closePopup && popup != null) {
//											popup.hide();
//											popup = null;
//										}
										showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
									}

								});
					}
				});
				table.setVisibleRangeAndClearData(table.getVisibleRange(), true);				
			}

			@Override
			public void onFailure(Throwable caught) {
				showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
			}
		});

	}

	protected void showMessage(String msg) {
		MessageDialog.error(msg);
	}

	private Widget getToolbarPanel() {
		toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label("tEDI center"));
		toolbar.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFindingToolbar());

		final Button clean = new Button();
		clean.setText(AON.MSG.refresh());
		clean.setTitle(AON.MSG.refresh());
		clean.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		clean.addStyleName(AON.AON_CSS.aonIconRefresh());
		clean.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onRefresh( true );
			}
		});
		buttonContainer.add(clean);

		acceptAll = new Button();
		acceptAll.setVisible(false);
		acceptAll.setText(AON.MSG.accept());
		acceptAll.setTitle(AON.MSG.accept());
		acceptAll.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		acceptAll.addStyleName(AON.AON_CSS.aonIconRefresh());
		acceptAll.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onAcceptAll();
			}
		});
		buttonContainer.add(acceptAll);

		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}

//	private PopupPanel showPopupPanel() {
//		popup = new PopupPanel(false, true);
//		Label label = new Label(AON.MSG.processing());
//		label.addStyleName(AON.AON_CSS.aonTediTimer());
//		popup.add(label);
//		popup.setGlassEnabled(true);
//		popup.setAnimationEnabled(true);
//		popup.center();
//		return popup;
//	}

	private void onRefresh(boolean popup ) {
//		if (popup) {
//			showPopupPanel();
//		}
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}
	
	protected void onAcceptAll() {
		LinkedList<TediResult> accepted = new LinkedList<TediResult>();
		for (TediResult result : resultList) {
			if (result.isChecked()) {
				accepted.add(result);
			}
		}
		onAcceptAll(accepted);
	}

	private void onAcceptAll(LinkedList<TediResult> accepted) {
//		showPopupPanel();
		SERVICE.putInvoices(getDomainName(), getUser(), getDomain(), accepted,
				new AsyncCallback<LinkedList<TediResult>>() {

					@Override
					public void onSuccess(LinkedList<TediResult> i) {
						onRefresh(false);
//						if (popup != null) {
//							popup.hide();
//							popup = null;
//						}
					}

					@Override
					public void onFailure(Throwable caught) {
//						if (popup != null){
//							popup.hide();
//							popup = null;
//						}
						showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
					}

				});
	}

	protected void onAccept(TediResult result) {
//		showPopupPanel();
		result.getTedi().setOldStatus(result.getTedi().getStatus());
		result.getTedi().setStatus(TediInvoiceStatus.accepted);
		SERVICE.putInvoice(getDomainName(), getUser(), getDomain(), result.getTedi(), new AsyncCallback<TediResult>() {

			@Override
			public void onSuccess(TediResult results) {
				onRefresh( false );
//				if (popup != null) {
//					popup.hide();
//					popup = null;
//				}
			}

			@Override
			public void onFailure(Throwable caught) {
//				if (popup != null) {
//					popup.hide();
//					popup = null;
//				}
				showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
			}

		});
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

	private void paintInvoice(TediResult result) {
		tabContent.clear();
		if (result.isImportable()) {
			paintAccountEntryModule( result );
		}
		
		if (!result.isImportable()) {
			if (result.getInvoice().getRegistry() == null 
			&& (result.getInvoice().getDetails() == null || result.getInvoice().getDetails().size() == 0)) {
				paintAccountEntryModule( result );
			}
			if (result.getInvoice().getRegistry() != null) {
				// TODO !!!! Aquí puede pasar cualquier cosa!!
				paintAccountEntryModule( result );
			}
			SimpleLayoutPanel problemsPanel = new SimpleLayoutPanel();
			paintProblems(result , problemsPanel);
			tabContent.add(problemsPanel, template.tab(AON.MSG.notifications(), AON.AON_CSS.aonIconError()));
		}
		
		SimpleLayoutPanel InvoiceViewerContainerPanel = new SimpleLayoutPanel();
		paintInvoiceViewer(InvoiceViewerContainerPanel, result );
		tabContent.add(InvoiceViewerContainerPanel, template.tab(AON.MSG.invoice(), AON.AON_CSS.aonIconInvoice()));
		
	};
	
	
	private void paintAccountEntryModule(TediResult result) {
		SimpleLayoutPanel accountEntryModuleContainer = new SimpleLayoutPanel();
		if (result.hasPDFAttach() || result.hasJPEGAttach()) {
			SplitLayoutPanel entrySplit = new SplitLayoutPanel();
			SimpleLayoutPanel attachContainer = new SimpleLayoutPanel();
			paintAttach( result , attachContainer);
			entrySplit.addEast(attachContainer, 450);
			entrySplit.add(accountEntryModuleContainer);
			tabContent.add(entrySplit, template.tab(AON.MSG.preview(), AON.AON_CSS.aonIconAttach()));
		} else {
			tabContent.add(accountEntryModuleContainer, template.tab(AON.MSG.accountEntry(), AON.AON_CSS.aonIconInvoice()));		
		}
		AccountEntryModule module = new AccountEntryModule();
		AccountingInvoice ai = result.getAccountingInvoice();
		module.onModuleLoad(accountEntryModuleContainer, getCurrentDomainName(), getCurrentUser(), getCurrentDomain(), configuration, ai,
				new ModuleCallback<AccountEntry>() {

					@Override
					public void onRemove(AccountEntry removed) {
					}

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onExit() {
					}

					@Override
					public void onChange(AccountEntry changed) {
						result.setAon(null);
						LinkedList<TediResult> accepted = new LinkedList<TediResult>();
						accepted.add(result);
						onAcceptAll(accepted);
						onRefresh( true );
					}
				});
	}
	
	private void paintInvoiceViewer(Panel contentSplitLayoutPanel, TediResult result) {
		TediInvoiceViewer tediInvoiceViewer = new TediInvoiceViewer(result.getInvoice());
		ScrollPanel invoicePanel = new ScrollPanel();
		invoicePanel.setStyleName(AON.AON_CSS.aonScrollArea());
		invoicePanel.setWidget(tediInvoiceViewer);
		contentSplitLayoutPanel.add(tediInvoiceViewer);
	}

	private void paintAttach(TediResult result, SimpleLayoutPanel container) {
		pdfViewer.clear();
		if (result.hasAttach()) {
			if (result.hasPDFAttach()) {
				ScrollPanel scrollPanel = new ScrollPanel();
				container.setWidget(scrollPanel);
				VerticalPanel verticalPanel = new VerticalPanel();
				verticalPanel.add(pdfViewer);
				scrollPanel.setWidget(verticalPanel);
//				tabContent.add(container, template.tab(AON.MSG.preview(), AON.AON_CSS.aonIconAttach()));
				
				SERVICE.getInvoiceAttachURL(getCurrentDomainName(), getUser(), getCurrentDomain(),
						result.getTedi().getUuid(), new AsyncCallback<String>() {
					
					@Override
					public void onSuccess(String result) {
						pdfViewer.setDocument(result, 1.0);
					}
					
					@Override
					public void onFailure(Throwable caught) {
					}
				});
			}
			if (result.hasJPEGAttach()) {
				Image image = new Image();
				ScrollPanel scrollPanel = new ScrollPanel();
				container.setWidget(scrollPanel);
				scrollPanel.setWidget(image);
				SERVICE.getInvoiceAttachURL(getCurrentDomainName(), getUser(), getCurrentDomain(),
						result.getTedi().getUuid(), new AsyncCallback<String>() {
					
					@Override
					public void onSuccess(String result) {
						image.setUrl(result);
					}
					
					@Override
					public void onFailure(Throwable caught) {
					}
				});
			}
		}
	}

	private void paintProblems(TediResult result, Panel problemsContainer) {
		if (result.getMessages() != null && result.getMessages().size() > 0) {
			ScrollPanel scrollPanel = new ScrollPanel();
			scrollPanel.setStyleName(AON.AON_CSS.aonScrollArea());
			
			FlowPanel panel = new FlowPanel();
			panel.setStyleName(AON.AON_CSS.aonMarginTop5());
			panel.addStyleName(AON.AON_CSS.aonMarginLeft());
			panel.addStyleName(AON.AON_CSS.aonSimpleBorder());
			panel.addStyleName(AON.AON_CSS.aonFixedFont());
			for (TediError error : result.getMessages()) {
				FocusPanel focuspanel = new FocusPanel();
				focuspanel.addStyleName(AON.AON_CSS.aonClickableLabel());
				FlowPanel flowPanel = new FlowPanel();
				focuspanel.setWidget(flowPanel);

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
				flowPanel.add(msgLabel);
				panel.add(focuspanel);
			}
			scrollPanel.setWidget(panel);
			problemsContainer.add(scrollPanel);
		}
	}

}

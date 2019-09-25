package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.CloseTab;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.tedi.ICallback;
import com.esferalia.aon.occam.api.model.tedi.TediError;
import com.esferalia.aon.occam.api.model.tedi.TediLevel;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
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
	private TediContextVisitor tediContextVisitor;
	
	private String currentDomainName;
	private int currentDomain;
	private String currentUser;
	private HashMap<String,Widget> tabs = new HashMap<String,Widget>();

	private AonConfiguration configuration;

	// private SplitLayoutPanel mainSplitLayoutPanel;
	private TabLayoutPanel mainTabLayoutPanel;

	private FlowPanel toolbarPanel;
	private Button acceptAll;
	private	Viewer pdfViewer = new Viewer();	
	
	private LinkedList<TediResult> resultList;
	private TediInvoiceTable table = new TediInvoiceTable(new TediResultProvidesKey());
	
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

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		
		SimpleLayoutPanel mainPanel = new SimpleLayoutPanel();
		mainPanel.setStyleName(AON.AON_CSS.aonSelector());
		mainTabLayoutPanel = new TabLayoutPanel(30,Unit.PX);
		mainPanel.add(mainTabLayoutPanel);
		root.add(mainPanel);

		SERVICE.getAonConfiguration(getDomainName(), getUser(), getDomain(), new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration result) {
				configuration = result;
				tediContextVisitor = new TediContextVisitor(getDomainName(), getDomain(), configuration);
				
				DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
				dockLayoutPanel.addStyleName(AON.AON_CSS.aonMarginTop());
				dockLayoutPanel.addNorth(getToolbarPanel(), 25);
				SimpleLayoutPanel sidebarContent = new SimpleLayoutPanel();
				dockLayoutPanel.add(sidebarContent);
				mainTabLayoutPanel.add(dockLayoutPanel,new CloseTab(AON.MSG.invoiceList(), false));
				ScrollPanel tableScrollPanel = new ScrollPanel();
				tableScrollPanel.setStyleName(AON.AON_CSS.aonScrollArea());
				sidebarContent.add(tableScrollPanel);
				tableScrollPanel.add(table);
				
				table.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						TediResult inv = table.getSelected();
						onSelect( inv );
					}
				});
				table.addRangeChangeHandler( new RangeChangeEvent.Handler() {
					
					@Override
					public void onRangeChange(RangeChangeEvent event) {
						SERVICE.getVerifiedInvoices(getDomainName(), getUser(), getDomain(),
								new AsyncCallback<LinkedList<TediResult>>() {

									@Override
									public void onSuccess(LinkedList<TediResult> results) {
										resultList = results;
										if (resultList == null) {
											resultList  = new LinkedList<TediResult>();
										}
										table.setRowData(resultList);
									}

									@Override
									public void onFailure(Throwable caught) {
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
	private void onSelect(TediResult result) {
		onSelect(result,false);
	}
	private void onSelect(TediResult result, boolean forceRefresh) {
		SplitLayoutPanel tediSplitPanel;
		boolean paint = false;
		if (tabs.containsKey(result.getTedi().getUuid())) {
			tediSplitPanel = (SplitLayoutPanel) tabs.get( result.getTedi().getUuid() );
			if (forceRefresh) {
				tediSplitPanel.clear();
				paint = true;
			}
			mainTabLayoutPanel.selectTab( tediSplitPanel );	
		} else {
			paint = true;
			tediSplitPanel = new SplitLayoutPanel(10);
			String tabLabel = AonStringUtils. trim(AonStringUtils.abbreviate(
					(result.getInvoice().getType() != null ? result.getInvoice().getType().getDescription() : " ")
					+ " " + AonStringUtils.defaultString(result.getTedi().getRname()) , 25));
			CloseTab closeTab = new CloseTab(tabLabel, true);
			closeTab.addCloseHandler(new CloseHandler<Integer>() {
				@Override
				public void onClose(CloseEvent<Integer> event) {
					mainTabLayoutPanel.remove(tediSplitPanel);
					tabs.remove( result.getTedi().getUuid());
				}
			});
			mainTabLayoutPanel.add(tediSplitPanel,closeTab);
			tabs.put( result.getTedi().getUuid(),tediSplitPanel);
			mainTabLayoutPanel.selectTab(mainTabLayoutPanel.getWidgetCount() - 1);
		}
		if (paint) {
			if (result.hasPDFAttach() || result.hasJPEGAttach()) {
				SimpleLayoutPanel attachContainer = new SimpleLayoutPanel();
				tediSplitPanel.addEast(attachContainer, 350);
				tediSplitPanel.setWidgetToggleDisplayAllowed(attachContainer, true);
				paintAttach( result , attachContainer);
			}
			SimpleLayoutPanel contentPanel = new SimpleLayoutPanel();
			tediSplitPanel.add(contentPanel);
			if (result.isImportable()) {
				paintInvoice(contentPanel,result);
			} else if (result.isEmpty()) {
				paintInvoice(contentPanel,result);
			} else if (result.getInvoice().getRegistry() != null) {
				paintInvoice(contentPanel,result);
			} else {
				contentPanel.setWidget( getProblemsWidget( result ) );
			}
		}
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
				onRefresh( );
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

	private Widget getAttachToolbarPanel(final String url) {
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
		toolbar.setWidget(0, 0, new Label(AON.MSG.attach()));
		toolbar.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFindingToolbar());
		Anchor download = new Anchor(AON.MSG.download(),url,"_blank");
		download.setTitle(AON.MSG.download());
		download.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		download.addStyleName(AON.AON_CSS.aonIconAttach());
		buttonContainer.add(download);
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}

	private void onRefresh() {
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
		SERVICE.putInvoices(getDomainName(), getUser(), getDomain(), accepted,
				new AsyncCallback<LinkedList<TediResult>>() {

					@Override
					public void onSuccess(LinkedList<TediResult> i) {
						for (TediResult result : i ) {
							SimpleLayoutPanel tabPanel = (SimpleLayoutPanel) tabs.get(result.getTedi().getUuid() );
							tabs.remove( result.getTedi().getUuid());
							mainTabLayoutPanel.remove(tabPanel);
						}
						onRefresh();
					}

					@Override
					public void onFailure(Throwable caught) {
						showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
					}

				});
	}

	protected void onAccept(TediResult result) {
		result.getTedi().setOldStatus(result.getTedi().getStatus());
		result.getTedi().setStatus(TediInvoiceStatus.accepted);
		SERVICE.putInvoice(getDomainName(), getUser(), getDomain(), result.getTedi(), new AsyncCallback<TediResult>() {

			@Override
			public void onSuccess(TediResult result) {
				SimpleLayoutPanel tabPanel = (SimpleLayoutPanel) tabs.get(result.getTedi().getUuid() );
				tabs.remove( result.getTedi().getUuid());
				mainTabLayoutPanel.remove(tabPanel);
				onRefresh( );
			}

			@Override
			public void onFailure(Throwable caught) {
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

	private void paintInvoice(SimplePanel container, TediResult result) {
		if (result.isImportable()) {
			paintAccountEntryModule(container, result );
		}
		
		if (!result.isImportable()) {
			if (result.getInvoice().getRegistry() == null 
			&& (result.getInvoice().getDetails() == null || result.getInvoice().getDetails().size() == 0)) {
				paintAccountEntryModule(container, result );
			}
			if (result.getInvoice().getRegistry() != null) {
				// TODO !!!! Aquí puede pasar cualquier cosa!!
				paintAccountEntryModule(container, result );
			}
		}
	};
	
	private void paintAccountEntryModule(SimplePanel container, TediResult result) {	
		AccountEntryModule module = new AccountEntryModule();
		AccountingInvoice ai = result.getAccountingInvoice();
		module.onModuleLoad(container, getCurrentDomainName(), getCurrentUser(), getCurrentDomain(), configuration, ai,
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
						onRefresh( );
					}
				});
	}
	
	private void paintAttach(TediResult result, SimpleLayoutPanel container) {
		if (result.hasPDFAttach()) {
			SplitLayoutPanel attachSplit = new SplitLayoutPanel();
			container.setWidget(attachSplit);
			ScrollPanel scrollPanel = new ScrollPanel();
			attachSplit.add(scrollPanel);
			
			VerticalPanel verticalPanel = new VerticalPanel();
			verticalPanel.add(pdfViewer);
			scrollPanel.setWidget(verticalPanel);
			pdfViewer.addStyleName(AON.AON_CSS.aonWidthAll());
			pdfViewer.addStyleName(AON.AON_CSS.aonHeightAll());
			SERVICE.getInvoiceAttachURL(getCurrentDomainName(), getUser(), getCurrentDomain(),
					result.getTedi().getUuid(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String result) {
					attachSplit.addNorth(getAttachToolbarPanel(result), 25);
					pdfViewer.setDocument(result, 1.0);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
				}
			});
		}
		if (result.hasJPEGAttach()) {
			SplitLayoutPanel attachSplit = new SplitLayoutPanel();
			container.setWidget(attachSplit);
			ScrollPanel scrollPanel = new ScrollPanel();
			attachSplit.add(scrollPanel);
			Image image = new Image();
			image.setStyleName(AON.AON_CSS.aonWidthAll());
			image.addStyleName(AON.AON_CSS.aonHeightAll());
			scrollPanel.setWidget(image);
			SERVICE.getInvoiceAttachURL(getCurrentDomainName(), getUser(), getCurrentDomain(),
					result.getTedi().getUuid(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String result) {
					attachSplit.addNorth(getAttachToolbarPanel(result), 25);
					image.setUrl(result);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
				}
			});
		}
	}

	private Widget getProblemsWidget(TediResult result) {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		FlowPanel panel = new FlowPanel();
		scrollPanel.setWidget(panel);
		if (result.getMessages() != null && result.getMessages().size() > 0) {
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
				if (error.canBeFixed()) {
					focuspanel.addClickHandler(new ClickHandler() {
	
						@Override
						public void onClick(ClickEvent event) {
							error.getContext().getKey().visit(result, tediContextVisitor, new ICallback() {
	
								@Override
								public void onCancel() {
	
								}
	
								@Override
								public void onAccept(TediResult result) {
									SERVICE.validateInvoice(getDomainName(), getUser(), getDomain(), result,
											new AsyncCallback<TediResult>() {
	
												@Override
												public void onSuccess(TediResult result) {
													onSelect(result, true);
												}
	
												@Override
												public void onFailure(Throwable caught) {
													showMessage(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
												}
											});
								}
							});
						}
					});
				}
				flowPanel.add(msgLabel);
				panel.add(focuspanel);
			}
		}
		return scrollPanel;
	}

}

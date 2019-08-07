package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeHandler;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeHandler;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryPrinter;
import com.esferalia.aon.gwt.fiscal.client.accounting.utilities.CustomPopup;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceRecorder;
import com.esferalia.aon.occam.api.model.tedi.TediLevel;
import com.esferalia.aon.occam.api.model.tedi.TediParserError;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import es.translogia.tedi.ewok.TediInvoiceStatus;

public class TediCenter extends MainEntryPoint {

	private static TediServiceAsync SERVICE;
	private String currentDomainName;
	private int currentDomain;
	private String currentUser;

	private AonConfiguration configuration;
	private DockLayoutPanel dockLayoutPanel;
	
	private SplitLayoutPanel mainSplitLayoutPanel;
	private SimpleLayoutPanel sidebarContent;
	
	private SplitLayoutPanel contentSplitLayoutPanel;
	private TabLayoutPanel invoiceContent;

	private FlowPanel toolbarPanel;	
	private Button acceptAll;
	
	private MinimizePanel footPanel;
	private TabLayoutPanel tabLayout;
	private SimpleLayoutPanel problemsContent;
	private LinkedList<TediResult> resultList;
	
	// FILTER
	private CheckBox noErrors = new CheckBox("Sin errores");
	private CheckBox information = new CheckBox("Info");
	private CheckBox warnings = new CheckBox("Avisos");
	private CheckBox errors = new CheckBox("Errores");

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
				dockLayoutPanel.addNorth(getHeaderPanel(), 60);
				mainSplitLayoutPanel = new SplitLayoutPanel();
				
				sidebarContent = new SimpleLayoutPanel();
				mainSplitLayoutPanel.addWest(sidebarContent, 400);
				contentSplitLayoutPanel = new SplitLayoutPanel();
				contentSplitLayoutPanel.setStyleName(AON.AON_CSS.aonSelector());
				mainSplitLayoutPanel.add(contentSplitLayoutPanel);
				
				footPanel = new MinimizePanel();
				footPanel.addMinimizeHandler(new MinimizeHandler() {
					
					@Override
					public void onMinimize(MinimizeEvent event) {
						closeFootPanel();
					}
				});
				
				footPanel.addMaximizeHandler(new MaximizeHandler() {
					
					@Override
					public void onMaximize(MaximizeEvent event) {
						contentSplitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2);
						contentSplitLayoutPanel.animate(500);
					}
				});

				footPanel.setStyleName(AON.AON_CSS.aonSelector());
				tabLayout = new TabLayoutPanel(26, Unit.PX);
				tabLayout.setWidth("100%");
				problemsContent = new SimpleLayoutPanel();
				tabLayout.add(problemsContent, template.tab(AON.MSG.problems(), AON.AON_CSS.aonIconError()));
				footPanel.add(tabLayout);
				contentSplitLayoutPanel.addSouth(footPanel, 26);

				invoiceContent = new TabLayoutPanel(26,Unit.PX);
				contentSplitLayoutPanel.add(invoiceContent);


				dockLayoutPanel.add(mainSplitLayoutPanel);

				onRefreshSplash();
			}

			@Override
			public void onFailure(Throwable caught) {
				showMessage( AON.MSG.error() + " [Interno: " + caught.getMessage() + "]" );
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
				onRefreshSplash();
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

	private void refreshToolbar() {
		int count = 0;
		if (resultList != null && resultList.size() > 0) {
			for ( TediResult tediResult : resultList) {
				if (tediResult.isChecked()) {
					count++;
				}
			}
		}
		acceptAll.setVisible(count>0);
		acceptAll.setText("Aceptar ("+count+")");
	}

//	private Widget getInvoiceToolbarPanel(TediResult result) {
//		FlowPanel toolbarPanel = new FlowPanel();
//		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
//		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
//		FlexTable toolbar = new FlexTable();
//		toolbar.setCellPadding(0);
//		toolbar.setCellSpacing(0);
//		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
//		FlowPanel titlePanel = new FlowPanel();
//		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
//		toolbar.setWidget(0, 0, titlePanel);
//		toolbar.setWidget(0, 0, new Label("tEDI center"));
//		toolbar.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFindingTitle());
//		toolbar.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
//		toolbar.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonNowrap());
//		toolbar.setWidget(0, 1, new Label());
//		toolbar.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFindingSubtitleIternal());
//		FlowPanel buttonContainer = new FlowPanel();
//		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
//		toolbar.setWidget(0, 2, buttonContainer);
//		toolbar.getCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFindingToolbar());
//
//		final Button accept = new Button();
//		accept.setText(AON.MSG.accept());
//		accept.setTitle(AON.MSG.accept());
//		accept.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
//		accept.addStyleName(AON.AON_CSS.aonIconSave());
//		accept.addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				onAccept(result);
//			}
//		});
//		buttonContainer.add(accept);
//
//		toolbarPanel.add(toolbar);
//		return toolbarPanel;
//	}

	private Widget getHeaderPanel() {
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonPanelGridSearch());
		tab.addStyleName(AON.AON_CSS.aonWidth90Percent());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());

		tab.getColumnFormatter().setWidth(0, "auto");
		tab.getColumnFormatter().setWidth(1, "30px;");

		noErrors.setValue(true);
		noErrors.setStyleName(AON.AON_CSS.aonMarginLeft());
		information.setStyleName(AON.AON_CSS.aonMarginLeft());
		information.setValue(true);
		warnings.setStyleName(AON.AON_CSS.aonMarginLeft());
		warnings.setValue(true);
		errors.setStyleName(AON.AON_CSS.aonMarginLeft());
		errors.setValue(true);

		noErrors.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				paintList(null);
			}
		});
		information.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				paintList(null);
			}
		});
		warnings.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				paintList(null);
			}
		});
		errors.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				paintList(null);
			}
		});

		FlowPanel checksPanel = new FlowPanel();
		checksPanel.setStyleName(AON.AON_CSS.aonNowrap() );
		InlineLabel showLabel = new InlineLabel(AON.MSG.show());
		checksPanel.add(showLabel);
		checksPanel.add(noErrors);
		checksPanel.add(information);
		checksPanel.add(warnings);
		checksPanel.add(errors);
		
		tab.setWidget(0, 0, checksPanel);
		tab.setWidget(0, 1, new Label());

		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		scrollPanel.setWidget(tab);
		return scrollPanel;
	}
	
	private PopupPanel showPopupPanel() {
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTediTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		return popup;
	}
	
	private void closeFootPanel() {
		contentSplitLayoutPanel.setWidgetSize(footPanel, 30);
		contentSplitLayoutPanel.animate(500);
	}
	
	private void onRefresh(PopupPanel popup) {
		SERVICE.getVerifiedInvoices(getDomainName(), getUser(), getDomain(), new AsyncCallback<LinkedList<TediResult>>() {

			@Override
			public void onSuccess(LinkedList<TediResult> results) {
				resultList = results;
				paintList( popup );
				refreshToolbar();
			}

			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				showMessage( AON.MSG.error() + " [Interno: " + caught.getMessage() + "]" );
			}

		});
		refreshProblems(null);
	}

	private void paintList(PopupPanel popup) {
		sidebarContent.clear();
		if (resultList != null && resultList.size() > 0) {
			ScrollPanel scrollPanel = new ScrollPanel();
			scrollPanel.setStyleName(AON.AON_CSS.aonScrollArea());
			sidebarContent.add(scrollPanel);
			FlowPanel invoiceContainer = new FlowPanel();
			scrollPanel.add(invoiceContainer);
			boolean first = true;
			for (TediResult result : resultList) {
				if (applyFilter(result)) {
					invoiceContainer.add(paintInvoice(result));
					if (first) {
						showInvoice(result);
						first = false;
					}
				}
			}
			if (popup != null) {
				popup.hide();
			}
		} else {
			if (popup != null) {
				popup.hide();
			}
			showMessage( "No se han encontrado facturas verificadas en tEDI.center" );
		}
	}

	private boolean applyFilter(TediResult result) {
		TediLevel curLevel = result.getMoreSeriousLevel();
		boolean accepted = false;
		if (!accepted && noErrors.getValue() && curLevel == null) {
			accepted = true;		
		}
		if (!accepted && information.getValue() && curLevel == TediLevel.INF) {
			accepted = true;		
		}
		if (!accepted && warnings.getValue() && curLevel == TediLevel.WRN) {
			accepted = true;
		}
		if (!accepted && errors.getValue() && curLevel == TediLevel.ERR) {
			accepted = true;
		}
		return accepted;
	}

	protected void onRefreshSplash() {
		PopupPanel popup = showPopupPanel();
		onRefresh(popup);
	}
	protected void onAcceptAll() {
		LinkedList<TediResult> accepted = new LinkedList<TediResult>(); 
		for ( TediResult result : resultList) {
			if (result.isChecked()){
				accepted.add(result);
			}
		}
		onAcceptAll(accepted);
	}
	
	private void onAcceptAll(LinkedList<TediResult> accepted) {
		PopupPanel popup = showPopupPanel();
		SERVICE.putInvoices(getDomainName(), getUser(), getDomain(), accepted, new AsyncCallback<LinkedList<TediResult>>() {

			@Override
			public void onSuccess(LinkedList<TediResult> i) {
				popup.hide();
				onRefresh(null);
			}

			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				showMessage( AON.MSG.error() + " [Interno: " + caught.getMessage() + "]" );
			}

		});
		refreshProblems(null);
	}
	
	protected void onAccept(TediResult result) {
		PopupPanel popup = showPopupPanel();
		result.getTedi().setOldStatus( result.getTedi().getStatus() );
		result.getTedi().setStatus( TediInvoiceStatus.accepted );
		SERVICE.putInvoice(getDomainName(), getUser(), getDomain(), result.getTedi(), new AsyncCallback<TediResult>() {

			@Override
			public void onSuccess(TediResult results) {
				popup.hide();
				onRefresh(null);
			}

			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				showMessage( AON.MSG.error() + " [Interno: " + caught.getMessage() + "]" );
			}

		});
		refreshProblems(null);
	}

	private Panel paintInvoice(TediResult result) {
		Invoice invoice = result.getInvoice();
		FocusPanel invoiceContainer = new FocusPanel();
		invoiceContainer.setStyleName(AON.AON_CSS.aonClickableBlock());
		invoiceContainer.addStyleName(AON.AON_CSS.aonSimpleBorder());
		invoiceContainer.addStyleName(AON.AON_CSS.aonMargin());

		FlexTable tab = new FlexTable();
		tab.getColumnFormatter().setWidth(0, "20px");
		tab.getColumnFormatter().setWidth(1, "40px");
		tab.getColumnFormatter().setWidth(2, "80px");
		tab.getColumnFormatter().setWidth(3, "60px");
		tab.getColumnFormatter().setWidth(4, "auto");
		tab.setStyleName(AON.AON_CSS.aonWidthAll());
		invoiceContainer.add(tab);

		TediLevel curLevel  = result.getMoreSeriousLevel();  
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
		tab.getElement().getStyle().setBackgroundColor(color);

		InlineLabel checkLabel = new InlineLabel(AonStringUtils.SPACE);
		checkLabel.setVisible(  curLevel != TediLevel.ERR);
		checkLabel.setStyleName(AON.AON_CSS.aonIconCheck());
		checkLabel.addStyleName(AON.AON_CSS.aonPaddingLeft());
		checkLabel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				result.setChecked( !result.isChecked() );
				if (result.isChecked()) {
					checkLabel.addStyleName(AON.AON_CSS.aonIconChecked());		
					checkLabel.removeStyleName(AON.AON_CSS.aonIconCheck());		
				} else {
					checkLabel.addStyleName(AON.AON_CSS.aonIconCheck());		
					checkLabel.removeStyleName(AON.AON_CSS.aonIconChecked());		
				}
				refreshToolbar();
				event.stopPropagation();
			}
		});
		tab.setWidget(0, 0, checkLabel);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonTextCenter());

		InlineLabel typeLabel = new InlineLabel(invoice.getType().getDescription());
		typeLabel.setStyleName(AON.AON_CSS.aonPaddingLeft());
		typeLabel.addStyleName(AON.AON_CSS.aonNowrap());
		typeLabel.addStyleName(AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().setColSpan(0, 1, 2);
		tab.setWidget(0, 1, typeLabel);
		tab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonTextCenter());

		InlineLabel totalLabel = new InlineLabel(AON.MSG.total());
		totalLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(0, 2, totalLabel);

		InlineLabel totalValueLabel = new InlineLabel(AON.FMT.format(invoice.getTotal()));
		totalValueLabel.setStyleName(AON.AON_CSS.aonBold());
		totalValueLabel.addStyleName(AON.AON_CSS.aonFontBig());
		tab.setWidget(0, 3, totalValueLabel);

		InlineLabel dateLabel = new InlineLabel(AON.MSG.date());
		dateLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.getFlexCellFormatter().setColSpan(1, 0, 2);
		tab.setWidget(1, 0, dateLabel);

		InlineLabel dateValueLabel = new InlineLabel(
				invoice.getIssueDate() != null ? AON.DATE_FORMAT.format(invoice.getIssueDate()) : "");
		dateValueLabel.setStyleName(AON.AON_CSS.aonBold());
		tab.setWidget(1, 1, dateValueLabel);

		InlineLabel invoiceNumberLabel = new InlineLabel(AON.MSG.invoiceNumberAbbr());
		invoiceNumberLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.setWidget(1, 2, invoiceNumberLabel);

		InlineLabel invoiceNumberValueLabel = new InlineLabel(invoice.getReferenceCode());
		invoiceNumberValueLabel.setStyleName(AON.AON_CSS.aonBold());
		invoiceNumberValueLabel.addStyleName(AON.AON_CSS.aonNowrap());
		tab.setWidget(1, 3, invoiceNumberValueLabel);
		

		InlineLabel titularLabel = new InlineLabel(AON.MSG.titular());
		titularLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		tab.getFlexCellFormatter().setColSpan(2, 0, 2);
		tab.setWidget(2, 0, titularLabel);

		String titular = invoice.getRegistryDocument() + " - " + invoice.getRegistryName();
		if (AonStringUtils.length(titular) > 35 ) {
			invoiceContainer.setTitle(titular);
			titular = AonStringUtils.abbreviate(titular, 35);
		}
		InlineLabel titularValueLabel = new InlineLabel(titular);
		titularValueLabel.setStyleName(AON.AON_CSS.aonBold());
		titularValueLabel.addStyleName(AON.AON_CSS.aonNowrap());
		tab.getFlexCellFormatter().setColSpan(2, 1, 3);
		tab.setWidget(2, 1, titularValueLabel);

		invoiceContainer.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showInvoice(result);
			}
		});

		return invoiceContainer;
	}

	private void showInvoice(TediResult result) {
//		if (mainSplitLayoutPanel.getWidgetSize(invoiceContent) <= 50) {
//			mainSplitLayoutPanel.setWidgetSize(invoiceContent, Window.getClientWidth() / 1.5);
//			mainSplitLayoutPanel.animate(500);
//		}
		invoiceContent.clear();
		TediInvoiceViewer tediInvoiceViewer = new TediInvoiceViewer(result.getInvoice());
		ScrollPanel invoicePanel = new ScrollPanel();
		invoicePanel.setStyleName(AON.AON_CSS.aonScrollArea());
		invoicePanel.setWidget(tediInvoiceViewer);
		invoiceContent.add(tediInvoiceViewer, template.tab(AON.MSG.invoice(), AON.AON_CSS.aonIconInvoice()));
		
		if ( result.isImportable() ) {
			AccountEntry[] entries = InvoiceRecorder.recordInvoice( result.getAccountingInvoice() );
			if (entries != null) {
				ScrollPanel entriesScrollPanel = new ScrollPanel();
				FlowPanel entriesPanel = new FlowPanel();
				entriesScrollPanel.setWidget(entriesPanel);
				for (AccountEntry entry : entries) {
					FocusPanel entryPanel = AccountEntryPrinter.print(entry);
					entryPanel.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							showEntry(entry.getDomain(), result);
						}
					});
					entriesPanel.add(entryPanel );
				}
				invoiceContent.add(entriesScrollPanel, template.tab(AON.MSG.previewAccountEntry(), AON.AON_CSS.aonIconCompany()));
			}	
		}
		refreshProblems(result);
	};

	private void refreshProblems(TediResult result) {
		problemsContent.clear();
		if (result == null) {
			problemsContent.setWidget(new Label(""));
		} else {
			if (contentSplitLayoutPanel.getWidgetSize(footPanel) <= 50) {
				contentSplitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
				contentSplitLayoutPanel.animate(500);
			}
			if (result.getMessages() == null) {
				Label okLabel = new Label("No se han encontrado errores.");
				okLabel.setStyleName(AON.AON_CSS.aonBold());
				okLabel.addStyleName(AON.AON_CSS.aonIconPointLightGreen());
				okLabel.addStyleName(AON.AON_CSS.aonPaddingLeft());
				problemsContent.setWidget(okLabel);
			} else {

				ScrollPanel scrollPanel = new ScrollPanel();
				scrollPanel.setStyleName(AON.AON_CSS.aonScrollArea());

				FlexTable tab = new FlexTable();
				tab.addStyleName(AON.AON_CSS.aonReportTable());
				tab.addStyleName(AON.AON_CSS.aonReportTableFontMedium());

				FlowPanel panel = new FlowPanel();
				panel.setStyleName(AON.AON_CSS.aonMarginLeft());
				panel.setStyleName(AON.AON_CSS.aonPaddingLeft());
				for (TediParserError error : result.getMessages()) {
					Label errLabel = new Label(error.getLevel().toString() + " " + error.getMessage());
					errLabel.setStyleName(AON.AON_CSS.aonMarginTop5());
					errLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
					errLabel.addStyleName(AON.AON_CSS.aonClickableLabel());
					if (error.getLevel() == TediLevel.ERR) {
						errLabel.addStyleName(AON.AON_CSS.aonIconPointRed());		
					} else if (error.getLevel() == TediLevel.WRN) {
						errLabel.addStyleName(AON.AON_CSS.aonIconPointOrange());
					} else {
						errLabel.addStyleName(AON.AON_CSS.aonIconPointYellow());
					}
					panel.add( errLabel );
				}
				scrollPanel.setWidget(panel);
				problemsContent.setWidget(scrollPanel);
			}
		}
	}

	private void showEntry(int domain,TediResult result) {
		CustomPopup entryDialog = new CustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption(AON.MSG.accountEntries());
		AccountEntryModule module = new AccountEntryModule();
		AccountingInvoice ai = result.getAccountingInvoice();
		module.onModuleLoad(entryDialog, getCurrentDomainName(), getCurrentUser(), domain, configuration, ai, new ModuleCallback<AccountEntry>() {
			
			@Override
			public void onRemove(AccountEntry removed) {
				entryDialog.hide();
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
			
			@Override
			public void onExit() {
				entryDialog.hide();
			}
			
			@Override
			public void onChange(AccountEntry changed) {
				result.setAon(null);
				LinkedList<TediResult> accepted = new LinkedList<TediResult>(); 
				accepted.add(result);
				onAcceptAll(accepted);
				entryDialog.hide();
			}
		});
		entryDialog.center();
		entryDialog.show();
	}
}

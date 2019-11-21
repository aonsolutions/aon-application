package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.fiscal.client.tedi.TediCenter.TediCenterCallback;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.tedi.TediCompanyResult;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class TediCompanyList  extends DockLayoutPanel { 
	
	private static TediServiceAsync SERVICE;
	
	private String currentDomainName;
	private int currentDomain;
	private String currentUser;
	private TediCenterCallback callback;
	private SimpleLayoutPanel tabContainer = new SimpleLayoutPanel();
	private LinkedList<TediCompanyResult> list;
	
	private int colorIndex = 0;

	public TediCompanyList(String currentDomainName,int currentDomain, String currentUser,TediCenterCallback callback) {
		super(Unit.PX);
		this.currentDomainName = currentDomainName;
		this.currentDomain = currentDomain;
		this.currentUser = currentUser;
		this.callback = callback;
		
		if (this.callback== null) {
			MessageDialog.error("No TEDI CENTER detected");
			throw new IllegalArgumentException("No TEDI CENTER detected");
		}

		TediServiceAsync serviceRaw = GWT.create(TediService.class);
		SERVICE = new TediServiceAsyncDecorator(serviceRaw);
		
		addNorth(getToolbarPanel(), 25);
		addNorth( paintFilterWidget() , 100);
		add(tabContainer);
		onSearch( );
	}
	
	private Widget paintFilterWidget() {
		SimpleLayoutPanel northPanel = new SimpleLayoutPanel();
		northPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		FilterPanel fp = new FilterPanel() {
			@Override
			protected void onDocumentChange(String value) {
				paintTable( filter());
			}

			@Override
			protected void onNameChange(String value) {
				paintTable( filter());
			}

			private LinkedList<TediCompanyResult> filter() {
				return list
					.stream()
					.filter(f -> (
						AonStringUtils.isBlank(getDocument()) 
							|| AonStringUtils.containsIgnoreCase( AonStringUtils.defaultString(f.getCompany().getDocument()), AonStringUtils.defaultString(getDocument()))
								)
						&& (
						AonStringUtils.isBlank(getName()) 
							|| AonStringUtils.containsIgnoreCase( AonStringUtils.defaultString(f.getCompany().getName()), AonStringUtils.defaultString(getName()))
							)
					)
					.collect(Collectors.toCollection(LinkedList::new));
			}

		};
		fp.getElement().getStyle().setMarginBottom(10, Unit.PX);
		northPanel.setWidget(fp);
		return northPanel;
	}

	private void onSync(Boolean showNotTedi) {
		FlowPanel p = new FlowPanel();
		p.setStyleName(AON.AON_CSS.aonWidthAll());
		p.addStyleName(AON.AON_CSS.aonTextCenter());
		Label wait = new Label( "Cargando. Un momento, por favor ....." );
		wait.setStyleName(AON.AON_CSS.aonTextCenter());
		wait.addStyleName(AON.AON_CSS.aonBold());
		wait.addStyleName(AON.AON_CSS.aonColoRoyalblue());
		wait.addStyleName(AON.AON_CSS.aonMarginTop());
		p.add(wait);
		tabContainer.setWidget(p);

		SERVICE.tediSync(this.currentDomainName, this.currentUser, this.currentDomain, callback.isSnapshot()
			, showNotTedi, new AsyncCallback<LinkedList<TediCompanyResult>>() {
			
			@Override
			public void onSuccess(LinkedList<TediCompanyResult> results) {
				setData(results);
			}

			@Override 
			public void onFailure(Throwable caught) {
				MessageDialog.error(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");	
			}
		});
	}
	
	private void onSearch() {
		FlowPanel p = new FlowPanel();
		p.setStyleName(AON.AON_CSS.aonWidthAll());
		p.addStyleName(AON.AON_CSS.aonTextCenter());
		Label wait = new Label( "Cargando. Un momento, por favor ....." );
		wait.setStyleName(AON.AON_CSS.aonTextCenter());
		wait.addStyleName(AON.AON_CSS.aonBold());
		wait.addStyleName(AON.AON_CSS.aonColoRoyalblue());
		wait.addStyleName(AON.AON_CSS.aonMarginTop());
		p.add(wait);
		tabContainer.setWidget(p);

		SERVICE.getCompanies(this.currentDomainName, this.currentUser, this.currentDomain, callback.isSnapshot(),
				new AsyncCallback<LinkedList<TediCompanyResult>>() {
					@Override
					public void onSuccess(LinkedList<TediCompanyResult> results) {
						setData(results);
					}

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog.error(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
					}

				});
	}

	private void setData( LinkedList<TediCompanyResult> list ) {
		this.list = list;
		paintTable(list);
	}
	
	private void paintTable( LinkedList<TediCompanyResult> list ) {
		tabContainer.clear();
		tabContainer.setWidget( getTableWidget(list) );
	}

	private Widget getTableWidget(LinkedList<TediCompanyResult> list) {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		if (list == null || list.size() == 0) {
			Label noData = new Label( AON.MSG.noData());
			noData.setStyleName(AON.AON_CSS.aonTextCenter());
			noData.addStyleName(AON.AON_CSS.aonBold());
			noData.addStyleName(AON.AON_CSS.aonMarginTop());
			scrollPanel.setWidget(noData);	
		} else {
			FlexTable tab = new FlexTable();
			tab.setStyleName(AON.AON_CSS.aonDataTable());
			tab.addStyleName(AON.AON_CSS.aonBlockCenter());
			tab.addStyleName(AON.AON_CSS.aonReport());
			
			int row = 0;
			int cell = 0;
			String[] widths = {"25px","150px","25px","auto","100px"};
			String[] labels = {"",AON.MSG.document(),"",AON.MSG.name(),"Pendientes"};
			for (String w : widths) {
				tab.getCellFormatter().setStyleName(row,cell, AON.AON_CSS.aonDataTableHeader());
				tab.getColumnFormatter().setWidth( cell, w);
				tab.setWidget(row, cell, new Label(labels[cell]));
				cell++;
			}
			tab.getCellFormatter().addStyleName(row,cell-1, AON.AON_CSS.aonTextRight());
			row++;
			
			for ( TediCompanyResult result : list) {
				paintRow(tab, row, result);
				row++;
			}
			scrollPanel.setWidget(tab);
		}
		return scrollPanel;
	}
	
	private void paintRow(FlexTable tab, int row, TediCompanyResult result) {
		int cell = 0;
		tab.getRowFormatter().setStyleName(row, AON.AON_CSS.aonReportRow());
		
		if ( result.getTedi()== null || result.getTedi() ) {
			Label selectButton = new Label();
			selectButton.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			selectButton.addStyleName(AON.AON_CSS.aonClickableLabel());
			selectButton.addStyleName(AON.AON_CSS.aonIconRowSelector());
			selectButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					onSelect(result);
				}
			});
			tab.setWidget(row, cell, selectButton);
			cell++;
		} else {
			tab.setWidget(row, cell, new Label());
			cell++;
		}
		
		Label document = new Label(result.getCompany().getDocument());
		tab.setWidget(row, cell, document);
		cell++;

		if ( result.getTedi()== null || result.getTedi() ) {
			tab.setWidget(row, cell, new Label());
			cell++;
		} else {
			Label resetButton = new Label();
			resetButton.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			resetButton.addStyleName(AON.AON_CSS.aonClickableLabel());
			resetButton.addStyleName(AON.AON_CSS.aonIconReset());
			resetButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					SyncDialog d = new SyncDialog(true) {
						
						@Override
						protected void onAccept() {
							hide();
							SERVICE.addTediCompany(TediCompanyList.this.currentDomainName, TediCompanyList.this.currentUser
								, TediCompanyList.this.currentDomain
								, callback.isSnapshot(), result, new AsyncCallback<Void>() {
								
								@Override public void onFailure(Throwable caught) {
									MessageDialog.error(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");											
								}
								@Override public void onSuccess(Void voidd) {
									result.setTedi(true); 
									result.setInboxCount(0);
									paintRow(tab, row, result);
								}
							});
						}
					};
					d.center();
				}
			});
			tab.setWidget(row, cell, resetButton);
			cell++;
		}
		
		Label name = new Label(result.getCompany().getName() + " (" + result.getCompany().getDomain() + ")");
		tab.setWidget(row, cell, name);
		cell++;

		SimplePanel pendingPanel = new SimplePanel();
		tab.setWidget(row, cell, pendingPanel);
		tab.getCellFormatter().setStyleName(row, cell, AON.AON_CSS.aonTextRight());
		if ( (result.getTedi()== null || result.getTedi()) && result.getInboxCount() == null) {
			Label requestLabel = new Label();
			requestLabel.setStyleName(AON.AON_CSS.aonLoader());
			pendingPanel.setWidget(requestLabel);

			SERVICE.getCountInboxInvoices(this.currentDomainName, this.currentUser, result.getCompany().getDomain()
				,callback.isSnapshot(), result.getCompany(), new AsyncCallback<Integer>() {
				
				@Override
				public void onSuccess(Integer count) {
					result.setInboxCount( count );
					Label pending = new Label(AON.FMT_INT.format(count));
					pendingPanel.setWidget(pending);
				}
		
				@Override
				public void onFailure(Throwable caught) {
					result.setInboxCount( -1 );
					Label pending = new Label();
					pendingPanel.setWidget(pending);
				}
			});
		} else {
			Label pending = new Label(result.getInboxCount()==null?"":AON.FMT_INT.format(result.getInboxCount()));
			pendingPanel.setWidget(pending);
		}
		cell++;
		
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
		clean.addStyleName(AON.AON_CSS.aonIconSearch());
		clean.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onSearch();
			}
		});
		buttonContainer.add(clean);

		final Button sync = new Button();
		sync.setText("Sincronizar");
		sync.setTitle("Sincronizar");
		sync.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		sync.addStyleName(AON.AON_CSS.aonIconRefresh());
		sync.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SyncDialog d = new SyncDialog() {
					
					@Override
					protected void onAccept() {
						hide();
						onSync(check.getValue());
					}
				};
				d.center();
			}
		});
		buttonContainer.add(sync);
		
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	private String getRandomColor() {
		String[] colors = {
				"#94dbff",
				"#ffeb94",
				"#afff94",
				"#ff94ff",
				"#94ffff",
				"#ff94ff",
				"#94ffff",
				"#94ffff",
				"#94dbff",
				"#ff94b2",
				"#94ffe2",
				"#94dbff",
				"#ff94ff"};
		return colors[colorIndex];
	}
	
	public void onSelect(TediCompanyResult companyResult) {
		if (companyResult.getTedi() == null || companyResult.getTedi()) {
			Company company = companyResult.getCompany();
			String tabId = "company:" + company.getDocument();
			if (callback.isAttached(tabId)) {
				callback.selectTab(tabId);
			} else {
				String tabLabel = AonStringUtils.abbreviate( company.getName() , 45);
				// TODO cambiar el nombre del dominio.
				String color = getRandomColor();
				TediInvoiceList invoiceList = callback.getTediInvoiceList(this.currentDomainName, company.getDomain(), this.currentUser, company, color);
				InvoicesCloseTab closeTab = new InvoicesCloseTab(tabLabel, color, true);
				closeTab.addCloseHandler(new CloseHandler<Integer>() {
					@Override
					public void onClose(CloseEvent<Integer> event) {
						callback.onDettachTab(tabId);
						colorIndex--;
						if (colorIndex <0) colorIndex =0;
					}
				});
				callback.onAttachTab(tabId, invoiceList, closeTab);
				colorIndex++;
				if (colorIndex > 12) colorIndex =0;
			}
		};
	}
	
}

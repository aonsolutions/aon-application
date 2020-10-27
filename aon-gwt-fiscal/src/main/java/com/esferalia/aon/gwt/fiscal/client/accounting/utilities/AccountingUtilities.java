package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.Domain;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AccountingUtilities extends MainEntryPoint{

	protected static interface IOption extends HasSelectionHandlers<IOption> {
		Widget getSidebarWidget();
		String getOptionDescription();
	}

	private static AccountingUtilitiesServiceAsync SERVICE;
	private String domainName;
	private String user;
	private int domain;
	
	public String getDomainName() {
		return domainName;
	}
	public String getUser() {
		return user;
	}
	public int getDomain() {
		return domain;
	}
	
	public static native String getRootPanel()
	/*-{
		return $wnd.localStorage.getItem("rootPanel");
	}-*/;
	
	@Override
	public void onModuleLoad() {
		onModuleLoad(getCurrentDomainName(), getCurrentUser(), getCurrentDomain());
	}
	
	public void onModuleLoad(String domainName, String user, int domain) {
		this.domainName = domainName;
		this.user = user;
		this.domain = domain;
		
		AON.ensureInjected();

		AccountingUtilitiesServiceAsync serviceRaw = GWT.create(AccountingUtilitiesService.class);
		SERVICE = new AccountingUtilitiesServiceAsyncDecorator(serviceRaw);
		
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		

		SERVICE.getDomain(domainName, user, domain, new AsyncCallback<Domain>() {
			
			@Override
			public void onSuccess(Domain domain) {
				root.add(paint( domain ));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				SimpleLayoutPanel content = new SimpleLayoutPanel();		
				Label errorLabel = new Label( "No se ha podido determinar la configuraci\u00F3n" );
				errorLabel.setStyleName(AON.AON_CSS.aonBold());
				errorLabel.addStyleName(AON.AON_CSS.aonColorRed());
				errorLabel.addStyleName(AON.AON_CSS.aonErrorPanel());
				content.setWidget(errorLabel);
				root.add(content);
			}
		});
		
	}
	
	protected Widget paint(Domain domain) {
		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel.addNorth(getToolbarPanel(), 25);
		
		SimpleLayoutPanel sidebar = new SimpleLayoutPanel();
		sidebar.setStyleName(AON.AON_CSS.aonBorderRight());
		dockLayoutPanel.addWest(sidebar, 275);
		
		ScrollPanel scrollPanel = new ScrollPanel(); 
		scrollPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		sidebar.setWidget(scrollPanel);
		
		FlowPanel sidebarMenu = new FlowPanel();
		scrollPanel.setWidget(sidebarMenu);

		SimpleLayoutPanel content = new SimpleLayoutPanel();
		
		DisclosurePanel checksDisclosurePanel = new DisclosurePanel("CHEQUEOS");
		checksDisclosurePanel.setOpen(true);
		FlowPanel checksPanel = new FlowPanel();
		checksDisclosurePanel.add(checksPanel);
		
		AccountIntegrityCheck accIntegrity = new AccountIntegrityCheck(getDomainName(),getUser(),domain);
		checksPanel.add(accIntegrity.getSidebarWidget());
		accIntegrity.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
			@Override
			public void onSelection(SelectionEvent<IOption> event) {
				content.setWidget( accIntegrity );
				accIntegrity.run();
			}
		});

		NoLowLevelAccountFinder noLowLevel = new NoLowLevelAccountFinder(getDomainName(),getUser(),domain);
		checksPanel.add(noLowLevel.getSidebarWidget());
		noLowLevel.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
			@Override
			public void onSelection(SelectionEvent<IOption> event) {
				content.setWidget( noLowLevel );
				noLowLevel.run();
			}
		});

		EmptyEntryFinder empty = new EmptyEntryFinder(getDomainName(),getUser(),domain);
		checksPanel.add(empty.getSidebarWidget());
		empty.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
			@Override
			public void onSelection(SelectionEvent<IOption> event) {
				content.setWidget( empty );
				empty.run();
			}
		});
		
		UnbalancedEntryFinder unbalanced = new UnbalancedEntryFinder(getDomainName(),getUser(),domain);
		checksPanel.add(unbalanced.getSidebarWidget());
		unbalanced.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
			@Override
			public void onSelection(SelectionEvent<IOption> event) {
				content.setWidget( unbalanced );
				unbalanced.run();
			}
		});
		sidebarMenu.add(checksDisclosurePanel);
		
		
		WrongRecordedInvoices  wrongInvoices = new WrongRecordedInvoices(getDomainName(),getUser(),domain);
		checksPanel.add(wrongInvoices.getSidebarWidget());
		wrongInvoices.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
			@Override
			public void onSelection(SelectionEvent<IOption> event) {
				content.setWidget( wrongInvoices );
				wrongInvoices.run();
			}
		});
		sidebarMenu.add(checksDisclosurePanel);

		if (!domain.isParent()) {
			DomainIntegrityCheck domainIntegrity = new DomainIntegrityCheck(getDomainName(),getUser(),domain);
			checksPanel.add(domainIntegrity.getSidebarWidget());
			domainIntegrity.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
				@Override
				public void onSelection(SelectionEvent<IOption> event) {
					content.setWidget( domainIntegrity );
					domainIntegrity.run();
				}
			});
		}

		DisclosurePanel utilitiesDisclosurePanel = new DisclosurePanel("UTILIDADES");
		utilitiesDisclosurePanel.setOpen(false);
		FlowPanel utilitiesPanel = new FlowPanel();
		utilitiesDisclosurePanel.add(utilitiesPanel);

		if (!domain.isParent()) {
			AccountRegistryChecker archecker = new AccountRegistryChecker(getDomainName(),getUser(),domain);
			utilitiesPanel.add(archecker.getSidebarWidget());
			archecker.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
				@Override
				public void onSelection(SelectionEvent<IOption> event) {
					content.setWidget( archecker );			
				}
			});
			
			JournalRegenerator journalRegenerator = new JournalRegenerator(getDomainName(),getUser(),domain);
			utilitiesPanel.add(journalRegenerator.getSidebarWidget());
			journalRegenerator.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
				@Override
				public void onSelection(SelectionEvent<IOption> event) {
					content.setWidget( journalRegenerator );
					journalRegenerator.run();
				}
			});
			
			EntriesRemover entriesRemover = new EntriesRemover(getDomainName(),getUser(),domain);
			utilitiesPanel.add(entriesRemover.getSidebarWidget());
			entriesRemover.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
			  @Override
			  public void onSelection(SelectionEvent<IOption> event) {
			    content.setWidget( entriesRemover );
			    entriesRemover.run();
			  }
			});

			InputVatRegenerator inputVatRegenerator = new InputVatRegenerator(getDomainName(),getUser(),domain);
			utilitiesPanel.add(inputVatRegenerator.getSidebarWidget());
			inputVatRegenerator.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
				@Override
				public void onSelection(SelectionEvent<IOption> event) {
					content.setWidget( inputVatRegenerator );
					inputVatRegenerator.run();
				}
			});
		}
		
		if (domain.isParent()) {
			ParentAccountLinker linker = new ParentAccountLinker(getDomainName(),getUser(),domain);
			utilitiesPanel.add(linker.getSidebarWidget());
			linker.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
				@Override
				public void onSelection(SelectionEvent<IOption> event) {
					content.setWidget( linker );			
				}
			});
		}
		sidebarMenu.add(utilitiesDisclosurePanel);
		
		dockLayoutPanel.add( content );
		return dockLayoutPanel; 
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
		toolbar.setWidget(0, 0, new Label("Utilidades contables"));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	
}

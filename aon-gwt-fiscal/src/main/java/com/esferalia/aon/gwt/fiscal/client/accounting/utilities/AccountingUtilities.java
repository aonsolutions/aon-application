package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.Domain;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
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
				errorLabel.setStyleName(AON.CSS.aonBold());
				errorLabel.addStyleName(AON.CSS.aonColorRed());
				content.setWidget(errorLabel);
				root.add(content);
			}
		});
		
	}
	
	protected Widget paint(Domain domain) {
		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		AonToolbar toolbar = new AonToolbar("Utilidades contables");
		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		
		SimpleLayoutPanel sidebar = new SimpleLayoutPanel();
		sidebar.setStyleName(AON.CSS.aonBorderRight());
		dockLayoutPanel.addWest(sidebar, 275);
		
		ScrollPanel scrollPanel = new ScrollPanel(); 
		scrollPanel.setStyleName(AON.CSS.aonWidthAll());
		sidebar.setWidget(scrollPanel);
		
		FlowPanel sidebarMenu = new FlowPanel();
		scrollPanel.setWidget(sidebarMenu);

		SimpleLayoutPanel content = new SimpleLayoutPanel();
		
		AonDisplayTable checksGrid = new AonDisplayTable();
		sidebarMenu.add(checksGrid);
		checksGrid.addRow().addCell(new InlineLabel("CHEQUEOS"), AON.CSS.aonBold(), AON.CSS.aonTextUnderline());
		
		AccountIntegrityCheck accIntegrity = new AccountIntegrityCheck(getDomainName(),getUser(),domain);
		checksGrid.addRow().addCell(accIntegrity.getSidebarWidget());
		accIntegrity.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
			@Override
			public void onSelection(SelectionEvent<IOption> event) {
				content.setWidget( accIntegrity );
				accIntegrity.run();
			}
		});

		NoLowLevelAccountFinder noLowLevel = new NoLowLevelAccountFinder(getDomainName(),getUser(),domain);
		checksGrid.addRow().addCell(noLowLevel.getSidebarWidget());
		noLowLevel.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
			@Override
			public void onSelection(SelectionEvent<IOption> event) {
				content.setWidget( noLowLevel );
				noLowLevel.run();
			}
		});

		EmptyEntryFinder empty = new EmptyEntryFinder(getDomainName(),getUser(),domain);
		checksGrid.addRow().addCell(empty.getSidebarWidget());
		empty.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
			@Override
			public void onSelection(SelectionEvent<IOption> event) {
				content.setWidget( empty );
				empty.run();
			}
		});
		
		UnbalancedEntryFinder unbalanced = new UnbalancedEntryFinder(getDomainName(),getUser(),domain);
		checksGrid.addRow().addCell(unbalanced.getSidebarWidget());
		unbalanced.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
			@Override
			public void onSelection(SelectionEvent<IOption> event) {
				content.setWidget( unbalanced );
				unbalanced.run();
			}
		});
		
		OutOfDateEntryFinder outOfDate = new OutOfDateEntryFinder(getDomainName(),getUser(),domain);
		checksGrid.addRow().addCell(outOfDate.getSidebarWidget());
		outOfDate.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
			@Override
			public void onSelection(SelectionEvent<IOption> event) {
				content.setWidget( outOfDate );
				outOfDate.run();
			}
		});
		
		WrongRecordedInvoices  wrongInvoices = new WrongRecordedInvoices(getDomainName(),getUser(),domain);
		checksGrid.addRow().addCell(wrongInvoices.getSidebarWidget());
		wrongInvoices.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
			@Override
			public void onSelection(SelectionEvent<IOption> event) {
				content.setWidget( wrongInvoices );
				wrongInvoices.run();
			}
		});

		if (!domain.isParent()) {
			InvoiceIntegrityCheck  invoiceIntegrityCheck = new InvoiceIntegrityCheck(getDomainName(),getUser(),domain);
			checksGrid.addRow().addCell(invoiceIntegrityCheck.getSidebarWidget());
			invoiceIntegrityCheck.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
				@Override
				public void onSelection(SelectionEvent<IOption> event) {
					content.setWidget( invoiceIntegrityCheck );
					invoiceIntegrityCheck.run();
				}
			});

			DomainIntegrityCheck domainIntegrity = new DomainIntegrityCheck(getDomainName(),getUser(),domain);
			checksGrid.addRow().addCell(domainIntegrity.getSidebarWidget());
			domainIntegrity.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
				@Override
				public void onSelection(SelectionEvent<IOption> event) {
					content.setWidget( domainIntegrity );
					domainIntegrity.run();
				}
			});
		}

		AonDisplayTable utilitiesGrid = new AonDisplayTable();
		utilitiesGrid.addStyleName(AON.CSS.aonMarginTop());
		sidebarMenu.add(utilitiesGrid);
		utilitiesGrid.addRow().addCell(new InlineLabel("UTILIDADES"), AON.CSS.aonBold(), AON.CSS.aonTextUnderline());
		

		if (!domain.isParent()) {
			AccountRegistryChecker archecker = new AccountRegistryChecker(getDomainName(),getUser(),domain);
			utilitiesGrid.addRow().addCell(archecker.getSidebarWidget());
			archecker.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
				@Override
				public void onSelection(SelectionEvent<IOption> event) {
					content.setWidget( archecker );			
				}
			});
			
			JournalRegenerator journalRegenerator = new JournalRegenerator(getDomainName(),getUser(),domain);
			utilitiesGrid.addRow().addCell(journalRegenerator.getSidebarWidget());
			journalRegenerator.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
				@Override
				public void onSelection(SelectionEvent<IOption> event) {
					content.setWidget( journalRegenerator );
					journalRegenerator.run();
				}
			});
			
			AccountChanger  accountChanger = new AccountChanger(getDomainName(),getUser(),domain);
			utilitiesGrid.addRow().addCell(accountChanger.getSidebarWidget());
			accountChanger.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
			  @Override
			  public void onSelection(SelectionEvent<IOption> event) {
			    content.setWidget( accountChanger );
			  }
			});

			EntriesRemover entriesRemover = new EntriesRemover(getDomainName(),getUser(),domain);
			utilitiesGrid.addRow().addCell(entriesRemover.getSidebarWidget());
			entriesRemover.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
			  @Override
			  public void onSelection(SelectionEvent<IOption> event) {
			    content.setWidget( entriesRemover );
			    // entriesRemover.run();
			  }
			});

			InputVatRegenerator inputVatRegenerator = new InputVatRegenerator(getDomainName(),getUser(),domain);
			utilitiesGrid.addRow().addCell(inputVatRegenerator.getSidebarWidget());
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
			utilitiesGrid.addRow().addCell(linker.getSidebarWidget());
			linker.addSelectionHandler( new SelectionHandler<AccountingUtilities.IOption>() {
				@Override
				public void onSelection(SelectionEvent<IOption> event) {
					content.setWidget( linker );			
				}
			});
		}
		dockLayoutPanel.add( content );
		return dockLayoutPanel; 
	}
	
}

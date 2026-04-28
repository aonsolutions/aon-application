package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.occam.api.model.Agreement;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.calendar.Calendar;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.scope.ScopeParams;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class WorkplaceTable extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(WorkplaceTable.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private String domainName;
	private Integer domain;
	private String user;
	private Integer registry;
	
	private List<Agreement> agreements;
	private List<Activity> activities;
	private List<Calendar> calendars;
	private List<RegistryAddress> addresses;
	private List<Scope> scopes;
	
	private static enum COLS {
		  STA(AON.MSG.status()					   	,"3rem", ""  )
		, DES(AON.MSG.description()					,"-moz-available", "min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" )
		, ADD(AON.MSG.address()						,"6rem", "max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"  )
		, ECO("C. Econ\u00f3mico"					,"6rem", "max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"  )
		, SCO(AON.MSG.scope()						,"6rem", "max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"  )
		, CAL("Calendario"							,"6rem", "max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"  )
		, AGR("Convenio"							,"6rem", "max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"  )
		, ACT(AON.MSG.activity()					,"6rem", "max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"  )
		, BUT(AonStringUtils.EMPTY					,"3rem", ""  )
		;

		String headerLabel;
		String colWidth;
		String styles;

		private COLS(String headerLabel,String colWidth,String styles) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.styles = styles;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getStyles() {
			return styles;
		}
	}
	
	public WorkplaceTable(String domainName, int domain, String user, Integer registry) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.registry = registry;
		
		this.agreements = new ArrayList<Agreement>();
		this.activities = new ArrayList<Activity>();
		this.calendars = new ArrayList<Calendar>();
		this.addresses = new ArrayList<RegistryAddress>();
		this.scopes = new ArrayList<Scope>();
		
		container = new SimplePanel();
		container.getElement().getStyle().setProperty("max-height", "200px");
		container.getElement().getStyle().setProperty("padding-left", "1px");
		setWidget(container);
		
		addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = getWidget().getOffsetHeight() - getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						searchData();
					}
				}
			}
		});
		
		getContextInfo(end -> {
			onSearch();
		});
		
	}

	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	public void disableSearch() {
		searchEnabled.setValue(-1);
	}
	public void enableSearch() {
		searchEnabled.setValue(0);
	}
	public boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	public void disableMoreData() {
		moreData.setValue(-1);
	}
	public void enableMoreData() {
		moreData.setValue(0);
	}
	
	public void onSearch() {
		enableMoreData();
		search();
	}

	private void search() {
		container.clear();
		tab = new AonCustomTable();
		tab.setMaxHeight("160x");
		scrollPanel = new ScrollPanel(tab);
		
		paintHeader();
		container.setWidget(scrollPanel);
		searchData();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) {
			if(col.equals(COLS.BUT)) {
				FlowPanel buttonContainer = new FlowPanel();
				buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
				
				AonTableButton button = new AonTableButton("Nuevo Centro Trabajo", AON.CSS.aonIconAdd());
				button.addStyleName(AON.CSS.aonCustomRowButtom());
				button.addClickHandler(e -> createWorkplace());
				buttonContainer.add(button);
				
				tab.addHeader(buttonContainer, col.getColWidth(), col.getStyles());
			} else
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
		
		}
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		getList(workplaces -> {
			boolean something = false;
			
			for(Workplace workplace : workplaces) {
				something = true;
				paintRow(workplace);
			}
			
			if (workplaces.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + workplaces.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				container.clear();
				container.add(line);
				disableMoreData();
			}
			enableSearch();
			
		});
	}
	
	private void paintRow(Workplace workplace) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		
		AonTableButton button;
		button = new AonTableButton("Borrar Centro Trabajo", AON.CSS.aonIconDelete());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				button.setEnabled(false);
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Media",
						new HTML("Se va a proceder a eliminar el centro de trabajo <b>" + workplace.getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						button.setEnabled(true);
					}

					@Override
					public void onAccept() {
						delete(workplace);
					}
				});
			}
		});
		buttonContainer.add(button);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onUpdateWorkplace(workplace), ClickEvent.getType());
		
		AonTableButton status = workplace.isActive() ? new AonTableButton("Activo", AON.CSS.aonIconCheckCircle()) : new AonTableButton("Inactivo", AON.CSS.aonIconBlock());
		status.setTitle(workplace.isActive() ? "Activo" : "Inactivo");
		tab.addInlineStyle(status, COLS.STA.getStyles());
		tab.addRow(row, status, COLS.STA.getColWidth());
		
		Label description = new Label(workplace.getDescription());
		description.setTitle(workplace.getDescription());
		tab.addInlineStyle(description, COLS.DES.getStyles());
		tab.addRow(row, description, COLS.DES.getColWidth());
		
		Label address = new Label();
		if(null != workplace.getAddress()) {
			Optional<RegistryAddress> addressOpt = this.addresses.stream().filter(a -> a.getId().equals(workplace.getAddress())).findFirst();
			if(addressOpt.isPresent()) {
				address = new Label(addressOpt.get().getFullAddress());
				address.setTitle(addressOpt.get().getFullAddress());
			}
		}
		
		tab.addInlineStyle(address, COLS.ADD.getStyles());
		tab.addRow(row, address, COLS.ADD.getColWidth());
		
		Label aconomicConcert = new Label(null == workplace.getEconomicAgreement() ? "" : workplace.getEconomicAgreement().getDescription());
		aconomicConcert.setTitle(null == workplace.getEconomicAgreement() ? "" : workplace.getEconomicAgreement().getDescription());
		tab.addInlineStyle(aconomicConcert, COLS.ECO.getStyles());
		tab.addRow(row, aconomicConcert, COLS.ECO.getColWidth());
		
		Label scope = new Label();
		if(null != workplace.getScope()) {
			Optional<Scope> scopeOpt = this.scopes.stream().filter(s -> s.getId().equals(workplace.getScope())).findFirst();
			if(scopeOpt.isPresent()) {
				scope = new Label(scopeOpt.get().getDescription());
				scope.setTitle(scopeOpt.get().getDescription());
			}
		}
		
		tab.addInlineStyle(scope, COLS.SCO.getStyles());
		tab.addRow(row, scope, COLS.SCO.getColWidth());
		
		Label calendar = new Label();
		if(null != workplace.getPayrollWorkplace() && null != workplace.getPayrollWorkplace().getCalendar()) {
			Optional<Calendar> calendarOpt = this.calendars.stream().filter(c -> c.getId().equals(workplace.getPayrollWorkplace().getCalendar())).findFirst();
			if(calendarOpt.isPresent()) {
				calendar = new Label(calendarOpt.get().getDescription());
				calendar.setTitle(calendarOpt.get().getDescription());
			}
		}
		
		tab.addInlineStyle(calendar, COLS.CAL.getStyles());
		tab.addRow(row, calendar, COLS.CAL.getColWidth());
		
		Label agreement = new Label();
		if(null != workplace.getPayrollWorkplace() && null != workplace.getPayrollWorkplace().getAgreement()) {
			Optional<Agreement> agreementOpt = this.agreements.stream().filter(a -> a.getId().equals(workplace.getPayrollWorkplace().getAgreement())).findFirst();
			if(agreementOpt.isPresent()) {
				agreement = new Label(agreementOpt.get().getDescription());
				agreement.setTitle(agreementOpt.get().getDescription());
			}
		}
		
		tab.addInlineStyle(agreement, COLS.AGR.getStyles());
		tab.addRow(row, agreement, COLS.AGR.getColWidth());
		
		Label activity = new Label();
		if(null != workplace.getPayrollWorkplace() && null != workplace.getPayrollWorkplace().getEnterpriseActivity()) {
			Optional<Activity> activityOpt = this.activities.stream().filter(a -> a.getId().equals(workplace.getPayrollWorkplace().getEnterpriseActivity())).findFirst();
			if(activityOpt.isPresent()) {
				activity = new Label(activityOpt.get().getDescription());
				activity.setTitle(activityOpt.get().getDescription());
			}
		}
		
		tab.addInlineStyle(activity, COLS.ACT.getStyles());
		tab.addRow(row, activity, COLS.ACT.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}
	
	private void getList(Consumer<List<Workplace>> success) {
		COMMON_SERVICE.getWorkplaces(domainName, domain, user, registry, new AsyncCallback<List<Workplace>>() {
			
			@Override
			public void onSuccess(List<Workplace> Workplaces) {
				success.accept(Workplaces);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void delete(Workplace workplace ) {
		COMMON_SERVICE.deleteWrokplace(domainName, domain, user, workplace.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				onSearch();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado: " + caught.getMessage());
			}
		});
	}
	
	private void getContextInfo(Consumer<Void> end) {
		COMMON_SERVICE.getActivities(domainName, domain, user, new AsyncCallback<List<Activity>>() {

			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error actividades: " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<Activity> activitiesDB) {
				activities = activitiesDB;
				
				COMMON_SERVICE.getCalendars(domainName, domain, user, new AsyncCallback<List<Calendar>>() {

					@Override
					public void onFailure(Throwable caught) {
						onShowErrorMessage("Error calendarios: " + caught.getMessage());
					}

					@Override
					public void onSuccess(List<Calendar> calendarsDB) {
						calendars = calendarsDB;
						
						COMMON_SERVICE.getAcgreements(domainName, domain, user, new AsyncCallback<List<Agreement>>() {

							@Override
							public void onFailure(Throwable caught) {
								onShowErrorMessage("Error convenios: " + caught.getMessage());
							}

							@Override
							public void onSuccess(List<Agreement> agreementsDB) {
								agreements = agreementsDB;
								
								COMMON_SERVICE.getRegistryAddresses(domainName, domain, user, registry, new AsyncCallback<List<RegistryAddress>>() {
									
									@Override
									public void onFailure(Throwable caught) {
										onShowErrorMessage("Error direcciones: " + caught.getMessage());
									}
									
									@Override
									public void onSuccess(List<RegistryAddress> addressesDB) {
										addresses = addressesDB;
										
										ScopeParams params = new ScopeParams()
												.setDomainName(domainName)
												.setDomain(domain)
												.setUser(user)
												.setOffset(0)
												.setLimit(Integer.MAX_VALUE);
										
										COMMON_SERVICE.getScopeList(params, new AsyncCallback<List<Scope>>() {
											
											@Override
											public void onFailure(Throwable caught) {
												onShowErrorMessage("Error direcciones: " + caught.getMessage());
											}
											
											@Override
											public void onSuccess(List<Scope> scopesDB) {
												scopes = scopesDB;
												
												end.accept(null);
											}
											
										});
									}
									
								});
								
							}
							
						});
						
					}
					
				});
				
			}
			
		});
	}
	
	private void onUpdateWorkplace(Workplace workplace) {
//		final AonCustomDialog dialog = new AonCustomDialog();
//		dialog.setCaption("Editar Centro Trabajo");
//		
//		final AonMediaPanel marketingCampaignPanel = new AonMediaPanel( domainName, domain, user, registryMedia, new AonMediaPanelCallback() {
//			
//			@Override
//			public void onCancel() {
//				dialog.hide();
//			}
//			
//			@Override
//			public void onAccept(RegistryMedia media) {
//				dialog.hide();
//				onSearch();
//			}
//		});
//		
//		dialog.add( marketingCampaignPanel );
//		dialog.showLoaded();
	}
	


	private void createWorkplace() {
		/*
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption("Nuevo Centro Trabajo");

		final AonMediaPanel marketingCampaignPanel = new AonMediaPanel(domainName, domain, user, registry, new AonMediaPanelCallback() {

					@Override
					public void onCancel() {
						dialog.hide();
					}

					@Override
					public void onAccept(RegistryMedia media) {
						dialog.hide();
						onSearch();
					}
				});

		dialog.add(marketingCampaignPanel);
		dialog.showLoaded();
		*/
	}

	protected abstract void onShowErrorMessage(String errorMessage);
	
}


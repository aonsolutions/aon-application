package com.esferalia.aon.gwt.fiscal.client.mod200;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.ContextMenu;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2013.Mod2002013Object;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2013.Model2002013;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2014.Mod2002014Object;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2014.Model2002014;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2015.Mod2002015Object;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2015.Model2002015;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Mod2002016Object;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Model2002016;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model200 extends MainEntryPoint {
	
	static Mod2002013ServiceAsync mod2002013Service;
	static Mod2002014ServiceAsync mod2002014Service;
	static Mod2002015ServiceAsync mod2002015Service;
	static Mod2002016ServiceAsync mod2002016Service;
	static FiscalServiceAsync fiscalService;
	static CommonServiceAsync commonService;
	
	Model200Table table;
	DeckLayoutPanel deckPanel;
	SimpleLayoutPanel container;
	
	public static FiscalServiceAsync getFiscalService() {
		if (fiscalService == null) {
			FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
			fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		}
		return fiscalService;
	}
	
	public static CommonServiceAsync getCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
		return commonService;
	}
	
	public static Mod2002016ServiceAsync getMod2002016Service() {
		if (mod2002016Service == null) {
			Mod2002016ServiceAsync mod2002016ServiceRaw = GWT.create(Mod2002016Service.class);
			mod2002016Service = new Mod2002016ServiceAsyncDecorator(mod2002016ServiceRaw);
		}
		return mod2002016Service;
	}

	public static Mod2002015ServiceAsync getMod2002015Service() {
		if (mod2002015Service == null) {
			Mod2002015ServiceAsync mod2002015ServiceRaw = GWT.create(Mod2002015Service.class);
			mod2002015Service = new Mod2002015ServiceAsyncDecorator(mod2002015ServiceRaw);
		}
		return mod2002015Service;
	}

	public static Mod2002014ServiceAsync getMod2002014Service() {
		if (mod2002014Service == null) {
			Mod2002014ServiceAsync mod2002014ServiceRaw = GWT.create(Mod2002014Service.class);
			mod2002014Service = new Mod2002014ServiceAsyncDecorator(mod2002014ServiceRaw);
		}
		return mod2002014Service;
	}

	public static Mod2002013ServiceAsync getMod2002013Service() {
		if (mod2002013Service == null) {
			Mod2002013ServiceAsync mod2002013ServiceRaw = GWT.create(Mod2002013Service.class);
			mod2002013Service = new Mod2002013ServiceAsyncDecorator(mod2002013ServiceRaw);
		}
		return mod2002013Service;
	}

	public class Model200Callback {
		public void canceled() {
			container.clear();
			table.setVisibleRangeAndClearData(table.getVisibleRange(), true);	
		}
		public void removed() {
			table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
		}
	}

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();


		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		deckPanel = new DeckLayoutPanel();

		DockLayoutPanel tableDockLayout = new DockLayoutPanel(Unit.PX);
		tableDockLayout.addNorth(getToolbarPanel(), 25);
		
		ScrollPanel tablePanel = new ScrollPanel();
		tablePanel.addStyleName(AON.AON_CSS.aonScrollArea());
		
		
		ProvidesKey<Mod200> providesKey = new ProvidesKey<Mod200>() {
			@Override
			public Object getKey(Mod200 model) {
				return model == null ? null : model.getId();
			}
		};
		table = new Model200Table(providesKey);
		NoSelectionModel<Mod200> tableModel = new NoSelectionModel<Mod200>(providesKey);
		table.setSelectionModel(tableModel);
		tableModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				changeView( tableModel.getLastSelectedObject() );
			}
		});
		
		table.addRangeChangeHandler( new RangeChangeEvent.Handler() {
		
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				getFiscalService().getMod200s(getCurrentDomainName(), getCurrentDomain(),
				new AsyncCallback<LinkedList<Mod200>>() {
					@Override
					public void onSuccess(LinkedList<Mod200> result) {
						int i = deckPanel.getWidgetIndex(tableDockLayout);
						table.setRowData(result);
						deckPanel.showWidget(i);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(AON.MSG.unableToReadDeclaration(AonStringUtils.abbreviate(caught.getMessage(), 300)));
					}
				});

			}
		});
		tablePanel.setWidget(table);
		tableDockLayout.add(tablePanel);
		deckPanel.add(tableDockLayout);
		
		container = new SimpleLayoutPanel();
		deckPanel.add(container);
		
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
		
		root.add(deckPanel);
		
	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
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
		toolbar.setWidget(0, 0, new Label( "Mod. 200"));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		
		final Button new2016 = new Button();
		new2016.setText(AON.MSG.newSomething("2016"));
		new2016.setTitle(new2016.getText());
		new2016.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		new2016.addStyleName(AON.AON_CSS.aonIconDelete());
		new2016.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				new2016();
			}
		});
		buttonContainer.add(new2016);
		
		final NewContextMenu newContextMenu = new NewContextMenu();
		final Button newButton = new Button();
		newButton.setText(AON.MSG.newAction());
		newButton.setTitle(newButton.getText());
		newButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		newButton.addStyleName(AON.AON_CSS.aonIconDelete());
		newButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				NativeEvent nativeEvent = event.getNativeEvent();
				newContextMenu.setPopupPosition(nativeEvent.getClientX(),
						nativeEvent.getClientY());
				newContextMenu.show();
			}
		});
		buttonContainer.add(newButton);

		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	
	private class NewContextMenu extends ContextMenu {
		public NewContextMenu() {
			super.addItem("200", AON.MSG.newSomething("2013"), new ScheduledCommand() {
				
				@Override
				public void execute() {
					new2013();
				}
			});
			super.addItem("200", AON.MSG.newSomething("2014"), new ScheduledCommand() {
				
				@Override
				public void execute() {
					new2014();
				}
			});
			super.addItem("200", AON.MSG.newSomething("2015"), new ScheduledCommand() {
				
				@Override
				public void execute() {
					new2015();
				}
			});
			addStyleName(AON.AON_CSS.aonSelector());
		}
	}


	protected void changeView(Mod200 mod) {
		if (mod.getYear() == 2013) {
			getMod2002013Service().getMod2002013ById(Model200.getCurrentDomainName()
					, getCurrentDomain(), mod.getId()
					, new AsyncCallback<Mod2002013>() {

						@Override
						public void onSuccess(Mod2002013 mod200) {
							changeView2013(mod200);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		} else if (mod.getYear() == 2014) {
			getMod2002014Service().getMod2002014ById(Model200.getCurrentDomainName()
					, getCurrentDomain(), mod.getId()
					, new AsyncCallback<Mod2002014>() {

						@Override
						public void onSuccess(Mod2002014 mod200) {
							changeView2014(mod200);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		} else if (mod.getYear() == 2015) {
			getMod2002015Service().getMod2002015ById(Model200.getCurrentDomainName()
					, getCurrentDomain(), mod.getId()
					, new AsyncCallback<Mod2002015>() {

						@Override
						public void onSuccess(Mod2002015 mod200) {
							changeView2015(mod200);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		} else if (mod.getYear() == 2016) {
			getMod2002016Service().getMod2002016ById(Model200.getCurrentDomainName()
					, getCurrentDomain(), mod.getId()
					, new AsyncCallback<Mod2002016>() {

						@Override
						public void onSuccess(Mod2002016 mod200) {
							changeView2016(mod200);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		} else {
			Window.alert(AON.MSG.unableToShowData( AON.MSG.noModuleForYear()));
		}
	}

	private void changeView2013(Mod2002013 mod200) {
		Mod2002013Object mod200Obj = new Mod2002013Object(getCurrentDomainName(), mod200);
		Model2002013 model2002013 = new Model2002013( new Model200Callback() );
		model2002013.startModel( mod200Obj);
		container.setWidget(model2002013);
		int i = deckPanel.getWidgetIndex(container);
		deckPanel.showWidget(i);
	}

	private void changeView2014(Mod2002014 mod200) {
		Mod2002014Object mod200Obj = new Mod2002014Object(getCurrentDomainName(), mod200);
		Model2002014 model2002014 = new Model2002014( new Model200Callback() );
		model2002014.startModel( mod200Obj);
		container.setWidget(model2002014);
		int i = deckPanel.getWidgetIndex(container);
		deckPanel.showWidget(i);
	}

	private void changeView2015(Mod2002015 mod200) {
		Mod2002015Object mod200Obj = new Mod2002015Object(getCurrentDomainName(), mod200);
		Model2002015 model2002015 = new Model2002015( new Model200Callback() );
		model2002015.startModel( mod200Obj );
		container.setWidget(model2002015);
		int i = deckPanel.getWidgetIndex(container);
		deckPanel.showWidget(i);
	}

	private void changeView2016(Mod2002016 mod200) {
		Mod2002016Object mod200Obj = new Mod2002016Object(getCurrentDomainName(), mod200);
		Model2002016 model2002016 = new Model2002016( new Model200Callback() );
		model2002016.startModel( mod200Obj );
		container.setWidget(model2002016);
		int i = deckPanel.getWidgetIndex(container);
		deckPanel.showWidget(i);
	}

	protected void new2013() {
		getMod2002013Service().createMod2002013(Model200.getCurrentDomainName()
		, getCurrentDomain(), 2013
		, new AsyncCallback<Mod2002013>() {

			@Override
			public void onSuccess(Mod2002013 mod200) {
				changeView2013(mod200);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	protected void new2014() {
		getMod2002014Service().createMod2002014(Model200.getCurrentDomainName()
		, getCurrentDomain(), 2014
		, new AsyncCallback<Mod2002014>() {

			@Override
			public void onSuccess(Mod2002014 mod200) {
				changeView2014(mod200);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	protected void new2015() {
		getMod2002015Service().createMod2002015(Model200.getCurrentDomainName()
		, getCurrentDomain(), 2015
		, new AsyncCallback<Mod2002015>() {

			@Override
			public void onSuccess(Mod2002015 mod200) {
				changeView2015(mod200);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	protected void new2016() {
		getMod2002016Service().createMod2002016(Model200.getCurrentDomainName()
		, getCurrentDomain(), 2016
		, new AsyncCallback<Mod2002016>() {

			@Override
			public void onSuccess(Mod2002016 mod200) {
				changeView2016(mod200);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
}

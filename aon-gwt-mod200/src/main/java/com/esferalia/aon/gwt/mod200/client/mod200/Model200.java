package com.esferalia.aon.gwt.mod200.client.mod200;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.mod200.client.IFiscalModelCallback;
import com.esferalia.aon.gwt.mod200.client.MainEntryPoint;
import com.esferalia.aon.gwt.mod200.client.mod200.e2013.Mod2002013Object;
import com.esferalia.aon.gwt.mod200.client.mod200.e2013.Mod2002013Service;
import com.esferalia.aon.gwt.mod200.client.mod200.e2013.Mod2002013ServiceAsync;
import com.esferalia.aon.gwt.mod200.client.mod200.e2013.Mod2002013ServiceAsyncDecorator;
import com.esferalia.aon.gwt.mod200.client.mod200.e2013.Model2002013;
import com.esferalia.aon.gwt.mod200.client.mod200.e2014.Mod2002014Object;
import com.esferalia.aon.gwt.mod200.client.mod200.e2014.Mod2002014Service;
import com.esferalia.aon.gwt.mod200.client.mod200.e2014.Mod2002014ServiceAsync;
import com.esferalia.aon.gwt.mod200.client.mod200.e2014.Mod2002014ServiceAsyncDecorator;
import com.esferalia.aon.gwt.mod200.client.mod200.e2014.Model2002014;
import com.esferalia.aon.gwt.mod200.client.mod200.e2015.Mod2002015Object;
import com.esferalia.aon.gwt.mod200.client.mod200.e2015.Mod2002015Service;
import com.esferalia.aon.gwt.mod200.client.mod200.e2015.Mod2002015ServiceAsync;
import com.esferalia.aon.gwt.mod200.client.mod200.e2015.Mod2002015ServiceAsyncDecorator;
import com.esferalia.aon.gwt.mod200.client.mod200.e2015.Model2002015;
import com.esferalia.aon.gwt.mod200.client.mod200.e2016.Mod2002016Object;
import com.esferalia.aon.gwt.mod200.client.mod200.e2016.Mod2002016Service;
import com.esferalia.aon.gwt.mod200.client.mod200.e2016.Mod2002016ServiceAsync;
import com.esferalia.aon.gwt.mod200.client.mod200.e2016.Mod2002016ServiceAsyncDecorator;
import com.esferalia.aon.gwt.mod200.client.mod200.e2016.Model2002016;
import com.esferalia.aon.gwt.mod200.client.mod200.e2017.Mod2002017Object;
import com.esferalia.aon.gwt.mod200.client.mod200.e2017.Mod2002017Service;
import com.esferalia.aon.gwt.mod200.client.mod200.e2017.Mod2002017ServiceAsync;
import com.esferalia.aon.gwt.mod200.client.mod200.e2017.Mod2002017ServiceAsyncDecorator;
import com.esferalia.aon.gwt.mod200.client.mod200.e2017.Model2002017;
import com.esferalia.aon.gwt.mod200.client.mod200.e2018.Mod2002018Object;
import com.esferalia.aon.gwt.mod200.client.mod200.e2018.Mod2002018Service;
import com.esferalia.aon.gwt.mod200.client.mod200.e2018.Mod2002018ServiceAsync;
import com.esferalia.aon.gwt.mod200.client.mod200.e2018.Mod2002018ServiceAsyncDecorator;
import com.esferalia.aon.gwt.mod200.client.mod200.e2018.Model2002018;
import com.esferalia.aon.gwt.mod200.client.mod200.e2019.Mod2002019Object;
import com.esferalia.aon.gwt.mod200.client.mod200.e2019.Mod2002019Service;
import com.esferalia.aon.gwt.mod200.client.mod200.e2019.Mod2002019ServiceAsync;
import com.esferalia.aon.gwt.mod200.client.mod200.e2019.Mod2002019ServiceAsyncDecorator;
import com.esferalia.aon.gwt.mod200.client.mod200.e2019.Model2002019;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Mod2002020Service;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Mod2002020ServiceAsync;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Mod2002020ServiceAsyncDecorator;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Mod2002021Service;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Mod2002021ServiceAsync;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Mod2002021ServiceAsyncDecorator;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Model2002021;
import com.esferalia.aon.gwt.mod200.client.mod200.e2022.Mod2002022Service;
import com.esferalia.aon.gwt.mod200.client.mod200.e2022.Mod2002022ServiceAsync;
import com.esferalia.aon.gwt.mod200.client.mod200.e2022.Mod2002022ServiceAsyncDecorator;
import com.esferalia.aon.gwt.mod200.client.mod200.e2022.Model2002022;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.Mod2002023Service;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.Mod2002023ServiceAsync;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.Mod2002023ServiceAsyncDecorator;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.Model2002023;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.mod200.api.model.Mod200;
import com.esferalia.aon.occam.mod200.api.model.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.mod200.api.model.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.mod200.api.model.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.mod200.api.model.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.mod200.api.model.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.mod200.api.model.mod200_2018.Mod2002018;
import com.esferalia.aon.occam.mod200.api.model.mod200_2019.Mod2002019;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model200 extends MainEntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(Model200.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	public static final Mod200ServiceAsync MOD200_SERVICE;
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		Mod200ServiceAsync serviceRaw = GWT.create(Mod200Service.class);
		MOD200_SERVICE = new Mod200ServiceAsyncDecorator(serviceRaw);
	}
	
	static Mod2002013ServiceAsync mod2002013Service;
	static Mod2002014ServiceAsync mod2002014Service;
	static Mod2002015ServiceAsync mod2002015Service;
	static Mod2002016ServiceAsync mod2002016Service;
	static Mod2002017ServiceAsync mod2002017Service;
	static Mod2002018ServiceAsync mod2002018Service;
	static Mod2002019ServiceAsync mod2002019Service;
	static Mod2002020ServiceAsync mod2002020Service;
	static Mod2002021ServiceAsync mod2002021Service;
	static Mod2002022ServiceAsync mod2002022Service;
	static Mod2002023ServiceAsync mod2002023Service;
	
	private Model200ModuleOptions options;

	private AonLayoutPanel aonLayout;
	private SimpleLayoutPanel declarationContainer;
	
	private Model200Table model200Table;
	
	public static Mod2002023ServiceAsync getMod2002023Service() {
		if (mod2002023Service == null) {
			Mod2002023ServiceAsync mod2002023ServiceRaw = GWT.create(Mod2002023Service.class);
			mod2002023Service = new Mod2002023ServiceAsyncDecorator(mod2002023ServiceRaw);
		}
		return mod2002023Service;
	}
	
	public static Mod2002022ServiceAsync getMod2002022Service() {
		if (mod2002022Service == null) {
			Mod2002022ServiceAsync mod2002022ServiceRaw = GWT.create(Mod2002022Service.class);
			mod2002022Service = new Mod2002022ServiceAsyncDecorator(mod2002022ServiceRaw);
		}
		return mod2002022Service;
	}
	
	public static Mod2002021ServiceAsync getMod2002021Service() {
		if (mod2002021Service == null) {
			Mod2002021ServiceAsync mod2002021ServiceRaw = GWT.create(Mod2002021Service.class);
			mod2002021Service = new Mod2002021ServiceAsyncDecorator(mod2002021ServiceRaw);
		}
		return mod2002021Service;
	}	

	public static Mod2002020ServiceAsync getMod2002020Service() {
		if (mod2002020Service == null) {
			Mod2002020ServiceAsync mod2002020ServiceRaw = GWT.create(Mod2002020Service.class);
			mod2002020Service = new Mod2002020ServiceAsyncDecorator(mod2002020ServiceRaw);
		}
		return mod2002020Service;
	}	

	public static Mod2002019ServiceAsync getMod2002019Service() {
		if (mod2002019Service == null) {
			Mod2002019ServiceAsync mod2002019ServiceRaw = GWT.create(Mod2002019Service.class);
			mod2002019Service = new Mod2002019ServiceAsyncDecorator(mod2002019ServiceRaw);
		}
		return mod2002019Service;
	}
	
	public static Mod2002018ServiceAsync getMod2002018Service() {
		if (mod2002018Service == null) {
			Mod2002018ServiceAsync mod2002018ServiceRaw = GWT.create(Mod2002018Service.class);
			mod2002018Service = new Mod2002018ServiceAsyncDecorator(mod2002018ServiceRaw);
		}
		return mod2002018Service;
	}

	public static Mod2002017ServiceAsync getMod2002017Service() {
		if (mod2002017Service == null) {
			Mod2002017ServiceAsync mod2002017ServiceRaw = GWT.create(Mod2002017Service.class);
			mod2002017Service = new Mod2002017ServiceAsyncDecorator(mod2002017ServiceRaw);
		}
		return mod2002017Service;
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

	public class Model200Callback implements IFiscalModelCallback<Mod200, Model200ModuleOptions> {
		
		// LO PONGO POR AHORA PARA COMPILAR LOS Model200xxxx anteriores
		public void canceled() {
			cleanErrorMessage();
			declarationContainer.setWidget(model200Table);
			model200Table.refresh( new Model200Callback() );
		}
		public void removed() {
			canceled();
	    }
		// -------------------------------------------------
		
		@Override
		public Model200ModuleOptions getOptions() {
			return Model200.this.options;
		}

		@Override
		public void onAccept(Mod200 mod200) {
			// 
		}

		@Override
		public void onRemove(Mod200 model) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onRemove(model);
			} else {
				onCancel(model);
			}
		}
		
		@Override
		public void onCancel(Mod200 mod200) {
			if (getOptions().isBackButtonVisible() && getOptions().hasExternalCallback()) {
				getOptions().getExternalCallback().onExit(mod200);
			} else {
				cleanErrorMessage();
				declarationContainer.setWidget(model200Table);
				model200Table.refresh( new Model200Callback() );
			}
		}
		
		@Override
		public void onNew() {					
		}
		public void onNew(int year) {
			newModel(getOptions(), year);			
		}

		@Override
		public void showError(String msg) {
			showErrorMessage(msg);
		}
		
		@Override
		public void hideError() {
			aonLayout.hideErrorPanel();
		}
		
		@Override
		public void showInfoPanel(String htmlText) {
		}

		@Override
		public void cleanInfoPanel() {
		}

		public void reset(Mod200 mod200) {
			// El botón inicializar, se utiliza a partir del 2020
			switch (mod200.getYear()) {
				case 2020:
					changeView2020((Mod2002020) mod200);
					break;			
				case 2021:
					changeView2021((Mod2002021) mod200);
					break;
			    case 2022:
					changeView2022((Mod2002022) mod200);
					break;
				case 2023:
					changeView2023((Mod2002023) mod200);
					break;					
			}
		}
		
		public void cleanErrorPanel() {
			cleanErrorMessage();
		}
		
		private void cleanErrorMessage() {
			aonLayout.hideErrorPanel();
		}
		
	}
	
//	private void cleanErrorMessage() {
//		aonLayout.hideErrorPanel();
//	}
	
	private void showErrorMessage(String msg) {
		aonLayout.showErrorPanel(msg);
	}
	
	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonConfiguration>() {
			
			@Override
			public void onSuccess(AonConfiguration config) {
				RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
				Model200ModuleOptions opts = new Model200ModuleOptions();
				opts.setParentWidget(root);
				opts.setDomainName(getCurrentDomainName());
				opts.setDomain(getCurrentDomain());
				opts.setUser(getCurrentUser());
				opts.setConfiguration(config);
				onModuleLoad( opts );
			}
			
			@Override public void onFailure(Throwable caught) {
				Window.alert( "Error al cargar el module" );
			}
		});
	}
	
	public void onModuleLoad(Model200ModuleOptions options) {
		this.options = options;
		
		AON.ensureInjected();

		aonLayout = new AonLayoutPanel();
		
		declarationContainer = new SimpleLayoutPanel();
		aonLayout.add(declarationContainer);
		
		model200Table = new Model200Table(new Model200Callback());
		model200Table.addSelectionHandler(event -> onSelectionChange(options,event));
		
		declarationContainer.setWidget(model200Table);

		options.getParentWidget().add(aonLayout);
		if (options.getFiscalModelId() != null ) {
			onSelect(options,options.getFiscalModelId());
		} else if (options.getNewModel() != null ) {
			newModel(options,options.getNewModel().getYear()); 
		} else {
			model200Table.refresh(new Model200Callback());
		}
	}
	
	private void onSelectionChange(Model200ModuleOptions options, SelectionEvent<Mod200> event) {
		Mod200 sel = event.getSelectedItem();
		onSelect(options, sel.getId());
	}
	
	private void onSelect(Model200ModuleOptions options, Integer id ) {
		LOGGER.info("OnSelect Model200 with a ID: " + options.getFiscalModelId());
		MOD200_SERVICE.getMod200(options.getOccam(), id, new AsyncCallback<Mod200>() {
			@Override
			public void onSuccess(Mod200 selected) {
				if (selected == null) {
					showErrorMessage(AON.MSG.unableToFindDeclaration());
				} else {
					changeView(options, selected);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(AON.MSG.unableToReadDeclaration(caught.getMessage()));
			}
		});
	}

	protected void changeView(Model200ModuleOptions options, Mod200 mod) {		
		if (mod.getYear() == 2013) {
			getMod2002013Service().getMod2002013ById(options.getDomainName(), options.getDomain(), mod.getId()
					, new AsyncCallback<Mod2002013>() {

						@Override
						public void onSuccess(Mod2002013 mod200) {
							changeView2013(options, mod200);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		} else if (mod.getYear() == 2014) {
			getMod2002014Service().getMod2002014ById(options.getDomainName(), options.getDomain(), mod.getId()
					, new AsyncCallback<Mod2002014>() {

						@Override
						public void onSuccess(Mod2002014 mod200) {
							changeView2014(options, mod200);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		} else if (mod.getYear() == 2015) {
			getMod2002015Service().getMod2002015ById(options.getDomainName(), options.getDomain(), mod.getId()
					, new AsyncCallback<Mod2002015>() {

						@Override
						public void onSuccess(Mod2002015 mod200) {
							changeView2015(options, mod200);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		} else if (mod.getYear() == 2016) {
			getMod2002016Service().getMod2002016ById(options.getDomainName(), options.getDomain(), mod.getId()
					, new AsyncCallback<Mod2002016>() {

						@Override
						public void onSuccess(Mod2002016 mod200) {
							changeView2016(options, mod200);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		} else if (mod.getYear() == 2017) {
			getMod2002017Service().getMod2002017ById(options.getDomainName(), options.getDomain(), mod.getId()
					, new AsyncCallback<Mod2002017>() {

						@Override
						public void onSuccess(Mod2002017 mod200) {
							changeView2017(options, mod200);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		} else if (mod.getYear() == 2018) {
			getMod2002018Service().getMod2002018ById(options.getDomainName(), options.getDomain(), mod.getId()
					, new AsyncCallback<Mod2002018>() {

						@Override
						public void onSuccess(Mod2002018 mod200) {
							changeView2018(options, mod200);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		} else if (mod.getYear() == 2019) {
			getMod2002019Service().getMod2002019ById(options.getDomainName(), options.getDomain(), mod.getId()
					, new AsyncCallback<Mod2002019>() {

						@Override
						public void onSuccess(Mod2002019 mod200) {
							changeView2019(options, mod200);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		} else if (mod.getYear() == 2020) {
			getMod2002020Service().getMod2002020ById(options.getOccam(), mod.getId()
					, new AsyncCallback<Mod2002020>() {

						@Override
						public void onSuccess(Mod2002020 mod200) {
							changeView2020(mod200);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		} else if (mod.getYear() == 2021) {
			getMod2002021Service().getMod2002021ById(options.getOccam(), mod.getId()
					, new AsyncCallback<Mod2002021>() {

						@Override
						public void onSuccess(Mod2002021 mod200) {
							changeView2021(mod200);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		} else if (mod.getYear() == 2022) {
			getMod2002022Service().getMod2002022ById(options.getOccam(), mod.getId()
					, new AsyncCallback<Mod2002022>() {

						@Override
						public void onSuccess(Mod2002022 mod200) {
							changeView2022(mod200);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		} else if (mod.getYear() == 2023) {
			getMod2002023Service().getMod2002023ById(options.getOccam(), mod.getId()
					, new AsyncCallback<Mod2002023>() {

						@Override
						public void onSuccess(Mod2002023 mod200) {
							changeView2023(mod200);
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		}else {
			Window.alert(AON.MSG.unableToShowData(AON.MSG.noModuleForYear()));
		}
		
	}

	private void changeView2013(Model200ModuleOptions options,Mod2002013 mod200) {
		Mod2002013Object mod200Obj = new Mod2002013Object(options.getDomainName(), mod200);
		Model2002013 model2002013 = new Model2002013( options, new Model200Callback() );
		model2002013.startModel( mod200Obj);
		declarationContainer.setWidget(model2002013);
	}

	private void changeView2014(Model200ModuleOptions options,Mod2002014 mod200) {
		Mod2002014Object mod200Obj = new Mod2002014Object(options.getDomainName(), mod200);
		Model2002014 model2002014 = new Model2002014( options, new Model200Callback() );
		model2002014.startModel( mod200Obj);
		declarationContainer.setWidget(model2002014);
	}

	private void changeView2015(Model200ModuleOptions options,Mod2002015 mod200) {
		Mod2002015Object mod200Obj = new Mod2002015Object(options.getDomainName(), mod200);
		Model2002015 model2002015 = new Model2002015( options, new Model200Callback() );
		model2002015.startModel( mod200Obj );
		declarationContainer.setWidget(model2002015);
	}

	private void changeView2016(Model200ModuleOptions options,Mod2002016 mod200) {
		Mod2002016Object mod200Obj = new Mod2002016Object(options.getDomainName(), mod200);
		Model2002016 model2002016 = new Model2002016( options, new Model200Callback() );
		model2002016.startModel( mod200Obj );
		declarationContainer.setWidget(model2002016);
	}

	private void changeView2017(Model200ModuleOptions options,Mod2002017 mod200) {
		Mod2002017Object mod200Obj = new Mod2002017Object(options.getDomainName(), mod200);
		Model2002017 model2002017 = new Model2002017( options, new Model200Callback() );
		model2002017.startModel( mod200Obj );
		declarationContainer.setWidget(model2002017);
	}
	
	private void changeView2018(Model200ModuleOptions options,Mod2002018 mod200) {
		Mod2002018Object mod200Obj = new Mod2002018Object(options.getDomainName(), mod200);
		Model2002018 model2002018 = new Model2002018( options, new Model200Callback() );
		model2002018.startModel( mod200Obj );
		declarationContainer.setWidget(model2002018);
	}
	
	private void changeView2019(Model200ModuleOptions options,Mod2002019 mod200) {
		Mod2002019Object mod200Obj = new Mod2002019Object(options.getDomainName(), mod200);
		Model2002019 model2002019 = new Model2002019( options, new Model200Callback() );
		model2002019.startModel( mod200Obj );
		declarationContainer.setWidget(model2002019);
	}
	
	private void changeView2020(Mod2002020 mod200) {
		declarationContainer.setWidget(new Model2002020(new Model200Callback(), mod200));
	}
	
	private void changeView2021(Mod2002021 mod200) {
		declarationContainer.setWidget(new Model2002021(new Model200Callback(), mod200));
	}
	
	private void changeView2022(Mod2002022 mod200) {
		declarationContainer.setWidget(new Model2002022(new Model200Callback(), mod200));
	}
	private void changeView2023(Mod2002023 mod200) {
		declarationContainer.setWidget(new Model2002023(new Model200Callback(), mod200));
	}
	
	protected void newModel(Model200ModuleOptions options, int year) {
		switch (year) {
			case 2013:
				new2013(options);	
				break;
			case 2014:
				new2014(options);	
				break;
			case 2015:
				new2015(options);	
				break;
			case 2016:
				new2016(options);	
				break;
			case 2017:
				new2017(options);	
				break;
			case 2018:
				new2018(options);	
				break;
			case 2019:
				new2019(options);	
				break;
			case 2020:
				new2020(options);	
				break;
			case 2021:
				new2021(options);	
				break;
			case 2022:
				new2022(options);	
				break;
			case 2023:
				new2023(options);	
				break;
		}		
	}
	
	protected void new2013(Model200ModuleOptions options) {
		getMod2002013Service().createMod2002013(options.getDomainName(), options.getDomain(), 2013
		, new AsyncCallback<Mod2002013>() {

			@Override
			public void onSuccess(Mod2002013 mod200) {
				changeView2013(options,mod200);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	
	protected void new2014(Model200ModuleOptions options) {
		getMod2002014Service().createMod2002014(options.getDomainName(), options.getDomain(), 2014
		, new AsyncCallback<Mod2002014>() {

			@Override
			public void onSuccess(Mod2002014 mod200) {
				changeView2014(options,mod200);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	protected void new2015(Model200ModuleOptions options) {
		getMod2002015Service().createMod2002015(options.getDomainName() , options.getDomain(), 2015
		, new AsyncCallback<Mod2002015>() {

			@Override
			public void onSuccess(Mod2002015 mod200) {
				changeView2015(options,mod200);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	protected void new2016(Model200ModuleOptions options) {
		getMod2002016Service().createMod2002016(options.getDomainName(), options.getDomain(), 2016
		, new AsyncCallback<Mod2002016>() {

			@Override
			public void onSuccess(Mod2002016 mod200) {
				changeView2016(options,mod200);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	protected void new2017(Model200ModuleOptions options) {
		getMod2002017Service().createMod2002017(options.getDomainName(), options.getDomain(), 2017
		, new AsyncCallback<Mod2002017>() {

			@Override
			public void onSuccess(Mod2002017 mod200) {
				changeView2017(options,mod200);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	
	protected void new2018(Model200ModuleOptions options) {
		getMod2002018Service().createMod2002018(options.getDomainName(), options.getDomain(), 2018
		, new AsyncCallback<Mod2002018>() {

			@Override
			public void onSuccess(Mod2002018 mod200) {				
				changeView2018(options, mod200);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	
	protected void new2019(Model200ModuleOptions options) {
		getMod2002019Service().createMod2002019(options.getDomainName(), options.getDomain(), 2019
		, new AsyncCallback<Mod2002019>() {

			@Override
			public void onSuccess(Mod2002019 mod200) {				
				changeView2019(options, mod200);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	
	protected void new2020(Model200ModuleOptions options) {
		getMod2002020Service().createMod2002020(options.getOccam(), 2020
		, new AsyncCallback<Mod2002020>() {

			@Override
			public void onSuccess(Mod2002020 mod200) {				
				changeView2020(mod200);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	
	protected void new2021(Model200ModuleOptions options) {
		getMod2002021Service().createMod2002021(options.getOccam(), 2021
		, new AsyncCallback<Mod2002021>() {

			@Override
			public void onSuccess(Mod2002021 mod200) {				
				changeView2021(mod200);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	
	protected void new2022(Model200ModuleOptions options) {
		getMod2002022Service().createMod2002022(options.getOccam(), 2022
		, new AsyncCallback<Mod2002022>() {

			@Override
			public void onSuccess(Mod2002022 mod200) {				
				changeView2022(mod200);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	
	protected void new2023(Model200ModuleOptions options) {
		getMod2002023Service().createMod2002023(options.getOccam(), 2023
		, new AsyncCallback<Mod2002023>() {

			@Override
			public void onSuccess(Mod2002023 mod200) {				
				changeView2023(mod200);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}	
	
	public static native double resolve(String expression) /*-{
		d = eval(expression);
		return d;
	}-*/;	
	
}

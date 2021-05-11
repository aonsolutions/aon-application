package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.fiscal.JsFiscalMenuItem;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod115.Model115;
import com.esferalia.aon.gwt.fiscal.client.mod115.Model115ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod123.Model123;
import com.esferalia.aon.gwt.fiscal.client.mod123.Model123ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod130.Model130;
import com.esferalia.aon.gwt.fiscal.client.mod130.Model130ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod200.Model200;
import com.esferalia.aon.gwt.fiscal.client.mod200.Model200ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202;
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390ModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.mod390HF.Model390HF;
import com.esferalia.aon.gwt.fiscal.client.mod390HF.Model390HFModuleOptions;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class ModelMatrix extends MainEntryPoint {

	interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"aon_tab_label {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}
	private static final TabLayoutFolderSafeTemplate TABLAYOUT_FOLDER_TEMPLATE = GWT.create(TabLayoutFolderSafeTemplate.class);

	private static final Logger LOGGER = Logger.getLogger(ModelMatrix.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	@FunctionalInterface
	private interface IPeriodTypeAccepter {
		boolean accept(Period mod);
	}
	
	private enum PeriodType {
		 MONTHLY	("Mensual",12,period -> period.isMonthPeriod())
		,QUARTERLY	("Trimes.", 4,period -> period.isQuarterPeriod())
		,YEARLY		("Anual", 1,period -> period == Period.YEAR)
		;
		private String value;
		private int arraySize;
		private IPeriodTypeAccepter accepter;
		private PeriodType( String value, int arraySize, IPeriodTypeAccepter accepter) {
			this.value = value;
			this.arraySize = arraySize;
			this.accepter = accepter;
		}
		private String getValue() {
			return value;
		}
		private int getArraySize() {
			return arraySize;
		}
		private int getIndex(Period period) {
			if (period == Period.YEAR) return 0;
			if (period.isMonthPeriod()) return period.ordinal();
			return (period.ordinal() - 12);
		}
		private static PeriodType getPeriodType(Period period) {
			for (PeriodType type : PeriodType.values()) {
				if (type.accepter.accept(period)) return type;
			}
			return null;
		}
	}
	
	protected static FiscalModelServiceAsync SERVICE;
	final FiscalServiceAsync impl = GWT.create(FiscalService.class);
	
	private SplitLayoutPanel splitLayoutPanel;
	private ScrollPanel scrollPanel; 
	private ModelMatrixFilterPanel filterPanel; 
	private TabLayoutPanel tabLayout;
	private AonMinimizePanel footPanel;
	boolean minimizedByUser;
	private NeoMatrix neo;
	
	private NeoMatrix getNeo() {
		return neo;
	}
	
	@Override
	public void onModuleLoad() {
		impl.getAonData(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(), new AsyncCallback<AonData>() {

			@Override public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(AonData aonData) {
				onModuleLoad(aonData);
			}
		});
	}
	
	public void onModuleLoad(AonData aonData) {
		AON.ensureInjected();
		
		FiscalModelServiceAsync serviceRaw = GWT.create(FiscalModelService.class);
		SERVICE = new FiscalModelServiceAsyncDecorator(serviceRaw);

		DockLayoutPanel dockLayout = new DockLayoutPanel(Unit.PX);
		filterPanel = new ModelMatrixFilterPanel(aonData);
		dockLayout.addNorth(filterPanel, 50);
		splitLayoutPanel = new SplitLayoutPanel();
		this.neo = new NeoMatrix(aonData) {
			@Override
			protected void onParentLoad() {
				filterPanel.fireValueChangeEvent();				
			}

			@Override
			protected void onOpenPanel() {
				openFootPanel();
			}

			@Override
			protected void onClosePanel() {
				closeFootPanel();
			}
		};
		splitLayoutPanel.addSouth(getMinimizePanel( aonData ), 30);
		scrollPanel = new ScrollPanel();
		splitLayoutPanel.add(scrollPanel);
		dockLayout.add(splitLayoutPanel);
		
		filterPanel.addValueChangeHandler( new ValueChangeHandler<FiscalMatrixParams>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<FiscalMatrixParams> event) {
				search( aonData, event.getValue() );
			}
		});
		
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockLayout);
		
		filterPanel.fireValueChangeEvent();
	}

//	private void search(AonData aonData, FiscalMatrixParams params) {
//		SERVICE.getFiscalPanel(aonData.getDomain().getName(), aonData.getUser().getLogin(), aonData.getDomain().getId(), params, 
//				new AsyncCallback<LinkedList<IFiscalModel>>() {
//					
//					@Override
//					public void onSuccess(LinkedList<IFiscalModel> matrix) {
//						scrollPanel.setWidget( paint(aonData, matrix, params ));
//					}
//					
//					@Override
//					public void onFailure(Throwable caught) {
//						
//					}
//				});
//	}
	
	private FlowPanel paint(AonData aonData, AonJsArray<JsFiscalMenuItem> aonJsArray, FiscalMatrixParams fiscalMatrixParams) {
		FlowPanel content = new FlowPanel();
		content.setStyleName(AON.CSS.aonMarginRight());
		content.addStyleName(AON.CSS.aonMarginLeft());
		content.addStyleName(AON.CSS.aonBlockCenter());

		if (aonJsArray == null || aonJsArray.length() == 0) {
			Label noData = new Label(AON.MSG.noData());
			noData.setStyleName(AON.CSS.aonTextCenter());
			noData.addStyleName(AON.CSS.aonMarginTop());
			noData.addStyleName(AON.CSS.aonPadding());
			noData.addStyleName(AON.CSS.aonColorRed());
			noData.addStyleName(AON.CSS.aonBold());
			content.add(noData);
			return content;
		}
		
		
		FlexTable tab = new FlexTable();
		tab.setCellSpacing(0);
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addStyleName(AON.CSS.aonTable());
		
		int col = 0;
		
		tab.getColumnFormatter().setWidth(col++, "450px");
		tab.getColumnFormatter().setWidth(col++, "50px");
		tab.getColumnFormatter().setWidth(col++, "25px");
		tab.getColumnFormatter().setWidth(col++, "70px");
		
		int modelOffset = col;
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");
		tab.getColumnFormatter().setWidth(col++, "30px");

		int colspan = col;
		int documentCol = 0;
		int modelCol = 1;
		int admonCol = 2;
		int periodCol = 3;
		
		int row = 0;
		col = 0;
		
		AonTableButton helpButton  = new AonTableButton("Leyenda",AON.CSS.aonIconHelp());
		helpButton.setText("Leyenda");
		helpButton.addStyleName(AON.CSS.aonMarginRight());
		helpButton.addStyleName(AON.CSS.aonMarginLeft());
		helpButton.getElement().getStyle().setPaddingLeft(3, Unit.EM);
		helpButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final AonCustomDialog dialog = new AonCustomDialog();
				dialog.setCaption("Leyenda");
				FlowPanel tabContainer = new FlowPanel();
				tabContainer.addStyleName(AON.CSS.aonPadding());

				AonDisplayTable statusLegendTab = new AonDisplayTable();
				statusLegendTab.getElement().getStyle().setProperty("border-collapse", "separate");
				statusLegendTab.getElement().getStyle().setProperty("border-spacing", "2px");
				statusLegendTab.setStyleName(AON.CSS.aonMarginTop());
				statusLegendTab.setStyleName(AON.CSS.aonMarginBottom());
				FiscalStatus[] statuses = new FiscalStatus[] {FiscalStatus.MISSING,FiscalStatus.PENDING,FiscalStatus.CUSTOMER_CHECK,FiscalStatus.FINISHED,FiscalStatus.SENT};
				for (FiscalStatus st : statuses) {
					InlineLabel statusLabel = new InlineLabel(st == FiscalStatus.MISSING? "No realizado" : st.getName());
					statusLabel.setStyleName(AON.CSS.aonMarginLeft());
					statusLabel.getElement().getStyle().setPadding(3, Unit.PX);
					AonDisplayTableRow legendRow = statusLegendTab.addRow();
					AonDisplayTableCell cell0 = legendRow.addCell();
					cell0.setWidth("200px");
					cell0.add(statusLabel);
					AonDisplayTableCell cell = legendRow.addCell();
					cell.addStyleName(AON.CSS.aonBorder());
					cell.setWidth("20px");
					cell.getElement().getStyle().setBackgroundColor(FiscalModelUtils.gettStatusBckColorRGB( st ));
					cell.add(new InlineLabel());
				}
				tabContainer.add( statusLegendTab );
				FlowPanel buttons = new FlowPanel();
		    	buttons.setStyleName(AON.CSS.aonTextCenter());
		    	buttons.addStyleName(AON.CSS.aonMarginTop());
		    	buttons.addStyleName(AON.CSS.aonMarginBottom());
		    	final Button okButton = new Button();
		    	okButton.setStyleName(AON.CSS.aonOkButton());
		    	okButton.setText( AON.MSG.accept());
		    	okButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						dialog.hide();
					}
				});
		    	buttons.add(okButton);
		    	
				tabContainer.add( buttons );
				dialog.add( tabContainer );
				dialog.center();
				dialog.show();
			}
		});
		tab.setWidget(row, col, helpButton );
		
		col = modelOffset;
		String[] months = new String[]{"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
		for (String month : months) {
			tab.setWidget(row, col, new Label( month ));
			tab.getCellFormatter().setStyleName(row, col  , AON.CSS.aonBorder());
			tab.getCellFormatter().addStyleName(row, col  , AON.CSS.aonTextCenter());
			tab.getCellFormatter().addStyleName(row, col++, AON.CSS.aonBold());
		}
		row++;
		
		col = modelOffset;
		String[] quars = new String[]{"1\u00BA Trim","2\u00BA Trim","3\u00BA Trim","4\u00BA Trim"};
		for (String quar : quars) {
			tab.setWidget(row, col, new Label( quar ));
			tab.getFlexCellFormatter().setColSpan(row, col, 3);
			tab.getCellFormatter().setStyleName(row, col  , AON.CSS.aonBorder());
			tab.getCellFormatter().addStyleName(row, col  , AON.CSS.aonTextCenter());
			tab.getCellFormatter().addStyleName(row, col++, AON.CSS.aonBold());
		}
		row++;

		LinkedHashMap<String, LinkedHashMap<Administration, LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<JsFiscalMenuItem>>>>>> domainMap = sortInfo(aonJsArray);
		for (String domainName : domainMap.keySet()) {
			 

			if (AonStringUtils.isBlank( fiscalMatrixParams.getModel() )) {
				Label emptyLabel = new Label();
				emptyLabel.setStyleName(AON.CSS.aonMarginTop());
				tab.setWidget(row, 0, emptyLabel);
				tab.getFlexCellFormatter().setColSpan(row, 0, colspan);
				row++;
				
				Label domainLabel = new Label(domainName);
				tab.getCellFormatter().setStyleName(row, 0 , AON.CSS.aonBold());
				tab.getCellFormatter().addStyleName(row, 0 , AON.CSS.aonTextCenter());
				tab.setWidget(row, 0, domainLabel);
				tab.getFlexCellFormatter().setColSpan(row, 0, colspan);
				row++;
			}

			LinkedHashMap<Administration, LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<JsFiscalMenuItem>>>>> admonMap = domainMap.get(domainName);
			for (Administration admon : admonMap.keySet()) {
				LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<JsFiscalMenuItem>>>> periodMap = admonMap.get(admon);
				for (PeriodType type : periodMap.keySet()) {
					LinkedHashMap<String, LinkedHashMap<String, LinkedList<JsFiscalMenuItem>>> modelMap = periodMap.get(type);
					for (String modelName : modelMap.keySet()) {
						LinkedHashMap<String, LinkedList<JsFiscalMenuItem>> documentMap = modelMap.get(modelName);
						for (String document : documentMap.keySet()) {
							
							tab.getCellFormatter().setStyleName(row, documentCol, AON.CSS.aonBorder());
							tab.setWidget(row, documentCol, new Label( document ));

							tab.getCellFormatter().setStyleName(row, modelCol, AON.CSS.aonBold());
							tab.getCellFormatter().addStyleName(row, modelCol, AON.CSS.aonBorder());
							tab.getCellFormatter().addStyleName(row, modelCol, AON.CSS.aonTextCenter());
							tab.setWidget(row, modelCol, new Label( modelName ));

							Label admonLabel = new Label();
							if (admon == Administration.UNKNOWN) {
								admonLabel.setText("\u2022");
							} else {
								admonLabel.setStyleName(AON.CSS.aonIconLabel());
								admonLabel.addStyleName(FiscalModelUtils.getAdministrationIconStyle(admon));	
							}
							tab.getCellFormatter().setStyleName(row, admonCol, AON.CSS.aonBorder());
							tab.getCellFormatter().addStyleName(row, admonCol, AON.CSS.aonTextCenter());
							tab.setWidget(row, admonCol, admonLabel);
							
							Label periodLabel = new Label(type.getValue());
							tab.getCellFormatter().setStyleName(row, periodCol, AON.CSS.aonBorder());
							tab.getCellFormatter().addStyleName(row, periodCol, AON.CSS.aonTextCenter());
							tab.setWidget(row, periodCol, periodLabel);

//							FiscalModelType modelType = FiscalModelType.safeValueByName(modelName);
//							boolean hasViewOption = modelType == FiscalModelType.M111; 
							int colspan2 = 12 / (type.getArraySize());
							for (int x = 0; x < type.getArraySize(); x++) {
								int c = x + modelOffset;
								tab.getFlexCellFormatter().setColSpan(row, c, colspan2);
								tab.getCellFormatter().setStyleName(row, c  , AON.CSS.aonBorder());
								tab.getCellFormatter().addStyleName(row, c, AON.CSS.aonTextCenter());
								
//								AonTableButton addButton = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
//								if (hasViewOption) {
//									newModel( aonData, model );
//								}
//								tab.setWidget(row, c, hasViewOption ?addButton:new InlineLabel());
								
								tab.setWidget(row, c, new InlineLabel());
								tab.getWidget(row, c ).getElement().getParentElement().getStyle().setBackgroundColor(FiscalModelUtils.gettStatusBckColorRGB( FiscalStatus.MISSING));
								
							}
							
							LinkedList<JsFiscalMenuItem> models = documentMap.get(document);
							for (JsFiscalMenuItem model : models) {
								if (model != null) {
									
									FiscalStatus status = FiscalStatus.safeValueOf(model.getStatus());
									Period period = Period.valueOf(model.getPeriod());
									col = (period.isQuarterPeriod()?(period.getStartMonth()/3):period.getStartMonth()) + modelOffset;
									FocusPanel focusPanel = new FocusPanel();
									focusPanel.setTitle(AON.MSG.selectAction());
									Label mod = new Label();
									mod.setText("\u2022");
									if (status != FiscalStatus.MISSING) {
										mod.setStyleName(AON.CSS.aonClickable());
										mod.addStyleName(AON.CSS.aonTextCenter());
										if (status == FiscalStatus.FINISHED || status == FiscalStatus.CUSTOMER_CHECK || status == FiscalStatus.SENT) {
											mod.setText( AON.FMT.format(model.getResult()));
											// mod.setStyleName(AonMathUtils.isGreatherThanZero(model.getResult()) ? AON.CSS.aonColorRed() : AON.CSS.aonColorBlue() );
										}
										if (status == FiscalStatus.FINISHED) {
											focusPanel.addStyleName(AON.CSS.aonClickable());
											focusPanel.setTitle("Seleccionar");
											focusPanel.addClickHandler( new ClickHandler() {
												
												@Override
												public void onClick(ClickEvent event) {
													openFootPanel();
													if(!getNeo().hasModel(model)) {
														getNeo().addModel(model);
													}
												}
												
											});
										} else {
											focusPanel.addClickHandler( new ClickHandler() {
												@Override
												public void onClick(ClickEvent event) {
													viewModel( aonData, model );
												}
											});
										}
									}
									focusPanel.add(mod);
									tab.setWidget(row, col, focusPanel);
									tab.getWidget(row, col ).getElement().getParentElement().getStyle().setBackgroundColor(FiscalModelUtils.gettStatusBckColorRGB( status ));
								}
							}
						}
						row++;
					}
					
				}
			}
		}
		content.add(tab);
		return content;
	}
	
	private LinkedHashMap<String, LinkedHashMap<Administration, LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<JsFiscalMenuItem>>>>>>
		sortInfo( AonJsArray<JsFiscalMenuItem> aonJsArray) {

		
		LinkedHashMap<String										// domainMap
			, LinkedHashMap<Administration							// admonMap
				, LinkedHashMap<PeriodType							// periodMap
					, LinkedHashMap<String							// modelMap
						, LinkedHashMap<String						// documentMap
							, LinkedList<JsFiscalMenuItem>>>>>>  		// models
		domainMap = new LinkedHashMap<String, LinkedHashMap<Administration, LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<JsFiscalMenuItem>>>>>>();
		for (int i = 0; i < aonJsArray.length() ; i++ ) {
			JsFiscalMenuItem item = aonJsArray.get(i);
			String domainName = item.getDomainName();
			String doc = item.getDocument();
			String n = item.getName();
			String s = item.getSurname();
			String name = AonStringUtils.join(new String[]{s,n}, AonStringUtils.isBlank(s)?"":", ");
			name = AonStringUtils.abbreviate(AonStringUtils.join(new String[]{doc,name}, " "), 50);
			FiscalModelType modelType = FiscalModelType.safeValueByName(item.getModel());
			Administration admon = Administration.safeValueOf(item.getAdministration());
			if (admon == null) admon = Administration.UNKNOWN;
			LOGGER.info("Period ... " + (item.getPeriod()==null?"NULL":("NOT NULL" + item.getPeriod() )));
			Period period = Period.valueOf(item.getPeriod());
			FiscalModel fs = new FiscalModel()
					.setModel(modelType)
					.setAdministration(admon)
					.setPeriod(period);
			String modelName = FiscalModelUtils.getModelName(fs);
			PeriodType type = PeriodType.getPeriodType(period);
			LinkedHashMap<Administration, LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<JsFiscalMenuItem>>>>> admonMap = domainMap.get(domainName);
			if (admonMap == null) {
				admonMap = new LinkedHashMap<Administration, LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<JsFiscalMenuItem>>>>>();
				domainMap.put(domainName,admonMap);	
			}
			
			LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<JsFiscalMenuItem>>>> periodMap = admonMap.get(admon);
			if (periodMap == null) {
				periodMap = new LinkedHashMap<PeriodType, LinkedHashMap<String, LinkedHashMap<String, LinkedList<JsFiscalMenuItem>>>>();
				admonMap.put(admon,periodMap);	
			}
			
			LinkedHashMap<String, LinkedHashMap<String, LinkedList<JsFiscalMenuItem>>> modelMap = periodMap.get(type);
			if (modelMap == null) {
				modelMap = new LinkedHashMap<String, LinkedHashMap<String, LinkedList<JsFiscalMenuItem>>>();
				periodMap.put(type,modelMap);	
			}
			
			LinkedHashMap<String, LinkedList<JsFiscalMenuItem>> documentMap = modelMap.get(modelName);
			if (documentMap == null) {
				documentMap = new LinkedHashMap<String, LinkedList<JsFiscalMenuItem>>();
				modelMap.put(modelName,documentMap);	
			}
			
			LinkedList<JsFiscalMenuItem> list = documentMap.get(name);
			if (list == null) {
				list = new LinkedList<JsFiscalMenuItem>();
				for (int x = 0; x < type.getArraySize(); x++) {
					list.add(null);
				}
				documentMap.put(name, list);				
			}
			list.set(type.getIndex(period), item);
		}
		return domainMap;
	}
	
	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}

	private void openFootPanel() {
		int effectiveHeigth = 3;
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / effectiveHeigth);
		splitLayoutPanel.animate(500);
	}

	private AonMinimizePanel getMinimizePanel(AonData aonData) {
		footPanel = new AonMinimizePanel();
		footPanel.addMinimizeHandler(new MinimizeHandler() {
			
			@Override
			public void onMinimize(MinimizeEvent event) {
				minimizedByUser = true;
				closeFootPanel();
			}
		});
		footPanel.addMaximizeHandler(new MaximizeHandler() {
			
			@Override
			public void onMaximize(MaximizeEvent event) {
				openFootPanel();
			}
		});
		footPanel.setStyleName(AON.CSS.aonSelector());
		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		
		footPanel.add(tabLayout);
		tabLayout.add(getNeo(), TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.actions(), AON.CSS.aonIconList()));
		return footPanel; 
	}
	
//	protected void newModel(AonData aonData, IFiscalModel model) {
//		Mod111 mod111 = new Mod111();
//		mod111.setAdministration(Administration.COMMON_TERRITORY);
//		mod111.setDomain(dom);
//		mod111.setModel(modelType);
//		mod111.setYear( filterPanel.getSelectedYear() );
//		if (type == PeriodType.YEARLY) {
//			mod111.setPeriod(Period.YEAR);	
//		}  else if (type == PeriodType.QUARTERLY) {
//			mod111.setPeriod( Period.values()[12 + x] );
//		} else {
//			mod111.setPeriod( Period.values()[x] );
//		};
//		addButton.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				AonCustomPopup entryDialog = new AonCustomPopup();
//				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
//				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
//				entryDialog.setAnimationEnabled(true);
//				entryDialog.setGlassEnabled(true);
//				entryDialog.setModal(true);
//				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong( modelType ) , 60 ));
//				
//				Model111 model111 = new Model111();
//				Model111ModuleOptions options = new Model111ModuleOptions();
//				options.setParentWidget(entryDialog);
//				options.setDomainName(aonData.getDomain().getName());
//				options.setDomain( dom );
//				options.setUser(aonData.getUser().getLogin());
//				options.setAonData(aonData);
//				options.setNewModel( mod111 );
//				options.setEmbedded(true);
//				model111.onModuleLoad( options );
//				
//				entryDialog.center();
//				entryDialog.show();
//			}
//		});
//	}

	protected void viewModel(AonData aonData, JsFiscalMenuItem model) {
		Integer id = Integer.valueOf(model.getId() + ""); 
		LOGGER.info("Before view Model: [" + model.getModel() + "] " + id );
		FiscalModelType modelType = FiscalModelType.safeValueByName(model.getModel());
		IFiscalModelTypeVisitor visitor = new IFiscalModelTypeVisitor() {
			@Override 
			public void visitM200() {
				LOGGER.info("Before visitM200");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model200 model200 = new Model200();
					Model200ModuleOptions options = new Model200ModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							entryDialog.clear();
						}
					});
					LOGGER.info("Before visitM303 model200.onModuleLoad( options )");
					model200.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}
			
			@Override 
			public void visitM390() {
				LOGGER.info("Before visitM390");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model390 model390 = new Model390();
					Model390ModuleOptions options = new Model390ModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							entryDialog.clear();
						}
					});
					LOGGER.info("Before visitM303 model390.onModuleLoad( options )");
					model390.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}
			
			@Override 
			public void visitM349() {
				LOGGER.info("Before visitM349");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model349 model349 = new Model349();
					Model349ModuleOptions options = new Model349ModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							entryDialog.clear();
						}
					});
					LOGGER.info("Before visitM303 model349.onModuleLoad( options )");
					model349.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}
			
			@Override 
			public void visitM347() {
				LOGGER.info("Before visitM347");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model347 model347 = new Model347();
					Model347ModuleOptions options = new Model347ModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							entryDialog.clear();
						}
					});
					LOGGER.info("Before visitM303 model347.onModuleLoad( options )");
					model347.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}
			
			@Override 
			public void visitM193() {
				LOGGER.info("Before visitM193");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model193 model193 = new Model193();
					Model193ModuleOptions options = new Model193ModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							entryDialog.clear();
						}
					});
					LOGGER.info("Before visitM303 model193.onModuleLoad( options )");
					model193.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}
			
			@Override 
			public void visitM190() {
				LOGGER.info("Before visitM190");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model190 model190 = new Model190();
					Model190ModuleOptions options = new Model190ModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							entryDialog.clear();
						}
					});
					LOGGER.info("Before visitM303 model190.onModuleLoad( options )");
					model190.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}
			
			@Override 
			public void visitM184() {
				LOGGER.info("Before visitM184");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model184 model184 = new Model184();
					Model184ModuleOptions options = new Model184ModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							entryDialog.clear();
						}
					});
					LOGGER.info("Before visitM303 model184.onModuleLoad( options )");
					model184.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}
			
			@Override 
			public void visitM180() {
				LOGGER.info("Before visitM180");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model180 model180 = new Model180();
					Model180ModuleOptions options = new Model180ModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							entryDialog.clear();
						}
					});
					LOGGER.info("Before visitM303 model180.onModuleLoad( options )");
					model180.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}
			
			@Override 
			public void visitM390HF(){
				LOGGER.info("Before visitM390HF");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model390HF model390HF = new Model390HF();
					Model390HFModuleOptions options = new Model390HFModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							entryDialog.clear();
						}
					});
					LOGGER.info("Before visitM303 model390HF.onModuleLoad( options )");
					model390HF.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}
			
			@Override 
			public void visitM303() {
				LOGGER.info("Before visitM303");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model303 model303 = new Model303();
					Model303ModuleOptions options = new Model303ModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							entryDialog.clear();
						}
					});
					model303.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}
			
			@Override 
			public void visitM202() {
				LOGGER.info("Before visitM202");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model202 model202 = new Model202();
					Model202ModuleOptions options = new Model202ModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							entryDialog.clear();
						}
					});
					model202.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}
			
			@Override 
			public void visitM131() {
				LOGGER.info("Before visitM131");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model131 model131 = new Model131();
					Model131ModuleOptions options = new Model131ModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							entryDialog.clear();
						}
					});
					model131.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}
			
			@Override 
			public void visitM130() {
				LOGGER.info("Before visitM130");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model130 model130 = new Model130();
					Model130ModuleOptions options = new Model130ModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							entryDialog.clear();
						}
					});
					model130.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}
			
			@Override 
			public void visitM123() {
				LOGGER.info("Before visitM123");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model123 model123 = new Model123();
					Model123ModuleOptions options = new Model123ModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							entryDialog.clear();
						}
					});
					model123.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}

			@Override 
			public void visitM115() {
				LOGGER.info("Before visitM115");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model115 model115 = new Model115();
					Model115ModuleOptions options = new Model115ModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}
						
						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					model115.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}
			
			@Override
			public void visitM111() {
				LOGGER.info("Before visitM111");
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption( AonStringUtils.abbreviate( AON.MSG.fiscalModelDescriptionlong(modelType) , 60 ));
				try {
					Model111 model111 = new Model111();
					Model111ModuleOptions options = new Model111ModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(aonData.getDomain().getName());
					options.setDomain( model.getDomain() );
					options.setUser(aonData.getUser().getLogin());
					options.setAonData(aonData);
					options.setFiscalModelId( id );
					options.setEmbedded(true);
					options.setBackButtonVisible(true);
					options.setExternalCallback( new ModuleCallback() {
						
						@Override
						public void onRemove(IAccountEntryWrapper removed) {
							hide();
						}

						@Override
						public void onFailure(Throwable caught) {}
						
						@Override
						public void onExit() {
							hide();
						}
						
						@Override
						public void onChange(IAccountEntryWrapper changed) {
							hide();
						}
						
						private void hide() {
							entryDialog.hide();
							entryDialog.clear();
						}
					});
					entryDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							entryDialog.clear();
						}
					});
					model111.onModuleLoad( options );
					entryDialog.center();
					entryDialog.show();
				} catch (Throwable t) {
					Window.alert("Error inesperado! [" + t.getMessage() + "]");
				}
			}
		};
		modelType.visit(visitor);
	}
	
	private void search(AonData aonData, FiscalMatrixParams params) {
		API API = new API(GWT.getHostPageBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getDomain().getId(),
				aonData.getUser().getLogin());
		HashMap<String, LinkedList<String>> filterMap = new HashMap<String, LinkedList<String>>();
		LinkedList<String> yearListt = new LinkedList<String>();
		yearListt.add(AonNumberUtils.toString(params.getYear()) );
		filterMap.put(IJsonNames.YEAR, yearListt);
		LinkedList<String> modelListt = new LinkedList<String>();
		modelListt.add(params.getModel());
		filterMap.put(IJsonNames.MODEL, modelListt);
		LinkedList<String> admonListt = new LinkedList<String>();
		admonListt.add(params.getAdministration()==null?"":params.getAdministration().toString());
		filterMap.put(IJsonNames.ADMINISTRATION, admonListt);
		LinkedList<String> configuredVisibleListt = new LinkedList<String>();
		configuredVisibleListt.add( params.isConfiguredVisible()?Boolean.TRUE.toString() : Boolean.FALSE.toString() );
		filterMap.put(IJsonNames.CONFIGURED_VISIBLE, configuredVisibleListt);
		LinkedList<String> madeModelsVisibleList = new LinkedList<String>();
		madeModelsVisibleList.add( params.isMadeModelsVisible()?Boolean.TRUE.toString() : Boolean.FALSE.toString() );
		filterMap.put(IJsonNames.MADE_MODELS_VISIBLE, madeModelsVisibleList);
		API.getFiscal().getFiscalModels( filterMap, new AsyncCallback<JSON<JsFiscalMenuItem>>() {
			
			@Override
			public void onSuccess(JSON<JsFiscalMenuItem> result) {
				scrollPanel.setWidget( paint(aonData, result.getData(), params));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});;
//		XMLHttpRequest xhr = XMLHttpRequest.create();
//		xhr.open(FormPanel.METHOD_POST, FISCAL_API_SERVLET);
//		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
//		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
//			@Override
//			public void onReadyStateChange(XMLHttpRequest xhr) {
//				if (xhr.getReadyState() == XMLHttpRequest.DONE) {
//					String json = xhr.getResponseText();
//					LOGGER.info(json);
//					JavaScriptObject unk = JsonUtils.safeEval(json);
//					JSONArray domains = new JSONArray(unk);
//					paint(aonData, domains, params);
//				}
//			}
//		});
//		JSONObject json = new JSONObject();
//		json.put(IJsonNames.YEAR, new JSONNumber((double) params.getYear()));
//		json.put(IJsonNames.MODEL, new JSONString(params.getModel()));
//		json.put(IJsonNames.ADMINISTRATION, new JSONString(params.getAdministration()==null?"":params.getAdministration().toString()));
//		json.put(IJsonNames.CONFIGURED_VISIBLE, JSONBoolean.getInstance(params.isConfiguredVisible()));
//		json.put(IJsonNames.MADE_MODELS_VISIBLE, JSONBoolean.getInstance(params.isMadeModelsVisible()));
//		xhr.send(json.toString());
	}
}

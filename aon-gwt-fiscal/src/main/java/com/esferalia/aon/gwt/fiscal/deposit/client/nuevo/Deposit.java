package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.fiscal.JsDepositConfiguration;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.Toolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemory;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemoryAsync;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.ConfigurationPanel;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.FreeText;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.ImportPanel;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.MemoryDocuments;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageF1;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageF1A;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageF1B;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageF1C;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageF1D;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageF1E;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageF1F;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageF1G;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageF1H;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageF2;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageF3;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageH1;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageH2;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageH3;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageH4;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageH5;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageM10;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageM11_2;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageM12_2;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageM13_2;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageM14_2;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageM15;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageM3_2;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageM5_2;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageM6_2;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.PageM7_2;
import com.esferalia.aon.gwt.fiscal.deposit.shared.DepositMenu;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryItem;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.paper.PaperIconButtonElement;
import com.vaadin.polymer.vaadin.VaadinUploadElement;

import net.aonsolutions.polymer.aon.AonComboBoxElement;
import net.aonsolutions.polymer.aon.widget.AonComboBox;



public class Deposit extends AonTemplate2 {
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);

	DockLayoutPanel d2Content;
	FlexTable header;
	ScrollPanel page;
	
	API API;
	AonData aonData;
	Company company;
	Map<String, String> deposit;
	Integer year;
	DepositMenu depositMenu;
	
	Stack<Map<String, String>> undoStack = new Stack<Map<String, String>>();
	Stack<Map<String, String>> redoStack = new Stack<Map<String, String>>();
	
	Deposit thiz = this;
	
	public Deposit(AonData aonData) {
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getDomain().getId(),
				aonData.getUser().getLogin());
	}
	
	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				PaperIconButtonElement.SRC,
				AonComboBoxElement.SRC,
				VaadinUploadElement.SRC
		));
		
		Polymer.whenReady(o -> {
			super.onModuleLoad();
			inma.getCompany(getAonData(), new AsyncCallback<Company>() {
				
				@Override
				public void onSuccess(Company result) {
					setCompany(result);
					startApplication();
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
			return null;
		});
	}
	
	private void startApplication() {
		toolbar();
		westContent();
		content();
	}
	
	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar("Cuentas Anuales");
		toolbar.addButton("Deshacer","aon-icon-undo").addClickHandler(undoClickHandler());
		toolbar.addButton("Rehacer","aon-icon-redo").addClickHandler(redoClickHandler());
		toolbar.addButton("Exportar",AON.AON_CSS.aonIconAeat()).addClickHandler(exportClickHandler());
		toolbar.addButton("Importar","aon-icon-file-upload").addClickHandler(importClickHandler());
		toolbar.addButton("Descargar",AON.AON_CSS.aonIconExcel()).addClickHandler(downloadClickHandler());
		setToolbar(toolbar);
	}
	
	private void westContent() {
		getDockLayoutPanel().setWidgetSize(getWestContent(), 300);
		setWestContent(new ConfigurationPanel(thiz));
	}
    
	private void content() {
 		setYear(AonDateUtils.getCurrentYear() - 1);
		header();
		deposit();
		setD2Content(new DockLayoutPanel(Unit.PX));
		getD2Content().addNorth(getHeader(), 55);
		getD2Content().add(getPage());
		setContent(getD2Content());
	}
	
	private void header() {
		setHeader(new FlexTable());
		getHeader().setStyleName(AON.AON_CSS.aonFiscalModelTable());
		
		Label image = new Label("");
		image.setStyleName(AON.AON_CSS.aonRegistroMercantilImage());

		getHeader().setWidget(0, 0, image);
		getHeader().getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
		getHeader().getFlexCellFormatter().setRowSpan(0, 0, 2);
		
		getHeader().setWidget(0, 1, new Label("Cuentas Anuales"));
		getHeader().getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		getHeader().getFlexCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonFiscalRegistroMercantil2());
		getHeader().getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		Label typeLabel = new Label("Tipo");
		typeLabel.addClickHandler(changeTypeClickHandler());
		getHeader().setWidget(0, 2, typeLabel);
		getHeader().getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		getHeader().getFlexCellFormatter().addStyleName(0, 2, AON.AON_CSS.aonFiscalRegistroMercantil2());
		
		getHeader().setWidget(1, 0, new Label("20XX"));
		getHeader().getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		getHeader().getFlexCellFormatter().addStyleName(1, 0, AON.AON_CSS.aonFiscalRegistroMercantil2());
	}
	
	public void updateHeader(String type, Integer year) {
		Label typeLabel = new Label(type);
		typeLabel.addClickHandler(changeTypeClickHandler());
		getHeader().setWidget(0, 2, typeLabel);
		getHeader().setWidget(1, 0, new Label(year.toString()));
	}
	
	private void updateType(String type) {
		// TODO
		updateHeader(type, getYear());
	}
	
	private void deposit() {
		setPage(new ScrollPanel());
		inma.getSchema(getAonData(), getCompany(), getYear(), false, new AsyncCallback<Map<String, String>>() {
			@Override
			public void onSuccess(Map<String, String> result) {
				setDeposit(result);
				updateHeader(getDeposit().get(D2DepositConstants.DEPOSIT_TYPE), getYear());
				updatePage(DepositMenu.HIS);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void refreshPage() {
		updatePage(getDepositMenu());
	}
	
	public void updatePage(DepositMenu depositMenu) {
		setDepositMenu(depositMenu);
		if(DepositMenu.HIS.equals(depositMenu)) getPage().setWidget(new PageH1(thiz));
		if(DepositMenu.AR.equals(depositMenu)) getPage().setWidget(new PageM3_2(thiz));
		if(DepositMenu.BS.equals(depositMenu)) getPage().setWidget(new PageH2(thiz));
		if(DepositMenu.CPG.equals(depositMenu)) getPage().setWidget(new PageH3(thiz));
		if(DepositMenu.ECPN.equals(depositMenu)) getPage().setWidget(new PageH4(thiz));
		if(DepositMenu.DM.equals(depositMenu)) getPage().setWidget(new PageH5(thiz));
		
		// MEMORIA
		if(DepositMenu.AE.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT1", false));
		if(DepositMenu.BP.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT2", false));
		if(DepositMenu.AR_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT3", false));
		if(DepositMenu.AR_CN.equals(depositMenu)) getPage().setWidget(new PageM3_2(thiz));
		if(DepositMenu.NRV.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT4", false));
		if(DepositMenu.IMIII_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT5", false));
		if(DepositMenu.IMIII_CN.equals(depositMenu)) getPage().setWidget(new PageM5_2(thiz));
		if(DepositMenu.AF_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT6", false));
		if(DepositMenu.AF_CN.equals(depositMenu)) getPage().setWidget(new PageM6_2(thiz));
		if(DepositMenu.PF_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT7", false));
		if(DepositMenu.PF_CN.equals(depositMenu)) getPage().setWidget(new PageM7_2(thiz));
		if(DepositMenu.FP.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT8", false));
		if(DepositMenu.SF.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT9", false));
		if(DepositMenu.IG.equals(depositMenu)) getPage().setWidget(new PageM10(thiz));
		if(DepositMenu.SDL_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT11", false));
		if(DepositMenu.SDL_CN.equals(depositMenu)) getPage().setWidget(new PageM11_2(thiz));
		if(DepositMenu.OPV_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT12", false));
		if(DepositMenu.OPV_CN.equals(depositMenu)) getPage().setWidget(new PageM12_2(thiz));
		if(DepositMenu.OI_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT13", false));
		if(DepositMenu.OI_CN.equals(depositMenu)) getPage().setWidget(new PageM13_2(thiz));
		if(DepositMenu.IM_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT14", false));
		if(DepositMenu.IM_CN.equals(depositMenu)) getPage().setWidget(new PageM14_2(thiz));
		if(DepositMenu.IA.equals(depositMenu)) getPage().setWidget(new PageM15(thiz));
		
		if(DepositMenu.D.equals(depositMenu)) getPage().setWidget(new MemoryDocuments(thiz));
		
		// TODO MODELO AUTOCARTERA
		if(DepositMenu.MA.equals(depositMenu)) getPage().setWidget(new PageF1(thiz));
		if(DepositMenu.MA1.equals(depositMenu)) getPage().setWidget(new PageF1A(thiz));
		if(DepositMenu.MA11.equals(depositMenu)) getPage().setWidget(new PageF1B(thiz));
		if(DepositMenu.MA2.equals(depositMenu)) getPage().setWidget(new PageF1C(thiz));
		if(DepositMenu.MA3.equals(depositMenu)) getPage().setWidget(new PageF1D(thiz));
		if(DepositMenu.MA4.equals(depositMenu)) getPage().setWidget(new PageF1E(thiz));
		if(DepositMenu.MA5.equals(depositMenu)) getPage().setWidget(new PageF1F(thiz));
		if(DepositMenu.MA6.equals(depositMenu)) getPage().setWidget(new PageF1G(thiz));
		if(DepositMenu.MA7.equals(depositMenu)) getPage().setWidget(new PageF1H(thiz));
		
		if(DepositMenu.IP.equals(depositMenu)) getPage().setWidget(new PageF2(thiz));
		if(DepositMenu.CHD.equals(depositMenu)) getPage().setWidget(new PageF3(thiz));		
	}
	
	private static final String IDA = "Hoja Identificativa de la sociedad";
	private static final String AR = "Aplicaci\u00f3n de resultados";
	private static final String BS = "Balance de situaci\u00f3n";
	private static final String PYG = "Cuenta de perdidas y ganancias";
	private static final String ECPN = "Estado de cambios en el patrimonio neto";
	private static final String DM = "Declaraci\u00f3n medioambiental";
 	private static final String MA = "Modelo de autocartera";
	private static final String IP = "Instancia de presentaci\u00f3n";
	private static final String CHD = "Certificaci\u00f3n de la huella digital";
	
	private void download(String format){
		FlexTable flex_table = new FlexTable();
		Integer index = 1;
		
		flex_table.setWidget(index, 0, new Label(IDA));
		CheckBox cbIDA = new CheckBox();cbIDA.setValue(true);
		flex_table.setWidget(index, 1, cbIDA);
		index++;
		
		if(year >= 2016){
			flex_table.setWidget(index, 0, new Label(AR));
			CheckBox cbAR = new CheckBox();cbAR.setValue(true);
			flex_table.setWidget(index, 1, cbAR);
			index++;
		}
		
		flex_table.setWidget(index, 0, new Label(BS));
		CheckBox cbBS = new CheckBox();cbBS.setValue(true);
		flex_table.setWidget(index, 1, cbBS);
		index++;
		
		flex_table.setWidget(index, 0, new Label(PYG));
		CheckBox cbPYG = new CheckBox();cbPYG.setValue(true);
		flex_table.setWidget(index, 1, cbPYG);
		index++;
		
		if(year < 2016){
			flex_table.setWidget(index, 0, new Label(ECPN));
			CheckBox cbECPN = new CheckBox();cbECPN.setValue(true);
			flex_table.setWidget(index, 1, cbECPN);
			index++;
		}
		
		flex_table.setWidget(index, 0, new Label(DM));
		CheckBox cbDM = new CheckBox();cbDM.setValue(true);
		flex_table.setWidget(index, 1, cbDM);
		index++;
		
		for(Integer pos = 0; pos < MemoryItem.getInstance().getApartadosSize(year); pos++){	
			flex_table.setWidget(index, 0, new Label(MemoryItem.getInstance().getApartadoName(year, pos)));
			CheckBox cb = new CheckBox();cb.setValue(true);
			
			flex_table.setWidget(index, 1, cb);
			index++;
		}
		
		flex_table.setWidget(index, 0, new Label(MA));
		CheckBox cbMA = new CheckBox();cbMA.setValue(true);
		flex_table.setWidget(index, 1, cbMA);
		index++;
		
		flex_table.setWidget(index, 0, new Label(IP));
		CheckBox cbIP = new CheckBox();cbIP.setValue(true);
		flex_table.setWidget(index, 1, cbIP);
		index++;
		
		flex_table.setWidget(index, 0, new Label(CHD));
		CheckBox cbCHD = new CheckBox();cbCHD.setValue(true);
		flex_table.setWidget(index, 1, cbCHD);
		index++;
		
		Label label =  new Label("Seleccionar apartados:");
		label.setStyleName(AON.AON_BOLD);
		flex_table.setWidget(0, 0, label);
		CheckBox cbALL = new CheckBox();cbALL.setValue(true);
		cbALL.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Boolean bool = cbALL.getValue();
				for(Integer i = 1; i < flex_table.getRowCount(); i++){
					CheckBox cb = (CheckBox) flex_table.getWidget(i, 1);
					cb.setValue(bool);
				}
			}
		});
		flex_table.setWidget(0, 1, cbALL);
		AonDialog dialog = new AonDialog("Descargar Deposito", flex_table) {
			
			@Override
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				String options = "";
				for(Integer i = 1; i < flex_table.getRowCount(); i++){
					CheckBox cb = (CheckBox) flex_table.getWidget(i, 1);
					options = options + (cb.getValue() ? "T":"F");
				}
			
				String fileDownloadURL = GWT.getModuleBaseURL() + "/CCAAPrint"
	                	+ "?schemaId=" + String.valueOf(1)
	                	+ "&domainId=" + Integer.toString(getAonData().getDomain().getId())
	                	+ "&domainName=" + getCurrentDomainName()
	                	+ "&cif=" + getCompany().getDocument()
						+ "&razonSocial=" + getCompany().getName()
						+ "&year=" + String.valueOf(year)
						+ "&type=" + getDeposit().get(D2DepositConstants.DEPOSIT_TYPE)
						+ "&options=" + options
						+ "&format=" + format
						+ "&isMemory=" + false;
				Window.open( fileDownloadURL, "_blank",null);
				hide();
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	/***** CLICK HANDLER *****/
	
	private ClickHandler changeTypeClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				getAPI().getFiscal().getDepositConfiguration(new AsyncCallback<JSON<JsDepositConfiguration>>() {
					
					@Override
					public void onSuccess(JSON<JsDepositConfiguration> result) {
						JsDepositConfiguration js = result.getData().get(0);
						AonComboBox acb = new AonComboBox();
				       	acb.setItemLabelPath("name");
				    	acb.setItemValuePath("name");
				    	acb.setItems(js.getOperationOption());
				    	acb.setInputElementValue(js.getOperation());
				    	acb.setStyle("padding-left:20px;padding-right:20px;padding-bottom: 20px; width:250px;");
				    	acb.setLabel("Tipo Deposito");
				    	AonDialog dialog = new AonDialog("Cambiar Tipo", acb) {
							
							@Override
							protected void onCancel() {
								hide();
							}
							
							@Override
							protected void onAccept() {
								hide();
								updateType(acb.getInputElementValue());
							}
						};
						dialog.setAutoHideEnabled(true);
						dialog.addAutoHidePartner(acb.getElementById("overlay"));
						dialog.getElement().getStyle().setWidth(310, Unit.PX);
						dialog.center();
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		};
	}

	private ClickHandler undoClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(!getUndoStack().isEmpty()) {
					Map<String, String> m = new HashMap<String, String>();
					for (String k : getDeposit().keySet()) {
						m.put(k, getDeposit().get(k));
					}
					getRedoStack().push(m);
					setDeposit(getUndoStack().pop());
					inma.saveDeposit(getAonData(), getDeposit(), getYear(), new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							refreshPage();
						}
						
						@Override public void onFailure(Throwable caught) {}
					});	
				}
			}
		};
	}
	
	private ClickHandler redoClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(!getRedoStack().isEmpty()) {
					Map<String, String> m = new HashMap<String, String>();
					for (String k : getDeposit().keySet()) {
						m.put(k, getDeposit().get(k));
					}
					getUndoStack().push(m);
					setDeposit(getRedoStack().pop());
					inma.saveDeposit(getAonData(), getDeposit(), getYear(), new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							refreshPage();
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
				}
			}
		};
	}


	private ClickHandler exportClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				inma.getDepositExercises(getAonData(), new AsyncCallback<String[]>() {

					@Override public void onFailure(Throwable caught) {}

					@Override
					public void onSuccess(String[] result) {
						String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_deposit/"
				            	+ "?domain_id=" + Integer.toString(getAonData().getDomain().getId())
				            	+ "&year="+ year;
						Window.open( fileDownloadURL, "_blank",null);
					}
				});
			}
		};
	}
	
	private ClickHandler downloadClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				download("excel");
			}
		};
	}
	
	private ClickHandler importClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				getInma().getDepositTemplates(getAonData(), new AsyncCallback<Vector<MemoryTemplate>>() {
					
					@Override
					public void onSuccess(Vector<MemoryTemplate> result) {
						ImportPanel ip = new ImportPanel(thiz, result);
						AonDialog dialog = new AonDialog("Importar", ip) {
							
							@Override
							protected void onCancel() {
								hide();
							}
							
							@Override
							protected void onAccept() {
								hide();
								ip.action();
							}
						};
						dialog.setAutoHideEnabled(true);
						dialog.addAutoHidePartner(ip.getMemoryBox().getElementById("overlay"));
						dialog.addAutoHidePartner(ip.getYearBox().getElementById("overlay"));
						dialog.addAutoHidePartner(ip.getSocBox().getElementById("overlay"));
						dialog.getElement().getStyle().setWidth(310, Unit.PX);
						dialog.center();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		};
	}
	
	
	
	/***** GETTERS & SETTERS *****/
	
	public INormalizedMemoryAsync getInma() {
		return inma;
	}
	
	public API getAPI() {
		return API;
	}
	
	public AonData getAonData() {
		return aonData;
	}
	
	public void setAonData(AonData aonData) {
		this.aonData = aonData;
	}
	
	public Company getCompany() {
		return company;
	}
	
	public void setCompany(Company company) {
		this.company = company;
	}
	
	public Map<String, String> getDeposit() {
		return deposit;
	}
	
	public void setDeposit(Map<String, String> deposit) {
		this.deposit = deposit;
	}
	
	public DockLayoutPanel getD2Content() {
		return d2Content;
	}

	public void setD2Content(DockLayoutPanel d2Content) {
		this.d2Content = d2Content;
	}
	
	public FlexTable getHeader() {
		return header;
	}
	
	public void setHeader(FlexTable header) {
		this.header = header;
	}
	
	public ScrollPanel getPage() {
		return page;
	}
	
	public void setPage(ScrollPanel page) {
		this.page = page;
	}
	
	public Integer getYear() {
		return year;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public DepositMenu getDepositMenu() {
		return depositMenu;
	}
	
	public void setDepositMenu(DepositMenu depositMenu) {
		this.depositMenu = depositMenu;
	}
	
	public Stack<Map<String, String>> getUndoStack() {
		return undoStack;
	}
	
	public void setUndoStack(Stack<Map<String, String>> undoStack) {
		this.undoStack = undoStack;
	}
	
	public Stack<Map<String, String>> getRedoStack() {
		return redoStack;
	}
	
	public void setRedoStack(Stack<Map<String, String>> redoStack) {
		this.redoStack = redoStack;
	}
}

package net.aonsolutions.aon.gwt.ccaa.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.Upload;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.FreeText;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.MemoryDocuments;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageF1;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageF1A;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageF1B;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageF1C;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageF1D;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageF1E;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageF1F;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageF1G;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageF1H;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageF2;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageF3;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageH1;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageH2;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageH3;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageH4;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageH5;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageH6;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageH7;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageITR;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageM10;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageM11_2;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageM12_2;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageM13_2;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageM14_2;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageM15;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageM3_2;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageM5_2;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageM6_2;
import net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory.PageM7_2;
import net.aonsolutions.aon.gwt.ccaa.shared.DepositMenu;
import net.aonsolutions.aon.gwt.ccaa.shared.MemoryItem;
import net.aonsolutions.aon.gwt.ccaa.shared.MemoryTemplate;



public class Deposit2 extends DockLayoutPanel {

	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);

	DockLayoutPanel d2Content;
	DepositHeader header;
	ScrollPanel page;
	
	API API;
	AonData aonData;
	Company company;
	Map<String, String> deposit;
	Integer year;
	String type;
	DepositMenu depositMenu;
	List<MemoryTemplate> memoryTemplates;
	
	AonToolbarButton undoButton;
	AonToolbarButton redoButton;
	AonToolbarButton exportButton;
	AonToolbarButton importButton;
	AonToolbarButton importMemoryButton;
	AonToolbarButton downloadButton;
	AonToolbarButton downloadPdfButton;
	AonToolbarButton resetButton;
	
	Stack<Map<String, String>> undoStack = new Stack<Map<String, String>>();
	Stack<Map<String, String>> redoStack = new Stack<Map<String, String>>();
	
	Deposit2 thiz = this;
	
	public Deposit2(AonData aonData) {
		super(Unit.PX);
		addStyleName("aon-Model-Detail");
		setAonData(aonData);
		init();
		inma.getCompany(getAonData(), new AsyncCallback<Company>() {
			
			@Override
			public void onSuccess(Company result) {
				setCompany(result);
				getInma().getDepositTemplates(getAonData(), new AsyncCallback<Vector<MemoryTemplate>>() {
					
					@Override
					public void onSuccess(Vector<MemoryTemplate> result) {
						setMemoryTemplates(result);
						startApplication();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});

			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	private void init() {
		setYear(AonDateUtils.getCurrentYear() - 1);
		setType("Abreviado");
	}
	
	private void startApplication() {
		addNorth(header(), DepositHeader.HEIGTH);
		addNorth(toolbar(), AonToolbar.HEIGTH-5);
		addWest(menu(), DepositWest.WIDTH);
		add(content());
	}
	
	private Widget menu() {
		return new DepositWest(thiz);
	}
	
	private Widget toolbar() {
		AonToolbar toolbarPanel = new AonToolbar(AonStringUtils.join(getCompany().getDocument(),AonStringUtils.SPACE, getCompany().getName()));
		
		undoButton = new AonToolbarButton("Deshacer", AON.CSS.aonIconUndo());
		undoButton.addClickHandler(undoClickHandler());
		toolbarPanel.add(undoButton);

		redoButton = new AonToolbarButton("Anular", AON.CSS.aonIconRedo());
		redoButton.addClickHandler(redoClickHandler());
		toolbarPanel.add(redoButton);
		
		exportButton = new AonToolbarButton("Exportar", AON.CSS.aonIconDownload());
		exportButton.addClickHandler(exportClickHandler());
		toolbarPanel.add(exportButton);
		
		importButton = new AonToolbarButton("Importar D2", AON.CSS.aonIconUpload());
		importButton.addClickHandler(importClickHandler());
		toolbarPanel.add(importButton);
		
		importMemoryButton = new AonToolbarButton("Importar Memoria", AON.CSS.aonIconImport());
		importMemoryButton.addClickHandler(importMemoryClickHandler());
		importMemoryButton.setVisible(!getMemoryTemplates().isEmpty());
		toolbarPanel.add(importMemoryButton);
		
		downloadButton = new AonToolbarButton("Descargar Excel", AON.CSS.aonIconExcel());
		downloadButton.addClickHandler(downloadClickHandler());
		toolbarPanel.add(downloadButton);
		
		downloadPdfButton = new AonToolbarButton("Descargar Pdf", AON.CSS.aonIconPdf());
		downloadPdfButton.addClickHandler(downloadPdfClickHandler());
		toolbarPanel.add(downloadPdfButton);
		
		resetButton = new AonToolbarButton("Resetear", AON.CSS.aonIconRefresh());
		resetButton.addClickHandler(resetClickHandler());
		toolbarPanel.add(resetButton);
		
		return toolbarPanel;
	}
	
	private Widget content() {
		deposit();
		return getPage();
	}
	
	private Widget header() {
		Label typeLabel = new Label(getType());
		typeLabel.getElement().getStyle().setCursor(Cursor.POINTER);
		typeLabel.addClickHandler(changeTypeClickHandler());
		setHeader(new DepositHeader(typeLabel, getYear()));
		return getHeader();
	}
	
	public void updateHeader(String type, Integer year) {
		setType(type);
		Label typeLabel = new Label(type);
		typeLabel.getElement().getStyle().setCursor(Cursor.POINTER);
		typeLabel.addClickHandler(changeTypeClickHandler());
		getHeader().refresh(typeLabel, year);
	}
	
	private void updateType(String type) {
		getInma().updateType(getAonData(), getYear(), type, new AsyncCallback<Map<String,String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				Map<String, String> m = new HashMap<>();
				for (String k : getDeposit().keySet()) {
					m.put(k, getDeposit().get(k));
				}
				getUndoStack().push(m);
				getRedoStack().clear();
				setDeposit(result);
				
				updateHeader(type, getYear());
				updatePage(getDepositMenu());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
	}
	
	private void deposit() {
		setPage(new ScrollPanel());
		getPage().addStyleName(AON.CSS.aonMarginTop());
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
	
	public void refreshPage(Integer tab) {
		updatePage(getDepositMenu(), tab);
	}

	public void updatePage(DepositMenu depositMenu) {
		updatePage(depositMenu, 0);		
	}
	
	public void updatePage(DepositMenu depositMenu, Integer tab) {
		setDepositMenu(depositMenu);
		if(DepositMenu.HIS.equals(depositMenu)) getPage().setWidget(new PageH1(thiz));
		if(DepositMenu.ITR.equals(depositMenu)) getPage().setWidget(new PageITR(thiz));
		if(DepositMenu.SRA.equals(depositMenu)) getPage().setWidget(new PageH6(thiz));
		if(DepositMenu.AR.equals(depositMenu)) getPage().setWidget(new PageM3_2(thiz));
		if(DepositMenu.BS.equals(depositMenu)) getPage().setWidget(new PageH2(thiz, tab));
		if(DepositMenu.CPG.equals(depositMenu)) getPage().setWidget(new PageH3(thiz, tab));
		if(DepositMenu.ECPN.equals(depositMenu)) getPage().setWidget(new PageH4(thiz, tab));
		if(DepositMenu.DM.equals(depositMenu)) getPage().setWidget(new PageH5(thiz, tab));
		if(DepositMenu.DC.equals(depositMenu)) getPage().setWidget(new PageH7(thiz));
		
		// MEMORIA
		if(DepositMenu.AE.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT1", false));
		if(DepositMenu.BP.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT2", false));
		if(DepositMenu.AR_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT3", false));
		if(DepositMenu.AR_CN.equals(depositMenu)) getPage().setWidget(new PageM3_2(thiz));
		if(DepositMenu.NRV.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT4", false));
		if(DepositMenu.IMIII_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT5", false));
		if(DepositMenu.IMIII_CN.equals(depositMenu)) getPage().setWidget(new PageM5_2(thiz, tab));
		if(DepositMenu.AF_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT6", false));
		if(DepositMenu.AF_CN.equals(depositMenu)) getPage().setWidget(new PageM6_2(thiz, tab));
		if(DepositMenu.PF_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT7", false));
		if(DepositMenu.PF_CN.equals(depositMenu)) getPage().setWidget(new PageM7_2(thiz, tab));
		if(DepositMenu.FP.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT8", false));
		if(DepositMenu.SF.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT9", false));
		if(DepositMenu.IG.equals(depositMenu)) getPage().setWidget(new PageM10(thiz, tab));
		if(DepositMenu.SDL_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT11", false));
		if(DepositMenu.SDL_CN.equals(depositMenu)) getPage().setWidget(new PageM11_2(thiz, tab));
		if(DepositMenu.OPV_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT12", false));
		if(DepositMenu.OPV_CN.equals(depositMenu)) getPage().setWidget(new PageM12_2(thiz, tab));
		if(DepositMenu.OI_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT13", false));
		if(DepositMenu.OI_CN.equals(depositMenu)) getPage().setWidget(new PageM13_2(thiz, tab));
		if(DepositMenu.IM_TL.equals(depositMenu)) getPage().setWidget(new FreeText(thiz, depositMenu.getDescription(), "MAT14", false));
		if(DepositMenu.IM_CN.equals(depositMenu)) getPage().setWidget(new PageM14_2(thiz, tab));
		if(DepositMenu.IA.equals(depositMenu)) getPage().setWidget(new PageM15(thiz, tab));
		
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
	private static final String ITR = "Identificador del titular real";
	private static final String SRA = "Documento sobre servicios a terceros";
	private static final String AR = "Aplicaci\u00f3n de resultados";
	private static final String CVA = "Declaraci\u00f3n Covid";
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
		
		if(year >= 2020 && year <= 2022){
			flex_table.setWidget(index, 0, new Label(CVA));
			CheckBox cbCVA = new CheckBox();cbCVA.setValue(true);
			flex_table.setWidget(index, 1, cbCVA);
			index++;
		}
		
    	if(year >= 2017) {
    		flex_table.setWidget(index, 0, new Label(ITR));
			CheckBox cbITR = new CheckBox();cbITR.setValue(true);
			flex_table.setWidget(index, 1, cbITR);
			index++;
    	}
    	
    	if(year >= 2018) {
    		flex_table.setWidget(index, 0, new Label(SRA));
			CheckBox cbSRA = new CheckBox();cbSRA.setValue(true);
			flex_table.setWidget(index, 1, cbSRA);
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
		AonDialog dialog = new AonDialog("Descargar Deposito", flex_table);
		dialog.setAutoHideEnabled(true);
		dialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept() {
				String options = "";
				for(Integer i = 1; i < flex_table.getRowCount(); i++){
					CheckBox cb = (CheckBox) flex_table.getWidget(i, 1);
					options = options + (cb.getValue() ? "T":"F");
				}
			
				String fileDownloadURL = GWT.getModuleBaseURL() + "/CCAAPrint"
	                	+ "?schemaId=" + String.valueOf(1)
	                	+ "&domainId=" + Integer.toString(getAonData().getDomain().getId())
	                	+ "&domainName=" + getAonData().getDomain().getName()
	                	+ "&user=" + getAonData().getUser().getLogin()
	                	+ "&cif=" + getCompany().getDocument()
						+ "&razonSocial=" + getCompany().getName()
						+ "&year=" + String.valueOf(year)
						+ "&type=" + getDeposit().get(D2DepositConstants.DEPOSIT_TYPE)
						+ "&options=" + options
						+ "&format=" + format
						+ "&isMemory=" + false;
				Window.open( fileDownloadURL, "_blank",null);
				dialog.hide();
			}
		});		
	}
	
	/***** CLICK HANDLER *****/
	
	private ClickHandler changeTypeClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {					
				ListBox acb = new ListBox();
				acb.addItem("Abreviado");
				acb.addItem("Pymes");
				acb.setSelectedIndex("Abreviado".equalsIgnoreCase(getType()) ? 0 : 1);
						
			   	AonDialog dialog = new AonDialog("Cambiar Tipo", acb);
			   	dialog.setAutoHideEnabled(true);
			   	dialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() {
						dialog.hide();
					}
					
					@Override
					public void onAccept() {
						dialog.hide();
						updateType(acb.getSelectedItemText());						
					}
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
							updateHeader(getDeposit().get(D2DepositConstants.DEPOSIT_TYPE), getYear());
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
							updateHeader(getDeposit().get(D2DepositConstants.DEPOSIT_TYPE), getYear());
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
				            	+ "&domain_name=" + DepositEntryPoint.getCurrentDomainName()
				            	+ "&year=" + year;
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
	
	private ClickHandler downloadPdfClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				download("pdf");
			}
		};
	}

	private ClickHandler resetClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AonDialog dialog = new AonDialog("Resetear Cuentas Anuales Ejercicio " + getYear(), new Label("Est\u00e1 seguro de resetear el dep\u00f3sito de cuentas Anuales del ejercicio " + getYear() + ". Se perder\u00e1n todos los datos almacenados hasta ahora."));
				dialog.setAutoHideEnabled(true);
				dialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() {
						dialog.hide();
					}
					
					@Override
					public void onAccept() {
						dialog.hide();

						final PopupPanel popup = new PopupPanel(false, true);
						popup.add(new AonSplash());
						popup.setGlassEnabled(true);
						popup.setAnimationEnabled(true);
						popup.center();
						
						getInma().reset(getAonData(), getCompany(), getYear(), new AsyncCallback<Map<String,String>>() {
							
							@Override
							public void onSuccess(Map<String, String> result) {
								Map<String, String> m = new HashMap<String, String>();
								for (String k : getDeposit().keySet()) {
									m.put(k, getDeposit().get(k));
								}
								getUndoStack().push(m);
								getRedoStack().clear();
								
								setDeposit(result);
								updateHeader(getDeposit().get(D2DepositConstants.DEPOSIT_TYPE), getYear());
								refreshPage();
								popup.hide();
							}
							
							@Override public void onFailure(Throwable caught) {
								popup.hide();
							}
						});						
					}
				});
			}
		};
	}
	
	
	// IMPORTAR ARCHIVO D2 - TIENE QUE SER UN ARCHIVO XML DEL PROGRAMA DEL DEPOSITO DIGITAL, ES DECIR UN ARCHIVO DEPOSITO.XML
	private ClickHandler importClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Upload upload = new Upload() {
					
					@Override
					protected void onUpload(String data, String type) {
						getInma().upload(getAonData(), data, type, getYear(), new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								getInma().getSchema(getAonData(), getCompany(), getYear(), false, new AsyncCallback<Map<String, String>>() {

									@Override public void onFailure(Throwable caught) {}

									@Override 
									public void onSuccess(Map<String, String> result) {
										Map<String, String> m = new HashMap<String, String>();
										for (String k : getDeposit().keySet()) {
											m.put(k, getDeposit().get(k));
										}
										getUndoStack().push(m);
										getRedoStack().clear();
										
										setDeposit(result);
										refreshPage();	
									}
								});
							}
							
							@Override
							public void onFailure(Throwable caught) {
								AonMessageDialog.error("Error al importar el archivo. Compruebe que el formato del archivo sea correcto y que corresponde al ejercicio seleccionado.");
							}
						});
					}
				};
				upload.upload();				
			}
		};
	}
	
	private ClickHandler importMemoryClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ListBox list = new ListBox();
				for (Integer i = 0 ; i < getMemoryTemplates().size(); i++) {
					MemoryTemplate memoryTemplate = getMemoryTemplates().get(i);
					list.addItem(memoryTemplate.getName(), memoryTemplate.getId() + "");
				}
								
				AonDialog dialog = new AonDialog("Importar Memoria", list);
				dialog.setAutoHideEnabled(true);
				dialog.confirm(new AonAcceptDialogCallback() {
						
					@Override
					public void onCancel() {
						dialog.hide();
					}
						
					@Override
					public void onAccept() {
						dialog.hide();
						String idStr = list.getSelectedValue();
						Integer id = Integer.parseInt(idStr);
				    	MemoryTemplate m = null;
				    	for(MemoryTemplate mt : getMemoryTemplates()) {	
				    		if(mt.getId().equals(id) || mt.getId() == id) m = mt;
				    	}
				    	if(m != null)
					    	
				    	getInma().updateTexts(getAonData(),m, getDeposit(), new AsyncCallback<Map<String, String>>() {

				    		@Override public void onFailure(Throwable caught) {}

							@Override
							public void onSuccess(Map<String, String> result) {
								Map<String, String> m = new HashMap<String, String>();
								for (String k : getDeposit().keySet()) { 
									m.put(k, getDeposit().get(k));
								}
								getUndoStack().push(m);
								getRedoStack().clear();
								
								setDeposit(result);
								getInma().saveDeposit(getAonData(), getDeposit(), getYear(), new AsyncCallback<Void>() {
										
									@Override
									public void onSuccess(Void result) {
										refreshPage();
									}
									
									@Override public void onFailure(Throwable caught) {}
								});
							}
						});
							
					}
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
	
	public List<MemoryTemplate> getMemoryTemplates() {
		if(memoryTemplates == null) 
			memoryTemplates = new LinkedList<>();
		return memoryTemplates;
	}
	
	public void setMemoryTemplates(List<MemoryTemplate> memoryTemplates) {
		this.memoryTemplates = memoryTemplates;
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
	
	public DepositHeader getHeader() {
		return header;
	}
	
	public void setHeader(DepositHeader header) {
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
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
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

package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.Toolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.DepositDialog;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.DownloadDialog;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemory;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemoryAsync;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory.FreeText;
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
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Enterprise;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.PaperIconButtonElement;
import com.vaadin.polymer.paper.widget.PaperItem;


public class Deposit extends AonTemplate2 {
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);

	DockLayoutPanel d2Content;
	FlexTable header;
	ScrollPanel page;
	
	AonData aonData;
	Company company;
	Map<String, String> deposit;
	Integer year;
	DepositMenu depositMenu;
	
	Stack<Map<String, String>> undoStack = new Stack<Map<String, String>>();
	Stack<Map<String, String>> redoStack = new Stack<Map<String, String>>();
	
	Deposit thiz = this;
	
	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				PaperIconButtonElement.SRC
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
		ScrollPanel sp = new ScrollPanel();
		
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		for(Integer y = AonDateUtils.getCurrentYear() - 1; y > 2013 ; y--) {
			PaperItem ej = buildItem("Ejercicio " + y, y.equals(getYear()) ? "arrow-drop-down":"arrow-drop-up", true);
			VerticalPanel ejContent = buildSubEjercicio(y);
			ej.addClickHandler(submenuClickHandler(ej, ejContent));
			vp.add(ej);
			vp.add(ejContent);
		}
		
		sp.setWidget(vp);

		setWestContent(sp);
	}
	
    public PaperItem buildItem(String text, String icon, Boolean title){
    	PaperItem pi = new PaperItem();
    	pi.setTitle(text);
    	if(icon != null) {
    		IronIcon ironIcon = new IronIcon();
    		ironIcon.setIcon(icon);
    		ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    		pi.add(ironIcon);
    	}
    	pi.add(new Label(text));
    	pi.setStyle("min-height:24px;font-size:12px;padding:0px;cursor:pointer;" + (title ? "font-weight:bold;" : "")); 
    	return pi;
    }

    
    public VerticalPanel buildSubEjercicio(Integer year){
    	VerticalPanel vp = new VerticalPanel();
    	
    	PaperItem his = buildItem(DepositMenu.HIS.getDescription(), null, false);
    	his.addClickHandler(menuClickHandler(DepositMenu.HIS, year));
    	vp.add(his);
    	
    	if(year >= 2016) {
    		PaperItem ar = buildItem(DepositMenu.AR.getDescription(), null, false);
    		ar.addClickHandler(menuClickHandler(DepositMenu.AR, year));
    		vp.add(ar);
    	}
    	
    	PaperItem bs = buildItem(DepositMenu.BS.getDescription(), null, false);
    	bs.addClickHandler(menuClickHandler(DepositMenu.BS, year));
    	vp.add(bs);
    	
    	PaperItem cpg = buildItem(DepositMenu.CPG.getDescription(), null, false);
    	cpg.addClickHandler(menuClickHandler(DepositMenu.CPG, year));
    	vp.add(cpg);
    	
    	if(year < 2016) {
    		PaperItem ecpn = buildItem(DepositMenu.ECPN.getDescription(), null, false);
        	ecpn.addClickHandler(menuClickHandler(DepositMenu.ECPN, year));
        	vp.add(ecpn);
    	}
    	
    	PaperItem dm = buildItem(DepositMenu.DM.getDescription(), null, false);
    	dm.addClickHandler(menuClickHandler(DepositMenu.DM, year));
    	vp.add(dm);
    	
    	PaperItem m = buildItem(DepositMenu.M.getDescription(), "arrow-drop-down", false);
    	VerticalPanel mContent = buildMemory(year);    
    	m.addClickHandler(submenuClickHandler(m, mContent));
    	vp.add(m);
    	vp.add(mContent);
    	
    	PaperItem d = buildItem(DepositMenu.D.getDescription(), null, false);
    	d.addClickHandler(menuClickHandler(DepositMenu.D, year));
    	vp.add(d);
    	
    	PaperItem ma = buildItem(DepositMenu.MA.getDescription(), "arrow-drop-down", false);
    	VerticalPanel maContent = buildMa(year);    
    	ma.addClickHandler(submenuClickHandler(ma, maContent));
    	vp.add(ma);
    	vp.add(maContent);
    	
    	PaperItem ip = buildItem(DepositMenu.IP.getDescription(), null, false);
    	ip.addClickHandler(menuClickHandler(DepositMenu.IP, year));
    	vp.add(ip);

    	PaperItem chd = buildItem(DepositMenu.CHD.getDescription(), null , false);
    	chd.addClickHandler(menuClickHandler(DepositMenu.CHD, year));
    	vp.add(chd);

    	vp.setVisible(year.equals(getYear()));
    	vp.setWidth("100%");
    	vp.getElement().getStyle().setMarginLeft(25, Unit.PX);
    	return vp;
    }
    
    public VerticalPanel buildMemory(Integer year){
    	VerticalPanel vp = new VerticalPanel();
    	Integer ap = 1;
    	PaperItem ae = buildItem("Apartado " + ap + ": " + DepositMenu.AE.getDescription(), null, false);
    	ae.addClickHandler(menuClickHandler(DepositMenu.AE, year));
    	vp.add(ae);
    	ap++;
    	
    	PaperItem bp = buildItem("Apartado " + ap + ": " + DepositMenu.BP.getDescription(), null, false);
    	bp.addClickHandler(menuClickHandler(DepositMenu.BP, year));
    	vp.add(bp);
    	ap++;
    	
    	if(year < 2016) {
        	PaperItem ar = buildItem("Apartado " + ap + ": " + DepositMenu.AR.getDescription(), "arrow-drop-down", false);
    		VerticalPanel arContent = buildMemoryItem(DepositMenu.AR_TL, DepositMenu.AR_CN, year);
        	ar.addClickHandler(submenuClickHandler(ar, arContent));
        	vp.add(ar);
        	vp.add(arContent);
        	ap++;
    	}
    	
    	PaperItem nrv = buildItem("Apartado " + ap + ": " + DepositMenu.NRV.getDescription(), null, false);
    	nrv.addClickHandler(menuClickHandler(DepositMenu.NRV, year));
    	vp.add(nrv);
    	ap++;

    	PaperItem imiii = buildItem("Apartado " + ap + ": " + DepositMenu.IMIII.getDescription(), "arrow-drop-down", false);
		VerticalPanel imiiiContent = buildMemoryItem(DepositMenu.IMIII_TL, DepositMenu.IMIII_CN, year);
    	imiii.addClickHandler(submenuClickHandler(imiii, imiiiContent));
    	vp.add(imiii);
    	vp.add(imiiiContent);
    	ap++;
    	
    	PaperItem af = buildItem("Apartado " + ap + ": " + DepositMenu.AF.getDescription(), "arrow-drop-down", false);
		VerticalPanel afContent = buildMemoryItem(DepositMenu.AF_TL, DepositMenu.AF_CN, year);
    	af.addClickHandler(submenuClickHandler(af, afContent));
    	vp.add(af);
    	vp.add(afContent);
    	ap++;
    	
    	PaperItem pf = buildItem("Apartado " + ap + ": " + DepositMenu.PF.getDescription(), "arrow-drop-down", false);
		VerticalPanel pfContent = buildMemoryItem(DepositMenu.PF_TL, DepositMenu.PF_CN, year);
    	pf.addClickHandler(submenuClickHandler(pf, pfContent));
    	vp.add(pf);
    	vp.add(pfContent);
    	ap++;
    	
    	PaperItem fp = buildItem("Apartado " + ap + ": " + DepositMenu.FP.getDescription(), null, false);
    	fp.addClickHandler(menuClickHandler(DepositMenu.FP, year));
    	vp.add(fp);
    	ap++;
    	
    	PaperItem sf = buildItem("Apartado " + ap + ": " + DepositMenu.SF.getDescription(), null, false);
    	sf.addClickHandler(menuClickHandler(DepositMenu.SF, year));
    	vp.add(sf);
    	ap++;
    	
    	if(year < 2016) {
    		PaperItem ig = buildItem("Apartado " + ap + ": " + DepositMenu.IG.getDescription(), null, false);
    		ig.addClickHandler(menuClickHandler(DepositMenu.IG, year));
    		vp.add(ig);
    		ap++;
    	

    		PaperItem sdl = buildItem("Apartado " + ap + ": " + DepositMenu.SDL.getDescription(), "arrow-drop-down", false);
    		VerticalPanel sdlContent = buildMemoryItem(DepositMenu.SDL_TL, DepositMenu.SDL_CN, year);
    		sdl.addClickHandler(submenuClickHandler(sdl, sdlContent));
    		vp.add(sdl);
    		vp.add(sdlContent);
    		ap++;
    	}
    	
    	PaperItem opv = buildItem("Apartado " + ap + ": " + DepositMenu.OPV.getDescription(), "arrow-drop-down", false);
		VerticalPanel opvContent = buildMemoryItem(DepositMenu.OPV_TL, DepositMenu.OPV_CN, year);
    	opv.addClickHandler(submenuClickHandler(opv, opvContent));
    	vp.add(opv);
    	vp.add(opvContent);
    	ap++;
    	
    	PaperItem oi = buildItem("Apartado " + ap + ": " + DepositMenu.OI.getDescription(), "arrow-drop-down", false);
		VerticalPanel oiContent = buildMemoryItem(DepositMenu.OI_TL, DepositMenu.OI_CN, year);
    	oi.addClickHandler(submenuClickHandler(oi, oiContent));
    	vp.add(oi);
    	vp.add(oiContent);
    	ap++;
    	
    	if(year < 2016) {
    		PaperItem im = buildItem("Apartado " + ap + ": " + DepositMenu.IM.getDescription(), "arrow-drop-down", false);
    		VerticalPanel imContent = buildMemoryItem(DepositMenu.IM_TL, DepositMenu.IM_CN, year);
    		im.addClickHandler(submenuClickHandler(im, imContent));
    		vp.add(im);
    		vp.add(imContent);
    		ap++;

    		PaperItem ia = buildItem("Apartado " + ap + ": " + DepositMenu.IA.getDescription(), null, false);
    		ia.addClickHandler(menuClickHandler(DepositMenu.IA, year));
    		vp.add(ia);
    		ap++;
    	}
    	
    	vp.setVisible(false);
    	vp.setWidth("100%");
    	vp.getElement().getStyle().setMarginLeft(25, Unit.PX);
    	return vp;
    }
    
    
    private VerticalPanel buildMemoryItem(DepositMenu tl, DepositMenu cn, Integer year) {
    	VerticalPanel vp = new VerticalPanel();

    	PaperItem pi1 = buildItem("Texto Libre", null, false);
    	pi1.addClickHandler(menuClickHandler(tl, year));
    	vp.add(pi1);
    	
    	PaperItem pi2 = buildItem("Cuadros Normalizados", null, false);
    	pi2.addClickHandler(menuClickHandler(cn, year));
    	vp.add(pi2);
    	
    	vp.setVisible(false);
    	vp.setWidth("100%");
    	vp.getElement().getStyle().setMarginLeft(25, Unit.PX);
    	return vp;
    }
    
    private VerticalPanel buildMa(Integer year) {
    	VerticalPanel vp = new VerticalPanel();
    	
    	PaperItem ma1 = buildItem(DepositMenu.MA1.getDescription(), null, false);
    	ma1.addClickHandler(menuClickHandler(DepositMenu.MA1, year));
    	vp.add(ma1);
    	
    	PaperItem ma11 = buildItem(DepositMenu.MA11.getDescription(), null, false);
    	ma11.addClickHandler(menuClickHandler(DepositMenu.MA11, year));
    	vp.add(ma11);
    	
    	PaperItem ma2 = buildItem(DepositMenu.MA2.getDescription(), null, false);
    	ma2.addClickHandler(menuClickHandler(DepositMenu.MA2, year));
    	vp.add(ma2);
    	
    	PaperItem ma3 = buildItem(DepositMenu.MA3.getDescription(), null, false);
    	ma3.addClickHandler(menuClickHandler(DepositMenu.MA3, year));
    	vp.add(ma3);
    	
    	PaperItem ma4 = buildItem(DepositMenu.MA4.getDescription(), null, false);
    	ma4.addClickHandler(menuClickHandler(DepositMenu.MA4, year));
    	vp.add(ma4);
    	
    	PaperItem ma5 = buildItem(DepositMenu.MA5.getDescription(), null, false);
    	ma5.addClickHandler(menuClickHandler(DepositMenu.MA5, year));
    	vp.add(ma5);
    	
    	PaperItem ma6 = buildItem(DepositMenu.MA6.getDescription(), null, false);
    	ma6.addClickHandler(menuClickHandler(DepositMenu.MA6, year));
    	vp.add(ma6);
    	
    	PaperItem ma7 = buildItem(DepositMenu.MA7.getDescription(), null, false);
    	ma7.addClickHandler(menuClickHandler(DepositMenu.MA7, year));
    	vp.add(ma7);

    	vp.setVisible(false);
    	vp.setWidth("100%");
    	vp.getElement().getStyle().setMarginLeft(25, Unit.PX);
    	return vp;
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
	
	private void updateHeader(String type, Integer year) {
		Label typeLabel = new Label(type);
		typeLabel.addClickHandler(changeTypeClickHandler());
		getHeader().setWidget(0, 2, typeLabel);
		getHeader().setWidget(1, 0, new Label(year.toString()));
	}
	
	private void updateType(String type) {
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
	
	private void updatePage(DepositMenu depositMenu) {
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
	
	private void download(String format){
		DownloadDialog dd = new DownloadDialog(false, false, getYear()) {
			
			@Override 
			protected void onCancel() {
				hide();
			}
			
			@Override
			protected void onAccept() {
				String options = "";
				for(Integer i = 1; i < getFlex_table().getRowCount(); i++){
					CheckBox cb = (CheckBox) getFlex_table().getWidget(i, 1);
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
		dd.addStyleName("gwt-PopupPanel-template");
		dd.setGlassEnabled(true);
		dd.center();
	}
	
	private void importAll() {
		inma.getDigitalDepositTemplates(getAonData().getDomain().getParentId(), getYear(), new AsyncCallback<Vector<MemoryTemplate>>() {

			@Override
			public void onSuccess(Vector<MemoryTemplate> result) {
				String url = GWT.getModuleBaseURL();
				Enterprise e = new Enterprise().setDocument(getCompany().getDocument()).setName(getCompany().getName()).setDomain(getAonData().getDomain().getId());
				DepositDialog popup = new DepositDialog("Importar", "importAll", e,url, result, false, null, year) {
									
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						hide();
						ListBox lb = (ListBox) flex_table.getWidget(0,1);
						String t = lb.getSelectedItemText();
						String ejercicio = "";
						if(t.equals("Memoria predefinida")){
							ListBox lb1 = (ListBox) flex_table.getWidget(1,1);
							String text = lb1.getSelectedItemText();
							MemoryTemplate m = new MemoryTemplate();
							
							for (MemoryTemplate mt : result) {
								if (mt.getName().equals(text))
									m = mt;
								}				
								inma.updateTexts(getAonData(),m, getFreeTextMap(), getAonData().getDomain().getId(), getCompany().getDocument(), getDeposit(), new AsyncCallback<Map<String, String>>() {

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
										inma.saveDeposit(getAonData(), getDeposit(), getYear(), new AsyncCallback<Void>() {
											
											@Override
											public void onSuccess(Void result) {
												refreshPage();
											}
											
											@Override public void onFailure(Throwable caught) {}
										});
									}
								});
							} else {
								if(t.equals("Balance (I.S.)")){
									ListBox ej = (ListBox) flex_table.getWidget(2, 1);
									ejercicio = ej.getSelectedItemText();
								} else if(t.equals("Perdidas y ganancias (I.S.)")){
									ListBox ej = (ListBox) flex_table.getWidget(2, 1);
									ejercicio = ej.getSelectedItemText();		
								} else if(t.equals("ECPN (I.S.)")){
									ListBox ej = (ListBox) flex_table.getWidget(2, 1);
									ejercicio = ej.getSelectedItemText();				
								} else if(t.equals("Memoria (Deposito.xml)")){
									ListBox ej = (ListBox) flex_table.getWidget(1, 1);
									ejercicio = ej.getSelectedItemText();
								}
								inma.importAll(getAonData(), t, ejercicio, null, getAonData().getDomain().getId(), getCompany().getDocument(), getDeposit(), getYear(), new AsyncCallback<Map<String, String>>() {
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
										inma.saveDeposit(getAonData(), getDeposit(), getYear(), new AsyncCallback<Void>() {
											
											@Override
											public void onSuccess(Void result) {
												refreshPage();
											}
											
											@Override public void onFailure(Throwable caught) {}
										});
									}
								});
							}
					}
				};
				popup.addStyleName("gwt-PopupPanel-template");
				popup.setGlassEnabled(true);
				popup.center();
			}
			
			@Override public void onFailure(Throwable caught) {}	
		});
			
	}
	
	/***** CLICK HANDLER *****/
	
	private ClickHandler changeTypeClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Window.alert("Cambiar Tipo");
			}
		};
	}
	
	private ClickHandler menuClickHandler(DepositMenu depositMenu, Integer year) {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(year.equals(getYear())) {
					updatePage(depositMenu);
				} else {
					getUndoStack().clear();
					getRedoStack().clear();
					setYear(year);
					inma.getSchema(getAonData(), getCompany(), getYear(), false, new AsyncCallback<Map<String, String>>() {
						@Override
						public void onSuccess(Map<String, String> result) {
							setDeposit(result);
							updateHeader(getDeposit().get(D2DepositConstants.DEPOSIT_TYPE), getYear());
							updatePage(depositMenu);
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
				}
			}
		};
	}
	
	private ClickHandler submenuClickHandler(PaperItem item, VerticalPanel content) {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				content.setVisible(!content.isVisible());
				IronIcon ironIcon = (IronIcon) item.getWidget(0);
				ironIcon.setIcon(content.isVisible() ? "arrow-drop-down" : "arrow-drop-up");
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
				importAll();
			}
		};
	}
	
	
	
	/***** GETTERS & SETTERS *****/
	
	public INormalizedMemoryAsync getInma() {
		return inma;
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

	public Deposit(AonData aonData) {
		this.aonData = aonData;
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

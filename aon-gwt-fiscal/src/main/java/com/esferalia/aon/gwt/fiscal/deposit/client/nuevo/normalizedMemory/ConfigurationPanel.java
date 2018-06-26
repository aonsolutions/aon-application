package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory;

import java.util.Map;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.fiscal.JsDepositConfiguration;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.Deposit;
import com.esferalia.aon.gwt.fiscal.deposit.shared.DepositMenu;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.iron.widget.event.IronSelectEvent;
import com.vaadin.polymer.iron.widget.event.IronSelectEventHandler;
import com.vaadin.polymer.paper.widget.PaperItem;
import com.vaadin.polymer.paper.widget.PaperTabs;

import net.aonsolutions.polymer.aon.widget.AonComboBox;

public class ConfigurationPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, ConfigurationPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);
    
    @UiField HTMLPanel panel;
    @UiField HTMLPanel tabContent;
    @UiField PaperTabs tabs;

    Deposit parent; 
    
    private static final String ZERO = "0";
    private static final String ONE = "1";
    
    public ConfigurationPanel(Deposit parent) {
    	this.parent = parent;
    	initWidget(binder.createAndBindUi(this));
    	tabs.setSelected("0");
        tabs.addIronSelectHandler(new IronSelectEventHandler() {
			
			@Override
			public void onIronSelect(IronSelectEvent event) {
				for(Integer i = 0; i < tabContent.getWidgetCount(); i++)
					tabContent.remove(i);
				ScrollPanel sp = new ScrollPanel();
				Integer h = Window.getClientHeight() -190;
				sp.getElement().getStyle().setHeight(h, Unit.PX);
		    	Window.addResizeHandler(new ResizeHandler() {
					
					@Override
					public void onResize(ResizeEvent event) {
						Integer h = Window.getClientHeight() -190;
						sp.getElement().getStyle().setHeight(h, Unit.PX);
					}
				});
				if(tabs.getSelected().toString().equals(ZERO)
					|| tabs.getSelected().toString() == ZERO){
					sp.add(menu());
				} else if(tabs.getSelected().toString().equals(ONE)
					|| tabs.getSelected().toString() == ONE){
					parent.getAPI().getFiscal().getDepositConfiguration(new AsyncCallback<JSON<JsDepositConfiguration>>() {
						
						@Override
						public void onSuccess(JSON<JsDepositConfiguration> result) {
							sp.add(configuration(result.getData().get(0)));
						}
						
						@Override
						public void onFailure(Throwable caught) {}
					});
					
				}
				tabContent.add(sp);
			}
		});       
    }
    
    private Widget menu() {
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		for(Integer y = AonDateUtils.getCurrentYear() - 1; y > 2013 ; y--) {
			PaperItem ej = buildItem("Ejercicio " + y, y.equals(parent.getYear()) ? "arrow-drop-down":"arrow-drop-up", true);
			VerticalPanel ejContent = buildSubEjercicio(y);
			ej.addClickHandler(submenuClickHandler(DepositMenu.HIS, ej, ejContent, y));
			vp.add(ej);
			vp.add(ejContent);
		}

		return vp;
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
    	
    	if(year >= 2017) {
    		PaperItem itr = buildItem(DepositMenu.ITR.getDescription(), null, false);
    		itr.addClickHandler(menuClickHandler(DepositMenu.ITR, year));
    		vp.add(itr);
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
    	m.addClickHandler(submenuClickHandler(DepositMenu.M, m, mContent, year));
    	vp.add(m);
    	vp.add(mContent);
    	
    	PaperItem d = buildItem(DepositMenu.D.getDescription(), null, false);
    	d.addClickHandler(menuClickHandler(DepositMenu.D, year));
    	vp.add(d);
    	
    	PaperItem ma = buildItem(DepositMenu.MA.getDescription(), "arrow-drop-down", false);
    	VerticalPanel maContent = buildMa(year);    
    	ma.addClickHandler(submenuClickHandler(DepositMenu.MA, ma, maContent, year));
    	vp.add(ma);
    	vp.add(maContent);
    	
    	PaperItem ip = buildItem(DepositMenu.IP.getDescription(), null, false);
    	ip.addClickHandler(menuClickHandler(DepositMenu.IP, year));
    	vp.add(ip);

    	PaperItem chd = buildItem(DepositMenu.CHD.getDescription(), null , false);
    	chd.addClickHandler(menuClickHandler(DepositMenu.CHD, year));
    	vp.add(chd);

    	vp.setVisible(year.equals(parent.getYear()));
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
        	ar.addClickHandler(submenuClickHandler(DepositMenu.AR, ar, arContent, year));
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
    	imiii.addClickHandler(submenuClickHandler(DepositMenu.IMIII, imiii, imiiiContent, year));
    	vp.add(imiii);
    	vp.add(imiiiContent);
    	ap++;
    	
    	PaperItem af = buildItem("Apartado " + ap + ": " + DepositMenu.AF.getDescription(), "arrow-drop-down", false);
		VerticalPanel afContent = buildMemoryItem(DepositMenu.AF_TL, DepositMenu.AF_CN, year);
    	af.addClickHandler(submenuClickHandler(DepositMenu.AF, af, afContent, year));
    	vp.add(af);
    	vp.add(afContent);
    	ap++;
    	
    	PaperItem pf = buildItem("Apartado " + ap + ": " + DepositMenu.PF.getDescription(), "arrow-drop-down", false);
		VerticalPanel pfContent = buildMemoryItem(DepositMenu.PF_TL, DepositMenu.PF_CN, year);
    	pf.addClickHandler(submenuClickHandler(DepositMenu.PF, pf, pfContent, year));
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
    		sdl.addClickHandler(submenuClickHandler(DepositMenu.SDL, sdl, sdlContent, year));
    		vp.add(sdl);
    		vp.add(sdlContent);
    		ap++;
    	}
    	
    	PaperItem opv = buildItem("Apartado " + ap + ": " + DepositMenu.OPV.getDescription(), "arrow-drop-down", false);
		VerticalPanel opvContent = buildMemoryItem(DepositMenu.OPV_TL, DepositMenu.OPV_CN, year);
    	opv.addClickHandler(submenuClickHandler(DepositMenu.OPV, opv, opvContent, year));
    	vp.add(opv);
    	vp.add(opvContent);
    	ap++;
    	
    	PaperItem oi = buildItem("Apartado " + ap + ": " + DepositMenu.OI.getDescription(), "arrow-drop-down", false);
		VerticalPanel oiContent = buildMemoryItem(DepositMenu.OI_TL, DepositMenu.OI_CN, year);
    	oi.addClickHandler(submenuClickHandler(DepositMenu.OI, oi, oiContent, year));
    	vp.add(oi);
    	vp.add(oiContent);
    	ap++;
    	
    	if(year < 2016) {
    		PaperItem im = buildItem("Apartado " + ap + ": " + DepositMenu.IM.getDescription(), "arrow-drop-down", false);
    		VerticalPanel imContent = buildMemoryItem(DepositMenu.IM_TL, DepositMenu.IM_CN, year);
    		im.addClickHandler(submenuClickHandler(DepositMenu.IM, im, imContent, year));
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
    
    private ClickHandler menuClickHandler(DepositMenu depositMenu, Integer year) {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				menuClick(depositMenu, year);
			}
		};
	}
	
    private void menuClick(DepositMenu depositMenu, Integer year) {
    	if(year.equals(parent.getYear())) {
			parent.updatePage(depositMenu);
		} else {
			parent.getUndoStack().clear();
			parent.getRedoStack().clear();
			parent.setYear(year);
			parent.getInma().getSchema(parent.getAonData(), parent.getCompany(), parent.getYear(), false, new AsyncCallback<Map<String, String>>() {
				@Override
				public void onSuccess(Map<String, String> result) {
					parent.setDeposit(result);
					parent.updateHeader(parent.getDeposit().get(D2DepositConstants.DEPOSIT_TYPE), parent.getYear());
					parent.updatePage(depositMenu);
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		}
	}
    
	private ClickHandler submenuClickHandler(DepositMenu depositMenu, PaperItem item, VerticalPanel content, Integer year) {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				content.setVisible(!content.isVisible());
				IronIcon ironIcon = (IronIcon) item.getWidget(0);
				ironIcon.setIcon(content.isVisible() ? "arrow-drop-down" : "arrow-drop-up");
				if(DepositMenu.MA.equals(depositMenu)) {
					menuClick(depositMenu, year);
				}
			}
		};
	}
    
    private Widget configuration(JsDepositConfiguration configuration) {
    	VerticalPanel vp = new VerticalPanel();
    	
    	AonComboBox acb = new AonComboBox();
       	acb.setItemLabelPath("name");
    	acb.setItemValuePath("name");
    	acb.setItems(configuration.getOperationOption());
    	acb.setInputElementValue(configuration.getOperation());
    	acb.setStyle("padding-left:20px;padding-right:20px;padding-bottom: 20px; width:250px;");
    	acb.setLabel("Tipo Dep\u00f3sito (por defecto)");
    	acb.addChangeHandler(new net.aonsolutions.polymer.aon.widget.event.ChangeEventHandler() {
			
			@Override
			public void onChange(net.aonsolutions.polymer.aon.widget.event.ChangeEvent event) {
				if(!acb.getInputElementValue().equals("")){
					String requestData= "{\"operation\":\""+ acb.getInputElementValue() +"\"}";
					parent.getAPI().getFiscal().setDepositConfiguration(requestData);
				}
			}
		});
    	
    	vp.add(acb);
    	return vp;
	}
}

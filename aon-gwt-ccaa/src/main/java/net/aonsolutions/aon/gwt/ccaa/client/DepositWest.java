package net.aonsolutions.aon.gwt.ccaa.client;

import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMenu;
import com.esferalia.aon.gwt.common.shared.AonMenuItem;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ScrollPanel;

import net.aonsolutions.aon.gwt.ccaa.shared.DepositMenu;
import net.aonsolutions.aon.gwt.ccaa.shared.MemoryTemplate;

public class DepositWest extends ScrollPanel{

	public static final int WIDTH = 250;
	public static final String APARTADO = "Apartado ";
	public static final String EJERCICIO = "Ejercicio ";
	
	AonMenu aonMenu;
    Deposit2 parent;
    DepositTextMode parentTextMode;
    List<MemoryTemplate> templates;
    boolean textMode;

	public DepositWest(Deposit2 parent) {
		super();
		this.parent = parent;
		this.textMode = false;
		init();	
	}
	
	public DepositWest(DepositTextMode parent, List<MemoryTemplate> templates) {
		super();
		this.parentTextMode = parent;
		this.textMode = true;
		this.templates = templates;
		init();	
	}
	
	public void reload(List<MemoryTemplate> templates) {
		this.templates = templates;
		init();
	}
	
	private void init() {
		aonMenu = new AonMenu();
		if(textMode) {
			for(Integer i = 0; i < templates.size(); i++) {
				aonMenu.addItem(buildTemplate(templates.get(i)));
			}
		} else {
			for(Integer y = AonDateUtils.getCurrentYear() - 1; y > 2013 ; y--) {
				aonMenu.addItem(buildYear(y));
			}
		}
		setWidget(aonMenu);
	}
	
	public void addTemplate(MemoryTemplate template) {
		aonMenu.addItem(buildTemplate(template));
	}
	
    private ClickHandler menuClickHandler(DepositMenu depositMenu, Integer year) {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				menuClick(depositMenu, year);
			}
		};
    }
    
    private ClickHandler menuTextModeClickHandler(DepositMenu depositMenu, MemoryTemplate template) {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				menuTextModeClick(depositMenu, template);
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

    private void menuTextModeClick(DepositMenu depositMenu, MemoryTemplate template) {
    	if(template.getId().equals(parentTextMode.getId())) {
    		parentTextMode.updatePage(depositMenu);
		} else {
			parentTextMode.getUndoStack().clear();
			parentTextMode.getRedoStack().clear();
			parentTextMode.setId(template.getId());
			parentTextMode.setName(template.getName());
			
			parentTextMode.getInma().getSchemaTextMode(parentTextMode.getAonData(), parentTextMode.getId(), new AsyncCallback<Map<String, String>>() {
				@Override
				public void onSuccess(Map<String, String> result) {
					parentTextMode.setDeposit(result);
					parentTextMode.updatePage(depositMenu);
					parentTextMode.updateHeader();
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		}
	}
    
	private AonMenuItem buildYear(Integer year) {
		AonMenuItem item = new AonMenuItem()
			.setTitle(EJERCICIO + year);
		
		item.addItem(new AonMenuItem().setTitle(DepositMenu.HIS.getDescription())
				.setHandler(menuClickHandler(DepositMenu.HIS, year)));
				
    	if(year >= 2016) {
    		item.addItem(new AonMenuItem().setTitle(DepositMenu.AR.getDescription())
    				.setHandler(menuClickHandler(DepositMenu.AR, year)));
    	}
    	
    	if(year >= 2020 && year < 2022) {
    		item.addItem(new AonMenuItem().setTitle(DepositMenu.DC.getDescription())
    				.setHandler(menuClickHandler(DepositMenu.DC, year)));
    	}
    	
    	if(year >= 2017) {
    		item.addItem(new AonMenuItem().setTitle(DepositMenu.ITR.getDescription())
    				.setHandler(menuClickHandler(DepositMenu.ITR, year)));
    	}
    	
    	if(year >= 2018) {
    		item.addItem(new AonMenuItem().setTitle(DepositMenu.SRA.getDescription())
    				.setHandler(menuClickHandler(DepositMenu.SRA, year)));
    	}
    	
		item.addItem(new AonMenuItem().setTitle(DepositMenu.BS.getDescription())
				.setHandler(menuClickHandler(DepositMenu.BS, year)));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.CPG.getDescription())
				.setHandler(menuClickHandler(DepositMenu.CPG, year)));
    	
    	if(year < 2016) {
    		item.addItem(new AonMenuItem().setTitle(DepositMenu.ECPN.getDescription())
    				.setHandler(menuClickHandler(DepositMenu.ECPN, year)));
    	}
		item.addItem(new AonMenuItem().setTitle(DepositMenu.DM.getDescription())
				.setHandler(menuClickHandler(DepositMenu.DM, year)));

		item.addItem(buildMemory(year));

		item.addItem(new AonMenuItem().setTitle(DepositMenu.D.getDescription())
				.setHandler(menuClickHandler(DepositMenu.D, year)));

		item.addItem(buildMa(year));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.IP.getDescription())
				.setHandler(menuClickHandler(DepositMenu.IP, year)));
		
		item.addItem(new AonMenuItem().setTitle(DepositMenu.CHD.getDescription())
				.setHandler(menuClickHandler(DepositMenu.CHD, year)));

    	return item;
	}
	
	private AonMenuItem buildMemory(Integer year) {
		AonMenuItem item = new AonMenuItem().setTitle(DepositMenu.M.getDescription());

		Integer ap = 1;
		item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.AE.getDescription())
				.setHandler(menuClickHandler(DepositMenu.AE, year)));
		ap++;
		
		item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.BP.getDescription())
				.setHandler(menuClickHandler(DepositMenu.BP, year)));
		ap++;
		
    	
    	if(year < 2016) {
    		item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.AR.getDescription())
    			.addItem(new AonMenuItem().setTitle(DepositMenu.AR_TL.getDescription())
    					.setHandler(menuClickHandler(DepositMenu.AR_TL, year)))
    			.addItem(new AonMenuItem().setTitle(DepositMenu.AR_CN.getDescription())
    					.setHandler(menuClickHandler(DepositMenu.AR_CN, year)))
    		);
    		ap++;
    	}
    	
    	item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.NRV.getDescription())
    			.setHandler(menuClickHandler(DepositMenu.NRV, year)));
		ap++;

		item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.IMIII.getDescription())
    		.addItem(new AonMenuItem().setTitle(DepositMenu.IMIII_TL.getDescription())
    				.setHandler(menuClickHandler(DepositMenu.IMIII_TL, year)))
    		.addItem(new AonMenuItem().setTitle(DepositMenu.IMIII_CN.getDescription())
    				.setHandler(menuClickHandler(DepositMenu.IMIII_CN, year)))
    	);
    	ap++;
    	
    	item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.AF.getDescription())
        	.addItem(new AonMenuItem().setTitle(DepositMenu.AF_TL.getDescription())
        			.setHandler(menuClickHandler(DepositMenu.AF_TL, year)))
        	.addItem(new AonMenuItem().setTitle(DepositMenu.AF_CN.getDescription())
        			.setHandler(menuClickHandler(DepositMenu.AF_CN, year)))
        );
        ap++;

        item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.PF.getDescription())
           	.addItem(new AonMenuItem().setTitle(DepositMenu.PF_TL.getDescription())
           			.setHandler(menuClickHandler(DepositMenu.PF_TL, year)))
           	.addItem(new AonMenuItem().setTitle(DepositMenu.PF_CN.getDescription())
           			.setHandler(menuClickHandler(DepositMenu.PF_CN, year)))
        );
        ap++;

    	item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.FP.getDescription())
    			.setHandler(menuClickHandler(DepositMenu.FP, year)));
    	ap++;
    
    	item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.SF.getDescription())
    			.setHandler(menuClickHandler(DepositMenu.SF, year)));
    	ap++;
    	
    	if(year < 2016) {
        	item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.IG.getDescription())
        			.setHandler(menuClickHandler(DepositMenu.IG, year)));
        	ap++;
        	
        	item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.SDL.getDescription())
               	.addItem(new AonMenuItem().setTitle(DepositMenu.SDL_TL.getDescription())
               			.setHandler(menuClickHandler(DepositMenu.SDL_TL, year)))
               	.addItem(new AonMenuItem().setTitle(DepositMenu.SDL_CN.getDescription())
               			.setHandler(menuClickHandler(DepositMenu.SDL_CN, year)))
            );
            ap++;
    	}
    	
    	item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.OPV.getDescription())
           	.addItem(new AonMenuItem().setTitle(DepositMenu.OPV_TL.getDescription())
           			.setHandler(menuClickHandler(DepositMenu.OPV_TL, year)))
          	.addItem(new AonMenuItem().setTitle(DepositMenu.OPV_CN.getDescription())
          			.setHandler(menuClickHandler(DepositMenu.OPV_CN, year)))
        );
        ap++;
    	
        item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.OI.getDescription())
        	.addItem(new AonMenuItem().setTitle(DepositMenu.OI_TL.getDescription())
        			.setHandler(menuClickHandler(DepositMenu.OI_TL, year)))
            .addItem(new AonMenuItem().setTitle(DepositMenu.OI_CN.getDescription())
            		.setHandler(menuClickHandler(DepositMenu.OI_CN, year)))
        );
        ap++;
    	
    	if(year < 2016) {
            item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.IM.getDescription())
              	.addItem(new AonMenuItem().setTitle(DepositMenu.IM_TL.getDescription())
              			.setHandler(menuClickHandler(DepositMenu.IM_TL, year)))
                .addItem(new AonMenuItem().setTitle(DepositMenu.IM_CN.getDescription())
                		.setHandler(menuClickHandler(DepositMenu.IM_CN, year)))
            );
            ap++;
    	
            item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.IA.getDescription())
            		.setHandler(menuClickHandler(DepositMenu.IA, year)));
    	}
    	
    	return item;
	}
	
	private AonMenuItem buildMa(Integer year) {
		AonMenuItem item = new AonMenuItem().setTitle(DepositMenu.MA.getDescription())
				.setHandler(menuClickHandler(DepositMenu.MA, year));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA1.getDescription())
				.setHandler(menuClickHandler(DepositMenu.MA1, year)));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA11.getDescription())
				.setHandler(menuClickHandler(DepositMenu.MA11, year)));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA2.getDescription())
				.setHandler(menuClickHandler(DepositMenu.MA2, year)));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA3.getDescription())
				.setHandler(menuClickHandler(DepositMenu.MA3, year)));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA4.getDescription())
				.setHandler(menuClickHandler(DepositMenu.MA4, year)));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA5.getDescription())
				.setHandler(menuClickHandler(DepositMenu.MA5, year)));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA6.getDescription())
				.setHandler(menuClickHandler(DepositMenu.MA6, year)));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA7.getDescription())
				.setHandler(menuClickHandler(DepositMenu.MA7, year)));
    	return item;
	}
	
	private AonMenuItem buildTemplate(MemoryTemplate memoryTemplate) {
		AonMenuItem item = new AonMenuItem().setTitle(memoryTemplate.getName());
		item.addItem(new AonMenuItem().setTitle(DepositMenu.AE.getDescription())
				.setHandler(menuTextModeClickHandler(DepositMenu.AE, memoryTemplate)));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.BP.getDescription())
				.setHandler(menuTextModeClickHandler(DepositMenu.BP, memoryTemplate)));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.AR.getDescription())
				.setHandler(menuTextModeClickHandler(DepositMenu.AR_TL, memoryTemplate)));
    	item.addItem(new AonMenuItem().setTitle(DepositMenu.NRV.getDescription())
    			.setHandler(menuTextModeClickHandler(DepositMenu.NRV, memoryTemplate)));
    	item.addItem(new AonMenuItem().setTitle(DepositMenu.IMIII.getDescription())
				.setHandler(menuTextModeClickHandler(DepositMenu.IMIII_TL, memoryTemplate)));
    	item.addItem(new AonMenuItem().setTitle(DepositMenu.AF.getDescription())
				.setHandler(menuTextModeClickHandler(DepositMenu.AF_TL, memoryTemplate)));
    	item.addItem(new AonMenuItem().setTitle(DepositMenu.PF.getDescription())
				.setHandler(menuTextModeClickHandler(DepositMenu.PF_TL, memoryTemplate)));
    	item.addItem(new AonMenuItem().setTitle(DepositMenu.FP.getDescription())
				.setHandler(menuTextModeClickHandler(DepositMenu.FP, memoryTemplate)));
    	item.addItem(new AonMenuItem().setTitle(DepositMenu.SF.getDescription())
    			.setHandler(menuTextModeClickHandler(DepositMenu.SF, memoryTemplate)));
    	item.addItem(new AonMenuItem().setTitle(DepositMenu.SDL.getDescription())
				.setHandler(menuTextModeClickHandler(DepositMenu.SDL_TL, memoryTemplate)));
    	item.addItem(new AonMenuItem().setTitle(DepositMenu.OPV.getDescription())
				.setHandler(menuTextModeClickHandler(DepositMenu.OPV_TL, memoryTemplate)));
    	item.addItem(new AonMenuItem().setTitle(DepositMenu.OI.getDescription())
				.setHandler(menuTextModeClickHandler(DepositMenu.OI_TL, memoryTemplate)));
    	item.addItem(new AonMenuItem().setTitle(DepositMenu.IM.getDescription())
				.setHandler(menuTextModeClickHandler(DepositMenu.IM_TL, memoryTemplate)));
    	return item;
	}

}

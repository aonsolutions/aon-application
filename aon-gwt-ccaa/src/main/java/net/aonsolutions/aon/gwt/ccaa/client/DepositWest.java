package net.aonsolutions.aon.gwt.ccaa.client;

import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMenu;
import com.esferalia.aon.gwt.common.shared.AonMenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;

import net.aonsolutions.aon.gwt.ccaa.shared.DepositMenu;

public class DepositWest extends ScrollPanel{

	public static final int WIDTH = 250;
	public static final String APARTADO = "Apartado ";
	public static final String EJERCICIO = "Ejercicio ";
	public DepositWest() {
		super();
		init();
		
	}
	
	private void init() {
		AonMenu aonMenu = new AonMenu();
		for(Integer y = AonDateUtils.getCurrentYear() - 1; y > 2013 ; y--) {
			aonMenu.addItem(buildYear(y));
		}
		setWidget(aonMenu);
	}
	
	private AonMenuItem buildYear(Integer year) {
		AonMenuItem item = new AonMenuItem()
			.setTitle(EJERCICIO + year);
		
		item.addItem(new AonMenuItem().setTitle(DepositMenu.HIS.getDescription()));
//				s.setHandler(chapter1_1Handler()))
		
    	if(year >= 2016 && year < 2018) {
    		item.addItem(new AonMenuItem().setTitle(DepositMenu.AR.getDescription()));
    	}
    	
    	if(year >= 2017) {
    		item.addItem(new AonMenuItem().setTitle(DepositMenu.ITR.getDescription()));
    	}
    	
    	if(year >= 2018) {
    		item.addItem(new AonMenuItem().setTitle(DepositMenu.SRA.getDescription()));
    	}
    	
		item.addItem(new AonMenuItem().setTitle(DepositMenu.BS.getDescription()));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.CPG.getDescription()));
    	
    	if(year < 2016) {
    		item.addItem(new AonMenuItem().setTitle(DepositMenu.ECPN.getDescription()));
    	}
		item.addItem(new AonMenuItem().setTitle(DepositMenu.DM.getDescription()));

		item.addItem(buildMemory(year));

		item.addItem(new AonMenuItem().setTitle(DepositMenu.D.getDescription()));

		item.addItem(buildMa(year));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.IP.getDescription()));
		
		item.addItem(new AonMenuItem().setTitle(DepositMenu.CHD.getDescription()));

    	return item;
	}
	
	private AonMenuItem buildMemory(Integer year) {
		AonMenuItem item = new AonMenuItem().setTitle(DepositMenu.M.getDescription());

		Integer ap = 1;
		item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.AE.getDescription()));
		ap++;
		
		item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.BP.getDescription()));
		ap++;
		
    	
    	if(year < 2016) {
    		item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.AR.getDescription())
    			.addItem(new AonMenuItem().setTitle(DepositMenu.AR_TL.getDescription()))
    			.addItem(new AonMenuItem().setTitle(DepositMenu.AR_CN.getDescription()))
    		);
    		ap++;
    	}
    	
    	item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.NRV.getDescription()));
		ap++;

		item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.IMIII.getDescription())
    		.addItem(new AonMenuItem().setTitle(DepositMenu.IMIII_TL.getDescription()))
    		.addItem(new AonMenuItem().setTitle(DepositMenu.IMIII_CN.getDescription()))
    	);
    	ap++;
    	
    	item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.AF.getDescription())
        	.addItem(new AonMenuItem().setTitle(DepositMenu.AF_TL.getDescription()))
        	.addItem(new AonMenuItem().setTitle(DepositMenu.AF_CN.getDescription()))
        );
        ap++;

        item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.PF.getDescription())
           	.addItem(new AonMenuItem().setTitle(DepositMenu.PF_TL.getDescription()))
           	.addItem(new AonMenuItem().setTitle(DepositMenu.PF_CN.getDescription()))
        );
        ap++;

    	item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.FP.getDescription()));
    	ap++;
    
    	item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.SF.getDescription()));
    	ap++;
    	
    	if(year < 2016) {
        	item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.IG.getDescription()));
        	ap++;
        	
        	item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.SDL.getDescription())
               	.addItem(new AonMenuItem().setTitle(DepositMenu.SDL_TL.getDescription()))
               	.addItem(new AonMenuItem().setTitle(DepositMenu.SDL_CN.getDescription()))
            );
            ap++;
    	}
    	
    	item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.OPV.getDescription())
           	.addItem(new AonMenuItem().setTitle(DepositMenu.OPV_TL.getDescription()))
          	.addItem(new AonMenuItem().setTitle(DepositMenu.OPV_CN.getDescription()))
        );
        ap++;
    	
        item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.OI.getDescription())
        	.addItem(new AonMenuItem().setTitle(DepositMenu.OI_TL.getDescription()))
            .addItem(new AonMenuItem().setTitle(DepositMenu.OI_CN.getDescription()))
        );
        ap++;
    	
    	if(year < 2016) {
            item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.IM.getDescription())
              	.addItem(new AonMenuItem().setTitle(DepositMenu.IM_TL.getDescription()))
                .addItem(new AonMenuItem().setTitle(DepositMenu.IM_CN.getDescription()))
            );
            ap++;
    	
            item.addItem(new AonMenuItem().setTitle(APARTADO + ap + ": " + DepositMenu.IA.getDescription()));
    	}
    	
    	return item;
	}
	
	private AonMenuItem buildMa(Integer year) {
		AonMenuItem item = new AonMenuItem().setTitle(DepositMenu.MA.getDescription());
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA1.getDescription()));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA11.getDescription()));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA2.getDescription()));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA3.getDescription()));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA4.getDescription()));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA5.getDescription()));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA6.getDescription()));
		item.addItem(new AonMenuItem().setTitle(DepositMenu.MA7.getDescription()));
    	return item;
	}
}

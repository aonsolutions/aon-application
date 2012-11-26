package com.code.aon.ui.ebackoffice.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.IItemConstants;

public class CatalogueOptionalControllerListener extends ControllerAdapter implements
		IItemConstants {

//	private static final Logger LOGGER = Logger
//			.getLogger(CatalogueOptionalControllerListener.class.getName());

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
	
	/*	try {
			Integer id =((Catalogue) this.getController().getTo()).getId();		
			IManagerBean bean = BeanManager.getManagerBean(Eccatalogue.class);
			String idCatalog = bean.getFieldName(IEbackofficeAlias.ECCATALOGUE_CATALOGUE_ID);
			Criteria cri1 = new Criteria();
			cri1.addEqualExpression(idCatalog, id);			
			EccatalogueController con = (EccatalogueController)FormUtil.getController("eccatalogue");
			con.setCriteria(cri1);
			if(con.getModel()!=null){
			con.onSelectFirst(null);
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		*/		
	}
	

	
	
	

}
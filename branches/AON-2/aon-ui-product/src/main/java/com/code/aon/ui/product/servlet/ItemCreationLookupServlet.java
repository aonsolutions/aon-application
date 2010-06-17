package com.code.aon.ui.product.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ILookupObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.ItemPos;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.lookup.LookupServlet;
import com.code.aon.ui.common.lookup.LookupUtils;
import com.code.aon.ui.product.util.ItemPriceProvider;

/**
 * Servlet used with the lookup of <code>Item</code>.
 * 
 */
public class ItemCreationLookupServlet extends LookupServlet {
	
	/** IDS_PARAMETER. */
	private static final String IDS_PARAMETER = "ids";
    
	/** ITEM_TOTAL_PRICE. */
	private static final String ITEM_TOTAL_PRICE = "Item_total_price";
    
    /** ITEM_BASE_PRICE. */
    private static final String ITEM_BASE_PRICE = "Item_base_price";
    
    /**
	 * Do get. Retrieves the data required by the lookup from the database
	 * 
	 * @param req the request
	 * @param res the response
	 * 
	 * @throws IOException the IO exception
	 * @throws ServletException the servlet exception
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Criteria criteria = new Criteria();
		String ids = req.getParameter(IDS_PARAMETER);
		Item item = null;
		try {
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_PRODUCT_CODE),req.getParameter(VALUE_PARAMETER));
			List<ITransferObject> list = itemBean.getList(criteria);
			if (list.size() > 0) {
				ILookupObject ito = (ILookupObject) list.get(0);
				item = (Item)ito;
			}else{
				criteria = new Criteria();
				IManagerBean itemPosBean = BeanManager.getManagerBean(ItemPos.class);
				criteria.addEqualExpression(itemPosBean.getFieldName(IProductAlias.ITEM_POS_BARCODE),req.getParameter(VALUE_PARAMETER));
				list = itemPosBean.getList(criteria);
		        if(list.size() > 0) {
		        	ItemPos itemPos = (ItemPos) list.get(0);
		        	item = itemPos.getItem();
		        }else{
		        	criteria = new Criteria();
		        	criteria.addEqualExpression(itemPosBean.getFieldName(IProductAlias.ITEM_POS_PLU),req.getParameter(VALUE_PARAMETER));
					list = itemPosBean.getList(criteria);
			        if(list.size() > 0) {
			        	ItemPos itemPos = (ItemPos) list.get(0);
			        	item = itemPos.getItem();
			        }
		        }
			}
			if(item == null){
				renderVoidResponse(res, ids);
			}else{
				renderResponse(res, item, ids);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage(), e);
		}
	}

	/**
	 * Adds custom entries into the lookup map.
	 * 
	 * @param ito the ILookupObject
	 * @param map the map
	 */
    protected void customizeLookupMap(ILookupObject ito, Map<String,Object> map) {
        ItemPriceProvider provider = new ItemPriceProvider();

        map.put(ITEM_TOTAL_PRICE, new Double(provider.getRealTotalPrice((Item)ito)));
        map.put(ITEM_BASE_PRICE, new Double(provider.getRealBasePrice((Item)ito)));
    }
    
    /**
     * Renders an empty response.
     * 
     * @param ids the ids
     * @param res the response
     * 
     * @throws IOException the IO exception
     */
    protected void renderVoidResponse(HttpServletResponse res, String ids) throws IOException {
		res.setContentType(CONTENT_TYPE);
		res.setCharacterEncoding(CHARACTER_ENCODING);
		Map<String,Object> map = new HashMap<String,Object>();
        map.put(IProductAlias.ITEM_ID, null);
        res.getOutputStream().print(LookupUtils.getResponseXML(map, ids));
		res.flushBuffer();
	}
}
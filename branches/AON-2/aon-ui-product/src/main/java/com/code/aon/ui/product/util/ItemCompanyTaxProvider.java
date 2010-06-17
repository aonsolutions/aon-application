package com.code.aon.ui.product.util;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.product.Item;
import com.code.aon.product.Tax;

/**
 * Provider to obtain tax related info.
 */
public class ItemCompanyTaxProvider {

    /** The LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(ItemCompanyTaxProvider.class.getName());

    /**
     * Gets the tax list of an item.
     * 
     * @param to the to
     * 
     * @return the tax list
     */
    public List getTaxList(ITransferObject to) {
        Item item = (Item)to;
        List<Tax> l = new LinkedList<Tax>();

        l.add(item.getProduct().getVat());
        return l;
    }

    /**
     * Checks if a surcharge has to be applied to the company.
     * 
     * @return true, if a surcharge has to be applied to the company.
     */
    public boolean isSurcharge() {
        IManagerBean companyBean;
        try {
            companyBean = BeanManager.getManagerBean(Company.class);

            List list = companyBean.getList(null);
            Iterator iter = list.iterator();
            if (iter.hasNext()) {
                return ((Company) iter.next()).isSurcharge();
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining is surcharged", e);
        }
        return false;
    }

    /**
     * Gets the tax list of an item.
     * 
     * @param to the to
     * @param date the date
     * 
     * @return the tax list
     */
    @SuppressWarnings("unused")
	public List getTaxList(ITransferObject to, Date date) {
		return getTaxList(to);
	}
}
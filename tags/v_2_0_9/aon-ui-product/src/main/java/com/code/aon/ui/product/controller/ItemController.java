package com.code.aon.ui.product.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.GridController;
import com.code.aon.ui.form.PageDataModel;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.product.util.ItemPriceProvider;

/**
 * Controller used in the item maintenance.
 */
public class ItemController extends GridController {

    /** The LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(ItemController.class.getName());
	
	/** The Price provider. */
	private ItemPriceProvider provider;
	
	/**
	 * The empty constructor.
	 */
	public ItemController() {
		super();
	}
	
	/**
	 * On start. Method launched by the menu. Clears the model
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unused")
	public void onStart(MenuEvent event) throws ManagerBeanException{
		((PageDataModel)this.getModel()).resize(0);
	}

    /**
     * Adds an equal expression of the field category to the <code>criteria</code>.
     * 
     * @param event the event
     * 
     * @throws ManagerBeanException the manager bean exception
     */
    public void addCategoryCriteria(ValueChangeEvent event) throws ManagerBeanException {
    	clearCriteria();
    	this.setModel(new PageDataModel(this,20));
	    if (event.getNewValue() != null) {
	        getCriteria().addEqualExpression(getManagerBean().getFieldName(IProductAlias.ITEM_PRODUCT_CATEGORY_ID), event.getNewValue()); 
	    }
    }
    
	/**
	 * Adds an equal expression to the <code>criteria</code> retrieving the field name from the <code>event</code>.
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addEqualExpression(ValueChangeEvent event)
		throws ManagerBeanException {
        if (event.getNewValue() != null) {
        	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addEqualExpression(getFieldName(event.getComponent().getId()), value);
			setCriteria(c);
		}
	}

	/**
	 * Gets the tax percentage.
	 * 
	 * @return the tax percentage
	 */
	public Double getTaxPercentage() {
		double d = (this.getTo() == null)?0:getProvider().getTax((Item)this.getTo()); 
		return new Double(d);
	}

	/**
	 * Gets the tax surcharge.
	 * 
	 * @return the tax surcharge
	 */
	public Double getTaxSurcharge() {
		double d = (this.getTo() == null)?0:getProvider().getSurcharge((Item)this.getTo());
		return new Double(d);
    }

	/**
	 * Gets the purchase price of the current item.
	 * 
	 * @return the purchase price
	 */
	public double getPurchasePrice() {
		return (this.getTo() == null?0:getProvider().getPurchasePrice((Item)this.getTo()));
	}
	
	/**
	 * Sets the purchase price. Empty method
	 * 
	 * @param price the price
	 */
	@SuppressWarnings("unused")
	public void setPurchasePrice(double price) {
	}
	
	/**
	 * Gets the base price of the current item.
	 * 
	 * @return the base price
	 */
	public double getBasePrice() {
        return(this.getTo() == null)?0:getProvider().getBasePrice((Item)this.getTo());
	}

    /**
     * Gets the real base price of the current item.
     * 
     * @return the real base price
     */
    public double getRealBasePrice() {
        return(this.getTo() == null)?0:getProvider().getRealBasePrice((Item)this.getTo());
    }

    /**
     * Gets the profit percent2 of the current item.
     * 
     * @return the profit percent2
     */
    public double getProfitPercent2() {
        return(this.getTo() == null)?0:getProvider().getProfitPercent2((Item)this.getTo());
    }

    /**
     * Gets the taxable base of the current item.
     * 
     * @return the taxable base
     */
    public double getTaxableBase() {
		return(this.getTo() == null)?0:getProvider().getTaxableBase((Item)this.getTo());
	}
	
	/**
	 * Gets the tax rate of the current item.
	 * 
	 * @return the tax rate
	 */
	public double getTaxRate() {
		return(this.getTo() == null)?0:getProvider().getTaxRate((Item)this.getTo());
	}
	
	/**
	 * Gets the surcharge rate of the current item.
	 * 
	 * @return the surcharge rate
	 */
	public double getSurchargeRate() {
		return(this.getTo() == null)?0:getProvider().getSurchargeRate((Item)this.getTo());
	}
	
    /**
     * Gets the total price of the current item..
     * 
     * @return the total price
     */
    public double getTotalPrice() {
        return(this.getTo() == null)?0:getProvider().getTotalPrice((Item)this.getTo());
    }

    /**
     * Gets the real total price of the current item..
     * 
     * @return the real total price
     */
    public double getRealTotalPrice() {
        return(this.getTo() == null)?0:getProvider().getRealTotalPrice((Item)this.getTo());
    }

	/**
	 * Checks if a surcharge has to be applied
	 * 
	 * @return true, if a surcharge has to be applied
	 */
	public boolean isSurcharge() {
		return getProvider().isSurcharge();
	}

// ***************************** PRICES *************************************
    
    /**
	 * Gets the tax percentage of the item contained in the current row of the model.
	 * 
	 * @return the row index tax percentage
	 */
	public Double getRowIndexTaxPercentage() {
        try {
        	double d = getProvider().getTax((Item) this.getModel().getRowData()); 
            return new Double(d);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting TaxPercentage", e);
            return new Double(0.0);
        }
    }
    
    /**
     * Sets the row index tax percentage. Empty Method
     * 
     * @param value the value
     */
    @SuppressWarnings("unused")
    public void setRowIndexTaxPercentage(Double value) {
    }

    /**
     * Gets the tax surcharge of the item contained in the current row of the model.
     * 
     * @return the row index tax surcharge
     */
    public Double getRowIndexTaxSurcharge() {
        try {
        	double d = getProvider().getSurcharge((Item)this.getModel().getRowData()); 
            return new Double(d); 
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting TaxPercentage", e);
            return new Double(0.0);
        }
    }

    /**
     * Sets the row index purchase price. Empty method
     * 
     * @param value the value
     */
    @SuppressWarnings("unused")
    public void setRowIndexPurchasePrice(double value) {
    }

    /**
     * Gets the purchase price of the item contained in the current row of the model.
     * 
     * @return the row index purchase price
     */
    public double getRowIndexPurchasePrice() {
        try {
            return getProvider().getPurchasePrice((Item)this.getModel().getRowData());
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting BasePrice", e);
            return 0;
        }
    }

    /**
     * Gets the real base price of the item contained in the current row of the model.
     * 
     * @return the row index real base price
     */
    public double getRowIndexRealBasePrice() {
        try {
            return getProvider().getRealBasePrice((Item)this.getModel().getRowData());
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting BasePrice", e);
            return 0;
        }
    }

    /**
     * Gets the base price of the item contained in the current row of the model.
     * 
     * @return the row index base price
     */
    public double getRowIndexBasePrice() {
        try {
            return getProvider().getBasePrice((Item)this.getModel().getRowData());
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting BasePrice", e);
            return 0;
        }
    }

    /**
     * Gets the profit percent2 of the item contained in the current row of the model.
     * 
     * @return the row index profit percent2
     */
    public double getRowIndexProfitPercent2() {
        try {
            return getProvider().getProfitPercent2((Item)this.getModel().getRowData());
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting ProfitPercent2", e);
            return 0;
        }
    }

    /**
     * Gets the profit percent of the item contained in the current row of the model.
     * 
     * @return the row index profit percent
     */
    public double getRowIndexProfitPercent() {
        try {
            return getProvider().getProfitPercent((Item)this.getModel().getRowData());
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting ProfitPercent", e);
            return 0;
        }
    }

    /**
     * Sets the row index profit percent. Empty method
     * 
     * @param value the value
     */
    @SuppressWarnings("unused")
    public void setRowIndexProfitPercent(double value) {
    }

    /**
     * Sets the row index profit percent2. Empty method
     * 
     * @param value the value
     */
    @SuppressWarnings("unused")
    public void setRowIndexProfitPercent2(double value) {
    }

    /**
     * Gets the total price of the item contained in the current row of the model.
     * 
     * @return the row index total price
     */
    public double getRowIndexTotalPrice() {
        try {
            return getProvider().getRealTotalPrice((Item)this.getModel().getRowData());
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting TotalPrice", e);
            return 0;
        }
    }

    /**
     * Sets the row index total price. Empty method
     * 
     * @param totalPrice the total price
     */
    @SuppressWarnings("unused")
    public void setRowIndexTotalPrice(double totalPrice) {
    }

//  ***************************** PRICES *************************************

    /**
	 * Gets the Price provider.
	 * 
	 * @return the provider
	 */
	public ItemPriceProvider getProvider() {
		    if (provider == null) {
				provider = new ItemPriceProvider();
	        }
		    return provider;
    }

	/**
	 * Updates current item, an selects the next item in the model if it exists
	 * 
	 * @param event the event
	 */
	public void onAcceptNext(ActionEvent event) {
		accept(event);
		try {
			int current = getSelectedIndex();
			int max = getModel().getRowCount();
			if ((current+1) < max){
				getModel().setRowIndex(current+1);
				onSelect(event);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

    /**
     * On reset. Method launched by the menu
     * 
     * @param event the event
     */
    public void onReset(MenuEvent event) {
        super.onReset(event);
    }
}
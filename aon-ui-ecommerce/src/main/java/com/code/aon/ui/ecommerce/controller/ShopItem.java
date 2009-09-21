package com.code.aon.ui.ecommerce.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tax;
import com.code.aon.ebackoffice.Ecconfig;
import com.code.aon.product.Item;
import com.code.aon.product.ItemAttachment;
import com.code.aon.product.Product;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.AttachmentType;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.strategy.BasicPriceStrategy;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;

public class ShopItem implements ICalculable{
	
	private Item item;
	private ItemAttachment thumbnail;
	private Double price;
	private Double vat;
	private Double total;
	private Double discount;
	private Double discountPercent;
	
	public ShopItem(Item item) {
		this.item = item;
	}

	public Ecconfig getConfig() {
		ConfigController cc = (ConfigController) AonUtil.getRegisteredBean(IECommerceConstants.CONFIG_CONTROLLER);
		return cc.getActiveConfig();
	}


	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Integer getId() {
		return getItem().getId();
	}

	public void setId(Integer id) {
		throw new IllegalAccessError("Solo lectura.");
	}

	public String getDetail() {
		return getItem().getDetail();
	}

	public void setDetail(String detail) {
		throw new IllegalAccessError("Solo lectura.");
	}

	public String getDescription() {
		return getItem().getDescription();
	}

	public void setDescription(String description) {
		throw new IllegalAccessError("Solo lectura.");
	}

	public double getOriginalPrice() {
		return getItem().getPrice();
	}
	
	public Double getDiscount() {
		if (discount == null) {
			discount = CommonUtil.round(getOriginalPrice() - getPrice());
		}
		return discount;
	}

	public Double getDiscountPercent() {
		if (discountPercent == null) {
			discountPercent = CommonUtil.round(getPrice()*getDiscount()/100);
		}
		return discountPercent;
	}

	public double getPrice() {
		if (price == null) {
			initializePrices();
		}
		return price;
	}
	public double getVat() {
		if (vat == null) {
			initializePrices();
		}
		return vat;
	}
	public double getTotal() {
		if (total == null) {
			initializePrices();
		}
		return total;
	}
	
	private void initializePrices() {
		BasicPriceStrategy strategy = new BasicPriceStrategy();
		price = strategy.getUnitPrice(this,new Date(), null);
		Tax tax = getItem().getProduct().getVat();
		vat = CommonUtil.round(price*tax.getPercentage()/100);
		total = CommonUtil.round(price + vat);
	}
	
	public void setPrice(double price) {
		throw new IllegalAccessError("Solo lectura.");
	}

	public Product getProduct() {
		return getItem().getProduct();
	}

	public void setProduct(Product product) {
		throw new IllegalAccessError("Solo lectura.");
	}

	public ProductStatus getStatus() {
		return getItem().getStatus();
	}

	public void setStatus(ProductStatus status) {
		throw new IllegalAccessError("Solo lectura.");
	}

	public double getExpensesFixed() {
		return getItem().getExpensesFixed();
	}

	public void setExpensesFixed(double expensesFixed) {
		throw new IllegalAccessError("Solo lectura.");
	}

	public double getExpensesPercent() {
		return getItem().getExpensesPercent();
	}

	public void setExpensesPercent(double expensesPercent) {
		throw new IllegalAccessError("Solo lectura.");
	}

	public double getProfitPercent() {
		return getItem().getProfitPercent();
	}

	public void setProfitPercent(double profitPercent) {
		throw new IllegalAccessError("Solo lectura.");
	}

	public double getPurchasePrice() {
		return getItem().getPurchasePrice();
	}

	public void setPurchasePrice(double purchasePrice) {
		throw new IllegalAccessError("Solo lectura.");
	}

	public ItemAttachment getThumbnail() {
		if (thumbnail == null) {
			try {
				IManagerBean attachBean = AonUtil.getManagerBean(ItemAttachment.class);
				Criteria criteria = new Criteria();
				String itemAlias = attachBean.getFieldName(IProductAlias.ITEM_ATTACHMENT_ITEM_ID);
				String typeAlias = attachBean.getFieldName(IProductAlias.ITEM_ATTACHMENT_TYPE);
				criteria.addEqualExpression(itemAlias, getItem().getId());
				criteria.addEqualExpression(typeAlias, AttachmentType.THUMBNAIL);
				List<ITransferObject> list = attachBean.getList(criteria);
				if (list != null && list.size() > 0) {
					thumbnail = (ItemAttachment) list.get(0);
				}
			} catch (ManagerBeanException e) {
				// Nada. La foto no se ve y punto.
			}
		}
		return thumbnail;
	}

	public void setThumbnail(ItemAttachment thumbnail) {
		this.thumbnail = thumbnail;
	}

	public void paintThumbnail(OutputStream out, Object data) {
		try {
			if (getThumbnail() != null) {
				out.write(getThumbnail().getData());
			} 
		} catch (IOException e) {
			// Nada. La foto no se ve y punto.
		}
	}

	@Override
	public DiscountExpression getDiscountExpression() {
		return new DiscountExpression("0");
	}

	@Override
	public double getQuantity() {
		return 1;
	}

	@Override
	public double getTaxes() throws ManagerBeanException {
		return 0;
	}
	
}

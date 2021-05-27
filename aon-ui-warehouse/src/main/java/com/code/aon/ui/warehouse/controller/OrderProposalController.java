package com.code.aon.ui.warehouse.controller;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.product.ProductCategory;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Warehouse;

public class OrderProposalController implements ICollectionProvider, Serializable{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Warehouse warehouse;
	private ProductCategory category;
	
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public ProductCategory getCategory() {
		return category;
	}
	public void setCategory(ProductCategory category) {
		this.category = category;
	}
	public String getBeanName() {
		return "orderProposal";
	}

	public void onEditSearch(ActionEvent event) {
		setWarehouse(null);
		setCategory(null);
	}
	
	@Override
	public Collection<?> getCollection() {
		try {
			return getCollection(false);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<?> getCollection(boolean forceRefresh) throws ManagerBeanException {
		Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
	        String stmt = 
	    		"SELECT "
	    		+"  p.code,p.name,c.id,c.name,w.name,iw.warehouse,iw.stock_min,iw.stock_max,IFNULL(s.quantity,0)"
	    		+" FROM item_warehouse iw"
	    		+" INNER JOIN item i ON iw.item = i.id"
	    		+" INNER JOIN product p ON i.product = p.id"
	    		+" LEFT OUTER JOIN pcategory c ON p.category = c.id"
	    		+" LEFT OUTER JOIN stock s ON iw.item = s.item AND iw.warehouse = s.warehouse"
	    		+" INNER JOIN warehouse w ON iw.warehouse = w.id"
	    		+" WHERE " + DomainManager.getSQLWhereClause("iw.domain")
	    		+" AND IFNULL(s.quantity,0) <= iw.stock_min";
	        if (getWarehouse() != null) {
	        	stmt += " AND iw.warehouse = " + getWarehouse().getId();
	        }
	        if (getCategory() != null) {
	        	stmt += " AND c.id = " + getCategory().getId();
	        }
	        stmt += " ORDER BY iw.warehouse,c.id";
			ps = conn.prepareStatement(stmt,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
			List<OrderProposal> list = new LinkedList<OrderProposal>(); 
			while (rs.next()) {
				OrderProposal op = new OrderProposal();
				op.setCode(rs.getString(1));
				op.setDescription(rs.getString(2));
				op.setCategoryId(rs.getInt(3));
				op.setCategory(rs.getString(4));
				op.setWarehouse(rs.getString(5));
				op.setWarehouseId(rs.getInt(6));
				op.setStockMin(rs.getDouble(7));
				op.setStockMax(rs.getDouble(8));
				op.setStock(rs.getDouble(9));
				list.add(op);
			}
			return list;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}

	public class OrderProposal {
		private String code;
		private String description;
		private Integer categoryId;
		private String category;
		private Integer warehouseId;
		private String warehouse;
		private Double stockMin;
		private Double stockMax;
		private Double stock;
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Integer getCategoryId() {
			return categoryId;
		}
		public void setCategoryId(Integer categoryId) {
			this.categoryId = categoryId;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public Integer getWarehouseId() {
			return warehouseId;
		}
		public void setWarehouseId(Integer warehouseId) {
			this.warehouseId = warehouseId;
		}
		public String getWarehouse() {
			return warehouse;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public Double getStockMin() {
			return stockMin;
		}
		public void setStockMin(Double stockMin) {
			this.stockMin = stockMin;
		}
		public Double getStockMax() {
			return stockMax;
		}
		public void setStockMax(Double stockMax) {
			this.stockMax = stockMax;
		}
		public Double getStock() {
			return stock;
		}
		public void setStock(Double stock) {
			this.stock = stock;
		}
		
		
	}
}

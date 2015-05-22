package com.code.aon.ui.warehouse.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.product.Item;
import com.code.aon.product.ProductCategory;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.enumeration.IncomeStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class IncomeSearchListener extends RegistrySearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String REGISTRY_SEARCH_PREFFIX = "Income_supplier_registry_";

	private Supplier supplier;

	private IncomeStatus[] incomeStatuses;
	
    private Item item;
    
    private WorkPlace workPlace;
    
    private Warehouse warehouse;
    
    private Project project;
    
    private ProductCategory category;
    
	public String getPreffix() throws ManagerBeanException {
		return REGISTRY_SEARCH_PREFFIX;
	}		
	
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public IncomeStatus[] getIncomeStatuses() {
		return incomeStatuses;
	}

	public void setIncomeStatuses(IncomeStatus[] incomeStatuses) {
		this.incomeStatuses = incomeStatuses;
	}
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}		
	
	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	
	public List<SelectItem> getWarehouses() throws ManagerBeanException {
		WorkPlace workPlace = getWorkPlace();
		LinkedList<SelectItem> warehouses = new LinkedList<SelectItem>();
		if(workPlace!=null){
			IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
			Criteria criteria = new Criteria();
			Expression exp1 = ExpressionUtilities.getNullExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE));
			Expression exp2 = ExpressionUtilities.getEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE_ID), workPlace.getId());
			criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
			criteria.addOrder(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_NAME));
			List<ITransferObject> c = warehouseBean.getList(criteria);
			Iterator<ITransferObject> iter = c.iterator();
			while (iter.hasNext()) {
				Warehouse warehouse = (Warehouse) iter.next();
				SelectItem item = new SelectItem(warehouse, warehouse.getName());
				warehouses.add(item);
			}
		}
		/*else{
			IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_NAME));
			List<ITransferObject> c = warehouseBean.getList(criteria);
			Iterator<ITransferObject> iter = c.iterator();
			while (iter.hasNext()) {
				Warehouse warehouse = (Warehouse) iter.next();
				SelectItem item = new SelectItem(warehouse, warehouse.getName());
				warehouses.add(item);
			}
			
		}*/
		return warehouses;
	}

	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setSupplier((Supplier)BeanManager.getManagerBean(Supplier.class).createNewTo());
		IncomeStatus[] defaultIncomeStatus = {IncomeStatus.PENDING};
		setIncomeStatuses(defaultIncomeStatus);
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
		setProject((Project)BeanManager.getManagerBean(Project.class).createNewTo());
		setCategory( (ProductCategory) BeanManager.getManagerBean(ProductCategory.class).createNewTo() );
		setWarehouse((Warehouse) BeanManager.getManagerBean(Warehouse.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		CompanyCollectionsController controller = (CompanyCollectionsController) AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		super.completeCriteria( criteria);
		if (getWorkPlace() != null && getWorkPlace().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.INCOME_WORK_PLACE_ID), getWorkPlace().getId());			
		} else {
			criteria.addInExpression(getFieldName(IEntityAlias.INCOME_WORK_PLACE_ID), controller.getCurrentUserWorkPlacesIds());
		}
		if (getSupplier() != null && getSupplier().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.INCOME_SUPPLIER_ID), getSupplier().getId());			
		}
		if (!ArrayUtils.isEmpty(getIncomeStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.INCOME_STATUS);
			addEnumToCriteria(criteria, status, getIncomeStatuses());
		}
		if ((getItem() != null) && (getItem().getId() != null)) {
			criteria.addEqualExpression("Income.lines.item.id", getItem().getId());
		}
		if ((getProject() != null) && (getProject().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.INCOME_PROJECT_ID), getProject().getId());			
		}
		if (getCategory() != null && getCategory().getId() != null) {
			criteria.addEqualExpression(getController().resolveAlias("Income_lines_item_product_category<id"), getCategory().getId());
		}		
		if (getWarehouse() != null && getWarehouse().getId() != null) {
			criteria.addEqualExpression(getController().resolveAlias("Income_lines_warehouse_id"), getWarehouse().getId());
		}
	}	
}
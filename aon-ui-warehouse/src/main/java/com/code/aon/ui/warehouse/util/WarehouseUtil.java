package com.code.aon.ui.warehouse.util;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.project.Project;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.enumeration.IncomeStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class WarehouseUtil extends CompanyEmailUtil {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public Income createIncome(Purchase purchase, String referenceCode) throws ManagerBeanException {
		Income income = new Income();
		income.setPayMethod(null);
		income.setProject(purchase.getProject());
		income.setWorkPlace(purchase.getWorkPlace());
		income.setRegistryAddress(purchase.getRegistryAddress());
		income.setScope(purchase.getScope());
		income.setSupplier(purchase.getSupplier());
		income.setReferenceCode(referenceCode);
		income.setIssueTime(purchase.getIssueDate());
		income.setSecurityLevel(purchase.getSecurityLevel());
		income.setStatus(IncomeStatus.PENDING);
		income.setComments(null);
		income.setRemarks("Orden de elaboración " + purchase.getReferenceCode());
		income.setNumberOfPayments(purchase.getNumberOfPayments());
		income.setDaysToFirstPayment(purchase.getDaysToFirstPayment());
		income.setDaysBetweenPayments(purchase.getDaysBetweenPayments());
		income.setPaymentDays(purchase.getPaymentDays());
		income.setBankAccount(purchase.getBankAccount());
		income.setBankAlias(purchase.getBankAlias());
		income.setBic(purchase.getBic());
		income = (Income) BeanManager.getManagerBean(Income.class).insert(
				income);
		return income;
	}

	public void createIncomeDetail(PurchaseDetail purchaseDetail,
			Income income, double totalQuantity) throws ManagerBeanException {
		createIncomeDetail(income.getProject(), purchaseDetail.getPurchase()
				.getWarehouse(), purchaseDetail, income,
				purchaseDetail.getItem(), calculateNextLine(income),
				purchaseDetail.getDescription(), purchaseDetail.getQuantity(),
				purchaseDetail.getPrice(),
				purchaseDetail.getDiscountExpression());
	}

	public void createIncomeDetail(Warehouse warehouse, Income income,
			ItemComposition composition, double quantity)
			throws ManagerBeanException {
		createIncomeDetail(income.getProject(), warehouse, null, income,
				composition.getCompositionItem(), calculateNextLine(income),
				composition.getDescription(), quantity * (-1), composition
						.getCompositionItem().getPrice(),
				composition.getDiscountExpression());
	}

	private void createIncomeDetail(Project project, Warehouse warehouse,
			PurchaseDetail purchaseDetail, Income income, Item item,
			Integer line, String description, double quantity, double price,
			DiscountExpression discountExpression) throws ManagerBeanException {
		IncomeDetail detail = new IncomeDetail();
		detail.setProject(project);
		detail.setWarehouse(warehouse);
		detail.setPurchaseDetail(purchaseDetail);
		detail.setIncome(income);
		detail.setItem(item);
		detail.setLine(line);
		detail.setDescription(description);
		detail.setQuantity(quantity);
		detail.setPrice(price);
		detail.setDiscountExpression(discountExpression);
		BeanManager.getManagerBean(IncomeDetail.class).insert(detail);
	}

	private Integer calculateNextLine(Income income)
			throws ManagerBeanException {
		IManagerBean detailBean = BeanManager
				.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				detailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID),
				income.getId());
		Projection projection = Projection.max(detailBean
				.getFieldName(IEntityAlias.INCOME_DETAIL_LINE));
		Object value = detailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer) value) + 1 : 1;
	}

}

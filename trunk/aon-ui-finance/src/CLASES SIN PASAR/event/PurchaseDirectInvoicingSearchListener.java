package temp.com.code.aon.ui.finance.deprecated.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class PurchaseDirectInvoicingSearchListener extends ControllerSearchListener {
	
	private Supplier supplier;

	private Date issueDateFrom;
	
	private Date issueDateTo;

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Date getIssueDateFrom() {
		return issueDateFrom;
	}

	public void setIssueDateFrom(Date issueDateFrom) {
		this.issueDateFrom = issueDateFrom;
	}

	public Date getIssueDateTo() {
		return issueDateTo;
	}

	public void setIssueDateTo(Date issueDateTo) {
		this.issueDateTo = issueDateTo;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setSupplier( new Supplier() );
		setIssueDateFrom(null);
		setIssueDateTo(null);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		criteria.addEqualExpression(getFieldName(IFinanceAlias.INVOICE_TYPE), InvoiceType.PURCHASE);
		if ( (getSupplier() != null) && (getSupplier().getId() != null) ) {
			String field = getController().getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID);
			criteria.addEqualExpression(field, getSupplier().getId());			
		}			
		if ( getIssueDateFrom() != null ) {
			String field = getController().getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE);
			criteria.addGreaterThanOrEqualExpression(field, getIssueDateFrom());
		}
		if ( getIssueDateTo() != null ) {
			String field = getController().getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE);
			criteria.addLessThanOrEqualExpression(field, getIssueDateTo());
		}
	}	
	
	
}
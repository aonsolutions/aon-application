package com.code.aon.ui.finance.fbatch.print;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.print.ReportFinanceBatch;
import com.code.aon.ui.finance.controller.FBatchController;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.form.FormUtil;

public class FBatchPrinter implements ICollectionProvider, IFinanceConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FBatchPrinter.class.getName());
	
	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		List<ReportFinanceBatch> reportFBatchList = new LinkedList<ReportFinanceBatch>();
		try {
			FBatchController fBatchController = (FBatchController)FormUtil.getController(FINANCE_BATCH_CONTROLLER_NAME);
			Iterator iter = ((List)fBatchController.getModel().getWrappedData()).iterator();
			while(iter.hasNext()){
				FinanceBatch fBatch = (FinanceBatch)iter.next();
				ReportFinanceBatch rFBatch = new ReportFinanceBatch();
				rFBatch.setFinanceBatch(fBatch);
				rFBatch.setRegs(fBatchController.getFinanceBatchTotalDetails(fBatch));
				rFBatch.setTotal(fBatchController.getFinanceBatchTotalAmount(fBatch));
				reportFBatchList.add(rFBatch);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining FinanceBatchList Collection", e);
		}
		return reportFBatchList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		return getCollection();
	}
	
}

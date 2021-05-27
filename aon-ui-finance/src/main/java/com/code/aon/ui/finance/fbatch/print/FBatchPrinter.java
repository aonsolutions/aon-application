package com.code.aon.ui.finance.fbatch.print;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.print.ReportFinanceBatch;
import com.code.aon.ui.finance.controller.FBatchController;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.form.FormUtil;

public class FBatchPrinter implements ICollectionProvider, IFinanceConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FBatchPrinter.class.getName());
	
	public Collection<?> getCollection() {
		List<ReportFinanceBatch> reportFBatchList = new LinkedList<ReportFinanceBatch>();
		try {
			FBatchController fBatchController = (FBatchController)FormUtil.getController(FINANCE_BATCH_CONTROLLER_NAME);
			List<ITransferObject> list = fBatchController.getManagerBean().getList(fBatchController.getCriteria());
			for (ITransferObject ito : list ){
				ReportFinanceBatch rFBatch = new ReportFinanceBatch();
				rFBatch.setFinanceBatch((FinanceBatch)ito);
				reportFBatchList.add(rFBatch);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining FinanceBatchList Collection", e);
		}
		return reportFBatchList;
	}

	@Override
	public Collection<?> getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
	
}

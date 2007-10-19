package com.code.aon.ui.finance.fbatch.print;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.print.ReportFinanceBatch;
import com.code.aon.ui.finance.controller.FBatchController;
import com.code.aon.ui.util.AonUtil;

public class FBatchPrinter implements ICollectionProvider {
	
	private static final Logger LOGGER = Logger.getLogger(FBatchPrinter.class.getName());
	
	private static final String FINANCE_BATCH_CONTROLLER_NAME = "fbatch";

	public Collection getCollection() {
		List<ReportFinanceBatch> reportFBatchList = new LinkedList<ReportFinanceBatch>();
		try {
			FBatchController fBatchController = (FBatchController)AonUtil.getController(FINANCE_BATCH_CONTROLLER_NAME);
			Iterator iter = ((List)fBatchController.getModel().getWrappedData()).iterator();
			while(iter.hasNext()){
				FinanceBatch fBatch = (FinanceBatch)iter.next();
				ReportFinanceBatch rFBatch = new ReportFinanceBatch();
				rFBatch.setFinanceBatch(fBatch);
				rFBatch.setTotal(fBatchController.getFinanceBatchTotal(fBatch));
				reportFBatchList.add(rFBatch);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining FinanceBatchList Collection", e);
		}
		return reportFBatchList;
	}
}

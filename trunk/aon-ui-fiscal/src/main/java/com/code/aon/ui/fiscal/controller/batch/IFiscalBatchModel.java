package com.code.aon.ui.fiscal.controller.batch;

import java.util.List;

import com.code.aon.common.AonException;
import com.code.aon.fiscal.FiscalBatch;
import com.code.aon.fiscal.FiscalBatchDetail;

public interface IFiscalBatchModel {
	
	List<Batchable> getPendingList( FiscalBatch fiscalBatch ) throws AonException;
	List<Batchable> getDetailsList( FiscalBatch fiscalBatch ) throws AonException;
	void batch( FiscalBatchDetail detail ) throws AonException;
	void unbatch( FiscalBatchDetail detail ) throws AonException;
	byte[] getData(FiscalBatch fb) throws AonException;

}

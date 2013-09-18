package com.code.aon.ui.fiscal.controller.batch;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_BATCH_DISK_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_BUNDLE;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD111.MOD111Format;
import com.code.aon.fiscal.FiscalBatch;
import com.code.aon.fiscal.FiscalBatchDetail;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.enumeration.FiscalModelStatus;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.fiscal.file.MOD111Writer;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod111BatchModel extends AbstractFiscalBatchModel {

	private static final String SELECT = "SELECT" 
		    +" fm.id " + ID
		    +",fm.year " + YEAR
			+",fm.period " + PERIOD
			+",fm.replacement " + REPLACEMENT
			+",fm.complementary " + COMPLEMENTARY
			+",fm.domain " + DOMAIN
			+",r.name " + COMPANY
			+",'DESCRIPCION' " + DESCRIPTION
			+",0 " + RESULT
			+" FROM fs_model fm"
			+" INNER JOIN domain dom on fm.domain = dom.id" 
			+"  AND (dom.id = ? or dom.parent = ?)"
			+" INNER JOIN company c on c.domain = fm.domain"
			+" INNER JOIN registry r on r.id = c.registry"
			+" WHERE fm.year = ?"
			+"  AND fm.period = ?"
			+"  AND fm.model = " + FiscalModelType.M111.getValue()
			+"  AND fm.administration = ? "
			+"  AND fm.security_level = ?"
			+"  AND fm.status = " + FiscalModelStatus.FINISHED.ordinal() 
			+"  AND fm.id NOT IN ( SELECT detail_id FROM fs_batch_detail fbd WHERE fbd.fs_batch = ?)"
		;
		
		
	@Override
	public void batch(FiscalBatchDetail detail) throws AonException {
		updateStatus(detail.getDetailId(), FiscalModelStatus.BATCHED);
	}

	@Override
	public void unbatch(FiscalBatchDetail detail) throws AonException {
		updateStatus(detail.getDetailId(), FiscalModelStatus.FINISHED);
	}

	private void updateStatus(Integer detailId, FiscalModelStatus status) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(FiscalModel.class);
		FiscalModel dec = (FiscalModel) bean.get(detailId);
		dec.setStatus(status);
		bean.update(dec);
	}

	@Override
	public byte[] getData(FiscalBatch fb) throws AonException {
		MOD111Writer writer = new MOD111Writer();
		int year = fb.getYear();
		if (year < 2010) {
			String msg = "No se permite la generación de archivos para declaraciones anteriores al ejercicio 2010.";
			AonUtil.addErrorMessage(msg);
			throw new AonException(msg);
		}
		if (fb.getPeriod() == Period.YEAR) {
			String msg = "La generación de archivos para declaraciones anuales del ejercicio "
					+ year + " no está aún implementada.";
			AonUtil.addErrorMessage(msg);
			throw new AonException(msg);
		}
		MOD111Format format = null;
		try { 
			format = MOD111Format.getFormat(fb.getAdministration(), fb.getYear());
		} catch (IllegalArgumentException e) {
			throw new AonException(e.getMessage(),e);
		}
		List<FiscalModel> declarations = new LinkedList<FiscalModel>();
		IManagerBean decBean = BeanManager.getManagerBean(FiscalModel.class);
		IManagerBean bean = BeanManager.getManagerBean(FiscalBatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				bean.getFieldName(IEntityAlias.FISCAL_BATCH_DETAIL_FISCAL_BATCH_ID),
				fb.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			FiscalBatchDetail detail = (FiscalBatchDetail) to;
			declarations.add((FiscalModel) decBean.get(detail.getDetailId()));
		}
		FileOutput fileOutput = writer.createMOD111(declarations, format);
		if (fileOutput != null && fileOutput.getErrors().size() > 0) {
			AonUtil.addErrorMessageFromBundle(FINANCE_BUNDLE, FINANCE_BATCH_DISK_ERROR);
			AonUtil.addErrorMessage("");
			int i = 0;
			for (Exception ex : fileOutput.getErrors()) {
				AonUtil.addErrorMessage(++i + ") " + ex.getLocalizedMessage());
			}
		}
		return fileOutput.getContent();
	}

	@Override
	protected String getSelect() {
		return SELECT;
	}

}

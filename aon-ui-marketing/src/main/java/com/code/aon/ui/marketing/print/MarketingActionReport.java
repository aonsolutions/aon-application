package com.code.aon.ui.marketing.print;

import static com.code.aon.ui.commercial.controller.ICommercialConstants.TARGET;
import static com.code.aon.ui.common.ICommonConstants.COMMENT;
import static com.code.aon.ui.common.ICommonConstants.COMPANY_DOCUMENT;
import static com.code.aon.ui.common.ICommonConstants.ID;
import static com.code.aon.ui.common.ICommonConstants.LOGIN_USER;
import static com.code.aon.ui.common.ICommonConstants.STATUS;
import static com.code.aon.ui.marketing.controller.IMarketingConstants.ACTION_EXPORT;
import static com.code.aon.ui.marketing.controller.IMarketingConstants.BUNDLE_NAME;
import static com.code.aon.ui.marketing.controller.IMarketingConstants.CAMPAIGN_ACTION_CONTROLLER_NAME;
import static com.code.aon.ui.marketing.controller.IMarketingConstants.SURVEY;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.QuestionType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.marketing.SurveyResponseDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.marketing.controller.IMarketingConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class MarketingActionReport {

	private static final Logger LOGGER = LoggerFactory.getLogger(MarketingActionReport.class.getName());
	
	private List<QuestionValueReport> questionValueReport;
	
	private File file;
	
	public File getFile() {
		return file;
	}
	
	public List<MarketingQuestionValue> getQuestionValueList() {
		if(getQuestionValueReport()!=null && getQuestionValueReport().get(0)!=null){
			return getQuestionValueReport().get(0).getQuestionList();
		}
		return null;
	}
	
	public List<QuestionValueReport> getQuestionValueReport() {
		return questionValueReport;
	}

	public void setQuestionValueReport(List<QuestionValueReport> questionValueReport) {
		this.questionValueReport = questionValueReport;
	}
	
	public void escribirExcel() throws ManagerBeanException {
		IController controller = FormUtil.getController(CAMPAIGN_ACTION_CONTROLLER_NAME);
		MarketingAction action = (MarketingAction) controller.getTo();
		buildQuestionValueReport(action);
        try {
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("Cuestionario");
            sheet.setDefaultColumnWidth(50);
            sheet.setColumnWidth(0, 8*256);
            sheet.setColumnWidth(1, 11*256);
            sheet.setColumnWidth(2, 16*256);
            sheet.setColumnWidth(3, 11*256);
            sheet.setColumnWidth(4, 19*256);
            sheet.setColumnWidth(5, 11*256);
            sheet.setColumnWidth(6, 8*256);
            sheet.setColumnWidth(7, 15*256);

            HSSFCellStyle headerCellStyle = wb.createCellStyle();
            headerCellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
            headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
            headerCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);  
            
            HSSFRow row = sheet.createRow(0);
            HSSFCellUtil.createCell(row, 0, "", headerCellStyle);
            HSSFCellUtil.createCell(row, 1, "", headerCellStyle);
            HSSFCellUtil.createCell(row, 2, "", headerCellStyle);
            HSSFCellUtil.createCell(row, 3, "", headerCellStyle);
            HSSFCellUtil.createCell(row, 4, "", headerCellStyle);
            HSSFCellUtil.createCell(row, 5, "", headerCellStyle);
            HSSFCellUtil.createCell(row, 6, "", headerCellStyle);
            HSSFCellUtil.createCell(row, 7, "", headerCellStyle);
            int cellIdx = 8;
            for (MarketingQuestionValue qv : getQuestionValueList()) {
            	HSSFCellUtil.createCell(row, cellIdx, qv.getId().toString(), headerCellStyle);
            	cellIdx++;
            }
            
            row = sheet.createRow(1);
            HSSFCellUtil.createCell(row, 0, AonUtil.getMessage(ID), headerCellStyle);
            HSSFCellUtil.createCell(row, 1, AonUtil.getMessage(COMPANY_DOCUMENT), headerCellStyle);
            HSSFCellUtil.createCell(row, 2, AonUtil.getMessage(ICommercialConstants.BUNDLE_NAME, TARGET), headerCellStyle);
            HSSFCellUtil.createCell(row, 3, AonUtil.getMessage(COMMENT), headerCellStyle);
            HSSFCellUtil.createCell(row, 4, AonUtil.getMessage(LOGIN_USER), headerCellStyle);
            HSSFCellUtil.createCell(row, 5, AonUtil.getMessage(STATUS), headerCellStyle);
            HSSFCellUtil.createCell(row, 6, AonUtil.getMessage(ID), headerCellStyle);
            HSSFCellUtil.createCell(row, 7, AonUtil.getMessage(IMarketingConstants.BUNDLE_NAME, SURVEY), headerCellStyle);

			cellIdx = 8;
			for (MarketingQuestionValue qv : getQuestionValueList()) {
				HSSFCellUtil.createCell(row, cellIdx, qv.getDescription(), headerCellStyle);
				cellIdx++;
			}
			
			int rowIdx = 2;
			for(QuestionValueReport qvr: getQuestionValueReport()){
				row = sheet.createRow(rowIdx);
				row.createCell(0).setCellValue(qvr.getSurveyResponse().getTarget().getId());
				row.createCell(1).setCellValue(qvr.getSurveyResponse().getTarget().getRegistry().getDocument());
				row.createCell(2).setCellValue(qvr.getSurveyResponse().getTarget().getRegistry().getFullName());
				row.createCell(3).setCellValue(qvr.getActionTarget().getComments());
				row.createCell(4).setCellValue(qvr.getSurveyResponse().getUser().getName());
				row.createCell(5).setCellValue(qvr.getStatus());
				row.createCell(6).setCellValue(qvr.getSurveyResponse().getSurvey().getId());
				row.createCell(7).setCellValue(qvr.getSurveyResponse().getSurvey().getDescription());
				cellIdx = 8;
				for (MarketingQuestionValue qv : qvr.getQuestionList()) {
					row = sheet.getRow(rowIdx);
					row.createCell(cellIdx).setCellValue(qv.getValue());
					cellIdx++;
				}
				rowIdx++;
			}
			
			// Leyenda - hoja resumen de preguntas del cuestionario
			sheet = wb.createSheet("Leyenda");
            sheet.setDefaultColumnWidth(50);
            sheet.setColumnWidth(0, 8*256);
            sheet.setColumnWidth(1, 100*256);
            row = sheet.createRow(0);
            HSSFCellUtil.createCell(row, 0, AonUtil.getMessage(ID), headerCellStyle);
            HSSFCellUtil.createCell(row, 1, AonUtil.getMessage(ICommercialConstants.BUNDLE_NAME, ICommercialConstants.QUESTION), headerCellStyle);
            rowIdx = 1;
			for (MarketingQuestionValue qv : getQuestionValueList()) {
				row = sheet.createRow(rowIdx);
				row.createCell(0).setCellValue(qv.getId());
				row.createCell(1).setCellValue(qv.getDescription());
				rowIdx++;
			}
 
            String fileName = AonUtil.getMessage(BUNDLE_NAME, ACTION_EXPORT, action.getId());
            file = File.createTempFile(fileName, ".XLS");
            FileOutputStream fileOut = new FileOutputStream(file);
            wb.write(fileOut);
            fileOut.close();
        }
        catch(IOException e) {
        	String msg = "Error al escribir el fichero.";
        	LOGGER.error(msg);
            System.out.println(msg);
        }
    }
	
	public void onExcelReport(ActionEvent event) throws ManagerBeanException {
		IController controller = FormUtil.getController(CAMPAIGN_ACTION_CONTROLLER_NAME);
		MarketingAction action = (MarketingAction) controller.getTo();
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = AonUtil.getMessage(BUNDLE_NAME, ACTION_EXPORT, action.getId());
			response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\";");

			escribirExcel();
			ServletOutputStream output = response.getOutputStream();
			InputStream input = new FileInputStream(getFile());
			int size = IOUtils.copy(input, output);
			if (size > 0) {
				response.setHeader("Content-Length", String.valueOf(size));
			}
			output.close();
			input.close();

			response.flushBuffer();
			faces.responseComplete();
		} catch (IOException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	private void buildQuestionValueReport(MarketingAction action) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(SurveyResponseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("SurveyResponseDetail.surveyResponse.action.id", action.getId());
		criteria.addOrder("SurveyResponseDetail.surveyResponse.target.id");
		
		setQuestionValueReport(new LinkedList<QuestionValueReport>());
		
		ActionTarget at = null;
		QuestionValueReport qvr = null;
		IManagerBean atBean = BeanManager.getManagerBean(ActionTarget.class);
		for( ITransferObject to : bean.getList(criteria) ) {
			SurveyResponseDetail srd = (SurveyResponseDetail) to;
			Target target = srd.getSurveyResponse().getTarget();
			if ( (at == null) || (! at.getTarget().equals(target)) ) {
				Criteria _criteria = new Criteria();
				_criteria.addEqualExpression(atBean.getFieldName(IEntityAlias.ACTION_TARGET_TARGET_ID), target.getId());
				_criteria.addEqualExpression(atBean.getFieldName(IEntityAlias.ACTION_TARGET_ACTION_ID), action.getId());
				List<ITransferObject> atList = atBean.getList(_criteria);
				if (! atList.isEmpty() ) {
					at = (ActionTarget) atList.get(0);
					qvr = new QuestionValueReport(srd, at);
					qvr.setQuestionList(new LinkedList<MarketingQuestionValue>());
					getQuestionValueReport().add(qvr);
				}
			}
			MarketingQuestionValue mqv = new MarketingQuestionValue();
			mqv.setId(srd.getQuestion().getId());
			mqv.setDescription(srd.getQuestion().getText());
			QuestionType type = srd.getQuestion().getType();
			Object object = srd.getValue(type);
			mqv.setValue(ObjectUtils.toString(object));
			qvr.getQuestionList().add(mqv);
		}
	}

}
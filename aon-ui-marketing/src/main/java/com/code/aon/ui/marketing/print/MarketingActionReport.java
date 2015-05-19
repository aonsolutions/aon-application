package com.code.aon.ui.marketing.print;

import static com.code.aon.ui.common.ICommonMessages.ACTION_EXPORT;
import static com.code.aon.ui.common.ICommonMessages.ID;
import static com.code.aon.ui.common.ICommonMessages.QUESTION;
import static com.code.aon.ui.marketing.controller.IMarketingConstants.CAMPAIGN_ACTION_CONTROLLER_NAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
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
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.marketing.SurveyQuestion;
import com.code.aon.marketing.SurveyResponse;
import com.code.aon.marketing.SurveyResponseDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Question;
import com.code.aon.registry.enumeration.QuestionType;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class MarketingActionReport implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(MarketingActionReport.class.getName());
	
	private List<QuestionValueReport> questionValueReport;
	
	private File file;
	
	private List<Question> surveyQuestionList;
	
	public File getFile() {
		return file;
	}
	
	public List<Question> getSurveyQuestionList() throws ManagerBeanException {
		return surveyQuestionList;
	}

	public void buildQuestionValueList() throws ManagerBeanException {
		IController controller = FormUtil.getController(CAMPAIGN_ACTION_CONTROLLER_NAME);
		MarketingAction action = (MarketingAction) controller.getTo();
		IManagerBean bean = BeanManager.getManagerBean(SurveyQuestion.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SURVEY_QUESTION_SURVEY_ID), action.getSurvey().getId());
		criteria.addOrder(bean.getFieldName(IEntityAlias.SURVEY_QUESTION_SURVEY_ID));
		surveyQuestionList = new LinkedList<Question>();
		for(ITransferObject to: bean.getList(criteria)){
			SurveyQuestion sq = (SurveyQuestion) to;
			if(sq.getQuestion().getType()!=QuestionType.INFO){
				surveyQuestionList.add(sq.getQuestion());
			}
		}
	}
	
	public List<QuestionValueReport> getQuestionValueReport() {
		return questionValueReport;
	}

	public void setQuestionValueReport(List<QuestionValueReport> questionValueReport) {
		this.questionValueReport = questionValueReport;
	}
	
	public void createExcel() throws ManagerBeanException {
		IController controller = FormUtil.getController(CAMPAIGN_ACTION_CONTROLLER_NAME);
		MarketingAction action = (MarketingAction) controller.getTo();
		buildQuestionValueReport(action);
		buildQuestionValueList();
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
            for (Question q : getSurveyQuestionList()) {
            	HSSFCellUtil.createCell(row, cellIdx, q.getId().toString(), headerCellStyle);
            	cellIdx++;
            }
            
            row = sheet.createRow(1);
            HSSFCellUtil.createCell(row, 0, AonUtil.getMessage(ID), headerCellStyle);
            HSSFCellUtil.createCell(row, 1, AonUtil.getMessage(ICommonMessages.COMPANY_DOCUMENT), headerCellStyle);
            HSSFCellUtil.createCell(row, 2, AonUtil.getMessage(ICommonMessages.TARGET), headerCellStyle);
            HSSFCellUtil.createCell(row, 3, AonUtil.getMessage(ICommonMessages.COMMENT), headerCellStyle);
            HSSFCellUtil.createCell(row, 4, AonUtil.getMessage(ICommonMessages.LOGIN_USER), headerCellStyle);
            HSSFCellUtil.createCell(row, 5, AonUtil.getMessage(ICommonMessages.STATUS), headerCellStyle);
            HSSFCellUtil.createCell(row, 6, AonUtil.getMessage(ID), headerCellStyle);
            HSSFCellUtil.createCell(row, 7, AonUtil.getMessage(ICommonMessages.SURVEY), headerCellStyle);

			cellIdx = 8;
			for (Question q : getSurveyQuestionList()) {
				HSSFCellUtil.createCell(row, cellIdx, q.getDescription(), headerCellStyle);
				cellIdx++;
			}
			
			int rowIdx = 2;
			for(QuestionValueReport qvr: getQuestionValueReport()){
				row = sheet.createRow(rowIdx);
				row.createCell(0).setCellValue(qvr.getSurveyResponse().getRegistry().getId());
				row.createCell(1).setCellValue(qvr.getSurveyResponse().getRegistry().getDocument());
				row.createCell(2).setCellValue(qvr.getSurveyResponse().getRegistry().getFullName());
				row.createCell(3).setCellValue(qvr.getActionTarget().getComments());
				row.createCell(4).setCellValue(qvr.getSurveyResponse().getUser().getName());
				row.createCell(5).setCellValue(qvr.getStatus());
				row.createCell(6).setCellValue(qvr.getSurveyResponse().getSurvey().getId());
				row.createCell(7).setCellValue(qvr.getSurveyResponse().getSurvey().getDescription());
				cellIdx = 8;
				for (Question q : getSurveyQuestionList()) {
					row = sheet.getRow(rowIdx);
					row.createCell(cellIdx).setCellValue(qvr.getResponses().get(q.getId()));
					cellIdx++;
				}
				rowIdx++;
			}
			
			// Leyenda - hoja resumen de preguntas del cuestionario
			sheet = wb.createSheet("Leyenda");
            sheet.setDefaultColumnWidth(50);
            sheet.setColumnWidth(0, 8*256);
            sheet.setColumnWidth(1, 100*256);
            sheet.setColumnWidth(2, 50*256);
            sheet.setColumnWidth(3, 200*256);
            row = sheet.createRow(0);
            HSSFCellUtil.createCell(row, 0, AonUtil.getMessage(ID), headerCellStyle);
            HSSFCellUtil.createCell(row, 1, AonUtil.getMessage(QUESTION), headerCellStyle);
            HSSFCellUtil.createCell(row, 2, AonUtil.getMessage(QUESTION), headerCellStyle);
            HSSFCellUtil.createCell(row, 3, AonUtil.getMessage(QUESTION), headerCellStyle);
            rowIdx = 1;
			for (Question q : getSurveyQuestionList()) {
				row = sheet.createRow(rowIdx);
				row.createCell(0).setCellValue(q.getId());
				row.createCell(1).setCellValue(q.getDescription());
				row.createCell(2).setCellValue(q.getAlias());
				row.createCell(3).setCellValue(q.getArgument());
				rowIdx++;
			}
 
            String fileName = AonUtil.getMessage(ACTION_EXPORT, action.getId());
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
			String fileName = AonUtil.getMessage(ACTION_EXPORT, action.getId());
			response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\";");

			createExcel();
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
		criteria.addOrder("SurveyResponseDetail.surveyResponse.registry.name");
		
		setQuestionValueReport(new LinkedList<QuestionValueReport>());
		
		ActionTarget at = null;
		SurveyResponse sr = null;
		QuestionValueReport qvr = null;
		IManagerBean atBean = BeanManager.getManagerBean(ActionTarget.class);
		for( ITransferObject to : bean.getList(criteria) ) {
			SurveyResponseDetail srd = (SurveyResponseDetail) to;
			Target target = (Target)BeanManager.getManagerBean(Target.class).get(srd.getSurveyResponse().getRegistry().getId());
			if ( at == null || !at.getTarget().equals(target) || !srd.getSurveyResponse().equals(sr) ) {
				Criteria _criteria = new Criteria();
				_criteria.addEqualExpression(atBean.getFieldName(IEntityAlias.ACTION_TARGET_TARGET_ID), target.getId());
				_criteria.addEqualExpression(atBean.getFieldName(IEntityAlias.ACTION_TARGET_ACTION_ID), action.getId());
				List<ITransferObject> atList = atBean.getList(_criteria);
				if (! atList.isEmpty() ) {
					at = (ActionTarget) atList.get(0);
					sr = srd.getSurveyResponse();
					qvr = new QuestionValueReport(srd, sr, at);
					qvr.setResponses(new HashMap<Integer,String>());
					getQuestionValueReport().add(qvr);
				}
			}
			QuestionType type = srd.getQuestion().getType();
			Object object = srd.getValue(type);
			qvr.getResponses().put(srd.getQuestion().getId(),ObjectUtils.toString(object));
		}
	}

}
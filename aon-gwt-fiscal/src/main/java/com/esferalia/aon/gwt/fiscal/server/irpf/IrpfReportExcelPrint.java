package com.esferalia.aon.gwt.fiscal.server.irpf;

import java.io.IOException;
import java.util.function.Consumer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.gwt.fiscal.server.JsonParser;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "IrpfReport Excel Print ", urlPatterns = { "/aon_gwt_fiscal/roms/IrpfReportExcelPrint" })
public class IrpfReportExcelPrint extends HttpServlet {

	private static final long serialVersionUID = 2782900860290220524L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			String irpfParams = req.getParameter( IRequestParamsNames.IRPF_PARAMS );
			IRPFParams params = JsonParser.parseIRPFParams(irpfParams);
			params.setDomain(Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID)));
			params.setUser(req.getParameter(IRequestParamsNames.USER));
			params.setDomainName(req.getParameter(IRequestParamsNames.DOMAIN_NAME));
			Occam occam = new Occam()
				.setDomainName(params.getDomainName())
				.setDomain(params.getDomain())
				.setUser(params.getUser());
			
			ExcelAction action = new ExcelAction( );
			action.initialize("IRPF");
			FISCAL.getIrpfBreakdown(occam, params)
				.forEach(action)						
			;
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"IRPF."+ MimeType.MS_EXCEL.getExtension()+ "\";");
			action.finalize(resp.getOutputStream());
			
			resp.flushBuffer();
			
		} catch (ParseException | java.text.ParseException e) {
			e.printStackTrace();
		}

	}
	
	private class ExcelAction extends AbsExcelAction implements Consumer<IrpfBreakdown>{

		@Override
		protected void headerRow() {
			row = sheet.createRow(rowCount++);
			cellCount = 0;

			XSSFCellStyle rightHeaderCellStyle = (XSSFCellStyle) headerCellStyle.clone();
			rightHeaderCellStyle.setAlignment(HorizontalAlignment.RIGHT);

			CellUtil.createCell(row, cellCount, "TIPO", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 8 * 256);

			CellUtil.createCell(row, cellCount, "EPIGR.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 6 * 256);

			CellUtil.createCell(row, cellCount, "FACTURA", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			CellUtil.createCell(row, cellCount, "PAIS", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 5 * 256);

			CellUtil.createCell(row, cellCount, "DOCUMENTO", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			CellUtil.createCell(row, cellCount, "TITULAR FACTURA", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 45 * 256);

			CellUtil.createCell(row, cellCount, "FECHA FAC.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "FECHA IMP.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "TIPO RET.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 18 * 256);

			CellUtil.createCell(row, cellCount, "BASE IMP.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "% IRPF", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "CUOTA", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "% DED.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);

			CellUtil.createCell(row, cellCount, "CUOTA DED.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 10 * 256);
			
			CellUtil.createCell(row, cellCount, "N. REFERENCIA", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 45 * 256);
			
		}

		@Override
		public void accept(IrpfBreakdown irpf) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			addCell(irpf.getInvoiceType().getDescription());
			addCell(AonStringUtils.defaultIfBlank(irpf.getEpigraph()));
			if ( irpf.getNumber() != null) {
				addCell(irpf.getDocumentNumber());
			} else {
				addCell(AonStringUtils.EMPTY);
			}
			addCell(irpf.getRegistryDocumentCountry());
			addCell(irpf.getRegistryDocument());
			addCell(irpf.getName());
			addCell(irpf.getIssueDate());
			addCell(irpf.getTaxDate());
			addCell(irpf.getWithholdingType()!=null?irpf.getWithholdingType().getDescription():AonStringUtils.SPACE);
			addCell(irpf.getBase());
			addCell(irpf.getPercent());
			addCell(irpf.getQuota());
			if (irpf.isSales()) {
				addCell(AonStringUtils.EMPTY);
				addCell(AonStringUtils.EMPTY);
			} else {
				addCell(irpf.getDeductiblePercent());
				addCell(irpf.getDeductibleQuota());
			}
			addCell(AonStringUtils.defaultIfBlank(irpf.getReferenceCode()));
		}
	}
}

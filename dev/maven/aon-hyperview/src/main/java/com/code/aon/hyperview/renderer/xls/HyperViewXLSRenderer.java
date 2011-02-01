package com.code.aon.hyperview.renderer.xls;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import com.code.aon.hyperview.HyperViewException;
import com.code.aon.hyperview.HyperViewUtil;
import com.code.aon.hyperview.model.HyperViewAttribute;
import com.code.aon.hyperview.model.HyperViewCompositeView;
import com.code.aon.hyperview.model.HyperViewContents;
import com.code.aon.hyperview.model.HyperViewFormSQLNode;
import com.code.aon.hyperview.model.HyperViewListSQLNode;
import com.code.aon.hyperview.model.HyperViewNode;
import com.code.aon.hyperview.model.IHyperViewContent;
import com.code.aon.hyperview.player.IHyperViewPlayerNode;
import com.code.aon.hyperview.player.ViewResult;
import com.code.aon.hyperview.renderer.HyperViewAbstractRenderer;
import com.code.aon.hyperview.renderer.HyperViewRendererException;

public class HyperViewXLSRenderer extends HyperViewAbstractRenderer {
	private static String MIME_TYPE = "application/vnd.ms-excel";

	private static String EXTENSION = "xls";

	private HSSFWorkbook wb;

	private HSSFCellStyle labelStyle;

	private HSSFCellStyle valueStyle;

	private HSSFCellStyle oddRowStyle;

	private HSSFCellStyle evenRowStyle;

	public String getContentType() {
		return MIME_TYPE;
	}

	public String getExtension() {
		return EXTENSION;
	}

	public HSSFWorkbook getWorkbook() {
		if (wb == null) {
			wb = new HSSFWorkbook();
		}
		return wb;
	}

	private HSSFCellStyle getLabelStyle() {
		if (labelStyle == null) {
			HSSFWorkbook wb = getWorkbook();
			labelStyle = wb.createCellStyle();
			HSSFFont labelFont = wb.createFont();
			labelFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			labelFont.setFontName("Verdana");
			labelFont.setColor(HSSFColor.BLACK.index);
			labelFont.setFontHeightInPoints((short) 8);
			labelStyle.setFont(labelFont);
			labelStyle
					.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
			labelStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			labelStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			labelStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			labelStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			labelStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		}
		return labelStyle;
	}

	private HSSFCellStyle getFormValueStyle() {
		if (valueStyle == null) {
			HSSFWorkbook wb = getWorkbook();
			valueStyle = wb.createCellStyle();
			fillValueStyle(valueStyle);
			valueStyle.setFillForegroundColor(HSSFColor.LEMON_CHIFFON.index);
		}
		return valueStyle;
	}

	private HSSFCellStyle getOddRowValueStyle() {
		if (oddRowStyle == null) {
			HSSFWorkbook wb = getWorkbook();
			oddRowStyle = wb.createCellStyle();
			fillValueStyle(oddRowStyle);
			oddRowStyle.setFillForegroundColor(HSSFColor.WHITE.index);
		}
		return oddRowStyle;
	}

	private HSSFCellStyle getEvenRowValueStyle() {
		if (evenRowStyle == null) {
			HSSFWorkbook wb = getWorkbook();
			evenRowStyle = wb.createCellStyle();
			fillValueStyle(evenRowStyle);
			evenRowStyle.setFillForegroundColor(HSSFColor.LEMON_CHIFFON.index);
		}
		return evenRowStyle;
	}

	private void fillValueStyle(HSSFCellStyle style) {
		HSSFFont valueFont = wb.createFont();
		valueFont.setFontName("Helvetica");
		valueFont.setFontHeightInPoints((short) 9);
		style.setFont(valueFont);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	}

	public void start(OutputStream out) throws HyperViewRendererException {
		try {
			parse(getNode(), out);
			getWorkbook().write(out);
		} catch (IOException e) {
			e.printStackTrace();
			throw new HyperViewRendererException(e.getMessage(), e);
		}

	}

	@SuppressWarnings("unchecked")
	public void parse(IHyperViewPlayerNode node, OutputStream out)
			throws HyperViewRendererException {
		try {
			if (!node.isDeployed()) {
				getHyperViewPlayer().deployNode(node);
			}
			HSSFWorkbook wb = getWorkbook();
			// begin [EUKE]
			// Las descripcines de las sheet de Excel no pueden tener
			// más de 31 carácters ni contener carácter extraños.
			String sheetName = node.getDescription();
			sheetName = HyperViewUtil.escapeCharacters(sheetName);
			sheetName = sheetName.replace("/", "_");
			sheetName = sheetName.replace("\\", "_");
			sheetName = sheetName.replace("*", "_");
			sheetName = sheetName.replace("?", "_");
			sheetName = sheetName.replace("[", "_");
			sheetName = sheetName.replace("]", "_");
			sheetName = getIdentifierFormatted(node.getIdentifier()) + " "
					+ sheetName;
			sheetName = StringUtils.abbreviate(sheetName, 31);
			// end [EUKE]
			HSSFSheet sheet = wb.createSheet(sheetName);
			HyperViewNode wnode = node.getHyperViewNode();
			resolveContents(sheet, node, wnode.getContents(), out, 0);
			if (isRecursive()) {
				List<IHyperViewPlayerNode> children = node.getChildren();
				if (children != null && !children.isEmpty()) {
					for (IHyperViewPlayerNode child : children) {
						parse(child, out);
					}
				}
			}
		} catch (HyperViewException e) {
			e.printStackTrace();
			throw new HyperViewRendererException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private int resolveContents(HSSFSheet sheet, IHyperViewPlayerNode node,
			HyperViewContents contents, OutputStream out, int offSet)
			throws HyperViewException, HyperViewRendererException {
		if (contents != null) {
			List<IHyperViewContent> list = contents.getContents();
			for (IHyperViewContent content : list) {
				if (content instanceof HyperViewFormSQLNode) {
					offSet = resolveForm(sheet, node, (HyperViewFormSQLNode) content,
							out, offSet);
				} else if (content instanceof HyperViewListSQLNode) {
					offSet = resolveList(sheet, node, (HyperViewListSQLNode) content,
							out, offSet);
				} else if (content instanceof HyperViewCompositeView) {
					HyperViewCompositeView composite = (HyperViewCompositeView) content;
					if (composite != null) {
						offSet = resolveContents(sheet, node, composite.getContents(),
								out, offSet);
					}
				}
			}
		}
		return offSet;
	}
	
	@SuppressWarnings("unused")
	private int resolveForm(HSSFSheet sheet, IHyperViewPlayerNode node,
			HyperViewFormSQLNode content, OutputStream out, int offSet)
			throws HyperViewException, HyperViewRendererException {
		if (content != null) {
			getHyperViewPlayer().resolveFormContent(node, content);
			List<HyperViewAttribute> attrs = content.getMetadata();
			HSSFRow row = null;
			HSSFCell cell = null;
			ViewResult result = content.getResult();
			if (result != null) {
				Object[] values = content.getResult().getValues();
				if (values != null) {
					sheet.setColumnWidth((short) 0, (short) 8000);
					sheet.setColumnWidth((short) 1, (short) 15000);
					for (int rownum = 0; rownum < attrs.size(); rownum++,offSet++) {
						HyperViewAttribute attr = attrs.get(rownum);
						row = sheet.createRow(rownum);
						cell = row.createCell((short) 0);
						cell.setCellValue(attr.getLabel());
						cell.setCellStyle(getLabelStyle());
						cell = row.createCell((short) 1);
						cell.setCellStyle(getFormValueStyle());
						Object value = values[rownum];
						fillHSSFCell(attr, value, cell);
					}
				}
			}
		}
		return offSet;
	}

	@SuppressWarnings("unused")
	private int resolveList(HSSFSheet sheet, IHyperViewPlayerNode node,
			HyperViewListSQLNode content, OutputStream out, int offSet)
			throws HyperViewException, HyperViewRendererException {
		getHyperViewPlayer().resolveListContent(node,content);
		List<HyperViewAttribute> attrs = content.getMetadata();
		HSSFRow row = null;
		HSSFCell cell = null;
		row = sheet.createRow(offSet++);
		for (int colnum = 0; colnum < attrs.size(); colnum++) {
			HyperViewAttribute attr = attrs.get(colnum);
			cell = row.createCell((short) colnum);
			cell.setCellValue(attr.getLabel());
			cell.setCellStyle(getLabelStyle());
		}
		List<ViewResult> list = content.getQueryResult();
		for (ViewResult qr: list ) {
			row = sheet.createRow(offSet++);
			Object[] objs = qr.getValues();
			for (int colnum = 0; colnum < objs.length; colnum++) {
				cell = row.createCell((short) colnum);
				cell.setCellStyle(offSet % 2 == 0 ? getOddRowValueStyle()
						: getEvenRowValueStyle());
				HyperViewAttribute attr = attrs.get(colnum);
				fillHSSFCell(attr, objs[colnum], cell);
			}
		}
		return offSet;
	}

	private void fillHSSFCell(HyperViewAttribute attr, Object value,
			HSSFCell cell) throws HyperViewRendererException {
		if (value == null) {
			cell.setCellValue("");
		} else {
			if (attr.isBoolean()) {
				if (!(value instanceof Boolean)) {
					throw new HyperViewRendererException(
							"Value of '" + attr.getLabel()
									+ "' is defined as Boolean, but is '"
									+ value != null ? value.getClass()
									.getName() : "undefined" + "'");
				}
				Boolean bool = (Boolean) value;
				cell.setCellValue(bool.booleanValue());
				cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
			} else if (attr.isDate()) {
				Date date = null;
				if (value instanceof Date) {
					date = (Date) value;
				} else {
					if ((value instanceof Number)) {
						date = getDate((Number) value);
					} else {
						throw new HyperViewRendererException("Value of '"
								+ attr.getLabel()
								+ "' is defined as Date, but is '"
								+ value.getClass().getName() + "'");
					}
				}
				if (date != null) {
					cell.setCellValue(getDateFormatter().format(date));
				}
				/*
				 * if (!(value instanceof Date)) { throw new
				 * HyperViewRendererException( "Value of '" + attr.getLabel() + "'
				 * is defined as Date, but is '" + value != null ? value
				 * .getClass().getName() : "undefined" + "'"); } Date date =
				 * (Date) value;
				 * cell.setCellValue(getDateFormatter().format(date));
				 */
			} else if (attr.isTime()) {
				if (!(value instanceof Date)) {
					throw new HyperViewRendererException(
							"Value of '" + attr.getLabel()
									+ "' is defined as Time, but is '" + value != null ? value
									.getClass().getName()
									: "undefined" + "'");
				}
				Date date = (Date) value;
				cell.setCellValue(getTimeFormatter().format(date));
			} else if (attr.isTimestamp()) {
				if (!(value instanceof Date)) {
					throw new HyperViewRendererException(
							"Value of '" + attr.getLabel()
									+ "' is defined as Timestamp, but is '"
									+ value != null ? value.getClass()
									.getName() : "undefined" + "'");
				}
				Date date = (Date) value;
				cell.setCellValue(getTimestampFormatter().format(date));
			} else if (attr.isDouble() || attr.isInteger()) {
				if (!(value instanceof Number)) {
					throw new HyperViewRendererException("Value of '"
							+ attr.getLabel()
							+ "' is defined as Integer or Double, but is '"
							+ value != null ? value.getClass().getName()
							: "undefined" + "'");
				}
				Number number = (Number) value;
				cell.setCellValue(number.doubleValue());
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			} else {
				String lit = (String) value;
				cell.setCellValue(lit);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			}
		}
	}

	private String getIdentifierFormatted(String identifier) {
		String[] arr = identifier.split(":");
		StringBuffer buf = new StringBuffer();
		if (arr.length > 0) {
			for (int i = 0; i < arr.length; i++) {
				if (i > 0) {
					buf.append("-");
				}
				int x = Integer.parseInt(arr[i]) + 1;
				buf.append(x);
			}
			return buf.toString();
		}
		return "1";
	}

}

package com.code.aon.ui.finance.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.company.Company;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.ExpenseInvoiceController;
import com.code.aon.ui.finance.controller.ExpenseInvoiceDetailController;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.PurchaseInvoiceController;
import com.code.aon.ui.finance.controller.PurchaseInvoiceDetailController;
import com.code.aon.ui.finance.controller.UndeductibleInvoiceController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceOcrProcess {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceOcrProcess.class.getName());
	
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public void process(String path, String domainName, Integer domainId, Invoice invoice, byte[] data) throws Exception {
		execute(path, domainName, domainId, data, invoice);
		
		
		if(invoice.getRegistryDocument()!=null && !"".equals(invoice.getRegistryDocument())){
			IRegistry ir = null;
			IManagerBean bean = null;
			bean = BeanManager.getManagerBean(Registry.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DOCUMENT), invoice.getRegistryDocument());
			
			List<ITransferObject> list = bean.getList(criteria);
			if(list!=null && !list.isEmpty()){
				Registry r = (Registry) list.get(0);
				if(invoice.isPurchase()){
					bean = BeanManager.getManagerBean(Supplier.class);
					ir = ((Supplier) bean.get(r.getId()));
					
					PurchaseInvoiceController invoiceController = (PurchaseInvoiceController) FormUtil.getController(IFinanceConstants.PURCHASE_INVOICE_CONTROLLER_NAME);	
					invoiceController.supplierChanged((Supplier)ir);
				} else if(invoice.isExpense()){
					bean = BeanManager.getManagerBean(Creditor.class);
					ir = ((Creditor) bean.get(r.getId()));
					
					ExpenseInvoiceController invoiceController = (ExpenseInvoiceController) FormUtil.getController(IFinanceConstants.EXPENSE_INVOICE_CONTROLLER_NAME);	
					invoiceController.creditorChanged((Creditor)ir);
				} else if(invoice.isUndeductible()){
					bean = BeanManager.getManagerBean(Creditor.class);
					ir = ((Creditor) bean.get(r.getId()));
					
					UndeductibleInvoiceController invoiceController = (UndeductibleInvoiceController) FormUtil.getController(IFinanceConstants.UNDEDUCTIBLE_INVOICE_CONTROLLER_NAME);	
					invoiceController.creditorChanged((Creditor)ir);
				}
			}
		}
		
		if(invoice.isPurchase()){
			PurchaseInvoiceDetailController detailController = (PurchaseInvoiceDetailController) FormUtil.getController(IFinanceConstants.PURCHASE_INVOICE_DETAIL_CONTROLLER_NAME);
			InvoiceDetail invoiceDetail = (InvoiceDetail) detailController.getTo();
			invoiceDetail.setDescription("Factura reconocida por OCR");
			detailController.quantityChanged(1);
			detailController.priceChanged(invoice.getTotal());
			detailController.fillTaxDataInDetail(false, true);
		} else if(invoice.isExpense() || invoice.isUndeductible()){
			ExpenseInvoiceDetailController detailController = (ExpenseInvoiceDetailController) FormUtil.getController(IFinanceConstants.EXPENSE_INVOICE_DETAIL_CONTROLLER_NAME);
			InvoiceDetail invoiceDetail = (InvoiceDetail)detailController.getTo();
			detailController.setTotalChanged(invoice.getTotal());
			detailController.totalChanged(invoiceDetail);
		}
	}
				
	public void execute(String path, String domainName, Integer domainId, byte[] data, Invoice invoice) throws Exception {
		if(data!=null){
			String user = "ingenet";
			String passwd = "1ng3n3t";
			StringBuilder postData = new StringBuilder();
			postData.append('&').append("username");
			postData.append('=').append(user);
			postData.append('&').append("password");
			postData.append('=').append(passwd);
			postData.append('&').append("domain");
			postData.append('=').append(domainName);
			postData.append('&').append("value");
			postData.append('=').append(data);
			postData.append('&').append("data");
			postData.append('=').append(data);
			byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8.name());
			
			
			String companyDocument = "";
			companyDocument = getCompanyDocument(domainId);
			
			URL url = new URL((path.startsWith("http://")?"":"http://")+path+"/?domain="+domainName+"&document="+companyDocument);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects( false );
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/octet-stream");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			try {
				conn.connect();
				conn.getOutputStream().write(Base64.getEncoder().encode(data));
				
				BufferedReader br = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), StandardCharsets.UTF_8.name()));
				StringBuffer sb = new StringBuffer();
				for (String in; (in = br.readLine()) != null;) {
					sb.append(in + "\n");
				}
				fillInvoice(sb.toString(), invoice);
				br.close();
				conn.disconnect();
			} catch (IOException e) {
				LOGGER.error("IMPOSIBLE CONECTAR. " + e.getMessage());
				AonUtil.addErrorMessage("IMPOSIBLE CONECTAR. " + e.getMessage());
			}
		} else {
			AonUtil.addInfoMessage("Sin datos");
		}
	}
	
	private void fillInvoice(String value, Invoice invoice) {
		try {
			JSONObject obj = new JSONObject(value);
			String number = obj.getString("number");
			String document = obj.getString("document");
			String date = obj.getString("date");
			String amount = obj.getString("amount");
			
			invoice.setReferenceCode(number);
			invoice.setRegistryDocument(document);
			invoice.setIssueDate(sdf.parse(date));
			invoice.setTaxDate(sdf.parse(date));
			invoice.setTotal(Double.parseDouble(amount));
			
			AonUtil.addInfoMessage("DATOS OBTENIDOS");
			AonUtil.addInfoMessage("Nº factura: " + number);
			AonUtil.addInfoMessage("NIF: " + document);
			AonUtil.addInfoMessage("Fecha: " + date);
			AonUtil.addInfoMessage("Importe: " + amount);
			
		} catch (JSONException e) {
			LOGGER.error("Error de JSON. " + e.getMessage());
		} catch (ParseException e) {
			LOGGER.error("Error de parse. " + e.getMessage());
		}
	}
	

	
	public static void main(String[] args) throws Exception {
		String path = "";
		path = "http://example.domain/ocr";
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("URL: " + path);
		System.out.print("Proceed? (y/n) (default yes): ");
		
		boolean exit = false;
		String inputText = null;
		while(!exit && scanner.hasNextLine()){
			inputText = scanner.nextLine();
			if(!"n".equals(inputText) && !"no".equals(inputText)){
				InvoiceOcrProcess ocr = new InvoiceOcrProcess();
				ocr.execute(path, null, null, "test".getBytes(), null);
				System.out.println("Done.");
			} else {
				System.out.println("Aborted.");
			}
			exit = true;
		}
		scanner.close();
	}
	
	private String getCompanyDocument(int domain){
		CompanyController controller = (CompanyController) AonUtil
				.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		return ((Company)controller.getTo()).getDocument();
	}
	
}

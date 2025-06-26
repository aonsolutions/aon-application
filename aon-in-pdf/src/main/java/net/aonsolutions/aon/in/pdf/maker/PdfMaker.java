package net.aonsolutions.aon.in.pdf.maker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;

import net.aonsolutions.aon.in.pdf.maker.exception.CanNotCreatePdfException;
import net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplate;
import net.aonsolutions.aon.in.pdf.maker.warehouse.MultipleWarehouseTemplate;
import net.aonsolutions.aon.in.pdf.maker.warehouse.PackagingTag;
import net.aonsolutions.aon.in.pdf.maker.warehouse.WarehouseSaleTemplate;
import net.aonsolutions.aon.in.pdf.maker.warehouse.WarehouseTemplate;
import net.aonsolutions.aon.in.pdf.maker.warehouse.paturpat.GenericPackagingTagTemplate;
import net.aonsolutions.aon.in.pdf.maker.warehouse.paturpat.MercadonaPackagingTagTemplate;

public class PdfMaker {

	/**
	 * CREATE INVOICE PDF
	 * 
	 * @param out
	 * @param invoice, Invoice Object
	 * @param config, print invoice configuration
	 */
	public static void printInvoice(OutputStream out, CompanyFull company, Invoice invoice, PrintInvoiceConfiguration config, String qrUrl, byte[] logo, String tbaiId) {
		try {
			InvoiceTemplate template = new InvoiceTemplate(company, invoice, config, qrUrl, logo, tbaiId);
			template.print(out);
		} catch (CanNotCreatePdfException e) {
			e.printStackTrace();
		}		
	}
	
	public static void printInvoice(OutputStream out, CompanyFull company, List<Invoice> invoices, PrintInvoiceConfiguration config, String qrUrl, byte[] logo, String tbaiId) {
		try {
			InvoiceTemplate template = new InvoiceTemplate(company, invoices, config, qrUrl, logo, tbaiId);
			template.print(out);
		} catch (CanNotCreatePdfException e) {
			e.printStackTrace();
		}		
	}

	/***** WAREHOUSE 
	 * @throws CanNotCreatePdfException 
	 * @throws IOException *****/

	public static void printGenericPackagingTag(OutputStream out, PackagingTag packagingTag) {
		try (GenericPackagingTagTemplate template = new GenericPackagingTagTemplate(packagingTag)) {
			template.print(out);
		} catch (CanNotCreatePdfException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printMercadonaPackagingTag(OutputStream out, PackagingTag packagingTag) {
		try (MercadonaPackagingTagTemplate template = new MercadonaPackagingTagTemplate(packagingTag)){
			template.print(out);
		} catch (CanNotCreatePdfException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printPackaging(OutputStream out, CompanyFull company, Item item, byte[] logo, String barcode, Double quantity, String ean128, String sscc) {
		try {
			WarehouseTemplate template = new WarehouseTemplate(company, item, logo, barcode, quantity, ean128, sscc);
			template.print(out);
		} catch (CanNotCreatePdfException e) {
			e.printStackTrace();
		}
	}
	
	public static void printMultiplePackaging(OutputStream out, PackagingTag packagingTag) {
		try {
			MultipleWarehouseTemplate template = new MultipleWarehouseTemplate(packagingTag);
			template.print(out);
		} catch (CanNotCreatePdfException e) {
			e.printStackTrace();
		}		
	}
	
	public static void printSalesPackaging(OutputStream out, Sales sales, Delivery delivery) {
		try {
			WarehouseSaleTemplate template = new WarehouseSaleTemplate(sales, delivery);
			template.print(out);
		} catch (CanNotCreatePdfException e) {
			e.printStackTrace();
		}		
	}
}

package net.aonsolutions.aon.in.pdf.maker;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.aon.in.pdf.maker.exception.CanNotCreatePdfException;
import net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplate;
import net.aonsolutions.aon.in.pdf.maker.invoice.InvoiceTemplateContext;
import net.aonsolutions.aon.in.pdf.maker.warehouse.DeliveryTemplate;
import net.aonsolutions.aon.in.pdf.maker.warehouse.MultipleWarehouseTemplate;
import net.aonsolutions.aon.in.pdf.maker.warehouse.PackagingTag;
import net.aonsolutions.aon.in.pdf.maker.warehouse.WarehouseSaleTemplate;
import net.aonsolutions.aon.in.pdf.maker.warehouse.WarehouseTemplate;
import net.aonsolutions.aon.in.pdf.maker.warehouse.paturpat.GenericPackagingTagTemplate;
import net.aonsolutions.aon.in.pdf.maker.warehouse.paturpat.MercadonaPackagingTagTemplate;

public class PdfMaker {

	private PdfMaker() {
	
	}
	
	/**
	 * CREATE INVOICE PDF
	 * 
	 * @param out
	 * @param invoice, Invoice Object
	 * @param config, print invoice configuration
	 */
	public static void printInvoice(OutputStream out, CompanyFull company, InvoiceCommunicationConfiguration icc, Invoice invoice, PrintInvoiceConfiguration config, String qrUrl, byte[] logo, String tbaiId) {
		try {
			InvoiceTemplateContext context = new InvoiceTemplateContext(company, AonCollectionUtils.toList(invoice), config, qrUrl, logo, tbaiId);
			context.setAdministration(icc.getAdministration().orElse(null));
			InvoiceTemplate template = new InvoiceTemplate(context);
			template.print(context, out);
		} catch (CanNotCreatePdfException e) {
			e.printStackTrace();
		}		
	}
	
	public static void printInvoice(OutputStream out, CompanyFull company, InvoiceCommunicationConfiguration icc, List<Invoice> invoices, PrintInvoiceConfiguration config, String qrUrl, byte[] logo, String tbaiId) {
		try {
			InvoiceTemplateContext context = new InvoiceTemplateContext(company, invoices, config, qrUrl, logo, tbaiId);
			context.setAdministration(icc.getAdministration().orElse(null));
			InvoiceTemplate template = new InvoiceTemplate(context);
			template.print(context, out);
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
	
	public static void printDelivery(OutputStream out, CompanyFull company, Delivery delivery, Warehouse warehouse, byte[] logo) {
		try (DeliveryTemplate template = new DeliveryTemplate(delivery, warehouse, company, logo)) {
			template.save(out);
		} catch (CanNotCreatePdfException | IOException e) {
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

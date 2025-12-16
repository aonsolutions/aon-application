package net.aonsolutions.aon.verifactu;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class InvoiceTypes {
	static EnterpriseActivity getActivityGeneral( AONContext ctx, int domain, VATRegime regime ) {
		return CompanyDAO.getEnterpriseActivities(ctx,domain)
			.filter(a -> a.getVatRegime() == regime)
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("No se ha encontrado actividad para regime " + regime));
	}
	static EnterpriseActivity getActivityGeneral( AONContext ctx, int domain ) {
		return getActivityGeneral(ctx, domain, VATRegime.GENERAL);
	}
	static EnterpriseActivity getActivityExempt( AONContext ctx, int domain ) {
		return getActivityGeneral(ctx, domain, VATRegime.EXEMPT);
	}
	static EnterpriseActivity getActivitySimplified( AONContext ctx, int domain ) {
		return getActivityGeneral(ctx, domain, VATRegime.SIMPLIFIED);
	}
	
	public enum Invoices {
		VENTA_NACIONAL_SIMPLE {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getCustomer(ctx, domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.NATIONAL)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.setAddress( customer.getMainAddress() )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(21.0)
							.setQuota(21.0)
							.setDeductibleQuota(21.0)
							.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						)
					)
					.setVatQuota(21.0)
					.setTotal(121.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_NACIONAL_SIMPLIFICADA {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getContadoCustomer(ctx, domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.NATIONAL)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(21.0)
							.setQuota(21.0)
							.setDeductibleQuota(21.0)
							.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						)
					)
					.setVatQuota(21.0)
					.setTotal(121.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_NACIONAL_SIMPLIFICADA_CON_CUSTOMER_SIN_DIRECCION {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getCustomer(ctx,domain);
				customer.getAddresses().clear();
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.NATIONAL)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(21.0)
							.setQuota(21.0)
							.setDeductibleQuota(21.0)
							.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						)
					)
					.setVatQuota(21.0)
					.setTotal(121.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_NACIONAL_SUPLIDOS {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getCustomer(ctx,domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.NATIONAL)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.setAddress( customer.getMainAddress() )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(21.0)
							.setQuota(21.0)
							.setDeductibleQuota(21.0)
							.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						)
					)
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(500.0)
						.setTaxableBase(500.0)
						.setPrepayment(true)
					)
					.setVatQuota(21.0)
					.setTotal(621.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_NACIONAL_RECTIFICATIVA_SIMPLE {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getCustomer(ctx,domain);
				String series = VerifactuTestsUtils.rectSeries( ctx );
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.NATIONAL)
					.setRectificationType(RectificationType.NORMAL_RECTIFIER)
					.setRectificationInvoice( 1 )
					.setRectificationInvoiceSeries("A" + AonDateUtils.getYear(VerifactuTestsUtils.issueDate()))
					.setRectificationInvoiceNumber( 1 )
					.setRectificationInvoiceReference("A" + AonDateUtils.getYear(VerifactuTestsUtils.issueDate()) + "/1")
					.setRectificationInvoiceDate(VerifactuTestsUtils.issueDate())
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.setAddress( customer.getMainAddress() )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(21.0)
							.setQuota(21.0)
							.setDeductibleQuota(21.0)
							.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						)
					)
					.setVatQuota(21.0)
					.setTotal(121.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_NACIONAL_RECTIFICATIVA_SIMPLIFICADA {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getContadoCustomer(ctx, domain);
				String series = VerifactuTestsUtils.rectSeries(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.NATIONAL)
					.setRectificationType(RectificationType.NORMAL_RECTIFIER)
					.setRectificationInvoice( 1 )
					.setRectificationInvoiceSeries("A" + AonDateUtils.getYear(VerifactuTestsUtils.issueDate()))
					.setRectificationInvoiceNumber( 1 )
					.setRectificationInvoiceReference("A" + AonDateUtils.getYear(VerifactuTestsUtils.issueDate()) + "/1")
					.setRectificationInvoiceDate(VerifactuTestsUtils.issueDate())
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(21.0)
							.setQuota(21.0)
							.setDeductibleQuota(21.0)
							.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						)
					)
					.setVatQuota(21.0)
					.setTotal(121.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_ISP {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getCustomer(ctx,domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.OTHER_ISP)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.setAddress( customer.getMainAddress() )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(0.0)
							.setQuota(0.0)
							.setDeductibleQuota(0.0)
							.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						)
					)
					.setVatQuota(0.0)
					.setTotal(100.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_NACIONAL_RE {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getCustomer(ctx,domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.NATIONAL)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(true)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.setAddress( customer.getMainAddress() )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(21.0)
							.setQuota(21.0)
							.setSurcharge(5.2)
							.setSurchargeQuota(5.2)
							.setDeductibleQuota(26.2)
						)
					)
					.setVatQuota(21.0)
					.setTotal(121.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_NACIONAL_IRPF_PROFESSIONAL {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getCustomer(ctx,domain); 
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.NATIONAL)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(true)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.setAddress( customer.getMainAddress() )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(21.0)
							.setQuota(21.0)
							.setDeductibleQuota(21.0)
							.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.RETENTION)
							.setBase(100.0)
							.setPercentage(15.0)
							.setQuota(15.0)
							.setDeductibleQuota(15.0)
							.setWithholdingType(WithholdingType.PROFESSIONAL)
						)
					)
					.setVatQuota(21.0)
					.setRetentionQuota(15.0)
					.setTotal(106.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_INTRACOMUNITARIA {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getIntrCustomer(ctx,domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.INTRACOMMUNITY)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.setAddress( customer.getMainAddress()  )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(0.0)
							.setQuota(0.0)
							.setDeductibleQuota(0.0)
							.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						)
					)
					.setVatQuota(0.0)
					.setTotal(100.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_INTRACOMUNITARIA_SERVICIOS {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getIntrCustomer(ctx,domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.INTRACOMMUNITY)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(true)
					.setAddress( customer.getMainAddress()  )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(0.0)
							.setQuota(0.0)
							.setDeductibleQuota(0.0)
							.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						)
					)
					.setVatQuota(0.0)
					.setTotal(100.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_NACIONAL_EXENTA_E1 {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getCustomer(ctx,domain); 
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setActivity(getActivityExempt(ctx, domain))
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.NATIONAL)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.setAddress( customer.getMainAddress() )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(0.0)
							.setQuota(0.0)
							.setDeductibleQuota(0.0)
							.setVatDeductionType(VatDeductionType.WITHOUT_RIGHT)
						)
					)
					.setVatQuota(0.0)
					.setTotal(100.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_ANULADA {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getCustomer(ctx,domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.NATIONAL)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.setAnnulled(true)
					.setAddress( customer.getMainAddress() )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(21.0)
							.setQuota(21.0)
							.setDeductibleQuota(0.0)
							.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						)
					)
					.setVatQuota(21.0)
					.setTotal(121.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_NACIONAL_SIMPLE_CRITERIO_CAJA {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getCustomer(ctx,domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.NATIONAL)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(true)
					.setInvestment(false)
					.setService(false)
					.setAddress( customer.getMainAddress() )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(21.0)
							.setQuota(21.0)
							.setDeductibleQuota(21.0)
							.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						)
					)
					.setVatQuota(21.0)
					.setTotal(121.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_NACIONAL_CLIENTE_NO_CENSADO {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getCustomerNoCensado(ctx, domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.NATIONAL)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.setAddress( customer.getMainAddress() )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(21.0)
							.setQuota(21.0)
							.setDeductibleQuota(21.0)
							.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						)
					)
					.setVatQuota(21.0)
					.setTotal(121.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_NACIONAL_CLIENTE_CEDILLA {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getCustomerCedilla(ctx, domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.NATIONAL)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.setAddress( customer.getMainAddress() )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(21.0)
							.setQuota(21.0)
							.setDeductibleQuota(21.0)
							.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						)
					)
					.setVatQuota(21.0)
					.setTotal(121.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_NACIONAL_CLIENTE_APOSTOFRE{
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getCustomerApostrofe(ctx, domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.NATIONAL)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.setAddress( customer.getMainAddress() )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
						.addTax(new InvoiceTax()
							.setTaxType(TaxType.VAT)
							.setBase(100.0)
							.setPercentage(21.0)
							.setQuota(21.0)
							.setDeductibleQuota(21.0)
							.setVatDeductionType(VatDeductionType.WITH_RIGHT)
						)
					)
					.setVatQuota(21.0)
					.setTotal(121.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_EXTRACOMUNITARIA {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getExtrCustomer(ctx,domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.setAddress( customer.getMainAddress()  )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
					)
					.setVatQuota(0.0)
					.setTotal(100.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_EXTRACOMUNITARIA_SERVICIO {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getExtrCustomer(ctx,domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(true)
					.setAddress( customer.getMainAddress()  )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
					)
					.setVatQuota(0.0)
					.setTotal(100.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_CAN_CEU_MEL {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getCanCeuMelCustomer(ctx,domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.CAN_CEU_MEL)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(false)
					.setAddress( customer.getMainAddress()  )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
					)
					.setVatQuota(0.0)
					.setTotal(100.0)
					.refreshTaxBreakdown()
				;
			}
		},
		VENTA_CAN_CEU_MEL_SERVICIO {
			@Override
			public Invoice get(AONContext ctx, int domain) {
				CustomerFull customer = VerifactuTestsUtils.getCanCeuMelCustomer(ctx,domain);
				String series = VerifactuTestsUtils.series(ctx);
				int number = 0;
				return new Invoice()
					.setDomain(domain)
					.setType(InvoiceType.SALES)
					.setSeries(series)
					.setNumber(number)
					.setIssueDate(VerifactuTestsUtils.issueDate())
					.setTaxDate(VerifactuTestsUtils.issueDate())
					.setTransaction(InvoiceTransactionType.CAN_CEU_MEL)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setRegistry(customer.getRegistry().getId())
					.setRegistryDocument(customer.getRegistry().getDocument())
					.setRegistryDocumentType(customer.getRegistry().getDocumentType())
					.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry())
					.setRegistryName(customer.getRegistry().getName())
					.setSurcharge(false)
					.setWithholding(false)
					.setWithholdingFarmer(false)
					.setVatAccrualPayment(false)
					.setInvestment(false)
					.setService(true)
					.setAddress( customer.getMainAddress()  )
					.addDetail(new InvoiceDetail()
						.setSource(InvoiceSource.TEDI)
						.setQuantity(1)
						.setPrice(100.0)
						.setTaxableBase(100.0)
					)
					.setVatQuota(0.0)
					.setTotal(100.0)
					.refreshTaxBreakdown()
				;
			}
		},
		;
		
		public abstract Invoice get( AONContext ctx, int domain);
	}

	public static List<Invoice> getAll(AONContext ctx, int domain) {
		return AonCollectionUtils.stream( Invoices.values() )
			.map(i -> i.get(ctx,domain))
			.collect(Collectors.toCollection(LinkedList::new));
	}
}

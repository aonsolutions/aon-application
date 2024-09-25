import { AonElement } from 'aonsolutions/components/AonElement.js';
import { MSG, CSS, EVENT, TAG } from 'aonsolutions/environments/environments.js'; 
import * as LS  from 'aonsolutions/services/localStorageService.js';

export class AonJsfApp extends AonElement {
	
	IFRAME;
	
	viewId;
	action;
	actionListener;
	
	constructor () {
		super();
	}

	connectedCallback () {
		this.clear();
		this.init();
		this.build();
	}

	init() {
        this.id = 'aonJsfApp';
		this.IFRAME = this.id + 'Frame';
	}

	build() {
        let iframe = this.createElement(TAG.IFRAME, this.IFRAME);
        iframe.name = this.IFRAME;
        iframe.style.border = 'none';
        iframe.style.width = '100%';
        iframe.style.height = 'calc(100vh - 69px)';
        

        this.appendChild(iframe);

        let form = this.createElement(TAG.FORM);
		form.style.display = 'none';        
        form.action = 'jsfapp';
        form.target = this.IFRAME;
        
        let viewIdInput = this.createElement(TAG.INPUT);
        viewIdInput.type = 'hidden';
        viewIdInput.name = 'viewId';
        viewIdInput.value = this.viewId;
		form.appendChild(viewIdInput);
		
        let actionInput = this.createElement(TAG.INPUT);
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = this.action;
		form.appendChild(actionInput);

        let actionListenerInput = this.createElement(TAG.INPUT);
        actionListenerInput.type = 'hidden';
        actionListenerInput.name = 'actionListener';
        actionListenerInput.value = this.actionListener;
		form.appendChild(actionListenerInput);

        let tokenInput = this.createElement(TAG.INPUT);
        tokenInput.type = 'hidden';
        tokenInput.name = 'token';
        tokenInput.value = LS.getToken();
		form.appendChild(tokenInput);

		let domainInput = this.createElement(TAG.INPUT);
		domainInput.type = 'hidden';
		domainInput.name = 'com.code.aon.jaas.domain';
		domainInput.value = LS.getDomainName();
		form.appendChild(domainInput);


		this.appendChild(form);

        form.submit();
        
	}

    getIFrame(){
        return this.getElement(this.IFRAME);
    }
    
     setViewId(viewId){
		this.viewId = viewId;
	}
	
	
}

/*
	Management
*/

export class AonJsfSale extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/sale.xhtml');
	}
	
}

export class AonJsfPurchase extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/purchase.xhtml');
	}
	
}

export class AonJsfExpense extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/expense.xhtml');
	}
	
}

export class AonJsfProduct extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/product.xhtml');
	}
	
}

export class AonJsfCustomer extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/customer.xhtml');
	}
	
}

export class AonJsfSupplier extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/supplier.xhtml');
	}
	
}

export class AonJsfCreditor extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/creditor.xhtml');
	}
	
}


export class AonJsfSaleInvoice extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/saleInvoice.xhtml');
	}
	
}

export class AonJsfPurchaseInvoice extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/purchaseInvoice.xhtml');
	}
	
}

export class AonJsfExpenseInvoice extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/expenseInvoice.xhtml');
	}
}

export class AonJsfUndeductibleInvoice extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/undeductibleInvoice.xhtml');
	}
}


export class AonJsfInvoicePrint extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/invoicePrint.xhtml');
	}
}

export class AonJsfInvoiceRemove extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/invoiceRemove.xhtml');
	}
}

export class AonJsfInvoiceDelivery extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/invoiceDelivery.xhtml');
	}
}

export class AonJsfFinanceCharge extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/financeCharge.xhtml');
	}
}

export class AonJsfFinancePayment extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/financePayment.xhtml');
	}
}

export class AonJsfFBatchCharge extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/fbatchCharge.xhtml');
	}
}

export class AonJsfFBatchPayment extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/fbatchPayment.xhtml');
	}
}

export class AonJsfPayMethod extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/payMethod.xhtml');
	}
}

export class AonJsfProductCategory extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/productCategory.xhtml');
	}
}

export class AonJsfSegment extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/segment.xhtml');
	}
}

export class AonJsfGeotree extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/geotree.xhtml');
	}
}

export class AonJsfSddMandate extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/sddMandate.xhtml');
	}
}

export class AonJsfFPaymentPrint extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/fPaymentPrint.xhtml');
	}
}

export class AonJsfBankStatement extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/bankStatement.xhtml');
	}
}

export class AonJsfPrepayment extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/prepayment.xhtml');
	}
}

export class AonJsfIncreaseItem extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/increaseItem.xhtml');
	}
}

export class AonJsfInvoicingGroup extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/invoicingGroup.xhtml');
	}
}

export class AonJsfInvoiceSigner extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/invoiceSigner.xhtml');
	}
}

export class AonJsfFinancePrint extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/financePrint.xhtml');
	}
}

export class AonJsfFinanceChequing extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/financeChequing.xhtml');
	}
}

export class AonJsfCashFlowForecast extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/cashFlowForecast.xhtml');
	}
}

export class AonJsfCashFlowForecastReport extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/cashFlowForecastReport.xhtml');
	}
}

export class AonJsfFeeInvoicing extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/feeInvoicing.xhtml');
	}
}

export class AonJsfFeePreInvoicing extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/feePreInvoicing.xhtml');
	}
}

export class AonJsfFeePrint extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/feePrint.xhtml');
	}
}

export class AonJsfFeeAssigment extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/feeAssigment.xhtml');
	}
}

export class AonJsfProjectCommercial extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/projectCommercial.xhtml');
	}
}

export class AonJsfCommercialTerm extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/commercialTerm.xhtml');
	}
}

export class AonJsfCommercialTracking extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/commercialTracking.xhtml');
	}
}

export class AonJsfCommercialActivity extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/commercialActivity.xhtml');
	}
}

export class AonJsfOffer extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/offer.xhtml');
	}
}

export class AonJsfCommercialStatSeller extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/commercialStatSeller.xhtml');
	}
}

export class AonJsfCommercialStatProduct extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/commercialStatProduct.xhtml');
	}
}

export class AonJsfCommercialStatTarget extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/commercialStatTarget.xhtml');
	}
}

export class AonJsfCommercialStatCategory extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/commercialStatCategory.xhtml');
	}
}

export class AonJsfSeller extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/seller.xhtml');
	}
}

export class AonJsfTarget extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/target.xhtml');
	}
}

export class AonJsfTargetDeduplication extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/targetDeduplication.xhtml');
	}
}

export class AonJsfCommercialSellerStat extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/commercialSellerStat.xhtml');
	}
}

export class AonJsfCommercialProductStat extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/commercialProductStat.xhtml');
	}
}

export class AonJsfCommercialTargetStat extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/commercialTargetStat.xhtml');
	}
}

export class AonJsfCommercialCategoryStat extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/commercialCategoryStat.xhtml');
	}
}

export class AonJsfOfferDetailCommission extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/offerDetailCommission.xhtml');
	}
}

export class AonJsfCommission extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/commission.xhtml');
	}
}

export class AonJsfCommissionCalc extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/commissionCalc.xhtml');
	}
}

export class AonJsfCommissionType extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/commissionType.xhtml');
	}
}

export class AonJsfCommercialGeozoneStat extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/commercialGeozoneStat.xhtml');
	}
}

export class AonJsfNews extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/news.xhtml');
	}
}

export class AonJsfNewsletter extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/newsletter.xhtml');
	}
}

export class AonJsfMessages extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/messages.xhtml');
	}
}

export class AonJsfHtmlTemplate extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/htmlTemplate.xhtml');
	}
}

export class AonJsfMarketingTemplate extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/marketingTemplate.xhtml');
	}
}

export class AonJsfCompanyImages extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/companyImages.xhtml');
	}
}

export class AonJsfMailProcess extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/mailProcess.xhtml');
	}
}

export class AonJsfCommunicationCenter extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/communicationCenter.xhtml');
	}
}

export class AonJsfMarketingCampaign extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/marketingCampaign.xhtml');
	}
}

export class AonJsfSurvey extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/survey.xhtml');
	}
}

export class AonJsfSurveyResponse extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/surveyResponse.xhtml');
	}
}

export class AonJsfProject extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/project.xhtml');
	}
}

export class AonJsfProjectType extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/projectType.xhtml');
	}
}

export class AonJsfActivityType extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/activityType.xhtml');
	}
}

if(!window.customElements.get(TAG.AON_JSF_APP)){
	window.customElements.define(TAG.AON_JSF_APP, AonJsfApp);
}

if(!window.customElements.get(TAG.AON_JSF_SALE)){
	window.customElements.define(TAG.AON_JSF_SALE, AonJsfSale);
}

if(!window.customElements.get(TAG.AON_JSF_PURCHASE)){
	window.customElements.define(TAG.AON_JSF_PURCHASE, AonJsfPurchase);
}

if(!window.customElements.get(TAG.AON_JSF_EXPENSE)){
	window.customElements.define(TAG.AON_JSF_EXPENSE, AonJsfExpense);
}

if(!window.customElements.get(TAG.AON_JSF_PRODUCT)){
	window.customElements.define(TAG.AON_JSF_PRODUCT, AonJsfProduct);
}

if(!window.customElements.get(TAG.AON_JSF_CUSTOMER)){
	window.customElements.define(TAG.AON_JSF_CUSTOMER, AonJsfCustomer);
}

if(!window.customElements.get(TAG.AON_JSF_CREDITOR)){
	window.customElements.define(TAG.AON_JSF_CREDITOR, AonJsfCreditor);
}

if(!window.customElements.get(TAG.AON_JSF_SUPPLIER)){
	window.customElements.define(TAG.AON_JSF_SUPPLIER, AonJsfSupplier);
}

if(!window.customElements.get(TAG.AON_JSF_SALE_INVOICE)){
	window.customElements.define(TAG.AON_JSF_SALE_INVOICE, AonJsfSaleInvoice);
}

if(!window.customElements.get(TAG.AON_JSF_PURCHASE_INVOICE)){
	window.customElements.define(TAG.AON_JSF_PURCHASE_INVOICE, AonJsfPurchaseInvoice);
}
if(!window.customElements.get(TAG.AON_JSF_EXPENSE_INVOICE)){
	window.customElements.define(TAG.AON_JSF_EXPENSE_INVOICE, AonJsfExpenseInvoice);
}

if(!window.customElements.get(TAG.AON_JSF_UNDEDUCTIBLE_INVOICE)){
	window.customElements.define(TAG.AON_JSF_UNDEDUCTIBLE_INVOICE, AonJsfUndeductibleInvoice);
}

if(!window.customElements.get(TAG.AON_JSF_INVOICE_PRINT)){
	window.customElements.define(TAG.AON_JSF_INVOICE_PRINT, AonJsfInvoicePrint);
}

if(!window.customElements.get(TAG.AON_JSF_INVOICE_REMOVE)){
	window.customElements.define(TAG.AON_JSF_INVOICE_REMOVE, AonJsfInvoiceRemove);
}

if(!window.customElements.get(TAG.AON_JSF_INVOICE_DELIVERY)){
	window.customElements.define(TAG.AON_JSF_INVOICE_DELIVERY, AonJsfInvoiceDelivery);
}

if(!window.customElements.get(TAG.AON_JSF_FINANCE_CHARGE)){
	window.customElements.define(TAG.AON_JSF_FINANCE_CHARGE, AonJsfFinanceCharge);
}

if(!window.customElements.get(TAG.AON_JSF_FINANCE_PAYMENT)){
	window.customElements.define(TAG.AON_JSF_FINANCE_PAYMENT, AonJsfFinancePayment);
}

if(!window.customElements.get(TAG.AON_JSF_PAY_METHOD)){
	window.customElements.define(TAG.AON_JSF_PAY_METHOD, AonJsfPayMethod);
}

if(!window.customElements.get(TAG.AON_JSF_PRODUCT_CATEGORY)){
	window.customElements.define(TAG.AON_JSF_PRODUCT_CATEGORY, AonJsfProductCategory);
}

if(!window.customElements.get(TAG.AON_JSF_SEGMENT)){
	window.customElements.define(TAG.AON_JSF_SEGMENT, AonJsfSegment);
}

if(!window.customElements.get(TAG.AON_JSF_GEOTREE)){
	window.customElements.define(TAG.AON_JSF_GEOTREE, AonJsfGeotree);
}

if(!window.customElements.get(TAG.AON_JSF_FBATCH_CHARGE)){
	window.customElements.define(TAG.AON_JSF_FBATCH_CHARGE, AonJsfFBatchCharge);
}

if(!window.customElements.get(TAG.AON_JSF_FBATCH_PAYMENT)){
	window.customElements.define(TAG.AON_JSF_FBATCH_PAYMENT, AonJsfFBatchPayment);
}

if(!window.customElements.get(TAG.AON_JSF_SDD_MANDATE)){
	window.customElements.define(TAG.AON_JSF_SDD_MANDATE, AonJsfSddMandate);
}

if(!window.customElements.get(TAG.AON_JSF_FPAYMENT_PRINT)){
	window.customElements.define(TAG.AON_JSF_FPAYMENT_PRINT, AonJsfFPaymentPrint);
}

if(!window.customElements.get(TAG.AON_JSF_BANK_STATEMENT)){
	window.customElements.define(TAG.AON_JSF_BANK_STATEMENT, AonJsfBankStatement);
}

if(!window.customElements.get(TAG.AON_JSF_PREPAYMENT)){
	window.customElements.define(TAG.AON_JSF_PREPAYMENT, AonJsfPrepayment);
}

if(!window.customElements.get(TAG.AON_JSF_INCREASE_ITEM)){
	window.customElements.define(TAG.AON_JSF_INCREASE_ITEM, AonJsfIncreaseItem);
}

if(!window.customElements.get(TAG.AON_JSF_INVOICING_GROUP)){
	window.customElements.define(TAG.AON_JSF_INVOICING_GROUP, AonJsfInvoicingGroup);
}

if(!window.customElements.get(TAG.AON_JSF_INVOICE_SIGNER)){
	window.customElements.define(TAG.AON_JSF_INVOICE_SIGNER, AonJsfInvoiceSigner);
}

if(!window.customElements.get(TAG.AON_JSF_FINANCE_PRINT)){
	window.customElements.define(TAG.AON_JSF_FINANCE_PRINT, AonJsfFinancePrint);
}

if(!window.customElements.get(TAG.AON_JSF_FINANCE_CHEQUING)){
	window.customElements.define(TAG.AON_JSF_FINANCE_CHEQUING, AonJsfFinanceChequing);
}

if(!window.customElements.get(TAG.AON_JSF_CASHFLOW_FORECAST)){
	window.customElements.define(TAG.AON_JSF_CASHFLOW_FORECAST, AonJsfCashFlowForecast);
}

if(!window.customElements.get(TAG.AON_JSF_CASHFLOW_FORECAST_REPORT)){
	window.customElements.define(TAG.AON_JSF_CASHFLOW_FORECAST_REPORT, AonJsfCashFlowForecastReport);
}

if(!window.customElements.get(TAG.AON_JSF_FEE_PRINT)){
	window.customElements.define(TAG.AON_JSF_FEE_PRINT, AonJsfFeePrint);
}

if(!window.customElements.get(TAG.AON_JSF_FEE_INVOICING)){
	window.customElements.define(TAG.AON_JSF_FEE_INVOICING, AonJsfFeeInvoicing);
}

if(!window.customElements.get(TAG.AON_JSF_FEE_PRE_INVOICING)){
	window.customElements.define(TAG.AON_JSF_FEE_PRE_INVOICING, AonJsfFeePreInvoicing);
}

if(!window.customElements.get(TAG.AON_JSF_FEE_ASSIGNMENT)){
	window.customElements.define(TAG.AON_JSF_FEE_ASSIGNMENT, AonJsfFeeAssigment);
}

if(!window.customElements.get(TAG.AON_JSF_PROJECT_COMMERCIAL)){
	window.customElements.define(TAG.AON_JSF_PROJECT_COMMERCIAL, AonJsfProjectCommercial);
}

if(!window.customElements.get(TAG.AON_JSF_COMMERCIAL_TERM)){
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_TERM, AonJsfCommercialTerm);
}

if(!window.customElements.get(TAG.AON_JSF_COMMERCIAL_TRACKING)){
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_TRACKING, AonJsfCommercialTracking);
}

if(!window.customElements.get(TAG.AON_JSF_COMMERCIAL_ACTIVITY)){
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_ACTIVITY, AonJsfCommercialActivity);
}

if(!window.customElements.get(TAG.AON_JSF_OFFER)){
	window.customElements.define(TAG.AON_JSF_OFFER, AonJsfOffer);
}

if(!window.customElements.get(TAG.AON_JSF_COMMERCIAL_STAT_SELLER)){
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_STAT_SELLER, AonJsfCommercialStatSeller);
}

if(!window.customElements.get(TAG.AON_JSF_COMMERCIAL_STAT_PRODUCT)){
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_STAT_PRODUCT, AonJsfCommercialStatProduct);
}

if(!window.customElements.get(TAG.AON_JSF_COMMERCIAL_STAT_CATEGORY)){
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_STAT_CATEGORY, AonJsfCommercialStatCategory);
}

if(!window.customElements.get(TAG.AON_JSF_COMMERCIAL_STAT_TARGET)){
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_STAT_TARGET, AonJsfCommercialStatTarget);
}

if(!window.customElements.get(TAG.AON_JSF_SELLER)){
	window.customElements.define(TAG.AON_JSF_SELLER, AonJsfSeller);
}

if(!window.customElements.get(TAG.AON_JSF_TARGET)){
	window.customElements.define(TAG.AON_JSF_TARGET, AonJsfTarget);
}

if(!window.customElements.get(TAG.AON_JSF_TARGET_DEDUPLICATION)){
	window.customElements.define(TAG.AON_JSF_TARGET_DEDUPLICATION, AonJsfTargetDeduplication);
}

if(!window.customElements.get(TAG.AON_JSF_COMMERCIAL_SELLER_STAT)){
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_SELLER_STAT, AonJsfCommercialSellerStat);
}

if(!window.customElements.get(TAG.AON_JSF_COMMERCIAL_PRODUCT_STAT)){
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_PRODUCT_STAT, AonJsfCommercialProductStat);
}

if(!window.customElements.get(TAG.AON_JSF_COMMERCIAL_CATEGORY_STAT)){
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_CATEGORY_STAT, AonJsfCommercialCategoryStat);
}

if(!window.customElements.get(TAG.AON_JSF_COMMERCIAL_TARGET_STAT)){
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_TARGET_STAT, AonJsfCommercialTargetStat);
}

if(!window.customElements.get(TAG.AON_JSF_COMMERCIAL_GEOZONE_STAT)){
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_GEOZONE_STAT, AonJsfCommercialGeozoneStat);
}


if(!window.customElements.get(TAG.AON_JSF_OFFER_DETAIL_COMMISSION)){
	window.customElements.define(TAG.AON_JSF_OFFER_DETAIL_COMMISSION, AonJsfOfferDetailCommission);
}

if(!window.customElements.get(TAG.AON_JSF_COMMISSION)){
	window.customElements.define(TAG.AON_JSF_COMMISSION, AonJsfCommission);
}

if(!window.customElements.get(TAG.AON_JSF_COMMISSION_CALC)){
	window.customElements.define(TAG.AON_JSF_COMMISSION_CALC, AonJsfCommissionCalc);
}

if(!window.customElements.get(TAG.AON_JSF_COMMISSION_TYPE)){
	window.customElements.define(TAG.AON_JSF_COMMISSION_TYPE, AonJsfCommissionType);
}

if(!window.customElements.get(TAG.AON_JSF_MESSAGES)){
	window.customElements.define(TAG.AON_JSF_MESSAGES, AonJsfMessages);
}

if(!window.customElements.get(TAG.AON_JSF_NEWS)){
	window.customElements.define(TAG.AON_JSF_NEWS, AonJsfNews);
}

if(!window.customElements.get(TAG.AON_JSF_NEWSLETTER)){
	window.customElements.define(TAG.AON_JSF_NEWSLETTER, AonJsfNewsletter);
}

if(!window.customElements.get(TAG.AON_JSF_HTML_TEMPLATE)){
	window.customElements.define(TAG.AON_JSF_HTML_TEMPLATE, AonJsfHtmlTemplate);
}

if(!window.customElements.get(TAG.AON_JSF_MARKETING_TEMPLATE)){
	window.customElements.define(TAG.AON_JSF_MARKETING_TEMPLATE, AonJsfMarketingTemplate);
}

if(!window.customElements.get(TAG.AON_JSF_COMPANY_IMAGES)){
	window.customElements.define(TAG.AON_JSF_COMPANY_IMAGES, AonJsfCompanyImages);
}

if(!window.customElements.get(TAG.AON_JSF_MAIL_PROCESS)){
	window.customElements.define(TAG.AON_JSF_MAIL_PROCESS, AonJsfMailProcess);
}

if(!window.customElements.get(TAG.AON_JSF_MARKETING_CAMPAIGN)){
	window.customElements.define(TAG.AON_JSF_MARKETING_CAMPAIGN, AonJsfMarketingCampaign);
}

if(!window.customElements.get(TAG.AON_JSF_COMMUNICATION_CENTER)){
	window.customElements.define(TAG.AON_JSF_COMMUNICATION_CENTER, AonJsfCommunicationCenter);
}

if(!window.customElements.get(TAG.AON_JSF_SURVEY)){
	window.customElements.define(TAG.AON_JSF_SURVEY, AonJsfSurvey);
}

if(!window.customElements.get(TAG.AON_JSF_SURVEY_RESPONSE)){
	window.customElements.define(TAG.AON_JSF_SURVEY_RESPONSE, AonJsfSurveyResponse);
}

if(!window.customElements.get(TAG.AON_JSF_PROJECT)){
	window.customElements.define(TAG.AON_JSF_PROJECT, AonJsfProject);
}

if(!window.customElements.get(TAG.AON_JSF_PROJECT_TYPE)){
	window.customElements.define(TAG.AON_JSF_PROJECT_TYPE, AonJsfProjectType);
}

if(!window.customElements.get(TAG.AON_JSF_ACTIVITY_TYPE)){
	window.customElements.define(TAG.AON_JSF_ACTIVITY_TYPE, AonJsfActivityType);
}



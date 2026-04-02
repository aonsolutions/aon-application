import { AonElement } from '../components/AonElement.js';
import { MSG, CSS, EVENT, TAG } from '../environments/environments.js';
import * as LS from '../services/localStorageService.js';

export class AonJsfApp extends AonElement {

	IFRAME;
	
	jsfapp = 'jsfapp';
	viewId = '';
	action = '';
	redirectUrl = '';
	elExpression = '';
	actionListener = '';
	expireSession = 'true';

	constructor() {
		super();
	}

	connectedCallback() {
		this.clear();
		this.init();
		this.build();
	}

	init() {
		this.id = 'aonJsfApp';
		this.IFRAME = this.id + 'Frame';
	}

	build() {

		let iframe = this.getIFrame();
		iframe.name = this.IFRAME;
		iframe.style.border = 'none';
		iframe.style.width = '100%';
		let top = this.getBoundingClientRect().top;
		iframe.style.height = `calc(100vh - ${top + 10}px)`;


		this.appendChild(iframe);

		let form = this.createElement(TAG.FORM);
		form.style.display = 'none';
		form.action =this.jsfapp;
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

		let domainIdInput = this.createElement(TAG.INPUT);
		domainIdInput.type = 'hidden';
		domainIdInput.name = 'domainId';
		domainIdInput.value = LS.getDomainId();
		form.appendChild(domainIdInput);

		let domainNameInput = this.createElement(TAG.INPUT);
		domainNameInput.type = 'hidden';
		domainNameInput.name = 'domainName';
		domainNameInput.value = LS.getDomainName();
		form.appendChild(domainNameInput);


		let languageInput = this.createElement(TAG.INPUT);
		languageInput.type = 'hidden';
		languageInput.name = 'language';
		languageInput.value = LS.getLanguage();
		form.appendChild(languageInput);

		let redirectUrlInput = this.createElement(TAG.INPUT);
		redirectUrlInput.type = 'hidden';
		redirectUrlInput.name = 'redirectUrl';
		redirectUrlInput.value = this.redirectUrl;
		form.appendChild(redirectUrlInput);

		let expireSessionInput = this.createElement(TAG.INPUT);
		expireSessionInput.type = 'hidden';
		expireSessionInput.name = 'expireSession';
		expireSessionInput.value = this.expireSession;
		form.appendChild(expireSessionInput);
		
		let readonlyInput = this.createElement(TAG.INPUT);
		readonlyInput.type = 'hidden';
		readonlyInput.name = 'readOnly';
		readonlyInput.value = isReadOnly();
		form.appendChild(readonlyInput);
		
		let jaasDomainInput = this.createElement(TAG.INPUT);
		jaasDomainInput.type = 'hidden';
		jaasDomainInput.name = 'com.code.aon.jaas.domain';
		form.appendChild(jaasDomainInput);
		
		let themeInput = this.createElement(TAG.INPUT);
		themeInput.type = 'hidden';
		themeInput.name = 'theme';
		themeInput.value = LS.getTheme() || LS.CUSTOM_THEME ;
		form.appendChild(themeInput);

		let elExpressionInput = this.createElement(TAG.INPUT);
		elExpressionInput.type = 'hidden';
		elExpressionInput.name = 'elExpression';
		elExpressionInput.value = this.elExpression;
		form.appendChild(elExpressionInput);

		this.appendChild(form);

		this.setJaasDomain( jaasDomainInput )
		.then( () => {
			form.submit();
			this.dispatchEvent(new CustomEvent(EVENT.BUILD, { panel: this }))
		} );
		



	}

	getForm() {
		return this.getIFrame().contentDocument?.querySelector(TAG.FORM);
	}
	
	getIFrame() {
		return this.getElement(this.IFRAME) || this.createElement(TAG.IFRAME, this.IFRAME);
	}

	setViewId(viewId) {
		this.viewId = viewId;
	}
	
	setJsfApp(jsfapp) {
		this.jsfapp = jsfapp;
	}

	setExpireSession(expireSession) {
		this.expireSession = expireSession;
	}

	setRedirectUrl(redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	
	setElExpression(elExpression) {
		this.elExpression = elExpression;
	}

	isLoaded() {
		let iframe = this.getIFrame();
		let idocument = iframe.document || iframe.contentDocument || iframe.contentWindow?.document;

		return new Promise((resolve, reject) => {
			if (idocument?.readyState === "complete") {
				resolve();
			} else if ( iframe.isConnected ) {
				iframe.addEventListener( EVENT.LOAD, resolve );
			} else  {
				this.addEventListener(EVENT.BUILD, () => {
					iframe.addEventListener( EVENT.LOAD, resolve );						
				});
			}
		});

	}
	
	getJsfApp(){
		return this.jsfapp;
	}

	getJaasDomain(dur) {
		if ( dur?.user?.domain == dur?.domain?.id ) 
			return dur?.domain?.name;
		if ( dur?.user?.domain == dur?.parentDomain?.id ) 
			return dur?.parentDomain?.name;
		
		let domainName = LS.getDomainName();
		let hostName = window?.location?.hostname;
		return domainName?.includes(hostName) ? hostName : domainName;
	}

	setJaasDomain(jaasDomainInput) {
		return new Promise( (resolve, reject) => {
			this.buildDur()
			.then( dur => jaasDomainInput.value = this.getJaasDomain(dur))
			.catch( () => jaasDomainInput.value = LS.getDomainName()  )
			.finally( () => resolve() );
	    });
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

// NO USAR EL BORRADO DE FACTURAS - ACTIVAR EN CASO NECESARIO.
// export class AonJsfInvoiceRemove extends AonJsfApp {

// 	constructor() {
// 		super();
// 		this.setViewId('/facelet/app/invoiceRemove.xhtml');
// 	}
// }

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
		this.setViewId('/facelet/app/feeAssignment.xhtml');
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

export class AonJsfOfferForm extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/offerForm.xhtml');
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

export class AonJsfProjectTas extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/projectTas.xhtml');
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

export class AonJsfMailContact extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/mailContact.xhtml');
	}
}

export class AonJsfMailAccount extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/mailAccount.xhtml');
	}
}

export class AonJsfMailSignature extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/mailSignature.xhtml');
	}
}

export class AonJsfMake extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/make.xhtml');
	}
}

export class AonJsfModel extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/model.xhtml');
	}
}

export class AonJsfTasItem extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/tasItem.xhtml');
	}
}

export class AonJsfTasStat extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/tasStat.xhtml');
	}
}

export class AonJsfGlobalConfig extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/globalConfig.xhtml');
	}
}

export class AonJsfTask extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/task.xhtml');
	}
}

export class AonJsfGantt extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/gantt.xhtml');
	}
}

export class AonJsfDailyTracking extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/dailyTracking.xhtml');
	}
}

export class AonJsfDailyTrackingReport extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/dailyTrackingReport.xhtml');
	}
}

export class AonJsfJobType extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/jobType.xhtml');
	}
}

export class AonJsfProcess extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/process.xhtml');
	}
}

export class AonJsfProcessTransactionType extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/processTransactionType.xhtml');
	}
}

export class AonJsfProcessWizard extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/processWizard.xhtml');
	}
}

export class AonJsfCampaign extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/campaign.xhtml');
	}
}

export class AonJsfCampaignType extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/campaignType.xhtml');
	}
}

export class AonJsfTaskHolder extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/taskHolder.xhtml');
	}
}

export class AonJsfWorkgroup extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/workgroup.xhtml');
	}
}

export class AonJsfCostProfile extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/costProfile.xhtml');
	}
}

export class AonJsfBrand extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/brand.xhtml');
	}
}

export class AonJsfProductTag extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/productTag.xhtml');
	}
}

export class AonJsfPackingTag extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/packingTag.xhtml');
	}
}

export class AonJsfTariff extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/tariff.xhtml');
	}
}

export class AonJsfCatalogue extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/catalogue.xhtml');
	}
}

export class AonJsfTax extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/tax.xhtml');
	}
}

export class AonJsfSeries extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/series.xhtml');
	}
}

export class AonJsfBankConcept extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/bankConcept.xhtml');
	}
}

export class AonJsfRelationship extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/relationship.xhtml');
	}
}

export class AonJsfLoader extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/loader.xhtml');
	}
}

export class AonJsfContractBatch extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/contractBatch.xhtml');
	}
}

export class AonJsfHolidays extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/holidays.xhtml');
	}
}

export class AonJsfIrpfData extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/irpfData.xhtml');
	}
}

export class AonJsfAmortization extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/amortization.xhtml');
	}
}

export class AonJsfPeriodAmortization extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/periodAmortization.xhtml');
	}
}

export class AonJsfEndPeriodEntries extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/endPeriodEntries.xhtml');
	}
}

export class AonJsfInvoiceIntegrity extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/invoiceIntegrity.xhtml');
	}
}

export class AonJsfInvoiceReport extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/invoiceReport.xhtml');
	}
}

export class AonJsfInvoiceRecorder extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/invoiceRecorder.xhtml');
	}
}

export class AonJsfInvoiceAmortization extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/invoiceAmortization.xhtml');
	}
}

export class AonJsfFinanceTrackingEntry extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/financeTrackingEntry.xhtml');
	}
}

export class AonJsfIncome extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/income.xhtml');
	}
}

export class AonJsfDelivery extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/delivery.xhtml');
	}
}

export class AonJsfWarehouseTransfer extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/warehouseTransfer.xhtml');
	}
}

export class AonJsfOrderServer extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/orderServer.xhtml');
	}
}

export class AonJsfOrderProposal extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/orderProposal.xhtml');
	}
}

export class AonJsfStock extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/stock.xhtml');
	}
}

export class AonJsfInventory extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/inventory.xhtml');
	}
}

export class AonJsfInventoryClose extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/inventoryClose.xhtml');
	}
}

export class AonJsfStockReportItem extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/stockReportItem.xhtml');
	}
}

export class AonJsfStockReportWarehouse extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/stockReportWarehouse.xhtml');
	}
}

export class AonJsfStockReportItemValued extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/stockReportItemValued.xhtml');
	}
}

export class AonJsfStockReportWarehouseValued extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/stockReportWarehouseValued.xhtml');
	}
}

export class AonJsfWarehouse extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/warehouse.xhtml');
	}
}

export class AonJsfCarrier extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/carrier.xhtml');
	}
}

export class AonJsfAccount extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/account.xhtml');
	}
}

export class AonJsfAccountingParams extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/accountingParams.xhtml');
	}
}

export class AonJsfFiscalParams extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/fiscalParams.xhtml');
	}
}

export class AonJsfPayrollParams extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/payrollParams.xhtml');
	}
}

export class AonJsfContractParams extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/contractParams.xhtml');
	}
}

export class AonJsfBalance extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/balance.xhtml');
	}
}

export class AonJsfAccPeriod extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/accPeriod.xhtml');
	}
}

export class AonJsfAmortizationType extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/amortizationType.xhtml');
	}
}

export class AonJsfAutConcept extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/autConcept.xhtml');
	}
}

export class AonJsfNewDomain extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/newDomain.xhtml');
	}
}

export class AonJsfRemoveDomain extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/removeDomain.xhtml');
	}
}

export class AonJsfAccountingBook extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/app/accountingBook.xhtml');
	}
}


export class AonJsfHelpContent extends AonJsfApp {

	constructor() {
		super();
		this.setViewId('/facelet/help/helpContent.xhtml');
	}
	
	connectedCallback() {
		this.buildDur()
		.catch( () => this.setJsfApp('jsfhelp') )
		.finally( () => super.connectedCallback() );
	}
	
	
}

export class AonJsfHelpNotification extends AonJsfApp {

	constructor() {
		super();
		this.setJsfApp('jsfhelp');
		this.setViewId('/facelet/help/helpNotification.xhtml');
	}
}

export class AonJsfGraph extends AonJsfApp {

	constructor() {
		super();
		this.setExpireSession(false);
	}

	connectedCallback() {
		super.clear();
		super.build();
		this.getIFrame().style.height = `22.5rem`;
	}
	
	getDocument(iframe) {
		return iframe.document 
		|| iframe.contentDocument 
		|| iframe.contentWindow?.document
	}
	
	isLoaded() {
		return new Promise((resolve, reject) => {
			let iframe = this.getIFrame();
			let idocument = this.getDocument(iframe);
			if ( idocument?.getElementById("completeSpan")){
				resolve();
			} else {
				iframe?.addEventListener("load", () => {
					let idocument = this.getDocument(iframe);
					if ( idocument?.getElementById("completeSpan")){
						resolve();
					} else {
						idocument?.addEventListener ("completed", resolve );	
					}
				});
				idocument?.addEventListener("completed", resolve );
			}
		});

	}
	

}

export class AonJsfAccountingGraph extends AonJsfGraph {

	constructor() {
		super();
		this.id = 'aonJsfAccountingGraph';
		this.IFRAME = this.id + 'Frame';
		this.setRedirectUrl('/facelet/app/accountingGraph.jsf');
	}

}

export class AonJsfPayrollGraph extends AonJsfGraph {

	constructor() {
		super();
		this.id = 'aonJsfPayrollGraph';
		this.IFRAME = this.id + 'Frame';
		this.setRedirectUrl('/facelet/app/payrollGraph.jsf');

	}

}

export class AonJsfContractGraph extends AonJsfGraph {

	constructor() {
		super();
		this.id = 'aonJsfContractOption';
		this.IFRAME = this.id + 'Frame';
		this.setRedirectUrl('/facelet/app/contractGraph.jsf');

	}

}

export class AonJsfContractOption extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/contractOption.xhtml');
	}
}

export class AonJsfTrainningCenter extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/trainningCenter.xhtml');
	}
}

export class AonJsfPosOpening extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/posOpening.xhtml');
	}
}

export class AonJsfPosClosing extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/posClosing.xhtml');
	}
}

export class AonJsfPosInvoice extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/posInvoice.xhtml');
	}
}

export class AonJsfPosFinance extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/posFinance.xhtml');
	}
}

export class AonJsfPos extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/pos.xhtml');
	}
}

export class AonJsfPosShift extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/posShift.xhtml');
	}
}


export class AonJsfCorporateIdentityLabel extends AonJsfApp {
	constructor() {
		super();
		this.setViewId('/facelet/app/corporateIdentityLabel.xhtml');
	}
}

export class AonJsfItemTagPrint extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/itemTagPrint.xhtml');
	}
}

export class AonJsfExpiredPassword extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/com/code/aon/ui/config/facelet/changePassword/expiredPasswordContent.xhtml');
	}
}


export class AonJsfPayMethodTypeDetail extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/payMethodTypeDetail.xhtml');
	}
}


if (!window.customElements.get(TAG.AON_JSF_APP)) {
	window.customElements.define(TAG.AON_JSF_APP, AonJsfApp);
}

if (!window.customElements.get(TAG.AON_JSF_SALE)) {
	window.customElements.define(TAG.AON_JSF_SALE, AonJsfSale);
}

if (!window.customElements.get(TAG.AON_JSF_PURCHASE)) {
	window.customElements.define(TAG.AON_JSF_PURCHASE, AonJsfPurchase);
}

if (!window.customElements.get(TAG.AON_JSF_EXPENSE)) {
	window.customElements.define(TAG.AON_JSF_EXPENSE, AonJsfExpense);
}

if (!window.customElements.get(TAG.AON_JSF_PRODUCT)) {
	window.customElements.define(TAG.AON_JSF_PRODUCT, AonJsfProduct);
}

if (!window.customElements.get(TAG.AON_JSF_CUSTOMER)) {
	window.customElements.define(TAG.AON_JSF_CUSTOMER, AonJsfCustomer);
}

if (!window.customElements.get(TAG.AON_JSF_CREDITOR)) {
	window.customElements.define(TAG.AON_JSF_CREDITOR, AonJsfCreditor);
}

if (!window.customElements.get(TAG.AON_JSF_SUPPLIER)) {
	window.customElements.define(TAG.AON_JSF_SUPPLIER, AonJsfSupplier);
}

if (!window.customElements.get(TAG.AON_JSF_SALE_INVOICE)) {
	window.customElements.define(TAG.AON_JSF_SALE_INVOICE, AonJsfSaleInvoice);
}

if (!window.customElements.get(TAG.AON_JSF_PURCHASE_INVOICE)) {
	window.customElements.define(TAG.AON_JSF_PURCHASE_INVOICE, AonJsfPurchaseInvoice);
}
if (!window.customElements.get(TAG.AON_JSF_EXPENSE_INVOICE)) {
	window.customElements.define(TAG.AON_JSF_EXPENSE_INVOICE, AonJsfExpenseInvoice);
}

if (!window.customElements.get(TAG.AON_JSF_UNDEDUCTIBLE_INVOICE)) {
	window.customElements.define(TAG.AON_JSF_UNDEDUCTIBLE_INVOICE, AonJsfUndeductibleInvoice);
}

if (!window.customElements.get(TAG.AON_JSF_INVOICE_PRINT)) {
	window.customElements.define(TAG.AON_JSF_INVOICE_PRINT, AonJsfInvoicePrint);
}

// if (!window.customElements.get(TAG.AON_JSF_INVOICE_REMOVE)) {
// 	window.customElements.define(TAG.AON_JSF_INVOICE_REMOVE, AonJsfInvoiceRemove);
// }

if (!window.customElements.get(TAG.AON_JSF_INVOICE_DELIVERY)) {
	window.customElements.define(TAG.AON_JSF_INVOICE_DELIVERY, AonJsfInvoiceDelivery);
}

if (!window.customElements.get(TAG.AON_JSF_FINANCE_CHARGE)) {
	window.customElements.define(TAG.AON_JSF_FINANCE_CHARGE, AonJsfFinanceCharge);
}

if (!window.customElements.get(TAG.AON_JSF_FINANCE_PAYMENT)) {
	window.customElements.define(TAG.AON_JSF_FINANCE_PAYMENT, AonJsfFinancePayment);
}

if (!window.customElements.get(TAG.AON_JSF_PAY_METHOD)) {
	window.customElements.define(TAG.AON_JSF_PAY_METHOD, AonJsfPayMethod);
}

if (!window.customElements.get(TAG.AON_JSF_PRODUCT_CATEGORY)) {
	window.customElements.define(TAG.AON_JSF_PRODUCT_CATEGORY, AonJsfProductCategory);
}

if (!window.customElements.get(TAG.AON_JSF_SEGMENT)) {
	window.customElements.define(TAG.AON_JSF_SEGMENT, AonJsfSegment);
}

if (!window.customElements.get(TAG.AON_JSF_GEOTREE)) {
	window.customElements.define(TAG.AON_JSF_GEOTREE, AonJsfGeotree);
}

if (!window.customElements.get(TAG.AON_JSF_FBATCH_CHARGE)) {
	window.customElements.define(TAG.AON_JSF_FBATCH_CHARGE, AonJsfFBatchCharge);
}

if (!window.customElements.get(TAG.AON_JSF_FBATCH_PAYMENT)) {
	window.customElements.define(TAG.AON_JSF_FBATCH_PAYMENT, AonJsfFBatchPayment);
}

if (!window.customElements.get(TAG.AON_JSF_SDD_MANDATE)) {
	window.customElements.define(TAG.AON_JSF_SDD_MANDATE, AonJsfSddMandate);
}

if (!window.customElements.get(TAG.AON_JSF_FPAYMENT_PRINT)) {
	window.customElements.define(TAG.AON_JSF_FPAYMENT_PRINT, AonJsfFPaymentPrint);
}

if (!window.customElements.get(TAG.AON_JSF_BANK_STATEMENT)) {
	window.customElements.define(TAG.AON_JSF_BANK_STATEMENT, AonJsfBankStatement);
}

if (!window.customElements.get(TAG.AON_JSF_PREPAYMENT)) {
	window.customElements.define(TAG.AON_JSF_PREPAYMENT, AonJsfPrepayment);
}

if (!window.customElements.get(TAG.AON_JSF_INCREASE_ITEM)) {
	window.customElements.define(TAG.AON_JSF_INCREASE_ITEM, AonJsfIncreaseItem);
}

if (!window.customElements.get(TAG.AON_JSF_INVOICING_GROUP)) {
	window.customElements.define(TAG.AON_JSF_INVOICING_GROUP, AonJsfInvoicingGroup);
}

if (!window.customElements.get(TAG.AON_JSF_INVOICE_SIGNER)) {
	window.customElements.define(TAG.AON_JSF_INVOICE_SIGNER, AonJsfInvoiceSigner);
}

if (!window.customElements.get(TAG.AON_JSF_FINANCE_PRINT)) {
	window.customElements.define(TAG.AON_JSF_FINANCE_PRINT, AonJsfFinancePrint);
}

if (!window.customElements.get(TAG.AON_JSF_FINANCE_CHEQUING)) {
	window.customElements.define(TAG.AON_JSF_FINANCE_CHEQUING, AonJsfFinanceChequing);
}

if (!window.customElements.get(TAG.AON_JSF_CASHFLOW_FORECAST)) {
	window.customElements.define(TAG.AON_JSF_CASHFLOW_FORECAST, AonJsfCashFlowForecast);
}

if (!window.customElements.get(TAG.AON_JSF_CASHFLOW_FORECAST_REPORT)) {
	window.customElements.define(TAG.AON_JSF_CASHFLOW_FORECAST_REPORT, AonJsfCashFlowForecastReport);
}

if (!window.customElements.get(TAG.AON_JSF_FEE_PRINT)) {
	window.customElements.define(TAG.AON_JSF_FEE_PRINT, AonJsfFeePrint);
}

if (!window.customElements.get(TAG.AON_JSF_FEE_INVOICING)) {
	window.customElements.define(TAG.AON_JSF_FEE_INVOICING, AonJsfFeeInvoicing);
}

if (!window.customElements.get(TAG.AON_JSF_FEE_PRE_INVOICING)) {
	window.customElements.define(TAG.AON_JSF_FEE_PRE_INVOICING, AonJsfFeePreInvoicing);
}

if (!window.customElements.get(TAG.AON_JSF_FEE_ASSIGNMENT)) {
	window.customElements.define(TAG.AON_JSF_FEE_ASSIGNMENT, AonJsfFeeAssigment);
}

if (!window.customElements.get(TAG.AON_JSF_PROJECT_COMMERCIAL)) {
	window.customElements.define(TAG.AON_JSF_PROJECT_COMMERCIAL, AonJsfProjectCommercial);
}

if (!window.customElements.get(TAG.AON_JSF_COMMERCIAL_TERM)) {
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_TERM, AonJsfCommercialTerm);
}

if (!window.customElements.get(TAG.AON_JSF_COMMERCIAL_TRACKING)) {
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_TRACKING, AonJsfCommercialTracking);
}

if (!window.customElements.get(TAG.AON_JSF_COMMERCIAL_ACTIVITY)) {
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_ACTIVITY, AonJsfCommercialActivity);
}

if (!window.customElements.get(TAG.AON_JSF_OFFER)) {
	window.customElements.define(TAG.AON_JSF_OFFER, AonJsfOffer);
}

if (!window.customElements.get(TAG.AON_JSF_OFFER_FORM)) {
	window.customElements.define(TAG.AON_JSF_OFFER_FORM, AonJsfOfferForm);
}

if (!window.customElements.get(TAG.AON_JSF_COMMERCIAL_STAT_SELLER)) {
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_STAT_SELLER, AonJsfCommercialStatSeller);
}

if (!window.customElements.get(TAG.AON_JSF_COMMERCIAL_STAT_PRODUCT)) {
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_STAT_PRODUCT, AonJsfCommercialStatProduct);
}

if (!window.customElements.get(TAG.AON_JSF_COMMERCIAL_STAT_CATEGORY)) {
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_STAT_CATEGORY, AonJsfCommercialStatCategory);
}

if (!window.customElements.get(TAG.AON_JSF_COMMERCIAL_STAT_TARGET)) {
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_STAT_TARGET, AonJsfCommercialStatTarget);
}

if (!window.customElements.get(TAG.AON_JSF_SELLER)) {
	window.customElements.define(TAG.AON_JSF_SELLER, AonJsfSeller);
}

if (!window.customElements.get(TAG.AON_JSF_TARGET)) {
	window.customElements.define(TAG.AON_JSF_TARGET, AonJsfTarget);
}

if (!window.customElements.get(TAG.AON_JSF_TARGET_DEDUPLICATION)) {
	window.customElements.define(TAG.AON_JSF_TARGET_DEDUPLICATION, AonJsfTargetDeduplication);
}

if (!window.customElements.get(TAG.AON_JSF_COMMERCIAL_SELLER_STAT)) {
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_SELLER_STAT, AonJsfCommercialSellerStat);
}

if (!window.customElements.get(TAG.AON_JSF_COMMERCIAL_PRODUCT_STAT)) {
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_PRODUCT_STAT, AonJsfCommercialProductStat);
}

if (!window.customElements.get(TAG.AON_JSF_COMMERCIAL_CATEGORY_STAT)) {
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_CATEGORY_STAT, AonJsfCommercialCategoryStat);
}

if (!window.customElements.get(TAG.AON_JSF_COMMERCIAL_TARGET_STAT)) {
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_TARGET_STAT, AonJsfCommercialTargetStat);
}

if (!window.customElements.get(TAG.AON_JSF_COMMERCIAL_GEOZONE_STAT)) {
	window.customElements.define(TAG.AON_JSF_COMMERCIAL_GEOZONE_STAT, AonJsfCommercialGeozoneStat);
}


if (!window.customElements.get(TAG.AON_JSF_OFFER_DETAIL_COMMISSION)) {
	window.customElements.define(TAG.AON_JSF_OFFER_DETAIL_COMMISSION, AonJsfOfferDetailCommission);
}

if (!window.customElements.get(TAG.AON_JSF_COMMISSION)) {
	window.customElements.define(TAG.AON_JSF_COMMISSION, AonJsfCommission);
}

if (!window.customElements.get(TAG.AON_JSF_COMMISSION_CALC)) {
	window.customElements.define(TAG.AON_JSF_COMMISSION_CALC, AonJsfCommissionCalc);
}

if (!window.customElements.get(TAG.AON_JSF_COMMISSION_TYPE)) {
	window.customElements.define(TAG.AON_JSF_COMMISSION_TYPE, AonJsfCommissionType);
}

if (!window.customElements.get(TAG.AON_JSF_MESSAGES)) {
	window.customElements.define(TAG.AON_JSF_MESSAGES, AonJsfMessages);
}

if (!window.customElements.get(TAG.AON_JSF_NEWS)) {
	window.customElements.define(TAG.AON_JSF_NEWS, AonJsfNews);
}

if (!window.customElements.get(TAG.AON_JSF_NEWSLETTER)) {
	window.customElements.define(TAG.AON_JSF_NEWSLETTER, AonJsfNewsletter);
}

if (!window.customElements.get(TAG.AON_JSF_HTML_TEMPLATE)) {
	window.customElements.define(TAG.AON_JSF_HTML_TEMPLATE, AonJsfHtmlTemplate);
}

if (!window.customElements.get(TAG.AON_JSF_MARKETING_TEMPLATE)) {
	window.customElements.define(TAG.AON_JSF_MARKETING_TEMPLATE, AonJsfMarketingTemplate);
}

if (!window.customElements.get(TAG.AON_JSF_COMPANY_IMAGES)) {
	window.customElements.define(TAG.AON_JSF_COMPANY_IMAGES, AonJsfCompanyImages);
}

if (!window.customElements.get(TAG.AON_JSF_MAIL_PROCESS)) {
	window.customElements.define(TAG.AON_JSF_MAIL_PROCESS, AonJsfMailProcess);
}

if (!window.customElements.get(TAG.AON_JSF_MARKETING_CAMPAIGN)) {
	window.customElements.define(TAG.AON_JSF_MARKETING_CAMPAIGN, AonJsfMarketingCampaign);
}

if (!window.customElements.get(TAG.AON_JSF_COMMUNICATION_CENTER)) {
	window.customElements.define(TAG.AON_JSF_COMMUNICATION_CENTER, AonJsfCommunicationCenter);
}

if (!window.customElements.get(TAG.AON_JSF_SURVEY)) {
	window.customElements.define(TAG.AON_JSF_SURVEY, AonJsfSurvey);
}

if (!window.customElements.get(TAG.AON_JSF_SURVEY_RESPONSE)) {
	window.customElements.define(TAG.AON_JSF_SURVEY_RESPONSE, AonJsfSurveyResponse);
}

if (!window.customElements.get(TAG.AON_JSF_PROJECT)) {
	window.customElements.define(TAG.AON_JSF_PROJECT, AonJsfProject);
}

if (!window.customElements.get(TAG.AON_JSF_PROJECT_TYPE)) {
	window.customElements.define(TAG.AON_JSF_PROJECT_TYPE, AonJsfProjectType);
}

if (!window.customElements.get(TAG.AON_JSF_ACTIVITY_TYPE)) {
	window.customElements.define(TAG.AON_JSF_ACTIVITY_TYPE, AonJsfActivityType);
}

if (!window.customElements.get(TAG.AON_JSF_MAIL_CONTACT)) {
	window.customElements.define(TAG.AON_JSF_MAIL_CONTACT, AonJsfMailContact);
}

if (!window.customElements.get(TAG.AON_JSF_MAIL_ACCOUNT)) {
	window.customElements.define(TAG.AON_JSF_MAIL_ACCOUNT, AonJsfMailAccount);
}

if (!window.customElements.get(TAG.AON_JSF_MAIL_SIGNATURE)) {
	window.customElements.define(TAG.AON_JSF_MAIL_SIGNATURE, AonJsfMailSignature);
}

if (!window.customElements.get(TAG.AON_JSF_GLOBAL_CONFIG)) {
	window.customElements.define(TAG.AON_JSF_GLOBAL_CONFIG, AonJsfGlobalConfig);
}

if (!window.customElements.get(TAG.AON_JSF_TASK)) {
	window.customElements.define(TAG.AON_JSF_TASK, AonJsfTask);
}

if (!window.customElements.get(TAG.AON_JSF_GANTT)) {
	window.customElements.define(TAG.AON_JSF_GANTT, AonJsfGantt);
}

if (!window.customElements.get(TAG.AON_JSF_DAILY_TRACKING)) {
	window.customElements.define(TAG.AON_JSF_DAILY_TRACKING, AonJsfDailyTracking);
}

if (!window.customElements.get(TAG.AON_JSF_DAILY_TRACKING_REPORT)) {
	window.customElements.define(TAG.AON_JSF_DAILY_TRACKING_REPORT, AonJsfDailyTrackingReport);
}

if (!window.customElements.get(TAG.AON_JSF_JOB_TYPE)) {
	window.customElements.define(TAG.AON_JSF_JOB_TYPE, AonJsfJobType);
}

if (!window.customElements.get(TAG.AON_JSF_PROCESS)) {
	window.customElements.define(TAG.AON_JSF_PROCESS, AonJsfProcess);
}

if (!window.customElements.get(TAG.AON_JSF_PROCESS_WIZARD)) {
	window.customElements.define(TAG.AON_JSF_PROCESS_WIZARD, AonJsfProcessWizard);
}

if (!window.customElements.get(TAG.AON_JSF_PROCESS_TRANSACTION_TYPE)) {
	window.customElements.define(TAG.AON_JSF_PROCESS_TRANSACTION_TYPE, AonJsfProcessTransactionType);
}

if (!window.customElements.get(TAG.AON_JSF_CAMPAIGN)) {
	window.customElements.define(TAG.AON_JSF_CAMPAIGN, AonJsfCampaign);
}

if (!window.customElements.get(TAG.AON_JSF_CAMPAIGN_TYPE)) {
	window.customElements.define(TAG.AON_JSF_CAMPAIGN_TYPE, AonJsfCampaignType);
}

if (!window.customElements.get(TAG.AON_JSF_TASK_HOLDER)) {
	window.customElements.define(TAG.AON_JSF_TASK_HOLDER, AonJsfTaskHolder);
}

if (!window.customElements.get(TAG.AON_JSF_WORKGROUP)) {
	window.customElements.define(TAG.AON_JSF_WORKGROUP, AonJsfWorkgroup);
}

if (!window.customElements.get(TAG.AON_JSF_COST_PROFILE)) {
	window.customElements.define(TAG.AON_JSF_COST_PROFILE, AonJsfCostProfile);
}

if (!window.customElements.get(TAG.AON_JSF_PRODUCT_TAG)) {
	window.customElements.define(TAG.AON_JSF_PRODUCT_TAG, AonJsfProductTag);
}

if (!window.customElements.get(TAG.AON_JSF_PACKING_TAG)) {
	window.customElements.define(TAG.AON_JSF_PACKING_TAG, AonJsfPackingTag);
}

if (!window.customElements.get(TAG.AON_JSF_BRAND)) {
	window.customElements.define(TAG.AON_JSF_BRAND, AonJsfBrand);
}

if (!window.customElements.get(TAG.AON_JSF_TARIFF)) {
	window.customElements.define(TAG.AON_JSF_TARIFF, AonJsfTariff);
}

if (!window.customElements.get(TAG.AON_JSF_CATALOGUE)) {
	window.customElements.define(TAG.AON_JSF_CATALOGUE, AonJsfCatalogue);
}

if (!window.customElements.get(TAG.AON_JSF_TAX)) {
	window.customElements.define(TAG.AON_JSF_TAX, AonJsfTax);
}

if (!window.customElements.get(TAG.AON_JSF_SERIES)) {
	window.customElements.define(TAG.AON_JSF_SERIES, AonJsfSeries);
}

if (!window.customElements.get(TAG.AON_JSF_BANK_CONCEPT)) {
	window.customElements.define(TAG.AON_JSF_BANK_CONCEPT, AonJsfBankConcept);
}

if (!window.customElements.get(TAG.AON_JSF_RELATIONSHIP)) {
	window.customElements.define(TAG.AON_JSF_RELATIONSHIP, AonJsfRelationship);
}

if (!window.customElements.get(TAG.AON_JSF_LOADER)) {
	window.customElements.define(TAG.AON_JSF_LOADER, AonJsfLoader);
}

if (!window.customElements.get(TAG.AON_JSF_CONTRACT_BATCH)) {
	window.customElements.define(TAG.AON_JSF_CONTRACT_BATCH, AonJsfContractBatch);
}

if (!window.customElements.get(TAG.AON_JSF_HOLIDAYS)) {
	window.customElements.define(TAG.AON_JSF_HOLIDAYS, AonJsfHolidays);
}

if (!window.customElements.get(TAG.AON_JSF_IRPF_DATA)) {
	window.customElements.define(TAG.AON_JSF_IRPF_DATA, AonJsfIrpfData);
}

if (!window.customElements.get(TAG.AON_JSF_AMORTIZATION)) {
	window.customElements.define(TAG.AON_JSF_AMORTIZATION, AonJsfAmortization);
}

if (!window.customElements.get(TAG.AON_JSF_PERIOD_AMORTIZATION)) {
	window.customElements.define(TAG.AON_JSF_PERIOD_AMORTIZATION, AonJsfPeriodAmortization);
}

if (!window.customElements.get(TAG.AON_JSF_END_PERIOD_ENTRIES)) {
	window.customElements.define(TAG.AON_JSF_END_PERIOD_ENTRIES, AonJsfEndPeriodEntries);
}

if (!window.customElements.get(TAG.AON_JSF_INVOICE_INTEGRITY)) {
	window.customElements.define(TAG.AON_JSF_INVOICE_INTEGRITY, AonJsfInvoiceIntegrity);
}

if (!window.customElements.get(TAG.AON_JSF_INVOICE_REPORT)) {
	window.customElements.define(TAG.AON_JSF_INVOICE_REPORT, AonJsfInvoiceReport);
}

if (!window.customElements.get(TAG.AON_JSF_INVOICE_RECORDER)) {
	window.customElements.define(TAG.AON_JSF_INVOICE_RECORDER, AonJsfInvoiceRecorder);
}

if (!window.customElements.get(TAG.AON_JSF_INVOICE_AMORTIZATION)) {
	window.customElements.define(TAG.AON_JSF_INVOICE_AMORTIZATION, AonJsfInvoiceAmortization);
}

if (!window.customElements.get(TAG.AON_JSF_FINANCE_TRACKING_ENTRY)) {
	window.customElements.define(TAG.AON_JSF_FINANCE_TRACKING_ENTRY, AonJsfFinanceTrackingEntry);
}

if (!window.customElements.get(TAG.AON_JSF_INCOME)) {
	window.customElements.define(TAG.AON_JSF_INCOME, AonJsfIncome);
}

if (!window.customElements.get(TAG.AON_JSF_DELIVERY)) {
	window.customElements.define(TAG.AON_JSF_DELIVERY, AonJsfDelivery);
}

if (!window.customElements.get(TAG.AON_JSF_WAREHOUSE_TRANSFER)) {
	window.customElements.define(TAG.AON_JSF_WAREHOUSE_TRANSFER, AonJsfWarehouseTransfer);
}

if (!window.customElements.get(TAG.AON_JSF_ORDER_SERVER)) {
	window.customElements.define(TAG.AON_JSF_ORDER_SERVER, AonJsfOrderServer);
}

if (!window.customElements.get(TAG.AON_JSF_STOCK)) {
	window.customElements.define(TAG.AON_JSF_STOCK, AonJsfStock);
}

if (!window.customElements.get(TAG.AON_JSF_INVENTORY)) {
	window.customElements.define(TAG.AON_JSF_INVENTORY, AonJsfInventory);
}

if (!window.customElements.get(TAG.AON_JSF_INVENTORY_CLOSE)) {
	window.customElements.define(TAG.AON_JSF_INVENTORY_CLOSE, AonJsfInventoryClose);
}

if (!window.customElements.get(TAG.AON_JSF_ORDER_PROPOSAL)) {
	window.customElements.define(TAG.AON_JSF_ORDER_PROPOSAL, AonJsfOrderProposal);
}

if (!window.customElements.get(TAG.AON_JSF_STOCK_REPORT_ITEM)) {
	window.customElements.define(TAG.AON_JSF_STOCK_REPORT_ITEM, AonJsfStockReportItem);
}

if (!window.customElements.get(TAG.AON_JSF_STOCK_REPORT_WAREHOUSE)) {
	window.customElements.define(TAG.AON_JSF_STOCK_REPORT_WAREHOUSE, AonJsfStockReportWarehouse);
}

if (!window.customElements.get(TAG.AON_JSF_STOCK_REPORT_ITEM_VALUED)) {
	window.customElements.define(TAG.AON_JSF_STOCK_REPORT_ITEM_VALUED, AonJsfStockReportItemValued);
}

if (!window.customElements.get(TAG.AON_JSF_STOCK_REPORT_WAREHOUSE_VALUED)) {
	window.customElements.define(TAG.AON_JSF_STOCK_REPORT_WAREHOUSE_VALUED, AonJsfStockReportWarehouseValued);
}

if (!window.customElements.get(TAG.AON_JSF_CARRIER)) {
	window.customElements.define(TAG.AON_JSF_CARRIER, AonJsfCarrier);
}

if (!window.customElements.get(TAG.AON_JSF_WAREHOUSE)) {
	window.customElements.define(TAG.AON_JSF_WAREHOUSE, AonJsfWarehouse);
}

if (!window.customElements.get(TAG.AON_JSF_ACCOUNT)) {
	window.customElements.define(TAG.AON_JSF_ACCOUNT, AonJsfAccount);
}

if (!window.customElements.get(TAG.AON_JSF_ACCOUNTING_PARAMS)) {
	window.customElements.define(TAG.AON_JSF_ACCOUNTING_PARAMS, AonJsfAccountingParams);
}

if (!window.customElements.get(TAG.AON_JSF_ACCOUNTING_BOOK)) {
	window.customElements.define(TAG.AON_JSF_ACCOUNTING_BOOK, AonJsfAccountingBook);
}

if (!window.customElements.get(TAG.AON_JSF_FISCAL_PARAMS)) {
	window.customElements.define(TAG.AON_JSF_FISCAL_PARAMS, AonJsfFiscalParams);
}

if (!window.customElements.get(TAG.AON_JSF_PAYROLL_PARAMS)) {
	window.customElements.define(TAG.AON_JSF_PAYROLL_PARAMS, AonJsfPayrollParams);
}

if (!window.customElements.get(TAG.AON_JSF_CONTRACT_PARAMS)) {
	window.customElements.define(TAG.AON_JSF_CONTRACT_PARAMS, AonJsfContractParams);
}

if (!window.customElements.get(TAG.AON_JSF_BALANCE)) {
	window.customElements.define(TAG.AON_JSF_BALANCE, AonJsfBalance);
}

if (!window.customElements.get(TAG.AON_JSF_ACC_PERIOD)) {
	window.customElements.define(TAG.AON_JSF_ACC_PERIOD, AonJsfAccPeriod);
}

if (!window.customElements.get(TAG.AON_JSF_AMORTIZATION_TYPE)) {
	window.customElements.define(TAG.AON_JSF_AMORTIZATION_TYPE, AonJsfAmortizationType);
}

if (!window.customElements.get(TAG.AON_JSF_AUT_CONCEPT)) {
	window.customElements.define(TAG.AON_JSF_AUT_CONCEPT, AonJsfAutConcept);
}

if (!window.customElements.get(TAG.AON_JSF_NEW_DOMAIN)) {
	window.customElements.define(TAG.AON_JSF_NEW_DOMAIN, AonJsfNewDomain);
}

if (!window.customElements.get(TAG.AON_JSF_REMOVE_DOMAIN)) {
	window.customElements.define(TAG.AON_JSF_REMOVE_DOMAIN, AonJsfRemoveDomain);
}

if (!window.customElements.get(TAG.AON_JSF_HELP_CONTENT)) {
	window.customElements.define(TAG.AON_JSF_HELP_CONTENT, AonJsfHelpContent);
}

if (!window.customElements.get(TAG.AON_JSF_HELP_NOTIFICATION)) {
	window.customElements.define(TAG.AON_JSF_HELP_NOTIFICATION, AonJsfHelpNotification);
}

if (!window.customElements.get(TAG.AON_JSF_ACCOUNTING_GRAPH)) {
	window.customElements.define(TAG.AON_JSF_ACCOUNTING_GRAPH, AonJsfAccountingGraph);
}

if (!window.customElements.get(TAG.AON_JSF_PAYROLL_GRAPH)) {
	window.customElements.define(TAG.AON_JSF_PAYROLL_GRAPH, AonJsfPayrollGraph);
}

if (!window.customElements.get(TAG.AON_JSF_CONTRACT_GRAPH)) {
	window.customElements.define(TAG.AON_JSF_CONTRACT_GRAPH, AonJsfContractGraph);
}

if (!window.customElements.get(TAG.AON_JSF_PROJECT_TAS)) {
	window.customElements.define(TAG.AON_JSF_PROJECT_TAS, AonJsfProjectTas);
}

if (!window.customElements.get(TAG.AON_JSF_MAKE)) {
	window.customElements.define(TAG.AON_JSF_MAKE, AonJsfMake);
}

if (!window.customElements.get(TAG.AON_JSF_MODEL)) {
	window.customElements.define(TAG.AON_JSF_MODEL, AonJsfModel);
}

if (!window.customElements.get(TAG.AON_JSF_TAS_ITEM)) {
	window.customElements.define(TAG.AON_JSF_TAS_ITEM, AonJsfTasItem);
}

if (!window.customElements.get(TAG.AON_JSF_TAS_STAT)) {
	window.customElements.define(TAG.AON_JSF_TAS_STAT, AonJsfTasStat);
}

if (!window.customElements.get(TAG.AON_JSF_CONTRACT_OPTION)) {
	window.customElements.define(TAG.AON_JSF_CONTRACT_OPTION, AonJsfContractOption);
}

if (!window.customElements.get(TAG.AON_JSF_TRAINNING_CENTER)) {
	window.customElements.define(TAG.AON_JSF_TRAINNING_CENTER, AonJsfTrainningCenter);
}

if (!window.customElements.get(TAG.AON_JSF_POS_OPENING)) {
	window.customElements.define(TAG.AON_JSF_POS_OPENING, AonJsfPosOpening);
}

if (!window.customElements.get(TAG.AON_JSF_POS_CLOSING)) {
	window.customElements.define(TAG.AON_JSF_POS_CLOSING, AonJsfPosClosing);
}

if (!window.customElements.get(TAG.AON_JSF_POS_INVOICE)) {
	window.customElements.define(TAG.AON_JSF_POS_INVOICE, AonJsfPosInvoice);
}

if (!window.customElements.get(TAG.AON_JSF_POS_FINANCE)) {
	window.customElements.define(TAG.AON_JSF_POS_FINANCE, AonJsfPosFinance);
}

if (!window.customElements.get(TAG.AON_JSF_POS)) {
	window.customElements.define(TAG.AON_JSF_POS, AonJsfPos);
}

if (!window.customElements.get(TAG.AON_JSF_POS_SHIFT)) {
	window.customElements.define(TAG.AON_JSF_POS_SHIFT, AonJsfPosShift);
}

if (!window.customElements.get(TAG.AON_JSF_ITEM_TAG_PRINT)) {
	window.customElements.define(TAG.AON_JSF_ITEM_TAG_PRINT, AonJsfItemTagPrint);
}

if (!window.customElements.get(TAG.AON_JSF_CORPORATE_IDENTITY_LABEL)) {
	window.customElements.define(TAG.AON_JSF_CORPORATE_IDENTITY_LABEL, AonJsfCorporateIdentityLabel);
}	

if (!window.customElements.get(TAG.AON_JSF_EXPIRED_PASSWORD)) {
	window.customElements.define(TAG.AON_JSF_EXPIRED_PASSWORD, AonJsfExpiredPassword);
}

if (!window.customElements.get(TAG.AON_JSF_PAY_METHOD_TYPE_DETAIL)) {
	window.customElements.define(TAG.AON_JSF_PAY_METHOD_TYPE_DETAIL, AonJsfPayMethodTypeDetail);
}

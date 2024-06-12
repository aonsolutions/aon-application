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

export class AonJsfExpense extends AonJsfApp {
	
	constructor() {
		super();
		this.setViewId('/facelet/app/expense.xhtml');
	}
	
}


if(!window.customElements.get(TAG.AON_JSF_APP)){
	window.customElements.define(TAG.AON_JSF_APP, AonJsfApp);
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

if(!window.customElements.get(TAG.AON_JSF_EXPENSE)){
	window.customElements.define(TAG.AON_JSF_EXPENSE, AonJsfExpense);
}

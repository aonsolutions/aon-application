import { AonElement as HELP } from '../components/AonElement.js';
import { EVENT, TAG, MSG, CSS } from '../environments/environments.js';

export class AonSiteHelp extends HELP {

	IFRAME;
	HOME_BUTTON;

	site ;
	

	constructor() {
		super();
	}

	connectedCallback() {
		this.clear();
		this.init();
		this.build();
	}

	init() {
		this.id = 'aonSiteHelp';
		this.IFRAME = this.id + 'Frame';
		this.HOME_BUTTON = this.id + 'HomeButton';
	}

	build() {
		
		let homeButton = this.getHomeButton();
		homeButton.innerHTML = MSG.HOME;
		homeButton.className = CSS.AON_BUTTON;
		homeButton.style.display = 'none';
		homeButton.addEventListener(EVENT.CLICK, () => this.reLoadSite().then(() => this.hideHeader()));  
		this.appendChild(homeButton);
		
		let iframe = this.getIFrame();
		iframe.name = this.IFRAME;
		iframe.style.border = 'none';
		iframe.style.width = '100%';
		iframe.style.display = 'none';
		let top = this.getBoundingClientRect().top;
		iframe.style.height = `calc(100vh - ${top + 10}px)`;
		iframe.src = `/proxy?url=${encodeURIComponent(this.site)}`;

		this.appendChild(iframe);
		
		this.isLoaded().then(() => [...this.getIDocument().getElementsByTagName('header')].forEach( header => {
			header.remove();
			this.showIFrame();
			this.shiowHomeButton();
			this.getHomeButton().innerHTML = header.textContent; 
		}));
		
	}
	
	setSite(site) {
		this.site = site;
	}

	getIFrame() {
		return this.getElement(this.IFRAME) || this.createElement(TAG.IFRAME, this.IFRAME);
	}
	
	showIFrame() {
		let iframe = this.getIFrame();	
		iframe.style.display = 'block';
	}
	
	shiowHomeButton() {
		let homeButton = this.getHomeButton();
		homeButton.style.removeProperty('display');
	}
	
	hideIFrame() {
		let iframe = this.getIFrame();	
		iframe.style.display = 'none';
	}
	
	getIDocument() {
		let iframe = this.getIFrame();
		return iframe.document || iframe.contentDocument || iframe.contentWindow?.document;
	}
	
	getHomeButton() {
		return this.getElement(this.HOME_BUTTON) || this.createElement(TAG.SPAN, this.HOME_BUTTON);
	}

	hideHeader() {
	    [...this.getIDocument().getElementsByTagName('header')].forEach(header => {
	        header.remove();
			this.showIFrame();
	        this.getHomeButton().innerHTML = header.textContent;
	    })
	}

	reLoadSite()
	{
		this.hideIFrame();
		let iframe = this.getIFrame();
		iframe.src = `/proxy?url=${encodeURIComponent(this.site)}`;
        return new Promise((resolve, reject) => {
            if (iframe.isConnected) {
                iframe.addEventListener(EVENT.LOAD, resolve);
            } else {
                this.addEventListener(EVENT.BUILD, () => {
                    iframe.addEventListener(EVENT.LOAD, resolve);
                });
            }
        });
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

}

export const HELP_SUITE_SITE = 'https://suite.aonsolutions.info/';


export class AonSiteHelpSuite extends AonSiteHelp {

	constructor() {
		super();
		this.setSite(HELP_SUITE_SITE);
	}
	
}

export const HELP_PORTAL_SITE = 'https://portal.aonsolutions.info/';

export class AonSiteHelpPortal extends AonSiteHelp {

	constructor() {
		super();
		this.setSite(HELP_PORTAL_SITE);
	}
}



if (!window.customElements.get(TAG.AON_SITE_HELP_SUITE)) {
	window.customElements.define(TAG.AON_SITE_HELP_SUITE, AonSiteHelpSuite);
}

if (!window.customElements.get(TAG.AON_SITE_HELP_PORTAL)) {
	window.customElements.define(TAG.AON_SITE_HELP_PORTAL, AonSiteHelpPortal);
}




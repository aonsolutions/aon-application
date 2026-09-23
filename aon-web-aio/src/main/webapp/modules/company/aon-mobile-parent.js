import {AonElement} from '../../components/AonElement.js';
import {closeSession, getCompanies, getUser} from  '../../services/service.js';
import { CSS, EVENT, MSG, TAG } from '../../environments/environments.js';
import { AonToolbar } from '../../components/aon-toolbar.js';
import '../../components/aon-application.js';
import { AonMobileHome } from '../home/aon-mobile-home.js';
import { AonMobileDesktop } from './aon-mobile-desktop.js';
import * as UA  from '../../services/userAgentService.js';
import { changeStatusBarColor } from '../../services/actionService.js';
import { mobileAction } from '../../services/service.js';

import * as LS from '../../services/localStorageService.js';

export class AonMobileParent extends AonElement {

  	companies;
  	selected;
	selection; // boolean

  	constructor () {
  		super();
  	}

  	connectedCallback () {
      	this.initialize();
		this.buildToolbar();
	 	this.init();
  	}

    initialize() {
      this.id = this.id || 'aonParent';
	  this.PARENT_CONTENT = this.id + 'Content';
    }

  	init(filter) {
		getCompanies().then( companies => {
			let cps = companies.filter(r => r.id == LS.getDomainId());
			if(this.selection && cps.length > 0 && !cps[0].parent) {
				this.companySelection(cps[0], companies.length == 1 );
			} else if (this.selection && LS.getCompany() ) {
				this.companySelection(LS.getCompany(), companies.length == 1 );
			} else if(companies.length === 1) {
				this.companySelection(companies[0], true);
			} else {
				this.build(companies.filter(f => this.companyFilter(f, filter)));
			}
		}, () => closeSession());
    }

  	companyFilter(f, q) {
  		if(!q) {
  			q = {
  				active: true
  			};
  		}
  		let value = true;
  		if(q && q.value) {
  			const document = f.document && f.document.toUpperCase().includes(q.value.toUpperCase());
  			const name = f.name && f.name.toUpperCase().includes(q.value.toUpperCase());
  			value = document || name;
  		}

  		if(q && q.active) {
  			value = f.active  && (f.parentId || f.type !== 'CONSULTANCY');
  		}

  		if(q && q.inactive) {
  			value = !f.active && f.type === 'CONSULTANCY';
  		}

  		if(q && q.shared) {
  			value = f.shared;
  		}

  		if(q && q.entorno) {
  			value = !f.parentId;
  		}

  		if(q && q.despacho) {
  			value = f.type === 'OFFICE';
  		}

  		if(q && q.ids) {
  			let idFilter;
  			q.ids.forEach((item, i) => {
  				 idFilter = f.id == item || idFilter;
  			});
  			value = idFilter;
  		}

  		return value;
  	}

	buildToolbar() {
		let toolbar = new AonToolbar();
		toolbar.id = this.id + 'Toolbar';
		toolbar.title = MSG.COMPANIES;
		this.appendChild(toolbar);
		toolbar.addSearchButton();
		toolbar.addEventListener(EVENT.SEARCH, (event) => {
			this.init({value: event.detail});
		});

	}

   	build(companies) {
  		this.statusBarOut();

  		let content = this.getElement(this.PARENT_CONTENT) || this.createElement(TAG.DIV);
		content.id = this.PARENT_CONTENT;
		this.clearElement(content);

  		let ul = this.createElement(TAG.UL);
	  	ul.classList.add(CSS.AON_UL);
		ul.classList.add(CSS.AON_LIST_GROUP);
		ul.style.marginLeft= '20px';
  		ul.style.marginRight= '20px';

  		for(let i = 0; i < companies.length; i++){
  			ul.appendChild(this.buildLi(companies[i], 'transparent'));
  		}
  		content.appendChild(ul);
  		this.appendChild(content);
  	}

	// Si se muestra el listado de empresas es que no hay ninguna empresa
	// seleccionada, la cabecera es blanca y hay que ajustar la status bar.
	statusBarOut() {
		let aonHeader = this.getElement('aonHeader');
		if(aonHeader && aonHeader.companyOut) {
			aonHeader.companyOut();
		} else {
			let ionicData = { action: "statusBar", statusBar: false};
			if(UA.isAndroidApp()) {
				changeStatusBarColor(ionicData, "#FFFFFF", false);
			} else mobileAction(ionicData);
		}
	}

  	buildLi(company, color) {
  		let li = this.createElement(TAG.LI);
  		li.className = CSS.AON_LI;
  		li.style.backgroundColor = company.parent ? '#E1ECFF' : color;
  		li.addEventListener(EVENT.CLICK, () => this.companySelection(company, false));

  		let span = this.createElement(TAG.SPAN);
  		span.className = 'aonLiSpan aonTextOverflow';

  		let i = this.createElement(TAG.I);
  		i.className = 'material-icons aonAvatar';

		if(company.type === 'OFFICE') i.innerHTML = 'work';
		else if(company.parent) i.innerHTML = 'apartment';
  		else if(company.shared) i.innerHTML = 'share';
  		else if(!company.active) i.innerHTML = 'domain_disabled';
  		else i.innerHTML = 'business';
  		let span2 = this.createElement(TAG.SPAN);
  		span2.innerHTML = company.name;

  		let span3 = this.createElement(TAG.SPAN);
  		span3.className = 'aonLiSpanSubtitle';
  		span3.innerHTML = company.document;

  		span.appendChild(i);
  		span.appendChild(span2);
  		span.appendChild(span3);
  		li.appendChild(span);

  		let sp = document.createElement('span');
  		let i2 = document.createElement('i');
  		i2.className = 'material-icons aonAvatar';
  		i2.innerHTML = 'keyboard_arrow_right';
  		sp.appendChild(i2);
  		li.appendChild(sp);
  		return li;
  	}

    companySelection(company, onlyOne) {
      localStorage.setItem('company', JSON.stringify(company));
      localStorage.setItem("aon_domain_id", company.id);
      localStorage.setItem("aon_domain_name", company.domain);
	  localStorage.setItem("aon_domain_document", company.document);
	  localStorage.setItem("onlyOne", onlyOne);

      let home = this.getElement('aonHome');
      home.showMenu(true);

      let aonHeader = this.getElement(home.AON_HEADER);
      aonHeader.showCompanyOption(company);
	  aonHeader.companyIn();
	  this.getElement(aonHeader.COMPANY_LIST).style.display = 'block';
      getUser().then(user => {
        localStorage.setItem('aon_domain_login', user.login);
        this.rootPanel(new AonMobileHome());
      });
    }


}
if(!window.customElements.get('aon-mobile-parent')){
	window.customElements.define('aon-mobile-parent', AonMobileParent);
}

import {AonElement} from '../../components/AonElement.js';
import {closeSession, getUserAppRole, getCompanies, getUserNotice, getUser, getTimeControl} from  '../../services/service.js';
import {rootPanel} from '../../services/gwtLoader.js';

import '../../components/aon-application.js';
import '../signin/aon-sign.js';

import { CSS, EVENT, TAG } from '../../environments/environments.js';

export class AonMobileParent extends AonElement {

  	companies;
  	selected;

  	constructor () {
  		super();
  	}

  	connectedCallback () {
      this.initialize();
			this.init();
  	}

    initialize() {
      this.id = this.id || 'aonParent';
    }

  	init(filter) {
  		getCompanies().then( companies => {
      		this.build(companies.filter(f => this.companyFilter(f, filter)));
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
  			value = f.active;
  		}

  		if(q && q.inactive) {
  			value = !f.active;
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

   	build(companies) {
  		let content = this.createElement(TAG.DIV);
  		let div = this.createElement(TAG.DIV);
  		div.style.borderBottom = '1px solid #5f6368';
  		div.style.marginTop = '15px';
  		div.style.marginLeft = '20px';
  		div.style.marginRight = '20px';
  		div.style.paddingBottom = '10px';
  		div.style.paddingLeft = '16px';
  		div.innerHTML = 'EMPRESAS';
  		content.appendChild(div);

  		let ul = this.createElement(TAG.UL);
  		ul.className = 'list-group';
  		ul.style.marginLeft= '20px';
  		ul.style.marginRight= '20px';

  		for(let i = 0; i < companies.length; i++){
  			ul.appendChild(this.buildLi(companies[i], 'transparent'));
  		}
  		content.appendChild(ul);
  		this.appendChild(content);
  	}

  	buildLi(company, color) {
  		let li = this.createElement(TAG.LI);
  		li.className = CSS.AON_LI;
  		li.style.backgroundColor = company.parent ? '#E1ECFF' : color;
  		li.addEventListener(EVENT.CLICK, () => this.companySelection(company));

  		let span = this.createElement(TAG.SPAN);
  		span.className = 'aonLiSpan aonTextOverflow';

  		let i = this.createElement(TAG.I);
  		i.className = 'material-icons aonAvatar';

  		if(company.parent) i.innerHTML = 'apartment';
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

    companySelection(company) {
      localStorage.setItem('company', JSON.stringify(company));
      localStorage.setItem("aon_domain_id", company.id);
      localStorage.setItem("aon_domain_name", company.domain);

      let home = this.getElement('aonHome');
      home.showMenu(true);

      let aonHeader = this.getElement(home.AON_HEADER);
      aonHeader.showCompanyOption(company);

      getUser().then(user => {
        localStorage.setItem('aon_domain_login', user.login);
        getUserAppRole().then(user => {
          if(!this.isMobile()){
            aonHeader.setAttribute('company', JSON.stringify(company));
            aonHeader.setAttribute('user', JSON.stringify(user));
          }
          rootPanel(this.isMobile()
            ? '<aon-mobile-desktop id="aonDesktop"></aon-mobile-desktop>'
            : '<aon-desktop id="aonDesktop"></aon-desktop>');
          let aonDesktop = document.getElementById('aonDesktop');
          aonDesktop.setAttribute('company', JSON.stringify(company));
          aonDesktop.setAttribute('user', JSON.stringify(user));
        });
      });
    }


}
if(!window.customElements.get('aon-mobile-parent')){
	window.customElements.define('aon-mobile-parent', AonMobileParent);
}

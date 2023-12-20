import {AonElement} from '../../components/AonElement.js';

import {getCompanyHeaderInfo} from  '../../services/service.js';

import { CSS, TAG } from '../../environments/environments.js'; 

export class AonStat extends AonElement {

  constructor () {
		super();
  }

  connectedCallback () {
    getCompanyHeaderInfo().then((pi) =>{
      this.build(pi);
    });
  }

  build(pi) {
    let div = this.createElement(TAG.DIV);
    div.style.display = 'flex';
    this.appendChild(div);

    let div1 = this.createElement(TAG.DIV);
    div1.className = CSS.AON_COMPANY_LOGO;
    div.appendChild(div1);

    let span = this.createElement(TAG.SPAN);
    span.className = CSS.AON_COMPANY_LOGO_SPAN;
    span.style.top = '30px';
    span.innerHTML = pi.name || 'ATENCIÓN AL CLIENTE';
    div1.appendChild(span);

    let span2 = this.createElement(TAG.SPAN);
    span2.className = CSS.AON_COMPANY_LOGO_SPAN;
    span2.style.top = '50px';
    span2.style.fontWeight = '400';
    span2.innerHTML = pi.fixed_phone || '900 831 205';
    div1.appendChild(span2);

    let span3 = this.createElement(TAG.SPAN);
    span3.className = CSS.AON_COMPANY_LOGO_SPAN;
    span3.style.top = '70px';
    span3.style.fontWeight = '400';
    span3.innerHTML =  pi.email ||  'soporte@aonSolutions.es';
    div1.appendChild(span3);

    let div2 = this.createElement(TAG.DIV);
    div2.className = CSS.AON_COMPANY_LOGO_IMG;
    div.appendChild(div2);
    let url = pi.logo || 'https://sig.aonsolutions.org/aonDocuments/company.logo' //https://mac.aonsolutions.net/aonDocuments/company.logo';
    let img = this.createElement(TAG.IMG);
    img.style.position = 'relative';
    img.style.maxHeight = '70px';
    img.style.maxWidth = '200px';
    img.src = url;
    img.addEventListener('error', () => {
      img.style.display = 'none';
      div1.style.width = '100%';
      div2.style.display = 'none';
    });
    div2.appendChild(img);
    //   div2.innerHTML = `<img style="position: relative;max-width: 201px;" src="${url}" width="200">`;
  }
}
if(!window.customElements.get('aon-stat')){
  window.customElements.define('aon-stat', AonStat);
}

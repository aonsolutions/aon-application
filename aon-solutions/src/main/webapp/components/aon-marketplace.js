import {Apps} from  '../services/app.js';
import {getDomainApps, setDomainApp} from  '../services/service.js';


class AonMarketplace extends HTMLElement {

	static get observedAttributes() {
		return ['company'];
	}

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get company() {
		return this.getAttribute('company');
	}

	set company(company) {
		this.setAttribute('company', company);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if('company' === name){
			let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
			if(company) {
				getDomainApps(company.domain).then(r => {
					this.build(r);
					componentHandler.upgradeAllRegistered();
				});
			}
		}
	}

	constructor () {
		super();
	}

	connectedCallback () {
		let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		if(company) {
			getDomainApps(company.domain).then(r => {
				this.build(r);
				componentHandler.upgradeAllRegistered();
			});
		}
  }

	build(r) {
			let ul = document.createElement('ul');
			for (let key in Apps){
				let li = document.createElement('li');
				li.style.display = 'inline-block';

				let div = document.createElement('div');
				div.style.margin = '20px';
				div.className = 'demo-card-wide mdl-card mdl-shadow--2dp';

				let span = document.createElement('span');
				span.style.margin = '20px';

				let img = document.createElement('img');
				img.style.width = '60px';
				img.src = Apps[key].logo;

				let span2 = document.createElement('span');
				span2.className = 'aonMarketplaceTitle';
				span2.innerHTML = Apps[key].title;

				span.appendChild(img);
				span.appendChild(span2);

				let div2 = document.createElement('div');
				let span3 = document.createElement('span');
				span3.style.padding = '25px';
				span3.style.color = '#7E7E7E';
				span3.innerHTML = Apps[key].description;
				div2.appendChild(span3);

				let div3 = document.createElement('div');
				div3.className = 'aonDivButton';
				let label = document.createElement('label');
				label.className = 'mdl-switch mdl-js-switch mdl-js-ripple-effect';
				label.for = 'switch-' + Apps[key].app;

				let input = document.createElement('input');
				input.type = 'checkbox';
				input.id = 'switch-' + Apps[key].app;
				input.className = 'mdl-switch__input';
				input.checked = r[Apps[key].app] ? r[Apps[key].app] : false;
			 	let a = Apps[key].app;
				input.addEventListener('change', () => {
					let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
					setDomainApp({
						domain: company.domain,
						app: a,
						active: input.checked
					});
				});
				let span4 = document.createElement('span');
				span4.className = 'mdl-switch__label'

				label.appendChild(input);
				label.appendChild(span4);
				div3.appendChild(label);

				div.appendChild(span);
				div.appendChild(div2);
				div.appendChild(div3);
				li.appendChild(div);
				ul.appendChild(li)
    }
		this.appendChild(ul);
	}
}

window.customElements.define('aon-marketplace', AonMarketplace);

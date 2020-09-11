import {Apps} from  '../services/app.js';
import {getDomainApps, setDomainApp} from  '../services/service.js';
import './aon-icon.js';

class AonDesktop extends HTMLElement {

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

	get user() {
		return this.getAttribute('user');
	}

	set user(user) {
		this.setAttribute('user', user);
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
			ul.style.margin = '0px';
			for (let key in Apps){
				if(r[Apps[key].app] ? r[Apps[key].app] : false) {
					let li = document.createElement('li');
					li.style.display = 'inline-block';

					let div = document.createElement('div');
					div.style.margin = '20px';
					div.className = 'demo-card-wide mdl-card mdl-shadow--2dp';

					let span = document.createElement('span');
					span.style.margin = '20px';

					if(Apps[key].icon) {
						span.innerHTML = `<aon-icon icon="${Apps[key].icon}" color="${Apps[key].color}" size="60px"></aon-icon>`;
					} else {
						let img = document.createElement('img');
						img.style.width = '60px';
						img.src = Apps[key].logo;
						span.appendChild(img);
					}

					let span2 = document.createElement('span');
					span2.className = 'aonMarketplaceTitle';
					span2.innerHTML = Apps[key].title;

					span.appendChild(span2);

					let div2 = document.createElement('div');
					let span3 = document.createElement('span');
					span3.style.padding = '25px';
					span3.style.color = '#7E7E7E';
					span3.innerHTML = Apps[key].description;
					div2.appendChild(span3);

					div.appendChild(span);
					div.appendChild(div2);
					li.appendChild(div);
					ul.appendChild(li);
				}
    }
		this.appendChild(ul);
	}
}

window.customElements.define('aon-desktop', AonDesktop);

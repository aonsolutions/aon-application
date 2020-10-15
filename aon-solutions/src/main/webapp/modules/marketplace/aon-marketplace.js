import {Apps, Services} from  '../../services/app.js';
import {getDomainApps, setDomainApp} from  '../../services/service.js';
import '../../components/aon-icon.js';

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
		this.appendChild(this.buildTitle('Aplicaciones'));
		this.appendChild(this.buildApps(Apps, r));

		this.appendChild(this.buildTitle('Servicios'));
		this.appendChild(this.buildApps(Services, r));
	}

	buildApps(apps, r) {
		let ul = document.createElement('ul');
		for (let key in apps){
				let li = document.createElement('li');
				li.style.display = 'inline-block';

				let div = document.createElement('div');
				div.style.margin = '20px';
				div.className = 'demo-card-wide mdl-card mdl-shadow--2dp';

				let span = document.createElement('span');
				span.style.margin = '20px';

				if(apps[key].icon) {
					span.innerHTML = `<aon-icon icon="${apps[key].icon}" color="${apps[key].color}" size="60px"></aon-icon>`;
				} else {
					let img = document.createElement('img');
					img.style.width = '60px';
					img.src = apps[key].logo;
					span.appendChild(img);
				}

				let span2 = document.createElement('span');
				span2.className = 'aonMarketplaceTitle';
				span2.innerHTML = apps[key].title;

				span.appendChild(span2);

				let div2 = document.createElement('div');
				let span3 = document.createElement('span');
				span3.style.padding = '25px';
				span3.style.color = '#7E7E7E';
				span3.innerHTML = apps[key].description;
				div2.appendChild(span3);

				let div3 = document.createElement('div');
				div3.className = 'aonDivButton';
				let label = document.createElement('label');
				label.className = 'mdl-switch mdl-js-switch mdl-js-ripple-effect';
				label.for = 'switch-' + apps[key].app;

				let input = document.createElement('input');
				input.type = 'checkbox';
				input.id = 'switch-' + apps[key].app;
				input.className = 'mdl-switch__input';
				input.checked = r[apps[key].app] ? r[apps[key].app] : false;
			 	let a = apps[key];
				input.addEventListener('change', () => {
					// let  aonMenu = document.getElementById('aonMenu');
					// if(input.checked) {
					// 	aonMenu.addApp(a);
					// } else aonMenu.removeApp(a);

					let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
					setDomainApp({
						domain: company.domain,
						app: a.app,
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
		return ul;
	}

	buildTitle(title) {
		let div = document.createElement('div');
		div.style.color = 'gray';
		div.style.paddingTop = '20px';
		div.style.paddingBottom = '20px';
		div.style.marginLeft = '60px';
		div.innerHTML = title.toUpperCase();
		return div;
	}
}

window.customElements.define('aon-marketplace', AonMarketplace);

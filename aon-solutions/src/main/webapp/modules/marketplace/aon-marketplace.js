import {AonElement} from '../../components/AonElement.js';
import {Apps, ClassicApps, Services, OtherServices} from  '../../services/app.js';
import {getDomainUserRoles, setDomainApp} from  '../../services/service.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';

import '../../components/aon-card.js';
import '../../components/aon-icon.js';
import '../../components/aon-icon-button.js';

export class AonMarketplace extends AonElement {

	APP;
	VIEW_MODE;

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.id = this.id || 'aonMarketplace';
		this.APP = this.id + 'App';
		this.VIEW_MODE = this.id + 'ViewMode';

		let company = JSON.parse(localStorage.getItem('company'));

		if(company) {
			getDomainUserRoles({}).then(r => {
				this.buildList(new DomainUserRoles(r));
			});
		}
	}

	buildList(dur) {
		this.innerHTML = `
			<aon-icon-button id="${this.VIEW_MODE}" icon="view_module" style="position:absolute; right: 20px"></aon-icon-button>
		`;

		this.getElement(this.VIEW_MODE).addEventListener('click', () => {
			this.buildModule(dur);
		});

		this.buildTitle('Aplicaciones');
		this.buildListApps(Apps, dur);

		this.buildTitle('Servicios');
		this.buildListApps(Services, dur);

		this.buildTitle('Aplicaciones Clásicas');
		this.buildListApps(ClassicApps, dur);

		this.buildTitle('Otros Servicios');
		this.buildListApps(OtherServices, dur);
	}

	buildModule(dur) {
		this.innerHTML = `
			<aon-icon-button id="${this.VIEW_MODE}" icon="view_list" style="position:absolute; right: 20px"></aon-icon-button>
		`;

		this.getElement(this.VIEW_MODE).addEventListener('click', () => {
			this.buildList(dur);
		});

		this.buildTitle('Aplicaciones');
		this.buildModuleApps(Apps, dur);

		this.buildTitle('Servicios');
		this.buildModuleApps(Services, dur);

		this.buildTitle('Aplicaciones Clásicas');
		this.buildModuleApps(ClassicApps, dur);

		this.buildTitle('Otros Servicios');
		this.buildModuleApps(OtherServices, dur);
	}

	buildListApps(apps, dur) {
		let ul = document.createElement('ul');
		ul.className = 'list-group';
		ul.style.marginLeft = '60px';
		ul.style.marginRight = '60px';
		this.appendChild(ul);
		for (let key in apps){
			let li = document.createElement('li');
			li.className = 'list-group-item aonAppLi';
			let span = document.createElement('span');
			span.style.margin = '20px';

			if(apps[key].icon) {
				span.innerHTML = `<aon-icon icon="${apps[key].icon}" color="${apps[key].color}" size="30px"></aon-icon>`;
			} else {
				let img = document.createElement('img');
				img.style.width = '30px';
				img.src = apps[key].logo;
				span.appendChild(img);
			}

			let span2 = document.createElement('span');
			span2.className = 'aonAppTitle';
			span2.innerHTML = apps[key].title;
			span.appendChild(span2);

			let buttons = document.createElement('span');
			buttons.style.position = 'absolute';
			buttons.style.right = '10px';

			let moreInfo = document.createElement('a');
			moreInfo.style.margin = '10px';
			moreInfo.style.color = 'gray';
			moreInfo.style.cursor = 'pointer';
			moreInfo.innerHTML = 'Más Info';
			moreInfo.addEventListener('click', () => {
				window.open(apps[key].moreInfo ? apps[key].moreInfo : 'https://www.aonsolutions.es/');
			});
			buttons.appendChild(moreInfo);

			let contratado = dur.hasApp(apps[key].app.toUpperCase())
			let contratar = document.createElement('button');
			contratar.className = 'aonButton';
			contratar.style.width = '110px';
			contratar.style.padding = '0.3rem 0.8rem';
			contratar.style.borderRadius = '25px';
			contratar.innerHTML = contratado ? 'Desactivar' : 'Contratar';
			contratar.style.backgroundColor = '#002469';
			contratar.style.opacity = contratado ? '0.3' : '1';
			if(dur.hasParentApp(apps[key].app.toUpperCase())){
				contratar.disabled = true;
			}
			if(OtherServices[key])  {
				buttons.style.right = '120px';
				contratar.disabled = true;
				contratar.style.backgroundColor= 'lightgrey';
			}
			contratar.addEventListener('click', () => {
				let company = JSON.parse(localStorage.getItem('company'));
				setDomainApp({
					domain: company.domain,
					app: apps[key].app,
					active: !contratado
				}).then(() => {
					getDomainUserRoles({reload:true}).then(r => {
						this.buildList(new DomainUserRoles(r));
					});
				});
			});
			if(!OtherServices[key])  {
				buttons.appendChild(contratar);
			}
			span.appendChild(buttons);
			li.appendChild(span);
			ul.appendChild(li);
		}
	}

	buildModuleApps(apps, dur) {
		let ul = document.createElement('ul');
		ul.style.marginLeft = '50px';
		this.appendChild(ul);
		for (let key in apps){
			let li = document.createElement('li');
			li.style.display = 'inline-block';
			li.style.backgroundColor = 'transparent';
			li.innerHTML = `<aon-card id="${this.APP + key}"> </aon-card>`;
			ul.appendChild(li);

			let div = document.createElement('div');
			div.style.margin = '0px';

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
			div2.style.height = '80px';
			div2.style.width = '250px';

			let span3 = document.createElement('span');
			span3.style.padding = '25px';
			span3.style.color = '#7E7E7E';
			span3.innerHTML = apps[key].description;
			div2.appendChild(span3);

			let buttons = document.createElement('div');
			buttons.style.marginLeft = '60px';
			let moreInfo = document.createElement('a');
			moreInfo.style.margin = '10px';
			moreInfo.style.color = 'gray';
			moreInfo.style.cursor = 'pointer';
			moreInfo.innerHTML = 'Más Info';
			moreInfo.addEventListener('click', () => {
				window.open(apps[key].moreInfo ? apps[key].moreInfo : 'https://www.aonsolutions.es/');
			});
			buttons.appendChild(moreInfo);

			let contratado = dur.hasApp(apps[key].app.toUpperCase())
			let contratar = document.createElement('button');
			contratar.className = 'aonButton';
			contratar.style.width = '110px';
			contratar.style.padding = '0.5rem 1rem';
			contratar.style.borderRadius = '25px';
			contratar.innerHTML = contratado ? 'Desactivar' : 'Contratar';
			contratar.style.backgroundColor = '#002469';
			contratar.style.opacity = contratado ? '0.3' : '1';
			if(dur.hasParentApp(apps[key].app.toUpperCase())){
				contratar.disabled = true;
			}
			if(OtherServices[key])  {
				contratar.disabled = true;
				contratar.style.backgroundColor= 'lightgrey';
			}
			contratar.addEventListener('click', () => {
				let company = JSON.parse(localStorage.getItem('company'));
				setDomainApp({
					domain: company.domain,
					app: apps[key].app,
					active: !contratado
				}).then(() => {
					getDomainUserRoles({reload: true}).then(r => {
						this.buildModule(new DomainUserRoles(r));
					});
				});
			});
			if(!OtherServices[key])  {
				buttons.appendChild(contratar);
			}

			div.appendChild(span);
			div.appendChild(div2);
			div.appendChild(buttons);

			let aonCard = this.getElement(this.APP + key);
			aonCard.setContent(div);
		}
	}

	buildTitle(title) {
		let div = document.createElement('div');
		div.style.color = 'gray';
		div.style.paddingTop = '20px';
		div.style.paddingBottom = '20px';
		div.style.marginLeft = '60px';
		div.innerHTML = title.toUpperCase();
		this.appendChild(div);
	}
}

window.customElements.define('aon-marketplace', AonMarketplace);

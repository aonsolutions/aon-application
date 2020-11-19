import {AonElement} from '../../components/AonElement.js';
import {Apps, ClassicApps, Services, OtherServices} from  '../../services/app.js';
import {getDomainApps, setDomainApp} from  '../../services/service.js';
import '../../components/aon-icon.js';

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
		this.id = this.id || 'aonMarketplace';
		this.APP = this.id + 'App';
		this.VIEW_MODE = this.id + 'ViewMode';
	}

	connectedCallback () {
		let company = JSON.parse(localStorage.getItem('company'));

		if(company) {
			getDomainApps(company.domain).then(r => {
				this.buildList(r);
			});
		}
	}

	buildList(r) {
		this.innerHTML = `
			<aon-icon-button id="${this.VIEW_MODE}" icon="view_module" style="position:absolute; right: 20px"></aon-icon-button>
		`;

		this.getElement(this.VIEW_MODE).addEventListener('click', () => {
			this.buildModule(r);
		});

		this.buildTitle('Aplicaciones');
		this.buildListApps(Apps, r);

		this.buildTitle('Servicios');
		this.buildListApps(Services, r);

		this.buildTitle('Aplicaciones Clásicas');
		this.buildListApps(ClassicApps, r);

		this.buildTitle('Otros Servicios');
		this.buildListApps(OtherServices, r);
	}

	buildModule(r) {
		this.innerHTML = `
			<aon-icon-button id="${this.VIEW_MODE}" icon="view_list" style="position:absolute; right: 20px"></aon-icon-button>
		`;

		this.getElement(this.VIEW_MODE).addEventListener('click', () => {
			this.buildList(r);
		});

		this.buildTitle('Aplicaciones');
		this.buildModuleApps(Apps, r);

		this.buildTitle('Servicios');
		this.buildModuleApps(Services, r);

		this.buildTitle('Aplicaciones Clásicas');
		this.buildModuleApps(ClassicApps, r);

		this.buildTitle('Otros Servicios');
		this.buildModuleApps(OtherServices, r);
	}

	buildListApps(apps, r) {
		let ul = document.createElement('ul');
		ul.className = 'list-group';
		ul.style.marginLeft = '60px';
		ul.style.marginRight = '60px';
		this.appendChild(ul);
		for (let key in apps){
			let li = document.createElement('li');
			li.className = 'list-group-item';
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

			let contratado = r.includes(apps[key].app)
			let contratar = document.createElement('button');
			contratar.className = 'aonButton';
			contratar.style.width = '110px';
			contratar.style.padding = '0.3rem 0.8rem';
			contratar.style.borderRadius = '25px';
			contratar.innerHTML = contratado ? 'Desactivar' : 'Contratar';
			contratar.style.backgroundColor = '#002469';
			contratar.style.opacity = contratado ? '0.3' : '1';
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
					getDomainApps(company.domain).then(r => {
						this.buildList(r);
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

	buildModuleApps(apps, r) {
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

			let contratado = r.includes(apps[key].app)
			let contratar = document.createElement('button');
			contratar.className = 'aonButton';
			contratar.style.width = '110px';
			contratar.style.padding = '0.5rem 1rem';
			contratar.style.borderRadius = '25px';
			contratar.innerHTML = contratado ? 'Desactivar' : 'Contratar';
			contratar.style.backgroundColor = '#002469';
			contratar.style.opacity = contratado ? '0.3' : '1';
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
					getDomainApps(company.domain).then(r => {
						this.buildModule(r);
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

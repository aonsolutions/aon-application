import './aon-user.js';
import './aon-toolbar.js';
import './aon-card.js';
import './aon-address.js';
import './aon-inputText.js';
import './aon-marketplace.js';
import './aon-user-list.js';

function createUser(user){
	let str = JSON.stringify(user);
	let content = document.getElementById("aon-configuration-content");
	console.log(str);
	content.innerHTML = '';
	let aonUser = document.createElement("aon-user");
	content.appendChild(aonUser);
}

function createTable(header, values) {
	let table = document.createElement("table");
	table.className = "mdl-data-table mdl-js-data-table mdl-data-table--selectable mdl-shadow--2dp aonTable";
	let tbody = document.createElement("tbody");
	let thead = document.createElement("thead");
	let trHead = document.createElement("tr");

	for(let i = 0; i < header.length ; i++){
	  	let th = document.createElement("th");
	  	th.className = "aonTableTh";
	  	th.appendChild(document.createTextNode(header[i].title));
	  	trHead.appendChild(th);
	}
	thead.appendChild(trHead);

	for(let j = 0; j < values.length ; j++){
		let tr = document.createElement("tr");
		  for(let k = 0; k < header.length; k++){
			  let td = document.createElement("td");
			  td.className = "aonTableTh";
		  	  td.appendChild(document.createTextNode(values[j][header[k].attribute]));
		  	  if(header[k].func){
		  		tr.addEventListener("click", function(){
		  			header[k].func(values[j]);
		  		});
		  	  }
		  	  tr.appendChild(td);
		  }
		  tbody.appendChild(tr);
	}

	table.appendChild(thead);
	table.appendChild(tbody);
	return table;
}

function toogleNav() {
	if(document.getElementById("aon-configuration-sidenav").style.width === "250px"){
		document.getElementById("aon-configuration-sidenav").style.width = "0px";
		document.getElementById("aon-configuration-content").style.marginLeft = "0px";
	} else {
		document.getElementById("aon-configuration-sidenav").style.width = "250px";
		document.getElementById("aon-configuration-content").style['margin-left'] = "250px";
	}
}

function closeNav() {
	document.getElementById("aon-configuration-sidenav").style.width = "0px";
	document.getElementById("aon-configuration-content").style.marginLeft = "250px";
}

class AonConfiguration extends HTMLElement {

	static get observedAttributes() {
		return ['company', 'user'];
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
		if('company' === name || 'user' === name){
			this.actualize();
		}
	}


	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `

			<!-- AON CONFIGURATION TOOLBAR -->
			<!-- <aon-configuration-toolbar> </aon-configuration-toolbar> -->
			<aon-toolbar title="CONFIGURACIÓN"></aon-toolbar>
			<!-- AON CONFIGURATION MENU (SIDENAV) -->
			<div id="aon-configuration-sidenav" class="sidenav">
				<ul class="aonClip">
					<li id="aon-configuration-personal" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">person</i>
						<span class="aonMenuItemSpan"> Datos Usuario </span>
					</li>

					<li id="aon-configuration-general" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">business</i>
						<span class="aonMenuItemSpan"> Información General	</span>
					</li>

					<li id="aon-configuration-user" class="aonAppMenuSidenavList aonOpacity">
						<i class="material-icons aonVerticalMiddle">people</i>
						<span class="aonMenuItemSpan"> Gestión de Usuarios </span>
					</li>

					<li id="aon-configuration-company" class="aonAppMenuSidenavList aonOpacity">
						<i class="material-icons aonVerticalMiddle">business</i>
						<span class="aonMenuItemSpan"> Gestión de Empresas </span>
					</li>

					<li id="aon-configuration-store" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">store_mall_directory</i>
						<span class="aonMenuItemSpan"> Contratación </span>
					</li>
				</ul>
			</div>

			<!-- AON CONFIGURATION CONTENT -->
			<div id="aon-configuration-content" class="aonContent">

			</div>
			`;
			this.build();
  }

	build() {
		let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;
		let listIds = ['aon-configuration-personal', 'aon-configuration-general', 'aon-configuration-user', 'aon-configuration-company', 'aon-configuration-store']
		listIds.forEach((id, i) => {
				let el = document.getElementById(id);
				if((!company || !user || (user && !user.admin)) && i != 0) {
					el.style.display = 'none';
				} else if(this.getAttribute('company') && i === 3) {
					let company = JSON.parse(this.getAttribute('company'));
					if(!company.parent) {
						el.style.display = 'none';
					} else el.style.display = 'list-item';
				} else el.style.display = 'list-item';

				el.addEventListener('mouseover', () => {
					el.style.backgroundColor = '#f1f1f1';
				});
				el.addEventListener('mouseleave', () => {
					el.style.backgroundColor = 'white';
				});
				el.addEventListener('click', () => {
					el.style.backgroundColor = '#ddd';
					this.buildContent(id);
				});
		});
	}

	actualize() {
		let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;
		let listIds = ['aon-configuration-personal', 'aon-configuration-general', 'aon-configuration-user', 'aon-configuration-company', 'aon-configuration-store']
		listIds.forEach((id, i) => {
				let el = document.getElementById(id);
				if((!company || !user || (user && !user.admin)) && i != 0) {
					el.style.display = 'none';
				} else if(this.getAttribute('company') && i === 3) {
					let company = JSON.parse(this.getAttribute('company'));
					if(!company.parent) {
						el.style.display = 'none';
					} else el.style.display = 'list-item';
				} else el.style.display = 'list-item';
		});
	}

	buildContent(id) {
		if('aon-configuration-general' === id ) {
			this.buildGeneral();
		} else if('aon-configuration-user' === id) {
			this.buildUser();
		} else if('aon-configuration-store' === id) {
			this.buildStore();
		}
	}

	buildGeneral() {
		let content = document.getElementById('aon-configuration-content');
		content.style.display = "flex";
		content.innerHTML = `
			<aon-card id="aon-configuration-general-card" title="Información General"></aon-card>
			<aon-card id="aon-configuration-general2-card" title="Información Adicional"></aon-card>
		`;
		let card = document.getElementById('aon-configuration-general-card');
		let cardDiv = document.getElementById('aon-configuration-general-card-div');
		cardDiv.style.width = '500px';
		card.addContent(`
			<form action="#" class="aon-margin-0">
				<aon-input-text class="aonWidth25" id="aon-configuration-general-nif" description="NIF" value=""></aon-input-text>
				<aon-input-text class="aonWidth75" id="aon-configuration-general-name" description="Razón Social" value=""></aon-input-text>
			</form>
			<form action="#" class="aon-margin-0">
				<aon-address class="aon-width-100" id="address" description="Dirección"></aon-address>
			</form>
		`);

		let card2 = document.getElementById('aon-configuration-general2-card');
		let cardDiv2 = document.getElementById('aon-configuration-general2-card-div');
		cardDiv2.style.width = '500px';
		card2.addContent(`
			<form action="#" class="aon-margin-0">
				<aon-input-text class="aonWidth50" id="aon-configuration-general2-phone" description="Teléfono" value=""></aon-input-text>
				<aon-input-text class="aonWidth50" id="aon-configuration-general2-fax" description="Fax" value=""></aon-input-text>
			</form>

			<form action="#" class="aon-margin-0">
				<aon-input-text class="aon-width-100" id="aon-configuration-general2-email" description="Email" value=""></aon-input-text>
			</form>

			<form action="#" class="aon-margin-0">
				<aon-input-text class="aon-width-100" id="aon-configuration-general2-web" description="Web" value=""></aon-input-text>
			</form>

			LOGO
		`);

		componentHandler.upgradeAllRegistered();
	}

	buildUser() {
		let content = document.getElementById('aon-configuration-content');
		content.style.display = "block";
		content.innerHTML = '<aon-user-list> </aon-user-list>' ;
	}

	buildStore() {
		let content = document.getElementById('aon-configuration-content');
		content.style.display = "block";
		content.innerHTML = '<aon-marketplace id="aonMarketplace" > </aon-marketplace>';
		if(this.getAttribute('company')){
			let aonMarketplace = document.getElementById('aonMarketplace');
			aonMarketplace.setAttribute('company', this.getAttribute('company'));
		}
	}

}

window.customElements.define('aon-configuration', AonConfiguration);
window.toogleNav= toogleNav;
window.closeNav= closeNav;

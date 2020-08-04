import './aon-user.js';
import './aon-toolbar.js';
import './aon-card.js';
import './aon-address.js';
import './aon-inputText.js';
import './aon-marketplace.js';
import './aon-user-list.js';
// import './aon-dialog.js';

function createUser(user){
	let str = JSON.stringify(user);
	let content = document.getElementById("aonConfigurationContent");
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

function toogleNav(id, content) {
	if(document.getElementById(id).style.width === "250px"){
		document.getElementById(id).style.width = "0px";
		document.getElementById(content).style.marginLeft = "0px";
	} else {
		document.getElementById(id).style.width = "250px";
		document.getElementById(content).style['margin-left'] = "250px";
	}
}

function closeNav() {
	document.getElementById("aonConfigurationSidenav").style.width = "0px";
	document.getElementById("aonConfigurationContent").style.marginLeft = "250px";
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

	get option() {
		return this.getAttribute('option');
	}

	set option(option) {
		this.setAttribute('option', option);
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
			<aon-toolbar id="aonConfiguration" title="CONFIGURACIÓN"></aon-toolbar>

			<!-- AON CONFIGURATION MENU (SIDENAV) -->
			<div id="aonConfigurationSidenav" class="sidenav">
				<ul class="aonClip">
					<li id="aonConfigurationPersonal" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">person</i>
						<span class="aonMenuItemSpan"> Datos Usuario </span>
					</li>

					<li id="aonConfigurationGeneral" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">business</i>
						<span class="aonMenuItemSpan"> Información General	</span>
					</li>

					<li id="aonConfigurationUser" class="aonAppMenuSidenavList aonOpacity">
						<i class="material-icons aonVerticalMiddle">people</i>
						<span class="aonMenuItemSpan"> Gestión de Usuarios </span>
					</li>

					<li id="aonConfigurationCompany" class="aonAppMenuSidenavList aonOpacity">
						<i class="material-icons aonVerticalMiddle">business</i>
						<span class="aonMenuItemSpan"> Gestión de Empresas </span>
					</li>

					<li id="aonConfigurationStore" class="aonAppMenuSidenavList aonOpacity" >
						<i class="material-icons aonVerticalMiddle">store_mall_directory</i>
						<span class="aonMenuItemSpan"> Contratación </span>
					</li>
				</ul>
			</div>

			<!-- AON CONFIGURATION CONTENT -->
			<div id="aonConfigurationContent" class="aonContent">

			</div>

			<!-- <aon-dialog id="aonConfigurationDialog" title="CONFIGURACIÓN"></aon-dialog> -->
			`;
			this.build();
  }

	build() {
		document.getElementById("aonConfigurationSidenav").style.width = "250px";
		document.getElementById("aonConfigurationContent").style['margin-left'] = "250px";
		let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;
		let listIds = ['aonConfigurationPersonal', 'aonConfigurationGeneral', 'aonConfigurationUser', 'aonConfigurationCompany', 'aonConfigurationStore']
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

		let addButton = document.getElementById('aonConfigurationToolbarAddButton');
		addButton.addEventListener('click', () => {
			let id = this.getAttribute('option');
			if('aonConfigurationPersonal' === id){

			} else if('aonConfigurationGeneral' === id ) {

			} else if('aonConfigurationUser' === id) {
				this.buildCreateUser();
			} else if('aonConfigurationCompany' === id ) {

			} else if('aonConfigurationStore' === id) {

			}
		});

	}

	actualize() {
		let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;
		let user = this.getAttribute('user') ? JSON.parse(this.getAttribute('user')) : undefined;
		let listIds = ['aonConfigurationPersonal', 'aonConfigurationGeneral', 'aonConfigurationUser', 'aonConfigurationCompany', 'aonConfigurationStore']
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
		this.setAttribute('option', id);
		if('aonConfigurationPersonal' === id){

		} else if('aonConfigurationGeneral' === id ) {
			this.buildGeneral();
		} else if('aonConfigurationUser' === id) {
			this.buildUser();
		} else if('aonConfigurationCompany' === id ) {

		} else if('aonConfigurationStore' === id) {
			this.buildStore();
		}
	}

	buildGeneral() {
		let company = this.getAttribute('company') ? JSON.parse(this.getAttribute('company')) : undefined;

		let content = document.getElementById('aonConfigurationContent');
		content.style.display = "flex";
		content.innerHTML = `
			<aon-card id="aonConfigurationGeneralCard" title="Información General"></aon-card>
			<aon-card id="aonConfigurationGeneral2Card" title="Información Adicional"></aon-card>
		`;
		let card = document.getElementById('aonConfigurationGeneralCard');
		let cardDiv = document.getElementById('aonConfigurationGeneralCard-div');
		cardDiv.style.width = '500px';
		card.addContent(`
			<form action="#" class="aon-margin-0">
				<aon-input-text class="aonWidth25" id="aonConfigurationGeneralNif" description="NIF" value="${company.document}"></aon-input-text>
				<aon-input-text class="aonWidth75" id="aonConfigurationGeneralName" description="Razón Social" value="${company.name}"></aon-input-text>
			</form>
			<form action="#" class="aon-margin-0">
				<aon-address class="aon-width-100" id="aonConfigurationGeneralAddress" description="Dirección"></aon-address>
			</form>
		`);

		let card2 = document.getElementById('aonConfigurationGeneral2Card');
		let cardDiv2 = document.getElementById('aonConfigurationGeneral2Card-div');
		cardDiv2.style.width = '500px';
		card2.addContent(`
			<form action="#" class="aon-margin-0">
				<aon-input-text class="aonWidth50" id="aonConfigurationGeneral2Phone" description="Teléfono" value=""></aon-input-text>
				<aon-input-text class="aonWidth50" id="aonConfigurationGeneral2Fax" description="Fax" value=""></aon-input-text>
			</form>

			<form action="#" class="aon-margin-0">
				<aon-input-text class="aon-width-100" id="aonConfigurationGeneral2Email" description="Email" value=""></aon-input-text>
			</form>

			<form action="#" class="aon-margin-0">
				<aon-input-text class="aon-width-100" id="aonConfigurationGeneral2Web" description="Web" value=""></aon-input-text>
			</form>

			<!-- LOGO -->
		`);

		componentHandler.upgradeAllRegistered();
	}

	buildUser() {
		let content = document.getElementById('aonConfigurationContent');
		content.style.display = "block";
		content.innerHTML = '<aon-user-list> </aon-user-list>' ;
	}

	buildCreateUser() {
		let content = document.getElementById('aonConfigurationContent');
		content.innerHTML = '<aon-user id="aonUserCreate" ><aon-user>';
		let aonUser = document.getElementById('aonUserCreate');
		aonUser.style.display = "flex";
		aonUser.style.width = "100%";
		aonUser.setAttribute('user', JSON.stringify({}));
		getDomainApps().then(apps => {
			aonUser.setAttribute('apps', JSON.stringify(apps));
		});
	}

	buildStore() {
		let content = document.getElementById('aonConfigurationContent');
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

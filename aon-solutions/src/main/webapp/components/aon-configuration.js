import './aon-user.js';
import './aon-toolbar.js';
import './aon-card.js';
import './aon-address.js';
import './aon-inputText.js';
import './aon-marketplace.js';
import './aon-user-list.js';

// function getUsers(){
// 	request('GET', '/ms/user', localStorage.getItem('aon_session_id'), undefined, undefined, function (users) {
// 		let header = [
// 			{title: 'Nombre', attribute: 'name', func: (user) => createUser(user)},
// 			{title: 'Usuario', attribute: 'login', func: (user) => createUser(user)}
// 		];
// 		let table = createTable(header, JSON.parse(users));
// 		let content = document.getElementById("aon-configuration-content");
// 		content.appendChild(table);
// 	});
// }

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
	table.className = "mdl-data-table mdl-js-data-table mdl-data-table--selectable mdl-shadow--2dp aon-table";
	let tbody = document.createElement("tbody");
	let thead = document.createElement("thead");
	let trHead = document.createElement("tr");

	for(let i = 0; i < header.length ; i++){
	  	let th = document.createElement("th");
	  	th.className = "aon-table-th";
	  	th.appendChild(document.createTextNode(header[i].title));
	  	trHead.appendChild(th);
	}
	thead.appendChild(trHead);

	for(let j = 0; j < values.length ; j++){
		let tr = document.createElement("tr");
		  for(let k = 0; k < header.length; k++){
			  let td = document.createElement("td");
			  td.className = "aon-table-th";
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
				<ul class="aon-clip">
					<li id="aon-configuration-general" class="aon-app-menu-sidenav-list aon-opacity" >
						<i class="material-icons aon-vertical-middle">business</i>
						<span class="aon-menu-item-span"> Información General	</span>
					</li>

					<li id="aon-configuration-user" class="aon-app-menu-sidenav-list aon-opacity">
						<i class="material-icons aon-vertical-middle">person</i>
						<span class="aon-menu-item-span"> Usuarios </span>
					</li>

					<li id="aon-configuration-store" class="aon-app-menu-sidenav-list aon-opacity" >
						<i class="material-icons aon-vertical-middle">store_mall_directory</i>
						<span class="aon-menu-item-span"> Contratación </span>
					</li>
				</ul>
			</div>

			<!-- AON CONFIGURATION CONTENT -->
			<div id="aon-configuration-content" class="aon-content">

			</div>
			`;
			let listIds = ['aon-configuration-general', 'aon-configuration-user', 'aon-configuration-store']
			listIds.forEach((id, i) => {
					let el = document.getElementById(id);
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
				<aon-input-text class="aon-width-25" id="aon-configuration-general-nif" description="NIF" value=""></aon-input-text>
				<aon-input-text class="aon-width-75" id="aon-configuration-general-name" description="Razón Social" value=""></aon-input-text>
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
				<aon-input-text class="aon-width-50" id="aon-configuration-general2-phone" description="Teléfono" value=""></aon-input-text>
				<aon-input-text class="aon-width-50" id="aon-configuration-general2-fax" description="Fax" value=""></aon-input-text>
			</form>

			<form action="#" class="aon-margin-0">
				<aon-input-text class="aon-width-100" id="aon-configuration-general2-email" description="Email" value=""></aon-input-text>
			</form>

			<form action="#" class="aon-margin-0">
				<aon-input-text class="aon-width-100" id="aon-configuration-general2-web" description="Web" value=""></aon-input-text>
			</form>

			LOGO
		`);
	}

	buildUser() {
		let content = document.getElementById('aon-configuration-content');
		content.style.display = "block";
		content.innerHTML = '<aon-user-list> </aon-user-list>' ;
	}

	buildStore() {
		let content = document.getElementById('aon-configuration-content');
		content.style.display = "block";
		content.innerHTML = '<aon-marketplace> </aon-marketplace>' ;
	}

}

window.customElements.define('aon-configuration', AonConfiguration);
window.toogleNav= toogleNav;
window.closeNav= closeNav;

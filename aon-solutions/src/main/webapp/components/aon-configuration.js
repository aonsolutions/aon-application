import './aon-user.js';
import './aon-toolbar.js';


function getUsers(){
	request('GET', '/ms/user', localStorage.getItem('session_id'), undefined, function (users) {
		let header = [
			{title: 'Nombre', attribute: 'name', func: (user) => createUser(user)},
			{title: 'Usuario', attribute: 'login', func: (user) => createUser(user)}
		];
		let table = createTable(header, JSON.parse(users));
		let content = document.getElementById("aon-configuration-content");
		content.appendChild(table);
	});
}

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
			<aon-configuration-toolbar> </aon-configuration-toolbar>

			<!-- AON CONFIGURATION MENU (SIDENAV) -->
			<div id="aon-configuration-sidenav" class="sidenav">
				<ul class="mdl-menu mdl-menu--bottom-left mdl-js-menu mdl-js-ripple-effect aon-clip">
					<li class="mdl-menu__item aon-opacity">
						<i class="material-icons mdl-list__item-icon"  style="vertical-align: middle;">person</i>
						<span class="aon-menu-item-span"> Usuarios </span>
					</li>
					<li class="mdl-menu__item aon-opacity">
						<i class="material-icons mdl-list__item-icon" style="vertical-align: middle;">business</i>
						<span class="aon-menu-item-span"> Empresas	</span>
					</li>
				</ul>
			</div>

			<!-- AON CONFIGURATION CONTENT -->
			<div id="aon-configuration-content" class"aon-content">

			</div>
			`;
  }
}

window.customElements.define('aon-configuration', AonConfiguration);
window.getUsers = getUsers;
window.toogleNav= toogleNav;
window.closeNav= closeNav;

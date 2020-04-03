import './request.js';
import '../components/aon-parent.js'
import '../components/aon-desktop.js'

let companies;
let company;


window.load = load;
window.closeSession = closeSession;
window.login = login;
window.getCompanies = getCompanies;
window.getCompany = getCompany;
window.companySelection = companySelection;

function load() {
	if(localStorage.getItem('session_id')){
		getCompanies().then(companies => {
			document.getElementById("aonLogin").style.display = 'none';
			document.getElementById("aonHome").style.display = 'block';
			if(companies.length === 1){
				localStorage.setItem('domain_id', companies[0].id);
				localStorage.setItem('domain_name', companies[0].domain);
				rootPanel('<aon-desktop></aon-desktop>');
			} else {
				localStorage.removeItem('domain_id');
				localStorage.removeItem('domain_name');
				rootPanel('<aon-parent></aon-parent>');
			}
		}).catch(error => {
			alert(error);
		});
	} else {
		document.getElementById("aonLogin").style.display = 'block';
		document.getElementById("aonHome").style.display = 'none';
	};
}

function closeSession() {
	localStorage.removeItem('session_id');
	localStorage.removeItem('domain_id');
	localStorage.removeItem('domain_name');
	document.getElementById("aonLogin").style.display = 'block';
	document.getElementById("aonHome").style.display = 'none';
}

function login() {
	const username = document.getElementById("user").value;
	const password = document.getElementById("password").value;
	const data = {
			username: username,
			password: password
	}

	request('POST', '/login', undefined, data, function (token, error) {

		localStorage.setItem('session_id', JSON.parse(token).session_id);
		getCompanies().then(companies => {
			document.getElementById("aonLogin").style.display = 'none';
			document.getElementById("aonHome").style.display = 'block';
			if(JSON.parse(companies).length === 1) {
				localStorage.setItem('domain_id', JSON.parse(companies)[0].id);
				localStorage.setItem('domain_name', JSON.parse(companies)[0].domain);
				rootPanel('<aon-desktop></aon-desktop>');
			} else {
				rootPanel('<aon-parent></aon-parent>');
			}
		}).catch(error => {
			alert(error);
		});
	});
}

function companySelection(domainName, domainId) {
	localStorage.setItem("domain_id", domainId);
	localStorage.setItem("domain_name", domainName);
	rootPanel('<aon-desktop></aon-desktop>');
}

function getCompanies() {
  return new Promise(function(resolve, reject){
    if(companies) {
		resolve(companies);
    } else {
      request('GET', '/ms/company', localStorage.getItem('session_id'), undefined, function (result, error) {
        if(error) {
          reject(error);
        } else {
          companies = JSON.parse(result);
          resolve(companies);
        }
   	  });
   	 }
  });
}

function getCompany() {
  return new Promise(function(resolve, reject){
    if(!company && localStorage.getItem('domain_id')) {
      getCompanies().then(companies => {
          for(let i = 0; i < companies.length; i++){
           	if(companies[i].id == localStorage.getItem('domain_id')){
            	setCompany(companies[i]);
            }
          }
      });
    }
    resolve(company);
  });
}

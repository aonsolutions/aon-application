function load() {
	if(localStorage.getItem('session_id')){
		request('GET', '/ms/company', localStorage.getItem('session_id'), undefined, function (companies) {
			document.getElementById("aonLogin").style.display = 'none';
			document.getElementById("aonHome").style.display = 'block';
			if(JSON.parse(companies).length === 1){
				localStorage.setItem('domain_id', JSON.parse(companies)[0].id);
				rootPanel('<aon-desktop></aon-desktop>');
			} else {
				localStorage.removeItem('domain_id');
				rootPanel('<aon-parent></aon-parent>');
			}
		});

	} else {
		document.getElementById("aonLogin").style.display = 'block';
		document.getElementById("aonHome").style.display = 'none';
	};
}

function closeSession() {
	localStorage.removeItem('session_id');
	localStorage.removeItem('domain_id');
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
		request('GET', '/ms/company', localStorage.getItem('session_id'), undefined, function (companies) {
			document.getElementById("aonLogin").style.display = 'none';
			document.getElementById("aonHome").style.display = 'block';
			if(JSON.parse(companies).length === 1){
				localStorage.setItem('domain_id', JSON.parse(companies)[0].id);
				rootPanel('<aon-desktop></aon-desktop>');
			} else {
				rootPanel('<aon-parent></aon-parent>');
			}
		});
	});
}

class AonLogin extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<!-- Wide card with share menu button -->
			<style>

				.form-center {
					display: flex;
					justify-content: center;
					height:100%;
					background-color: #377DFF;
				}

				.sign-in {
					right: 0px;
					position: absolute;
				}


				.aon-card {
					padding-bottom:50px;
					padding-top: 20px;
					padding-left: 20px;
					padding-right: 20px;
					height:350px;
					margin-top: 15% !important;

				    font-size: 16px;
				    background: #fff;
					border-radius: 2px;
					box-sizing: border-box;
				}

				.aon-input {
					position: relative;
					font-size: 16px;
					display: inline-block;
					box-sizing: border-box;
					width: 300px;
					max-width: 100%;
					margin: 0;
					padding: 20px 0;
				}

			</style>
			<div class="form-center">

			<div class="aon-card">
				<div style="margin-bottom:15px;">
					<img style="width:300px;" src="tedi-center/assets/logo.png"/>
				</div>

				<form action="#">
					<div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label" >
						<input class="mdl-textfield__input" type="text" id="user">
						<label class="mdl-textfield__label" for="sample1">Usuario</label>
					</div>
				</form>

				<form action="#">
					<div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
						<input class="mdl-textfield__input" type="password" id="password">
						<label class="mdl-textfield__label" for="sample1">Contraseña</label>
					</div>
				</form>

				<div style="position:relative;padding-bottom:5px; margin-bottom: 5px;">
					<button class="mdl-button mdl-js-button" type="submit" >¿Has olvidado tu contraseña?</button>
				</div>
				<div style="position:relative;">
					<button class="mdl-button mdl-js-button mdl-button--raised sign-in" type="submit" onclick="login()">Iniciar Sesión</button>
				</div>
			</div>
			</div>
			`;
  }
}

window.customElements.define('aon-login', AonLogin);

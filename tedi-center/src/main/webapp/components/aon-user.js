
class AonUser extends HTMLElement {

	constructor () {
		super();
	}
	
	connectedCallback () {
		this.innerHTML = `
			
	    <style>
		  		.aon-card {
					width: 80%;
				}
				
		  		.form-center {
					display: flex;
					justify-content: center;
				}
				.demo-card-wide.mdl-card {
					margin-top: 50px;
					padding-bottom:50px;
					padding-top: 20px;
					padding-left: 20px;
					padding-right: 20px;
				}
				
				.demo-card-wide > .mdl-card__title {

				}
				.demo-card-wide > .mdl-card__menu {
				
				}
	    </style>
		<div class="form-center">

			<div class="demo-card-wide mdl-card mdl-shadow--2dp aon-card">
				<div class="mdl-card__title">
					<h2 class="mdl-card__title-text"> USUARIO </h2>
				</div>
				
				<form action="#">
					<div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label" >
						<input class="mdl-textfield__input" type="text" id="aon-user-name">
						<label class="mdl-textfield__label" for="samp1">Nombre</label>
					</div>
				</form>
			</div>
		</div>
			`;
  }
}

window.customElements.define('aon-user', AonUser);

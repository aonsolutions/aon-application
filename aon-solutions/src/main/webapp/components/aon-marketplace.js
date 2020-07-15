class AonMarketplace extends HTMLElement {
	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<!-- Wide card with share menu button -->
			<style>
				.demo-card-wide.mdl-card {
					margin: 20px;
				}
			</style>

			<ul>
			<li style="display: inline-block;">

				<div class="demo-card-wide mdl-card mdl-shadow--2dp">
					<span style="margin:20px;">
						<img src="../aon-solutions/assets/apps/invoice.png"  width="60px"/>
						<span class="aon-marketplace-title"> Facturas </span>
					</span>
					<div>
						<span style="padding: 25px;color: #7E7E7E;">
							Gestor de Facturas.
						</span>
					</div>
					<div class="aon-div-button">
						<label class="mdl-switch mdl-js-switch mdl-js-ripple-effect" for="switch-1">
							<input type="checkbox" id="switch-1" class="mdl-switch__input" checked>
							<span class="mdl-switch__label"></span>
						</label>
					</div>
				</div>
			</li>

			<li style="display: inline-block;">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp">
					<span style="margin:20px;width:">
						<img src="../aon-solutions/assets/apps/documental.png" width="60px"/>
						<span class="aon-marketplace-title"> DOCUMENTAL </span>
					</span>
					<div>
						<span style="padding: 25px;color: #7E7E7E;">
							Gestor de Documentos.
						</span>
					</div>
					<div class="aon-div-button">
						<label class="mdl-switch mdl-js-switch mdl-js-ripple-effect" for="switch-2">
  						<input type="checkbox" id="switch-2" class="mdl-switch__input" checked>
  						<span class="mdl-switch__label"></span>
						</label>
					</div>
				</div>
			</li>

			<li style="display: inline-block;">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp">
					<span style="margin:20px;width:">
						<img src="../aon-solutions/assets/apps/helpdesk.png" width="60px"/>
						<span class="aon-marketplace-title"> HELP DESK </span>
					</span>
					<div>
						<span style="padding: 25px;color: #7E7E7E;">
							Gestor de Ayuda (Help Desk).
						</span>
					</div>
					<div class="aon-div-button">
						<label class="mdl-switch mdl-js-switch mdl-js-ripple-effect" for="switch-3">
  						<input type="checkbox" id="switch-3" class="mdl-switch__input" checked>
  						<span class="mdl-switch__label"></span>
						</label>
					</div>
				</div>
			</li>

			<li style="display: inline-block;">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp">
					<span style="margin:20px;width:">
						<img src="../aon-solutions/assets/apps/conta.png" width="60px"/>
						<span class="aon-marketplace-title"> Contabilidad </span>
					</span>
					<div>
						<span style="padding: 25px;color: #7E7E7E;">
							Gestor de Contabilidad.
						</span>
					</div>
					<div class="aon-div-button">
						<label class="mdl-switch mdl-js-switch mdl-js-ripple-effect" for="switch-4">
  						<input type="checkbox" id="switch-4" class="mdl-switch__input" checked>
  						<span class="mdl-switch__label"></span>
						</label>
					</div>
				</div>
			</li>

			<li style="display: inline-block;">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp">
					<span style="margin:20px;width:">
						<img src="../aon-solutions/assets/apps/fiscal.png" width="60px"/>
						<span class="aon-marketplace-title"> Fiscal </span>
					</span>
					<div>
						<span style="padding: 25px;color: #7E7E7E;">
							Gestor Fiscal.
						</span>
					</div>
					<div class="aon-div-button">
						<label class="mdl-switch mdl-js-switch mdl-js-ripple-effect" for="switch-5">
  						<input type="checkbox" id="switch-5" class="mdl-switch__input" checked>
  						<span class="mdl-switch__label"></span>
						</label>
					</div>
				</div>
			</li>

			<li style="display: inline-block;">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp">
					<span style="margin:20px;width:">
						<img src="../aon-solutions/assets/apps/laboral.png" width="60px"/>
						<span class="aon-marketplace-title"> Laboral </span>
					</span>
					<div>
						<span style="padding: 25px;color: #7E7E7E;">
							Gestor Laboral.
						</span>
					</div>
					<div class="aon-div-button">
						<label class="mdl-switch mdl-js-switch mdl-js-ripple-effect" for="switch-6">
  						<input type="checkbox" id="switch-6" class="mdl-switch__input" checked>
  						<span class="mdl-switch__label"></span>
						</label>
					</div>
				</div>
			</li>

			<li style="display: inline-block;">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp">
					<span style="margin:20px;width:">
						<img src="../aon-solutions/assets/apps/ocr.png" width="60px"/>
						<span class="aon-marketplace-title"> OCR </span>
					</span>
					<div>
						<span style="padding: 25px;color: #7E7E7E;">
							Gestor OCR.
						</span>
					</div>
					<div class="aon-div-button">
						<label class="mdl-switch mdl-js-switch mdl-js-ripple-effect" for="switch-7">
  						<input type="checkbox" id="switch-7" class="mdl-switch__input" checked>
  						<span class="mdl-switch__label"></span>
						</label>
					</div>
				</div>
			</li>

			<li style="display: inline-block;">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp">
					<span style="margin:20px;width:">
						<img src="../aon-solutions/assets/aon.png" width="60px"/>
						<span class="aon-marketplace-title"> AiO </span>
					</span>
					<div>
						<span style="padding: 25px;color: #7E7E7E;">
							AON SOLUTIONS AiO.
						</span>
					</div>
					<div class="aon-div-button">
						<label class="mdl-switch mdl-js-switch mdl-js-ripple-effect" for="switch-8">
  						<input type="checkbox" id="switch-8" class="mdl-switch__input" checked>
  						<span class="mdl-switch__label"></span>
						</label>
					</div>
				</div>
			</li>

			<li style="display: inline-block;">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp">
					<span style="margin:20px;width:">
						<img src="../aon-solutions/assets/apps/selfconta.png" width="60px"/>
						<span class="aon-marketplace-title"> Selfconta </span>
					</span>
					<div>
						<span style="padding: 25px;color: #7E7E7E;">
							Selfconta.
						</span>
					</div>
					<div class="aon-div-button">
						<label class="mdl-switch mdl-js-switch mdl-js-ripple-effect" for="switch-9">
							<input type="checkbox" id="switch-9" class="mdl-switch__input" checked>
							<span class="mdl-switch__label"></span>
						</label>
					</div>
				</div>
			</li>

			<li style="display: inline-block;">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp">
					<span style="margin:20px;width:">
						<img src="../aon-solutions/assets/apps/saltra.png" width="60px"/>
						<span class="aon-marketplace-title"> Saltra </span>
					</span>
					<div>
						<span style="padding: 25px;color: #7E7E7E;">
							Saltra.
						</span>
					</div>
					<div class="aon-div-button">
						<label class="mdl-switch mdl-js-switch mdl-js-ripple-effect" for="switch-10">
  						<input type="checkbox" id="switch-10" class="mdl-switch__input" checked>
  						<span class="mdl-switch__label"></span>
						</label>
					</div>
				</div>
			</li>

			<li style="display: inline-block;">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp">
					<span style="margin:20px;width:">
						<img src="../aon-solutions/assets/apps/bidoq.png" width="60px"/>
						<span class="aon-marketplace-title"> Bidoq </span>
					</span>
					<div>
						<span style="padding: 25px;color: #7E7E7E;">
							Bidoq.
						</span>
					</div>
					<div class="aon-div-button">
						<label class="mdl-switch mdl-js-switch mdl-js-ripple-effect" for="switch-11">
  						<input type="checkbox" id="switch-11" class="mdl-switch__input" checked>
  						<span class="mdl-switch__label"></span>
						</label>
					</div>
				</div>
			</li>

			<li style="display: inline-block;">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp">
					<span style="margin:20px;width:">
						<img src="../aon-solutions/assets/apps/alma.png" width="60px"/>
						<span class="aon-marketplace-title"> Alma </span>
					</span>
					<div>
						<span style="padding: 25px;color: #7E7E7E;">
							Alma.
						</span>
					</div>
					<div class="aon-div-button">
						<label class="mdl-switch mdl-js-switch mdl-js-ripple-effect" for="switch-12">
  						<input type="checkbox" id="switch-12" class="mdl-switch__input" checked>
  						<span class="mdl-switch__label"></span>
						</label>
					</div>
				</div>
			</li>

			<li style="display: inline-block;">
				<div class="demo-card-wide mdl-card mdl-shadow--2dp">
					<span style="margin:20px;width:">
						<img src="../aon-solutions/assets/apps/learning.png" width="60px"/>
						<span class="aon-marketplace-title"> Learning </span>
					</span>
					<div>
						<span style="padding: 25px;color: #7E7E7E;">
							Learning.
						</span>
					</div>
					<div class="aon-div-button">
						<label class="mdl-switch mdl-js-switch mdl-js-ripple-effect" for="switch-13">
  						<input type="checkbox" id="switch-13" class="mdl-switch__input" checked>
  						<span class="mdl-switch__label"></span>
						</label>
					</div>
				</div>
			</li>

			</ul>
			`;
			componentHandler.upgradeAllRegistered();
  }
}

window.customElements.define('aon-marketplace', AonMarketplace);

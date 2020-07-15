

class AonDialog extends HTMLElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<dialog class="mdl-dialog">
		    <div class="mdl-dialog__content">

		    </div>
		    <div class="mdl-dialog__actions mdl-dialog__actions--full-width">
		      <button type="button" class="mdl-button">Agree</button>
		      <button type="button" class="mdl-button close">Disagree</button>
		    </div>
		  </dialog>
			`;
  }
}

window.customElements.define('aon-dialog', AonDialog);

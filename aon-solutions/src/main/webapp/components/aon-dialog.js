

class AonDialog extends HTMLElement {

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get title() {
		return this.getAttribute('title');
	}

	set title(title) {
		this.setAttribute('title', title);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		// this.innerHTML = `
		// 	<dialog class="mdl-dialog">
		// 		<h4 class="mdl-dialog__title">Allow data collection?</h4>
		//     <div class="mdl-dialog__content">
		//
		//     </div>
		//     <div class="mdl-dialog__actions mdl-dialog__actions--full-width">
		//       <button type="button" class="mdl-button">Agree</button>
		//       <button type="button" class="mdl-button close">Disagree</button>
		//     </div>
		//   </dialog>
		// 	`;

			let dialog = document.createElement('dialog');
			dialog.setAttribute('id', 'aonDialog');
			dialog.className = 'mdl-dialog';

			let title = document.createElement('h4');
			title.setAttribute('id', this.getAttribute('id') + 'Title');
			title.className = 'mdl-dialog__title';
			title.innerHTML = this.getAttribute('title');

			let content = document.createElement('div');
			content.setAttribute('id', this.getAttribute('id') + 'Content');
			content.className = 'mdl-dialog__content';

			let actions = document.createElement('div');
			actions.setAttribute('id', this.getAttribute('id') + 'Actions');
			actions.className = 'mdl-dialog__actions mdl-dialog__actions--full-width';

			let cancel = document.createElement('button');
			cancel.className = 'mdl-button';
			cancel.setAttribute('id', this.getAttribute('id') + 'ActionsCancelButton');
			cancel.addEventListener('click', () => dialog.close());
			cancel.innerHTML = 'Cancelar';

			let accept = document.createElement('button');
			accept.className = 'mdl-button';
			accept.setAttribute('id', this.getAttribute('id') + 'ActionsAcceptButton');
			accept.addEventListener('click', () => dialog.close());
			accept.innerHTML = 'Aceptar';

			actions.appendChild(accept)
			actions.appendChild(cancel);

			dialog.appendChild(title);
			dialog.appendChild(content);
			dialog.appendChild(actions);

			this.appendChild(dialog);
			//dialogPolyfill.registerDialog(dialog);
  }

	open(){
		let dialog = document.createElement('dialog');
		dialog.showModal();
	}

	addContent(widget) {
		let content = document.getElementById(this.getAttribute('id') + 'Content');
		content.appendChild(widget)
	}

	addContentHtml(html) {
		let content = document.getElementById(this.getAttribute('id') + 'Content');
		content.innerHTML = html;
	}

	addCancelAction(fn) {
		let cancel = document.getElementById(this.getAttribute('id') + 'ActionsCancelButton');
		cancel.addEventListener('click', fn);
	}

	addAcceptAction(fn) {
		let accept = document.getElementById(this.getAttribute('id') + 'ActionsAcceptButton');
		accept.addEventListener('click', fn);
	}
}

window.customElements.define('aon-dialog', AonDialog);

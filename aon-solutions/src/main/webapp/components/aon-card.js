(function() {

	class AonCard extends HTMLElement {

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
			this.appendChild(this.build());
		}

		build() {
			let div = document.createElement('div');
			div.setAttribute('id', this.getAttribute('id') + '-div');
			div.className = "demo-card-wide mdl-card mdl-shadow--2dp aon-card";

			let div1 = document.createElement('div');
			div1.className = "mdl-card__title";

			let h2 = document.createElement('h2');
			h2.className = "mdl-card__title-text";
			h2.innerHTML = this.getAttribute('title');

			div1.appendChild(h2);
			div.appendChild(div1);

			return div;
		}

		addContent(content) {
			let div = document.getElementById(this.getAttribute('id') + '-div');
			let div2 = document.createElement('div');
			div2.innerHTML = content;
			div.appendChild(div2);
		}
	}

	window.customElements.define('aon-card',  AonCard);

})();

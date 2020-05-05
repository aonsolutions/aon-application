(function() {

	class AonCard extends HTMLElement {

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
	}

	window.customElements.define('aon-card',  AonCard);

})();

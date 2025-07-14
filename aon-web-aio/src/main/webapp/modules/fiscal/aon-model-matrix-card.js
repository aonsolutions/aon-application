import { AonElement } from "../../components/AonElement.js";
import { AonLoader } from '../../components/aon-loader.js';
import * as GWT from "../../gwt/gwt.js";

export class AonModelMatrixCard extends AonElement {

	constructor() {
		super();
	}

	connectedCallback() {
		this.initialize();
		this.build();
	}

	initialize() {
		this.id = "aonModelMatrixCard";
	}


	build() {
		this.style.display = "flex";
		this.style.flexDirection = "column";
		this.style.justifyContent = "space-between";
		this.style.height = "100%";
		// elementTarget : "rootPanel" for activate compact mode.
		GWT.iLoad(GWT.MODEL_MATRIX, this.id, { elementTarget: "rootPanel" })
			.then(this.fixGwtPopups);



	}

	fixGwtPopups(gwtIFrame) {


		const gwtDocument = gwtIFrame.document || gwtIFrame.contentDocument || gwtIFrame.contentWindow.document;
		
		// Create an observer instance linked to the callback function
		const observer = new MutationObserver((records, observer) => {
			for (const record of records) {
				for (const addedNode of record.addedNodes) {
					if (addedNode.className == 'gwt-PopupPanelGlass') {
						gwtIFrame.style.top = '0px';
						gwtIFrame.style.left = '0px';
						gwtIFrame.style.zIndex = '999';
						gwtIFrame.style.position = 'fixed';
						gwtIFrame.style.width = `calc(100vw)`;
						gwtIFrame.style.height = `calc(100vh)`;
					} else if (addedNode.className == 'gwt-PopupPanel') {
						if (gwtDocument.getElementsByClassName('gwt-PopupPanel').item(0) == addedNode ) {
							// If the added node is the first gwt-PopupPanel, we need to adjust its size.
							const gwtPopupPanel = addedNode;
							const gwtPopupContent = gwtPopupPanel.firstChild;
							gwtPopupContent.firstChild.style.width = `calc(100vw - 50px)`;
							gwtPopupContent.firstChild.style.height = `calc(100vh - 50px)`;
						}
					} 
				}
				for (const removedNode of record.removedNodes) {
					if (removedNode.className == 'gwt-PopupPanelGlass') {
						// If there are no more gwt-PopupPanelGlass elements, reset the gwtIFrame styles.
						if (gwtDocument.getElementsByClassName('gwt-PopupPanelGlass').length == 0) {
							gwtIFrame.style.width = `100%`;
							gwtIFrame.style.height = `100%`;
							gwtIFrame.style.removeProperty('top');
							gwtIFrame.style.removeProperty('left');
							gwtIFrame.style.removeProperty('position');
							gwtIFrame.style.removeProperty('z-index');
						}
					} else if (removedNode.className == 'gwt-PopupPanel') {
						// Noop			
					}
				}
			}
		});

		// Start observing the target node for configured mutations
		observer.observe(gwtDocument.body, { childList: true, subtree: true });


	}
	

}
window.customElements.define("aon-model-matrix-card", AonModelMatrixCard);

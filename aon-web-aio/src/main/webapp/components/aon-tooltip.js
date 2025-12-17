import { TAG } from '../environments/environments.js';

export class AonTooltip {

	constructor(target, text, options = {}) {
		this.target = target;
		this.text = text;

		this.options = {
			position: options.position || 'right', // right | bottom | top | left
			offset: options.offset ?? 8
		};

		this.tooltip = null;

		this.onEnter = this.onEnter.bind(this);
		this.onLeave = this.onLeave.bind(this);
		this.onReposition = this.onReposition.bind(this);

		target.addEventListener('mouseenter', this.onEnter);
		target.addEventListener('mouseleave', this.onLeave);
		target.addEventListener('focus', this.onEnter);
		target.addEventListener('blur', this.onLeave);
	}

	onEnter() {
		if (this.tooltip) return;

		this.tooltip = document.createElement('div');
		this.tooltip.className = 'tooltip';
		this.tooltip.textContent = this.text;
		document.body.appendChild(this.tooltip);

		this.positionTooltip();

		// Forzar repaint para que la animación funcione
		requestAnimationFrame(() => {
			this.tooltip.classList.add('visible');
		});

		window.addEventListener('scroll', this.onReposition, true);
		window.addEventListener('resize', this.onReposition);
	}

	onLeave() {
		if (!this.tooltip) return;

		this.tooltip.classList.remove('visible');

		// Esperar a que termine el fade-out
		setTimeout(() => {
			if (this.tooltip) {
				this.tooltip.remove();
				this.tooltip = null;
			}
		}, 150);

		window.removeEventListener('scroll', this.onReposition, true);
		window.removeEventListener('resize', this.onReposition);
	}

	onReposition() {
		if (!this.tooltip) return;
		this.positionTooltip();
	}

	positionTooltip() {
		const rect = this.target.getBoundingClientRect();
		const tipRect = this.tooltip.getBoundingClientRect();
		const { position, offset } = this.options;

		const fitsRight = rect.right + tipRect.width + offset <= window.innerWidth;
		const fitsBottom = rect.bottom + tipRect.height + offset <= window.innerHeight;

		let top, left;

		if (position === 'right' && fitsRight) {
			top = rect.top + (rect.height - tipRect.height) / 2;
			left = rect.right + offset;

		} else if (position === 'bottom' && fitsBottom) {
			top = rect.bottom + offset;
			left = rect.left + (rect.width - tipRect.width) / 2;

		} else if (position === 'left') {
			top = rect.top + (rect.height - tipRect.height) / 2;
			left = rect.left - tipRect.width - offset;

		} else if (position === 'top') {
			top = rect.top - tipRect.height - offset;
			left = rect.left + (rect.width - tipRect.width) / 2;

		} else if (fitsBottom) {
			// fallback automático
			top = rect.bottom + offset;
			left = rect.left + (rect.width - tipRect.width) / 2;

		} else {
			// último recurso
			top = Math.max(offset, rect.top);
			left = Math.max(offset, rect.left);
		}

		let gap = 30;

		this.tooltip.style.top = `${top}px`;
		this.tooltip.style.left = `${left - gap}px`;
	}

	destroy() {
		this.onLeave();
		this.target.removeEventListener('mouseenter', this.onEnter);
		this.target.removeEventListener('mouseleave', this.onLeave);
		this.target.removeEventListener('focus', this.onEnter);
		this.target.removeEventListener('blur', this.onLeave);
	}
	
}
if(!window.customElements.get(TAG.AON_TOOLTIP)){
	window.customElements.define(TAG.AON_TOOLTIP, AonTooltip);
}

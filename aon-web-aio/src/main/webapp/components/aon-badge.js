import { AonElement } from './AonElement.js';
import { TAG } from '../environments/environments.js';

export class AonBadge extends AonElement {
  BADGE;

  // ========== GETTERS & SETTERS ==========
  get text() {
    return this.textContent;
  }

  set text(value) {
    this.textContent = value;
  }

  get count() {
    return this.getAttribute('data-count');
  }

  set count(value) {
    this.setAttribute('data-count', value);
    this.updateCount();
  }

  // ========== CONSTRUCTOR ==========
  constructor() {
    super();
  }

  // ========== LIFECYCLE METHODS ==========
  connectedCallback() {
    this.initialize();
    this.build();
  }

  static get observedAttributes() {
    return ['data-count'];
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if (name === 'data-count' && oldValue !== newValue) {
      this.updateCount(); // corregido
    }
  }

  // ========== INITIALIZE / BUILD ==========
  initialize() {
    const id   = this.id || `aonBadge-${generateSimpleUUID()}`;
    this.id    = id;
    this.BADGE = id + 'Badge';
  }
  build() {
    this.classList.add('badge-container');

    // Badge
    const badge     = this.createElement(TAG.DIV);
    badge.id        = this.BADGE;
    badge.className = 'badge';
    this.appendChild(badge);

    this.updateCount();
  }

  // ========== HELPERS ==========
  updateCount() {
    const badge = this.getElement(this.BADGE);
    if (!badge) return;

    const value       = parseInt(this.count, 10);
    badge.textContent = isNaN(value) ? '' : (value > 99 ? '+99' : value.toString());
  }
}

if (!window.customElements.get(TAG.AON_BADGE)) {
  window.customElements.define(TAG.AON_BADGE, AonBadge);
}

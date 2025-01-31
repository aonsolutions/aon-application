import { AonElement } from 'aonsolutions/components/AonElement.js';
import { TAG } from 'aonsolutions/environments/environments.js';
import { AonCard } from './aon-card.js';
import { AonIcon } from './aon-icon.js';

export class AonDragLeft extends AonElement {
  constructor() {
    super();
    this.currentCardIndex = 0;
    this.cards = [];
    this.numCards = 0; // No establecemos un número fijo; se configurará dinámicamente
    this.dots = [];
  }

  connectedCallback() {
    this.initialize();
    this.build();
    this.addSwipeListeners();
  }

  initialize() {
    // La inicialización queda más genérica
    this.cards = [];
    this.dots = [];
    this.numCards = this.numCards || 0;
  }

  build() {
    // Construimos la estructura básica de contenedores
    const container = this.createElement('div');
    container.className = 'aonDragLeftContainer';
    container.id = "aonDragLeftContainer";
    this.container = container;
    this.appendChild(container);

    const innerContainer = this.createElement('div');
    innerContainer.className = "aonDragLeftInnerContainer";
    this.innerContainer = innerContainer;
    container.appendChild(innerContainer);

    const dotsContainer = this.createElement('div');
    dotsContainer.className = "aonDragLeftDotsContainer";
    this.dotsContainer = dotsContainer;
    this.appendChild(dotsContainer);
  }

  addSwipeListeners() {
    let startX;
    this.addEventListener('touchstart', (e) => {
      startX = e.touches[0].clientX;
    });

    this.addEventListener('touchend', (e) => {
      const endX = e.changedTouches[0].clientX;
      if (startX - endX > 50) {
        this.showNextCard();
      } else if (endX - startX > 50) {
        this.showPreviousCard();
      }
    });
  }

  showNextCard() {
    if (this.currentCardIndex < this.numCards - 1) {
      this.currentCardIndex++;
      this.updateCardVisibility();
    }
  }

  showPreviousCard() {
    if (this.currentCardIndex > 0) {
      this.currentCardIndex--;
      this.updateCardVisibility();
    }
  }

  updateCardVisibility() {
    const translateXValue = -this.currentCardIndex * 100;
    this.innerContainer.style.transform = `translateX(${translateXValue}%)`;

    this.dots.forEach((dot, index) => {
      dot.style.backgroundColor = index === this.currentCardIndex ? 'black' : 'lightgray';
    });
  }

  setContent(cards) {
    // Limpiamos el contenido anterior
    this.innerContainer.innerHTML = '';
    this.dotsContainer.innerHTML = '';
    this.cards = cards;
    this.numCards = cards.length;

    // Añadimos las tarjetas al contenedor
    cards.forEach((card, index) => {
      this.innerContainer.appendChild(card);
      
      const dot = this.createElement('span');
      dot.className = "aonDragLeftDot";
      dot.style.backgroundColor = index === 0 ? 'black' : 'lightgray';
      this.dots.push(dot);
      this.dotsContainer.appendChild(dot);
    });

    this.updateCardVisibility();
  }
}


if (!window.customElements.get(TAG.AON_DRAGLEFT)) {
  window.customElements.define(TAG.AON_DRAGLEFT, AonDragLeft);
}

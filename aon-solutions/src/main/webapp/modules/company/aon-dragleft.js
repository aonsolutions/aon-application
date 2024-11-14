import { AonElement } from 'aonsolutions/components/AonElement.js';
import { TAG } from 'aonsolutions/environments/environments.js';
import { AonCard } from '../../components/aon-card.js';
import { AonIcon } from '../../components/aon-icon.js';

export class AonDragLeft extends AonElement {
  constructor() {
    super();
    this.currentCardIndex = 0;
  }

  connectedCallback() {
    this.initialize();
    this.build();
    this.addSwipeListeners();
  }

  initialize() {
    this.cards = [];
    this.numCards = 5;
    this.dots = [];
  }

  build() {
    const container = this.createElement('div');
    container.className = 'aonDragLeftContainer';
    this.container = container;
    this.appendChild(container);

    const innerContainer = this.createElement('div');
    innerContainer.className = "aonDragLeftInnerContainer";
    this.innerContainer = innerContainer;
    container.appendChild(innerContainer);

    for (let i = 0; i < this.numCards; i++) {
      const card = new AonCard();
      card.id = `card-${i}`;
      card.className = "aonDragLeftCard";
      this.cards.push(card);
      innerContainer.appendChild(card);
      this.addNotificationToCard(card);
    }

    const dotsContainer = this.createElement('div');
    dotsContainer.className = "aonDragLeftDotsContainer";
    this.appendChild(dotsContainer);

    for (let i = 0; i < this.numCards; i++) {
      const dot = this.createElement('span');
      dot.className = "aonDragLeftDot";
      dot.style.backgroundColor = i === 0 ? 'black' : 'lightgray';
      this.dots.push(dot);
      dotsContainer.appendChild(dot);
    }

    let card0 = this.getElement("card-0Card");
    card0.style.border = "0px";
    card0.style.margin = "0px";
    let card1 = this.getElement("card-1Card");
    card1.style.border = "0px";
    card1.style.margin = "0px";
    let card2 = this.getElement("card-2Card");
    card2.style.border = "0px";
    card2.style.margin = "0px";
    let card3 = this.getElement("card-3Card");
    card3.style.border = "0px";
    card3.style.margin = "0px";
    let card4 = this.getElement("card-4Card");
    card4.style.border = "0px";
    card4.style.margin = "0px";

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

  addNotificationToCard(card) {
    const notificationDiv = this.createElement('div');
    notificationDiv.className = 'notificationCard';
    notificationDiv.style.padding = '10px';
    notificationDiv.style.margin = '10px 0';
    notificationDiv.style.backgroundColor = '#f5f5f5';
    notificationDiv.style.borderRadius = '8px';

    const title = this.createElement('span');
    title.innerHTML = 'Notificación importante'+card.id;
    title.style.fontWeight = 'bold';
    title.style.display = 'block';
    title.style.marginLeft = "35px";

    let divContent = this.createElement(TAG.DIV);
    divContent.style.display = "flex";
    divContent.style.marginTop = "3px";
    divContent.style.marginBottom = "3px";

    let icon = new AonIcon();
    icon.icon = "aon_new_documental";
    icon.size = "35px";
    divContent.appendChild(icon);

    const body = this.createElement('span');
    body.innerHTML = 'Esta es una notificación de ejemplo. ¡Revisa los detalles!';
    body.style.display = 'block';
    divContent.appendChild(body);

    const date = this.createElement('span');
    date.innerHTML = '(Hoy)';
    date.style.fontSize = '12px';
    date.style.color = '#888';
    date.style.marginLeft = "35px";

    notificationDiv.appendChild(title);
    notificationDiv.appendChild(divContent);
    notificationDiv.appendChild(date);

    card.setContent(notificationDiv);
  }
}

if (!window.customElements.get(TAG.AON_DRAGLEFT)) {
  window.customElements.define(TAG.AON_DRAGLEFT, AonDragLeft);
}

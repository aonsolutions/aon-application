import {AonElement} from '../../components/AonElement.js';

export class AonStat extends AonElement {

  constructor () {
		super();
  }

  connectedCallback () {
    this.buildSlide();
  }

  buildSlide() {
    let div = document.createElement('div');
    div.appendChild(this.buildSlideContent())
    this.appendChild(div);
    this.appendChild(this.buildSlideDot(1));
  }

  buildSlideContent() {
    let div = document.createElement('div');
    div.className = 'aonNone aonDesktopSlideFade';
    div.style.display = 'block';
    div.style.marginTop = '10px';

    let bannerImg = document.createElement('img');
    bannerImg.src = '../assets/img/atp_img_publi.jpg';
    bannerImg.style.width = '100%';
    bannerImg.style.maxWidth = '1117px';

    div.appendChild(bannerImg);

    return div;
  }

  buildSlideDot(index) {
    let div = document.createElement('div');
    div.style.marginTop = '10px';
    div.style.textAlign = 'center';
    for(let i = 0; i < index; i++ ) {
      let span = document.createElement('span');
      span.className = 'aonDot';
      // span.addEventListener('click' => {
      //
      // });
      div.appendChild(span);
    }
    return div;
  }
}
window.customElements.define('aon-stat', AonStat);

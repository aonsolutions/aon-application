import { AonElement } from '../../components/AonElement';
import { AonCard } from '../../components/aon-card';
import { TAG } from "../../environments/environments";

export class AonCertification extends AonElement {
  constructor () {
    super();
  }

  connectedCallback () {
    this.buildDur().then(() => {
      this.build();
    });
  }

  build () {
    //
    // AEAT
    const aeatCard     = new AonCard();
    aeatCard.id        = "aeat-card";
    aeatCard.title     = "AEAT";
    this.appendChild(aeatCard);
    // Contenido
      // imagen
      const imgAeat     = this.createElement(TAG.DIV);
      imgAeat.className = "card-img-aeat";
      aeatCard.setContent(imgAeat);
      // textos
      const optionsAeat = [
        "Homologado para la Digitalización Certificada de Facturas Nº Homologación: RGE405069592024",
        "Software integrado con Veri*Factu",
        "Registrado como Colaborador Social",
        {
          title   : "Comunic@ / AEAT:",
          children: [
            "Comunicación Online en tiempo real de modelos fiscales",
            "Consulta y descarga de modelos fiscales presentados"
          ]
        }
      ];
      const content = this.buildListContent(optionsAeat);
      // Agregar el contenido
      aeatCard.addContent(content);
    // Footer
      const footer = this.createCardFooter({
        text: "Ver software homologados",
        href: "https://sede.agenciatributaria.gob.es/static_files/Sede/Procedimiento_ayuda/FZ01/RSoftHomologado_doc_base.pdf",
        note: "(Página 11)"
      });
      aeatCard.addContent(footer);
    // FIN AEAT
    //

    //
    // VERIFACTU
    const verifactuCard     = new AonCard();
    verifactuCard.id        = "verifactu-card";
    verifactuCard.title     = "Software Integrado con Veri*Factu";
    this.appendChild(verifactuCard);
    // Contenido
      // imagen
      const imgVeri = this.createElement(TAG.DIV);
      imgVeri.className = "card-img-veri";
      verifactuCard.setContent(imgVeri);
      // textos
      const verifactuText       = this.createElement(TAG.P);
      verifactuText.textContent = `Nuestro software cumple con todos los requisitos técnicos y legales establecidos por la Agencia Tributaria. 
Ayuda asesorías, Pymes y autónomos a adaptarse de manera sencilla al nuevo marco de facturación electrónica 
con total seguridad y respaldo normativo.`;
      // Agregar el contenido
      verifactuCard.setContent(verifactuText);
    // Footer
      const verifactuFooter = this.createCardFooter({
          text : "Ver Declaración Responsable",
          href : "https://drive.google.com/file/d/1xObCAXybwhoOEa339DgFy9h3KL97CYJ5/view?usp=sharing"
      });
      verifactuCard.addContent(verifactuFooter);
    // FIN VERIFACTU
    //

    //
    // HACIENDAS FORALES
    const haciendasCard     = new AonCard();
    haciendasCard.id        = "haciendas-card";
    haciendasCard.title     = "Haciendas Forales";
    this.appendChild(haciendasCard);
    // Contenido
      // imagen
      const imgHaciendas = this.createElement(TAG.DIV);
      imgHaciendas.className = "card-img-haciendas";
      haciendasCard.setContent(imgHaciendas);
      // textos
      const haciendasText       = this.createElement(TAG.P);
      haciendasText.textContent = `Solución cloud homologada por el Gobierno Vasco como software garante del cumplimiento legal de “Ticket BAI” para las tres Haciendas Forales:`;

      const haciendasOptions = [
        "Hacienda Foral de ÁLAVA/ARABA",
        "Hacienda Foral de BIZKAIA",
        "Hacienda Foral de GIPUZKOA"
      ];
      const haciendasList = this.buildListContent(haciendasOptions);
      // Agregar el contenido
      haciendasCard.setContent(haciendasText);
      haciendasCard.addContent(haciendasList);
    // Footer
      const haciendasFooter = this.createCardFooter({
        text : "Ver software registrados",
        href : "https://www.batuz.eus/es/registro-de-software?q=aonSolutions"
      });
      haciendasCard.addContent(haciendasFooter);
    // FIN HACIENDAS FORALES
    //

    //
    // AGENCIA TRIBUTARIA CANARIA
    const atcCard     = new AonCard();
    atcCard.id        = "atc-card";
    atcCard.className = "no-space";
    atcCard.title     = "Agencia Tributaria Canaria";
    this.appendChild(atcCard);
    // Contenido
      // imagen
      const imgAtc = this.createElement(TAG.DIV);
      imgAtc.className = "card-img-atc";
      atcCard.setContent(imgAtc);
      // textos
      const atcOptions = [
        "Software con soporte del IGIC",
        "Software integrado con Veri*Factu",
        "Registrado como Colaborador Social"
      ];
      const atcContent = this.buildListContent(atcOptions);
      // Agregar el contenido
      atcCard.setContent(atcContent);
    // FIN AGENCIA TRIBUTARIA CANARIA
    //

    //
    // TGSS
    const tgssCard     = new AonCard();
    tgssCard.id        = "tgss-card";
    tgssCard.title     = "TGSS";
    this.appendChild(tgssCard);
    // Contenido
      // imagen
      const imgTgss = this.createElement(TAG.DIV);
      imgTgss.className = "card-img-tgss";
      tgssCard.setContent(imgTgss);
      // textos
      const tgssOptions = [
        "Software de nóminas registrado",
        "Código Proveedor de nóminas nº 558",
        {
          title   : "Comunic@ / TGSS:",
          children: [
            "Comunicación Online en tiempo real de ALTAS previas, mensajes AFI y FIE",
            "Consulta de Certificado de empresa, Vida Laboral, IDC y FIE",
            "Sincronización de datos entre TGSS y AON"
          ]
        }
      ];
      const tgssContent = this.buildListContent(tgssOptions);
      // Agregar el contenido
      tgssCard.setContent(tgssContent);
    // Footer
    const tgssFooter = this.createCardFooter({
      text : "Ver listado proveedores",
      href : "https://www.seg-social.es/wps/wcm/connect/wss/7ada0194-1392-4dca-bc7f-e2496ec372b8/T86-Proveedor+de+n%C3%B3mina+2018-07.pdf?MOD=AJPERES&CVID=",
      note : "(Página 4)"
    });
    tgssCard.addContent(tgssFooter);
    // FIN TGSS
    //

    //
    // SEPE
    const sepeCard     = new AonCard();
    sepeCard.id        = "sepe-card";
    sepeCard.title     = "SEPE";
    sepeCard.className = "no-space";
    this.appendChild(sepeCard);
    // Contenido
      // imagen
      const imgSepe = this.createElement(TAG.DIV);
      imgSepe.className = "card-img-sepe";
      sepeCard.setContent(imgSepe);
      // textos
      const sepeOptions = [
        {
          title   : "Comunic@ / TGSS:",
          children: [
            "Comunicación Online en tiempo real de Contrat@, Copia básica, prórrogas y Certific@2",
            "Consulta de Contratos y Copia básica"
          ]
        }
      ];
      const sepeContent = this.buildListContent(sepeOptions);
      // Agregar el contenido
      sepeCard.setContent(sepeContent);
    // FIN SEPE
    //
  }

  buildListContent(options) {
    const ul        = this.createElement(TAG.UL);

    for (const option of options) {
      const li     = this.createElement(TAG.LI);
      if (typeof option === "string") {
        li.innerHTML = option;
      } else if ( typeof option === "object" && option.title && Array.isArray(option.children) ) {
        li.innerHTML = option.title;
        const nestedUl      = this.createElement(TAG.UL);

        for (const child of option.children) {
          const nestedLi       = this.createElement(TAG.LI);
          nestedLi.textContent = child;
          nestedUl.appendChild(nestedLi);
        }
        li.appendChild(nestedUl);
      }

      ul.appendChild(li);
    }
    return ul;
  }

  createCardFooter(footerOption) {
    const footer = this.createElement(TAG.DIV);
    footer.className = "card-footer right-align";

    const link       = this.createElement(TAG.A);
    link.href        = footerOption.href;
    link.target      = "_blank";
    link.rel         = "noopener noreferrer";
    link.className   = "card-footer-link";
    link.textContent = footerOption.text;

    footer.appendChild(link);

    if (footerOption.note) {
      const note       = this.createElement(TAG.SPAN);
      note.className   = "card-footer-note";
      note.textContent = footerOption.note;
      footer.appendChild(note);
    }

    return footer;
  }

}

if(!window.customElements.get(TAG.AON_CERTIFICATION)){
  window.customElements.define(TAG.AON_CERTIFICATION, AonCertification);
}

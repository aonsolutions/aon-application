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
    this.createCard({
      id        : "aeat-card",
      title     : "AEAT",
      imgClass  : "card-img-aeat",
      listOptions: [
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
      ],
      footer: {
        text: "Ver software homologados",
        href: "https://sede.agenciatributaria.gob.es/static_files/Sede/Procedimiento_ayuda/FZ01/RSoftHomologado_doc_base.pdf",
        note: "(Página 11)"
      }
    });
    // FIN AEAT
    //

    //
    // VERIFACTU
    this.createCard({
      id         : "verifactu-card",
      title      : "Software Integrado con Veri*Factu",
      imgClass   : "card-img-veri",
      textContent: `Nuestro software cumple con todos los requisitos técnicos y legales establecidos por la Agencia Tributaria. 
        Ayuda asesorías, Pymes y autónomos a adaptarse de manera sencilla al nuevo marco de facturación electrónica 
        con total seguridad y respaldo normativo.`,
      footer: {
        text : "Ver Declaración Responsable",
        href : "https://drive.google.com/file/d/1xObCAXybwhoOEa339DgFy9h3KL97CYJ5/view?usp=sharing"
      }
    });
    // FIN VERIFACTU
    //

    //
    // HACIENDAS FORALES
    this.createCard({
      id         : "haciendas-card",
      title      : "Haciendas Forales",
      imgClass   : "card-img-haciendas",
      textContent: `Solución cloud homologada por el Gobierno Vasco como software garante del cumplimiento legal de ?Ticket BAI? para las tres Haciendas Forales:`,
      listOptions: [
        "Hacienda Foral de ÁLAVA/ARABA",
        "Hacienda Foral de BIZKAIA",
        "Hacienda Foral de GIPUZKOA"
      ],
      footer: {
        text : "Ver software registrados",
        href : "https://www.batuz.eus/es/registro-de-software?q=aonSolutions"
      }
    });
    // FIN HACIENDAS FORALES
    //

    //
    // AGENCIA TRIBUTARIA CANARIA
    this.createCard({
      id         : "atc-card",
      title      : "Agencia Tributaria Canaria",
      className  : "no-space",
      imgClass   : "card-img-atc",
      listOptions: [
        "Software con soporte del IGIC",
        "Software integrado con Veri*Factu",
        "Registrado como Colaborador Social"
      ]
    });
    // FIN AGENCIA TRIBUTARIA CANARIA
    //

    //
    // TGSS
    this.createCard({
      id         : "tgss-card",
      title      : "TGSS",
      imgClass   : "card-img-tgss",
      listOptions: [
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
      ],
      footer: {
        text : "Ver listado proveedores",
        href : "https://www.seg-social.es/wps/wcm/connect/wss/7ada0194-1392-4dca-bc7f-e2496ec372b8/T86-Proveedor+de+n%C3%B3mina+2018-07.pdf?MOD=AJPERES&CVID=",
        note : "(Página 4)"
      }
    });
    // FIN TGSS
    //

    //
    // SEPE
    this.createCard({
      id         : "sepe-card",
      title      : "SEPE",
      className  : "no-space",
      imgClass   : "card-img-sepe",
      listOptions: [
        {
          title   : "Comunic@ / TGSS:",
          children: [
            "Comunicación Online en tiempo real de Contrat@, Copia básica, prórrogas y Certific@2",
            "Consulta de Contratos y Copia básica"
          ]
        }
      ]
    });
    // FIN SEPE
    //
  }

  createCard({ id, title, className, imgClass, listOptions, textContent, footer }) {
    const card  = new AonCard();
    card.id     = id;
    card.title  = title;
    if (className) card.className = className;
    this.appendChild(card);

    // imagen
    const img     = this.createElement(TAG.DIV);
    img.className = imgClass;
    card.setContent(img);

    // textos
    if (textContent) {
      const text       = this.createElement(TAG.P);
      text.textContent = textContent;
      card.setContent(text);
    }

    if (listOptions) {
      const list = this.addListContent(listOptions);
      card.addContent(list);
    }

    // Footer
    if (footer) {
      const footerEl = this.createCardFooter(footer);
      card.addContent(footerEl);
    }
  }

  addListContent(options) {
    const ul = this.createElement(TAG.UL);

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

import { AonElement } from "../../../../components/AonElement";
import { AonApplication } from "../../../../components/aon-application";
import { AonAgendaAllDays } from "./aon-agenda-all-days";

export class AgendaApplication extends AonElement {
  
  _agendaComponent;
  _applicationEl;
  
  connectedCallback() {
    this.build();
  }
  
  async build() {
    // 1. Crear la aplicación
    this.createApplication('aonAgenda', 'Agenda', new AonApplication());
    this._applicationEl = this.getApplication();
    
    // 2. Configurar header del sidenav móvil
    const Apps = {
      icon: "event",
      color: "#1a73e8",
      title: "Agenda",
      app: "agenda"
    };
    this._applicationEl.addMobileSidenavHeader(Apps);
    
    // 3. Añadir opciones al sidenav (botón "Hoy")
    this.addSidenavOptions();
    
    // 4. Crear el widget de agenda y añadirlo al contenido
    this._agendaComponent = new AonAgendaAllDays();
    this._applicationEl.setContent(this._agendaComponent);
  }
  
  addSidenavOptions() {
    const sidenavData = {
      id: "AgendaOptions",
      name: "Navegación",
      app: {
        color: "#1a73e8"
      },
      options: [
        {
          id: "today",
          name: "Hoy",
          icon: "today",
          fn: () => this.goToToday()
        }
      ]
    };
    
    this._applicationEl.addSidenavOptions3(sidenavData);
  }
  
  goToToday() {
    if (this._agendaComponent && typeof this._agendaComponent.goToToday === 'function') {
      this._agendaComponent.goToToday();
    }
  }
  
  // Método público para recargar la agenda
  reload() {
    if (this._agendaComponent && typeof this._agendaComponent.reload === 'function') {
      this._agendaComponent.reload();
    }
  }
}

window.customElements.define("agenda-application", AgendaApplication);
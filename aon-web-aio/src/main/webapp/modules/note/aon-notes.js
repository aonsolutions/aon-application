import { AonElement } from "../../components/AonElement.js";
import { AonApplication } from "../../components/aon-application.js";
import { AON_ICONS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments.js";
import { NOTES } from "../../services/app.js";
import { AON_NOTES } from "../../environments/aonTag.js";
import { getNotes, saveNote, deleteNote, saveNoteTag, deleteNoteTag } from "../../services/noteService.js";
import { AonIconButton } from "../../components/aon-icon-button.js";
import { Note } from "../../models/note/Note.js";
import { AonDialogMenu } from "../../components/aon-dialog-menu.js";
import { AonNewDialog } from "../../components/aon-new-dialog.js";
import { AonNewInput } from "../../components/aon-new-input";
import { sortBy } from "../../services/utils.js";
import { AonIcon } from "../../components/aon-icon.js";
import { createInput } from "../../components/CreateComponent.js";

export class AonNotes extends AonElement {
  NOTES;
  NOTES_TAG;
  NOTES_STATUS;
  DIALOG;

  notesContainer;
  filter;

  constructor() {
    super();
    this.filter = {};
    this.filter.order = 'pinUp';
  }

  async connectedCallback() {
    await this.build();
    this.initDialog();
  }

  async build() {
    // TRAER la cantidad de notas
    await this.getNotes();
    // MONTAR
    this.createApplication(AON_NOTES, MSG.NOTES, new AonApplication());
    // CABECERA
    this.buildToolbar();
    // Menu de la izquierda
    await this.buildSidenav();
    this.buildTagSidenav();
    // Listado de notas, Por lo visto si existe el boton de activas
    this.waitForElementToExist("aon-notesSidenavNotesOpen").then(
      (noteSidenavOpt) => {
        noteSidenavOpt.click();
        // Hide sideNav
        if (this.isMobile() || window.innerWidth < 730) {
          this.waitForElementToExist("aon-notesToolbarHeaderTitleSectionMenuIconButton").then((collapseMenuBtn) => {
            collapseMenuBtn.click();
          });
        }
      }
    );
  }

  initDialog() {
    if (!this.DIALOG) {
      const application = document.querySelector('aon-application');
      if (application) {
        this.DIALOG = document.getElementById(application.DIALOG);
      }
    }
  }

// ----------------------------- TOOLBAR
  buildToolbar() {
    // Filtro - Dialog de filtro
    let orderOpt = new AonDialogMenu();
    orderOpt.id  = "orderOptDialog";
    this.getApplication().appendChild(orderOpt);
    // Boton del dialog para el filtro
    this.getApplication().addToolbarOption2({
        id  : "Order",
        name: "Odernar",
        icon: MATERIAL_ICONS.FILTER_LIST
      },() => {
        this.showOrderDialog();
      });
    // Agregar
    this.getApplication().addToolbarOption2({
        id: MATERIAL_ICONS.ADD,
        name: "Nueva Nota",
        icon: MATERIAL_ICONS.ADD
      },() => {
        const titleInputId = this.addNote();
        this.waitForElementToExist(`${titleInputId}`).then((titleInput) => {
          titleInput.focus();
        });
      });
  }

  showOrderDialog(){
    
    console.log('showOrderDialog() - - - - - -');
    
    this.waitForElementToExist("aon-notesToolbarHeaderToolSectionOrderButtonIconButton").then(
      (button) => {
        let height = window.innerHeight;
        let top  = button.getBoundingClientRect().top;
        const left = button.getBoundingClientRect().left;

        if((height - top) < (height / 2)) {
          top = top - (ayudat ? 205 : 170);
        }

        let d = document.getElementById('orderOptDialog');

        const defaultOrder = {
          name: 'Por defecto',
          title:"Por defecto",
          icon: 'filter_list',
          fn: () => {
            this.filter.order = 'pinUp';
            this.aonNotes();
          }
        };

        const updateOrder = {
          name: 'Fecha modif.',
          title:"Fecha modificación",
          icon: 'filter_list',
          fn : () => {
            this.filter.order = 'modificationDate';
            this.aonNotes();
          }
        };

        const creationOrder = {
          name: "Fecha creación",
          title: "Fecha creación",
          icon: 'filter_list',
          fn :  () => {
            this.filter.order = 'creationDate';
            this.aonNotes();
          }
        };
        
        let options = [defaultOrder, updateOrder, creationOrder];
        d.setMenuOptions(options, top, left);
        d.open();
      });
  }

// ----------------------------- GET NOTES
  async getNotes(){
    await getNotes().then((notes) => {
      this.NOTES = notes;
      this.NOTES = sortBy(this.NOTES, 'subject');

      this.NOTES_TAG = {};
      this.NOTES.forEach((note) => {
        if(note.noteTag){
          var valor = note.noteTag;
          if (this.NOTES_TAG[valor]) {
            this.NOTES_TAG[valor]++;
          } else {
            this.NOTES_TAG[valor] = 1;
          }
        }
      });

      this.NOTES_TAG = Object.keys(this.NOTES_TAG) 
        .sort().reduce((temp_obj, key) => {
          temp_obj[key] = this.NOTES_TAG[key];
          return temp_obj;
        }, {});

      if(this.NOTES_TAG.length === 0 || !this.NOTES_TAG[this.filter.tag]){
        this.filter.tag = undefined;
      }

      let currentDate = new Date().getTime();
      this.NOTES_STATUS = {};
      this.NOTES_STATUS.all = this.NOTES.length;
      this.NOTES_STATUS.active = this.NOTES.filter((note) => note.archive === false && currentDate <= new Date(note.date).getTime()).length;
      this.NOTES_STATUS.expired = this.NOTES.filter((note) => note.archive === false && currentDate > new Date(note.date).getTime()).length;
      this.NOTES_STATUS.archived = this.NOTES.filter((note) => note.archive === true).length;
    });
  }

// ----------------------------- SIDENAV
  async buildSidenav() {
    // Notas
    await this.buildUtilitiesOptions();
    this.getApplication().addEventListener(EVENT.SELECT_OPTION, (e) =>
      // filtros de las notas
      this.selectOption(e.detail)
    );
  }
  // ----------------------------- SIDENAV (NOTES)
  async selectOption(option) {
    this.filter.all      = false;
    this.filter.active   = false;
    this.filter.expired  = false;
    this.filter.archived = false;
    switch (option.id) {
      case "NotesAll":
        this.filter.all = true;
        break;
      case "NotesOpen":
        this.filter.active = true;
        break;
      case "NotesExpired":
        this.filter.expired = true;
        break;
      case "NotesArchived":
        this.filter.archived = true;
        break;
    }
    this.aonNotes();
  }
  // Opciones de filtros en las notas
  async buildUtilitiesOptions() {
    let notesOpts = [
      {
        id: CONSTANT.NOTES.initCap() + "All",
        name: `Todas (${this.NOTES_STATUS.all})`,
        app: NOTES,
        fn: () => {
          // Filter selectOption method
        }
      },
      {
        id: CONSTANT.NOTES.initCap() + "Open",
        name: `Activas (${this.NOTES_STATUS.active})`,
        app: NOTES,
        fn: () => {
          // Filter selectOption method
        }
      },
      {
        id: CONSTANT.NOTES.initCap() + "Expired",
        name: `Expiradas (${this.NOTES_STATUS.expired})`,
        app: NOTES,
        fn: () => {
          // Filter selectOption method
        }
      },
      {
        id: CONSTANT.NOTES.initCap() + "Archived",
        name: `Archivadas (${this.NOTES_STATUS.archived})`,
        app: NOTES,
        fn: () => {
          // Filter selectOption method
        }
      }
    ];

    let options = {
      id: CONSTANT.NOTES.initCap(),
      name: MSG.STATUS.toUpperCase(),
      app: NOTES,
      options: notesOpts
    };

    this.getApplication().addSidenavOptions3(options);
  }
  // ----------------------------- SIDENAV (FIN - NOTES)

  // ----------------------------- SIDENAV (TAG)
  async buildTagSidenav() {
    let notesOpts = [];
    Object.keys(this.NOTES_TAG).forEach(key => {
      notesOpts.push({
        id: key,
        name: `${this.truncateKey(key)} (${this.NOTES_TAG[key] || '0'})`,
        title: `${key} (${this.NOTES_TAG[key] || '0'})`,
        actions: [
          {
            id: "Delete",
            icon: MATERIAL_ICONS.DELETE,
            action: () => this.deleteTag(key)
          },
          {
            id: "Edit",
            icon: MATERIAL_ICONS.EDIT,
            action: () => this.dialogTag(key)
          }
        ],
        fn: async () => {
          // Filter notes by tag
          this.filter.tag = this.filter.tag && this.filter.tag === key ? undefined : key;
          this.aonNotes();
        }
      });
    });
    
    // Solo si tenemos etiquetas
    if(notesOpts.length > 0){
      let options = {
        id: "Tags",
        name: MSG.TAG,
        app: NOTES,
        options: notesOpts
      };

      this.getApplication().addSidenavOptions3(options);
    }
  }
  // ----------------------------- SIDENAV (FIN - TAG)
// ----------------------------- FIN - SIDENAV

  truncateKey(value){
    if(value && value.length > 10){
      value = `${value.substring(0, 10)}...`;
    }
    return value;
  }

  deleteTag(noteTag) {
    this.DIALOG.clear();
    this.DIALOG.setTitle("Eliminar etiqueta");
    // Mensaje
    const textnode = document.createTextNode(
      `Existe al menos una nota con esta etiqueta asignada. ¿Realmente desea eliminar la etiqueta '${noteTag}'?`
    );
    this.DIALOG.setContent(textnode);
    // Cancelar
    this.DIALOG.addCancelAction();
    // Aceptar
    this.DIALOG.addSendAction(async () => {
      let data = {};
      data.noteTag = noteTag;
      await deleteNoteTag(data);
      if(this.filter.tag && this.filter.tag === noteTag){
        this.filter.tag = undefined;
      }
      await this.reloadNotes();
    }, "delete");
    this.DIALOG.open();
  }

  dialogTag(noteTag) {
    this.DIALOG.clear();
    this.DIALOG.setTitle("Editar etiqueta");
    
    // Create name input
    let nameInput   = createInput('nameInput', 'Nombre');
    nameInput.type  = 'text';
    nameInput.id    = "dialogTag";
    nameInput.value = noteTag ?? '';
    nameInput.setAttribute("maxLength", 17);
    this.DIALOG.setContent(nameInput);
    nameInput.focus();
    // Cancelar
    this.DIALOG.addCancelAction();
    // Aceptar
    this.DIALOG.addSendAction(async () => {
      let data = {};
      data.noteTagOld = noteTag;
      data.noteTagNew = nameInput.value;
      await saveNoteTag(data);
      await this.reloadNotes();
    }, "save");
    
    this.DIALOG.open();
  }

  // ----------------------------- NOTES
  aonNotes() {
    this.notesContainer = this.createElement(TAG.DIV);
    this.notesContainer.id = "notesContainer";
    this.notesContainer.className = CSS.NOTE_CONTAINER;
    this.getApplication().setContent(this.notesContainer);

    let emptyNotes = this.createElement(TAG.DIV);
    emptyNotes.id = "notesEmpty";
    emptyNotes.className = CSS.EMPTY_NOTE;
    emptyNotes.innerHTML = "No existen notas disponibles";
    this.notesContainer.appendChild(emptyNotes);
    emptyNotes.style.display = "none";

    let filteredNotes = this.filterNotes();
    filteredNotes = filteredNotes.sort((a, b) => 
      (a[this.filter.order] > b[this.filter.order]) ? 1 : 
      (a[this.filter.order] === b[this.filter.order]) ? ((a.modificationDate > b.modificationDate) ? 1 : (a.modificationDate === b.modificationDate) ? ((a.subject > b.subject) ? -1 : 1) : -1) : -1 );

    if (!filteredNotes || filteredNotes.length === 0) {
      emptyNotes.style.display = "block";
    } else {
      filteredNotes.forEach((noteJson) => {
        let note = new Note(noteJson);
        this.addNote(note);
      });
    }

    this.addFilterSelection();
    if(this.filter.tag){ 
      this.updateTagSideNavCount();
    } else {
      this.updateSideNavCount();
    }
  }

  filterNotes() {
    let currentDate = new Date().getTime();
    let filteredNotes;
    if (this.filter.active) {
      filteredNotes = this.NOTES.filter(
        (note) => !note.archive && currentDate <= new Date(note.date).getTime()
      );
    } else if (this.filter.expired) {
      filteredNotes = this.NOTES.filter(
        (note) => !note.archive && currentDate > new Date(note.date).getTime()
      );
    } else if (this.filter.archived) {
      filteredNotes = this.NOTES.filter((note) => note.archive);
    } else {
      filteredNotes = this.NOTES;
    }

    if(this.filter.tag){
      filteredNotes = filteredNotes.filter((note) => note.noteTag === this.filter.tag);
    }

    return filteredNotes;
  }

  addNote(note) {
    try {
      document.getElementById("notesEmpty").style.display = "none";
    } catch (e) {}

    if (!note) {
      note = new Note();
      note.setId(Math.floor((Math.random() - 1) * 100));
      note.setCreationDate(new Date());
    }

    let noteCard = this.createElement(TAG.DIV);
    noteCard.id = note.getId() + "Card";
    noteCard.className = CSS.NOTE_CARD;
    if(note.color){
      noteCard.style.backgroundColor = note.color;
    }
    // Title
    let noteCardTitleDiv = this.createElement(TAG.DIV);
    noteCardTitleDiv.className = CSS.NOTE_TITLE_CARD;
    noteCard.appendChild(noteCardTitleDiv);

    //let noteCardTitle = this.createElement(TAG.INPUT);
    let noteCardTitle = new AonNewInput();
    noteCardTitle.id = note.getId() + "Title";
    noteCardTitle.type = "text";
    noteCardTitle.title = MSG.TITLE;
    noteCardTitle.className = CSS.NOTE_TITLE;
    noteCardTitle.value = note.getSubject() ? note.getSubject() : "";
    noteCardTitleDiv.appendChild(noteCardTitle);

    if(note.getPinUp()){
      const pinUpTitleButton = this.createNoteButton(AON_ICONS.AON_PIN_OFF, "Liberar", async (ev) => {
        note.setPinUp(!note.getPinUp());
        await this.saveNote(note);
        await this.reloadNotes();
      });
      pinUpTitleButton.id = "pinUpTitleButton";
      noteCardTitleDiv.appendChild(pinUpTitleButton);
    }

    // Body
    let noteCardBodyDiv = this.createElement(TAG.DIV);
    noteCardBodyDiv.className = CSS.NOTE_BODY_CONTENT;
    noteCard.appendChild(noteCardBodyDiv);

    let textAreaBody = this.createElement("textarea");
    textAreaBody.placeholder = MSG.WRITE_A_NOTE;
    textAreaBody.rows = "6";
    textAreaBody.className = CSS.NOTE_BODY;
    textAreaBody.value = note.getNote() ? note.getNote() : "";
    noteCardBodyDiv.appendChild(textAreaBody);

    // Date
    let noteCardDate = this.createElement(TAG.INPUT);
    noteCardDate.type = "date";
    noteCardDate.id = note.getId() + "Date";
    noteCardDate.className = CSS.NOTE_DATE;
    noteCardDate.style.display = "none";

    // Bottom
    let bottomNoteDiv = this.createElement(TAG.DIV);
    bottomNoteDiv.classList.add("note-footer");
    noteCardBodyDiv.appendChild(bottomNoteDiv);

    let noteTagDiv = this.createElement(TAG.SMALL);
    noteTagDiv.classList.add("note-tags");
    bottomNoteDiv.appendChild(noteTagDiv);

    if(note.getNoteTag()){
      noteTagDiv.innerHTML = note.getNoteTag();
    } else 
      noteTagDiv.classList.add("hidden");

    let buttonsNoteDiv = this.createElement(TAG.DIV);
    buttonsNoteDiv.classList.add("note-buttons", "hidden");
    bottomNoteDiv.appendChild(buttonsNoteDiv);

    // Delete
    let deleteButton = this.createNoteButton(MATERIAL_ICONS.DELETE, "Eliminar nota", () => {
      this.DIALOG.clear();
      this.DIALOG.setTitle("Eliminar nota");
      // Mensaje
      const textnode = document.createTextNode(
        `¿Desea eliminar la nota '${note.getSubject()}'?`
      );
      this.DIALOG.setContent(textnode);
      // Cancelar
      this.DIALOG.addCancelAction();
      // Aceptar
      this.DIALOG.addSendAction(async () => {
        await deleteNote(note);
        await this.reloadNotes();
      }, "delete");
      this.DIALOG.open();
    });
    buttonsNoteDiv.appendChild(deleteButton);

    let pinUpButton = this.createNoteButton(MATERIAL_ICONS.PUSH_PIN, note.getPinUp() ? "Liberar" : "Fijar", async () => {
      note.setPinUp(!note.getPinUp());
      await this.saveNote(note);
      await this.reloadNotes();
    });
    if(!note.getPinUp()){
      buttonsNoteDiv.appendChild(pinUpButton);
    }

    let colorButton = this.createNoteButton(MATERIAL_ICONS.PALETTE, "Color", (ev) => {
      this.createColorsSelection(ev, note, buttonsNoteDiv);
    });
    buttonsNoteDiv.appendChild(colorButton);

    let alarmButton = this.createNoteButton(MATERIAL_ICONS.NOTIFICATION_ADD, "Fecha", () => {
      noteCardDate.style.display = "block";
      const date = document.getElementById(note.getId() + "Date");
      date.showPicker();
    });
    buttonsNoteDiv.appendChild(alarmButton);

    let archiveButton = this.createNoteButton(
      note.getArchive() ? MATERIAL_ICONS.OUTBOX : MATERIAL_ICONS.MOVE_TO_INBOX,
      note.getArchive() ? "Desarchivar" : "Archivar",
      async () => {
        note.setDate(undefined);
        note.setArchive(!note.getArchive());
        note.setArchiveDate(note.getArchive() ? new Date() : null);
        await this.saveNote(note);
        await this.reloadNotes();
      }
    );
    buttonsNoteDiv.appendChild(archiveButton);

    let tagButton = this.createNoteButton(MATERIAL_ICONS.LABEL, "Etiqueta", () => {
      this.DIALOG.clear();
      this.DIALOG.setTitle("Asignar etiqueta");
      // Create name input
      let tagInputDiv     = this.createElement(TAG.DIV);
      let selectInputDic  = this.createElement(TAG.INPUT);
      selectInputDic.type = "text";
      selectInputDic.placeholder = "Introduce la etiqueta";
      selectInputDic.setAttribute("list", "tagDatalist");
      selectInputDic.setAttribute("maxLength", 17);
      selectInputDic.id = "tagInputDialog";
      selectInputDic.value = note.getNoteTag();
      tagInputDiv.appendChild(selectInputDic);

      let datalist = this.createElement("datalist");
      datalist.id = "tagDatalist";
      tagInputDiv.appendChild(datalist);

      Object.keys(this.NOTES_TAG).forEach(tag => {
        let optionSelectDic = this.createElement("option");
        optionSelectDic.innerText = tag;
        optionSelectDic.value = tag;
        datalist.appendChild(optionSelectDic);
      });

      let tagSpantDic = this.createElement(TAG.SPAN);
      tagSpantDic.innerText = "Etiqueta";
      tagInputDiv.appendChild(tagSpantDic);

      this.DIALOG.setContent(tagInputDiv);
      // Cancelar
      this.DIALOG.addCancelAction();
      // Aceptar
      this.DIALOG.addSendAction(async () => {
        note.noteTag = selectInputDic.value;
        await this.saveNote(note);
        await this.reloadNotes();
      }, "save");

      this.DIALOG.open();
    });
    buttonsNoteDiv.appendChild(tagButton);

    // Check if its expired
    if(note.getDate() && !note.getArchive() && new Date(note.getDate()).getTime() !== new Date("9999-01-01").getTime()){
      noteCardDate.value = note.getDate();
      noteCardDate.style.display = "block";
    } else if(note.getArchive() && new Date(note.getArchiveDate()).getTime() !== new Date("9999-01-01").getTime()){
      noteCardDate.value = note.getArchiveDate();
      noteCardDate.style.display = "block";
    } 

    if (note.getDate()) {
      let date = new Date();
      let dateNote = new Date(note.getDate());
      if (dateNote.getTime() < date.getTime()) noteCardDate.style.color = "#e1444c";
    }
    
    bottomNoteDiv.appendChild(noteCardDate);

    if(note.getArchive()){
      alarmButton.style.display = "none";
    }

    // EVENTS
    noteCard.addEventListener("mouseover",(event) => {
        buttonsNoteDiv.classList.remove("hidden");
        noteTagDiv.classList.add("hidden");
      },false,
    );

    noteCard.addEventListener("mouseout",(event) => {
        buttonsNoteDiv.classList.add("hidden");
        if (noteTagDiv.children.length > 0 || noteTagDiv.textContent.trim() !== "") {
          noteTagDiv.classList.remove("hidden");
        }
      },false,
    );

    noteCardTitle.addEventListener("change", async ({ target }) => {
      note.setSubject(target.value);
      await this.saveNote(note);
      await this.reloadNotes();
    });

    textAreaBody.addEventListener("change", async ({ target }) => {
      note.setNote(target.value);
      await this.saveNote(note);
      await this.reloadNotes();
    });

    noteCardDate.addEventListener("change", async ({ target }) => {
      note.setDate(target.value);

      if (note.getDate()) {
        let date = new Date();
        let dateNote = new Date(note.getDate());
        if (dateNote.getTime() < date.getTime())
          noteCardDate.style.color = "#e1444c";
        else noteCardDate.style.color = "#787885";
      }

      await this.saveNote(note);
      await this.reloadNotes();
    });

    let aonDialogMenu = new AonDialogMenu();
    aonDialogMenu.id = note.getId() + "Options";
    noteCard.appendChild(aonDialogMenu);

    this.notesContainer.prepend(noteCard);

    return note.getId() + "Title";
  }

  createColorsSelection(ev, note, buttonsPanel){
    let colorsDiv = this.createElement(TAG.DIV);
    colorsDiv.style.backgroundColor = "white";
    colorsDiv.style.borderRadius = "15px";
    colorsDiv.style.display = "flex";
    colorsDiv.style.gap = ".5rem";
    colorsDiv.style.flexWrap = "wrap";
    colorsDiv.style.width = "10rem";
    colorsDiv.style.position = "absolute";
    colorsDiv.style.top = ev.clientY - 135;
    colorsDiv.style.left = ev.clientX - 310;
    colorsDiv.style.zIndex = "1";
    colorsDiv.style.padding = ".5rem";
    colorsDiv.style.cursor = "pointer";
    buttonsPanel.appendChild(colorsDiv);

    colorsDiv.addEventListener("mouseleave", (event) => {
      event.preventDefault();
      buttonsPanel.removeChild(colorsDiv);
    });

    let aviableColors = [
      '#faafa8',
      '#f39f76',
      '#fff8b8',
      '#e2f6d3',
      '#b4ddd3',
      '#d4e4ed',
      '#aeccdc',
      '#d3bfdb',
      '#f6e2dd',
      '#e9e3d4',
      '#efeff1',
      '#fff'
    ]

    aviableColors.forEach(color => {
      let colorDiv = this.createElement(TAG.DIV);
      colorDiv.style.width = "1rem";
      colorDiv.style.height = "1rem";
      colorDiv.style.borderRadius = "50%";
      colorDiv.style.borderColor = color;
      colorDiv.style.backgroundColor = color;

      if(color === "#fff"){
        colorDiv.style.border = "1px solid #ddd";
      }

      colorDiv.addEventListener("click", async (event) => {
        event.preventDefault();
        buttonsPanel.removeChild(colorsDiv);
        note.color = color;
        await this.saveNote(note);
        await this.reloadNotes();
      });

      colorsDiv.appendChild(colorDiv);
    });
  }

  createNoteButton(icon, title, fn){
    let button     = new AonIconButton();
    button.noHover = true;
    button.icon    = icon;
    button.title   = title;
        
    button.addEventListener(EVENT.CLICK, (ev)=>{
      ev.stopPropagation();
      fn(ev);
    });

    return button;
  }

  async saveNote(note) {
    note.setModificationDate(new Date());
    if(note.getId() < 0){
      note.setCreationDate(new Date());
    }
    const { id } = await saveNote(note);
  }

  // ----------------------------- SIDENAV (COUNTS)

  async updateSideNavCount(){
    let currentDate = new Date().getTime();

    document.getElementById("aon-notesSidenavNotesAll").childNodes[0].innerText =
      `Todas (${
        this.NOTES.length
      })`;

    document.getElementById("aon-notesSidenavNotesOpen").childNodes[0].innerText =
      `Activas (${
        this.NOTES.filter((note) => !note.archive && currentDate <= new Date(note.date).getTime()).length
      })`;

    document.getElementById("aon-notesSidenavNotesExpired").childNodes[0].innerText =
      `Expiradas (${
        this.NOTES.filter((note) => !note.archive && currentDate > new Date(note.date).getTime()).length
      })`;
    
    document.getElementById("aon-notesSidenavNotesArchived").childNodes[0].innerText =
      `Archivadas (${
        this.NOTES.filter((note) => note.archive).length
      })`;
  }

  async updateTagSideNavCount(){
    let currentDate = new Date().getTime();

    document.getElementById("aon-notesSidenavNotesAll").childNodes[0].innerText =
      `Todas (${
        this.NOTES.filter((note) => note.noteTag === this.filter.tag).length
      })`;

    document.getElementById("aon-notesSidenavNotesOpen").childNodes[0].innerText =
      `Activas (${
        this.NOTES.filter((note) => !note.archive && currentDate <= new Date(note.date).getTime() && note.noteTag === this.filter.tag).length
      })`;

    document.getElementById("aon-notesSidenavNotesExpired").childNodes[0].innerText =
      `Expiradas (${
        this.NOTES.filter((note) => !note.archive && currentDate > new Date(note.date).getTime() && note.noteTag === this.filter.tag).length
      })`;
    
    document.getElementById("aon-notesSidenavNotesArchived").childNodes[0].innerText =
      `Archivadas (${
        this.NOTES.filter((note) => note.archive && note.noteTag === this.filter.tag).length
      })`;
  }

  // ----------------------------- SIDENAV (COLORS)

  addFilterSelection(){
    this.getApplication().removeBackgroundSidenavAll(NOTES.color);

    if(this.filter.all){
      this.getApplication().addBackgroundSidenav('NotesAll', NOTES.color);
    } else if(this.filter.active){
      this.getApplication().addBackgroundSidenav('NotesOpen', NOTES.color);
    } else if(this.filter.expired){
      this.getApplication().addBackgroundSidenav('NotesExpired', NOTES.color);
    } else if(this.filter.archived){
      this.getApplication().addBackgroundSidenav('NotesArchived', NOTES.color);
    }

    if(this.filter.tag){
      this.getApplication().addBackgroundSidenav(this.filter.tag, NOTES.color);
    }
  }

  // ----------------------------- RELOAD NOTES
  async reloadNotes(){
    await this.getNotes();

    this.getApplication().removeSidenavById("Notes");
    this.getApplication().removeSidenavById("Tags");

    this.buildSidenav();
    this.buildTagSidenav();
    this.aonNotes();
  }

  waitForElementToExist(selector) {
    return new Promise((resolve) => {
      if (document.getElementById(selector)) {
        return resolve(document.getElementById(selector));
      }

      const observer = new MutationObserver(() => {
        if (document.getElementById(selector)) {
          resolve(document.getElementById(selector));
          observer.disconnect();
        }
      });

      observer.observe(document.body, {
        subtree: true,
        childList: true,
      });
    });
  }
}

if (!window.customElements.get(TAG.AON_NOTES)) {
  window.customElements.define(TAG.AON_NOTES, AonNotes);
}

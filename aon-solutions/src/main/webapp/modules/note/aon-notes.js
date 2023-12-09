import { AonElement } from "../../components/AonElement.js";
import { AonApplication } from "../../components/aon-application.js";
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments.js";
import Apps, { NOTES } from "../../services/app.js";
import { AON_NOTES } from "../../environments/aonTag.js";
import { getNotes, saveNote, deleteNote, saveNoteTag, deleteNoteTag } from "../../services/noteService.js";
import { AonIconButton } from "../../components/aon-icon-button.js";
import { Note } from "../../models/note/Note.js";
import { AonDialogMenu } from "../../components/aon-dialog-menu.js";
import { AonNewDialog } from "../../components/aon-new-dialog.js";
import { AonNewInput } from "../../components/aon-new-input.js";
import { sortBy } from "../../services/utils.js";

export class AonNotes extends AonElement {
  NOTES;
  NOTES_TAG;
  NOTES_STATUS;

  notesContainer;
  filter;

  constructor() {
    super();
    this.filter = {};
    this.filter.order = 'pinUp';
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {}

  async build() {
    this.createApplication(AON_NOTES, MSG.NOTES, new AonApplication());
    this.buildToolbar();

    await this.getNotes();
    
    this.buildSidenav();
    this.buildTagSidenav();

    this.waitForElementToExist("aon-notesSidenavNotesOpen").then(
      (noteSidenavOpt) => {
        noteSidenavOpt.click();

        // Hide sideNav
        if (this.isMobile() || window.innerWidth < 730) {
          this.waitForElementToExist(
            "aon-notesToolbarHeaderTitleSectionMenuIconButton"
          ).then((collapseMenuBtn) => {
            collapseMenuBtn.click();
          });
        }
      }
    );
  }

  // ----------------------------- TOOLBAR

  buildToolbar() {
    this.getApplication().addToolbarOption2(
      {
        name: "Odernar",
        icon: MATERIAL_ICONS.FILTER_LIST,
        id: "Order",
      },
      () => {
        this.showOrderDialog();
      }
    );

    this.getApplication().addToolbarOption2(
      {
        name: "Nueva Nota",
        icon: MATERIAL_ICONS.ADD,
        id: MATERIAL_ICONS.ADD,
      },
      () => {
        const titleInputId = this.addNote();
        this.waitForElementToExist(`${titleInputId}`).then((titleInput) => {
          titleInput.focus();
        });
      }
    );

    let orderOpt = new AonDialogMenu();
    orderOpt.id = "orderOptDialog";
    this.getApplication().appendChild(orderOpt);

  }

  showOrderDialog(){

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

  buildSidenav() {
    this.getApplication().addEventListener(EVENT.SELECT_OPTION, (e) =>
      this.selectOption(e.detail)
    );

    this.buildUtilitiesOptions();
  }

  async selectOption(option) {
    switch (option.id) {
      case "NotesAll":
        this.filter.all = true;
        this.filter.active = false;
        this.filter.expired = false;
        this.filter.archived = false;
        this.aonNotes();
        break;
      case "NotesOpen":
        this.filter.all = false;
        this.filter.active = true;
        this.filter.expired = false;
        this.filter.archived = false;
        this.aonNotes();
        break;
      case "NotesExpired":
        this.filter.all = false;
        this.filter.active = false;
        this.filter.expired = true;
        this.filter.archived = false;
        this.aonNotes();
        break;
      case "NotesArchived":
        this.filter.all = false;
        this.filter.active = false;
        this.filter.expired = false;
        this.filter.archived = true;
        this.aonNotes();
        break;
      default:
        break;
    }
  }

  // ----------------------------- SIDENAV (NOTES)

  buildUtilitiesOptions() {
    let notesOpts = [];
    notesOpts.push(
      {
        id: CONSTANT.NOTES.initCap() + "All",
        name: `Todas (${this.NOTES_STATUS.all})`,
        icon: MATERIAL_ICONS.ALL_INBOX,
        app: NOTES,
        fn: () => {
          // Filter selectOption method
        },
      },
      {
        id: CONSTANT.NOTES.initCap() + "Open",
        name: `Activas (${this.NOTES_STATUS.active})`,
        icon: MATERIAL_ICONS.NOTES,
        app: NOTES,
        fn: () => {
          // Filter selectOption method
        },
      },
      {
        id: CONSTANT.NOTES.initCap() + "Expired",
        name: `Expiradas (${this.NOTES_STATUS.expired})`,
        icon: MATERIAL_ICONS.SCHEDULE,
        app: NOTES,
        fn: () => {
          // Filter selectOption method
        },
      },
      {
        id: CONSTANT.NOTES.initCap() + "Archived",
        name: `Archivadas (${this.NOTES_STATUS.archived})`,
        icon: MATERIAL_ICONS.INBOX,
        app: NOTES,
        fn: () => {
          // Filter selectOption method
        },
      }
    );

    let options = {
      id: CONSTANT.NOTES.initCap(),
      name: MSG.STATUS.toUpperCase(),
      app: NOTES,
    };

    this.getApplication().addSidenavOptions2(options, notesOpts);
  }

  // ----------------------------- SIDENAV (TAG)

  async buildTagSidenav() {
    let notesOpts = [];
    Object.keys(this.NOTES_TAG).forEach(key => {
      notesOpts.push({
        id: key,
        name: `${key} (${this.NOTES_TAG[key] || '0'})`,
        icon: MATERIAL_ICONS.LABEL,
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
          },
        ],
        fn: async () => {
          // Filter notes by tag
          if(this.filter.tag && this.filter.tag === key){
            this.filter.tag = undefined;
          } else {
            this.filter.tag = key;
          }
          this.aonNotes();
        },
      });
    });

    let options = {
      id: "Tags",
      name: MSG.TAG,
       app: NOTES,
    };

    this.getApplication().addSidenavOptions2(options, notesOpts);
  }

  deleteTag(noteTag) {
    let aonDeleteTagDialog = new AonNewDialog();
    aonDeleteTagDialog.id = noteTag + "Delete";
    this.getApplication().appendChild(aonDeleteTagDialog);

    aonDeleteTagDialog.createMessage(
      `Existe al menos una nota con esta etiqueta asignada. ¿Realmente desea eliminar la etiqueta '${noteTag}'?`
    );

    aonDeleteTagDialog.createAcceptButton(async () => {
      let data = {};
      data.noteTag = noteTag;
      await deleteNoteTag(data);
      this.getApplication().removeChild(aonDeleteTagDialog);
      if(this.filter.tag && this.filter.tag === noteTag){
        this.filter.tag = undefined;
      }
      await this.reloadNotes();
    });

    aonDeleteTagDialog.createCancelButton(() => {
      this.getApplication().removeChild(aonDeleteTagDialog);
    });

    this.waitForElementToExist(`${noteTag}DeleteAcceptButton`).then((acceptButton) => {
      acceptButton.focus();
    });
  }

  dialogTag(noteTag) {
    let aonCreateUpdateTagDialog = new AonNewDialog("Etiqueta");
    aonCreateUpdateTagDialog.id = "createUpdateTagDialog";
    this.getApplication().appendChild(aonCreateUpdateTagDialog);

    // Create name input
    let nameInput = this.createAonElement(new AonNewInput(), 'nameInput', 'Nombre');
    nameInput.type = 'text';
    nameInput.setAttribute("maxLength", 11);
    nameInput.id = "dialogTag"
    if (noteTag) nameInput.value = noteTag;

    // Add dialog body
    aonCreateUpdateTagDialog.createBody(nameInput);
    nameInput.focus();

    aonCreateUpdateTagDialog.createAcceptButton(async () => {
      let data = {};
      data.noteTagOld = noteTag;
      data.noteTagNew = nameInput.value;
      await saveNoteTag(data);
      this.getApplication().removeChild(aonCreateUpdateTagDialog);
      await this.reloadNotes();
    });

    aonCreateUpdateTagDialog.createCancelButton(() => {
      this.getApplication().removeChild(aonCreateUpdateTagDialog);
    });

    this.waitForElementToExist(`dialogTagInput`).then((input) => {
      input.setAttribute("maxLength", 11);
      input.focus();
    });
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
    if(this.filter.tag){ this.updateTagSideNavCount(); } 
    else { this.updateSideNavCount(); }
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

    // Title
    let noteCardTitleDiv = this.createElement(TAG.DIV);
    noteCardTitleDiv.className = CSS.NOTE_TITLE_CARD;
    if(note.color){
      noteCardTitleDiv.style.backgroundColor = note.color;
    } else {
      noteCardTitleDiv.style.backgroundColor = "white";
    }
    noteCard.appendChild(noteCardTitleDiv);

    let noteCardTitle = this.createElement(TAG.INPUT);
    noteCardTitle.id = note.getId() + "Title";
    noteCardTitle.type = "text";
    noteCardTitle.maxlength = 60;
    noteCardTitle.placeholder = MSG.TITLE;
    noteCardTitle.className = CSS.NOTE_TITLE;
    noteCardTitle.value = note.getSubject() ? note.getSubject() : "";
    noteCardTitleDiv.appendChild(noteCardTitle);

    let pinUpTitleButton = this.createNoteButton(
      MATERIAL_ICONS.PUSH_PIN,
      "Liberar",
      async () => {
        note.setPinUp(!note.getPinUp());
        await this.saveNote(note);
        await this.reloadNotes();
      }
    );
    if(note.getPinUp()){
      noteCardTitleDiv.appendChild(pinUpTitleButton);
    }


     // Body
    let noteCardBodyDiv = this.createElement(TAG.DIV);
    noteCardBodyDiv.className = CSS.NOTE_BODY_CONTENT;
    noteCard.appendChild(noteCardBodyDiv);

    let textAreaBody = this.createElement("textarea");
    textAreaBody.placeholder = MSG.WRITE_A_NOTE;
    textAreaBody.rows = "4";
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
    bottomNoteDiv.style.width = "100%";
    bottomNoteDiv.style.display = "flex";
    bottomNoteDiv.style.justifyContent = "space-between";
    bottomNoteDiv.style.alignItems = "center";
    noteCardBodyDiv.appendChild(bottomNoteDiv);

    let noteTagDiv = this.createElement(TAG.DIV);
    noteTagDiv.style.display = "none";
    noteTagDiv.style.padding = ".5rem";
    noteTagDiv.style.backgroundColor = "#ddd";
    noteTagDiv.style.borderRadius = "15px";
    noteTagDiv.style.height = "30px";
    noteTagDiv.style.display = "block";
    noteTagDiv.style.fontSize = ".7rem";
    bottomNoteDiv.appendChild(noteTagDiv);

    if(note.getNoteTag()){
      noteTagDiv.innerHTML = note.getNoteTag();
    } else noteTagDiv.style.visibility = "hidden";

    let buttonsNoteDiv = this.createElement(TAG.DIV);
    buttonsNoteDiv.style.display = "none";
    bottomNoteDiv.appendChild(buttonsNoteDiv);

    // Delete
    let deleteButton = this.createNoteButton(
      MATERIAL_ICONS.DELETE,
      "Eliminar nota",
      () => {
        let aonDeleteDialog = new AonNewDialog();
        aonDeleteDialog.id = note.getId();
        this.getApplication().appendChild(aonDeleteDialog);

        aonDeleteDialog.createMessage(`¿Desea eliminar la nota '${note.getSubject()}'?`);

        aonDeleteDialog.createAcceptButton(async () => {
          await deleteNote(note);
          this.getApplication().removeChild(aonDeleteDialog);
          await this.reloadNotes();
        });

        aonDeleteDialog.createCancelButton(() => {
          this.getApplication().removeChild(aonDeleteDialog);
        });

        this.waitForElementToExist(`${note.getId()}AcceptButton`).then(
          (acceptButton) => {
            acceptButton.focus();
          }
        );
      }
    )
    buttonsNoteDiv.appendChild(deleteButton);

    let pinUpButton = this.createNoteButton(
      MATERIAL_ICONS.PUSH_PIN,
      note.getPinUp() ? "Liberar" : "Fijar",
      async () => {
        note.setPinUp(!note.getPinUp());
        await this.saveNote(note);
        await this.reloadNotes();
      }
    );
    buttonsNoteDiv.appendChild(pinUpButton);

    let colorButton = this.createNoteButton(
      MATERIAL_ICONS.PALETTE,
      "Color",
      (ev) => {
        this.createColorsSelection(ev, note, buttonsNoteDiv);
      }
    );
    buttonsNoteDiv.appendChild(colorButton);

    let alarmButton = this.createNoteButton(
      MATERIAL_ICONS.NOTIFICATION_ADD,
      "Fecha",
      () => {
        noteCardDate.style.display = "block";
        const date = document.getElementById(note.getId() + "Date");
        date.showPicker();
      }
    );
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

    let tagButton = this.createNoteButton(
      MATERIAL_ICONS.LABEL,
      "Etiqueta",
      () => {
        let aonTagDialog = new AonNewDialog("Asignar etiqueta");
        aonTagDialog.id = note.getId() + "Tag";
        this.getApplication().appendChild(aonTagDialog);

        // Create name input
        let tagInputDiv = this.createElement(TAG.DIV);
        tagInputDiv.style.height = "50px";
        tagInputDiv.style.border = "1px solid #d2d2d6";
        tagInputDiv.style.borderRadius = "3px";
        tagInputDiv.style.position = "relative";

        let selectInputDic = this.createElement(TAG.INPUT);
        selectInputDic.type = "text";
        selectInputDic.placeholder = "Introduce la etiqueta";
        selectInputDic.setAttribute("list", "tagDatalist");
        selectInputDic.style.width = "100%";
        selectInputDic.style.height = "100%";
        selectInputDic.style.background = "none";
        selectInputDic.style.border = "none";
        selectInputDic.style.paddingLeft = "1rem";
        selectInputDic.setAttribute("maxLength", 11);
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
        tagSpantDic.style.top = "-8px";
        tagSpantDic.style.padding = "0 5px";
        tagSpantDic.style.background = "white";
        tagSpantDic.style.position = "absolute";
        tagSpantDic.style.left = "10px";
        tagSpantDic.style.fontSize = "12px";
        tagInputDiv.appendChild(tagSpantDic);
        
        aonTagDialog.createBody(tagInputDiv);

        aonTagDialog.createAcceptButton(async () => {
          this.getApplication().removeChild(aonTagDialog);
          note.noteTag = selectInputDic.value;
          await this.saveNote(note);
          await this.reloadNotes();
        });

        aonTagDialog.createCancelButton(() => {
          this.getApplication().removeChild(aonTagDialog);
        });

        this.waitForElementToExist(`tagInputDialog`).then(
          (input) => {
            input.focus();
          }
        );
      }
    );
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

    if(note.getColor()){
      noteCardBodyDiv.style.backgroundColor = note.getColor();
      textAreaBody.style.backgroundColor = note.getColor();
      noteCardDate.style.backgroundColor = note.getColor();
    }
    else {
      noteCardBodyDiv.style.backgroundColor = "white";
      textAreaBody.style.backgroundColor = "white";
      noteCardDate.style.backgroundColor = "white";
    }

    // EVENTS
    noteCard.addEventListener(
      "mouseover",
      (event) => {
        buttonsNoteDiv.style.display = "flex";
        noteTagDiv.style.display = "none";
      },
      false,
    );

    noteCard.addEventListener(
      "mouseout",
      (event) => {
        buttonsNoteDiv.style.display = "none";
        noteTagDiv.style.display = "block";
      },
      false,
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
    let button = new AonIconButton();
    button.noHover = true;
    button.icon = icon;
    button.title = title;
    
    let innerButton = button.getButton();
    innerButton.style.height = "30px";
    innerButton.style.minWidth = "30px";
    innerButton.style.width = "30px";

    let innerIcon = button.getIcon();
    innerIcon.style.fontSize = "1.3rem";
    
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

    document.getElementById("aon-notesSidenavNotesAll").childNodes[1].innerText =
      `Todas (${
        this.NOTES.length
      })`;

    document.getElementById("aon-notesSidenavNotesOpen").childNodes[1].innerText =
      `Activas (${
        this.NOTES.filter((note) => !note.archive && currentDate <= new Date(note.date).getTime()).length
      })`;

    document.getElementById("aon-notesSidenavNotesExpired").childNodes[1].innerText =
      `Expiradas (${
        this.NOTES.filter((note) => !note.archive && currentDate > new Date(note.date).getTime()).length
      })`;
    
    document.getElementById("aon-notesSidenavNotesArchived").childNodes[1].innerText =
      `Archivadas (${
        this.NOTES.filter((note) => note.archive).length
      })`;
  }

  async updateTagSideNavCount(){
    let currentDate = new Date().getTime();

    document.getElementById("aon-notesSidenavNotesAll").childNodes[1].innerText =
      `Todas (${
        this.NOTES.filter((note) => note.noteTag === this.filter.tag).length
      })`;

    document.getElementById("aon-notesSidenavNotesOpen").childNodes[1].innerText =
      `Activas (${
        this.NOTES.filter((note) => !note.archive && currentDate <= new Date(note.date).getTime() && note.noteTag === this.filter.tag).length
      })`;

    document.getElementById("aon-notesSidenavNotesExpired").childNodes[1].innerText =
      `Expiradas (${
        this.NOTES.filter((note) => !note.archive && currentDate > new Date(note.date).getTime() && note.noteTag === this.filter.tag).length
      })`;
    
    document.getElementById("aon-notesSidenavNotesArchived").childNodes[1].innerText =
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

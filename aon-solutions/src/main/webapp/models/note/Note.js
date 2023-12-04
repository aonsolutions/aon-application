import { AonDateUtils } from "../../modules/utils/AonDateUtils.js";
import { Domain } from "../Domain.js";

export class Note {
  id;
  domain;
  subject;
  date;
  owner;
  note;
  archive;
  tag;

  constructor(note) {
    if(note) {
      this.id          = note.id || undefined;
      this.domain      = note.domain || new Domain().getId();
      this.subject     = note.subject || "";
      this.note        = note.note || "";
      this.owner       = note.owner || undefined;
      this.archive     = note.archive || false;
      this.date        = note.date && !note.date.includes("0001-01-01") ? AonDateUtils.formatDateOrigin(note.date) : null;
      this.tag         = note.tag || {};
    } else {
      this.id          = undefined;
      this.domain      = new Domain().getId(); 
      this.subject     = "";
      this.note        = undefined;
      this.owner       = undefined;
      this.date        = null;
      this.archive     = false;
      this.tag         = {};
    }
  }

  getId() {
    return this.id;
  }

  setId(id) {
    this.id = id;
  }

  getSubject() {
    return this.subject;
  }

  setSubject(subject) {
    this.subject = subject;
  }

  getDate() {
    return this.date;
  }

  setDate(date) {
    this.date = date;
  }

  getOwner() {
    return this.owner;
  }

  setOwner(owner) {
    this.owner = owner;
  }

  getNote() {
    return this.note;
  }

  setNote(note) {
    this.note = note;
  }

  getDomain() {
    return this.domain;
  }

  setDomain(domain) {
    this.domain = domain;
  }

  getArchive() {
    return this.archive;
  }

  setArchive(archive) {
    this.archive = archive;
  }

}


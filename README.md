# Lexical Analyzer Compiler - Pascal Language

This repository contains a lexical analyzer implemented in Python as part of an academic project for the **Compilers** course. The goal of this activity was to develop a simple **compiler using only lexical analysis techniques**, capable of processing a Pascal-like language.

## 📌 Project Objective

The main objective of this activity is to implement a lexical analyzer that processes a source code written in Pascal (stored in `Programa_fonte.txt`), identifying:

- Reserved keywords (e.g., `program`, `var`, `begin`, `end`)
- Assignment operators (`:=`)
- Identifiers (IDs)
- Type declarations (e.g., `integer`, `real`)
- Symbols (e.g., `;`, `:`, `=`, `+`, `(`, `)`)

All tokens must be stored in a **symbol table**, and any lexical **errors must be identified and reported individually**, including but not limited to:

- Unrecognized identifiers
- Invalid keywords
- Misspelled reserved words
- Missing or unexpected symbols

The analyzer continues processing until **all errors are found** and **reported clearly** in the output.

---

## 🗂️ Repository Structure


# Istruzioni aggiuntive - StockTrack

Questo file raccoglie le regole operative usate durante il refactor del progetto. Le istruzioni originali in `istruzioni.md` restano la fonte principale; questo documento serve come guida pratica per mantenere coerenti le prossime modifiche.

## 1. Separazione BCE/MVC

- Mantenere il flusso: `View -> GraphicalController -> Boundary -> Bean -> Controller applicativo -> Entity/DAO/Gateway`.
- Non far comunicare direttamente view, CLI o controller JavaFX con entity, DAO o adapter.
- La boundary deve essere unica per caso d'uso e condivisa da CLI e JavaFX.
- La boundary non deve contenere codice JavaFX, codice CLI, logica grafica o logica di persistenza.
- La boundary deve creare o ricevere bean, validarli quando necessario, istanziare il controller applicativo dentro il metodo e tradurre le eccezioni in messaggi comprensibili.

## 2. Bean e validazione

- I bean sono gli oggetti di passaggio tra boundary e controller applicativi.
- La validazione sintattica riutilizzabile deve stare nei bean, tramite metodi come `validate()`.
- Esempi di validazione da bean: campi obbligatori, quantita numeriche valide, codice fornitore non vuoto, prodotto selezionato valido.
- Le regole semantiche restano nei controller applicativi.
- Esempi di regole semantiche: permessi utente, prodotto inesistente, inventario insufficiente, duplicati, acquisto consentito solo al titolare.
- I bean di risposta devono evitare valori ambigui. Se una lista puo fallire o essere vuota, usare un bean di esito con lista, successo e messaggio.

## 3. Boundary

- Ogni metodo boundary deve essere sottile: prepara bean, valida, chiama il controller, cattura eccezioni applicative e restituisce bean di risposta.
- Non conservare stato utente dentro le boundary.
- Non conservare controller applicativi come campi della boundary.
- Istanziare il controller applicativo all'inizio del metodo boundary, come richiesto dalle istruzioni del progetto.
- Evitare duplicazione di messaggi tra CLI e JavaFX: la boundary deve fornire messaggi coerenti e le view devono solo mostrarli.
- Durante una migrazione, gli overload vecchi possono restare temporaneamente come deleghe, ma alla pulizia finale vanno rimossi se non usati.

## 4. Controller grafici e CLI

- I controller JavaFX e la CLI devono occuparsi solo di input/output, parsing, navigazione e presentazione dei messaggi.
- Non mettere logica applicativa nei controller JavaFX o nella CLI.
- Non duplicare regole di validazione gia presenti nei bean.
- CLI e JavaFX devono usare gli stessi metodi boundary per lo stesso caso d'uso.
- I messaggi mostrati a schermo devono provenire dalla boundary o dai bean di esito, non essere reinventati in ogni view.

## 5. Controller applicativi

- I controller applicativi devono essere stateless: niente campi con stato utente, carrelli correnti, prodotti selezionati o sessioni salvate.
- Possono usare entity, DAO, gateway e adapter.
- Devono contenere le regole di business e le precondizioni applicative.
- Devono verificare autorizzazioni e sessione corrente quando il caso d'uso lo richiede.
- Possono usare lock statici solo per proteggere operazioni composte su stato condiviso, senza trasformarsi in contenitori di stato utente.

## 6. Sessioni e concorrenza

- Non assumere applicazione single-user o single-thread.
- La sessione corrente non deve essere una variabile globale unica per tutta l'applicazione.
- Usare un modello compatibile con piu utenti e piu thread, per esempio sessioni indicizzate e sessione corrente legata al thread.
- Le strutture in memoria condivise in DEMO devono essere sicure per accessi concorrenti.
- Le operazioni composte di tipo leggi-modifica-salva devono essere protette, specialmente su inventario, prodotti e prodotti fornitore.

## 7. Persistenza e modalita DEMO/FULL

- In modalita DEMO i dati devono restare in memoria e devono essere persi alla chiusura dell'applicazione.
- In modalita DEMO non bisogna scrivere su CSV o DB.
- In modalita FULL i dati devono passare dai layer di persistenza previsti.
- I DAO devono restare disponibili nelle versioni File System e DBMS.
- La scelta di persistenza e interfaccia deve continuare a passare da `config.properties`.
- Adapter e gateway non devono aggirare la modalita configurata.

## 8. Pattern richiesti

- Singleton: usarlo per componenti globali motivati, come il gestore sessioni, evitando stato utente globale unico.
- Adapter: usarlo per gateway esterni o simulati, come pagamenti e API fornitori.
- Abstract Factory: usarla per selezionare la famiglia corretta di DAO.
- Non introdurre pattern aggiuntivi se non servono al caso d'uso o alle istruzioni.
- Il pattern Observer resta fuori dallo scope attuale, come indicato nelle istruzioni originali.

## 9. Entity, DAO e gateway

- Le entity non devono conoscere view, boundary, controller, JavaFX, CLI o DAO.
- I DAO devono occuparsi solo di accesso ai dati.
- I DAO non devono contenere logica di business.
- I gateway e gli adapter devono isolare i dettagli delle API esterne o simulate.
- Se un adapter mantiene stato simulato in memoria, quello stato deve rispettare DEMO/FULL e accessi concorrenti.

## 10. Errori e messaggi

- Le eccezioni applicative devono essere catturate dalla boundary quando devono diventare messaggi per l'utente.
- I messaggi CLI e JavaFX devono essere coerenti per lo stesso errore.
- Evitare messaggi tecnici all'utente finale quando esiste un messaggio applicativo piu chiaro.
- Evitare valori sentinella poco espressivi quando si puo restituire un bean di esito.

## 11. Pulizia e Sonar

- Evitare duplicazione di literal: usare costanti quando il testo o il nome colonna viene ripetuto.
- Evitare regex complesse se una piccola funzione esplicita e piu leggibile e sicura.
- Ridurre `break` e `continue` nei loop quando Sonar segnala troppa complessita.
- Preferire method reference quando Sonar segnala lambda sostituibili.
- Rimuovere metodi di compatibilita solo dopo aver verificato che CLI, JavaFX e test non li usano piu.

## 12. Verifica prima di chiudere

- Dopo modifiche al codice, eseguire sempre una build di verifica con Maven wrapper.
- Preferire `mvnw` del progetto rispetto a Maven globale.
- Se possibile, usare `clean test` per forzare la ricompilazione completa.
- Prima della consegna, controllare che non siano state introdotte dipendenze non richieste o modifiche fuori scope.

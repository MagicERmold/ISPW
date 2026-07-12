# CODING INSTRUCTIONS - StockTrack

## 1. Obiettivo

Realizzare il progetto StockTrack che consiste in un software dove un titolare di un'attività potrà avere controllo delle rimanenze di magazzino e potrà interfacciarsi direttamente con i fornitori per l'acquisto di prodotti che sono in esaurimento.

## 2. Dettagli

- circa 4k LOC;
- Inserire almeno 2 exceptions;
- L'applicazione dovrà runnare in 2 modalità differenti: DEMO e FULL. DEMO significa che tutti i dati devono essere salvati in memoria, ovvero non ci devono essere dei layer di persistenza, se l'applicazione termina tutti i dati vengono persi. FULL significa che tutti i dati devono essere salvati in layer di persistenza;
- Tutti i DAO devono essere disponibili in due versioni di persistenza: DMBS e File System;
- Tutte le funzionalità devono essere implementate in due GUI diverse, una testuale (CLI) e un'altra grafica con JavaFX. Le dipendenze sono già presenti nel pom.xml e module-info (Se necessario modificare solamente il module-info, chiedere sempre il permesso per modificare il pom.xml);
- Usare il file config.properties per scegliere la versione da usare per la persistenza e per la GUI/CLI;

## 3. Regole

Il progetto non deve essere completo. La priorità è rispettare la progettazione richiesta dai professori:

- BCE/MVC;
- Boundary, Graphical controller, controller applicativi, bean, entity e persistenza separati;
- controller stateless;
- l'applicazione deve essere progettata per funzionare con piu thread e piu utenti concorrenti, evitando assunzioni single thread o single user;
- bean usati per passaggio dati e validazione;
- pattern richiesti implementati in modo coerente;
- Usare la separazione: View (CLI o GUI) -> GraphicalController -> Boundary -> Bean -> Controller -> Entity/DAO/Gateway
- La boundary NON deve mai interagire direttamente con entity o DAO
- Il controller può usare entity, DAO, gateway e Adapter.
- Le entity non devono conoscere boundary, view, controller, DAO o JavaFX
- I bean sono gli oggetti usati per passare  dati tra boundary e controller

La grafica deve essere semplice: deve solo rendere dimostrabile il flusso del caso d'uso

## 4. Boundary

Per il caso d'uso deve esistere una sola Boundary BCE, non creare diverse boundary per CLI e GUI.

- Ogni metodo boundary deve essere sottile: prepara bean, valida, chiama il controller, cattura eccezioni applicative e restituisce bean di risposta.
- Non conservare stato utente dentro le boundary.
- Non conservare controller applicativi come campi della boundary.
- Istanziare il controller applicativo all'inizio del metodo boundary, come richiesto dalle istruzioni del progetto.
- Evitare duplicazione di messaggi tra CLI e JavaFX: la boundary deve fornire messaggi coerenti e le view devono solo mostrarli.



## 5. Caso d'uso principale

1. Il titolare effettua il login.
2. Il sistema configura i dati del profilo del titolare.
3. Il titolare seleziona il fornitore.
4. Il sistema carica i prodotti del fornitore.
5. Il titolare seleziona i prodotti da acquistare.
6. Il sistema aggiunge i prodotti al carrello.
7. Il titolare inserisce i dati per il pagamento.
8. Il sistema elabora la transazione di pagamento.
9. Il sistema invia una notifica di pagamento effettuato al fornitore.
10. Il sistema aggiorna il magazzino e salva i dati solo se la modalità è FULL.

Gestire almeno questi flussi alternativi:

- prodotto selezionato non disponibile;
- dati inseriti non validi;
- errore di persistenza;

## 6. Bean e validazione

- I bean sono gli oggetti di passaggio tra boundary e controller applicativi.
- La validazione sintattica riutilizzabile deve stare nei bean, tramite metodi come `validate()`.
- Esempi di validazione da bean: campi obbligatori, quantita numeriche valide, codice fornitore non vuoto, prodotto selezionato valido.
- Le regole semantiche restano nei controller applicativi.
- Esempi di regole semantiche: permessi utente, prodotto inesistente, inventario insufficiente, duplicati, acquisto consentito solo al titolare.
- I bean di risposta devono evitare valori ambigui. Se una lista puo fallire o essere vuota, usare un bean di esito con lista, successo e messaggio.

## 7. Controller applicativi

- I controller applicativi devono essere stateless: niente campi con stato utente, carrelli correnti, prodotti selezionati o sessioni salvate.
- Possono usare entity, DAO, gateway e adapter.
- Devono contenere le regole di business e le precondizioni applicative.
- Devono verificare autorizzazioni e sessione corrente quando il caso d'uso lo richiede.

## 8. Sessioni e concorrenza

- Non assumere applicazione single-user o single-thread.
- La sessione corrente non deve essere una variabile globale unica per tutta l'applicazione.
- Usare un modello compatibile con piu utenti e piu thread, per esempio sessioni indicizzate e sessione corrente legata al thread.
- Le strutture in memoria condivise in DEMO devono essere sicure per accessi concorrenti.
- Le operazioni composte di tipo leggi-modifica-salva devono essere protette, specialmente su inventario, prodotti e prodotti fornitore.

## 9. Persistenza e modalita DEMO/FULL

- In modalita DEMO i dati devono restare in memoria e devono essere persi alla chiusura dell'applicazione.
- In modalita DEMO non bisogna scrivere su CSV o DB.
- In modalita FULL i dati devono passare dai layer di persistenza previsti.
- I DAO devono restare disponibili nelle versioni File System e DBMS.
- La scelta di persistenza e interfaccia deve continuare a passare da `config.properties`.
- Adapter e gateway non devono aggirare la modalita configurata.

## 10. Pattern richiesti

- Singleton: usarlo per componenti globali motivati, come il gestore sessioni, evitando stato utente globale unico.
- Adapter: usarlo per gateway esterni o simulati, come pagamenti e API fornitori.
- Abstract Factory: usarla per selezionare la famiglia corretta di DAO.
- Non introdurre pattern aggiuntivi se non servono al caso d'uso o alle istruzioni.

## 11. Errori e messaggi

- Le eccezioni applicative devono essere catturate dalla boundary quando devono diventare messaggi per l'utente.
- I messaggi CLI e JavaFX devono essere coerenti per lo stesso errore.
- Evitare messaggi tecnici all'utente finale quando esiste un messaggio applicativo piu chiaro.
- Evitare valori sentinella poco espressivi quando si puo restituire un bean di esito.

## 12. Pulizia e Sonar

- Evitare duplicazione di literal: usare costanti quando il testo o il nome colonna viene ripetuto.
- Evitare regex complesse se una piccola funzione esplicita e piu leggibile e sicura.
- Ridurre `break` e `continue` nei loop quando Sonar segnala troppa complessita.
- Preferire method reference quando Sonar segnala lambda sostituibili.
- Rimuovere metodi di compatibilita solo dopo aver verificato che CLI, JavaFX e test non li usano piu.

## 13 Altro

- Seguire i diagrammi condivisi
- Seguire i pattern: Singleton, Adapter e Abstract Factory.
- Modificare esclusivamente il progetto nella cartella Stocktrack
- I metodi solitamente non devono avere un valore di ritorno, ma devono essere void e modificare lo stato di un oggetto. Non devono operare come funzioni.

# Vector Race: Gioco Vettoriale di Formula 1

## Descrizione
Vector Race è un gioco che simula una gara di Formula 1 basata su vettori. I giocatori, che possono essere sia umani che bot controllati dal computer, competono su un circuito disegnato su una griglia. Lo sviluppo del progetto ha seguito il pattern architetturale Model-View-Controller (MVC), con particolare attenzione al rispetto dei principi SOLID e all'utilizzo di design pattern consolidati.

Il gioco è pensato per appassionati di giochi di strategia e corse, nonché per studenti e sviluppatori interessati a vedere un'applicazione pratica dei concetti di MVC e IA in Java.

## Sommario
* [Descrizione](#descrizione)
* [Caratteristiche Principali](#caratteristiche-principali)
* [Tecnologie Utilizzate](#tecnologie-utilizzate)
* [Prerequisiti](#prerequisiti)
* [Installazione](#installazione)
* [Configurazione Giocatori](#configurazione-giocatori)
* [Utilizzo](#utilizzo)
* [Esecuzione dei Test](#esecuzione-dei-test)
* [Struttura del Progetto](#struttura-del-progetto)
* [Come Contribuire](#come-contribuire)
* [Licenza](#licenza)
* [Autori e Riconoscimenti](#autori-e-riconoscimenti)
* [Contatti](#contatti)

## Caratteristiche Principali
*   **Simulazione di Gara Vettoriale:** Gameplay strategico basato sul calcolo di vettori di movimento.
*   **Circuiti da File:** I tracciati di gara vengono caricati da file di testo (`.txt`), dove ogni carattere rappresenta un elemento del circuito.
*   **Circuiti Multipli:** Include 3 circuiti predefiniti selezionabili all'avvio.
*   **Obiettivo Chiaro:** Vince il primo giocatore che raggiunge una cella di arrivo, determinando la fine immediata della partita.
*   **Intelligenza Artificiale Avanzata:** I movimenti dei bot sono gestiti da due algoritmi:
    *   Un adattamento dell'algoritmo A* per la ricerca del percorso ottimale.
    *   L'algoritmo Breadth-First Search (BFS) per l'esplorazione.
*   **Doppia Interfaccia Utente:**
    *   **CLI (Command Line Interface):** Per un'esperienza di gioco testuale.
    *   **GUI (Graphical User Interface):** Realizzata con JavaFX per un'interazione più visuale.
*   **Configurazione Giocatori Esterna:** I giocatori (sia umani che bot) e le loro caratteristiche vengono caricati da file di testo dedicati.
*   **Architettura MVC:** Progettato seguendo il pattern Model-View-Controller per una chiara separazione delle responsabilità.
*   **Principi SOLID e Design Pattern:** Sviluppato con attenzione alla qualità del codice e alla manutenibilità.

## Tecnologie Utilizzate
*   **Linguaggio:** Java 21
*   **Build Tool:** Gradle
*   **GUI Framework:** JavaFX 21.0.4
    *   `org.openjfx:javafx-controls:21.0.4`
    *   `org.openjfx:javafx-fxml:21.0.4`
*   **Librerie:**
    *   Google Guava `33.3.1-jre`
    *   Jansi `2.4.1` (per il supporto colori ANSI nella CLI)
*   **Testing:** JUnit 5 (Jupiter)

## Prerequisiti
Prima di iniziare, assicurati di avere installato sul tuo sistema:
*   Java Development Kit (JDK) versione 21 o successiva.
*   Gradle (sebbene il progetto includa un wrapper Gradle, averlo installato globalmente può essere utile). Puoi scaricarlo da [sito ufficiale Gradle](https://gradle.org/install/).

## Installazione
1.  Clona il repository del progetto:
    ```bash
    git clone <URL_DEL_REPOSITORY_GIT_DEL_PROGETTO>
    cd vector-race 
    ```
    (Sostituisci `<URL_DEL_REPOSITORY_GIT_DEL_PROGETTO>` con l'URL effettivo del repository del progetto).
2.  Compila il progetto utilizzando Gradle Wrapper. Questo scaricherà automaticamente la versione corretta di Gradle, se non presente:
    ```bash
    ./gradlew build
    ```
    Su Windows:
    ```bash
    gradlew.bat build
    ```

## Configurazione Giocatori
I giocatori vengono caricati da file di testo specifici a seconda della modalità di gioco:
*   `app/src/main/resources/players/playersCLI.txt`: Per la modalità CLI (supporta solo giocatori Bot).
*   `app/src/main/resources/players/playersGUI.txt`: Per la modalità GUI (supporta giocatori Umani e Bot).

**Formato del file `playersGUI.txt`:**
Ogni riga rappresenta un giocatore e deve seguire il formato:
`Tipo;Nome;ColoreHEX;StrategiaBot`
*   `Tipo`: `human` o `Bot`.
*   `Nome`: Nome del giocatore.
*   `ColoreHEX`: Codice esadecimale del colore del giocatore (es. `#FF0000` per rosso).
*   `StrategiaBot`: (Solo per `Bot`) Numero identificativo della strategia:
    *   `1`: BFS (Breadth-First Search)
    *   `2`: PureAStarStrategy (basata su A*)

*Esempio `playersGUI.txt`*:
```
human;Human1;#FF0000
human;Human2;#00FF00
Bot;Bot3;#FFFFFF;1
Bot;Bot4;#0000F0;2
```

**Formato del file `playersCLI.txt`:**
Ogni riga rappresenta un giocatore Bot e deve seguire il formato:
`Bot;Nome;StrategiaBot`
*   `Bot`: Tipo fisso.
*   `Nome`: Nome del Bot.
*   `StrategiaBot`: Numero identificativo della strategia (1 per BFS, 2 per PureAStarStrategy).

*Esempio `playersCLI.txt`*:
```
Bot;Bot1;1
Bot;Bot2;2
```

**Nota sul Numero di Giocatori:**
*   Per `circuit1.txt` e `circuit3.txt`: Massimo 5 giocatori.
*   Per `circuit2.txt`: Massimo 4 giocatori.

## Utilizzo
Dopo aver compilato il progetto (`./gradlew build`), puoi avviarlo in modalità CLI o GUI.

### Modalità CLI (Solo Bot)
1.  Apri un terminale nella directory principale del progetto.
2.  Avvia l'applicazione con:
    ```bash
    ./gradlew run
    ```
    Su Windows:
    ```bash
    gradlew.bat run
    ```
3.  Segui le istruzioni a schermo per selezionare il circuito e avviare la partita.
    *Nota: In modalità CLI possono partecipare solo giocatori Bot configurati in `playersCLI.txt`.*

### Modalità GUI (Umani e Bot)
1.  Apri un terminale nella directory principale del progetto.
2.  Avvia l'applicazione con:
    ```bash
    ./gradlew run --args="gui"
    ```
    Su Windows:
    ```bash
    gradlew.bat run --args="gui"
    ```
3.  Si aprirà una finestra grafica. Seleziona il circuito desiderato e avvia la gara.

## Esecuzione dei Test
Per eseguire la suite di test automatizzati del progetto, utilizza il seguente comando dalla directory principale:
```bash
./gradlew test
```
Su Windows:
```bash
gradlew.bat test
```
I risultati dei test verranno visualizzati nella console.

## Struttura del Progetto
Il progetto segue una struttura modulare per favorire la manutenibilità e la chiarezza, aderendo al pattern MVC.
```
vector-race/
├── app/                                # Codice sorgente dell'applicazione e risorse
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/it/unicam/cs/mdp/vectorrace/
│   │   │   │   ├── Main.java           # Entry point dell'applicazione
│   │   │   │   ├── config/             # Classi di configurazione (CLIConfig, GUIConfig)
│   │   │   │   ├── controller/         # GameController (logica di coordinamento MVC)
│   │   │   │   ├── model/
│   │   │   │   │   ├── core/           # Entità base (Position, Vector, CellType, Track, TrackLoader, AccelerationType)
│   │   │   │   │   ├── game/           # Logica di gioco (GameState, MovementManager, TurnManager, validators)
│   │   │   │   │   ├── ai/             # Intelligenza Artificiale (algoritmi, checkpoint, servizi, strategie)
│   │   │   │   │   └── players/        # Gestione giocatori (Player, BotPlayer, PlayerFactory, PlayerParser)
│   │   │   │   └── view/               # Gestione interfacce utente (CLIView, GUIView, renderers, etc.)
│   │   │   └── resources/              # Risorse statiche
│   │   │       ├── circuits/           # File .txt dei circuiti (circuit1.txt, circuit2.txt, circuit3.txt)
│   │   │       └── players/            # File .txt di configurazione giocatori (playersCLI.txt, playersGUI.txt)
│   │   └── test/                       # Codice e risorse per i test
│   └── build.gradle                    # Script di build per il modulo app
├── gradle/                             # Configurazione del wrapper Gradle
└── ...                                 # Altri file di progetto (build.gradle, gradlew, LICENSE, etc.)
```
**Responsabilità dei Package Principali del Modello (`model`):**
*   **`model.core`**: Modella il dominio applicativo, definendo le entità fondamentali come posizioni, vettori, tipi di celle, tracciati e il loro caricamento.
*   **`model.game`**: Coordina lo stato della partita, la gestione dei turni e la validazione dei movimenti, incluso il rilevamento delle collisioni.
*   **`model.ai`**: Contiene tutta la logica per l'intelligenza artificiale dei bot, inclusi algoritmi di pathfinding (A*, BFS), gestione dei checkpoint, servizi di supporto (localizzazione arrivo, validazione mosse) e le strategie di movimento.
*   **`model.players`**: Gestisce i partecipanti al gioco (umani e bot), includendo il parsing dei file di configurazione, la creazione dei giocatori e l'assegnazione delle strategie ai bot.

**Responsabilità dei Package Principali della Vista (`view`):**
*   Gestisce la comunicazione con l'utente attraverso interfacce testuali (CLI) e grafiche (GUI), occupandosi del rendering dello stato del gioco e della cattura dell'input utente.

**Responsabilità del Controller (`controller`):**
*   Assicura la corretta interazione tra il modello (dati e logica di business) e la vista (presentazione all'utente), orchestrando il flusso applicativo.

## Come Contribuire
I contributi sono benvenuti! Se desideri migliorare Vector Race, segui questi passaggi:

1.  **Fork del Repository:** Crea una tua copia (fork) del repository del progetto.
2.  **Crea un Branch:** Lavora su un branch dedicato per la tua feature o bugfix (es. `git checkout -b mia-nuova-feature`).
3.  **Apporta Modifiche:** Implementa le tue modifiche e assicurati che i test passino.
4.  **Commit:** Fai commit delle tue modifiche con messaggi chiari (es. `git commit -am 'Aggiunta nuova feature Xyz'`).
5.  **Push:** Carica il tuo branch sul tuo fork (es. `git push origin mia-nuova-feature`).
6.  **Pull Request:** Apri una Pull Request verso il branch `main` (o `develop`, se presente) del repository originale, descrivendo le modifiche apportate.

### Estensione delle Strategie dei Bot
Per aggiungere una nuova strategia di intelligenza artificiale per i bot:
1.  Crea una nuova classe Java nel package `app.src.main.java.it.unicam.cs.mdp.vectorrace.model.ai.strategies` che implementi l'interfaccia `it.unicam.cs.mdp.vectorrace.model.ai.strategies.AIStrategy` (o `IPathFinder` a seconda del livello di astrazione desiderato).
2.  Implementa il metodo richiesto dall'interfaccia (es. `getNextAcceleration(Player player, GameState gameState)` per `AIStrategy`).
3.  Aggiorna il metodo `createStrategy(StrategyType strategyType)` nella classe `it.unicam.cs.mdp.vectorrace.model.players.PlayerFactory` per includere la tua nuova strategia, associandola a un nuovo `StrategyType` o a un identificatore numerico.
4.  Configura il nuovo tipo di strategia nei file `playersGUI.txt` o `playersCLI.txt` utilizzando l'identificatore che hai definito.

## Licenza
Questo progetto è rilasciato sotto la Licenza MIT. Vedi il file [`LICENSE`](LICENSE) per maggiori dettagli.
Copyright (c) 2025 Angelo Albanesi.

## Autori e Riconoscimenti
*   **Autore :** Angelo Albanesi


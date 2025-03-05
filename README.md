# Projektname
Ein kurzer Text als Intro

# Inhaltsverzeichnis
- [Einleitung](#einleitung)
    - [Explore-Board](#explore-board)
    - [Create-Board](#create-board)
    - [Evaluate-Board](#evaluate-board)
    - [Diskussion Feedback Pitch](#diskussion-feedback-pitch)
- [Anforderungen](#anforderungen)
    - [Use-Case Diagramm](#use-case-diagramm)
    - [Use-Case Beschreibung](#use-case-beschreibung)
    - [Fachliches Datenmodell](#fachliches-datenmodell)
    - [Erläuterungen zum Datenmodell](#erläuterungen-zum-datenmodell)
    - [Zustandsdiagramm](#zustandsdiagramm)
    - [UI-Mockup](#ui-mockup)
- [Implementation](#implementation)
    - [Frontend](#frontend)
    - [KI-Funktionen](#ki-funktionen)
- [Fazit](#fazit)
    - [Stand der Implementation](#stand-der-implementation)
    
# Einleitung

## Explore-Board
### TRENDS & TECHNOLOGIE
- Künstliche Intelligenz (KI): Automatisierte Faktenchecks mithilfe eines KI-Modells über die Spring AI-API.
- Blockchain-Technologie: Möglichkeit zur fälschungssicheren Speicherung von Quellenangaben.
- Personalisierte Feeds: Machine Learning für individualisierte News-Empfehlungen.
- Digitale Zahlungsmodelle: Nutzung von Krypto-Zahlungen oder Micropayments für abonnementbasierte Inhalte.
- Dezentralisierung: Nutzung von Open-Source-Plattformen zur Vermeidung von Zensur.
- Progressive Web Apps (PWA): Offline-fähige App-Version für bessere Benutzererfahrung.

### POTENTIELLE PARTNER & WETTBEWERB
> Wettbewerber:
- Traditionelle Nachrichtenportale (z. B. Spiegel, BBC, The Guardian).
- Alternative Medienplattformen (z. B. Correctiv, Substack, Mastodon als Social-Media-Alternative).
- KI-gestützte News-Services (z. B. Google News KI-Feeds).
> Potenzielle Partner:
- Faktencheck-Organisationen (z. B. Snopes, Mimikama, Correctiv).
- NGOs für unabhängigen Journalismus (Reporter ohne Grenzen, Open Knowledge Foundation).
- Technologie-Partner für KI-Modelle und Cloud-Hosting (Azure, MongoDB Atlas).
### FAKTEN
- Spring Boot + MongoDB Atlas als Technologie-Stack für das Backend.
- Frontend mit Svelte für eine schnelle, reaktionsfähige Benutzeroberfläche.
- Deployment auf Azure App Service mit Docker & GitHub Actions.
- Authentifizierung über Auth0, um unterschiedliche Benutzerrollen zu ermöglichen.
- Abo-Modell als Finanzierungsmöglichkeit für unabhängigen Journalismus.
- Community-Bewertungen für Artikel (Leser können Glaubwürdigkeit bewerten).
> Nutzerrollen:
- Leser (Artikel konsumieren, abonnieren).
- Journalisten (Artikel schreiben, veröffentlichen, anonyme Beiträge erstellen).
- Faktenprüfer (Beiträge verifizieren).


### POTENZIALFELDER
- Vertrauenswürdige Berichterstattung: Verlässliche News durch Faktenprüfung.
- Mitsprache der Community: Nutzer können Artikel bewerten oder kommentieren.
- Schutz von Journalisten: Möglichkeit, Artikel anonym zu veröffentlichen.
- Individuelle Feeds: Algorithmen zur personalisierten News-Auswahl.
- Alternative Finanzierungsmodelle: Spenden, Crowdfunding, Krypto-Micropayments.

### USER
> Leser:
- Suchen unabhängige Nachrichten, fernab von Mainstream-Medien.
- Wollen eine personalisierte Feed-Funktion.
- Interessieren sich für transparente Quellenangaben.
> Journalisten:
- Möchten ohne redaktionelle Einschränkungen publizieren.
- Brauchen sichere Kanäle für investigativen Journalismus.
> Faktenchecker:
- Überprüfen Inhalte und kennzeichnen Falschmeldungen.
- Arbeiten mit KI-Unterstützung, um Fake News schneller zu entlarven.

### BEDÜRFNISSE
- Zugang zu faktenbasierten, unabhängigen Nachrichten.
- Sicherheit für Journalisten, um auch brisante Themen zu veröffentlichen.
- Transparenz bei Informationen durch Blockchain oder Faktenchecks.
- Nutzerfreundlichkeit: Eine moderne, intuitive Plattform.

### ERKENNTNISSE
- Fake News und Desinformation sind eine große Herausforderung.
- Viele Leser vertrauen traditionellen Medien nicht mehr und suchen Alternativen.
- Community-Engagement ist wichtig – Nutzer wollen interagieren, nicht nur konsumieren.
### TOUCHPOINTS
- Web-App (PWA) als zentrale Plattform für Artikel, Abos und Community-Interaktionen.
- Social Media für Nachrichtenverbreitung (Twitter/X, Mastodon, LinkedIn).
- Newsletter mit personalisierten News.
- Podcast- & Videoformate als alternative Verbreitungskanäle.


### WIE KÖNNEN WIR?
- Wie können wir eine sichere, unabhängige News-Plattform schaffen, die Journalisten schützt und Lesern verlässliche Inhalte bietet?
- Wie können wir eine sichere, unabhängige News-Plattform schaffen, die Journalisten schützt und Lesern verlässliche Inhalte bietet?
- Wie können wir KI-gestützte Faktenchecks in den Journalismus integrieren?
- Wie können wir ein Finanzierungsmodell entwickeln, das unabhängigen Journalismus nachhaltig unterstützt?
## Create-Board
### IDEEN-BESCHREIBUNG
Unsere Plattform ermöglicht unabhängigen Journalismus durch eine **sichere, transparente und community-getriebene Nachrichtenplattform**.  
Journalisten können investigativ berichten – auch **anonym**, um sich vor Repressionen zu schützen.  
Leser erhalten **faktenbasierte Nachrichten**, kuratiert durch eine Kombination aus **KI-gestützter Faktenprüfung und Community-Bewertungen**.  
Ein **Abo-Modell & alternative Finanzierungsmethoden** (z. B. Krypto-Spenden) gewährleisten Unabhängigkeit von Werbung und staatlichen Geldern.

### ADRESSIERTE NUTZER
### 📖 Leser
- Interessieren sich für **alternative, unabhängige Nachrichten**.
- Wollen sicher sein, dass Inhalte **geprüft & faktenbasiert** sind.
- Möchten ihre **Nachrichten personalisieren**.

### 📝 Journalisten
- Suchen eine **Plattform ohne Zensur oder politische Einflussnahme**.
- Wollen auch **anonym veröffentlichen können**, um Risiken zu vermeiden.
- Brauchen eine **faire Monetarisierung** für ihre Arbeit.

### ✅ Faktenprüfer & Community
- Unterstützen die Plattform durch **Faktenchecks & Bewertungen**.
- Helfen, **Desinformation zu reduzieren**.

### ADRESSIERTE BEDÜRFNISSE
✔️ **Verlässliche Nachrichten** – Nutzer wollen **ungefilterte, faktenbasierte** News.  
✔️ **Schutz für Journalisten** – Sichere Veröffentlichungen, auch anonym.  
✔️ **Alternative Finanzierung** – Unabhängigkeit von Werbung & staatlichen Subventionen.  
✔️ **Community-Engagement** – Leser möchten sich **aktiv beteiligen** (Kommentieren, Bewerten).  
✔️ **Personalisierte Inhalte** – Jeder Nutzer bekommt einen **individuellen Feed**. 

### PROBLEME
1. **Fake News & Desinformation**: Viele Nachrichtenquellen sind beeinflusst oder ungeprüft.  
2. **Gefährdung von Journalisten**: In autoritären Staaten drohen Journalisten **Repressionen**.  
3. **Abhängigkeit von Werbeeinnahmen**: Viele Medienhäuser sind von Werbung & Politik beeinflusst.  

### IDEENPOTENZIAL
Mehrwert: Mückenstich vs. Hai-Attacke

🔵🔵🔵⚪️⚪️⚪️⚪️⚪️⚪️⚪️  
  → Die Plattform deckt ein ernstes Problem auf, das nicht jeder als dringlich wahrnimmt – bis Fake News sie direkt betreffen.  

Übertragbarkeit: Robinson Crusoe vs. die Welt

🔵🔵🔵🔵🔵⚪️⚪️⚪️⚪️⚪️  
  → Kann in **verschiedenen Märkten & Sprachen** angewendet werden (globales Thema).
  
Machbarkeit: Hammer vs. Raumschiff

🔵🔵🔵🔵🔵🔵🔵🔵⚪️⚪️  
  → Technisch realisierbar mit **KI, Blockchain & bestehender Web-Technologie**.  


### DAS WOW
📰 **"Die erste wirklich unabhängige News-Plattform mit anonymer Veröffentlichung & Faktenchecks!"**  
🎯 **"Journalismus neu gedacht – ohne politische Einflussnahme, Werbung oder Fake News!"**  
🚀 **"Faktenchecks – Verlässlichkeit auf Knopfdruck!"**  

### HIGH-LEVEL-KONZEPT
- 🛡 **"Das Wikipedia für unabhängige Nachrichten."**  
- 🤖 **"Netflix für faktenbasierte Berichterstattung – personalisiert & werbefrei."**  
- 🔍 **"Twitter ohne Bots, Fake News & Algorithmen-Manipulation."**  

### WERTVERSPRECHEN
🗞 **"Unsere Plattform bietet eine sichere, transparente und community-getriebene Umgebung für investigativen Journalismus – mit garantiert unabhängigen, faktenbasierten Nachrichten für jeden."**

## Evaluate-Board
### KANÄLE
> Beschreibe die Vertriebs- und Marketingkanäle, über welche die NutzerInnen erreicht werden sollen. Beispiel: TikTok, E-Mail, Flyer etc.

### UNFAIRER VORTEIL
> Notiere Faktoren der Lösung, die nur schwer oder gar nicht kopierbar sind. Diese Faktoren machen es schwierig, ein Konkurrenzprodukt deiner Lösung zu lancieren. 

### KPI
> Trage hier Messgrössen ein, mit denen sich der Erfolg deiner Lösung messen lässt. Beispiele: Anzahl Verkäufe, Anzahl Kunden, Anzahl Transaktionen, Umsatz...

### EINNAHMEQUELLEN
> Beschreibe, wie mit deiner Lösung Geld verdient werden soll. Wo und durch wen werden Einnahmen generiert? Hinweis: die Einnahmen müssen nicht unbedingt von den NutzerInnen stammen. Es kann auch eine Trägerschaft wie z.B. ein Verein mit Mitgliederbeiträgen, Spenden oder ähnlichem gewählt werden.

## Diskussion Feedback Pitch
> Diskussion des Feedbacks aus dem Pitch (bezogen auf Projektinhalt)

# Anforderungen
## Use-Case Diagramm
> Hier das Diagramm einbinden

## Use-Case Beschreibung
> Hier die Use-Case Beschreibung einfügen so wie du das in RE gelernt hast. 

## Fachliches Datenmodell 
> Hier das fachliche Datenmodell (ER-Modell) einbinden. Ein fachliches Modell enthält **keine** IDs oder Ähnliches

## Erläuterungen zum Datenmodell 
> Beschreibe die Entitäten, deren Attribute sowie die Beziehungen zwischen den Entitäten.

## Zustandsdiagramm
> Hier das Zustandsdiagramm einbinden für diejenige Entität(en), welche mehrere Zustände durchläuft mit Events, Effects und Guards.

## UI-Mockup 
> Mockup oder Skizze des UIs

# Implementation
## Frontend
> Beschreibung des Frontends mit Screenshots der fertigen Applikation. Alle Teile des GUIs, die bewertet werden sollen, müssen abgebildet sein.

## KI-Funktionen
> Aufgaben und Funktionen des eingebundenen KI-Modells.

# Fazit

## Stand der Implementation
> Stand der Implementation, nächste Schritte (mit Referenz auf den Backlog).

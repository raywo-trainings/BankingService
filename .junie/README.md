# JetBrains Junie Konfiguration

Diese Konfigurationsdateien steuern das Verhalten von JetBrains Junie in diesem Projekt.

## Übersicht der Dateien

- **config.json**: Hauptkonfigurationsdatei für Junie mit Einstellungen für Modelle, Kontext, Verhalten, Sprache und Sicherheit
- **prompts.json**: Benutzerdefinierte Prompts und Code-Snippets für häufige Aufgaben im Banking-Kontext
- **context.json**: Domänenspezifische Informationen über die Bankanwendung, ihre Architektur und Konventionen

## Anpassung

### Modell-Einstellungen

In `config.json` können Sie das Standardmodell und alternative Modelle konfigurieren:

```
{
  "models": {
    "default": "gpt-4",
    "alternatives": ["gpt-3.5-turbo", "claude-3-opus"]
  }
}
```

### Spracheinstellungen

Die bevorzugte Sprache ist auf Deutsch eingestellt, mit Englisch als Fallback:

```
{
  "language": {
    "preferred": "de",
    "fallback": "en"
  }
}
```

### Benutzerdefinierte Prompts

In `prompts.json` können Sie neue benutzerdefinierte Prompts hinzufügen:

```
{
  "name": "meinPrompt",
  "description": "Beschreibung des Prompts",
  "prompt": "Text des Prompts mit {{variablen}}",
  "context": ["relevante-Dateien.java"]
}
```

### Domänenkontext

In `context.json` können Sie Informationen über die Domäne, Architektur und Konventionen des Projekts aktualisieren.

## Verwendung

JetBrains Junie verwendet diese Konfigurationsdateien automatisch, wenn Sie Junie in der IDE verwenden. Die benutzerdefinierten Prompts sind über das Junie-Menü oder durch Eingabe des Promptnamens verfügbar.

**Note: This plugin is included in OmegaT 4.0 r9023 and later. Do not use this
  plugin with this or later versions.**

# omegat-history-completer
An autocompleter plugin that suggests words from existing translations in
a project

- **Completion View:** As you type a word, this view suggests completions from
    words found in existing translations in the current project.
- **Prediction View:** This view suggests words that have appeared elsewhere in
    the current project following the previous word. The suggestions are sorted
    by frequency of use.

## Requirements
- OmegaT 3.6.0 r8270 or later, 4.0 r9022 or earlier
- Java 1.7

## Obtaining
Download the JAR from [Releases]
(https://github.com/amake/omegat-history-completer/releases) or build yourself.

## Building
Clone the repository, then run `mvn install`.

## Installing
1. Place the JAR in one of OmegaT's `plugins` directories (alongside
`OmegaT.jar`, in the configuration directory, etc.).
2. Enable the view in `Options` > `Auto-completion` > `History Completer`.

## Acknowledgments
This project uses [trie4j](https://github.com/takawitter/trie4j) under the
Apache License 2.0.

## License
This project is distributed under the [GNU General Public License, v3]
(http://www.gnu.org/licenses/gpl-3.0.html).


Copyright 2016 Aaron Madlon-Kay <aaron@madlon-kay.com>

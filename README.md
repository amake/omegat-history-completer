# omegat-history-completer
An autocompleter view that suggests words from existing translations in
a project

This autocompleter view suggests words from existing translations* in
the current project that share a common prefix with the word currently
being typed (minimum 4 characters long).

\*"Default" translations only  

## Requirements
- OmegaT 3.6.0 r8270 or later (only tested on trunk)
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

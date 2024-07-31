![Java](https://img.shields.io/badge/java-21-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=gradle&logoColor=white)
[![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/NeedCoolerShoes/SuperSimpleEmoji)
![Release](https://img.shields.io/badge/release-alpha-11FF05.svg?style=for-the-badge&logoColor=white)
[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/NeedCoolerShoes/SuperSimpleEmoji/gradle.yml?style=for-the-badge&logo=github)](https://github.com/NeedCoolerShoes/SuperSimpleEmoji/actions/workflows/gradle.yml)
![GitHub Last Commit](https://img.shields.io/github/last-commit/NeedCoolerShoes/SuperSimpleEmoji?style=for-the-badge&logo=github)
![GitHub Commit Activity](https://img.shields.io/github/commit-activity/w/NeedCoolerShoes/SuperSimpleEmoji?style=for-the-badge&logo=github)
![Code Size](https://img.shields.io/github/languages/code-size/NeedCoolerShoes/SuperSimpleEmoji?style=for-the-badge&logo=github)
![Lines of Code](https://img.shields.io/endpoint?style=for-the-badge&logo=github&url=https://ghloc.vercel.app/api/NeedCoolerShoes/SuperSimpleEmoji/badge?filter=.java$&label=lines%20of%20code&color=blue)

# SuperSimpleEmoji

Stupidly simple emoji plugin.

Requires Paper 1.21.

Emojis can be configured in `config.yml` as such:
```yml
emojis:
  heart:
    pattern: '<3'
    text: '{"text":"❤","color":"red"}'
```

The pattern will be compiled to a regular expression.

# Known Incompatibilities

LPC

The text is interpreted as a JSON text component.

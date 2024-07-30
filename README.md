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

The text is interpreted as a JSON text component.
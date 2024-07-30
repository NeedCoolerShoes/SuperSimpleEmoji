package com.needcoolershoes.supersimpleemoji;

import java.util.regex.Pattern;

public class Emoji {
    public String name;
    public Pattern pattern;
    public String jsonText;

    public Emoji(String name, String pattern, String jsonText) {
        this.name = name;
        this.pattern = Pattern.compile(pattern);
        this.jsonText = jsonText;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return name.equals(object);
    }
}

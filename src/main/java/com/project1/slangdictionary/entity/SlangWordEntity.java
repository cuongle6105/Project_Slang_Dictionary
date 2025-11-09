package com.project1.slangdictionary.entity;

import java.util.List;

public class SlangWordEntity {
    private String word;
    private List<String> definition;

    public SlangWordEntity(String word, List<String> definition) {
        this.word = word;
        this.definition = definition;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<String> getDefinition() {
        return definition;
    }

    public void setDefinition(List<String> definition) {
        this.definition = definition;
    }
}

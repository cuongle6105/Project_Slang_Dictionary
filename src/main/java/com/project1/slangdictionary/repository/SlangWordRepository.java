package com.project1.slangdictionary.repository;

import com.project1.slangdictionary.entity.SlangWordEntity;

import java.util.List;
import java.util.Set;

public interface SlangWordRepository {
    List<SlangWordEntity> findAll();
    SlangWordEntity findByWord(String word);
    List<SlangWordEntity> findByDefinition(String definition);
    void add(SlangWordEntity slangWord);
    void update(SlangWordEntity slangWord);
    void remove(SlangWordEntity slangWord);
    void reset();
}

package com.project1.slangdictionary.repository;

import com.project1.slangdictionary.entity.SlangWordEntity;

import java.util.List;
import java.util.Set;

public interface SlangWordRepository {
    List<SlangWordEntity> findAll();
    SlangWordEntity findByWord(String word);
    List<SlangWordEntity> findByDefinition(String definition);
}

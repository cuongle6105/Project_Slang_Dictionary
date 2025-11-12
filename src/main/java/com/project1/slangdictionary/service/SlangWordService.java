package com.project1.slangdictionary.service;

import com.project1.slangdictionary.entity.SlangWordEntity;

import java.util.List;

public interface SlangWordService {
    List<SlangWordEntity> findAll();
    SlangWordEntity findByWord(String word);
    List<SlangWordEntity> findByDefinition(String definition);
    
}

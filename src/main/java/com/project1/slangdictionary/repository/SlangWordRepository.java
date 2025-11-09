package com.project1.slangdictionary.repository;

import com.project1.slangdictionary.entity.SlangWordEntity;

import java.util.List;

public interface SlangWordRepository {
    List<SlangWordEntity> findAll();
}

package com.project1.slangdictionary.service.impl;

import com.project1.slangdictionary.entity.SlangWordEntity;
import com.project1.slangdictionary.repository.SlangWordRepository;
import com.project1.slangdictionary.repository.impl.SlangWordRepositoryImpl;
import com.project1.slangdictionary.service.SlangWordService;

import java.util.List;

public class SlangWordServiceImpl implements SlangWordService {
    private final SlangWordRepository slangWordRepository;

    public SlangWordServiceImpl() {
        this.slangWordRepository = new SlangWordRepositoryImpl();
    }


    @Override
    public List<SlangWordEntity> findAll() {
        return slangWordRepository.findAll();
    }
}

package com.project1.slangdictionary.service.impl;

import com.project1.slangdictionary.entity.SlangWordEntity;
import com.project1.slangdictionary.repository.SlangWordRepository;
import com.project1.slangdictionary.repository.impl.SlangWordRepositoryImpl;
import com.project1.slangdictionary.service.SlangWordService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SlangWordServiceImpl implements SlangWordService {
    private final SlangWordRepository slangWordRepository;

    public SlangWordServiceImpl() {
        this.slangWordRepository = new SlangWordRepositoryImpl();
    }


    @Override
    public List<SlangWordEntity> findAll() {
        return slangWordRepository.findAll();
    }

    @Override
    public SlangWordEntity findByWord(String word) {
        return slangWordRepository.findByWord(word);
    }

    @Override
    public List<SlangWordEntity> findByDefinition(String definition) {
        return slangWordRepository.findByDefinition(definition);
    }

    @Override
    public boolean add(SlangWordEntity slangWordEntity) {
        SlangWordEntity slangWord = slangWordRepository.findByWord(slangWordEntity.getWord());
        if (slangWord == null) {
            slangWordRepository.add(slangWordEntity);
            return true;
        }
        return false;
    }

    @Override
    public void addOverwrite(SlangWordEntity slangWordEntity) {
        SlangWordEntity slangWord = slangWordRepository.findByWord(slangWordEntity.getWord());
        if (slangWord != null) {
            slangWordRepository.update(slangWordEntity);
        }
    }

    @Override
    public void addDuplicate(SlangWordEntity slangWordEntity) {
        SlangWordEntity slangWord = slangWordRepository.findByWord(slangWordEntity.getWord());
        if (slangWord != null) {
            slangWordEntity.setDefinition(Stream.concat(slangWord.getDefinition().stream(), slangWordEntity.getDefinition().stream()).distinct().collect(Collectors.toList()));
            slangWordRepository.update(slangWordEntity);
        }
    }

    @Override
    public void remove(SlangWordEntity slangWordEntity) {
        SlangWordEntity slangWord = slangWordRepository.findByWord(slangWordEntity.getWord());
        if (slangWord != null) {
            slangWordRepository.remove(slangWordEntity);
        }
    }

    @Override
    public void reset() {
        slangWordRepository.reset();
    }


}

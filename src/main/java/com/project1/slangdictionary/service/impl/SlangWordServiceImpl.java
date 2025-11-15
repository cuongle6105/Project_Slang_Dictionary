package com.project1.slangdictionary.service.impl;

import com.project1.slangdictionary.dto.QuizQuestionDTO;
import com.project1.slangdictionary.entity.SlangWordEntity;
import com.project1.slangdictionary.repository.SlangWordRepository;
import com.project1.slangdictionary.repository.impl.SlangWordRepositoryImpl;
import com.project1.slangdictionary.service.SlangWordService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SlangWordServiceImpl implements SlangWordService {
    private final SlangWordRepository slangWordRepository;
    private final Random random;

    public SlangWordServiceImpl() {
        this.slangWordRepository = new SlangWordRepositoryImpl();
        this.random = new Random();
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
            slangWordEntity.setDefinition(Stream.concat(slangWord.getDefinition().stream(), slangWordEntity.getDefinition().stream()).distinct().toList());
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

    @Override
    public SlangWordEntity random() {
        List<SlangWordEntity> slangWordEntities = slangWordRepository.findAll();
        if (slangWordEntities.isEmpty()) {
            return null;
        }
        return slangWordEntities.get(random.nextInt(slangWordEntities.size()));
    }

    @Override
    public List<QuizQuestionDTO> createSlangWordsQuizQuestions(int numberOfQuestions, int numberOfOptions) {
        List<SlangWordEntity> slangWordEntities = slangWordRepository.findAll();
        List<QuizQuestionDTO> quizQuestionDTOS = new ArrayList<>();
        for (int i = 0; i < numberOfQuestions; i++) {
            SlangWordEntity correctSlangWord = slangWordEntities.get(random.nextInt(slangWordEntities.size()));
            String correctDefinition = correctSlangWord.getDefinition().get(random.nextInt(correctSlangWord.getDefinition().size()));
            List<String> options = new ArrayList<>();
            options.add(correctDefinition);
            while (options.size() < numberOfOptions) {
                SlangWordEntity candidate = slangWordEntities.get(random.nextInt(slangWordEntities.size()));
                if (candidate == correctSlangWord) continue;
                String wrongDefinition = candidate.getDefinition().get(random.nextInt(candidate.getDefinition().size()));
                if (options.contains(wrongDefinition) || correctSlangWord.getDefinition().contains(wrongDefinition)) continue;
                options.add(wrongDefinition);
            }
            Collections.shuffle(options);
            quizQuestionDTOS.add(new QuizQuestionDTO(correctSlangWord.getWord(), options, options.indexOf(correctDefinition)));
        }
        return quizQuestionDTOS;
    }

    @Override
    public List<QuizQuestionDTO> createDefinitionsQuizQuestions(int numberOfQuestions, int numberOfOptions) {
        List<SlangWordEntity> slangWordEntities = slangWordRepository.findAll();
        List<QuizQuestionDTO> quizQuestionDTOS = new ArrayList<>();
        for (int i = 0; i < numberOfQuestions; i++) {
            SlangWordEntity correctSlangWord = slangWordEntities.get(random.nextInt(slangWordEntities.size()));
            String definitionQuestion = correctSlangWord.getDefinition().get(random.nextInt(correctSlangWord.getDefinition().size()));
            List<String> options = new ArrayList<>();
            options.add(correctSlangWord.getWord());
            while (options.size() < numberOfOptions) {
                SlangWordEntity candidate = slangWordEntities.get(random.nextInt(slangWordEntities.size()));
                if (options.contains(candidate.getWord()) || candidate.getDefinition().contains(definitionQuestion)) continue;
                options.add(candidate.getWord());
            }
            Collections.shuffle(options);
            quizQuestionDTOS.add(new QuizQuestionDTO(definitionQuestion, options, options.indexOf(correctSlangWord.getWord())));
        }
        return quizQuestionDTOS;
    }


}

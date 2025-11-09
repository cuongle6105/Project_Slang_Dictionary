package com.project1.slangdictionary.repository.impl;

import com.project1.slangdictionary.entity.SlangWordEntity;
import com.project1.slangdictionary.repository.SlangWordRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SlangWordRepositoryImpl implements SlangWordRepository {
    private String path;
    private Map<String, SlangWordEntity> dictionary;

    public SlangWordRepositoryImpl() {
        this.path = "data/slang.txt";
        this.dictionary = readDictionary();
    }


    private Map<String, SlangWordEntity> readDictionary() {
        Map<String, SlangWordEntity> dictionary = new HashMap<>();
        try(BufferedReader br = new BufferedReader(new FileReader(path));) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("`", 2);
                if (parts.length != 2) continue;
                String word = parts[0];
                String[] definition = parts[1].split("\\|");
                dictionary.put(word, new SlangWordEntity(word, Arrays.asList(definition)));
            }
            return dictionary;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SlangWordEntity> findAll() {
        return List.copyOf(dictionary.values());
    }
}

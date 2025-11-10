package com.project1.slangdictionary.repository.impl;

import com.project1.slangdictionary.entity.SlangWordEntity;
import com.project1.slangdictionary.repository.SlangWordRepository;
import com.project1.slangdictionary.util.TokenUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class SlangWordRepositoryImpl implements SlangWordRepository {
    private String path;
    private Map<String, SlangWordEntity> dictionary;
    private Map<String, Set<String>> definitionTokens = new HashMap<>();

    public SlangWordRepositoryImpl() {
        this.path = "data/slang.txt";
        this.dictionary = readDictionary();
        this.definitionTokens = buildDefinitions();
    }

    private Map<String, Set<String>> buildDefinitions () {
        Map<String, Set<String>> definitions = new HashMap<>();
        for (SlangWordEntity slangWordEntity : dictionary.values()) {
            for (String definition : slangWordEntity.getDefinition()) {
                for (String token : TokenUtil.splitToTokens(definition)) {
                    Set<String> set = definitions.get(token);
                    if (set == null) {
                        set = new HashSet<>();
                        definitions.put(token, set);
                    }
                    set.add(slangWordEntity.getWord());
                }
            }
        }
        return definitions;
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

    @Override
    public SlangWordEntity findByWord(String word) {
        return dictionary.get(word);
    }

    @Override
    public List<SlangWordEntity> findByDefinition(String definition) {
        List<Set<String>> sets = new ArrayList<>();
        List<String> tokens = TokenUtil.splitToTokens(definition);
        if (tokens == null || tokens.isEmpty()) return Collections.emptyList();
        for (String token : tokens) {
            Set<String> set = definitionTokens.get(token);
            if (set == null || set.isEmpty()) return Collections.emptyList();
            sets.add(set);
        }
        sets.sort(Comparator.comparingInt(Set::size));
        Set<String> words = new HashSet<>(sets.getFirst());
        for (int i = 1; i < sets.size(); i++) {
            words.retainAll(sets.get(i));
            if (words.isEmpty()) return Collections.emptyList();
        }
        List<SlangWordEntity> result = new ArrayList<>();
        for (String word : words) {
            result.add(findByWord(word));
        }
        return result;
    }
}

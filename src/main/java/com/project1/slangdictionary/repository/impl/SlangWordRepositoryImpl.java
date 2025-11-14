package com.project1.slangdictionary.repository.impl;

import com.project1.slangdictionary.entity.SlangWordEntity;
import com.project1.slangdictionary.repository.SlangWordRepository;
import com.project1.slangdictionary.util.TokenUtil;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SlangWordRepositoryImpl implements SlangWordRepository {
    private String filePath;
    private Map<String, SlangWordEntity> dictionary;
    private Map<String, Set<String>> definitionTokens = new HashMap<>();

    public SlangWordRepositoryImpl() {
        this.filePath = "data/slang.txt";
        this.dictionary = readDictionary();
        this.definitionTokens = buildDefinitionTokens();
    }



    private Map<String, Set<String>> buildDefinitionTokens () {
        Map<String, Set<String>> definitions = new HashMap<>();
        for (SlangWordEntity slangWordEntity : dictionary.values()) {
            splitDefinitions(definitions, slangWordEntity);
        }
        return definitions;
    }

    private void splitDefinitions(Map<String, Set<String>> definitions, SlangWordEntity slangWordEntity) {
        for (String definition : slangWordEntity.getDefinition()) {
            for (String token : TokenUtil.splitToTokens(definition)) {
                Set<String> set = definitions.get(token);
                if (set == null || set.isEmpty()) {
                    set = new HashSet<>();
                    definitions.put(token, set);
                }
                set.add(slangWordEntity.getWord());
            }
        }
    }


    private Map<String, SlangWordEntity> readDictionary() {
        Map<String, SlangWordEntity> dictionary = new HashMap<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filePath));) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("`", 2);
                if (parts.length != 2) continue;
                String word = parts[0];
                String[] definition = parts[1].split("\\s*\\|\\s*");
                dictionary.put(word, new SlangWordEntity(word, Arrays.asList(definition)));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return dictionary;
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

    @Override
    public void add(SlangWordEntity slangWord) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(slangWord.getWord() + "`" + String.join("|", slangWord.getDefinition()));
            dictionary.put(slangWord.getWord(), slangWord);
            splitDefinitions(definitionTokens, slangWord);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(SlangWordEntity slangWord) {
        SlangWordEntity slangWordEntity = dictionary.get(slangWord.getWord());
        removeTokensInDefinitionTokens(slangWordEntity);

        dictionary.put(slangWord.getWord(), slangWord);
        splitDefinitions(definitionTokens, slangWord);
        writeFile();
    }

    private void removeTokensInDefinitionTokens(SlangWordEntity slangWordEntity) {
        for (String definition : slangWordEntity.getDefinition()) {
            for (String token : TokenUtil.splitToTokens(definition)) {
                Set<String> set = definitionTokens.get(token);
                if (set != null) {
                    set.remove(slangWordEntity.getWord());
                    if (set.isEmpty()) definitionTokens.remove(token);
                }
            }
        }
    }

    @Override
    public void remove(SlangWordEntity slangWord) {
        SlangWordEntity slangWordEntity = dictionary.get(slangWord.getWord());
        removeTokensInDefinitionTokens(slangWordEntity);
        dictionary.remove(slangWord.getWord());
        writeFile();
    }

    private void writeFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (SlangWordEntity slangWordEntity : dictionary.values()) {
                bw.write(slangWordEntity.getWord() + "`" + String.join("|", slangWordEntity.getDefinition()));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

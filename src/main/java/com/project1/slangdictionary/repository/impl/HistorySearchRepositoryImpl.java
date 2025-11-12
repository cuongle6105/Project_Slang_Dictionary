package com.project1.slangdictionary.repository.impl;

import com.project1.slangdictionary.entity.HistorySearchEntity;
import com.project1.slangdictionary.repository.HistorySearchRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class HistorySearchRepositoryImpl implements HistorySearchRepository {
    private String filePath;

    public HistorySearchRepositoryImpl() {
        this.filePath = "data/history.txt";
    }

    @Override
    public void saveHistorySearch(HistorySearchEntity historySearchEntity) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(historySearchEntity.toString());
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<HistorySearchEntity> findAll() {
        List<HistorySearchEntity> historySearchEntities = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while (line != null) {
                String[] split = line.split(",", 3);
                historySearchEntities.add(new HistorySearchEntity(split[0], split[1], split[2]));
                line = br.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return historySearchEntities;
    }

    @Override
    public void clearHistorySearch() throws IOException {
        if(!Files.exists(Path.of(filePath))) {
            return;
        }
        Files.write(Path.of(filePath), new byte[0], StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}

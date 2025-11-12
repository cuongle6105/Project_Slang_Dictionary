package com.project1.slangdictionary.service.impl;

import com.project1.slangdictionary.entity.HistorySearchEntity;
import com.project1.slangdictionary.repository.HistorySearchRepository;
import com.project1.slangdictionary.repository.impl.HistorySearchRepositoryImpl;
import com.project1.slangdictionary.service.HistorySearchService;

import java.io.IOException;
import java.util.List;

public class HistorySearchServiceImpl implements HistorySearchService {

    private final HistorySearchRepository historySearchRepository;

    public HistorySearchServiceImpl() {
        historySearchRepository = new HistorySearchRepositoryImpl();
    }

    @Override
    public void saveHistorySearch(HistorySearchEntity historySearchEntity) {
        historySearchRepository.saveHistorySearch(historySearchEntity);
    }

    @Override
    public List<HistorySearchEntity> findAll() {
        return historySearchRepository.findAll();
    }

    @Override
    public void clearHistorySearch() throws IOException {
        historySearchRepository.clearHistorySearch();
    }
}

package com.project1.slangdictionary.repository;

import com.project1.slangdictionary.entity.HistorySearchEntity;

import java.io.IOException;
import java.util.List;

public interface HistorySearchRepository {
    void saveHistorySearch(HistorySearchEntity historySearchEntity);
    List<HistorySearchEntity> findAll();
    void clearHistorySearch() throws IOException;
}

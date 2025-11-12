package com.project1.slangdictionary.entity;

import java.time.LocalDateTime;

public class HistorySearchEntity {
    String methodSearch;
    String time;
    String content;
    public HistorySearchEntity() {}
    public HistorySearchEntity(String methodSearch, String time, String content) {
        this.methodSearch = methodSearch;
        this.time = time;
        this.content = content;
    }

    public String getMethodSearch() {
        return methodSearch;
    }

    public void setMethodSearch(String methodSearch) {
        this.methodSearch = methodSearch;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String toString() {
        return methodSearch + "," + time + "," + content;
    }
}

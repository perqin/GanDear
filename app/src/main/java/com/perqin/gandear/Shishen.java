package com.perqin.gandear;

import java.util.List;

/**
 * Author   : perqin
 * Date     : 17-4-5
 */

public class Shishen {
    private String id;
    private String name;
    private String imageSrcUrl;
    private String clues;
    private List<String> queries;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageSrcUrl() {
        return imageSrcUrl;
    }

    public void setImageSrcUrl(String imageSrcUrl) {
        this.imageSrcUrl = imageSrcUrl;
    }

    public String getClues() {
        return clues;
    }

    public void setClues(String clues) {
        this.clues = clues;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getQueries() {
        return queries;
    }

    public void setQueries(List<String> queries) {
        this.queries = queries;
    }
}

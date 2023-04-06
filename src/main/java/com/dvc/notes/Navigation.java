package com.dvc.notes;

import java.io.Serializable;

public class Navigation implements Serializable {

    private String title;
    private String link;
    private Integer depth;
    
    public Navigation(String title, String link, Integer depth) {
	this.title = title;
	this.link = link;
	this.depth = depth;
    }

    public String getTitle() {
	return this.title;
    }

    public String getLink() {
	return this.link;
    }

    public Integer getDepth() {
	return this.depth;
    }

}

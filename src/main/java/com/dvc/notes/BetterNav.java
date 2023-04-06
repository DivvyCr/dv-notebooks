package com.dvc.notes;

import java.io.Serializable;
import java.util.ArrayList;

public class BetterNav implements Serializable {

    private Integer id;
    private String title;
    private String link;
    private String path;
    private ArrayList<BetterNav> children;
    
    public BetterNav(Integer id, String title, String link, String path, ArrayList<BetterNav> children) {
	this.id = id;
	this.title = title;
	this.link = link;
	this.path = path;
	this.children = children;
    }

    public Integer getId() {
	return this.id;
    }

    public String getTitle() {
	return this.title;
    }

    public String getLink() {
	return this.link;
    }

    public String getPath() {
	return this.path;
    }

    public ArrayList<BetterNav> getChildren() {
	return this.children;
    }

    public void addChild(BetterNav child) {
	this.children.add(child);
    }

}

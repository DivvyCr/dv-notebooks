package com.dvc.notes.relations;

import java.io.Serializable;
import java.util.ArrayList;

public class Navigation implements Serializable {

    private Integer id;
    private String title;
    private String link;
    private String path;
    private ArrayList<Navigation> children;
    
    public Navigation(Integer id, String title, String link, String path, ArrayList<Navigation> children) {
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

    public ArrayList<Navigation> getChildren() {
	return this.children;
    }

    public void addChild(Navigation child) {
	this.children.add(child);
    }

}

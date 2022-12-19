package com.dvc.notes;

import java.io.Serializable;
import java.util.ArrayList;

public class Navigation implements Serializable {

    private Integer noteid;
    private String link;
    private ArrayList<Navigation> children;
    
    public Navigation(Integer noteid, String link, ArrayList<Navigation> children) {
	this.noteid = noteid;
	this.link = link;
	this.children = children;
    }

    public Integer getNoteid() {
	return this.noteid;
    }

    public String getLink() {
	return this.link;
    }

    public ArrayList<Navigation> getChildren() {
	return this.children;
    }

}

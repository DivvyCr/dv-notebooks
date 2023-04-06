package com.dvc.notes;

import java.io.Serializable;

public class Book implements Serializable {
    private Integer id;
    private String code;
    private String title;
    private String description;

    public Book() {}
    
    public Book(Integer id, String code, String title, String description) {
	this.id = id;
	this.code = code;
	this.title = title;
	this.description = description;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getTitle() {
	return title;
    }
    
    public void setTitle(String title) {
	this.title = title;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

}

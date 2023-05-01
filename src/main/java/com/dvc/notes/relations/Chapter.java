package com.dvc.notes.relations;

import java.io.Serializable;

import com.dvc.notes.MarkdownRenderer;

public class Chapter implements Serializable {
    private Integer id;
    private String title;
    private String mdContent;
    private String htmlContent;

    private Integer parentID; // TODO: remove via dedicated POST-form class
    private Integer precedingID;

    public Chapter() {}
    
    public Chapter(Integer id, String title, String mdContent) {
        this.id = id;
        this.title = title;
        this.mdContent = mdContent;
        this.htmlContent = MarkdownRenderer.renderMarkdown(this.mdContent);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMdContent() {
        return mdContent;
    }

    public void setMdContent(String mdContent) {
        this.mdContent = mdContent;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public Integer getParentID() {
        return this.parentID;
    }
    
    public void setParentID(Integer parentID) {
        this.parentID = parentID;
    }

    public Integer getPrecedingID() {
        return this.precedingID;
    }
    
    public void setPrecedingID(Integer precedingID) {
        this.precedingID = precedingID;
    }

}

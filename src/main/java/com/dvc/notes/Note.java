package com.dvc.notes;

import java.io.Serializable;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class Note implements Serializable {
    private Integer id;
    private String moduleCode;
    private String title;
    private String content;

    public Note(Integer id, String moduleCode, String title, String content) {
	this.id = id;
	this.moduleCode = moduleCode;
	this.title = title;

	// Parser p = Parser.builder().build();
	// Node md = p.parse(content);
	// HtmlRenderer html = HtmlRenderer.builder().build();
	this.content = content; // html.render(md);
    }

    public Integer getId() {
	return id;
    }

    public String getModuleCode() {
	return moduleCode;
    }

    public String getTitle() {
	return title;
    }

    public String getContent() {
	return content;
    }

}

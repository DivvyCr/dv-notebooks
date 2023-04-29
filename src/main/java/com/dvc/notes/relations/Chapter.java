package com.dvc.notes.relations;

import java.io.Serializable;
import java.util.List;

import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.parser.Parser;
import com.dvc.notes.admonition.AdmonitionExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.data.MutableDataSet;

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

        MutableDataSet options = new MutableDataSet()
                .set(Parser.HTML_BLOCK_DEEP_PARSER, true)
                .set(Parser.INDENTED_CODE_BLOCK_PARSER, false)
                .set(Parser.EXTENSIONS, List.of(
                    AdmonitionExtension.create(),
                    AttributesExtension.create(),
                    TablesExtension.create()));

        Parser p = Parser.builder(options).build();
        Node md = p.parse(mdContent);
        HtmlRenderer html = HtmlRenderer.builder(options).build();
        this.htmlContent = html.render(md);
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

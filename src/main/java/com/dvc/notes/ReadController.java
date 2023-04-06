package com.dvc.notes;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ReadController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void addNavigationEntries(String bookCode, Model model) {
	String q = "SELECT * FROM chapters_navtree WHERE book_code = ? ORDER BY agg_priority DESC";
	List<BetterNav> navTree = jdbcTemplate.query(q, new BetterNavRowMapper(), bookCode);
	Collections.reverse(navTree);
	navTree.removeAll(Collections.singleton(null));
	model.addAttribute("navTree", navTree);
    }

    private Integer getChapterIdFromURL(String bookCode, String chapterKebab) {
	String idQuery = "SELECT chapterid FROM chapter_links WHERE link = ?";
	return jdbcTemplate.queryForObject(idQuery, Integer.class, "/"+bookCode+"/"+chapterKebab);
    }

    @GetMapping({"/read/{bookCode}", "/read/{bookCode}/"})
    public String redirectToFirstChapter(@PathVariable String bookCode, Model model) {
	String q = "SELECT link FROM first_chapters WHERE book_code = ?";
	String chapterLink = jdbcTemplate.queryForObject(q, String.class, bookCode);

	return ("redirect:/read" + chapterLink);
    }

    @GetMapping({"/read/{bookCode}/{chapterKebab}", "/read/{bookCode}/{chapterKebab}/"})
    public String readChapter(@PathVariable String bookCode, @PathVariable String chapterKebab, Model model) {
	String q = "SELECT * FROM chapters WHERE id = ?";
	Chapter chapter = jdbcTemplate.queryForObject(q, new ChapterRowMapper(), getChapterIdFromURL(bookCode, chapterKebab));
	model.addAttribute("chapterObj", chapter);

	addNavigationEntries(bookCode, model);
	return "read";
    }

}

package com.dvc.notes;

import com.dvc.notes.relations.Book;
import com.dvc.notes.relations.BookRowMapper;
import com.dvc.notes.relations.Chapter;
import com.dvc.notes.relations.ChapterRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public String home(Model model) {
        Chapter index = jdbcTemplate.queryForObject("SELECT * FROM chapters WHERE title = '.index'", new ChapterRowMapper());
        model.addAttribute("content", index.getHtmlContent());

        List<Book> storedBooks = jdbcTemplate.query("SELECT * FROM books", new BookRowMapper());
        model.addAttribute("books", storedBooks);

        model.addAttribute("pageTitle", "Crib Sheet");

        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Editor Access - Crib Sheet");
        return "login";
    }

    @GetMapping("/edit/index")
    public String editIndex(Model model) {
        String indexMd = jdbcTemplate.queryForObject("SELECT content FROM chapters WHERE title = '.index'", String.class);
        model.addAttribute("curContent", indexMd);
        model.addAttribute("pageTitle", "Index - Editing Crib Sheet");
        return "editors/index-editor";
    }

    @PostMapping("/edit/index")
    public String saveIndex(@RequestParam("new-content") String newContent, Model model) {
        jdbcTemplate.update("UPDATE chapters SET content = ? WHERE title = '.index'", newContent);
        return "redirect:/";
    }
}

package com.dvc.notes;

import com.dvc.notes.relations.Book;
import com.dvc.notes.relations.BookRowMapper;
import org.codehaus.groovy.tools.shell.IO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class HomeController {

    private final Path indexPath = Paths.get("src/main/resources/static/index.md");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public String home(Model model) {
        List<Book> storedBooks = jdbcTemplate.query("SELECT * FROM books", new BookRowMapper());
        model.addAttribute("books", storedBooks);
        model.addAttribute("pageTitle", "Crib Sheet");
        model.addAttribute("content", MarkdownRenderer.renderMarkdown(getIndexMd()));

        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Editor Access - Crib Sheet");
        return "login";
    }

    @GetMapping("/edit/index")
    public String editIndex(Model model) {
        model.addAttribute("curContent", getIndexMd());
        return "editors/index-editor";
    }

    @PostMapping("/edit/index")
    public String saveIndex(@RequestParam("new-content") String newContent, Model model) {
        try {
            Files.writeString(indexPath, newContent);
        } catch (IOException ignored) {} // TODO: Convert to an HTTP Error?

        return "redirect:/";
    }

    private String getIndexMd() {
        String indexMd = "";
        try {
            indexMd = Files.readString(indexPath);
        } catch (IOException ignored) {} // Ignore since we can simply return an empty String.
        return indexMd;
    }
}

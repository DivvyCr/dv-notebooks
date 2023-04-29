package com.dvc.notes;

import com.dvc.notes.relations.Book;
import com.dvc.notes.relations.BookRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public String home(Model model) {
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

}

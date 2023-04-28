package com.dvc.notes;

import com.dvc.notes.admonition.AdmonitionExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
public class EditController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void addNavigationEntries(Integer bookId, Model model) {
        String q = "SELECT * FROM chapters_navtree WHERE bookid = ? ORDER BY agg_priority DESC";
        List<BetterNav> navTree = jdbcTemplate.query(q, new BetterNavRowMapper(), bookId);
        Collections.reverse(navTree);
        navTree.removeAll(Collections.singleton(null));
        model.addAttribute("navTree", navTree);
    }

    @GetMapping("/make/chapter")
    public String newChapter(@RequestParam("id") Integer precedingChapterId, Model model) {
        String q1 = "SELECT parent_chapterid FROM navigation WHERE chapterid = ?";
        Integer parentChapterId = jdbcTemplate.queryForObject(q1, Integer.class, precedingChapterId);

        Chapter newChapter = new Chapter();
        newChapter.setPrecedingID(precedingChapterId);
        newChapter.setParentID(parentChapterId);
        model.addAttribute("chapterObj", newChapter);

        String q = "SELECT bookid FROM navigation WHERE chapterid = ?";
        Integer bookId = jdbcTemplate.queryForObject(q, Integer.class, precedingChapterId);
        addNavigationEntries(bookId, model);

        return "chapter-editor";
    }

    @PostMapping("/make/chapter")
    public String saveNewChapter(@RequestParam("id") Integer precedingChapterId, Chapter newChapter, Model model) {
        SimpleJdbcCall newChapterCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("append_chapter");
        SqlParameterSource newChapterParameters = new MapSqlParameterSource()
                .addValue("precedingId", precedingChapterId)
                .addValue("chapterTitle", newChapter.getTitle())
                .addValue("chapterContent", newChapter.getMdContent());
        Integer newChapterId = newChapterCall.executeFunction(Integer.class, newChapterParameters);

        String redirectLinkQuery = "SELECT link FROM chapter_links WHERE chapterid = ?";
        String redirectLink = jdbcTemplate.queryForObject(redirectLinkQuery, String.class, newChapterId);

        return ("redirect:/read" + redirectLink);
    }

    @GetMapping("/edit/chapter")
    public String editChapter(@RequestParam("id") Integer chapterId, Model model) {
        String q1 = "SELECT * FROM chapters WHERE id = ?";
        Chapter chapter = jdbcTemplate.queryForObject(q1, new ChapterRowMapper(), chapterId);
        model.addAttribute("chapterObj", chapter);

        String q2 = "SELECT bookid FROM navigation WHERE chapterid = ?";
        Integer bookId = jdbcTemplate.queryForObject(q2, Integer.class, chapterId);
        addNavigationEntries(bookId, model);

        return "chapter-editor";
    }

    @PostMapping("/edit/chapter")
    public String saveChapter(Chapter chapter, Model model) {
        jdbcTemplate.update("CALL update_chapter(?, ?, ?)", chapter.getId(), chapter.getTitle(), chapter.getMdContent());
        jdbcTemplate.update("CALL move_chapter(?, ?, ?)", chapter.getId(), chapter.getParentID(), chapter.getPrecedingID());

        String redirectLinkQuery = "SELECT link FROM chapter_links WHERE chapterid = ?";
        String redirectLink = jdbcTemplate.queryForObject(redirectLinkQuery, String.class, chapter.getId());

        return ("redirect:/read" + redirectLink);
    }

    @PostMapping(value = "/edit/chapter/preview", consumes = "text/markdown", produces = "text/markdown")
    public ResponseEntity<String> test(@RequestBody String temp) {
        MutableDataSet options = new MutableDataSet()
                .set(Parser.EXTENSIONS, List.of(AdmonitionExtension.create()));

        Parser p = Parser.builder(options).build();
        Node md = p.parse(temp);
        HtmlRenderer html = HtmlRenderer.builder(options).build();
        String res = html.render(md);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/delete/chapter")
    public String deleteNote(@RequestParam("id") Integer chapterId, Model model) {
        jdbcTemplate.update("CALL delete_chapter(?)", chapterId);

        return "redirect:/"; // TODO
    }

    @GetMapping({"/make/book", "/make/book/"})
    public String newBook(Model model) {
        model.addAttribute("bookObj", new Book());
        return "book-editor";
    }

    @PostMapping({"/make/book", "/make/book/"})
    public String saveNewBook(Book newBook, Model model) {
        SimpleJdbcCall newBookCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("create_book");
        SqlParameterSource newBookParameters = new MapSqlParameterSource()
                .addValue("bookCode", newBook.getCode().toLowerCase())
                .addValue("bookTitle", newBook.getTitle())
                .addValue("bookDescription", newBook.getDescription());
        Integer newBookId = newBookCall.executeFunction(Integer.class, newBookParameters);

        String redirectLinkQuery = "SELECT link FROM first_chapters WHERE book_code = ?";
        String redirectLink = jdbcTemplate.queryForObject(redirectLinkQuery, String.class, newBook.getCode());

        return ("redirect:/read" + redirectLink);
    }

    @GetMapping("/edit/book")
    public String editBook(@RequestParam("id") Integer bookId, Model model) {
        String q = "SELECT * FROM books WHERE id = ?";
        Book book = jdbcTemplate.queryForObject(q, new BookRowMapper(), bookId);
        model.addAttribute("bookObj", book);
        return "book-editor";
    }

    @PostMapping("/edit/book")
    public String saveModule(Book book, Model model) {
        jdbcTemplate.update("CALL update_book(?, ?, ?, ?)", book.getId(), book.getCode(), book.getTitle(), book.getDescription());

        String redirectLinkQuery = "SELECT link FROM first_chapters WHERE book_code = ?";
        String redirectLink = jdbcTemplate.queryForObject(redirectLinkQuery, String.class, book.getCode());

        return ("redirect:/read" + redirectLink);
    }

    @PostMapping("/delete/book")
    public String deleteBook(@RequestParam("id") Integer bookId, Model model) {
        jdbcTemplate.update("CALL delete_book(?)", bookId);

        return "redirect:/"; // TODO
    }

}

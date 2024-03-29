package com.dvc.notes;

import com.dvc.notes.relations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.error.Mark;

import java.util.Collections;
import java.util.List;

@Controller
public class EditController {

    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    private String badRequest() {
        return "errors/400";
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void addNavigationEntries(Integer bookId, Model model) {
        String q = "SELECT * FROM chapters_navtree WHERE bookid = ? ORDER BY agg_priority DESC";
        List<Navigation> navTree = jdbcTemplate.query(q, new NavigationRowMapper(), bookId);
        Collections.reverse(navTree);
        navTree.removeAll(Collections.singleton(null));
        model.addAttribute("navTree", navTree);
    }

    private void setPageTitle(String title, Model model) {
        model.addAttribute("pageTitle", title + " - Editing Crib Sheet");
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
        setPageTitle("New Chapter", model);

        return "editors/chapter-editor";
    }

    @PostMapping("/make/chapter")
    public String saveNewChapter(@RequestParam("id") Integer precedingChapterId, Chapter newChapter, Model model) {
        SimpleJdbcCall newChapterCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("append_chapter");
        SqlParameterSource newChapterParameters = new MapSqlParameterSource()
                .addValue("parentId", newChapter.getParentID())
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
        setPageTitle(chapter.getTitle(), model);

        return "editors/chapter-editor";
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
    public ResponseEntity<String> generatePreview(@RequestBody String markdown) {
        String preview = MarkdownRenderer.renderMarkdown(markdown);
        return new ResponseEntity<>(preview, HttpStatus.OK);
    }

    @PostMapping("/delete/chapter")
    public String deleteChapter(@RequestParam("id") Integer chapterId, Model model) {
        SimpleJdbcCall deleteChapterCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("delete_chapter");
        SqlParameterSource deleteChapterParameters = new MapSqlParameterSource()
                .addValue("deleteChapterId", chapterId);
        Integer bookId = deleteChapterCall.executeFunction(Integer.class, deleteChapterParameters);

        String bookCode = jdbcTemplate.queryForObject("SELECT code FROM books WHERE id = ?", String.class, bookId);

        return "redirect:/read/" + bookCode;
    }

    @GetMapping({"/make/book", "/make/book/"})
    public String newBook(Model model) {
        model.addAttribute("bookObj", new Book());
        setPageTitle("New Book", model);
        return "editors/book-editor";
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
        setPageTitle(book.getTitle(), model);
        return "editors/book-editor";
    }

    @PostMapping("/edit/book")
    public String saveBook(Book book, Model model) {
        jdbcTemplate.update("CALL update_book(?, ?, ?, ?)", book.getId(), book.getCode(), book.getTitle(), book.getDescription());

        String redirectLinkQuery = "SELECT link FROM first_chapters WHERE book_code = ?";
        String redirectLink = jdbcTemplate.queryForObject(redirectLinkQuery, String.class, book.getCode());

        return ("redirect:/read" + redirectLink);
    }

    @PostMapping("/delete/book")
    public String deleteBook(@RequestParam("id") Integer bookId, Model model) {
        jdbcTemplate.update("CALL delete_book(?)", bookId);

        return "redirect:/";
    }

}

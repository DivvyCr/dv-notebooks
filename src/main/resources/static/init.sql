CREATE TABLE books
(
    id          INTEGER GENERATED ALWAYS AS IDENTITY,
    code        VARCHAR(5)   NOT NULL CHECK (code ~ '^[a-zA-Z0-9_\-]+$'), /* don't allow whitespace */
    title       VARCHAR(64)  NOT NULL,
    description VARCHAR(256) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (code)
);

CREATE TABLE chapters
(
    id      INTEGER GENERATED ALWAYS AS IDENTITY,
    title   TEXT NOT NULL,
    content TEXT,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX home_chapter ON chapters (title) WHERE title = '.index';
INSERT INTO chapters (title, content) VALUES ('.index', '');

CREATE TABLE navigation
(
    bookid           INTEGER NOT NULL,
    chapterid        INTEGER NOT NULL,
    parent_chapterid INTEGER NOT NULL,
    priority         INTEGER NOT NULL CHECK (priority > 0),
    FOREIGN KEY (bookid) REFERENCES books (id),
    FOREIGN KEY (chapterid) REFERENCES chapters (id) ON DELETE CASCADE,
    FOREIGN KEY (parent_chapterid) REFERENCES chapters (id),
    UNIQUE (bookid, chapterid)
);

CREATE TABLE editors
(
    username VARCHAR(32) NOT NULL,
    password TEXT        NOT NULL,
    PRIMARY KEY (username)
);

CREATE VIEW chapter_links AS
SELECT b.id                                                      AS bookid,
       c.id                                                      AS chapterid,
       '/' || b.code || '/' || REPLACE(LOWER(c.title), ' ', '-') AS link
FROM navigation n
         JOIN books b ON n.bookid = b.id
         JOIN chapters c ON n.chapterid = c.id;

CREATE VIEW chapters_tree AS
WITH RECURSIVE cte AS (SELECT bookid,
                              chapterid,
                              parent_chapterid,
                              priority::TEXT AS agg_priority
                       FROM navigation
                       WHERE parent_chapterid = chapterid
                       UNION ALL
                       SELECT nav.bookid,
                              nav.chapterid,
                              nav.parent_chapterid,
                              cte.agg_priority || '.' || nav.priority
                       FROM cte
                                JOIN navigation nav
                                     ON cte.chapterid = nav.parent_chapterid AND cte.chapterid != nav.chapterid)
SELECT b.id    AS bookid,
       b.code  AS book_code,
       c.id    AS chapterid,
       c.title AS chapter_title,
       cte.parent_chapterid,
       agg_priority
FROM cte
         JOIN chapters c ON c.id = cte.chapterid
         JOIN books b ON b.id = cte.bookid
ORDER BY agg_priority;

CREATE OR REPLACE VIEW chapters_navtree AS
WITH children_cte AS (SELECT parent_chapterid,
                             array_agg(chapterid) AS children
                      FROM navigation
                      WHERE parent_chapterid != chapterid
                      GROUP BY parent_chapterid)
SELECT t.bookid,
       t.book_code,
       t.chapterid,
       t.chapter_title,
       t.parent_chapterid,
       c.children,
       l.link,
       t.agg_priority /* to allow re-ordering */
FROM chapters_tree t
         JOIN chapter_links l ON l.chapterid = t.chapterid
         LEFT OUTER JOIN children_cte c ON c.parent_chapterid = t.chapterid;

CREATE OR REPLACE VIEW first_chapters AS
WITH cte AS (SELECT bookid,
                    book_code,
                    chapterid,
                    agg_priority,
                    ROW_NUMBER() OVER (PARTITION BY bookid ORDER BY agg_priority) AS top
             FROM chapters_tree)
SELECT cte.book_code,
       cte.chapterid,
       l.link,
       cte.agg_priority
FROM cte
         JOIN chapter_links l ON cte.chapterid = l.chapterid
WHERE top = 1;

/* ============================================================================================== */

CREATE OR REPLACE FUNCTION safe_delete_chapter() RETURNS TRIGGER AS
$$
DECLARE
    delete_chapterid INTEGER;
    delete_parentid  INTEGER;
    delete_priority  INTEGER;
BEGIN
    SELECT OLD.id INTO delete_chapterid;

    SELECT priority FROM navigation WHERE chapterid = delete_chapterid INTO delete_priority;
    SELECT parent_chapterid FROM navigation WHERE chapterid = delete_chapterid INTO delete_parentid;

    UPDATE navigation
    SET priority = priority - 1
    WHERE priority >= delete_priority
      AND priority > 1
      AND (parent_chapterid = delete_parentid OR (parent_chapterid IS NULL AND delete_parentid IS NULL));

    IF delete_parentid = delete_chapterid THEN
        UPDATE navigation
        SET parent_chapterid = chapterid
        WHERE parent_chapterid = delete_parentid;
    ELSE
        UPDATE navigation
        SET parent_chapterid = delete_parentid
        WHERE parent_chapterid = delete_chapterid;
    END IF;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER chapter_delete
    BEFORE DELETE
    ON chapters
    FOR EACH ROW
EXECUTE PROCEDURE safe_delete_chapter();

/* ============================================================================================== */

CREATE OR REPLACE FUNCTION create_book(bookCode VARCHAR(5), bookTitle VARCHAR(50), bookDescription VARCHAR(200))
    RETURNS INTEGER AS
$$
DECLARE
    new_bookid INTEGER;
BEGIN
    INSERT INTO books (code, title, description)
    VALUES (bookCode, bookTitle, bookDescription)
    RETURNING id INTO new_bookid;

    PERFORM create_chapter(new_bookid, NULL, NULL, bookCode || ' Intro', ''); /* PERFORM instead of SELECT since we are ignoring return value */

    RETURN new_bookid;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE update_book(bookId INTEGER, newBookCode VARCHAR(5), newBookTitle VARCHAR(50),
                                        newBookDescription VARCHAR(200))
AS
$$
BEGIN
    UPDATE books
    SET code        = newBookCode,
        title       = newBookTitle,
        description = newBookDescription
    WHERE id = bookId;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE delete_book(deleteBookId INTEGER)
AS
$$
BEGIN
    DELETE
    FROM chapters c
        USING navigation n
    WHERE n.bookid = deleteBookId
      AND c.id = n.chapterid;

    DELETE FROM books WHERE id = deleteBookId;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION append_chapter(parentId INTEGER, precedingId INTEGER, chapterTitle VARCHAR(50),
                                          chapterContent TEXT)
    RETURNS INTEGER AS
$$
DECLARE
    new_bookid    INTEGER;
    new_chapterid INTEGER;
BEGIN
    SELECT bookid FROM navigation WHERE chapterid = precedingId INTO new_bookid;
    SELECT create_chapter(new_bookid, parentId, precedingId, chapterTitle, chapterContent) INTO new_chapterid;
    RETURN new_chapterid;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION create_chapter(book_id INTEGER, parentId INTEGER, precedingId INTEGER,
                                          chapterTitle VARCHAR(50),
                                          chapterContent TEXT)
    RETURNS INTEGER AS
$$
DECLARE
    new_chapterid INTEGER;
    new_parentid  INTEGER;
    new_priority  INTEGER;
BEGIN
    INSERT INTO chapters (title, content) VALUES (chapterTitle, chapterContent) RETURNING id INTO new_chapterid;

    IF parentId IS NULL THEN
        SELECT new_chapterid INTO new_parentid;
    ELSE
        SELECT parentId INTO new_parentid;
    END IF;

    IF precedingId IS NULL THEN
        SELECT 1 INTO new_priority;
    ELSE
        SELECT priority + 1 FROM navigation WHERE chapterid = precedingId INTO new_priority;
    END IF;

    UPDATE navigation
    SET priority = priority + 1
    WHERE priority >= new_priority
      AND (parent_chapterid = new_parentid OR (new_parentid = new_chapterid AND parent_chapterid = chapterid));

    INSERT INTO navigation (bookid, chapterid, parent_chapterid, priority)
    VALUES (book_id, new_chapterid, new_parentid, new_priority);

    RETURN new_chapterid;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE update_chapter(chapterId INTEGER, newChapterTitle VARCHAR(50), newChapterContent TEXT)
AS
$$
BEGIN
    UPDATE chapters
    SET title   = newChapterTitle,
        content = newChapterContent
    WHERE id = chapterId;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delete_chapter(deleteChapterId INTEGER)
    RETURNS INTEGER AS
$$
DECLARE
    delete_bookid INTEGER;
BEGIN
    /* Ensure at least one chapter remains: */

    SELECT bookid FROM navigation WHERE chapterid = deleteChapterId INTO delete_bookid;
    IF (SELECT COUNT(*) FROM navigation WHERE bookid = delete_bookid) <= 1
    THEN
        RETURN -1;
    END IF;

    /* Carry out deletion: */

    DELETE FROM chapters WHERE id = deleteChapterId;

    RETURN delete_bookid;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE move_chapter(moveChapterId INTEGER, newParentId INTEGER, newPrecedingId INTEGER)
AS
$$
DECLARE
    old_parentid   INTEGER;
    old_priority   INTEGER;
    new_parentid   INTEGER;
    new_priority   INTEGER;
BEGIN
    SELECT priority FROM navigation WHERE chapterid = moveChapterId INTO old_priority;
    SELECT parent_chapterid FROM navigation WHERE chapterid = moveChapterId INTO old_parentid;

    IF newParentId IS NULL THEN
        SELECT moveChapterId INTO new_parentid;
    ELSE
        SELECT newParentId INTO new_parentid;
    END IF;

    IF newPrecedingId IS NULL THEN
        SELECT 1 INTO new_priority;
    ELSE
        SELECT priority + 1 FROM navigation WHERE chapterid = newPrecedingId INTO new_priority;
    END IF;

    IF old_parentid = new_parentid THEN
        IF new_priority > old_priority THEN
            SELECT new_priority - 1 INTO new_priority; /* Account for the shift of other priorities. */

            UPDATE navigation
            SET priority = priority - 1
            WHERE priority <= new_priority
              AND priority > old_priority
              /* Second part of OR accounts for 'root' entries, where ParentID == ChapterID: */
              AND (parent_chapterid = new_parentid OR (new_parentid = moveChapterId AND parent_chapterid = chapterid));
        END IF;

        IF new_priority < old_priority THEN
            UPDATE navigation
            SET priority = priority + 1
            WHERE priority >= new_priority
              AND priority < old_priority
              /* Second part of OR accounts for 'root' entries, where ParentID == ChapterID: */
              AND (parent_chapterid = new_parentid OR (new_parentid = moveChapterId AND parent_chapterid = chapterid));
        END IF;
    ELSE
        UPDATE navigation SET priority = priority - 1 WHERE priority > old_priority AND parent_chapterid = old_parentid;
        UPDATE navigation SET priority = priority + 1 WHERE priority >= new_priority AND parent_chapterid = new_parentid;
    END IF;

    UPDATE navigation SET parent_chapterid = new_parentid, priority = new_priority WHERE chapterid = moveChapterId;
END;
$$ LANGUAGE plpgsql;


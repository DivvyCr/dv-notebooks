CREATE OR REPLACE VIEW paths AS
WITH RECURSIVE cte AS (
    SELECT
	moduleid,
        noteid,
        parent_noteid,
        priority::text path
    FROM nav
    WHERE parent_noteid IS NULL
  UNION ALL
    SELECT
	nav.moduleid,
        nav.noteid,
        nav.parent_noteid,
        cte.path || '.' || nav.priority
    FROM
        cte
        JOIN nav ON cte.noteid = nav.parent_noteid
)
SELECT modules.id AS moduleid, code, notes.id AS noteid, title, link, path, parent_noteid
FROM
    cte
    JOIN notes ON notes.id = cte.noteid
    JOIN modules ON modules.id = cte.moduleid
ORDER BY path;

CREATE OR REPLACE VIEW pathschildren AS
WITH cte AS (
  SELECT
    parent_noteid,
    array_agg(noteid) AS children
  FROM nav
  WHERE parent_noteid IS NOT NULL
  GROUP BY parent_noteid
)
SELECT
  code,
  noteid,
  paths.parent_noteid,
  children,
  title,
  path,
  link
FROM
  paths
  LEFT OUTER JOIN cte ON cte.parent_noteid = paths.noteid;

CREATE OR REPLACE VIEW firstnav AS
SELECT code,notes.id,title,content
FROM
    notes
    JOIN nav ON nav.noteid = notes.id
    JOIN modules ON modules.id = nav.moduleid
ORDER BY parent_noteid IS NOT NULL, priority;

CREATE OR REPLACE FUNCTION make_note(modulecode VARCHAR(5), precedingid INTEGER)
  RETURNS INTEGER AS $$
DECLARE
  new_noteid INTEGER;
  new_parentid INTEGER;
  new_moduleid INTEGER;
  new_priority INTEGER;
BEGIN
  INSERT INTO notes (title, content) VALUES ('New Note', '') RETURNING id INTO new_noteid;

  IF precedingid IS NULL THEN
    SELECT 1 INTO new_priority;
    SELECT NULL INTO new_parentid;
   ELSE
     SELECT priority+1 FROM nav WHERE noteid = precedingid INTO new_priority;
     SELECT parent_noteid FROM nav WHERE noteid = precedingid INTO new_parentid;
   END IF;
  
  SELECT id FROM modules WHERE code = modulecode INTO new_moduleid;
  
  UPDATE nav SET priority = priority+1 WHERE priority >= new_priority AND (parent_noteid = new_parentid OR (parent_noteid IS NULL AND new_parentid IS NULL));
  INSERT INTO nav (moduleid, noteid, parent_noteid, priority) VALUES (new_moduleid, new_noteid, new_parentid, new_priority);

  RETURN new_noteid;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE delete_note(deleteid INTEGER)
  AS $$
DECLARE
  delete_parentid INTEGER;
  delete_priority INTEGER;
BEGIN
  DELETE FROM notes WHERE id = deleteid;

  SELECT parent_noteid FROM nav WHERE noteid = deleteid INTO delete_parentid;
  SELECT priority FROM nav WHERE noteid = deleteid INTO delete_priority;
  DELETE FROM nav WHERE noteid = deleteid;
  UPDATE nav SET priority = priority-1 WHERE priority >= delete_priority AND (parent_noteid = delete_parentid OR (parent_noteid IS NULL AND delete_parentid IS NULL));
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE move_note(movedid INTEGER, new_parentid INTEGER, new_precedingid INTEGER)
  AS $$
DECLARE
  old_parentid INTEGER;
  old_priority INTEGER;
  new_priority INTEGER;
BEGIN
  SELECT parent_noteid FROM nav WHERE noteid = movedid INTO old_parentid;
  SELECT priority FROM nav WHERE noteid = movedid INTO old_priority;

  IF new_precedingid IS NULL THEN
    SELECT 1 INTO new_priority;
  ELSE
    SELECT priority+1 FROM nav WHERE noteid = new_precedingid INTO new_priority;
  END IF;

  IF old_parentid = new_parentid OR (old_parentid IS NULL AND new_parentid IS NULL) THEN
    IF new_priority > old_priority THEN
      UPDATE nav SET priority = priority-1 WHERE priority <= new_priority AND priority > old_priority AND (parent_noteid = new_parentid OR (parent_noteid IS NULL AND new_parentid IS NULL));
    END IF;
    IF new_priority < old_priority THEN
      UPDATE nav SET priority = priority+1 WHERE priority >= new_priority AND priority < old_priority AND (parent_noteid = new_parentid OR (parent_noteid IS NULL AND new_parentid IS NULL));
    END IF;
  ELSE
    UPDATE nav SET priority = priority-1 WHERE priority > old_priority AND (parent_noteid = old_parentid OR (parent_noteid IS NULL AND old_parentid IS NULL));
    UPDATE nav SET priority = priority+1 WHERE priority >= new_priority AND (parent_noteid = new_parentid OR (parent_noteid IS NULL AND new_parentid IS NULL));
  END IF;

  UPDATE nav SET parent_noteid = new_parentid, priority = new_priority WHERE noteid = movedid;
END;
$$ LANGUAGE plpgsql;


package com.dvc.notes.relations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.jdbc.core.RowMapper;

public class NavigationRowMapper implements RowMapper<Navigation> {

    private final HashMap<Integer, Navigation> idToNav = new HashMap<>();

    @Override
    public Navigation mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Integer noteID = resultSet.getInt("chapterid");
        String noteTitle = resultSet.getString("chapter_title");
        String noteLink = resultSet.getString("link");
        String notePath = resultSet.getString("agg_priority");

        ArrayList<Navigation> noteChildren = new ArrayList<>();
        Object x = resultSet.getArray("children");
        if (x != null) {
            Integer[] childrenIDs = (Integer[]) resultSet.getArray("children").getArray();
            for (Integer id : childrenIDs) {
                noteChildren.add(idToNav.get(id));
            }

            noteChildren.sort((bn1, bn2) -> bn1.getPath().compareToIgnoreCase(bn2.getPath()));
        }

        // TODO: consider improving (?) whole navigation system

        Navigation newNav = new Navigation(noteID, noteTitle, noteLink, notePath, noteChildren);

        this.idToNav.put(noteID, newNav);

        int noteParent = resultSet.getInt("parent_chapterid");
        return (noteParent == noteID) ? newNav : null;
    }

}

package com.dvc.notes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.springframework.jdbc.core.RowMapper;

public class BetterNavRowMapper implements RowMapper<BetterNav> {

    private HashMap<Integer, BetterNav> idToNav = new HashMap<>();
    
    @Override
    public BetterNav mapRow(ResultSet resultSet, int rowNum) throws SQLException {
	Integer noteID = resultSet.getInt("chapterid");
	String noteTitle = resultSet.getString("chapter_title");
	String noteLink = resultSet.getString("link");
	String notePath = resultSet.getString("agg_priority");

	ArrayList<BetterNav> noteChildren = new ArrayList<>();
	Object x = resultSet.getArray("children");
	if (x != null) {
	    Integer[] childrenIDs = (Integer[]) resultSet.getArray("children").getArray();
	    for (Integer id : childrenIDs) {
		noteChildren.add(idToNav.get(id));
	    }

	    Collections.sort(noteChildren,new Comparator<BetterNav>() {
		    @Override
		    public int compare(BetterNav bn1, BetterNav bn2) {
			return bn1.getPath().compareToIgnoreCase(bn2.getPath());
		    }
		});
	}

	// TODO: improve/change whole navigation system

	BetterNav newNav = new BetterNav(noteID, noteTitle, noteLink, notePath, noteChildren);

	this.idToNav.put(noteID, newNav);

	Integer noteParent = resultSet.getInt("parent_chapterid");
	return (noteParent == noteID) ? newNav : null;
    }
    
}

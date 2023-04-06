package com.dvc.notes;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class NavigationRowMapper implements RowMapper<Navigation> {

    @Override
    public Navigation mapRow(ResultSet resultSet, int rowNum) throws SQLException {
	String noteTitle = resultSet.getString("title");
	String noteLink = resultSet.getString("link");
	String notePath = resultSet.getString("path");

	return new Navigation(noteTitle, noteLink, notePath.split("\\.").length - 1);
    }
    
}

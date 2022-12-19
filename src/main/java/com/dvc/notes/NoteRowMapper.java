package com.dvc.notes;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class NoteRowMapper implements RowMapper<Note> {

    @Override
    public Note mapRow(ResultSet resultSet, int rowNum) throws SQLException {
	return new Note(resultSet.getInt("id"),
			resultSet.getString("code"),
			resultSet.getString("title"),
			resultSet.getString("content"));
    }
    
}

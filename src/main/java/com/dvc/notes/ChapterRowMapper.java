package com.dvc.notes;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ChapterRowMapper implements RowMapper<Chapter> {

    @Override
    public Chapter mapRow(ResultSet resultSet, int rowNum) throws SQLException {
	return new Chapter(resultSet.getInt("id"),
			   resultSet.getString("title"),
			   resultSet.getString("content"));
    }
    
}

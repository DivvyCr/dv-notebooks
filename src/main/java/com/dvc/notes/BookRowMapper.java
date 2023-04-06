package com.dvc.notes;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class BookRowMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet resultSet, int rowNum) throws SQLException {
	return new Book(resultSet.getInt("id"),
			resultSet.getString("code"),
			resultSet.getString("title"),
			resultSet.getString("description"));
    }
    
}

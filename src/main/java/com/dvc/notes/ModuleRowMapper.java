package com.dvc.notes;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ModuleRowMapper implements RowMapper<Module> {

    @Override
    public Module mapRow(ResultSet resultSet, int rowNum) throws SQLException {
	return new Module(resultSet.getInt("id"),
			  resultSet.getString("code"),
			  resultSet.getString("name"),
			  resultSet.getString("description"));
    }
    
}

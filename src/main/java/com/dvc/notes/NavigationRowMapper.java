package com.dvc.notes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.jdbc.core.RowMapper;

public class NavigationRowMapper implements RowMapper<Navigation> {

    private HashMap<Integer, Navigation> temp = new HashMap<>();
    
    @Override
    public Navigation mapRow(ResultSet resultSet, int rowNum) throws SQLException {
	Integer id = resultSet.getInt("noteid");
	if (temp.containsKey(id)) {
	    return temp.get(id);
	}
	
	ArrayList<Navigation> children = new ArrayList<>();
	for (Integer i : (Integer[])resultSet.getArray("children").getArray()) {
	    children.add(temp.get(i));
	}
	Navigation n = new Navigation(id, "", children);
	temp.put(id,n);
	return n;
    }
    
}

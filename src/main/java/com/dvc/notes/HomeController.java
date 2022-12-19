package com.dvc.notes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public String home(Model model) {
	String moduleQuery = "SELECT * FROM modules";
	model.addAttribute("modules", jdbcTemplate.query(moduleQuery, new ModuleRowMapper()));
	return "index";
    }

    @GetMapping("/CS{moduleCode}")
    public String test(@PathVariable String moduleCode, Model model) {
	NavigationRowMapper nrm = new NavigationRowMapper();

	String initNavQuery = "WITH x AS (SELECT parent_noteid, array_agg(noteid) FROM nav WHERE parent_noteid IS NOT NULL GROUP BY parent_noteid) SELECT noteid,COALESCE(array_agg, array[]::integer[]) AS children,priority AS order FROM nav LEFT JOIN x ON nav.noteid=x.parent_noteid";
	jdbcTemplate.query(initNavQuery, nrm);

	String renderNavQuery = "WITH x AS (SELECT parent_noteid, array_agg(noteid) FROM nav WHERE parent_noteid IS NOT NULL GROUP BY parent_noteid), y AS (SELECT array_agg(noteid) FROM nav WHERE parent_noteid IS NOT NULL) SELECT noteid,COALESCE(x.array_agg, array[]::integer[]) AS children,priority AS order FROM nav LEFT JOIN x ON nav.noteid=x.parent_noteid CROSS JOIN y WHERE nav.noteid != ALL(y.array_agg)";
	model.addAttribute("entries", jdbcTemplate.query(renderNavQuery, nrm));
	return "book";
    }

}

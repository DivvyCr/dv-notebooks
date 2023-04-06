package com.dvc.notes;

import java.io.Serializable;
import java.util.ArrayList;

public class NavigationForm implements Serializable {

    private ArrayList<Navigation> navList;
    
    public NavigationForm() {
	
    }

    public ArrayList<Navigation> getNavList() {
	return this.navList;
    }

    public void addNavItem(Navigation nav) {
	this.navList.add(nav);
    }
    
}

package phonebook.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AccountType extends AbstractModelObject {
    
	private List<Category> m_categories = new ArrayList();;
	private String name = "";
	
	public AccountType(String n) {
		name = n;
	}
	
	public String getName() {
		if (name==null) name = "";
		return name;
	}

	public void setName(String n) {
		String oldValue = name;
		name = n;
		firePropertyChange("name", oldValue, name);
	}
	
	public void addCategory(Category e) {
		m_categories.add(e);
		firePropertyChange("categories", null, m_categories);
	}

	public void removeCategory(Category e) {
		m_categories.remove(e);
		firePropertyChange("categories", null, m_categories);
	}

	public List getCategories() {
		return m_categories;
	}
	
	public String toString() {
		return name;
	}
	
	public double getSum() {
	    double total = 0;

	    for (Iterator<Category> i1 = m_categories.iterator(); i1.hasNext();) {
	    	Category c = i1.next();
	        for (Iterator<DownloadEntry> i2 = c.getEntries().iterator(); i2.hasNext();) {
	            DownloadEntry d = i2.next();
	            total += d.getAmount();
	        }
	    }
	    return total;
	}
	
}


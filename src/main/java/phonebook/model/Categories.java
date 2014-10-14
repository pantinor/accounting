package phonebook.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Categories extends AbstractModelObject {
	private final List<Category> m_categories = new ArrayList();

	public void addCategory(Category c) {
		m_categories.add(c);
		firePropertyChange("categories", null, m_categories);
	}

	public void removeCategory(Category c) {
		m_categories.remove(c);
		firePropertyChange("categories", null, m_categories);
	}

	public List getCategories() {
		return m_categories;
	}
	
	public void clear() {
		m_categories.clear();
	}
	
	public Category findCategory(String name) {
		Category category = null;
    	for (Iterator<Category> iter = m_categories.iterator(); iter.hasNext();) {
    		Category c = iter.next();
    		if (c.getName().equals(name)) {
    			category = c;
    		}
    	}
    	return category;
	}
	
}

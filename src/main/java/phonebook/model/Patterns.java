package phonebook.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Patterns extends AbstractModelObject {
	private final List<CategoryPattern> m_patterns = new ArrayList();

	public void addPattern(CategoryPattern c) {
		m_patterns.add(c);
		firePropertyChange("patterns", null, m_patterns);
	}

	public void removePattern(CategoryPattern c) {
		m_patterns.remove(c);
		firePropertyChange("patterns", null, m_patterns);
	}

	public List getPatterns() {
		return m_patterns;
	}
	
	public void clear() {
		m_patterns.clear();
	}
	
	public CategoryPattern findPattern(String p) {
		CategoryPattern pattern = null;
    	for (Iterator<CategoryPattern> iter = m_patterns.iterator(); iter.hasNext();) {
    		CategoryPattern c = iter.next();
    		if (c.getPattern().equals(p)) {
    			pattern = c;
    		}
    	}
    	return pattern;
	}
}

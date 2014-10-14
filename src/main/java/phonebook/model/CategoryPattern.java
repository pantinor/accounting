package phonebook.model;

import java.util.regex.*;

public class CategoryPattern extends AbstractModelObject {
	
	private String patternStr = "";
	private Pattern pattern = null; 
	private Category category = null;
	
	public CategoryPattern() {
	}
	
	public CategoryPattern(String p, Category c) {
		patternStr = p;
		category = c;
		pattern = Pattern.compile(patternStr); 
	}

	public String getPattern() {
		return patternStr;
	}

	public void setPattern(String p) {
		String oldValue = patternStr;
		patternStr = p;
		pattern = Pattern.compile(patternStr); 
		firePropertyChange("pattern", oldValue, patternStr);
	}
	
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category c) {
		Category oldValue = category;
		category = c;
		firePropertyChange("category", oldValue, category);
	}
	
	public String toString() {
		String name = "";
		if (category != null) name = category.getName();
		return patternStr + "|" + name + "\n";
	}
	
	public boolean matches(String inputStr) {
		Matcher matcher = pattern.matcher(inputStr); 
		boolean matchFound = matcher.matches(); 
		return matchFound;
	}

}


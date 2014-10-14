package phonebook;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;

import phonebook.model.Category;

public class CategorySelectionUpdateStrategy extends UpdateValueStrategy {
	
	List<Category> categories;

	public CategorySelectionUpdateStrategy(List c) {
		this.categories = c;
	}

	protected IStatus doSet(IObservableValue observableValue, Object value) {		
		String categoryName = (String) value;
    	for (Iterator<Category> i = categories.iterator(); i.hasNext();) {
    		Category c = (Category) i.next();
    		if (c.getName().equals(categoryName))
    			return super.doSet(observableValue, c);
		}
    	return super.doSet(observableValue, null);
	}

}
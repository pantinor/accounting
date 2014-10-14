package phonebook;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import phonebook.model.Category;
import phonebook.model.CategoryPattern;


public class PatternDialog extends Dialog {

	private Text patternText;
	private Combo categoryText;
	
	private final CategoryPattern pattern;
	private final List categories;
	private final boolean canCreate;


	public PatternDialog(Shell parentShell, CategoryPattern p, List c, boolean canCreate) {
		super(parentShell);
		this.pattern = p;
		this.categories = c;
		this.canCreate = canCreate;
	}


	protected Control createDialogArea(Composite parent) {
		
		Composite container = (Composite) super.createDialogArea(parent);
		
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);

		final Label patternLabel = new Label(container, SWT.NONE);
		patternLabel.setText("Pattern:");

		patternText = new Text(container, SWT.BORDER);
		patternText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		patternText.setText(pattern.getPattern());
		
		final Label categoryLabel = new Label(container, SWT.NONE);
		categoryLabel.setText("Category:");
		
		categoryText = new Combo(container, SWT.READ_ONLY);
        for (Iterator<Category> i = categories.iterator(); i.hasNext();) {
    		Category c = i.next();
    		categoryText.add(c.getName());
		}
        
        if (pattern.getCategory()!= null) 
        	categoryText.select(categoryText.indexOf(pattern.getCategory().getName()));
		
		return container;
	}


	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,true);
		if (canCreate) {
			createButton(parent, IDialogConstants.CANCEL_ID,IDialogConstants.CANCEL_LABEL, false);
		}
		initDataBindings();

	}


	protected Point getInitialSize() {
		return new Point(465, 188);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Pattern");
	}

	protected DataBindingContext initDataBindings() {
		
		IObservableValue patternNameObserveValue = BeansObservables.observeValue(pattern, "pattern");
		IObservableValue patternTextObserveWidget = SWTObservables.observeText(patternText, SWT.Modify);
		
		IObservableValue patternCategoryObserveValue = BeansObservables.observeValue(pattern, "category");
		IObservableValue categoryTextObserveWidget = SWTObservables.observeSelection(categoryText);
		
		DataBindingContext bindingContext = new DataBindingContext();
		
		bindingContext.bindValue(patternTextObserveWidget, patternNameObserveValue, null, null);
		bindingContext.bindValue(categoryTextObserveWidget, patternCategoryObserveValue, new CategorySelectionUpdateStrategy(categories), null);

		return bindingContext;
	}

}
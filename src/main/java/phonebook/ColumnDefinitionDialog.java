package phonebook;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import phonebook.model.DownloadColumnDefinition;


public class ColumnDefinitionDialog extends Dialog {

	private Text descriptionCol;
	private Text amountCol;
	private Text dateCol;

	private final DownloadColumnDefinition col_def;
	
	public ColumnDefinitionDialog(Shell parentShell, DownloadColumnDefinition d) {
		super(parentShell);
		this.col_def = d;
	}

	protected Control createDialogArea(Composite parent) {
		
		Composite container = (Composite) super.createDialogArea(parent);
		
		final GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);

		final Label l1 = new Label(container, SWT.NONE);
		l1.setText("Description Column:");
		
		descriptionCol = new Text(container, SWT.BORDER);
		descriptionCol.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		descriptionCol.setText(col_def.getDescriptioncolumn());
		
		final Label l2 = new Label(container, SWT.NONE);
		l2.setText("Amount Column:");
		
		amountCol = new Text(container, SWT.BORDER);
		amountCol.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		amountCol.setText(col_def.getAmountcolumn());

		final Label l3 = new Label(container, SWT.NONE);
		l3.setText("Date Column:");
		
		dateCol = new Text(container, SWT.BORDER);
		dateCol.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		dateCol.setText(col_def.getDatecolumn());

		return container;
	}


	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,true);
		createButton(parent, IDialogConstants.CANCEL_ID,IDialogConstants.CANCEL_LABEL, false);
		initDataBindings();
	}


	protected Point getInitialSize() {
		return new Point(260, 173);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Column Definition");
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		
		IObservableValue os1 = SWTObservables.observeText(dateCol, SWT.Modify);
		IObservableValue ov1 = BeansObservables.observeValue(col_def, "datecolumn");
		bindingContext.bindValue(os1, ov1, null, null);
		
		IObservableValue os2 = SWTObservables.observeText(descriptionCol, SWT.Modify);
		IObservableValue ov2 = BeansObservables.observeValue(col_def, "descriptioncolumn");
		bindingContext.bindValue(os2, ov2, null, null);
		
		IObservableValue os3 = SWTObservables.observeText(amountCol, SWT.Modify);
		IObservableValue ov3 = BeansObservables.observeValue(col_def, "amountcolumn");
		bindingContext.bindValue(os3, ov3, null, null);
		
		return bindingContext;
	}
}
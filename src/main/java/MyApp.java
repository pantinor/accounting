import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import phonebook.ColumnDefinitionDialog;
import phonebook.PatternDialog;
import phonebook.model.AccountType;
import phonebook.model.AccountTypes;
import phonebook.model.Categories;
import phonebook.model.Category;
import phonebook.model.CategoryPattern;
import phonebook.model.DownloadColumnDefinition;
import phonebook.model.DownloadEntry;
import phonebook.model.Downloads;
import phonebook.model.Patterns;

import com.swtdesigner.SWTResourceManager;

public class MyApp {
	
	DownloadColumnDefinition col_def = new DownloadColumnDefinition();

	ToolItem btnEditPattern;
	ToolItem btnRemovePattern;
	ToolItem btnLoadPattern;
	ToolItem btnSavePattern;
	ToolItem btnAutoPattern;
	
	ToolItem btnResolveEntries;
	
	String patternFilename;
	String treeFilename;
	String downloadsFilename;

	Label colLabel;
	
	TableViewer m_downloadViewer;	
	TableViewer m_patternViewer;
	TreeViewer m_categoryViewer;
	TableViewer m_financialsViewer;


	Downloads m_entries = new Downloads();	
	Categories m_categories = new Categories();
	Patterns m_patterns = new Patterns();
	AccountTypes m_types = new AccountTypes();
	Downloads m_financials = new Downloads();	

	
	Table downloadTable;	
	Tree categoryTree;
	Table patternTable;
	Table financialsTable;


	Shell shlOrganizeDownloadEntries;
	
	DataBindingContext m_bindingContext;
	
	static final int CATEGORY_COLUMN = 3;
	
	static final String INCOME = "Income";
	static final String COST_GOODS_SOLD = "Cost of Goods Sold";
	static final String GROSS_PROFIT = "Gross Profit";
	static final String EXPENSE = "Expense";
	static final String EBIT = "EBIT";
	
	
	Image saveIcon;
    Image openIcon;
    Image refreshIcon;
    Image resolveIcon;
    Image editIcon;
    Image configIcon;
    
    Image newIcon;
    Image deleteIcon;
    Image generateIcon;

    
    AccountType at_income;    
    AccountType at_cgs;	        
    AccountType at_exp;	

	public static void main(String[] args) {
		Display display = new Display();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {

				try {
					MyApp window = new MyApp();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public void open() {
		final Display display = Display.getDefault();
		createContents(display);
		loadCategories();
		shlOrganizeDownloadEntries.open();
		shlOrganizeDownloadEntries.layout();
		while (!shlOrganizeDownloadEntries.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void loadCategories() {
		
		BufferedReader fis;
	    try {  
	    	fis = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("conf//categories.txt")));
        } catch(Exception ex) {
            return;
        }
        
		m_types.clear();
		m_categories.clear();
		m_financials.clear();
       
        String instring = null;
       
        while (true) {
            try {
            	instring = fis.readLine();
                if ( instring == null ) break;
            } catch ( IOException ioe ) {
                break;
            }
           
            String[] split = instring.split("\\t");
            Category c = new Category(split[0].trim());
            AccountType t = m_types.findType(split[1].trim());
            if (t == null) {
            	t = new AccountType(split[1].trim());
            	m_types.addType(t);
            }
            t.addCategory(c );
            c.setType(t);
            m_categories.addCategory(c);
        }

        try {
            fis.close();
        } catch ( IOException ioe ) {
        }
        
        //set financial entries
        at_income = m_types.findType(INCOME);    
        at_cgs = m_types.findType(COST_GOODS_SOLD);	        
        at_exp = m_types.findType(EXPENSE);

        m_financials.addEntry(new DownloadEntry(0,INCOME,null));
        m_financials.addEntry(new DownloadEntry(0,COST_GOODS_SOLD,null));
        m_financials.addEntry(new DownloadEntry(0,GROSS_PROFIT,null));
        m_financials.addEntry(new DownloadEntry(0,EXPENSE,null));
        m_financials.addEntry(new DownloadEntry(0,EBIT,null));

    	m_categoryViewer.refresh();
    	m_financialsViewer.refresh();

	}
	
	protected void reset() {
		
		m_entries.clear();
		
		loadCategories();
		
		//refresh category objects in existing patterns as well
    	for (Iterator<CategoryPattern> iter = m_patterns.getPatterns().iterator(); iter.hasNext();) {
    		CategoryPattern c = iter.next();
    		Category old_cat = c.getCategory();
    		if (old_cat == null) continue;
    		Category new_cat = m_categories.findCategory(old_cat.getName());
    		c.setCategory(new_cat);
    	}
		
    	m_downloadViewer.refresh();
	}


	protected void createContents(Display display) {
		
		shlOrganizeDownloadEntries = new Shell();
		shlOrganizeDownloadEntries.setText("Accounting Otto");
		FillLayout fl_shlOrganizeDownloadEntries = new FillLayout();
		fl_shlOrganizeDownloadEntries.marginWidth = 5;
		fl_shlOrganizeDownloadEntries.marginHeight = 5;
		fl_shlOrganizeDownloadEntries.spacing = 5;
		fl_shlOrganizeDownloadEntries.type = SWT.VERTICAL;
		shlOrganizeDownloadEntries.setLayout(fl_shlOrganizeDownloadEntries);
		shlOrganizeDownloadEntries.setSize(1071, 785);
		
		try {
	        saveIcon = new Image(display, getClass().getResourceAsStream("icons\\save.ico"));
	        openIcon = new Image(display, getClass().getResourceAsStream("icons\\download.ico"));
	        refreshIcon = new Image(display, getClass().getResourceAsStream("icons\\refresh.ico"));
	        resolveIcon = new Image(display, getClass().getResourceAsStream("icons\\notes.ico"));
	        editIcon = new Image(display, getClass().getResourceAsStream("icons\\edit.ico"));
	        configIcon = new Image(display, getClass().getResourceAsStream("icons\\config.ico"));
	        
	        newIcon = new Image(display, getClass().getResourceAsStream("icons\\add.ico"));
	        deleteIcon = new Image(display, getClass().getResourceAsStream("icons\\delete.ico"));
	        generateIcon = new Image(display, getClass().getResourceAsStream("icons\\wizard.ico"));

		} catch (Exception e) {
		}
		
		SashForm topSashForm = new SashForm(shlOrganizeDownloadEntries, SWT.NONE);
		topSashForm.setOrientation(SWT.VERTICAL);
		//topSashForm.setSashWidth(5);
		
		
		CTabFolder tabFolder = new CTabFolder (topSashForm, SWT.NONE);
		tabFolder.setSimple(false);

		Composite compositeDownloads = new Composite(tabFolder, SWT.NONE);
		Composite compositePatterns = new Composite(tabFolder, SWT.NONE);
		Composite compositeCategories = new Composite(tabFolder, SWT.NONE);
		
		CTabItem tabItemDownloads = new CTabItem (tabFolder, SWT.NULL);
		tabItemDownloads.setText ("Downloaded Entries");
		tabItemDownloads.setControl(compositeDownloads);
		
		CTabItem tabItemPatterns = new CTabItem (tabFolder, SWT.NULL);
		tabItemPatterns.setText ("Patterns");
		tabItemPatterns.setControl(compositePatterns);
		
		CTabItem tabItemCategories = new CTabItem (tabFolder, SWT.NULL);
		tabItemCategories.setText ("Accounts");
		tabItemCategories.setControl(compositeCategories);
 		
		
		
		
        compositeDownloads.setLayout(new GridLayout(1, false));

         ToolBar toolBar = new ToolBar(compositeDownloads,SWT.NONE);
         GridData gd_toolBar = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
         gd_toolBar.widthHint = 1000;
         toolBar.setLayoutData(gd_toolBar);
         
          ToolItem btnFileOpen = new ToolItem(toolBar, SWT.PUSH);
          btnFileOpen.setText("Open");
          btnFileOpen.setImage(openIcon);
          btnFileOpen.setToolTipText("Open");
          btnFileOpen.addSelectionListener(new SelectionAdapter() {
          	public void widgetSelected(SelectionEvent e) {
          	    FileDialog dialog = new FileDialog(shlOrganizeDownloadEntries, SWT.OPEN);
          	    dialog.setFileName(downloadsFilename);
          	    downloadsFilename = dialog.open();
          	    openDownloadEntries(downloadsFilename);
          		m_downloadViewer.refresh();
          	}
          });
          
        ToolItem btnDefineCols = new ToolItem(toolBar, SWT.PUSH);
        btnDefineCols.setText("Columns");
        btnDefineCols.setImage(configIcon);
        btnDefineCols.setToolTipText("Define columns for entry import");
        btnDefineCols.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		ColumnDefinitionDialog dialog = new ColumnDefinitionDialog(shlOrganizeDownloadEntries, col_def);
        	    dialog.open();
        	}
        });
        
        ToolItem btnReset = new ToolItem(toolBar, SWT.PUSH);
        btnReset.setText("Reset");
        btnReset.setImage(refreshIcon);
        btnReset.setToolTipText("Reset");
        btnReset.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		reset();
        	}
        });
        
        btnResolveEntries = new ToolItem(toolBar, SWT.PUSH);
        btnResolveEntries.setText("Resolve");
        btnResolveEntries.setImage(resolveIcon);
        btnResolveEntries.setToolTipText("Resolve Entries");
        btnResolveEntries.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		resolveDownloads();
        	}
        });
        
        ToolItem btnResolveUnmatchedEntries = new ToolItem(toolBar, SWT.PUSH);
        btnResolveUnmatchedEntries.setText("Manual");
        btnResolveUnmatchedEntries.setImage(editIcon);
        btnResolveUnmatchedEntries.setToolTipText("Resolve Unmatched Entry");
        btnResolveUnmatchedEntries.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		resolveUnmatchedEntries();
        	}
        });
        
        ToolItem btnSaveReconciliation = new ToolItem(toolBar, SWT.PUSH);
        btnSaveReconciliation.setText("Save");
        btnSaveReconciliation.setImage(saveIcon);
        btnSaveReconciliation.setToolTipText("Save Account Balances");
        btnSaveReconciliation.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        	    FileDialog dialog = new FileDialog(shlOrganizeDownloadEntries, SWT.SAVE);
        	    dialog.setFileName(treeFilename);
        	    treeFilename = dialog.open();
        	    saveReconciliation(treeFilename);
        	}
        });
                
        toolBar.pack(); 

		
		m_downloadViewer = new TableViewer(compositeDownloads, SWT.FULL_SELECTION);
		downloadTable = m_downloadViewer.getTable();
		downloadTable.setHeaderVisible(true);
		downloadTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final TableColumn downloadTableColumn_1 = new TableColumn(downloadTable, SWT.NONE);
		downloadTableColumn_1.setWidth(281);
		downloadTableColumn_1.setText("Description");
		
		final TableColumn downloadTableColumn_2 = new TableColumn(downloadTable, SWT.NONE);
		downloadTableColumn_2.setWidth(100);
		downloadTableColumn_2.setText("Amount");
		
		final TableColumn downloadTableColumn_3 = new TableColumn(downloadTable, SWT.NONE);
		downloadTableColumn_3.setWidth(85);
		downloadTableColumn_3.setText("Date");
		
		final TableColumn downloadTableColumn_4 = new TableColumn(downloadTable, SWT.NONE);
		downloadTableColumn_4.setWidth(300);
		downloadTableColumn_4.setText("Accounts");
		
		m_downloadViewer.setContentProvider(new DownloadContentProvider());
		m_downloadViewer.setLabelProvider(new DownloadsLabelProvider());
		m_downloadViewer.setInput(m_entries);
		
		
	    final TableEditor editor = new TableEditor(downloadTable);
	    
	    downloadTable.addMouseListener(new MouseAdapter() {
	        public void mouseDown(MouseEvent event) {
	           Control old = editor.getEditor();
	           if (old != null) old.dispose();
	        
	           Point pt = new Point(event.x, event.y);
	        
	           final TableItem item = downloadTable.getItem(pt);
	           if (item != null) {
	               int column = -1;
	               for (int i = 0, n = downloadTable.getColumnCount(); i < n; i++) {
	                   Rectangle rect = item.getBounds(i);
	                   if (rect.contains(pt)) {
	                       column = i;
	                       break;
	                   }
	               }
	        
	               if (column == CATEGORY_COLUMN) {
	                   final Combo combo = new Combo(downloadTable, SWT.READ_ONLY);
	                   for (Iterator<Category> i = m_categories.getCategories().iterator(); i.hasNext();) {
	                       Category c = i.next();
	                       combo.add(c.getName());
	                   }
	        
	                   combo.select(combo.indexOf(item.getText(column)));
	        
	                   editor.minimumWidth = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
	                   downloadTable.getColumn(column).setWidth(editor.minimumWidth);
	        
	                   combo.setFocus();
	                   editor.setEditor(combo, item, column);
	        
	                   final int col = column;
	                   combo.addSelectionListener(new SelectionAdapter() {
	                                                  public void widgetSelected(SelectionEvent event) {
	                                                      item.setText(col, combo.getText());
	                                                      combo.dispose();
	                                                  }});
	               }
	           }
	        }
	    });
	    
	    
	    
	    
	    
	    
		compositeCategories.setLayout(new FormLayout());
		
		m_categoryViewer = new TreeViewer(compositeCategories, SWT.FULL_SELECTION);
		categoryTree = m_categoryViewer.getTree();
		FormData fd_categoryTree = new FormData();
		fd_categoryTree.top = new FormAttachment(0, 5);
		fd_categoryTree.left = new FormAttachment(0, 5);
		fd_categoryTree.right = new FormAttachment(100, -5);
		categoryTree.setLayoutData(fd_categoryTree);
		categoryTree.setHeaderVisible(true);
		
		final TreeColumn newColumnTableColumn0 = new TreeColumn(categoryTree, SWT.NONE);
		newColumnTableColumn0.setWidth(200);
		newColumnTableColumn0.setText("Type");
		
		final TreeColumn newColumnTableColumn1 = new TreeColumn(categoryTree, SWT.NONE);
		newColumnTableColumn1.setWidth(300);
		newColumnTableColumn1.setText("Account");
		
		final TreeColumn newColumnTableColumn2 = new TreeColumn(categoryTree, SWT.NONE);
		newColumnTableColumn2.setWidth(200);
		newColumnTableColumn2.setText("Entries");
		
		final TreeColumn newColumnTableColumn3 = new TreeColumn(categoryTree, SWT.NONE);
		newColumnTableColumn3.setWidth(150);
		newColumnTableColumn3.setText("Amount");
		
		m_categoryViewer.setContentProvider(new CategoryTreeContentProvider());
		m_categoryViewer.setLabelProvider(new CategoryLabelProvider());
		m_categoryViewer.setInput(m_types);
		
		
		m_financialsViewer = new TableViewer(compositeCategories, SWT.FULL_SELECTION);
		financialsTable = m_financialsViewer.getTable();		
		financialsTable.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		financialsTable.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		fd_categoryTree.bottom = new FormAttachment(financialsTable, -5);
		FormData fd_financialsTable = new FormData();
		fd_financialsTable.top = new FormAttachment(100, -100);//height from the bottom, each row is 20 in size
		fd_financialsTable.right = new FormAttachment(100, -5);
		fd_financialsTable.left = new FormAttachment(0, 5);
		fd_financialsTable.bottom = new FormAttachment(100, -5);
		financialsTable.setLayoutData(fd_financialsTable);

		final TableColumn finCol1 = new TableColumn(financialsTable, SWT.NONE);
		finCol1.setWidth(200);
		finCol1.setText("Type");
		
		final TableColumn finCol2 = new TableColumn(financialsTable, SWT.NONE);
		finCol2.setWidth(200);
		finCol2.setText("Amount");
		
		m_financialsViewer.setContentProvider(new FinancialsContentProvider());
		m_financialsViewer.setLabelProvider(new FinancialsLabelProvider());
		m_financialsViewer.setInput(m_financials);
		
		compositePatterns.setLayout(new GridLayout());

	
		
		Composite patternCompInside = new Composite(compositePatterns, SWT.FULL_SELECTION);
		patternCompInside.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl_patternCompInside = new GridLayout();
		gl_patternCompInside.horizontalSpacing = 0;
		gl_patternCompInside.marginHeight = 0;
		gl_patternCompInside.marginWidth = 0;
		gl_patternCompInside.verticalSpacing = 0;
		patternCompInside.setLayout(gl_patternCompInside);
		
		
        ToolBar patternToolbar = new ToolBar(patternCompInside,SWT.NONE);
        GridData gd_pattBar = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_pattBar.widthHint = 1000;
        toolBar.setLayoutData(gd_pattBar);
        
        ToolItem btnAddPattern = new ToolItem(patternToolbar, SWT.PUSH);
        btnAddPattern.setText("New");
        btnAddPattern.setImage(newIcon);
				
        btnEditPattern = new ToolItem(patternToolbar, SWT.PUSH);
        btnEditPattern.setText("Edit");
        btnEditPattern.setImage(editIcon);		
		//btnEditPattern.setEnabled(false);
		
		btnRemovePattern = new ToolItem(patternToolbar, SWT.PUSH);
		btnRemovePattern.setText("Delete");
		btnRemovePattern.setImage(deleteIcon);		
		//btnRemovePattern.setEnabled(false);
		
		btnLoadPattern = new ToolItem(patternToolbar, SWT.PUSH);
		btnLoadPattern.setText("Load");
		btnLoadPattern.setImage(openIcon);
		
		btnSavePattern = new ToolItem(patternToolbar, SWT.PUSH);
		btnSavePattern.setText("Save");
		btnSavePattern.setImage(saveIcon);
		
		btnAutoPattern = new ToolItem(patternToolbar, SWT.PUSH);
		btnAutoPattern.setText("Generate");
		btnAutoPattern.setImage(generateIcon);
		
		
		m_patternViewer = new TableViewer(patternCompInside, SWT.FULL_SELECTION);
		patternTable = m_patternViewer.getTable();
		patternTable.setToolTipText("Patterns to Match Category Accounts");
		patternTable.setHeaderVisible(true);
		GridData gd_patternTable = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_patternTable.heightHint = 119;
		patternTable.setLayoutData(gd_patternTable);
		
		TableColumn tblclmnPattern = new TableColumn(patternTable, SWT.NONE);
		tblclmnPattern.setWidth(200);
		tblclmnPattern.setText("Pattern");
		
		TableColumn tblclmnCategory = new TableColumn(patternTable, SWT.NONE);
		tblclmnCategory.setWidth(300);
		tblclmnCategory.setText("Account");
		

		
		m_patternViewer.setContentProvider(new PatternContentProvider());
		m_patternViewer.setLabelProvider(new PatternLabelProvider());
		m_patternViewer.setInput(m_patterns);
		
		
		btnAddPattern.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CategoryPattern pattern = new CategoryPattern("",(Category)m_categories.getCategories().get(0));
				PatternDialog dialog = new PatternDialog(shlOrganizeDownloadEntries, pattern, m_categories.getCategories(), true);
				if (dialog.open() == Window.OK) {
					m_patterns.addPattern(pattern);
				}
				m_patternViewer.refresh();
			}
		});
		
		btnEditPattern.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) m_patternViewer.getSelection();
				CategoryPattern pattern = (CategoryPattern) selection.getFirstElement();
				if (pattern==null) return;
				PatternDialog dialog = new PatternDialog(shlOrganizeDownloadEntries, pattern, m_categories.getCategories(), false);
				dialog.open();
				m_patternViewer.refresh();
			}
		});
		
		btnRemovePattern.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) m_patternViewer.getSelection();
				CategoryPattern pattern = (CategoryPattern) selection.getFirstElement();
				//boolean confirm = MessageDialog.openConfirm(shlOrganizeDownloadEntries,
				//		"Confirm Delete","Are you sure you want to delete pattern '" + pattern.getPattern() + "'?");
				//if (confirm) {
					m_patterns.removePattern(pattern);
				//}
				m_patternViewer.refresh();
			}
		});
		
		btnLoadPattern.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean confirm = false;
				if (m_patterns.getPatterns().size() == 0) {
					confirm = true;
				} else {
					confirm = MessageDialog.openConfirm(shlOrganizeDownloadEntries,
						"Confirm Load","Unsaved changes will be lost, OK to proceed? '");
				}
				if (confirm) {
				    FileDialog dialog = new FileDialog(shlOrganizeDownloadEntries, SWT.OPEN);
				    dialog.setFileName(patternFilename);
				    patternFilename = dialog.open();
				    loadPatterns(patternFilename);
				}
				m_patternViewer.refresh();
			}
		});
		
		btnSavePattern.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			    FileDialog dialog = new FileDialog(shlOrganizeDownloadEntries, SWT.SAVE);
			    dialog.setFileName(patternFilename);
			    patternFilename = dialog.open();
			    savePatterns(patternFilename);
			}
		});
		
		btnAutoPattern.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				autoGenerate();
				m_patternViewer.refresh();
			}
		});
		
		patternTable.addMouseListener(new MouseAdapter() {
	        public void mouseDown(MouseEvent event) {
	           Control old = editor.getEditor();
	           if (old != null) old.dispose();
	        
	           Point pt = new Point(event.x, event.y);
	        
	           final TableItem item = patternTable.getItem(pt);
	           if (item != null) {
	               int column = -1;
	               for (int i = 0, n = patternTable.getColumnCount(); i < n; i++) {
	                   Rectangle rect = item.getBounds(i);
	                   if (rect.contains(pt)) {
	                       column = i;
	                       break;
	                   }
	               }
	        
	               if (column == 1) {
	                   final Combo combo = new Combo(patternTable, SWT.READ_ONLY);
	                   for (Iterator<Category> i = m_categories.getCategories().iterator(); i.hasNext();) {
	                       Category c = i.next();
	                       combo.add(c.getName());
	                   }
	        
	                   combo.select(combo.indexOf(item.getText(column)));
	                   editor.minimumWidth = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
	                   patternTable.getColumn(column).setWidth(editor.minimumWidth);
	                   combo.setFocus();
	                   editor.setEditor(combo, item, column);
	        
	                   final int col = column;
	                   combo.addSelectionListener(new SelectionAdapter() {
                              public void widgetSelected(SelectionEvent event) {
                            	  item.setText(col, combo.getText());
                            	  CategoryPattern pattern = m_patterns.findPattern(item.getText(0));
           	                   	  if (pattern != null) pattern.setCategory(m_categories.findCategory(combo.getText()));
                                  combo.dispose();
                              }});
	               }
	           }
	        }
	    });
	    editor.horizontalAlignment = SWT.LEFT;
	    editor.grabHorizontal = true;
	    
	    
	    initDataBindings();

	}

	
	protected void resolveDownloads() {
		
    	for (Iterator<DownloadEntry> i = m_entries.getEntries().iterator(); i.hasNext();) {
    		DownloadEntry entry = i.next();
    		boolean match = false;
    		CategoryPattern matchedPattern = null;
    		
    		//entry can match one or more patterns, but only choose the most explicit pattern (ie the longest one)
        	for (Iterator<CategoryPattern> j = m_patterns.getPatterns().iterator(); j.hasNext();) {
        		CategoryPattern pattern = j.next();
        		if (pattern.getCategory()==null) continue;
        		match = pattern.matches(entry.getDescription());
        		if (match) {
        			if (matchedPattern == null || pattern.getPattern().length() > matchedPattern.getPattern().length())
        				matchedPattern = pattern;
        		}
        	}
        	
        	if (matchedPattern != null) {
        		matchedPattern.getCategory().addEntry(entry);
        		entry.setDeleted(true);
        	}
        		
		}
    	
		int index = 0;
    	while (index != -1 && m_entries.getEntries().size() > 0) {
    		DownloadEntry entry = m_entries.getEntries().get(index);
    		if (entry == null) index = -1;
    		if (entry.isDeleted()) {
    			m_entries.getEntries().remove(index);
    		} else {
    			index ++;
    		}
    		if (index >= m_entries.getEntries().size()) index = -1;
    	}
    	
    	m_categoryViewer.refresh();
    	m_downloadViewer.refresh();
    	
    	
    	//refresh financials
        double inc = at_income.getSum();
        double cgs = at_cgs.getSum();
        double gp = inc - cgs;
        double exp = at_exp.getSum();
        double ebit = inc - cgs - exp;
        
        m_financials.getEntries().get(0).setAmount(inc);
        m_financials.getEntries().get(1).setAmount(cgs);
        m_financials.getEntries().get(2).setAmount(gp);
        m_financials.getEntries().get(3).setAmount(exp);
        m_financials.getEntries().get(4).setAmount(ebit);
        
        m_financialsViewer.refresh();
	    	    
	}
	
	protected void resolveUnmatchedEntries() {
		
		for (int i = 0; i < downloadTable.getItemCount();i++) {
			
			TableItem item = downloadTable.getItem(i);
			
			String categoryName = item.getText(CATEGORY_COLUMN);
			Category category = m_categories.findCategory(categoryName);
	    	if (category == null) continue;
	    	
	    	DownloadEntry entry = null;
	    	for (Iterator<DownloadEntry> iter = m_entries.getEntries().iterator(); iter.hasNext();) {
	    		DownloadEntry e = iter.next();
	    		if (e.getDescription().equals(item.getText(0))) {
	    			entry = e;
	    			break;
	    		}
	    	}
	    	
	    	category.addEntry(entry);
	    	m_entries.removeEntry(entry);
	    	
		}
		
    	m_categoryViewer.refresh();
    	m_downloadViewer.refresh();
	}
	
	
	class CategoryTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object inputElement) {
			return m_types.getTypes().toArray();
		}
		public Object[] getChildren(Object parentElement) {
		    if (parentElement instanceof Category) {
		    	Category cat = (Category)parentElement;
		        return cat.getEntries().toArray();
		    }
		    if (parentElement instanceof AccountType) {
		    	AccountType cat = (AccountType)parentElement;
		        return cat.getCategories().toArray();
		    }
		    return new Object[0];
		}
		public Object getParent(Object element) {
		    if(element instanceof DownloadEntry) {
		    	for (Iterator iterator = m_categories.getCategories().iterator(); iterator.hasNext();) {
		    		Category cat = (Category) iterator.next();
					if (cat.getEntries().contains(element)) {
						return cat;
					}
				}
		    }
		    if(element instanceof Category) {
		    	for (Iterator iterator = m_types.getTypes().iterator(); iterator.hasNext();) {
		    		AccountType type = (AccountType) iterator.next();
					if (type.getCategories().contains(element)) {
						return type;
					}
				}
		    }
		    return null;
		}
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}
	}
	
    class DownloadsLabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof DownloadEntry) {
                DownloadEntry e = (DownloadEntry) element;
                switch (columnIndex) {
                case 0:
                    return e.getDescription();
                case 1:
                    return e.getCurrencyFormattedAmount();
                case 2:
                    return e.getFormattedDate();
                default:
                    return "";
                }
            }
            return "";
        }
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
        public void addListener(ILabelProviderListener listener) {
        }
        public void dispose() {
        }
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }
        public void removeListener(ILabelProviderListener listener) {
        }
    }

    class CategoryLabelProvider implements ITableLabelProvider {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof DownloadEntry) {
                DownloadEntry e = (DownloadEntry) element;
                switch (columnIndex) {
                case 2:
                    return e.getDescription();
                case 3:
                    return e.getCurrencyFormattedAmount();
                case 4:
                    return e.getFormattedDate();
                default:
                    return "";
                }
            } else if (element instanceof Category) {
                Category e = (Category) element;
                switch (columnIndex) {
                case 1:
                    return e.getName();
                case 2:
                    return ""+e.getEntries().size();
                case 3:
                    return nf.format(e.getSum());
                default:
                    return "";
                }
            } else if (element instanceof AccountType) {
                AccountType t = (AccountType) element;
                switch (columnIndex) {
                case 0:
                    return t.getName();
                case 3:
                    return nf.format(t.getSum());
                default:
                    return "";
                }
            }
            return "";
        }
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
        public void addListener(ILabelProviderListener listener) {
        }
        public void dispose() {
        }
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }
        public void removeListener(ILabelProviderListener listener) {
        }
    }

    class PatternLabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof CategoryPattern) {
                CategoryPattern e = (CategoryPattern) element;
                switch (columnIndex) {
                case 0:
                    return e.getPattern();
                case 1:
                    return(e.getCategory()==null?"":e.getCategory().getName());
                default:
                    return "";
                }
            }
            return "";
        }
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
        public void addListener(ILabelProviderListener listener) {
        }
        public void dispose() {
        }
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }
        public void removeListener(ILabelProviderListener listener) {
        }
    }
    class FinancialsLabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof DownloadEntry) {
                DownloadEntry e = (DownloadEntry) element;
                switch (columnIndex) {
                case 0:
                    return e.getDescription();
                case 1:
                    return e.getCurrencyFormattedAmount();
                default:
                    return "";
                }
            }
            return "";
        }
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
        public void addListener(ILabelProviderListener listener) {
        }
        public void dispose() {
        }
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }
        public void removeListener(ILabelProviderListener listener) {
        }
    }
	class DownloadContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object inputElement) {
			return m_entries.getEntries().toArray();
		}
	}
	class PatternContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object inputElement) {
			return m_patterns.getPatterns().toArray();
		}
	}
	class FinancialsContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object inputElement) {
			return m_financials.getEntries().toArray();
		}
	}

	
	protected DataBindingContext initDataBindings() {
		
		//IObservableValue editPatternButtonEnabledObserveWidget = SWTObservables.observeEnabled(btnEditPattern);
		//IObservableValue removePatternButtonEnabledObserveWidget = SWTObservables.observeEnabled(btnRemovePattern);
		//IObservableValue patternTableSelectionIndexObserveWidget = SWTObservables.observeSingleSelectionIndex(patternTable);
		
		DataBindingContext bindingContext = new DataBindingContext();
		
		//bindingContext.bindValue(patternTableSelectionIndexObserveWidget, editPatternButtonEnabledObserveWidget, new phonebook.SelectionUpdateValueStrategy(), new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER));
		//bindingContext.bindValue(patternTableSelectionIndexObserveWidget, removePatternButtonEnabledObserveWidget, new phonebook.SelectionUpdateValueStrategy(), new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER));
		
		return bindingContext;
	}
	
	
	public void openDownloadEntries(String file) {
		
		BufferedReader fis;
	    try {  
	    	fis = new BufferedReader( new FileReader( file ) );
        } catch(Exception ex) {
            return;
        }
        
        String instring = null;
        
        m_entries.clear();

        while (true) {
            try {
                instring = fis.readLine();
                if ( instring == null ) break;
            } catch ( IOException ioe ) {
                break;
            }
            
            System.out.println(instring);

            String[] split = instring.split(",");
            DownloadEntry entry = new DownloadEntry();
            for(int i = 0; i < split.length ; i++) {
            	if (i==col_def.getDateColumnInt()) entry.setDate(split[i]);
            	if (i==col_def.getDescriptionColumnInt()) entry.setDescription(split[i]);
            	if (i==col_def.getAmountColumnInt()) entry.setAmount(split[i]);
            }
            m_entries.addEntry(entry);
        }

        try {
            fis.close();
        } catch ( IOException ioe ) {
        }
        
    	btnResolveEntries.setEnabled(true);

	}
	
	public void loadPatterns(String file) {
		
		BufferedReader fis;
	    try {  
	    	fis = new BufferedReader( new FileReader( file ) );
        } catch(Exception ex) {
            return;
        }
        
        String instring = null;
        
        m_patterns.clear();

        while (true) {
            try {
                instring = fis.readLine();
                if ( instring == null ) break;
            } catch ( IOException ioe ) {
                break;
            }
            
            String[] split = instring.split("\\|");
            CategoryPattern pattern = new CategoryPattern();
            for(int i = 0; i < split.length ; i++) {
            	if (i==0) pattern.setPattern(split[i]);
            	if (i==1) pattern.setCategory(m_categories.findCategory(split[i]));
            }
            m_patterns.addPattern(pattern);
        }

        try {
            fis.close();
        } catch ( IOException ioe ) {
        }
	}

	
	public void savePatterns(String file) {
		try {
	        FileWriter writer = new FileWriter(file);
	    	for (Iterator<CategoryPattern> iter = m_patterns.getPatterns().iterator(); iter.hasNext();) {
	    		CategoryPattern c = iter.next();
	    		writer.write(c.toString());
	    	}
	        writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveReconciliation(String file) {
		try {
	        FileWriter writer = new FileWriter(file);
	        
	        TreeItem[] cats = categoryTree.getItems();
 	        for (int i = 0; i < cats.length; i++)
	        	writeTreeItem(writer, cats[i]);
	        
			writer.write("\n");
		    writer.write(INCOME + "\t" + m_financials.getEntries().get(0).getCurrencyFormattedAmount() + "\n");
		    writer.write(COST_GOODS_SOLD + "\t" + m_financials.getEntries().get(1).getCurrencyFormattedAmount() + "\n");
		    writer.write(GROSS_PROFIT + "\t" + m_financials.getEntries().get(2).getCurrencyFormattedAmount() + "\n");
		    writer.write(EXPENSE + "\t" + m_financials.getEntries().get(3).getCurrencyFormattedAmount() + "\n");
		    writer.write(EBIT + "\t" + m_financials.getEntries().get(4).getCurrencyFormattedAmount() + "\n");
 	        
	        writer.close();
		} catch (Exception e) {
		}
	}
	
	public void writeTreeItem(FileWriter writer, TreeItem item) throws IOException {
		
	    writer.write(item.getText(0) + "\t" + item.getText(3) + "\n");
	    
        //TreeItem[] items = item.getItems();
        //for (int i = 0; i < items.length; i++)
        	//writeTreeItem(writer, items[i]);
	}
		
	public void autoGenerate() {
    	DownloadEntry entry = null;
    	Vector v = new Vector();
    	for (Iterator<DownloadEntry> iter = m_entries.getEntries().iterator(); iter.hasNext();) {
    		DownloadEntry e = iter.next();
    		String pattern = generatePattern(e.getDescription());
    		if (!v.contains(pattern)) v.add(pattern);
    	}
    	for (int i =0;i<v.size();i++) {
    		CategoryPattern c = new CategoryPattern();
    		c.setPattern((String)v.get(i));
    		m_patterns.addPattern(c);
    	}
	}
	
	//it looks for the first digit if any and makes a substring up to that point
	public String generatePattern(String name) {
		if (name == null) return null;
		String temp = name.trim();
		temp = temp.replace("\"","");
		char[] chars = temp.toCharArray();
		int end = chars.length;
		for (int i=0;i<chars.length;i++) {
			if (Character.isDigit(chars[i])) {
				end = i;
				break;
			}
		}
		return "." + temp.substring(0,end) + ".*";
	}
}

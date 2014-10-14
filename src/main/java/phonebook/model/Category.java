package phonebook.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import convert.Cash;

public class Category extends AbstractModelObject {
	
	private List<DownloadEntry> m_entries = new ArrayList();;
	private String name = "";
	private AccountType type = null;
	
	public Category(String n) {
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
	
	public void addEntry(DownloadEntry e) {
		m_entries.add(e);
		firePropertyChange("entries", null, m_entries);
	}

	public void removeEntry(DownloadEntry e) {
		m_entries.remove(e);
		firePropertyChange("entries", null, m_entries);
	}

	public List getEntries() {
		return m_entries;
	}
	
	public AccountType getType() {
		return type;
	}

	public void setType(AccountType n) {
		AccountType oldValue = type;
		type = n;
		firePropertyChange("type", oldValue, type);
	}
	
	public String toString() {
		return name;
	}
	
	public boolean equals(Object o) {
		if (o instanceof String) return (((String)o).equals(name));
		if (o instanceof Category) return (((Category)o).getName().equals(name));
		return false;
	}
	
	public double getSum() {
	    double total = 0;
        for (Iterator<DownloadEntry> i = m_entries.iterator(); i.hasNext();) {
            DownloadEntry d = i.next();
            total += d.getAmount();
        }
	    return total;
	}
	

}


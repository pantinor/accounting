package phonebook.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AccountTypes extends AbstractModelObject {
	private final List<AccountType> m_types = new ArrayList();

	public void addType(AccountType c) {
		m_types.add(c);
		firePropertyChange("types", null, m_types);
	}

	public void removeType(AccountType c) {
		m_types.remove(c);
		firePropertyChange("types", null, m_types);
	}

	public List getTypes() {
		return m_types;
	}
	
	public void clear() {
		m_types.clear();
	}
	
	public AccountType findType(String name) {
		AccountType type = null;
    	for (Iterator<AccountType> iter = m_types.iterator(); iter.hasNext();) {
    		AccountType t = iter.next();
    		if (t.getName().equals(name)) {
    			type = t;
    		}
    	}
    	return type;
	}
	
}

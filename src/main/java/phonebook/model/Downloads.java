package phonebook.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Downloads extends AbstractModelObject {
	private final List<DownloadEntry> m_entries = new ArrayList();

	public void addEntry(DownloadEntry e) {
		m_entries.add(e);
		firePropertyChange("entries", null, m_entries);
	}

	public void removeEntry(DownloadEntry e) {
		m_entries.remove(e);
		firePropertyChange("entries", null, m_entries);
	}

	public List<DownloadEntry> getEntries() {
		return m_entries;
	}
	
	public void clear() {
		m_entries.clear();
	}
}
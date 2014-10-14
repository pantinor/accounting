package phonebook.model;


public class DownloadColumnDefinition extends AbstractModelObject {
	
	private String m_datecolumn = "0";	
	private String m_descriptioncolumn = "1";
	private String m_amountcolumn = "2";
	
	public DownloadColumnDefinition() {
	}

	public void setDatecolumn(String n) {
		String oldValue = m_datecolumn;
		m_datecolumn = n;
		firePropertyChange("datecolumn", oldValue, m_datecolumn);
	}

	public void setDescriptioncolumn(String n) {
		String oldValue = m_descriptioncolumn;
		m_descriptioncolumn = n;
		firePropertyChange("descriptioncolumn", oldValue, m_descriptioncolumn);
	}

	public void setAmountcolumn(String n) {
		String oldValue = m_amountcolumn;
		m_amountcolumn = n;
		firePropertyChange("amountcolumn", oldValue, m_amountcolumn);
	}
	
	public String getDescriptioncolumn() {
		return m_descriptioncolumn;
	}
	
	public String getAmountcolumn() {
		return m_amountcolumn;
	}
	
	public String getDatecolumn() {
		return m_datecolumn;
	}
	
	public int getAmountColumnInt() {
		int i = 0;
		try {
			i = Integer.parseInt(m_amountcolumn);
		} catch (Exception e) {
		}
		return i;
	}
	
	public int getDescriptionColumnInt() {
		int i = 0;
		try {
			i = Integer.parseInt(m_descriptioncolumn);
		} catch (Exception e) {
		}
		return i;
	}
	
	public int getDateColumnInt() {
		int i = 0;
		try {
			i = Integer.parseInt(m_datecolumn);
		} catch (Exception e) {
		}
		return i;
	}
	
	public String toString() {
		return "Description column " + m_descriptioncolumn + ", Amount column " + m_amountcolumn + ", Date column " + m_datecolumn;
	}
	


}


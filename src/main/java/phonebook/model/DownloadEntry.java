package phonebook.model;

import java.util.*;
import java.text.*;

public class DownloadEntry extends AbstractModelObject {
	
	private String description = "";
	private double amount = 0;
	private Date date = new Date();
	private boolean deleted = false;
	
    NumberFormat nf = NumberFormat.getCurrencyInstance();
    SimpleDateFormat format = new SimpleDateFormat("M/d/yy");
	
	public DownloadEntry(double am, String de, Date da) {
		amount = am;
		description = de;
		date = da;
	}
	
	public DownloadEntry() {
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double a) {
		double oldValue = amount;
		amount = a;
		firePropertyChange("amount", oldValue, amount);
	}
	
	public void setAmount(String d) {
		double newd = 0;
		try {
			//strip quotes from it
			d = d.replace("\"","");
			newd = Double.parseDouble(d);
		} catch(Exception e) {
		}
		double oldValue = amount;
		amount = newd;
		firePropertyChange("amount", oldValue, amount);
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String d) {
		if (d.length()<1) d = "\"MANUAL CHECK\"";
		String oldValue = description;
		description = d;
		firePropertyChange("description", oldValue, description);
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date d) {
		Date oldValue = date;
		date = d;
		firePropertyChange("date", oldValue, date);
	}
	
	public void setDate(String d) {
		Date newd = new Date();
		try {
			newd = format.parse(d);
		} catch(Exception e) {
		}
		Date oldValue = date;
		date = newd;
		firePropertyChange("date", oldValue, date);
	}
	
	public void setDeleted(boolean b) {
		deleted = b;
	}
	
	public boolean isDeleted() {
		return deleted;
	}

	public String toString() {
		return description + " - " + amount;
	}
	
	public String getCurrencyFormattedAmount() {
		  return nf.format(amount);
	}
	
	public String getFormattedDate() {
		  return format.format(date);
	}

}

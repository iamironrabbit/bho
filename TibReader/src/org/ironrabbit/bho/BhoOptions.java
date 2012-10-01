package org.ironrabbit.bho;


public class BhoOptions {
    private String label;
    private boolean isChecked;
    
    public BhoOptions(String label, boolean isChecked) {
        this.label = label;
        this.isChecked = isChecked;
    }
    
    public boolean getChecked() {
        return isChecked;
    }
    
    public String getLabel() {
        return label;
    }
    
    
    public void setChecked(boolean newChecked) {
        isChecked = newChecked;
    }
}

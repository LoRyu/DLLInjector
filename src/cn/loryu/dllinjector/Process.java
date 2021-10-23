package cn.loryu.dllinjector;

public class Process {
    private String name = "";
    private String id = "";
    private boolean selected = false;

    public Process(String name, String id) {
        this.id = id.trim();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

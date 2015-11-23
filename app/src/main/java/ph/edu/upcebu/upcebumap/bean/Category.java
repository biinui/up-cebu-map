package ph.edu.upcebu.upcebumap.bean;

/**
 * Created by user on 11/22/2015.
 */
public class Category {
    private int id;
    private String categoryName = "";
    private String icon = "";

    public Category() {
    }

    public Category(int id, String cName, String icon) {
        this.setId(id);
        this.categoryName = cName;
        this.icon = icon;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

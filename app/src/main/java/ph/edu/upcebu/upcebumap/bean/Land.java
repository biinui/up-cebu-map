package ph.edu.upcebu.upcebumap.bean;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import ph.edu.upcebu.upcebumap.model.Room;

/**
 * Created by user on 11/22/2015.
 */
public class Land {
    private long id;
    private Category category;
    private String title = "";
    private Shape shape;
    private int xpos;
    private int ypos;
    private ArrayList<LatLng> latLngs;

    private List<Room> rooms = new ArrayList<>();

    public Land() {
        setCategory(new Category());
        setTitle("");
        setLatLngs(new ArrayList<LatLng>());
        setShape(new Shape());
    }

    public Land(int id, Category category, String title, ArrayList<LatLng> latLngs, Shape shape, int xpos, int ypos) {
        this.setId(id);
        this.setCategory(category);
        this.setTitle(title);
        this.setLatLngs(latLngs);
        this.setShape(shape);
        this.setXpos(xpos);
        this.setYpos(ypos);
    }

    public void addLatLngs(double xpos, double ypos) {
        this.latLngs.add(new LatLng(xpos, ypos));
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<LatLng> getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(ArrayList<LatLng> latLngs) {
        this.latLngs = latLngs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public int getXpos() {
        return xpos;
    }

    public void setXpos(int xpos) {
        this.xpos = xpos;
    }

    public int getYpos() {
        return ypos;
    }

    public void setYpos(int ypos) {
        this.ypos = ypos;
    }

    public void addRoom(Room room) {
        this.rooms.add(room);
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }
}

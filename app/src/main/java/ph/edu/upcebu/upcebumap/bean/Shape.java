package ph.edu.upcebu.upcebumap.bean;

/**
 * Created by user on 11/22/2015.
 */
public class Shape {
    private int shapeID;
    private int landmarkID;
    private String shapeType;
    private String strokeColor;
    private String fillColor;
    private int radius;

    public Shape() {
    }

    public Shape(int sid, int lid, String shapeType, String strokeColor, String fillColor, int radius) {
        this.shapeID = sid;
        this.landmarkID = lid;
        this.shapeType = shapeType;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
        this.radius = radius;
    }

    public int getShapeID() {
        return shapeID;
    }

    public void setShapeID(int shapeID) {
        this.shapeID = shapeID;
    }

    public int getLandmarkID() {
        return landmarkID;
    }

    public void setLandmarkID(int landmarkID) {
        this.landmarkID = landmarkID;
    }

    public String getShapeType() {
        return shapeType;
    }

    public void setShapeType(String shapeType) {
        this.shapeType = shapeType;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}

package ph.edu.upcebu.upcebumap.model;
/**
 * Created by user on 11/21/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import ph.edu.upcebu.upcebumap.bean.Building;
import ph.edu.upcebu.upcebumap.bean.Category;
import ph.edu.upcebu.upcebumap.bean.Land;
import ph.edu.upcebu.upcebumap.bean.Shape;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "UPCebuMap.db";
    public static final String LANDMARK_TABLE_NAME = "Landmark";
    public static final String SHAPE_TABLE_NAME = "Shape";
    public static final String BOUNDARY_TABLE_NAME = "Boundary";
    public static final String CATEGORY_TABLE_NAME = "Category";

    public static final String ROOM_TABLE_NAME = "Room";

    public static final String LANDMARK_COLUMN_ID = "landmark_id";
    public static final String LANDMARK_COLUMN_XPOS = "xpos";
    public static final String LANDMARK_COLUMN_YPOS = "ypos";
    public static final String LANDMARK_COLUMN_TITLE = "title";
    public static final String LANDMARK_COLUMN_CATEGORY = "category";
    public static final String SHAPE_COLUMN_ID = "shape_id";
    public static final String SHAPE_COLUMN_LID = "landmark_id";
    public static final String SHAPE_COLUMN_SHAPETYPE = "shape_type";
    public static final String SHAPE_COLUMN_SCOLOR = "stroke_color";
    public static final String SHAPE_COLUMN_FCOLOR = "fill_color";
    public static final String SHAPE_COLUMN_RADIUS = "radius";
    public static final String BOUNDARY_COLUMN_ID = "boundary_id";
    public static final String BOUNDARY_COLUMN_SID = "shape_id";
    public static final String BOUNDARY_COLUMN_XPOS = "xpos";
    public static final String BOUNDARY_COLUMN_YPOS = "ypos";
    public static final String BOUNDARY_COLUMN_ISHOLE = "is_hole";
    public static final String CATEGORY_COLUMN_ID = "category_id";
    public static final String CATEGORY_COLUMN_NAME = "category";
    public static final String CATEGORY_COLUMN_ICON = "icon";

    public static final String ROOM_COLUMN_ID = "room_id";
    public static final String ROOM_COLUMN_TYPE = "type";
    public static final String ROOM_COLUMN_OFFICE = "office_name";
    public static final String ROOM_COLUMN_ROOM = "room_num";
    public static final String ROOM_COLUMN_PHONE = "phone";
    public static final String ROOM_COLUMN_HEAD = "head";
    public static final String ROOM_COLUMN_DESCRIPTION = "description";
    public static final String ROOM_COLUMN_BUILDING = "landmark_id";


    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " + ROOM_TABLE_NAME +
                        " (" + ROOM_COLUMN_ID + " integer primary key, " + ROOM_COLUMN_TYPE + " text, " +
                        ROOM_COLUMN_OFFICE + " text, " + ROOM_COLUMN_ROOM + " text, " + ROOM_COLUMN_PHONE +
                        " text, " + ROOM_COLUMN_HEAD + " text, " + ROOM_COLUMN_DESCRIPTION + " text, " +
                        ROOM_COLUMN_BUILDING + " integer)"
        );

        db.execSQL(
                "create table " + LANDMARK_TABLE_NAME +
                        " (" + LANDMARK_COLUMN_ID + " integer primary key, " + LANDMARK_COLUMN_TITLE + " text, " +
                        LANDMARK_COLUMN_XPOS + " real, " + LANDMARK_COLUMN_YPOS + " real, " + LANDMARK_COLUMN_CATEGORY + " text)"
        );

        db.execSQL(
                "create table " + SHAPE_TABLE_NAME +
                        " (" + SHAPE_COLUMN_ID + " integer primary key, " + SHAPE_COLUMN_LID + " integer, " +
                        SHAPE_COLUMN_SHAPETYPE + " text, " + SHAPE_COLUMN_FCOLOR + " text, " + SHAPE_COLUMN_SCOLOR + " text, " + SHAPE_COLUMN_RADIUS + " integer)"
        );

        db.execSQL(
                "create table " + BOUNDARY_TABLE_NAME +
                        " (" + BOUNDARY_COLUMN_ID + " integer primary key, " + BOUNDARY_COLUMN_SID + " integer, " +
                        BOUNDARY_COLUMN_XPOS + " real, " + BOUNDARY_COLUMN_YPOS + " real, " + BOUNDARY_COLUMN_ISHOLE + " integer)"
        );

        db.execSQL(
                "create table " + CATEGORY_TABLE_NAME +
                        " (" + CATEGORY_COLUMN_ID + " integer primary key, " + CATEGORY_COLUMN_NAME + " text, " +
                        CATEGORY_COLUMN_ICON + " text)"
        );

        db.execSQL(
                "insert into " + CATEGORY_TABLE_NAME + " (" + CATEGORY_COLUMN_NAME + ", " + CATEGORY_COLUMN_ICON +
                        ") values ('Building','office_building'), ('Health Service','medicine')," +
                        "('Dormitory','home_2'), ('Library','library')," +
                        "('Wing','air_fixwing')," +
                        "('Activity Area','bowling'), ('Square','conference')," +
                        "('Canteen','restaurant'), ('Comfort Room','toilets')," +
                        "('Parking Area','car')," +
                        "('Road','road'), ('Others','zoom')," +
                        "('Study Area','cabin_2')"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + ROOM_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LANDMARK_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SHAPE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BOUNDARY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);
        onCreate(db);
    }

    public long insertRoom(String type, String officename, String roomnum, String phone, String head, String description, int lid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROOM_COLUMN_TYPE, type);
        contentValues.put(ROOM_COLUMN_OFFICE, officename);
        contentValues.put(ROOM_COLUMN_ROOM, roomnum);
        contentValues.put(ROOM_COLUMN_PHONE, phone);
        contentValues.put(ROOM_COLUMN_HEAD, head);
        contentValues.put(ROOM_COLUMN_DESCRIPTION, description);
        contentValues.put(ROOM_COLUMN_BUILDING, lid);
        long id = db.insert(ROOM_TABLE_NAME, null, contentValues);
        return id;
    }

    public long insertLandmark(String title, String category, double xpos, double pos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LANDMARK_COLUMN_TITLE, title);
        contentValues.put(LANDMARK_COLUMN_CATEGORY, category);
        contentValues.put(LANDMARK_COLUMN_XPOS, xpos);
        contentValues.put(LANDMARK_COLUMN_YPOS, pos);
        long id = db.insert(LANDMARK_TABLE_NAME, null, contentValues);
        return id;
    }

    public long insertBoundary(long sid, double xpos, double ypos, int ishole) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BOUNDARY_COLUMN_SID, sid);
        contentValues.put(BOUNDARY_COLUMN_XPOS, xpos);
        contentValues.put(BOUNDARY_COLUMN_YPOS, ypos);
        contentValues.put(BOUNDARY_COLUMN_ISHOLE, ishole);
        long id = db.insert(BOUNDARY_TABLE_NAME, null, contentValues);
//        db.close();
        return id;
    }

    public long insertShape(long lid, String stype, String scolor, String fcolor, int radius) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SHAPE_COLUMN_LID, lid);
        contentValues.put(SHAPE_COLUMN_SHAPETYPE, stype);
        contentValues.put(SHAPE_COLUMN_SCOLOR, scolor);
        contentValues.put(SHAPE_COLUMN_FCOLOR, fcolor);
        contentValues.put(SHAPE_COLUMN_RADIUS, radius);
        long id = db.insert(SHAPE_TABLE_NAME, null, contentValues);
//        db.close();
        return id;
    }

    public long insertCategory(int cid, String name, String icon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORY_COLUMN_ID, cid);
        contentValues.put(CATEGORY_COLUMN_NAME, name);
        contentValues.put(CATEGORY_COLUMN_ICON, icon);
        long id = db.insert(CATEGORY_TABLE_NAME, null, contentValues);
//        db.close();
        return id;
    }

    public Cursor getData(String tableName, String columnName, long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + tableName + " where " + columnName + " = " + id + "", null);
//        db.close();
        return res;
    }

    public Cursor getData(String tableName, String columnName, String column) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + tableName + " where " + columnName + " = '" + column + "' ", null);
//        db.close();
        return res;
    }

    public Cursor getAllLandmarksWithTitleLike(String column) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + LANDMARK_TABLE_NAME + " where " + LANDMARK_COLUMN_TITLE + " like '%" + column + "%' ", null);
        return res;
    }

    public Cursor getLandmarkWithTitle(String column) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + LANDMARK_TABLE_NAME + " where " + LANDMARK_COLUMN_TITLE + " = '" + column + "' ", null);
        return res;
    }

    public Cursor getLandmarkLikeTitle(String column) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + LANDMARK_TABLE_NAME + " where " + LANDMARK_COLUMN_TITLE + " like '%" + column + "%' ", null);
        return res;
    }

    public int numberOfRows(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, tableName);
//        db.close();
        return numRows;
    }

    public boolean updateLandmark(long id, String title, String category, double xpos, double pos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LANDMARK_COLUMN_TITLE, title);
        contentValues.put(LANDMARK_COLUMN_CATEGORY, title);
        contentValues.put(LANDMARK_COLUMN_XPOS, title);
        contentValues.put(LANDMARK_COLUMN_YPOS, title);
        db.update(LANDMARK_TABLE_NAME, contentValues, LANDMARK_COLUMN_ID + " = ? ", new String[]{Long.toString(id)});
//        db.close();
        return true;
    }

    public boolean updateBoundary(long id, double xpos, double ypos, int ishole) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BOUNDARY_COLUMN_XPOS, xpos);
        contentValues.put(BOUNDARY_COLUMN_YPOS, ypos);
        contentValues.put(BOUNDARY_COLUMN_ISHOLE, ishole);
        db.update(BOUNDARY_TABLE_NAME, contentValues, BOUNDARY_COLUMN_SID + " = ? ", new String[]{Long.toString(id)});
//        db.close();
        return true;
    }

    public boolean updateShape(long id, int lid, String stype, String scolor, String fcolor, int radius) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SHAPE_COLUMN_LID, lid);
        contentValues.put(SHAPE_COLUMN_SHAPETYPE, stype);
        contentValues.put(SHAPE_COLUMN_SCOLOR, scolor);
        contentValues.put(SHAPE_COLUMN_FCOLOR, fcolor);
        contentValues.put(SHAPE_COLUMN_RADIUS, radius);
        db.update(SHAPE_TABLE_NAME, contentValues, SHAPE_COLUMN_ID + " = ? ", new String[]{Long.toString(id)});
//        db.close();
        return true;
    }

    public boolean updateCategory(int id, String name, String icon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORY_COLUMN_NAME, name);
        contentValues.put(CATEGORY_COLUMN_ICON, icon);
        db.update(CATEGORY_TABLE_NAME, contentValues, CATEGORY_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
//        db.close();
        return true;
    }

    public boolean updateRoom(int id, String type, String officename, String roomnum, String phone, String head, String description, int lid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROOM_COLUMN_TYPE, type);
        contentValues.put(ROOM_COLUMN_OFFICE, officename);
        contentValues.put(ROOM_COLUMN_ROOM, roomnum);
        contentValues.put(ROOM_COLUMN_PHONE, phone);
        contentValues.put(ROOM_COLUMN_HEAD, head);
        contentValues.put(ROOM_COLUMN_DESCRIPTION, description);
        contentValues.put(ROOM_COLUMN_BUILDING, lid);
        db.update(ROOM_TABLE_NAME, contentValues, ROOM_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
//        db.close();
        return true;
    }

    public Integer deleteItem(String tableName, String columnName, Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer num = db.delete(tableName,
                columnName + " = ? ",
                new String[]{Integer.toString(id)});
//        db.close();
        return num;
    }

    public Integer deleteLandmark(int id) {
        this.deleteItem(LANDMARK_TABLE_NAME, LANDMARK_COLUMN_ID, id);
        Cursor shapeID = this.getData(SHAPE_TABLE_NAME, SHAPE_COLUMN_LID, id);
        this.deleteItem(SHAPE_COLUMN_ID, SHAPE_COLUMN_LID, id);
        return this.deleteItem(BOUNDARY_TABLE_NAME, SHAPE_COLUMN_ID, shapeID.getInt(shapeID.getColumnIndex(SHAPE_COLUMN_ID)));
    }

    public ArrayList<Category> getAllCategory() {
        ArrayList<Category> array_list = new ArrayList<Category>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + CATEGORY_TABLE_NAME, null);
        res.moveToFirst();
        Category category;
        while (res.isAfterLast() == false) {
            category = new Category(res.getInt(res.getColumnIndex(CATEGORY_COLUMN_ID)), res.getString(res.getColumnIndex(CATEGORY_COLUMN_NAME)), res.getString(res.getColumnIndex(CATEGORY_COLUMN_ICON)));
            array_list.add(category);
            res.moveToNext();
        }
        db.close();

        return array_list;
    }

    public ArrayList<Land> getAllLandmark() {
        ArrayList<Land> array_list = new ArrayList<Land>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor landmarkCursor = db.rawQuery("select * from " + LANDMARK_TABLE_NAME, null);
//        db.close();
        landmarkCursor.moveToFirst();
        Land landmark;
        while (landmarkCursor.isAfterLast() == false) {
            landmark = new Land();
            long lid = landmarkCursor.getInt(landmarkCursor.getColumnIndex(LANDMARK_COLUMN_ID));
            landmark.setId(lid);
            landmark.setTitle(landmarkCursor.getString(landmarkCursor.getColumnIndex(LANDMARK_COLUMN_TITLE)));
            Cursor categoryCursor = this.getData(CATEGORY_TABLE_NAME, CATEGORY_COLUMN_NAME, landmarkCursor.getString(landmarkCursor.getColumnIndex(LANDMARK_COLUMN_CATEGORY)));
            categoryCursor.moveToFirst();
            Category category = new Category(categoryCursor.getInt(categoryCursor.getColumnIndex(CATEGORY_COLUMN_ID)), categoryCursor.getString(categoryCursor.getColumnIndex(CATEGORY_COLUMN_NAME)), categoryCursor.getString(categoryCursor.getColumnIndex(CATEGORY_COLUMN_ICON)));
            landmark.setCategory(category);
            Cursor shapeCursor = this.getData(SHAPE_TABLE_NAME, SHAPE_COLUMN_LID, lid);
            Shape shape = new Shape();
            shapeCursor.moveToFirst();
            long sid = shapeCursor.getInt(shapeCursor.getColumnIndex(SHAPE_COLUMN_ID));
            shape.setShapeID(sid);
            shape.setLandmarkID(shapeCursor.getInt(shapeCursor.getColumnIndex(SHAPE_COLUMN_LID)));
            shape.setShapeType(shapeCursor.getString(shapeCursor.getColumnIndex(SHAPE_COLUMN_SHAPETYPE)));
            shape.setFillColor(shapeCursor.getString(shapeCursor.getColumnIndex(SHAPE_COLUMN_FCOLOR)));
            shape.setStrokeColor(shapeCursor.getString(shapeCursor.getColumnIndex(SHAPE_COLUMN_SCOLOR)));
            shape.setRadius(shapeCursor.getInt(shapeCursor.getColumnIndex(SHAPE_COLUMN_RADIUS)));
            landmark.setShape(shape);
            Cursor boundary = this.getData(BOUNDARY_TABLE_NAME, BOUNDARY_COLUMN_SID, sid);
            boundary.moveToFirst();
            while (boundary.isAfterLast() == false) {
                double x = boundary.getDouble(boundary.getColumnIndex(BOUNDARY_COLUMN_XPOS));
                double y = boundary.getDouble(boundary.getColumnIndex(BOUNDARY_COLUMN_YPOS));
                Log.d("Boundaries", "x=" + x + " y=" + y + " title=" + landmark.getTitle());
                landmark.addLatLngs(x, y);
                boundary.moveToNext();
            }

            Cursor rooms = this.getData(ROOM_TABLE_NAME, ROOM_COLUMN_BUILDING, lid);
            rooms.moveToFirst();
            while (rooms.isAfterLast() == false) {
                Room room = new Room();
                room.setId(rooms.getLong(rooms.getColumnIndex(ROOM_COLUMN_ID)));
                room.setType(rooms.getString(rooms.getColumnIndex(ROOM_COLUMN_TYPE)));
                room.setOffice(rooms.getString(rooms.getColumnIndex(ROOM_COLUMN_OFFICE)));
                room.setRoom(rooms.getString(rooms.getColumnIndex(ROOM_COLUMN_ROOM)));
                room.setPhone(rooms.getString(rooms.getColumnIndex(ROOM_COLUMN_PHONE)));
                room.setHead(rooms.getString(rooms.getColumnIndex(ROOM_COLUMN_HEAD)));
                room.setDescription(rooms.getString(rooms.getColumnIndex(ROOM_COLUMN_DESCRIPTION)));
                room.setLandmarkId(rooms.getLong(rooms.getColumnIndex(ROOM_COLUMN_BUILDING)));
                landmark.addRoom(room);
                rooms.moveToNext();
            }

            array_list.add(landmark);
            landmarkCursor.moveToNext();
        }

        return array_list;
    }

    public ArrayList<Building> getAllBuilding() {
        ArrayList<Building> array_list = new ArrayList<Building>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + LANDMARK_TABLE_NAME + " where " + LANDMARK_COLUMN_CATEGORY + " = 'Building'", null);
        res.moveToFirst();
        Building building;
        while (res.isAfterLast() == false) {
            building = new Building(res.getInt(res.getColumnIndex(LANDMARK_COLUMN_ID)), res.getString(res.getColumnIndex(LANDMARK_COLUMN_TITLE)));
            array_list.add(building);
            res.moveToNext();
        }
        db.close();

        return array_list;
    }

    public void addLandmark(Land landmark) {
        int id = (int) this.insertLandmark(landmark.getTitle(), landmark.getCategory().getCategoryName(), landmark.getXpos(), landmark.getYpos());
        int sid = (int) this.insertShape(id, landmark.getShape().getShapeType(), landmark.getShape().getStrokeColor(), landmark.getShape().getFillColor(), landmark.getShape().getRadius());
        for (LatLng bound : landmark.getLatLngs()) {
            this.insertBoundary(sid, bound.latitude, bound.longitude, 0);
        }
    }

    public void editLandmark(Land landmark) {
        this.updateLandmark(landmark.getId(), landmark.getTitle(), landmark.getCategory().getCategoryName(), landmark.getXpos(), landmark.getYpos());
        this.updateShape(landmark.getShape().getShapeID(), landmark.getShape().getLandmarkID(), landmark.getShape().getShapeType(), landmark.getShape().getStrokeColor(), landmark.getShape().getFillColor(), landmark.getShape().getRadius());
        for (LatLng bound : landmark.getLatLngs()) {
            this.updateBoundary(landmark.getShape().getShapeID(), bound.latitude, bound.longitude, 0);
        }
    }

    /*
    public ArrayList<String> getAllCotacts()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }*/
}

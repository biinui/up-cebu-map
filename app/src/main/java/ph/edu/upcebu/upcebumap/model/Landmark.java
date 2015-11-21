package ph.edu.upcebu.upcebumap.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.List;

import ph.edu.upcebu.upcebumap.util.Constant;

/**
 * Created by yu on 11/8/2015.
 */
public class Landmark {
    private long id;
    private String title;
    private LatLng latlng;
    private String category;

    public Landmark(String title, LatLng latlng) {
        this.title = title;
        this.latlng = latlng;
    }

    public static List<Landmark> Buildings() {
        return Arrays.asList(
                new Landmark(Constant.ADMINISTRATION_BUILDING, Constant.ADMINISTRATION_BUILDING_POSITION)
                , new Landmark(Constant.HEALTH_SERVICES, Constant.HEALTH_SERVICES_POSITION)
                , new Landmark(Constant.CANTEEN, Constant.CANTEEN_POSITION)
                , new Landmark(Constant.MANAGEMENT_BUILDING, Constant.MANAGEMENT_BUILDING_POSITION)
                , new Landmark(Constant.GP_BUILDING, Constant.GP_BUILDING_POSITION)
                , new Landmark(Constant.DORMITORY, Constant.DORMITORY_POSITION)
                , new Landmark(Constant.GUEST_HOUSE, Constant.GUEST_HOUSE_POSITION)
                , new Landmark(Constant.LIBRARY, Constant.LIBRARY_POSITION)
                , new Landmark(Constant.UNDERGRADUATE_BUILDING, Constant.UNDERGRADUATE_BUILDING_POSITION)
                , new Landmark(Constant.AS_WEST_WING, Constant.AS_WEST_WING_POSITION)
                , new Landmark(Constant.AS_LOBBY, Constant.AS_LOBBY_POSITION)
                , new Landmark(Constant.AS_EAST_WING, Constant.AS_EAST_WING_POSITION)
                , new Landmark(Constant.HIGH_SCHOOL_CLASSROOMS, Constant.HIGH_SCHOOL_CLASSROOMS_POSITION)
                , new Landmark(Constant.HIGH_SCHOOL_SCIENCE_BUILDING, Constant.HIGH_SCHOOL_SCIENCE_BUILDING_POSITION)
                , new Landmark(Constant.HIGH_SCHOOL_FACULTY_ROOM, Constant.HIGH_SCHOOL_FACULTY_ROOM_POSITION)
        );
    }

    public static List<Landmark> ActivityAreas() {
        return Arrays.asList(
                new Landmark(Constant.TENNIS_VOLLEYBALL_COURT, Constant.TENNIS_VOLLEYBALL_COURT_POSITION)
                , new Landmark(Constant.OBLATION_SQUARE, Constant.OBLATION_SQUARE_POSITION)
                , new Landmark(Constant.COLLEGE_BASKETBALL_COURT, Constant.COLLEGE_BASKETBALL_COURT_POSITION)
                , new Landmark(Constant.COLLEGE_FOOTBALL_FIELD, Constant.COLLEGE_FOOTBALL_FIELD_POSITION)
                , new Landmark(Constant.COLLEGE_STAGE, Constant.COLLEGE_STAGE_POSITION)
                , new Landmark(Constant.HIGH_SCHOOL_OPEN_COURT, Constant.HIGH_SCHOOL_OPEN_COURT_POSITION)
                , new Landmark(Constant.HIGH_SCHOOL_FIELD, Constant.HIGH_SCHOOL_FIELD_POSITION)
                , new Landmark(Constant.AS_FIELD, Constant.AS_FIELD_POSITION)
        );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }
}

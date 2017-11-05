package in.bananaa.object.location;

public class LocationStore {
    private Integer id;
    private String name;
    private LocationType locationType;

    public LocationStore() {}

    public LocationStore(Integer id, String name, LocationType locationType) {
        this.id = id;
        this.name = name;
        this.locationType = locationType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }
}

package de.projectride.ride.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Place.
 */
@Entity
@Table(name = "place")
public class Place implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "postcode")
    private Integer postcode;

    @Column(name = "city_name")
    private String cityName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Place latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Place longitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getPostcode() {
        return postcode;
    }

    public Place postcode(Integer postcode) {
        this.postcode = postcode;
        return this;
    }

    public void setPostcode(Integer postcode) {
        this.postcode = postcode;
    }

    public String getCityName() {
        return cityName;
    }

    public Place cityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Place place = (Place) o;
        if(place.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, place.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Place{" +
            "id=" + id +
            ", latitude='" + latitude + "'" +
            ", longitude='" + longitude + "'" +
            ", postcode='" + postcode + "'" +
            ", cityName='" + cityName + "'" +
            '}';
    }
}

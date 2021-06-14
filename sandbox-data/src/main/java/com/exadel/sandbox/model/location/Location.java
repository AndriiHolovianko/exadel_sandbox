package com.exadel.sandbox.model.location;

import com.exadel.sandbox.model.BaseEntity;
import com.exadel.sandbox.model.vendorinfo.VendorLocation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "location")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "city")
@EqualsAndHashCode(callSuper = false,exclude = {"city", "vendorLocations"})
public class Location extends BaseEntity {

    @NonNull
    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "street")
    private String street;

    @Column(name = "number")
    private String number;

    @ManyToOne
    @JoinColumn(name = "city_id")
    @JsonIgnore
    private City city;

    @OneToMany(mappedBy = "location")
    @JsonIgnore
    private Set<VendorLocation> vendorLocations = new HashSet<>();

}

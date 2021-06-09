package com.exadel.sandbox.model.vendorinfo;

import com.exadel.sandbox.model.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vendor")
@Data
@NoArgsConstructor
@Builder
@ToString(exclude = "products")
@EqualsAndHashCode(callSuper = false, exclude = "products")
public class Vendor extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "vendor")
    private Set<Product> products = new HashSet<>();

}

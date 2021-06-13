CREATE TABLE vendor_location
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    vendor_id   BIGINT,
    location_id BIGINT,
    FOREIGN KEY (vendor_id) REFERENCES vendor (id),
    FOREIGN KEY (location_id) REFERENCES location (id)
);
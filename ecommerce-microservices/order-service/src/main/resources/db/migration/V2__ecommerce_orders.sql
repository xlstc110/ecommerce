CREATE TABLE `orders`
(
    `id`               VARCHAR(36) NOT NULL,
    `order_time_ms`    BIGINT      NOT NULL,
    `total_cost_cents` INT         NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `order_items`
(
    `order_id`                   VARCHAR(36) NOT NULL,
    `line_number`                INT         NOT NULL,
    `product_id`                 VARCHAR(36) NOT NULL,
    `quantity`                   INT         NOT NULL,
    `estimated_delivery_time_ms` BIGINT      NOT NULL,
    PRIMARY KEY (`order_id`, `line_number`),
    CONSTRAINT `fk_order_items_order`
        FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
);

INSERT INTO `orders` (`id`, `order_time_ms`, `total_cost_cents`)
VALUES ('27cba69d-4c3d-4098-b42d-ac7fa62b7664', 1723456800000, 3506),
       ('b6b6c212-d30e-4d4a-805d-90b52ce6b37d', 1718013600000, 4190);

INSERT INTO `order_items`
    (`order_id`, `line_number`, `product_id`, `quantity`, `estimated_delivery_time_ms`)
VALUES
    ('27cba69d-4c3d-4098-b42d-ac7fa62b7664', 0, 'e43638ce-6aa0-4b85-b27f-e1d07eb678c6', 1, 1723716000000),
    ('27cba69d-4c3d-4098-b42d-ac7fa62b7664', 1, '83d4ca15-0f35-48f5-b7a3-1ea210004f2e', 2, 1723456800000),
    ('b6b6c212-d30e-4d4a-805d-90b52ce6b37d', 0, '15b6fc6f-327a-4ec4-896f-486349e85a3d', 2, 1718618400000);

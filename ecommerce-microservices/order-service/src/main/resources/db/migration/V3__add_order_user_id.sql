ALTER TABLE `orders`
    ADD COLUMN `user_id` VARCHAR(255) NULL;

UPDATE `orders`
SET `user_id` = 'demo-user'
WHERE `user_id` IS NULL;

ALTER TABLE `orders`
    MODIFY COLUMN `user_id` VARCHAR(255) NOT NULL;

CREATE INDEX `idx_orders_user_time`
    ON `orders` (`user_id`, `order_time_ms`);

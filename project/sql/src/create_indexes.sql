/* Indexes for Users */

CREATE INDEX user_index_username ON Users USING BTREE(name);

CREATE INDEX user_index_password ON Users USING BTREE(password);

CREATE INDEX user_index_type ON Users USING BTREE(type);


/* Indexes for Store */

CREATE INDEX store_index_storeid ON Store USING BTREE(storeID);

CREATE INDEX store_index_managerID ON Store USING BTREE(managerID);

/* Indexes for Product */

CREATE INDEX product_index_storeid ON Product USING BTREE(storeID);

/* Indexes for Warehouse */

CREATE INDEX warehouse_index_warehouseID ON Warehouse USING BTREE(WarehouseID);

/* Indexes for Orders */

CREATE INDEX orders_index_orderNumber ON Orders USING BTREE(orderNumber);


CREATE INDEX order_index_customerID ON Orders USING BTREE(customerID);

CREATE INDEX order_index_storeID ON Orders USING BTREE(storeID);

/* Indexes for ProductUpdates */

CREATE INDEX productupdates_index_managerid ON ProductUpdates USING BTREE(managerID);




drop table Dianping_City_SubCategory;
create table dbo.Dianping_City_SubCategory (
    sub_category varchar(255),
    sub_category_id varchar(255) not null,
    category varchar(255),
    category_id varchar(255) not null,
    primary_category varchar(255),
    primary_category_id varchar(255),
    city_id varchar(255) not null,
    city_cnname varchar(255),
    city_enname varchar(255),
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_City_SubCategory ADD CONSTRAINT Dianping_City_SubCategory_PK PRIMARY KEY (sub_category_id,category_id,city_id);

drop table Dianping_City_SubRegion;
create table dbo.Dianping_City_SubRegion (
    sub_region varchar(255),
    sub_region_id varchar(255) not null,
    region varchar(255),
    region_id varchar(255) not null,
    city_id varchar(255) not null,
    city_cnname varchar(255),
    city_enname varchar(255),
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_City_SubRegion ADD CONSTRAINT Dianping_City_SubRegion_PK PRIMARY KEY (sub_region_id,region_id,city_id);

drop table Dianping_SubCategory_SubRegion;
create table dbo.Dianping_SubCategory_SubRegion (
    url varchar(500) not null,
    sub_category_id varchar(255) not null,
    sub_category varchar(255),
    category_id varchar(255),
    category varchar(255),
    primary_category_id varchar(255),
    primary_category varchar(255),
    sub_region_id varchar(255) not null,
    sub_region varchar(255) not null,
    region_id varchar(255),
    region varchar(255),
    city_id varchar(255),
    city_cnname varchar(255),
    city_enname varchar(255),
    shop_total_page int,
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_SubCategory_SubRegion ADD CONSTRAINT Dianping_SubCategory_SubRegion_PK PRIMARY KEY (url);

drop table Dianping_SubCategory_SubRegion_Page;
create table dbo.Dianping_SubCategory_SubRegion_Page (
    url varchar(500) not null,
    sub_category_id varchar(255) not null,
    sub_category varchar(255),
    category_id varchar(255),
    category varchar(255),
    primary_category_id varchar(255),
    primary_category varchar(255),
    sub_region_id varchar(255) not null,
    sub_region varchar(255) not null,
    region_id varchar(255),
    region varchar(255),
    city_id varchar(255),
    city_cnname varchar(255),
    city_enname varchar(255),
    shop_total_page int,
    page int not null,
    status int,
    version varchar(255) not null,
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_SubCategory_SubRegion_Page ADD CONSTRAINT Dianping_SubCategory_SubRegion_Page_PK PRIMARY KEY (url, page, version);

-- 店铺信息表
drop table Dianping_ShopInfo;
create table dbo.Dianping_ShopInfo (
    shop_id varchar(255) not null,
    shop_name varchar(500) not null,
    shop_url varchar(500),
    tuan_support smallint,
    out_support smallint,
    promotion_support smallint,
    book_support smallint,
    has_branch smallint,
    brand_url varchar(255),
    star_level varchar(255),
    review_num int default 0,
    avg_price varchar(50),
    address varchar(500),
    taste_score varchar(50),
    environment_score varchar(50),
    service_score varchar(50),
    sub_category_id varchar(255),
    sub_region_id varchar(255),
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_ShopInfo ADD CONSTRAINT Dianping_ShopInfo_PK PRIMARY KEY (shop_id);


-- 店铺推荐菜
drop table Dianping_Shop_Recommend_Info;
create table dbo.Dianping_Shop_Recommend_Info (
    dish_id varchar(255) not null,
    dish varchar(255) not null,
    shop_id varchar(255) not null,
    recommend_count int,
    price varchar(255),
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_Shop_Recommend_Info ADD CONSTRAINT Dianping_Shop_Recommend_Info_PK PRIMARY KEY (dish_id, shop_id);





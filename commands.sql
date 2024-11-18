CREATE SEQUENCE seq_product
START WITH 1
INCREMENT BY 1;

CREATE TABLE tb_products(
    cd_product NUMBER(5) PRIMARY KEY,
    nm_product VARCHAR2(50) NOT NULL,
    ds_product VARCHAR2(200),
    vl_product NUMBER(10,2),
    nr_stock NUMBER(5)
);

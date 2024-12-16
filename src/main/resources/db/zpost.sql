CREATE TABLE warehouse_company_connection(
        warehouse   UUID NOT NULL REFERENCES warehouse(id),
        company     UUID NOT NULL REFERENCES company(id)
);

CREATE TABLE company_user_connection(
        userId      UUID NOT NULL REFERENCES company_user(id),
        companyId   UUID NOT NULL REFERENCES company(id)
);

CREATE TABLE warehouse_user_connection(
        userId      UUID NOT NULL REFERENCES company_user(id),
        warehouse   UUID NOT NULL REFERENCES warehouse(id)
);

CREATE TABLE tool_warehouse_connection(
        toolId      UUID NOT NULL REFERENCES tool(id),
        warehouse   UUID NOT NULL REFERENCES warehouse(id)
);

CREATE TABLE product_warehouse_connection(
        productId   UUID NOT NULL REFERENCES product(id),
        warehouse   UUID NOT NULL REFERENCES warehouse(id)
);

CREATE TABLE supplier_company_connection(
        supplierId  UUID NOT NULL REFERENCES supplier(id),
        companyId   UUID NOT NULL REFERENCES company(id)
);

CREATE TABLE supplier_product_connection(
        supplierId  UUID NOT NULL REFERENCES supplier(id),
        productId   UUID NOT NULL REFERENCES product(id)
);

CREATE TABLE supplier_tool_connection(
        supplierId  UUID NOT NULL REFERENCES supplier(id),
        toolId      UUID NOT NULL REFERENCES tool(id)
);




-- @TODO create connection between warehouse and products

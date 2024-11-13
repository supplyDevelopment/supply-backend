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

-- @TODO create connection between warehouse and products

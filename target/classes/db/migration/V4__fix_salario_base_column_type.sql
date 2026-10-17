-- Corrige tipo da coluna salario_base: DECIMAL -> DOUBLE PRECISION (compatível com JPA Double)
ALTER TABLE cargos ALTER COLUMN salario_base TYPE DOUBLE PRECISION USING salario_base::double precision;
ALTER TABLE funcionarios ALTER COLUMN salario_base TYPE DOUBLE PRECISION USING salario_base::double precision;

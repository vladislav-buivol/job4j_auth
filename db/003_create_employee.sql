create table employee
(
    id          serial primary key not null,
    first_name  varchar(200),
    second_name varchar(200),
    inn         varchar(200),
    hire_date   date
);
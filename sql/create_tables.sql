create table issues (
    id uuid primary key DEFAULT uuid_generate_v4(),
    prjID uuid,
    message varchar(100),
    userID uuid NULLABLE,
    deadline date NULLABLE,
    create_time timestamp,
    global_role varchar(20),
    update_time timestamp NULLABLE
);

insert into users values (0, 'xXRausAusDenSchulden69Xx', 'Peter', 'Zwegat', current_date, current_timestamp, 'SCHULDENBERATER', current_timestamp)

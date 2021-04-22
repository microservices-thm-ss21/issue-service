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

insert into issues values (null,'34d93c95-f279-453c-8269-ac16efefded6', 'xXRausAusDenSchulden69Xx', null, null, current_date, 'SCHULDENBERATER', null)

create table issues (
    id uuid primary key DEFAULT uuid_generate_v4(),
    prjId uuid,
    message varchar(100),
    userId uuid NULLABLE,
    deadline date NULLABLE,
    createTime timestamp,
    globalRole varchar(20),
    updateTime timestamp NULLABLE
);

insert into issues values (null,'34d93c95-f279-453c-8269-ac16efefded6', 'xXRausAusDenSchulden69Xx', null, null, current_date, 'SCHULDENBERATER', null)

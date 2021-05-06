create table issues (
    id uuid primary key DEFAULT uuid_generate_v4(),
    project_id uuid NOT NULL,
    message varchar(100) NOT NULL,
    assigned_user_id uuid NULL,
    deadline date NULL,
    global_role varchar(20) NOT NULL, -- Warum Global Role?
    create_time timestamp NOT NULL ,
    update_time timestamp NULL,
    CONSTRAINT fk_project
        FOREIGN KEY (project_id)
            REFERENCES projects(project_id),
    CONSTRAINT fk_user
        FOREIGN KEY (assigned_user_id)
            REFERENCES users(user_id)
);

create table projects (
    project_id uuid primary key
);

create table users (
    user_id uuid primary key
);

insert into projects values ('54ed2c8e-054d-4fb0-81ac-d7ed726b1879');
insert into users values ('a443ffd0-f7a8-44f6-8ad3-87acd1e91042');

insert into issues values (DEFAULT,'54ed2c8e-054d-4fb0-81ac-d7ed726b1879', 'xXRausAusDenSchulden69Xx', 'a443ffd0-f7a8-44f6-8ad3-87acd1e91042',
                           null, 'SCHULDENBERATER', current_date,  null)

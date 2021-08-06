CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


create table projectIds (
    id uuid primary key
);

create table userIds (
    id uuid primary key
);

create table issues (
    id uuid primary key DEFAULT uuid_generate_v4(),
    project_id uuid NOT NULL,
    message varchar(100) NOT NULL,
    assigned_user_id uuid,
    creator_id uuid,
    deadline date,
    create_time timestamp NOT NULL,
    update_time timestamp,
    status varchar(30) NOT NULL,
    CONSTRAINT fk_project
        FOREIGN KEY (project_id)
            REFERENCES projectIds(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_assigned_user
        FOREIGN KEY (assigned_user_id)
            REFERENCES userIds(id)
            ON DELETE SET NULL,
    CONSTRAINT fk_creator_user
        FOREIGN KEY (creator_id)
            REFERENCES userIds(id)
            ON DELETE SET NULL
);


insert into projectIds values ('e86c57cb-d703-4f39-9632-3782cb5500e8');
insert into userIds values ('a443ffd0-f7a8-44f6-8ad3-87acd1e91042');
insert into userIds values ('a443ffd0-f7a8-44f6-8ad3-87acd1e91043');
insert into userIds values ('a443ffd0-f7a8-44f6-8ad3-87acd1e91044');

insert into issues values ('a3974d24-5735-410c-b109-ad262755d4d3','e86c57cb-d703-4f39-9632-3782cb5500e8', 'Das ist eine Nachricht', 'a443ffd0-f7a8-44f6-8ad3-87acd1e91044','a443ffd0-f7a8-44f6-8ad3-87acd1e91043',
                           null, current_date, null, 'OPEN');

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


create table projectIds (
    project_id uuid primary key
);

create table userIds (
    user_id uuid primary key
);

create table issues (
    id uuid primary key DEFAULT uuid_generate_v4(),
    project_id uuid NOT NULL,
    message varchar(100) NOT NULL,
    assigned_user_id uuid NULL,
    creator_id uuid NOT NULL,
    deadline date NULL,
    create_time timestamp NOT NULL ,
    update_time timestamp NULL,
    CONSTRAINT fk_project
        FOREIGN KEY (project_id)
            REFERENCES projectIds(project_id),
    CONSTRAINT fk_user
        FOREIGN KEY (assigned_user_id)
            REFERENCES userIds(user_id)
);


insert into projectIds values ('e86c57cb-d703-4f39-9632-3782cb5500e8');
insert into userIds values ('a443ffd0-f7a8-44f6-8ad3-87acd1e91042');
insert into userIds values ('a443ffd0-f7a8-44f6-8ad3-87acd1e91043');
insert into userIds values ('a443ffd0-f7a8-44f6-8ad3-87acd1e91044');

insert into issues values ('a3974d24-5735-410c-b109-ad262755d4d3','e86c57cb-d703-4f39-9632-3782cb5500e8', 'Das ist eine Nachricht', 'a443ffd0-f7a8-44f6-8ad3-87acd1e91044','a443ffd0-f7a8-44f6-8ad3-87acd1e91043',
                           null, current_date,  null);

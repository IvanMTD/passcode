create table if not exists authorities (
    id long primary key auto_increment,
    authority varchar(32) not null unique,
    display_name varchar(32) not null unique
);

//insert into authorities (authority,display_name) values ('ROLE_ADMIN','Administrator');
//insert into authorities (authority,display_name) values ('ROLE_USER','User');

create table if not exists persons (
    id long primary key auto_increment,
    authority_id long,
    username varchar(32) not null unique,
    password text not null,
    first_name varchar(32) not null,
    lastname varchar(64),
    placed_at date not null,
    FOREIGN KEY (authority_id) references authorities(id)
);

//insert into persons (authority_id, username, password, first_name, lastname, placed_at) VALUES ( 1,'morgan@ostrov.net','$2a$10$LpIJxeeXNQSIVjrMXv7Zp.7pC6c1ED37eP0WJz5AsXhM39CTOj7Rq','Иван','Карачков','2023-11-09');

create table if not exists content (
    id long primary key auto_increment,
    file_name varchar(64) not null,
    placed_at date not null,
    file_size long not null,
    hash_data text not null unique
);

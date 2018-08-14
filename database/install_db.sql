GRANT ALL PRIVILEGES ON dolphin.* TO 'dolphin'@'%' IDENTIFIED BY 'dolphinX2018';
GRANT ALL PRIVILEGES ON dolphin.* TO 'dolphin'@'localhost' IDENTIFIED BY 'dolphinX2018';
USE dolphin;
create table notes
(
	id integer auto_increment,
	content varchar(500) null,
	created_at datetime not null,
	title varchar(255) null,
	updated_at datetime not null,
	PRIMARY KEY (id)
)
;
INSERT INTO dolphin.notes (id, content, created_at, title, updated_at) VALUES (1, '1', '2018-08-10 02:37:17', '1', '2018-08-10 02:37:20');

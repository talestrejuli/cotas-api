create table usuarios(

    id bigint not null auto_increment,
    nome varchar(100) not null,
    email varchar(100) not null unique,
    aceita_aviso varchar(6) not null,
    data_nascimento varchar(100) not null,
    telefone varchar(100),
    cep varchar(9) not null,
    logradouro varchar(100) not null,
    bairro varchar(100) not null,
    cidade varchar(100) not null,
    complemento varchar(100),
    numero varchar(20),
    uf char(2) not null,

    primary key(id)

);
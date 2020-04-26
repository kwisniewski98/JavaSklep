WHILE(EXISTS(SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE CONSTRAINT_TYPE = 'FOREIGN KEY' AND TABLE_NAME IN ('Table1', 'Table2') AND CONSTRAINT_NAME LIKE '%FK__%__DL%'))
    BEGIN
        DECLARE @sql_alterTable_fk NVARCHAR(2000)

        SELECT  TOP 1 @sql_alterTable_fk = ('ALTER TABLE ' + TABLE_SCHEMA + '.[' + TABLE_NAME + '] DROP CONSTRAINT [' + CONSTRAINT_NAME + ']')
        FROM    INFORMATION_SCHEMA.TABLE_CONSTRAINTS
        WHERE   CONSTRAINT_TYPE = 'FOREIGN KEY'
          AND TABLE_NAME IN ('Table1', 'Table2')
          AND CONSTRAINT_NAME LIKE '%FK__%__DL%'

        EXEC (@sql_alterTable_fk)
    END


if not exists ( SELECT *
                 FROM INFORMATION_SCHEMA.TABLES where  TABLE_NAME = 'Adres') 
	create table Adres(
		id int PRIMARY KEY identity ,
		nr_lokalu int check (nr_lokalu > 0),
		nr_budynku int check(nr_budynku > 0) not null,
		ulica varchar(40) not null,
		miasto varchar(40) not null,
		wojewodztwo varchar(40) not null

	)
go

if not exists ( SELECT * 
                 FROM INFORMATION_SCHEMA.TABLES where  TABLE_NAME = 'Osoba')
	create table Osoba(
		id int PRIMARY KEY identity,
		imie varchar(20) not null,
		nazwisko varchar(20)not null,
		adres int foreign key references Adres(id) not null,

	)
go

if not exists ( SELECT * 
                 FROM INFORMATION_SCHEMA.TABLES where  TABLE_NAME = 'Oddzial') 
	create table Oddzial (
		id int PRIMARY KEY identity ,
		Adres int foreign key references Adres(id) not null ,
		manager int foreign key references Osoba(id) not null,
		nazwa varchar(20) not null,
		typ varchar(20) not null
	)
go

if not exists ( SELECT * 
                 FROM INFORMATION_SCHEMA.TABLES where  TABLE_NAME = 'Typ') 
	create table Typ(
		id int PRIMARY KEY identity ,
		skrot varchar(6) check (len(skrot) = 6) not null,
		opis varchar(1000),
		przeznaczenie varchar(20),		
		)
go

if not exists ( SELECT * 
                 FROM INFORMATION_SCHEMA.TABLES where  TABLE_NAME = 'Produkt') 
	create table Produkt(
		id int PRIMARY KEY identity ,
		Nazwa varchar(100) not null,
		cena_netto float check (cena_netto > 0) not null,
		vat float check (vat > 0) not null,
		typ int foreign key references Typ(id) not null
		)
go

if not exists ( SELECT * 
                 FROM INFORMATION_SCHEMA.TABLES where  TABLE_NAME = 'Stan') 
	create table Stan(
		id int PRIMARY KEY identity ,
		oddzial int foreign key references Oddzial(id) not null,
		ilosc int check (ilosc >=0),
		produkt int foreign key references Produkt(id) not null,
		wartosc_netto float ,
		)
go

if not exists ( SELECT * 
                 FROM INFORMATION_SCHEMA.TABLES where  TABLE_NAME = 'Zamowienie') 
	create table Zamowienie(
		id int PRIMARY KEY identity ,
		ilosc int check (ilosc >=0) not null,
		produkt int foreign key references Stan(id) not null,
		wartosc_brutto float,
		data_zamowienia Date not null,
		data_realizacji Date,
		status varchar(20)
		)
go

if not exists ( SELECT * 
                 FROM INFORMATION_SCHEMA.TABLES where  TABLE_NAME = 'Zapotrzebowanie') 
	create table Zapotrzebowanie(
		id int PRIMARY KEY identity ,
		oddzial int foreign key references Oddzial(id) not null,
		ilosc int check (ilosc >=0) not null,
		produkt int foreign key references Produkt(id) not null,
		wartosc float,
		data Date not null,

		)
go
if not exists ( SELECT *
                FROM INFORMATION_SCHEMA.TABLES where  TABLE_NAME = 'Uzytkownicy')
create table Uzytkownik(
                           login varchar(30) primary key,
                           haslo varchar(30) not null ,
                           typ varchar(15) check(typ = 'Manager' or typ = 'Klient' or typ = 'Sprzedawca') not null,
                           osoba int foreign key references Osoba(id),
)
go

insert into Adres values(1, 1, 'Dluga', 'Gdansk', 'Pomorskie')
insert into Adres values(2, 2, 'Szeroka', 'Gdansk', 'Pomorskie')
insert into Adres values(3, 3, 'Wojska Polskiego', 'Gdansk', 'Pomorskie')
go

insert into Osoba values('Andrzej', 'Kowalski', 1)
insert into Osoba values('Ania', 'Kowalska', 1)
insert into Osoba values('Grzegorz', 'Nowak', 2)
go

insert into Oddzial values(2, 1, 'G³ówny sklep', 'Sklep')
insert into Oddzial values(3, 2, 'G³owny magazyn', 'Magazyn')
go

insert into Typ values('PROCCG', 'Procesor konsumencki', 'Konsumencki')
insert into Typ values('PROCPR', 'Procesor do zastosowan polprofesjonalnych', 'HEDT')
insert into Typ values('PROCSV', 'Procesor do serwerow', 'Serwer')
go

insert into Produkt values('Intel core i7-9700K', 1600, 0.23, 1)
insert into Produkt values('Intel core i7-9900K', 2200, 0.23, 2)
go

insert into Stan (oddzial, ilosc, produkt) values(1, 5, 1)
insert into Stan (oddzial, ilosc, produkt) values(2, 20, 1)
insert into Stan (oddzial, ilosc, produkt) values(1, 1, 2)
insert into Stan (oddzial, ilosc, produkt) values(2, 5, 2)
go

insert into Zamowienie (ilosc, produkt, wartosc_brutto, data_zamowienia) values(1, 1, 1968, GETDATE())
go

insert into Zapotrzebowanie (oddzial, ilosc, produkt, data )values(2, 5, 2, GETDATE())
go
insert into Uzytkownik values ('temp', 'temp123', 'Manager', 1)
insert into Uzytkownik values ('Klient', 'temp123', 'Klient', 1)
insert into Uzytkownik values ('S', 'temp123', 'Sprzedawca', 1)

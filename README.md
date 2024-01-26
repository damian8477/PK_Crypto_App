# PK Crypto App

Witaj w repozytorium PK Crypto App! To jest projekt związanym z kryptowalutami.

## Wprowadzenie

Projekt ma na celu dostarczenie narzędzia do obsługi konta binance. 
Aplikacja oferuje funkcje takie jak: śledzenie cen, otwieranie/zamykanie zleceń, alerty na telegrama, własne strategie.

## Wykorzystane technlogie

1. **Programming Language:** Java 11+
2. **Framework:** Spring Boot 2.x.x
3. **Security:** Spring Security
4. **Database:** MySQL
5. **Frontend:** JSP

## Funkcje

- Rejestracja użytkowników
- Przeglądanie aktualnych zleceń
- Przeglądanie historii zleceń
- Dodania strategii
- Zmiana hasła za pomocą e-maila
- Śledzenie cen różnych kryptowalut.
- Powiadomienia o zmianach cen

## Rozpoczęcie

Poniżej znajdziesz instrukcje, jak uruchomić projekt na swoim lokalnym środowisku.

### Instalacja

1. Sklonuj repozytorium lub fork: `git clone https://github.com/damian8477/PK_Crypto_App.git`
2. Otwórz aplikacje w Intellij
3. Dodaj konfiguracje Spring boot
4. Ustawienie zmiennych środowiskowych dla obsługi kont na telegramie, email, binance oraz wybranej bazy danych (MySql)
      - telegram.token.var.name
      - telegram.token.var.token
      
      - binance.api.var.api-key
      - binance.api.var.secret-key
      
      - spring.datasource.url
      - spring.datasource.username
      - spring.datasource.password
      
      - spring.mail.username
      - spring.mail.password

### Uruchomienie w konsoli
1. Stwórz plik jar `mvn clean compile package`
2. Utwórz dwa telegram boty
3. Stwórz email administratora np. gmail
4. Stwórz baze danych mysql 
2. Wystartuj aplikacje `java 
                        -Dbinance.api.var.api-key=apikey
                        -Dbinance.api.var.secret-key=secretkey
                        -Dspring.datasource.password=password
                        -Dspring.datasource.url=jdbc:mysql://localhost:3306/db
                        -Dspring.mail.password=password
                        -Dspring.mail.username=email@gmail.com 
                        -Dtelegram.token.var.name=PkCryptoAppInfo
                        -Dtelegram.token.var.token=token
                        -DTELEGRAM_NAME=PkCryptoApp 
                        -DTELEGRAM_TOKEN=token
                        -Dtelegram.token.vip.name=SignalRead 
                        -Dtelegram.token.vip.token=token
                        -jar /path/PK_Crypto_App-0.0.1-SNAPSHOT.jar`

## Użycie

1. Zarejestruj się
2. Zaloguj
3. Przejdź do ustawień i dodaj konto binance
4. Przejdź do ustawień i edytuj je wpisz w czat telegrama podany kod, aby powiązać konto użytkownika z telegramem
5. Następnie zgodnie z tym co widać, szczegóły wkrótce.


# TalkMeow

TalkMeow is a simple chat application that allows users to 
communicate with each other in real-time.

## Why that name?

TalkMeow is like TalkNow but with a cat twist. 
The main theme of the application is cats.
The theme might be now present, but it will be in the future for sure :)

## Must before running
1. Docker installed and running. Correct container for fallback database will be created automatically.
2. Use `init.sql` file to create the database and tables in the fallback database.
3. `.env` file created in resources folder with following format (if the data is not present here, enter your own):
    ```shell
    #Database
    DATABASE_URL=
    DATABASE_USERNAME=
    DATABASE_PASSWORD=
    
    # Fallback database
    FALLBACK_DATABASE_URL=jdbc:postgresql://localhost:5432/talkMeowDatabase
    FALLBACK_DATABASE_USERNAME=VeryHardLogin
    FALLBACK_DATABASE_PASSWORD=VeryStrongPassword
    ```
4. Plugin installed that will make use of `.env` file. For example: `EnvFile` by `Borys Pierov` in IntelliJ IDEA and `.env` added to run configuration

## Important endpoints
1. `/home` - Home page
2. `/admin/home` - Admin home page
3. `/manager/home` - Manager home page

## Features
### Registration and login
1. User can register and login

### Chat
1. User can chat with other users

### Admin
1. Admin can manage users

### Manager
1. Manager can manage users
    








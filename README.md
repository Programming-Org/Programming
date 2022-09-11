[![Java CI with Gradle](https://github.com/Programming-Org/Programming/actions/workflows/gradle.yml/badge.svg)](https://github.com/Programming-Org/Programming/actions/workflows/gradle.yml) [![All Contributors](https://img.shields.io/badge/all_contributors-2-orange.svg?style=flat-square)](#contributors-) [![codecov](https://codecov.io/gh/Programming-Org/Programming/branch/main/graph/badge.svg?token=M1KBWF0CDY)](https://codecov.io/gh/Programming-Org/Programming)

# Welcome to the Programming Server's Discord bot!

## Note
The bot uses Java 17

## Before you start
Make sure you have a good knowledge of java and know how [JDA](https://github.com/DV8FromTheWorld/JDA/) works.

Also read the [Code of conduct](https://github.com/Programming-Org/Programming/blob/main/.github/CODE_OF_CONDUCT.md).

And finally do not forget to have a look at the [Example Command](https://github.com/Programming-Org/Programming/blob/main/src/main/java/io/github/org/programming/bot/commands/ExampleCommand.java) to get an idea of how your command should be structured.
## Getting Started

### Part 1
To get started, you'll need to create a Discord bot account and get a token.

   1. Go to https://discord.com/developers/applications
   2. Click on the **New Application** button
   3. Create a name
   4. Click on the **Bot** tab
   5. Give the bot a name
   6. After that go to **OAuth2** tab
   7. click **URL Generator**
   8. Select bot and applications.commands
   9. Select the needed permissions
   10. Copy the url and add the bot to your server
   11. For the token go back to the bot tab and copy the Token.
   12. See part 2

### Part 2
Once you have added the bot to your server, you will need to create a .env file as seen here.

Where it says 11111111111 replace it with a long value
```json
{
  "TOKEN": "YOUR_TOKEN_HERE",
  "GUILD_ID": 11111111111,
  "OWNER_ID": 11111111111,
  "AUDIT_LOG_CHANNEL_ID": 11111111111,
  "CORE_POOL_SIZE": 1,
  "DATABASE_NAME": "YOUR_DATABASE_NAME_HERE",
  "DB_USER": "YOUR_DB_USER_HERE",
  "DB_PASSWORD": "YOUR_DB_PASSWORD_HERE",
  "DB_URL": "YOUR_DB_URL_HERE",
  "PORT_NUMBER": 8080,
  "SERVER_NAME": "YOUR_SERVER_NAME_HERE",
  "HELP_CHANNEL_ID": 11111111111,
  "ACTIVE_QUESTION_CHANNEL_ID": 11111111111,
  "ASK_LIMIT": 3,
  "USER_AMOUNT_CHANNEL_ID": 11111111111
}
```

### Part 3
To get started with postgres you will need to install it using homebrew or any other package manager.

To install it with homebrew run the following command:
```brew install postgresql```

For linux see the [postgresql download](https://www.postgresql.org/download/linux/)

To start the server run the following command:
```brew servers start postgresql```


### Part 4

In order to make a command for the bot, you will need to create an issue and wait to see if the command is needed.

If it is then you will be assigned to the issue, and you can begin making the command.

Once you have made a command, you will need to create a pr on which you will be assigned and the team will review the code and see if it meets the standards.

If it does it will be merged immediately, if not you will be told what to change.

Copyright (C) 2022 - present, Programming Org


## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="http://realyusufismail-github-io.vercel.app"><img src="https://avatars.githubusercontent.com/u/67903886?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Yusuf Arfan Ismail</b></sub></a><br /><a href="#infra-RealYusufIsmail" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="https://github.com/Programming-Org/Programming/commits?author=RealYusufIsmail" title="Tests">⚠️</a> <a href="https://github.com/Programming-Org/Programming/commits?author=RealYusufIsmail" title="Code">💻</a> <a href="https://github.com/Programming-Org/Programming/pulls?q=is%3Apr+reviewed-by%3ARealYusufIsmail" title="Reviewed Pull Requests">👀</a></td>
    <td align="center"><a href="https://github.com/Haleloch"><img src="https://avatars.githubusercontent.com/u/75441206?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Haleloch</b></sub></a><br /><a href="https://github.com/Programming-Org/Programming/commits?author=Haleloch" title="Code">💻</a> <a href="https://github.com/Programming-Org/Programming/pulls?q=is%3Apr+reviewed-by%3AHaleloch" title="Reviewed Pull Requests">👀</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!

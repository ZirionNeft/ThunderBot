![Thunder Logo](https://github.com/ZirionNeft/ThunderBot/blob/master/images/logo-github-banner.png?raw=true)
# ThunderBot [![Discord Thunder Guild](https://img.shields.io/badge/Join-Thunder-%237289da.svg?style=flat-square&logo=discord)](https://discord.gg/Y4H9ctT) [![Travis Status](	https://img.shields.io/travis/ZirionNeft/ThunderBot/master.svg?style=flat-square&logo=travis)](https://travis-ci.org/ZirionNeft/ThunderBot)
A new open source bot for [Discord](https://discordapp.com/) with useful user functionality.

More information, new features, and the site will be developed in the future.

## Tasks
- [x] Bot Configuration for each guild
- [ ] Server stats
- [ ] User profiles, Levels
- [ ] Global economy, Currency
- [ ] Guild Notes, Events
<br>...And some fun features!

## Commands
The standard command prefix is ```>```, but it can be configured individually for each server.

Commands syntax: ```>cmd [arg1, arg2, ...]```

Example: ```>weather set Moscow 9:00```

Use | Arguments | Description
--- | --------- | -----------
help | [cmd name] | Shows commands list or help on the command specified in the argument
weather, wr | [City name]<br><br> set "City" [Broadcast time]<br><br> time [Broadcast time]  | Shows weather information
translate, tr | [lang]<br><br>list "lang" | Translate the specified text
set | *see `Guild settings`* | Guild settings 

## Configuration
### Bot config
File ```settings.properties```

### Guild settings


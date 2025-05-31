
---

Download the plugin from the following address:
[https://github.com/erikzimmermann/TradeSystem](https://github.com/erikzimmermann/TradeSystem)

Then, go to the `spigot` package and import the `ozaii` package into it.

Next, paste the following into the `config.yml` file:

```yaml
ozaii:
  listener:
    debug: true
  Database:
    MySQL:
      Connection_URL: "jdbc:mysql://localhost:3306/<name_of_database_where_leaderos_is>?useSSL=false&autoReconnect=true"
      User: "username"
      Password: "password"
```

After that, go to the `PluginDependencies.java` file and paste the following code right below the Vault check:

```java
if (PluginDependencies.isEnabled(LeaderOSDependency.class)) {

    if (MySQLDatabaseManager.getInstance().checkConn()){
        message.addExtra("\n§8- §eLeaderOS layout §8[");

        TextComponent activateLeaderos = new TextComponent("§aactivate");
        //noinspection deprecation
        activateLeaderos.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent("§8» §aActivate the LeaderOS layout §8«")}));
        activateLeaderos.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tradesystem layout activate " + DefaultLeaderosPattern.NAME));

        message.addExtra(activateLeaderos);
        message.addExtra("§8]");

    } else {
        TradeSystem.getInstance().getLogger().warning("The required database connection for LeaderOS credits could not be established, so it cannot be used.");
    }

}
```


# Lua Tela Installer
*Installer for the Lua Tela Web Framework*
*(current version: 1.0)*

If you are unfamiliar with the [Lua Tela](https://www.github.com/lua-tela/lua-tela) web framework, I highly suggest you check that out!
This repo is primarily for the installation software used for said framework. As for the installer,
all the power is behind the `LuaTelaInstaller.jar`
This `jar` file can be located in the `dist` folder in the root repository directory.
To run the file, use the `java -jar` command followed by the path to the jar. Like so...

```
java -jar "LuaTelaInstaller.jar"
```

You'll be confronted with a command prompt which you can use to execute various commands, like `help`.
If you would like, you can also inline the command after the jar file. Like so...

```
java -jar "LuaTelaInstaller.jar" help
```

This will directly route into the `help` command. Useful for automation or ease of use.

## Installing Lua Tela
For the most part, the installer should be able to install on any operating system (*thanks to the use of Java!*)
But on the off chance that there are certain functionalities or conditions that need to be executed or met before
the installation can proceed successfully.

### Getting Started
First enter the lua tela installer command prompt so that you can probe your environment for installation. Like so...

```
java -jar "LuaTelaInstaller.jar"
```

You'll see something that looks like this...

```
--[===========================[ LUA-TELA ]===========================]
Lua-Tela Web Framework (the power within)
/*
 * installer and diagnostic tool for Lua-Tela web framework.
 */

Date: 7 Mar 2021 01:46 AM
Type help or a command...

cmd:
```

Below in the `cmd:` section, you can enter a command to execute, or help if you need it.

Otherwise, you can type the `install-tomcat` command which will extract an Apache Tomcat Server into the directory.
Once tomcat is extracted, you can head into the `bin` directory and use the
`startup` (`.sh` or `.bat`) and `shutdown` (`.sh` or `.bat`) files to begin or stop the server.

#### `install-tomcat`
This command has some options that can be specified to ensure certain connections and environment properties.
To connect Lua-Tela to our MySQL database, we can specify the command with certain parameters. Like so...

```
cmd: install-tomcat --datauser [username] --datapass [password] --datahost [localhost] --database [databasename]
```

This assumes the port `3306` for the MySQL connection. Sometimes there are also other options to specify with the connection,
this is where the `--dataext [ext]` option can be used to add a query string to the connection.

Another way to specify the database connection is using the `--dataurl` option which triumps the others and should include everything.
For example:
```
cmd: install-tomcat --datauser myuser --datapass mypass --datahost localhost --dataport 3306 --database mybase --dataext useSSL=false
```
This is identical to the following data url
```
cmd: install-tomcat --datauser myuser --datapass mypass --dataurl jdbc:mysql://localhost:3306/mybase?useSSL=false
```

Once completely installed, you should be able to place your relative files in the
`webapps/ROOT/base`
folder where majority of your code will belong.

From there, you should be able to follow the Lua-Tela wiki and tutorials.
someluigis-peripherals
======================

An addon for the Minecraft mod "ComputerCraft". This addon currently adds only a HTTPServer block which allows you to program a http server for your ingame computers for usage on external web browsers.




Building
--------

Build like Pahimar's Equivalent Exchange 3, though the source directory can be called whatever you want, relative paths are used.


Your directory structure should be like:

```

mc-modding/
 mcp/
  forge/
  other-mcp-stuff/
 source/
  SLP-Peripherals/
   slp_common/
   resources/
   build.xml

```

*Remember to install MCP + Forge properly first!*
Then open cmd or bash, go to mcmodding/source/SLP-Peripherals and run the command `ant release`.
Please note you need Apache Ant installed, aswell as the JDK and other things MCP needs.

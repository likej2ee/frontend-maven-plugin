# Frontend maven plugin

support for fis
show more infomation https://github.com/eirslett/frontend-maven-plugin


# Usage
没搞定maven 中央仓库，以后跟进

### Working directory
The working directory is where you've put `package.json` and your frontend configuration files (`fis-conf.js` etc). 


### Running fis
Very similar to the Grunt execution. It will run gulp according to the `fis-conf.js` in your working directory.
By default, no colors will be shown in the log.
```xml
<execution>
    <id>fis build</id>
    <goals>
        <goal>fis</goal>
    </goals>
    
    <!-- optional: the default phase is "generate-resources" -->
    <phase>generate-resources</phase>
    
    <configuration>
        <!-- optional: if not specified, it will run gulp's default
        task (and you can remove this whole <configuration> section.) -->
        <outputdir>${project.basedir}/src/main/webapp</outputdir>
        <arguments>release</arguments>
    </configuration>
</execution>
```

__Skipping tests:__ If you run maven with the `-DskipTests` flag, karma tests will be skipped.

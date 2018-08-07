# Release flow

The command `mvn clean deploy` will create a build, will sign it, and will push it to a staging repository (in nexus).

You can browse/enquire the staging repository [here](https://oss.sonatype.org/#stagingRepositories).
It is possible to check if the structure of the jar files is correct.

If everything is ok, then the "release" button should be clicked, if not, then the "drop" button.
This can also be done from command line with commands: `mvn nexus-staging:release` / `mvn nexus-staging:drop`

This 2-step release is not mandatory, but is the current way of working, to ensure that everything is ok.
Automatic release can be configured by setting `autoReleaseAfterClose` to true in the `pom.xml`

# Setup of settings.xml

Open the `settings.xml` of maven.

> In IntelliJ you can do this by right clicking the `pom.xml` file, then in the maven context menu click "open settings.xml".
The first time you may have to click "create settings.xml". 
On windows, the settings.xml is stored in `c:\users\<user>\.m2\settings.xml`.

The content of the file should roughly look like this:

    <?xml version="1.0" encoding="UTF-8"?>
    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
      <servers>
        <server>
          <id>ossrh</id>
          <username>YOUR_USERNAME</username>
          <password>YOUR_PASSWORD</password>
        </server>
      </servers>
  
      <profiles>
        <profile>
          <id>ossrh</id>
          <activation>
            <activeByDefault>true</activeByDefault>
          </activation>
          <properties>
            <gpg.executable>gpg</gpg.executable>
            <gpg.passphrase>YOUR_GPG_KEY</gpg.passphrase>
          </properties>
        </profile>
      </profiles>
    </settings>


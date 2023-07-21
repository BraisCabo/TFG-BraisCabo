#!/bin/bash

# Verifica si se proporcionaron los dos argumentos (username y password)
if [ $# -ne 3 ]; then
  echo "Use: $0 <username> <password> <fileAddress>"
  exit 1
fi

# Guarda el username y la contraseña en variables
username="$1"
password="$2"
address="$3"

# Imprime el contenido del archivo settings.xml con el username y contraseña reemplazados
cat <<EOF > "$address"
<settings>
    <activeProfiles>
        <activeProfile>github</activeProfile>
    </activeProfiles>
    <profiles>
        <profile>
            <id>github</id>
            <repositories>
                <repository>
                    <id>github</id>
                    <name>GitHub UOC Apache Maven Packages</name>
                    <url>https://maven.pkg.github.com/uoc/java-lti-1.3-core</url>
                </repository>
                <repository>
                    <id>github</id>
                    <name>GitHub UOC Apache Maven Packages</name>
                    <url>https://maven.pkg.github.com/uoc/java-lti-1.3-jwt</url>
                </repository>
                <repository>
                    <id>github</id>
                    <name>GitHub UOC Apache Maven Packages</name>
                    <url>https://maven.pkg.github.com/uoc/java-lti-1.3</url>
                </repository>
                <repository>
                    <id>github</id>
                    <name>GitHub UOC Apache Maven Packages</name>
                    <url>https://maven.pkg.github.com/uoc/spring-boot-lti-advantage</url>
                </repository>
            </repositories>
        </profile>
    </profiles>
    <servers>
        <server>
            <id>github</id>
            <username>$username</username>
            <password>$password</password>
        </server>
    </servers>
</settings>
EOF
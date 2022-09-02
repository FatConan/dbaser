import sbt.util

name := """dbaser"""
organization := "de.themonstrouscavalca"
// maintainer := "oss@themonstrouscavalca.de"
version := "2022.6.2-SNAPSHOT"
//scalaVersion := "2.12.13"
scalaVersion := "2.13.8"


resolvers ++= Seq(Resolver.mavenLocal,
    "Sonatype snapshots repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "Shibboleth releases" at "https://build.shibboleth.net/nexus/content/repositories/releases/",
    "Aspose Releases" at "https://artifact.aspose.com/repo/",
    "Atlassian Releases" at "https://maven.atlassian.com/public/",
    "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
    "org.xerial"                    % "sqlite-jdbc"             % "3.36.0.3"                % Test,
    "junit"                         % "junit"                   % "4.13.1"                  % Test,
    "com.novocode"                  % "junit-interface"         % "0.11"                    % Test,
    "org.slf4j"                     % "slf4j-api"               % "1.7.32",
    "org.slf4j"                     % "slf4j-simple"            % "1.7.32"
)

Compile / compile / logLevel := Level.Warn
Test / test / logLevel := util.Level.Error
parallelExecution in Test := false
logBuffered := false
scalacOptions += "-deprecation"
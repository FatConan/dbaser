mkdir $HOME/.m2 -p
echo "<settings><servers><server><id>github</id><username>$GITHUB_USERNAME</username><password>$GITHUB_TOKEN</password></server></servers></settings>" > $HOME/.m2/settings.xml
cat $HOME/.m2/settings.xml
mvn deploy
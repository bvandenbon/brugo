mvn install:install-file -DgroupId=brugo -DartifactId=brugo -Dversion=0.1.0 -Dfile=C:\Users\ba\IdeaProjects\brugo\target\brugo-0.1.0.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true 
git add -A .
git commit -m "released version"
git push origin repository

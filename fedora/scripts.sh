
#
# Call scripts on same level as fcrepo-webapp-6.1.1-jetty-console.jar
# downloadable from github!
#

# as in tutorial
java -Dfcrepo.namespace.registry=custom_namespaces.yml -jar fcrepo-webapp-6.1.1-jetty-console.jar

# with full path on windows
java -Dfcrepo.namespace.registry=C:\\Users\\stoffse\\Desktop\\fedora\\custom_namespaces.yml -jar fcrepo-webapp-6.1.1-jetty-console.jar
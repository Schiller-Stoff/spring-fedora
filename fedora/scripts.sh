
#
# Call scripts on same level as fcrepo-webapp-6.1.1-jetty-console.jar
# downloadable from github!
#

# first cd to fedora location
cd C:\Users\stoffse\Desktop\fedora

# with full path on windows
# !adapt if on other machine!
java -Dfcrepo.namespace.registry=C:\\Users\\stoffse\\Desktop\\fedora\\custom_namespaces.yml -jar fcrepo-webapp-6.1.1-jetty-console.jar
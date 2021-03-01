# HEC LMS Discovery Search

Recherce de documents sur OCLC


### Requis

- Node.js

- Exécuter ``npm install`` dans le dossier ``search``

### Build

Exécuter ``mvn package`` dans la racine de l'application. Ceci produira un war pour les web-services et un war pour l'interface de recherche.

### Web-services

Application Java qui communique avec l'api OCLC. Un fichier properties doit être configuré avec vos accès et les ids de BD de votre institution, et déployé dans le répertoire /properties dans TOMCAT_HOME (web-services\properties\local\hec-lms-discovery-web-services.properties).

### Search

Application Vue.js, un formulaire de recherche qui appelle les web-services et affiche les résultats. Chaque résultat contient un Open Url destiné à l'import dans le LMS, et un lien direct vers la ressource. Les liens vers worldcat sont préfixés par l'institution (configuré dans Resource.java).

# Instabus
Instabus est une application Android réalisée en Kotlin pour un projet de cours. C'est un réseau social, clone d'Instagram, où les utilisateurs peuvent voir les stations de bus (uniquement Barcelone) et y prendre des photographies. Elles sont ensuite visionnables par tous les utilisateurs.

***[ENG]** Instabus is an Android application created using Kotlin for a school project. It’s a social network like Instagram where users can search for bus stations is Barcelona and take pictures of them. Taken pictures can be seen by all users.*

## Exigence / Requirement
Le projet fonctionne actuellement en collaboration avec un service web hébergé sur un réseau privé. Le service web étant éteint, il faut l'héberger soi-même en local (WAMP par exemple) et modifier la constante **WEBSERVICE_ADDRESS** [dans le fichier WebServiceLink.kt](https://github.com/EmpireDemocratiqueDuPoulpe/Instabus/blob/master/app/src/main/java/com/eddp/instabus/data/WebServiceLink.kt).

[Le service web est disponible ici.](https://github.com/EmpireDemocratiqueDuPoulpe/instabus_webservice)

***[ENG]** This project uses an external web service hosted on a private network. The web service is actually off to save RAM. You will need to host it locally (using WAMP or others) and edit the **WEBSERVICE_ADDRESS** constant [in the WebServiceLink.kt file](https://github.com/EmpireDemocratiqueDuPoulpe/Instabus/blob/master/app/src/main/java/com/eddp/instabus/data/WebServiceLink.kt).*

*[The web service is available here.](https://github.com/EmpireDemocratiqueDuPoulpe/instabus_webservice)*

## Bugs connus / Known bugs
- **[Caméra - API 16 -> API 20] -** Parfois le service de caméra ne démarre pas et l'application plante.
- **[Interface / Données] -** L'application peut perdre la liste des stations et des posts au changement de thème (il est possible de rafraîchir)
- **[Localisation / Émulateur] -** L'émulateur Android envoie parfois des coordonées éronnées située à San Francisco. La carte n'affiche que les stations à un kilomètre à la ronde donc les stations disparaissent.
- **[Champs de texte] -** Utiliser un émoji dans un champ de texte fait planter le script côté serveur.


***[ENG]***
- ***[Camera - API 16 -> API 20] -** Sometimes, the camera service doesn't work and the app crash. Only happened a few times in dev*
- ***[Interface / Data] -** The app loses every post and station sometimes.*
- ***[Maps / Emulator Only] -** Sometime the emulator sends wrong coordinates*
- ***[Fields] -** Using emoji in a field can make the webservice script crash*

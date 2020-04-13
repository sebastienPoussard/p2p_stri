# p2p_stri

This Java project is developped in collaboration with Romain BREDARIOL & Léo-Paul Dewitte.

# Goal

This application act as a P2P Client to share files.

# Links
[Git](https://github.com/sebastienPoussard/p2p_stri)
[Documentation Doxygen](https://pouseb.fr/P2P/index.html)

# Subject

**But** : le but de ce projet est de créer une application répartie en Java de téléchargement de fichier en mode P2P (peer to peer ou poste à poste).
Les étapes suivantes sont conseillées.
## Étape 1 : Téléchargement à la FTP

La première étape doit permettre de télécharger un fichier en intégralité d'une machine vers une autre machine de façon similaire aux applications suivant le protocole FTP.

Plan de travail :

- Lire le sujet jusqu'au bout et la RFC FTP (version anglaise, version française)
- Concevoir l'application répartie avec UML
- Écrire l'application serveur et l'application cliente.

## Étape 2 : Téléchargement en parallèle

Dans la seconde étape, on permet à un client de télécharger le fichier depuis plusieurs serveurs.
Le fichier sera découpé en plusieurs blocs de tailles égales (par exemple 4 Ko) qui seront téléchargés depuis plusieurs serveurs.
Dans cette étape c'est le client qui choisit (par exemple aléatoirement) quel bloc télécharger depuis quel serveur.

Plan de travail :

- Modifier le serveur pour gérer l'envoi de n'importe quel bloc d'un fichier
- Modifier le client pour qu'il puisse demander le téléchargement de n'importe quel bloc et re-créer le fichier complet.

## Étape 3 : Transformation en P2P simple

Dans cette étape, il n'y a plus de clients ni de serveurs ; les applications sont les deux à la fois.
Chaque application devra noter de quelle partie du fichier elle dispose. Au démarrage certaines applications auront le fichier complet et les autres aucun bloc.
Les applications demanderont aléatoirement chaque bloc manquant à n'importe quelle autre application qui renverra soit le bloc soit un message d'erreur.

## Étape 4 : P2P coordonné

Dans cette étape, on ajoute un serveur dont le rôle est de maintenir la liste des applications gérant le téléchargement d'un fichier et quel bloc chaque application possède.
Ce serveur coordonnera le téléchargement en précisant à chaque application, à qui se connecter et ce qui y est disponible.

## Étape 5 : P2P coopératif

Dans cette étape, on doit s'assurer que les applications envoient et reçoivent globalement les même quantités.
On essaiera ainsi de désavantager les applications qui ne font que télécharger et n'envoient rien.

## Options :

- Créer une IHM (interface homme machine) Graphique pour les applications avec Swing par exemple.
- Gérer à la fois des communications UDP et TCP.

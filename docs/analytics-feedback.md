# 📈 Analytics et Feedback

<div align="center">

**Collecte et analyse des données d'utilisation pour améliorer GraphQL AutoGen.**

</div>

---

## 🎯 Vue d'ensemble

Pour assurer le développement continu et l'amélioration de GraphQL AutoGen, nous prévoyons de mettre en place des mécanismes de collecte d'analytics et de feedback. Ces données nous aideront à comprendre comment le projet est utilisé, à identifier les points faibles et à prioriser les nouvelles fonctionnalités.

---

## 🚀 Collecte d'Analytics

### 1. Données d'Utilisation Anonymes

-   **Objectif :** Comprendre les fonctionnalités les plus utilisées, les versions de Spring Boot et de Java les plus populaires, et les environnements de build (Maven/Gradle).
-   **Méthode :** Intégration d'un mécanisme d'analytics anonyme et opt-in dans le starter et les plugins. Aucune donnée personnelle ne sera collectée.
-   **Exemples de données :** Version du starter, version de Java, type de build (Maven/Gradle), nombre de types/opérations générés (agrégé).

### 2. Rapports d'Erreurs Automatisés

-   **Objectif :** Recevoir des rapports d'erreurs non bloquants pour identifier et corriger rapidement les bugs.
-   **Méthode :** Utilisation d'un service de rapport d'erreurs (par exemple, Sentry, Bugsnag) avec un consentement explicite de l'utilisateur.
-   **Données incluses :** Stack traces, informations sur l'environnement (version OS, JVM), mais **aucune donnée sensible ou personnelle**.

---

## 💬 Canaux de Feedback

### 1. Issues GitHub

-   Le principal canal pour les rapports de bugs, les demandes de fonctionnalités et les questions techniques.

### 2. Sondages Utilisateurs

-   Des sondages périodiques pour recueillir des avis qualitatifs sur l'expérience utilisateur, les besoins non satisfaits et les suggestions d'amélioration.

### 3. Discussions Communautaires

-   Mise en place de forums ou de canaux de discussion (Discord, Slack) pour faciliter les échanges entre utilisateurs et avec l'équipe de développement.

---

## 🔒 Confidentialité des Données

La confidentialité des utilisateurs est primordiale. Toutes les données collectées seront anonymisées autant que possible et utilisées uniquement dans le but d'améliorer le projet. Une politique de confidentialité claire sera publiée.

---

**🎉 Votre feedback est essentiel pour faire évoluer GraphQL AutoGen !**

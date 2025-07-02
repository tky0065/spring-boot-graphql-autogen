# 🐛 Processus de Correction de Bugs

<div align="center">

**Comment les bugs sont signalés, triés et corrigés dans GraphQL AutoGen.**

</div>

---

## 🎯 Vue d'ensemble

Ce document décrit le processus que nous suivons pour gérer les rapports de bugs, de leur signalement initial à leur correction et à leur déploiement. Notre objectif est de résoudre les problèmes rapidement et efficacement, en minimisant l'impact sur les utilisateurs.

---

## 🚀 Étapes du Processus

### 1. Signalement du Bug

-   **Canal :** Les bugs doivent être signalés via les [Issues GitHub](https://github.com/tky0065/spring-boot-graphql-autogen/issues).
-   **Informations requises :** Utilisez le template de rapport de bug. Incluez les étapes de reproduction, le comportement attendu, le comportement actuel, les messages d'erreur, les versions de GraphQL AutoGen, Spring Boot, Java, etc.

### 2. Tri et Priorisation

-   **Vérification :** L'équipe de maintenance examine le rapport pour confirmer le bug et s'assurer qu'il n'a pas déjà été signalé.
-   **Reproduction :** Tente de reproduire le bug en utilisant les informations fournies.
-   **Priorisation :** Le bug est classé en fonction de sa gravité (critique, majeur, mineur) et de son impact sur les utilisateurs. Une étiquette de priorité est attribuée (ex: `P0: Critical`, `P1: High`, `P2: Medium`, `P3: Low`).
-   **Assignation :** Le bug est assigné à un développeur pour investigation et correction.

### 3. Correction du Bug

-   **Développement :** Le développeur travaille sur la correction du bug. Cela inclut l'écriture de tests unitaires et d'intégration pour s'assurer que le bug est corrigé et qu'aucune régression n'est introduite.
-   **Pull Request (PR) :** Une PR est soumise avec la correction. La PR doit inclure une description claire du bug, de la solution, et des tests associés.
-   **Revue de Code :** La PR est revue par un autre membre de l'équipe pour s'assurer de la qualité du code et de l'efficacité de la correction.

### 4. Tests et Validation

-   **Tests CI/CD :** La correction est validée par la pipeline CI/CD, qui exécute tous les tests automatisés.
-   **Tests Manuels (si nécessaire) :** Pour les bugs complexes, des tests manuels supplémentaires peuvent être effectués.

### 5. Déploiement

-   **Intégration :** Une fois la correction validée, elle est fusionnée dans la branche principale.
-   **Release :** La correction est incluse dans la prochaine version patch ou mineure, selon sa priorité et son urgence.
-   **Communication :** Le rapporteur du bug est informé de la correction et de la version dans laquelle elle sera disponible.

---

## 💡 Bonnes Pratiques pour les Rapporteurs

-   **Soyez précis :** Plus les informations sont détaillées, plus il est facile de reproduire et de corriger le bug.
-   **Soyez patient :** Nous faisons de notre mieux pour traiter les bugs rapidement, mais la priorisation est nécessaire.
-   **Soyez collaboratif :** Répondez aux questions des développeurs si des informations supplémentaires sont nécessaires.

---

**🎉 Votre aide est précieuse pour maintenir la qualité de GraphQL AutoGen !**
